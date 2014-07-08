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

import dk.dma.epd.common.prototype.model.route.PartialRouteFilter;
import dk.dma.epd.common.prototype.settings.CloudSettings;

/**
 * Ship-specific Maritime Cloud settings and its services
 */
public class EPDCloudSettings extends CloudSettings {

    private static final long serialVersionUID = 1L;
    private PartialRouteFilter intendedRouteFilter = PartialRouteFilter.DEFAULT;

    /**
     * Constructor
     */
    public EPDCloudSettings() {  
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void readProperties(Properties props) {
        super.readProperties(props);
        
        intendedRouteFilter = PartialRouteFilter.fromString(props.getProperty(getPrefix() + "intendedRouteFilter", intendedRouteFilter.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Properties props) {
        super.setProperties(props);
        
        props.put(getPrefix() + "intendedRouteFilter", intendedRouteFilter.toString());        
    }


    public PartialRouteFilter  getIntendedRouteFilter() {
        return intendedRouteFilter;
    }
    
    public void setIntendedRouteFilter(PartialRouteFilter intendedRouteFilter) {
        this.intendedRouteFilter = intendedRouteFilter;
    }    
}
