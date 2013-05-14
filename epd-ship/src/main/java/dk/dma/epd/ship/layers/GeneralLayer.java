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
package dk.dma.epd.ship.layers;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.MapMenu;

/**
 * General layer for handling mouse right click
 */
public class GeneralLayer extends OMGraphicHandlerLayer implements
        MapMouseListener {

    private static final long serialVersionUID = 1L;

    private MapMenu mapMenu;

    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent arg0) {
        if (arg0.getButton() == MouseEvent.BUTTON3) {
            mapMenu.generalMenu(true);
            mapMenu.setVisible(true);

            if (EPDShip.getMainFrame().getHeight() < arg0.getYOnScreen()
                    + mapMenu.getHeight()) {
                mapMenu.show(this, arg0.getX() - 2,
                        arg0.getY() - mapMenu.getHeight());
            } else {
                mapMenu.show(this, arg0.getX() - 2, arg0.getY() - 2);
            }

            return true;
        }

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

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MapMenu) {
            mapMenu = (MapMenu) obj;
        }
    }

}
