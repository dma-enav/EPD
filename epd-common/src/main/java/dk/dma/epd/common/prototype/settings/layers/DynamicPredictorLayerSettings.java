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

import dk.dma.epd.common.prototype.settings.observers.DynamicPredictorLayerSettingsListener;

/**
 * @author Janus Varmarken
 */
public class DynamicPredictorLayerSettings extends
        LayerSettings<DynamicPredictorLayerSettingsListener> {

    /*
     * Callbacks to settings specific to DynamicPredictorLayer goes here...
     */

    private boolean enabled;

    public boolean isEnabled() {
        try {
            this.settingLock.readLock().lock();
            return enabled;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setEnabled(final boolean enabled) {
        try {
            this.settingLock.writeLock().lock();
            if (this.enabled == enabled) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.enabled = enabled;
            for (DynamicPredictorLayerSettingsListener obs : this.observers) {
                obs.onEnabledChanged(this, enabled);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

}
