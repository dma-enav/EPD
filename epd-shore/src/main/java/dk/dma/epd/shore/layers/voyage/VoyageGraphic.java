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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.shore.voyage.Voyage;

/**
 * Graphic for showing routes
 */
public class VoyageGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private Voyage voyage;
    private LinkedList<RouteWaypoint> routeWaypoints;
    private List<VoyageLegGraphic> routeLegs = new ArrayList<>();

    protected Stroke voyageStroke;
    protected Color color;

    private int voyageIndex;

    boolean animation;

    public VoyageGraphic(Voyage voyage, int voyageIndex, Color color) {
        super();
        this.voyage = voyage;
        this.voyageIndex = voyageIndex;

        voyageStroke = new BasicStroke(12.0f, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                12.0f, // Miter limit
                new float[] { 1.0f}, // Dash pattern
                12.0f);
        
        this.color = color;
        initGraphics();
    }



    public VoyageGraphic(Stroke stroke, Color color) {
        super();
        this.voyageStroke = stroke;
        this.color = color;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
        initGraphics();
    }

    public void initGraphics() {
        routeWaypoints = voyage.getRoute().getWaypoints();
        for (RouteWaypoint routeWaypoint : routeWaypoints) {
            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg routeLeg = routeWaypoint.getOutLeg();
                VoyageLegGraphic voyageLegGraphic = new VoyageLegGraphic(routeLeg,
                        voyageIndex, this.color, this.voyageStroke);
                add(voyageLegGraphic);
                routeLegs.add(0, voyageLegGraphic);
            }
        }
    }

}
