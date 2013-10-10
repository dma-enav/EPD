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

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Ellipsoid;

/**
 * Where all SAR Calculations are performed
 * 
 * Has a type and the type determines which options are available
 * 
 * @author David
 * 
 */
public class SAROperation {

    SAR_TYPE operationType;

    // // Time of Last known position
    // DateTime TLKP;
    //
    // // Commence Search Start
    // DateTime CSS;
    //
    // // Commence time of surface Drift - When is this used?
    // Date CTSD;
    //
    // // Last known Position
    // Position LKP;
    //
    // // COmmence Start Point
    // Position CSP;
    //
    // // SURFACE DRIFT VARIABLES
    //
    // // Observed Total Water Current (WTC, knots)
    // double TWCknots;
    //
    // // TWC Vector //either a direction or heading, or degrees
    // double TWCHeading;
    //
    // // Leeway (LW), knots
    // double LWknots;
    //
    // // Leeway Vector, heading or degrees
    // double LWHeading;
    //
    // // The way the wind is blow means the object is pushed in the opposite
    // // direction
    // double downWind;
    //
    // // Initial Position Error X, nm
    // double x;
    //
    // // SRU Navigational Error Y, (GPS = 0.1 nm), nm
    // double y;
    //
    // // Safety Factor, FS
    // double SF;

    VOCTManager voctManager;

    public SAROperation(SAR_TYPE operationType, VOCTManager voctManager) {
        this.operationType = operationType;
        this.voctManager = voctManager;
    }

    public void startRapidResponseCalculations(RapidResponseData data) {

        System.out
                .println("Starting Rapid Response with the following parameters");
        System.out.println("Time of Last known position: " + data.getLKPDate());
        System.out.println("Commence Search Start time: " + data.getCSSDate());

        double difference = (double) (data.getCSSDate().getMillis() - data
                .getLKPDate().getMillis()) / 60 / 60 / 1000;

        data.setTimeElasped(difference);

        // System.out.println("Hours since started: " + difference);

        rapidResponse(data);
    }

    public void startDatumPointCalculations(DatumPointData data) {
        System.out
                .println("Starting Datum Point with the following parameters");
        System.out.println("Time of Last known position: " + data.getLKPDate());
        System.out.println("Commence Search Start time: " + data.getCSSDate());

        double difference = (double) (data.getCSSDate().getMillis() - data
                .getLKPDate().getMillis()) / 60 / 60 / 1000;

        data.setTimeElasped(difference);

        // System.out.println("Hours since started: " + difference);

        datumPoint(data);
    }

    /**
     * @return the voctManager
     */
    public VOCTManager getVoctManager() {
        return voctManager;
    }

    /**
     * @param voctManager
     *            the voctManager to set
     */
    public void setVoctManager(VOCTManager voctManager) {
        this.voctManager = voctManager;
    }

    private double searchObjectValue(int searchObject, double LWKnots) {

        switch (searchObject) {
        case 0:
            return LeewayValues.personInWater(LWKnots);
        case 1:
            return LeewayValues.raftFourToSix(LWKnots);
        case 2:
            return LeewayValues.raftFourToSixWithDriftAnker(LWKnots);
        case 3:
            return LeewayValues.raftFourToSixWithoutDriftAnker(LWKnots);
        case 4:
            return LeewayValues.raftFifteenToTwentyFive(LWKnots);
        case 5:
            return LeewayValues.raftFifteenToTwentyFiveWithDriftAnker(LWKnots);
        case 6:
            return LeewayValues
                    .raftFifteenToTwentyFiveWitouthDriftAnker(LWKnots);
        case 7:
            return LeewayValues.dinghyFlatBottom(LWKnots);
        case 8:
            return LeewayValues.dinghyWithKeel(LWKnots);
        case 9:
            return LeewayValues.dinghyCapsized(LWKnots);
        case 10:
            return LeewayValues.kayakWithPerson(LWKnots);
        case 11:
            return LeewayValues.surfboardWithPerson(LWKnots);
        case 12:
            return LeewayValues.windsurferWithPersonMastAndSailInWater(LWKnots);
        case 13:
            return LeewayValues.sailboatLongKeel(LWKnots);
        case 14:
            return LeewayValues.sailboatFinKeel(LWKnots);
        case 15:
            return LeewayValues.motorboat(LWKnots);
        case 16:
            return LeewayValues.fishingVessel(LWKnots);
        case 17:
            return LeewayValues.trawler(LWKnots);
        case 18:
            return LeewayValues.coaster(LWKnots);
        case 19:
            return LeewayValues.wreckage(LWKnots);

        }

        return -9999.9;
    }

    private int searchObjectValue(int searchObject) {

        switch (searchObject) {
        case 0:
            return LeewayValues.personInWater();
        case 1:
            return LeewayValues.raftFourToSix();
        case 2:
            return LeewayValues.raftFourToSixWithDriftAnker();
        case 3:
            return LeewayValues.raftFourToSixWithoutDriftAnker();
        case 4:
            return LeewayValues.raftFifteenToTwentyFive();
        case 5:
            return LeewayValues.raftFifteenToTwentyFiveWithDriftAnker();
        case 6:
            return LeewayValues.raftFifteenToTwentyFiveWitouthDriftAnker();
        case 7:
            return LeewayValues.dinghyFlatBottom();
        case 8:
            return LeewayValues.dinghyWithKeel();
        case 9:
            return LeewayValues.dinghyCapsized();
        case 10:
            return LeewayValues.kayakWithPerson();
        case 11:
            return LeewayValues.surfboardWithPerson();
        case 12:
            return LeewayValues.windsurferWithPersonMastAndSailInWater();
        case 13:
            return LeewayValues.sailboatLongKeel();
        case 14:
            return LeewayValues.sailboatFinKeel();
        case 15:
            return LeewayValues.motorboat();
        case 16:
            return LeewayValues.fishingVessel();
        case 17:
            return LeewayValues.trawler();
        case 18:
            return LeewayValues.coaster();
        case 19:
            return LeewayValues.wreckage();

        }

        return -9999;
    }

    public void datumPoint(DatumPointData data) {

        double timeElapsed = data.getTimeElasped();

        double currentTWC = data.getWeatherPoints().get(0).getTWCknots()
                * timeElapsed;

        double leewayspeed = searchObjectValue(data.getSearchObject(), data
                .getWeatherPoints().get(0).getLWknots());

        double leeway = leewayspeed * timeElapsed;
        double leewayDivergence = searchObjectValue(data.getSearchObject());

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Object starts at LKP, with TWCheading, drifting for currentWTC
        // knots where will it end up
        Position currentPos = Calculator.calculateEndingGlobalCoordinates(
                reference, data.getLKP(), data.getWeatherPoints().get(0)
                        .getTWCHeading(), Converter.nmToMeters(currentTWC),
                endBearing);

        // This is the TWC point
        data.setWtc(currentPos);

        // We now have 3 different datums to calculate, DW, min and max.
        endBearing = new double[1];

        Position datumDownWind = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, data.getWeatherPoints().get(0)
                        .getDownWind(), Converter.nmToMeters(leeway),
                endBearing);

        endBearing = new double[1];

        Position datumMin = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, data.getWeatherPoints().get(0)
                        .getDownWind()
                        - leewayDivergence, Converter.nmToMeters(leeway),
                endBearing);

        endBearing = new double[1];

        Position datumMax = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, data.getWeatherPoints().get(0)
                        .getDownWind()
                        + leewayDivergence, Converter.nmToMeters(leeway),
                endBearing);

        data.setDatumDownWind(datumDownWind);
        data.setDatumMin(datumMin);
        data.setDatumMax(datumMax);

        // RDV Direction
        double rdvDirectionDownWind = Calculator.bearing(data.getLKP(),
                datumDownWind, Heading.RL);

        data.setRdvDirectionDownWind(rdvDirectionDownWind);

        double rdvDirectionMin = Calculator.bearing(data.getLKP(), datumMin,
                Heading.RL);

        data.setRdvDirectionMin(rdvDirectionMin);

        double rdvDirectionMax = Calculator.bearing(data.getLKP(), datumMax,
                Heading.RL);

        data.setRdvDirectionMax(rdvDirectionMax);

        // RDV Distance
        double rdvDistanceDownWind = Calculator.range(data.getLKP(),
                datumDownWind, Heading.RL);

        data.setRdvDistanceDownWind(rdvDistanceDownWind);

        double rdvDistanceMin = Calculator.range(data.getLKP(), datumMin,
                Heading.RL);

        data.setRdvDistanceMin(rdvDistanceMin);

        double rdvDistanceMax = Calculator.range(data.getLKP(), datumMax,
                Heading.RL);

        data.setRdvDistanceMax(rdvDistanceMin);

        // RDV Speed
        double rdvSpeedDownWind = rdvDistanceDownWind / data.getTimeElasped();
        data.setRdvSpeedDownWind(rdvSpeedDownWind);

        double rdvSpeedMin = rdvDistanceMin / data.getTimeElasped();
        data.setRdvSpeedMin(rdvSpeedMin);

        double rdvSpeedMax = rdvDistanceMax / data.getTimeElasped();
        data.setRdvSpeedMax(rdvSpeedMax);

        // Radius:
        double radiusDownWind = ((data.getX() + data.getY()) + 0.3
                * rdvDistanceDownWind) * data.getSF();
        data.setRadiusDownWind(radiusDownWind);

        double radiusMin = ((data.getX() + data.getY()) + 0.3 * rdvDistanceMin)
                * data.getSF();
        data.setRadiusMin(radiusMin);

        double radiusMax = ((data.getX() + data.getY()) + 0.3 * rdvDistanceMax)
                * data.getSF();

        data.setRadiusMax(radiusMax);

        findSmallestSquare(data);

        voctManager.setSarData(data);
    }

    private void findSmallestSquare(DatumPointData data) {

        // Find circles furtest from each other - that will be the encompassing
        // box - after this box has been decided grow the box so it encompasses
        // the last circle.

        // Will always be max and min?

        Position datumMin = data.getDatumMin();
        Position datumMax = data.getDatumMax();
        Position datumDownWind = data.getDatumDownWind();

        double radiusMin = data.getRadiusMin();
        double radiusMax = data.getRadiusMax();
        double radiusDownWind = data.getRadiusDownWind();

        // Bearing between the two points - this will be the direction of the
        // box.
        double lengthBearing = Calculator.bearing(datumMax, datumMin,
                Heading.RL);

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Find A and D

        // Find top Position from radius
        Position TopPointMax = Calculator.calculateEndingGlobalCoordinates(
                reference, datumMax, lengthBearing + 90,
                Converter.nmToMeters(radiusMax), endBearing);

        endBearing = new double[1];

        Position BottomPointMax = Calculator.calculateEndingGlobalCoordinates(
                reference, datumMax, lengthBearing - 90,
                Converter.nmToMeters(radiusMax), endBearing);
        endBearing = new double[1];

        Position topPointMaxInnerBox = Calculator
                .calculateEndingGlobalCoordinates(reference, TopPointMax,
                        lengthBearing + 180, Converter.nmToMeters(radiusMax),
                        endBearing);

        data.setA(topPointMaxInnerBox);

        Position bottomPointMaxInnerBox = Calculator
                .calculateEndingGlobalCoordinates(reference, BottomPointMax,
                        lengthBearing + 180, Converter.nmToMeters(radiusMax),
                        endBearing);

        data.setD(bottomPointMaxInnerBox);

        // Find top Position from radius
        Position TopPointMin = Calculator.calculateEndingGlobalCoordinates(
                reference, datumMin, lengthBearing + 90,
                Converter.nmToMeters(radiusMin), endBearing);

        endBearing = new double[1];

        Position BottomPointMin = Calculator.calculateEndingGlobalCoordinates(
                reference, datumMin, lengthBearing - 90,
                Converter.nmToMeters(radiusMin), endBearing);
        endBearing = new double[1];

        // Position top of min circle, turn 180 around and go radius distance to
        // find a box point
        Position topPointMinInnerBox = Calculator
                .calculateEndingGlobalCoordinates(reference, TopPointMin,
                        lengthBearing, Converter.nmToMeters(radiusMin),
                        endBearing);

        data.setB(topPointMinInnerBox);

        endBearing = new double[1];

        Position bottomPointMinInnerBox = Calculator
                .calculateEndingGlobalCoordinates(reference, BottomPointMin,
                        lengthBearing, Converter.nmToMeters(radiusMin),
                        endBearing);
        endBearing = new double[1];

        data.setC(bottomPointMinInnerBox);

        System.out.println("Bearing from datum to max is");
        double bearingFromDownwindToMax = Calculator.bearing(datumDownWind,
                datumMax, Heading.RL);
        System.out.println(bearingFromDownwindToMax);

        double growthBearing;

        
        System.out.println("Length bearing of normal box is " + lengthBearing);
        
        if (bearingFromDownwindToMax > 90 && bearingFromDownwindToMax < 270) {
            growthBearing = lengthBearing + 90;
        }else{
            growthBearing = -(lengthBearing + 90);
        }
        
            // Going down - replace D, C
            growthBearing = lengthBearing + 90;
            
            
            System.out.println("Growing in plus 90 " + growthBearing);
            
            double boxLength = topPointMaxInnerBox.distanceTo(
                    topPointMinInnerBox, CoordinateSystem.GEODETIC);

                System.out.println("Length of Box: " + boxLength);
            
            Position growthCenterPosition = Calculator
                    .calculateEndingGlobalCoordinates(reference, datumDownWind,
                            growthBearing, Converter.nmToMeters(radiusDownWind),
                            endBearing);
            endBearing = new double[1];
          
            Position A = Calculator.calculateEndingGlobalCoordinates(reference,
                    growthCenterPosition, lengthBearing - 180,
                    boxLength/2, endBearing);
            endBearing = new double[1];
            data.setA(A);

            Position B = Calculator.calculateEndingGlobalCoordinates(reference,
                    growthCenterPosition, lengthBearing,
                    boxLength/2, endBearing);
            endBearing = new double[1];
            
            data.setB(B);
     
            
//            Position An = data.getA();
//            Position Bn = data.getB();
//            Position Cn = data.getC();
//            Position Dn = data.getD();
////            
////            
//            data.setA(Dn);
//            data.setB(Cn);
////            
//            data.setC(Bn);
//            data.setD(An);
            
    }

    public Position applyDriftToPoint(SARData data, Position point,
            double timeElapsed) {

        double currentTWC = data.getWeatherPoints().get(0).getTWCknots()
                * timeElapsed;
        double leewayspeed = searchObjectValue(data.getSearchObject(), data
                .getWeatherPoints().get(0).getLWknots());
        double leeway = leewayspeed * timeElapsed;

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Object starts at LKP, with TWCheading, drifting for currentWTC
        // knots where will it end up
        Position currentPos = Calculator.calculateEndingGlobalCoordinates(
                reference, point, data.getWeatherPoints().get(0)
                        .getTWCHeading(), Converter.nmToMeters(currentTWC),
                endBearing);

        endBearing = new double[1];

        Position windPos = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, data.getWeatherPoints().get(0)
                        .getDownWind(), Converter.nmToMeters(leeway),
                endBearing);

        Position datum = windPos;

        return datum;
    }

    private void rapidResponse(RapidResponseData data) {

        // System.out.println("Calculation for Rapid Response");

        double currentTWC = data.getWeatherPoints().get(0).getTWCknots()
                * data.getTimeElasped();
        // System.out.println("Current TWC is: " + currentTWC +
        // " with heading: "
        // + TWCHeading);

        // Example person in water, influenced by the wind of LWknots speed
        // will have a final speed of leewayspeed:
        // double leewayspeed = LeewayValues.personInWater(LWknots);
        double leewayspeed = searchObjectValue(data.getSearchObject(), data
                .getWeatherPoints().get(0).getLWknots());

        // Leeway, object have floated for how long at what time
        double leeway = leewayspeed * data.getTimeElasped();

        System.out.println("Leeway is: " + leeway
                + " nautical miles with heading: "
                + data.getWeatherPoints().get(0).getDownWind());

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Object starts at LKP, with TWCheading, drifting for currentWTC
        // knots where will it end up
        Position currentPos = Calculator.calculateEndingGlobalCoordinates(
                reference, data.getLKP(), data.getWeatherPoints().get(0)
                        .getTWCHeading(), Converter.nmToMeters(currentTWC),
                endBearing);

        System.out.println("Current is: " + currentPos.getLatitude());
        System.out.println("Current is: " + currentPos.getLongitude());

        data.setWtc(currentPos);

        endBearing = new double[1];

        Position windPos = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, data.getWeatherPoints().get(0)
                        .getDownWind(), Converter.nmToMeters(leeway),
                endBearing);

        System.out.println("Wind pos is: " + windPos.getLatitude());
        System.out.println("Wind pos is: " + windPos.getLongitude());

        Position datum = windPos;

        data.setDatum(datum);

        System.out.println("Final position is " + datum);

        // RDV Direction
        double rdvDirection = Calculator.bearing(data.getLKP(), windPos,
                Heading.RL);

        data.setRdvDirection(rdvDirection);
        System.out.println("RDV Direction: " + rdvDirection);

        // RDV Distance
        double rdvDistance = Calculator.range(data.getLKP(), windPos,
                Heading.RL);

        data.setRdvDistance(rdvDistance);
        System.out.println("RDV Distance: " + rdvDistance);

        // RDV Speed
        double rdvSpeed = rdvDistance / data.getTimeElasped();

        data.setRdvSpeed(rdvSpeed);
        System.out.println("RDV Speed: " + rdvSpeed);

        // Radius:
        double radius = ((data.getX() + data.getY()) + 0.3 * rdvDistance)
                * data.getSF();

        data.setRadius(radius);

        System.out.println("Radius is: " + radius);

        // datum
        // radius
        // LKP
        // windPos

        // find box

        findRapidResponseBox(datum, radius, data);

        voctManager.setSarData(data);
    }

    public static void findRapidResponseBox(Position datum, double radius,
            RapidResponseData data) {
        // Search box
        // The box is square around the circle, with center point at datum
        // Radius is the calculated Radius
//        data.getRdvDirection()
        double verticalDirection = data.getRdvDirection();
        double horizontalDirection = verticalDirection + 90;
        
        if (horizontalDirection > 360){
            horizontalDirection = horizontalDirection - 360;
        }

        // First top side of the box
        Position topCenter = Calculator.findPosition(datum, verticalDirection,
                Converter.nmToMeters(radius));
        

        // Bottom side of the box
        Position bottomCenter = Calculator.findPosition(datum, Calculator.reverseDirection(verticalDirection),
                Converter.nmToMeters(radius));

        // Go left radius length
        Position A = Calculator.findPosition(topCenter, Calculator.reverseDirection(horizontalDirection),
                Converter.nmToMeters(radius));
        Position B = Calculator.findPosition(topCenter, horizontalDirection,
                Converter.nmToMeters(radius));
        Position C = Calculator.findPosition(bottomCenter, horizontalDirection,
                Converter.nmToMeters(radius));
        Position D = Calculator.findPosition(bottomCenter, Calculator.reverseDirection(horizontalDirection),
                Converter.nmToMeters(radius));

        System.out.println("Final box parameters:");
        System.out.println("A: " + A.getLatitude());
        System.out.println("A: " + A.getLongitude());

        System.out.println("B: " + B.getLatitude());
        System.out.println("B: " + B.getLongitude());

        System.out.println("C: " + C.getLatitude());
        System.out.println("C: " + C.getLongitude());

        System.out.println("D: " + D.getLatitude());
        System.out.println("D: " + D.getLongitude());

        double area = radius * 2 * radius * 2;

        System.out.println("Area in nm2 is: " + area);

        // Effort Allocation /// wait

        // // desired pod, in decimals
        // double pod = 0.79;
        //
        // // Swep Width
        // double W = 0.5;
        //
        // // System.out.println("W : 0.8");
        // // System.out.println("S : 0.7875");
        //
        // // System.out.println("POD is " + findPoD(0.8, 0.7875));
        // System.out.println("POD is " + findPoD(1.1, 0.8));

        data.setBox(A, B, C, D);

    }

    public SAR_TYPE getOperationType() {
        return this.operationType;
    }

    public void calculateEffortAllocation(SARData data) {
        double trackSpacing = findS(data.getEffortAllocationData().getW(), data
                .getEffortAllocationData().getPod());
        
        
        data.getEffortAllocationData().setTrackSpacing(trackSpacing);

        double groundSpeed = data.getEffortAllocationData().getGroundSpeed();
        int timeSearching = data.getEffortAllocationData().getSearchTime();

        System.out.println("Track Spacing is: " + trackSpacing);
        System.out.println("Ground speed is: " + groundSpeed);
        System.out.println("Time searching is: " + timeSearching);

        double areaSize = trackSpacing * groundSpeed * timeSearching;

        data.getEffortAllocationData().setEffectiveAreaSize(areaSize);

        System.out.println("Area size: " + areaSize);

    }

    private double findS(double W, double PoD) {
        // S = W*(-5/8*ln(1-x))^(-5/7)

        double val1 = (-5.0 / 8.0) * Math.log(1 - PoD);
        double val2 = Math.pow(val1, -5.0 / 7.0);

        // System.out.println("Val 1 is " + val1);
        // System.out.println("Val 2 is " + val2);

        return W * val2;
    }

}
