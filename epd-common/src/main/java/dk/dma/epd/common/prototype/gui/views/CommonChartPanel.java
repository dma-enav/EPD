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

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.gui.util.SimpleOffScreenMapRenderer;
import dk.dma.epd.common.prototype.layers.wms.WMSLayer;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;

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
//    protected Layer encDragLayer;
    protected Layer bgLayer;
    protected PntData pntData;
    protected MouseDelegator mouseDelegator;
    protected WMSLayer wmsLayer;
    protected WMSLayer wmsDragLayer;
    private HistoryListener historyListener;
    
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
    
    public HistoryListener getHistoryListener() {
        return historyListener;
    }
    
    public void setHistoryListener(HistoryListener historyListener2) {
        this.historyListener = historyListener2;
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

    /**
     * Changes the current center of the map to a new position.
     * @param position Position to change to center.
     */
    public void goToPosition(Position position) {
        this.getMap().setCenter(position.getLatitude(), position.getLongitude());
    }
}
