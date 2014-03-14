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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLegGraphic;
import dk.dma.epd.common.prototype.layers.route.WaypointCircle;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.voyage.IVoyageUpdateListener;
import dk.dma.epd.common.prototype.model.voyage.VoyageEventDispatcher;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.voyage.Voyage;


/**
 * Layer for showing routes
 */
public class VoyageHandlingLayer extends EPDLayerCommon implements IVoyageUpdateListener {

    private static final long serialVersionUID = 1L;
    
    private boolean dragging;
    private OMGraphic selectedGraphic;

    private VoyagePlanInfoPanel voyagePlanInfoPanel = new VoyagePlanInfoPanel(this);
    private VoyageHandlingMouseOverPanel voyageHandlingMouseOverPanel = new VoyageHandlingMouseOverPanel();

    private Voyage voyage;

    private Route originalRoute;
    private Route initialReceivedRoute;
    private Route newRoute;
    private boolean modified;
    private boolean renegotiate;

    boolean routeChange;

    Stroke stroke = new BasicStroke(1.0f,   // Width
            BasicStroke.CAP_SQUARE,         // End cap
            BasicStroke.JOIN_MITER,         // Join style
            10.0f,                          // Miter limit
            new float[] { 1.0f, 5.0f },     // Dash pattern
            0.0f);

    /**
     * Constructor
     */
    public VoyageHandlingLayer() {
        super();
        
        
        // Automatically add info panels
        registerInfoPanel(voyageHandlingMouseOverPanel, RouteLegGraphic.class, WaypointCircle.class);
        voyagePlanInfoPanel.setVisible(true);
        
        // Register the classes the will trigger the map menu
        registerMapMenuClasses(WaypointCircle.class, RouteLegGraphic.class);
        
        // register self as listener for voyage changes
        EPDShore.getInstance().getVoyageEventDispatcher().registerListener(this);
    }

    /**
     * Returns the map frame cast as a {@linkplain JMapFrame}
     * @return the map frame cast as a {@linkplain JMapFrame}
     */
    @Override
    public JMapFrame getMapFrame() {
        return (JMapFrame)mapFrame;
    }
    
    /**
     * Returns the map menu cast as {@linkplain MapMenu}
     * @return the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu)mapMenu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof JMapFrame) {
            JMapFrame mapFrame = (JMapFrame)obj;
            voyagePlanInfoPanel.setPreferredSize(new Dimension(200, 300));
            voyagePlanInfoPanel.setMinimumSize(new Dimension(200, 300));
            mapFrame.addContentPanel(voyagePlanInfoPanel, BorderLayout.EAST);
            //mapFrame.revalidate();
        }

        if (obj instanceof AisHandler) {
            voyagePlanInfoPanel.setAisHandler((AisHandler) obj);
        }

        if (obj instanceof ChartPanel) {
            voyagePlanInfoPanel.setChartPanel((ChartPanel) obj);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }
    
    /**
     * Compares the ETA's of the initial and new routes
     */
    private void checkIfETAChanged() {
        if (!modified) {
            for (int i = 0; i < newRoute.getEtas().size(); i++) {
                if (initialReceivedRoute.getEtas().get(i) != newRoute.getEtas()
                        .get(i)) {
                    modified = true;
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {
        selectedGraphic = clickedGraphics;
        
        if (clickedGraphics instanceof WaypointCircle){
            WaypointCircle wpc = (WaypointCircle) selectedGraphic;

            if (wpc.getRouteIndex() == 1) {
                checkIfETAChanged();
                voyage.setRoute(newRoute);
                getMapMenu().voyageWaypointMenu(this, mapBean, voyage, true, newRoute, null,
                        evt.getPoint(), wpc.getWpIndex());
            }
        
        } else if (clickedGraphics instanceof RouteLegGraphic) {
            RouteLegGraphic rlg = (RouteLegGraphic) selectedGraphic;

            if (rlg.getRouteIndex() == 1) {
                checkIfETAChanged();
                voyage.setRoute(newRoute);
                getMapMenu().voyageWaypointMenu(this, mapBean, voyage, false, newRoute,
                        rlg.getRouteLeg(), evt.getPoint(), 0);
            }            
        }
    }
    
    /**
     * This method is called in order to send the voyage back to the ship.
     * <p>
     * Afterwards, the layer and parent window will self-destruct.
     * 
     * @param message the message to send along
     */
    public void sendVoyage(String message) {
        checkIfETAChanged();
        voyage.setRoute(newRoute);
        
        StrategicRouteStatus replyStatus = 
                modified
                ? StrategicRouteService.StrategicRouteStatus.NEGOTIATING
                : StrategicRouteService.StrategicRouteStatus.AGREED;

        EPDShore.getInstance().getStrategicRouteHandler()
            .sendStrategicRouteReply(
                    voyage.getId(), 
                    message,
                    System.currentTimeMillis(), 
                    replyStatus, 
                    voyage.getRoute().getFullRouteData(), 
                    renegotiate);
        getMapFrame().dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }

        if (!dragging) {
            selectedGraphic = getSelectedGraphic(e, WaypointCircle.class);
        }

        if (selectedGraphic instanceof WaypointCircle) {
            WaypointCircle wpc = (WaypointCircle) selectedGraphic;

            if (wpc.getRouteIndex() == 1 && newRoute != null) {
                RouteWaypoint routeWaypoint = newRoute.getWaypoints().get(wpc.getWpIndex());
                LatLonPoint newLatLon = mapBean.getProjection().inverse(e.getPoint());
                Position newLocation = Position.create(
                        newLatLon.getLatitude(),
                        newLatLon.getLongitude());
                routeWaypoint.setPos(newLocation);

                newRoute.calcValues(true);

                if (!modified) {
                    changeName();
                }
                modified = true;

                updateVoyages();

                dragging = true;
                return true;
            }
        }

        return false;
    }

    private void changeName() {
        // newRoute.setName(newRoute.getName() + " modified");
    }

    /**
     * @param newRoute
     *            the newRoute to set
     */
    public void setNewRoute(Route newRoute) {
        this.newRoute = newRoute;
    }

    /**
     * Update voyages, clear all graphics, redraw original but in red, draw
     * the new voyage.
     */
    public void updateVoyages() {

        graphics.clear();

        Color ECDISOrange = new Color(213, 103, 45, 255);

        // Modified route, ecdis line, green broadline
        RouteGraphic modifiedVoyageGraphic = new RouteGraphic(newRoute, 1,
                false, stroke, ECDISOrange,
                new Color(0.39f, 0.69f, 0.49f, 0.6f), true, true);
        graphics.add(modifiedVoyageGraphic);

        // Red
        RouteGraphic originalRouteGraphic = new RouteGraphic(originalRoute, 0,
                false, stroke, ECDISOrange, new Color(1f, 0, 0, 0.4f), false,
                true);
        graphics.add(originalRouteGraphic);

        if (routeChange) {
            RouteGraphic voyageGraphic = new RouteGraphic(initialReceivedRoute,
                    2, false, stroke, ECDISOrange, new Color(1f, 1f, 0, 0.7f),
                    false, true);
            graphics.add(voyageGraphic);
        }

        graphics.project(getProjection(), true);

        doPrepare();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved() {
        // TODO: Is this really necessary?
        //graphics.deselect();
        //repaint();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof WaypointCircle) {
            WaypointCircle waypointCircle = (WaypointCircle) newClosest;
            voyageHandlingMouseOverPanel.showType(waypointCircle.getRouteIndex());
            return true;
        
        } else if (newClosest instanceof RouteLegGraphic) {
            RouteLegGraphic waypointLeg = (RouteLegGraphic) newClosest;
            voyageHandlingMouseOverPanel.showType(waypointLeg.getRouteIndex());
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            updateVoyages();
            return true;
        }
        return false;
    }

    /**
     * Functions called when creating the layer, it paints the initial voyage
     * 
     * @param voyage
     */
    public void handleVoyage(Route originalRoute, Voyage voyage, boolean renegotiate) {
        graphics.clear();

        this.renegotiate = renegotiate;
        this.originalRoute = originalRoute.copy();
        this.voyage = voyage;
        this.initialReceivedRoute = voyage.getRoute().copy();
        this.newRoute = voyage.getRoute();

        Color ECDISOrange = new Color(213, 103, 45, 255);

        // Added the route as green, original received one
        RouteGraphic voyageGraphic = new RouteGraphic(newRoute, 1, false,
                stroke, ECDISOrange, new Color(0.39f, 0.69f, 0.49f, 0.6f),
                false, true);

        if (originalRoute.getWaypoints().size() != voyage.getRoute()
                .getWaypoints().size()) {
            routeChange = true;
        } else {
            for (int i = 0; i < originalRoute.getWaypoints().size(); i++) {

                double originalLat = originalRoute.getWaypoints().get(i)
                        .getPos().getLatitude();
                double originalLon = originalRoute.getWaypoints().get(i)
                        .getPos().getLongitude();

                double newLat = voyage.getRoute().getWaypoints().get(i)
                        .getPos().getLatitude();
                double newLon = voyage.getRoute().getWaypoints().get(i)
                        .getPos().getLongitude();

                if (originalLat != newLat) {
                    routeChange = true;
                    break;
                }

                if (originalLon != newLon) {
                    routeChange = true;
                    break;
                }

            }
        }

        if (routeChange) {

            // Are the routes the same?
            // originalroute vs. newroute
            RouteGraphic originalRouteGraphic = new RouteGraphic(originalRoute,
                    0, false, stroke, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false, true);
            graphics.add(originalRouteGraphic);
        }

        voyagePlanInfoPanel.setVoyage(voyage);

        graphics.add(voyageGraphic);
        graphics.project(getProjection(), true);
        doPrepare();

    }

    /**
     * Called by the {@linkplain VoyageEventDispatcher} when a voyage is updated
     */
    @Override
    public void voyageUpdated(VoyageUpdateEvent typeOfUpdate, Route updatedVoyage, int routeIndex) {
        if (typeOfUpdate == VoyageUpdateEvent.WAYPOINT_INSERTED) {
            // A waypoint was inserted into a voyage.
            // Redraw voyages in case we are displaying the voyage that was
            // changed.
            // Ideally route would have an equal method that could
            // then be used to determine if we need to redraw voyages.
            updateVoyages();
        }
    }
}
