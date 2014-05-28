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
package dk.dma.epd.shore.settings;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * Maintains shore identity settings such as shore ID and position of the shore
 * station.
 * 
 * @author Janus Varmarken
 */
public class IdentitySettings extends
        ObservedSettings<IdentitySettings.IObserver> {

    /**
     * ID of the shore station.
     */
    private String shoreId = MaritimeCloudUtils.STCC_MMSI_PREFIX
            + System.currentTimeMillis();

    /**
     * Position of the shore station. Default is somewhere around Helsingør.
     */
    private LatLonPoint shorePos = new LatLonPoint.Double(56.02, 12.36);

    /**
     * Gets the setting that specifies the ID of the shore station.
     * 
     * @return The ID of the shore station.
     */
    public String getShoreId() {
        try {
            this.settingLock.readLock().lock();
            return this.shoreId;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the ID of the shore station.
     * 
     * @param shoreId
     *            The ID of the shore station.
     */
    public void setShoreId(final String shoreId) {
        try {
            this.settingLock.writeLock().lock();
            if (this.shoreId.equals(shoreId)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.shoreId = shoreId;
            for (IObserver obs : this.observers) {
                obs.shoreIdChanged(shoreId);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the location of the shore station.
     * 
     * @return The location of the shore station.
     */
    public LatLonPoint getShorePos() {
        try {
            this.settingLock.readLock().lock();
            return this.shorePos;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the location of the shore station.
     * 
     * @param shorePos
     *            The new location of the shore station.
     */
    public void setShorePos(final LatLonPoint shorePos) {
        try {
            this.settingLock.writeLock().lock();
            if (this.shorePos.equals(shorePos)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            // Make a copy to avoid reference leak.
            LatLonPoint copy = new LatLonPoint.Double(shorePos.getLatitude(),
                    shorePos.getLongitude());
            this.shorePos = copy;
            for (IObserver obs : this.observers) {
                /*
                 * Make a copy for each observer. This is to prevent one
                 * observer from affecting the other observers in case the
                 * observer chooses to mutate the LatLonPoint argument it is
                 * invoked with.
                 */
                copy = new LatLonPoint.Double(shorePos.getLatitude(),
                        shorePos.getLongitude());
                obs.shorePosChanged(copy);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing an {@link IdentitySettings} for changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver {

        /**
         * Invoked when {@link IdentitySettings#getShoreId()} has changed.
         * 
         * @param shoreId
         *            The new shore ID.
         */
        void shoreIdChanged(String shoreId);

        /**
         * Invoked when {@link IdentitySettings#getShorePos()} has changed.
         * 
         * @param shorePos
         *            The new shore position.
         */
        void shorePosChanged(LatLonPoint shorePos);
    }
}
