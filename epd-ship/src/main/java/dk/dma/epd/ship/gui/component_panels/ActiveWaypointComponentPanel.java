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

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.ship.event.IMapCoordListener;
import dk.dma.epd.ship.gui.panels.ActiveWaypointPanel;
import dk.dma.epd.ship.route.RouteManager;

public class ActiveWaypointComponentPanel extends OMComponentPanel implements IPntDataListener, Runnable, ProjectionListener, IMapCoordListener, IRoutesUpdateListener {

    private static final long serialVersionUID = 1L;
    private final ActiveWaypointPanel activeWaypointPanel;
    private RouteManager routeManager;
    
    public ActiveWaypointComponentPanel(){
        super();
        
//        this.setMinimumSize(new Dimension(10, 165));
        
        activeWaypointPanel = new ActiveWaypointPanel();
//        activeWaypointPanel.setVisible(false);
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
//        if(routeManager.isRouteActive()){
//            activeWaypointPanel.setVisible(true);
//            activeWaypointPanel.updateActiveNavData();
//        } else if (activeWaypointPanel.isVisible()) {
//            activeWaypointPanel.setVisible(false);
//        }
    }
    @Override
    public void recieveCoord(LatLonPoint llp) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
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
    

}
