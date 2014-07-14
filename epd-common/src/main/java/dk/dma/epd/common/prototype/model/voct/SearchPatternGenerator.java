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
package dk.dma.epd.common.prototype.model.voct;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Ellipsoid;

public class SearchPatternGenerator {

    private RouteManagerCommonSettings<?> settings;

    public enum searchPattern {
        Parallel_Sweep_Search, Creeping_Line_Search, Track_Line_Search, Track_Line_Search_nonreturn, Expanding_Square_Search, unknown
    }

    private SAROperation sarOperation;

    public SearchPatternGenerator(SAROperation sarOperation) {
        this.sarOperation = sarOperation;
    }

    public SearchPatternRoute generateSearchPattern(searchPattern pattern,
            SARData data, RouteManagerCommonSettings<?> settings, int i) {

        this.settings = settings;

        switch (pattern) {
        case Parallel_Sweep_Search:
            return parallelSweepSearch(data.getCSP(), data
                    .getEffortAllocationData().get(i), data);
        case Creeping_Line_Search:
            return creepingLineSearch(data.getCSP(), data
                    .getEffortAllocationData().get(i), data);
        case Expanding_Square_Search:
            return expandingSquareSearch(data.getCSP(), data
                    .getEffortAllocationData().get(i), data);
        default:
            return null;
        }
    }

    private SearchPatternRoute expandingSquareSearch(Position CSP,
            EffortAllocationData effortAllocationData, SARData sarData) {

        Position A = effortAllocationData.getEffectiveAreaA();
        Position B = effortAllocationData.getEffectiveAreaB();
        Position C = effortAllocationData.getEffectiveAreaC();
        // Position D = effortAllocationData.getEffectiveAreaD();

        double width = A.distanceTo(B, CoordinateSystem.CARTESIAN);
        double height = A.distanceTo(C, CoordinateSystem.CARTESIAN);

        double verticalBearing = Calculator.bearing(A, C, Heading.RL);

        // verticalBearing = 180;

        double horizontalBearing = Calculator.bearing(A, B, Heading.RL);

        // horizontalBearing = 90;

        // System.out.println("width bearing is " + horizontalBearing);
        // System.out.println("height bearing is " + verticalBearing);

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Find center of top line

        Position topCenter = Calculator.calculateEndingGlobalCoordinates(
                reference, A, horizontalBearing, width / 2, endBearing);

        endBearing = new double[1];

        // Move down
        Position center = Calculator.calculateEndingGlobalCoordinates(
                reference, topCenter, verticalBearing, height / 2, endBearing);

        // center = topCenter;

        double S = effortAllocationData.getTrackSpacing();

        // Start from center position

        // Determine how long of a track we can generate
        double totalLengthOfTrack = effortAllocationData.getEffectiveAreaSize()
                / S;

        // We have not plotted anything yet
        double trackPlotted = 0;
        double currentTrackLength = S;

        Position currentPos = center;
        Position nextPos;

        List<Position> positionList = new ArrayList<Position>();

        positionList.add(currentPos);
        //
        // System.out.println("Horizontal Bearing " + horizontalBearing);
        // System.out.println("Vertical Bearing " + verticalBearing);

        // horizontalBearing = 90;
        // verticalBearing = 0;

        // First move vertical

        int multiplier = 2;

        while (trackPlotted < totalLengthOfTrack) {

            // Move vertical
            nextPos = Calculator.findPosition(currentPos, verticalBearing,
                    Converter.nmToMeters(currentTrackLength));

            // Reverse direction
            verticalBearing = Calculator.reverseDirection(verticalBearing);

            // Do we place another track?
            if ((trackPlotted + currentTrackLength) <= totalLengthOfTrack) {

                trackPlotted = trackPlotted + currentTrackLength;

                currentPos = nextPos;

                positionList.add(currentPos);

                // Move horizontally
                nextPos = Calculator.findPosition(currentPos,
                        horizontalBearing,
                        Converter.nmToMeters(currentTrackLength));

                // Reverse direction
                horizontalBearing = Calculator
                        .reverseDirection(horizontalBearing);

                // Do we move up?
                if ((trackPlotted + currentTrackLength) <= totalLengthOfTrack) {

                    trackPlotted = trackPlotted + currentTrackLength;

                    currentPos = nextPos;

                    positionList.add(currentPos);

                    currentTrackLength = multiplier * S;
                    multiplier = multiplier + 2;

                }

                else {
                    // Cannot draw vertical track, draw what we can
                    System.out
                            .println("Cannot draw vertical track, draw what we can");

                    break;

                    // Reverse direction
                    // verticalBearing = reverseDirection(verticalBearing);
                    //
                    // double remainingDistance = totalLengthOfTrack
                    // - trackPlotted;
                    //
                    // nextPos = Calculator.findPosition(currentPos,
                    // verticalBearing,
                    // Converter.nmToMeters(remainingDistance));
                    // trackPlotted = trackPlotted + remainingDistance;
                    // currentPos = nextPos;
                    // positionList.add(currentPos);
                }
            } else {
                System.out
                        .println("Cannot draw horizontal track, draw what we can");
                break;
                // Cannot draw the full length of the track, draw the remaining
                // distance
                // horizontalBearing = reverseDirection(horizontalBearing);
                //
                // double remainingDistance = totalLengthOfTrack - trackPlotted;
                // nextPos = Calculator.findPosition(currentPos,
                // horizontalBearing,
                // Converter.nmToMeters(remainingDistance));
                //
                // trackPlotted = trackPlotted + remainingDistance;
                //
                // currentPos = nextPos;
                // positionList.add(currentPos);
            }
        }

        // Create a new Search Pattern Route based on static locations
        SearchPatternRoute searchRoute = new SearchPatternRoute(positionList);

        // Set values to each waypoint
        LinkedList<RouteWaypoint> waypoints = searchRoute.getWaypoints();
        for (RouteWaypoint routeWaypoint : waypoints) {
            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg outLeg = routeWaypoint.getOutLeg();
                double xtd = effortAllocationData.getTrackSpacing() / 2;
                outLeg.setXtdPort(xtd);
                outLeg.setXtdStarboard(xtd);
                outLeg.setHeading(Heading.RL);
                outLeg.setSpeed(effortAllocationData.getGroundSpeed());
            }
            routeWaypoint.setTurnRad(settings.getDefaultTurnRad());

        }

        // Search Route Start time to CSS
        searchRoute.setStarttime(new Date(sarData.getCSSDate().getMillis()));

        // Calculate ETAS and TTGS based on these values
        searchRoute.calcValues(true);

        // Setname
        if (sarData.getSarID().equals("")) {
            searchRoute.setName("Expanding Square Search");
        } else {
            searchRoute.setName("Expanding Square Search - SAR No. "
                    + sarData.getSarID());
        }

        // Dynamic waypoints
        List<Position> waypointsAdjustedForWeather = new ArrayList<Position>();

        // We must calculate from time 0 of our arrival as the effective area
        // has been calculated from LKP
        // Thus our time 0 is Commence Search Start
        DateTime cssDate = sarData.getCSSDate();

        for (int i = 0; i < searchRoute.getWaypoints().size(); i++) {
            Date wpETA = searchRoute.getEtas().get(i);
            Position wpPos = searchRoute.getWaypoints().get(i).getPos();

            // How long has elapsed since time 0
            double timeElapsed = ((double) (wpETA.getTime() - cssDate
                    .getMillis())) / 60 / 60 / 1000;

            // System.out.println("Elapsed is for " + i + " is " + timeElapsed);

            if (sarData instanceof DatumPointDataSARIS) {
                waypointsAdjustedForWeather.add(wpPos);
            } else {
                Position newPos = sarOperation.applyDriftToPoint(sarData,
                        wpPos, timeElapsed);

                waypointsAdjustedForWeather.add(newPos);
            }

            // searchRoute.getWaypoints().get(i).setPos(newPos);
        }

        searchRoute.setDynamicPositions(waypointsAdjustedForWeather);

        searchRoute.getWaypoints().get(0).setName("Start");

        searchRoute.getWaypoints().get(searchRoute.getWaypoints().size() - 1)
                .setName("End");

        return searchRoute;

    }

    private SearchPatternRoute creepingLineSearch(Position CSP,
            EffortAllocationData effortAllocationData, SARData sarData) {

        // Find closest corner point
        Position A = effortAllocationData.getEffectiveAreaA();
        Position B = effortAllocationData.getEffectiveAreaB();
        Position C = effortAllocationData.getEffectiveAreaC();
        Position D = effortAllocationData.getEffectiveAreaD();

        double aCSP = A.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double bCSP = B.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double cCSP = C.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double dCSP = D.distanceTo(CSP, CoordinateSystem.CARTESIAN);

        // Assumptions about the corners, A is top left, B is top right, C is
        // bottom left and D is bottom right.
        // Using these assumptions we can determine which way to let the search
        // pattern unfold
        double smallest = aCSP;

        Position toDrawTo = A;

        double horizontalBearing = A.rhumbLineBearingTo(B);
        double verticalBearing = A.rhumbLineBearingTo(C);

        if (bCSP < smallest) {
            smallest = bCSP;
            toDrawTo = B;
            horizontalBearing = B.rhumbLineBearingTo(A);
            verticalBearing = B.rhumbLineBearingTo(D);
        }
        if (cCSP < smallest) {
            smallest = cCSP;
            toDrawTo = C;
            horizontalBearing = C.rhumbLineBearingTo(D);
            verticalBearing = C.rhumbLineBearingTo(A);
        }
        if (dCSP < smallest) {
            smallest = dCSP;
            toDrawTo = D;
            horizontalBearing = D.rhumbLineBearingTo(C);
            verticalBearing = D.rhumbLineBearingTo(B);

        }

        double S = effortAllocationData.getTrackSpacing();

        // Find init position from the corner
        Position verticalPos = Calculator.findPosition(toDrawTo,
                verticalBearing, Converter.nmToMeters(S / 2));

        // This is where we start
        Position finalPos = Calculator.findPosition(verticalPos,
                horizontalBearing, Converter.nmToMeters(S / 2));

        // Determine how long of a track we can generate
        double totalLengthOfTrack = effortAllocationData.getEffectiveAreaSize()
                / S;

        // Individual track length - has ½S in each end, so we take width minus
        // S
        double trackHeight = Calculator.range(A, C, Heading.RL) - S;

        // We have not plotted anything yet
        double trackPlotted = 0;

        // Note what if the CSP is too far from the area? - To be determined

        Position currentPos = finalPos;
        Position nextPos;

        List<Position> positionList = new ArrayList<Position>();

        positionList.add(currentPos);

        while (trackPlotted < totalLengthOfTrack) {

            System.out.println("Vertical Bearing " + verticalBearing);

            // Move vertically
            nextPos = Calculator.findPosition(currentPos, verticalBearing,
                    Converter.nmToMeters(trackHeight));

            // Reverse direction
            // verticalBearing = verticalBearing + 180;
            verticalBearing = Calculator.reverseDirection(verticalBearing);

            // Do we place another track?
            if ((trackPlotted + trackHeight) <= totalLengthOfTrack) {

                trackPlotted = trackPlotted + trackHeight;

                currentPos = nextPos;

                positionList.add(currentPos);

                // Move horizontally
                nextPos = Calculator.findPosition(currentPos,
                        horizontalBearing, Converter.nmToMeters(S / 2));

                // Do we move up a half ½S distance?
                if ((trackPlotted + (S / 2)) <= totalLengthOfTrack) {

                    trackPlotted = trackPlotted + (S / 2);

                    currentPos = nextPos;

                    positionList.add(currentPos);

                    System.out.println("Track Plotted is: " + trackPlotted
                            + " vs. the total length " + totalLengthOfTrack);
                } else {
                    // Cannot draw ½S track, draw what we can
                    double remainingDistance = totalLengthOfTrack
                            - trackPlotted;

                    nextPos = Calculator.findPosition(currentPos,
                            horizontalBearing,
                            Converter.nmToMeters(remainingDistance));
                    trackPlotted = trackPlotted + remainingDistance;
                    currentPos = nextPos;
                    positionList.add(currentPos);
                }
            } else {
                // Cannot draw the full length of the track, draw the remaining
                // distance
                verticalBearing = Calculator.reverseDirection(verticalBearing);

                double remainingDistance = totalLengthOfTrack - trackPlotted;
                nextPos = Calculator.findPosition(currentPos, verticalBearing,
                        Converter.nmToMeters(remainingDistance));

                trackPlotted = trackPlotted + remainingDistance;

                currentPos = nextPos;
                positionList.add(currentPos);
            }
        }

        // Create a new Search Pattern Route based on static locations
        SearchPatternRoute searchRoute = new SearchPatternRoute(positionList);

        // Set values to each waypoint
        LinkedList<RouteWaypoint> waypoints = searchRoute.getWaypoints();
        for (RouteWaypoint routeWaypoint : waypoints) {
            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg outLeg = routeWaypoint.getOutLeg();
                double xtd = effortAllocationData.getTrackSpacing() / 2;
                outLeg.setXtdPort(xtd);
                outLeg.setXtdStarboard(xtd);
                outLeg.setHeading(Heading.RL);
                outLeg.setSpeed(effortAllocationData.getGroundSpeed());
            }
            routeWaypoint.setTurnRad(settings.getDefaultTurnRad());

        }

        // Search Route Start time to CSS
        searchRoute.setStarttime(new Date(sarData.getCSSDate().getMillis()));

        // Calculate ETAS and TTGS based on these values
        searchRoute.calcValues(true);

        // Setname
        if (sarData.getSarID().equals("")) {
            searchRoute.setName("Creeping Line Search");
        } else {
            searchRoute.setName("Creeping Line Search - SAR No. "
                    + sarData.getSarID());
        }

        // Dynamic waypoints
        List<Position> waypointsAdjustedForWeather = new ArrayList<Position>();

        // We must calculate from time 0 of our arrival as the effective area
        // has been calculated from LKP
        // Thus our time 0 is Commence Search Start
        DateTime cssDate = sarData.getCSSDate();

        for (int i = 0; i < searchRoute.getWaypoints().size(); i++) {
            Date wpETA = searchRoute.getEtas().get(i);
            Position wpPos = searchRoute.getWaypoints().get(i).getPos();

            // How long has elapsed since time 0
            double timeElapsed = ((double) (wpETA.getTime() - cssDate
                    .getMillis())) / 60 / 60 / 1000;

            // System.out.println("Elapsed is for " + i + " is " + timeElapsed);

            if (sarData instanceof DatumPointDataSARIS) {
                waypointsAdjustedForWeather.add(wpPos);
            } else {

                Position newPos = sarOperation.applyDriftToPoint(sarData,
                        wpPos, timeElapsed);

                waypointsAdjustedForWeather.add(newPos);

            }

            // searchRoute.getWaypoints().get(i).setPos(newPos);
        }

        searchRoute.setDynamicPositions(waypointsAdjustedForWeather);

        searchRoute.getWaypoints().get(0).setName("Start");

        searchRoute.getWaypoints().get(searchRoute.getWaypoints().size() - 1)
                .setName("End");

        return searchRoute;
    }

    public void calculateDynamicWaypoints(SearchPatternRoute searchRoute,
            SARData sarData) {

        if (sarData instanceof DatumPointDataSARIS) {
            ArrayList<Position> dummyPositionList = new ArrayList<Position>();
            for (int i = 0; i < searchRoute.getWaypoints().size(); i++) {

                Position wpPos = searchRoute.getWaypoints().get(i).getPos();

                dummyPositionList.add(wpPos);

            }

            searchRoute.setDynamicPositions(dummyPositionList);
        } else {

            // Dynamic waypoints
            List<Position> waypointsAdjustedForWeather = new ArrayList<Position>();

            // We must calculate from time 0 of our arrival as the effective
            // area
            // has been calculated from LKP
            // Thus our time 0 is Commence Search Start
            DateTime cssDate = sarData.getCSSDate();

            for (int i = 0; i < searchRoute.getWaypoints().size(); i++) {
                Date wpETA = searchRoute.getEtas().get(i);
                Position wpPos = searchRoute.getWaypoints().get(i).getPos();

                // How long has elapsed since time 0
                double timeElapsed = ((double) (wpETA.getTime() - cssDate
                        .getMillis())) / 60 / 60 / 1000;

                // System.out.println("Elapsed is for " + i + " is " +
                // timeElapsed);

                Position newPos = sarOperation.applyDriftToPoint(sarData,
                        wpPos, timeElapsed);

                waypointsAdjustedForWeather.add(newPos);
                // searchRoute.getWaypoints().get(i).setPos(newPos);
            }

            searchRoute.setDynamicPositions(waypointsAdjustedForWeather);
        }
    }

    private SearchPatternRoute parallelSweepSearch(Position CSP,
            EffortAllocationData effortAllocationData, SARData sarData) {
        // Find closest corner point
        Position A = effortAllocationData.getEffectiveAreaA();
        Position B = effortAllocationData.getEffectiveAreaB();
        Position C = effortAllocationData.getEffectiveAreaC();
        Position D = effortAllocationData.getEffectiveAreaD();

        double aCSP = A.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double bCSP = B.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double cCSP = C.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double dCSP = D.distanceTo(CSP, CoordinateSystem.CARTESIAN);

        // Assumptions about the corners, A is top left, B is top right, C is
        // bottom left and D is bottom right.
        // Using these assumptions we can determine which way to let the search
        // pattern unfold
        double smallest = aCSP;

        Position toDrawTo = A;

        double horizontalBearing = A.rhumbLineBearingTo(B);
        double verticalBearing = A.rhumbLineBearingTo(C);

        if (bCSP < smallest) {
            smallest = bCSP;
            toDrawTo = B;
            horizontalBearing = B.rhumbLineBearingTo(A);
            verticalBearing = B.rhumbLineBearingTo(D);
        }
        if (cCSP < smallest) {
            smallest = cCSP;
            toDrawTo = C;
            horizontalBearing = C.rhumbLineBearingTo(D);
            verticalBearing = C.rhumbLineBearingTo(A);
        }
        if (dCSP < smallest) {
            smallest = dCSP;
            toDrawTo = D;
            horizontalBearing = D.rhumbLineBearingTo(C);
            verticalBearing = D.rhumbLineBearingTo(B);

        }

        double S = effortAllocationData.getTrackSpacing();

        // Find init position from the corner
        Position verticalPos = Calculator.findPosition(toDrawTo,
                verticalBearing, Converter.nmToMeters(S / 2));

        // This is where we start
        Position finalPos = Calculator.findPosition(verticalPos,
                horizontalBearing, Converter.nmToMeters(S / 2));

        // Determine how long of a track we can generate
        double totalLengthOfTrack = effortAllocationData.getEffectiveAreaSize()
                / S;

        // Individual track length - has ½S in each end, so we take width minus
        // S
        double trackLength = Calculator.range(A, B, Heading.RL) - S;

        // We have not plotted anything yet
        double trackPlotted = 0;

        // Note what if the CSP is too far from the area? - To be determined

        Position currentPos = finalPos;
        Position nextPos;

        List<Position> positionList = new ArrayList<Position>();

        positionList.add(currentPos);

        while (trackPlotted < totalLengthOfTrack) {

            // Move horizontally
            nextPos = Calculator.findPosition(currentPos, horizontalBearing,
                    Converter.nmToMeters(trackLength));

            // Reverse direction
            horizontalBearing = Calculator.reverseDirection(horizontalBearing);
            // horizontalBearing = -horizontalBearing;

            // Do we place another track?
            if ((trackPlotted + trackLength) <= totalLengthOfTrack) {

                trackPlotted = trackPlotted + trackLength;

                currentPos = nextPos;

                positionList.add(currentPos);

                // Move vertically
                nextPos = Calculator.findPosition(currentPos, verticalBearing,
                        Converter.nmToMeters(S / 2));

                // Do we move up a half ½S distance?
                if ((trackPlotted + (S / 2)) <= totalLengthOfTrack) {

                    trackPlotted = trackPlotted + (S / 2);

                    currentPos = nextPos;

                    positionList.add(currentPos);

                    System.out.println("Track Plotted is: " + trackPlotted
                            + " vs. the total length " + totalLengthOfTrack);
                } else {
                    // Cannot draw ½S track, draw what we can
                    double remainingDistance = totalLengthOfTrack
                            - trackPlotted;

                    nextPos = Calculator.findPosition(currentPos,
                            verticalBearing,
                            Converter.nmToMeters(remainingDistance));
                    trackPlotted = trackPlotted + remainingDistance;
                    currentPos = nextPos;
                    positionList.add(currentPos);
                }
            } else {
                // Cannot draw the full length of the track, draw the remaining
                // distance
                horizontalBearing = Calculator
                        .reverseDirection(horizontalBearing);

                double remainingDistance = totalLengthOfTrack - trackPlotted;
                nextPos = Calculator.findPosition(currentPos,
                        horizontalBearing,
                        Converter.nmToMeters(remainingDistance));

                trackPlotted = trackPlotted + remainingDistance;

                currentPos = nextPos;
                positionList.add(currentPos);
            }
        }

        // Create a new Search Pattern Route based on static locations
        SearchPatternRoute searchRoute = new SearchPatternRoute(positionList);

        // Set values to each waypoint
        LinkedList<RouteWaypoint> waypoints = searchRoute.getWaypoints();
        for (RouteWaypoint routeWaypoint : waypoints) {
            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg outLeg = routeWaypoint.getOutLeg();
                double xtd = effortAllocationData.getTrackSpacing() / 2;
                outLeg.setXtdPort(xtd);
                outLeg.setXtdStarboard(xtd);
                outLeg.setHeading(Heading.RL);
                outLeg.setSpeed(effortAllocationData.getGroundSpeed());
            }
            routeWaypoint.setTurnRad(settings.getDefaultTurnRad());

        }

        // Search Route Start time to CSS
        searchRoute.setStarttime(new Date(sarData.getCSSDate().getMillis()));

        // Calculate ETAS and TTGS based on these values
        searchRoute.calcValues(true);

        // Setname
        if (sarData.getSarID().equals("")) {
            searchRoute.setName("Parallel Sweep Search");
        } else {
            searchRoute.setName("Parallel Sweep Search - SAR No. "
                    + sarData.getSarID());
        }

        // Dynamic waypoints
        List<Position> waypointsAdjustedForWeather = new ArrayList<Position>();

        // We must calculate from time 0 of our arrival as the effective area
        // has been calculated from LKP
        // Thus our time 0 is Commence Search Start
        DateTime cssDate = sarData.getCSSDate();

        for (int i = 0; i < searchRoute.getWaypoints().size(); i++) {
            Date wpETA = searchRoute.getEtas().get(i);
            Position wpPos = searchRoute.getWaypoints().get(i).getPos();

            // How long has elapsed since time 0
            double timeElapsed = ((double) (wpETA.getTime() - cssDate
                    .getMillis())) / 60 / 60 / 1000;

            // System.out.println("Elapsed is for " + i + " is " + timeElapsed);

            if (sarData instanceof DatumPointDataSARIS) {
                waypointsAdjustedForWeather.add(wpPos);
            } else {
                Position newPos = sarOperation.applyDriftToPoint(sarData,
                        wpPos, timeElapsed);

                waypointsAdjustedForWeather.add(newPos);
            }

            // Position newPos = sarOperation.applyDriftToPoint(sarData, wpPos,
            // timeElapsed);

            // waypointsAdjustedForWeather.add(newPos);
            // searchRoute.getWaypoints().get(i).setPos(newPos);

        }

        searchRoute.setDynamicPositions(waypointsAdjustedForWeather);

        searchRoute.getWaypoints().get(0).setName("Start");

        searchRoute.getWaypoints().get(searchRoute.getWaypoints().size() - 1)
                .setName("End");

        return searchRoute;
    }

    public void generateSearchPatternInvalid(RapidResponseData rapidResponseData) {

        //
        // System.out.println("Horizontal = " + horizontalBearing);
        // System.out.println("Vertical = " + verticalBearing);

        // voctLayer.drawPoints(CSP, toDrawTo);

        // Hack
        // if (horizontalBearing > 180){
        // horizontalBearing = 270;
        // }
        //
        // if (horizontalBearing < 180){
        // horizontalBearing = 90;
        // }
        //
        //
        // if (verticalBearing > 270 || verticalBearing < 90){
        // verticalBearing = 0;
        // }else{
        // verticalBearing = 90;
        // }

        // if (verticalBearing < 270 || verticalBearing > 90){
        // verticalBearing = 90;
        // }

        // System.out.println("Horizontal = " + horizontalBearing);
        // System.out.println("Vertical = " + verticalBearing);
        //
        //
        //
        // voctLayer.drawPoints(CSP, finalPos);
        // voctLayer.drawPoints(verticalPos, finalPos);
        // toDrawTo

        // Position routeStartPoint =

        // EPDShip.getRouteManager().addRoute(searchRoute);

        // //Should I go left or right? / better way!
        //
        // double horizontalBearing;
        //
        // int bearingToA = (int) toDrawTo.rhumbLineBearingTo(A);
        // int bearingToB = (int) toDrawTo.rhumbLineBearingTo(B);
        // int bearingToC = (int) toDrawTo.rhumbLineBearingTo(C);
        // int bearingToD = (int) toDrawTo.rhumbLineBearingTo(D);
        //
        //
        // System.out.println(bearingToA);
        // System.out.println(bearingToB);
        // System.out.println(bearingToC);
        // System.out.println(bearingToD);
        //
        //
        //
        //
        // //What direction should we go now?
        // //We are standing in toDrawTo
        //
        // if (bearingToA == 90 || bearingToA == 270) {
        // System.out.println("Go horizontal towards A");
        // horizontalBearing = toDrawTo.rhumbLineBearingTo(A);
        // }
        //
        // if (bearingToB == 90 || bearingToB == 270) {
        // System.out.println("Go horizontal towards B");
        // horizontalBearing = toDrawTo.rhumbLineBearingTo(B);
        // }
        // if (bearingToC == 90 || bearingToC == 270) {
        // System.out.println("Go horizontal towards C");
        // horizontalBearing = toDrawTo.rhumbLineBearingTo(C);
        // }
        // if (bearingToD == 90 || bearingToD == 270) {
        // System.out.println("Go horizontal towards D");
        // horizontalBearing = toDrawTo.rhumbLineBearingTo(D);
        // }
        //
        //
        // //Up or down
        // double verticalDirection;
        //
        // if (bearingToA == 359 || bearingToA == 179) {
        // System.out.println("Go vertically towards A");
        // verticalDirection = toDrawTo.rhumbLineBearingTo(A);
        // }
        //
        // if (bearingToB == 359 || bearingToB == 179) {
        // System.out.println("Go vertically towards B");
        // verticalDirection = toDrawTo.rhumbLineBearingTo(B);
        // }
        // if (bearingToC == 359 || bearingToC == 179) {
        // System.out.println("Go vertically towards C");
        // verticalDirection = toDrawTo.rhumbLineBearingTo(C);
        // }
        // if (bearingToD == 359 || bearingToD == 179) {
        // System.out.println("Go vertically towards D");
        // verticalDirection = toDrawTo.rhumbLineBearingTo(D);
        // }
        //
    }

}
