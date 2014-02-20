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
package dk.dma.epd.shore.gui.notification;

import java.awt.Window;

import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.service.RouteSuggestionHandler;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.service.RouteSuggestionHandler.RouteSuggestionListener;
import dk.dma.epd.shore.service.StrategicRouteHandler.StrategicRouteListener;

/**
 * Shore-specific notification center implementation
 */
public class NotificationCenter extends NotificationCenterCommon implements 
    RouteSuggestionListener, 
    StrategicRouteListener {

    private static final long serialVersionUID = 1L;
    
    private RouteSuggestionHandler routeSuggestionHandler;
    private StrategicRouteHandler strategicRouteHandler;
    
    private StrategicRouteNotificationPanel strategicRoutePanel;
    private RouteSuggestionNotificationPanel routeSuggestionPanel;
    
    /**
     * Constructor
     * 
     * @param window the parent window
     */
    public NotificationCenter(Window window) {
        super(window);
        
        routeSuggestionHandler = EPDShore.getInstance().getRouteSuggestionHandler();
        routeSuggestionHandler.addRouteSuggestionListener(this);
        strategicRouteHandler = EPDShore.getInstance().getStrategicRouteHandler();
        strategicRouteHandler.addStrategicRouteListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPanels() {
        super.registerPanels();
        
        // Add the shore specific panels
        routeSuggestionPanel = new RouteSuggestionNotificationPanel();
        strategicRoutePanel = new StrategicRouteNotificationPanel();
        panels.add(routeSuggestionPanel);
        panels.add(strategicRoutePanel);
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

