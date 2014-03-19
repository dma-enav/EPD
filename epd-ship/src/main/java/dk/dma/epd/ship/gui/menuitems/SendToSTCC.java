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
