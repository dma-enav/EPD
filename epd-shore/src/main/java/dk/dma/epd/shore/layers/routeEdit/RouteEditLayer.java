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
package dk.dma.epd.shore.layers.routeEdit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.PanListener;
import com.bbn.openmap.event.PanSupport;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.layers.common.WpCircle;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.shore.event.RouteEditMouseMode;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.MapMenu;



/**
 * Layer for drawing new route. When active it will use a panning mouse mode.
 */
public class RouteEditLayer extends OMGraphicHandlerLayer implements MapMouseListener {

    private static final long serialVersionUID = 1L;
    private ChartPanel chartPanel;
    private LinkedList<RouteWaypoint> waypoints;
    private OMGraphicList graphics = new OMGraphicList();
    private WpCircle wpCircle;
    private OMLine wpLeg;
    protected PanSupport panDelegate;
    boolean panning;
    private NewRouteContainerLayer routeContainerLayer;
    private MapMenu mapMenu;

    public RouteEditLayer() {
        panDelegate = new PanSupport(this);
        wpCircle = new WpCircle();
        wpCircle.setLatLon(0, 0);
        wpCircle.setVisible(false);
        graphics.add(wpCircle);
        wpLeg = new OMLine(0,0,10,10,OMGraphicConstants.LINETYPE_RHUMB);
        wpLeg.setStroke(new BasicStroke(2));
        wpLeg.setLinePaint(Color.black);
        wpLeg.setVisible(false);
        graphics.add(wpLeg);
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        if(waypoints.size() <= 0) {
            wpLeg.setVisible(false);
        }
        return graphics;
    }

    public synchronized void addPanListener(PanListener listener) {
        panDelegate.add(listener);
    }

    protected synchronized void firePanEvent(float az) {
        //panDelegate.firePan(az,0.05f);
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof NewRouteContainerLayer){
            routeContainerLayer = (NewRouteContainerLayer) obj;
            waypoints = routeContainerLayer.getWaypoints();
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
        }
        if (obj instanceof PanListener) {
            addPanListener((PanListener) obj);
        }
        if (obj instanceof MapMenu) {
            mapMenu = (MapMenu) obj;
        }
        super.findAndInit(obj);
    }

    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }

    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[1];
        ret[0] = RouteEditMouseMode.MODEID;
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            RouteWaypoint newWaypoint = new RouteWaypoint();


            LatLonPoint newPoint = chartPanel.getMap().getProjection().inverse(e.getPoint());
            Position gl = Position.create(newPoint.getLatitude(), newPoint.getLongitude());
            newWaypoint.setPos(gl);
            waypoints.add(newWaypoint);

            if(waypoints.size() > 1) {
                RouteLeg newLeg = new RouteLeg();
                newLeg.setHeading(Heading.RL);
                RouteWaypoint prevWaypoint = waypoints.get(waypoints.size()-2);
                prevWaypoint.setOutLeg(newLeg);
                newWaypoint.setInLeg(newLeg);
                newLeg.setStartWp(prevWaypoint);
                newLeg.setEndWp(newWaypoint);
            }
            routeContainerLayer.doPrepare();
            return true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            mapMenu.routeEditMenu();
            mapMenu.setVisible(true);
            mapMenu.show(this, e.getX()-2, e.getY()-2);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        wpCircle.setVisible(true);
        doPrepare();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        wpCircle.setVisible(false);
        doPrepare();
    }

    @Override
    public void mouseMoved() {
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        int panFrame = 20;
        if(e.getPoint().x < panFrame){
            firePanEvent(270f);
        }
        if(e.getPoint().x > chartPanel.getSize().width-panFrame){
            firePanEvent(90f);
        }
        if(e.getPoint().y < panFrame){
            panning = true;
            firePanEvent(0f);
        }
        if(e.getPoint().y > chartPanel.getSize().height-panFrame){
            firePanEvent(180f);
        }
        LatLonPoint latlon = chartPanel.getMap().getProjection().inverse(e.getPoint());
        wpCircle.setLatLon(latlon.getLatitude(), latlon.getLongitude());
        if(waypoints.size() > 0){
            wpLeg.setVisible(true);
            double[] pos = new double[4];
            pos[0] = waypoints.get(waypoints.size()-1).getPos().getLatitude();
            pos[1] = waypoints.get(waypoints.size()-1).getPos().getLongitude();
            pos[2] = latlon.getLatitude();
            pos[3] = latlon.getLongitude();
            wpLeg.setLL(pos);
        }
        doPrepare();
        return true;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        return false;
    }
}
