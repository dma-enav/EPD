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
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.swing.BorderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.BufferedLayerMapBean;
import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.Proj;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.util.MapBeanSlave;
import dk.dma.epd.common.prototype.gui.util.SimpleOffScreenMapRenderer;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.MSIFilterMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.component_panels.ActiveWaypointComponentPanel;
import dk.dma.epd.ship.gui.nogo.NogoDialog;
import dk.dma.epd.ship.layers.EncLayerFactory;
import dk.dma.epd.ship.layers.GeneralLayer;
import dk.dma.epd.ship.layers.ais.AisLayer;
import dk.dma.epd.ship.layers.background.CoastalOutlineLayer;
import dk.dma.epd.ship.layers.gps.GpsLayer;
import dk.dma.epd.ship.layers.msi.EpdMsiLayer;
import dk.dma.epd.ship.layers.nogo.DynamicNogoLayer;
import dk.dma.epd.ship.layers.nogo.NogoLayer;
import dk.dma.epd.ship.layers.route.RouteLayer;
import dk.dma.epd.ship.layers.routeEdit.RouteEditLayer;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.settings.EPDMapSettings;

/**
 * The panel with chart. Initializes all layers to be shown on the map.
 */
public class ChartPanel extends OMComponentPanel implements IGpsDataListener,
        MouseWheelListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ChartPanel.class);

    private MapHandler mapHandler;
    private MapHandler dragMapHandler;
    private LayerHandler layerHandler;
    private BufferedLayerMapBean map;
    private BufferedLayerMapBean dragMap;
    private SimpleOffScreenMapRenderer dragMapRenderer;
    private GpsLayer gpsLayer;
    private Layer encLayer;
    private Layer encDragLayer;
    private AisLayer aisLayer;
    private GeneralLayer generalLayer;
    private CoastalOutlineLayer coastalOutlineLayer;
    private NavigationMouseMode mapNavMouseMode;
    private DragMouseMode dragMouseMode;
    private MouseDelegator mouseDelegator;
    private RouteLayer routeLayer;
    private VoyageLayer voyageLayer;
    private EpdMsiLayer msiLayer;
    private NogoLayer nogoLayer;
    private DynamicNogoLayer dynamicNogoLayer;
    private TopPanel topPanel;
    private RouteEditMouseMode routeEditMouseMode;
    private RouteEditLayer routeEditLayer;
    private NewRouteContainerLayer newRouteContainerLayer;
    public int maxScale = 5000;
    private MSIFilterMouseMode msiFilterMouseMode;
    private GpsData gpsData;
    private boolean nogoMode;

    private ActiveWaypointComponentPanel activeWaypointPanel;

    private NogoDialog nogoDialog;

    public ChartPanel(ActiveWaypointComponentPanel activeWaypointPanel) {
        super();
        // Set map handler
        mapHandler = EPDShip.getMapHandler();
        dragMapHandler = new MapHandler();
        // Set layout
        setLayout(new BorderLayout());
        // Set border
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        this.activeWaypointPanel = activeWaypointPanel;
        // Max scale
        this.maxScale = EPDShip.getSettings().getMapSettings().getMaxScale();

        setBackground(new Color(0.1f, 0.1f, 0.1f, 0.1f));
    }

    /**
     * 
     */
    public void initChart() {

        EPDMapSettings mapSettings = EPDShip.getSettings().getMapSettings();
        Properties props = EPDShip.getProperties();

        // Try to create ENC layer
        EncLayerFactory encLayerFactory = new EncLayerFactory(EPDShip
                .getSettings().getMapSettings());
        encLayer = encLayerFactory.getEncLayer();

        EncLayerFactory encLayerFactory2 = new EncLayerFactory(EPDShip
                .getSettings().getMapSettings());

        encDragLayer = encLayerFactory2.getEncLayer();

        // Create a MapBean, and add it to the MapHandler.
        map = new BufferedLayerMapBean();
        map.setDoubleBuffered(true);

        dragMap = new BufferedLayerMapBean();
        dragMap.setDoubleBuffered(true);

        // Orthographic test = new Orthographic((LatLonPoint)
        // mapSettings.getCenter(), mapSettings.getScale(), 1000, 1000);
        // map.setProjection(test);

        mouseDelegator = new MouseDelegator();
        mapHandler.add(mouseDelegator);

        // Add MouseMode. The MouseDelegator will find it via the
        // MapHandler.
        // Adding NavMouseMode first makes it active.
        // mapHandler.add(new NavMouseMode());
        mapNavMouseMode = new NavigationMouseMode(this);
        routeEditMouseMode = new RouteEditMouseMode();
        msiFilterMouseMode = new MSIFilterMouseMode();
        dragMouseMode = new DragMouseMode();

        mouseDelegator.addMouseMode(mapNavMouseMode);
        mouseDelegator.addMouseMode(routeEditMouseMode);
        mouseDelegator.addMouseMode(msiFilterMouseMode);
        mouseDelegator.addMouseMode(dragMouseMode);
        mouseDelegator.setActive(mapNavMouseMode);

        mapHandler.add(mapNavMouseMode);
        mapHandler.add(routeEditMouseMode);
        mapHandler.add(msiFilterMouseMode);

        mapHandler.add(activeWaypointPanel);

        // Use the LayerHandler to manage all layers, whether they are
        // on the map or not. You can add a layer to the map by
        // setting layer.setVisible(true).
        layerHandler = new LayerHandler();
        // Get plugin layers
        createPluginLayers(props);

        // Add layer handler to map handler
        mapHandler.add(layerHandler);

        dragMapHandler.add(new LayerHandler());

        // Create the general layer
        generalLayer = new GeneralLayer();
        generalLayer.setVisible(true);
        mapHandler.add(generalLayer);

        // Create route layer
        routeLayer = new RouteLayer();
        routeLayer.setVisible(true);
        mapHandler.add(routeLayer);

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
        msiLayer = new EpdMsiLayer();
        msiLayer.setVisible(true);
        mapHandler.add(msiLayer);

        // Create Nogo layer
        nogoLayer = new NogoLayer();
        nogoLayer.setVisible(true);
        mapHandler.add(nogoLayer);

        dynamicNogoLayer = new DynamicNogoLayer();
        dynamicNogoLayer.setVisible(true);
        mapHandler.add(dynamicNogoLayer);

        // Create AIS layer
        aisLayer = new AisLayer();
        aisLayer.setMinRedrawInterval(EPDShip.getSettings().getAisSettings()
                .getMinRedrawInterval() * 1000);
        aisLayer.setVisible(true);
        mapHandler.add(aisLayer);

        // Create GPS layer
        gpsLayer = new GpsLayer();
        gpsLayer.setVisible(true);
        mapHandler.add(gpsLayer);

        // Create a esri shape layer
        // URL dbf = EeINS.class.getResource("/shape/urbanap020.dbf");
        // URL shp = EeINS.class.getResource("/shape/urbanap020.shp");
        // URL shx = EeINS.class.getResource("/shape/urbanap020.shx");
        //
        // DrawingAttributes da = new DrawingAttributes();
        // da.setFillPaint(Color.blue);
        // da.setLinePaint(Color.black);
        //
        // EsriLayer esriLayer = new EsriLayer("Drogden", dbf, shp, shx, da);
        // mapHandler.add(esriLayer);

        // Create background layer
        String layerName = "background";
        coastalOutlineLayer = new CoastalOutlineLayer();
        coastalOutlineLayer.setProperties(layerName, props);
        coastalOutlineLayer.setAddAsBackground(true);
        coastalOutlineLayer.setVisible(true);

        mapHandler.add(coastalOutlineLayer);

        CoastalOutlineLayer coastalOutlineLayerDrag = new CoastalOutlineLayer();
        coastalOutlineLayerDrag.setProperties(layerName, props);
        coastalOutlineLayerDrag.setAddAsBackground(true);
        coastalOutlineLayerDrag.setVisible(true);
        dragMapHandler.add(coastalOutlineLayerDrag);

        if (encLayer != null) {
            mapHandler.add(encLayer);
            dragMapHandler.add(encDragLayer);
        }

        // Add map to map handler
        mapHandler.add(map);

        dragMapHandler.add(dragMap);

        // Set last postion
        map.setCenter(mapSettings.getCenter());
        dragMap.setCenter(mapSettings.getCenter());

        // Get from settings
        map.setScale(mapSettings.getScale());
        dragMap.setScale(mapSettings.getScale());

        add(map);

        // create an offscreen renderer
        dragMapRenderer = new SimpleOffScreenMapRenderer(map, dragMap, 3);
        dragMapRenderer.start();

        // Force a route layer and sensor panel update
        routeLayer.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);
        activeWaypointPanel.routesChanged(RoutesUpdateEvent.ROUTE_ADDED);

        // Force a MSI layer update
        msiLayer.doUpdate();

        // Add this class as GPS data listener
        EPDShip.getGpsHandler().addListener(this);

        // Set ENC map settings
        encLayerFactory.setMapSettings();
        encLayerFactory2.setMapSettings();

        // Hack to flush ENC layer
        encLayerFactory.emptyCacheAndPrepare();
//        encLayerFactory2.emptyCacheAndPrepare();

        // Show AIS or not
        aisVisible(EPDShip.getSettings().getAisSettings().isVisible());
        // Show ENC or not
        encVisible(EPDShip.getSettings().getMapSettings().isEncVisible());
        // Maybe disable ENC
        if (encLayer == null && topPanel != null) {
            topPanel.setEncDisabled();
            coastalOutlineLayer.setVisible(true);
        }

        getMap().addMouseWheelListener(this);

    }

    public SimpleOffScreenMapRenderer getDragMapRenderer() {
        return dragMapRenderer;
    }

    /**
     * @return the dragMap
     */
    public BufferedLayerMapBean getDragMap() {
        return dragMap;
    }

    public void saveSettings() {
        EPDMapSettings mapSettings = EPDShip.getSettings().getMapSettings();
        mapSettings.setCenter((LatLonPoint) map.getCenter());
        mapSettings.setScale(map.getScale());
    }

    public MapBean getMap() {
        return map;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public Layer getGpsLayer() {
        return gpsLayer;
    }

    public Layer getEncLayer() {
        return encLayer;
    }

    public void centreOnShip() {
        // Get current position
        GpsData gpsData = EPDShip.getGpsHandler().getCurrentData();
        if (gpsData == null) {
            return;
        }
        if (gpsData.getPosition() == null) {
            return;
        }
        map.setCenter((float) gpsData.getPosition().getLatitude(),
                (float) gpsData.getPosition().getLongitude());

    }

    public void doZoom(float factor) {
        float newScale = map.getScale() * factor;
        if (newScale < maxScale) {
            newScale = maxScale;
        }
        map.setScale(newScale);
        autoFollow();
    }

    public void aisVisible(boolean visible) {
        aisLayer.setVisible(visible);
    }

    public void encVisible(boolean visible) {
        if (encLayer != null) {
            encLayer.setVisible(visible);
            encDragLayer.setVisible(visible);
            coastalOutlineLayer.setVisible(!visible);
            if (!visible) {
                // Force update of background layer
                coastalOutlineLayer.forceRedraw();
            }
        } else {
            coastalOutlineLayer.setVisible(true);
        }
    }

    /**
     * Change the mouse mode
     * 
     * @param mode
     *            0 for NavMode, 1 for DragMode, 2 for SelectMode
     */
    public void setMouseMode(int mode) {
        // Mode0 is routeEditMouseMode
        if (mode == 0) {
            mouseDelegator.setActive(routeEditMouseMode);
            routeEditLayer.setVisible(true);
            routeEditLayer.setEnabled(true);
            newRouteContainerLayer.setVisible(true);

            EPDShip.getMainFrame().getTopPanel().getNavigationMouseMode()
                    .setSelected(false);
            EPDShip.getMainFrame().getTopPanel().getDragMouseMode()
                    .setSelected(false);
        }

        // Mode1 is NavMouseMode
        // Mode2 is DragMouseMode
        if (mode == 1 || mode == 2) {
            routeEditLayer.setVisible(false);
            routeEditLayer.doPrepare();
            newRouteContainerLayer.setVisible(false);
            newRouteContainerLayer.getWaypoints().clear();
            newRouteContainerLayer.getRouteGraphics().clear();
            newRouteContainerLayer.doPrepare();
            EPDShip.getMainFrame().getTopPanel().getNewRouteBtn()
                    .setSelected(false);
            EPDShip.getMainFrame().getEeINSMenuBar().getNewRoute()
                    .setSelected(false);

            if (mode == 1) {
                System.out.println("Setting nav mouse mode");
                mouseDelegator.setActive(mapNavMouseMode);
                EPDShip.getMainFrame().getTopPanel().getNavigationMouseMode()
                        .setSelected(true);
                EPDShip.getMainFrame().getTopPanel().getDragMouseMode()
                        .setSelected(false);
            }
            if (mode == 2) {
                mouseDelegator.setActive(dragMouseMode);
                EPDShip.getMainFrame().getTopPanel().getNavigationMouseMode()
                        .setSelected(false);
                EPDShip.getMainFrame().getTopPanel().getDragMouseMode()
                        .setSelected(true);
                System.out.println("Setting drag mouse mode");
            }
        }

    }

    // public void editMode(boolean enable) {
    // if (enable) {
    // mouseDelegator.setActive(routeEditMouseMode);
    // routeEditLayer.setVisible(true);
    // routeEditLayer.setEnabled(true);
    // newRouteContainerLayer.setVisible(true);
    // } else {
    // mouseDelegator.setActive(mapNavMouseMode);
    // routeEditLayer.setVisible(false);
    // routeEditLayer.doPrepare();
    // newRouteContainerLayer.setVisible(false);
    // newRouteContainerLayer.getWaypoints().clear();
    // newRouteContainerLayer.getRouteGraphics().clear();
    // newRouteContainerLayer.doPrepare();
    // EPDShip.getMainFrame().getTopPanel().getNewRouteBtn().setSelected(false);
    // EPDShip.getMainFrame().getEeINSMenuBar().getNewRoute().setSelected(false);
    // }
    // }

    public void autoFollow() {
        // Do auto follow
        if (!EPDShip.getSettings().getNavSettings().isAutoFollow()) {
            return;
        }

        // Only do auto follow if not bad position
        if (gpsData.isBadPosition()) {
            return;
        }

        boolean lookahead = EPDShip.getSettings().getNavSettings()
                .isLookAhead();

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
            if (gpsData.getSog() < lookAheadMinSpd) {
                factor = 0;
            } else if (gpsData.getSog() < lookAheadMaxSpd) {
                factor = gpsData.getSog() / lookAheadMaxSpd;
            } else {
                factor = 1.0;
            }

            double phiX = Math.cos(Math.toRadians(gpsData.getCog()) - 3
                    * Math.PI / 2);
            double phiY = Math.sin(Math.toRadians(gpsData.getCog()) - 3
                    * Math.PI / 2);

            double fx = factor * phiX;
            double fy = factor * phiY;

            desiredX = centerX + (centerX - lookAheadBorder) * fx;
            desiredY = centerY + (centerY - lookAheadBorder) * fy;

        }

        // Get projected x,y of current position
        Point2D shipXY = map.getProjection().forward(
                gpsData.getPosition().getLatitude(),
                gpsData.getPosition().getLongitude());

        // Calculate how many percent the position is off for x and y
        double pctOffX = Math.abs(desiredX - shipXY.getX()) / map.getWidth()
                * 100.0;
        double pctOffY = Math.abs(desiredY - shipXY.getY()) / map.getHeight()
                * 100.0;

        // LOG.info("pctOffX: " + pctOffX + " pctOffY: " + pctOffY);

        int tollerated = EPDShip.getSettings().getNavSettings()
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
            map.setCenter((float) gpsData.getPosition().getLatitude(),
                    (float) gpsData.getPosition().getLongitude());
        }

    }

    /**
     * Receive GPS update
     */
    @Override
    public void gpsDataUpdate(GpsData gpsData) {
        this.gpsData = gpsData;
        autoFollow();
    }

    /**
     * Given a set of points scale and center so that all points are contained
     * in the view
     * 
     * @param waypoints
     */
    public void zoomTo(List<Position> waypoints) {
        if (waypoints.size() == 0) {
            return;
        }

        // Disable auto follow
        EPDShip.getSettings().getNavSettings().setAutoFollow(false);
        topPanel.updateButtons();

        if (waypoints.size() == 1) {
            map.setCenter(waypoints.get(0).getLatitude(), waypoints.get(0)
                    .getLongitude());
            return;
        }

        // Find bounding box
        double maxLat = -91;
        double minLat = 91;
        double maxLon = -181;
        double minLon = 181;
        for (Position pos : waypoints) {
            if (pos.getLatitude() > maxLat) {
                maxLat = pos.getLatitude();
            }
            if (pos.getLatitude() < minLat) {
                minLat = pos.getLatitude();
            }
            if (pos.getLongitude() > maxLon) {
                maxLon = pos.getLongitude();
            }
            if (pos.getLongitude() < minLon) {
                minLon = pos.getLongitude();
            }
        }

        double centerLat = (maxLat + minLat) / 2.0;
        double centerLon = (maxLon + minLon) / 2.0;
        map.setCenter(centerLat, centerLon);

        // LatLonPoint minlatlon = new LatLonPoint.Double(maxLat, minLon); //
        // upper left corner
        // LatLonPoint maxlatlon = new LatLonPoint.Double(minLat, maxLon); //
        // lower right corner
        //
        // Point2D pixelminlatlon = map.getProjection().forward(minlatlon);
        // Point2D pixelmaxlatlon = map.getProjection().forward(maxlatlon);
        //
        // float newScale = ProjMath.getScaleFromProjected(pixelminlatlon,
        // pixelmaxlatlon, map.getProjection());

        /*
         * System.out.println("Scale: " + newScale + "\n");
         * System.out.println("geomin: " + minlatlon + "\n");
         * System.out.println("geomax: " + maxlatlon + "\n");
         * System.out.println("pixelmin: " + pixelminlatlon + "\n");
         * System.out.println("pixelmax: " + pixelmaxlatlon);
         */

        // map.setScale(newScale*5);
    }

    /**
     * Called when projection has been changed by user
     */
    public void manualProjChange() {
        topPanel.disableAutoFollow();
    }

    public MouseDelegator getMouseDelegator() {
        return mouseDelegator;
    }

    private void createPluginLayers(Properties props) {
        String layersValue = props.getProperty("eeins.plugin_layers");
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

    public int getMaxScale() {
        return maxScale;
    }

    public void setNogoMode(boolean value) {
        nogoMode = value;
    }

    public boolean getNogoMode() {
        return nogoMode;
    }

    public void setNogoDialog(NogoDialog dialog) {
        this.nogoDialog = dialog;
    }

    public NogoDialog getNogoDialog() {
        return nogoDialog;
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof TopPanel) {
            topPanel = (TopPanel) obj;
            // Maybe no S52 layer
            if (encLayer == null) {
                topPanel.setEncDisabled();
            }
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
     * 
     * @param direction
     *            1 == Up 2 == Down 3 == Left 4 == Right
     * 
     *            Moving by 100 units in each direction Map center is [745, 445]
     */
    public void pan(int direction) {
        Point point = null;
        Projection projection = map.getProjection();

        int width = projection.getWidth();
        int height = projection.getHeight();

        switch (direction) {
        case 1:
            point = new Point(width / 2, height / 2 - 100);
            break;
        case 2:
            point = new Point(width / 2, height / 2 + 100);
            break;
        case 3:
            point = new Point(width / 2 - 100, height / 2);
            break;
        case 4:
            point = new Point(width / 2 + 100, height / 2);
            break;
        }

        Proj p = (Proj) projection;
        LatLonPoint llp = projection.inverse(point);
        p.setCenter(llp);
        map.setProjection(p);
        manualProjChange();
    }

}
