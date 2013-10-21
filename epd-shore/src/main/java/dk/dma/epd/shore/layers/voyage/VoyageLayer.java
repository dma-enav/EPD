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

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.layers.ais.AisLayer;
import dk.dma.epd.shore.service.StrategicRouteExchangeHandler;
import dk.dma.epd.shore.service.StrategicRouteExchangeListener;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;
import dk.dma.epd.shore.voyage.VoyageUpdateEvent;
import dk.dma.epd.shore.voyage.VoyageUpdateListener;

//import dk.frv.enav.ins.gui.MapMenu;

/**
 * Layer for showing routes
 */
public class VoyageLayer extends OMGraphicHandlerLayer implements
        VoyageUpdateListener, MapMouseListener, StrategicRouteExchangeListener,
        IAisTargetListener, ProjectionListener {

    private static final long serialVersionUID = 1L;

    private VoyageManager voyageManager;
    private StrategicRouteExchangeHandler monaLisaHandler;

    // private ShipIndicatorPanel shipIndicatorPanel;
    private VoyageInfoPanel voyageInfoPanel = new VoyageInfoPanel();

    private Map<Long, ShipIndicatorPanel> shipIndicatorPanels = new HashMap<>();

    // private MetocInfoPanel metocInfoPanel;
    // private WaypointInfoPanel waypointInfoPanel;
    private ChartPanel chartPanel;
    private MapBean mapBean;

    private OMGraphicList graphics = new OMGraphicList();
    private OMGraphic closest;
    private OMGraphic selectedGraphic;
    private JMapFrame jMapFrame;

    private AisLayer aisLayer;
    private AisHandler aisHandler;
    private boolean windowHandling;

    private MapMenu routeMenu;

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
        if (obj instanceof StrategicRouteExchangeHandler) {
            monaLisaHandler = (StrategicRouteExchangeHandler) obj;
            monaLisaHandler.addStrategicRouteExchangeListener(this);
            System.out.println("listener added");
        }

        if (obj instanceof JMapFrame) {
            // if (waypointInfoPanel == null && voyageManager != null) {
            // waypointInfoPanel = new WaypointInfoPanel();
            // }
            //
            jMapFrame = (JMapFrame) obj;
            jMapFrame.getGlassPanel().add(voyageInfoPanel);
            // shipIndicatorPanel = new ShipIndicatorPanel();
            // jMapFrame.getGlassPanel().add(shipIndicatorPanel);

        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer) obj;
        }

        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            aisHandler.addListener(this);
        }

        if (obj instanceof MapMenu) {
            routeMenu = (MapMenu) obj;
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
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
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        if (this.isVisible()) {

            selectedGraphic = null;
            OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                    5.0f);
            for (OMGraphic omGraphic : allClosest) {

                System.out.println(omGraphic.getClass());

                if (omGraphic instanceof VoyageWaypointCircle
                        || omGraphic instanceof VoyageLegGraphic) {
                    selectedGraphic = omGraphic;
                    break;
                }
            }

            if (selectedGraphic instanceof VoyageWaypointCircle) {
                // VoyageWaypointCircle wpc = (VoyageWaypointCircle)
                // selectedGraphic;

                // System.out.println("Voyage circle");

                // routeMenu.routeWaypointMenu(wpc.getRouteIndex(),
                // wpc.getWpIndex());
                // routeMenu.setVisible(true);
                // routeMenu.show(this, e.getX()-2, e.getY()-2);
                return true;
            }
            if (selectedGraphic instanceof VoyageLegGraphic) {
                VoyageLegGraphic rlg = (VoyageLegGraphic) selectedGraphic;
                int voyageIndex = rlg.getVoyageIndex();

                if (voyageManager.getVoyageCount() > voyageIndex) {

                    Voyage currentVoyage = voyageManager.getVoyage(voyageIndex);

                    routeMenu.voyageGeneralMenu(currentVoyage.getId(),
                            currentVoyage.getMmsi(), currentVoyage.getRoute(),
                            mapBean);
                    routeMenu.setVisible(true);

                    try {
                        routeMenu.show(this, e.getX() - 2, e.getY() - 2);
                    } catch (Exception e2) {
                        System.out.println("Exception error: "
                                + e2.getMessage());
                    }
                }
                return true;
            }

        }
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
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                3.0f);
        for (OMGraphic omGraphic : allClosest) {
            newClosest = omGraphic;
            break;
        }

        if (allClosest.size() == 0) {
            voyageInfoPanel.setVisible(false);
            closest = null;
            return false;
        }

        if (newClosest != closest && this.isVisible()) {
            Point containerPoint = SwingUtilities.convertPoint(chartPanel,
                    e.getPoint(), jMapFrame);

            if (newClosest instanceof VoyageLegGraphic) {
                closest = newClosest;
                VoyageLegGraphic wpLeg = (VoyageLegGraphic) newClosest;
                voyageInfoPanel.setPos((int) containerPoint.getX(),
                        (int) containerPoint.getY() - 10);
                
                int voyageIndex = wpLeg.getVoyageIndex();
                
                Voyage currentVoyage = voyageManager.getVoyage(voyageIndex);
                
                VesselTarget ship = aisHandler.getVesselTargets().get(currentVoyage.getMmsi());
                String name = "" + currentVoyage.getMmsi();
                
                if (ship != null){
                    if (ship.getStaticData() != null){
                        name = ship.getStaticData().getName();
                    }
                }
                
                voyageInfoPanel.showVoyageInfo(currentVoyage, name);
            }
        }
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
            // Route route = voyageManager.getVoyages().get(i).getRoute();
            Voyage voyage = voyageManager.getVoyages().get(i);
            // System.out.println(route);
            if (voyage.getRoute().isVisible()) {
                System.out.println("Adding Voyage");
                VoyageGraphic voyageGraphic = new VoyageGraphic(voyage, i,
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
                            .getStrategicNegotiationData()
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
    public void strategicRouteUpdate() {
        updateDialogLocations();
    }

    @Override
    public void targetUpdated(AisTarget aisTarget) {
        updateDialogLocations();
    }

}
