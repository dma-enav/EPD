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
package dk.dma.epd.common.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import dk.dma.enav.model.geometry.Position;

/**
 * Utility functionality for handling the safe haven
 */
public class SafeHavenUtils {

    public static final Color SF_COLOR_GRAY     = new Color(150, 150, 150);
    public static final Color SF_COLOR_GREEN    = new Color(80, 180, 80);
    public static final Color SF_COLOR_YELLOW   = new Color(180, 180, 70);
    public static final Color SF_COLOR_RED      = new Color(180, 80, 80);
    
    /**
     * Calculates the safe haven corners based on the parameters.
     * If the {@code bounds} parameter is null, a new list is created and returned.
     * 
     * @param position the safe haven center position
     * @param bearing the safe haven bearing
     * @param width the safe haven in knots
     * @param length the safe haven height in knots
     * @param bounds either null, or the list to use
     * @return the updated list of safe have corners
     */
    public static List<Position> calculateBounds(Position position, double bearing, double width, double length, List<Position> bounds) {
        
        // If not polygon list is passed along, create a new list
        if (bounds == null) {
            bounds = new ArrayList<>(4);
        } else {
            bounds.clear();
        }

        double angle = 90 + bearing;
        if (angle > 360) {
            angle = angle - 360;
        }
        
        double oppositeBearing = 180 + bearing;
        if (oppositeBearing > 360) {
            oppositeBearing = oppositeBearing - 360;
        }
        
        Position topLinePt = Calculator.findPosition(position, bearing, length / 2);
        Position bottomLinePt = Calculator.findPosition(position, oppositeBearing, length / 2);

        bounds.add(Calculator.findPosition(bottomLinePt, angle, width / 2));
        bounds.add(Calculator.findPosition(topLinePt, angle, width / 2));
        bounds.add(Calculator.findPosition(topLinePt, angle + 180, width / 2));
        bounds.add(Calculator.findPosition(bottomLinePt, angle + 180, width / 2));

        return bounds;
    }


}
