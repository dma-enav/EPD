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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.gui.ais.SartDetailsDialog;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;

/**
 * Menu item that displays the sar target details
 */
public class SarTargetDetails extends JMenuItem implements IMapMenuAction {
    private static final long serialVersionUID = 1L;
    
    private JFrame mainFrame;
    private PntHandler gpsHandler;
    private SarTarget sarTarget;

    /**
     * Constructor
     * @param text menu item text
     */
    public SarTargetDetails(String text) {
        super();
        this.setText(text);
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
         new SartDetailsDialog(mainFrame, sarTarget, gpsHandler);
    }
    
    /**
     * Sets the main frame
     * @param mainFrame
     */
    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    /**
     * Sets the GPS handler
     * @param gpsHandler
     */
    public void setGpsHandler(PntHandler gpsHandler) {
        this.gpsHandler = gpsHandler;
    }
    
    /**
     * Sets the sar target
     * @param sarTarget
     */
    public void setSarTarget(SarTarget sarTarget) {
        this.sarTarget = sarTarget;
    }
}
