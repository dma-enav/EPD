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
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;

import com.bbn.openmap.MouseDelegator;

import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.MainFrame;

public class GeneralNewRoute extends JMenuItem implements IMapMenuAction {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private MouseDelegator mouseDelegator;
    private MainFrame mainFrame;

    public GeneralNewRoute(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        if(mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.MODE_ID || mouseDelegator.getActiveMouseModeID() == DragMouseMode.MODE_ID){
//            mainFrame.getChartPanel().setMouseMode(0);
        	mainFrame.getChartPanel().setMouseMode(RouteEditMouseMode.MODE_ID);
        } else {
//            mainFrame.getChartPanel().setMouseMode(1);
        	mainFrame.getChartPanel().setMouseMode(NavigationMouseMode.MODE_ID);
        }
    }

    public void setMouseDelegator(MouseDelegator mouseDelegator) {
        this.mouseDelegator = mouseDelegator;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

}
