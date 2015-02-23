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
    private String cloudServerHost = "mms03.maritimecloud.net";
    private int cloudServerPort = 43234;
    
    // Intended route settings
    private boolean broadcastIntendedRoute = true;
    private boolean intendedRouteFilterOn;
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

// TODO: Maritime Cloud 0.2 re-factoring
// Force cloud server 0.2 for the test:
//        // Cloud server configuration
        cloudServerHost = props.getProperty(PREFIX + "cloudServerHost", cloudServerHost);
        cloudServerPort = PropUtils.intFromProperties(props, PREFIX + "cloudServerPort", cloudServerPort);
        
        // Intended route settings
        broadcastIntendedRoute = PropUtils.booleanFromProperties(props, PREFIX + "broadcastIntendedRoute", broadcastIntendedRoute);
        this.timeBetweenBroadCast = PropUtils.intFromProperties(props, PREFIX + "timeBetweenBroadCast", this.timeBetweenBroadCast);
        this.adaptionTime = PropUtils.intFromProperties(props, PREFIX + "adaptionTime", this.adaptionTime);
        intendedRouteFilterOn = PropUtils.booleanFromProperties(props, PREFIX + "intendedRouteFilterOn", intendedRouteFilterOn);
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
        props.put(PREFIX + "adaptionTime",Integer.toString(adaptionTime));
        props.put(PREFIX + "intendedRouteFilterOn", Boolean.toString(intendedRouteFilterOn));
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
    
    public boolean isIntendedRouteFilterOn() {
        return intendedRouteFilterOn;
    }
    
    public void setIntendedRouteFilterOn(boolean intendedRouteFilterOn) {
        this.intendedRouteFilterOn = intendedRouteFilterOn;
    }
    
}
