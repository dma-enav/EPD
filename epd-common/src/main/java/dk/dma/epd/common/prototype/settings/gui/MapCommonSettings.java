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
     * Key used in the properties file to store the latitude of the center of
     * the map (see {@link #center}).
     */
    private static final String KEY_CENTER_LAT = "center_lat";

    /**
     * Key used in the properties file to store the longitude of the center of
     * the map (see {@link #center}).
     */
    private static final String KEY_CENTER_LON = "center_lon";

    /**
     * Key used in the properties file to store the initial map scale (see
     * {@link #initialMapScale}).
     */
    private static final String KEY_INITIAL_MAP_SCALE = "initialMapScale";

    /**
     * Key used in the properties file to store the minimum map scale (see
     * {@link #minMapScale}).
     */
    private static final String KEY_MIN_MAP_SCALE = "minimumMapScale";

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

    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE: This is a concrete implementation. Any subclass should make sure
     * to invoke the super implementation.</b>
     * </p>
     */
    @Override
    protected void onLoadSuccess(Properties settings) {
        // Acquire lock in order for the settings to load in a single batch.
        this.settingLock.writeLock().lock();
        // Read the initial center of the map.
        double centerLat = PropUtils.doubleFromProperties(settings,
                KEY_CENTER_LAT, this.center.getLatitude());
        double centerLon = PropUtils.doubleFromProperties(settings,
                KEY_CENTER_LON, this.center.getLongitude());
        this.setCenter(new LatLonPoint.Double(centerLat, centerLon));
        // Read the initial map scale.
        this.setInitialMapScale(PropUtils.floatFromProperties(settings,
                KEY_INITIAL_MAP_SCALE, this.initialMapScale));
        // Read the minimum map scale.
        this.setMinMapScale(PropUtils.intFromProperties(settings,
                KEY_MIN_MAP_SCALE, this.minMapScale));
        // Release the batch lock.
        this.settingLock.writeLock().unlock();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>NOTE: This is a concrete implementation. Any subclass should make sure
     * to invoke the super implementation, add its own settings to the
     * {@link Properties} instance returned by the super call and finally return
     * that instance.</b>
     * </p>
     */
    @Override
    protected Properties onSaveSettings() {
        this.settingLock.readLock().lock();
        Properties toSave = new Properties();
        // Store map center latitude.
        toSave.setProperty(KEY_CENTER_LAT,
                Double.toString(this.center.getLatitude()));
        // Store map center longitude.
        toSave.setProperty(KEY_CENTER_LON,
                Double.toString(this.center.getLongitude()));
        // Store initial map scale.
        toSave.setProperty(KEY_INITIAL_MAP_SCALE,
                Float.toString(this.initialMapScale));
        // Store minimum map scale.
        toSave.setProperty(KEY_MIN_MAP_SCALE,
                Integer.toString(this.minMapScale));
        this.settingLock.readLock().unlock();
        return toSave;
    }
}
