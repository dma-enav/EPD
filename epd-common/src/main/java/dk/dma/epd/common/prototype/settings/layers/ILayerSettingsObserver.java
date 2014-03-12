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

import dk.dma.epd.common.prototype.settings.ISettingsObserver;

/**
 * Base interface used to observe a {@link LayerSettings} for changes. I.e.
 * <i>this interface should only contain callbacks for changes to settings that
 * are relevant to all layer types.</i>
 * 
 * @author Janus Varmarken
 */
public interface ILayerSettingsObserver extends ISettingsObserver {
    /*
     * Specify setting-changed callbacks that are relevant to all layer types
     * here.
     */

    /**
     * Invoked when the visibility of a layer is toggled on/off.
     * 
     * @param newValue
     *            {@code true} if the layer should now be visible, {@code false}
     *            if the layer should now be invisible.
     */
    void isVisibleChanged(boolean newValue);

    /**
     * Invoked when the graphic interact tolerance setting has been changed.
     * 
     * @param newValue
     *            The new tolerance level in pixels.
     */
    void graphicInteractToleranceChanged(float newValue);
}
