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
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.mouse.IMapCoordListener;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.SensorPanel;
import dk.dma.epd.ship.gui.panels.CursorPanel;

public class CursorComponentPanel extends OMComponentPanel implements IPntDataListener, IMapCoordListener, DockableComponentPanel {

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

    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "Cursor";
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
