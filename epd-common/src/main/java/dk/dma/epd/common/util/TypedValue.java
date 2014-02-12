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
package dk.dma.epd.common.util;

import java.util.Locale;

/**
 * This classes and its sub-classes implements support 
 * for converting between and within distance, time and 
 * speed based values.
 * <p>
 * Examples:
 * <pre>
 *   // Convert from meters to nautical miles
 *   double nm = new Dist(DistType.METERS, meters)
 *                 .in(DistType.NAUTICAL_MILES).doubleValue();
 *   
 *   // Get the safe haven length of a route leg in milliseconds 
 *   long sflen = new Dist(DistType.METERS, routeLeg.getSFLen())
 *                 .withSpeed(new Speed(SpeedType.KNOTS, routeLeg.getSpeed()))
 *                   .in(TimeType.MILLISECONDS).longValue();
 * </pre>
 */
public abstract class TypedValue<T extends TypedValue.Type> {

    double value;
    T type;

    /**
     * Constructor
     * 
     * @param type the type 
     * @param value the value
     */
    protected TypedValue(T type, double value) {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Returns the type of the value
     * @return the type of the value
     */
    public T getType() {
        return type;
    }

    /**
     * Returns the value as a double
     * @return the value as a double
     */
    public double doubleValue() {
        return value;
    }

    /**
     * Returns the value as a long
     * @return the value as a long
     */
    public long longValue() {
        return Math.round(value);
    }
    
    /**
     * Converts the value from one type to another
     * @param type the type to convert to
     * @return the converted value
     */
    public abstract TypedValue<T> in(T type);

    /**
     * Returns a string representation of the value including type
     * @return a string representation of the value including type
     */
    @Override
    public String toString() {
        return formatDouble(value, 2) + " " + type;
    }
    
    /**
     * Utility method for rounding and formatting a double value
     * @param d the value to format
     * @param decimals the number of decimals to include
     * @return the formatted value
     */
    public static String formatDouble(double d, int decimals) {
        if (decimals == 0) {
            return String.format(Locale.US, "%d", Math.round(d));
        }
        String format = "%." + decimals + "f";
        return String.format(Locale.US, format, d);
    }
    
    /**********************************************/
    /** Type constants                           **/
    /**********************************************/
    
    /**
     * Base class for all types
     */
    protected abstract static class Type {
        String typeShort;
        String typeLong;

        /**
         * Constructor
         * 
         * @param typeShort a short name for the type
         * @param typeLong a long name for the type
         */
        protected Type(String typeShort, String typeLong) {
            this.typeShort = typeShort;
            this.typeLong = typeLong;
        }
        
        /**
         * Returns a string representation of the type
         * @return a string representation of the type
         */
        @Override
        public String toString() {
            return typeShort;
        }
    }
    
    /**
     * Represents a distance-based type
     */
    public static class DistType extends Type {
                
        public static final DistType METERS = new DistType("m", "meters", 1);
        public static final DistType KILOMETERS = new DistType("km", "kilometers", 1000);
        public static final DistType MILES = new DistType("mi", "miles", 1609.344);
        public static final DistType NAUTICAL_MILES = new DistType("nm", "nautical miles", 1852);

        double unitMeters;
        
        /**
         * Constructor
         * 
         * @param typeShort a short name for the type
         * @param typeLong a long name for the type
         * @param unitMeters the number of meters per unit of the type
         */
        public DistType(String typeShort, String typeLong, double unitMeters) {
            super(typeShort, typeLong);
            this.unitMeters = unitMeters;
        }
        
        /**
         * Returns the number of meters per unit of the type
         * @return the number of meters per unit of the type
         */
        public double getUnitMeters() {
            return unitMeters;
        }
    }

    /**
     * Represents a time-based type
     */
    public static class TimeType extends Type {
        
        public static final TimeType MILLISECONDS = new TimeType("ms", "milliseconds", 1.0 / 1000.0);
        public static final TimeType SECONDS = new TimeType("s", "seconds", 1);
        public static final TimeType MINUTES = new TimeType("m", "minutes", 60);
        public static final TimeType HOURS = new TimeType("h", "hours", 60 * 60);

        double unitSeconds;
        
        /**
         * Constructor
         * 
         * @param typeShort a short name for the type
         * @param typeLong a long name for the type
         * @param unitSeconds the number of seconds per unit of the type
         */
        public TimeType(String typeShort, String typeLong, double unitSeconds) {
            super(typeShort, typeLong);
            this.unitSeconds = unitSeconds;
        }
        
        /**
         * Returns the number of seconds per unit of the type
         * @return the number of seconds per unit of the type
         */
        public double getUnitSeconds() {
            return unitSeconds;
        }
    }
    
    /**
     * Represents a speed-based type
     */
    public static class SpeedType extends Type {
        
        public static final SpeedType KNOTS = new SpeedType(DistType.NAUTICAL_MILES, TimeType.HOURS);
        public static final SpeedType KMH = new SpeedType(DistType.KILOMETERS, TimeType.HOURS);
        public static final SpeedType MH = new SpeedType(DistType.MILES, TimeType.HOURS);
        public static final SpeedType MS = new SpeedType(DistType.METERS, TimeType.SECONDS);
        
        DistType distType;
        TimeType timeType;
        
        /**
         * Constructor
         * 
         * @param distType the distance type
         * @param timeType the time type
         */
        public SpeedType(DistType distType, TimeType timeType) {
            this(distType, 
                 timeType, 
                 distType.typeShort + "/" + timeType.typeShort, 
                 distType.typeLong + " pr " + timeType.typeLong);
        }
        
        /**
         * Constructor
         * 
         * @param distType the distance type
         * @param timeType the time type
         * @param typeShort a short name for the type
         * @param typeLong a long name for the type
         */
        public SpeedType(DistType distType, TimeType timeType, String typeShort, String typeLong) {
            super(typeShort, typeLong);
            this.distType = distType;
            this.timeType = timeType;
        }

        /**
         * Returns the distance type of the speed type
         * @return the distance type of the speed type
         */
        public DistType getDistType() {
            return distType;
        }
        
        /**
         * Returns the time type of the speed type
         * @return the time type of the speed type
         */
        public TimeType getTimeType() {
            return timeType;
        }
    }
    
    /**********************************************/
    /** Value types                              **/
    /**********************************************/

    /**
     * Represents a distance value
     */
    public static class Dist extends TypedValue<DistType> {
        
        /**
         * Constructor
         * 
         * @param type the type 
         * @param value the value
         */
        public Dist(DistType type, double value) {
            super(type, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dist in(DistType type) {
            if (type == this.type) {
                return this;
            }
            return new Dist(type, value * this.type.unitMeters / type.unitMeters);
        }
        
        /**
         * Returns the distance in meters
         * @return the distance in meters
         */
        public double meters() {
            return in(DistType.METERS).doubleValue();
        }
        
        /**
         * Returns the amount of time it takes to travel this distance with the given speed
         * @param speed the speed
         * @return the amount of time it takes to travel this distance
         */
        public Time withSpeed(Speed speed) {
            return new Time(
                    TimeType.SECONDS, 
                    meters() / speed.metersPrSecond());
        }
        
        /**
         * Returns the speed needed to travel this distance in the given time
         * @param time the time
         * @return the speed needed to travel this distance in the given time
         */
        public Speed inTime(Time time) {
            return new Speed(
                    SpeedType.MS,
                    meters() / time.seconds());
        }
    }
    

    /**
     * Represents a time value
     */
    public static class Time extends TypedValue<TimeType> {
        
        /**
         * Constructor
         * 
         * @param type the type 
         * @param value the value
         */
        public Time(TimeType type, double value) {
            super(type, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Time in(TimeType type) {
            if (type == this.type) {
                return this;
            }
            return new Time(type, value * this.type.unitSeconds / type.unitSeconds);
        }
        
        /**
         * Returns the time in seconds
         * @return the time in seconds
         */
        public double seconds() {
            return in(TimeType.SECONDS).doubleValue();
        }
        
        /**
         * Returns the distance traveled with the given speed in this amount of time
         * @param speed the speed
         * @return the distance traveled with the given speed in this amount of time
         */
        public Dist withSpeed(Speed speed) {
            return new Dist(
                    DistType.METERS,
                    seconds() * speed.metersPrSecond());
        }
        
        /**
         * Returns the speed required to travel the given distance in this amount of time
         * @param dist the distance
         * @return the speed required to travel the given distance in this amount of time
         */
        public Speed forDist(Dist dist) {
            return new Speed(
                    SpeedType.MS,
                    dist.meters() / seconds());
        }
    }

    /**
     * Represents a speed value
     */
    public static class Speed extends TypedValue<SpeedType> {
        
        /**
         * Constructor
         * 
         * @param type the type 
         * @param value the value
         */
        public Speed(SpeedType type, double value) {
            super(type, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Speed in(SpeedType type) {
            if (type == this.type) {
                return this;
            }
            double ms = value * (this.type.distType.unitMeters / this.type.timeType.unitSeconds);
            double toSpeed = ms / (type.distType.unitMeters / type.timeType.unitSeconds);
            return new Speed(type, toSpeed);
        }
        
        /**
         * Returns the speed in meters per second
         * @return the speed in meters per second
         */
        public double metersPrSecond() {
            return in(SpeedType.MS).doubleValue();
        }
        
        /**
         * Returns the amount of time it takes to travel the given distance with this speed
         * @param dist the distance
         * @return the amount of time it takes to travel the given distance with this speed
         */
        public Time forDist(Dist dist) {
            return new Time(
                    TimeType.SECONDS,
                    dist.meters() / metersPrSecond());
        }
        
        /**
         * Returns the distance traveled over the given time with this speed
         * @param time the time
         * @return the distance traveled over the given time with this speed
         */
        public Dist forTime(Time time) {
            return new Dist(
                    DistType.METERS,
                    time.seconds() * metersPrSecond());
        }
    }
}
