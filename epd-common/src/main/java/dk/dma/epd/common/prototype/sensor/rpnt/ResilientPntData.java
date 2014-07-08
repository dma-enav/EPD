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
package dk.dma.epd.common.prototype.sensor.rpnt;

import java.io.Serializable;

import dk.dma.epd.common.prototype.sensor.nmea.PntSource;
import dk.dma.epd.common.util.EnumUtils;
import net.jcip.annotations.Immutable;

/**
 * Class representing RPNT data, which is sent by the multi-source positioning service.
 * <p>
 * The data indicates which PNT source to use along with the current error state of 
 * the source signal.
 */
@Immutable
public class ResilientPntData implements Serializable {

    private static final long serialVersionUID = 1615343877277060937L;

    private PntSource pntSource;
    private JammingFlag jammingFlag;
    private double hpl;
    private ErrorEllipse errorEllipse;

    /**
     * Constructor
     * 
     * @param pntSource the PNT source
     * @param jammingFlag the GPS jamming flag
     * @param hpl the horizontal protection level
     * @param errorEllipseMajorAxis the size of the error-ellipse major axis in meters
     * @param errorEllipseMinorAxis the size of the error-ellipse minor axis in meters
     * @param errorEllipseBearinf the bearing of the error-ellipse major axis from True North in degrees
     */
    public ResilientPntData(PntSource pntSource, JammingFlag jammingFlag, double hpl, 
            double errorEllipseMajorAxis, double errorEllipseMinorAxis, double errorEllipseBearing) {
        this(pntSource, 
             jammingFlag, 
             hpl, 
             new ErrorEllipse(
                     errorEllipseMajorAxis, 
                     errorEllipseMinorAxis, 
                     errorEllipseBearing));
    }
    
    /**
     * Constructor
     * 
     * @param pntSource the PNT source
     * @param jammingFlag the GPS jamming flag
     * @param hpl the horizontal protection level
     * @param errorEllipse the error ellipse
     */
    public ResilientPntData(PntSource pntSource, JammingFlag jammingFlag, double hpl, ErrorEllipse errorEllipse) {
        this.pntSource = pntSource;
        this.jammingFlag = jammingFlag;
        this.hpl = hpl;
        this.errorEllipse = errorEllipse;
    }
    
    public PntSource getPntSource() {
        return pntSource;
    }

    public JammingFlag getJammingFlag() {
        return jammingFlag;
    }

    public double getHpl() {
        return hpl;
    }

    public ErrorEllipse getErrorEllipse() {
        return errorEllipse;
    }

    /**
     * Returns a string representation of this entity
     * @return a string representation of this entity
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RpntData");
        builder.append(" [pntSource=").append(pntSource);
        builder.append(", jammingFlag=").append(jammingFlag);
        builder.append(", hpl=").append(hpl);
        builder.append(", errorEllipse=").append(errorEllipse);
        builder.append("]");
        return builder.toString();
    }

    /*********** Helper classes and Enumerations ************/
    
    /**
     * Defines the possible GPS jamming flags
     */
    public enum JammingFlag implements EnumUtils.KeyedEnum<String> {
        OK("A"),
        JAMMING("J"),
        SPOOFING("S");
        
        private String key;
        private JammingFlag(String key) { this.key = key; }
        @Override public String getKey() { return key; }
    }
    
    /**
     * Represents an error-ellipse
     */
    @Immutable
    public static class ErrorEllipse implements Serializable {
        private static final long serialVersionUID = 6519708372319736290L;
        double majorAxis;
        double minorAxis;
        double bearing;
        
        /**
         * Constructor
         * 
         * @param majorAxis
         * @param minorAxis
         * @param bearing
         */
        public ErrorEllipse(double majorAxis, double minorAxis, double bearing) {
            this.majorAxis = majorAxis;
            this.minorAxis = minorAxis;
            this.bearing = bearing;
        }
        
        public double getMajorAxis() { return majorAxis; }
        public double getMinorAxis() { return minorAxis; }
        public double getBearing() { return bearing; }

        /**
         * Converts the bearing to OpenMap rotation angle in radians
         * @return the bearing as an OpenMap rotation angle
         */
        public double getOMBearing() {
            // the bearing is relative to true North.
            // In OM, the ellipse major axis is horizontal at 0 radians. 
            return Math.toRadians(getBearing() - 90);
        }
        
        @Override
        public String toString() {
            return new StringBuilder()
                .append("ErrorEllipse [majorAxis=").append(majorAxis)
                .append(", minorAxis=").append(minorAxis)
                .append(", bearing=").append(bearing)
                .append("]").toString();
        }
    }    
}
