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
package dk.dma.epd.ship.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.math.Vector2D;
import dk.dma.epd.common.prototype.gui.metoc.MetocGraphic;
import dk.dma.epd.common.prototype.gui.metoc.MetocInfoPanel;
import dk.dma.epd.common.prototype.gui.metoc.MetocPointGraphic;
import dk.dma.epd.common.prototype.layers.route.ActiveRouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLegGraphic;
import dk.dma.epd.common.prototype.layers.route.WaypointCircle;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.route.SafeHavenArea;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.route.strategic.RecievedRoute;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;

/**
 * Layer for showing routes
 */
public class RouteLayer extends OMGraphicHandlerLayer implements
        IRoutesUpdateListener, MapMouseListener, Runnable {

    private static final long serialVersionUID = 1L;

    private RouteManager routeManager;
    private MainFrame mainFrame;
    private MetocInfoPanel metocInfoPanel;
    private WaypointInfoPanel waypointInfoPanel;
    private MapBean mapBean;

    private OMGraphicList graphics = new OMGraphicList();
    private OMGraphicList metocGraphics = new OMGraphicList();
    private boolean arrowsVisible;
    private OMGraphic closest;
    private OMGraphic selectedGraphic;
    private MetocGraphic routeMetoc;
    private SuggestedRouteGraphic suggestedRoute;
    private float routeWidth = 2.0f;

    private MapMenu routeMenu;
    private boolean dragging;
    SafeHavenArea safeHavenArea = new SafeHavenArea();
    private boolean activeSafeHaven;
    
    private float tolerance;
    

    public RouteLayer() {
        new Thread(this).start();
        routeWidth = EPDShip.getSettings().getNavSettings().getRouteWidth();
        tolerance =  EPDShip.getSettings().getGuiSettings().getMouseSelectTolerance();
    }

    private void updateSafeHaven() {
        if (routeManager.isRouteActive()) {
            ActiveRoute activeRoute = routeManager.getActiveRoute();
            if (activeRoute.isVisible()) {

                if (activeSafeHaven) {
                    graphics.remove(safeHavenArea);
                    if (activeRoute.getActiveWp().getOutLeg() != null) {

                        safeHavenArea.moveSymbol(
                                activeRoute.getSafeHavenLocation(),
                                activeRoute.getSafeHavenBearing(),
                                activeRoute.getActiveWp().getOutLeg()
                                        .getSFWidth(), activeRoute
                                        .getActiveWp().getOutLeg().getSFLen());

                        graphics.add(safeHavenArea);
                    } else {
                        safeHavenArea
                                .moveSymbol(
                                        activeRoute.getSafeHavenLocation(),
                                        activeRoute.getSafeHavenBearing(),
                                        activeRoute
                                                .getWaypoints()
                                                .get(activeRoute.getWaypoints()
                                                        .size() - 2)
                                                .getOutLeg().getSFWidth(),
                                        activeRoute
                                                .getWaypoints()
                                                .get(activeRoute.getWaypoints()
                                                        .size() - 2)
                                                .getOutLeg().getSFLen());
                        graphics.add(safeHavenArea);
                    }
                }

                // safeHavenArea.setVisible(true);

            }
        }

    }

    @Override
    public synchronized void routesChanged(RoutesUpdateEvent e) {
        if (e == RoutesUpdateEvent.ROUTE_MSI_UPDATE) {
            return;
        }

        if (e == null) {
            updateSafeHaven();

            doPrepare();
            return;
        }

        graphics.clear();

        Stroke stroke = new BasicStroke(routeWidth, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 3.0f, 10.0f }, // Dash pattern
                0.0f);
        Stroke activeStroke = new BasicStroke(routeWidth, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f); // Dash phase

        Color ECDISOrange = new Color(213, 103, 45, 255);

        int activeRouteIndex = routeManager.getActiveRouteIndex();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            Route route = routeManager.getRoutes().get(i);
            if (route.isVisible() && i != activeRouteIndex) {
                RouteGraphic routeGraphic;

                if (route.isStccApproved()) {
                    Color greenApproved = new Color(0.39f, 0.69f, 0.49f, 0.6f);

                    routeGraphic = new RouteGraphic(route, i, arrowsVisible,
                            stroke, ECDISOrange, greenApproved, false, false);
                } else {
                    routeGraphic = new RouteGraphic(route, i, arrowsVisible,
                            stroke, ECDISOrange);
                }

                graphics.add(routeGraphic);
            }
        }

        if (routeManager.isRouteActive()) {
            ActiveRoute activeRoute = routeManager.getActiveRoute();
            if (activeRoute.isVisible()) {

                ActiveRouteGraphic activeRouteExtend;

                Route route = routeManager.getRoutes().get(activeRouteIndex);
                if (route.isStccApproved()) {
                    Color greenApproved = new Color(0.39f, 0.69f, 0.49f, 0.6f);

                    activeRouteExtend = new ActiveRouteGraphic(activeRoute,
                            activeRouteIndex, arrowsVisible, activeStroke,
                            Color.RED, greenApproved);
                } else {
                    activeRouteExtend = new ActiveRouteGraphic(activeRoute,
                            activeRouteIndex, arrowsVisible, activeStroke,
                            Color.RED);
                }

                graphics.add(activeRouteExtend);

                if (activeSafeHaven) {
                    // System.out.println("Activating safehaven");
                    if (activeRoute.getActiveWp().getOutLeg() != null) {
                        // System.out.println("outleg isnt zero");
                        safeHavenArea.moveSymbol(
                                activeRoute.getSafeHavenLocation(),
                                activeRoute.getSafeHavenBearing(),
                                activeRoute.getActiveWp().getOutLeg()
                                        .getSFWidth(), activeRoute
                                        .getActiveWp().getOutLeg().getSFLen());
                        graphics.add(safeHavenArea);
                    } else {
                        // System.out.println("outleg is null");
                        safeHavenArea
                                .moveSymbol(
                                        activeRoute.getSafeHavenLocation(),
                                        activeRoute.getSafeHavenBearing(),
                                        activeRoute
                                                .getWaypoints()
                                                .get(activeRoute.getWaypoints()
                                                        .size() - 2)
                                                .getOutLeg().getSFWidth(),
                                        activeRoute
                                                .getWaypoints()
                                                .get(activeRoute.getWaypoints()
                                                        .size() - 2)
                                                .getOutLeg().getSFLen());
                        graphics.add(safeHavenArea);
                    }
                }

                // safeHavenArea.setVisible(true);

            }
        }

        // Handle route metoc
        metocGraphics.clear();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            Route route = routeManager.getRoutes().get(i);
            boolean activeRoute = false;

            if (routeManager.isActiveRoute(i)) {
                route = routeManager.getActiveRoute();
                activeRoute = true;
            }

            if (routeManager.showMetocForRoute(route)) {
                routeMetoc = new MetocGraphic(route, activeRoute, EPDShip
                        .getSettings().getEnavSettings());
                metocGraphics.add(routeMetoc);
            }
        }
        if (metocGraphics.size() > 0) {
            graphics.add(0, metocGraphics);
        }

        // for (AisAdressedRouteSuggestion routeSuggestion : routeManager
        // .getAddressedSuggestedRoutes()) {
        // if (!routeSuggestion.isHidden()) {
        // suggestedRoute = new SuggestedRouteGraphic(routeSuggestion,
        // stroke);
        // graphics.add(suggestedRoute);
        // }
        // }
        for (RecievedRoute routeSuggestion : routeManager.getSuggestedRoutes()) {
            if (!routeSuggestion.isHidden()) {
                suggestedRoute = new SuggestedRouteGraphic(routeSuggestion,
                        stroke);
                graphics.add(suggestedRoute);
            }
        }

        graphics.project(getProjection(), true);

        doPrepare();
    }

    /**
     * Calculate distance between displayed METOC-points projected onto the
     * screen
     * 
     * @param metocGraphic
     *            METOC-graphics containing METOC-points
     * @return The smallest distance between displayed METOC-points projected
     *         onto the screen
     */
    public double calculateMetocDistance(MetocGraphic metocGraphic) {
        List<OMGraphic> forecasts = metocGraphic.getTargets();
        double minDist = 0;
        for (int i = 0; i < forecasts.size(); i++) {
            if (i < forecasts.size() - 2) {
                MetocPointGraphic metocForecastPoint = (MetocPointGraphic) forecasts
                        .get(i);
                MetocPointGraphic metocForecastPointNext = (MetocPointGraphic) forecasts
                        .get(i + 1);
                double lat = metocForecastPoint.getLat();
                double lon = metocForecastPoint.getLon();

                double latnext = metocForecastPointNext.getLat();
                double lonnext = metocForecastPointNext.getLon();

                Point2D current = getProjection().forward(lat, lon);
                Point2D next = getProjection().forward(latnext, lonnext);

                Vector2D vector = new Vector2D(current.getX(), current.getY(),
                        next.getX(), next.getY());

                double newDist = vector.norm();

                if (i == 0) {
                    minDist = newDist;
                }

                if (minDist > newDist) {
                    minDist = newDist;
                }
            }
        }
        return minDist;
    }

    /**
     * Calculate distance between each METOC-point projected onto the screen
     * 
     * @param route
     *            The route which contains metoc data (check for this before!)
     * @return The smallest distance between METOC-points projected onto the
     *         screen
     */
    public double calculateMetocDistance(Route route) {
        MetocForecast routeMetoc = route.getMetocForecast();
        List<MetocForecastPoint> forecasts = routeMetoc.getForecasts();
        double minDist = 0;
        for (int i = 0; i < forecasts.size(); i++) {
            if (i < forecasts.size() - 2) {
                MetocForecastPoint metocForecastPoint = forecasts.get(i);
                MetocForecastPoint metocForecastPointNext = forecasts
                        .get(i + 1);
                double lat = metocForecastPoint.getLat();
                double lon = metocForecastPoint.getLon();

                double latnext = metocForecastPointNext.getLat();
                double lonnext = metocForecastPointNext.getLon();

                Point2D current = getProjection().forward(lat, lon);
                Point2D next = getProjection().forward(latnext, lonnext);

                Vector2D vector = new Vector2D(current.getX(), current.getY(),
                        next.getX(), next.getY());

                double newDist = vector.norm();

                if (i == 0) {
                    minDist = newDist;
                }

                if (minDist > newDist) {
                    minDist = newDist;
                }
            }
        }
        return minDist;
    }

    @Override
    public synchronized OMGraphicList prepare() {
        // System.out.println("Entering RouteLayer.prepare()");
        // long start = System.nanoTime();
        for (OMGraphic omgraphic : graphics) {
            if (omgraphic instanceof RouteGraphic) {
                ((RouteGraphic) omgraphic).showArrowHeads(getProjection()
                        .getScale() < EPDShip.getSettings().getNavSettings()
                        .getShowArrowScale());
            }
        }

        List<OMGraphic> metocList = metocGraphics.getTargets();
        for (OMGraphic omGraphic : metocList) {
            MetocGraphic metocGraphic = (MetocGraphic) omGraphic;
            Route route = metocGraphic.getRoute();
            if (routeManager.showMetocForRoute(route)) {
                double minDist = calculateMetocDistance(route);
                Double tmp = 5.0 / minDist;
                int step = 1;
                if (tmp < 1) {
                    step = 1;
                } else {
                    step = tmp.intValue();
                }
                // System.out.println("minDist = "+minDist+" step = "+step);
                metocGraphic.setStep(step);

                // temporary fix for drawing metoc information
                // All scales will draw all metoc.
                metocGraphic.setStep(1);

                metocGraphic.paintMetoc();

            }
        }

        graphics.project(getProjection());
        // System.out.println("Finished RouteLayer.prepare() in " +
        // EeINS.elapsed(start) + " ms\n---");
        return graphics;
    }

    // @Override
    // public void paint(Graphics g) {
    // System.out.println("Entering RouteLayer.paint)");
    // long start = System.nanoTime();
    // super.paint(g);
    // System.out.println("Finished RouteLayer.paint() in " +
    // EeINS.elapsed(start) + " ms\n---");
    // }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
            routeManager.addListener(this);
        }
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
            metocInfoPanel = new MetocInfoPanel();
            mainFrame.getGlassPanel().add(metocInfoPanel);
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        if (obj instanceof MapMenu) {
            routeMenu = (MapMenu) obj;
        }
        if (waypointInfoPanel == null && routeManager != null
                && mainFrame != null) {
            waypointInfoPanel = new WaypointInfoPanel();
            mainFrame.getGlassPanel().add(waypointInfoPanel);
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == routeManager) {
            routeManager.removeListener(this);
        }
    }

    @Override
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
//        System.out.println("Mouse Clicked");
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        selectedGraphic = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                tolerance);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof SuggestedRouteGraphic
                    || omGraphic instanceof WaypointCircle
                    || omGraphic instanceof RouteLegGraphic) {
                selectedGraphic = omGraphic;
                break;
            }
        }
        routeMenu.setRouteLocation(new Point(e.getX(), e.getY()));

        if (selectedGraphic instanceof SuggestedRouteGraphic) {
//            mainFrame.getGlassPane().setVisible(false);
            waypointInfoPanel.setVisible(false);
            SuggestedRouteGraphic suggestedRoute = (SuggestedRouteGraphic) selectedGraphic;
            RecievedRoute aisSuggestedRoute = suggestedRoute
                    .getRouteSuggestion();
            routeMenu.suggestedRouteMenu(aisSuggestedRoute);
            routeMenu.setVisible(true);
//            routeMenu.show(this, e.getX() - 2, e.getY() - 2);
            routeMenu(e);
            return true;
        }
        if (selectedGraphic instanceof WaypointCircle) {
            WaypointCircle wpc = (WaypointCircle) selectedGraphic;
//            mainFrame.getGlassPane().setVisible(false);
            waypointInfoPanel.setVisible(false);
            routeMenu.routeWaypointMenu(wpc.getRouteIndex(), wpc.getWpIndex());
            routeMenu.setVisible(true);
//             routeMenu.show(this, e.getX() - 2, e.getY() - 2);
            routeMenu(e);
            return true;
        }
        if (selectedGraphic instanceof RouteLegGraphic) {
            RouteLegGraphic rlg = (RouteLegGraphic) selectedGraphic;
//            mainFrame.getGlassPane().setVisible(false);
            waypointInfoPanel.setVisible(false);
            routeMenu.routeLegMenu(rlg.getRouteIndex(), rlg.getRouteLeg(),
                    e.getPoint());
            routeMenu.setVisible(true);
            // routeMenu.show(this, e.getX() - 2, e.getY() - 2);
            routeMenu(e);
            return true;
        }

        return false;
    }

    private void routeMenu(MouseEvent arg0) {
        if (EPDShip.getMainFrame().getHeight() < arg0.getYOnScreen()
                + routeMenu.getHeight()) {
            routeMenu.show(this, arg0.getX() - 2,
                    arg0.getY() - routeMenu.getHeight());
        } else {
            routeMenu.show(this, arg0.getX() - 2, arg0.getY() - 2);
        }

    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
//        System.out.println("Mouse dragged!");
        if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }

        if (!dragging) {
//            mainFrame.getGlassPane().setVisible(false);
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
            if (routeManager.getActiveRouteIndex() != wpc.getRouteIndex()) {
                RouteWaypoint routeWaypoint = wpc.getRoute().getWaypoints()
                        .get(wpc.getWpIndex());
                LatLonPoint newLatLon = mapBean.getProjection().inverse(
                        e.getPoint());
                Position newLocation = Position.create(newLatLon.getLatitude(),
                        newLatLon.getLongitude());
                routeWaypoint.setPos(newLocation);

                if (wpc.getRoute().isStccApproved()) {

                    wpc.getRoute().setStccApproved(false);
                    try {
                        wpc.getRoute().setName(
                                wpc.getRoute().getName().split(":")[1].trim());
                    } catch (Exception e2) {
                        System.out
                                .println("Failed to remove STCC Approved part of name");
                    }
                }
                routesChanged(RoutesUpdateEvent.ROUTE_WAYPOINT_MOVED);
                dragging = true;
                return true;
            } else {
                // Attemping to drag an active route, make a route copy and drag
                // that one

                int dialogresult = JOptionPane
                        .showConfirmDialog(
                                EPDShip.getMainFrame(),
                                "You are trying to edit an active route \nDo you wish to make a copy to edit?",
                                "Route Editing", JOptionPane.YES_OPTION);
                if (dialogresult == JOptionPane.YES_OPTION) {
                    Route route = routeManager.getRoute(
                            routeManager.getActiveRouteIndex()).copy();
                    route.setName(route.getName() + " copy");
                    routeManager.addRoute(route);
                    // dragging = true;

                    // routesChanged(RoutesUpdateEvent.ROUTE_ADDED);
                }
                return true;

                // RouteWaypoint routeWaypoint = route.getWaypoints().get(
                // wpc.getWpIndex());
                // LatLonPoint newLatLon = mapBean.getProjection().inverse(
                // e.getPoint());
                // Position newLocation =
                // Position.create(newLatLon.getLatitude(),
                // newLatLon.getLongitude());
                // routeWaypoint.setPos(newLocation);
                // routesChanged(RoutesUpdateEvent.ROUTE_WAYPOINT_MOVED);
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
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                2.0f);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof MetocPointGraphic
                    || omGraphic instanceof WaypointCircle) {
                newClosest = omGraphic;
                break;
            }
        }

        if (routeMetoc != null && metocInfoPanel != null) {
            if (newClosest != closest) {
                if (newClosest == null) {
                    metocInfoPanel.setVisible(false);
//                    mainFrame.getGlassPane().setVisible(false);
                    waypointInfoPanel.setVisible(false);
                    closest = null;
                } else {
                    if (newClosest instanceof MetocPointGraphic) {
                        closest = newClosest;
                        MetocPointGraphic pointGraphic = (MetocPointGraphic) newClosest;
                        MetocForecastPoint pointForecast = pointGraphic
                                .getMetocPoint();
                        Point containerPoint = SwingUtilities.convertPoint(
                                mapBean, e.getPoint(), mainFrame);
                        metocInfoPanel.setPos((int) containerPoint.getX(),
                                (int) containerPoint.getY());
                        metocInfoPanel.showText(pointForecast, pointGraphic
                                .getMetocGraphic().getRoute()
                                .getRouteMetocSettings());
                        waypointInfoPanel.setVisible(false);
                        mainFrame.getGlassPane().setVisible(true);
                        return true;
                    }
                }
            }
        }

        if (newClosest != closest) {
            if (newClosest instanceof WaypointCircle) {
                closest = newClosest;
                WaypointCircle waypointCircle = (WaypointCircle) closest;
                Point containerPoint = SwingUtilities.convertPoint(mapBean,
                        e.getPoint(), mainFrame);
                waypointInfoPanel.setPos((int) containerPoint.getX(),
                        (int) containerPoint.getY() - 10);
                waypointInfoPanel.showWpInfo(waypointCircle.getRoute(),
                        waypointCircle.getWpIndex());
                mainFrame.getGlassPane().setVisible(true);
                metocInfoPanel.setVisible(false);
                return true;
            } else {
                waypointInfoPanel.setVisible(false);
                mainFrame.getGlassPane().setVisible(false);
                closest = null;
                return true;
            }
        }
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
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_MSI_UPDATE);
            return true;
        }
        return false;
    }

    public void toggleSafeHaven() {
        activeSafeHaven = !activeSafeHaven;
        safeHavenArea.setVisible(activeSafeHaven);
        routesChanged(null);
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(1000);

            routesChanged(null);

        }
    }
}
