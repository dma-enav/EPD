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
package dk.dma.epd.common.prototype.gui.views;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.layer.shape.MultiShapeLayer;
import com.bbn.openmap.proj.Proj;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.event.mouse.CommonDistanceCircleMouseMode;
import dk.dma.epd.common.prototype.event.mouse.CommonDragMouseMode;
import dk.dma.epd.common.prototype.event.mouse.CommonNavigationMouseMode;
import dk.dma.epd.common.prototype.event.mouse.CommonRouteEditMouseMode;
import dk.dma.epd.common.prototype.gui.util.DraggableLayerMapBean;
import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteLayerCommon;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteTCPALayer;
import dk.dma.epd.common.prototype.layers.msi.MsiLayerCommon;
import dk.dma.epd.common.prototype.layers.route.RouteLayerCommon;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.layers.routeedit.RouteEditLayerCommon;
import dk.dma.epd.common.prototype.layers.util.LayerVisibilityAdapter;
import dk.dma.epd.common.prototype.layers.wms.WMSLayer;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings.ENCColorScheme;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.observers.ENCLayerCommonSettingsListener;

/**
 * The panel with chart. Initializes all layers to be shown on the map.
 * 
 * @author Jens Tuxen (mail@jenstuxen.com)
 */
public abstract class ChartPanelCommon extends OMComponentPanel implements ENCLayerCommonSettingsListener {
    
    private static final long serialVersionUID = 1L;
    
    // Mouse modes
    protected String mouseMode;
    protected MouseDelegator mouseDelegator;
    protected CommonNavigationMouseMode mapNavMouseMode;
    protected CommonDragMouseMode dragMouseMode;
    protected CommonRouteEditMouseMode routeEditMouseMode;
    protected CommonDistanceCircleMouseMode rangeCirclesMouseMode;
    
    // Layers and handlers
    protected MapHandler mapHandler;
    protected LayerHandler layerHandler;
    protected DraggableLayerMapBean map;
    protected OMGraphicHandlerLayer encLayer;
    protected MultiShapeLayer bgLayer;
    protected WMSLayer wmsLayer;
    protected AisLayerCommon<?> aisLayer;
    protected RouteLayerCommon routeLayer;
    protected RouteEditLayerCommon routeEditLayer;
    protected NewRouteContainerLayer newRouteContainerLayer;
    protected MsiLayerCommon msiLayer;
    protected IntendedRouteLayerCommon intendedRouteLayer;
    protected IntendedRouteTCPALayer intendedRouteTCPALayer;
    
    protected LayerVisibilityAdapter encVisibilityAdapter = new LayerVisibilityAdapter();
    protected HistoryListener historyListener;
    
    private MapCommonSettings<?> mapSettings;
    
    /**
     * Constructor
     */
    protected ChartPanelCommon(MapCommonSettings<?> mapSettings) {
        this.mapSettings = Objects.requireNonNull(mapSettings);
//        maxScale = EPD.getInstance().getSettings().getMapSettings().getMaxScale();
    }
    
    public MapCommonSettings<?> getMapSettings() {
        return mapSettings;
    }
    
    /**
     * Save chart settings for workspace
     */
    public void saveSettings() {
//        MapSettings mapSettings = EPD.getInstance().getSettings().getMapSettings();
        mapSettings.setCenter((LatLonPoint) map.getCenter());
        mapSettings.setInitialMapScale(map.getScale());
    }

    /**
     * Force an update in the AIS layer
     */
    public void forceAisLayerUpdate() {
        if (aisLayer != null) {
            aisLayer.forceLayerUpdate();
        }
    }
    
    /**
     * Returns the current mouse mode
     * @return the current mouse mode
     */
    public String getMouseMode() {
        return mouseMode;
    }
    
    /**
     * Change the mouse mode.
     * 
     * @param mode The mode ID of the mouse mode to swap to (e.g.
     *            DistanceCircleMouseMode.MODE_ID).
     */
    public abstract void setMouseMode(String modeID);


    /*******************************/
    /** Zooming and panning       **/
    /*******************************/
    
    /**
     * Changes the current center of the map to a new position.
     * @param position Position to change to center.
     */
    public void goToPosition(Position position) {
        getMap().setCenter(position.getLatitude(), position.getLongitude());
        forceAisLayerUpdate();
    }
    
    /**
     * Zooms to the given list of way points
     * 
     * @param waypoints the list of way points to zoom to
     */
    public void zoomToWaypoints(List<RouteWaypoint> waypoints) { 
        if (waypoints == null || waypoints.size() == 0) {
            return;
        }
        
        List<Position> positions = new ArrayList<>();
        for (RouteWaypoint wp : waypoints) {
            positions.add(wp.getPos());
        }
        zoomTo(positions);
    }
    
    /**
     * Given a set of points scale and center so that all points are contained in the view
     * 
     * @param waypoints
     */
    public void zoomTo(List<Position> waypoints) {
        if (waypoints == null || waypoints.size() == 0) {
            return;
        }

        if (waypoints.size() == 1) {
            map.setCenter(waypoints.get(0).getLatitude(), waypoints.get(0).getLongitude());
            forceAisLayerUpdate();
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
        
        // Compute scale, taking the width/height ratio into account.
        // Also, make sure we do not divide by zero...
        Projection proj = map.getProjection();
        if (proj != null && proj.getWidth() > 0) {
            double mapRatio = (double)proj.getHeight() / (double)proj.getWidth();
            
            // Go to screen x-y to adjust ratio of scale
            Point2D p0 = proj.forward(minLat, minLon);
            Point2D p1 = proj.forward(maxLat, maxLon);
            double routeHeight = p1.getY() - p0.getY();
            double routeWidth = p1.getX() - p0.getX();
            if (Math.abs(routeWidth) > 0.001) { 
                double routeRatio = Math.abs(routeHeight / routeWidth);
                
                if (routeRatio > mapRatio) {
                    float sign = (routeHeight < 0) ? 1 : -1;
                    p1.setLocation(p0.getX() + sign * Math.abs(routeHeight / mapRatio), p1.getY());
                } else {
                    float sign = (routeWidth < 0) ? 1 : -1;
                    p1.setLocation(p1.getX(), p0.getY() + sign * Math.abs(routeWidth * mapRatio));
                }
                
                float scale = ProjMath.getScale(proj.inverse(p0), proj.inverse(p1), proj);
                
                // Restrict to min scale and scale with 10% 
                scale = Math.max((float)mapSettings.getMinMapScale(), scale * 1.1f);
                
                map.setScale(scale);
            }
        }
        
        forceAisLayerUpdate();
    }
    
    /**
     * Change zoom level on map
     * 
     * @param factor
     */
    public void doZoom(float factor) {
        float newScale = map.getScale() * factor;
        if (newScale < mapSettings.getMinMapScale()) {
            newScale = mapSettings.getMinMapScale();
        }
        map.setScale(newScale);
        forceAisLayerUpdate();
    }
    
    /**
     * Pans the map in the given direction
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

        forceAisLayerUpdate();
    }
    
    /*******************************/
    /** Layer visibility          **/
    /*******************************/
    
    /**
     * Sets AIS layer visibility
     * 
     * @param visible the visibility
     */
    public void aisVisible(boolean visible) {
        if (aisLayer != null) {
            aisLayer.getSettings().setVisible(visible);
        }
    }

//    /**
//     * Sets ENC layer visibility
//     * 
//     * @param visible the visibility
//     */
//    public void encVisible(boolean visible) {
//        encVisible(visible, true);
//    }

    // TODO fix this when new setting has be re-introduced in new design
//    /**
//     * Sets ENC layer visibility
//     * 
//     * @param visible the visibility
//     * @param persist persist the change to the settings
//     */
//    public void encVisible(boolean visible, boolean persist) {
//        // Note: After upgrading to OpenMap 5.0.3, there seemed to be a minor problem
//        // with having multiple background layers installed (EPD-186), causing the 
//        // background layer to turn blank after launch.
//        // Hence instead of adding both background layers and toggling the visibility,
//        // the strategy was changed to add and remove the background layers:
//        
//        if (encLayer != null) {
//            if (visible) {
//                encLayer.setVisible(true);
//                if (!EPD.getInstance().getSettings().getMapSettings().isMultipleBackgrounds()) {
//                    mapHandler.remove(bgLayer);
//                    mapHandler.add(encLayer);
//                    encLayer.doPrepare();
//                    encVisibilityAdapter.notifyVisibilityListeners(encLayer);
//                }
//            } else {
//                encLayer.setVisible(false);
//                if (!EPD.getInstance().getSettings().getMapSettings().isMultipleBackgrounds()) {
//                    mapHandler.remove(encLayer);
//                    mapHandler.add(bgLayer);
//                    bgLayer.doPrepare();
//                    encVisibilityAdapter.notifyVisibilityListeners(encLayer);
//                }
//            }
//            if (persist) {
//                EPD.getInstance().getSettings().getMapSettings().setEncVisible(visible);
//            }
//        } else {
//            bgLayer.setVisible(true);
//        }
//    }

    /**
     * Returns if the ENC layer is visible or not
     * @return if the ENC layer is visible or not
     */
    public boolean isEncVisible() {
//        return encLayer != null && encLayer.isVisible() && mapHandler.contains(encLayer);
        return getEncLayerSettings() != null && getEncLayerSettings().isEncInUse() && getEncLayerSettings().isVisible();
    }
    
    /**
     * Sets WMS layer visibility
     * 
     * @param visible the visibility
     */
    public void wmsVisible(boolean visible) {
        if (wmsLayer != null) {
            // Layer observes its settings.
            wmsLayer.getSettings().setVisible(visible);
        }
    }

    /**
     * Sets Intended Route layer visibility
     * 
     * @param visible the visibility
     */
    public void intendedRouteLayerVisible(boolean visible) {
        if (intendedRouteLayer != null) {
            // TODO Layer should observe its settings.
            intendedRouteLayer.getSettings().setVisible(visible);
        }
    }

    
    /*******************************/
    /** Getters and setters       **/
    /*******************************/
    
    public DraggableLayerMapBean getMap() {
        return map;
    }
    
    public MapHandler getMapHandler() {
        return mapHandler;
    }
    
    public MultiShapeLayer getBgLayer() {
        return bgLayer;
    }
    
    public AisLayerCommon<?> getAisLayer() {
        return aisLayer;
    }
    
    public RouteLayerCommon getRouteLayer() {
        return routeLayer;
    }

    public RouteEditLayerCommon getRouteEditLayer() {
        return routeEditLayer;
    }

    public NewRouteContainerLayer getNewRouteContainerLayer() {
        return newRouteContainerLayer;
    }

    public MsiLayerCommon getMsiLayer() {
        return msiLayer;
    }

    public WMSLayer getWmsLayer() {
        return wmsLayer;
    }

    public Layer getEncLayer() {
        return encLayer;
    }
    
    public IntendedRouteLayerCommon getIntendedRouteLayer() {
        return this.intendedRouteLayer;
    }
    
    public HistoryListener getHistoryListener() {
        return historyListener;
    }
    
    public void setHistoryListener(HistoryListener historyListener2) {
        this.historyListener = historyListener2;
    }
    
    public MouseDelegator getMouseDelegator() {
        return mouseDelegator;
    }

    public LayerVisibilityAdapter getEncVisibilityAdapter() {
        return encVisibilityAdapter;
    }
    
    /**
     * Get the settings for the ENC layer of this {@link ChartPanelCommon}.
     * As the ENC layer is a 3rd party dynamically loaded class, the layer itself cannot hold its settings instance.
     * As such, the {@link ChartPanelCommon} is responsible for the the link between the settings instance and the layer
     * (i.e. the {@link ChartPanelCommon} listen for changes to the layer's settings and modifies the layer accordingly).
     * 
     * @return The settings for the ENC layer of this {@link ChartPanelCommon}.
     */
    public abstract ENCLayerCommonSettings<? extends ENCLayerCommonSettingsListener> getEncLayerSettings();

    /*
     * Begin settings listener methods.
     */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        if (source == getEncLayerSettings()) {
            
            // Note: After upgrading to OpenMap 5.0.3, there seemed to be a minor problem
            // with having multiple background layers installed (EPD-186), causing the 
            // background layer to turn blank after launch.
            // Hence instead of adding both background layers and toggling the visibility,
            // the strategy was changed to add and remove the background layers:
            
            if (encLayer != null) {
                if (newValue) {
                    mapHandler.remove(bgLayer);
                    encLayer.setVisible(true);
                    mapHandler.add(encLayer);
                    encLayer.doPrepare();
                    encVisibilityAdapter.notifyVisibilityListeners(encLayer);                
                } else {
                    mapHandler.remove(encLayer);
                    mapHandler.add(bgLayer);
                    encLayer.setVisible(false);
                    bgLayer.doPrepare();
                    encVisibilityAdapter.notifyVisibilityListeners(encLayer);                
                }
            } else {
                bgLayer.setVisible(true);
            }
        }
    }

    @Override
    public void isEncInUseChanged(boolean useEnc) {
        // Not relevant.
    }

    @Override
    public void isS52ShowTextChanged(boolean showText) {
        // Not relevant.
    }

    @Override
    public void isS52ShallowPatternChanged(boolean useShallowPattern) {
        // Not relevant.
    }

    @Override
    public void s52ShallowContourChanged(int shallowContour) {
        // Not relevant.
    }

    @Override
    public void s52SafetyDepthChanged(int safetyDepth) {
        // Not relevant.
    }

    @Override
    public void s52SafetyContourChanged(int safetyContour) {
        // Not relevant.
    }

    @Override
    public void s52DeepContourChanged(int deepContour) {
        // Not relevant.
    }

    @Override
    public void isUseSimplePointSymbolsChanged(boolean useSimplePointSymbols) {
        // Not relevant.
    }

    @Override
    public void isUsePlainAreasChanged(boolean usePlainAreas) {
        // Not relevant.
    }

    @Override
    public void isS52TwoShadesChanged(boolean s52TwoShades) {
        // Not relevant.
    }

    @Override
    public void encColorSchemeChanged(ENCColorScheme newScheme) {
        // Not relevant.
    }
    
    /*
     * End settings listener methods.
     */
}
