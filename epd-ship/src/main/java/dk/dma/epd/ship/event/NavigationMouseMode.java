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

    public NavigationMouseMode(ChartPanel chartPanel) {
        super(chartPanel, EPDShip.getInstance().getSettings().getMapSettings().getMaxScale(), MODE_ID);
        clickTimer = ClickTimer.getClickTimer();
        
        this.setModeCursor(super.NAV_CURSOR);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        clickTimer.setInterval(500);
        clickTimer.startTime();
        super.mousePressed(e);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
       
        super.mouseClicked(e);
        EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().setShouldSave(true);
        EPDShip.getInstance().getMainFrame().getChartPanel().getProjectChangeListener().saveToHistoryBeforeMoving();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        
        super.mouseReleased(e);
        EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().setShouldSave(true);
        EPDShip.getInstance().getMainFrame().getChartPanel().getHistoryListener().saveToHistoryBeforeMoving();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }
}
