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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.bbn.openmap.util.PropUtils;

/**
 * AIS settings
 */
public class AisSettings implements Serializable {

    /**
     * Property name that a {@code PropertyChangeListener} of this bean should use to verify that a received {@code PropertyChangeEvent} concerns the {@link #showNameLabels} property.
     */
    public static final String SHOW_NAME_LABELS_CHANGED = "showNameLabels";
    
    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "ais.";

    private boolean visible = true;
    private boolean strict = true; // Strict timeout rules
    private int minRedrawInterval = 5; // 5 sec
    private boolean allowSending = true;
    private int sartPrefix = 970;
    private String[] simulatedSartMmsi = {}; // Specify comma-separated mmsi list to simulate SarTarget's
    private boolean showNameLabels = true;
    private boolean showRisk;
    private int pastTrackMaxTime = 4 * 60; // In minutes
    private int pastTrackDisplayTime = 30; // In minutes
    private int pastTrackMinDist = 100; // In meters
    private int pastTrackOwnShipMinDist = 20; // In meters

    /**
     * The minimum length of the COG vector in minutes.
     */
    private int cogVectorLengthMin = 1;

    /**
     * The maximum length of the COG vector in minutes.
     */
    private int cogVectorLengthMax = 8;

    /**
     * The scale interval that separates two values for cogVectorLength. E.g.: if assigned a value of x, the valid map scale for a
     * cogVectorLength of n minutes is in ](n-1) * x; n * x].
     */
    private float cogVectorLengthScaleInterval = 5000.0f;
    private float cogVectorHideBelow = 0.1f;

    private final String varNameCogVectorLengthMin = "cogVectorLengthMin";
    private final String varNameCogVectorLengthMax = "cogVectorLengthMax";
    private final String varNameCogVectorLengthScaleInterval = "cogVectorLengthScaleInterval";
    private final String varNameCogVectorHideBelow = "cogVectorHideBelow";

    /**
     * Used to notify listeners of changes to properties in this bean.
     */
    private final PropertyChangeSupport notifier = new PropertyChangeSupport(this);
    
    /**
     * Constructor
     */
    public AisSettings() {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.notifier.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.notifier.removePropertyChangeListener(listener);
    }
    
    /**
     * Returns the AisSettings prefix
     * 
     * @return the AisSettings prefix
     */
    public static String getPrefix() {
        return PREFIX;
    }

    /**
     * Loads the AIS-specific properties from the given {@code props} properties
     * 
     * @param props
     *            the properties to load the AIS properties from
     */
    public void readProperties(Properties props) {
        visible = PropUtils.booleanFromProperties(props, PREFIX + "visible", visible);
        strict = PropUtils.booleanFromProperties(props, PREFIX + "strict", strict);
        minRedrawInterval = PropUtils.intFromProperties(props, PREFIX + "minRedrawInterval", minRedrawInterval);
        allowSending = PropUtils.booleanFromProperties(props, PREFIX + "allowSending", allowSending);
        sartPrefix = PropUtils.intFromProperties(props, PREFIX + "sartPrefix", sartPrefix);
        simulatedSartMmsi = PropUtils.stringArrayFromProperties(props, PREFIX + "simulatedSartMmsi", ",");
        showNameLabels = PropUtils.booleanFromProperties(props, PREFIX + "showNameLabels", showNameLabels);
        pastTrackMaxTime = PropUtils.intFromProperties(props, PREFIX + "pastTrackMaxTime", pastTrackMaxTime);
        pastTrackDisplayTime = PropUtils.intFromProperties(props, PREFIX + "pastTrackDisplayTime", pastTrackDisplayTime);
        pastTrackMinDist = PropUtils.intFromProperties(props, PREFIX + "pastTrackMinDist", pastTrackMinDist);
        pastTrackOwnShipMinDist = PropUtils.intFromProperties(props, PREFIX + "pastTrackOwnShipMinDist", pastTrackOwnShipMinDist);

        this.cogVectorLengthMin = PropUtils.intFromProperties(props, PREFIX + this.varNameCogVectorLengthMin,
                this.cogVectorLengthMin);
        this.cogVectorLengthMax = PropUtils.intFromProperties(props, PREFIX + this.varNameCogVectorLengthMax,
                this.cogVectorLengthMax);
        this.cogVectorLengthScaleInterval = PropUtils.floatFromProperties(props, PREFIX + this.varNameCogVectorLengthScaleInterval,
                this.cogVectorLengthScaleInterval);
        this.cogVectorHideBelow = PropUtils.floatFromProperties(props, PREFIX + this.varNameCogVectorHideBelow,
                this.cogVectorHideBelow);

        if (simulatedSartMmsi == null) {
            simulatedSartMmsi = new String[0];
        }
    }

    /**
     * Updates the the given {@code props} properties with the the AIS-specific settings
     * 
     * @param props
     *            the properties to update
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "visible", Boolean.toString(visible));
        props.put(PREFIX + "strict", Boolean.toString(strict));
        props.put(PREFIX + "minRedrawInterval", Integer.toString(minRedrawInterval));
        props.put(PREFIX + "allowSending", Boolean.toString(allowSending));
        props.put(PREFIX + "sartPrefix", Integer.toString(sartPrefix));
        props.put(PREFIX + "simulatedSartMmsi", StringUtils.defaultString(StringUtils.join(simulatedSartMmsi, ",")));
        props.put(PREFIX + "showNameLabels", Boolean.toString(showNameLabels));
        props.put(PREFIX + "pastTrackMaxTime", Integer.toString(pastTrackMaxTime));
        props.put(PREFIX + "pastTrackDisplayTime", Integer.toString(pastTrackDisplayTime));
        props.put(PREFIX + "pastTrackMinDist", Integer.toString(pastTrackMinDist));
        props.put(PREFIX + "pastTrackOwnShipMinDist", Integer.toString(pastTrackOwnShipMinDist));

        props.put(PREFIX + this.varNameCogVectorLengthMin, Integer.toString(this.cogVectorLengthMin));
        props.put(PREFIX + this.varNameCogVectorLengthMax, Integer.toString(this.cogVectorLengthMax));
        props.put(PREFIX + this.varNameCogVectorLengthScaleInterval, Float.toString(this.cogVectorLengthScaleInterval));
        props.put(PREFIX + this.varNameCogVectorHideBelow, Float.toString(this.cogVectorHideBelow));
    }

    /** Getters and setters **/

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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
        boolean oldVal = this.showNameLabels;
        this.showNameLabels = showNameLabels;
        // notify property change listeners of the changed in value
        this.notifier.firePropertyChange(SHOW_NAME_LABELS_CHANGED, oldVal, this.showNameLabels);
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

    public int getPastTrackOwnShipMinDist() {
        return pastTrackOwnShipMinDist;
    }

    public void setPastTrackOwnShipMinDist(int pastTrackOwnShipMinDist) {
        this.pastTrackOwnShipMinDist = pastTrackOwnShipMinDist;
    }

    public int getCogVectorLengthMin() {
        return cogVectorLengthMin;
    }

    public void setCogVectorLengthMin(int cogVectorLengthMin) {
        this.cogVectorLengthMin = cogVectorLengthMin;
    }

    public int getCogVectorLengthMax() {
        return cogVectorLengthMax;
    }

    public void setCogVectorLengthMax(int cogVectorLengthMax) {
        this.cogVectorLengthMax = cogVectorLengthMax;
    }

    public float getCogVectorLengthScaleInterval() {
        return cogVectorLengthScaleInterval;
    }

    public void setCogVectorLengthScaleInterval(float cogVectorLengthScaleInterval) {
        this.cogVectorLengthScaleInterval = cogVectorLengthScaleInterval;
    }

    public float getCogVectorHideBelow() {
        return cogVectorHideBelow;
    }

    public void setCogVectorHideBelow(float cogVectorHideBelow) {
        this.cogVectorHideBelow = cogVectorHideBelow;
    }
}
