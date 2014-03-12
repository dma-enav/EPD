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
package dk.dma.epd.common.prototype.settings.handlers;

import java.util.Properties;

import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * Maintains settings relevant to an {@link IntendedRouteHandlerCommon} or any of its subclasses. This class inherits from
 * {@link ObservedSettings} allowing clients to register for notifications of changes to any setting maintained by this class.
 */
public abstract class IntendedRouteHandlerCommonSettings<OBSERVER extends IIntendedRouteHandlerCommonSettingsObserver> extends
        HandlerSettings<OBSERVER> {

    private long routeTimeToLive = 10 * 60 * 1000; // 10 minutes.
    private double filterDistance = 0.5;
    private double notificationDistance = 0.5; // Nautical miles.
    private double alertDistance = 0.3; // Nautical miles.
    
    @Override
    protected void onLoadSuccess(Properties settings) {
        // TODO init settings variables based on the provided Properties instance.
    }

    @Override
    protected Properties onSaveSettings() {
        Properties savedVars = new Properties();
        // TODO store instance fields in savedVars
        return savedVars;
    }
}
