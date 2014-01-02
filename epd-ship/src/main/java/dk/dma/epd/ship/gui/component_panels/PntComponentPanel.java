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

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.SensorPanel;
import dk.dma.epd.ship.gui.panels.PntPanel;

public class PntComponentPanel extends OMComponentPanel implements
        IPntDataListener {

    private static final long serialVersionUID = 1L;
    private PntHandler pntHandler;
    private AisHandler aisHandler;
    
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
        VesselTarget ownShip = null;
        
        if (aisHandler != null) {
            ownShip = aisHandler.getOwnShip();
        }
        
        if (ownShip != null) {
            VesselPositionData posData = ownShip.getPositionData();

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
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }

    }
    

}
