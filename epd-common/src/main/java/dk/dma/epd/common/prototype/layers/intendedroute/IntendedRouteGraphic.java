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
    private static final float SCALE    = 0.7f;    // "Size" of graphics
    private static final int TTL        = 600;     // Time to live of graphics, i.e. 10 minutes
    
    /**
     * Valid colors for intended routes 
     */
    public static final Color[] COLORS = {
        new Color(170, 40, 40),    // red'ish
        new Color(40, 170, 40),    // green'ish
        new Color(40, 40, 130),    // blue'ish
        new Color(170, 170, 40),   // yellow'ish
        new Color(100, 100, 100),  // gray'ish
        new Color(240, 240, 240),  // white'ish
        new Color(20, 20, 20)      // black'ish
    };
    
    private CloudIntendedRoute previousData;
    private IntendedRouteLegGraphic activeWpLine;
    private double[] activeWpLineLL = new double[4];
    private Color routeColor = COLORS[1];
    private String name;
    private boolean arrowsVisible;

    private VesselTarget vesselTarget;

    private List<IntendedRouteLegGraphic> routeLegs = new ArrayList<>();
    private List<IntendedRouteWpCircle> routeWps = new ArrayList<>();

    public IntendedRouteGraphic() {
        super();
        Position nullGeoLocation = Position.create(0, 0);
        activeWpLine = new IntendedRouteLegGraphic(0, this, true,
                nullGeoLocation, nullGeoLocation, routeColor, SCALE);
        setVisible(false);
    }

    private void makeLegLine(int index, Position start, Position end) {
        IntendedRouteLegGraphic leg = new IntendedRouteLegGraphic(index, this,
                false, start, end, routeColor, SCALE);
        routeLegs.add(leg);
        add(leg);
    }

    private void makeWpCircle(int index, Position wp) {
        IntendedRouteWpCircle wpCircle = new IntendedRouteWpCircle(this, index,
                wp.getLatitude(), wp.getLongitude(), routeColor, SCALE);
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
        for (IntendedRouteLegGraphic routeLeg : routeLegs) {
            routeLeg.setLinePaint(color);
        }
        for (IntendedRouteWpCircle routeWp : routeWps) {
            routeWp.setLinePaint(color);
        }
        activeWpLine.setLinePaint(color);
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

        // Adjust the transparency of the color depending on the last-received time for the route
        long secondsSinceRecieved = 
                    (System.currentTimeMillis() - cloudIntendedRoute.getReceived().getTime()) / 1000L;

        if (secondsSinceRecieved < TTL) {
            float factor = 1.0f - (float)secondsSinceRecieved / (float)TTL;
            Color color = adjustColor(routeColor, factor, factor);
            updateColor(color);
            this.setVisible(vesselTarget.getSettings().isShowRoute());

        } else {
            this.setVisible(false);
        }        
    }

    /**
     * Adjusts the saturation and opacity of the color according to the parameters
     * 
     * @param col the color to adjust
     * @param saturation the change to saturation
     * @param opacity the change to opacity
     * @return the result
     */
    private Color adjustColor(Color col, float saturation, float opacity) {
        float[] hsb = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
        hsb[1] = (float)Math.max(Math.min(hsb[1] * saturation, 1.0), 0.0);
        col = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        int alpha = (int)Math.max(Math.min(255.0 * opacity, 255.0), 0.0);
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
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
     * Returns the color for the intended route
     * @return the color for the intended route
     */
    public Color getRouteColor() {
        return routeColor;
    }

    /**
     * Sets the color for the intended route
     * @param routeColor the color for the intended route
     */
    public void setRouteColor(Color routeColor) {
        this.routeColor = routeColor;
    }
}
