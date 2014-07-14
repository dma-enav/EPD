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
package dk.dma.epd.shore.layers.voyage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for a route leg
 */
public class VoyageLegGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private RouteLeg routeLeg;
    private OMLine line;
    
    private Color color;


    private int voyageIndex;
    
    float[] dash = { 35.0f, 35.0f };
    float dashPhase;

    /**
     * Creates a route leg
     * 
     * @param routeLeg
     *            RouteLeg object containing information about the route leg
     * @param voyageIndex
     *            TODO
     * @param color
     *            Color of the route leg
     * @param stroke
     *            Stroke type of the route leg
     */
    public VoyageLegGraphic(RouteLeg routeLeg, int voyageIndex, Color color,
            Stroke stroke) {
        super();
        this.voyageIndex = voyageIndex;
        this.routeLeg = routeLeg;
        this.color = color;
        this.stroke = stroke;
        this.setVague(true);
        initGraphics();
    }

    public void initGraphics() {
        if (routeLeg.getEndWp() != null) {
            RouteWaypoint legStart = routeLeg.getStartWp();
            RouteWaypoint legEnd = routeLeg.getEndWp();

            double startLat = legStart.getPos().getLatitude();
            double startLon = legStart.getPos().getLongitude();

            double endLat = legEnd.getPos().getLatitude();
            double endLon = legEnd.getPos().getLongitude();

            if (routeLeg.getHeading() == Heading.GC) {
                lineType = LINETYPE_GREATCIRCLE;
            } else if (routeLeg.getHeading() == Heading.RL) {
                lineType = LINETYPE_RHUMB;
            }

            line = new OMLine(startLat, startLon, endLat, endLon, lineType);
            line.setLinePaint(color);
            line.setStroke(stroke);

            add(line);
        }
    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }



    /**
     * @return the voyageIndex
     */
    public int getVoyageIndex() {
        return voyageIndex;
    }

    public RouteLeg getRouteLeg() {
        return routeLeg;
    }
}
