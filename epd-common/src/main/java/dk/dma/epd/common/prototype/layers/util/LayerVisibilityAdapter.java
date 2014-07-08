/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
