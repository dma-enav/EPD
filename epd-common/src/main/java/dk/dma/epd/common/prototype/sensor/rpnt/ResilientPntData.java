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
