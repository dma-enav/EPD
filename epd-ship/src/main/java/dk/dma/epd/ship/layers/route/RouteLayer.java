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
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon.RouteSuggestionListener;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.ownship.IOwnShipListener;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.service.RouteSuggestionHandler;

/**
 * Layer for showing routes
 */
public class RouteLayer extends RouteLayerCommon implements IOwnShipListener,
        RouteSuggestionListener {

    private static final long serialVersionUID = 1L;

    private SafeHavenArea safeHavenArea = new SafeHavenArea();
    private boolean activeSafeHaven;

    /**
     * Constructor
     */
    public RouteLayer() {
        super();

        // Register ship-specific classes that will trigger the map menu
        registerMapMenuClasses(RouteSuggestionGraphic.class);

        // Repaint every five seconds, necessary for safehaven and route color
        startTimer(5000, 5000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void timerAction() {
        routesChanged(null);
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
                    safeHavenArea.moveSymbol(
                            activeRoute.getSafeHavenLocation(),
                            activeRoute.getSafeHavenBearing(),
                            activeRoute.getSafeHavenWidth(),
                            activeRoute.getSafeHavenLength());
                    graphics.add(safeHavenArea);
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

        // if (e == null) {
        updateSafeHaven();
        doPrepare();
        // return;
        // }

        graphics.clear();

        float routeWidth = EPD.getInstance().getSettings().getNavSettings()
                .getRouteWidth();
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
                    activeRoute.getSafeHavenLocation();

                    safeHavenArea.moveSymbol(
                            activeRoute.getSafeHavenLocation(),
                            activeRoute.getSafeHavenBearing(),
                            activeRoute.getSafeHavenWidth(),
                            activeRoute.getSafeHavenLength());
                    graphics.add(safeHavenArea);
                }

                if (EPDShip.getInstance().getPosition() != null) {
                    activeRouteExtend.updateActiveWpLine(EPDShip.getInstance()
                            .getPosition());
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
                routeMetoc = new MetocGraphic(route, activeRoute, EPDShip
                        .getInstance().getSettings().getEnavSettings());
                metocGraphics.add(routeMetoc);
            }
        }
        if (metocGraphics.size() > 0) {
            graphics.add(0, metocGraphics);
        }

        for (RouteSuggestionData routeSuggestion : EPDShip.getInstance()
                .getRouteSuggestionHandler().getSortedRouteSuggestions()) {
            if (routeSuggestion.getRoute().isVisible()) {
                graphics.add(new RouteSuggestionGraphic(routeSuggestion, stroke));
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

        if (clickedGraphics instanceof RouteSuggestionGraphic) {
            RouteSuggestionGraphic suggestedRoute = (RouteSuggestionGraphic) clickedGraphics;
            getMapMenu().routeSuggestionMenu(
                    suggestedRoute.getRouteSuggestion());
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
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof OwnShipHandler) {
            ((OwnShipHandler) obj).addListener(this);
        } else if (obj instanceof RouteSuggestionHandler) {
            ((RouteSuggestionHandler) obj).addRouteSuggestionListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ownShipUpdated(OwnShipHandler ownShipHandler) {
        // Update
        safeHavenArea.shipPositionChanged(ownShipHandler.getPntData()
                .getPosition());

        if (routeManager.isRouteActive()) {
            for (int i = 0; i < graphics.size(); i++) {
                if (graphics.get(i) instanceof ActiveRouteGraphic) {
                    ActiveRouteGraphic activeRouteGraphics = (ActiveRouteGraphic) graphics
                            .get(i);

                    activeRouteGraphics.updateActiveWpLine(ownShipHandler
                            .getPntData().getPosition());
                    break;
                }
            }
        }
        doPrepare();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ownShipChanged(VesselTarget oldValue, VesselTarget newValue) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void routeUpdate() {
        routesChanged(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
    }

}
