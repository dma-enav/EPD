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
package dk.dma.epd.ship.service;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.MaritimeCloudServiceCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * Ship-specific service that provides an interface to the Maritime Cloud connection.
 */
public class MaritimeCloudService extends MaritimeCloudServiceCommon {

    private OwnShipHandler ownShipHandler;
    private PntHandler pntHandler;
   
    /**
     * Returns the maritime id to connect with
     * @return the maritime id to connect with
     */
    @Override
    public MaritimeId getMaritimeId() {
        if (ownShipHandler != null && ownShipHandler.getMmsi() != null) {
            return new MmsiId(ownShipHandler.getMmsi().intValue());
        }
        return null;
    }
    
    /**
     * Returns the current position
     * @return the current position
     */
    @Override
    public Position getCurrentPosition() {
        if (pntHandler != null) {
            return pntHandler.getCurrentData().getPosition();
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof OwnShipHandler) {
            this.ownShipHandler = (OwnShipHandler) obj;
        } else if (obj instanceof PntHandler) {
            this.pntHandler = (PntHandler) obj;
        }     
    }
   
}
