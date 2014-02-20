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

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Sends a chat message to the selected vessel if the vessel 
 * supports chat messages
 */
public class SendChatMessage extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    
    VesselTarget vesselTarget;
    
    /**
     * Constructor
     */
    public SendChatMessage() {
        super("Send message...");
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        if (checkEnabled()) {
            MaritimeId id = new MmsiId((int)vesselTarget.getMmsi());
            EPD.getInstance().getMainFrame().getChatServiceDialog().init(id);
        }
    }
    
    /**
     * Checks if the current AIS target accepts 
     * chat messages. Enables the menu item accordingly
     * @return if the vessel can receive chat messages
     */
    public boolean checkEnabled() {
        boolean enabled = vesselTarget != null &&
            EPD.getInstance().getChatServiceHandler().shipAvailableForChatSuggestion((int)vesselTarget.getMmsi());
        setEnabled(enabled);
        return enabled;
    }
    
    /**
     * Sets the vessel target
     * @param vesselTarget the vessel target
     */
    public void setVesselTarget(VesselTarget vesselTarget) {
        this.vesselTarget = vesselTarget;
    }
}
