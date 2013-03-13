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
package dk.dma.epd.shore.service;

import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionReply;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.AIS_STATUS;


public class RouteSuggestionData {

    private RouteSuggestionMessage outgoingMsg;
    private RouteSuggestionReply reply;
    private long id;
    private long mmsi;
    private boolean acknowleged;

    private AIS_STATUS status;

    public RouteSuggestionData(RouteSuggestionMessage outgoingMsg,
            RouteSuggestionReply reply, long id, long mmsi, boolean acknowleged,
            AIS_STATUS status) {
        this.outgoingMsg = outgoingMsg;
        this.reply = reply;
        this.id = id;
        this.mmsi = mmsi;
        this.acknowleged = acknowleged;
        this.status = status;
    }

    public RouteSuggestionMessage getOutgoingMsg() {
        return outgoingMsg;
    }

    public void setOutgoingMsg(RouteSuggestionMessage outgoingMsg) {
        this.outgoingMsg = outgoingMsg;
    }

    public RouteSuggestionReply getReply() {
        return reply;
    }

    public void setReply(RouteSuggestionReply reply) {
        this.reply = reply;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMmsi() {
        return mmsi;
    }

    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }

    public boolean isAcknowleged() {
        return acknowleged;
    }

    public void setAcknowleged(boolean acknowleged) {
        this.acknowleged = acknowleged;
    }

    public AIS_STATUS getStatus() {
        return status;
    }

    public void setStatus(AIS_STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RouteSuggestionData [outgoingMsg=" + outgoingMsg + ", reply="
                + reply + ", id=" + id + ", mmsi=" + mmsi + ", acknowleged="
                + acknowleged + ", status=" + status + "]";
    }

}
