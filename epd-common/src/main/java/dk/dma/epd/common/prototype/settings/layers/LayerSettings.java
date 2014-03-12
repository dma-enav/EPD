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

import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * <p>
 * A base class for maintaining settings that apply to an individual layer. I.e.
 * this class should be used as an abstract base class when writing classes that
 * store settings that are specifically targeted at a given type of layer.
 * </p>
 * <p>
 * If you discover a setting that is relevant to <b>all</b> layer types, you
 * should place that setting in this class.
 * </p>
 * <p>
 * This class inherits from {@link ObservedSettings} which allows clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * </p>
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the {@code LayerSettings} for
 *            changes.
 * @author Janus Varmarken
 */
public abstract class LayerSettings<OBSERVER extends ILayerSettingsObserver>
        extends ObservedSettings<OBSERVER> {
    /*
     * Add settings that are relevant to all layer types here.
     */
    
    /**
     * Should the layer be displayed?
     * TODO implement getters/setters
     */
    private boolean visible = true;
   
    /**
     * Maximum distance to a graphic object for the graphic object to be taken into account for mouse click/hover.
     */
    private float mouseSelectTolerance = 5.0f;
    
}
