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
package dk.dma.epd.common.prototype.model.route;

import java.io.Serializable;

/**
 * Class used to wrap information on how TTG is calculated for a route.
 * @author Janus Varmarken
 */
public class RouteTtgData implements Serializable {
    
    /**
     * default.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Current speed of vessel.
     */
    private double currSpeed;
    
    /**
     * The current TTG calculation mode.
     */
    private TtgCalculation ttgCalcType;
    
    /**
     * Holds whether this instance was updated with speed data at some point.
     */
    private boolean speedUpdated;
    
    /**
     * Enum used to swap between TTG calculation modes.
     * @author Janus Varmarken
     *
     */
    public enum TtgCalculation {
        PLANNED_SPEED {
            @Override
            public String toString() {
                return "Planned speed"; 
            }
        },
        DYNAMIC_SPEED {
            @Override
            public String toString() {
                return "Current speed";
            }  
        },
        HYBRID {
            @Override
            public String toString() {
                return "Hybrid";
            }
        }
    }
    
    /**
     * Check if a TtgCalculation requires a current speed value.
     * @param type The TtgCalculation to check.
     * @return True if speed is required, false otherwise.
     */
    public static boolean isCurrentSpeedRequired(TtgCalculation type) {
        switch(type) {
        case PLANNED_SPEED:
            return false;
        case DYNAMIC_SPEED:
            return true;
        case HYBRID:
            return true;
        default:
            return true;
        }
    }
    
    public RouteTtgData(TtgCalculation type) {
        if(type == null) {
            throw new IllegalArgumentException(TtgCalculation.class.getSimpleName() + " param cannot be null.");
        }
        this.ttgCalcType = type;
    }
    
    /**
     * Get current speed of vessel.
     * @return The current speed of the vessel.
     */
    public double getCurrentSpeed() {
        return this.currSpeed;
    }
    
    /**
     * Set current speed of vessel.
     * @param currentSpeed The updated current speed.
     */
    public void setCurrentSpeed(double currentSpeed) {
        this.speedUpdated = true;
        this.currSpeed = currentSpeed;
    }
    
    /**
     * Was the speed value ever updated for this instance or is it still the default value?
     * @return True if the speed was updated using the setter, false otherwise.
     */
    public boolean hasCurrentSpeed() {
        return this.speedUpdated;
    }
    
    /**
     * Get the current TTG calculation mode.
     * @return The current TTG calculation mode.
     */
    public TtgCalculation getTtgCalculationType() {
        return this.ttgCalcType;
    }
    
    /**
     * Set the current TTG calculation mode.
     * @param type The updated TTG calculation mode.
     */
    public void setTtgCalculationType(TtgCalculation type) {
        this.ttgCalcType = type;
    }
    
    /**
     * Checks if data is available for TTG calculation.
     * (fx if the TTG calculation type requires current
     * speed to be sat)
     *  @return true if required data is available, false otherwise.
     */
    public boolean isTtgCalcDataAvailable() {
        if(this.hasCurrentSpeed()) {
            // if current speed has been set, we can perform all types of TTG calculations.
            return true;
        }
        else if(this.ttgCalcType == TtgCalculation.PLANNED_SPEED) {
            // we can always perform TTG calculations using planned speed
            return true;
        }
        // we cannot perform TTG calculations that considers current speed
        // if current speed has not been set
        return false;
    }
}
