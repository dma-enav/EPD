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
package dk.dma.epd.common.prototype.settings.layers;

import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.AisLayerCommonSettingsListener;

/**
 * Maintains settings relevant to an {@link AisLayerCommon} or any of its
 * subclasses. This class inherits from {@link ObservedSettings} allowing
 * clients to register for notifications of changes to any setting maintained by
 * this class.
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the
 *            {@code AisLayerCommonSettings} for changes.
 * @author Janus Varmarken
 */
public class AisLayerCommonSettings<OBSERVER extends AisLayerCommonSettingsListener>
        extends VesselLayerSettings<OBSERVER> implements
        AisLayerCommonSettingsListener {

    /**
     * Specifies if all past tracks should be shown.
     */
    private boolean showAllPastTracks;

    /**
     * Setting specifying how often the layer should repaint itself (in
     * seconds).
     */
    private int layerRedrawInterval = 5;

    @Override
    public AisLayerCommonSettings<OBSERVER> copy() {
        return (AisLayerCommonSettings<OBSERVER>) super.copy();
    }

    /**
     * Get the value of the setting specifying if all past tracks should be
     * shown.
     * 
     * @return {@code true} if all past tracks should be shown, {@code false} if
     *         all past tracks should be hidden.
     */
    public boolean isShowAllPastTracks() {
        try {
            this.settingLock.readLock().lock();
            return this.showAllPastTracks;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting specifying if all past tracks should be shown. The
     * registered observers are notified of this change.
     * 
     * @param show
     *            {@code true} if all past tracks should be shown, {@code false}
     *            if all past tracks should be hidden.
     */
    public void setShowAllPastTracks(final boolean show) {
        try {
            this.settingLock.writeLock().lock();
            if (this.showAllPastTracks == show) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.showAllPastTracks = show;
            for (OBSERVER obs : this.observers) {
                obs.showAllPastTracksChanged(this, show);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the value of the setting specifying how often the associated AIS
     * layer(s) should repaint itself/themselves.
     * 
     * @return The number of seconds between each repaint.
     */
    public int getLayerRedrawInterval() {
        try {
            this.settingLock.readLock().lock();
            return this.layerRedrawInterval;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting specifying how often the associated AIS layer(s)
     * should repaint itself/themselves.
     * 
     * @param seconds
     *            The number of seconds between each repaint.
     * @throws IllegalArgumentException
     *             if {@code seconds} is less than 1.
     */
    public void setLayerRedrawInterval(final int seconds) {
        // Sanity check setting value
        if (seconds < 1) {
            throw new IllegalArgumentException(
                    "A redraw interval below 1 second is not allowed.");
        }
        try {
            this.settingLock.writeLock().lock();
            if (this.layerRedrawInterval == seconds) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.layerRedrawInterval = seconds;
            for (OBSERVER obs : this.observers) {
                obs.layerRedrawIntervalChanged(this, seconds);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /*
     * Begin: Listener methods that are only used if this instance observes
     * another instance of this class.
     */

    @Override
    public void showAllPastTracksChanged(AisLayerCommonSettings<?> source, boolean newValue) {
        // TODO Auto-generated method stub
    }

    @Override
    public void layerRedrawIntervalChanged(AisLayerCommonSettings<?> source, int newValue) {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */
}
