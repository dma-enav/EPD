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

/**
 * Defines the filtering parameters used to extract a partial route from
 * a full route.
 */
public class PartialRouteFilter {

    public static final PartialRouteFilter DEFAULT = 
            new PartialRouteFilter(FilterType.MINUTES, 10000, 20000);
    
    /**
     * Defines the filter type, either time in minutes, distance in meters or number of way points
     */
    public enum FilterType { MINUTES, METERS, COUNT }
    
    private FilterType type;
    private int forward;
    private int backward;
    
    /**
     * Constructor
     */
    public PartialRouteFilter(FilterType type, int backward, int forward) {
        super();
        this.type = type;
        this.backward = backward;
        this.forward = forward;
    }
    
    /**
     * Returns the filter type
     * @return the filter type
     */
    public FilterType getType() {
        return type;
    }
    
    public void setType(FilterType type) {
        this.type = type;
    }

    /**
     * Returns the the time or distance forward
     * @return the the time or distance forward
     */
    public int getForward() {
        return forward;
    }
    
    public void setForward(int forward) {
        this.forward = forward;
    }

    /**
     * Returns the the time or distance backward
     * @return the the time or distance backward
     */
    public int getBackward() {
        return backward;
    }
    
    public void setBackward(int backward) {
        this.backward = backward;
    }

    /**
     * Returns the string representation for this filter
     * @return the string representation for this filter 
     */
    @Override
    public String toString() {
        return String.format("%s,%d,%d", type, forward, backward);
    }
    
    /**
     * Parses the given string, which should have been generated
     * using {@linkplain #toString()}, into a {@code PartialRouteFilter} object.
     * <p>
     * Returns null, if the string cannot be parsed
     * 
     * @param str the string to parse
     * @return the parsed {@code PartialRouteFilter} or null
     */
    public static PartialRouteFilter fromString(String str) {
        try {
            String[] vals = str.split(",");
            return new PartialRouteFilter(
                    FilterType.valueOf(vals[0]),
                    Integer.parseInt(vals[1]),
                    Integer.parseInt(vals[2]));
        } catch (Exception ex) {
        }
        return null;
    }
}

