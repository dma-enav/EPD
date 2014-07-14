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
package dk.dma.epd.shore.event;

import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.bbn.openmap.BufferedLayerMapBean;

import dk.dma.epd.common.prototype.event.mouse.CommonDragMouseMode;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Mouse mode for dragging
 */
public class DragMouseMode extends CommonDragMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Drag".
     */
    public static final transient String MODEID = "Drag";

    private JPanel glassFrame;

    /**
     * Constructs a new DragMouseMode object for ship. The constructor
     * takes one parameter, which declares which ChartPanel is creating
     * this object. The constructor calls the super constructor with
     * the id of this mouse mode and the chart panel.
     * @param chartPanel
     *          The ChartPanel which this DragMouseMode should
     *          drag upon.
     */
    public DragMouseMode(ChartPanel chartPanel) {
        super(chartPanel, MODEID);
    }

    /**
    * Called when a CoordMouseMode is added to a BeanContext, or when another
    * object is added to the BeanContext after that. The CoordMouseMode looks
    * for an InformationDelegator to use to fire the coordinate updates. If
    * another InforationDelegator is added when one is already set, the later
    * one will replace the current one.
    * 
    * @param someObj an object being added to the BeanContext.
    */
    @Override
    public void findAndInit(Object someObj) {
        if (someObj instanceof JMapFrame) {
            glassFrame = ((JMapFrame) someObj).getGlassPanel();
            glassFrame.setVisible(true);
        }

        super.findAndInit(someObj);
    }

    /**
    * Called when a CoordMouseMode is added to a BeanContext, or when another
    * object is added to the BeanContext after that. The CoordMouseMode looks
    * for an InformationDelegator to use to fire the coordinate updates. If
    * another InforationDelegator is added when one is already set, the later
    * one will replace the current one.
    * 
    * @param someObj an object being added to the BeanContext.
    */
    @Override
    public void mousePressed(MouseEvent e){
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            glassFrame.setCursor(super.DRAG_DOWN_CURSOR);
            BufferedLayerMapBean bean = (BufferedLayerMapBean) e.getSource();
            
            super.chartPanel = (ChartPanelCommon) bean.getParent();
        }
    }

    /**
     * This method is called when the mouse is released. It will get
     * the coordinates for the current view (which is dragged to) and
     * set center of the map to that location.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            EPDShore.getInstance().getMainFrame().getActiveMapWindow().getLayerTogglingPanel().getHistoryListener().saveToHistoryBeforeMoving();
            EPDShore.getInstance().getMainFrame().getActiveMapWindow().getLayerTogglingPanel().getHistoryListener().setShouldSave(true);            
        }
        
        super.mouseReleased(e);
        this.glassFrame.setCursor(super.DRAG_CURSOR);
    }


    /**
     * This method changes the cursor to the drag cursor when it's
     * hovering a map.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        this.glassFrame.setCursor(super.DRAG_CURSOR);
    }
}
