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
package dk.dma.epd.common.prototype.shoreservice;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import dk.dma.epd.common.prototype.settings.EnavSettings;
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
import dk.frv.enav.common.xml.risk.request.RiskRequest;
import dk.frv.enav.common.xml.risk.response.RiskList;
import dk.frv.enav.common.xml.risk.response.RiskResponse;

/**
 * Shore service component providing the functional link to shore.
 */
public class ShoreServicesCommon extends MapHandlerChild implements IStatusComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ShoreServicesCommon.class);

    private AisHandlerCommon aisHandler;
    private PntHandler pntHandler;
    protected EnavSettings enavSettings;
    private ShoreServiceStatus status = new ShoreServiceStatus();
    private static final String ENCODING = "UTF-8";

    public ShoreServicesCommon(EnavSettings enavSettings) {
        this.enavSettings = enavSettings;
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

        for (RouteWaypoint waypointEeins : waypoint) {
            Waypoint waypointEnavshore = new Waypoint();

            // ETA
            waypointEnavshore.setEta(route.getWpEta(i));

            // Heading
            // Heading headingEeins = waypointEeins.getHeading();
            dk.frv.enav.common.xml.Waypoint.Heading headingEnavshore = null;
            waypointEnavshore.setHeading(headingEnavshore);

            // Latitude
            waypointEnavshore.setLat(waypointEeins.getPos().getLatitude());

            // Longitude
            waypointEnavshore.setLon(waypointEeins.getPos().getLongitude());

            // Rate of turn
            waypointEnavshore.setRot(waypointEeins.getRot());

            // Speed
            // waypointEnavshore.setSpeed(waypointEeins.getOutLeg().getSpeed());

            // Turn radius
            waypointEnavshore.setTurnRad(waypointEeins.getTurnRad());

            // Port XTD
            // waypointEnavshore.setXtdPort(waypointEeins.getOutLeg().getXtdPort());

            // Starboard XTD
            // waypointEnavshore.setXtdStarboard(waypointEeins.getOutLeg().getXtdStarboard());

            waypoints.add(waypointEnavshore);

            i++;

        }
        xmlRoute.setWaypoints(waypoints);
        xmlRoute.setActiveWaypoint(0);

        return xmlRoute;
    }

    public NogoResponse nogoPoll(double draught, Position northWestPoint, Position southEastPoint, Date startDate, Date endDate)
            throws ShoreServiceException {
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
     * @param request the request to adjust
     */
    protected void addRequestParameters(ShoreServiceRequest request) {
    }

    private ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request)
            throws ShoreServiceException {
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

            xml = xml.replace("<ns2:RouteRequest xmlns:ns2=\"http://www.sspa.se/optiroute\"",
                    "<RouteRequest xmlns:fi=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\"");
            xml = xml.replace("xmlns=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\">",
                    "xmlns=\"http://www.sspa.se/optiroute\">");

            xml = xml.replace("ns2:", "");
            xml = xml.replace(":ns2", "");
            xml = xml.replace(":ns2", "");

            xml = xml.replace("waypoints>", "fi:waypoints>");
            xml = xml.replace("waypoint>", "fi:waypoint>");

            xml = xml.replace("wpt-id", "fi:wpt-id");
            xml = xml.replace("ETA", "fi:ETA");
            xml = xml.replace("wpt-name", "fi:wpt-name");
            xml = xml.replace("position", "fi:position");
            xml = xml.replace("latitude", "fi:latitude");
            xml = xml.replace("longitude", "fi:longitude");

            // fix later maybe?

             if (showInput) {
             new XMLDialog(xml, "Sent XML");
             }

            // System.out.println("Sending the following:");
            // System.out.println(xml);

            // Create HTTP request
            RouteHttp routeHttp = new RouteHttp(enavSettings);
            // Init HTTP
            routeHttp.init(timeout);
            // Set content
            routeHttp.setRequestBody(xml);

            // routeHttp.set
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

//        System.out.println("Recieved the following:");
//        System.out.println(xmlReturnRoute);

         if (showOutput) {
         new XMLDialog(xmlReturnRoute, "Returned XML");
         }

        if (xmlReturnRoute.contains("<ErrorResponse xmlns=\"http://www.sspa.se/optiroute\">")) {
            String errorMessage = xmlReturnRoute.split("<ErrorResponse xmlns=\"http://www.sspa.se/optiroute\">")[1]
                    .split("</ErrorResponse>")[0];

            errorMessage = errorMessage.trim();
            return new SSPAResponse(null, errorMessage);

        } else {
            if (xmlReturnRoute != null) {
                if (xmlReturnRoute.length() > 300000) {
                    System.out.println("Failed to recieve a route in the area, buffer timedout");
                    return new SSPAResponse(null, "Failed to recieve a route in the area, buffer timedout");
                }

                xmlReturnRoute = xmlReturnRoute
                        .replace(
                                "<RouteResponse xmlns:fi=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\" xmlns=\"http://www.sspa.se/optiroute\">",
                                "<RouteResponse xmlns=\"http://www.sspa.se/optiroute\" xmlns:ns2=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\">");

                xmlReturnRoute=                xmlReturnRoute.replace("fi", "ns2");

                // System.out.println(xmlReturnRoute);

                Unmarshaller u;
                JAXBContext jc;
                RouteresponseType routeResponse = null;

                // xmlReturnRoute = xmlReturnRoute.replace("RouteResponse",
                // "routeresponseType");

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
        // return new SSPAResponse(null, "null error");
        return null;
    }

}
