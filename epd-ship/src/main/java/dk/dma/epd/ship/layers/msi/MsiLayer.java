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
package dk.dma.epd.ship.layers.msi;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;

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
import dk.dma.epd.common.prototype.settings.handlers.MSIHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.MSILayerCommonSettingsListener;
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
    private MSIHandlerCommonSettings<?> handlerSettings;
    
    /**
     * Constructor
     */
    public MsiLayer(MSILayerCommonSettings<MSILayerCommonSettingsListener> layerSettings, MSIHandlerCommonSettings<?> handlerSettings) {
        super(layerSettings);
        // register self as observer of own settings
        layerSettings.addObserver(this);
        this.handlerSettings = Objects.requireNonNull(handlerSettings);
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
        if(handlerSettings.isMsiFilter()){
            // It is set to be visible
            if(!message.visible) {
                if(mousePosition == null) {
                    return false;
                }
            }
            
            // Check proximity to current location (free navigation mode)
            if(mousePosition != null && !message.visible) {
                
                // Check if MSI messages should be visible to ship.
                boolean visibleToShip = 
                        distanceToShip(message, this.mousePosition) <= getSettings().getMsiVisibilityFromNewWaypoint();
                
                // Check if MSI messages should be visible on route.
                boolean visibleOnRoute = false;
                
                // Go through each waypoint of the route to check if the MSI message should be visible.
                for (int i = 0; i < this.newRouteLayer.getRoute().getWaypoints().size(); i++) {
                    
                    RouteWaypoint rWaypoint = this.newRouteLayer.getRoute().getWaypoints().get(i);
                    Projection projection = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getProjection();
                    Point2D pointA = null;
                    Point2D pointB = null;
                    Point2D pnt;
                    
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
                            ((pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX())) * getSettings().getMsiVisibilityFromNewWaypoint());
                    
                    // If the value of slope is more than the value of visibilityFromNewWaypoint, 
                    // change the slop reverse the x and y axis.
                    if (Math.abs(slope) > getSettings().getMsiVisibilityFromNewWaypoint()) {
                        double dy = Math.abs(pointB.getY()-pointA.getY());
                        slope = Math.round(((pointB.getX() - pointA.getX()) / (pointB.getY() - pointA.getY())) * getSettings().getMsiVisibilityFromNewWaypoint());
                        for (int j = 0; j*getSettings().getMsiVisibilityFromNewWaypoint() < dy; j++) {
                            pnt = pointA;
                            
                            // The first point should be placed a point where the mouse was clicked.
                            if (j == 0) {
                                visibleOnRoute = setMessageVisible(message, getSettings().getMsiVisibilityFromNewWaypoint(), visibleOnRoute, projection, pnt);
                                continue;
                            }
                            
                            //Mouse placed on the right side of the last placed waypoint.
                            if (pointA.getX() <= pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()+slope, pointA.getY()+getSettings().getMsiVisibilityFromNewWaypoint());
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()+posSlope, pointA.getY()-getSettings().getMsiVisibilityFromNewWaypoint());
                                }
                                
                            // mouse placed on the left side.
                            } else if (pointA.getX() > pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()-slope, pointA.getY()-getSettings().getMsiVisibilityFromNewWaypoint());
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()-posSlope, pointA.getY()+getSettings().getMsiVisibilityFromNewWaypoint());
                                }
                            }
                            
                            // Handles placing of point on a vertical line.
                            if (pointA.getY() < pointB.getY() && slope == 0) {
                                pnt.setLocation(pointA.getX(), pointA.getY()+getSettings().getMsiVisibilityFromNewWaypoint());
                            } else if (pointA.getY() > pointB.getY() && slope == 0) {
                                pnt.setLocation(pointA.getX(), pointA.getY()-getSettings().getMsiVisibilityFromNewWaypoint());
                            }
                            
                            visibleOnRoute = setMessageVisible(message, getSettings().getMsiVisibilityFromNewWaypoint(), visibleOnRoute, projection, pnt);
                        }
                    } else {
                        double dx = Math.abs(pointB.getX()-pointA.getX());
                        for (int j = 0; j*getSettings().getMsiVisibilityFromNewWaypoint() < dx; j++) {
                            pnt = pointA;
                            
                            if (j == 0) {
                                visibleOnRoute = setMessageVisible(message, getSettings().getMsiVisibilityFromNewWaypoint(), visibleOnRoute, projection, pnt);
                                continue;
                            }
                            
                            // Mouse placed on the right side of the last placed waypoint.
                            if (pointA.getX() <= pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()+getSettings().getMsiVisibilityFromNewWaypoint(), pointA.getY()+slope);
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()+getSettings().getMsiVisibilityFromNewWaypoint(), pointA.getY()-posSlope);
                                }                            
                                
                            // Mouse placed on the left side of the last placed waypoint.
                            } else if (pointA.getX() > pointB.getX()) {
                                
                                if (slope > 0) {
                                    pnt.setLocation(pointA.getX()-getSettings().getMsiVisibilityFromNewWaypoint(), pointA.getY()-slope);
                                } else if (slope < 0) {
                                    double posSlope = Math.abs(slope);
                                    pnt.setLocation(pointA.getX()-getSettings().getMsiVisibilityFromNewWaypoint(), pointA.getY()+posSlope);
                                }
                            }
                            
                            if (pointA.getX() < pointB.getX() && 
                                    slope == 0) {
                                pnt.setLocation(pointA.getX()+getSettings().getMsiVisibilityFromNewWaypoint(), pointA.getY());
                            } else if (pointA.getX() > pointB.getX() && 
                                    slope == 0) {
                                pnt.setLocation(pointA.getX()-getSettings().getMsiVisibilityFromNewWaypoint(), pointA.getY());
                            }
                            
                            visibleOnRoute = setMessageVisible(message, getSettings().getMsiVisibilityFromNewWaypoint(), visibleOnRoute, projection, pnt);
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
//        this.newRouteLayer.getGraphics().fillOval((int) Math.round(pnt.getX()), (int) Math.round(pnt.getY()), 10, 10);
        
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
