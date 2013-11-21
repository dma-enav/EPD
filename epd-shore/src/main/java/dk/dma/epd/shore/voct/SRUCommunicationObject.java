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
package dk.dma.epd.shore.voct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.VOCTSARBroadCast;
import dk.dma.epd.common.prototype.model.route.Route;

public class SRUCommunicationObject {

    SRU sru;

    // List<VOCTSARBroadCast> sruCommunicationObjects = new
    // ArrayList<VOCTSARBroadCast>();

    List<Position> positions = new ArrayList<Position>();
    List<Date> messageDates = new ArrayList<Date>();

    private double heading;
    private boolean visible;
    private Route intendedSearchPattern;

    public SRUCommunicationObject(SRU sru) {
        this.sru = sru;
    }

    public SRU getSru() {
        return sru;
    }

    public synchronized void addBroadcastMessage(VOCTSARBroadCast message) {
        
        positions.add(Position.create(message.getLat(), message.getLon()));
        this.heading = message.getHeading();

        intendedSearchPattern = new Route(message.getIntendedSearchPattern());
        messageDates.add(new Date(message.getDate()));

        // sruCommunicationObjects.add(message);
    }

    public Date getLastMessageDate() {
        if (messageDates.size() == 0) {
            return null;
        } else {
            return messageDates.get(messageDates.size() - 1);
        }
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible
     *            the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the positions
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * @return the messageDates
     */
    public List<Date> getMessageDates() {
        return messageDates;
    }

    /**
     * @return the heading
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @return the intendedSearchPattern
     */
    public Route getIntendedSearchPattern() {
        return intendedSearchPattern;
    }

}
