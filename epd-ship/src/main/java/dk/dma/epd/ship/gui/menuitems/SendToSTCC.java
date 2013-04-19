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

import java.awt.Point;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.monalisa.MonaLisaSTCCDialog;
import dk.dma.epd.ship.layers.route.RouteLayer;

public class SendToSTCC extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    private Route route;
    private MonaLisaSTCCDialog monaLisaSTCCDialog;
    private Point windowLocation;
    private RouteLayer routeLayer;

    public SendToSTCC(String text) {
        super();
        this.setText(text);
    }

    @Override
    public void doAction() {


        if (monaLisaSTCCDialog.isActive()){
            monaLisaSTCCDialog.setVisible(true);
        }else{
            
            //Sending route
            EPDShip.getEnavServiceHandler().sendMonaLisaRouteRequest(
                    route.getFullRouteData(), "mmsi://219230000", "Route Approval Requested");
            
            routeLayer.setRouteAnimated(route);
            
//            EPDShip.getMainFrame().getChartPanel().getr

            monaLisaSTCCDialog.setLocation(windowLocation);
            monaLisaSTCCDialog.setLocationRelativeTo(EPDShip.getMainFrame());
            monaLisaSTCCDialog.setVisible(true);
            monaLisaSTCCDialog.setRouteName(route.getName());
            monaLisaSTCCDialog.setRouteLayer(routeLayer);
        }
        
        // String mmsiStr = "mmsi://" + mmsi;


    }

    
    public void setRouteLayer(RouteLayer routeLayer) {
        this.routeLayer = routeLayer;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setSTCCDialog(MonaLisaSTCCDialog monaLisaSTCCDialog) {
        this.monaLisaSTCCDialog = monaLisaSTCCDialog;
    }

    public void setRouteLocation(Point windowLocation) {
        this.windowLocation = windowLocation;
    }
}
