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

import dk.dma.epd.common.prototype.event.mouse.CommonNavigationMouseMode;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ChartPanel;

/**
 * The mouse mode used in navigation mode
 */
public class NavigationMouseMode extends CommonNavigationMouseMode {
    
    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Navigation".
     */
    public static final transient String MODE_ID = "Navigation";

    private ClickTimer clickTimer;

    /**
     * Creates the NavigatopnMouseMode for Ship.
     * @param chartPanel
     */
    public NavigationMouseMode(ChartPanel chartPanel) {
        super(chartPanel, MODE_ID);
        this.clickTimer = ClickTimer.getClickTimer();
        
        this.setModeCursor(super.NAV_CURSOR);
    }
    
    /**
     * Handles a mouse pressed event. This will save the current position
     * of the mouse and reset the second point.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        clickTimer.setInterval(500);
        clickTimer.startTime();
        super.mousePressed(e);
    }
    
    /**
     * If the mouse is pressed twice right after each other, this mouse
     * event handler method will update the location on the map by the
     * position of the mouse. 
     * If the control button is pushed down when this method is called, 
     * a new scale value will be calculated so that a zoom to the new 
     * position will be done too. If the control and shift button are
     * both down at when called a zoom out from the point will be done.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
       
        EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().setShouldSave(true);
        EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().saveToHistoryBeforeMoving();
        super.mouseClicked(e);
    }

    /**
     * When the mouse is released, this event will check if the control
     * button was held down, when the mouse was released. If it was, it 
     * will find the best rectangle in ratio to fit the selected area
     * into. If the control button was not held down, the method will
     * zoom to the selected rectangle. 
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().setShouldSave(true);
        EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().saveToHistoryBeforeMoving();
        super.mouseReleased(e);
    }
}
