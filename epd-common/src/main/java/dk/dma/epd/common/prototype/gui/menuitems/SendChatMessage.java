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

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.notification.NotificationType;

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
            EPD.getInstance().getNotificationCenter().openNotification(NotificationType.MESSAGES, id, false);
        }
    }
    
    /**
     * Checks if the current AIS target accepts 
     * chat messages. Enables the menu item accordingly
     * @return if the vessel can receive chat messages
     */
    public boolean checkEnabled() {
        boolean enabled = vesselTarget != null &&
            EPD.getInstance().getChatServiceHandler().availableForChat((int)vesselTarget.getMmsi());
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
