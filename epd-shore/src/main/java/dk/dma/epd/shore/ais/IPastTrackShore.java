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

import java.util.Collection;

import dk.dma.enav.model.geometry.Position;

/**
 * Interface for past track implementations
 * NOTE NOTE: this is almost a copy of package dk.dma.ais.data's IPastTrack
 * This is halfway to commonalizing between the two, 
 * problems: SHORE/EPD make use of toString() which is a pain to update
 * AisHandler, AisLayer make use of non-collection interface like list.get(i) (assume arraylist)
 */
public interface IPastTrackShore{

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
