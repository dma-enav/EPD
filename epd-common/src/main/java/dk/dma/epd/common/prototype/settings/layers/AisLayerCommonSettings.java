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
package dk.dma.epd.common.prototype.settings.layers;

import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

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
public class AisLayerCommonSettings<OBSERVER extends AisLayerCommonSettings.IObserver>
        extends VesselLayerSettings<OBSERVER> {

    /**
     * Specifies if all past tracks should be shown.
     */
    private boolean showAllPastTracks;

    /**
     * Setting specifying how often the layer should repaint itself (in
     * seconds).
     */
    private int layerRedrawInterval = 5;

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
            if(this.showAllPastTracks == show) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.showAllPastTracks = show;
            for (OBSERVER obs : this.observers) {
                obs.showAllPastTracksChanged(show);
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
            if(this.layerRedrawInterval == seconds) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.layerRedrawInterval = seconds;
            for (OBSERVER obs : this.observers) {
                obs.layerRedrawIntervalChanged(seconds);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for clients that want to listen for changes to an instance of
     * {@link AisLayerCommonSettings}.
     * 
     * @author Janus Varmarken
     */
    public interface IObserver extends VesselLayerSettings.IObserver {

        /**
         * Invoked when the setting specifying whether to show all past tracks has
         * been changed on the observed instance.
         * 
         * @param newValue
         *            The updated value of the setting.
         */
        void showAllPastTracksChanged(boolean newValue);

        /**
         * Invoked when the setting specifying how often the layer should repaint
         * itself has been changed on the observed instance.
         * 
         * @param newValue The updated value of the setting.
         */
        void layerRedrawIntervalChanged(int newValue);
    }
}
