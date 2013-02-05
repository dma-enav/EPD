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
package dk.dma.epd.ship.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Example layer that registers itself as listener for GPS, AIS and route updates
 * It paints small circles to make a simple track indication after vessel AIS targets
 */
public class ExampleLayer extends OMGraphicHandlerLayer implements IGpsDataListener, IAisTargetListener, IRoutesUpdateListener {
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(ExampleLayer.class);
    
    // A handler able to read and parse GPS messages
    private GpsHandler gpsHandler;
    // An AIS target table
    private AisHandler aisHandler;
    // A manager doing the general route handling
    private RouteManager routeManager;
    
    // The graphics to present
    private OMGraphicList graphics = new OMGraphicList();
    
    /**
     * Receive GPS updates in the form of GpsData messages
     */
    @Override
    public void gpsDataUpdate(GpsData gpsData) {
        LOG.info("New GPS data: " + gpsData);        
    }

    /**
     * Receive AIS target updates
     */
    @Override
    public void targetUpdated(AisTarget aisTarget) {
        LOG.info("AIS target updated: " + aisTarget);
        
        if (aisTarget instanceof VesselTarget) {
            VesselTarget vesselTarget = (VesselTarget)aisTarget;
            LOG.info("Vessel " + vesselTarget.getMmsi() + " heading: " + vesselTarget.getPositionData().getTrueHeading());
            // Create small circles for last 15 position report
            double lat = vesselTarget.getPositionData().getPos().getLatitude();
            double lon = vesselTarget.getPositionData().getPos().getLongitude();
            OMCircle circle = new OMCircle(0, 0, 0, 0, 4, 4);
            circle.setLatLon(lat, lon);
            graphics.add(circle);
            
            int excess = graphics.size() - 1000;
            for (int i=0; i < excess; i++) {
                graphics.remove(0);
            }
            
            graphics.project(getProjection(), true);
                        
            doPrepare();
        }
        
    }

    /**
     * Receive updates when routes changes
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        LOG.info("Route update event: " + e);        
        if (routeManager.isRouteActive()) {
            ActiveRoute activeRoute = routeManager.getActiveRoute();
            LOG.info("TTG for next waypoint: " + activeRoute.getActiveWpTtg());
        }
    }
    
    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }
    
    /**
     * Bean context method to find other components
     */
    @Override
    public void findAndInit(Object obj) {
        LOG.info("Hello from findAndInit obj.class: " + obj.getClass());
        if (gpsHandler == null && obj instanceof GpsHandler) {
            gpsHandler = (GpsHandler)obj;
            gpsHandler.addListener(this);
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
            aisHandler.addListener(this);
        }
        if (routeManager == null && obj instanceof RouteManager) {
            routeManager = (RouteManager)obj;
            routeManager.addListener(this);
        }
    }
    
    /**
     * Bean context method to remove other components
     */
    @Override
    public void findAndUndo(Object obj) {
        if (gpsHandler == obj) {
            gpsHandler.removeListener(this);
            gpsHandler = null;
        }
        if (aisHandler == obj) {
            aisHandler.removeListener(this);
            aisHandler = null;
        }
        if (obj == routeManager) {
            routeManager.removeListener(this);
            routeManager = null;
        }
    }


}
