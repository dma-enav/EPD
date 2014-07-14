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

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.LayerSettingsListener;

/**
 * <p>
 * A base class for maintaining settings that apply to an individual layer. I.e.
 * this class should be used as an abstract base class when writing classes that
 * store settings that are specifically targeted at a given type of layer.
 * </p>
 * <p>
 * If you discover a setting that is relevant to <b>all</b> layer types, you
 * should place that setting in this class.
 * </p>
 * <p>
 * This class inherits from {@link ObservedSettings} which allows clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * </p>
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the {@code LayerSettings} for
 *            changes.
 * @author Janus Varmarken
 */
public abstract class LayerSettings<OBSERVER extends LayerSettingsListener>
        extends ObservedSettings<OBSERVER> implements LayerSettingsListener {
    /*
     * Add settings that are relevant to all layer types here.
     */

    /**
     * Specifies if the layer should be displayed.
     */
    private boolean visible = true;

    /**
     * Get if the layer should be displayed.
     * 
     * @return {@code true} if the layer should be displayed, {@code false} if
     *         it should be hidden.
     */
    public boolean isVisible() {
        try {
            this.settingLock.readLock().lock();
            return this.visible;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Set if the layer should be displayed.
     * 
     * @param visible
     *            {@code true} to display the layer, {@code false} to hide the
     *            layer.
     */
    public void setVisible(boolean visible) {
        try {
            this.settingLock.writeLock().lock();
            if (this.visible == visible) {
                // No change, no need to notify observers.
                return;
            }
            this.visible = visible;
            for (OBSERVER obs : this.observers) {
                obs.isVisibleChanged(this, this.visible);
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
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        // Obey to change in observed instance.
        this.setVisible(newValue);
    }
    
    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */
}
