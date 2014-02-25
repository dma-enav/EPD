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
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.mouse.IMapCoordListener;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.SensorPanel;
import dk.dma.epd.ship.gui.panels.CursorPanel;

public class CursorComponentPanel extends OMComponentPanel implements IPntDataListener, IMapCoordListener {

    private static final long serialVersionUID = 1L;
    private final CursorPanel cursorPanel = new CursorPanel();
    private PntData pntData;
    
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
    public void receiveCoord(LatLonPoint llp) {
        cursorPanel.getCurLatLabel().setText(Formatter.latToPrintable(llp.getLatitude()));
        cursorPanel.getCurLonLabel().setText(Formatter.lonToPrintable(llp.getLongitude()));
        PntData pntData = this.getPntData();
        if(pntData == null || pntData.isBadPosition() || pntData.getPosition() == null){
            cursorPanel.getCurCursLabel().setText("N/A");
            cursorPanel.getCurDistLabel().setText("N/A");
        } else {
            Position pos = pntData.getPosition();
            Position curPos = Position.create(llp.getLatitude(), llp.getLongitude()); 
            cursorPanel.getCurCursLabel().setText(Formatter.formatDegrees(pos.rhumbLineBearingTo(curPos), 1));
            double distance = pos.rhumbLineDistanceTo(curPos)/1852;
            cursorPanel.getCurDistLabel().setText(Formatter.formatDistNM(distance));                        
        }
    }
    
    public PntData getPntData() {
        synchronized (SensorPanel.class) {
            return pntData;
        }
    }
    
    public void setGpsData(PntData gpsData) {
        synchronized (SensorPanel.class) {
            this.pntData = gpsData;
        }
    }
    
    @Override
    public void pntDataUpdate(PntData gpsData) {
        this.setGpsData(gpsData);
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof PntHandler) {
            ((PntHandler) obj).addListener(this);
        }
    }
}
