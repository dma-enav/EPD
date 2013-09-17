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
package dk.dma.epd.common.prototype.gui.views;

import com.bbn.openmap.BufferedLayerMapBean;
import com.bbn.openmap.Layer;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.gui.util.SimpleOffScreenMapRenderer;
import dk.dma.epd.common.prototype.layers.wms.WMSLayer;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;

/**
 * The panel with chart. Initializes all layers to be shown on the map.
 * 
 * @author Jens Tuxen (mail@jenstuxen.com)
 */
public abstract class CommonChartPanel extends OMComponentPanel {
    protected MapHandler mapHandler;
    protected MapHandler dragMapHandler;
    protected LayerHandler layerHandler;
    protected BufferedLayerMapBean map;
    protected BufferedLayerMapBean dragMap;
    protected SimpleOffScreenMapRenderer dragMapRenderer;
    protected Layer encLayer;
    protected Layer encDragLayer;
    protected Layer bgLayer;
    protected GpsData gpsData;
    protected MouseDelegator mouseDelegator;
    protected WMSLayer wmsLayer;
    protected WMSLayer wmsDragLayer;
    
    public WMSLayer getWmsDragLayer() {
        return wmsDragLayer;
    }

    public SimpleOffScreenMapRenderer getDragMapRenderer() {
        return dragMapRenderer;
    }

    public void setDragMapRenderer(SimpleOffScreenMapRenderer dragMapRenderer) {
        this.dragMapRenderer = dragMapRenderer;
    }

    public Layer getEncLayer() {
        return encLayer;
    }
    
    public Layer getWmsLayer() {
        return wmsLayer;
    }

    public void setEncLayer(Layer encLayer) {
        this.encLayer = encLayer;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MapBean getMap() {
        return map;
    }
    
    /**
     * Return the mapHandler
     * 
     * @return mapHandler
     */
    public MapHandler getMapHandler() {
        return mapHandler;
    }
    
    public abstract Layer getBgLayer();
    
    /**
     * Initiate dragmap with mapsettings
     */
    protected abstract void initDragMap();
    
    /**
     * @return the dragMap
     */
    public final BufferedLayerMapBean getDragMap() {
        return dragMap;
    }


}
