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

import java.util.Date;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceErrorCode;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteMetocSettings;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequestWp;
/**
 * Metoc shore service request generator 
 */
public class Metoc {
    
    private static final long MAX_FORECAST_FUTURE = 60;

    public static MetocForecastRequest generateMetocRequest(Route route, Position pos)
            throws ShoreServiceException {
        MetocForecastRequest req = new MetocForecastRequest();
        RouteMetocSettings settings = route.getRouteMetocSettings();
        req.setDt(settings.getInterval());
        
        // Set all datatypes (could also just be settings.getDataTypes())
        MetocDataTypes.allTypes();
        for (MetocDataTypes dataType : MetocDataTypes.allTypes()) {
            req.getDataTypes().add(dataType);
        }
        
        //req.setProvider(settings.getProvider());


        // Special handling for active waypoint. Add one special wp and offset
        // all etas
        // Start at active waypoint
        int startWpIndex = 0;
        if (route instanceof ActiveRoute) {
            ActiveRoute activeRoute = (ActiveRoute) route;

            // Recalculate all remaining ETA's
            if (!activeRoute.reCalcRemainingWpEta()) {
                throw new ShoreServiceException(ShoreServiceErrorCode.NO_VALID_GPS_DATA);
            }
            
            startWpIndex = activeRoute.getActiveWaypointIndex();

            // Insert current location
            MetocForecastRequestWp reqWp = new MetocForecastRequestWp();
            reqWp.setEta(PntTime.getDate());
            reqWp.setHeading(activeRoute.getCurrentLeg().getHeading().name());
            reqWp.setLat(pos.getLatitude());
            reqWp.setLon(pos.getLongitude());

            req.getWaypoints().add(reqWp);
            //System.out.println("First     wp: " + reqWp);


        } else {
            route.adjustStartTime();
        }
        
        Date now = PntTime.getDate();

        for (int i = startWpIndex; i < route.getWaypoints().size(); i++) {
            Date eta = route.getWpEta(i);
            
            // Stop if ETA is too far in the future
            double inFutureHours = (eta.getTime() - now.getTime()) / 1000.0 / 3600.0;
            if (inFutureHours > MAX_FORECAST_FUTURE) {
                break;
            }
            
            // If not last waypoint and eta in past, leave out if next also in the past.
            if (i < route.getWaypoints().size() - 1) {
                Date nextEta = route.getWpEta(i + 1);
                if (eta.before(now) && nextEta.before(now)) {
                    continue;
                }
            }
                        
            RouteWaypoint wp = route.getWaypoints().get(i);
            MetocForecastRequestWp reqWp = new MetocForecastRequestWp();
            reqWp.setEta(eta);
            if (wp.getOutLeg() != null) {
                reqWp.setHeading(wp.getOutLeg().getHeading().name());
            }
            reqWp.setLat(wp.getPos().getLatitude());
            reqWp.setLon(wp.getPos().getLongitude());

            req.getWaypoints().add(reqWp);            
        }
        

        return req;
    }

}
