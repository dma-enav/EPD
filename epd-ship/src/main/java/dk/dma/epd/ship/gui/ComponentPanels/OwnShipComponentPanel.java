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
package dk.dma.epd.ship.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.GpsHandler;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.Panels.OwnShipPanel;

public class OwnShipComponentPanel extends OMComponentPanel implements IGpsDataListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final OwnShipPanel ownShipPanel = new OwnShipPanel();
    
    private AisHandler aisHandler;
    private GpsHandler gpsHandler;
    
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
    public void gpsDataUpdate(GpsData gpsData) {

        String ownName = null;
        String ownCallsign = null;
        Long ownMmsi = null;
        VesselTarget ownShip = null;
        
        if (aisHandler != null) {
            ownShip = aisHandler.getOwnShip();
        }
        
        if (ownShip != null) {
            VesselStaticData staticData = ownShip.getStaticData();

            ownMmsi = ownShip.getMmsi();

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
        if (gpsHandler == null && obj instanceof GpsHandler) {
            gpsHandler = (GpsHandler)obj;
            gpsHandler.addListener(this);
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }
    }

}
