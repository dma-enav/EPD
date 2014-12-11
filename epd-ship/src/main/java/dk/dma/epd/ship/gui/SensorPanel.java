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
package dk.dma.epd.ship.gui;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.event.mouse.IMapCoordListener;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.panels.ActiveWaypointPanel;
import dk.dma.epd.ship.gui.panels.CursorPanel;
import dk.dma.epd.ship.gui.panels.OwnShipPanel;
import dk.dma.epd.ship.gui.panels.PntPanel;
import dk.dma.epd.ship.gui.panels.ScalePanel;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import java.util.Locale;

/**
 * Sensor panel right of map
 */
public class SensorPanel extends OMComponentPanel implements IPntDataListener, Runnable, ProjectionListener, IMapCoordListener, IRoutesUpdateListener {
    
    private static final long serialVersionUID = 1L;
    
    private PntHandler gpsHandler;
    private OwnShipHandler ownShipHandler;
    private MsiNmServiceHandlerCommon msiNmHandler;
    
    private PntData gpsData;
    private ChartPanel chartPanel;
    private RouteManager routeManager;
    private final ScalePanel scalePanel = new ScalePanel();    
    private final OwnShipPanel ownShipPanel = new OwnShipPanel();
    private final PntPanel gpsPanel = new PntPanel();
    private final CursorPanel cursorPanel = new CursorPanel();
    private final ActiveWaypointPanel activeWaypointPanel;
    private final JLabel euBalticLogo = new JLabel("");
    private final JLabel efficienseaLogo = new JLabel("");
    
    public SensorPanel() {
        super();
        activeWaypointPanel = new ActiveWaypointPanel();
        activeWaypointPanel.setVisible(false);
        activeWaypointPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        cursorPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        gpsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        scalePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        ownShipPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        efficienseaLogo.setIcon(EPDShip.res().getCachedImageIcon("/images/sensorPanel/efficiensea.png"));
        euBalticLogo.setIcon(EPDShip.res().getCachedImageIcon("/images/sensorPanel/euBaltic.png"));
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(ownShipPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(scalePanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(gpsPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(cursorPanel, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(activeWaypointPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(euBalticLogo, Alignment.TRAILING)
                        .addComponent(efficienseaLogo, Alignment.TRAILING))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(scalePanel, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(ownShipPanel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(gpsPanel, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(cursorPanel, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(activeWaypointPanel, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                    .addComponent(efficienseaLogo)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(euBalticLogo)
                    .addContainerGap())
        );
        setLayout(groupLayout);
        new Thread(this).start();
    }

    /**
     * Receive GPS update
     */
    @Override
    public void pntDataUpdate(PntData gpsData) {
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
        String ownName = null;
        String ownCallsign = null;
        Long ownMmsi = null;
        
        if (ownShipHandler != null) {
            VesselPositionData posData = ownShipHandler.getPositionData();
            VesselStaticData staticData = ownShipHandler.getStaticData();

            ownMmsi = ownShipHandler.getMmsi();
            if (posData != null && posData.getTrueHeading() < 360) {
                heading = (double) posData.getTrueHeading();
            }

            if (staticData != null) {
                ownName = staticData.getTrimmedName();
                ownCallsign = staticData.getTrimmedCallsign();
            }

        }
        
        gpsPanel.getHdgLabel().setText(Formatter.formatDegrees(heading, 1));
        ownShipPanel.getNameLabel().setText("<html>" + Formatter.formatString(ownName, "N/A") + "</html>");
        ownShipPanel.getCallsignLabel().setText("<html>" + Formatter.formatString(ownCallsign, "N/A") + "</html>");
        ownShipPanel.getMmsiLabel().setText(Formatter.formatLong(ownMmsi));
        
        activeWaypointPanel.updateActiveNavData();
        
    }
    
    @Override
    public void run() {
        while (true) {
            scalePanel.getTimeLabel().setText(Formatter.formatLongDateTime(PntTime.getDate()));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { }
        }
        
    }
    
    public void initPanel(ChartPanel chartPanel) {
        // Add gps panel as position listener
        EPDShip.getInstance().getPntHandler().addListener(this);
        
        // Start time panel thread
        this.chartPanel = chartPanel;
        new Thread(this).start();
        
    }
    
    public PntData getGpsData() {
        synchronized (SensorPanel.class) {
            return gpsData;
        }
    }
    
    public void setGpsData(PntData gpsData) {
        synchronized (SensorPanel.class) {
            this.gpsData = gpsData;
        }
    }

    @Override
    public void projectionChanged(ProjectionEvent arg0) {
        setScale(chartPanel.getMap().getProjection().getScale());
    }
    
    public void setScale(float scale){
        scalePanel.getScaleLabel().setText("Scale: " + String.format(Locale.US, "%3.0f", scale));
    }

    /**
     * Receive mouse location
     */
    @Override
    public void receiveCoord(LatLonPoint llp) {
        cursorPanel.getCurLatLabel().setText(Formatter.latToPrintable(llp.getLatitude()));
        cursorPanel.getCurLonLabel().setText(Formatter.lonToPrintable(llp.getLongitude()));
        PntData gpsData = this.getGpsData();
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

    /**
     * Receive route update
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if(routeManager.isRouteActive()){
            activeWaypointPanel.setVisible(true);
            activeWaypointPanel.updateActiveNavData();
        } else if (activeWaypointPanel.isVisible()) {
            activeWaypointPanel.setVisible(false);
        }
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel)obj;
            chartPanel.getMap().addProjectionListener(this);
            return;
        }
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager)obj;
            activeWaypointPanel.setRouteManager(routeManager);
            routeManager.addListener(this);
            return;
        }
        if (gpsHandler == null && obj instanceof PntHandler) {
            gpsHandler = (PntHandler)obj;
            gpsHandler.addListener(this);
        }
        if (ownShipHandler == null && obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler)obj;
        }
        if (msiNmHandler == null && obj instanceof MsiNmServiceHandlerCommon) {
            msiNmHandler = (MsiNmServiceHandlerCommon)obj;
        }
    }
    
    @Override
    public void findAndUndo(Object obj) {
        if (ownShipHandler == obj) {
            ownShipHandler = null;
        }
    }
}
