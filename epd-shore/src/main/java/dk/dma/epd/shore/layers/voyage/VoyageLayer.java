/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.shore.layers.voyage;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon.StrategicRouteListener;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.layers.VoyageLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.VoyageLayerCommonSettingsListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;
import dk.dma.epd.shore.voyage.VoyageUpdateEvent;
import dk.dma.epd.shore.voyage.VoyageUpdateListener;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends EPDLayerCommon implements VoyageUpdateListener, StrategicRouteListener, IAisTargetListener,
        ProjectionListener, VoyageLayerCommonSettingsListener {

    private static final long serialVersionUID = 1L;

    private VoyageManager voyageManager;
    private StrategicRouteHandler strategicRouteHandler;
    private ChartPanel chartPanel;
    private AisHandler aisHandler;

    private VoyageInfoPanel voyageInfoPanel = new VoyageInfoPanel();
    private Map<Long, ShipIndicatorPanel> shipIndicatorPanels = new HashMap<>();

    private boolean windowHandling;

    /**
     * Constructor
     */
    public VoyageLayer(VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener> localLayerSettings) {
        this(localLayerSettings, false);
    }

    /**
     * Constructor
     */
    public VoyageLayer(VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener> localLayerSettings, boolean windowHandling) {
        super(Objects.requireNonNull(localLayerSettings));
        // register self as observer of own settings
        localLayerSettings.addObserver(this);
        this.windowHandling = windowHandling;

        // Automatically add info panels
        registerInfoPanel(voyageInfoPanel, VoyageLegGraphic.class);

        // Register the classes that will trigger the map menu
        registerMapMenuClasses(VoyageWaypointCircle.class, VoyageLegGraphic.class);

        voyageManager = EPDShore.getInstance().getVoyageManager();
        voyageManager.addListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener> getSettings() {
        // TODO Auto-generated method stub
        return (VoyageLayerCommonSettings<VoyageLayerCommonSettingsListener>) super.getSettings();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof StrategicRouteHandler) {
            strategicRouteHandler = (StrategicRouteHandler) obj;
            strategicRouteHandler.addStrategicRouteListener(this);
            //For update of voyages
            voyagesChanged(null);
        }
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            aisHandler.addListener(this);
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj == voyageManager) {
            voyageManager.removeListener(this);
        }
        super.findAndUndo(obj);
    }

    /**
     * {@inheritDoc}
     */
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {

        if (clickedGraphics instanceof VoyageLegGraphic) {
            VoyageLegGraphic rlg = (VoyageLegGraphic) clickedGraphics;
            int voyageIndex = rlg.getVoyageIndex();

            if (voyageManager.getVoyageCount() > voyageIndex) {
                Voyage currentVoyage = voyageManager.getVoyage(voyageIndex);
                getMapMenu().voyageGeneralMenu(currentVoyage.getId(), currentVoyage.getMmsi(), currentVoyage.getRoute(), mapBean);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof VoyageLegGraphic) {

            // Re-position the menu
            containerPoint = SwingUtilities.convertPoint(chartPanel, evt.getPoint(), mapFrame.asComponent());
            voyageInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);

            VoyageLegGraphic wpLeg = (VoyageLegGraphic) newClosest;
            int voyageIndex = wpLeg.getVoyageIndex();
            Voyage currentVoyage = voyageManager.getVoyage(voyageIndex);
            VesselTarget ship = aisHandler.getVesselTarget(currentVoyage.getMmsi());
            String name = "" + currentVoyage.getMmsi();
            if (ship != null) {
                if (ship.getStaticData() != null) {
                    name = ship.getStaticData().getTrimmedName();
                }
            }
            voyageInfoPanel.showVoyageInfo(currentVoyage, name);
            return true;
        }
        return false;
    }

    /**
     * Called by the {@linkplain VoyageManager} when the voyage is changed
     * 
     * @param e
     *            the voyage update event
     */
    @Override
    public void voyagesChanged(VoyageUpdateEvent e) {

        graphics.clear();

        for (int i = 0; i < voyageManager.getVoyages().size(); i++) {
            // Route route = voyageManager.getVoyages().get(i).getRoute();
            Voyage voyage = voyageManager.getVoyages().get(i);
            // System.out.println(route);
            if (voyage.getRoute().isVisible()) {
                System.out.println("Adding Voyage");
                VoyageGraphic voyageGraphic = new VoyageGraphic(voyage, i, new Color(0.4f, 0.8f, 0.5f, 0.5f));
                graphics.add(voyageGraphic);
            }

        }

        graphics.project(getProjection(), true);

        doPrepare();
    }

    /**
     * Adjusts the position of the ship indicator that is displayed for ships with unhandled transactions
     */
    private void updateDialogLocations() {
        // Ensure that we operate in the Swing event thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    updateDialogLocationsInSwingThread();
                }
            });
        } else {
            updateDialogLocationsInSwingThread();
        }
    }
    
    /**
     * Adjusts the position of the ship indicator that is displayed for ships with unhandled transactions
     */
    private synchronized void updateDialogLocationsInSwingThread() {

        if (strategicRouteHandler != null && !windowHandling) {

            Set<Long> updatedMmsi = new HashSet<>();

            for (Long transactionId : strategicRouteHandler.getUnhandledTransactions()) {
                long mmsi = strategicRouteHandler.getStrategicNegotiationData()
                        .get(transactionId).getMmsi();
                
                VesselTarget ship = aisHandler.getVesselTarget(mmsi);
                
                if (ship != null) {
                    // Register that we update the panel for this MMSI
                    updatedMmsi.add(mmsi);

                    // Look up or create the ship indicator panel
                    ShipIndicatorPanel shipIndicatorPanel = shipIndicatorPanels.get(mmsi);
                    if (shipIndicatorPanel == null) {
                        shipIndicatorPanel = new ShipIndicatorPanel(transactionId);
                        shipIndicatorPanels.put(mmsi, shipIndicatorPanel);
                        mapFrame.getGlassPanel().add(shipIndicatorPanel);
                    }
                    
                    // Compute the updates position
                    Position position = ship.getPositionData().getPos();
                    Point2D resultPoint = getProjection().forward(position.getLatitude(), position.getLongitude());
                    Point newPoint = new Point((int) resultPoint.getX(), (int) resultPoint.getY());
                    shipIndicatorPanel.setLocation(newPoint);
                }
            }
            
            // Clean up ship indicator panels for which no unhandled transaction or ship exists
            for (Iterator<Map.Entry<Long, ShipIndicatorPanel>> it = shipIndicatorPanels.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Long, ShipIndicatorPanel> entry = it.next();
                if (!updatedMmsi.contains(entry.getKey())) {
                    mapFrame.getGlassPanel().remove(entry.getValue());
                    it.remove();
                }
            }
        }
    }

    /**
     * Called by the {@linkplain StrategicRouteHandler} upon updates to the strategic routes
     */
    @Override
    public void strategicRouteUpdate() {
        updateDialogLocations();
    }

    /**
     * Called by the {@linkplain AisHandler} when an AIS target has been updated
     * 
     * @param aisTarget
     *            the AIS target that has been updated
     */
    @Override
    public void targetUpdated(AisTarget aisTarget) {

        if (strategicRouteHandler != null && strategicRouteHandler.getStrategicNegotiationData() != null) {
            for (StrategicRouteNegotiationData data : strategicRouteHandler.getStrategicNegotiationData().values()) {
                if (data.getMmsi() == aisTarget.getMmsi()) {
                    // only run update if this vessel has negotiation data
                    this.updateDialogLocations();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent pe) {
        super.projectionChanged(pe);
        this.updateDialogLocations();
    }

    /**
     * Returns the map menu cast as {@linkplain MapMenu}
     * 
     * @return the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu) mapMenu;
    }

    /*
     * [Begin settings listener methods]
     */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        if (source == this.getSettings()) {
            this.setVisible(newValue);
        }
    }

    /*
     * [End settings listener methods]
     */
    
}
