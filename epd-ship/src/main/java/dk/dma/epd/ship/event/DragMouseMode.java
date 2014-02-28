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
package dk.dma.epd.ship.event;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import dk.dma.epd.common.prototype.event.mouse.CommonDragMouseMode;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ChartPanel;

/**
 * Mouse mode for route edit
 */
public class DragMouseMode extends CommonDragMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "DragMouse".
     */
    public static final transient String MODE_ID = "DragMouse";

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
        super(chartPanel, MODE_ID);
    }

    /**
     * This method is called when the mouse is released. It will get
     * the coordinates for the current view (which is dragged to) and
     * set center of the map to that location.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().setShouldSave(true);
        EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().saveToHistoryBeforeMoving();
        super.mouseReleased(e);
        this.chartPanel.getMap().setCursor(super.DRAG_CURSOR);
    }
    
    /**
     * This pressed changes the cursor to drag down cursor, when the
     * user is hovering the map and pressing the left mouse button.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {            
            this.chartPanel.getMap().setCursor(super.DRAG_DOWN_CURSOR);
        }
    }
}
