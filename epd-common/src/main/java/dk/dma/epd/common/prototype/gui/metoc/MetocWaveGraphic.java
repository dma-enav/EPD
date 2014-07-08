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

import dk.dma.epd.common.prototype.settings.EnavSettings;

/**
 * Metoc wave graphic
 */
public class MetocWaveGraphic extends MetocRaster {

    private static final long serialVersionUID = 1L;
    
    String markerDir = "/images/metoc/wave/mark";

    public MetocWaveGraphic(double lat, double lon, double angle, double waveHeight, double waveWarnLimit, EnavSettings eNavSettings) {
        super();
        
        double defaultWaveLow = eNavSettings.getDefaultWaveLow();
        double defaultWaveMedium = eNavSettings.getDefaultWaveMedium();
                
        if(waveHeight >= 0 && waveHeight <= defaultWaveLow){
            markerDir += "01";
        } else if (waveHeight > defaultWaveLow && waveHeight <= defaultWaveMedium){
            markerDir += "02";
        } else if (waveHeight > defaultWaveMedium){
            markerDir += "03";
        }
        
        if(waveHeight >= waveWarnLimit){
            markerDir += "red.png";
        } else {
            markerDir += ".png";
        }
        
        addRaster(markerDir, lat, lon, angle);
    }
}
