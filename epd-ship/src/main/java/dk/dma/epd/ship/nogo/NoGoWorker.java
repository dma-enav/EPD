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
package dk.dma.epd.ship.nogo;

import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogoslices.response.NogoResponseSlices;

public class NoGoWorker extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(NoGoWorker.class);

    private NogoHandler nogoHandler;
    private ShoreServicesCommon shoreServices;
    int id;
    double draught;
    Position northWestPoint;
    Position southEastPoint;
    Date validFrom;
    Date validTo;
    int slices;

    public NoGoWorker(NogoHandler nogoHandler, ShoreServicesCommon shoreCommon, int id, int slices) {
        this.nogoHandler = nogoHandler;
        shoreServices = shoreCommon;
        this.id = id;
        this.slices = slices;
    }

    public void setValues(double draught, Position northWestPoint, Position southEastPoint, DateTime startDate, DateTime endDate) {

        this.draught = draught;
        this.northWestPoint = northWestPoint;
        this.southEastPoint = southEastPoint;
        this.validFrom = new Date(startDate.getMillis());
        this.validTo = new Date(endDate.getMillis());

    }

    @Override
    public void run() {

        LOG.info("NoGo Worker " + id + " has started its request");

        if (shoreServices == null) {
            nogoHandler.noNetworkConnection();
            // Send fault message

            return;
        }

        try {

            if (slices == 1) {

                NogoResponse nogoResponse = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom, validTo);

                // Check the nogoresponse stuff

                if (nogoResponse == null || nogoResponse.getPolygons() == null) {
                    nogoHandler.nogoTimedOut();
                    return;
                }

                // Store results

                nogoHandler.nogoWorkerCompleted(id, nogoResponse);
            } else {
                NogoResponseSlices nogoResponse = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom,
                        validTo, slices);

                // Check the nogoresponse stuff

                if (nogoResponse == null || nogoResponse.getResponses() == null) {
                    nogoHandler.nogoTimedOut();
                    nogoHandler.setNoGoRequestCompleted();
                    return;
                }

                // Store results
                for (int i = 0; i < nogoResponse.getResponses().size(); i++) {
                    System.out.println("Completed " + i);
                    nogoHandler.nogoWorkerCompleted(i, nogoResponse.getResponses().get(i));
                }

                
                
            }
        } catch (ShoreServiceException e) {
            // TODO Auto-generated catch block
            nogoHandler.noNetworkConnection();
            LOG.error("Failed to get NoGo from shore: " + e.getMessage());
            
        }
        // Perform the thing
        nogoHandler.setNoGoRequestCompleted();
    }

}
