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
package dk.dma.epd.common.prototype.model.route;

/**
 * Defines the filtering parameters used to extract a partial route from
 * a full route.
 */
public class PartialRouteFilter {

    public static final PartialRouteFilter DEFAULT = 
            new PartialRouteFilter(FilterType.METERS, 18520, 111120);
    
    /**
     * Defines the filter type, either time in minutes, distance in meters or number of way points
     */
    public enum FilterType { MINUTES, METERS, COUNT }
    
    private FilterType type;
    private int forward;
    private int backward;
    
    /**
     * <b>Do not use this constructor in code.</b>
     * It is provided as it is required when restoring application settings from YAML files. 
     */
    public PartialRouteFilter() {
        
    }
    
    /**
     * Constructor
     */
    public PartialRouteFilter(FilterType type, int forward, int backward) {
        super();
        this.type = type;
        this.forward = forward;
        this.backward = backward;
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

