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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.service.StrategicRouteHandler.StrategicRouteListener;
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
    public VoyageLayer() {
        this(false);
    }

    /**
     * Constructor
     */
    public VoyageLayer(boolean windowHandling) {
        super();
        this.windowHandling = windowHandling;

        // Automatically add info panels
        registerInfoPanel(voyageInfoPanel, VoyageLegGraphic.class);

        // Register the classes the will trigger the map menu
        registerMapMenuClasses(VoyageWaypointCircle.class, VoyageLegGraphic.class);

        voyageManager = EPDShore.getInstance().getVoyageManager();
        voyageManager.addListener(this);
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
                        .get(transactionId).getRouteMessage().get(0).getMmsi();
                
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
}
