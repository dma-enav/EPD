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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.notification.NotificationType;


public class VoyageShowTransaction extends JMenuItem implements IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private long transactionID;

    public VoyageShowTransaction(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {
        EPD.getInstance().getNotificationCenter()
            .openNotification(NotificationType.STRATEGIC_ROUTE, transactionID, false);
    }

    /**
     * @param transactionID the transactionID to set
     */
    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;
    }

    
    
}
