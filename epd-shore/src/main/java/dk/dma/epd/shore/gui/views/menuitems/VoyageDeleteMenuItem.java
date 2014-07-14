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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.shore.EPDShore;

/**
 * MenuItem that allows deletion of saved a Voyage (a Voyage that has been accepted by a ship).
 * @author Janus Varmarken
 */
public class VoyageDeleteMenuItem extends JMenuItem implements IMapMenuAction {

    /**
     * ID of Voyage to delete.
     */
    private long voyageId = -1L;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public VoyageDeleteMenuItem(String menuItemText) {
        super(menuItemText);
    }

    @Override
    public void doAction() {
        // Delete voyage from VoyageManager.
        EPDShore.getInstance().getVoyageManager().deleteVoyage(this.voyageId);
    }

    /**
     * Set the Voyage ID associated with this menu item (i.e. what Voyage will
     * be deleted when this menuy item's doAction is invoked).
     * 
     * @param voyageId
     *            ID of the voyage that this menu item will delete when invoked.
     */
    public void setVoyageId(long voyageId) {
        this.voyageId = voyageId;
    }
}
