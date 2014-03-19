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
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.metoc.MetocGraphic;
import dk.dma.epd.common.prototype.layers.route.ActiveRouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLayerCommon;
import dk.dma.epd.common.prototype.layers.route.SafeHavenArea;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.SuggestedRoute;

/**
 * Layer for showing routes
 */
public class RouteLayer extends RouteLayerCommon implements Runnable, IOwnShipListener {

    private static final long serialVersionUID = 1L;

    private SuggestedRouteGraphic suggestedRoute;
    private SafeHavenArea safeHavenArea = new SafeHavenArea();
    private boolean activeSafeHaven;

    /**
     * Constructor
     */
    public RouteLayer() {
        super();

        // Register ship-specific classes that will trigger the map menu
        registerMapMenuClasses(SuggestedRouteGraphic.class);

        new Thread(this).start();
    }

    /**
     * Returns a reference to the map menu
     * 
     * @return a reference to the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu) mapMenu;
    }

    /**
     * Called by {@linkplain #routesChanged()} and will update the safe haven
     */
    private void updateSafeHaven() {
        if (routeManager.isRouteActive()) {
            ActiveRoute activeRoute = routeManager.getActiveRoute();
            if (activeRoute.isVisible()) {

                if (activeSafeHaven) {
                    graphics.remove(safeHavenArea);
                    if (activeRoute.getActiveWp().getOutLeg() != null) {

                        safeHavenArea.moveSymbol(activeRoute.getSafeHavenLocation(),

                        activeRoute.getSafeHavenBearing(),

                        activeRoute.getActiveWp().getOutLeg().getSFWidth(), activeRoute.getActiveWp().getOutLeg().getSFLen());

                        graphics.add(safeHavenArea);
                    } else {
                        safeHavenArea.moveSymbol(activeRoute.getSafeHavenLocation(), activeRoute.getSafeHavenBearing(),
                                activeRoute.getSafeHavenWidth(),

                                // .getWaypoints()
                                // .get(activeRoute.getWaypoints()
                                // .size() - 2)
                                // .getOutLeg().getSFWidth(),
                                activeRoute.getSafeHavenLength()

                        // .getWaypoints()
                        // .get(activeRoute.getWaypoints()
                        // .size() - 2)
                        // .getOutLeg().getSFLen()
                                );
                        graphics.add(safeHavenArea);
                    }
                }
            }
        }
    }

    /**
     * Called when routes are changed
     * 
     * @param e
     *            the routes update event
     */
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

        float routeWidth = EPD.getInstance().getSettings().getNavSettings().getRouteWidth();
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

                    routeGraphic = new RouteGraphic(route, i, arrowsVisible, stroke, ECDISOrange, greenApproved, false, false);
                } else {
                    routeGraphic = new RouteGraphic(route, i, arrowsVisible, stroke, ECDISOrange);
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

                    activeRouteExtend = new ActiveRouteGraphic(activeRoute, activeRouteIndex, arrowsVisible, activeStroke,
                            Color.RED, greenApproved);
                } else {
                    activeRouteExtend = new ActiveRouteGraphic(activeRoute, activeRouteIndex, arrowsVisible, activeStroke,
                            Color.RED);
                }

                graphics.add(activeRouteExtend);

                if (activeSafeHaven) {
                    // System.out.println("Activating safehaven");
                    if (activeRoute.getActiveWp().getOutLeg() != null) {
                        // System.out.println("outleg isnt zero");
                        safeHavenArea.moveSymbol(activeRoute.getSafeHavenLocation(), activeRoute.getSafeHavenBearing(), activeRoute
                                .getActiveWp().getOutLeg().getSFWidth(), activeRoute.getActiveWp().getOutLeg().getSFLen());
                        graphics.add(safeHavenArea);
                    } else {
                        // System.out.println("outleg is null");
                        safeHavenArea.moveSymbol(activeRoute.getSafeHavenLocation(), activeRoute.getSafeHavenBearing(), activeRoute
                                .getWaypoints().get(activeRoute.getWaypoints().size() - 2).getOutLeg().getSFWidth(), activeRoute
                                .getWaypoints().get(activeRoute.getWaypoints().size() - 2).getOutLeg().getSFLen());
                        graphics.add(safeHavenArea);
                    }
                }
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
                routeMetoc = new MetocGraphic(route, activeRoute, EPDShip.getInstance().getSettings().getEnavSettings());
                metocGraphics.add(routeMetoc);
            }
        }
        if (metocGraphics.size() > 0) {
            graphics.add(0, metocGraphics);
        }

        for (SuggestedRoute routeSuggestion : ((RouteManager) routeManager).getSuggestedRoutes()) {
            if (!routeSuggestion.isHidden()) {
                suggestedRoute = new SuggestedRouteGraphic(routeSuggestion, stroke);
                graphics.add(suggestedRoute);
            }
        }

        graphics.project(getProjection(), true);

        doPrepare();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {

        // Let super handle common map menu cases
        super.initMapMenu(clickedGraphics, evt);

        if (clickedGraphics instanceof SuggestedRouteGraphic) {
            SuggestedRouteGraphic suggestedRoute = (SuggestedRouteGraphic) clickedGraphics;
            SuggestedRoute aisSuggestedRoute = suggestedRoute.getRouteSuggestion();
            getMapMenu().suggestedRouteMenu(aisSuggestedRoute);
        }
    }

    /**
     * Toggles the display of the safe haven zone
     */
    public void toggleSafeHaven() {
        activeSafeHaven = !activeSafeHaven;
        safeHavenArea.setVisible(activeSafeHaven);
        routesChanged(null);
    }

    /**
     * Main thread run method
     */
    @Override
    public void run() {
        while (true) {
            Util.sleep(1000);
            routesChanged(null);
        }
    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof OwnShipHandler) {
            ((OwnShipHandler) obj).addListener(this);

        }
    }

    @Override
    public void ownShipUpdated(OwnShipHandler ownShipHandler) {
        // Update
        safeHavenArea.shipPositionChanged(ownShipHandler.getPntData().getPosition());
    }

    @Override
    public void ownShipChanged(VesselTarget oldValue, VesselTarget newValue) {
        

    }
}
