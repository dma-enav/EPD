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
package dk.dma.epd.common.prototype.layers;

import java.awt.event.ActionListener;

import javax.swing.Timer;

import dk.dma.epd.common.prototype.settings.layers.LayerSettings;

/**
 * Layer that provides a timer that is invoked at a given interval. Subclasses
 * should implement the {@code actionPerformed(ActionEvent e)} method to respond
 * to this event.
 * 
 * This class is primarily intended for layers that wish to redraw themselves
 * repeatedly at a given interval. As the {@code actionPerformed(ActionEvent e)}
 * method is invoked on the Java Event Dispatch Thread, make sure that your
 * subclass implementation of this method does not perform a long task.
 * 
 * SupressWarnings("serial") is used as a layer should never be serialized.
 * 
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public abstract class LazyLayerCommon extends EPDLayerCommon implements
        ActionListener {

    /**
     * The timer that fires events (e.g. signaling that a repaint should occur).
     * Subclasses should implement the actionPerformed method to respond to this
     * event.
     */
    private Timer repaintTimer;

    /**
     * Creates a {@code LazyLayerCommon} that will invoke the subclass
     * implementation of {@code actionPerformed(ActionEvent e)} at the given
     * interval.
     * 
     * @param repaintIntervalMillis
     *            Milliseconds between each invocation of
     *            {@code actionPerformed(ActionEvent e)}
     */
    public LazyLayerCommon(int repaintIntervalMillis, LayerSettings<?> settings) {
        super(settings);
        this.repaintTimer = new Timer(repaintIntervalMillis, this);
        this.repaintTimer.setRepeats(true);
        this.repaintTimer.start();
    }

    /**
     * Updates the delay between each invocation of
     * {@link #actionPerformed(java.awt.event.ActionEvent)}.
     * 
     * @param repaintIntervalMillis
     *            The new delay in milliseconds.
     */
    public void setRepaintInterval(int repaintIntervalMillis) {
        synchronized (this.repaintTimer) {
            this.repaintTimer.setDelay(repaintIntervalMillis);
        }
    }
}
