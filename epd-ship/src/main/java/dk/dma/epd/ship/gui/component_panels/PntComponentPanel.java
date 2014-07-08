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

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.SensorPanel;
import dk.dma.epd.ship.gui.panels.PntPanel;
import dk.dma.epd.ship.ownship.OwnShipHandler;

public class PntComponentPanel extends OMComponentPanel implements
        IPntDataListener, DockableComponentPanel {

    private static final long serialVersionUID = 1L;
    private PntHandler pntHandler;
    private OwnShipHandler ownShipHandler;
    
    private PntData pntData;
    
    private final PntPanel pntPanel = new PntPanel();

    public PntComponentPanel() {
        super();
        
//        this.setMinimumSize(new Dimension(10, 115));
        
        pntPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        setLayout(new BorderLayout(0, 0));
        add(pntPanel, BorderLayout.NORTH);
        setVisible(false);
    }



    public void setGpsData(PntData gpsData) {
        synchronized (SensorPanel.class) {
            this.pntData = gpsData;
        }
    }
    
    public PntData getPntData() {
        synchronized (SensorPanel.class) {
            return pntData;
        }
    }
    
    @Override
    public void pntDataUpdate(PntData pntData) {
        this.setGpsData(pntData);
        Position pos = pntData.getPosition();
        if (pntData.isBadPosition() || pos == null) {
            pntPanel.getLatLabel().setText("N/A");
            pntPanel.getLonLabel().setText("N/A");
        } else {
            pntPanel.getLatLabel().setText(Formatter.latToPrintable(pos.getLatitude()));
            pntPanel.getLonLabel().setText(Formatter.lonToPrintable(pos.getLongitude()));
        }
        
        if (pntData.isBadPosition() || pntData.getSog() == null) {
            pntPanel.getSogLabel().setText("N/A");
        } else {
            pntPanel.getSogLabel().setText(Formatter.formatSpeed(pntData.getSog()));
        }
        
        if (pntData.isBadPosition() || pntData.getCog() == null) {
            pntPanel.getCogLabel().setText("N/A");
        } else {
            pntPanel.getCogLabel().setText(Formatter.formatDegrees(pntData.getCog(), 1));
        }
        
        Double heading = null;
        
        if (ownShipHandler != null) {
            VesselPositionData posData = ownShipHandler.getPositionData();

            if (posData != null && posData.getTrueHeading() < 360) {
                heading = (double) posData.getTrueHeading();
            }

        }
        
        pntPanel.getHdgLabel().setText(Formatter.formatDegrees(heading, 1));

        
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
        return "GPS";
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
