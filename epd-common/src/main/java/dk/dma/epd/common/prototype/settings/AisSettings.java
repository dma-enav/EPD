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

import org.apache.commons.lang.StringUtils;

import com.bbn.openmap.util.PropUtils;

/**
 * AIS settings
 */
public class AisSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "ais.";
    
    private boolean visible = true;
    private double cogVectorLength = 6; // minutes
    private boolean strict = true; // Strict timeout rules
    private int minRedrawInterval = 5; // 5 sec
    private boolean allowSending = true;
    private boolean broadcastIntendedRoute = true;
    private boolean showIntendedRouteByDefault;
    private int intendedRouteMaxWps = 8;
    private int intendedRouteMaxTime; // In minutes 0 = inf
    private int sartPrefix = 970;
    private String[] simulatedSartMmsi = {};     // Specify comma-separated mmsi list to simulate SarTarget's
    private boolean showNameLabels = true;
    private int showMinuteMarksAISTarget = 200;
    private boolean showRisk;
    private int pastTrackMaxTime = 4 * 60;  // In minutes
    private int pastTrackDisplayTime = 30;  // In minutes
    private int pastTrackMinDist = 100;     // In meters
    
    /**
     * Constructor
     */
    public AisSettings() {   
    }
    
    /**
     * Returns the AisSettings prefix    
     * @return the AisSettings prefix
     */
    public static String getPrefix() {
        return PREFIX;
    }

    /**
     * Loads the AIS-specific properties from the given {@code props} properties
     * @param props the properties to load the AIS properties from
     */
    public void readProperties(Properties props) {
        visible = PropUtils.booleanFromProperties(props, PREFIX + "visible", visible);
        cogVectorLength = PropUtils.doubleFromProperties(props, PREFIX + "cogVectorLength", cogVectorLength);
        strict = PropUtils.booleanFromProperties(props, PREFIX + "strict", strict);
        minRedrawInterval = PropUtils.intFromProperties(props, PREFIX + "minRedrawInterval", minRedrawInterval);
        allowSending = PropUtils.booleanFromProperties(props, PREFIX + "allowSending", allowSending);
        broadcastIntendedRoute = PropUtils.booleanFromProperties(props, PREFIX + "broadcastIntendedRoute", broadcastIntendedRoute);
        showIntendedRouteByDefault = PropUtils.booleanFromProperties(props, PREFIX + "showIntendedRouteByDefault", showIntendedRouteByDefault);
        intendedRouteMaxWps = PropUtils.intFromProperties(props, PREFIX + "intendedRouteMaxWps", intendedRouteMaxWps);
        intendedRouteMaxTime = PropUtils.intFromProperties(props, PREFIX + "intendedRouteMaxTime", intendedRouteMaxTime);
        sartPrefix = PropUtils.intFromProperties(props, PREFIX + "sartPrefix", sartPrefix);
        simulatedSartMmsi = PropUtils.stringArrayFromProperties(props, PREFIX + "simulatedSartMmsi", ",");
        showNameLabels = PropUtils.booleanFromProperties(props, PREFIX + "showNameLabels", showNameLabels);
        showMinuteMarksAISTarget = PropUtils.intFromProperties(props, PREFIX + "showMinuteMarksAISTarget", showMinuteMarksAISTarget);
        pastTrackMaxTime = PropUtils.intFromProperties(props, PREFIX + "pastTrackMaxTime", pastTrackMaxTime);
        pastTrackDisplayTime = PropUtils.intFromProperties(props, PREFIX + "pastTrackDisplayTime", pastTrackDisplayTime);
        pastTrackMinDist = PropUtils.intFromProperties(props, PREFIX + "pastTrackMinDist", pastTrackMinDist);
        
        if (simulatedSartMmsi == null){
            simulatedSartMmsi = new String[0];
        }
    }
    
    /**
     * Updates the the given {@code props} properties with the the AIS-specific settings
     * @param props the properties to update
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "visible", Boolean.toString(visible));
        props.put(PREFIX + "cogVectorLength", Double.toString(cogVectorLength));
        props.put(PREFIX + "strict", Boolean.toString(strict));
        props.put(PREFIX + "minRedrawInterval", Integer.toString(minRedrawInterval));
        props.put(PREFIX + "allowSending", Boolean.toString(allowSending));
        props.put(PREFIX + "broadcastIntendedRoute", Boolean.toString(broadcastIntendedRoute));
        props.put(PREFIX + "showIntendedRouteByDefault", Boolean.toString(showIntendedRouteByDefault));
        props.put(PREFIX + "intendedRouteMaxWps", Integer.toString(intendedRouteMaxWps));
        props.put(PREFIX + "intendedRouteMaxTime", Integer.toString(intendedRouteMaxTime));
        props.put(PREFIX + "sartPrefix", Integer.toString(sartPrefix));
        props.put(PREFIX + "simulatedSartMmsi", StringUtils.defaultString(StringUtils.join(simulatedSartMmsi, ",")));
        props.put(PREFIX + "showNameLabels", Boolean.toString(showNameLabels));
        props.put(PREFIX + "showMinuteMarksAISTarget", Float.toString(showMinuteMarksAISTarget));
        props.put(PREFIX + "pastTrackMaxTime", Integer.toString(pastTrackMaxTime));
        props.put(PREFIX + "pastTrackDisplayTime", Integer.toString(pastTrackDisplayTime));
        props.put(PREFIX + "pastTrackMinDist", Integer.toString(pastTrackMinDist));
    }
    
    /** Getters and setters **/

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getCogVectorLength() {
        return cogVectorLength;
    }

    public void setCogVectorLength(double cogVectorLength) {
        this.cogVectorLength = cogVectorLength;
    }
    
    public boolean isStrict() {
        return strict;
    }
    
    public void setStrict(boolean strict) {
        this.strict = strict;
    }
    
    public int getMinRedrawInterval() {
        return minRedrawInterval;
    }
    
    public void setMinRedrawInterval(int minRedrawInterval) {
        this.minRedrawInterval = minRedrawInterval;
    }

    public boolean isAllowSending() {
        return allowSending;
    }

    public void setAllowSending(boolean allowSending) {
        this.allowSending = allowSending;
    }

    public boolean isBroadcastIntendedRoute() {
        return broadcastIntendedRoute;
    }

    public void setBroadcastIntendedRoute(boolean broadcastIntendedRoute) {
        this.broadcastIntendedRoute = broadcastIntendedRoute;
    }

    public boolean isShowIntendedRouteByDefault() {
        return showIntendedRouteByDefault;
    }

    public void setShowIntendedRouteByDefault(boolean showIntendedRouteByDefault) {
        this.showIntendedRouteByDefault = showIntendedRouteByDefault;
    }
    
    public int getIntendedRouteMaxWps() {
        return intendedRouteMaxWps;
    }
    
    public void setIntendedRouteMaxWps(int intendedRouteMaxWps) {
        this.intendedRouteMaxWps = intendedRouteMaxWps;
    }
    
    public int getIntendedRouteMaxTime() {
        return intendedRouteMaxTime;
    }
    
    public void setIntendedRouteMaxTime(int intendedRouteMaxTime) {
        this.intendedRouteMaxTime = intendedRouteMaxTime;
    }
    
    public String getSartPrefix() {
        return Integer.toString(sartPrefix);
    }
    
    public void setSartPrefix(String sartPrefix) {
        this.sartPrefix = new Integer(sartPrefix);
    }
    
    public String[] getSimulatedSartMmsi() {
        return simulatedSartMmsi;
    }
    
    public void setSimulatedSartMmsi(String[] simulatedSartMmsi) {
        this.simulatedSartMmsi = simulatedSartMmsi;
    }
    
    public boolean isShowNameLabels() {
        return showNameLabels;
    }
    
    public void setShowNameLabels(boolean showNameLabels) {
        this.showNameLabels = showNameLabels;
    }
    
    public int getShowMinuteMarksAISTarget() {
        return showMinuteMarksAISTarget;
    }

    public void setShowMinuteMarksAISTarget(int showMinuteMarksAISTarget) {
        this.showMinuteMarksAISTarget = showMinuteMarksAISTarget;
    }

    public boolean isShowRisk() {
        return showRisk;
    }

    public void setShowRisk(boolean showRisk) {
        this.showRisk = showRisk;
    }
    
    public int getPastTrackMaxTime() {
        return pastTrackMaxTime;
    }

    public void setPastTrackMaxTime(int pastTrackMaxTime) {
        this.pastTrackMaxTime = pastTrackMaxTime;
    }

    public int getPastTrackDisplayTime() {
        return pastTrackDisplayTime;
    }

    public void setPastTrackDisplayTime(int pastTrackDisplayTime) {
        this.pastTrackDisplayTime = pastTrackDisplayTime;
    }

    public int getPastTrackMinDist() { 
        return pastTrackMinDist;
    }

    public void setPastTrackMinDist(int pastTrackMinDist) {
        this.pastTrackMinDist = pastTrackMinDist;
    }    
}
