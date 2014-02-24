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

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import dk.dma.epd.common.prototype.event.mouse.CommonNavigationMouseMode;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * The mouse mode used in navigation mode - click zoom mode
 */
public class NavigationMouseMode extends CommonNavigationMouseMode {
    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Navigation".
     */
    public static final transient String MODEID = "NAVIGATION";

    private ClickTimer clickTimer;
    protected Point point1, point2;

    private JPanel glassFrame;

    /**
     * Construct a NavMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public NavigationMouseMode(ChartPanel chartPanel) {
        super(chartPanel, EPDShore.getInstance().getSettings().getMapSettings().getMaxScale(), MODEID);
        clickTimer = ClickTimer.getClickTimer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object someObj) {
        if (someObj instanceof JMapFrame) {
            glassFrame = ((JMapFrame) someObj).getGlassPanel();
        }

        super.findAndInit(someObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        glassFrame.setCursor(super.NAV_CURSOR);
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
    public void mousePressed(MouseEvent e) {
        clickTimer.setInterval(500);
        clickTimer.startTime();
        super.mousePressed(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
    }
}
