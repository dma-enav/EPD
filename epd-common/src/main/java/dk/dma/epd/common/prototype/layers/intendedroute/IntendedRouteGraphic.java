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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;

/**
 * Graphic for intended route
 */
public class IntendedRouteGraphic extends OMGraphicList {
    
    private static final long serialVersionUID = 1L;
    private static final float SCALE = 0.7f;    // "Size" of graphics
    private static final int ALPHA = 150;       // Alpha of color
    
    /**
     * Valid colors for intended routes 
     */
    public static final Color[] COLORS = {
        new Color(170, 40, 40, ALPHA),    // red'ish
        new Color(40, 170, 40, ALPHA),    // green'ish
        new Color(40, 40, 130, ALPHA),    // blue'ish
        new Color(100, 100, 100, ALPHA)   // gray'ish
    };
    public static final Color COLOR_LATE        =  new Color(255, 255, 0, ALPHA); // Yellow'ish
    public static final Color COLOR_VERY_LATE   =  new Color(128, 128, 128, ALPHA); // Gray'ish
    
    private CloudIntendedRoute previousData;
    private IntendedRouteLegGraphic activeWpLine;
    private double[] activeWpLineLL = new double[4];
    private Color normalColor = COLORS[1];
    private Color currentColor = normalColor;
    private String name;
    private boolean arrowsVisible;

    private VesselTarget vesselTarget;

    private List<IntendedRouteLegGraphic> routeLegs = new ArrayList<>();
    private List<IntendedRouteWpCircle> routeWps = new ArrayList<>();

    public IntendedRouteGraphic() {
        super();
        Position nullGeoLocation = Position.create(0, 0);
        activeWpLine = new IntendedRouteLegGraphic(0, this, true,
                nullGeoLocation, nullGeoLocation, currentColor, SCALE);
        setVisible(false);
    }

    private void makeLegLine(int index, Position start, Position end) {
        IntendedRouteLegGraphic leg = new IntendedRouteLegGraphic(index, this,
                false, start, end, currentColor, SCALE);
        routeLegs.add(leg);
        add(leg);
    }

    private void makeWpCircle(int index, Position wp) {
        IntendedRouteWpCircle wpCircle = new IntendedRouteWpCircle(this, index,
                wp.getLatitude(), wp.getLongitude(), currentColor, SCALE);
        routeWps.add(wpCircle);
        add(wpCircle);
    }


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

    /**
     * Updates the intended route color
     * @param color the color to use
     */
    private void updateColor(Color color) {        
        this.setVisible(true);
        currentColor =  color;
        
        for (IntendedRouteLegGraphic routeLeg : routeLegs) {
            routeLeg.setLinePaint(currentColor);
        }
        for (IntendedRouteWpCircle routeWp : routeWps) {
            routeWp.setLinePaint(currentColor);
        }
        activeWpLine.setLinePaint(currentColor);
    }
    
    /**
     * Called when the vessel target has been updated
     * 
     * @param vesselTarget the vessel target
     * @param name the name
     * @param cloudIntendedRoute the intended route data
     * @param pos the current vessel position
     */
    public void update(VesselTarget vesselTarget, String name,
            CloudIntendedRoute cloudIntendedRoute, Position pos) {

        // Set visible if not visible
        if (!isVisible()) {
            setVisible(true);
        }
        
        
        if (cloudIntendedRoute != null) {

            long secondsSinceRecieved = 
                    (System.currentTimeMillis() - cloudIntendedRoute.getReceived().getTime()) / 1000L;
            
            if (secondsSinceRecieved < 60){
                // Fresh route, within a minute
                updateColor(normalColor);
            
            } else if (secondsSinceRecieved < 300) {
                // Between 1 and 5 minutes since received
                updateColor(COLOR_LATE);

            } else if (secondsSinceRecieved < 600) {
                // Between 5 and 10 minutes since received
                updateColor(COLOR_VERY_LATE);
                
            } else {
                // > 10 min since received
                this.setVisible(false);
            }
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
            for (RouteWaypoint routeWp : cloudIntendedRoute.getWaypoints()) {
                waypoints.add(routeWp.getPos());
            }
            
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

    /**
     * Turn on anti-aliasing
     */
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }

    /**
     * Returns the normal (i.e. fresh) color for the intended route
     * @return the normal color for the intended route
     */
    public Color getNormalColor() {
        return normalColor;
    }

    /**
     * Sets the normal (i.e. fresh) color for the intended route
     * @param normalColor the normal color for the intended route
     */
    public void setNormalColor(Color normalColor) {
        this.normalColor = normalColor;
    }
}
