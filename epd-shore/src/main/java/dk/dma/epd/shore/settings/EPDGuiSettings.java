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

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.GuiSettings;

/**
 * General GUI settings
 */
public class EPDGuiSettings extends GuiSettings {

    private static final long serialVersionUID = 1L;

    private String workspace = "";
    private String PREFIX = super.getPrefix();
    private boolean useAudio;

    /**
     * Constructor
     */
    public EPDGuiSettings() {
        super();
    }

    /**
     * Read the properties element and set the internal variables
     * 
     * @param props
     */
    public void readProperties(Properties props) {
        workspace = props.getProperty(PREFIX + "workspace");
        useAudio = PropUtils.booleanFromProperties(props, PREFIX + "useAudio", useAudio);
        super.readProperties(props);
    }

    /**
     * Set the properties to the value from the internal, usually called when saving settings to file
     * 
     * @param props
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "workspace", workspace);
        super.setProperties(props);
        props.put(PREFIX + "useAudio", Boolean.toString(useAudio));
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    /**
     * @return the useAudio
     */
    public boolean isUseAudio() {
        return useAudio;
    }

    /**
     * @param useAudio the useAudio to set
     */
    public void setUseAudio(boolean useAudio) {
        this.useAudio = useAudio;
    }
    
    
    
}
