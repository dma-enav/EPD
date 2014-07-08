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
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;

import com.bbn.openmap.MouseDelegator;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
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
        if (mouseDelegator.getActiveMouseModeID() == NavigationMouseMode.MODE_ID
                || mouseDelegator.getActiveMouseModeID() == DragMouseMode.MODE_ID) {
            // mainFrame.getChartPanel().setMouseMode(0);
            mainFrame.getChartPanel().setMouseMode(RouteEditMouseMode.MODE_ID);
        } else {
            // mainFrame.getChartPanel().setMouseMode(1);
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
