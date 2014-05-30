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
package dk.dma.epd.common.prototype.settings.observers;

import dk.dma.epd.common.prototype.settings.layers.RouteLayerCommonSettings;

/**
 * Interface for observing a {@link RouteLayerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface RouteLayerCommonSettingsListener extends LayerSettingsListener {

    /**
     * Invoked when {@link RouteLayerCommonSettings#getShowArrowScale()} has
     * changed.
     * 
     * @param maxScaleForArrowDisplay
     *            The updated value. Refer to
     *            {@link RouteLayerCommonSettings#getShowArrowScale()} for its
     *            interpretation.
     */
    void showArrowScaleChanged(float maxScaleForArrowDisplay);

    /**
     * Invoked when {@link RouteLayerCommonSettings#getRouteWidth()} has
     * changed.
     * 
     * @param routeWidth
     *            The updated value. Refer to
     *            {@link RouteLayerCommonSettings#getRouteWidth()} for its
     *            interpretation.
     */
    void routeWidthChanged(float routeWidth);
}
