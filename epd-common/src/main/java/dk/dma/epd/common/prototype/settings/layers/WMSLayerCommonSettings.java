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

import dk.dma.epd.common.prototype.settings.observers.WMSLayerCommonSettingsListener;


/**
 * This class is used to maintain settings for a WMS layer.
 * 
 * @author Janus Varmarken
 */
public class WMSLayerCommonSettings<OBSERVER extends WMSLayerCommonSettingsListener>
        extends LayerSettings<OBSERVER> {

    /**
     * Setting specifying if the WMS layer should be loaded on startup. Default
     * is false.
     */
    private boolean useWms;

    /**
     * Setting specifying the WMS base query.
     */
    private String wmsQuery = "";

    /**
     * Get the setting that specifies if the WMS layer should be loaded on
     * startup.
     * 
     * @return {@code true} if the WMS layer should be loaded on startup,
     *         {@code false} if it shouldn't.
     */
    public boolean isUseWms() {
        try {
            this.settingLock.readLock().lock();
            return this.useWms;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the WMS layer should be loaded on
     * startup.
     * 
     * @param useWms
     *            {@code true} if the WMS layer should be loaded on startup,
     *            {@code false} if it shouldn't.
     */
    public void setUseWms(final boolean useWms) {
        try {
            this.settingLock.writeLock().lock();
            if(this.useWms == useWms) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.useWms = useWms;
            for(OBSERVER obs : this.observers) {
                obs.isUseWmsChanged(useWms);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the WMS query.
     * 
     * @return The WMS query.
     */
    public String getWmsQuery() {
        try {
            this.settingLock.readLock().lock();
            return this.wmsQuery;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the WMS query.
     * 
     * @param wmsQuery
     *            The new WMS query to use.
     */
    public void setWmsQuery(String wmsQuery) {
        try {
            this.settingLock.writeLock().lock();
            if(this.wmsQuery.equals(wmsQuery)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.wmsQuery = wmsQuery;
            for(OBSERVER obs : this.observers) {
                obs.wmsQueryChanged(wmsQuery);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    @Override
    public WMSLayerCommonSettings<OBSERVER> copy() {
        // TODO Auto-generated method stub
        return (WMSLayerCommonSettings<OBSERVER>) super.copy();
    }
    
}
