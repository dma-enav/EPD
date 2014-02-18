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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Clears the map for:
 * <ul>
 *  <li>Past-tracks</li>
 *  <li>Intended routes</li>
 *  <li>Labels</li>
 * </ul>
 */
public class GeneralClearMap extends JMenuItem implements IMapMenuAction {
    
    private static final long serialVersionUID = 1L;    

    IMapMenuAction[] mapMenuActions;
    
    /**
     * Constructor
     * @param text the name of the menu item
     */
    public GeneralClearMap(String text) {
        super();
        this.setText(text);
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        if (mapMenuActions != null) {
            for (IMapMenuAction action : mapMenuActions) {
                action.doAction();
            }
        }
    }
    
    /**
     * Sets the list of menu actions to execute
     * @param mapMenuActions the 
     */
    public void setMapMenuActions(IMapMenuAction... mapMenuActions) {
        this.mapMenuActions = mapMenuActions;
    }
}
