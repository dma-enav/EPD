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
package dk.dma.epd.ship.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MouseDelegator;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.event.mouse.CommonDistanceCircleMouseMode;
import dk.dma.epd.common.prototype.gui.util.DraggableLayerMapBean;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.layers.CommonRulerLayer;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteTCPALayer;
import dk.dma.epd.common.prototype.layers.predictor.DynamicPredictorLayer;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.layers.wms.WMSLayer;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DistanceCircleMouseMode;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.MSIFilterMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.NoGoMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.component_panels.ActiveWaypointComponentPanel;
import dk.dma.epd.ship.gui.component_panels.DockableComponentPanel;
import dk.dma.epd.ship.gui.nogo.NogoDialog;
import dk.dma.epd.ship.layers.EncLayerFactory;
import dk.dma.epd.ship.layers.GeneralLayer;
import dk.dma.epd.ship.layers.ais.AisLayer;
import dk.dma.epd.ship.layers.background.CoastalOutlineLayer;
import dk.dma.epd.ship.layers.msi.MsiLayer;
import dk.dma.epd.ship.layers.nogo.NogoLayer;
import dk.dma.epd.ship.layers.ownship.OwnShipLayer;
import dk.dma.epd.ship.layers.route.RouteLayer;
import dk.dma.epd.ship.layers.routeedit.RouteEditLayer;
import dk.dma.epd.ship.layers.voct.VoctLayer;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.service.voct.VOCTManager;
import dk.dma.epd.ship.settings.EPDMapSettings;

/**
 * The panel with chart. Initializes all layers to be shown on the map.
 */
public class ChartPanel extends ChartPanelCommon implements DockableComponentPanel, IPntDataListener,
        MouseWheelListener, VOCTUpdateListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ChartPanel.class);

    // Mouse modes
    private MSIFilterMouseMode msiFilterMouseMode;
    private CommonDistanceCircleMouseMode rangeCirclesMouseMode;
    private NoGoMouseMode noGoMouseMode;
    
    // Layers
    private OwnShipLayer ownShipLayer;
    private VoyageLayer voyageLayer;
    private NogoLayer nogoLayer;
    private VoctLayer voctLayer;
    private CommonRulerLayer rulerLayer;
    private DynamicPredictorLayer dynamicPredictorLayer;

    private TopPanel topPanel;
    private VOCTManager voctManager;
    private ActiveWaypointComponentPanel activeWaypointPanel;
    private NogoDialog nogoDialog;
    protected PntData pntData;

    
    /**
     * Constructor
     * @param activeWaypointPanel
     */
    public ChartPanel(ActiveWaypointComponentPanel activeWaypointPanel) {
        super();
        
        // Set map handler
        mapHandler = EPDShip.getInstance().getMapHandler();
        
        // Set layout
        setLayout(new BorderLayout());
        // Set border
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.activeWaypointPanel = activeWaypointPanel;

        setBackground(new Color(0.1f, 0.1f, 0.1f, 0.1f));
    }

    /**
     * Initialize the chart panel
     */
    public void initChart() {

        EPDMapSettings mapSettings = EPDShip.getInstance().getSettings()
                .getMapSettings();
        Properties props = EPDShip.getInstance().getProperties();

        // Try to create ENC layer
        EncLayerFactory encLayerFactory = new EncLayerFactory(EPDShip
                .getInstance().getSettings().getMapSettings());
        encLayer = encLayerFactory.getEncLayer();

        // Add WMS Layer
        if (mapSettings.isUseWms()) {
            wmsLayer = new WMSLayer(mapSettings.getWmsQuery());
            mapHandler.add(wmsLayer);
        }

        // Create a MapBean, and add it to the MapHandler.
        map = new DraggableLayerMapBean();
        map.setDoubleBuffered(true);

        mouseDelegator = new MouseDelegator();
        mapHandler.add(mouseDelegator);

        // Add MouseMode. The MouseDelegator will find it via the
        // MapHandler.
        // Adding NavMouseMode first makes it active.
        mapNavMouseMode = new NavigationMouseMode(this);
        noGoMouseMode = new NoGoMouseMode(this);
        routeEditMouseMode = new RouteEditMouseMode(this);
        
        msiFilterMouseMode = new MSIFilterMouseMode();
        dragMouseMode = new DragMouseMode(this);
        rangeCirclesMouseMode = new CommonDistanceCircleMouseMode();

        mouseDelegator.addMouseMode(mapNavMouseMode);
        mouseDelegator.addMouseMode(noGoMouseMode);
        mouseDelegator.addMouseMode(routeEditMouseMode);
        mouseDelegator.addMouseMode(msiFilterMouseMode);
        mouseDelegator.addMouseMode(dragMouseMode);
        mouseDelegator.addMouseMode(rangeCirclesMouseMode);
        getMap().addKeyListener(mapNavMouseMode);
        getMap().addKeyListener(noGoMouseMode);

        mouseDelegator.setActive(mapNavMouseMode);
        // Inform the distance circle mouse mode what mouse mode was initially
        // the active one
        rangeCirclesMouseMode
                .setPreviousMouseModeModeID(NavigationMouseMode.MODE_ID);

        mapHandler.add(mapNavMouseMode);
        mapHandler.add(noGoMouseMode);
        mapHandler.add(routeEditMouseMode);
        mapHandler.add(msiFilterMouseMode);
        mapHandler.add(activeWaypointPanel);
        mapHandler.add(rangeCirclesMouseMode);
        // added this to fix bug where cursor panel was not updated when in drag
        // mode
        mapHandler.add(dragMouseMode);

        // Use the LayerHandler to manage all layers, whether they are
        // on the map or not. You can add a layer to the map by
        layerHandler = new LayerHandler();
        // Get plugin layers
        createPluginLayers(props);
        
        // Add layer handler to map handler
        mapHandler.add(layerHandler);

        // Create the general layer
        GeneralLayer generalLayer = new GeneralLayer();
        generalLayer.setVisible(true);
        mapHandler.add(generalLayer);

        // Create VOCT Layer
        voctLayer = new VoctLayer();
        voctLayer.setVisible(true);
        mapHandler.add(voctLayer);

        // Create route layer
        routeLayer = new RouteLayer();
        routeLayer.setVisible(true);
        mapHandler.add(routeLayer);
        
        ScaleDisplayLayer scaleDisplay = new ScaleDisplayLayer();
        scaleDisplay.setVisible(true);
        mapHandler.add(scaleDisplay);

        // Create ruler layer
        rulerLayer = new CommonRulerLayer();
        rulerLayer.setVisible(true);
        mapHandler.add(rulerLayer);

        // Create voyage layer
        voyageLayer = new VoyageLayer();
        voyageLayer.setVisible(true);
        mapHandler.add(voyageLayer);

        // Create route editing layer
        newRouteContainerLayer = new NewRouteContainerLayer();
        newRouteContainerLayer.setVisible(true);
        mapHandler.add(newRouteContainerLayer);
        routeEditLayer = new RouteEditLayer();
        routeEditLayer.setVisible(true);
        mapHandler.add(routeEditLayer);

        // Create MSI layer
        msiLayer = new MsiLayer();
        msiLayer.setVisible(true);
        mapHandler.add(msiLayer);

        // Create Nogo layer
        nogoLayer = new NogoLayer();
        nogoLayer.setVisible(true);
        mapHandler.add(nogoLayer);

        // Create AIS layer
        aisLayer = new AisLayer(EPDShip.getInstance().getSettings().getAisSettings().getMinRedrawInterval() * 1000);
        aisLayer.setVisible(true);
        mapHandler.add(aisLayer);

        // Create own ship layer layer
        ownShipLayer = new OwnShipLayer();
        ownShipLayer.setVisible(true);
        mapHandler.add(ownShipLayer);

        // Create Intended Route Layer
        intendedRouteLayer = new IntendedRouteLayerCommon();
        intendedRouteLayer.setVisible(true);
        mapHandler.add(intendedRouteLayer);

        intendedRouteTCPALayer = new  IntendedRouteTCPALayer();
        intendedRouteTCPALayer.setVisible(true);
        mapHandler.add(intendedRouteTCPALayer);

        // Create dynamic prediction layer
        if (EPDShip.getInstance().getSettings().getNavSettings().isDynamicPrediction()) {
            dynamicPredictorLayer = new DynamicPredictorLayer();
            dynamicPredictorLayer.setVisible(true);
            mapHandler.add(dynamicPredictorLayer);
        }
        
        // Create background layer
        String layerName = "background";
        bgLayer = new CoastalOutlineLayer();
        bgLayer.setProperties(layerName, props);
        bgLayer.setAddAsBackground(true);
        bgLayer.setVisible(true);

        mapHandler.add(bgLayer);

        // Add ENC layer
        if (encLayer != null) {
            mapHandler.add(encLayer);
        }
        
        // Add map to map handler
        mapHandler.add(map);

        encLayerFactory.setMapSettings();

        // Set last postion
        map.setCenter(mapSettings.getCenter());

        // Get from settings
        map.setScale(mapSettings.getScale());
        // Set ENC map settings

        add(map);

        // Force a route layer and sensor panel update
        routeLayer.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);
        activeWaypointPanel.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);

        // Force a MSI layer update
        msiLayer.doUpdate();

        // Add this class as PNT data listener
        EPDShip.getInstance().getPntHandler().addListener(this);

        // Show AIS or not
        aisVisible(EPDShip.getInstance().getSettings().getAisSettings()
                .isVisible());
        // Show ENC or not
        encVisible(EPDShip.getInstance().getSettings().getMapSettings()
                .isEncVisible());

        // Show WMS or not
        wmsVisible(EPDShip.getInstance().getSettings().getMapSettings()
                .isWmsVisible());

        // Show intended routes or not
        intendedRouteLayerVisible(EPDShip.getInstance().getSettings().getCloudSettings().isShowIntendedRoute());
        
        getMap().addMouseWheelListener(this);
        


    }

    /**
     * Create the plug-in layers
     * @param props
     */
    private void createPluginLayers(Properties props) {
        String layersValue = props.getProperty("epd.plugin_layers");
        if (layersValue == null) {
            return;
        }
        String[] layerNames = layersValue.split(" ");
        for (String layerName : layerNames) {
            String classProperty = layerName + ".class";
            String className = props.getProperty(classProperty);
            if (className == null) {
                LOG.error("Failed to locate property " + classProperty);
                continue;
            }
            try {
                // Create it if you do...
                Object obj = java.beans.Beans.instantiate(null, className);
                if (obj instanceof Layer) {
                    Layer l = (Layer) obj;
                    // All layers have a setProperties method, and
                    // should intialize themselves with proper
                    // settings here. If a property is not set, a
                    // default should be used, or a big, graceful
                    // complaint should be issued.
                    l.setProperties(layerName, props);
                    l.setVisible(true);
                    layerHandler.addLayer(l);
                }
            } catch (java.lang.ClassNotFoundException e) {
                LOG.error("Layer class not found: \"" + className + "\"");
            } catch (java.io.IOException e) {
                LOG.error("IO Exception instantiating class \"" + className
                        + "\"");
            }
        }
    }
    
    /**
     * Zooms to the current position of the own-ship
     */
    public void centreOnShip() {
        // Get current position
        PntData gpsData = EPDShip.getInstance().getPntHandler()
                .getCurrentData();
        if (gpsData == null) {
            return;
        }
        if (gpsData.getPosition() == null) {
            return;
        }
        map.setCenter((float) gpsData.getPosition().getLatitude(),
                (float) gpsData.getPosition().getLongitude());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doZoom(float factor) {
        super.doZoom(factor);
        autoFollow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoomTo(List<Position> waypoints) {
        if (waypoints.size() > 0) {
            // Disable auto follow
            EPDShip.getInstance().getSettings().getNavSettings()
                .setAutoFollow(false);
            topPanel.updateButtons();

            super.zoomTo(waypoints);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMouseMode(String modeID) {
        
        this.mouseMode = modeID;
        
        // Switching to RouteEditMouseMode
        if (modeID.equals(RouteEditMouseMode.MODE_ID)) {
            routeEditLayer.doPrepare();
            mouseDelegator.setActive(routeEditMouseMode);

            // make sure toggle button is toggled if this mouse mode is enabled
            // by exiting DistanceCircleMouseMode
            topPanel.getNewRouteBtn().setSelected(true);
            topPanel.getNavigationMouseMode().setSelected(false);
            topPanel.getDragMouseMode().setSelected(false);
        
        } else {
            // Clear new route graphics and toggle buttons
            routeEditLayer.doPrepare();
            newRouteContainerLayer.getWaypoints().clear();
            newRouteContainerLayer.getRouteGraphics().clear();
            newRouteContainerLayer.doPrepare();
            topPanel.getNewRouteBtn().setSelected(false);
            EPDShip.getInstance().getMainFrame().getJMenuBar()
                    .getNewRoute().setSelected(false);
        }
        
        // Navigation mouse mode
        if (modeID.equals(NavigationMouseMode.MODE_ID)) {
            mouseDelegator.setActive(mapNavMouseMode);
            topPanel.getNavigationMouseMode().setSelected(true);
            topPanel.getDragMouseMode().setSelected(false);
        }
        
        // Drag mouse mode
        if (modeID.equals(DragMouseMode.MODE_ID)) {
            mouseDelegator.setActive(dragMouseMode);

            topPanel.getNavigationMouseMode().setSelected(false);
            topPanel.getDragMouseMode().setSelected(true);
            System.out.println("Setting drag mouse mode");
        }
        
        // Distance circle mouse mode
        if (modeID.equals(DistanceCircleMouseMode.MODE_ID)) {
            // Disable the other mouse mode toggle buttons.
            topPanel.getNavigationMouseMode().setSelected(false);
            topPanel.getDragMouseMode().setSelected(false);
            topPanel.getNewRouteBtn().setSelected(false);
            String prevMouseModeId = mouseDelegator.getActiveMouseMode()
                    .getID();
            // Store previous mouse mode ID such that we can go back to that
            // mouse mode
            rangeCirclesMouseMode.setPreviousMouseModeModeID(prevMouseModeId);
            // Display the ruler layer.
            mouseDelegator.setActive(rangeCirclesMouseMode);
        } else {
            // When mouse mode is changed to something different than distance
            // circle mode
            // we untoggle the distance circle mode button
            topPanel.getToggleButtonDistanceCircleMouseMode().setSelected(false);
            rulerLayer.clearRuler();
        }
        
        // Request NoGo Area.
        if (modeID.equals(NoGoMouseMode.MODE_ID)) {
            // Set the mouse mode.
            mouseDelegator.setActive(noGoMouseMode);
        }     
    }

    /**
     * Will auto-follow the own-ship if the setting is turned on
     */
    public void autoFollow() {
        // Do auto follow
        if (!EPDShip.getInstance().getSettings().getNavSettings()
                .isAutoFollow()) {
            return;
        }

        // Only do auto follow if not bad position
        if (pntData == null || pntData.isBadPosition()) {
            return;
        }

        boolean lookahead = EPDShip.getInstance().getSettings()
                .getNavSettings().isLookAhead();

        // Find desired location (depends on look-ahead or not)

        double centerX = map.getWidth() / 2.0;
        double centerY = map.getHeight() / 2.0;
        double desiredX = centerX;
        double desiredY = centerY;

        if (lookahead) {
            double lookAheadBorder = 100.0;
            double lookAheadMinSpd = 1.0;
            double lookAheadMaxSpd = 15.0;

            // Calculate a factor [0;1] from speed
            double factor = 0;
            if (pntData.getSog() < lookAheadMinSpd) {
                factor = 0;
            } else if (pntData.getSog() < lookAheadMaxSpd) {
                factor = pntData.getSog() / lookAheadMaxSpd;
            } else {
                factor = 1.0;
            }

            double phiX = Math.cos(Math.toRadians(pntData.getCog()) - 3
                    * Math.PI / 2);
            double phiY = Math.sin(Math.toRadians(pntData.getCog()) - 3
                    * Math.PI / 2);

            double fx = factor * phiX;
            double fy = factor * phiY;

            desiredX = centerX + (centerX - lookAheadBorder) * fx;
            desiredY = centerY + (centerY - lookAheadBorder) * fy;

        }

        // Get projected x,y of current position
        Point2D shipXY = map.getProjection().forward(
                pntData.getPosition().getLatitude(),
                pntData.getPosition().getLongitude());

        // Calculate how many percent the position is off for x and y
        double pctOffX = Math.abs(desiredX - shipXY.getX()) / map.getWidth()
                * 100.0;
        double pctOffY = Math.abs(desiredY - shipXY.getY()) / map.getHeight()
                * 100.0;

        // LOG.info("pctOffX: " + pctOffX + " pctOffY: " + pctOffY);

        int tollerated = EPDShip.getInstance().getSettings().getNavSettings()
                .getAutoFollowPctOffTollerance();
        if (pctOffX < tollerated && pctOffY < tollerated) {
            return;
        }

        if (lookahead) {
            Point2D forwardCenter = map.getProjection().inverse(
                    centerX - desiredX + shipXY.getX(),
                    centerY - desiredY + shipXY.getY());
            map.setCenter((float) forwardCenter.getY(),
                    (float) forwardCenter.getX());
        } else {
            map.setCenter((float) pntData.getPosition().getLatitude(),
                    (float) pntData.getPosition().getLongitude());
        }

    }

    /**
     * Receive GPS update
     */
    @Override
    public void pntDataUpdate(PntData gpsData) {
        this.pntData = gpsData;
        autoFollow();
    }

    /**
     * Called when projection has been changed by user
     */
    public void manualProjChange() {
        topPanel.disableAutoFollow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof TopPanel) {
            topPanel = (TopPanel) obj;
            // Maybe no S52 layer
            if (encLayer == null) {
                topPanel.setEncDisabled();
            }
            if (wmsLayer == null) {
                topPanel.setWMSDisabled();
            }
        }
        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
            voctManager.addListener(this);
        }
    }

    /**
     * Call auto follow when zooming
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        autoFollow();
    }

    /**
     * Called upon receiving VOCT updates
     */
    @Override
    public void voctUpdated(VOCTUpdateEvent e) {
        if (e == VOCTUpdateEvent.SAR_RECEIVED_CLOUD) {
            if (voctManager.getSarType() == SAR_TYPE.RAPID_RESPONSE) {

                RapidResponseData data = (RapidResponseData) voctManager
                        .getSarData();

                if (data.getEffortAllocationData().size() > 0) {
                    List<Position> waypoints = new ArrayList<Position>();
                    waypoints.add(data.getA());
                    waypoints.add(data.getB());
                    waypoints.add(data.getC());
                    waypoints.add(data.getD());

                    zoomTo(waypoints);
                    return;

                } else {
                    List<Position> waypoints = new ArrayList<Position>();

                    waypoints.add(data.getDatum());
                    zoomTo(waypoints);
                    return;

                }

            }
        }

        if (e == VOCTUpdateEvent.SAR_DISPLAY) {

            if (voctManager.getSarType() == SAR_TYPE.RAPID_RESPONSE) {
                RapidResponseData data = (RapidResponseData) voctManager
                        .getSarData();

                List<Position> waypoints = new ArrayList<Position>();
                waypoints.add(data.getA());
                waypoints.add(data.getB());
                waypoints.add(data.getC());
                waypoints.add(data.getD());

                zoomTo(waypoints);
                return;
            }

            if (voctManager.getSarType() == SAR_TYPE.DATUM_POINT) {
                DatumPointData data = (DatumPointData) voctManager.getSarData();

                List<Position> waypoints = new ArrayList<Position>();
                waypoints.add(data.getA());
                waypoints.add(data.getB());
                waypoints.add(data.getC());
                waypoints.add(data.getD());

                zoomTo(waypoints);
                return;
            }

        }
    }

    public HistoryListener getProjectChangeListener() {
        return getHistoryListener();
    }
    
    public NoGoMouseMode getNoGoMouseMode() {
        return noGoMouseMode;
    }

    public void setNogoDialog(NogoDialog dialog) {
        this.nogoDialog = dialog;
    }

    public NogoDialog getNogoDialog() {
        return nogoDialog;
    }
    
    public void setDynamicPredictorLayerVisibility(boolean visible) {
        if(this.dynamicPredictorLayer != null) {
            this.dynamicPredictorLayer.setVisible(visible);
        }
    }

    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "Chart";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return false;
    }
}
