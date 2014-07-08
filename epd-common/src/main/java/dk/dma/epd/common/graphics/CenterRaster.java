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
package dk.dma.epd.common.graphics;

import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.proj.Projection;

/**
 * A raster graphic that is centered
 */
public class CenterRaster extends OMRaster {
    private static final long serialVersionUID = 1L;

    private Point2D center;
    private Boolean notGeolocation = false;
    private float radius = 5;
    private int x;
    private int y;

    /**
     * Position in lat,lon with width i and height j
     * @param lat
     * @param lon
     * @param i
     * @param j
     * @param imageIcon
     */
    public CenterRaster(double lat, double lon, int i, int j, ImageIcon imageIcon) {
        super(lat, lon, -(i / 2), -(j / 2), imageIcon);
    }
    
    /**
     * Position in x,y
     * @param x
     * @param y
     * @param imageIcon
     */
    public CenterRaster(int x, int y, ImageIcon imageIcon) {
        super(x-imageIcon.getIconWidth() / 2, y-imageIcon.getIconHeight() / 2, imageIcon);
        this.x = x;
        this.y = y;
        notGeolocation = true;
    }

    /**
     * Set the radius to be used in distance calculations
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public boolean generate(Projection proj) {
        if(notGeolocation) {
            center = new Point2D.Double(x, y);
        } else {
            center = proj.forward(getLat(), getLon());
        }
        return super.generate(proj);
    }

    @Override
    public float distance(double mx, double my) {
        if (center == null || getNeedToRegenerate() || shape == null) {
            return Float.MAX_VALUE;
        }

        float dist = (float) Math.sqrt(Math.pow(mx - center.getX(), 2) + Math.pow(my - center.getY(), 2)) - radius;
        if (dist < 0) {
            dist = 0;
        }

        return dist;
    }

    @Override
    public boolean shouldRenderFill() {
        return false;
    }

}
