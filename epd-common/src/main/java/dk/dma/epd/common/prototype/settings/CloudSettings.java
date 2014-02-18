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
package dk.dma.epd.common.prototype.settings;

import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

/**
 * Base class for Maritime Cloud settings and its services
 */
public class CloudSettings  implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "cloud.";
    
    /**
     * Cloud server configuration
     */
    private String cloudServerHost = "mms.maritimecloud.net";
    private int cloudServerPort = 43234;
    
    // Intended route settings
    private boolean broadcastIntendedRoute = true;
    private boolean showIntendedRoute = true;
    private int timeBetweenBroadCast = 1;
    private int adaptionTime = 1;

    /**
     * Constructor
     */
    public CloudSettings() {        
    }

    /**
     * Loads the Maritime Cloud-specific properties from 
     * the given {@code props} properties
     * 
     * @param props the properties to load the Maritime Cloud-specific properties from
     */
    public void readProperties(Properties props) {
        // Cloud server configuration
        cloudServerHost = props.getProperty(PREFIX + "cloudServerHost", cloudServerHost);
        cloudServerPort = PropUtils.intFromProperties(props, PREFIX + "cloudServerPort", cloudServerPort);
        
        // Intended route settings
        broadcastIntendedRoute = PropUtils.booleanFromProperties(props, PREFIX + "broadcastIntendedRoute", broadcastIntendedRoute);
        this.timeBetweenBroadCast = PropUtils.intFromProperties(props, PREFIX + "timeBetweenBroadCast", this.timeBetweenBroadCast);
        this.adaptionTime = PropUtils.intFromProperties(props, PREFIX + "adaptionTime", this.adaptionTime);
        showIntendedRoute = PropUtils.booleanFromProperties(props, PREFIX + "showIntendedRoute", showIntendedRoute);
    }
    
    /**
     * Updates the the given {@code props} properties with the the 
     * Maritime Cloud-specific settings
     * 
     * @param props the properties to update
     */
    public void setProperties(Properties props) {
        // Cloud server configuration
        props.put(PREFIX + "cloudServerHost", cloudServerHost);
        props.put(PREFIX + "cloudServerPort", Integer.toString(cloudServerPort));
        
        System.out.println(this.isBroadcastIntendedRoute());
        
        // Intended route settings
        props.put(PREFIX + "broadcastIntendedRoute", Boolean.toString(broadcastIntendedRoute));
        props.put(PREFIX + "timeBetweenBroadCast", Integer.toString(this.timeBetweenBroadCast));
        props.put(PREFIX + "adaptionTime", this.adaptionTime);
        props.put(PREFIX + "showIntendedRoute", Boolean.toString(showIntendedRoute));
    }
    
    /**
     * Returns the {@code CloudSettings} prefix
     * @return the {@code CloudSettings} prefix
     */
    public static String getPrefix() {
        return PREFIX;
    }
    
    /****** Getters and setters *******/
    
    public String getCloudServerHost() {
        return cloudServerHost;
    }

    public void setCloudServerHost(String cloudServerHost) {
        this.cloudServerHost = cloudServerHost;
    }

    public int getCloudServerPort() {
        return cloudServerPort;
    }

    public void setCloudServerPort(int cloudServerPort) {
        this.cloudServerPort = cloudServerPort;
    }
    
    public boolean isBroadcastIntendedRoute() {
        return broadcastIntendedRoute;
    }

    public void setBroadcastIntendedRoute(boolean broadcastIntendedRoute) {
        this.broadcastIntendedRoute = broadcastIntendedRoute;
    }

    public boolean isShowIntendedRoute() {
        return showIntendedRoute;
    }

    public void setShowIntendedRoute(boolean showIntendedRoute) {
        this.showIntendedRoute = showIntendedRoute;
    }

    public int getTimeBetweenBroadCast() {
        return timeBetweenBroadCast;
    }

    public void setTimeBetweenBroadCast(int timeBetweenBroadCast) {
        this.timeBetweenBroadCast = timeBetweenBroadCast;
    }

    public int getAdaptionTime() {
        return adaptionTime;
    }

    public void setAdaptionTime(int adaptionTime) {
        this.adaptionTime = adaptionTime;
    }
}
