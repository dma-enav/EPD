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
package dk.dma.epd.common.prototype.gui.metoc;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.graphics.CenterRaster;

/**
 * Abstract base class for metoc raster images
 */
public abstract class MetocRaster extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    public MetocRaster() {
        super();
        setVague(true);
    }

    /**
     * Places the raster correctly. Requirements for the raster: -Width and
     * height must be uneven -If image is asymmetric (eg. an arrow with origin
     * in center of image), an equal amount of empty pixels (+1) must exist on
     * the opposite side of the image
     * 
     * @param rasterURI
     *            Location of the raster
     * @param lat
     *            Position of the raster's vertical center
     * @param lon
     *            Position of the raster's horizontal center
     * @param angle
     *            Rotational angle in radians
     */
    public void addRaster(String rasterURI, double lat, double lon, double angle) {
        ImageIcon imageIcon = new ImageIcon(MetocRaster.class.getResource(rasterURI));
        
        int imageWidth = imageIcon.getIconWidth();
        int imageHeight = imageIcon.getIconHeight();
        CenterRaster rasterMark = new CenterRaster(lat, lon, imageWidth,
                imageHeight, imageIcon);
        // rasterMark.setStroke(new BasicStroke());
        // rasterMark.setSelected(true);
        rasterMark.setRotationAngle(angle);
        add(rasterMark);
    }

}
