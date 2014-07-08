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
package dk.dma.epd.ship.settings;

import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.MapSettings;

/**
 * Map/chart settings specific for EPDShip
 */
public class EPDMapSettings extends MapSettings {

    private static final long serialVersionUID = 1L;

    private boolean msPntVisible; // Default is false

    /**
     * Reads the properties from the {@code Properties} parameter
     * @param props the {@code Properties} to read from
     */
    @Override
    public void readProperties(Properties props) {
        super.readProperties(props);
        
        // Multi-source PNT layer settings
        msPntVisible = PropUtils.booleanFromProperties(props, super.getPrefix() + "msPntVisible", msPntVisible);
    }

    /**
     * Reads the properties from the {@code Properties} parameter
     * @param props the {@code Properties} to read from
     */
    @Override
    public void setProperties(Properties props) {
        super.setProperties(props);
        
        // Multi-source PNT layer settings
        props.put(super.getPrefix() + "msPntVisible", Boolean.toString(msPntVisible));        
    }

    /******************** Getters and setters **************************/
    
    public boolean isMsPntVisible() {
        return msPntVisible;
    }

    public void setMsPntVisible(boolean msPntVisible) {
        this.msPntVisible = msPntVisible;
    }

}
