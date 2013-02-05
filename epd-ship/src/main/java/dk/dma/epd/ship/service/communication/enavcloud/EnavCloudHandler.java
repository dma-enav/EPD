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
package dk.dma.epd.ship.service.communication.enavcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.messaging.MaritimeMessage;
import dk.dma.enav.model.ship.ShipId;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.IGpsDataListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.settings.EPDEnavSettings;

/**
 * Component to handle eNav Cloud communication
 */
public class EnavCloudHandler extends MapHandlerChild  implements IGpsDataListener, Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(EnavCloudHandler.class);
    
    private String hostPort;
    //private MessageBus messageBus = null;
    private ShipId shipId;
    private GpsHandler gpsHandler;
    private AisHandler aisHandler;
    
    public EnavCloudHandler(EPDEnavSettings enavSettings) {
        this.hostPort = String.format("failover://tcp://%s:%d", enavSettings.getCloudServerHost(), enavSettings.getCloudServerPort());        
    }
    
    /**
     * Send maritime message over enav cloud
     * @param message
     * @return
     */
    public boolean sendMessage(MaritimeMessage message) {
        // TODO shape from where?
//        if (messageBus == null) {
//            return false;
//        }
        
    //    message.setSource(shipId);
        
        // Make metadata with area
        //MessageMetadata metadata = MessageMetadata.create();
        //TODO metadata.setShape();
         
        
    //    messageBus.send(message, metadata);
        return true;
    }
    
    /**
     * Create the message bus
     */
    public void init() {
        LOG.info("Connecting to enav cloud server: " + hostPort + " with shipId " + shipId.getId());
        //ENavContainerConfiguration conf = new ENavContainerConfiguration();
    //    conf.addDatasource(new JmsC2SMessageSource(hostPort, shipId));
    //    ENavContainer client = conf.createAndStart();
//        messageBus = client.getService(MessageBus.class);
        LOG.info("Started succesfull cloud server: " + hostPort + " with shipId " + shipId.getId());

        
    }
    
    /**
     * Receive position updates
     */
    @Override
    public void gpsDataUpdate(GpsData gpsData) {
        // TODO give information to messageBus if valid position        
    }
    
    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof GpsHandler) {
            this.gpsHandler = (GpsHandler)obj;
            this.gpsHandler.addListener(this);
        } else if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler)obj;
        }
    }
    
    @Override
    public void run() {
        // For now ship id will be MMSI so we need to know
        // own ship information. Busy wait for it.
        while (true) {
            Util.sleep(1000);
            if (this.aisHandler != null) {
                VesselTarget ownShip = this.aisHandler.getOwnShip();
                if (ownShip != null) {
                    if (ownShip.getMmsi() > 0) {
                        shipId = ShipId.create(Long.toString(ownShip.getMmsi()));
                        init();
                        return;
                    }
                }
            }
        }        
    }
    
    public void start() {
        new Thread(this).start();
    }

}
