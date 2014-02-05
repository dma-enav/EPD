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
package dk.dma.epd.common.prototype.ais;

import java.io.Serializable;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Class holding settings for vessel targets
 */
@ThreadSafe
public class VesselTargetSettings implements Serializable {
    
    private static final long serialVersionUID = 1919231736214081361L;
    
    @GuardedBy("this") private boolean hide;
    @GuardedBy("this") private boolean showPastTrack;
    @GuardedBy("this") private int pastTrackDisplayTime;
    @GuardedBy("this") private int pastTrackMinDist;
    
    /**
     * Empty constructor
     */
    public VesselTargetSettings() {   
    }
    
    /**
     * Copy constructor
     * @param settings
     */
    public VesselTargetSettings(VesselTargetSettings settings) {
        this.hide = settings.hide;
        this.showPastTrack = settings.showPastTrack;
        this.pastTrackDisplayTime = settings.pastTrackDisplayTime;
        this.pastTrackMinDist = settings.pastTrackMinDist;
    }

    /**
     * Is the target hidden on the display or not
     * @return
     */
    public synchronized boolean isHide() {
        return hide;
    }

    /**
     * Set visibility
     * @param hide
     */
    public synchronized void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * Will the past-track be shown for the target 
     * @return
     */
    public synchronized boolean isShowPastTrack() {
        return showPastTrack;
    }

    /**
     * Set visibility of intended route
     * @param showPastTrack
     */
    public synchronized void setShowPastTrack(boolean showPastTrack) {
        this.showPastTrack = showPastTrack;
    }    

    /**
     * Returns the number of minutes of the past-tack to display
     * @return the number of minutes of the past-tack to display
     */
    public synchronized int getPastTrackDisplayTime() {
        return pastTrackDisplayTime;
    }

    /**
     * Sets the number of minutes of the past-tack to display
     * @param pastTrackDisplayTime the number of minutes of the past-tack to display
     */
    public synchronized void setPastTrackDisplayTime(int pastTrackDisplayTime) {
        this.pastTrackDisplayTime = pastTrackDisplayTime;
    }

    /**
     * Returns the minimum distance in meters between two past-track points
     * @return the minimum distance in meters between two past-track points
     */
    public synchronized int getPastTrackMinDist() {
        return pastTrackMinDist;
    }

    /**
     * Sets the minimum distance in meters between two past-track points
     * @param pastTrackMinDist the minimum distance in meters between two past-track points
     */
    public synchronized void setPastTrackMinDist(int pastTrackMinDist) {
        this.pastTrackMinDist = pastTrackMinDist;
    }
}
