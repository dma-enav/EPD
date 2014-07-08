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

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.monalisa.MonaLisaSSPAOptionsDialog;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;

public class MonaLisaRouteRequest extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    private int routeIndex;
    private RouteManager routeManager;
    private MainFrame mainFrame;
    private OwnShipHandler ownShipHandler;

    public MonaLisaRouteRequest(String text) {
        super();
        setText(text);
    }

    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }

    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
    
    

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setOwnShipHandler(OwnShipHandler ownShipHandler) {
        this.ownShipHandler = ownShipHandler;
    }

    @Override
    public void doAction() {

        MonaLisaSSPAOptionsDialog monaLisaDialog = new MonaLisaSSPAOptionsDialog(mainFrame, routeManager, ownShipHandler);
        monaLisaDialog.showDialog(routeIndex);
        

    }

}
