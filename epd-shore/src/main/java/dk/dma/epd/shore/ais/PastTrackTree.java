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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;

/**
 * Ripping off PastTrackSortedSet from dk.dma.ais.data.PastTrackSortedSet, refactor into one common codebase later
 * @author jtj-sfs
 *
 */
public class PastTrackTree extends TreeSet<PastTrackPoint> implements IPastTrackShore, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void addPosition(Position vesselPosition, int minDist) {
        PastTrackPoint last = null;
        try {
            last = this.last();
        } catch (NoSuchElementException e) {
            
        }
        
        if (last == null) {
            this.add(new PastTrackPoint(new Date(), vesselPosition));
        }
        else if (last.getPosition().distanceTo(vesselPosition, CoordinateSystem.CARTESIAN) > minDist) {
            this.add(new PastTrackPoint(new Date(), vesselPosition));
        }
        

    }

    @Override
    public void cleanup(int ttl) {
        while (size() > 0 && first().isDead(ttl)) {
            pollFirst();
        }
    }

    @Override
    public Collection<PastTrackPoint> getPoints() {
        return this;
    }

}
