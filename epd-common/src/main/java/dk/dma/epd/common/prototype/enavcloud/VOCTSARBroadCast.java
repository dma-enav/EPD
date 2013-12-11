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
package dk.dma.epd.common.prototype.enavcloud;

import java.util.Date;

import dk.dma.enav.maritimecloud.broadcast.BroadcastMessage;
import dk.dma.enav.model.voyage.Route;

//import dk.dma.epd.common.prototype.model.route.Route;


public class VOCTSARBroadCast extends BroadcastMessage {

    private Route intendedSearchPattern;

    private double lat;
    private double lon;
    
    private double heading;
    
    private long date;
    
    
    public VOCTSARBroadCast(){
        super();
        
        date = new Date().getTime();
    }
    

    /**
     * @return the intendedSearchPattern
     */
    public Route getIntendedSearchPattern() {
        return intendedSearchPattern;
    }

    /**
     * @param intendedSearchPattern the intendedSearchPattern to set
     */
    public void setIntendedSearchPattern(Route intendedSearchPattern) {
        this.intendedSearchPattern = intendedSearchPattern;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * @return the heading
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @param heading the heading to set
     */
    public void setHeading(double heading) {
        this.heading = heading;
    }


    /**
     * @return the date
     */
    public long getDate() {
        return date;
    }


    
    
}
