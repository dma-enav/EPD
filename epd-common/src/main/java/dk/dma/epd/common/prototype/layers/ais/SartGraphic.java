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

import dk.dma.epd.common.graphics.CenterRaster;

/**
 * Graphic for AIS SART symbol
 */
public class SartGraphic extends CenterRaster {
    private static final long serialVersionUID = 1L;
    
    private SarTargetGraphic sarTargetGraphic;

    public SartGraphic(double lat, double lon, int i, int j, ImageIcon imageIcon, SarTargetGraphic sarTargetGraphic) {
        super(lat, lon, i, j, imageIcon);
        this.sarTargetGraphic = sarTargetGraphic;
    }
    
    public SarTargetGraphic getSarTargetGraphic() {
        return sarTargetGraphic;
    }

}
