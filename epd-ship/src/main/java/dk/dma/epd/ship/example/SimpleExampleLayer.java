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
package dk.dma.epd.ship.example;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.NavMouseMode;
import com.bbn.openmap.event.SelectMouseMode;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.dma.epd.ship.layers.GeneralLayer;

/**
 * Simple example layer to show how to use mouse events  
 */
public class SimpleExampleLayer extends GeneralLayer {

    private static final long serialVersionUID = 1L;

    private OMCircle circle;

    public SimpleExampleLayer() {
        circle = new OMCircle(55f, 11f, 0, 0, 18, 18);
        graphics.add(circle);
        graphics.project(getProjection(), true);
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection(), true);
        return graphics;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[1];
        ret[0] = NavMouseMode.modeID;
        ret[1] = SelectMouseMode.modeID;
        return ret; 
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 5.0f);
        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof OMCircle) {
                System.out.println("Mouse clicked on omGraphic: " + omGraphic);
                // Consumed by this
                return true;
            }            
        }
        return false;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 5.0f);
        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof OMCircle) {
                System.out.println("Mouse over omGraphic: " + omGraphic);
                // Consumed by this
                return true;
            }            
        }
        return false;
    }
}
