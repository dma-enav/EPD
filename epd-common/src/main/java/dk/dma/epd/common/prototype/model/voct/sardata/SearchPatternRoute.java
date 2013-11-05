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
package dk.dma.epd.common.prototype.model.voct.sardata;

import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

public class SearchPatternRoute extends Route {

    private static final long serialVersionUID = 1L;
    List<Position> staticPositions;
    List<Position> dynamicPositions;

    private boolean dynamic;
    
    
    public SearchPatternRoute(Route route){
        super(route);
    }
    
    public SearchPatternRoute(List<Position> positions) {

        this.staticPositions = positions;

        if (positions.size() >= 2) {

            for (int i = 0; i < positions.size(); i++) {

                RouteWaypoint newWaypoint = new RouteWaypoint();
                newWaypoint.setName("");
                newWaypoint.setPos(positions.get(i));
                waypoints.add(newWaypoint);

                if (waypoints.size() > 1) {
                    RouteLeg newLeg = new RouteLeg();
                    newLeg.setHeading(Heading.RL);
                    RouteWaypoint prevWaypoint = waypoints
                            .get(waypoints.size() - 2);
                    prevWaypoint.setOutLeg(newLeg);
                    newWaypoint.setInLeg(newLeg);
                    newLeg.setStartWp(prevWaypoint);
                    newLeg.setEndWp(newWaypoint);
                }

            }

        }

    }

    /**
     * @param dynamicPositions the dynamicPositions to set
     */
    public void setDynamicPositions(List<Position> dynamicPositions) {
        this.dynamicPositions = dynamicPositions;
    }
    
    
    public void switchToDynamic(){
        for (int i = 0; i < waypoints.size(); i++) {
            waypoints.get(i).setPos(dynamicPositions.get(i));
        }
        
        dynamic = true;
    }
    
    
    public void switchToStatic(){
        for (int i = 0; i < waypoints.size(); i++) {
            waypoints.get(i).setPos(staticPositions.get(i));
        }
        
        dynamic = false;
    }

    /**
     * @return the dynamic
     */
    public boolean isDynamic() {
        return dynamic;
    }
    
    
    

}
