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

import javax.swing.JPanel;

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
     * Returns a reference to the main frame
     * @return a reference to the main frame
     */
    @Override
    public MainFrame getMainFrame() {
        return (MainFrame)mainFrame;
    }

    /**
     * Returns a reference to the map menu
     * @return a reference to the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu)mapMenu;
    }   

    /**
     * Returns a reference to the glass pane
     * @return a reference to the glass pane
     */
    public JPanel getGlassPanel() {
        return getMainFrame().getGlassPanel();
    }    
}
