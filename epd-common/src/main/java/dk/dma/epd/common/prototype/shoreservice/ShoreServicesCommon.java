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
package dk.dma.epd.common.prototype.shoreservice;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.communication.webservice.ShoreHttp;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceErrorCode;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.monalisa.SSPAResponse;
import dk.dma.epd.common.prototype.monalisa.XMLDialog;
import dk.dma.epd.common.prototype.monalisa.sspa.RouterequestType;
import dk.dma.epd.common.prototype.monalisa.sspa.RouteresponseType;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.ShoreServiceStatus;
import dk.frv.enav.common.xml.ShoreServiceRequest;
import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.Waypoint;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
import dk.frv.enav.common.xml.metoc.response.MetocForecastResponse;
import dk.frv.enav.common.xml.msi.request.MsiPollRequest;
import dk.frv.enav.common.xml.msi.response.MsiResponse;
import dk.frv.enav.common.xml.nogo.request.NogoRequest;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogoslices.request.NogoRequestSlices;
import dk.frv.enav.common.xml.nogoslices.response.NogoResponseSlices;
import dk.frv.enav.common.xml.risk.request.RiskRequest;
import dk.frv.enav.common.xml.risk.response.RiskList;
import dk.frv.enav.common.xml.risk.response.RiskResponse;

/**
 * Shore service component providing the functional link to shore.
 */
public class ShoreServicesCommon extends MapHandlerChild implements IStatusComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ShoreServicesCommon.class);

    /**
     * Shore services connection settings.
     */
    protected NetworkSettings<?> shoreServicesConnSettings;
    
    /**
     * MonaLisa connection settings.
     */
    protected NetworkSettings<?> monaLisaConnSettings;
    
    private AisHandlerCommon aisHandler;
    private PntHandler pntHandler;

    private ShoreServiceStatus status = new ShoreServiceStatus();
    private static final String ENCODING = "UTF-8";
    
    public ShoreServicesCommon(NetworkSettings<?> shoreServicesConnectionSettings, NetworkSettings<?> monaLisaConnectionSettings) {
        this.shoreServicesConnSettings = Objects.requireNonNull(shoreServicesConnectionSettings);
        this.monaLisaConnSettings = Objects.requireNonNull(monaLisaConnectionSettings);
    }

    public static double floatToDouble(float converThisNumberToFloat) {

        String floatNumberInString = String.valueOf(converThisNumberToFloat);
        double floatNumberInDouble = Double.parseDouble(floatNumberInString);
        return floatNumberInDouble;

    }

    public static dk.frv.enav.common.xml.Route convertRoute(Route route) {
        // TO DO

        dk.frv.enav.common.xml.Route xmlRoute = new dk.frv.enav.common.xml.Route();
        LinkedList<RouteWaypoint> waypoint = route.getWaypoints();
        List<Waypoint> waypoints = new ArrayList<>();
        int i = 0;

        for (RouteWaypoint routeWaypoint : waypoint) {
            Waypoint waypointEnavshore = new Waypoint();

            // ETA
            waypointEnavshore.setEta(route.getWpEta(i));

            // Heading
            dk.frv.enav.common.xml.Waypoint.Heading headingEnavshore = null;
            waypointEnavshore.setHeading(headingEnavshore);

            // Latitude
            waypointEnavshore.setLat(routeWaypoint.getPos().getLatitude());

            // Longitude
            waypointEnavshore.setLon(routeWaypoint.getPos().getLongitude());

            // Rate of turn
            waypointEnavshore.setRot(routeWaypoint.getRot());

            // Speed

            // Turn radius
            waypointEnavshore.setTurnRad(routeWaypoint.getTurnRad());

            // Port XTD
            // waypointEnavshore.setXtdPort(routeWaypoint.getOutLeg().getXtdPort());

            // Starboard XTD
            // waypointEnavshore.setXtdStarboard(routeWaypoint.getOutLeg().getXtdStarboard());

            waypoints.add(waypointEnavshore);

            i++;

        }
        xmlRoute.setWaypoints(waypoints);
        xmlRoute.setActiveWaypoint(0);

        return xmlRoute;
    }

    public NogoResponse nogoPoll(double draught, Position northWestPoint, Position southEastPoint, Date startDate, Date endDate)
            throws ShoreServiceException {

        // nogoPoll(draught, northWestPoint, southEastPoint, startDate, endDate, 2);

        // Create request
        NogoRequest nogoRequest = new NogoRequest();

        // Set request parameters
        nogoRequest.setDraught(draught);
        nogoRequest.setNorthWestPointLat(northWestPoint.getLatitude());
        nogoRequest.setNorthWestPointLon(northWestPoint.getLongitude());
        nogoRequest.setSouthEastPointLat(southEastPoint.getLatitude());
        nogoRequest.setSouthEastPointLon(southEastPoint.getLongitude());
        nogoRequest.setStartDate(startDate);
        nogoRequest.setEndDate(endDate);

        // Add request parameters
        addRequestParameters(nogoRequest);

        NogoResponse nogoResponse = (NogoResponse) makeRequest("/api/xml/nogo", "dk.frv.enav.common.xml.nogo.request",
                "dk.frv.enav.common.xml.nogo.response", nogoRequest);

        return nogoResponse;
    }

    public NogoResponseSlices nogoPoll(double draught, Position northWestPoint, Position southEastPoint, Date startDate,
            Date endDate, int slices) throws ShoreServiceException {

        // System.out.println("Nogo response slice test");

        NogoRequestSlices nogoRequest = new NogoRequestSlices();
        // System.out.println("Request created");

        // Set request parameters
        nogoRequest.setDraught(draught);
        nogoRequest.setNorthWestPointLat(northWestPoint.getLatitude());
        nogoRequest.setNorthWestPointLon(northWestPoint.getLongitude());
        nogoRequest.setSouthEastPointLat(southEastPoint.getLatitude());
        nogoRequest.setSouthEastPointLon(southEastPoint.getLongitude());
        nogoRequest.setStartDate(startDate);
        nogoRequest.setEndDate(endDate);
        nogoRequest.setSlices(slices);

        // Add request parameters
        addRequestParameters(nogoRequest);

        // System.out.println("Adding parameters");

        NogoResponseSlices nogoResponse = (NogoResponseSlices) makeRequest("/api/xml/slicesnogo",
                "dk.frv.enav.common.xml.nogoslices.request", "dk.frv.enav.common.xml.nogoslices.response", nogoRequest);

        return nogoResponse;

    }

    public MsiResponse msiPoll(int lastMessage) throws ShoreServiceException {
        // Create request
        MsiPollRequest msiPollRequest = new MsiPollRequest();
        msiPollRequest.setLastMessage(lastMessage);

        // Add request parameters
        addRequestParameters(msiPollRequest);

        MsiResponse msiResponse = (MsiResponse) makeRequest("/api/xml/msi", "dk.frv.enav.common.xml.msi.request",
                "dk.frv.enav.common.xml.msi.response", msiPollRequest);

        return msiResponse;
    }

    public List<RiskList> getRiskIndexes(double southWestLat, double northEastLat, double southWestLon, double northEastLon)
            throws ShoreServiceException {
        // Create request
        RiskRequest req = new RiskRequest();
        req.setLatMin(southWestLat);
        req.setLonMin(southWestLon);
        req.setLatMax(northEastLat);
        req.setLonMax(northEastLon);
        // req.setMmsiList(list);
        // Add request parameters
        addRequestParameters(req);

        RiskResponse resp = (RiskResponse) makeRequest("/api/xml/risk", "dk.frv.enav.common.xml.risk.request",
                "dk.frv.enav.common.xml.risk.response", req);

        return resp.getList();
    }

    public MetocForecast routeMetoc(Route route) throws ShoreServiceException {
        // Get current position if active route
        Position pos = null;
        if (route instanceof ActiveRoute) {
            PntData pntData = pntHandler.getCurrentData();
            if (pntData.isBadPosition()) {
                throw new ShoreServiceException(ShoreServiceErrorCode.NO_VALID_GPS_DATA);
            }
            pos = pntData.getPosition();
        }
        // Create request
        MetocForecastRequest request = Metoc.generateMetocRequest(route, pos);

        // Add request parameters
        addRequestParameters(request);

        // Make request
        MetocForecastResponse res = (MetocForecastResponse) makeRequest("/api/xml/routeMetoc",
                "dk.frv.enav.common.xml.metoc.request", "dk.frv.enav.common.xml.metoc.response", request);

        return res.getMetocForecast();
    }

    /**
     * Allow subclasses to adjust the shore service request
     * 
     * @param request
     *            the request to adjust
     */
    protected void addRequestParameters(ShoreServiceRequest request) {
    }

    private ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request)
            throws ShoreServiceException {
        // Create HTTP request
        ShoreHttp shoreHttp = new ShoreHttp(uri, shoreServicesConnSettings);
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
        if (aisHandler == null && obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
        }
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == aisHandler) {
            aisHandler = null;
        } else if (obj == pntHandler) {
            pntHandler = null;
        }
    }

    @Override
    public ComponentStatus getStatus() {
        return status;
    }

    @SuppressWarnings({ "rawtypes", "unused" })
    public SSPAResponse makeMonaLisaRouteRequest(RouterequestType monaLisaRoute, int timeout, boolean showInput, boolean showOutput) {

        JAXBContext context = null;
        String xmlReturnRoute = "";

        String xml = "";

        try {
            context = JAXBContext.newInstance(RouterequestType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, ENCODING);

            // Convert the generated xml route to a String
            StringWriter st = new StringWriter();
            m.marshal(monaLisaRoute, st);
            xml = st.toString();

            if (showInput) {
                new XMLDialog(xml, "Sent XML");
            }

            // Create HTTP request
            RouteHttp routeHttp = new RouteHttp(monaLisaConnSettings);
            // Init HTTP
            routeHttp.init(timeout);
            // Set content
            routeHttp.setRequestBody(xml);

            // Make request
            try {
                routeHttp.makeRequest();

                xmlReturnRoute = routeHttp.getResponseBody();
            } catch (Exception e) {
                // status.markContactError(e);
                // throw e;
                return new SSPAResponse(null, e.getMessage());
            }

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            return new SSPAResponse(null, e.getMessage());
        }

        if (showOutput) {
            new XMLDialog(xmlReturnRoute, "Returned XML");
        }

        String responseMessage = xmlReturnRoute.split("<ResponseMessage>")[1].split("</ResponseMessage>")[0];

        // This means there was an error inside the server
        if (responseMessage.length() > 0) {
            return new SSPAResponse(null, responseMessage);
        } else {
            if (xmlReturnRoute != null) {
                if (xmlReturnRoute.length() > 300000) {
                    System.out.println("Failed to receive a route in the area, buffer timedout");
                    return new SSPAResponse(null, "Failed to receive a route in the area, buffer timedout");
                }

                Unmarshaller u;
                JAXBContext jc;
                RouteresponseType routeResponse = null;

                StringReader sr = new StringReader(xmlReturnRoute);

                try {
                    jc = JAXBContext.newInstance("dk.dma.epd.common.prototype.monalisa.sspa");
                    u = jc.createUnmarshaller();

                    routeResponse = (RouteresponseType) ((javax.xml.bind.JAXBElement) u.unmarshal(sr)).getValue();

                } catch (JAXBException e1) {
                    e1.printStackTrace();
                }

                return new SSPAResponse(routeResponse, "Success");

            }
        }
        return null;
    }

}
