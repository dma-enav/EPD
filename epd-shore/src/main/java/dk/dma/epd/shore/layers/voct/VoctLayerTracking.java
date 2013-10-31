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
package dk.dma.epd.shore.layers.voct;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.voct.SarGraphics;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumLineData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;

public class VoctLayerTracking extends VoctLayerCommon {
    private static final long serialVersionUID = 1L;
    private OMGraphicList graphics = new OMGraphicList();

    @Override
    public void voctUpdated(VOCTUpdateEvent e) {

        if (e == VOCTUpdateEvent.SAR_CANCEL) {
            graphics.clear();
            this.setVisible(false);
        }

        if (e == VOCTUpdateEvent.SAR_DISPLAY) {
            if (voctManager.getSarType() == SAR_TYPE.RAPID_RESPONSE) {
                drawRapidResponse();
            }
            if (voctManager.getSarType() == SAR_TYPE.DATUM_POINT) {
                drawDatumPoint();
            }
            if (voctManager.getSarType() == SAR_TYPE.DATUM_LINE) {
                drawDatumLine();
            }

            this.setVisible(true);
        }

        // Do nothing
        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY) {

        }

    }

    private void drawRapidResponse() {

        RapidResponseData data = (RapidResponseData) voctManager.getSarData();

        Position A = data.getA();
        Position B = data.getB();
        Position C = data.getC();
        Position D = data.getD();
      
        graphics.clear();

        SarGraphics sarGraphics = new SarGraphics(A, B, C, D);
        graphics.add(sarGraphics);

        doPrepare();
    }
    
    private void drawDatumPoint() {

        DatumPointData data = (DatumPointData) voctManager.getSarData();

        Position A = data.getA();
        Position B = data.getB();
        Position C = data.getC();
        Position D = data.getD();

        graphics.clear();

        SarGraphics sarGraphics = new SarGraphics(A, B, C, D);
        graphics.add(sarGraphics);

        doPrepare();
    }
    
    private void drawDatumLine() {

        // Create as many data objects as is contained

        // Clear all previous
        graphics.clear();

        DatumLineData datumLineData = (DatumLineData) voctManager.getSarData();

        for (int i = 0; i < datumLineData.getDatumPointDataSets().size(); i++) {

            System.out.println("Creating area " + i);
            DatumPointData data = datumLineData.getDatumPointDataSets().get(i);

            Position A = data.getA();
            Position B = data.getB();
            Position C = data.getC();
            Position D = data.getD();

            SarGraphics sarGraphics = new SarGraphics(A, B, C, D);
            graphics.add(sarGraphics);
        }

 

        // public SarGraphics(Position datumDownWind, Position datumMin,
        // Position datumMax, double radiusDownWind, double radiusMin, double
        // radiusMax, Position LKP, Position current) {

        doPrepare();

    }
    
    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

}
