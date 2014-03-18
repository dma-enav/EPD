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

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.voyage.Voyage;

public class VoyageRenegotiate extends JMenuItem implements IMapMenuAction {

    private long transactionid;
    private AisHandlerCommon aisHandler;
    private static final long serialVersionUID = 1L;
    private StrategicRouteHandler strategicRouteHandler;

    /**
     * @param transactionid
     *            the transactionid to set
     */
    public void setTransactionid(long transactionid) {
        this.transactionid = transactionid;
    }

    public VoyageRenegotiate(String text) {
        super();
        setText(text);
    }

    
    
    /**
     * @param aisHandler the aisHandler to set
     */
    public void setAisHandler(AisHandlerCommon aisHandler) {
        this.aisHandler = aisHandler;
    }

    /**
     * @param strategicRouteHandler the strategicRouteHandler to set
     */
    public void setStrategicRouteHandler(StrategicRouteHandler strategicRouteHandler) {
        this.strategicRouteHandler = strategicRouteHandler;
    }

    @Override
    public void doAction() {
        handleNegotiation();
        
        
    }
    
    private void handleNegotiation(){
        
        if (strategicRouteHandler.getStrategicNegotiationData().containsKey(transactionid)){
            
            StrategicRouteNegotiationData entry = strategicRouteHandler.getStrategicNegotiationData().get(transactionid);
            
    
            String shipName = "" + entry.getMmsi();
            
            VesselTarget vesselTarget = aisHandler.getVesselTarget(entry.getMmsi());
            if (vesselTarget.getStaticData() != null) {
                shipName = vesselTarget.getStaticData().getTrimmedName();
            }
    
            // Get latest route
            Route route = new Route(entry.getLatestRoute());
    
            Voyage voyage = new Voyage(entry.getMmsi(), route, entry.getId());
    
            Route originalRoute = new Route(entry.getOriginalRoute());
            
            EPDShore.getInstance().getMainFrame().addStrategicRouteExchangeHandlingWindow(originalRoute,
                    shipName, voyage, true);
        }
    }
}
