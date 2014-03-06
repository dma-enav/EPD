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
package dk.dma.epd.common.prototype.layers.util;

import java.util.concurrent.CopyOnWriteArrayList;

import com.bbn.openmap.Layer;

/**
 * Handles a list of {@linkplain LayerVisiblityListener} listeners
 */
public class LayerVisibilityAdapter {
    
    private CopyOnWriteArrayList<LayerVisiblityListener> visibilityListener = new CopyOnWriteArrayList<>();

    /**
     * Publish the change of layers visibility to all listeners
     */
    public void notifyVisibilityListeners(Layer layer) {
        for (LayerVisiblityListener listener : visibilityListener) {
            listener.visibilityChanged(layer);
        }
    }

    /**
     * Add visibility Listener
     * 
     * @param targetListener
     */
    public final void addVisibilityListener(LayerVisiblityListener targetListener) {
        visibilityListener.add(targetListener);
    }

    /**
     * Remove visibility Listener
     * 
     * @param targetListener
     */
    public final void removeVisibilityListener(LayerVisiblityListener targetListener) {
        visibilityListener.remove(targetListener);
    }
}
