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

import java.util.Properties;

import javax.annotation.concurrent.GuardedBy;

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * Maintains settings relevant to an {@link AisLayerCommon} or any of its
 * subclasses. This class inherits from {@link ObservedSettings} allowing
 * clients to register for notifications of changes to any setting maintained by
 * this class.
 * 
 * @author Janus Varmarken
 */
public abstract class AisLayerCommonSettings<OBSERVER extends IAisLayerCommonSettingsObserver>
        extends LayerSettings<OBSERVER> {

    /**
     * The setting key for the "show all AIS names" setting.
     */
    public static final String KEY_SHOW_ALL_AIS_NAMES = "showAllAisNameLabels";

    /**
     * The setting key for the "show all past tracks" setting.
     */
    public static final String KEY_SHOW_ALL_PAST_TRACKS = "showAllPastTracks";

    /**
     * Specifies if all AIS name labels should be shown.
     */
    @GuardedBy("lockShowAllAisNameLabels")
    private boolean showAllAisNameLabels = true;

    /**
     * Used as lock when writing to or reading from
     * {@link #showAllAisNameLabels}.
     */
    private Object lockShowAllAisNameLabels = new Object();

    /**
     * Specifies if all past tracks should be shown.
     */
    @GuardedBy("lockShowAllPastTracks")
    private boolean showAllPastTracks;

    /**
     * Used as lock when writing to or reading from {@link #showAllPastTracks}.
     */
    private Object lockShowAllPastTracks = new Object();

    /**
     * Get the value of the setting specifying if all AIS name labels should be
     * shown.
     * 
     * @return {@code true} if all AIS name labels should be shown,
     *         {@code false} if all AIS name labels should be hidden.
     */
    public boolean isShowAllAisNameLabels() {
        synchronized (lockShowAllAisNameLabels) {
            return this.showAllAisNameLabels;
        }
    }

    /**
     * Changes the setting specifying if all AIS name labels should be shown.
     * The registered observers are notified of this change.
     * 
     * @param show
     *            {@code true} to show all AIS name labels, {@code false} to
     *            hide all AIS name labels.
     */
    public void setShowAllAisNameLabels(boolean show) {
        synchronized (this.lockShowAllAisNameLabels) {
            boolean oldVal = this.showAllAisNameLabels;
            this.showAllAisNameLabels = show;
            // Notify observers of change to this setting
            for (OBSERVER obs : this.observers) {
                obs.showAllAisNameLabelsChanged(oldVal,
                        this.showAllAisNameLabels);
            }
        }
    }

    /**
     * Get the value of the setting specifying if all past tracks should be
     * shown.
     * 
     * @return {@code true} if all past tracks should be shown, {@code false} if
     *         all past tracks should be hidden.
     */
    public boolean isShowAllPastTracks() {
        synchronized (this.lockShowAllPastTracks) {
            return this.showAllPastTracks;
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
    public void setShowAllPastTracks(boolean show) {
        synchronized (this.lockShowAllPastTracks) {
            boolean oldVal = this.showAllPastTracks;
            this.showAllPastTracks = show;
            // Notify observers of change to this setting
            for (OBSERVER obs : this.observers) {
                obs.showAllPastTracksChanged(oldVal, this.showAllPastTracks);
            }
        }
    }

    @Override
    protected void onLoadSuccess(Properties settings) {
        this.setShowAllAisNameLabels(PropUtils.booleanFromProperties(settings,
                KEY_SHOW_ALL_AIS_NAMES, this.isShowAllAisNameLabels()));
        this.setShowAllPastTracks(PropUtils.booleanFromProperties(settings,
                KEY_SHOW_ALL_PAST_TRACKS, this.isShowAllPastTracks()));
        // TODO init other settings variables based on the provided Properties
        // instance.
    }

    @Override
    protected Properties onSaveSettings() {
        Properties savedVars = new Properties();
        savedVars.setProperty(KEY_SHOW_ALL_AIS_NAMES,
                Boolean.toString(this.isShowAllAisNameLabels()));
        savedVars.setProperty(KEY_SHOW_ALL_PAST_TRACKS,
                Boolean.toString(this.isShowAllPastTracks()));
        // TODO store other settings variables based on field values
        return savedVars;
    }
}
