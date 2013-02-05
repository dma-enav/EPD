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
package dk.dma.epd.shore.service.ais;

import java.util.Date;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.service.ais.AisServices.AIS_STATUS;

public class RouteSuggestionData {

    private long mmsi;
    private Route route;
    private Date timeSent;
    private AIS_STATUS status;
    private int id;
    private boolean acknowleged;
    private Date appAck;

    public RouteSuggestionData(int id, int mmsi, Route route, Date timeSent, AIS_STATUS status, boolean acknowleged, Date appAck){
        this.mmsi = mmsi;
        this.route = route;
        this.timeSent = timeSent;
        this.status = status;
        this.id = id;
        this.acknowleged = acknowleged;
        this.appAck = appAck;
    }



    public Date getAppAck() {
        return appAck;
    }



    public void setAppAck(Date appAck) {
        this.appAck = appAck;
    }



    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }



    public long getMmsi() {
        return mmsi;
    }

    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public AIS_STATUS getStatus() {
        return status;
    }

    public void setStatus(AIS_STATUS status) {
        this.status = status;
    }


    public String toString(){
        return mmsi + " " + route.getName() + " " + status;
    }



    public boolean isAcknowleged() {
        return acknowleged;
    }



    public void setAcknowleged(boolean acknowleged) {
        this.acknowleged = acknowleged;
    }


}
