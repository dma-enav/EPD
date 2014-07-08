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
package dk.dma.epd.common.prototype.layers.wms;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.event.WMSEvent;
import dk.dma.epd.common.prototype.event.WMSEventListener;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;

/**
 * Layer handling all WMS data and displaying of it
 * 
 * @author David A. Camre (davidcamre@gmail.com)
 * 
 */
public class WMSLayer extends EPDLayerCommon implements Runnable, WMSEventListener {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(WMSLayer.class);
    
    private static final int PROJ_SCALE_THRESHOLD = 3428460;
    
    volatile boolean shouldRun = true;
    private StreamingTiledWmsService wmsService;
    private int height = -1;
    private int width = -1;
    private float lastScale = -1F;

    private CopyOnWriteArrayList<OMGraphic> internalCache = new CopyOnWriteArrayList<>();

    /**
     * Constructor that starts the WMS layer in a separate thread
     * @param query the WMS query
     */
    public WMSLayer(String query) {
        LOG.debug("WMS Layer inititated");
        wmsService = new StreamingTiledWmsService(query, 4);
        wmsService.addWMSEventListener(this);
        new Thread(this).start();

    }
    
    /**
     * Constructor that starts the WMS layer in a separate thread
     * @param query the WMS query
     * @param sharedCache the shared cache to use
     */
    public WMSLayer(String query,ConcurrentHashMap<String, OMGraphicList> sharedCache) {
        wmsService = new StreamingTiledWmsService(query, 4, sharedCache);
        wmsService.addWMSEventListener(this);
        new Thread(this).start();
    }

    /**
     * Returns a reference to the WMS service
     * @return a reference to the WMS service
     */
    public AbstractWMSService getWmsService() {
        return wmsService;
    }

    /**
     * Draw the WMS onto the map
     * 
     * @param graphics
     *            of elements to be drawn
     */
    public void drawWMS(OMGraphicList tiles) {
        this.internalCache.addAllAbsent(tiles);
        graphics.clear();
        graphics.addAll(internalCache);
        graphics.addAll(tiles);
        doPrepare();            
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent e) {
        if (e.getProjection() != null) {
            Projection proj = e.getProjection().makeClone();
    
            if (proj.getScale() != lastScale) {
                clearWMS();
                lastScale = proj.getScale();
    
            }
    
            width = proj.getWidth();
            height = proj.getHeight();
            if (width > 0 && height > 0 && proj.getScale() <= PROJ_SCALE_THRESHOLD) {
                wmsService.queue(proj);
            } else {
                this.setVisible(false);
            }
        }

        // OMGraphicsHandlerLayer has its own thing
        super.projectionChanged(e);

    }

    /**
     * Clears the WMS layer
     */
    public void clearWMS() {
        // Aggressively flush the buffered images 
        for (OMGraphic g : internalCache) {
            if (g instanceof CenterRaster) {
                CenterRaster cr = (CenterRaster)g;
                if (cr.getImage() instanceof BufferedImage) {
                    ((BufferedImage)cr.getImage()).flush();
                }
            }
        }
        this.internalCache.clear();
        this.drawWMS(new OMGraphicList());
    }

    /**
     * Main thread run method
     * TODO: remove this since we now use WMSEvent and AbstractWMSService is observable.
     */
    @Override
    public void run() {
        while (shouldRun) {
            try {
                Thread.sleep(25000);
                final Projection proj = this.getProjection();
                if (proj != null) {
                    width = proj.getWidth();
                    height = proj.getHeight();
    
                    if (width > 0 && height > 0 && proj.getScale() <= PROJ_SCALE_THRESHOLD) {
                        OMGraphicList result = wmsService.getWmsList(proj);
                        drawWMS(result);
                    }
                }

            } catch (InterruptedException | NullPointerException e) {
                // do nothing
            }
        }
    }

    /**
     * Stop the thread
     */
    public void stop() {
        shouldRun = false;
    }

    /**
     * Called by the WMS service upon a WMS change
     * @param evt the WMS event
     */
    @Override
    public void changeEventReceived(WMSEvent evt) {
        final Projection proj = this.getProjection();
        if (proj != null && width > 0 && height > 0 && proj.getScale() <= PROJ_SCALE_THRESHOLD) {
            OMGraphicList result = wmsService.getWmsList(proj);
            drawWMS(result);
        }
    }
}
