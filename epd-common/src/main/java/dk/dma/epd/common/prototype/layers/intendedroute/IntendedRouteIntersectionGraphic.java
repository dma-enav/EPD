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
