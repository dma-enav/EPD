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

import java.util.Date;

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage21;
import dk.dma.enav.model.geometry.Position;

/**
 * Class representing a AtoN target
 */
@ThreadSafe
public class AtoNTarget extends AisTarget {
    
    private static final long serialVersionUID = 1L;
    
    private Position pos;
    private AtoNTargetType atonType;
    private String name;
    private int posAcc;
    private int dimBow;
    private int dimStern;
    private int dimPort;
    private int dimStarboard;
    private int posType;
    private int offPosition;
    private int regional;
    private int raim;
    private int virtual;
    private int assigned;
    private String nameExt;
    
    /**
     * Empty constructor
     */
    public AtoNTarget() {
        super();
    }
    
    /**
     * Copy constructor
     * @param atoNTarget
     */
    public AtoNTarget(AtoNTarget atoNTarget) {
        super(atoNTarget);
        pos = atoNTarget.pos;
        atonType = atoNTarget.atonType;
        name = atoNTarget.name;
        posAcc = atoNTarget.posAcc;
        dimBow = atoNTarget.dimBow;
        dimStern = atoNTarget.dimStern;
        dimPort = atoNTarget.dimPort;
        dimStarboard = atoNTarget.dimStarboard;
        posType = atoNTarget.posType;
        offPosition = atoNTarget.offPosition;
        regional = atoNTarget.regional;
        raim = atoNTarget.raim;
        virtual = atoNTarget.virtual;
        assigned = atoNTarget.assigned;
        nameExt = atoNTarget.nameExt;
    }
    
    /**
     * Update AtoN target given AIS message #21
     * @param msg21
     */
    public synchronized void update(AisMessage21 msg21) {
        pos = msg21.getPos().getGeoLocation();
        int atonTypeCode = msg21.getAtonType();
        // TODO add null check or "crash and burn"? (if type code is invalid)
        this.atonType = AtoNTargetType.getAtoNTargetTypeFromTypeCode(atonTypeCode);
        name = msg21.getName();
        posAcc = msg21.getPosAcc();
        dimBow = msg21.getDimBow();
        dimStern = msg21.getDimStern();
        dimPort = msg21.getDimPort();
        dimStarboard = msg21.getDimStarboard();
        posType = msg21.getPosType();
        offPosition = msg21.getOffPosition();
        regional = msg21.getRegional();
        raim = msg21.getRaim();
        virtual = msg21.getVirtual();
        assigned = msg21.getAssigned();
        nameExt = msg21.getNameExt();
    }
    
    /**
     * Determine if AtoN target has gone
     */
    @Override
    public synchronized boolean hasGone(Date now, boolean strict) {
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;        
        // Base gone "loosely" on ITU-R Rec M1371-4 4.2.1  (3 minutes)
        long tol = 600; // 10 minutes
        return elapsed > tol;
    }
    
    public synchronized Position getPos() {
        return pos;
    }

    public synchronized AtoNTargetType getAtonType() {
        return atonType;
    }

    public synchronized void setAtonType(AtoNTargetType atonType) {
        this.atonType = atonType;
    }

    public String getTrimmedName() {
        return AisMessage.trimText(getName());
    }
    
    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized int getPosAcc() {
        return posAcc;
    }

    public synchronized void setPosAcc(int posAcc) {
        this.posAcc = posAcc;
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

    public synchronized int getOffPosition() {
        return offPosition;
    }

    public synchronized void setOffPosition(int offPosition) {
        this.offPosition = offPosition;
    }

    public synchronized int getRegional() {
        return regional;
    }

    public synchronized void setRegional(int regional) {
        this.regional = regional;
    }

    public synchronized int getRaim() {
        return raim;
    }

    public synchronized void setRaim(int raim) {
        this.raim = raim;
    }

    public synchronized int getVirtual() {
        return virtual;
    }

    public synchronized void setVirtual(int virtual) {
        this.virtual = virtual;
    }

    public synchronized int getAssigned() {
        return assigned;
    }

    public synchronized void setAssigned(int assigned) {
        this.assigned = assigned;
    }

    public synchronized String getNameExt() {
        return nameExt;
    }

    public synchronized void setNameExt(String nameExt) {
        this.nameExt = nameExt;
    }

    public synchronized void setPos(Position pos) {
        this.pos = pos;
    }
    
}
