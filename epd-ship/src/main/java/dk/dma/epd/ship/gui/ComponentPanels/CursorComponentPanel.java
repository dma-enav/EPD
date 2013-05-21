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
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.GpsHandler;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.event.IMapCoordListener;
import dk.dma.epd.ship.gui.SensorPanel;
import dk.dma.epd.ship.gui.Panels.CursorPanel;

public class CursorComponentPanel extends OMComponentPanel implements IGpsDataListener, IMapCoordListener {

    private static final long serialVersionUID = 1L;
    private final CursorPanel cursorPanel = new CursorPanel();
    private GpsData gpsData;
    
    public CursorComponentPanel(){
        super();
        
//        this.setMinimumSize(new Dimension(10, 110));
        
        cursorPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        setLayout(new BorderLayout(0, 0));     
        add(cursorPanel, BorderLayout.NORTH);
        setVisible(false);
    }

    /**
     * Receive mouse location
     */
    @Override
    public void recieveCoord(LatLonPoint llp) {
        cursorPanel.getCurLatLabel().setText(Formatter.latToPrintable(llp.getLatitude()));
        cursorPanel.getCurLonLabel().setText(Formatter.lonToPrintable(llp.getLongitude()));
        GpsData gpsData = this.getGpsData();
        if(gpsData == null || gpsData.isBadPosition() || gpsData.getPosition() == null){
            cursorPanel.getCurCursLabel().setText("N/A");
            cursorPanel.getCurDistLabel().setText("N/A");
        } else {
            Position pos = gpsData.getPosition();
            Position curPos = Position.create(llp.getLatitude(), llp.getLongitude()); 
            cursorPanel.getCurCursLabel().setText(Formatter.formatDegrees(pos.rhumbLineBearingTo(curPos), 1));
            double distance = pos.rhumbLineDistanceTo(curPos)/1852;
            cursorPanel.getCurDistLabel().setText(Formatter.formatDistNM(distance));                        
        }
    }
    
    public GpsData getGpsData() {
        synchronized (SensorPanel.class) {
            return gpsData;
        }
    }
    
    public void setGpsData(GpsData gpsData) {
        synchronized (SensorPanel.class) {
            this.gpsData = gpsData;
        }
    }
    
    @Override
    public void gpsDataUpdate(GpsData gpsData) {
        this.setGpsData(gpsData);
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof GpsHandler) {
            ((GpsHandler) obj).addListener(this);
        }
    }
}
