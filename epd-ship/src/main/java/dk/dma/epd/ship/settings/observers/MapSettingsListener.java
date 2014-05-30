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
package dk.dma.epd.ship.settings.observers;

import dk.dma.epd.common.prototype.settings.observers.MapCommonSettingsListener;
import dk.dma.epd.ship.settings.gui.MapSettings;

/**
 * Interface for observing a {@link MapSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface MapSettingsListener extends MapCommonSettingsListener {
    
    /**
     * Invoked when {@link MapSettings#getAutoFollowPctOffTollerance()} has
     * changed.
     * 
     * @param newAutoFollowPctOffTolerance
     */
    void autoFollowPctOffToleranceChanged(int newAutoFollowPctOffTolerance);

    /**
     * Invoked when {@link MapSettings#isLookAhead()} has changed.
     * 
     * @param newLookAhead
     */
    void lookAheadChanged(boolean newLookAhead);

    /**
     * Invoked when {@link MapSettings#isAutoFollow()} has changed.
     * 
     * @param newAutoFollow
     */
    void autoFollowChanged(boolean newAutoFollow);
    
}
