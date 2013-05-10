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

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.layers.ais.AisLayer;
import dk.dma.epd.shore.layers.ais.Vessel;
import dk.dma.epd.shore.service.MonaLisaHandler;
import dk.dma.epd.shore.service.MonaLisaRouteExchangeListener;
import dk.dma.epd.shore.service.MonaLisaRouteNegotiationData;
import dk.dma.epd.shore.voyage.VoyageManager;
import dk.dma.epd.shore.voyage.VoyageUpdateEvent;
import dk.dma.epd.shore.voyage.VoyageUpdateListener;

//import dk.frv.enav.ins.gui.MapMenu;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends OMGraphicHandlerLayer implements
        VoyageUpdateListener, MapMouseListener, MonaLisaRouteExchangeListener,
        IAisTargetListener {

    private static final long serialVersionUID = 1L;

    private VoyageManager voyageManager;
    private MonaLisaHandler monaLisaHandler;

    // private ShipIndicatorPanel shipIndicatorPanel;

    private Map<Long, ShipIndicatorPanel> shipIndicatorPanels = new HashMap<>();

    // private MetocInfoPanel metocInfoPanel;
    // private WaypointInfoPanel waypointInfoPanel;
    // private MapBean mapBean;

    private OMGraphicList graphics = new OMGraphicList();
    // private OMGraphic closest;
    // private OMGraphic selectedGraphic;
    private JMapFrame jMapFrame;

    private AisLayer aisLayer;
    private AisHandler aisHandler;
    private boolean windowHandling;
    

    // private MapMenu routeMenu;

    public VoyageLayer() {
        voyageManager = EPDShore.getVoyageManager();
        voyageManager.addListener(this);
    }

    public VoyageLayer(boolean windowHandling) {
        voyageManager = EPDShore.getVoyageManager();
        voyageManager.addListener(this);
        this.windowHandling = windowHandling;
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MonaLisaHandler) {
            monaLisaHandler = (MonaLisaHandler) obj;
            monaLisaHandler.addMonaLisaRouteExchangeListener(this);
            System.out.println("listener added");
        }

        if (obj instanceof JMapFrame) {
            // if (waypointInfoPanel == null && voyageManager != null) {
            // waypointInfoPanel = new WaypointInfoPanel();
            // }
            //
            jMapFrame = (JMapFrame) obj;
            // shipIndicatorPanel = new ShipIndicatorPanel();
            // jMapFrame.getGlassPanel().add(shipIndicatorPanel);

        }
        if (obj instanceof MapBean) {
            // mapBean = (MapBean) obj;
        }
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer) obj;
        }

        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            aisHandler.addListener(this);
        }

    }

    
    
    public boolean isWindowHandling() {
        return windowHandling;
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == voyageManager) {
            voyageManager.removeListener(this);
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
    public void voyagesChanged(VoyageUpdateEvent e) {

        graphics.clear();

        for (int i = 0; i < voyageManager.getVoyages().size(); i++) {
            Route route = voyageManager.getVoyages().get(i).getRoute();
            System.out.println(route);
            if (route.isVisible()) {
                System.out.println("Adding Voyage");
                VoyageGraphic voyageGraphic = new VoyageGraphic(route, i,
                        new Color(0.4f, 0.8f, 0.5f, 0.5f));
                graphics.add(voyageGraphic);
            }

        }

        graphics.project(getProjection(), true);

        doPrepare();
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    private void updateDialogLocations() {

        if (monaLisaHandler != null && !windowHandling) {

            List<Long> unhandledTransactions = monaLisaHandler
                    .getUnhandledTransactions();

            if (unhandledTransactions.size() > 0) {

                for (int j = 0; j < unhandledTransactions.size(); j++) {

                    long mmsi = monaLisaHandler
                            .getMonaLisaNegotiationData()
                            .get(monaLisaHandler.getUnhandledTransactions()
                                    .get(j)).getRouteMessage().get(0).getMmsi();

                    ShipIndicatorPanel shipIndicatorPanel;

                    if (shipIndicatorPanels.containsKey(mmsi)) {
                        shipIndicatorPanel = shipIndicatorPanels.get(mmsi);
                    } else {
                        shipIndicatorPanel = new ShipIndicatorPanel(
                                unhandledTransactions.get(j));
                    }

                    VesselTarget ship = aisHandler.getVesselTargets().get(mmsi);
                    Position position = ship.getPositionData().getPos();

                    Point2D resultPoint = aisLayer.getProjection().forward(
                            position.getLatitude(), position.getLongitude());

                    Point newPoint = new Point((int) resultPoint.getX(),
                            (int) resultPoint.getY());

                    shipIndicatorPanel.setLocation(newPoint);

                    shipIndicatorPanels.put(mmsi, shipIndicatorPanel);
                    jMapFrame.getGlassPanel().add(shipIndicatorPanel);
                    // ShipIndicatorPanels

                }
            } else {
                // Iterate through and remove old

                for (ShipIndicatorPanel value : shipIndicatorPanels.values()) {
                    jMapFrame.getGlassPanel().remove(value);
                }
                shipIndicatorPanels.clear();
            }
        }
    }

    @Override
    public void monaLisaRouteUpdate() {
        updateDialogLocations();
    }

    @Override
    public void targetUpdated(AisTarget aisTarget) {
        updateDialogLocations();
    }

}
