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
package dk.dma.epd.ship.layers.voyage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLegGraphic;
import dk.dma.epd.common.prototype.layers.route.WaypointCircle;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.voyage.IVoyageUpdateListener;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.service.StrategicRouteHandler;

/**
 * Layer for showing voyages
 */
public class VoyageLayer extends EPDLayerCommon implements Runnable, IVoyageUpdateListener {

    private static final long serialVersionUID = 1L;
    private static final Color ECDIS_ORANGE = new Color(213, 103, 45, 255);
    
    public static final int ROUTE_INDEX_ORIGINAL_ROUTE = 0;
    public static final int ROUTE_INDEX_STCC_ROUTE     = 1;
    public static final int ROUTE_INDEX_EDITABLE_ROUTE = 2;
    public static final int ROUTE_INDEX_CURRENT        = 3;
    
    private float routeWidth = 2.0f;
    private Timer routeAnimatorTimer;

    private Route primaryRoute;
    private Route stccRoute;
    private Route modifiedSTCCRoute;
    private StrategicRouteHandler strategicRouteHandler;

    private boolean dragging;

    private int animationTimer = 100;
    private VoyageHandlingMouseOverPanel voyageHandlingMouseOverPanel = new VoyageHandlingMouseOverPanel();

    private OMGraphic selectedGraphic;

    /**
     * Constructor
     */
    public VoyageLayer() {
        super(null);
        
        // Automatically add info panels
        registerInfoPanel(voyageHandlingMouseOverPanel, WaypointCircle.class, RouteLegGraphic.class);

        // Register the classes the will trigger the map menu
        registerMapMenuClasses(WaypointCircle.class, RouteLegGraphic.class);
        
        // Register this layer as listener for voyage update events
        EPDShip.getInstance().getVoyageEventDispatcher().registerListener(this);
    }

    /**
     * Receives a route from the {@linkplain StrategicRouteHandler}
     * @param route the new route
     */
    public void startRouteNegotiation(Route route) {

        primaryRoute = route;

        // Added the route as green, original received one
        drawRoute(route, ECDIS_ORANGE);

        startRouteAnimation();
    }

    /**
     * Add the graphics for the given route to the layer
     * @param route the route to draw
     * @param color the color of the route
     */
    private void drawRoute(Route route, Color color) {

        Stroke stroke = new BasicStroke(
                routeWidth,                     // Width
                BasicStroke.CAP_SQUARE,         // End cap
                BasicStroke.JOIN_MITER,         // Join style
                10.0f,                          // Miter limit
                new float[] { 3.0f, 10.0f },    // Dash pattern
                0.0f);

        // Added the route as green, original received one
        RouteGraphic routeGraphic = new RouteGraphic(route, ROUTE_INDEX_CURRENT, true, stroke, color);
        graphics.add(routeGraphic);
        graphics.project(getProjection(), true);
        doPrepare();
    }

    /**
     * Add the graphics for the given route to the layer
     * @param id the route index
     * @param route the route
     * @param color the color of the route
     * @param broadLineColor the broad line color
     * @param circleDash dashed circle or not
     */
    private void drawRoute(int id, Route route, Color color,
            Color broadLineColor, boolean circleDash) {

        if (route == null) {
            return;
        }
        
        Stroke stroke = new BasicStroke(
                routeWidth,                     // Width
                BasicStroke.CAP_SQUARE,         // End cap
                BasicStroke.JOIN_MITER,         // Join style
                10.0f,                          // Miter limit
                new float[] { 3.0f, 10.0f },    // Dash pattern
                0.0f);

        // Added the route as green, original received one
        RouteGraphic routeGraphic = new RouteGraphic(route, id, true, stroke,
                color, broadLineColor, circleDash, true);
        graphics.add(routeGraphic);
        graphics.project(getProjection(), true);
        doPrepare();

    }

    /**
     * Starts animating the route
     */
    private void startRouteAnimation() {

        RouteGraphic animatedRoute = null;

        for (int i = 0; i < graphics.size(); i++) {
            if (graphics.get(i) instanceof RouteGraphic) {
                if (primaryRoute == ((RouteGraphic) graphics.get(i)).getRoute()) {
                    animatedRoute = (RouteGraphic) graphics.get(i);
                    animatedRoute.activateAnimation();
                }
            }
        }

        final RouteGraphic animatedRoute2 = animatedRoute;

        routeAnimatorTimer = new Timer(true);

        routeAnimatorTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                animatedRoute2.updateAnimationLine();
            }
        }, 0, animationTimer);

        doPrepare();
    }

    /**
     * Stops animating the route
     */
    private void stopRouteAnimated() {
        if (routeAnimatorTimer != null){
            routeAnimatorTimer.cancel();    
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof StrategicRouteHandler) {
            strategicRouteHandler = (StrategicRouteHandler) obj;
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
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {
        selectedGraphic = clickedGraphics;
        
        if (clickedGraphics instanceof WaypointCircle) {
            WaypointCircle wpc = (WaypointCircle) clickedGraphics;
            getMapMenu().sendToSTCC(wpc.getRouteIndex());
            if (wpc.getRouteIndex() == ROUTE_INDEX_EDITABLE_ROUTE) {
                // This is a route under modification: allow append way point
                getMapMenu().addVoyageHandlingWaypointAppendMenuItem(wpc.getRoute(), wpc.getRouteIndex());
                // also allow Way point deletion
                getMapMenu().addVoyageHandlingWaypointDeleteMenuItem(wpc.getRoute(), wpc.getRouteIndex(), wpc.getWpIndex());
            }
            
        } else if (clickedGraphics instanceof RouteLegGraphic) {
            RouteLegGraphic rlg = (RouteLegGraphic) clickedGraphics;
            getMapMenu().sendToSTCC(rlg.getRouteIndex());
            if (rlg.getRouteIndex() == ROUTE_INDEX_EDITABLE_ROUTE && modifiedSTCCRoute != null) {
                // This is a route under modification: allow insert way point
                getMapMenu().addVoyageHandlingLegInsertWaypointMenuItem(modifiedSTCCRoute,
                        rlg.getRouteLeg(), evt.getPoint(), rlg.getRouteIndex());
            }
        }
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

            if (wpc.getRouteIndex() == ROUTE_INDEX_EDITABLE_ROUTE && modifiedSTCCRoute != null) {
                RouteWaypoint routeWaypoint = modifiedSTCCRoute.getWaypoints()
                        .get(wpc.getWpIndex());
                LatLonPoint newLatLon = mapBean.getProjection().inverse(
                        e.getPoint());
                Position newLocation = Position.create(newLatLon.getLatitude(),
                        newLatLon.getLongitude());
                routeWaypoint.setPos(newLocation);

                drawAllRoutes();

                dragging = true;
                return true;
            }
        }

        return false;
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
            drawAllRoutes();
            return true;
        }
        return false;
    }

    /**
     * Called whilst dragging and redraws all routes
     */
    private void drawAllRoutes() {

        // First time modifying
        if (!strategicRouteHandler.isRouteModified()) {
            strategicRouteHandler.modifiedRequest();
        }

        graphics.clear();

        drawModifiedSTCCRoute(false);

        // old STCC in green
        drawRoute(ROUTE_INDEX_STCC_ROUTE, stccRoute, ECDIS_ORANGE, new Color(0.39f, 0.69f, 0.49f, 0.6f), false);

        // Old route in red
        drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, primaryRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f), false);
    }

    /**
     * Redraw a modified STCC route (e.g. when waypoint is appended).
     * 
     * @param clearOld
     *            Specifies if the graphic list should be traversed looking for
     *            and removing any old modified STCC route.
     */
    private void drawModifiedSTCCRoute(boolean clearOld) {
        if (clearOld) {
            // attempt to find the old route in graphics list
            for (int i = 0; i < graphics.size(); i++) {
                OMGraphic omg = graphics.get(i);
                if (omg instanceof RouteGraphic) {
                    RouteGraphic rg = (RouteGraphic) omg;
                    if (rg.getRouteIndex() == ROUTE_INDEX_EDITABLE_ROUTE) {
                        // remove modified STCC route
                        graphics.remove(rg);
                        break;
                    }
                }
            }
        }
        drawRoute(ROUTE_INDEX_EDITABLE_ROUTE, modifiedSTCCRoute, ECDIS_ORANGE,
                new Color(1f, 1f, 0, 0.4f), true);
    }

    /**
     * Main thread run timer
     */
    @Override
    public void run() {
        while (true) {
            Util.sleep(animationTimer);
        }
    }

    /**
     * Called by the {@link StrategicRouteHandler} when a route is accepted
     */
    public void routeAccepted() {
        graphics.clear();
        doPrepare();
    }

    /**
     * Called by the {@link StrategicRouteHandler} when the route is locked for editing
     */
    public void lockEditing() {
        // Draw only original and the recently sent one?
        graphics.clear();

        // New route in green
        drawRoute(ROUTE_INDEX_CURRENT, modifiedSTCCRoute, ECDIS_ORANGE,
                new Color(1f, 1f, 0, 0.4f), true);
    }

    /**
     * Called by the {@link StrategicRouteHandler} to handle re-negotiation
     */
    public void handleReNegotiation(StrategicRouteMessage routeMessage,
            Route previousAcceptedRoute) {
        modifiedSTCCRoute = new Route(routeMessage.getRoute());
        stccRoute = modifiedSTCCRoute.copy();
        primaryRoute = previousAcceptedRoute;

        // Stop the animation
        stopRouteAnimated();

        // Shore agrees
        if (routeMessage.getStatus() == StrategicRouteStatus.AGREED) {
            // Display routeLayer with green
            graphics.clear();
            drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, stccRoute, ECDIS_ORANGE, new Color(0.39f, 0.69f, 0.49f,
                    0.6f), false);

            drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, primaryRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f),
                    false);
        } else if (routeMessage.getStatus() == StrategicRouteStatus.NEGOTIATING) {
            // Draw old one in red and new one in green with lines
            // seperated on new Color(1f, 1f, 0, 0.7f)
            graphics.clear();

            // New route in green
            drawRoute(ROUTE_INDEX_EDITABLE_ROUTE, modifiedSTCCRoute, ECDIS_ORANGE, new Color(0.39f,
                    0.69f, 0.49f, 0.6f), true);

            // Old route in red
            drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, primaryRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f),
                    false);

        } else if (routeMessage.getStatus() == StrategicRouteStatus.REJECTED) {
            // Display route with red - might not be relevant?
            graphics.clear();
            drawRoute(ROUTE_INDEX_EDITABLE_ROUTE, stccRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f),
                    false);
        }
    }

    /**
     * Called by the {@link StrategicRouteHandler} upon receiving a reply
     * @param reply the reply
     */
    public void handleReply(StrategicRouteMessage routeMessage) {

        modifiedSTCCRoute = new Route(routeMessage.getRoute());
        stccRoute = modifiedSTCCRoute.copy();
        // modifiedSTCCRoute = stccRoute;

        // Stop the animation
        stopRouteAnimated();

        // Shore agrees
        if (routeMessage.getStatus() == StrategicRouteStatus.AGREED) {
            // Display routeLayer with green
            graphics.clear();
            drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, stccRoute, ECDIS_ORANGE, new Color(0.39f, 0.69f, 0.49f,
                    0.6f), false);

            drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, primaryRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f),
                    false);
        } else if (routeMessage.getStatus() == StrategicRouteStatus.NEGOTIATING) {
            // Draw old one in red and new one in green with lines
            // seperated on new Color(1f, 1f, 0, 0.7f)
            graphics.clear();

            // New route in green
            drawRoute(ROUTE_INDEX_EDITABLE_ROUTE, modifiedSTCCRoute, ECDIS_ORANGE, new Color(0.39f,
                    0.69f, 0.49f, 0.6f), true);

            // Old route in red
            drawRoute(ROUTE_INDEX_ORIGINAL_ROUTE, primaryRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f),
                    false);

        } else if (routeMessage.getStatus() == StrategicRouteStatus.REJECTED) {
            // Display route with red - might not be relevant?
            graphics.clear();
            drawRoute(ROUTE_INDEX_EDITABLE_ROUTE, stccRoute, ECDIS_ORANGE, new Color(1f, 0, 0, 0.4f),
                    false);
        }

    }

    /**
     * Called by the {@link StrategicRouteHandler} when cancelling a request
     */
    public void cancelRequest() {
        stopRouteAnimated();
        graphics.clear();
        doPrepare();
    }

    /**
     * Returns the modified STCC route
     */
    public Route getModifiedSTCCRoute() {
        return modifiedSTCCRoute;
    }

    /**
     * Called when the voyage has been updated
     */
    @Override
    public void voyageUpdated(VoyageUpdateEvent typeOfUpdate,
            Route updatedVoyage, int routeIndex) {
        if (routeIndex == ROUTE_INDEX_EDITABLE_ROUTE) {
            voyageUpdated();
        }
    }
    
    /**
     * Called when the voyage has been updated
     */
    public void voyageUpdated() {
        // Sanity check
        if (modifiedSTCCRoute == null) {
            return;
        }
        
        // This is a modified STCC route
        // Redraw the route to reflect modifications
        drawModifiedSTCCRoute(true);
        // update dialog to "send modified"
        strategicRouteHandler.modifiedRequest();
    }
}
