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
package dk.dma.epd.common.prototype.enavcloud.intendedroute;

import java.util.Date;

public class Waypoint {
    
    private double latitude;
    private double longitude;
    private Double rot;
    private Date eta;
    private Double turnRad;
    private Leg outLeg;
    
    public Waypoint() {
        
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getRot() {
        return rot;
    }

    public void setRot(Double rot) {
        this.rot = rot;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Double getTurnRad() {
        return turnRad;
    }

    public void setTurnRad(Double turnRad) {
        this.turnRad = turnRad;
    }
    
    public Leg getOutLeg() {
        return outLeg;
    }
    
    public void setOutLeg(Leg outLeg) {
        this.outLeg = outLeg;
    }
    
}
