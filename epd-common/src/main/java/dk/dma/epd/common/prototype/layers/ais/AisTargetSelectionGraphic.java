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
package dk.dma.epd.common.prototype.layers.ais;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.EPD;

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

        targetImage = EPD.res().getCachedImageIcon("images/ais/highlight.png");
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
