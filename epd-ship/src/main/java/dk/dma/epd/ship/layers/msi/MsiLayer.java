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
package dk.dma.epd.ship.layers.msi;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.layers.msi.MsiDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiLayerCommon;
import dk.dma.epd.common.prototype.layers.msi.MsiSymbolGraphic;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.MapMenu;
import dk.frv.enav.common.xml.msi.MsiPoint;

/**
 * Ship specific layer class for handling all MSI messages
 */
public class MsiLayer extends MsiLayerCommon {
    
    private static final long serialVersionUID = 1L;

    private MouseDelegator mouseDelegator;
    private LatLonPoint mousePosition;
    private NewRouteContainerLayer newRouteLayer;

    /**
     * Constructor
     */
    public MsiLayer() {
        super();

        // Register the classes the will trigger the map menu
        registerMapMenuClasses(MsiSymbolGraphic.class, MsiDirectionalIcon.class);
    }

    /**
     * Returns a reference to the map menu
     * @return a reference to the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu)mapMenu;
    }   

    /**
     * If filtering is turned on, return whether to include the message or not
     * @param message the message to check
     * @return whether to include the message or not
     */
    @Override
    protected boolean filterMessage(MsiMessageExtended message) {
        // Filtering begins here
        if(EPDShip.getInstance().getSettings().getEnavSettings().isMsiFilter()){
            // It is set to be visible
            if(!message.visible) {
                if(mousePosition == null) {
                    return false;
                }
            }
            
            // Check proximity to current location (free navigation mode)
            if(mousePosition != null && !message.visible) {
                
                // Get allowed MSI visibility distance.
                double visibilityFromNewWaypoint = EPDShip.getInstance().getSettings().getEnavSettings().getMsiVisibilityFromNewWaypoint();
                
                // Check if MSI messages should be visible to ship.
                boolean visibleToShip = 
                        distanceToShip(message, this.mousePosition) <= visibilityFromNewWaypoint;
                
                // Check if MSI messages should be visible on route.
                boolean visibleOnRoute = false;
                
                // Go through each waypoint of the route to check if the MSI message should be visible.
                for (int i = 0; i < this.newRouteLayer.getRoute().getWaypoints().size(); i++) {
                    
                    RouteWaypoint rWaypoint = this.newRouteLayer.getRoute().getWaypoints().get(i);
                    Projection projection = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getProjection();
                    Point2D pointA = null;
                    Point2D pointB = null;
                    Point2D pnt    = null;
                    boolean upper  = false;
                    
                    // If the waypoint is not the last placed waypoint compare it to the next in line.
                    // Else compare it to the mouse location.
                    if (rWaypoint == this.newRouteLayer.getRoute().getWaypoints().getLast()) {
                        pointA = projection.forward(rWaypoint.getPos().getLatitude(), rWaypoint.getPos().getLongitude());
                        pointB = projection.forward(this.mousePosition.getLatitude(), this.mousePosition.getLongitude());
                    } else if (rWaypoint != this.newRouteLayer.getRoute().getWaypoints().getLast()) {
                        RouteWaypoint nWaypoint = this.newRouteLayer.getRoute().getWaypoints().get(i+1);
                        pointA = projection.forward(rWaypoint.getPos().getLatitude(), rWaypoint.getPos().getLongitude());
                        pointB = projection.forward(nWaypoint.getPos().getLatitude(), nWaypoint.getPos().getLongitude());
                    }
                    
                    // The slope of the line.
                    double slope = Math.round(
                            ((pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX())) * visibilityFromNewWaypoint);
                    
                    // If the value of slope is more than the value of visibilityFromNewWaypoint, 
                    // change the slop reverse the x and y axis.
                    if (Math.abs(slope) > visibilityFromNewWaypoint) {
                        double dy = Math.abs(pointB.getY()-pointA.getY());
                        slope = Math.round(((pointB.getX() - pointA.getX()) / (pointB.getY() - pointA.getY())) * visibilityFromNewWaypoint);
                        for (int j = 1; j*visibilityFromNewWaypoint < dy; j++) {
                            pnt = pointA;
                            
                            //Mouse placed on the right side of the last placed waypoint.
                            if (pointA.getX() <= pointB.getX()) {
                                
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()+slope, pointA.getY()+visibilityFromNewWaypoint);
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()+posSlope, pointA.getY()-visibilityFromNewWaypoint);
                                }
                                
                            // mouse placed on the left side.
                            } else if (pointA.getX() > pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()-slope, pointA.getY()-visibilityFromNewWaypoint);
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()-posSlope, pointA.getY()+visibilityFromNewWaypoint);
                                }
                            }
                            
                            if (pointA.getY() < pointB.getY() && slope == 0) {
                                pnt.setLocation(pointA.getX(), pointA.getY()+visibilityFromNewWaypoint);
                            } else if (pointA.getY() > pointB.getY() && slope == 0) {
                                pnt.setLocation(pointA.getX(), pointA.getY()-visibilityFromNewWaypoint);
                            }
                            
                            visibleOnRoute = setMessageVisible(message, visibilityFromNewWaypoint, visibleOnRoute, projection, pnt);
                        }
                    } else {
                        double dx = Math.abs(pointB.getX()-pointA.getX());
                        for (int j = 1; j*visibilityFromNewWaypoint < dx; j++) {
                            
                            pnt = pointA;
                            
                            // Mouse placed on the right side of the last placed waypoint.
                            if (pointA.getX() <= pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()+visibilityFromNewWaypoint, pointA.getY()+slope);
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()+visibilityFromNewWaypoint, pointA.getY()-posSlope);
                                }                            
                                
                                // Mouse placed on the left side of the last placed waypoint.
                            } else if (pointA.getX() > pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()-visibilityFromNewWaypoint, pointA.getY()-slope);
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()-visibilityFromNewWaypoint, pointA.getY()+posSlope);
                                }
                            }
                            
                            visibleOnRoute = setMessageVisible(message, visibilityFromNewWaypoint, visibleOnRoute, projection, pnt);
                        }
                    }
                }
                
                // If MSI message is not visible to either ship or route return false.
                if (!visibleToShip && !visibleOnRoute) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean setMessageVisible(MsiMessageExtended message, double visibilityFromNewWaypoint, boolean visibleOnRoute,
            Projection projection, Point2D pnt) {
        
        // Draw graphic to show where the point is placed on the line.
         this.newRouteLayer.getGraphics().fillOval((int) Math.round(pnt.getX()), (int) Math.round(pnt.getY()), 10, 10);
        
        LatLonPoint llpnt = projection.inverse(pnt);
        Position position = Position.create(llpnt.getLatitude(), llpnt.getLongitude());
        
        if (distanceToPoint(message, position) <= visibilityFromNewWaypoint) {
            visibleOnRoute = true;
        }
        
        return visibleOnRoute;
    }

    /**
     * Calculates the spherical distance from an MSI warning to the ship's
     * position. Currently just a test-implementation where the mouse simulates
     * the ship's position
     * 
     * @param msiMessageExtended
     *            MSI message to calculate distance for
     * @return Arc distance `c'
     */
    protected double distanceToShip(MsiMessageExtended msiMessageExtended, 
            LatLonPoint position) {
        List<MsiPoint> msiPoints = msiMessageExtended.msiMessage.getLocation()
                .getPoints();
        Double distance = Double.MAX_VALUE;
        for (MsiPoint msiPoint : msiPoints) {
            Position mouseLocation = Position.create(
                    position.getLatitude(), position.getLongitude());
            Position msiLocation = Position.create(msiPoint.getLatitude(),
                    msiPoint.getLongitude());
            double currentDistance = Calculator.range(mouseLocation,
                    msiLocation, Heading.GC);
            distance = Math.min(currentDistance, distance);
        }
        return distance;
    }

    /**
     * Calculates the spherical distance from an MSI warning to a given position
     * 
     * @param msiMessageExtended
     *            MSI message to calculate distance for
     * @return Arc distance `c'
     */
    protected double distanceToPoint(MsiMessageExtended msiMessageExtended,
            Position position) {
        List<MsiPoint> msiPoints = msiMessageExtended.msiMessage.getLocation()
                .getPoints();
        Double distance = Double.MAX_VALUE;
        for (MsiPoint msiPoint : msiPoints) {
            Position msiLocation = Position.create(msiPoint.getLatitude(),
                    msiPoint.getLongitude());
            double currentDistance = Calculator.range(position, msiLocation,
                    Heading.GC);
            distance = Math.min(currentDistance, distance);
        }
        return distance;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MouseDelegator) {
            mouseDelegator = (MouseDelegator) obj;
        }
        if (obj instanceof NewRouteContainerLayer) {
            newRouteLayer = (NewRouteContainerLayer) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = RouteEditMouseMode.MODE_ID;
        ret[2] = DragMouseMode.MODE_ID;
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {        
        if(clickedGraphics instanceof MsiSymbolGraphic){
            MsiSymbolGraphic msi = (MsiSymbolGraphic) clickedGraphics;
            getMapMenu().msiMenu(msi);
        
        } else if(clickedGraphics instanceof MsiDirectionalIcon) {
            MsiDirectionalIcon direction = (MsiDirectionalIcon) clickedGraphics;
            getMapMenu().msiDirectionalMenu(direction, this);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        if (mouseDelegator.getActiveMouseModeID() == RouteEditMouseMode.MODE_ID) {
            mousePosition = null;
            doUpdate();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseMoved(MouseEvent e) {
        // Testing mouse mode for the MSI relevancy
        if (mouseDelegator.getActiveMouseModeID() == RouteEditMouseMode.MODE_ID) {
            LatLonPoint mousePosition = ((MapBean) e.getSource())
                    .getProjection().inverse(e.getPoint());
            this.mousePosition = mousePosition;
            doUpdate();
        }
        
        return super.mouseMoved(e);
    }
}
