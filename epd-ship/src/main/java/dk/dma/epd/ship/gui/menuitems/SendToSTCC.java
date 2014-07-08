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
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.ship.EPDShip;

public class SendToSTCC extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    private Route route;
    private Long transactionId;


    public SendToSTCC(String text) {
        super();
        this.setText(text);
    }

    @Override
    public void doAction() {
        // Check if a transaction is ongoing
        if (transactionId != null) {
            // If so, show the strategic route notification
            EPDShip.getInstance().getNotificationCenter()
                .openNotification(NotificationType.STRATEGIC_ROUTE, transactionId, false);
        } else {
            // No transaction, show the SendStrategicRouteDialog
            EPDShip.getInstance().getMainFrame().getSendStrategicRouteDialog().setSelectedRoute(route);
            EPDShip.getInstance().getMainFrame().getSendStrategicRouteDialog().setVisible(true);
        }
    }
    

    public void setRoute(Route route) {
        this.route = route;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
