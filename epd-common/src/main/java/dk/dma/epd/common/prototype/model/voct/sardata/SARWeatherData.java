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

public class SARWeatherData {

    private double TWCHeading;
    private double TWCknots;

    private double LWknots;
    private double LWHeading;
    private double downWind;

    public SARWeatherData(double tWCHeading, double tWCknots, double lWknots,
            double lWHeading) {
        TWCHeading = tWCHeading;
        TWCknots = tWCknots;
        LWknots = lWknots;
        LWHeading = lWHeading;

        downWind = lWHeading - 180;

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

    
    public String generateHTML(){
        
        StringBuilder str = new StringBuilder();
        
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Total Water Current: " + TWCknots + " knots with heading "
                + TWCHeading + "°");
        str.append("<br>Leeway: " + LWknots + " knots with heading "
                + LWHeading + "°</br>");
        
        return str.toString();
    }
    
}
