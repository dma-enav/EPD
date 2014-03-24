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
package dk.dma.epd.common.prototype.monalisa;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.Route.EtaCalculationType;
import dk.dma.epd.common.prototype.monalisa.sspa.CurrentShipDataType;
import dk.dma.epd.common.prototype.monalisa.sspa.DraftType;
import dk.dma.epd.common.prototype.monalisa.sspa.PositionType;
import dk.dma.epd.common.prototype.monalisa.sspa.RouteType;
import dk.dma.epd.common.prototype.monalisa.sspa.RouterequestType;
import dk.dma.epd.common.prototype.monalisa.sspa.RouteresponseType;
import dk.dma.epd.common.prototype.monalisa.sspa.WaypointType;
import dk.dma.epd.common.prototype.monalisa.sspa.WaypointsType;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;

//import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.Routerequest;

/**
 * Shore service component providing the functional link to shore.
 */
public class MonaLisaRouteOptimizationCommon extends MapHandlerChild
// implements
{
    // Runnable

    // private static final Logger LOG = Logger
    // .getLogger(MonaLisaRouteExchange.class);

    // private ShoreServiceStatus status = new ShoreServiceStatus();
    protected ShoreServicesCommon shoreService;
    protected RouteManagerCommon routeManager;

    public MonaLisaRouteOptimizationCommon() {

    }

    public RouterequestType convertRoute(Route route,
            boolean removeIntermediateETA, float trim, int ukc,
            List<Boolean> selectedWp) {

        // float trim = 6.0f;

        // Create the route request
        RouterequestType monaLisaRoute = new RouterequestType();

        // Create the ship data
        CurrentShipDataType currentShipData = new CurrentShipDataType();

        // Current ship data
        currentShipData.setImoid("1234567");
        currentShipData.setMmsi("123456789");
        currentShipData.setUkcrequested(ukc);

        DraftType draft = new DraftType();
        draft.setAft(trim);
        draft.setForward(trim);
        currentShipData.setDraft(draft);

        monaLisaRoute.setCurrentShipData(currentShipData);

        RouteType monaLisaRouteType = new RouteType();

        WaypointsType waypoints = new WaypointsType();

        // Convert the existing waypoints into the Mona Lisa Format
        List<WaypointType> monaLisaWaypoints = waypoints.getWaypoint();
        LinkedList<RouteWaypoint> routeWaypoints = route.getWaypoints();

        for (int i = 0; i < routeWaypoints.size(); i++) {

            if (selectedWp.get(i)) {


                // System.out.println("Creating WP for " + i);
                RouteWaypoint routeWaypoint = routeWaypoints.get(i);
                WaypointType waypoint = new WaypointType();

                // Set name
                waypoint.setWptName(routeWaypoint.getName());

                // Set ID
                waypoint.setWptId(i + 1);

		// If we don't want intermediate ETA waypoints, then skip setting time
	        if( removeIntermediateETA && i != 0 && i != (routeWaypoints.size() - 1) ){
			waypoint.setFixed(false);
		} else {
			try {
				waypoint.setETA(convertDate(route.getEtas().get(i)));
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
			waypoint.setFixed(true);
		}

                // Set positon
                PositionType position = new PositionType();

                position.setLatitude(routeWaypoint.getPos().getLatitude());
                position.setLongitude(routeWaypoint.getPos().getLongitude());

                waypoint.setPosition(position);

                monaLisaWaypoints.add(waypoint);
            }

        }

        monaLisaRouteType.setWaypoints(waypoints);
        monaLisaRoute.setRoute(monaLisaRouteType);

        return monaLisaRoute;
    }

    public Route convertRouteBack(RouteresponseType response) {
        Route route = new Route();
//        route.setEtas(new ArrayList<Date>());
        
        ArrayList<Date> etas = new ArrayList<>();
        
        route.setName("Optimized Mona Lisa Route");

        WaypointsType waypointsType = response.getRoute().getWaypoints();
        List<WaypointType> responseWaypoints = waypointsType.getWaypoint();

        LinkedList<RouteWaypoint> routeWaypoints = route.getWaypoints();

        for (int i = 0; i < responseWaypoints.size(); i++) {

            RouteWaypoint waypoint = new RouteWaypoint();
            WaypointType responseWaypoint = responseWaypoints.get(i);

            waypoint.setName(responseWaypoint.getWptName());

            if (i != 0) {
                RouteLeg inLeg = new RouteLeg();
                inLeg.setHeading(Heading.RL);
                waypoint.setInLeg(inLeg);

                // RouteWaypoint prevWaypoint =
                // routeWaypoints.get(routeWaypoints
                // .size() - 2);
                // System.out.println("For waypoint" + i + " creating in leg");
            }

            // Outleg always has next
            if (i != responseWaypoints.size() - 1) {
                RouteLeg outLeg = new RouteLeg();
                outLeg.setHeading(Heading.RL);
                waypoint.setOutLeg(outLeg);
                // System.out.println("For waypoint" + i + " creating out leg");
            }

            // if (waypoint.getInLeg() != null) {
            // waypoint.getInLeg().setSpeed(5.0);
            // }

            // if (waypoint.getOutLeg() != null) {
            // System.out.println("SEtting stuff?");
            // waypoint.getOutLeg().setSpeed(5.0);
            // // System.out.println(waypoint.getOutLeg().getSpeed());
            // }

            Position position = Position.create(responseWaypoint.getPosition()
                    .getLatitude(), responseWaypoint.getPosition()
                    .getLongitude());
            waypoint.setPos(position);

            if (responseWaypoint.getLegInfo() != null) {

                if (responseWaypoint.getLegInfo().getTurnRadius() != null) {
                    waypoint.setRot((double) responseWaypoint.getLegInfo()
                            .getTurnRadius());
                }

                if (responseWaypoint.getLegInfo().getPlannedSpeed() != null) {

                    waypoint.setSpeed(responseWaypoint.getLegInfo()
                            .getPlannedSpeed());

                }

                if (responseWaypoint.getLegInfo().getTurnRadius() != null) {
                    waypoint.setTurnRad((double) responseWaypoint.getLegInfo()
                            .getTurnRadius());
                }
            }

            etas.add(responseWaypoint.getETA().toGregorianCalendar().getTime());
            
            routeWaypoints.add(waypoint);

        }

        if (routeWaypoints.size() > 1) {
            for (int i = 0; i < routeWaypoints.size(); i++) {

                // System.out.println("Looking at waypoint:" + i);
                RouteWaypoint waypoint = routeWaypoints.get(i);

                // Waypoint 0 has no in leg, one out leg... no previous
                if (i != 0) {
                    RouteWaypoint prevWaypoint = routeWaypoints.get(i - 1);

                    if (waypoint.getInLeg() != null) {
                        // System.out.println("Setting inleg prev for waypoint:"
                        // + i);
                        waypoint.getInLeg().setStartWp(prevWaypoint);
                        waypoint.getInLeg().setEndWp(waypoint);
                    }

                    if (prevWaypoint.getOutLeg() != null) {
                        // System.out.println("Setting outleg prev for waypoint:"
                        // + i);
                        prevWaypoint.getOutLeg().setStartWp(prevWaypoint);
                        prevWaypoint.getOutLeg().setEndWp(waypoint);

                    }
                }

            }
        }

        route.setEtas(etas);
        route.calcValues(true);
        
        return route;
    }

    public MonaLisaOptimizationResponse makeRouteRequest(Route route,
            boolean removeIntermediateETA, float draft, int ukc, int timeout,
            List<Boolean> selectedWp, boolean showInput, boolean showOutput) {

        // new Thread(this).start();

        RouterequestType monaLisaRoute = convertRoute(route,
                removeIntermediateETA, draft, ukc, selectedWp);

        // monaLisaRoute.

        SSPAResponse routeResponse = null;
        try {
            routeResponse = shoreService.makeMonaLisaRouteRequest(
                    monaLisaRoute, timeout, showInput, showOutput);
        } catch (Exception e) {
            return new MonaLisaOptimizationResponse("An exception occured",
                    e.getMessage());
        }

        if (!routeResponse.isValid()) {
            return new MonaLisaOptimizationResponse("Server error",
                    routeResponse.getErrorMessage());
        }

        Route newRoute = null;

        if (routeResponse.isValid()) {
            try {
                newRoute = convertRouteBack(routeResponse.getMonaLisaResponse());
            } catch (Exception e) {
                return new MonaLisaOptimizationResponse("An exception occured",
                        e.getMessage());
            }
        }

        if (newRoute != null) {
            route.setVisible(false);
            routeManager.addRoute(newRoute);
        }

        float fuelSaving = (routeResponse.getMonaLisaResponse()
                .getFuelRequested() - routeResponse.getMonaLisaResponse()
                .getFuelFinal())
                / routeResponse.getMonaLisaResponse().getFuelRequested() * 100;

        return new MonaLisaOptimizationResponse(
                "Succesfully received optimized route",

                "\nInitial route consumption is "
                        + routeResponse.getMonaLisaResponse()
                                .getFuelRequested() + " Metric Tons.\n"
                        + "MonaLisa optimized route consumption is "
                        + routeResponse.getMonaLisaResponse().getFuelFinal()
                        + " Metric Tons.\n" + "The relative fuel saving is "
                        + fuelSaving + " percent\n\n" + "Minimum route UKC is "
                        + routeResponse.getMonaLisaResponse().getUkcActual()
                        + " meters.\n");

    }

    @Override
    public void findAndInit(Object obj) {

        if (shoreService == null && obj instanceof ShoreServicesCommon) {
            shoreService = (ShoreServicesCommon) obj;
        }
        if (routeManager == null && obj instanceof RouteManagerCommon) {
            routeManager = (RouteManagerCommon) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {

    }

    // @Override
    // public ComponentStatus getStatus() {
    // return status;
    // }

    // @Override
    // public void run() {
    //
    // RouterequestType monaLisaRoute = convertRoute(route);
    //
    // RouteresponseType routeResponse = EPDShip.getShoreServices()
    // .makeMonaLisaRouteRequest(monaLisaRoute);
    //
    // Route newRoute = null;
    //
    // if (routeResponse != null) {
    // newRoute = convertRouteBack(routeResponse);
    // }
    //
    // if (newRoute != null) {
    //
    // EPDShip.getRouteManager().addRoute(newRoute);
    //
    // }
    // }

    private XMLGregorianCalendar convertDate(Date date)
            throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(c);

        // No time zone?
        date2.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        date2.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        return date2;

    }
}
