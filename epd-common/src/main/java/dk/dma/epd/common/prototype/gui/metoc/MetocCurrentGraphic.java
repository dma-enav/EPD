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

import dk.dma.epd.common.prototype.settings.layers.MetocLayerCommonSettings;

/**
 * Metoc current graphic
 */
public class MetocCurrentGraphic extends MetocRaster {

    private static final long serialVersionUID = 1L;
    
    String markerDir = "/images/metoc/current/mark";
    
    
    
    public MetocCurrentGraphic(double lat, double lon, double angle, double currentSpeedMs, double currentWarnLimit, MetocLayerCommonSettings<?> settings) {
        super();
        
        double defaultCurrentLow = settings.getDefaultCurrentLow();
        double defaultCurrentMedium = settings.getDefaultCurrentMedium();
        
        double currentSpeedKn = currentSpeedMs * (3.6d/1.852d);
        
        if(currentSpeedKn >= 0 && currentSpeedKn <= defaultCurrentLow){
            markerDir += "01";
        } else if (currentSpeedKn > 1 && currentSpeedKn <= defaultCurrentMedium){
            markerDir += "02";
        } else if (currentSpeedKn > defaultCurrentMedium){
            markerDir += "03";
        }
        
        if(currentSpeedKn >= currentWarnLimit){
            markerDir += "red.png";
        } else {
            markerDir += ".png";
        }
        
        addRaster(markerDir, lat, lon, angle);
    }
}
