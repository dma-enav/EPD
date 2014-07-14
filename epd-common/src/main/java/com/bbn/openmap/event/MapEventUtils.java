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
        // Sanity check
        if (graphic == null) {
            return null;
        }
        
        OMList<OMGraphic> allClosest;
        synchronized (graphic) {
            allClosest = graphic.findAll(evt.getX(), evt.getY(), limit);
        }
        if (allClosest.size() > 0 && types.length == 0) {
            return allClosest.get(0);
        }
        for (Class<?> type : types) {
            for (OMGraphic g : allClosest) {
                if (type.isAssignableFrom(g.getClass())) {
                    return g;
                }
            }
        }
        return null;
    }    
}
