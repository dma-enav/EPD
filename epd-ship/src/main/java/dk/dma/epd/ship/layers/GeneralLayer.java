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
package dk.dma.epd.ship.layers;

import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.MapMenu;

/**
 * General layer for EPDShip that may be sub-classed by other layers.
 */
public class GeneralLayer extends EPDLayerCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Provides default behavior for right-clicks by
     * showing the general menu.
     * 
     * @param evt the mouse event
     */
    @Override
    public boolean mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            mapMenu.generalMenu(true);
            mapMenu.setVisible(true);

            if (mainFrame.getHeight() < evt.getYOnScreen() + mapMenu.getHeight()) {
                mapMenu.show(this, evt.getX() - 2, evt.getY() - mapMenu.getHeight());
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
}
