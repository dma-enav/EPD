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
package dk.dma.epd.common.prototype.layers.nogo;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.Position;

public class NoGoGraphicsFrame extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    private Position northWest;
    private Position southEast;

    public NoGoGraphicsFrame(Position northWest, Position southEast) {
        this.northWest = northWest;
        this.southEast = southEast;
        drawAreaBox();
    }

    @SuppressWarnings("deprecation")
    private void drawAreaBox() {
        // space for lat-lon points plus first lat-lon pair to close the polygon

        // Four lines are needed

        double[] westernLine = new double[4];
        westernLine[0] = northWest.getLatitude();
        westernLine[1] = northWest.getLongitude();
        westernLine[2] = southEast.getLatitude();
        westernLine[3] = northWest.getLongitude();

        OMPoly poly = new OMPoly(westernLine, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);

        double[] easternLine = new double[4];
        easternLine[0] = northWest.getLatitude();
        easternLine[1] = southEast.getLongitude();
        easternLine[2] = southEast.getLatitude();
        easternLine[3] = southEast.getLongitude();

        OMPoly poly1 = new OMPoly(easternLine, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);

        double[] northernLine = new double[4];
        northernLine[0] = northWest.getLatitude();
        northernLine[1] = northWest.getLongitude();
        northernLine[2] = northWest.getLatitude();
        northernLine[3] = southEast.getLongitude();

        OMPoly poly2 = new OMPoly(northernLine, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);

        double[] southernLine = new double[4];
        southernLine[0] = southEast.getLatitude();
        southernLine[1] = northWest.getLongitude();
        southernLine[2] = southEast.getLatitude();
        southernLine[3] = southEast.getLongitude();

        OMPoly poly3 = new OMPoly(southernLine, OMGraphicConstants.DECIMAL_DEGREES, OMGraphicConstants.LINETYPE_RHUMB, 1);

        poly.setLineColor(Color.gray);
        poly1.setLineColor(Color.gray);
        poly2.setLineColor(Color.gray);
        poly3.setLineColor(Color.gray);

        add(poly);
        add(poly1);
        add(poly2);
        add(poly3);

    }
}
