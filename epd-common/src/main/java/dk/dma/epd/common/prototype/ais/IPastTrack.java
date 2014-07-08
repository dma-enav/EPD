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
package dk.dma.epd.common.prototype.ais;

import java.util.Collection;

import dk.dma.enav.model.geometry.Position;

/**
 * Interface for past track implementations
 * NOTE NOTE: this is almost a copy of package dk.dma.ais.data's IPastTrack
 * This is halfway to commonalizing between the two, 
 * problems: SHORE/EPD make use of toString() which is a pain to update
 * AisHandler, AisLayer make use of non-collection interface like list.get(i) (assume arraylist)
 * <p>
 * 131213: Class moved from epd-shore to epd-common, so that it may be used in epd-ship
 */
public interface IPastTrack{

    /**
     * Add position to past track if it is more than minimum distance from last position
     * 
     * @param vesselPosition
     * @param minDist
     */
    void addPosition(Position vesselPosition, int minDist);

    /**
     * Remove points in past track older than ttl
     * 
     * @param ttl
     */
    void cleanup(int ttl);

    /**
     * Get past track points
     * 
     * @return
     */
    Collection<PastTrackPoint> getPoints();

}
