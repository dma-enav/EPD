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

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.GuiSettings;

/**
 * General GUI settings
 */
public class ESDGuiSettings extends GuiSettings {

    private static final long serialVersionUID = 1L;

    private boolean fullscreen;
    private String workspace = "";
    private String wmsQuery = "";
    private boolean useWMS;
    private String PREFIX = super.getPrefix();

    /**
     * Constructor
     */
    public ESDGuiSettings() {

    }


    public String getWmsQuery() {
        return wmsQuery;
    }

    public String getWorkspace() {
        return workspace;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }




    /**
     * Read the properties element and set the internal variables
     * @param props
     */
    public void readProperties(Properties props) {

        fullscreen = PropUtils.booleanFromProperties(props, super.getPrefix() + "fullscreen", fullscreen);
        workspace = props.getProperty(PREFIX + "workspace");
        wmsQuery = props.getProperty(PREFIX + "wmsQuery");
        useWMS = PropUtils.booleanFromProperties(props, PREFIX + "useWMS", useWMS);
    }



    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }



    /**
     * Set the properties to the value from the internal, usually called
     * when saving settings to file
     * @param props
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "fullscreen", Boolean.toString(fullscreen));
        props.put(PREFIX + "workspace", workspace);
        props.put(PREFIX + "wmsQuery", wmsQuery);
        props.put(PREFIX + "useWMS", Boolean.toString(useWMS));
    }

    public void setWmsQuery(String wmsQuery) {
        this.wmsQuery = wmsQuery;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public boolean useWMS() {
        return useWMS;
    }

    public void setUseWMS(boolean useWMS) {
        this.useWMS = useWMS;
    }


}
