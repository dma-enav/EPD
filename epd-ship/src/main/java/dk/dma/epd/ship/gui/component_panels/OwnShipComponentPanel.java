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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.panels.OwnShipPanel;
import dk.dma.epd.ship.ownship.OwnShipHandler;

import javax.swing.JScrollPane;

public class OwnShipComponentPanel extends OMComponentPanel implements IPntDataListener, DockableComponentPanel {

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
        
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.SOUTH);
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
                ownName = staticData.getTrimmedName();
                ownCallsign = staticData.getTrimmedCallsign();
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

    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "Own Ship";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
