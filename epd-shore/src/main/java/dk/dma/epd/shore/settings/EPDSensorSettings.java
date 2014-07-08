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

import dk.dma.epd.common.prototype.settings.SensorSettings;

/**
 * Sensor settings
 */
public class EPDSensorSettings extends SensorSettings{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public EPDSensorSettings() {
        super();
        
        // Default aisTcpOrUdpPort is 4001 for edp-ship, but 4002 for edp-shore
        setAisTcpOrUdpPort(4002);
    }
}
