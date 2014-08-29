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
package dk.dma.epd.ship.layers.intendedroute;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Date;

import org.joda.time.DateTime;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteWpCircle;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.ship.EPDShip;

public class IntendedRouteLayer extends IntendedRouteLayerCommon {

    private static final long serialVersionUID = 3001820678205337239L;

    private IntendedRouteComparisonGraphics comparisonLine;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {

        graphics.remove(comparisonLine);

        if (newClosest instanceof IntendedRouteWpCircle) {
            intendedRouteInfoPanel.showWpInfo((IntendedRouteWpCircle) newClosest);
        } else {

            if (newClosest instanceof IntendedRouteLegGraphic) {
                // lets user see ETA continually along route leg

                Point2D worldLocation = chartPanel.getMap().getProjection().inverse(evt.getPoint());

                ActiveRoute activeRoute = EPDShip.getInstance().getRouteManager().getActiveRoute();

                Position activeRoutePosition = null;

                closest = dummyCircle;

                if (activeRoute != null) {

                    int legIndex = ((IntendedRouteLegGraphic) newClosest).getIndex();
                    if (legIndex == 0) {
                        return false;
                    }
                    IntendedRoute routeData = ((IntendedRouteLegGraphic) newClosest).getIntendedRouteGraphic().getIntendedRoute();
                    RouteWaypoint startWp = routeData.getWaypoints().get(legIndex - 1);
                    RouteLeg leg = startWp.getOutLeg();

                    Position startPos = startWp.getPos();
                    Position midPos = Position.create(worldLocation.getY(), worldLocation.getX());
                    double midRange = Calculator.range(startPos, midPos, leg.getHeading());

                    Date startEta = routeData.getEtas().get(legIndex - 1);

                    DateTime intendedRouteETA = new DateTime((long) (midRange / leg.getSpeed() * 3600000 + startEta.getTime()));

                    activeRoutePosition = findPositionActiveRoute(intendedRouteETA);

                    if (activeRoutePosition != null) {

                        // Draw it
                        Position intendedRoutePosition = Position.create(worldLocation.getY(), worldLocation.getX());

                        comparisonLine = new IntendedRouteComparisonGraphics(activeRoutePosition, intendedRoutePosition);
                        graphics.add(comparisonLine);
                        doPrepare();

                    }

                }

                if (activeRoutePosition == null) {
                    intendedRouteInfoPanel.showLegInfo((IntendedRouteLegGraphic) newClosest, worldLocation);
                } else {
                    intendedRouteInfoPanel.showLegInfo((IntendedRouteLegGraphic) newClosest, worldLocation, activeRoutePosition);
                }

            }
        }
        return true;
        // return super.initInfoPanel(infoPanel, newClosest, evt, containerPoint);

    }

    private Position findPositionActiveRoute(DateTime intendedRouteETA) {

        ActiveRoute activeRoute = EPDShip.getInstance().getRouteManager().getActiveRoute();

        // The ETA of the time is before we start our active route, or after the last waypoint date
        if (intendedRouteETA.isBefore(new DateTime(activeRoute.getEtas().get(0).getTime()))

        || intendedRouteETA.isAfter(new DateTime(activeRoute.getEtas().get(activeRoute.getEtas().size() - 1).getTime()))) {

            return null;
        }

        // Find leg we're on

        // Find start WP
        int startWP = activeRoute.getActiveWaypointIndex() - 1;
        if (startWP < 0) {
            startWP = 0;
        }

        for (int i = startWP; i < activeRoute.getWaypoints().size(); i++) {

            RouteWaypoint currentWaypoint = activeRoute.getWaypoints().get(i);

            // We are in the route
            if (new DateTime(activeRoute.getEtas().get(i).getTime()).isBefore(intendedRouteETA)) {

                // Do we have an out leg
                if (currentWaypoint.getOutLeg() != null) {

                    if (new DateTime(activeRoute.getEtas().get(i + 1).getTime()).isAfter(intendedRouteETA)) {

                        long secondsSailTime = (intendedRouteETA.getMillis() - activeRoute.getEtas().get(i).getTime()) / 1000;

                        double distanceTravelled = Converter.milesToNM(Calculator.distanceAfterTimeMph(activeRoute.getWaypoints()
                                .get(i).getOutLeg().getSpeed(), secondsSailTime));

                        return Calculator.findPosition(currentWaypoint.getPos(), currentWaypoint.getOutLeg().calcBrg(),
                                Converter.nmToMeters(distanceTravelled));
                    }
                }
            }
        }
        return null;

    }

    @Override
    public boolean mouseMoved(MouseEvent evt) {

        if (!isVisible() || mapMenu == null || mapMenu.isVisible()) {
            return false;
        }

        if (!infoPanels.isEmpty()) {
            OMGraphic newClosest = getSelectedGraphic(infoPanelsGraphics, evt, infoPanels.getGraphicsList());

            if (newClosest == null) {
                // Remove the line
                if (comparisonLine != null) {

                    graphics.remove(comparisonLine);
                    doPrepare();
                }
            }
        }

        return super.mouseMoved(evt);
    }

}
