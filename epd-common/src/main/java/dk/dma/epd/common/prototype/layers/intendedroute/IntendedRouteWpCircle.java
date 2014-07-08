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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.BasicStroke;
import java.awt.Color;

import dk.dma.epd.common.prototype.layers.common.WpCircle;

/**
 * Graphic for intended route WP circle
 */
public class IntendedRouteWpCircle extends WpCircle {
    private static final long serialVersionUID = 1L;

    private IntendedRouteGraphic intendedRouteGraphic;
    private int index;

    public IntendedRouteWpCircle(IntendedRouteGraphic intendedRouteGraphic, int index, double latitude, double longitude, Color color, float scale) {
        super(latitude, longitude, 0, 0, (int)(18.0 * scale), (int)(18.0 * scale));
        setStroke(new BasicStroke(3.0f * scale));
        setLinePaint(color);
        this.index = index;
        this.intendedRouteGraphic = intendedRouteGraphic;
    }

    public int getIndex() {
        return index;
    }

    public IntendedRouteGraphic getIntendedRouteGraphic() {
        return intendedRouteGraphic;
    }
    
}
