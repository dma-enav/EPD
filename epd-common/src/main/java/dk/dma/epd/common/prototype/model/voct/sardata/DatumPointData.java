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
import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.DatumPointDTO;
import dk.dma.enav.model.voct.WeatherDataDTO;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;

public class DatumPointData extends SARData {

    private static final long serialVersionUID = 1L;

    private Position wtc;

    private Position datumDownWind;
    private Position datumMin;
    private Position datumMax;

    private List<Position> currentListDownWind;
    private List<Position> windListDownWind;

    private List<Position> currentListMax;
    private List<Position> windListMax;

    private List<Position> currentListMin;
    private List<Position> windListMin;

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

    public DatumPointData(DatumPointDTO data) {
        super(data.getSarID(), new DateTime(data.getLKPDate()), new DateTime(
                data.getCSSDate()), Position.create(
                data.getLKP().getLatitude(), data.getLKP().getLongitude()),
                data.getX(), data.getY(), data.getSafetyFactor(), data
                        .getSearchObject());

        this.datumDownWind = Position.create(data.getDatumDownWind()
                .getLatitude(), data.getDatumDownWind().getLongitude());
        this.datumMax = Position.create(data.getDatumMax().getLatitude(), data
                .getDatumMax().getLongitude());
        this.datumMin = Position.create(data.getDatumMin().getLatitude(), data
                .getDatumMin().getLongitude());

        this.radiusDownWind = data.getRadiusDownWind();
        this.radiusMin = data.getRadiusMin();
        this.radiusMax = data.getRadiusMax();

        this.timeElasped = data.getTimeElasped();

        this.rdvDirectionDownWind = data.getRdvDirectionDownWind();
        this.rdvDirectionMax = data.getRdvDirectionMax();
        this.rdvDirectionMin = data.getRdvDirectionMin();

        this.rdvDistanceDownWind = data.getRdvDistanceDownWind();
        this.rdvDistanceMax = data.getRdvDistanceMax();
        this.rdvDistanceMin = data.getRdvDistanceMin();

        this.rdvSpeedDownWind = data.getRadiusDownWind();
        this.rdvSpeedMax = data.getRadiusMax();
        this.rdvSpeedMin = data.getRadiusMin();
        
        this.A = Position.create(data.getA().getLatitude(), data.getA()
                .getLongitude());
        this.B = Position.create(data.getB().getLatitude(), data.getB()
                .getLongitude());
        this.C = Position.create(data.getC().getLatitude(), data.getC()
                .getLongitude());
        this.D = Position.create(data.getD().getLatitude(), data.getD()
                .getLongitude());

        currentListDownWind = new ArrayList<Position>();
        currentListMax = new ArrayList<Position>();
        currentListMin = new ArrayList<Position>();

        windListDownWind = new ArrayList<Position>();
        windListMax = new ArrayList<Position>();
        windListMin = new ArrayList<Position>();

        for (int i = 0; i < data.getCurrentListDownWind().size(); i++) {
            currentListDownWind.add(Position.create(data
                    .getCurrentListDownWind().get(i).getLatitude(), data
                    .getCurrentListDownWind().get(i).getLongitude()));
        }

        for (int i = 0; i < data.getCurrentListMax().size(); i++) {
            currentListMax.add(Position.create(data.getCurrentListMax().get(i)
                    .getLatitude(), data.getCurrentListMax().get(i)
                    .getLongitude()));
        }

        for (int i = 0; i < data.getCurrentListMin().size(); i++) {
            currentListMin.add(Position.create(data.getCurrentListMin().get(i)
                    .getLatitude(), data.getCurrentListMin().get(i)
                    .getLongitude()));
        }

        for (int i = 0; i < data.getWindListDownWind().size(); i++) {
            windListDownWind.add(Position.create(data.getWindListDownWind()
                    .get(i).getLatitude(), data.getWindListDownWind().get(i)
                    .getLongitude()));
        }

        for (int i = 0; i < data.getWindListMax().size(); i++) {
            windListMax.add(Position
                    .create(data.getWindListMax().get(i).getLatitude(), data
                            .getWindListMax().get(i).getLongitude()));
        }

        for (int i = 0; i < data.getWindListMin().size(); i++) {
            windListMin.add(Position
                    .create(data.getWindListMin().get(i).getLatitude(), data
                            .getWindListMin().get(i).getLongitude()));
        }

        List<SARWeatherData> weatherPoints = new ArrayList<SARWeatherData>();

        for (int i = 0; i < data.getWeatherData().size(); i++) {
            weatherPoints.add(new SARWeatherData(data.getWeatherData().get(i)));
        }

        this.setWeatherPoints(weatherPoints);

        

        this.wtc = currentListDownWind.get(currentListDownWind.size()-1);
        
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
     * @param rdvDirectionDownWind
     *            the rdvDirectionDownWind to set
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
     * @param rdvDirectionMin
     *            the rdvDirectionMin to set
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
     * @param rdvDirectionMax
     *            the rdvDirectionMax to set
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
     * @param rdvDistanceDownWind
     *            the rdvDistanceDownWind to set
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
     * @param rdvDistanceMin
     *            the rdvDistanceMin to set
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
     * @param rdvDistanceMax
     *            the rdvDistanceMax to set
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
     * @param rdvSpeedDownWind
     *            the rdvSpeedDownWind to set
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
     * @param rdvSpeedMin
     *            the rdvSpeedMin to set
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
     * @param rdvSpeedMax
     *            the rdvSpeedMax to set
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
     * @param radiusDownWind
     *            the radiusDownWind to set
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
     * @param radiusMin
     *            the radiusMin to set
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
     * @param radiusMax
     *            the radiusMax to set
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
     * @param datumDownWind
     *            the datumDownWind to set
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
     * @param datumMin
     *            the datumMin to set
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
     * @param datumMax
     *            the datumMax to set
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

    /**
     * @return the currentListDownWind
     */
    public List<Position> getCurrentListDownWind() {
        return currentListDownWind;
    }

    /**
     * @param currentListDownWind
     *            the currentListDownWind to set
     */
    public void setCurrentListDownWind(List<Position> currentListDownWind) {
        this.currentListDownWind = currentListDownWind;
    }

    /**
     * @return the windListDownWind
     */
    public List<Position> getWindListDownWind() {
        return windListDownWind;
    }

    /**
     * @param windListDownWind
     *            the windListDownWind to set
     */
    public void setWindListDownWind(List<Position> windListDownWind) {
        this.windListDownWind = windListDownWind;
    }

    /**
     * @return the currentListMax
     */
    public List<Position> getCurrentListMax() {
        return currentListMax;
    }

    /**
     * @param currentListMax
     *            the currentListMax to set
     */
    public void setCurrentListMax(List<Position> currentListMax) {
        this.currentListMax = currentListMax;
    }

    /**
     * @return the windListMax
     */
    public List<Position> getWindListMax() {
        return windListMax;
    }

    /**
     * @param windListMax
     *            the windListMax to set
     */
    public void setWindListMax(List<Position> windListMax) {
        this.windListMax = windListMax;
    }

    /**
     * @return the currentListMin
     */
    public List<Position> getCurrentListMin() {
        return currentListMin;
    }

    /**
     * @param currentListMin
     *            the currentListMin to set
     */
    public void setCurrentListMin(List<Position> currentListMin) {
        this.currentListMin = currentListMin;
    }

    /**
     * @return the windListMin
     */
    public List<Position> getWindListMin() {
        return windListMin;
    }

    /**
     * @param windListMin
     *            the windListMin to set
     */
    public void setWindListMin(List<Position> windListMin) {
        this.windListMin = windListMin;
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

        str.append("<br>Applying Leeway and TWC gives a datum downwind of  "
                + datumDownWind.toString() + "</br>");
        str.append("<br>With the following Downwind Residual Drift Vector</br>");
        str.append("<br>RDV Downwind Direction: " + rdvDirectionDownWind
                + "°</br>");
        str.append("<br>RDV Downwind Distance: "
                + Formatter.formatDouble(rdvDistanceDownWind, 2) + " nm</br>");
        str.append("<br>RDV Speed: "
                + Formatter.formatDouble(rdvSpeedDownWind, 2) + " kn/h</br>");
        str.append("<br>With radius: "
                + Formatter.formatDouble(radiusDownWind, 2) + "nm </br>");

        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Area:");

        str.append("<br>A: " + A.toString() + "</br>");
        str.append("<br>B: " + B.toString() + "</br>");
        str.append("<br>C: " + C.toString() + "</br>");
        str.append("<br>D: " + D.toString() + "</br>");

        double width = Converter.metersToNm(getA().distanceTo(getD(),
                CoordinateSystem.CARTESIAN));
        double height = Converter.metersToNm(getB().distanceTo(getC(),
                CoordinateSystem.CARTESIAN));

        str.append("<br>Total Size: "
                + Formatter.formatDouble(width * height, 2) + " nm2</br>");

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

    public DatumPointDTO getModelData() {

        List<PositionDTO> windListDTODownWind = new ArrayList<PositionDTO>();
        List<PositionDTO> windListDTOMax = new ArrayList<PositionDTO>();
        List<PositionDTO> windListDTOMin = new ArrayList<PositionDTO>();

        for (int i = 0; i < windListDownWind.size(); i++) {
            windListDTODownWind.add(windListDownWind.get(i).getDTO());
        }

        for (int i = 0; i < windListMax.size(); i++) {
            windListDTOMax.add(windListMax.get(i).getDTO());
        }

        for (int i = 0; i < windListMin.size(); i++) {
            windListDTOMin.add(windListMin.get(i).getDTO());
        }

        List<PositionDTO> currentListDTODownWind = new ArrayList<PositionDTO>();
        List<PositionDTO> currentListDTOMax = new ArrayList<PositionDTO>();
        List<PositionDTO> currentListDTOMin = new ArrayList<PositionDTO>();

        for (int i = 0; i < currentListDownWind.size(); i++) {
            currentListDTODownWind.add(currentListDownWind.get(i).getDTO());
        }

        for (int i = 0; i < currentListMax.size(); i++) {
            currentListDTOMax.add(currentListMax.get(i).getDTO());
        }

        for (int i = 0; i < currentListMin.size(); i++) {
            currentListDTOMin.add(currentListMax.get(i).getDTO());
        }

        PositionDTO cSPPos = null;

        if (getCSP() != null) {
            cSPPos = getCSP().getDTO();
        }

        List<WeatherDataDTO> weatherList = new ArrayList<WeatherDataDTO>();

        for (int i = 0; i < getWeatherPoints().size(); i++) {
            weatherList.add(getWeatherPoints().get(i).getDTO());
        }

        // String sarID, Date lKPDate, Date cSSDate,
        // PositionDTO lKP, PositionDTO cSP, double x, double y,
        // double safetyFactor, int searchObject,
        // List<WeatherDataDTO> weatherData,
        // List<PositionDTO> currentListDownWind,
        // List<PositionDTO> currentListMax, List<PositionDTO> currentListMin,

        // List<PositionDTO> windListDownWind, List<PositionDTO> windListMax,
        // List<PositionDTO> windListMin,

        // PositionDTO datumDownWind,
        // PositionDTO datumMax, PositionDTO datumMin,
        // double radiusDownWind,
        // double radiusMax, double radiusMin, double timeElasped,

        // double rdvDirectionDownWind, double rdvDirectionMax,
        // double rdvDirectionMin, double rdvDistanceDownWind,
        // double rdvDistanceMax, double rdvDistanceMin,

        // double rdvSpeedDownWind, double rdvSpeedMax, double rdvSpeedMin,
        // PositionDTO a, PositionDTO b, PositionDTO c, PositionDTO d) {

        return new DatumPointDTO(getSarID(), this.getLKPDate().toDate(), this
                .getCSSDate().toDate(), this.getLKP().getDTO(), cSPPos,
                this.getX(), this.getY(), this.getSafetyFactor(),
                this.getSearchObject(), weatherList, currentListDTODownWind,
                currentListDTOMax, currentListDTOMin,

                windListDTODownWind, windListDTOMax, windListDTOMin,
                datumDownWind.getDTO(), datumMax.getDTO(), datumMin.getDTO(),
                radiusDownWind, radiusMax, radiusMin, timeElasped,
                rdvDirectionDownWind, rdvDirectionMax, rdvDirectionMin,
                rdvDistanceDownWind, rdvDistanceMax, rdvDistanceMin,
                rdvSpeedDownWind, rdvSpeedMax, rdvSpeedMin, A.getDTO(),
                B.getDTO(), C.getDTO(), D.getDTO());
    }
}
