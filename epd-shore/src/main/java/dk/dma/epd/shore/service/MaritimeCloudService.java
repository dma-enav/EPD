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
package dk.dma.epd.shore.service;

import com.bbn.openmap.proj.coords.LatLonPoint;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.service.MaritimeCloudServiceCommon;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.settings.EPDEnavSettings;

/**
 * Shore-specific service that provides an interface to the Maritime Cloud connection.
 */
public class MaritimeCloudService extends MaritimeCloudServiceCommon {

    private String shoreID;
    private LatLonPoint shorePos = new LatLonPoint.Double(0.0, 0.0);

    /**
     * Reads the e-Navigation settings for connection parameters
     */
    @Override
    protected void readEnavSettings() {
        super.readEnavSettings();
        
        EPDEnavSettings enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
        shoreID = (String) enavSettings.getShoreId().subSequence(0, 9);
        shorePos = enavSettings.getShorePos();
    }
    
    /**
     * Returns the maritime id to connect with
     * @return the maritime id to connect with
     */
    @Override
    public MaritimeId getMaritimeId() {
        if (shoreID != null && shoreID.length() > 0) {
            return new MmsiId(Integer.valueOf(shoreID));
        }
        return null;
    }
    
    /**
     * Returns the current position
     * @return the current position
     */
    @Override
    public Position getCurrentPosition() {
        if (shorePos != null) {
            return Position.create(shorePos.getLatitude(), shorePos.getLongitude());
        }
        return null;
    }

}
