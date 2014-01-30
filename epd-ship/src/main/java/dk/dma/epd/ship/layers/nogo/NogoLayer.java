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
import java.util.List;

import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * No-go layer
 */
public class NogoLayer extends EPDLayerCommon {
    
    private static final long serialVersionUID = 1L;

    private NogoHandler nogoHandler;

    /**
     * Constructor
     */
    public NogoLayer() {
        super();
    }

    /**
     * Called by the {@linkplain NogoHandler} with status updates.
     * Updates the graphics with the no-go elemetns
     * @param completed
     */
    public void doUpdate(boolean completed) {
        Date validFrom = nogoHandler.getValidFrom();
        Date validTo = nogoHandler.getValidTo();
        double draught = nogoHandler.getDraught();

        graphics.clear();
        if (completed) {
            // Get polygons
            List<NogoPolygon> polygons = nogoHandler.getPolygons();
            
            if (nogoHandler.getNogoFailed()) {
                nogoHandler.setNogoFailed(false);
                NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught,
                        "Connection to shore timed out - NoGo request failed. Please try again in a few minutes", nogoHandler.getNorthWestPoint(),
                        nogoHandler.getSouthEastPoint(), -1, true, Color.RED);
                graphics.add(nogoGraphic);

            } else {

                if (nogoHandler.getNoGoErrorCode() == 17) {
                    NogoGraphic nogoGraphic = new NogoGraphic(null, null, null, draught,
                            "No data available for requested area", null, null, nogoHandler.getNoGoErrorCode(), true, Color.RED);
                    graphics.add(nogoGraphic);
                }

                if (nogoHandler.getNoGoErrorCode() == 18) {
                    for (NogoPolygon polygon : polygons) {
                        NogoGraphic nogoGraphic = new NogoGraphic(polygon, validFrom, validTo, draught, "",
                                nogoHandler.getNorthWestPoint(), nogoHandler.getSouthEastPoint(),
                                nogoHandler.getNoGoErrorCode(), false, Color.RED);
                        graphics.add(nogoGraphic);
                    }
                    addFrame("", validFrom, validTo, draught, nogoHandler.getNoGoErrorCode());
                }

                if (nogoHandler.getNoGoErrorCode() == 0) {
                    for (NogoPolygon polygon : polygons) {
                        NogoGraphic nogoGraphic = new NogoGraphic(polygon, validFrom, validTo, draught, "",
                                nogoHandler.getNorthWestPoint(), nogoHandler.getSouthEastPoint(),
                                nogoHandler.getNoGoErrorCode(), false, Color.RED);
                        graphics.add(nogoGraphic);
                    }

                    if (polygons.size() == 0) {
                        NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught,
                                "The selected area is Go", nogoHandler.getNorthWestPoint(),
                                nogoHandler.getSouthEastPoint(), 1, true, Color.RED);
                        graphics.add(nogoGraphic);
                    }else{
                        addFrame("", validFrom, validTo, draught, nogoHandler.getNoGoErrorCode());
                    }

                }
            }
        } else {
            // We have just sent a nogo request - display a message telling the
            // user to standby
            addFrame("NoGo area requested - standby", validFrom, validTo, draught, 1);
        }

        doPrepare();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof NogoHandler) {
            nogoHandler = (NogoHandler) obj;
        }
    }

    /**
     * Adds a no-go frame to the graphics
     * @param message the message to display
     * @param validFrom valid from
     * @param validTo valid to
     * @param draught the draught
     * @param errorCode the error code
     */
    public void addFrame(String message, Date validFrom, Date validTo, Double draught, int errorCode){
        NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught, message, nogoHandler.getNorthWestPoint(), nogoHandler.getSouthEastPoint(),
                errorCode, true, Color.RED);
        graphics.add(nogoGraphic);
    }
    
}
