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

import dk.dma.epd.common.prototype.layers.route.RouteLayerCommon;
import dk.dma.epd.common.prototype.settings.observers.RouteLayerCommonSettingsListener;

/**
 * This class is used to maintain settings for a {@link RouteLayerCommon}.
 * 
 * @author Janus Varmarken
 */
public class RouteLayerCommonSettings<OBSERVER extends RouteLayerCommonSettingsListener>
        extends LayerSettings<OBSERVER> implements RouteLayerCommonSettingsListener {

    /**
     * Setting specifying at what scale the directional arrows should be shown
     * for a route. The arrows should be displayed if the current map scale is
     * between 0 and this value.
     */
    private float showArrowScale = 450000.0f;

    /**
     * Setting specifying the visual width of a route leg.
     */
    private float routeWidth = 2.0f;
    
    @Override
    public RouteLayerCommonSettings<OBSERVER> copy() {
        return (RouteLayerCommonSettings<OBSERVER>) super.copy();
    }
    
    /**
     * Gets the setting that specifies at what scale the directional arrows
     * should be shown for a route. The arrows should be displayed if the
     * current map scale is between 0 and this value.
     * 
     * @return The upper bound on scale for display of directional arrows.
     */
    public float getShowArrowScale() {
        try {
            this.settingLock.readLock().lock();
            return this.showArrowScale;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies at what scale the directional arrows
     * should be shown for a route. The arrows should be displayed if the
     * current map scale is between 0 and the given value.
     * 
     * @param maxScaleForArrowDisplay
     *            The upper bound on scale for display of directional arrows.
     */
    public void setShowArrowScale(final float maxScaleForArrowDisplay) {
        try {
            this.settingLock.writeLock().lock();
            if (this.showArrowScale == maxScaleForArrowDisplay) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.showArrowScale = maxScaleForArrowDisplay;
            for (OBSERVER obs : this.observers) {
                obs.showArrowScaleChanged(maxScaleForArrowDisplay);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting that specifies the visual width of a route leg.
     * 
     * @return The visual width of a route leg.
     */
    public float getRouteWidth() {
        try {
            this.settingLock.readLock().lock();
            return this.routeWidth;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the visual width of a route leg.
     * 
     * @param routeWidth
     *            The visual width of a route leg.
     */
    public void setRouteWidth(final float routeWidth) {
        try {
            this.settingLock.writeLock().lock();
            if (this.routeWidth == routeWidth) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.routeWidth = routeWidth;
            for (OBSERVER obs : this.observers) {
                obs.routeWidthChanged(routeWidth);
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
    public void showArrowScaleChanged(float maxScaleForArrowDisplay) {
        // Obey to change in observed instance.
        this.setShowArrowScale(maxScaleForArrowDisplay);
    }

    @Override
    public void routeWidthChanged(float routeWidth) {
        // Obey to change in observed instance.
        this.setRouteWidth(routeWidth);
    }
    
    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */
}
