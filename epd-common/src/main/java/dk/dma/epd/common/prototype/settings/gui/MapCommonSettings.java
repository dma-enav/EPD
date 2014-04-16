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
package dk.dma.epd.common.prototype.settings.gui;

import java.io.IOException;
import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * This class is used to maintain map settings (e.g. what the map scale should
 * be when the application is launched).
 * 
 * @author Janus Varmarken
 */
public class MapCommonSettings<OBSERVER extends IMapCommonSettingsObserver>
        extends ObservedSettings<OBSERVER> {

    /**
     * Specifies a (Latitude, Longitude) point that the map should be centered
     * around when the application is launched.
     */
    private LatLonPoint center = new LatLonPoint.Double(56, 11);

    /**
     * Specifies the scale of the map when the application is launched.
     */
    private float initialMapScale = 10000000f;

    /**
     * The lowest possible map scale (i.e. it defines the highest possible level
     * of zoom). TODO: Consider changing to float as OpenMap uses float for
     * scale.
     */
    private int minMapScale = 5000;

    /**
     * Get the point that the map should be centered around when the application
     * is launched.
     * 
     * @return The point that the map should be centered around when the
     *         application is launched.
     */
    public LatLonPoint getCenter() {
        try {
            this.settingLock.readLock().lock();
            return this.center;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Set a point that the map should be centered around when the application
     * is launched.
     * 
     * @param center
     *            A point that the map should be centered around when the
     *            application is launched.
     * 
     */
    public void setCenter(LatLonPoint center) {
        try {
            this.settingLock.writeLock().lock();
            if (this.center.equals(center)) {
                // No change, no need to notify observers.
                return;
            }
            /*
             * Create a copy in order to ensure thread safe access (avoid
             * reference leak).
             */
            LatLonPoint copy = null;
            if (center instanceof LatLonPoint.Double) {
                copy = new LatLonPoint.Double(center);
            } else if (center instanceof LatLonPoint.Float) {
                copy = new LatLonPoint.Float(center);
            } else {
                return;
            }
            this.center = copy;
            for (OBSERVER obs : this.observers) {
                // Feed each observer with its own instance.
                LatLonPoint obsCopy = center instanceof LatLonPoint.Double ? new LatLonPoint.Double(
                        center) : new LatLonPoint.Float(center);
                obs.mapCenterChanged(obsCopy);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get at what scale the map should be initialized when the application is
     * launched.
     * 
     * @return At what scale the map should be initialized when the application
     *         is launched.
     */
    public float getInitalMapScale() {
        try {
            this.settingLock.readLock().lock();
            return this.initialMapScale;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Set at what scale the map should be initialized when the application is
     * launched.
     * 
     * @param scale
     *            The updated value for the map scale on application launch.
     */
    public void setInitialMapScale(final float scale) {
        try {
            this.settingLock.writeLock().lock();
            if (this.initialMapScale == scale) {
                // No change, no need to notify observers.
                return;
            }
            this.initialMapScale = scale;
            for (OBSERVER obs : this.observers) {
                obs.initialMapScaleChanged(scale);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the lowest possible map scale (i.e. the highest level of zoom).
     * 
     * @return The lowest possible map scale (i.e. the highest level of zoom).
     */
    public int getMinMapScale() {
        try {
            this.settingLock.readLock().lock();
            return this.minMapScale;
        } finally {
            this.settingLock.readLock().unlock();
        }

    }

    /**
     * Set the lowest possible map scale (i.e. the highest level of zoom).
     * 
     * @param minScale
     *            The new value for the lowest possible map scale.
     */
    public void setMinMapScale(final int minScale) {
        try {
            this.settingLock.writeLock().lock();
            if (this.minMapScale == minScale) {
                // No change, no need to notify observers.
                return;
            }
            this.minMapScale = minScale;
            for (OBSERVER obs : this.observers) {
                obs.minimumMapScaleChanged(minScale);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}
