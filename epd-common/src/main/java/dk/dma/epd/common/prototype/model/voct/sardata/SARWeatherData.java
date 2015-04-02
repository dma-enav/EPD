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

import java.io.Serializable;

import org.joda.time.DateTime;

public class SARWeatherData implements Serializable {

    private static final long serialVersionUID = 1L;
    private double TWCHeading;
    private double TWCknots;

    private double LWknots;
    private double LWHeading;
    private double downWind;
    private DateTime dateTime;

    public SARWeatherData(dma.voct.SARWeatherData sarWeatherData) {
        this.TWCHeading = sarWeatherData.getTwcHeading();
        this.TWCknots = sarWeatherData.getTwcKnots();
        this.LWknots = sarWeatherData.getLeewayKnots();
        this.LWHeading = sarWeatherData.getLeewayHeading();
        this.downWind = sarWeatherData.getDownWindBearing();
        this.dateTime = new DateTime(sarWeatherData.getDate());
    }

    public SARWeatherData(double tWCHeading, double tWCknots, double lWknots,
            double lWHeading, DateTime dateTime) {
        TWCHeading = tWCHeading;
        TWCknots = tWCknots;
        LWknots = lWknots;
        LWHeading = lWHeading;

        downWind = lWHeading - 180;
        this.dateTime = dateTime;

    }

    /**
     * @return the dateTime
     */
    public DateTime getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime
     *            the dateTime to set
     */
    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
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

    public String generateHTML() {

        StringBuilder str = new StringBuilder();

        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Total Water Current: " + TWCknots + " knots with heading "
                + TWCHeading + "°");
        str.append("<br>Leeway: " + LWknots + " knots with heading "
                + LWHeading + "°</br>");

        return str.toString();
    }

    public dma.voct.SARWeatherData getDTO() {

        dma.voct.SARWeatherData weatherData = new dma.voct.SARWeatherData();

        net.maritimecloud.util.Timestamp timeStamp = dk.dma.epd.common.util.MCTypeConverter
                .getMaritimeCloudTimeStamp(dateTime);

        weatherData.setDate(timeStamp);

        weatherData.setDownWindBearing(downWind);

        weatherData.setLeewayHeading(LWHeading);

        weatherData.setLeewayKnots(LWknots);

        weatherData.setTwcHeading(TWCHeading);

        weatherData.setTwcKnots(TWCknots);

        return weatherData;
    }

}
