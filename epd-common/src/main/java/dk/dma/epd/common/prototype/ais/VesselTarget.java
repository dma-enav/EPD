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

import java.util.Date;

import net.jcip.annotations.ThreadSafe;

/**
 * Class representing an AIS vessel target
 */
@ThreadSafe
public class VesselTarget extends MobileTarget {
    
    private static final long serialVersionUID = -5911356750376325979L;
    
    
    /**
     * Target class A or B 
     */
    public enum AisClass {A, B};
    
    // NB: We do not want to persist intended route data
    private transient AisClass aisClass; 

    /**
     * Copy constructor
     * @param vesselTarget
     */
    public VesselTarget(VesselTarget vesselTarget) {
        super(vesselTarget);
        this.aisClass = vesselTarget.aisClass;
    }

    /**
     * Empty constructor
     */
    public VesselTarget() {
        super();
    }
    
    @Override
    public synchronized void setPositionData(VesselPositionData positionData) {
        super.setPositionData(positionData);
    }

    public synchronized AisClass getAisClass() {
        return aisClass;
    }

    public synchronized void setAisClass(AisClass aisClass) {
        this.aisClass = aisClass;
    }
    
    /**
     * Determine if the target has gone.
     * @param now will be used as current time
     * @param strict when strict is false more relaxed rules will used suitable for down sampled data
     * @return if the target has gone  
     */
    @Override
    public synchronized boolean hasGone(Date now, boolean strict) {
        long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;
        
        // Base gone "loosely" on ITU-R Rec M1371-4 4.2.1
        long tol = 10;
        float sog = positionData.getSog();
        boolean anchorOrMoored = positionData.getNavStatus() == 1 || positionData.getNavStatus() == 5;
        
        if (aisClass == AisClass.A) {
            if (anchorOrMoored) {
                if (sog > 3) {
                    tol = 10;
                }
                tol = 180;
            } else if (sog <= 14) {
                tol = 10;
            } else {
                tol = 6;
            }
        } else {
            if (sog <= 2) {
                tol = 180;
            } else if (sog <= 14) {
                tol = 30;
            } else {
                tol = 15;
            }
        }
        
        // Some home made tolerance rules
        if (strict) {
            tol *= 4;
            if (tol < 120) {
                tol = 120;
            }
        } else {
            // Allow for long timeout
            tol = 600; // 10 minutes
        }
        
        return elapsed > tol;
    }

    /**
     * Returns a string representation of this target
     * @return a string representation of this target
     */
    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VesselTarget [aisClass=");
        builder.append(aisClass);
        builder.append(", positionData=");
        builder.append(positionData);
        builder.append(", staticData=");
        builder.append(staticData);
        builder.append("]");
        return builder.toString();
    }
    
}
