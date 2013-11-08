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
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;

/**
 * Class representing an AIS vessel target
 */
@ThreadSafe
public class VesselTarget extends AisTarget {
    
    private static final long serialVersionUID = 1L;

    /**
     * Time an intended route is considered valid without update
     */
    public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min
    
    /**
     * Target class A or B 
     */
    public enum AisClass {A, B};
    
    private VesselPositionData positionData;    
    private VesselStaticData staticData;
    private AisIntendedRoute aisIntendedRoute;
    private AisClass aisClass; 
    private VesselTargetSettings settings;
    private CloudIntendedRoute intendedRoute;
    

    /**
     * Copy constructor
     * @param vesselTarget
     */
    public VesselTarget(VesselTarget vesselTarget) {
        super(vesselTarget);
        this.aisClass = vesselTarget.aisClass;
        if (vesselTarget.positionData != null) {
            this.positionData = new VesselPositionData(vesselTarget.positionData);
        }
        if (vesselTarget.staticData != null) {
            this.staticData = new VesselStaticData(vesselTarget.staticData);
        }
        if (vesselTarget.aisIntendedRoute != null) {
            this.aisIntendedRoute = new AisIntendedRoute(vesselTarget.aisIntendedRoute);
        }
        if (vesselTarget.settings != null) {
            this.settings = new VesselTargetSettings(vesselTarget.settings);
        }
    }

    /**
     * Empty constructor
     */
    public VesselTarget() {
        super();
        settings = new VesselTargetSettings();
    }
    
    public synchronized VesselPositionData getPositionData() {
        return positionData;
    }

    public synchronized void setPositionData(VesselPositionData positionData) {
        this.positionData = positionData;
        if (aisIntendedRoute != null) {
            aisIntendedRoute.update(positionData);
        }
    }

    public synchronized VesselStaticData getStaticData() {
        return staticData;
    }

    public synchronized void setStaticData(VesselStaticData staticData) {
        this.staticData = staticData;
    }
    
    public synchronized AisIntendedRoute getAisRouteData() {
        return aisIntendedRoute;
    }
    
    public synchronized void setAisRouteData(AisIntendedRoute aisIntendedRoute) {
        this.aisIntendedRoute = aisIntendedRoute;
        this.aisIntendedRoute.update(positionData);
    }
    
    public synchronized void setCloudRouteData(CloudIntendedRoute intendedRoute) {
        this.intendedRoute = intendedRoute;
        this.intendedRoute.update(positionData);
    }
    
    public synchronized CloudIntendedRoute getIntendedRoute() {
        return intendedRoute;
    }

    public synchronized void setIntendedRoute(CloudIntendedRoute intendedRoute) {
        this.intendedRoute = intendedRoute;
    }

    public synchronized AisClass getAisClass() {
        return aisClass;
    }

    public synchronized void setAisClass(AisClass aisClass) {
        this.aisClass = aisClass;
    }
    
    public synchronized VesselTargetSettings getSettings() {
        return settings;
    }
    
    public synchronized void setSettings(VesselTargetSettings settings) {
        this.settings = settings;
    }
    
    /**
     * Returns true if route information changes from valid to invalid
     * @return
     */
    public synchronized boolean checkAisRouteData() {
        if (aisIntendedRoute == null || aisIntendedRoute.getWaypoints().size() == 0 || aisIntendedRoute.getDuration() == 0) {
            return false;
        }
        Date now = PntTime.getInstance().getDate();
        long elapsed = now.getTime() - aisIntendedRoute.getReceived().getTime();
        if (elapsed > ROUTE_TTL) {
            aisIntendedRoute = null;
            return true;
        }
        return false;        
    }
    
    public synchronized boolean hasIntendedRoute() {
        return intendedRoute != null;
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
