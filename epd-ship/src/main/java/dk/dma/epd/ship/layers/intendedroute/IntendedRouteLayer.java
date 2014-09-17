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
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteWpCircle;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
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
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest,
            MouseEvent evt, Point containerPoint) {
        intendedRouteInfoPanel.setVisible(false);
        graphics.remove(comparisonLine);

        if (newClosest instanceof IntendedRouteWpCircle) {

            IntendedRouteWpCircle routeWP = (IntendedRouteWpCircle) newClosest;

            if (routeWP.getIndex() >= routeWP.getIntendedRouteGraphic()
                    .getIntendedRoute().getActiveWpIndex()) {
                intendedRouteInfoPanel
                        .showWpInfo((IntendedRouteWpCircle) newClosest);
            } else {
                return false;
            }

        } else {

            if (newClosest instanceof IntendedRouteLegGraphic) {
                // lets user see ETA continually along route leg

                Point2D worldLocation = chartPanel.getMap().getProjection()
                        .inverse(evt.getPoint());

                IntendedRouteLegGraphic selectedLegGraphics = (IntendedRouteLegGraphic) newClosest;
                int legIndex = ((IntendedRouteLegGraphic) newClosest)
                        .getIndex();

                IntendedRoute routeData = ((IntendedRouteLegGraphic) newClosest)
                        .getIntendedRouteGraphic().getIntendedRoute();

                ActiveRoute activeRoute = EPDShip.getInstance()
                        .getRouteManager().getActiveRoute();

                Position activeRoutePosition = null;

                closest = dummyCircle;

                if (activeRoute != null) {

                    if (legIndex - 1 < routeData.getActiveWpIndex()
                            && !selectedLegGraphics.isActiveWpLine()) {
                        // System.out.println("Not showing comparison line! "
                        // + selectedLegGraphics.isActiveWpLine());
                        return false;
                    }

                    RouteWaypoint startWp;
                    RouteLeg leg;
                    Position startPos;
                    Position midPos;
                    double midRange;
                    Date startEta;

                    DateTime intendedRouteETA = null;

                    if (selectedLegGraphics.isActiveWpLine()) {
                        // System.out.println("Active WP Line");
                        // leg = startWp.getOutLeg();

                        startPos = EPDShip.getInstance().getAisHandler()
                                .getVesselTarget(routeData.getMmsi())
                                .getPositionData().getPos();

                        midPos = Position.create(worldLocation.getY(),
                                worldLocation.getX());

                        midRange = Calculator.range(startPos, midPos,
                                Heading.RL);

                        startEta = PntTime.getDate();

                        Date endEta = routeData.getEtas().get(
                                routeData.getActiveWpIndex());

                        long milisecondsTravelTime = endEta.getTime()
                                - startEta.getTime();

                        Position endPosition = routeData.getWaypoints()
                                .get(routeData.getActiveWpIndex()).getPos();

                        double lengthToTravel = Calculator.range(startPos,
                                endPosition, Heading.RL);

                        double speed = (lengthToTravel / milisecondsTravelTime) * 1000 * 60 * 60;

                        intendedRouteETA = new DateTime((long) (midRange
                                / speed * 3600000 + startEta.getTime()));

                    } else {
                        startWp = routeData.getWaypoints().get(
                                routeData.getActiveWpIndex());
                        leg = startWp.getOutLeg();

                        startPos = startWp.getPos();
                        midPos = Position.create(worldLocation.getY(),
                                worldLocation.getX());
                        midRange = Calculator.range(startPos, midPos,
                                leg.getHeading());

                        startEta = routeData.getEtas().get(
                                routeData.getActiveWpIndex());

                        intendedRouteETA = new DateTime(
                                (long) (midRange / leg.getSpeed() * 3600000 + startEta
                                        .getTime()));

                    }

                    activeRoutePosition = findPositionActiveRoute(intendedRouteETA);

                    if (activeRoutePosition != null) {

                        // Draw it
                        Position intendedRoutePosition = Position.create(
                                worldLocation.getY(), worldLocation.getX());

                        comparisonLine = new IntendedRouteComparisonGraphics(
                                activeRoutePosition, intendedRoutePosition);
                        graphics.add(comparisonLine);
                        doPrepare();

                    }

                }

                if (legIndex - 1 < routeData.getActiveWpIndex()
                        && !selectedLegGraphics.isActiveWpLine()) {

                    return false;
                } else {

                    if (activeRoutePosition == null) {
                        intendedRouteInfoPanel.showLegInfo(
                                (IntendedRouteLegGraphic) newClosest,
                                worldLocation);
                    } else {
                        intendedRouteInfoPanel.showLegInfo(
                                (IntendedRouteLegGraphic) newClosest,
                                worldLocation, activeRoutePosition);
                    }
                }

            }
        }
        return true;
        // return super.initInfoPanel(infoPanel, newClosest, evt,
        // containerPoint);

    }

    private Position findPositionActiveRoute(DateTime intendedRouteETA) {

        // System.out.println("Where are we at " + intendedRouteETA);

        ActiveRoute activeRoute = EPDShip.getInstance().getRouteManager()
                .getActiveRoute();

        // The ETA of the time is before we start our active route, or after the
        // last waypoint date
        if (intendedRouteETA.isBefore(new DateTime(activeRoute.getEtas().get(0)
                .getTime()))

                || intendedRouteETA.isAfter(new DateTime(activeRoute.getEtas()
                        .get(activeRoute.getEtas().size() - 1).getTime()))) {

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
            if (new DateTime(activeRoute.getEtas().get(i).getTime())
                    .isBefore(intendedRouteETA)) {

                // Do we have an out leg
                if (currentWaypoint.getOutLeg() != null) {

                    if (new DateTime(activeRoute.getEtas().get(i + 1).getTime())
                            .isAfter(intendedRouteETA)) {

                        // Time Left in route
                        // long secondsSailTime = (activeRoute.getEtas()
                        // .get(i + 1).getTime() - intendedRouteETA
                        // .getMillis()) / 1000;

                        long secondsSailTime = intendedRouteETA.minus(
                                activeRoute.getEtas().get(i).getTime())
                                .getMillis() / 1000;

                        // System.out.println(intendedRouteETA.minus(
                        // activeRoute.getEtas().get(i).getTime())
                        // .getMillis() / 1000 / 60);

                        // secondsSailTime = totalTime - secondsSailTime;

                        // System.out.println("We have travelled for "
                        // + secondsSailTime / 60);

                        Date startEta = activeRoute.getEtas().get(i);
                        Date endEta = activeRoute.getEtas().get(i + 1);

                        // Try and calculate the speed
                        long milisecondsTravelTime = endEta.getTime()
                                - startEta.getTime();

                        Position startPos = activeRoute.getWaypoints().get(i)
                                .getPos();
                        //
                        Position endPosition = activeRoute.getWaypoints()
                                .get(i + 1).getPos();
                        //

                        double lengthToTravel = Calculator.range(startPos,
                                endPosition, Heading.RL);

                        double speed = (lengthToTravel / milisecondsTravelTime) * 1000 * 60 * 60;

                        double distanceTravelled = Calculator
                                .distanceAfterTimeMph(speed, secondsSailTime);

                        return Calculator.findPosition(
                                currentWaypoint.getPos(), currentWaypoint
                                        .getOutLeg().calcBrg(), Converter
                                        .nmToMeters(distanceTravelled));

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
            OMGraphic newClosest = getSelectedGraphic(infoPanelsGraphics, evt,
                    infoPanels.getGraphicsList());

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
