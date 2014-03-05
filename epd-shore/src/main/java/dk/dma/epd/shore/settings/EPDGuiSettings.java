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

import dk.dma.epd.common.prototype.settings.GuiSettings;

/**
 * General GUI settings
 */
public class EPDGuiSettings extends GuiSettings {

    private static final long serialVersionUID = 1L;

    private String workspace = "";
    private String PREFIX = super.getPrefix();

    /**
     * Constructor
     */
    public EPDGuiSettings() {
        super();
    }


    /**
     * Read the properties element and set the internal variables
     * @param props
     */
    public void readProperties(Properties props) {
        workspace = props.getProperty(PREFIX + "workspace");
        super.readProperties(props);
    }

    /**
     * Set the properties to the value from the internal, usually called
     * when saving settings to file
     * @param props
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "workspace", workspace);
        
        super.setProperties(props);
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }
}
