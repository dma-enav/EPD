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
package dk.dma.epd.shore.layers.wms;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;

/**
 * Layer handling all WMS data and displaying of it
 *
 * @author David A. Camre (davidcamre@gmail.com)
 *
 */
public class WMSLayer extends OMGraphicHandlerLayer implements Runnable {
    private static final long serialVersionUID = 1L;
    private OMGraphicList list = new OMGraphicList();
    private ChartPanel chartPanel;
    private WMSInfoPanel wmsInfoPanel;
    private JMapFrame jMapFrame;
    private MainFrame mainFrame;

    volatile boolean shouldRun = true;
    private WMSService wmsService;
    private Double upperLeftLon = 0.0;
    private Double upperLeftLat = 0.0;
    private Double lowerRightLon = 0.0;
    private Double lowerRightLat = 0.0;
    private int height = -1;
    private int width = -1;

    /**
     * Constructor that starts the WMS layer in a seperate thread
     */
    public WMSLayer() {
        wmsService = new WMSService();
        new Thread(this).start();

    }

    public WMSService getWmsService() {
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
            jMapFrame.getChartPanel().getBgLayer().setVisible(false);
        } else {
            jMapFrame.getChartPanel().getBgLayer().setVisible(true);
        }

        doPrepare();
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
            // chartPanel.getMapHandler().addPropertyChangeListener("WMS", pcl)
        }
        if (obj instanceof JMapFrame) {
            jMapFrame = (JMapFrame) obj;
            wmsInfoPanel = new WMSInfoPanel();
            jMapFrame.getGlassPanel().add(wmsInfoPanel);
            wmsInfoPanel.setPos(20, 30);
        }
        if (obj instanceof MainFrame){
            mainFrame = (MainFrame) obj;
        }

    }

    @Override
    public synchronized OMGraphicList prepare() {
        list.project(getProjection());
        return list;
    }

    @Override
    public void run() {
        while (shouldRun) {
            EPDShore.sleep(1000);

//            if (this.isVisible() && jMapFrame.getWidth() > 0 && jMapFrame.getWidth() > 0 && chartPanel.getMap().getScale() <= 3428460) {
            if (mainFrame.isWmsLayerEnabled() && jMapFrame.getWidth() > 0 && jMapFrame.getWidth() > 0 && chartPanel.getMap().getScale() <= 3428460) {
                setVisible(true);
                chartPanel.getBgLayer().setVisible(false);

                // if (height != chartPanel.getMap().getHeight() || width !=
                // chartPanel.getMap().getWidth()){
                // wmsInfoPanel.setPos( (jMapFrame.getChartPanel().getHeight() /
                // 2) -50, (jMapFrame.getChartPanel().getWidth() / 2) - 50);
                // }

                if (upperLeftLon != chartPanel.getMap().getProjection().getUpperLeft().getX()
                        || upperLeftLat != chartPanel.getMap().getProjection().getUpperLeft().getY()
                        || lowerRightLon != chartPanel.getMap().getProjection().getLowerRight().getX()
                        || lowerRightLat != chartPanel.getMap().getProjection().getLowerRight().getY()
                        || width != chartPanel.getMap().getWidth() || height != chartPanel.getMap().getHeight()) {

                    // System.out.println("New request");
                    // wmsInfoPanel.showText("Loading");
                    // System.out.println(jMapFrame.getHeight());
                    // System.out.println(jMapFrame.getWidth());

                    wmsInfoPanel.displayLoadingImage();
                    // wmsInfoPanel.setVisible(true);

                    jMapFrame.getGlassPanel().setVisible(true);

                    upperLeftLon = chartPanel.getMap().getProjection().getUpperLeft().getX();
                    upperLeftLat = chartPanel.getMap().getProjection().getUpperLeft().getY();
                    lowerRightLon = chartPanel.getMap().getProjection().getLowerRight().getX();
                    lowerRightLat = chartPanel.getMap().getProjection().getLowerRight().getY();

                    width = chartPanel.getMap().getWidth();
                    height = chartPanel.getMap().getHeight();

//                    System.out.println(height);
//                    System.out.println(width);

                    // System.out.println(chartPanel.getMap().getProjection().forward(chartPanel.getMap().getProjection().getLowerRight()));
                    // System.out.println(upperLeftLon);
                    // System.out.println(upperLeftLat);
                    // System.out.println(lowerRightLon);
                    // System.out.println(lowerRightLat);

                    wmsService.setZoomLevel(chartPanel.getMap().getScale());
                    wmsService.setWMSPosition(chartPanel.getMap().getProjection().getCenter().getX(), chartPanel
                            .getMap().getProjection().getCenter().getY(), upperLeftLon, upperLeftLat, lowerRightLon,
                            lowerRightLat, width, height);

                    drawWMS(wmsService.getWmsList());
                    wmsInfoPanel.setVisible(false);
                }
            }else{
                this.setVisible(false);
                chartPanel.getBgLayer().setVisible(true);
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
