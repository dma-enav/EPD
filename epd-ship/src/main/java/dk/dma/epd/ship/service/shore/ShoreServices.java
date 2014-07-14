/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.ship.service.shore;

import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.ship.ownship.OwnShipHandler;
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
    public ShoreServices(NetworkSettings<?> shoreServicesConnectionSettings, NetworkSettings<?> monaLisaConnectionSettings) {
        super(shoreServicesConnectionSettings, monaLisaConnectionSettings);
    }

    /**
     * Override super to adjust the shore service request with the position of the own-ship
     * 
     * @param request
     *            the request to adjust
     */
    @Override
    protected void addRequestParameters(ShoreServiceRequest request) {
        if (ownShipHandler != null && ownShipHandler.getMmsi() != null) {
            request.setMmsi(ownShipHandler.getMmsi());
            if (ownShipHandler.isPositionDefined()) {
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
     * 
     * @param obj
     *            the bean that was added
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
     * 
     * @param obj
     *            the bean that was removed
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj == ownShipHandler) {
            ownShipHandler = null;
        }

        super.findAndUndo(obj);
    }
}
