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
package dk.dma.epd.common.prototype.gui.metoc;




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
        }else if (windSpeedKnots > 100){
            markerDir += "105";
        }

        if(windSpeed >= windWarnLimit){
            markerDir += "red.png";
        } else {
            markerDir += ".png";
        }
        
//        System.out.println("Marker dir: " + markerDir + " windspeedknots is " + windSpeedKnots);
        addRaster(markerDir, lat, lon, angle);
    }
}
