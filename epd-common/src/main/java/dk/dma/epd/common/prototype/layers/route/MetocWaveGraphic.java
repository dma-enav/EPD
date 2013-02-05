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
