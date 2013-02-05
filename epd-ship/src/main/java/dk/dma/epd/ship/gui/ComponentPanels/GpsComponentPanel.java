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

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.gui.SensorPanel;
import dk.dma.epd.ship.gui.Panels.GPSPanel;

public class GpsComponentPanel extends OMComponentPanel implements
        IGpsDataListener {

    private static final long serialVersionUID = 1L;
    private GpsHandler gpsHandler;
    private AisHandler aisHandler;
    
    private GpsData gpsData;
    
    private final GPSPanel gpsPanel = new GPSPanel();

    public GpsComponentPanel() {
        super();
        
//        this.setMinimumSize(new Dimension(10, 115));
        
        gpsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        setLayout(new BorderLayout(0, 0));
        add(gpsPanel, BorderLayout.NORTH);
    }



    public void setGpsData(GpsData gpsData) {
        synchronized (SensorPanel.class) {
            this.gpsData = gpsData;
        }
    }
    
    public GpsData getGpsData() {
        synchronized (SensorPanel.class) {
            return gpsData;
        }
    }
    
    @Override
    public void gpsDataUpdate(GpsData gpsData) {
        this.setGpsData(gpsData);
        Position pos = gpsData.getPosition();
        if (gpsData.isBadPosition() || pos == null) {
            gpsPanel.getLatLabel().setText("N/A");
            gpsPanel.getLonLabel().setText("N/A");
        } else {
            gpsPanel.getLatLabel().setText(Formatter.latToPrintable(pos.getLatitude()));
            gpsPanel.getLonLabel().setText(Formatter.lonToPrintable(pos.getLongitude()));
        }
        
        if (gpsData.isBadPosition() || gpsData.getSog() == null) {
            gpsPanel.getSogLabel().setText("N/A");
        } else {
            gpsPanel.getSogLabel().setText(Formatter.formatSpeed(gpsData.getSog()));
        }
        
        if (gpsData.isBadPosition() || gpsData.getCog() == null) {
            gpsPanel.getCogLabel().setText("N/A");
        } else {
            gpsPanel.getCogLabel().setText(Formatter.formatDegrees(gpsData.getCog(), 1));
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
        
        gpsPanel.getHdgLabel().setText(Formatter.formatDegrees(heading, 1));

        
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
