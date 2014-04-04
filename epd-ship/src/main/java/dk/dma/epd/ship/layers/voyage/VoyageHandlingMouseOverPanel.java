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
package dk.dma.epd.ship.layers.voyage;

import dk.dma.epd.common.prototype.gui.util.InfoPanel;

public class VoyageHandlingMouseOverPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public VoyageHandlingMouseOverPanel() {
        super();
    }

    public void showType(int type) {

        if (type == VoyageLayer.ROUTE_INDEX_ORIGINAL_ROUTE) {
            showText("Original Sent Route");
        }
        if (type == VoyageLayer.ROUTE_INDEX_EDITABLE_ROUTE) {
            showText("Editable Route");
        }
        if (type == VoyageLayer.ROUTE_INDEX_STCC_ROUTE) {
            showText("Route suggested by Shore");
        }
        if (type == VoyageLayer.ROUTE_INDEX_CURRENT) {
            showText("Route currently being negotiated");
        }

    }

  
}
