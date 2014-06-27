/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.dma.epd.shore.layers.voyage;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.layers.VoyageLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.VoyageLayerCommonSettingsListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.service.StrategicRouteHandler.StrategicRouteListener;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;
import dk.dma.epd.shore.voyage.VoyageUpdateEvent;
import dk.dma.epd.shore.voyage.VoyageUpdateListener;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends EPDLayerCommon implements VoyageUpdateListener, StrategicRouteListener, IAisTargetListener,
        ProjectionListener {

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
        this.windowHandling = windowHandling;

        // Automatically add info panels
        registerInfoPanel(voyageInfoPanel, VoyageLegGraphic.class);

        // Register the classes that will trigger the map menu
        registerMapMenuClasses(VoyageWaypointCircle.class, VoyageLegGraphic.class);

        voyageManager = EPDShore.getInstance().getVoyageManager();
        voyageManager.addListener(this);
    }

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
    public void mouseMoved() {
        // TODO: Is this really necessary?
        // graphics.deselect();
        // repaint();
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
                    name = ship.getStaticData().getName();
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
    private synchronized void updateDialogLocations() {

        if (strategicRouteHandler != null && !windowHandling) {

            List<Long> unhandledTransactions = strategicRouteHandler.getUnhandledTransactions();

            if (unhandledTransactions.size() > 0) {

                for (int j = 0; j < unhandledTransactions.size(); j++) {
                    long mmsi = strategicRouteHandler.getStrategicNegotiationData()
                            .get(strategicRouteHandler.getUnhandledTransactions().get(j)).getRouteMessage().get(0).getMmsi();

                    ShipIndicatorPanel shipIndicatorPanel;

                    if (shipIndicatorPanels.containsKey(mmsi)) {
                        shipIndicatorPanel = shipIndicatorPanels.get(mmsi);
                    } else {
                        shipIndicatorPanel = new ShipIndicatorPanel(unhandledTransactions.get(j));
                    }

                    VesselTarget ship = aisHandler.getVesselTarget(mmsi);
                    
                    if (ship != null){
                        
                    
                    
                    Position position = ship.getPositionData().getPos();

                    Point2D resultPoint = this.getProjection().forward(position.getLatitude(), position.getLongitude());

                    Point newPoint = new Point((int) resultPoint.getX(), (int) resultPoint.getY());

                    shipIndicatorPanel.setLocation(newPoint);

                    shipIndicatorPanels.put(mmsi, shipIndicatorPanel);
                    mapFrame.getGlassPanel().remove(shipIndicatorPanel);
                    mapFrame.getGlassPanel().add(shipIndicatorPanel);

                    shipIndicatorPanel.paintAll(shipIndicatorPanel.getGraphics());
                    }
                }
            } else {
                // Iterate through and remove old

                for (ShipIndicatorPanel value : shipIndicatorPanels.values()) {
                    mapFrame.getGlassPanel().remove(value);
                }
                shipIndicatorPanels.clear();
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
}
