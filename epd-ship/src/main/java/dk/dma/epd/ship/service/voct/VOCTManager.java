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
package dk.dma.epd.ship.service.voct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateListener;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.voct.SARInput;
import dk.dma.epd.ship.layers.voct.VoctLayer;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all
 * information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManager implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    private SAROperation sarOperation;
    private static final Logger LOG = LoggerFactory
            .getLogger(VOCTManager.class);

    private boolean hasSar;

    private SARInput sarInputDialog;

    private CopyOnWriteArrayList<VOCTUpdateListener> listeners = new CopyOnWriteArrayList<>();

    private RapidResponseData rapidResponseData;
    VoctLayer voctLayer;

    public VOCTManager() {
        EPDShip.startThread(this, "VOCTManager");
        LOG.info("Started VOCT Manager");
    }

    public void showSarInput() {
        LOG.info("Started new SAR Operation");
        if (!hasSar) {
            hasSar = true;

            // Create the GUI input boxes

            // Voct specific test
            sarInputDialog = new SARInput(this);
            sarInputDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            sarInputDialog.setVisible(true);

        } else {
            // Cannot inititate a SAR without terminating the existing one, show
            // existing dialog?
            sarInputDialog.setVisible(true);
        }

    }

    /**
     * @param voctLayer
     *            the voctLayer to set
     */
    public void setVoctLayer(VoctLayer voctLayer) {
        this.voctLayer = voctLayer;
    }

    /**
     * @return the hasSar
     */
    public boolean isHasSar() {
        return hasSar;
    }

    public void setSarType(SAR_TYPE type) {
        sarOperation = null;
        sarOperation = new SAROperation(type, this);
    }

    public SAR_TYPE getSarType() {
        if (sarOperation != null) {
            return sarOperation.getOperationType();
        }
        return SAR_TYPE.NONE;
    }

    public void inputRapidResponseData(DateTime TLKP, DateTime CSS,
            Position LKP, double TWCknots, double TWCHeading,
            double LWknots, double LWHeading, double x, double y, double SF,
            int searchObject) {

        RapidResponseData data = new RapidResponseData(TLKP, CSS, LKP,
                TWCknots, TWCHeading, LWknots, LWHeading, x, y, SF,
                searchObject);

        sarOperation.startRapidResponseCalculations(data);

    }

    /**
     * User has clicked the Cancel button, abort operation and reset
     */
    public void cancelSarOperation() {
        sarOperation = null;
        hasSar = false;

        notifyListeners(VOCTUpdateEvent.SAR_CANCEL);
    }

    public void displaySar() {
        notifyListeners(VOCTUpdateEvent.SAR_DISPLAY);
    }

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            Util.sleep(10000);

        }

    }

    public static VOCTManager loadVOCTManager() {

        // Where we load or serialize old VOCTS
        return new VOCTManager();

    }

    public void notifyListeners(VOCTUpdateEvent e) {
        for (VOCTUpdateListener listener : listeners) {
            listener.voctUpdated(e);
        }

        // Persist update VOCT info
        // saveToFile();
    }

    public void addListener(VOCTUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(VOCTUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * @return the rapidResponseData
     */
    public RapidResponseData getRapidResponseData() {
        return rapidResponseData;
    }

    /**
     * @param rapidResponseData
     *            the rapidResponseData to set
     */
    public void setRapidResponseData(RapidResponseData rapidResponseData) {
        this.rapidResponseData = rapidResponseData;

        notifyListeners(VOCTUpdateEvent.SAR_READY);
    }

    public void EffortAllocationDataEntered() {
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_READY);
        sarOperation.calculateEffortAllocation(rapidResponseData);

        System.out.println("Display");
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY);

    }

    public void generateSearchPattern() {

        // Find closest corner point

        Position A = rapidResponseData.getEffortAllocationData().getEffectiveAreaA();
        Position B = rapidResponseData.getEffortAllocationData().getEffectiveAreaB();
        Position C = rapidResponseData.getEffortAllocationData().getEffectiveAreaC();
        Position D = rapidResponseData.getEffortAllocationData().getEffectiveAreaD();

        Position CSP = rapidResponseData.getCSP();

        double aCSP = A.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double bCSP = B.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double cCSP = C.distanceTo(CSP, CoordinateSystem.CARTESIAN);
        double dCSP = D.distanceTo(CSP, CoordinateSystem.CARTESIAN);

        double smallest = aCSP;

        Position toDrawTo = A;

        double horizontalBearing = A.rhumbLineBearingTo(B);
        double verticalBearing = A.rhumbLineBearingTo(C);

        if (bCSP < smallest) {
            smallest = bCSP;
            toDrawTo = B;
            System.out.println("Draw to is B " + smallest);
            horizontalBearing = B.rhumbLineBearingTo(A);
            verticalBearing = B.rhumbLineBearingTo(D);
        }
        if (cCSP < smallest) {
            smallest = cCSP;
            toDrawTo = C;
            System.out.println("Draw to is C " + smallest);
            horizontalBearing = C.rhumbLineBearingTo(D);
            verticalBearing = C.rhumbLineBearingTo(A);
        }
        if (dCSP < smallest) {
            smallest = dCSP;
            toDrawTo = D;
            System.out.println("Draw to is D " + smallest);
            horizontalBearing = D.rhumbLineBearingTo(C);
            verticalBearing = D.rhumbLineBearingTo(B);

        }

        System.out.println("Horizontal = " + horizontalBearing);
        System.out.println("Vertical = " + verticalBearing);

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

        System.out.println("Horizontal = " + horizontalBearing);
        System.out.println("Vertical = " + verticalBearing);

        double S = rapidResponseData.getEffortAllocationData().getTrackSpacing();

        Position verticalPos = Calculator.findPosition(toDrawTo,
                verticalBearing, Converter.nmToMeters(S / 2));
        Position finalPos = Calculator.findPosition(verticalPos,
                horizontalBearing, Converter.nmToMeters(S / 2));

        voctLayer.drawPoints(CSP, finalPos);
        // voctLayer.drawPoints(verticalPos, finalPos);
        // toDrawTo

        // Position routeStartPoint =

        double totalLengthOfTrack = rapidResponseData.getEffortAllocationData().getEffectiveAreaSize()
                / S;
        double trackLength = rapidResponseData.getEffortAllocationData().getEffectiveAreaWidth() - S;

        double trackPlotted = 0;

        Position currentPos = finalPos;
        Position nextPos;

        System.out.println("Track Plotted is: " + trackPlotted
                + " vs. the total length " + totalLengthOfTrack);

        List<Position> positionList = new ArrayList<Position>();

        positionList.add(currentPos);

        while (trackPlotted < totalLengthOfTrack) {

            // Move horizontally
            nextPos = Calculator.findPosition(currentPos, horizontalBearing,
                    Converter.nmToMeters(trackLength));

            horizontalBearing = -horizontalBearing;

            System.out.println(totalLengthOfTrack + " vs "
                    + (trackPlotted + trackLength));

            if ((trackPlotted + trackLength) <= totalLengthOfTrack) {

                trackPlotted = trackPlotted + trackLength;

                voctLayer.drawPoints(currentPos, nextPos);

                currentPos = nextPos;

                positionList.add(currentPos);

                // Move vertically
                nextPos = Calculator.findPosition(currentPos, verticalBearing,
                        Converter.nmToMeters(S / 2));

                if ((trackPlotted + (S / 2)) <= totalLengthOfTrack) {

                    trackPlotted = trackPlotted + (S / 2);

                    voctLayer.drawPoints(currentPos, nextPos);

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
                    voctLayer.drawPoints(currentPos, nextPos);
                    currentPos = nextPos;
                    positionList.add(currentPos);
                }
            } else {

                horizontalBearing = -horizontalBearing;

                double remainingDistance = totalLengthOfTrack - trackPlotted;
                nextPos = Calculator.findPosition(currentPos,
                        horizontalBearing,
                        Converter.nmToMeters(remainingDistance));

                trackPlotted = trackPlotted + remainingDistance;

                voctLayer.drawPoints(currentPos, nextPos);
                currentPos = nextPos;
                positionList.add(currentPos);
            }
        }

        Route searchRoute = new Route(positionList);
        
        LinkedList<RouteWaypoint> waypoints = searchRoute.getWaypoints();
        for (RouteWaypoint routeWaypoint : waypoints) {
            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg outLeg = routeWaypoint.getOutLeg();
                double xtd = EPDShip.getSettings().getNavSettings().getDefaultXtd();
                outLeg.setXtdPort(xtd);
                outLeg.setXtdStarboard(xtd);
                outLeg.setHeading(Heading.RL);
                outLeg.setSpeed(rapidResponseData.getEffortAllocationData().getGroundSpeed());
            }
            routeWaypoint.setTurnRad(EPDShip.getSettings().getNavSettings().getDefaultTurnRad());

        }
        
        searchRoute.setStarttime(new Date(rapidResponseData.getCSSDate().getMillis()));
//        searchRoute.adjustStartTime();
        searchRoute.calcValues(true);
        
        
        
        
//        searchRoute.setSpeed(rapidResponseData.getGroundSpeed());
        searchRoute.setName("Parallel Sweep Search");

        System.out.println(positionList.size());

        System.out.println(" FINISHED Track Plotted is: " + trackPlotted
                + " vs. the total length " + totalLengthOfTrack);

        DateTime lkpDate = rapidResponseData.getCSSDate();

        for (int i = 0; i < searchRoute.getWaypoints().size(); i++) {
            Date wpETA = searchRoute.getEtas().get(i);
            Position wpPos = searchRoute.getWaypoints().get(i).getPos();
            
            double timeElapsed = ((double) (wpETA.getTime() - lkpDate.getMillis()))  / 60 / 60 / 1000;
            System.out.println("Elapsed is for " + i + " is " + timeElapsed);

            Position newPos = sarOperation.applyDriftToPoint(rapidResponseData, wpPos, timeElapsed);

            searchRoute.getWaypoints().get(i).setPos(newPos);

        }

         

        EPDShip.getRouteManager().addRoute(searchRoute);

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
