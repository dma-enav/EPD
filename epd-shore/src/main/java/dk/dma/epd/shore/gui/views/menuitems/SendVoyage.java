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

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.strategicRouteExchange.SendStrategicRouteDialog;
import dk.dma.epd.shore.voyage.Voyage;



public class SendVoyage extends JMenuItem implements IMapMenuAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Voyage voyage;
    private SendStrategicRouteDialog sendVoyageDialog;
    private boolean modifiedRoute;
    private JMapFrame parent;
    private boolean renegotiate;
    
    
    public SendVoyage(String text) {
        super();
        this.setText(text);
    }

    @Override
    public void doAction() {
        sendVoyageDialog.setParent(parent);
        sendVoyageDialog.setVoyage(voyage);
        sendVoyageDialog.setModifiedRoute(modifiedRoute);
        sendVoyageDialog.setRenegotiate(renegotiate);
        sendVoyageDialog.setVisible(true);
    }

    /**
     * @param renegotiate the renegotiate to set
     */
    public void setRenegotiate(boolean renegotiate) {
        this.renegotiate = renegotiate;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(JMapFrame parent) {
        this.parent = parent;
    }

    public void setSendVoyageDialog(SendStrategicRouteDialog sendVoyageDialog){
        this.sendVoyageDialog = sendVoyageDialog;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }
    
    public void setModifiedRoute(boolean modified){
        this.modifiedRoute = modified;
    }
    

}
