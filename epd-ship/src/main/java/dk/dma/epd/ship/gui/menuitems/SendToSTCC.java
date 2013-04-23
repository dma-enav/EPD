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
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.monalisa.MonaLisaSTCCDialog;
import dk.dma.epd.ship.layers.route.RouteLayer;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.route.RouteManager;

public class SendToSTCC extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    private Route route;
    private MonaLisaSTCCDialog monaLisaSTCCDialog;
    private Point windowLocation;
    private RouteLayer routeLayer;
    private VoyageLayer voyageLayer;
    private RouteManager routeManager;
    private long ownMMSI;

    public SendToSTCC(String text) {
        super();
        this.setText(text);
    }

    @Override
    public void doAction() {


        if (monaLisaSTCCDialog.isActive()){
            monaLisaSTCCDialog.setVisible(true);
        }else{
            
            // Initiate Mona Lisa Route Exchange
            voyageLayer.startRouteNegotiation(route.copy());
            route.setVisible(false);
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
            
            
            //Sending route
            EPDShip.getEnavServiceHandler().sendMonaLisaRouteRequest(
                    route.getFullRouteData(), "mmsi://" + ownMMSI, "Route Approval Requested");


            monaLisaSTCCDialog.setLocation(windowLocation);
            monaLisaSTCCDialog.setLocationRelativeTo(EPDShip.getMainFrame());
            monaLisaSTCCDialog.setVisible(true);
            monaLisaSTCCDialog.setRouteName(route.getName());
            monaLisaSTCCDialog.setRouteLayer(routeLayer);
        }
        
        // String mmsiStr = "mmsi://" + mmsi;


    }
    
    public void setOwnMMSI(long mmsi){
        this.ownMMSI = mmsi;
    }

    
    public void setVoyageLayer(VoyageLayer voyageLayer){
        this.voyageLayer = voyageLayer;
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
    
    public void setRouteManager(RouteManager routeManager){
        this.routeManager = routeManager;
    }
}
