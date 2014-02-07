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
package dk.dma.epd.common.prototype.layers;

import java.awt.event.ActionListener;

import javax.swing.Timer;

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
    public LazyLayerCommon(int repaintIntervalMillis) {
        this.repaintTimer = new Timer(repaintIntervalMillis, this);
        this.repaintTimer.setRepeats(true);
        this.repaintTimer.start();
    }
}
