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

import dk.dma.epd.common.prototype.settings.MapSettings;

/**
 * Map/chart settings
 */
public class EPDMapSettings extends MapSettings {

    private static final long serialVersionUID = 1L;
    
    //Used internally to check if new map windows should try to make dongle check - if no dongle, don't retry on every new map
    private boolean encSuccess = true;

    public boolean isEncSuccess() {
        return encSuccess;
    }

    public void setEncSuccess(boolean encSuccess) {
        this.encSuccess = encSuccess;
    }
    

}
