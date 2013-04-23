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
package dk.dma.epd.ship.layers.voyage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.layers.route.RouteGraphic;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends OMGraphicHandlerLayer implements
        MapMouseListener, Runnable {

    private static final long serialVersionUID = 1L;

    private OMGraphicList graphics = new OMGraphicList();
    private float routeWidth = 2.0f;
    private Timer routeAnimatorTimer;

    
    private Route primaryRoute;
    
    private int animationTimer = 100;

    public VoyageLayer() {

    }

    
    public void startRouteNegotiation(Route route){
        
        this.primaryRoute = route;
        
        Stroke stroke = new BasicStroke(routeWidth, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 3.0f, 10.0f }, // Dash pattern
                0.0f);
        
        Color ECDISOrange = new Color(213, 103, 45, 255);

        // Added the route as green, original recieved one
        RouteGraphic routeGraphic = new RouteGraphic(primaryRoute, 0,
                true, stroke, ECDISOrange);
        graphics.add(routeGraphic);
        graphics.project(getProjection(), true);
        doPrepare();
        
        startRouteAnimation();
    }
    
    
    
    
    private void startRouteAnimation() {

        RouteGraphic animatedRoute = null;

        for (int i = 0; i < graphics.size(); i++) {

            if (graphics.get(i) instanceof RouteGraphic) {

                if (primaryRoute == ((RouteGraphic) graphics.get(i)).getRoute()) {
                    animatedRoute = (RouteGraphic) graphics.get(i);
                    animatedRoute.activateAnimation();

                }

            }
        }
        final RouteGraphic animatedRoute2 = animatedRoute;

        routeAnimatorTimer = new Timer(true);

        routeAnimatorTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                animatedRoute2.updateAnimationLine();
            }
        }, 0, animationTimer);

    }
    
    public void stopRouteAnimated() {
        routeAnimatorTimer.cancel();
    }
    
    @Override
    public void findAndInit(Object obj) {
        // if (obj instanceof VoyageManager) {
        // voyageManager = (VoyageManager)obj;
        // voyageManager.addListener(this);
        // }

        // if (obj instanceof JMapFrame){
        // if (waypointInfoPanel == null && voyageManager != null) {
        // waypointInfoPanel = new WaypointInfoPanel();
        // }
        //
        // jMapFrame = (JMapFrame) obj;
        // metocInfoPanel = new MetocInfoPanel();
        // jMapFrame.getGlassPanel().add(metocInfoPanel);
        // jMapFrame.getGlassPanel().add(waypointInfoPanel);
        // }
        if (obj instanceof MapBean) {
//            mapBean = (MapBean) obj;
        }
        // if(obj instanceof MapMenu){
        // routeMenu = (MapMenu) obj;
        // }

    }

    @Override
    public void findAndUndo(Object obj) {

    }

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
    public boolean mouseClicked(MouseEvent e) {
        // if(e.getButton() != MouseEvent.BUTTON3){
        // return false;
        // }
        //
        // selectedGraphic = null;
        // OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
        // 5.0f);
        // for (OMGraphic omGraphic : allClosest) {
        // if (omGraphic instanceof WaypointCircle || omGraphic instanceof
        // RouteLegGraphic) {
        // selectedGraphic = omGraphic;
        // break;
        // }
        // }
        //
        //
        // if(selectedGraphic instanceof WaypointCircle){
        // WaypointCircle wpc = (WaypointCircle) selectedGraphic;
        // waypointInfoPanel.setVisible(false);
        // routeMenu.routeWaypointMenu(wpc.getRouteIndex(), wpc.getWpIndex());
        // routeMenu.setVisible(true);
        // routeMenu.show(this, e.getX()-2, e.getY()-2);
        // return true;
        // }
        // if(selectedGraphic instanceof RouteLegGraphic){
        // RouteLegGraphic rlg = (RouteLegGraphic) selectedGraphic;
        // waypointInfoPanel.setVisible(false);
        // routeMenu.routeLegMenu(rlg.getRouteIndex(), rlg.getRouteLeg(),
        // e.getPoint());
        // routeMenu.setVisible(true);
        // routeMenu.show(this, e.getX()-2, e.getY()-2);
        // return true;
        // }
        //
        return false;
    }

    // Cannot edit voyages
    @Override
    public boolean mouseDragged(MouseEvent e) {

        return false;
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
        // OMGraphic newClosest = null;
        // OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
        // 2.0f);
        //
        // for (OMGraphic omGraphic : allClosest) {
        // if (omGraphic instanceof MetocPointGraphic || omGraphic instanceof
        // WaypointCircle) {
        // newClosest = omGraphic;
        // break;
        // }
        // }
        //
        // if (routeMetoc != null && metocInfoPanel != null) {
        // if (newClosest != closest) {
        // if (newClosest == null) {
        // metocInfoPanel.setVisible(false);
        // waypointInfoPanel.setVisible(false);
        // closest = null;
        // } else {
        // if (newClosest instanceof MetocPointGraphic) {
        // closest = newClosest;
        // MetocPointGraphic pointGraphic = (MetocPointGraphic)newClosest;
        // MetocForecastPoint pointForecast = pointGraphic.getMetocPoint();
        // Point containerPoint = SwingUtilities.convertPoint(mapBean,
        // e.getPoint(), jMapFrame);
        // metocInfoPanel.setPos((int)containerPoint.getX(),
        // (int)containerPoint.getY());
        // metocInfoPanel.showText(pointForecast,
        // pointGraphic.getMetocGraphic().getRoute().getRouteMetocSettings());
        // waypointInfoPanel.setVisible(false);
        // jMapFrame.getGlassPane().setVisible(true);
        // return true;
        // }
        // }
        // }
        // }
        //
        // if (newClosest != closest) {
        // if (newClosest instanceof WaypointCircle) {
        // closest = newClosest;
        // WaypointCircle waypointCircle = (WaypointCircle)closest;
        // Point containerPoint = SwingUtilities.convertPoint(mapBean,
        // e.getPoint(), jMapFrame);
        // waypointInfoPanel.setPos((int)containerPoint.getX(),
        // (int)containerPoint.getY() - 10);
        // waypointInfoPanel.showWpInfo(waypointCircle.getRoute(),
        // waypointCircle.getWpIndex());
        // jMapFrame.getGlassPane().setVisible(true);
        // metocInfoPanel.setVisible(false);
        // return true;
        // } else {
        // waypointInfoPanel.setVisible(false);
        // closest = null;
        // return true;
        // }
        // }
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        return false;
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(animationTimer);
        }
    }
    
    
}
