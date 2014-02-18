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
