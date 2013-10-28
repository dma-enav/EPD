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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.shore.EPDShore;

/**
 * @author Janus Varmarken
 */
public class VoyageDeleteMenuItem extends JMenuItem implements IMapMenuAction {

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
        EPDShore.getVoyageManager().deleteVoyage(this.voyageId);
    }

    /**
     * Set the Voyage ID associated with this menu item (i.e. what Voyage will
     * be deleted when this menu item's doAction is invoked).
     * 
     * @param voyageId
     *            ID of the voyage that this menu item will delete when invoked.
     */
    public void setVoyageId(long voyageId) {
        this.voyageId = voyageId;
    }
}
