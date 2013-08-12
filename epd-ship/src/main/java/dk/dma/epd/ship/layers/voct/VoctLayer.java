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
package dk.dma.epd.ship.layers.voct;

import java.awt.event.MouseEvent;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;

public class VoctLayer extends OMGraphicHandlerLayer implements
        MapMouseListener {
    private static final long serialVersionUID = 1L;

    // private DynamicNogoHandler dynamicNogoHandler = null;

    private OMGraphicList graphics = new OMGraphicList();
    private OMGraphic selectedGraphic;
    private boolean dragging;
    private MapBean mapBean;

    public VoctLayer() {
        drawSAR();
    }

    public void drawSAR() {
        // Position A = Position.create(56.335, 7.885);
        // Position B = Position.create(56.335, 8.034444);
        // Position C = Position.create(56.252222, 8.034444);
        // Position D = Position.create(56.252222, 7.885);
        //
        // Position datum = Position.create(56.300555555555555,
        // 7.966666666666667);
        // double radius = 2.42;
        //

        Position A = Position.create(56.3318597430453, 7.906002842313335);
        Position B = Position.create(56.3318597430453, 8.062171759296268);
        Position C = Position.create(56.24510270810811, 8.061995117339452);
        Position D = Position.create(56.24510270810811, 7.906179484270152);

        Position datum = Position.create(56.2885059390279, 7.984087300804801);
        double radius = 2.6080318165935816;

        Position LKP = Position.create(56.37167, 7.966667);
        Position WTCPoint = Position.create(56.28850716421507, 7.966667);

        graphics.clear();

        SarGraphics sarGraphics = new SarGraphics(datum, radius, A, B, C, D,
                LKP, WTCPoint);
         graphics.add(sarGraphics);

        // Probability of Detection Area - updateable

        // PoD for each SRU, initialized with an effective area? possibly a
        // unique ID

        // Effective Area: 10 nm2 Initialize by creating box
        double width = Math.sqrt(10.0);
        double length = Math.sqrt(10.0);
        
        
        
        
//        AreaInternalGraphics effectiveArea = new AreaInternalGraphics(A,
//                width, length);
//        graphics.add(effectiveArea);
        EffectiveSRUAreaGraphics effectiveArea = new EffectiveSRUAreaGraphics(A,
                width, length);
        graphics.add(effectiveArea);

        

        System.out.println("A is: " + A.getLongitude());
        System.out.println("B is: " + B);
        System.out.println("C is: " + C);
        System.out.println("D is: " + D);

        System.out.println("Datum is: " + datum);
        
        
//        SomeGraphics routeLegGraphic = new SomeGraphics(A, B, C, D);
//        graphics.add(routeLegGraphic);
//        
//        WaypointCircle wpCircle = new WaypointCircle(EPDShip.getRouteManager().getRoute(0), 0, 0);
//        graphics.add(wpCircle);

        
        doPrepare();
    }


    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    @Override
    public void findAndInit(Object obj) {

        // if (obj instanceof DynamicNogoHandler) {
        // dynamicNogoHandler = (DynamicNogoHandler) obj;
        // }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
    }

    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
    }

    @Override
    public boolean mousePressed(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
//            doPrepare();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        // System.out.println("Mouse Clicked");
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        selectedGraphic = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                5.0f);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof AreaInternalGraphics) {
//                System.out.println("Selected Effective Area");
                selectedGraphic = omGraphic;
                break;
            }
        }

        // if (selectedGraphic instanceof WaypointCircle) {
        // WaypointCircle wpc = (WaypointCircle) selectedGraphic;
        // // mainFrame.getGlassPane().setVisible(false);
        // waypointInfoPanel.setVisible(false);
        // routeMenu.routeWaypointMenu(wpc.getRouteIndex(), wpc.getWpIndex());
        // routeMenu.setVisible(true);
        // // routeMenu.show(this, e.getX() - 2, e.getY() - 2);
        // routeMenu(e);
        // return true;
        // }

        return false;
    }

    @Override
    public void mouseEntered(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub

        
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        // System.out.println("Mouse dragged!");
        if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }

        if (!dragging) {
//            mainFrame.getGlassPane().setVisible(false);
            selectedGraphic = null;
            OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                    2.0f);
            for (OMGraphic omGraphic : allClosest) {
                if (omGraphic instanceof SarEffectiveAreaLines  ) {
//                    System.out.println("selected something");
                    selectedGraphic = omGraphic;
                    break;
                }else{
                    if (omGraphic instanceof AreaInternalGraphics  ) {
//                      System.out.println("selected something");
                      selectedGraphic = omGraphic;
//                      break;
                  }
//                    if (|| omGraphic instanceof AreaInternalGraphics)
                }
            }
        }

        if (selectedGraphic instanceof SarEffectiveAreaLines) {
            System.out.println("Selected line");
              SarEffectiveAreaLines selectedLine = (SarEffectiveAreaLines) selectedGraphic;

              
              
              
              //If bottom or top we can only adjust latitude
              
              //If sides we can adjust longitude
              
              
            // New Position of line
            LatLonPoint newLatLon = mapBean.getProjection().inverse(
                    e.getPoint());
            
            

            Position newPos = Position.create(newLatLon.getLatitude(),
                    newLatLon.getLongitude());

            selectedLine.updateArea(newPos);
            
            doPrepare();
            dragging = true;
            return true;

        }
        
        if (selectedGraphic instanceof AreaInternalGraphics) {
            System.out.println("Moving box");
            AreaInternalGraphics selectedArea = (AreaInternalGraphics) selectedGraphic;

            // New Center
            LatLonPoint newLatLon = mapBean.getProjection().inverse(
                    e.getPoint());

            Position newPos = Position.create(newLatLon.getLatitude(),
                    newLatLon.getLongitude());

            selectedArea.moveCenter(newPos);
            doPrepare();
            dragging = true;
            return true;

        }
        
        


        // if (selectedGraphic instanceof WaypointCircle) {
        // WaypointCircle wpc = (WaypointCircle) selectedGraphic;
        // if (routeManager.getActiveRouteIndex() != wpc.getRouteIndex()) {
        // RouteWaypoint routeWaypoint = wpc.getRoute().getWaypoints()
        // .get(wpc.getWpIndex());
        // LatLonPoint newLatLon = mapBean.getProjection().inverse(
        // e.getPoint());
        // Position newLocation = Position.create(newLatLon.getLatitude(),
        // newLatLon.getLongitude());
        // routeWaypoint.setPos(newLocation);
        //
        // if (wpc.getRoute().isStccApproved()) {
        //
        // wpc.getRoute().setStccApproved(false);
        // try {
        // wpc.getRoute().setName(
        // wpc.getRoute().getName().split(":")[1].trim());
        // } catch (Exception e2) {
        // System.out
        // .println("Failed to remove STCC Approved part of name");
        // }
        // }
        // routesChanged(RoutesUpdateEvent.ROUTE_WAYPOINT_MOVED);
        // dragging = true;
        // return true;
        // } else {
        // // Attemping to drag an active route, make a route copy and drag
        // // that one
        //
        // int dialogresult = JOptionPane
        // .showConfirmDialog(
        // EPDShip.getMainFrame(),
        // "You are trying to edit an active route \nDo you wish to make a copy to edit?",
        // "Route Editing", JOptionPane.YES_OPTION);
        // if (dialogresult == JOptionPane.YES_OPTION) {
        // Route route = routeManager.getRoute(
        // routeManager.getActiveRouteIndex()).copy();
        // route.setName(route.getName() + " copy");
        // routeManager.addRoute(route);
        // // dragging = true;
        //
        // // routesChanged(RoutesUpdateEvent.ROUTE_ADDED);
        // }
        // return true;
        //
        // // RouteWaypoint routeWaypoint = route.getWaypoints().get(
        // // wpc.getWpIndex());
        // // LatLonPoint newLatLon = mapBean.getProjection().inverse(
        // // e.getPoint());
        // // Position newLocation =
        // // Position.create(newLatLon.getLatitude(),
        // // newLatLon.getLongitude());
        // // routeWaypoint.setPos(newLocation);
        // // routesChanged(RoutesUpdateEvent.ROUTE_WAYPOINT_MOVED);
        // }
        // }

        return false;
    }

    @Override
    public boolean mouseMoved(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void mouseMoved() {
        graphics.deselect();
        repaint();
    }

    // public void addFrame(String message, Date validFrom, Date validTo, Double
    // draught, int errorCode){
    // SarGraphics nogoGraphic = new SarGraphics(null, validFrom, validTo,
    // draught, message, nogoHandler.getNorthWestPoint(),
    // nogoHandler.getSouthEastPoint(),
    // errorCode, true, Color.RED);
    // graphics.add(nogoGraphic);
    // }

}
