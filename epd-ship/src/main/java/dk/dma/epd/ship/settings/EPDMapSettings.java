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
