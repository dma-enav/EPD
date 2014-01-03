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
package dk.dma.epd.ship.service.shore;

import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.frv.enav.common.xml.PositionReport;
import dk.frv.enav.common.xml.ShoreServiceRequest;

/**
 * Shore service component providing the functional link to shore.
 * <p>
 * This sub-class adds own-ship functionality to the {@code ShoreServicesCommon} class. 
 */
public class ShoreServices extends ShoreServicesCommon {

    private OwnShipHandler ownShipHandler;
    
    /**
     * Constructor
     * 
     * @param enavSettings
     */
    public ShoreServices(EPDEnavSettings enavSettings) {
        super(enavSettings);
    }
    
    
    /**
     * Override super to adjust the shore service request with the position of the own-ship
     * @param request the request to adjust
     */
    @Override
    protected void addRequestParameters(ShoreServiceRequest request) {
        if (ownShipHandler != null && ownShipHandler.getMmsi() != null) {
            request.setMmsi(ownShipHandler.getMmsi());
            if (ownShipHandler.getPositionData().getPos() != null) {
                PositionReport posReport = convertPositionReport(ownShipHandler.getPositionData());
                if (posReport != null) {
                    request.setPositionReport(posReport);
                }
            }
        }

    }
    
    public static PositionReport convertPositionReport(VesselPositionData position) {
        PositionReport enavshorePos = new PositionReport();

        if (position == null || position.getPos() == null) {
            return null;
        }

        enavshorePos.setCog(floatToDouble(position.getCog()));
        enavshorePos.setHeading(floatToDouble(position.getTrueHeading()));
        enavshorePos.setLatitude(position.getPos().getLatitude());
        enavshorePos.setLongitude(position.getPos().getLongitude());
        enavshorePos.setRot(floatToDouble(position.getRot()));
        enavshorePos.setSog(floatToDouble(position.getSog()));
        return enavshorePos;
    }

    /**
     * Called when a bean is added to this bean context
     * @param obj the bean that was added
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (ownShipHandler == null && obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler) obj;
        }
    }

    /**
     * Called when a bean is removed from this bean context
     * @param obj the bean that was removed
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj == ownShipHandler) {
            ownShipHandler = null;
        }
        
        super.findAndUndo(obj);
    }
}
