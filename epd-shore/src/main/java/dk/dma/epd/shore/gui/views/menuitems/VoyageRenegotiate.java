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
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.service.StrategicRouteExchangeHandler;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;
import dk.dma.epd.shore.voyage.Voyage;

public class VoyageRenegotiate extends JMenuItem implements IMapMenuAction {

    private long transactionid;
    private AisHandler aisHandler;
    private static final long serialVersionUID = 1L;
    private StrategicRouteExchangeHandler monaLisaHandler;

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
    public void setAisHandler(AisHandler aisHandler) {
        this.aisHandler = aisHandler;
    }

    /**
     * @param monaLisaHandler the monaLisaHandler to set
     */
    public void setMonaLisaHandler(StrategicRouteExchangeHandler monaLisaHandler) {
        this.monaLisaHandler = monaLisaHandler;
    }

    @Override
    public void doAction() {
        handleNegotiation();
        
        
    }
    
    private void handleNegotiation(){
        
        if (monaLisaHandler.getStrategicNegotiationData().containsKey(transactionid)){
            
        System.out.println("Handling it!");
        
        StrategicRouteNegotiationData message = monaLisaHandler.getStrategicNegotiationData().get(transactionid);
        

        String shipName = "" + message.getMmsi();
        
        if (aisHandler.getVesselTargets().get(message.getMmsi())
                .getStaticData() != null) {
            shipName = aisHandler.getVesselTargets()
                    .get(message.getMmsi()).getStaticData()
                    .getName();
        }

        // Get latest route
        Route route = null;
        
        //The one we sent out was accepted
        if (message.getRouteMessage().size() > message.getRouteReply().size()){
          route = new Route(message.getRouteMessage()
          .get(message.getRouteMessage().size() - 1)
          .getRoute());
        }else{
            route = new Route(message.getRouteReply()
                    .get(message.getRouteReply().size() - 1)
                    .getRoute());  
        }

        Voyage voyage = new Voyage(message.getMmsi(), route,
                message.getId());

        Route originalRoute = new Route(message.getRouteMessage().get(0).getRoute());
        
        EPDShore.getMainFrame().addStrategicRouteExchangeHandlingWindow(originalRoute,
                shipName, voyage, true);
    }
    }
}
