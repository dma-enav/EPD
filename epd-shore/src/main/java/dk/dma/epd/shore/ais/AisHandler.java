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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.AisSettings;

/**
 * Class for handling incoming AIS messages on a vessel and maintainer of AIS target tables
 */
public class AisHandler extends AisHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(AisHandler.class);

    protected ConcurrentHashMap<Long, IPastTrackShore> pastTrack = new ConcurrentHashMap<>(100000);

    protected int pastTrackMaxTime; // NB: In minutes
    protected int pastTrackMinDist; // NB: In meters
    
    /**
     * Empty constructor not used
     */
    public AisHandler(AisSettings aisSettings) {
        super(aisSettings);
        this.pastTrackMaxTime = aisSettings.getPastTrackMaxTime();
        this.pastTrackMinDist = aisSettings.getPastTrackMinDist();
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
        vesselTarget.setLastReceived(PntTime.getInstance().getDate());
        // Update status
        vesselTarget.setStatus(AisTarget.Status.OK);

        // Add past track
        if (pastTrack.containsKey(mmsi)) {
            IPastTrackShore ptps = pastTrack.get(mmsi);

            ptps.addPosition(positionData.getPos(), pastTrackMinDist);

        } else {
            pastTrack.putIfAbsent(mmsi, new PastTrackSortedSet());
            pastTrack.get(mmsi).addPosition(positionData.getPos(), pastTrackMinDist);

        }
        
        //DEBUG on performance
        
        long timeS = System.currentTimeMillis();
        for (IPastTrackShore t: pastTrack.values()) {
            t.cleanup(60*pastTrackMaxTime); // Convert from minutes to seconds
        }
        long timeE = System.currentTimeMillis();
        
        if ((timeE-timeS)/1000 > 1) {
            LOG.error("Time to clean pastTrack: "+(timeE-timeS)/1000);
        }
        

        // Publish update
        publishUpdate(vesselTarget);
    }

    public Map<Long, IPastTrackShore> getPastTrack() {
        return pastTrack;
    }

}
