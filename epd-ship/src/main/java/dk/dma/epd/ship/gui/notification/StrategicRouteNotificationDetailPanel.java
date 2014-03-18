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
package dk.dma.epd.ship.gui.notification;

import static dk.dma.epd.common.graphics.GraphicsUtil.bold;
import static dk.dma.epd.common.graphics.GraphicsUtil.minSize;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.notification.StrategicRouteNotificationDetailPanelCommon;

/**
 * Ship-specific panel that displays relevant strategic detail information 
 * for the selected route in the notification center.
 */
public class StrategicRouteNotificationDetailPanel extends StrategicRouteNotificationDetailPanelCommon<StrategicRouteNotification> {

    private static final long serialVersionUID = 1L;
    
    JLabel mmsiTxt;
    
    /**
     * Constructor
     */
    public StrategicRouteNotificationDetailPanel() {
        super();
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel createInfoPanel() {
        mmsiTxt = new JLabel(" ");        
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        Insets insets2  = new Insets(2, 5, 2, 5);

        infoPanel.setBorder(BorderFactory.createTitledBorder("STCC Info"));
        infoPanel.add(bold(new JLabel("Name:")), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        infoPanel.add(minSize(mmsiTxt, 40), 
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
        
        return infoPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(StrategicRouteNotification notification) {
        super.setNotification(notification);

        // If no notification is set, reset all fields
        if (notification == null) {
            mmsiTxt.setText("");
        } else {
            mmsiTxt.setText(notification.getCallerlName());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMessageViewTitle(StrategicRouteMessage routeMessage) {
        return routeMessage.isFromStcc() ? "Received from STCC" : "Sent to STCC";
    }
}
