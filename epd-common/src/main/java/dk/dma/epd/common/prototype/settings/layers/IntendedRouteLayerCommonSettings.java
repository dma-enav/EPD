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


/**
 * @author Janus Varmarken
 */
public class IntendedRouteLayerCommonSettings<OBSERVER extends IIntendedRouteLayerCommonSettingsObserver>
        extends LayerSettings<OBSERVER> {

    /**
     * Setting specifying whether to use intended route filter.
     */
    private boolean filter;

    /**
     * Get if intended route filter is enabled.
     * 
     * @return {@code true} if intended route filter is enabled, {@code false}
     *         if it is disabled.
     */
    public boolean isIntendedRouteFilterInUse() {
        try {
            this.settingLock.readLock().lock();
            return this.filter;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Toggles intended route filtering on/off.
     * 
     * @param isFilterInUse
     *            {@code true} if intended route filter should be enabled,
     *            {@code false} if it should be disabled.
     */
    public void setIntendedRouteFilterInUse(final boolean isFilterInUse) {
        try {
            this.settingLock.writeLock().lock();
            if (this.filter == isFilterInUse) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.filter = isFilterInUse;
            for (OBSERVER obs : this.observers) {
                obs.isIntendedRouteFilterInUseChanged(isFilterInUse);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

}
