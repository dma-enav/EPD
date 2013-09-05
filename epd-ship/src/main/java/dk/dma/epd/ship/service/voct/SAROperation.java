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
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Ellipsoid;

/**
 * Where all SAR Calculations are performed
 * 
 * Has a type and the type determines which options are available
 * @author David
 *
 */
public class SAROperation {

    
    SAR_TYPE operationType;
    
    
    public SAROperation(){
        

        System.out.println("VOCT Calculations initializing");
        
        DateTimeZone timeZone = DateTimeZone.forID("CET");

        // System.out.println(timeZone.getID());
        // System.out.println(TLKP.toDateTimeISO());

        // DateTime test = TLKP.toDateTime(DateTimeZone.forID("GMT"));
        // System.out.println(test);

        // Time of Last known position
        DateTime TLKP = new DateTime(2013, 7, 2, 8, 0, timeZone);

        // Commence Search Start
        DateTime CSS = new DateTime(2013, 7, 2, 10, 30, timeZone);

        // Last known Position
        Position LKP = Position.create(56.37167, 7.966667);

        // Surface Drift

        // Commence time of surface Drift
        Date CTSD;

        // Observed Total Water Current (WTC, knots)
        double TWCknots = 2;

        // TWC Vector //either a direction or heading, or degrees
        double TWCHeading = 180;

        // Leeway (LW), knots
        double LWknots = 15;

        // Leeway Vector, heading or degrees
        double LWHeading = 270;
        
        //The way the wind is blow means the object is pushed in the opposite direction
        double downWind = LWHeading-180;

        // Initial Position Error X, nm
        double x = 1.0;

        // SRU Navigational Error Y, (GPS = 0.1 nm), nm
        double y = 0.1;

        // Safety Factor, FS
        double SF = 1.0;
        
        
        System.out.println("Starting search with the following parameters");
        System.out.println("Time of Last known position: " + TLKP);
        System.out.println("Commence Search Start time: " + CSS);
        
        
        double difference = (double) (CSS.getMillis() - TLKP.getMillis()) / 60 / 60 / 1000;
        System.out.println("Hours since started: " + difference);
        
        
        rapidResponse(LKP, TWCHeading, downWind, LWknots, TWCknots, difference, x, y, SF);
    }
    
    
    
    
    private static void rapidResponse(Position LKP, double TWCHeading, double downWind, double LWknots, double TWCknots, double timeElasped, double x, double y, double SF){
        System.out.println("Calculation for Rapid Response");
        
        System.out.println("LKP is: " + LKP);
        
        
        double currentTWC = TWCknots * timeElasped;
        System.out.println("Current TWC is: " + currentTWC + " with heading: " + TWCHeading);
        
        // Example person in water, influenced by the wind of LWknots speed
        // will have a final speed of leewayspeed:
        double leewayspeed = LeewayValues.personInWater(LWknots);
        
        // Leeway, object have floated for how long at what time
        double leeway = leewayspeed * timeElasped;
        
        System.out.println("Leeway is: " + leeway
                + " nautical miles with heading: " + downWind);
//        leeway = 0.5875;
        
        System.out.println(LKP.getLatitude());
        System.out.println(LKP.getLongitude());
        
        
        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Object starts at LKP, with TWCheading, drifting for currentWTC
        // knots where will it end up
        Position currentPos = Calculator.calculateEndingGlobalCoordinates(reference,
                LKP, TWCHeading, Converter.nmToMeters(currentTWC),
                endBearing);

        System.out.println("Current is: " + currentPos.getLatitude());
        System.out.println("Current is: " + currentPos.getLongitude());

        endBearing = new double[1];
        
        Position windPos = Calculator.calculateEndingGlobalCoordinates(reference,
                currentPos, downWind, Converter.nmToMeters(leeway),
                endBearing);

        
        
        System.out.println("Wind pos is: " + windPos.getLatitude());
        System.out.println("Wind pos is: " + windPos.getLongitude());
        
        
        Position datum = windPos;
        
        System.out.println("Final position is " + datum);

        // RDV Direction
        double rdvDirection = Calculator.bearing(LKP, windPos, Heading.RL);

        System.out.println("RDV Direction: " + rdvDirection);

        // RDV Distance
        double rdvDistance = Calculator.range(LKP, windPos, Heading.RL);

        System.out.println("RDV Distance: " + rdvDistance);

        // RDV Speed
        double rdvSpeed = rdvDistance / timeElasped;

        System.out.println("RDV Speed: " + rdvSpeed);

        // Radius:
        double radius = x + y + 0.3 * rdvDistance * SF;

        System.out.println("Radius is: " + radius);
        
        
        
//find box
        
        findBox(datum, radius);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void findBox(Position datum, double radius){
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

        // Effort Allocation

        // desired pod, in decimals
        double pod = 0.79;

        // Swep Width
        double W = 0.5;

        // System.out.println("W : 0.8");
        // System.out.println("S : 0.7875");

//        System.out.println("POD is " + findPoD(0.8, 0.7875));
        System.out.println("POD is " + findPoD(1.1, 0.8));
    }
    
    
    
    
    public static double findPoD(double W, double S) {
        System.out.println("W is " + W);
        System.out.println("S is " + S);
        
        double val1 = -8.0/5.0;
        double val2 = W/S;
        double val3 = Math.pow(val2, 7.0/5.0);
        
//        System.out.println("val 1: " + val1);
//        System.out.println("val 2: " + val2);
//        System.out.println("val 3: " + val3);
//        System.out.println("internal: " + (val1*val3));

        double pod = 1 - Math.exp(val1 * val3);

        return pod;

    }
}

