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
