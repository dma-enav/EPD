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
package dk.dma.epd.common.prototype.layers.wms;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;

/**
 * Layer handling all WMS data and displaying of it
 * 
 * @author David A. Camre (davidcamre@gmail.com)
 * 
 */
public class WMSLayer extends OMGraphicHandlerLayer implements Runnable {
    private static final long serialVersionUID = 1L;
    private OMGraphicList list = new OMGraphicList();
//    private CommonChartPanel chartPanel;
    //private WMSInfoPanel wmsInfoPanel;
    volatile boolean shouldRun = true;
    private StreamingTiledWmsService wmsService;
    private int height = -1;
    private int width = -1;
    private float lastScale = -1F;
    private Logger LOG;

    private CopyOnWriteArrayList<OMGraphic> testList = new CopyOnWriteArrayList<>();

    /**
     * Constructor that starts the WMS layer in a seperate thread
     */
    public WMSLayer(String query) {
        LOG = LoggerFactory.getLogger(WMSLayer.class);
        LOG.debug("WMS Layer inititated");
        wmsService = new StreamingTiledWmsService(query, 4);
        new Thread(this).start();

    }
    
    public WMSLayer(String query,ConcurrentHashMap<String, OMGraphicList> sharedCache) {
        LOG = LoggerFactory.getLogger(WMSLayer.class);
        wmsService = new StreamingTiledWmsService(query, 4, sharedCache);
        new Thread(this).start();
    }

    public AbstractWMSService getWmsService() {
        return wmsService;
    }

    /**
     * Draw the WMS onto the map
     * 
     * @param list
     *            of elements to be drawn
     */
    public void drawWMS(OMGraphicList tiles) {
        this.testList.addAllAbsent(tiles);
        list.clear();
        list.addAll(testList);
        list.addAll(tiles);
        doPrepare();            
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof CommonChartPanel) {
//            this.chartPanel = (CommonChartPanel) obj;
            // chartPanel.getMapHandler().addPropertyChangeListener("WMS", pcl)

            // this.chartPanel.getMap().addProjectionListener(this);
        }

    }

    @Override
    public OMGraphicList prepare() {
        list.project(getProjection());
        return list;
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        Projection proj = e.getProjection().makeClone();

        if (proj.getScale() != lastScale) {
            clearWMS();
            lastScale = proj.getScale();

        }

        width = proj.getWidth();
        height = proj.getHeight();
        if (width > 0 && height > 0 && proj.getScale() <= 3428460) {
            wmsService.queue(proj);

        } else {
            this.setVisible(false);
        }

        // OMGraphicsHandlerLayer has its own thing
        super.projectionChanged(e);

    }

    public void clearWMS() {
        this.testList.clear();
        this.drawWMS(new OMGraphicList());
    }

    @Override
    public void run() {
        while (shouldRun) {
            try {
                Thread.sleep(250);
                final Projection proj = this.getProjection();
                width = proj.getWidth();
                height = proj.getHeight();

                if (width > 0 && height > 0 && proj.getScale() <= 3428460) {
                    OMGraphicList result = wmsService.getWmsList(proj);
                    drawWMS(result);
                }

            } catch (InterruptedException | NullPointerException e) {
                //LOG.debug(e.getMessage());
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
    
//    @Override
//    public void setVisible(boolean visible){
//        System.out.println("Set visible called on WMS Layer " + visible);
//        super.setVisible(visible);
//    }

}
