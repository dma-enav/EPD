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
    public void strategicRouteUpdate() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void routeUpdate() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof RouteSuggestionHandler && routeSuggestionHandler != null) {
            routeSuggestionHandler = (RouteSuggestionHandler) obj;
            routeSuggestionHandler.addRouteSuggestionListener(this);
        } else if (obj instanceof StrategicRouteHandler && strategicRouteHandler != null){
            strategicRouteHandler = (StrategicRouteHandler) obj;
            strategicRouteHandler.addStrategicRouteListener(this);
        }
    }    
}

