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

import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.ship.nogo.NoGoDataEntry;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * No-go layer
 */
public class NogoLayer extends EPDLayerCommon {

    private static final long serialVersionUID = 1L;

    // private NogoHandler nogoHandler;

    List<OMGraphicList> multipleNoGo = new ArrayList<OMGraphicList>();

    int currentSelected;

    /**
     * Constructor
     */
    public NogoLayer() {
        super();
    }

    public void initializeNoGoStorage(int count) {
//        System.out.println("Initialize storage");
        cleanUp();
        for (int i = 0; i < count; i++) {
            multipleNoGo.add(null);
        }
    }

    /**
     * Called by the {@linkplain NogoHandler} with status updates. Updates the graphics with the no-go elemetns
     * 
     * @param dataEntry
     * @param id
     */
    public void addResultFromMultipleRequest(NoGoDataEntry dataEntry, int id) {

//        System.out.println("Adding result from multiple at id " + id);

        multipleNoGo.add(id, createNoGoGraphics(dataEntry));

        if (id == 0) {
            drawSpecificResult(0);
        }
    }

    public void drawSpecificResult(int id) {
//        System.out.println("Drawing " + id);
        if (multipleNoGo.size() >= id+1) {

            graphics.remove(multipleNoGo.get(currentSelected));

            if (multipleNoGo.get(id) != null) {
                graphics.add(multipleNoGo.get(id));
                currentSelected = id;
            } else {
                System.out.println("Value is null");
            }
        }

        doPrepare();
        
    }

    private OMGraphicList createNoGoGraphics(NoGoDataEntry dataEntry) {
        OMGraphicList nogoPolygon = new OMGraphicList();
        List<NogoPolygon> polygons = dataEntry.getNogoPolygons();
        for (NogoPolygon polygon : polygons) {
            NogoGraphic nogoGraphic = new NogoGraphic(polygon);
            nogoPolygon.add(nogoGraphic);
        }

        return nogoPolygon;
    }

    private void cleanUp() {
//        System.out.println("Cleanup");
        for (int i = 0; i < multipleNoGo.size(); i++) {
            graphics.remove(multipleNoGo.get(i));
        }

        multipleNoGo.clear();
    }

    /**
     * Called by the {@linkplain NogoHandler} with status updates. Updates the graphics with the no-go elemetns
     * 
     * @param dataEntry
     */
    public void singleResultCompleted(NoGoDataEntry dataEntry) {
        cleanUp();

        multipleNoGo.add(0, createNoGoGraphics(dataEntry));

        graphics.add(multipleNoGo.get(0));

        doPrepare();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        // if (obj instanceof NogoHandler) {
        // nogoHandler = (NogoHandler) obj;
        // }
    }

    /**
     * Adds a no-go frame to the graphics
     * 
     * @param message
     *            the message to display
     * @param validFrom
     *            valid from
     * @param validTo
     *            valid to
     * @param draught
     *            the draught
     * @param errorCode
     *            the error code
     */
    public void addFrame(Position northWest, Position southEast) {
        graphics.clear();
        // NogoGraphic nogoGraphic = new NogoGraphic(null, validFrom, validTo, draught, message, nogoHandler.getNorthWestPoint(),
        // nogoHandler.getSouthEastPoint(), errorCode, true, Color.RED);

        NoGoGraphicsFrame graphicsFrame = new NoGoGraphicsFrame(northWest, southEast);
        graphics.add(graphicsFrame);

        doPrepare();
    }

}
