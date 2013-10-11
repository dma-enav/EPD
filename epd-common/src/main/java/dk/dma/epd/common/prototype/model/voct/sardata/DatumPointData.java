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
package dk.dma.epd.common.prototype.model.voct.sardata;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.text.Formatter;

public class DatumPointData extends SARData {

    private Position wtc;

    private Position datumDownWind;
    private Position datumMin;
    private Position datumMax;

    
    
    double rdvDirectionDownWind;
    double rdvDirectionMin;
    double rdvDirectionMax;
    
    double rdvDistanceDownWind;
    double rdvDistanceMin;
    double rdvDistanceMax;
    
    double rdvSpeedDownWind;
    double rdvSpeedMin;
    double rdvSpeedMax;
    
    double radiusDownWind;
    double radiusMin;
    double radiusMax;
    
    
    
    
    private double timeElasped;

    private Position A;
    private Position B;
    private Position C;
    private Position D;

    // Init data
    public DatumPointData(String sarID, DateTime TLKP, DateTime CSS,
            Position LKP, double x, double y, double SF, int searchObject) {
        super(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);
    }

    public void setBox(Position A, Position B, Position C, Position D) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
    }
    
    
    /**
     * @return the rdvDirectionDownWind
     */
    public double getRdvDirectionDownWind() {
        return rdvDirectionDownWind;
    }

    /**
     * @param rdvDirectionDownWind the rdvDirectionDownWind to set
     */
    public void setRdvDirectionDownWind(double rdvDirectionDownWind) {
        this.rdvDirectionDownWind = rdvDirectionDownWind;
    }

    /**
     * @return the rdvDirectionMin
     */
    public double getRdvDirectionMin() {
        return rdvDirectionMin;
    }

    /**
     * @param rdvDirectionMin the rdvDirectionMin to set
     */
    public void setRdvDirectionMin(double rdvDirectionMin) {
        this.rdvDirectionMin = rdvDirectionMin;
    }

    /**
     * @return the rdvDirectionMax
     */
    public double getRdvDirectionMax() {
        return rdvDirectionMax;
    }

    /**
     * @param rdvDirectionMax the rdvDirectionMax to set
     */
    public void setRdvDirectionMax(double rdvDirectionMax) {
        this.rdvDirectionMax = rdvDirectionMax;
    }

    /**
     * @return the rdvDistanceDownWind
     */
    public double getRdvDistanceDownWind() {
        return rdvDistanceDownWind;
    }

    /**
     * @param rdvDistanceDownWind the rdvDistanceDownWind to set
     */
    public void setRdvDistanceDownWind(double rdvDistanceDownWind) {
        this.rdvDistanceDownWind = rdvDistanceDownWind;
    }

    /**
     * @return the rdvDistanceMin
     */
    public double getRdvDistanceMin() {
        return rdvDistanceMin;
    }

    /**
     * @param rdvDistanceMin the rdvDistanceMin to set
     */
    public void setRdvDistanceMin(double rdvDistanceMin) {
        this.rdvDistanceMin = rdvDistanceMin;
    }

    /**
     * @return the rdvDistanceMax
     */
    public double getRdvDistanceMax() {
        return rdvDistanceMax;
    }

    /**
     * @param rdvDistanceMax the rdvDistanceMax to set
     */
    public void setRdvDistanceMax(double rdvDistanceMax) {
        this.rdvDistanceMax = rdvDistanceMax;
    }

    /**
     * @return the rdvSpeedDownWind
     */
    public double getRdvSpeedDownWind() {
        return rdvSpeedDownWind;
    }

    /**
     * @param rdvSpeedDownWind the rdvSpeedDownWind to set
     */
    public void setRdvSpeedDownWind(double rdvSpeedDownWind) {
        this.rdvSpeedDownWind = rdvSpeedDownWind;
    }

    /**
     * @return the rdvSpeedMin
     */
    public double getRdvSpeedMin() {
        return rdvSpeedMin;
    }

    /**
     * @param rdvSpeedMin the rdvSpeedMin to set
     */
    public void setRdvSpeedMin(double rdvSpeedMin) {
        this.rdvSpeedMin = rdvSpeedMin;
    }

    /**
     * @return the rdvSpeedMax
     */
    public double getRdvSpeedMax() {
        return rdvSpeedMax;
    }

    /**
     * @param rdvSpeedMax the rdvSpeedMax to set
     */
    public void setRdvSpeedMax(double rdvSpeedMax) {
        this.rdvSpeedMax = rdvSpeedMax;
    }

    /**
     * @return the radiusDownWind
     */
    public double getRadiusDownWind() {
        return radiusDownWind;
    }

    /**
     * @param radiusDownWind the radiusDownWind to set
     */
    public void setRadiusDownWind(double radiusDownWind) {
        this.radiusDownWind = radiusDownWind;
    }

    /**
     * @return the radiusMin
     */
    public double getRadiusMin() {
        return radiusMin;
    }

    /**
     * @param radiusMin the radiusMin to set
     */
    public void setRadiusMin(double radiusMin) {
        this.radiusMin = radiusMin;
    }

    /**
     * @return the radiusMax
     */
    public double getRadiusMax() {
        return radiusMax;
    }

    /**
     * @param radiusMax the radiusMax to set
     */
    public void setRadiusMax(double radiusMax) {
        this.radiusMax = radiusMax;
    }

    /**
     * @return the datumDownWind
     */
    public Position getDatumDownWind() {
        return datumDownWind;
    }

    /**
     * @param datumDownWind the datumDownWind to set
     */
    public void setDatumDownWind(Position datumDownWind) {
        this.datumDownWind = datumDownWind;
    }

    /**
     * @return the datumMin
     */
    public Position getDatumMin() {
        return datumMin;
    }

    /**
     * @param datumMin the datumMin to set
     */
    public void setDatumMin(Position datumMin) {
        this.datumMin = datumMin;
    }

    /**
     * @return the datumMax
     */
    public Position getDatumMax() {
        return datumMax;
    }

    /**
     * @param datumMax the datumMax to set
     */
    public void setDatumMax(Position datumMax) {
        this.datumMax = datumMax;
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
     * @param wtc
     *            the wtc to set
     */
    public void setWtc(Position wtc) {
        this.wtc = wtc;
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
     * @return the wtc
     */
    public Position getWtc() {
        return wtc;
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

    @Override
    public String generateHTML() {

        DateTimeFormatter fmt = DateTimeFormat
                .forPattern("dd MMMM, yyyy, 'at.' HH':'mm");

        // // Generate a html sheet of rapid response calculations
        StringBuilder str = new StringBuilder();
        // String name = "How does this look";
        //
        str.append("<html>");
        str.append("<table >");
        str.append("<tr>");
        str.append("<td align=\"left\" style=\"vertical-align: top;\">");
        str.append("<h1>Search and Rescue - Datum Point</h1>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Time of Last Known Position: "
                + fmt.print(this.getLKPDate()) + "");
        str.append("<br>Last Known Position: " + this.getLKP().toString()
                + "</br>");
        str.append("<br>Commence Search Start time: "
                + fmt.print(this.getCSSDate()) + "</br>");

        str.append(this.getWeatherPoints().get(0).generateHTML());

        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Initial Position Error, X in nautical miles: "
                + this.getX() + "");
        str.append("<br>SRU Navigational Error, Y in nautical miles: "
                + this.getY() + "</br>");
        str.append("<br>Safety Factor, Fs: " + this.getSafetyFactor() + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Object: "
                + LeewayValues.getLeeWayTypes().get(this.getSearchObject())
                + "");
        str.append("<br>With value: "
                + LeewayValues.getLeeWayContent().get(this.getSearchObject())
                + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Time Elapsed: " + Formatter.formatHours(timeElasped) + "");
//        str.append("<br>Applying Leeway and TWC gives a datum of  "
//                + datum.toString() + "</br>");
//        str.append("<br>With the following Residual Drift Vector</br>");
//        str.append("<br>RDV Direction: " + rdvDirection + "°</br>");
//        str.append("<br>RDV Distance: "
//                + Formatter.formatDouble(rdvDistance, 2) + " nm</br>");
//        str.append("<br>RDV Speed: " + Formatter.formatDouble(rdvSpeed, 2)
//                + " kn/h</br>");
//        str.append("<br>With radius: " + Formatter.formatDouble(radius, 2)
//                + "nm </br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Area:");
//        str.append("<br>A: " + A.toString() + "</br>");
//        str.append("<br>B: " + B.toString() + "</br>");
//        str.append("<br>C: " + C.toString() + "</br>");
//        str.append("<br>D: " + D.toString() + "</br>");
//        str.append("<br>Total Size: "
//                + Formatter.formatDouble(radius * 2 * radius * 2, 2)
//                + " nm2</br>");

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
