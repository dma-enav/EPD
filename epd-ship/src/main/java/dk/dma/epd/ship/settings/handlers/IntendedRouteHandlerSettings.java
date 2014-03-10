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
package dk.dma.epd.ship.settings.handlers;

import java.io.IOException;

import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.ship.service.IntendedRouteHandler;

/**
 * <p>
 * An instance of this class maintains settings specifically for {@link IntendedRouteHandler} instance(s), but it still obeys to
 * changes to settings that are considered global to all instances of {@link IntendedRouteHandlerCommon}.
 * </p>
 * 
 */
public class IntendedRouteHandlerSettings<OBSERVER extends IIntendedRouteHandlerSettingsObserver> extends
        IntendedRouteHandlerCommonSettings<OBSERVER> {

    /**
     * Specifies if the ship should broadcast it's intendedroute or not
     */
    private boolean broadcastIntendedRoute = true;
    
    
    /**
     * Specifies the intended route broadcast time
     */
    private int timeBetweenBroadCast = 1;
    
    
    
    private int adaptionTime = 1;

    @Override
    protected void onLoadFailure(IOException error) {
        // TODO figure out what to do with read error.
    }

    @Override
    protected void onSaveFailure(IOException error) {
        // TODO possibly log save error or simply ignore it.
    }
}
