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
package dk.dma.epd.shore.settings;

import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.settings.EnavSettings;

/**
 * Specific e-Navigation settings
 * <p>
 * Extends the {@linkplain EnavSettings} with shore ID and position.
 */
public class EPDEnavSettings extends EnavSettings {

    private static final long serialVersionUID = -1365519240986252515L;

    private static final String PREFIX = EnavSettings.getPrefix();

    private String shoreId = (MaritimeCloudUtils.STCC_MMSI_PREFIX + System.currentTimeMillis()).substring(0, 9);
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
