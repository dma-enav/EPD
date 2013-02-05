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
package dk.dma.epd.shore.services.shore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.communication.webservice.ShoreHttp;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceErrorCode;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.settings.ESDEnavSettings;
import dk.dma.epd.shore.status.ComponentStatus;
import dk.dma.epd.shore.status.IStatusComponent;
import dk.dma.epd.shore.status.ShoreServiceStatus;
import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.msi.request.MsiPollRequest;
import dk.frv.enav.common.xml.msi.response.MsiResponse;


/**
 * Shore service component providing the functional link to shore.
 */
public class ShoreServices extends MapHandlerChild implements IStatusComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ShoreServices.class);

    private AisHandler aisHandler;
//    private GpsHandler gpsHandler;
    private ESDEnavSettings enavSettings;
    private ShoreServiceStatus status = new ShoreServiceStatus();

//    public ShoreServices(EnavSettings enavSettings) {
//        this.enavSettings = enavSettings;
//    }

    public ShoreServices(ESDEnavSettings enavSettings) {
        this.enavSettings = enavSettings;
    }



    public static double floatToDouble (float converThisNumberToFloat) {

        String floatNumberInString = String.valueOf(converThisNumberToFloat);
        double floatNumberInDouble = Double.parseDouble(floatNumberInString);
        return floatNumberInDouble;

        }


    public MsiResponse msiPoll(int lastMessage) throws ShoreServiceException {
        // Create request
        MsiPollRequest msiPollRequest = new MsiPollRequest();
        msiPollRequest.setLastMessage(lastMessage);

        // Add request parameters
//        addRequestParameters(msiPollRequest);

        MsiResponse msiResponse = (MsiResponse)makeRequest("/api/xml/msi", "dk.frv.enav.common.xml.msi.request", "dk.frv.enav.common.xml.msi.response", msiPollRequest);

        return msiResponse;
    }


    private ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request) throws ShoreServiceException {
        // Create HTTP request
        ShoreHttp shoreHttp = new ShoreHttp(uri, enavSettings);
        // Init HTTP
        shoreHttp.init();
        // Set content
        try {
            shoreHttp.setXmlMarshalContent(reqContextPath, request);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Failed to make XML request: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
        }

        // Make request
        try {
            shoreHttp.makeRequest();
        } catch (ShoreServiceException e) {
            status.markContactError(e);
            throw e;
        }

        ShoreServiceResponse res;
        try {
            Object resObj = shoreHttp.getXmlUnmarshalledContent(resContextPath);
            res = (ShoreServiceResponse)resObj;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Failed to unmarshal XML response: " + e.getMessage());
            throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
        }

        // Set last fail/contact
        status.markContactSuccess();

        // Report if an error response
        if (res.getErrorCode() != 0) {
            throw new ShoreServiceException(ShoreServiceErrorCode.SERVICE_ERROR, res.getErrorMessage());
        }

        return res;
    }

    @Override
    public void findAndInit(Object obj) {
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == aisHandler) {
            aisHandler = null;
        }
    }

    @Override
    public ComponentStatus getStatus() {
        return status;
    }

}
