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
     * @param height the safe haven height in knots
     * @param bounds either null, or the list to use
     * @return the updated list of safe have corners
     */
    public static List<Position> calculateBounds(Position position, double bearing, double width, double height, List<Position> bounds) {
        
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
        
        Position topLinePt = Calculator.findPosition(position, bearing, width / 2);
        Position bottomLinePt = Calculator.findPosition(position, oppositeBearing, width / 2);

        bounds.add(Calculator.findPosition(bottomLinePt, angle, height / 2));
        bounds.add(Calculator.findPosition(topLinePt, angle, height / 2));
        bounds.add(Calculator.findPosition(topLinePt, angle + 180, height / 2));
        bounds.add(Calculator.findPosition(bottomLinePt, angle + 180, height / 2));

        return bounds;
    }


}
