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
package dk.dma.epd.common.prototype.model.voct.sardata;

import java.util.ArrayList;
import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
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
        
        staticPositions = new ArrayList<Position>();
        for (int i = 0; i < this.waypoints.size(); i++) {
            staticPositions.add(waypoints.get(i).getPos());
        }
        
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
    
    
    

    public boolean isActiveRoute(ActiveRoute activeRoute){
        //Compare waypoints
        
      if (waypoints.size() != activeRoute.getWaypoints().size()){
          return false;
      }
      
      
      for (int i = 0; i < waypoints.size(); i++) {
        
          if (waypoints.get(i).getPos() != activeRoute.getWaypoints().get(i).getPos()){
              return false;
          }
          
    }
        
        return true;
    }
    
}
