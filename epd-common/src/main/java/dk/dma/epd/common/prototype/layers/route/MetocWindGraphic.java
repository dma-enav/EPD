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
package dk.dma.epd.common.prototype.layers.route;



/**
 * Graphic for showing wind  
 */
public class MetocWindGraphic extends MetocRaster {

    private static final long serialVersionUID = 1L;
    
    String markerDir = "/images/metoc/wind/mark";
    
    public MetocWindGraphic(double lat, double lon, double angle, double windSpeed, double windWarnLimit) {
        super();
        
        double windSpeedKnots = windSpeed * (3.6d/1.852d);

        if(windSpeedKnots >= 0 && windSpeedKnots <= 5){
            markerDir += "005";
        } else if (windSpeedKnots > 5 && windSpeedKnots <= 10){
            markerDir += "010";
        } else if (windSpeedKnots > 10 && windSpeedKnots <= 15){
            markerDir += "015";
        } else if (windSpeedKnots > 15 && windSpeedKnots <= 20){
            markerDir += "020";
        } else if (windSpeedKnots > 20 && windSpeedKnots <= 25){
            markerDir += "025";
        } else if (windSpeedKnots > 25 && windSpeedKnots <= 30){
            markerDir += "030";
        } else if (windSpeedKnots > 30 && windSpeedKnots <= 35){
            markerDir += "035";
        } else if (windSpeedKnots > 35 && windSpeedKnots <= 40){
            markerDir += "040";
        } else if (windSpeedKnots > 40 && windSpeedKnots <= 45){
            markerDir += "045";
        } else if (windSpeedKnots > 45 && windSpeedKnots <= 50){
            markerDir += "050";
        } else if (windSpeedKnots > 50 && windSpeedKnots <= 55){
            markerDir += "055";
        } else if (windSpeedKnots > 55 && windSpeedKnots <= 60){
            markerDir += "060";
        } else if (windSpeedKnots > 60 && windSpeedKnots <= 65){
            markerDir += "065";
        } else if (windSpeedKnots > 65 && windSpeedKnots <= 70){
            markerDir += "070";
        } else if (windSpeedKnots > 70 && windSpeedKnots <= 75){
            markerDir += "075";
        } else if (windSpeedKnots > 75 && windSpeedKnots <= 80){
            markerDir += "080";
        } else if (windSpeedKnots > 80 && windSpeedKnots <= 85){
            markerDir += "085";
        } else if (windSpeedKnots > 85 && windSpeedKnots <= 90){
            markerDir += "090";
        } else if (windSpeedKnots > 90 && windSpeedKnots <= 95){
            markerDir += "095";
        } else if (windSpeedKnots > 95 && windSpeedKnots <= 100){
            markerDir += "100";
        } else if (windSpeedKnots > 100 && windSpeedKnots <= 105){
            markerDir += "105";
        }

        if(windSpeed >= windWarnLimit){
            markerDir += "red.png";
        } else {
            markerDir += ".png";
        }
        
        addRaster(markerDir, lat, lon, angle);
    }
}
