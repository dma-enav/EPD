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
    
    private boolean showIntendedRoute;

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
     * @return the showIntendedRoute
     */
    public boolean isShowIntendedRoute() {
        return showIntendedRoute;
    }

    /**
     * @param showIntendedRoute the showIntendedRoute to set
     */
    public void setShowIntendedRoute(boolean showIntendedRoute) {
        this.showIntendedRoute = showIntendedRoute;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VesselTarget [aisClass=" + aisClass + ", showIntendedRoute=" + showIntendedRoute + ", positionData=" + positionData
                + ", staticData=" + staticData + "]";
    }

    


}
