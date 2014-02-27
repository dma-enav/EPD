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
package dk.dma.epd.shore.event;

import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.bbn.openmap.BufferedLayerMapBean;

import dk.dma.epd.common.prototype.event.mouse.CommonDragMouseMode;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
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
        
        if (e.getButton() == MouseEvent.BUTTON1) {
            
            glassFrame.setCursor(super.DRAG_DOWN_CURSOR);
            BufferedLayerMapBean bean = (BufferedLayerMapBean) e.getSource();
            
            super.chartPanel = (ChartPanelCommon) bean.getParent();
            super.calledFromShore = true; // Ensure that Shore drag is called.
        }
    }

    /**
     * This method is called when the mouse is released. It will get
     * the coordinates for the current view (which is dragged to) and
     * set center of the map to that location.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
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
