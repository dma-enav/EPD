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
package dk.dma.epd.common.prototype.layers.voct;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;

public class SarGraphics extends OMGraphicList {
    private static final long serialVersionUID = 1L;

//    private NogoPolygon polygon;
    
    //private MsiTextBox msiTextBox;
    
    public SarGraphics(Position datum, double radius, Position A, Position B, Position C, Position D, Position LKP, Position current) {
        super();
        
//        this.polygon = polygon;
        
        // Create location grahic
        SarAreaGraphic sarArea = new SarAreaGraphic(A, B, C, D);
        SarCircleGraphic sarCircle = new SarCircleGraphic(datum, radius);
        SarLinesGraphics sarLines = new SarLinesGraphics(LKP, current, datum, true, "Datum");
        
        
        add(sarArea);
        add(sarCircle);
        add(sarLines);
    }


    
    public SarGraphics(Position datumDownWind, Position datumMin, Position datumMax, double radiusDownWind, double radiusMin, double radiusMax, Position LKP, Position current, Position A, Position B, Position C, Position D) {
        super();
        
//        this.polygon = polygon;
        
        // Create location grahic
        
        SarAreaGraphic sarArea = new SarAreaGraphic(A, B, C, D);
        
        
        SarCircleGraphic sarCircleDownWind = new SarCircleGraphic(datumDownWind, radiusDownWind);
        SarCircleGraphic sarCircleMin = new SarCircleGraphic(datumMin, radiusMin);
        SarCircleGraphic sarCircleMax = new SarCircleGraphic(datumMax, radiusMax);

        
        SarLinesGraphics sarLinesDownWind = new SarLinesGraphics(LKP, current, datumDownWind, true, "Datum DW");
        SarLinesGraphics sarLinesMin = new SarLinesGraphics(LKP, current, datumMin, false, "Datum Min");
        SarLinesGraphics sarLinesMax = new SarLinesGraphics(LKP, current, datumMax, false, "Datum Max");
        
        add(sarArea);
        
        add(sarCircleDownWind);
        add(sarCircleMin);
        add(sarCircleMax);
        
        add(sarLinesDownWind);
        add(sarLinesMin);
        add(sarLinesMax);
    }
    
}
