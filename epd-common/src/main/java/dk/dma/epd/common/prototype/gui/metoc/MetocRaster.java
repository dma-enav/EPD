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
