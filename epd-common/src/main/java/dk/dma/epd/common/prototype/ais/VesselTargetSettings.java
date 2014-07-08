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
package dk.dma.epd.common.prototype.ais;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Class holding settings for vessel targets
 */
@ThreadSafe
public class VesselTargetSettings implements Serializable {

    private static final long serialVersionUID = 1919231736214081361L;

    @GuardedBy("this")
    private boolean hide;
    @GuardedBy("this")
    private boolean showPastTrack;
    @GuardedBy("this")
    private int pastTrackDisplayTime;
    @GuardedBy("this")
    private int pastTrackMinDist;

    /**
     * Set set of observers of changes to properties of this {@code VesselTargetSettings}.
     */
    private transient CopyOnWriteArrayList<IVesselTargetSettingsListener> changeListeners = new CopyOnWriteArrayList<>();

    /**
     * Empty constructor
     */
    public VesselTargetSettings() {
    }

    /**
     * Copy constructor
     * 
     * @param settings
     */
    public VesselTargetSettings(VesselTargetSettings settings) {
        this.hide = settings.hide;
        this.showPastTrack = settings.showPastTrack;
        this.pastTrackDisplayTime = settings.pastTrackDisplayTime;
        this.pastTrackMinDist = settings.pastTrackMinDist;
    }
    
    /**
     * Returns the list of change listeners
     * @return the list of change listeners
     */
    public synchronized CopyOnWriteArrayList<IVesselTargetSettingsListener> getChangeListeners() {
        if(this.changeListeners == null) {
            // May need to init as this may be deserialized and hence field initialization may not have been performed.
            this.changeListeners = new CopyOnWriteArrayList<>();
        }
        return changeListeners;
    }

    /**
     * Add a listener to receive notifications of changes to properties of this
     * {@code VesselTargetSettings}. A listener can only be registered once.
     * 
     * @param listener
     *            A {@link IVesselTargetSettingsListener} that wants to receive
     *            notifications of changes to properties of this
     *            {@code VesselTargetSettings}.
     */
    public synchronized void addChangeListener(
            IVesselTargetSettingsListener listener) {
        getChangeListeners().addIfAbsent(listener);
    }

    /**
     * Remove a listener such that it will no longer receive notifications of
     * changes to properties of this {@code VesselTargetSettings}.
     * 
     * @param listener
     *            The {@link IVesselTargetSettingsListener} that should no
     *            longer listen for changes to properties of this
     *            {@code VesselTargetSettings}.
     * @return True if the listener was successfully removed, false if the
     *         listener was not registered with this
     *         {@code VesselTargetSettings} and hence could not be removed.
     */
    public synchronized boolean removeChangeListener(
            IVesselTargetSettingsListener listener) {
        return getChangeListeners().remove(listener);
    }

    /**
     * Is the target hidden on the display or not
     * 
     * @return
     */
    public synchronized boolean isHide() {
        return hide;
    }

    /**
     * Set visibility
     * 
     * @param hide
     */
    public synchronized void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * Will the past-track be shown for the target
     * 
     * @return
     */
    public synchronized boolean isShowPastTrack() {
        return showPastTrack;
    }

    /**
     * Set visibility of intended route
     * 
     * @param showPastTrack
     */
    public synchronized void setShowPastTrack(boolean showPastTrack) {
        this.showPastTrack = showPastTrack;
        for(IVesselTargetSettingsListener listener : getChangeListeners()) {
            listener.showPastTrackUpdated(this);
        }
    }

    /**
     * Returns the number of minutes of the past-tack to display
     * 
     * @return the number of minutes of the past-tack to display
     */
    public synchronized int getPastTrackDisplayTime() {
        return pastTrackDisplayTime;
    }

    /**
     * Sets the number of minutes of the past-tack to display
     * 
     * @param pastTrackDisplayTime
     *            the number of minutes of the past-tack to display
     */
    public synchronized void setPastTrackDisplayTime(int pastTrackDisplayTime) {
        this.pastTrackDisplayTime = pastTrackDisplayTime;
    }

    /**
     * Returns the minimum distance in meters between two past-track points
     * 
     * @return the minimum distance in meters between two past-track points
     */
    public synchronized int getPastTrackMinDist() {
        return pastTrackMinDist;
    }

    /**
     * Sets the minimum distance in meters between two past-track points
     * 
     * @param pastTrackMinDist
     *            the minimum distance in meters between two past-track points
     */
    public synchronized void setPastTrackMinDist(int pastTrackMinDist) {
        this.pastTrackMinDist = pastTrackMinDist;
    }
}
