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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.panels.OwnShipPanel;
import dk.dma.epd.ship.ownship.OwnShipHandler;

public class OwnShipComponentPanel extends OMComponentPanel implements IPntDataListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final OwnShipPanel ownShipPanel = new OwnShipPanel();
    
    private OwnShipHandler ownShipHandler;
    private PntHandler pntHandler;
    
    public OwnShipComponentPanel(){
        super();
        
//        this.setMinimumSize(new Dimension(10, 70));
        
        ownShipPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        setLayout(new BorderLayout(0, 0));
        add(ownShipPanel, BorderLayout.NORTH);
        setVisible(false);
    }
    

    @Override
    public void pntDataUpdate(PntData pntData) {

        String ownName = null;
        String ownCallsign = null;
        Long ownMmsi = null;
        
        if (ownShipHandler != null) {
            VesselStaticData staticData = ownShipHandler.getStaticData();

            ownMmsi = ownShipHandler.getMmsi();

            if (staticData != null) {
                ownName = AisMessage.trimText(staticData.getName());
                ownCallsign = AisMessage.trimText(staticData.getCallsign());
            }

        }
        
        ownShipPanel.getNameLabel().setText("<html>" + Formatter.formatString(ownName, "N/A") + "</html>");
        ownShipPanel.getCallsignLabel().setText("<html>" + Formatter.formatString(ownCallsign, "N/A") + "</html>");
        ownShipPanel.getMmsiLabel().setText(Formatter.formatLong(ownMmsi));
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler)obj;
            pntHandler.addListener(this);
        }
        if (ownShipHandler == null && obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler)obj;
        }
    }

}
