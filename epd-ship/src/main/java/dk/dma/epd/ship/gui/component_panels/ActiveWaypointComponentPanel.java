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

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.event.mouse.IMapCoordListener;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.ship.gui.panels.ActiveWaypointPanel;
import dk.dma.epd.ship.route.RouteManager;

public class ActiveWaypointComponentPanel extends OMComponentPanel 
    implements IPntDataListener, Runnable, ProjectionListener, IMapCoordListener, IRoutesUpdateListener, DockableComponentPanel {

    private static final long serialVersionUID = 1L;
    private final ActiveWaypointPanel activeWaypointPanel;
    private RouteManager routeManager;
    
    public ActiveWaypointComponentPanel(){
        super();
                
        activeWaypointPanel = new ActiveWaypointPanel();
        activeWaypointPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        setLayout(new BorderLayout(0, 0));
        add(activeWaypointPanel, BorderLayout.NORTH);
        setVisible(false);
    }
    
    /**
     * Receive route update
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        activeWaypointPanel.updateActiveNavData();
    }
    @Override
    public void receiveCoord(LatLonPoint llp) {
        
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        
    }

    @Override
    public void run() {
        
    }

    /**
     * Receive PNT update
     */
    @Override
    public void pntDataUpdate(PntData pntData) {
        activeWaypointPanel.updateActiveNavData();
    }
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager)obj;
            activeWaypointPanel.setRouteManager(routeManager);
            routeManager.addListener(this);
            return;
        }
        if (obj instanceof PntHandler) {
            ((PntHandler)obj).addListener(this);
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
        return "Active Waypoint";
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
