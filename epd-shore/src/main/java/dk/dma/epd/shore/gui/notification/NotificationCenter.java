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
package dk.dma.epd.shore.gui.notification;

import java.awt.Window;

import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.notification.Notification;

/**
 * Shore-specific notification center implementation
 */
public class NotificationCenter extends NotificationCenterCommon {

    private static final long serialVersionUID = 1L;
    
    private StrategicRouteNotificationPanel strategicRoutePanel;
    private RouteSuggestionNotificationPanel routeSuggestionPanel;
    
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
        
        // Add the shore specific panels
        routeSuggestionPanel = new RouteSuggestionNotificationPanel(this);
        strategicRoutePanel = new StrategicRouteNotificationPanel(this);
        panels.add(routeSuggestionPanel);
        panels.add(strategicRoutePanel);
    }

    /**
     * Adds a notification of the given type.
     * 
     * @param notification the notification to add
     */
    @Override
    public void addNotification(Notification<?, ?> notification) {
        if (notification instanceof RouteSuggestionNotification) {
            routeSuggestionPanel.addNotification((RouteSuggestionNotification)notification);
        } else if (notification instanceof StrategicRouteNotification) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void routeUpdate() {
        routeSuggestionPanel.refreshNotifications();
    }
}

