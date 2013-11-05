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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dk.dma.enav.model.dto.PositionDTO;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.RapidResponseDTO;
import dk.dma.enav.model.voct.WeatherDataDTO;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.text.Formatter;

public class RapidResponseData extends SARData {

    private List<Position> currentList;
    private List<Position> windList;

    private Position datum;
    // private Position wtc;

    private double radius;

    private double timeElasped;

    private double rdvDirection;
    private double rdvDistance;
    private double rdvSpeed;

    private double rdvDirectionLast;
    private double rdvSpeedLast;

    private Position A;
    private Position B;
    private Position C;
    private Position D;

    // Init data
    public RapidResponseData(String sarID, DateTime TLKP, DateTime CSS,
            Position LKP, double x, double y, double SF, int searchObject) {

        super(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);
    }

    public RapidResponseData(RapidResponseDTO data) {
        // super(data.getSarID(), new DateTime(data.getLKPDate()), new
        // DateTime(data.getCSSDate()), LKP, x, y, safetyFactor, searchObject);
        super(data.getSarID(), new DateTime(data.getLKPDate()), new DateTime(
                data.getCSSDate()), Position.create(
                data.getLKP().getLatitude(), data.getLKP().getLongitude()),
                data.getX(), data.getY(), data.getSafetyFactor(), data
                        .getSearchObject());

        this.datum = Position.create(data.getDatum().getLatitude(), data.getDatum().getLongitude());        
        this.radius = data.getRadius();
        this.timeElasped = data.getTimeElasped();
        this.rdvDirection = data.getRdvDirection();
        this.rdvDistance = data.getRdvDistance();
        this.rdvSpeed = data.getRdvSpeed();
        this.rdvDirectionLast = data.getRdvSpeedLast();
        this.A = Position.create(data.getA().getLatitude(), data.getA().getLongitude());
        this.B = Position.create(data.getB().getLatitude(), data.getB().getLongitude());
        this.C = Position.create(data.getC().getLatitude(), data.getC().getLongitude());
        this.D = Position.create(data.getD().getLatitude(), data.getD().getLongitude());
        
        
        
        currentList = new ArrayList<Position>();
        windList = new ArrayList<Position>();
        
        for (int i = 0; i < data.getCurrentList().size(); i++) {
            currentList.add(Position.create(data.getCurrentList().get(i).getLatitude(), data.getCurrentList().get(i).getLongitude()));
        }
        
        for (int i = 0; i < data.getWindList().size(); i++) {
            windList.add(Position.create(data.getWindList().get(i).getLatitude(), data.getWindList().get(i).getLongitude()));
        }
        
        
        List<SARWeatherData> weatherPoints = new ArrayList<SARWeatherData>();
        
        for (int i = 0; i < data.getWeatherData().size(); i++) {
            weatherPoints.add(new SARWeatherData(data.getWeatherData().get(i)));
        }
        
        this.setWeatherPoints(weatherPoints);
    }

    public void setBox(Position A, Position B, Position C, Position D) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
    }

    /**
     * @return the rdvDirectionLast
     */
    public double getRdvDirectionLast() {
        return rdvDirectionLast;
    }

    /**
     * @param rdvDirectionLast
     *            the rdvDirectionLast to set
     */
    public void setRdvDirectionLast(double rdvDirectionLast) {
        this.rdvDirectionLast = rdvDirectionLast;
    }

    /**
     * @return the rdvSpeedLast
     */
    public double getRdvSpeedLast() {
        return rdvSpeedLast;
    }

    /**
     * @param rdvSpeedLast
     *            the rdvSpeedLast to set
     */
    public void setRdvSpeedLast(double rdvSpeedLast) {
        this.rdvSpeedLast = rdvSpeedLast;
    }

    /**
     * @return the currentList
     */
    public List<Position> getCurrentList() {
        return currentList;
    }

    /**
     * @param currentList
     *            the currentList to set
     */
    public void setCurrentList(List<Position> currentList) {
        this.currentList = currentList;
    }

    /**
     * @return the windList
     */
    public List<Position> getWindList() {
        return windList;
    }

    /**
     * @param windList
     *            the windList to set
     */
    public void setWindList(List<Position> windList) {
        this.windList = windList;
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
     * @param datum
     *            the datum to set
     */
    public void setDatum(Position datum) {
        this.datum = datum;
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
     * @return the datum
     */
    public Position getDatum() {
        return datum;
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
        str.append("<h1>Search and Rescue - Rapid Response</h1>");
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
        str.append("<br>Applying Leeway and TWC gives a datum of  "
                + datum.toString() + "</br>");
        str.append("<br>With the following Residual Drift Vector</br>");
        str.append("<br>RDV Direction: " + rdvDirection + "°</br>");
        str.append("<br>RDV Distance: "
                + Formatter.formatDouble(rdvDistance, 2) + " nm</br>");
        str.append("<br>RDV Speed: " + Formatter.formatDouble(rdvSpeed, 2)
                + " kn/h</br>");
        str.append("<br>With radius: " + Formatter.formatDouble(radius, 2)
                + "nm </br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Area:");
        str.append("<br>A: " + A.toString() + "</br>");
        str.append("<br>B: " + B.toString() + "</br>");
        str.append("<br>C: " + C.toString() + "</br>");
        str.append("<br>D: " + D.toString() + "</br>");
        str.append("<br>Total Size: "
                + Formatter.formatDouble(radius * 2 * radius * 2, 2)
                + " nm2</br>");

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

    public RapidResponseDTO getModelData() {

        List<PositionDTO> windListDTO = new ArrayList<PositionDTO>();

        for (int i = 0; i < windList.size(); i++) {
            windListDTO.add(windList.get(i).getDTO());
        }

        List<PositionDTO> currentListDTO = new ArrayList<PositionDTO>();

        for (int i = 0; i < currentList.size(); i++) {
            currentListDTO.add(currentList.get(i).getDTO());
        }
        
        PositionDTO cSPPos = null;
        
        if (getCSP() != null){
            cSPPos = getCSP()            .getDTO();
        }
        

        List<WeatherDataDTO> weatherList = new ArrayList<WeatherDataDTO>();
        
        for (int i = 0; i < getWeatherPoints().size(); i++) {
            weatherList.add(getWeatherPoints().get(i).getDTO());
        }
        
        return new RapidResponseDTO(getSarID(), this.getLKPDate().toDate(), this
                .getCSSDate().toDate(), this.getLKP().getDTO(), 
                cSPPos
                , this.getX(), this.getY(), this.getSafetyFactor(),
                this.getSearchObject(), currentListDTO, windListDTO,
                datum.getDTO(), radius, timeElasped, rdvDirection, rdvDistance,
                rdvSpeed, rdvDirectionLast, rdvSpeedLast, A.getDTO(),
                B.getDTO(), C.getDTO(), D.getDTO(), weatherList);
    }

}
