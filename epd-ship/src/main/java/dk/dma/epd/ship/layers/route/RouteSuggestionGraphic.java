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
package dk.dma.epd.ship.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for a suggested route
 */
public class RouteSuggestionGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private List<RouteWaypoint> routeWaypoints;
    private RouteSuggestionData routeSuggestion;
    
    private Stroke stroke;

    public RouteSuggestionGraphic(RouteSuggestionData routeSuggestion, Stroke stroke) {
        this.routeSuggestion = routeSuggestion;
        this.stroke = stroke;
        
        routeWaypoints = routeSuggestion.getRoute().getWaypoints();
        initGraphics();
        setVague(true);
    }

    public void initGraphics() {

        Stroke backgroundStroke = new BasicStroke(
                10.0f,                      // Width
                BasicStroke.CAP_ROUND,      // End cap
                BasicStroke.JOIN_MITER,     // Join style
                10.0f,                      // Miter limit
                null,                       // Dash pattern
                0.0f);

        RouteWaypoint prevPoint = null;
        RouteWaypoint nextPoint = null;
        for (RouteWaypoint geoLocation : routeWaypoints) {
            nextPoint = geoLocation;
            if (prevPoint != null) {
                OMLine leg = new OMLine(prevPoint.getPos().getLatitude(), prevPoint.getPos().getLongitude(), nextPoint.getPos().getLatitude(), nextPoint.getPos()
                        .getLongitude(), OMGraphicConstants.LINETYPE_RHUMB);
                leg.setStroke(stroke);
                leg.setLinePaint(new Color(183, 68, 237, 255));
                add(leg);
                
                if (!routeSuggestion.isReplied()) {
                    OMLine legBackground = new OMLine(prevPoint.getPos().getLatitude(), prevPoint.getPos().getLongitude(), nextPoint.getPos().getLatitude(),
                            nextPoint.getPos().getLongitude(), OMGraphicConstants.LINETYPE_RHUMB);
                    legBackground.setStroke(backgroundStroke);
                    legBackground.setLinePaint(new Color(42, 172, 12, 120));                
                    add(legBackground);
                }
            }
            prevPoint = nextPoint;
        }
    }

    public RouteSuggestionData getRouteSuggestion() {
        return routeSuggestion;
    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}
