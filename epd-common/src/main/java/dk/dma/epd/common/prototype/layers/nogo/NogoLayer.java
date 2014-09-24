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

import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.nogo.NoGoDataEntry;
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
        // System.out.println("Initialize storage");
        cleanUp();
        for (int i = 0; i < count; i++) {
            multipleNoGo.add(null);
        }
    }

    /**
     * Called by the {@linkplain NogoHandler} with status updates. Updates the graphics with the no-go elements
     * 
     * @param dataEntry
     * @param id
     */
    public void addResultFromMultipleRequest(NoGoDataEntry dataEntry, int id) {

        // System.out.println("Adding result from multiple at id " + id);

        multipleNoGo.add(id, createNoGoGraphics(dataEntry));

        if (id == 0) {
            drawSpecificResult(0);
        }
    }

    public void drawSpecificResult(int id) {
        // System.out.println("Drawing " + id);
        if (multipleNoGo.size() >= id + 1) {
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
        // System.out.println("Cleanup");
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
        currentSelected = 0;
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
