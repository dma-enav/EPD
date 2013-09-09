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
 * 
 * @author David
 * 
 */
public class SAROperation {

    SAR_TYPE operationType;

    // Time of Last known position
    DateTime TLKP;

    // Commence Search Start
    DateTime CSS;

    // Commence time of surface Drift - When is this used?
    Date CTSD;

    // Last known Position
    Position LKP;

    // SURFACE DRIFT VARIABLES

    // Observed Total Water Current (WTC, knots)
    double TWCknots;

    // TWC Vector //either a direction or heading, or degrees
    double TWCHeading;

    // Leeway (LW), knots
    double LWknots;

    // Leeway Vector, heading or degrees
    double LWHeading;

    // The way the wind is blow means the object is pushed in the opposite
    // direction
    double downWind;

    // Initial Position Error X, nm
    double x;

    // SRU Navigational Error Y, (GPS = 0.1 nm), nm
    double y;

    // Safety Factor, FS
    double SF;

    VOCTManager voctManager;

    public SAROperation(SAR_TYPE operationType, VOCTManager voctManager) {
        this.operationType = operationType;
        this.voctManager = voctManager;

        // Standard use CET?
        DateTimeZone timeZone = DateTimeZone.forID("CET");

        TLKP = new DateTime(2013, 7, 2, 8, 0, timeZone);

        CSS = new DateTime(2013, 7, 2, 10, 30, timeZone);

        LKP = Position.create(56.37167, 7.966667);

        TWCknots = 2;

        TWCHeading = 180;

        LWknots = 15;

        LWHeading = 270;

        downWind = LWHeading - 180;

        x = 1.0;

        y = 0.1;

        SF = 1.0;

        System.out.println("Starting search with the following parameters");
        System.out.println("Time of Last known position: " + TLKP);
        System.out.println("Commence Search Start time: " + CSS);

        double difference = (double) (CSS.getMillis() - TLKP.getMillis()) / 60 / 60 / 1000;
        System.out.println("Hours since started: " + difference);

        rapidResponse(LKP, TWCHeading, downWind, LWknots, TWCknots, difference,
                x, y, SF);
    }

    /**
     * @return the operationType
     */
    public SAR_TYPE getOperationType() {
        return operationType;
    }

    /**
     * @return the tLKP
     */
    public DateTime getTLKP() {
        return TLKP;
    }

    /**
     * @param tLKP
     *            the tLKP to set
     */
    public void setTLKP(DateTime tLKP) {
        TLKP = tLKP;
    }

    /**
     * @return the cSS
     */
    public DateTime getCSS() {
        return CSS;
    }

    /**
     * @param cSS
     *            the cSS to set
     */
    public void setCSS(DateTime cSS) {
        CSS = cSS;
    }

    /**
     * @return the cTSD
     */
    public Date getCTSD() {
        return CTSD;
    }

    /**
     * @param cTSD
     *            the cTSD to set
     */
    public void setCTSD(Date cTSD) {
        CTSD = cTSD;
    }

    /**
     * @return the lKP
     */
    public Position getLKP() {
        return LKP;
    }

    /**
     * @param lKP
     *            the lKP to set
     */
    public void setLKP(Position lKP) {
        LKP = lKP;
    }

    /**
     * @return the tWCknots
     */
    public double getTWCknots() {
        return TWCknots;
    }

    /**
     * @param tWCknots
     *            the tWCknots to set
     */
    public void setTWCknots(double tWCknots) {
        TWCknots = tWCknots;
    }

    /**
     * @return the tWCHeading
     */
    public double getTWCHeading() {
        return TWCHeading;
    }

    /**
     * @param tWCHeading
     *            the tWCHeading to set
     */
    public void setTWCHeading(double tWCHeading) {
        TWCHeading = tWCHeading;
    }

    /**
     * @return the lWknots
     */
    public double getLWknots() {
        return LWknots;
    }

    /**
     * @param lWknots
     *            the lWknots to set
     */
    public void setLWknots(double lWknots) {
        LWknots = lWknots;
    }

    /**
     * @return the lWHeading
     */
    public double getLWHeading() {
        return LWHeading;
    }

    /**
     * @param lWHeading
     *            the lWHeading to set
     */
    public void setLWHeading(double lWHeading) {
        LWHeading = lWHeading;
    }

    /**
     * @return the downWind
     */
    public double getDownWind() {
        return downWind;
    }

    /**
     * @param downWind
     *            the downWind to set
     */
    public void setDownWind(double downWind) {
        this.downWind = downWind;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y
     *            the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the sF
     */
    public double getSF() {
        return SF;
    }

    /**
     * @param sF
     *            the sF to set
     */
    public void setSF(double sF) {
        SF = sF;
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

    private static void rapidResponse(Position LKP, double TWCHeading,
            double downWind, double LWknots, double TWCknots,
            double timeElasped, double x, double y, double SF) {
        System.out.println("Calculation for Rapid Response");

        System.out.println("LKP is: " + LKP);

        double currentTWC = TWCknots * timeElasped;
        System.out.println("Current TWC is: " + currentTWC + " with heading: "
                + TWCHeading);

        // Example person in water, influenced by the wind of LWknots speed
        // will have a final speed of leewayspeed:
        double leewayspeed = LeewayValues.personInWater(LWknots);

        // Leeway, object have floated for how long at what time
        double leeway = leewayspeed * timeElasped;

        System.out.println("Leeway is: " + leeway
                + " nautical miles with heading: " + downWind);
        // leeway = 0.5875;

        System.out.println(LKP.getLatitude());
        System.out.println(LKP.getLongitude());

        Ellipsoid reference = Ellipsoid.WGS84;
        double[] endBearing = new double[1];

        // Object starts at LKP, with TWCheading, drifting for currentWTC
        // knots where will it end up
        Position currentPos = Calculator.calculateEndingGlobalCoordinates(
                reference, LKP, TWCHeading, Converter.nmToMeters(currentTWC),
                endBearing);

        System.out.println("Current is: " + currentPos.getLatitude());
        System.out.println("Current is: " + currentPos.getLongitude());

        endBearing = new double[1];

        Position windPos = Calculator.calculateEndingGlobalCoordinates(
                reference, currentPos, downWind, Converter.nmToMeters(leeway),
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

        // find box

        findBox(datum, radius);
    }

    public static void findBox(Position datum, double radius) {
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

        // System.out.println("POD is " + findPoD(0.8, 0.7875));
        System.out.println("POD is " + findPoD(1.1, 0.8));
    }

    public static double findPoD(double W, double S) {
        System.out.println("W is " + W);
        System.out.println("S is " + S);

        double val1 = -8.0 / 5.0;
        double val2 = W / S;
        double val3 = Math.pow(val2, 7.0 / 5.0);

        // System.out.println("val 1: " + val1);
        // System.out.println("val 2: " + val2);
        // System.out.println("val 3: " + val3);
        // System.out.println("internal: " + (val1*val3));

        double pod = 1 - Math.exp(val1 * val3);

        return pod;

    }
}
