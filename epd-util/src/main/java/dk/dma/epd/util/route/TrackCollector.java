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
package dk.dma.epd.util.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.util.function.Consumer;

/**
 * Utility for collecting tracks from AIS feed.
 */
public class TrackCollector implements Consumer<AisMessage>{
    
    private static final double FIXED_SPEED = (12.0 * 1852.0) / 3600.0; // 12 knots
    
    private final long mmsi;
    private final List<TimePoint> track = new ArrayList<>();
    private Position lastPos;
    private Date time;
    
    public TrackCollector(long mmsi) {
        this.mmsi = mmsi;
        this.time = new Date();
    }

    @Override
    public void accept(AisMessage aisMessage) {
        Date timestamp = null;
        if (aisMessage.getUserId() != mmsi) {
            return;
        }
        if (!(aisMessage instanceof AisPositionMessage)) {
            return;
        }
        if (aisMessage.getSourceTag() != null) {
            timestamp = aisMessage.getSourceTag().getTimestamp();            
        }
        
        AisPositionMessage posMessage = (AisPositionMessage)aisMessage;
        Position pos = posMessage.getPos().getGeoLocation();
        
        //System.out.println("----");
        //System.out.println("time     : " + time);
        if (timestamp == null) {
            // We will try to calculate the time
            if (lastPos == null) {
                timestamp = time;
            } else {
                double t = lastPos.rhumbLineDistanceTo(pos) / FIXED_SPEED;
                timestamp = new Date(time.getTime() + (long)(t * 1000));
            }            
        }        
        //System.out.println("timestamp: " + timestamp);
        time = timestamp;
        lastPos = pos;
        
        
        TimePoint point = new TimePoint(pos, timestamp);
        track.add(point);
    }
    
    public List<TimePoint> getSortedTrack() {
        Collections.sort(track);
        return track;
    }

}
