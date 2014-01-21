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
package dk.dma.epd.shore.settings;

import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.EnavSettings;

/**
 * Specific e-Navigation settings
 * <p>
 * Extends the {@linkplain EnavSettings} with shore ID and position.
 */
public class EPDEnavSettings extends EnavSettings {

    private static final long serialVersionUID = -1365519240986252515L;

    private static final String PREFIX = EnavSettings.getPrefix();

    private String shoreId = "999" + System.currentTimeMillis();
    private LatLonPoint shorePos = new LatLonPoint.Double(56.02, 12.36); // Somewhere around Helsingør
 
    /**
     * Constructor
     */
    public EPDEnavSettings() {
        super();
    }

    /**
     * Read the properties element and set the internal variables
     * @param props the properties to read from
     */
    public void readProperties(Properties props) {
        super.readProperties(props);
        
        shorePos.setLatitude(PropUtils.doubleFromProperties(props, PREFIX + "shorePos_lat", shorePos.getLatitude()));
        shorePos.setLongitude(PropUtils.doubleFromProperties(props, PREFIX + "shorePos_lon", shorePos.getLongitude()));
        shoreId = props.getProperty(PREFIX + "shoreId", shoreId);
    }

    /**
     * Set the properties to the value from the internal, usually called
     * when saving settings to file
     * @param props the properties to update
     */
    public void setProperties(Properties props) {
        super.setProperties(props);
        
        props.put(PREFIX + "shorePos_lat", Double.toString(shorePos.getLatitude()));
        props.put(PREFIX + "shorePos_lon", Double.toString(shorePos.getLongitude()));
        props.put(PREFIX + "shoreId", shoreId);
        
    }

    /******************** Getters and setters **************************/
    
    public String getShoreId() {
        return shoreId;
    }

    public void setShoreId(String shoreId) {
        this.shoreId = shoreId;
    }

    public LatLonPoint getShorePos() {
        return shorePos;
    }

    public void setShorePos(LatLonPoint shorePos) {
        this.shorePos = shorePos;
    }

}
