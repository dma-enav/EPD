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
package dk.dma.epd.shore.settings;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * @author Janus Varmarken
 */
public class IdentitySettings extends ObservedSettings<OBSERVER> {
    private String shoreId = MaritimeCloudUtils.STCC_MMSI_PREFIX + System.currentTimeMillis();
    private LatLonPoint shorePos = new LatLonPoint.Double(56.02, 12.36); // Somewhere around Helsingør
}
