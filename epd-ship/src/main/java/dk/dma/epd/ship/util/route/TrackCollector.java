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
package dk.dma.epd.ship.util.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.util.function.Consumer;

/**
 * Utility for collecting tracks from AIS feed.
 */
public class TrackCollector implements Consumer<AisMessage>{
    
    private long mmsi;
    private List<TimePoint> track = new ArrayList<>();  
    
    public TrackCollector(long mmsi) {
        this.mmsi = mmsi;
    }

    @Override
    public void accept(AisMessage aisMessage) {
        if (aisMessage.getUserId() != mmsi) {
            return;
        }
        if (!(aisMessage instanceof AisPositionMessage)) {
            return;
        }
        if (aisMessage.getSourceTag() == null) {
            System.err.println("No GH source tag for position message: " + aisMessage.getVdm().getOrgLinesJoined());
            return;
        }
        Date timestamp = aisMessage.getSourceTag().getTimestamp();
        if (timestamp == null) {
            System.err.println("No timestamp in GH source tag for position message: " + aisMessage.getVdm().getOrgLinesJoined());
            return;
        }
        
        AisPositionMessage posMessage = (AisPositionMessage)aisMessage;
        TimePoint point = new TimePoint(posMessage.getPos().getGeoLocation(), timestamp);
        track.add(point);
    }
    
    public List<TimePoint> getSortedTrack() {
        Collections.sort(track);
        return track;
    }

}
