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
package dk.dma.epd.ship.layers.background;

import java.awt.Graphics;

import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

/**
 * Layer for simple coastal outline background
 */
public class CoastalOutlineLayer extends ShapeLayer {
    
    private static final long serialVersionUID = 1L;
    
    public CoastalOutlineLayer() {
        super();
    }
    
    public void forceRedraw() {
        doPrepare();
    }
    
    @Override
    public synchronized OMGraphicList prepare() {
        if (!isVisible()) {
            return null;
        }
        //long start = System.nanoTime();
        OMGraphicList list = super.prepare();
        //System.out.println("Finished CoastalOutlineLayer.prepare() in " + EeINS.elapsed(start) + " ms\n---");
        return list;
    }
    
    @Override
    public void paint(Graphics g) {
        //long start = System.nanoTime();
        super.paint(g);
        //System.out.println("Finished CoastalOutlineLayer.paint() in " + EeINS.elapsed(start) + " ms\n---");
    }
    
}
