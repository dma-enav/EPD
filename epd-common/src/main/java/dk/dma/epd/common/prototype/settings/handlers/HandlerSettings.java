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

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.HandlerSettingsListener;

/**
 * <p>
 * A base class for maintaining settings that apply to an individual handlers. I.e.
 * this class should be used as an abstract base class when writing classes that
 * store settings that are specifically targeted at a given type of handler.
 * </p>
 * <p>
 * If you discover a setting that is relevant to <b>all</b> handler types, you
 * should place that setting in this class.
 * </p>
 * <p>
 * This class inherits from {@link ObservedSettings} which allows clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * </p>
 */
public abstract class HandlerSettings<OBSERVER extends HandlerSettingsListener>
        extends ObservedSettings<OBSERVER> {
    
    /*
     * Add settings that are relevant to all handler types here.
     */

}
