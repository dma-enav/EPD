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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.prototype.notification.NotificationType;

import javax.swing.JMenuItem;


public class MsiNmDetails extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    
    private MsiNmNotification message;

    /**
     * Constructor
     * @param text
     */
    public MsiNmDetails(String text) {
        super();
        setText(text);
    }

    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        EPD.getInstance().getNotificationCenter().openNotification(NotificationType.MSI_NM, message.getId(), false);
    }

    /**
     * Sets the MSI message
     * @param message the MSI message
     */
    public void setMsiNmMessage(MsiNmNotification message) {
        this.message = message;
    }
}
