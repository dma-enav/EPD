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
package dk.dma.epd.common.prototype.layers.route;

import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for showing routes
 */
public class RouteGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private static final float SCALE = 0.7f; // "Size" of graphics
    private Route route;
    private boolean arrowsVisible;
    private LinkedList<RouteWaypoint> routeWaypoints;
    protected List<RouteLegGraphic> routeLegs = new ArrayList<>();

    protected Stroke routeStroke;
    protected Color color;
    protected Color broadLineColor;
    protected boolean circleDash;
    protected boolean lineDash;

    private int routeIndex;

    boolean animation;

    public RouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color) {
        super();
        this.route = route;
        this.routeIndex = routeIndex;
        this.arrowsVisible = arrowsVisible;
        this.routeStroke = stroke;
        this.color = color;
        initGraphics();
    }

    public RouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color, Color broadLineColor,
            boolean circleDash, boolean lineDash) {
        super();
        this.lineDash = lineDash;
        this.route = route;
        this.routeIndex = routeIndex;
        this.arrowsVisible = arrowsVisible;
        this.routeStroke = stroke;
        this.color = color;
        this.broadLineColor = broadLineColor;
        this.circleDash = circleDash;
        initVoyageGraphics();
    }

    public Route getRoute() {
        return route;
    }

    public RouteGraphic(boolean arrowsVisible, Stroke stroke, Color color) {
        super();
        this.arrowsVisible = arrowsVisible;
        this.routeStroke = stroke;
        this.color = color;
    }

    public void setRoute(Route route) {
        this.route = route;
        initGraphics();
    }

    public void initVoyageGraphics() {
        routeWaypoints = route.getWaypoints();
        int i = 0;
        for (RouteWaypoint routeWaypoint : routeWaypoints) {
            if (route instanceof ActiveRoute && ((ActiveRoute) route).getActiveWaypointIndex() == i) {
                RouteWaypointGraphic routeWaypointGraphicActive = new RouteWaypointGraphic(route, routeIndex, i, routeWaypoint,
                        Color.RED, 30, 30, SCALE);
                add(0, routeWaypointGraphicActive);
            }

            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg routeLeg = routeWaypoint.getOutLeg();

                // Do we want dashed broad legs or continued?
                RouteLegGraphic routeLegGraphic = null;

                if (lineDash) {
                    routeLegGraphic = new RouteLegGraphic(routeLeg, routeIndex, this.color, this.routeStroke, broadLineColor, SCALE);
                } else {
                    float[] dash = { 1000000.0f };

                    routeLegGraphic = new RouteLegGraphic(routeLeg, routeIndex, this.color, this.routeStroke, broadLineColor, dash, SCALE);
                }

                add(routeLegGraphic);
                routeLegs.add(0, routeLegGraphic);
            }

            // Dashed circles
            RouteWaypointGraphic routeWaypointGraphic = new RouteWaypointGraphic(route, routeIndex, i, routeWaypoint, this.color,
                    18, 18, circleDash, SCALE);

            add(0, routeWaypointGraphic);
            i++;

        }
    }

    public void initGraphics() {
        routeWaypoints = route.getWaypoints();
        int i = 0;
        for (RouteWaypoint routeWaypoint : routeWaypoints) {
            if (route instanceof ActiveRoute && ((ActiveRoute) route).getActiveWaypointIndex() == i) {
                RouteWaypointGraphic routeWaypointGraphicActive = new RouteWaypointGraphic(route, routeIndex, i, routeWaypoint,
                        Color.RED, 30, 30, SCALE);
                add(0, routeWaypointGraphicActive);
            }
            if (routeWaypoint.getOutLeg() != null) {
                RouteLeg routeLeg = routeWaypoint.getOutLeg();
                
                RouteLegGraphic routeLegGraphic;
                if (route instanceof ActiveRoute){
                    routeLegGraphic = new ActiveRouteLegGraphic(routeLeg, routeIndex, this.color, this.routeStroke, SCALE, this, i);     
                }else{
                    routeLegGraphic = new RouteLegGraphic(routeLeg, routeIndex, this.color, this.routeStroke, SCALE, this, i);
                }
                
                
                add(routeLegGraphic);
                routeLegs.add(0, routeLegGraphic);
            }
            RouteWaypointGraphic routeWaypointGraphic = new RouteWaypointGraphic(route, routeIndex, i, routeWaypoint, this.color,
                    18, 18, SCALE);
            add(0, routeWaypointGraphic);
            i++;
        }
    }

    public void activateAnimation() {
        for (int i = 0; i < routeLegs.size(); i++) {
            routeLegs.get(i).addAnimatorLine();
        }
    }

    public void updateAnimationLine() {
        for (int i = 0; i < routeLegs.size(); i++) {
            routeLegs.get(i).updateAnimationLine();
        }
    }

    public boolean isAnimation() {
        return animation;
    }

    public void setAnimation(boolean animation) {
        this.animation = animation;
    }

    public void showArrowHeads(boolean show) {
        if (this.arrowsVisible != show) {
            for (RouteLegGraphic routeLeg : routeLegs) {
                if (routeLeg != null) {
                    routeLeg.setArrows(show);
                }
            }
            this.arrowsVisible = show;
        }
    }

    public int getRouteIndex() {
        return this.routeIndex;
    }
}
