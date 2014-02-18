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
package dk.dma.epd.common.prototype.layers.ais;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;

/**
 * Defines the outline of the selected target
 */
public class AisTargetSelectionGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;
    private CenterRaster selectionGraphics;
    private ImageIcon targetImage;
    private int imageWidth;
    private int imageHeight;

    public AisTargetSelectionGraphic() {
        super();

        createGraphics();

    }

    private void createGraphics() {

        targetImage = new ImageIcon(
                AisTargetSelectionGraphic.class.getResource("/images/ais/highlight.png"));
        imageWidth = targetImage.getIconWidth();
        imageHeight = targetImage.getIconHeight();

        selectionGraphics = new CenterRaster(0, 0, imageWidth, imageHeight,
                targetImage);
    }

    public void moveSymbol(Position pos) {
        remove(selectionGraphics);
        selectionGraphics = new CenterRaster(pos.getLatitude(),
                pos.getLongitude(), imageWidth, imageHeight, targetImage);
        add(selectionGraphics);
    }

    public void removeSymbol() {
        remove(selectionGraphics);
    }

}
