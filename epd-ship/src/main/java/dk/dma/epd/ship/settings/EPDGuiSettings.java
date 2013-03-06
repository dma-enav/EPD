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

import dk.dma.epd.common.prototype.settings.GuiSettings;

/**
 * General GUI settings
 */
public class EPDGuiSettings extends GuiSettings {

    private static final long serialVersionUID = 1L;
    private boolean fullscreen;
    
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Read the properties element and set the internal variables
     * @param props
     */
    public void readProperties(Properties props) {
        super.readProperties(props);
        fullscreen = PropUtils.booleanFromProperties(props, super.getPrefix() + "fullscreen", fullscreen);
    }


    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }


    /**
     * Set the properties to the value from the internal, usually called
     * when saving settings to file
     * @param props
     */
    @SuppressWarnings("static-access")
    public void setProperties(Properties props) {
        super.setProperties(props);
        props.put(super.PREFIX + "fullscreen", Boolean.toString(fullscreen));
    }
    
    
}
