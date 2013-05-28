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
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.common.prototype.layers.route.MetocGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLegGraphic;
import dk.dma.epd.common.prototype.layers.route.WaypointCircle;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.monalisa.MonaLisaHandler;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends OMGraphicHandlerLayer implements
        MapMouseListener, Runnable {

    private static final long serialVersionUID = 1L;

    private OMGraphicList graphics = new OMGraphicList();
    private float routeWidth = 2.0f;
    private Timer routeAnimatorTimer;
    Color ECDISOrange = new Color(213, 103, 45, 255);

    private MapBean mapBean;
    private MapMenu routeMenu;

    private Route primaryRoute;
    private Route stccRoute;
    private Route modifiedSTCCRoute;
    private MonaLisaHandler monaLisaHandler;

    private boolean dragging;

    private int animationTimer = 100;

    // private OMGraphic closest;
    private OMGraphic selectedGraphic;

    // private boolean modified;

    public VoyageLayer() {

    }

    public void startRouteNegotiation(Route route) {

        this.primaryRoute = route;

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
        RouteGraphic routeGraphic = new RouteGraphic(route, 0, true, stroke,
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
                color, broadLineColor, circleDash);
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
        // if (obj instanceof VoyageManager) {
        // voyageManager = (VoyageManager)obj;
        // voyageManager.addListener(this);
        // }

        // if (obj instanceof JMapFrame){
        // if (waypointInfoPanel == null && voyageManager != null) {
        // waypointInfoPanel = new WaypointInfoPanel();
        // }
        //
        // jMapFrame = (JMapFrame) obj;
        // metocInfoPanel = new MetocInfoPanel();
        // jMapFrame.getGlassPanel().add(metocInfoPanel);
        // jMapFrame.getGlassPanel().add(waypointInfoPanel);
        // }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        } else if (obj instanceof MapMenu) {
            routeMenu = (MapMenu) obj;
        } else if (obj instanceof MonaLisaHandler) {
            monaLisaHandler = (MonaLisaHandler) obj;
        }

    }

    @Override
    public void findAndUndo(Object obj) {

    }

    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
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
                    5.0f);
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
                routeMenu.sendToSTCC(wpc.getRouteIndex());
                routeMenu.setVisible(true);
                routeMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }
            if (selectedGraphic instanceof RouteLegGraphic) {
                RouteLegGraphic rlg = (RouteLegGraphic) selectedGraphic;
                // waypointInfoPanel.setVisible(false);
                routeMenu.sendToSTCC(rlg.getRouteIndex());
                routeMenu.setVisible(true);
                routeMenu.show(this, e.getX() - 2, e.getY() - 2);
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
                    5.0f);
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
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved() {
        graphics.deselect();
        repaint();
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        // OMGraphic newClosest = null;
        // OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
        // 2.0f);
        //
        // for (OMGraphic omGraphic : allClosest) {
        // if (omGraphic instanceof MetocPointGraphic || omGraphic instanceof
        // WaypointCircle) {
        // newClosest = omGraphic;
        // break;
        // }
        // }
        //
        // if (routeMetoc != null && metocInfoPanel != null) {
        // if (newClosest != closest) {
        // if (newClosest == null) {
        // metocInfoPanel.setVisible(false);
        // waypointInfoPanel.setVisible(false);
        // closest = null;
        // } else {
        // if (newClosest instanceof MetocPointGraphic) {
        // closest = newClosest;
        // MetocPointGraphic pointGraphic = (MetocPointGraphic)newClosest;
        // MetocForecastPoint pointForecast = pointGraphic.getMetocPoint();
        // Point containerPoint = SwingUtilities.convertPoint(mapBean,
        // e.getPoint(), jMapFrame);
        // metocInfoPanel.setPos((int)containerPoint.getX(),
        // (int)containerPoint.getY());
        // metocInfoPanel.showText(pointForecast,
        // pointGraphic.getMetocGraphic().getRoute().getRouteMetocSettings());
        // waypointInfoPanel.setVisible(false);
        // jMapFrame.getGlassPane().setVisible(true);
        // return true;
        // }
        // }
        // }
        // }
        //
        // if (newClosest != closest) {
        // if (newClosest instanceof WaypointCircle) {
        // closest = newClosest;
        // WaypointCircle waypointCircle = (WaypointCircle)closest;
        // Point containerPoint = SwingUtilities.convertPoint(mapBean,
        // e.getPoint(), jMapFrame);
        // waypointInfoPanel.setPos((int)containerPoint.getX(),
        // (int)containerPoint.getY() - 10);
        // waypointInfoPanel.showWpInfo(waypointCircle.getRoute(),
        // waypointCircle.getWpIndex());
        // jMapFrame.getGlassPane().setVisible(true);
        // metocInfoPanel.setVisible(false);
        // return true;
        // } else {
        // waypointInfoPanel.setVisible(false);
        // closest = null;
        // return true;
        // }
        // }
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
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
        drawRoute(2, modifiedSTCCRoute, ECDISOrange, new Color(1f, 1f, 0, 0.4f), true);

        // old STCC in green
        drawRoute(1, stccRoute, ECDISOrange, new Color(0.39f, 0.69f,
                0.49f, 0.6f), false);

        // Old route in red
        drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                false);

        // draw original - id 0

        // draw stcc - id 1

        // draw modified stcc - id 2

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
        drawRoute(1, modifiedSTCCRoute, ECDISOrange, 
                new Color(1f, 1f, 0, 0.4f), true);
        // Old route in red
        drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                false);

    }

    
    public void handleReNegotiation(MonaLisaRouteRequestReply reply, Route previousAcceptedRoute) {
        modifiedSTCCRoute = new Route(reply.getRoute());
        stccRoute = modifiedSTCCRoute.copy();
        this.primaryRoute = previousAcceptedRoute;
        
        // Stop the animation
        stopRouteAnimated();

        // Shore agrees
        if (reply.getStatus() == MonaLisaRouteStatus.AGREED) {
            // Display routeLayer with green
            graphics.clear();
            drawRoute(0, stccRoute, ECDISOrange, new Color(0.39f, 0.69f, 0.49f,
                    0.6f), false);

            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        } else if (reply.getStatus() == MonaLisaRouteStatus.NEGOTIATING) {
            // Draw old one in red and new one in green with lines
            // seperated on new Color(1f, 1f, 0, 0.7f)
            graphics.clear();

            // New route in green
            drawRoute(2, modifiedSTCCRoute, ECDISOrange, new Color(0.39f,
                    0.69f, 0.49f, 0.6f), true);

            // Old route in red
            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);

        } else if (reply.getStatus() == MonaLisaRouteStatus.REJECTED) {
            // Display route with red - might not be relevant?
            graphics.clear();
            drawRoute(2, stccRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        }
    }
    
    public void handleReply(MonaLisaRouteRequestReply reply) {

        modifiedSTCCRoute = new Route(reply.getRoute());
        stccRoute = modifiedSTCCRoute.copy();
        // modifiedSTCCRoute = stccRoute;

        // Stop the animation
        stopRouteAnimated();

        // Shore agrees
        if (reply.getStatus() == MonaLisaRouteStatus.AGREED) {
            // Display routeLayer with green
            graphics.clear();
            drawRoute(0, stccRoute, ECDISOrange, new Color(0.39f, 0.69f, 0.49f,
                    0.6f), false);

            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);
        } else if (reply.getStatus() == MonaLisaRouteStatus.NEGOTIATING) {
            // Draw old one in red and new one in green with lines
            // seperated on new Color(1f, 1f, 0, 0.7f)
            graphics.clear();

            // New route in green
            drawRoute(2, modifiedSTCCRoute, ECDISOrange, new Color(0.39f,
                    0.69f, 0.49f, 0.6f), true);

            // Old route in red
            drawRoute(0, primaryRoute, ECDISOrange, new Color(1f, 0, 0, 0.4f),
                    false);

        } else if (reply.getStatus() == MonaLisaRouteStatus.REJECTED) {
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

}
