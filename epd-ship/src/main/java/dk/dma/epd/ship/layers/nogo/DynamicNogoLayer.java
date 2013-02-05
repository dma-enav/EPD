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

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.ship.nogo.DynamicNogoHandler;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class DynamicNogoLayer extends OMGraphicHandlerLayer {
    private static final long serialVersionUID = 1L;

    private DynamicNogoHandler nogoHandler;

    private OMGraphicList graphics = new OMGraphicList();

    public DynamicNogoLayer() {

    }

    
    public void cleanUp(){
        graphics.clear();
    }
    
    /**
     * Errorcode -1 means server experinced a timeout 
     * Errorcode 0 means everything went ok 
     * Errorcode 1 is the standby message 
     * Errorcode 17 means no data 
     * Errorcode 18 means no tide data
     * 
     * @param completed
     */
    public void doUpdate(boolean completed) {

        Date validFrom = nogoHandler.getValidFromOwn();
        Date validTo = nogoHandler.getValidToOwn();
        double draught = nogoHandler.getDraughtOwn();

        if (completed) {

            // Clean up
             graphics.clear();

            // Get polygons
            List<NogoPolygon> polygonsOwn = nogoHandler.getNogoPolygonsOwn();
            List<NogoPolygon> polygonsTarget = nogoHandler.getNogoPolygonsTarget();
            
            
            if (nogoHandler.getNogoFailed()) {
                nogoHandler.setNogoFailed(false);
                NogoGraphic nogoGraphic = new NogoGraphic(
                        null,
                        validFrom,
                        validTo,
                        draught,
                        "Connection to shore timed out - NoGo request failed. Please try again in a few minutes",
                        nogoHandler.getNorthWestPointOwn(), nogoHandler.getSouthEastPointOwn(), -1, true, Color.RED);
                graphics.add(nogoGraphic);
                
                nogoGraphic = new NogoGraphic(
                        null,
                        validFrom,
                        validTo,
                        draught,
                        "Connection to shore timed out - NoGo request failed. Please try again in a few minutes",
                        nogoHandler.getNorthWestPointTarget(), nogoHandler.getSouthEastPointTarget(), -1, true, Color.RED);
                graphics.add(nogoGraphic);
                
            } else {

                if (nogoHandler.getNoGoErrorCodeOwn() == 17 && nogoHandler.getNoGoErrorCodeTarget() == 17) {
                    NogoGraphic nogoGraphic = new NogoGraphic(null, null, null,
                            draught, "No data available for requested area",
                            null, null, nogoHandler.getNoGoErrorCodeOwn(), true, Color.RED);
                    graphics.add(nogoGraphic);
                }

                if (nogoHandler.getNoGoErrorCodeOwn() == 18 && nogoHandler.getNoGoErrorCodeTarget() == 18) {
                    
                    //Own graphics
                    for (NogoPolygon polygon : polygonsOwn) {
                        NogoGraphic nogoGraphic = new NogoGraphic(polygon,
                                validFrom, validTo, draught, "",
                                nogoHandler.getNorthWestPointOwn(),
                                nogoHandler.getSouthEastPointOwn(),
                                nogoHandler.getNoGoErrorCodeOwn(), false, Color.RED);
                        graphics.add(nogoGraphic);
                    }
                    
                    //Target graphics
                    for (NogoPolygon polygon : polygonsTarget) {
                        NogoGraphic nogoGraphic = new NogoGraphic(polygon,
                                validFrom, validTo, draught, "",
                                nogoHandler.getNorthWestPointTarget(),
                                nogoHandler.getSouthEastPointTarget(),
                                nogoHandler.getNoGoErrorCodeTarget(), false, Color.ORANGE);
                        graphics.add(nogoGraphic);
                    }

                    addFrame("", validFrom, validTo, draught,
                            nogoHandler.getNoGoErrorCodeOwn());
                }

                if (nogoHandler.getNoGoErrorCodeOwn() == 0 && nogoHandler.getNoGoErrorCodeTarget() == 0) {

                    //Own graphics
                    for (NogoPolygon polygon : polygonsOwn) {
                        NogoGraphic nogoGraphic = new NogoGraphic(polygon,
                                validFrom, validTo, draught, "",
                                nogoHandler.getNorthWestPointOwn(),
                                nogoHandler.getSouthEastPointOwn(),
                                nogoHandler.getNoGoErrorCodeOwn(), false, Color.RED);
                        graphics.add(nogoGraphic);
                    }
                    
                    //Target graphics
                    for (NogoPolygon polygon : polygonsTarget) {
                        NogoGraphic nogoGraphic = new NogoGraphic(polygon,
                                validFrom, validTo, draught, "",
                                nogoHandler.getNorthWestPointTarget(),
                                nogoHandler.getSouthEastPointTarget(),
                                nogoHandler.getNoGoErrorCodeTarget(), false, Color.ORANGE);
                        graphics.add(nogoGraphic);
                    }

                    if (polygonsOwn.size() == 0) {
                        NogoGraphic nogoGraphic = new NogoGraphic(null,
                                validFrom, validTo, draught,
                                "The selected area is Go",
                                nogoHandler.getNorthWestPointOwn(),
                                nogoHandler.getSouthEastPointOwn(), 1, true, Color.RED);
                        graphics.add(nogoGraphic);
                    } else {
                        addFrame("", validFrom, validTo, draught,
                                nogoHandler.getNoGoErrorCodeOwn());
                    }

                }

                // We have selected an area outside of the available data - send
                // appropiate message
                // if (polygons.size() == 0) {
                //
                // } else {
                // // Data available, go through each polygon and draw them
                // for (NogoPolygon polygon : polygons) {
                // NogoGraphic nogoGraphic = new NogoGraphic(polygon, validFrom,
                // validTo, draught, "", nogoHandler.getNorthWestPoint(),
                // nogoHandler.getSouthEastPoint());
                // graphics.add(nogoGraphic);
                // }
                // }
            }
        } else {
            // We have just sent a nogo request - display a message telling the
            // user to standby

            addFrame("NoGo area requested - standby", validFrom, validTo,
                    draught, 1);

            // NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom,
            // validTo, draught,
            // "NoGo area requested - standby",
            // nogoHandler.getNorthWestPointOwn(),
            // nogoHandler.getSouthEastPointOwn(),
            // 1, true);
            // graphics.add(nogoGraphic);
        }

        doPrepare();
    }

    public void addFrame(String message, Date validFrom, Date validTo,
            Double draught, int errorCode) {
        NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo,
                draught, message, nogoHandler.getNorthWestPointOwn(),
                nogoHandler.getSouthEastPointOwn(), errorCode, true, Color.RED);
        graphics.add(nogoGraphic);
        
        NogoGraphic nogoGraphicTarget = new NogoGraphic(null, validFrom, validTo,
                draught, message, nogoHandler.getNorthWestPointTarget(),
                nogoHandler.getSouthEastPointTarget(), errorCode, true, Color.ORANGE);
        
        graphics.add(nogoGraphic);
        graphics.add(nogoGraphicTarget);
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof DynamicNogoHandler) {
            nogoHandler = (DynamicNogoHandler) obj;
        }
    }

}
