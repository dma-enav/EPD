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
package dk.dma.epd.ship.service.communication.webservice;

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
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.communication.webservice.ShoreHttp;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceErrorCode;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.route.sspa.RouterequestType;
import dk.dma.epd.ship.route.sspa.RouteresponseType;
import dk.dma.epd.ship.services.shore.RouteHttp;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.dma.epd.ship.status.ComponentStatus;
import dk.dma.epd.ship.status.IStatusComponent;
import dk.dma.epd.ship.status.ShoreServiceStatus;
import dk.frv.enav.common.xml.PositionReport;
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
public class ShoreServices extends MapHandlerChild implements IStatusComponent {

    private static final Logger LOG = LoggerFactory
            .getLogger(ShoreServices.class);

    private AisHandler aisHandler;
    private GpsHandler gpsHandler;
    private EPDEnavSettings enavSettings;
    private ShoreServiceStatus status = new ShoreServiceStatus();
    private static final String ENCODING = "UTF-8";

    public ShoreServices(EPDEnavSettings enavSettings) {
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

    public static PositionReport convertPositionReport(
            VesselPositionData position) {
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

    public NogoResponse nogoPoll(double draught, Position northWestPoint,
            Position southEastPoint, Date startDate, Date endDate)
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

        NogoResponse nogoResponse = (NogoResponse) makeRequest("/api/xml/nogo",
                "dk.frv.enav.common.xml.nogo.request",
                "dk.frv.enav.common.xml.nogo.response", nogoRequest);
        return nogoResponse;
    }

    public MsiResponse msiPoll(int lastMessage) throws ShoreServiceException {
        // Create request
        MsiPollRequest msiPollRequest = new MsiPollRequest();
        msiPollRequest.setLastMessage(lastMessage);

        // Add request parameters
        addRequestParameters(msiPollRequest);

        MsiResponse msiResponse = (MsiResponse) makeRequest("/api/xml/msi",
                "dk.frv.enav.common.xml.msi.request",
                "dk.frv.enav.common.xml.msi.response", msiPollRequest);

        return msiResponse;
    }

    public List<RiskList> getRiskIndexes(double southWestLat,
            double northEastLat, double southWestLon, double northEastLon)
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

        RiskResponse resp = (RiskResponse) makeRequest("/api/xml/risk",
                "dk.frv.enav.common.xml.risk.request",
                "dk.frv.enav.common.xml.risk.response", req);

        return resp.getList();
    }

    public MetocForecast routeMetoc(Route route) throws ShoreServiceException {
        // Get current position if active route
        Position pos = null;
        if (route instanceof ActiveRoute) {
            GpsData gpsData = gpsHandler.getCurrentData();
            if (gpsData.isBadPosition()) {
                throw new ShoreServiceException(
                        ShoreServiceErrorCode.NO_VALID_GPS_DATA);
            }
            pos = gpsData.getPosition();
        }
        // Create request
        MetocForecastRequest request = Metoc.generateMetocRequest(route, pos);

        // Add request parameters
        addRequestParameters(request);

        // Make request
        MetocForecastResponse res = (MetocForecastResponse) makeRequest(
                "/api/xml/routeMetoc", "dk.frv.enav.common.xml.metoc.request",
                "dk.frv.enav.common.xml.metoc.response", request);

        return res.getMetocForecast();
    }

    private void addRequestParameters(ShoreServiceRequest request) {
        if (aisHandler != null && aisHandler.getOwnShip() != null) {
            request.setMmsi(aisHandler.getOwnShip().getMmsi());
            if (aisHandler.getOwnShip().getPositionData() != null) {
                PositionReport posReport = convertPositionReport(aisHandler
                        .getOwnShip().getPositionData());
                if (posReport != null) {
                    request.setPositionReport(posReport);
                }
            }
        }

    }

    private ShoreServiceResponse makeRequest(String uri, String reqContextPath,
            String resContextPath, Object request) throws ShoreServiceException {
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
            throw new ShoreServiceException(
                    ShoreServiceErrorCode.INTERNAL_ERROR);
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
            throw new ShoreServiceException(
                    ShoreServiceErrorCode.INVALID_RESPONSE);
        }

        // Set last fail/contact
        status.markContactSuccess();

        // Report if an error response
        if (res.getErrorCode() != 0) {
            throw new ShoreServiceException(
                    ShoreServiceErrorCode.SERVICE_ERROR, res.getErrorMessage());
        }

        return res;
    }

    @Override
    public void findAndInit(Object obj) {
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }
        if (gpsHandler == null && obj instanceof GpsHandler) {
            gpsHandler = (GpsHandler) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == aisHandler) {
            aisHandler = null;
        } else if (obj == gpsHandler) {
            gpsHandler = null;
        }
    }

    @Override
    public ComponentStatus getStatus() {
        return status;
    }

    @SuppressWarnings("rawtypes")
    public RouteresponseType makeMonaLisaRouteRequest(
            RouterequestType monaLisaRoute) {

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

            xml = xml.replace("<ns2:RouteRequest xmlns:ns2=\"http://www.sspa.se/optiroute\"", "<RouteRequest xmlns:fi=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\"");
            xml = xml.replace("xmlns=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\">", "xmlns=\"http://www.sspa.se/optiroute\">");

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
            
            

            System.out.println("Sending the following:");
            System.out.println(xml);

            // Create HTTP request
            RouteHttp routeHttp = new RouteHttp(enavSettings);
            // Init HTTP
            routeHttp.init();
            // Set content
            routeHttp.setRequestBody(xml);
            // Make request
            try {
                routeHttp.makeRequest();
                xmlReturnRoute = routeHttp.getResponseBody();
            } catch (Exception e) {
                // status.markContactError(e);
                // throw e;
                System.out.println(e.getMessage());
            }

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Recieved the following:");
        System.out.println(xmlReturnRoute);

        if (xmlReturnRoute != null) {

            
            xmlReturnRoute.replace("<RouteResponse xmlns:fi=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\" xmlns=\"http://www.sspa.se/optiroute\">"
                   ,"<RouteResponse xmlns=\"http://www.sspa.se/optiroute\" xmlns:ns2=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\">");
            
            
            xmlReturnRoute.replace("fi", "ns2");

            // System.out.println(xmlReturnRoute);

            Unmarshaller u;
            JAXBContext jc;
            RouteresponseType routeResponse = null;

            // xmlReturnRoute = xmlReturnRoute.replace("RouteResponse",
            // "routeresponseType");

            StringReader sr = new StringReader(xmlReturnRoute);

            try {
                jc = JAXBContext.newInstance("dk.dma.epd.ship.route.sspa");
                u = jc.createUnmarshaller();

                routeResponse = (RouteresponseType) ((javax.xml.bind.JAXBElement) u
                        .unmarshal(sr)).getValue();

            } catch (JAXBException e1) {
                e1.printStackTrace();
            }

            return routeResponse;

        } else {

            return null;
        }

    }

}
