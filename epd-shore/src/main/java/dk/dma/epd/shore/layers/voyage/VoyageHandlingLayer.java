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
package dk.dma.epd.shore.layers.voyage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.layers.route.RouteLegGraphic;
import dk.dma.epd.common.prototype.layers.route.WaypointCircle;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;

//import dk.frv.enav.ins.gui.MapMenu;

/**
 * Layer for showing routes
 */
public class VoyageHandlingLayer extends OMGraphicHandlerLayer implements
        MapMouseListener {

    private static final long serialVersionUID = 1L;
    private boolean dragging;
    private MapMenu routeMenu;
    // private OMGraphic closest;
    private OMGraphic selectedGraphic;

    private JMapFrame jMapFrame;

    private VoyageManager voyageManager;
    private VoyagePlanInfoPanel voyagePlanInfoPanel = new VoyagePlanInfoPanel(this);
    
    private VoyageHandlingMouseOverPanel voyageHandlingMouseOverPanel = new VoyageHandlingMouseOverPanel();
    
    private OMGraphic closest;
    private float tolerance;
    
    // private MetocInfoPanel metocInfoPanel;
    // private WaypointInfoPanel waypointInfoPanel;
    private MapBean mapBean;

    private Voyage voyage;

    private Route originalRoute;
    private Route initialRecievedRoute;
    private Route newRoute;
    private boolean modified;
    private boolean renegotiate;
    
    
    boolean routeChange;

    Stroke stroke = new BasicStroke(1.0f, // Width
            BasicStroke.CAP_SQUARE, // End cap
            BasicStroke.JOIN_MITER, // Join style
            10.0f, // Miter limit
            new float[] { 1.0f, 5.0f }, // Dash pattern
            0.0f);

    private OMGraphicList graphics = new OMGraphicList();

    // private OMGraphic closest;
    // private OMGraphic selectedGraphic;
    // private JMapFrame jMapFrame;
    // private MapMenu routeMenu;

    public VoyageHandlingLayer() {
        voyageManager = EPDShore.getVoyageManager();
        // voyageManager.addListener(this);

        voyagePlanInfoPanel.setVisible(true);
        tolerance =  EPDShore.getSettings().getGuiSettings().getMouseSelectTolerance();

    }

    @Override
    public void findAndInit(Object obj) {
        // if (obj instanceof VoyageManager) {
        // voyageManager = (VoyageManager)obj;
        // voyageManager.addListener(this);
        // }

        if (obj instanceof JMapFrame) {

            // if (waypointInfoPanel == null && voyageManager != null) {
            // waypointInfoPanel = new WaypointInfoPanel();
            // }

            jMapFrame = (JMapFrame) obj;

            voyagePlanInfoPanel.setParent(jMapFrame);

            jMapFrame.getGlassPanel().add(voyagePlanInfoPanel);
            jMapFrame.getGlassPanel().add(voyageHandlingMouseOverPanel);
            // voyagePlanInfoPanel.setLocation(0, 0);
            voyagePlanInfoPanel.setBounds(0, 20, 208, 300);
            // metocInfoPanel = new MetocInfoPanel();
            // jMapFrame.getGlassPanel().add(metocInfoPanel);
            // jMapFrame.getGlassPanel().add(waypointInfoPanel);
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        if (obj instanceof MapMenu) {
            routeMenu = (MapMenu) obj;
        }

        if (obj instanceof AisHandler) {
            voyagePlanInfoPanel.setAisHandler((AisHandler) obj);
        }
        
        
        if (obj instanceof ChartPanel) {
            voyagePlanInfoPanel.setChartPanel((ChartPanel) obj);
        }

    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == voyageManager) {
            // voyageManager.removeListener(this);
        }
    }

    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = DragMouseMode.MODEID; // "DragMouseMode"
        ret[1] = NavigationMouseMode.MODEID; // "ZoomMouseMode"
        ret[2] = SelectMouseMode.MODEID; // "SelectMouseMode"
        return ret;
    }

    private void checkIfETAChanged(){
        if (!modified){
            for (int i = 0; i < newRoute.getEtas().size(); i++) {
                
                if (initialRecievedRoute.getEtas().get(i) != newRoute.getEtas().get(i)){
                    modified = true;
                    System.out.println("Forcing modified");
                    break;
                }
                
            }
//            newRoute
            
//            initialRecievedRoute
        }
    }
    
    @Override
    public boolean mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        selectedGraphic = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                tolerance);
        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof WaypointCircle
                    || omGraphic instanceof RouteLegGraphic) {
                selectedGraphic = omGraphic;
                break;
            }
        }
        
        if (selectedGraphic instanceof WaypointCircle) {
            WaypointCircle wpc = (WaypointCircle) selectedGraphic;

            if (wpc.getRouteIndex() == 1) {

                checkIfETAChanged();
                
                voyage.setRoute(newRoute);

                routeMenu.voyageWaypontMenu(this, mapBean, voyage, modified, jMapFrame,
                        voyagePlanInfoPanel, true, newRoute, null, e.getPoint(), wpc.getWpIndex(),  this.renegotiate);
                routeMenu.setVisible(true);
                routeMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }

            // We need a info panel?
            // waypointInfoPanel.setVisible(false);

        }

        if (selectedGraphic instanceof RouteLegGraphic) {
            RouteLegGraphic rlg = (RouteLegGraphic) selectedGraphic;

            if (rlg.getRouteIndex() == 1) {
                
                checkIfETAChanged();
                
                voyage.setRoute(newRoute);

                routeMenu.voyageWaypontMenu(this, mapBean,  voyage, modified, jMapFrame,
                        voyagePlanInfoPanel, false, newRoute, rlg.getRouteLeg(), e.getPoint(), 0, this.renegotiate);

                // routeMenu.routeLegMenu(rlg.getRouteIndex(),
                // rlg.getRouteLeg(), e.getPoint());
                routeMenu.setVisible(true);
                routeMenu.show(this, e.getX() - 2, e.getY() - 2);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }

        if (!dragging) {
            selectedGraphic = null;
            OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                    tolerance);
            for (OMGraphic omGraphic : allClosest) {
                if (omGraphic instanceof WaypointCircle) {
                    selectedGraphic = omGraphic;
                    break;
                }
            }
        }

        if (selectedGraphic instanceof WaypointCircle) {
            WaypointCircle wpc = (WaypointCircle) selectedGraphic;

            if (wpc.getRouteIndex() == 1 && newRoute != null) {
                RouteWaypoint routeWaypoint = newRoute.getWaypoints().get(
                        wpc.getWpIndex());
                LatLonPoint newLatLon = mapBean.getProjection().inverse(
                        e.getPoint());
                Position newLocation = Position.create(newLatLon.getLatitude(),
                        newLatLon.getLongitude());
                routeWaypoint.setPos(newLocation);

//                newRoute.calcAllWpEta();
                newRoute.calcValues(true);
                
                
                if (!modified) {
                    changeName();
                }
                modified = true;

                updateVoyages();

                dragging = true;
                return true;
            }

        }

        return false;
    }

    private void changeName() {
//        newRoute.setName(newRoute.getName() + " modified");
    }

    
    
    
    /**
     * @param newRoute the newRoute to set
     */
    public void setNewRoute(Route newRoute) {
        this.newRoute = newRoute;
    }

    public void updateVoyages() {
        // Update voyages, clear all graphics, redraw original but in red, draw
        // the new voyage.

        graphics.clear();

        Color ECDISOrange = new Color(213, 103, 45, 255);

        
        
        // Modified route, ecdis line, green broadline
        RouteGraphic modifiedVoyageGraphic = new RouteGraphic(newRoute, 1,
                false, stroke, ECDISOrange,
                new Color(0.39f, 0.69f, 0.49f, 0.6f), true, true);
        graphics.add(modifiedVoyageGraphic);

        // graphics.project(getProjection(), true);

        // Original route
        // VoyageGraphic voyageGraphic = new VoyageGraphic(originalRoute, 0,
        // new Color(1f, 0, 0, 0.6f));

        
        //Red
        RouteGraphic originalRouteGraphic = new RouteGraphic(originalRoute, 0,
                false, stroke, ECDISOrange, new Color(1f, 0, 0, 0.4f), false, true);
        graphics.add(originalRouteGraphic);
        
        
        if (routeChange){
            RouteGraphic voyageGraphic = new RouteGraphic(initialRecievedRoute, 2,
                    false, stroke, ECDISOrange, new Color(1f, 1f, 0, 0.7f), false, true);      
            graphics.add(voyageGraphic);
        }
        

        
//        new Color(1f, 1f, 0, 0.7f)
        
        

        

        graphics.project(getProjection(), true);

        doPrepare();

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved() {
        graphics.deselect();
        repaint();
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), tolerance);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof RouteLegGraphic || omGraphic instanceof WaypointCircle) {
                newClosest = omGraphic;
                break;
            }
        }



        if (newClosest != closest) {
            if (newClosest instanceof WaypointCircle || newClosest instanceof RouteLegGraphic) {
                closest = newClosest;
                
                if (closest instanceof  WaypointCircle){
                    WaypointCircle waypointCircle = (WaypointCircle)closest;
                    Point containerPoint = SwingUtilities.convertPoint(mapBean, e.getPoint(), jMapFrame);
                    voyageHandlingMouseOverPanel.setPos((int)containerPoint.getX(), (int)containerPoint.getY() - 10);
                    
                    System.out.println("Waypoint Circle info: " + waypointCircle.getRouteIndex());
                    
                    voyageHandlingMouseOverPanel.showType(waypointCircle.getRouteIndex());
                }else{
                    RouteLegGraphic waypointLeg = (RouteLegGraphic)closest;
                    Point containerPoint = SwingUtilities.convertPoint(mapBean, e.getPoint(), jMapFrame);
                    voyageHandlingMouseOverPanel.setPos((int)containerPoint.getX(), (int)containerPoint.getY() - 10);
                    
                    System.out.println("Waypoint Circle info: " + waypointLeg.getRouteIndex());
                    
                    voyageHandlingMouseOverPanel.showType(waypointLeg.getRouteIndex());
                }
                

                jMapFrame.getGlassPane().setVisible(true);
                return true;
            } else {
                voyageHandlingMouseOverPanel.setVisible(false);
                closest = null;
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean mousePressed(MouseEvent e) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            updateVoyages();
            return true;
        }
        return false;
    }

    // @Override
    // public void voyagesChanged(VoyageUpdateEvent e) {
    // graphics.clear();
    //
    // for (int i = 0; i < voyageManager.getVoyages().size(); i++) {
    // Route route = voyageManager.getVoyages().get(i).getRoute();
    //
    // if (route.isVisible()) {
    // VoyageGraphic voyageGraphic = new VoyageGraphic(route, i,
    // new Color(0.4f, 0.8f, 0.5f, 0.5f));
    // graphics.add(voyageGraphic);
    // }
    // }
    //
    // graphics.project(getProjection(), true);
    //
    // // updateVoyages();
    //
    // doPrepare();
    // }

    /**
     * Functions called when creating the layer, it paints the initial voyage
     * 
     * @param voyage
     */
    public void handleVoyage(Route originalRoute, Voyage voyage, boolean renegotiate) {
        graphics.clear();
        
        this.renegotiate = renegotiate;
        this.originalRoute = originalRoute.copy();
        this.voyage = voyage;
        this.initialRecievedRoute = voyage.getRoute().copy();
        this.newRoute = voyage.getRoute();

        Color ECDISOrange = new Color(213, 103, 45, 255);

        // Added the route as green, original recieved one
        RouteGraphic voyageGraphic = new RouteGraphic(newRoute, 1, false,
                stroke, ECDISOrange, new Color(0.39f, 0.69f, 0.49f, 0.6f), false, true);

        
        
        
       
        
        
        if (originalRoute.getWaypoints().size() != voyage.getRoute().getWaypoints().size()){
            routeChange = true;
        }else{
            for (int i = 0; i < originalRoute.getWaypoints().size(); i++) {
                
                double originalLat = originalRoute.getWaypoints().get(i).getPos().getLatitude();
                double originalLon = originalRoute.getWaypoints().get(i).getPos().getLongitude();
                
                
                double newLat = voyage.getRoute().getWaypoints().get(i).getPos().getLatitude();
                double newLon= voyage.getRoute().getWaypoints().get(i).getPos().getLongitude();
                
                
                if (originalLat != newLat){
                    routeChange = true;
                    break;
                }
                
                if (originalLon != newLon){
                    routeChange = true;
                    break;
                }
                
                
            }
        }
        

        if (routeChange){
        
        //Are the routes the same?
        //originalroute vs. newroute
        RouteGraphic originalRouteGraphic = new RouteGraphic(originalRoute, 0, false,
                stroke, ECDISOrange,  new Color(1f, 0, 0, 0.4f), false, true);
        graphics.add(originalRouteGraphic);
        }
        
        
//        RouteGraphic voyageGraphic = new RouteGraphic(newRoute, 1, false,
//                stroke, ECDISOrange, new Color(1f, 1f, 0, 0.7f), false);
        
        
        // VoyageGraphic voyageGraphic = new VoyageGraphic(newRoute, 1,
        // new Color(1f, 1f, 0, 0.6f));

        voyagePlanInfoPanel.setVoyage(voyage);


        graphics.add(voyageGraphic);
        graphics.project(getProjection(), true);
        doPrepare();

    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

}
