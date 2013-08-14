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


import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
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
    private CommonChartPanel chartPanel;   
    private WMSInfoPanel wmsInfoPanel;
    //simple flag set on projectionChanged to register for new WMS
    volatile boolean shouldRun = true;
    private AbstractWMSService wmsService;
    private Double upperLeftLon = 0.0;
    private Double upperLeftLat = 0.0;
    private Double lowerRightLon = 0.0;
    private Double lowerRightLat = 0.0;
    private int height = -1;
    private int width = -1;
    
    
    private static final Logger LOG = LoggerFactory
            .getLogger(WMSLayer.class);


    /**
     * Constructor that starts the WMS layer in a seperate thread
     */
    public WMSLayer(String query) {
        
        wmsService = new StreamingTiledWmsService(query, 4);
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
    public void drawWMS(OMGraphicList list) {
        this.list.clear();
        this.list.add(list);
        
        
        // wmsInfoPanel.setVisible(false);

        if (wmsService.isWmsImage() && this.isVisible()) {
            chartPanel.getBgLayer().setVisible(false);
        } else {
            chartPanel.getBgLayer().setVisible(true);
        }

        doPrepare();
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof CommonChartPanel) {
            this.chartPanel = (CommonChartPanel) obj;
            // chartPanel.getMapHandler().addPropertyChangeListener("WMS", pcl)
            
            //this.chartPanel.getMap().addProjectionListener(this);
        }

    }

    @Override
    public OMGraphicList prepare() {
        list.project(getProjection());
        return list;
    }
    
    @Override
    public void projectionChanged(ProjectionEvent e) {
        //OMGraphicsHandlerLayer has its own thing
        
        super.projectionChanged(e);
        
        clearWMS();
        
        Projection proj = e.getProjection();

        width = proj.getWidth();
        height = proj.getHeight();
        if (width > 0 && height > 0 && proj.getScale() <= 3428460) {
            
            upperLeftLon = proj.getUpperLeft().getX();
            upperLeftLat = proj.getUpperLeft().getY();
            lowerRightLon = proj.getLowerRight().getX();
            lowerRightLat = proj.getLowerRight().getY();
        

            wmsService.setZoomLevel(proj.getScale());
            wmsService.setWMSPosition(upperLeftLon, upperLeftLat, upperLeftLon, upperLeftLat, lowerRightLon,
                    lowerRightLat, width, height);
            

        } else {
            this.setVisible(false);
        }
    }
    
    public void clearWMS() {
        this.drawWMS(new OMGraphicList());
    }

    @Override
    public void run() {
        while (shouldRun) {
            try {
                Thread.sleep(200); 
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            
            width = getProjection().getWidth();
            height = getProjection().getHeight();
            if (width > 0 && height > 0 && getProjection().getScale() <= 3428460) {
                this.setVisible(true);
                OMGraphicList result = wmsService.getWmsList();
                
                drawWMS(result);                
            }
        }
    }

    /**
     * Stop the thread
     */
    public void stop() {
        shouldRun = false;
    }

}
