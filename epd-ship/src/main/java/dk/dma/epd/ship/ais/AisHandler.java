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
package dk.dma.epd.ship.ais;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;

import net.jcip.annotations.ThreadSafe;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.nmea.IAisListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.ship.EPDShip;

/**
 * Class for handling incoming AIS messages and maintainer of AIS target tables
 */
@ThreadSafe
public class AisHandler extends AisHandlerCommon implements IAisListener, IStatusComponent {

    private volatile VesselTarget ownShip = new VesselTarget();
    
    private final double aisRange;

    public AisHandler(SensorSettings sensorSettings, AisSettings aisSettings) {
        super(aisSettings);
        aisRange = sensorSettings.getAisSensorRange();                
    }


    /**
     * Method for receiving own ship AIS messages
     * 
     * @param aisMessage
     */
    @Override
    public void receiveOwnMessage(AisMessage aisMessage) {
        // Determine if our vessel has changed. Clear if so.
        if (aisMessage.getUserId() != ownShip.getMmsi()) {
            ownShip = new VesselTarget();
        }

        synchronized (ownShip) {
            if (aisMessage instanceof AisPositionMessage) {
                AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
                ownShip.setAisClass(VesselTarget.AisClass.A);
                ownShip.setPositionData(new VesselPositionData(aisPositionMessage));
            } else if (aisMessage instanceof AisMessage18) {
                AisMessage18 posMessage = (AisMessage18) aisMessage;
                ownShip.setAisClass(VesselTarget.AisClass.B);
                ownShip.setPositionData(new VesselPositionData(posMessage));
            } else if (aisMessage instanceof AisMessage5) {
                AisMessage5 msg5 = (AisMessage5) aisMessage;
                ownShip.setStaticData(new VesselStaticData(msg5));
            }
            ownShip.setLastReceived(PntTime.getInstance().getDate());
            ownShip.setMmsi(aisMessage.getUserId());
        }

    }


    /**
     * Determine if position is within range
     * 
     * @param pos
     * @return
     */
    @Override
    protected boolean isWithinRange(Position pos) {
        if (getAisRange() <= 0) {
            return true;
        }
        PntData gpsData = EPDShip.getPntHandler().getCurrentData();
        if (gpsData == null) {
            return false;
        }
        double distance = gpsData.getPosition().rhumbLineDistanceTo(pos) / 1852.0;
        return distance <= aisRange;
    }

    @Override
    public VesselTarget getOwnShip() {
        return ownShip;
    }


    public double getAisRange() {
        return aisRange;
    }

    /**
     * Get AisMessageExtended for a single VesselTarget
     * 
     * @param currentTarget
     * @return
     */
    public AisMessageExtended getShip(VesselTarget currentTarget) {
        String name = " N/A";
        String dst = "N/A";
        Position ownPosition;
        double hdg = -1;
        Position targetPosition = null;

        if (currentTarget.getStaticData() != null) {
            name = " " + AisMessage.trimText(currentTarget.getStaticData().getName());
        }
        if (!EPDShip.getPntHandler().getCurrentData().isBadPosition()) {
            ownPosition = EPDShip.getPntHandler().getCurrentData().getPosition();

            if (currentTarget.getPositionData().getPos() != null) {
                targetPosition = currentTarget.getPositionData().getPos();
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                dst = nf.format(Converter.metersToNm(ownPosition.rhumbLineDistanceTo(targetPosition))) + " NM";

            }
        }
        hdg = currentTarget.getPositionData().getCog();

        // System.out.println("Key: " + key + ", Value: " +
        // this.getVesselTargets().get(key));
        AisMessageExtended newEntry = new AisMessageExtended(name, currentTarget.getMmsi(), hdg, dst);

        return newEntry;

    }
    
    @Override
    public void saveView() {
        super.saveView();
        
        try (FileOutputStream fileOut = new FileOutputStream(".ownship");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(ownShip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void loadView() {
        super.loadView();
        try (FileInputStream fileIn = new FileInputStream(".ownship");
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            ownShip = (VesselTarget) objectIn.readObject();
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
//            LOG.error("Failed to load AIS view file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(".ownship").delete();
        }
        
        if(ownShip == null) {
            ownShip = new VesselTarget();
        }
    }
}
