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

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapEventUtils;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.EPD;

/**
 * Common EPD layer subclass that may be sub-classed by other layers.
 */
public abstract class GeneralLayerCommon extends OMGraphicHandlerLayer implements MapMouseListener {

    private static final long serialVersionUID = 1L;

    protected OMGraphicList graphics = new OMGraphicList();

    /**
     * Returns {@code this} as the {@linkplain MapMouseListener}
     * @return this
     */
    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public boolean mouseClicked(MouseEvent evt) {
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent arg0) {
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved() {
    }

    @Override
    public boolean mouseMoved(MouseEvent arg0) {
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent arg0) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent arg0) {
        return false;
    }

    /**
     * Returns the mouse selection tolerance
     * @return the mouse selection tolerance
     */
    public float getMouseSelectTolerance() {
        return EPD.getInstance().getSettings().getGuiSettings().getMouseSelectTolerance();
    }
    
    /**
     * Returns the first graphics element placed at the mouse event location
     * that matches any of the types passed along. 
     * 
     * @param evt the mouse event
     * @param types the possible types
     * @return the first matching graphics element
     */
    public final OMGraphic getSelectedGraphic(MouseEvent evt, Class<?>... types) {
        return MapEventUtils.getSelectedGraphic(graphics, evt, getMouseSelectTolerance(), types);
    }
}
