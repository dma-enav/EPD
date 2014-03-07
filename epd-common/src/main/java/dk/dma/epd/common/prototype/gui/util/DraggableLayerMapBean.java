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
package dk.dma.epd.common.prototype.gui.util;

import com.bbn.openmap.BufferedLayerMapBean;

/**
 * This sub-class of the {@linkplain BufferedLayerMapBean} will
 * turn off repainting when in dragging mode.
 */
public class DraggableLayerMapBean extends BufferedLayerMapBean {

    private static final long serialVersionUID = 1L;
    
    boolean dragging;

    /**
     * Constructor
     */
    public DraggableLayerMapBean() {
        super();
    }
    
    /**
     * Returns if the map bean is in dragging mode
     * @return if the map bean is in dragging mode
     */
    public synchronized boolean isDragging() {
        return dragging;
    }

    /**
     * Flags that the map bean enters dragging mode
     */
    public synchronized void startDragging() {
        if (!dragging) {
            dragging = true;
        }
    }

    /**
     * Flags that the map bean exits dragging mode
     */
    public synchronized void stopDragging() {
        if (dragging) {
            dragging = false;
        }
    }

    /**
     * Repaint method.
     * <p>
     * Turns off repainting when the {@code dragging} flag is set.
     */
    @Override
    public void repaint() {
        if (!dragging) {
            super.repaint();
        }
    }
}
