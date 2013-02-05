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
package dk.dma.epd.ship.layers.nogo;

import java.awt.Color;
import java.util.Date;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class NogoGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private NogoPolygon polygon;
    
    //private MsiTextBox msiTextBox;
    
    public NogoGraphic(NogoPolygon polygon, Date validFrom, Date validTo, double draught, String message, Position northWest, Position southEast, int errorCode, boolean frame, Color color) {
        super();
        
        this.polygon = polygon;
        
        // Create location grahic
        NogoLocationGraphic nogoLocationGraphic = new NogoLocationGraphic(this.polygon, validFrom, validTo, draught, message, northWest, southEast, errorCode, frame, color);
        add(nogoLocationGraphic);
    }


}
