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
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;

import javax.swing.JMenuItem;

public class MsiNmAcknowledge extends JMenuItem implements IMapMenuAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private MsiNmServiceHandlerCommon msiNmHandler;
    private MsiNmNotification msiMessage;

    public MsiNmAcknowledge(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        msiMessage.setAcknowledged(true);
        EPD.getInstance().getMsiNmHandler().doUpdate();
    }

    public void setMsiNmHandler(MsiNmServiceHandlerCommon msiNmHandler) {
        this.msiNmHandler = msiNmHandler;
    }

    public void setMsiNmMessage(MsiNmNotification msiMessage) {
        this.msiMessage = msiMessage;
        
    }
}
