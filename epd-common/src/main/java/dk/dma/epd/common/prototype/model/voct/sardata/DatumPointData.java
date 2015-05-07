/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.model.voct.sardata;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.DatumPointDTO;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.MCTypeConverter;
import dma.voct.DatumPoint;

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

    /**
     * Function used for cloning data object
     * 
     * @param data
     */
    public DatumPointData(DatumPointData data, int additionalTime) {
        super(data.getSarID(), data.getLKPDate(), data.getCSSDate().plusMinutes(additionalTime), data.getLKP(), data.getX(), data.getY(), data
                .getSafetyFactor(), data.getSearchObject());
        this.wtc = data.getWtc();
        this.datumDownWind = data.getDatumDownWind();
        this.datumMin = data.getDatumMin();
        this.datumMax = data.getDatumMax();

        this.currentListDownWind = data.getCurrentListDownWind();
        this.windListDownWind = data.getWindListDownWind();

        this.currentListMax = data.getCurrentListMax();
        this.windListMax = data.getCurrentListMax();

        this.currentListMin = data.getCurrentListMin();
        this.windListMin = data.getWindListMin();

        rdvDirectionDownWind = data.getRdvDirectionDownWind();
        rdvDirectionMin = data.getRdvDirectionMin();
        rdvDirectionMax = data.getRdvDirectionMax();

        rdvSpeedDownWind = data.getRdvSpeedDownWind();
        rdvSpeedMin = data.getRdvSpeedMin();
        rdvSpeedMax = data.getRdvSpeedMax();

        radiusDownWind = data.getRadiusDownWind();
        radiusMin = data.getRadiusMin();
        radiusMax = data.getRadiusMax();

        timeElasped = data.getTimeElasped() + additionalTime;

        A = data.getA();
        B = data.getB();
        C = data.getC();
        D = data.getD();
        
        this.setWeatherPoints(data.getWeatherPoints());

    }

    // Init data
    public DatumPointData(String sarID, DateTime TLKP, DateTime CSS, Position LKP, double x, double y, double SF, int searchObject) {
        super(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);
    }

    public DatumPointData(DatumPoint datumPoint) {
        super(datumPoint.getSarID(), new DateTime(datumPoint.getLKPDate().getTime()), new DateTime(datumPoint.getCSSDate().getTime()), Position.create(datumPoint.getLkp()
                .getLatitude(), datumPoint.getLkp().getLongitude()), datumPoint.getX(), datumPoint.getY(), datumPoint.getSafetyFactor(), datumPoint
                .getSearchObject());

        this.datumDownWind = Position.create(datumPoint.getDatumDownWind().getLatitude(), datumPoint.getDatumDownWind().getLongitude());
        this.datumMax = Position.create(datumPoint.getDatumMax().getLatitude(), datumPoint.getDatumMax().getLongitude());
        this.datumMin = Position.create(datumPoint.getDatumMin().getLatitude(), datumPoint.getDatumMin().getLongitude());

        this.radiusDownWind = datumPoint.getRadiusDownWind();
        this.radiusMin = datumPoint.getRadiusMin();
        this.radiusMax = datumPoint.getRadiusMax();

        this.timeElasped = datumPoint.getTimeElapsed();

        this.rdvDirectionDownWind = datumPoint.getRdvDirectionDownWind();
        this.rdvDirectionMax = datumPoint.getRdvDirectionMax();
        this.rdvDirectionMin = datumPoint.getRdvDirectionMin();

        this.rdvDistanceDownWind = datumPoint.getRdvDistanceDownWind();
        this.rdvDistanceMax = datumPoint.getRdvDistanceMax();
        this.rdvDistanceMin = datumPoint.getRdvDistanceMin();

        this.rdvSpeedDownWind = datumPoint.getRadiusDownWind();
        this.rdvSpeedMax = datumPoint.getRadiusMax();
        this.rdvSpeedMin = datumPoint.getRadiusMin();

        this.A = Position.create(datumPoint.getA().getLatitude(), datumPoint.getA().getLongitude());
        this.B = Position.create(datumPoint.getB().getLatitude(), datumPoint.getB().getLongitude());
        this.C = Position.create(datumPoint.getC().getLatitude(), datumPoint.getC().getLongitude());
        this.D = Position.create(datumPoint.getD().getLatitude(), datumPoint.getD().getLongitude());

        currentListDownWind = new ArrayList<Position>();
        currentListMax = new ArrayList<Position>();
        currentListMin = new ArrayList<Position>();

        windListDownWind = new ArrayList<Position>();
        windListMax = new ArrayList<Position>();
        windListMin = new ArrayList<Position>();

        for (int i = 0; i < datumPoint.getCurrentListDownWind().size(); i++) {
            currentListDownWind.add(Position.create(datumPoint.getCurrentListDownWind().get(i).getLatitude(), datumPoint
                    .getCurrentListDownWind().get(i).getLongitude()));
        }

        for (int i = 0; i < datumPoint.getCurrentListMax().size(); i++) {
            currentListMax.add(Position.create(datumPoint.getCurrentListMax().get(i).getLatitude(), datumPoint.getCurrentListMax().get(i)
                    .getLongitude()));
        }

        for (int i = 0; i < datumPoint.getCurrentListMin().size(); i++) {
            currentListMin.add(Position.create(datumPoint.getCurrentListMin().get(i).getLatitude(), datumPoint.getCurrentListMin().get(i)
                    .getLongitude()));
        }

        for (int i = 0; i < datumPoint.getWindListDownWind().size(); i++) {
            windListDownWind.add(Position.create(datumPoint.getWindListDownWind().get(i).getLatitude(), datumPoint.getWindListDownWind().get(i)
                    .getLongitude()));
        }

        for (int i = 0; i < datumPoint.getWindListMax().size(); i++) {
            windListMax.add(Position
                    .create(datumPoint.getWindListMax().get(i).getLatitude(), datumPoint.getWindListMax().get(i).getLongitude()));
        }

        for (int i = 0; i < datumPoint.getWindListMin().size(); i++) {
            windListMin.add(Position
                    .create(datumPoint.getWindListMin().get(i).getLatitude(), datumPoint.getWindListMin().get(i).getLongitude()));
        }

        List<SARWeatherData> weatherPoints = new ArrayList<SARWeatherData>();

        for (int i = 0; i < datumPoint.getWeatherData().size(); i++) {
            weatherPoints.add(new SARWeatherData(datumPoint.getWeatherData().get(i)));
        }

        this.setWeatherPoints(weatherPoints);

        this.wtc = currentListDownWind.get(currentListDownWind.size() - 1);

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

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM, yyyy, 'at.' HH':'mm");

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
        str.append("Time of Last Known Position: " + fmt.print(this.getLKPDate()) + "");
        str.append("<br>Last Known Position: " + this.getLKP().toString() + "</br>");
        str.append("<br>Commence Search Start time: " + fmt.print(this.getCSSDate()) + "</br>");

        str.append(this.getWeatherPoints().get(0).generateHTML());

        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Initial Position Error, X in nautical miles: " + this.getX() + "");
        str.append("<br>SRU Navigational Error, Y in nautical miles: " + this.getY() + "</br>");
        str.append("<br>Safety Factor, Fs: " + this.getSafetyFactor() + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Object: " + LeewayValues.getLeeWayTypes().get(this.getSearchObject()) + "");
        str.append("<br>With value: " + LeewayValues.getLeeWayContent().get(this.getSearchObject()) + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Time Elapsed: " + Formatter.formatHours(timeElasped) + "");

        str.append("<br>Applying Leeway and TWC gives a datum downwind of  " + datumDownWind.toString() + "</br>");
        str.append("<br>With the following Downwind Residual Drift Vector</br>");
        str.append("<br>RDV Downwind Direction: " + rdvDirectionDownWind + "°</br>");
        str.append("<br>RDV Downwind Distance: " + Formatter.formatDouble(rdvDistanceDownWind, 2) + " nm</br>");
        str.append("<br>RDV Speed: " + Formatter.formatDouble(rdvSpeedDownWind, 2) + " kn/h</br>");
        str.append("<br>With radius: " + Formatter.formatDouble(radiusDownWind, 2) + "nm </br>");

        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Area:");

        str.append("<br>A: " + A.toString() + "</br>");
        str.append("<br>B: " + B.toString() + "</br>");
        str.append("<br>C: " + C.toString() + "</br>");
        str.append("<br>D: " + D.toString() + "</br>");

        double width = Converter.metersToNm(getA().distanceTo(getD(), CoordinateSystem.CARTESIAN));
        double height = Converter.metersToNm(getB().distanceTo(getC(), CoordinateSystem.CARTESIAN));

        str.append("<br>Total Size: " + Formatter.formatDouble(width * height, 2) + " nm2</br>");

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

    public DatumPoint getModelData() {
        
        DatumPoint datumPointData = new DatumPoint();
        
        for (int i = 0; i < getWeatherPoints().size(); i++) {
            SARWeatherData weatherPoint = getWeatherPoints().get(i);
            datumPointData.addWeatherData(weatherPoint.getDTO());
        }

        
        for (int i = 0; i < windListDownWind.size(); i++) {
            datumPointData.addWindListDownWind(MCTypeConverter
                    .getMaritimeCloudPositin(windListDownWind.get(i)));
        }
        
        for (int i = 0; i < windListMax.size(); i++) {
            datumPointData.addWindListMax(MCTypeConverter
                    .getMaritimeCloudPositin(windListMax.get(i)));
        }
        
        for (int i = 0; i < windListMin.size(); i++) {
            datumPointData.addWindListDownWind(MCTypeConverter
                    .getMaritimeCloudPositin(windListMin.get(i)));
        }
        
        

        for (int i = 0; i < currentListDownWind.size(); i++) {
            datumPointData.addCurrentListDownWind(MCTypeConverter
                    .getMaritimeCloudPositin(currentListDownWind.get(i)));

        }
        
        for (int i = 0; i < currentListMax.size(); i++) {
            datumPointData.addCurrentListMax(MCTypeConverter
                    .getMaritimeCloudPositin(currentListMax.get(i)));

        }
        
        for (int i = 0; i < currentListMin.size(); i++) {
            datumPointData.addCurrentListMin(MCTypeConverter
                    .getMaritimeCloudPositin(currentListMin.get(i)));

        }


        net.maritimecloud.util.geometry.Position cSPPos = null;

        if (getCSP() != null) {
            cSPPos = MCTypeConverter.getMaritimeCloudPositin(getCSP());
            datumPointData.setC(cSPPos);

        }
        

        
        datumPointData.setA(MCTypeConverter.getMaritimeCloudPositin(A));
        datumPointData.setB(MCTypeConverter.getMaritimeCloudPositin(B));
        datumPointData.setC(MCTypeConverter.getMaritimeCloudPositin(C));
        datumPointData.setD(MCTypeConverter.getMaritimeCloudPositin(D));
        
        datumPointData.setCSSDate(MCTypeConverter
                .getMaritimeCloudTimeStamp(getCSSDate()));

        datumPointData.setLKPDate(MCTypeConverter
                .getMaritimeCloudTimeStamp(getLKPDate()));
        
        datumPointData.setDatumDownWind(MCTypeConverter
                .getMaritimeCloudPositin(datumDownWind));
        
        datumPointData.setDatumMin(MCTypeConverter
                .getMaritimeCloudPositin(datumMin));
        
        datumPointData.setDatumMax(MCTypeConverter
                .getMaritimeCloudPositin(datumMax));
        
        
        datumPointData.setSafetyFactor(getSafetyFactor());
        datumPointData.setSarID(getSarID());
        datumPointData.setSearchObject(getSearchObject());
        datumPointData.setTimeElapsed(getTimeElasped());
        datumPointData.setX(getX());
        datumPointData.setY(getY());
        
        datumPointData.setCSSDate(MCTypeConverter
                .getMaritimeCloudTimeStamp(getCSSDate()));
        
        datumPointData.setLKPDate(MCTypeConverter
                .getMaritimeCloudTimeStamp(getLKPDate()));
        
        datumPointData.setSarID(getSarID());
        datumPointData.setLkp(MCTypeConverter.getMaritimeCloudPositin(this.getLKP()));
        

        datumPointData.setRadiusDownWind(radiusDownWind);
        datumPointData.setRadiusMax(radiusMax);
        datumPointData.setRadiusMin(radiusMin);
        
        datumPointData.setRdvDirectionDownWind(rdvDirectionDownWind);
        datumPointData.setRdvDirectionMax(rdvDirectionMax);
        datumPointData.setRdvDirectionMin(rdvDirectionMin);
        
        
        datumPointData.setRdvDistanceDownWind(rdvDistanceDownWind);
        datumPointData.setRdvDistanceMax(rdvDistanceMax);
        datumPointData.setRdvDistanceMin(rdvDistanceMin);
        
        datumPointData.setRdvSpeedDownWind(rdvSpeedDownWind);
        datumPointData.setRdvSpeedMax(rdvSpeedMax);
        datumPointData.setRdvSpeedMin(rdvSpeedMin);
        
//        rapidResponseData.setRdvSpeed(rdvSpeed);
//        rapidResponseData.setRdvSpeedLast(rdvSpeedLast);


        // double radiusDownWind,
        // double radiusMax, double radiusMin, double timeElasped,

        // double rdvDirectionDownWind, double rdvDirectionMax,
        // double rdvDirectionMin, double rdvDistanceDownWind,
        // double rdvDistanceMax, double rdvDistanceMin,

        // double rdvSpeedDownWind, double rdvSpeedMax, double rdvSpeedMin,
        // PositionDTO a, PositionDTO b, PositionDTO c, PositionDTO d) {

//        return new DatumPointDTO(getSarID(), this.getLKPDate().toDate(), this.getCSSDate().toDate(), this.getLKP().getDTO(),
//                cSPPos, this.getX(), this.getY(), this.getSafetyFactor(), this.getSearchObject(), weatherList,
//                currentListDTODownWind, currentListDTOMax, currentListDTOMin,
//
//                windListDTODownWind, windListDTOMax, windListDTOMin, datumDownWind.getDTO(), datumMax.getDTO(), datumMin.getDTO(),
//                radiusDownWind, radiusMax, radiusMin, timeElasped, rdvDirectionDownWind, rdvDirectionMax, rdvDirectionMin,
//                rdvDistanceDownWind, rdvDistanceMax, rdvDistanceMin, rdvSpeedDownWind, rdvSpeedMax, rdvSpeedMin, A.getDTO(),
//                B.getDTO(), C.getDTO(), D.getDTO());
        
        return datumPointData;
    }
}
