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
package dk.dma.epd.ship.layers.ais;

import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.ship.EPDShip;

/**
 * MSI mouse over info
 */
public class HighlightInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    public HighlightInfoPanel() {
        super(EPDShip.res().getCachedImageIcon("images/ais/highlight.png"));
    }

    /**
     * Show the image
     */
    public void displayHighlight(int x, int y) {
        setPos(x, y);
        showImage();
    }
}
