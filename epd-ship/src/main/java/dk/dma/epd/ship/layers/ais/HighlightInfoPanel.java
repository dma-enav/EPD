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
