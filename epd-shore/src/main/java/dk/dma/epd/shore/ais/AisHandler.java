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
package dk.dma.epd.shore.ais;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.settings.AisSettings;

/**
 * Class for handling incoming AIS messages on a vessel and maintainer of AIS target tables
 */
public class AisHandler extends AisHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(AisHandler.class);

    protected Map<Long, List<PastTrackPoint>> pastTrack = new HashMap<Long, List<PastTrackPoint>>();

    /**
     * Empty constructor not used
     */
    public AisHandler(AisSettings aisSettings) {
        super(aisSettings);
    }

    /**
     * Update vessel target position data
     * 
     * @param mmsi
     * @param positionData
     * @param aisClass
     */
    @Override
    protected void updatePos(long mmsi, VesselPositionData positionData, VesselTarget.AisClass aisClass) {
        // Determine if this is SART
        if (isSarTarget(mmsi)) {
            updateSartPos(mmsi, positionData);
            return;
        }

        // Try to find exiting target
        VesselTarget vesselTarget = vesselTargets.get(mmsi);
        // If not exists, create and insert
        if (vesselTarget == null) {
            vesselTarget = new VesselTarget();
            vesselTarget.getSettings().setShowRoute(showIntendedRouteDefault);
            vesselTarget.setMmsi(mmsi);
            vesselTargets.put(mmsi, vesselTarget);
        }
        // Update class and pos data
        vesselTarget.setAisClass(aisClass);
        vesselTarget.setPositionData(positionData);
        // Update track
        // TODO
        // Update last received
        vesselTarget.setLastReceived(GnssTime.getInstance().getDate());
        // Update status
        vesselTarget.setStatus(AisTarget.Status.OK);

        // Add past track
        if (pastTrack.containsKey(mmsi)) {
            LinkedList<PastTrackPoint> ptps = (LinkedList<PastTrackPoint>) pastTrack.get(mmsi);

            // Should it add the key?
            Position prevPos = ptps.get(ptps.size() - 1).getPosition();

            // LOG.info("current size of pastTrack hashmap: "+pastTrack.size());

            // In km, how often should points be saved? 1km?
            if (prevPos.distanceTo(positionData.getPos(), CoordinateSystem.CARTESIAN) > 100) {
                // System.out.println("Target " + mmsi + " has moved more than 50 since last");

                try {
                    ptps.add(new PastTrackPoint(new Date(), positionData.getPos()));
                } catch (Exception exception) {
                    LOG.error("Target " + mmsi + " has List<PastTrackPoint> size of " + ptps.size());
                    LOG.error("current size of pastTrack hashmap: " + pastTrack.size());
                    throw exception;
                }

            }

            // System.out.println(prevPos.distanceTo(positionData.getPos(), CoordinateSystem.CARTESIAN));

        } else {
            try {
                pastTrack.put(mmsi, new LinkedList<PastTrackPoint>());
                pastTrack.get(mmsi).add(new PastTrackPoint(new Date(), positionData.getPos()));
            } catch (Exception exception) {
                LOG.error("Failed to create or add new ArrayList<PastTrackPoint>");
                LOG.error("current size of pastTrack hashmap: " + pastTrack.size());
                throw exception;
            }

        }

        // Publish update
        publishUpdate(vesselTarget);
    }

    public Map<Long, List<PastTrackPoint>> getPastTrack() {
        return pastTrack;
    }

}
