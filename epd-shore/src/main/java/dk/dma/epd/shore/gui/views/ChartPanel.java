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
package dk.dma.epd.shore.gui.views;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Properties;

import javax.swing.BoxLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.event.ProjectionSupport;
import com.bbn.openmap.layer.shape.MultiShapeLayer;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.util.DraggableLayerMapBean;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteTCPALayer;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.layers.wms.WMSLayer;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.RouteEditMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.layers.EncLayerFactory;
import dk.dma.epd.shore.layers.GeneralLayer;
import dk.dma.epd.shore.layers.ais.AisLayer;
import dk.dma.epd.shore.layers.msi.MsiLayer;
import dk.dma.epd.shore.layers.route.RouteLayer;
import dk.dma.epd.shore.layers.routeedit.RouteEditLayer;
import dk.dma.epd.shore.layers.voct.VoctLayerCommon;
import dk.dma.epd.shore.layers.voct.VoctLayerPlanning;
import dk.dma.epd.shore.layers.voct.VoctLayerTracking;
import dk.dma.epd.shore.layers.voyage.VoyageHandlingLayer;
import dk.dma.epd.shore.layers.voyage.VoyageLayer;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.settings.EPDMapSettings;

/**
 * The panel with chart. Initializes all layers to be shown on the map.
 * 
 * @author David A. Camre (davidcamre@gmail.com)
 */
public class ChartPanel extends ChartPanelCommon {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ChartPanel.class);

    // Mouse modes
    private SelectMouseMode selectMouseMode;

    // Layers
    private VoyageLayer voyageLayer;
    private VoyageHandlingLayer voyageHandlingLayer;
    private VoctLayerCommon voctLayer;

    private MainFrame mainFrame;
    private Color background = new Color(168, 228, 255);
    protected transient ProjectionSupport projectionSupport = new ProjectionSupport(this, false);
    private LayerTogglingPanel layerTogglingPanel;

    /**
     * Constructor
     * 
     * @param mainFrame mainFrame used
     * @param jmapFrame The jmapframe connected to this chartPanel
     */
    public ChartPanel(MainFrame mainFrame, JMapFrame jmapFrame) {
        super();

        this.mainFrame = mainFrame;
        // Create the charts own maphandler
        mapHandler = new MapHandler();

        // Add the handlers to this bean
        mapHandler.add(EPDShore.getInstance().getAisHandler());
        mapHandler.add(EPDShore.getInstance().getShoreServices());
        mapHandler.add(EPDShore.getInstance().getIntendedRouteHandler());
        mapHandler.add(this);
        mapHandler.add(mainFrame);
        mapHandler.add(mainFrame.getStatusArea());
        mapHandler.add(jmapFrame);

        layerTogglingPanel = jmapFrame.getLayerTogglingPanel();
        
        // Set layout
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    /**
     * Create plugin layers
     * 
     * @param props
     *            properties
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
                LOG.error("IO Exception instantiating class \"" + className + "\"");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
    }

    /**
     * Initiate the chart with a custom map type
     */
    public void initChart(MapFrameType mapType) {

        EPDMapSettings mapSettings = EPDShore.getInstance().getSettings().getMapSettings();

        initChartDefault(mapType);

        // Set last postion
        map.setCenter(mapSettings.getCenter());

        // Get from settings
        map.setScale(mapSettings.getScale());

        add(map);
    }

    /**
     * Initiate the chart with a specific center and zoom scale
     * 
     * @param center
     *            map center
     * @param scale
     *            zoom scale
     */
    public void initChart(Point2D center, float scale) {

        initChartDefault(MapFrameType.standard);

        // Get from settings
        map.setCenter(center);
        map.setScale(scale);

        add(map);
    }

    /**
     * Initiate the default map values - must be called by a chart
     * 
     * @param voyageLayer2
     */
    public void initChartDefault(MapFrameType type) {

        Properties props = EPDShore.getInstance().getProperties();
        EPDMapSettings mapSettings = EPDShore.getInstance().getSettings().getMapSettings();

        if (EPDShore.getInstance().getSettings().getMapSettings().isUseEnc()) {
            // Try to create ENC layer
            EncLayerFactory encLayerFactory = new EncLayerFactory(EPDShore.getInstance().getSettings().getMapSettings());
            encLayer = encLayerFactory.getEncLayer();
        }

        map = new DraggableLayerMapBean();
        map.addClipComponents(mainFrame.getToolbar(), mainFrame.getStatusArea(), layerTogglingPanel);

        mouseDelegator = new MouseDelegator();
        mapHandler.add(mouseDelegator);

        mapNavMouseMode = new NavigationMouseMode(this);
        dragMouseMode = new DragMouseMode(this);
        selectMouseMode = new SelectMouseMode(this);
        routeEditMouseMode = new RouteEditMouseMode(this);

        mouseDelegator.addMouseMode(mapNavMouseMode);
        mouseDelegator.addMouseMode(dragMouseMode);
        mouseDelegator.addMouseMode(selectMouseMode);
        mouseDelegator.addMouseMode(routeEditMouseMode);
        getMap().addKeyListener(mapNavMouseMode);

        if (type != MapFrameType.SAR_Planning || type != MapFrameType.SAR_Tracking) {
            setMouseMode(mainFrame.getMouseMode());
        }

        mapHandler.add(dragMouseMode);
        mapHandler.add(mapNavMouseMode);
        mapHandler.add(selectMouseMode);
        mapHandler.add(routeEditMouseMode);

        layerHandler = new LayerHandler();

        // Get plugin layers
        createPluginLayers(props);

        map.setBackground(background);

        // Add layer handler to map handler
        mapHandler.add(layerHandler);

        // Create the general layer
        GeneralLayer generalLayer = new GeneralLayer();
        generalLayer.setVisible(true);
        mapHandler.add(generalLayer);

        // Add WMS Layer
        if (mapSettings.isUseWms()) {
            wmsLayer = new WMSLayer(EPDShore.getInstance().getSettings().getMapSettings().getWmsQuery());
            mapHandler.add(wmsLayer);
        }

        if (type == MapFrameType.standard) {
            // Add Voyage Layer
            voyageLayer = new VoyageLayer();
            voyageLayer.setVisible(true);
            mapHandler.add(voyageLayer);

            // Add AIS Layer
            aisLayer = new AisLayer(EPD.getInstance().getSettings().getAisSettings().getMinRedrawInterval() * 1000);
            aisLayer.setVisible(true);
            mapHandler.add(aisLayer);

            // Add MSI Layer
            msiLayer = new MsiLayer();
            msiLayer.setVisible(true);
            mapHandler.add(msiLayer);

            // Add Route Layer
            routeLayer = new RouteLayer();
            routeLayer.setVisible(true);
            mapHandler.add(routeLayer);

            // Create route editing layer
            newRouteContainerLayer = new NewRouteContainerLayer();
            newRouteContainerLayer.setVisible(true);
            mapHandler.add(newRouteContainerLayer);
            routeEditLayer = new RouteEditLayer();
            routeEditLayer.setVisible(true);
            mapHandler.add(routeEditLayer);

            
            // Create Intended Route Layer
            intendedRouteLayer = new IntendedRouteLayerCommon();
            intendedRouteLayer.setVisible(EPD.getInstance().getSettings().getCloudSettings().isShowIntendedRoute());
            mapHandler.add(intendedRouteLayer);

            
            //Create TCPA Graphics
            intendedRouteTCPALayer = new  IntendedRouteTCPALayer();
            intendedRouteTCPALayer.setVisible(true);
            mapHandler.add(intendedRouteTCPALayer);
            
        }

        if (type == MapFrameType.suggestedRoute) {

            // Add Voyage Layer
            voyageLayer = new VoyageLayer(true);
            voyageLayer.setVisible(true);
            mapHandler.add(voyageLayer);

            voyageHandlingLayer = new VoyageHandlingLayer();
            voyageHandlingLayer.setVisible(true);
            mapHandler.add(voyageHandlingLayer);

            // Add AIS Layer
            aisLayer = new AisLayer(EPD.getInstance().getSettings().getAisSettings().getMinRedrawInterval() * 1000);
            aisLayer.setVisible(true);
            mapHandler.add(aisLayer);

            // Add Route Layer
            routeLayer = new RouteLayer();
            routeLayer.setVisible(true);
            mapHandler.add(routeLayer);

            // Create route editing layer
            newRouteContainerLayer = new NewRouteContainerLayer();
            newRouteContainerLayer.setVisible(true);
            mapHandler.add(newRouteContainerLayer);
            routeEditLayer = new RouteEditLayer();
            routeEditLayer.setVisible(true);
            mapHandler.add(routeEditLayer);
            
            
            
            // Create Intended Route Layer
            intendedRouteLayer = new IntendedRouteLayerCommon();
            intendedRouteLayer.setVisible(EPD.getInstance().getSettings().getCloudSettings().isShowIntendedRoute());
            mapHandler.add(intendedRouteLayer);

            
            //Create TCPA Graphics
            intendedRouteTCPALayer = new  IntendedRouteTCPALayer();
            intendedRouteTCPALayer.setVisible(true);
            mapHandler.add(intendedRouteTCPALayer);
            
        }

        if (type == MapFrameType.SAR_Planning) {
            voctLayer = new VoctLayerPlanning();
            voctLayer.setVisible(true);
            mapHandler.add(voctLayer);
            mapHandler.add(EPDShore.getInstance().getVoctManager());
            mapHandler.add(EPDShore.getInstance().getSRUManager());

            // Add AIS Layer
            aisLayer = new AisLayer(EPD.getInstance().getSettings().getAisSettings().getMinRedrawInterval() * 1000);
            aisLayer.setVisible(true);
            mapHandler.add(aisLayer);

            // Add Route Layer
            routeLayer = new RouteLayer();
            routeLayer.setVisible(true);
            mapHandler.add(routeLayer);

            // Create route editing layer
            newRouteContainerLayer = new NewRouteContainerLayer();
            newRouteContainerLayer.setVisible(true);
            mapHandler.add(newRouteContainerLayer);
            routeEditLayer = new RouteEditLayer();
            routeEditLayer.setVisible(true);
            mapHandler.add(routeEditLayer);

        }

        if (type == MapFrameType.SAR_Tracking) {
            voctLayer = new VoctLayerTracking();
            voctLayer.setVisible(true);
            mapHandler.add(voctLayer);
            mapHandler.add(EPDShore.getInstance().getVoctManager());
            mapHandler.add(EPDShore.getInstance().getSRUManager());
        }

        // Create MSI handler
        MsiHandler msiHandler = EPDShore.getInstance().getMsiHandler();
        mapHandler.add(msiHandler);

        StrategicRouteHandler strategicRouteHandler = EPDShore.getInstance().getStrategicRouteHandler();
        mapHandler.add(strategicRouteHandler);


        // Create background layer
        String layerName = "background";
        bgLayer = new MultiShapeLayer();
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

        if (routeLayer != null) {
            // Force a route layer update
            routeLayer.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMouseMode(String modeID) {
        this.mouseMode = modeID;
        
        // Mode0 is mapNavMouseMode
        if (modeID.equals(NavigationMouseMode.MODEID)) {
            mouseDelegator.setActive(mapNavMouseMode); 
        }
        // Mode1 is DragNavMouseMode
        else if (modeID.equals(DragMouseMode.MODEID)) {
            mouseDelegator.setActive(dragMouseMode);
        }
        
        // Mode2 is Select
        else if (modeID.equals(SelectMouseMode.MODEID)) {
            mouseDelegator.setActive(selectMouseMode);
        }
        // Mode3 is Route Edit
        else if (modeID.equals(RouteEditMouseMode.MODEID)) {
            mouseDelegator.setActive(routeEditMouseMode);
        }
    }
    
    public VoyageLayer getVoyageLayer() {
        return voyageLayer;
    }

    public VoyageHandlingLayer getVoyageHandlingLayer() {
        return voyageHandlingLayer;
    }

}
