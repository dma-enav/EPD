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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.util.function.Consumer;

/**
 * Utility for collecting tracks from AIS feed.
 */
public class TrackCollector implements Consumer<AisMessage> {

    static final Logger LOG = LoggerFactory.getLogger(TrackCollector.class);

    private long mmsi;
    private List<TimePoint> track = new ArrayList<TimePoint>();

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
        Date timestamp = aisMessage.getVdm().getTimestamp();
        if (timestamp == null) {
            LOG.error("No timestamp in GH source tag for position message: " + aisMessage.getVdm().getOrgLinesJoined());
            return;
        }

        AisPositionMessage posMessage = (AisPositionMessage) aisMessage;
        if (posMessage.isPositionValid()) {
            track.add(new TimePoint(posMessage.getPos().getGeoLocation(), timestamp));
        }
    }

    public List<TimePoint> getSortedTrack() {
        Collections.sort(track);
        return track;
    }

}
