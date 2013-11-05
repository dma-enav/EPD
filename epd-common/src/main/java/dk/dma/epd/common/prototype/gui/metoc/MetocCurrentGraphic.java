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
package dk.dma.epd.common.prototype.gui.metoc;

import dk.dma.epd.common.prototype.settings.EnavSettings;

/**
 * Metoc current graphic
 */
public class MetocCurrentGraphic extends MetocRaster {

    private static final long serialVersionUID = 1L;
    
    String markerDir = "/images/metoc/current/mark";
    
    
    
    public MetocCurrentGraphic(double lat, double lon, double angle, double currentSpeedMs, double currentWarnLimit, EnavSettings eNavSettings) {
        super();
        
        double defaultCurrentLow = eNavSettings.getDefaultCurrentLow();
        double defaultCurrentMedium = eNavSettings.getDefaultCurrentMedium();
        
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
