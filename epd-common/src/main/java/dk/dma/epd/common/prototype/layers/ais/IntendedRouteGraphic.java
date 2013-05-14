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
package dk.dma.epd.common.prototype.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;

/**
 * Graphic for intended route
 */
public class IntendedRouteGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    // private AisIntendedRoute previousData;
    private CloudIntendedRoute previousData;
    private IntendedRouteLegGraphic activeWpLine;
    private double[] activeWpLineLL = new double[4];
    private Color legColor = new Color(42, 172, 12, 255);
    private String name;
    private boolean arrowsVisible;

    private VesselTarget vesselTarget;

    private List<IntendedRouteLegGraphic> routeLegs = new ArrayList<>();
    private List<IntendedRouteWpCircle> routeWps = new ArrayList<>();

    public IntendedRouteGraphic() {
        super();
        Position nullGeoLocation = Position.create(0, 0);
        activeWpLine = new IntendedRouteLegGraphic(0, this, true,
                nullGeoLocation, nullGeoLocation, legColor);
        setVisible(false);
    }

    private void makeLegLine(int index, Position start, Position end) {
        IntendedRouteLegGraphic leg = new IntendedRouteLegGraphic(index, this,
                false, start, end, legColor);
        routeLegs.add(leg);
        add(leg);
    }

    private void makeWpCircle(int index, Position wp) {
        IntendedRouteWpCircle wpCircle = new IntendedRouteWpCircle(this, index,
                wp.getLatitude(), wp.getLongitude(), 0, 0, 18, 18);
        wpCircle.setStroke(new BasicStroke(3));
        wpCircle.setLinePaint(legColor);
        routeWps.add(wpCircle);
        add(wpCircle);
    }

    //
    // public void update(VesselTarget vesselTarget, String label,
    // AisIntendedRoute routeData, Position pos) {
    // this.vesselTarget = vesselTarget;
    // this.name = label;
    // // Handle no or empty route
    // if (routeData == null || routeData.getWaypoints().size() == 0) {
    // clear();
    // if (isVisible()) {
    // setVisible(false);
    // }
    // previousData = null;
    // return;
    // }
    //
    // if (previousData != routeData) {
    // // Route has changed, draw new route
    // clear();
    // add(activeWpLine);
    // List<Position> waypoints = routeData.getWaypoints();
    // // Make first WP circle
    // makeWpCircle(0, waypoints.get(0));
    // for (int i=0; i < waypoints.size() - 1; i++) {
    // Position start = waypoints.get(i);
    // Position end = waypoints.get(i + 1);
    //
    // // Make wp circle
    // makeWpCircle(i + 1, end);
    //
    // // Make leg line
    // makeLegLine(i + 1, start, end);
    // }
    // previousData = routeData;
    // }
    //
    // // Update leg to first waypoint
    // Position activeWpPos = routeData.getWaypoints().get(0);
    // activeWpLineLL[0] = pos.getLatitude();
    // activeWpLineLL[1] = pos.getLongitude();
    // activeWpLineLL[2] = activeWpPos.getLatitude();
    // activeWpLineLL[3] = activeWpPos.getLongitude();
    // activeWpLine.setLL(activeWpLineLL);
    //
    // // Set visible if not visible
    // if (!isVisible()) {
    // setVisible(true);
    // }
    //
    // }

    public VesselTarget getVesselTarget() {
        return vesselTarget;
    }

    public String getName() {
        return name;
    }

    public void showArrowHeads(boolean show) {
        if (this.arrowsVisible != show) {
            for (IntendedRouteLegGraphic routeLeg : routeLegs) {
                routeLeg.setArrows(show);
            }
            this.arrowsVisible = show;
        }
    }

    public void update(VesselTarget vesselTarget, String name,
            CloudIntendedRoute cloudIntendedRoute, Position pos) {

        
        // Set visible if not visible
        if (!isVisible()) {
            setVisible(true);
        }
        
        
        if (cloudIntendedRoute != null){

            long timeSinceRecieved = System.currentTimeMillis() - cloudIntendedRoute.getReceived().getTime();
            
//            System.out.println(timeSinceRecieved);

            //Fresh route
            if (timeSinceRecieved < 60000){
                this.setVisible(true);
                legColor =  new Color(42, 172, 12, 255);
                
                for (int i = 0; i < routeLegs.size(); i++) {
                    routeLegs.get(i).setLinePaint(legColor);
                }

                for (int i = 0; i < routeWps.size(); i++) {
                    routeWps.get(i).setLinePaint(legColor);
                }
                activeWpLine.setLinePaint(legColor);
                
            }
            
            //1 min since recieved
            if (timeSinceRecieved > 60000){
                this.setVisible(true);
                legColor = Color.YELLOW;                
                for (int i = 0; i < routeLegs.size(); i++) {
                    routeLegs.get(i).setLinePaint(legColor);
                }

                for (int i = 0; i < routeWps.size(); i++) {
                    routeWps.get(i).setLinePaint(legColor);
                }
                activeWpLine.setLinePaint(legColor);
                
                
            }
            
            
            
            //5 min since recieved
            if (timeSinceRecieved > 300000){
                this.setVisible(true);
                legColor = Color.GRAY;
                for (int i = 0; i < routeLegs.size(); i++) {
                    routeLegs.get(i).setLinePaint(legColor);
                }
                
                for (int i = 0; i < routeWps.size(); i++) {
                    routeWps.get(i).setLinePaint(legColor);
                }
                activeWpLine.setLinePaint(legColor);
            }
            
            //10 min since recieved
            if (timeSinceRecieved > 600000){
                this.setVisible(false);
            }
            
//            System.out.println(cloudIntendedRoute.getReceived());   
        }
        



        
        this.vesselTarget = vesselTarget;
        this.name = name;
        // Handle no or empty route
        if (cloudIntendedRoute == null || cloudIntendedRoute.getWaypoints().size() == 0) {
            clear();
            if (isVisible()) {
                setVisible(false);
            }
            previousData = null;
            return;
        }
        
        if (previousData != cloudIntendedRoute) {
            // Route has changed, draw new route
            clear();
            add(activeWpLine);
            
            List<Position> waypoints = new ArrayList<>();
            
            for (int i = 0; i < cloudIntendedRoute.getWaypoints().size(); i++) {
                waypoints.add(cloudIntendedRoute.getWaypoints().get(i).getPos());
            }
            
//            List<Position> waypoints = cloudIntendedRoute.getWaypoints();
            // Make first WP circle
            makeWpCircle(0, waypoints.get(0));
            for (int i=0; i < waypoints.size() - 1; i++) {
                Position start = waypoints.get(i);
                Position end = waypoints.get(i + 1);
                
                // Make wp circle
                makeWpCircle(i + 1, end);
                
                // Make leg line
                makeLegLine(i + 1, start, end);
            }
            previousData = cloudIntendedRoute;
        }
        
        // Update leg to first waypoint
        Position activeWpPos = cloudIntendedRoute.getWaypoints().get(0).getPos();
        activeWpLineLL[0] = pos.getLatitude();
        activeWpLineLL[1] = pos.getLongitude();
        activeWpLineLL[2] = activeWpPos.getLatitude();
        activeWpLineLL[3] = activeWpPos.getLongitude();
        activeWpLine.setLL(activeWpLineLL);

        

        
    }
}
