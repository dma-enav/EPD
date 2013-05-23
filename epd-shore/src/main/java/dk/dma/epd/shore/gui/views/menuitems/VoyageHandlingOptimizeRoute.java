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
package dk.dma.epd.shore.gui.views.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.views.monalisa.MonaLisaSSPAOptionsDialog;
import dk.dma.epd.shore.layers.voyage.VoyageHandlingLayer;



public class VoyageHandlingOptimizeRoute extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    private VoyageHandlingLayer voyageHandlingLayer;
    private Route route;
    private AisHandler aisHandler;
    private long mmsi;
    

    public VoyageHandlingOptimizeRoute(String text) {
        super();
        setText(text);
    }
    
    
    /**
     * @return the voyageHandlingLayer
     */
    public VoyageHandlingLayer getVoyageHandlingLayer() {
        return voyageHandlingLayer;
    }




    /**
     * @param voyageHandlingLayer the voyageHandlingLayer to set
     */
    public void setVoyageHandlingLayer(VoyageHandlingLayer voyageHandlingLayer) {
        this.voyageHandlingLayer = voyageHandlingLayer;
    }




    /**
     * @return the route
     */
    public Route getRoute() {
        return route;
    }




    /**
     * @param route the route to set
     */
    public void setRoute(Route route) {
        this.route = route;
    }




    /**
     * @return the aisHandler
     */
    public AisHandler getAisHandler() {
        return aisHandler;
    }




    /**
     * @param aisHandler the aisHandler to set
     */
    public void setAisHandler(AisHandler aisHandler) {
        this.aisHandler = aisHandler;
    }




    /**
     * @return the mmsi
     */
    public long getMmsi() {
        return mmsi;
    }




    /**
     * @param mmsi the mmsi to set
     */
    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }




    @Override
    public void doAction() {

//        Route route = routeManager.getRoute(routeIndex);
//        if (routeManager.isRouteActive()) {
//            route = routeManager.getActiveRoute();
//        }
        
        
        MonaLisaSSPAOptionsDialog monaLisaDialog = new MonaLisaSSPAOptionsDialog(route, voyageHandlingLayer, aisHandler, mmsi);
        monaLisaDialog.showDialog();
        

    }

}
