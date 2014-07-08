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

import dk.dma.epd.common.prototype.layers.common.WpCircle;
import dk.dma.epd.common.prototype.model.route.Route;

/**
 * Graphic for waypoint circle
 */
public class WaypointCircle extends WpCircle {
    private static final long serialVersionUID = 1L;
    
    private Route route;
    private int wpIndex;

    private int routeIndex;

    public WaypointCircle(Route route, int routeIndex, int wpIndex) {
        super();
        this.routeIndex = routeIndex;
        this.route = route;
        this.wpIndex = wpIndex;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getWpIndex() {
        return wpIndex;
    }

    public void setWpIndex(int wpIndex) {
        this.wpIndex = wpIndex;
    }
    
    public int getRouteIndex() {
        return routeIndex;
    }
}
