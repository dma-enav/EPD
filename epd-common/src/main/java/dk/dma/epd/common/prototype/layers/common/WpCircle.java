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
package dk.dma.epd.common.prototype.layers.common;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;

/**
 * Graphic for a WP circle
 */
public class WpCircle extends OMCircle {
    private static final long serialVersionUID = 1L;

    public WpCircle() {
        super(0, 0, 0, 0, 10, 10);
    }

    public WpCircle(double latitude, double longitude, int offX1, int offY1, int w, int h) {
        super(latitude, longitude, offX1, offY1, w, h);
    }

    @Override
    public float distance(double x, double y) {
        float distance = Float.POSITIVE_INFINITY;

        if (getNeedToRegenerate() || shape == null) {
            return distance;
        }

        float dist = (float) Math.sqrt(Math.pow(x - x1, 2) + Math.pow(y - y1, 2));

        double rad = getWidth() / 2.0;

        if (dist <= rad) {
            dist = 0;
        } else {
            dist -= rad;
        }

        return dist;
    }

}
