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
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLegGraphic;
import dk.dma.epd.common.prototype.layers.route.WaypointCircle;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.voyage.IVoyageUpdateListener;
import dk.dma.epd.common.prototype.model.voyage.VoyageUpdateEvent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.layers.GeneralLayer;
import dk.dma.epd.ship.route.strategic.StrategicRouteExchangeHandler;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends GeneralLayer implements
        Runnable, IVoyageUpdateListener {

    private static final long serialVersionUID = 1L;

    private float routeWidth = 2.0f;
    private Timer routeAnimatorTimer;
    Color ECDISOrange = new Color(213, 103, 45, 255);

    private Route primaryRoute;
    private Route stccRoute;
    private Route modifiedSTCCRoute;
    private StrategicRouteExchangeHandler monaLisaHandler;

    private boolean dragging;

    private int animationTimer = 100;
    private VoyageHandlingMouseOverPanel voyageHandlingMouseOverPanel = new VoyageHandlingMouseOverPanel();

    private OMGraphic closest;
    private OMGraphic selectedGraphic;

    private float tolerance;

    // private boolean modified;

    public VoyageLayer() {
        // Register this layer as listener for voyage update events
        EPDShip.getInstance().getVoyageEventDispatcher().registerListener(this);
    }

    public void startRouteNegotiation(Route route) {

        this.primaryRoute = route;
        tolerance = EPDShip.getInstance().getSettings().getGuiSettings()
                .getMouseSelectTolerance();

        // Added the route as green, original recieved one
        drawRoute(route, ECDISOrange);

        startRouteAnimation();
    }

    private void drawRoute(Route route, Color color) {

        Stroke stroke = new BasicStroke(routeWidth, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 3.0f, 10.0f }, // Dash pattern
                0.0f);

        // Added the route as green, original recieved one
        RouteGraphic routeGraphic = new RouteGraphic(route, 3, true, stroke,
                color);
        graphics.add(routeGraphic);
        graphics.project(getProjection(), true);
        doPrepare();

        // RouteGraphic(Route route, int routeIndex, boolean arrowsVisible,
        // Stroke stroke, Color color, Color broadLineColor, boolean circleDash)
    }

    private void drawRoute(int id, Route route, Color color,
            Color broadLineColor, boolean circleDash) {

        Stroke stroke = new BasicStroke(routeWidth, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 3.0f, 10.0f }, // Dash pattern
                0.0f);

        // Added the route as green, original recieved one
        RouteGraphic routeGraphic = new RouteGraphic(route, id, true, stroke,
                color, broadLineColor, circleDash, true);
        graphics.add(routeGraphic);
        graphics.project(getProjection(), true);
        doPrepare();

    }

    private void startRouteAnimation() {

        // System.out.println("Starting route animation");

        RouteGraphic animatedRoute = null;

        for (int i = 0; i < graphics.size(); i++) {

            if (graphics.get(i) instanceof RouteGraphic) {
                if (primaryRoute == ((RouteGraphic) graphics.get(i)).getRoute()) {
                    // System.out.println("Animate the specific one");
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

    private void stopRouteAnimated() {
        routeAnimatorTimer.cancel();
    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MainFrame) {
            getMainFrame().getGlassPanel().add(voyageHandlingMouseOverPanel);
        } else if (obj instanceof StrategicRouteExchangeHandler) {
            monaLisaHandler = (StrategicRouteExchangeHandler) obj;
        }

    }

    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        if (this.isVisible()) {

            // System.out.println("Right click!");

            selectedGraphic = null;
            OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                    tolerance);
            for (OMGraphic omGraphic : allClosest) {
                if (omGraphic instanceof WaypointCircle
                        || omGraphic instanceof RouteLegGraphic) {
                    selectedGraphic = omGraphic;
                    break;
                }
            }

            if (selectedGraphic instanceof WaypointCircle) {
                WaypointCircle wpc = (WaypointCircle) selectedGraphic;

                // waypointInfoPanel.setVisible(false);
                getMapMenu().sendToSTCC(wpc.getRouteIndex());
                if (wpc.getRouteIndex() == 2) {
                    // This is a route under modification: allow append waypoint
                    this.getMapMenu().addVoyageHandlingWaypointAppendMenuItem(wpc.getRoute(), wpc.getRouteIndex());
                    // also allow Waypoint deletion
                    this.getMapMenu().addVoyageHandlingWaypointDeleteMenuItem(wpc.getRoute(), wpc.getRouteIndex(), wpc.getWpIndex());
                }

                getMapMenu().setVisible(true);
                getMapMenu().show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }
            if (selectedGraphic instanceof RouteLegGraphic) {
                RouteLegGraphic rlg = (RouteLegGraphic) selectedGraphic;
                // waypointInfoPanel.setVisible(false);
                getMapMenu().sendToSTCC(rlg.getRouteIndex());
                if(rlg.getRouteIndex() == 2 && this.modifiedSTCCRoute != null) {
                    // This is a route under modification: allow insert waypoint
                    getMapMenu().addVoyageHandlingLegInsertWaypointMenuItem(this.modifiedSTCCRoute,
                            rlg.getRouteLeg(), e.getPoint(), rlg.getRouteIndex());
                }
                
                getMapMenu().setVisible(true);
                getMapMenu().show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }

        if (!dragging) {
            selectedGraphic = null;
            OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                    tolerance);
            for (OMGraphic omGraphic : allClosest) {
                if (omGraphic instanceof WaypointCircle) {
                    selectedGraphic = omGraphic;
                    break;
                }
            }
        }

        if (selectedGraphic instanceof WaypointCircle) {
            WaypointCircle wpc = (WaypointCircle) selectedGraphic;

            if (wpc.getRouteIndex() == 2 && modifiedSTCCRoute != null) {
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

    @Override
    public void mouseMoved() {
        graphics.deselect();
        repaint();
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                tolerance);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof RouteLegGraphic
                    || omGraphic instanceof WaypointCircle) {
                newClosest = omGraphic;
                break;
            }
        }

        if (newClosest != closest) {
            if (newClosest instanceof WaypointCircle
                    || newClosest instanceof RouteLegGraphic) {
                closest = newClosest;

                if (closest instanceof WaypointCircle) {
                    WaypointCircle waypointCircle = (WaypointCircle) closest;
                    Point containerPoint = SwingUtilities.convertPoint(mapBean,
                            e.getPoint(), mainFrame);
                    voyageHandlingMouseOverPanel.setPos(
                            (int) containerPoint.getX(),
                            (int) containerPoint.getY() - 10);

                    // System.out.println("Waypoint Circle info: "
                    // + waypointCircle.getRouteIndex());

                    voyageHandlingMouseOverPanel.showType(waypointCircle
                            .getRouteIndex());
                } else {
                    RouteLegGraphic waypointLeg = (RouteLegGraphic) closest;
                    Point containerPoint = SwingUtilities.convertPoint(mapBean,
                            e.getPoint(), mainFrame);
                    voyageHandlingMouseOverPanel.setPos(
                            (int) containerPoint.getX(),
                            (int) containerPoint.getY() - 10);

                    // System.out.println("Waypoint Circle info: "
                    // + waypointLeg.getRouteIndex());

                    voyageHandlingMouseOverPanel.showType(waypointLeg
                            .getRouteIndex());
                }

                mainFrame.getGlassPane().setVisible(true);
                return true;
            } else {
                voyageHandlingMouseOverPanel.setVisible(false);
                closest = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            drawAllRoutes();
            return true;
        }
        return false;
    }

    private void drawAllRoutes() {

        // First time modifying
        if (!monaLisaHandler.isRouteModified()) {
            // System.out.println("We are modifying");
            monaLisaHandler.modifiedRequest();
            // modifiedSTCCRoute.setName("Modified Reply Route");
        }

        // modifiedSTCCRoute.calcAllWpEta();

        graphics.clear();

        // New route in yellow
        // drawRoute(2, modifiedSTCCRoute, ECDISOrange,
        // new Color(1f, 1f, 0, 0.4f), true);
        this.drawModifiedSTCCRoute(false);

        // old STCC in green
        drawRoute(1, stccRoute, ECDISOrange, new Color(0.39f, 0.69f, 0.49f,
                0.6f), false);

        // Old route in red
        drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                false);

        // draw original - id 0

        // draw stcc - id 1

        // draw modified stcc - id 2

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
            for (int i = 0; i < this.graphics.size(); i++) {
                OMGraphic omg = this.graphics.get(i);
                if (omg instanceof RouteGraphic) {
                    RouteGraphic rg = (RouteGraphic) omg;
                    if (rg.getRouteIndex() == 2) {
                        // remove modified STCC route
                        this.graphics.remove(rg);
                        break;
                    }
                }
            }
        }
        drawRoute(2, modifiedSTCCRoute, ECDISOrange,
                new Color(1f, 1f, 0, 0.4f), true);
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(animationTimer);
        }
    }

    public void routeAccepted() {
        graphics.clear();
        doPrepare();
    }

    public void lockEditing() {

        // Draw only original and the recently sent one?
        graphics.clear();

        // New route in green
        drawRoute(3, modifiedSTCCRoute, ECDISOrange,
                new Color(1f, 1f, 0, 0.4f), true);

        // Do we need to show this?
        // Old route in red
        // drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
        // false);

    }

    public void handleReNegotiation(StrategicRouteRequestReply reply,
            Route previousAcceptedRoute) {
        modifiedSTCCRoute = new Route(reply.getRoute());
        stccRoute = modifiedSTCCRoute.copy();
        this.primaryRoute = previousAcceptedRoute;

        // Stop the animation
        stopRouteAnimated();

        // Shore agrees
        if (reply.getStatus() == StrategicRouteStatus.AGREED) {
            // Display routeLayer with green
            graphics.clear();
            drawRoute(0, stccRoute, ECDISOrange, new Color(0.39f, 0.69f, 0.49f,
                    0.6f), false);

            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        } else if (reply.getStatus() == StrategicRouteStatus.NEGOTIATING) {
            // Draw old one in red and new one in green with lines
            // seperated on new Color(1f, 1f, 0, 0.7f)
            graphics.clear();

            // New route in green
            drawRoute(2, modifiedSTCCRoute, ECDISOrange, new Color(0.39f,
                    0.69f, 0.49f, 0.6f), true);

            // Old route in red
            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);

        } else if (reply.getStatus() == StrategicRouteStatus.REJECTED) {
            // Display route with red - might not be relevant?
            graphics.clear();
            drawRoute(2, stccRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        }
    }

    public void handleReply(StrategicRouteRequestReply reply) {

        modifiedSTCCRoute = new Route(reply.getRoute());
        stccRoute = modifiedSTCCRoute.copy();
        // modifiedSTCCRoute = stccRoute;

        // Stop the animation
        stopRouteAnimated();

        // Shore agrees
        if (reply.getStatus() == StrategicRouteStatus.AGREED) {
            // Display routeLayer with green
            graphics.clear();
            drawRoute(0, stccRoute, ECDISOrange, new Color(0.39f, 0.69f, 0.49f,
                    0.6f), false);

            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        } else if (reply.getStatus() == StrategicRouteStatus.NEGOTIATING) {
            // Draw old one in red and new one in green with lines
            // seperated on new Color(1f, 1f, 0, 0.7f)
            graphics.clear();

            // New route in green
            drawRoute(2, modifiedSTCCRoute, ECDISOrange, new Color(0.39f,
                    0.69f, 0.49f, 0.6f), true);

            // Old route in red
            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);

        } else if (reply.getStatus() == StrategicRouteStatus.REJECTED) {
            // Display route with red - might not be relevant?
            graphics.clear();
            drawRoute(2, stccRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        }

    }

    public void cancelRequest() {
        stopRouteAnimated();
        graphics.clear();
        doPrepare();
    }

    public Route getModifiedSTCCRoute() {
        return modifiedSTCCRoute;
    }

    @Override
    public void voyageUpdated(VoyageUpdateEvent typeOfUpdate,
            Route updatedVoyage, int routeIndex) {
        if (routeIndex == 2) {
            // This is a modified STCC route
            // Redraw the route to reflect modifications
            this.drawModifiedSTCCRoute(true);
            // update dialog to "send modified"
            this.monaLisaHandler.modifiedRequest();
        }

    }

}
