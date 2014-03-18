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

import java.awt.Window;

import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.notification.Notification;

/**
 * Ship-specific notification center implementation
 */
public class NotificationCenter extends NotificationCenterCommon {

    private static final long serialVersionUID = 1L;
    
    private StrategicRouteNotificationPanel strategicRoutePanel;
    
    /**
     * Constructor
     * 
     * @param window the parent window
     */
    public NotificationCenter(Window window) {
        super(window);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPanels() {
        super.registerPanels();
        
        // Add the ship specific panels
        strategicRoutePanel = new StrategicRouteNotificationPanel();
        panels.add(strategicRoutePanel);
    }    

    /**
     * Adds a notification of the given type.
     * 
     * @param notification the notification to add
     */
    @Override
    public void addNotification(Notification<?, ?> notification) {
        if (notification instanceof StrategicRouteNotification) {
            strategicRoutePanel.addNotification((StrategicRouteNotification)notification);
        } else {
            super.addNotification(notification);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void strategicRouteUpdate() {
        strategicRoutePanel.refreshNotifications();
    }
}
