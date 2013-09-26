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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.text.Formatter;


public class RapidResponseData {

    private DateTime LKPDate;
    private DateTime CSSDate;

    private Position LKP;
    private Position CSP;
    private double x;
    private double y;
    private double SF;
    private int searchObject;
    private double TWCHeading;
    private double TWCknots;

    private double LWknots;
    private double LWHeading;
    private double downWind;

    private Position datum;
    private Position wtc;

    private double radius;

    private double timeElasped;

    private double rdvDirection;
    private double rdvDistance;
    private double rdvSpeed;

    private Position A;
    private Position B;
    private Position C;
    private Position D;

    
    
    private double w;
    private double groundSpeed;
    private double pod;
    private double trackSpacing;
    private int searchTime;
    private double effectiveAreaSize;
    
    
    double effectiveAreaWidth;
    double effectiveAreaHeight;
    
    Position effectiveAreaA;
    Position effectiveAreaB;
    Position effectiveAreaC;
    Position effectiveAreaD;
    
    
    
    // public RapidResponseData(Position lKP, Position datum, Position wtc,
    // double radius, DateTime LKPDate, DateTime CSSDate) {
    // super();
    // LKP = lKP;
    // this.datum = datum;
    // this.wtc = wtc;
    // this.radius = radius;
    // this.LKPDate = LKPDate;
    // this.CSSDate = CSSDate;
    // }

    // Init data
    public RapidResponseData(DateTime TLKP, DateTime CSS, Position LKP,
            Position CSP, double TWCknots, double TWCHeading, double LWknots,
            double LWHeading, double x, double y, double SF, int searchObject) {

        this.LKP = LKP;
        this.LKPDate = TLKP;
        this.CSSDate = CSS;
        this.CSP = CSP;
        this.TWCknots = TWCknots;
        this.TWCHeading = TWCHeading;
        this.LWknots = LWknots;
        this.LWHeading = LWHeading;

        this.x = x;
        this.y = y;
        this.SF = SF;
        this.searchObject = searchObject;

    }

    public void setBox(Position A, Position B, Position C, Position D) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
    }

    
    
    
    /**
     * @return the effectiveAreaSize
     */
    public double getEffectiveAreaSize() {
        return effectiveAreaSize;
    }

    /**
     * @param effectiveAreaSize the effectiveAreaSize to set
     */
    public void setEffectiveAreaSize(double effectiveAreaSize) {
        this.effectiveAreaSize = effectiveAreaSize;
    }

    /**
     * @return the searchTime
     */
    public int getSearchTime() {
        return searchTime;
    }

    /**
     * @param searchTime the searchTime to set
     */
    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    /**
     * @return the w
     */
    public double getW() {
        return w;
    }

    /**
     * @param w the w to set
     */
    public void setW(double w) {
        this.w = w;
    }

    /**
     * @return the groundSpeed
     */
    public double getGroundSpeed() {
        return groundSpeed;
    }

    /**
     * @param groundSpeed the groundSpeed to set
     */
    public void setGroundSpeed(double groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    /**
     * @return the pod
     */
    public double getPod() {
        return pod;
    }

    /**
     * @param pod the pod to set
     */
    public void setPod(double pod) {
        this.pod = pod;
    }

    /**
     * @return the trackSpacing
     */
    public double getTrackSpacing() {
        return trackSpacing;
    }

    /**
     * @param trackSpacing the trackSpacing to set
     */
    public void setTrackSpacing(double trackSpacing) {
        this.trackSpacing = trackSpacing;
    }

    /**
     * @return the rdvDirection
     */
    public double getRdvDirection() {
        return rdvDirection;
    }

    /**
     * @param rdvDirection
     *            the rdvDirection to set
     */
    public void setRdvDirection(double rdvDirection) {
        this.rdvDirection = rdvDirection;
    }

    /**
     * @return the rdvDistance
     */
    public double getRdvDistance() {
        return rdvDistance;
    }

    /**
     * @param rdvDistance
     *            the rdvDistance to set
     */
    public void setRdvDistance(double rdvDistance) {
        this.rdvDistance = rdvDistance;
    }

    /**
     * @return the rdvSpeed
     */
    public double getRdvSpeed() {
        return rdvSpeed;
    }

    /**
     * @param rdvSpeed
     *            the rdvSpeed to set
     */
    public void setRdvSpeed(double rdvSpeed) {
        this.rdvSpeed = rdvSpeed;
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
     * @return the lKPDate
     */
    public DateTime getLKPDate() {
        return LKPDate;
    }

    /**
     * @param lKPDate
     *            the lKPDate to set
     */
    public void setLKPDate(DateTime lKPDate) {
        LKPDate = lKPDate;
    }

    /**
     * @return the cSSDate
     */
    public DateTime getCSSDate() {
        return CSSDate;
    }

    /**
     * @param cSSDate
     *            the cSSDate to set
     */
    public void setCSSDate(DateTime cSSDate) {
        CSSDate = cSSDate;
    }

    /**
     * @return the cSP
     */
    public Position getCSP() {
        return CSP;
    }

    /**
     * @param cSP
     *            the cSP to set
     */
    public void setCSP(Position cSP) {
        CSP = cSP;
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
     * @return the searchObject
     */
    public int getSearchObject() {
        return searchObject;
    }

    /**
     * @param searchObject
     *            the searchObject to set
     */
    public void setSearchObject(int searchObject) {
        this.searchObject = searchObject;
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
     * @return the timeElasped
     */
    public double getTimeElasped() {
        return timeElasped;
    }

    /**
     * @param timeElasped
     *            the timeElasped to set
     */
    public void setTimeElasped(double timeElasped) {
        this.timeElasped = timeElasped;
    }

    /**
     * @param lKP
     *            the lKP to set
     */
    public void setLKP(Position lKP) {
        LKP = lKP;
    }

    /**
     * @param datum
     *            the datum to set
     */
    public void setDatum(Position datum) {
        this.datum = datum;
    }

    /**
     * @param wtc
     *            the wtc to set
     */
    public void setWtc(Position wtc) {
        this.wtc = wtc;
    }

    /**
     * @param radius
     *            the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * @param a
     *            the a to set
     */
    public void setA(Position a) {
        A = a;
    }

    /**
     * @param b
     *            the b to set
     */
    public void setB(Position b) {
        B = b;
    }

    /**
     * @param c
     *            the c to set
     */
    public void setC(Position c) {
        C = c;
    }

    /**
     * @param d
     *            the d to set
     */
    public void setD(Position d) {
        D = d;
    }

    /**
     * @return the lKP
     */
    public Position getLKP() {
        return LKP;
    }

    /**
     * @return the datum
     */
    public Position getDatum() {
        return datum;
    }

    /**
     * @return the wtc
     */
    public Position getWtc() {
        return wtc;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return the a
     */
    public Position getA() {
        return A;
    }

    /**
     * @return the b
     */
    public Position getB() {
        return B;
    }

    /**
     * @return the c
     */
    public Position getC() {
        return C;
    }

    /**
     * @return the d
     */
    public Position getD() {
        return D;
    }

    
    
    
    /**
     * @return the effectiveAreaWidth
     */
    public double getEffectiveAreaWidth() {
        return effectiveAreaWidth;
    }

    /**
     * @param effectiveAreaWidth the effectiveAreaWidth to set
     */
    public void setEffectiveAreaWidth(double effectiveAreaWidth) {
        this.effectiveAreaWidth = effectiveAreaWidth;
    }

    /**
     * @return the effectiveAreaHeight
     */
    public double getEffectiveAreaHeight() {
        return effectiveAreaHeight;
    }

    /**
     * @param effectiveAreaHeight the effectiveAreaHeight to set
     */
    public void setEffectiveAreaHeight(double effectiveAreaHeight) {
        this.effectiveAreaHeight = effectiveAreaHeight;
    }

    /**
     * @return the effectiveAreaA
     */
    public Position getEffectiveAreaA() {
        return effectiveAreaA;
    }

    /**
     * @param effectiveAreaA the effectiveAreaA to set
     */
    public void setEffectiveAreaA(Position effectiveAreaA) {
        this.effectiveAreaA = effectiveAreaA;
    }

    /**
     * @return the effectiveAreaB
     */
    public Position getEffectiveAreaB() {
        return effectiveAreaB;
    }

    /**
     * @param effectiveAreaB the effectiveAreaB to set
     */
    public void setEffectiveAreaB(Position effectiveAreaB) {
        this.effectiveAreaB = effectiveAreaB;
    }

    /**
     * @return the effectiveAreaC
     */
    public Position getEffectiveAreaC() {
        return effectiveAreaC;
    }

    /**
     * @param effectiveAreaC the effectiveAreaC to set
     */
    public void setEffectiveAreaC(Position effectiveAreaC) {
        this.effectiveAreaC = effectiveAreaC;
    }

    /**
     * @return the effectiveAreaD
     */
    public Position getEffectiveAreaD() {
        return effectiveAreaD;
    }

    /**
     * @param effectiveAreaD the effectiveAreaD to set
     */
    public void setEffectiveAreaD(Position effectiveAreaD) {
        this.effectiveAreaD = effectiveAreaD;
    }

    public String generateHTML() {
        
        
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM, yyyy, 'at.' HH':'mm");
        
        // // Generate a html sheet of rapid response calculations
        StringBuilder str = new StringBuilder();
        // String name = "How does this look";
        //
        str.append("<html>");
        str.append("<table >");
        str.append("<tr>");
        str.append("<td align=\"left\" style=\"vertical-align: top;\">");
        str.append("<h1>Search and Rescue - Rapid Response</h1>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Time of Last Known Position: " + fmt.print(LKPDate) + "");
        str.append("<br>Last Known Position: " + LKP.toString() + "</br>");
        str.append("<br>Commence Search Start time: " + fmt.print(CSSDate) + "</br>");
        str.append("<br>Commence Search Point: " + CSP.toString() + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Total Water Current: " + TWCknots + " knots with heading " + TWCHeading + "°");
        str.append("<br>Leeway: " + LWknots + " knots with heading " + LWHeading + "°</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Initial Position Error, X in nautical miles: " + x + "");
        str.append("<br>SRU Navigational Error, Y in nautical miles: " + y + "</br>");
        str.append("<br>Safety Factor, Fs: " + SF + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Object: " + LeewayValues.getLeeWayTypes().get(searchObject) + "");
        str.append("<br>With value: " + LeewayValues.getLeeWayContent().get(searchObject) + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Time Elapsed: " + Formatter.formatHours(timeElasped) + "");
        str.append("<br>Applying Leeway and TWC gives a datum of  " + datum.toString() + "</br>");
        str.append("<br>With the following Residual Drift Vector</br>");
        str.append("<br>RDV Direction: " + rdvDirection + "°</br>");
        str.append("<br>RDV Distance: " + Formatter.formatDouble(rdvDistance, 2) + " nm</br>");
        str.append("<br>RDV Speed: " + Formatter.formatDouble(rdvSpeed, 2) + " kn/h</br>");
        str.append("<br>With radius: " + Formatter.formatDouble(radius, 2) + "nm </br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Area:");
        str.append("<br>A: " + A.toString() + "</br>");
        str.append("<br>B: " + B.toString() + "</br>");
        str.append("<br>C: " + C.toString() + "</br>");
        str.append("<br>D: " + D.toString() + "</br>");
        str.append("<br>Total Size: " + Formatter.formatDouble(radius*2*radius*2, 2) + " nm2</br>");

        str.append("</font>");
        str.append("</td>");
        str.append("</tr>");
        str.append("</table>");
        str.append("</html>");
        //
        // calculationsText.setText(str.toString());
        //
        //

        return str.toString();
    }
    
    
    
}
