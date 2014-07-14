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
package dk.dma.epd.shore.services.shore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.communication.webservice.ShoreHttp;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceErrorCode;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.ShoreServiceStatus;
import dk.dma.epd.shore.ais.AisHandler;
import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.msi.request.MsiPollRequest;
import dk.frv.enav.common.xml.msi.response.MsiResponse;

/**
 * Shore service component providing the functional link to shore.
 */
public class ShoreServices extends ShoreServicesCommon implements IStatusComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ShoreServices.class);

    private AisHandler aisHandler;
    // private GpsHandler gpsHandler;
    private ShoreServiceStatus status = new ShoreServiceStatus();

    public ShoreServices(NetworkSettings<?> shoreServicesConnectionSettings, NetworkSettings<?> monaLisaConnectionSettings) {
        super(shoreServicesConnectionSettings, monaLisaConnectionSettings);
    }

    public static double floatToDouble(float converThisNumberToFloat) {

        String floatNumberInString = String.valueOf(converThisNumberToFloat);
        double floatNumberInDouble = Double.parseDouble(floatNumberInString);
        return floatNumberInDouble;

    }

    public MsiResponse msiPoll(int lastMessage) throws ShoreServiceException {
        // Create request
        MsiPollRequest msiPollRequest = new MsiPollRequest();
        msiPollRequest.setLastMessage(lastMessage);

        // Add request parameters
        // addRequestParameters(msiPollRequest);

        MsiResponse msiResponse = (MsiResponse) makeRequest("/api/xml/msi", "dk.frv.enav.common.xml.msi.request",
                "dk.frv.enav.common.xml.msi.response", msiPollRequest);

        return msiResponse;
    }

    private ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request)
            throws ShoreServiceException {
        // Create HTTP request
        ShoreHttp shoreHttp = new ShoreHttp(uri, this.shoreServicesConnSettings);
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
            res = (ShoreServiceResponse) resObj;
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
            aisHandler = (AisHandler) obj;
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
