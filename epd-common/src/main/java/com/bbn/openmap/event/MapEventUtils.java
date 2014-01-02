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
package com.bbn.openmap.event;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

/**
 * Helper class for common Map event functionality
 */
public class MapEventUtils {

    /**
     * Returns the first graphics element placed at the mouse event location
     * that matches any of the types passed along. 
     * 
     * @param graphic the graphic list to search
     * @param evt the mouse event
     * @param limit the max distance from the mouse point
     * @param types the possible types
     * @return the first matching graphics element
     */
    @SafeVarargs
    public static final OMGraphic getSelectedGraphic(OMGraphicList graphic, MouseEvent evt, float limit, Class<?>... types) {
        OMList<OMGraphic> allClosest;
        synchronized (graphic) {
            allClosest = graphic.findAll(evt.getX(), evt.getY(), limit);
        }
        if (allClosest.size() > 0 && types.length == 0) {
            return allClosest.get(0);
        }
        for (OMGraphic g : allClosest) {
            for (Class<?> type : types) {
                if (type.isAssignableFrom(g.getClass())) {
                    return g;
                }
            }
        }
        return null;
    }    
}
