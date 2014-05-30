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
package dk.dma.epd.ship.settings.layers;

import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;
import dk.dma.epd.ship.layers.ownship.OwnShipLayer;
import dk.dma.epd.ship.settings.observers.OwnShipLayerSettingsListener;

/**
 * Maintains settings for an {@link OwnShipLayer}.
 * 
 * @author Janus Varmarken
 */
public class OwnShipLayerSettings extends VesselLayerSettings<OwnShipLayerSettingsListener> {

    /**
     * Display multi source PNT.
     */
    private boolean multiSourcePntVisible; // Default is false

    public OwnShipLayerSettings() {
        super();
        // By default we do not show vessel name label for own ship.
        this.setShowVesselNameLabels(false);
    }
    
    /**
     * Gets the setting that specifies if multi source PNT should be displayed.
     * 
     * @return {@code true} if multi source PNT should be displayed,
     *         {@code false} if multi source PNT should <i>not</i> be displayed.
     */
    public boolean isMultiSourcePntVisible() {
        try {
            this.settingLock.readLock().lock();
            return this.multiSourcePntVisible;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if multi source PNT should be
     * displayed.
     * 
     * @param multiSourcePntVisible
     *            {@code true} if multi source PNT should be displayed,
     *            {@code false} if multi source PNT should <i>not</i> be
     *            displayed.
     */
    public void setMultiSourcePntVisible(final boolean multiSourcePntVisible) {
        try {
            this.settingLock.writeLock().lock();
            if (this.multiSourcePntVisible == multiSourcePntVisible) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.multiSourcePntVisible = multiSourcePntVisible;
            for (OwnShipLayerSettingsListener obs : this.observers) {
                obs.multiSourcePntVisibilityChanged(multiSourcePntVisible);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}
