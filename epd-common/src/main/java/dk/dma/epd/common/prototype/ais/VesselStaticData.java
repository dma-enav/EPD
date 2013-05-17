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
package dk.dma.epd.common.prototype.ais;

import java.io.Serializable;

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.AisMessage24;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.ShipTypeCargo;

/**
 * Class representing the static data of an AIS vessel target
 */
@ThreadSafe
public class VesselStaticData implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private long imo;
    private String callsign;
    private String name;
    private ShipTypeCargo shipType = new ShipTypeCargo(-1);
    private int dimBow;
    private int dimStern;
    private int dimPort;
    private int dimStarboard;
    private int posType;
    private long eta;
    private float draught;
    private String destination;

    /**
     * Copy constructor
     * @param vesselStaticData
     */
    public VesselStaticData(VesselStaticData vesselStaticData) {
        this.imo = vesselStaticData.imo;
        this.callsign = vesselStaticData.callsign;        
        this.name = vesselStaticData.name;
        this.shipType = vesselStaticData.shipType;
        this.dimBow = vesselStaticData.dimBow;
        this.dimStern = vesselStaticData.dimStern;
        this.dimPort = vesselStaticData.dimPort;
        this.dimStarboard = vesselStaticData.dimStarboard;
        this.posType = vesselStaticData.posType;
        this.eta = vesselStaticData.eta;
        this.draught = vesselStaticData.draught;
        this.destination = vesselStaticData.destination;
    }

    /**
     * Constructor given AIS message #5
     * @param msg5
     */
    public VesselStaticData(AisMessage5 msg5) {
        imo = msg5.getImo();
        callsign = msg5.getCallsign();
        name = msg5.getName();
        shipType = new ShipTypeCargo(msg5.getShipType());
        dimBow = msg5.getDimBow();
        dimStern = msg5.getDimStern();
        dimPort = msg5.getDimPort();
        dimStarboard = msg5.getDimStarboard();
        posType = msg5.getPosType();
        eta = msg5.getEta();
        draught = msg5.getDraught();
        destination = msg5.getDest();
    }
    
    /**
     * Constructor given AIS message #24
     * @param msg24
     */
    public VesselStaticData(AisMessage24 msg24) {
        update(msg24);
    }
    
    /**
     * Update static data given an AIS message #24
     * @param msg24
     */
    public synchronized void update(AisMessage24 msg24) {
        if (msg24.getPartNumber() == 0) {
            // part A
            this.name = msg24.getName();
            return;
        }
        // part B
        callsign = msg24.getCallsign();
        shipType = new ShipTypeCargo(msg24.getShipType());
        dimBow = msg24.getDimBow();
        dimStern = msg24.getDimStern();
        dimPort = msg24.getDimPort();
        dimStarboard = msg24.getDimStarboard();        
    }

    public synchronized long getImo() {
        return imo;
    }

    public synchronized void setImo(long imo) {
        this.imo = imo;
    }

    public synchronized String getCallsign() {
        return callsign;
    }

    public synchronized void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized ShipTypeCargo getShipType() {
        return shipType;
    }

    public synchronized void setShipType(ShipTypeCargo shipType) {
        this.shipType = shipType;
    }

    public synchronized int getDimBow() {
        return dimBow;
    }

    public synchronized void setDimBow(int dimBow) {
        this.dimBow = dimBow;
    }

    public synchronized int getDimStern() {
        return dimStern;
    }

    public synchronized void setDimStern(int dimStern) {
        this.dimStern = dimStern;
    }

    public synchronized int getDimPort() {
        return dimPort;
    }

    public synchronized void setDimPort(int dimPort) {
        this.dimPort = dimPort;
    }

    public synchronized int getDimStarboard() {
        return dimStarboard;
    }

    public synchronized void setDimStarboard(int dimStarboard) {
        this.dimStarboard = dimStarboard;
    }

    public synchronized int getPosType() {
        return posType;
    }

    public synchronized void setPosType(int posType) {
        this.posType = posType;
    }

    public synchronized long getEta() {
        return eta;
    }

    public synchronized void setEta(long eta) {
        this.eta = eta;
    }

    public synchronized float getDraught() {
        return draught;
    }

    public synchronized void setDraught(float draught) {
        this.draught = draught;
    }

    public synchronized String getDestination() {
        return destination;
    }

    public synchronized void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VesselStaticData [callsign=");
        builder.append(callsign);
        builder.append(", destination=");
        builder.append(destination);
        builder.append(", dimBow=");
        builder.append(dimBow);
        builder.append(", dimPort=");
        builder.append(dimPort);
        builder.append(", dimStarboard=");
        builder.append(dimStarboard);
        builder.append(", dimStern=");
        builder.append(dimStern);
        builder.append(", draught=");
        builder.append(draught);
        builder.append(", eta=");
        builder.append(eta);
        builder.append(", imo=");
        builder.append(imo);
        builder.append(", name=");
        builder.append(name);
        builder.append(", posType=");
        builder.append(posType);
        builder.append(", shipType=");
        builder.append(shipType);
        builder.append("]");
        return builder.toString();
    }
    
}
