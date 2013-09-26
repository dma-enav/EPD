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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.prototype.model.voct.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
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
        double downWind = data.getLWHeading() - 180;
        data.setDownWind(downWind);

        System.out.println("Starting search with the following parameters");
        System.out.println("Time of Last known position: " + data.getLKPDate());
        System.out.println("Commence Search Start time: " + data.getCSSDate());

        double difference = (double) (data.getCSSDate().getMillis() - data
                .getLKPDate().getMillis()) / 60 / 60 / 1000;

        data.setTimeElasped(difference);

        // System.out.println("Hours since started: " + difference);

        rapidResponse(data);
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

    // private void rapidResponse(Position LKP, double TWCHeading,
    // double downWind, double LWknots, double LWHeading, double TWCknots,
    // double timeElasped, double x, double y, double SF,
    // int searchObject, DateTime LKPDate, DateTime CSSDate) {

    private void rapidResponse(RapidResponseData data) {

        // System.out.println("Calculation for Rapid Response");

        double currentTWC = data.getTWCknots() * data.getTimeElasped();
        // System.out.println("Current TWC is: " + currentTWC +
        // " with heading: "
        // + TWCHeading);

        // Example person in water, influenced by the wind of LWknots speed
        // will have a final speed of leewayspeed:
        // double leewayspeed = LeewayValues.personInWater(LWknots);
        double leewayspeed = searchObjectValue(data.getSearchObject(),
                data.getLWknots());

        // Leeway, object have floated for how long at what time
        double leeway = leewayspeed * data.getTimeElasped();

        System.out.println("Leeway is: " + leeway
                + " nautical miles with heading: " + data.getDownWind());

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Object starts at LKP, with TWCheading, drifting for currentWTC
        // knots where will it end up
        Position currentPos = Calculator.calculateEndingGlobalCoordinates(
                reference, data.getLKP(), data.getTWCHeading(),
                Converter.nmToMeters(currentTWC), endBearing);

        System.out.println("Current is: " + currentPos.getLatitude());
        System.out.println("Current is: " + currentPos.getLongitude());

        data.setWtc(currentPos);

        endBearing = new double[1];

        Position windPos = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, data.getDownWind(),
                Converter.nmToMeters(leeway), endBearing);

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
        double radius = data.getX() + data.getY() + 0.3 * rdvDistance
                * data.getSF();

        data.setRadius(radius);

        System.out.println("Radius is: " + radius);

        // datum
        // radius
        // LKP
        // windPos

        // find box

        findRapidResponseBox(datum, radius, data);

        voctManager.setRapidResponseData(data);
    }

    public static void findRapidResponseBox(Position datum, double radius,
            RapidResponseData data) {
        // Search box
        // The box is square around the circle, with center point at datum
        // Radius is the calculated Radius

        // First top side of the box
        Position topCenter = Calculator.findPosition(datum, 0,
                Converter.nmToMeters(radius));

        // Bottom side of the box
        Position bottomCenter = Calculator.findPosition(datum, 180,
                Converter.nmToMeters(radius));

        // Go left radius length
        Position A = Calculator.findPosition(topCenter, 270,
                Converter.nmToMeters(radius));
        Position B = Calculator.findPosition(topCenter, 90,
                Converter.nmToMeters(radius));
        Position C = Calculator.findPosition(bottomCenter, 90,
                Converter.nmToMeters(radius));
        Position D = Calculator.findPosition(bottomCenter, 270,
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
    
    
    public void calculateEffortAllocation(RapidResponseData data){
        double trackSpacing = findS(data.getW(), data.getPod());
        
        data.setTrackSpacing(trackSpacing);
        
        double groundSpeed = data.getGroundSpeed();
        int timeSearching = data.getSearchTime();
        
        System.out.println("Track Spacing is: "  + trackSpacing) ;
        System.out.println("Ground speed is: "  + groundSpeed) ;
        System.out.println("Time searching is: "  + timeSearching) ;
        
        double areaSize = trackSpacing * groundSpeed * timeSearching;
        
        data.setEffectiveAreaSize(areaSize);
        
        System.out.println("Area size: " + areaSize);
        
    }
    
    private double findS(double W, double PoD){
//      S = W*(-5/8*ln(1-x))^(-5/7)
      
      double val1 = (-5.0/8.0)*Math.log(1-PoD);
      double val2 = Math.pow(val1, -5.0/7.0);
      
      
//      System.out.println("Val 1 is " + val1);
//      System.out.println("Val 2 is " + val2);
      
      return W*val2;
  }
  
}
