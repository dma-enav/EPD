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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.layers.GeneralLayerCommon;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.MapMenu;

/**
 * General layer that may be sub-classed by other layers.
 * <p>
 * Contains default functionality for handling mouse right click
 */
public class GeneralLayer extends GeneralLayerCommon {

    private static final long serialVersionUID = 1L;

    protected MapMenu mapMenu;

    /**
     * Returns the mouse mode service list
     * @return the mouse mode service list
     */
    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
    }

    /**
     * Provides default behavior for right-clicks by
     * showing the general menu.
     * @param evt the mouse event
     */
    @Override
    public boolean mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            mapMenu.generalMenu(true);
            mapMenu.setVisible(true);

            if (EPD.getInstance().getMainFrame().getHeight() < evt.getYOnScreen()
                    + mapMenu.getHeight()) {
                mapMenu.show(this, evt.getX() - 2,
                        evt.getY() - mapMenu.getHeight());
            } else {
                mapMenu.show(this, evt.getX() - 2, evt.getY() - 2);
            }

            return true;
        }

        return false;
    }

    /**
     * Called when a bean is added to the bean context
     * @param obj the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MapMenu) {
            mapMenu = (MapMenu) obj;
        }
    }

    /**
     * Called when a bean is removed from the bean context
     * @param obj the bean being removed
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj == mapMenu) {
            mapMenu = null;
        }
        super.findAndUndo(obj);
    }
    
    /**
     * Returns a reference to the main frame
     * @return a reference to the main frame
     */
    public MainFrame getMainFrame() {
        return (MainFrame)mainFrame;
    }
}
