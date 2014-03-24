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

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.settings.ISettingsObserver;

/**
 * @author Janus Varmarken
 */
public interface IMapCommonSettingsObserver extends ISettingsObserver {

    /**
     * Invoked when the setting, specifying the center of the map on application
     * launch, has been changed.
     * 
     * @param newCenter
     *            The updated center of the map to be used on next launch of the
     *            application.
     */
    void mapCenterChanged(LatLonPoint newCenter);

    /**
     * Invoked when the setting, specifying the scale of the map on application
     * launch, has been changed.
     * 
     * @param newScale
     *            The updated scale of the map to be used on next launch of the
     *            application.
     */
    void initialMapScaleChanged(float newScale);

    /**
     * Invoked when the setting, specifying the lowest possible map scale, has
     * been changed.
     * 
     * @param newMinScale
     *            The new value for the lowest possible map scale.
     */
    void minimumMapScaleChanged(float newMinScale);

}
