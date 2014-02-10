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
package dk.dma.epd.common.prototype.layers.intendedroute;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;

/**
 * Graphic for MSI symbol
 */
public class IntendedRouteIntersectionGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    public IntendedRouteIntersectionGraphic(Position pos) {

        setVague(true);
        createSymbol(pos);
        this.setVisible(true);
    }

    public void createSymbol(Position pos) {
        CenterRaster intersectSymbol;
        ImageIcon intersectSymbolImage;
        int imageWidth;
        int imageHeight;

        intersectSymbolImage =new ImageIcon(getClass().getResource("/images/intendedroute/intersect.png"));
//                new ImageIcon(getClass().getResource("/images/msi/msi_symbol_32.png"));
//                
        imageWidth = intersectSymbolImage.getIconWidth();
        imageHeight = intersectSymbolImage.getIconHeight();

        intersectSymbol = new CenterRaster(pos.getLatitude(), pos.getLongitude(), imageWidth, imageHeight, intersectSymbolImage);
        add(intersectSymbol);
    }

}
