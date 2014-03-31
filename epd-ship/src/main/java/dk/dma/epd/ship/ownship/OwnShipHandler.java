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
package dk.dma.epd.ship.ownship;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.sensor.nmea.IAisSensorListener;
import dk.dma.epd.common.prototype.sensor.pnt.IPntDataListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.util.Util;
import net.jcip.annotations.ThreadSafe;

/**
 * Class for handling incoming own-ship related messages and status data
 */
@ThreadSafe
public class OwnShipHandler extends MapHandlerChild implements Runnable,
        IAisSensorListener, IPntDataListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(OwnShipHandler.class);
    private static final String OWN_SHIP_FILE = EPD.getInstance().getHomePath()
            .resolve(".ownship").toString();

    private final AisSettings aisSettings;
    private PntHandler pntHandler;

    private volatile VesselTarget aisTarget;
    private volatile PntData pntData;

    protected CopyOnWriteArrayList<IOwnShipListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Constructor
     * 
     * @param aisSettings
     */
    public OwnShipHandler(AisSettings aisSettings) {
        super();
        this.aisSettings = aisSettings;
        initAisTarget();
        publishOwnShipChanged(null, aisTarget);
        
        EPD.startThread(this, "OwnShipHandler");
    }

    /**
     * Initializes an AIS vessel target
     */
    private synchronized void initAisTarget() {
        // Log previous own ship value for use in publishOwnShipChanged below.
        aisTarget = new VesselTarget();
        aisTarget.getSettings().setPastTrackDisplayTime(
                aisSettings.getPastTrackDisplayTime());
        aisTarget.getSettings().setPastTrackMinDist(
                aisSettings.getPastTrackOwnShipMinDist());
    }

    /**
     * Method receiving AIS messages from AIS sensor
     */
    @Override
    public final void receive(AisMessage aisMessage) {
        // The AisHandler will process these
    }

    /**
     * Method for receiving own-ship AIS messages
     * 
     * @param aisMessage
     */
    @Override
    public synchronized void receiveOwnMessage(AisMessage aisMessage) {
        // Determine if our vessel has changed. Clear if so.
        VesselTarget oldOwnShip = aisTarget;
        
        if (aisMessage.getUserId() != aisTarget.getMmsi()) {
            initAisTarget();
        }

        if (aisMessage instanceof AisPositionMessage) {
            AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
            aisTarget.setAisClass(VesselTarget.AisClass.A);
            aisTarget.setPositionData(new VesselPositionData(aisPositionMessage));
        } else if (aisMessage instanceof AisMessage18) {
            AisMessage18 posMessage = (AisMessage18) aisMessage;
            aisTarget.setAisClass(VesselTarget.AisClass.B);
            aisTarget.setPositionData(new VesselPositionData(posMessage));
        } else if (aisMessage instanceof AisMessage5) {
            AisMessage5 msg5 = (AisMessage5) aisMessage;
            aisTarget.setStaticData(new VesselStaticData(msg5));
        }
        aisTarget.setLastReceived(PntTime.getInstance().getDate());
        aisTarget.setMmsi(aisMessage.getUserId());

        // Update the past-tracks
        updatePastTrackPosition();

        // If the MMSI has changed, broadcast to change listeners
        if (oldOwnShip != aisTarget) {
            publishOwnShipChanged(oldOwnShip, aisTarget);
        }
    }

    /**
     * Returns a reference to the current AIS target.
     * <p>
     * You should probably use the computed {@code getPositionData()} or
     * {@code getStaticData()} instead.
     * 
     * @return a reference to the current AIS target
     */
    public synchronized VesselTarget getAisTarget() {
        return aisTarget;
    }

    /**
     * Returns the {@code VesselStaticData()} of the current AIS target, or
     * {@code null} if the AIS target is undefined.
     * 
     * @return the {@code VesselStaticData} of the current AIS target
     */
    public synchronized VesselStaticData getStaticData() {
        return (aisTarget.getStaticData() == null) ? null : aisTarget
                .getStaticData();
    }

    /**
     * Returns the MMSI of the current AIS target, or {@code null} if the AIS
     * target is undefined.
     * 
     * @return the MMSI of the current AIS target
     */
    public synchronized Long getMmsi() {
        return aisTarget.getMmsi();
    }

    /**
     * Called by the PNT handler when the PntData is updated
     * 
     * @param pntData
     *            the updated PntData
     */
    @Override
    public synchronized void pntDataUpdate(PntData pntData) {
        this.pntData = pntData;

        // Update the past-tracks
        updatePastTrackPosition();

        // Update the listeners
        publishUpdate();
    }

    /**
     * Returns the current PNT data.
     * <p>
     * You should probably use the computed {@code getPositionData()} instead.
     * 
     * @return the current PNT data
     */
    public synchronized PntData getPntData() {
        return pntData;
    }

    /**
     * Returns the <i>best</i> computed {@linkplain VesselPositionData} by
     * combining the current AIS and PNT data.
     * 
     * @return the current own-ship position data
     */
    public synchronized VesselPositionData getPositionData() {
        // Initialize the position with an AIS target position
        VesselPositionData pos = (aisTarget.getPositionData() == null) ? new VesselPositionData()
                : new VesselPositionData(aisTarget.getPositionData());

        // Update with PNT position
        if (pntData != null) {
            if (pntData.getPosition() != null) {
                pos.setPos(pntData.getPosition());
            }
            if (pntData.getCog() != null) {
                pos.setCog(pntData.getCog().floatValue());
            }
            if (pntData.getSog() != null) {
                pos.setSog(pntData.getSog().floatValue());
            }
        }
        return pos;
    }

    /**
     * Returns if the own-ship has an associated position
     * 
     * @return if the own-ship has an associated position
     */
    public synchronized boolean isPositionDefined() {
        // Same as asking if getPositionData.getPos() != null:
        return (aisTarget.getPositionData() != null && aisTarget
                .getPositionData().getPos() != null)
                || (pntData != null && pntData.getPosition() != null);
    }

    /**
     * Shows or hides past tracks for the own-ship
     * 
     * @param show
     *            whether to show or hide past tracks
     */
    public synchronized void setShowPastTracks(boolean show) {
        aisTarget.getSettings().setShowPastTrack(show);
    }

    /**
     * Updates the current position to the recorded list of past-track
     * positions.
     * <p>
     * The positions are aggregated according to the past-track settings.
     */
    private void updatePastTrackPosition() {
        Position pos = getPositionData().getPos();
        if (pos != null) {
            getAisTarget().getPastTrackData().addPosition(pos,
                    aisSettings.getPastTrackMinDist());
        }
    }

    /**
     * Sub-classes can override to perform periodic updates. Called every 10
     * seconds.
     */
    protected synchronized void updatePeriodic() {
        aisTarget.getPastTrackData().cleanup(
                60 * aisSettings.getPastTrackMaxTime());
    }

    /**
     * Saves the own-ship object
     */
    public void saveView() {
        try (FileOutputStream fileOut = new FileOutputStream(OWN_SHIP_FILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(aisTarget);
        } catch (IOException e) {
            LOG.error("Failed to save own-ship file: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the own-ship object
     */
    public void loadView() {
        try (FileInputStream fileIn = new FileInputStream(OWN_SHIP_FILE);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            VesselTarget oldOwnShip = aisTarget;
            aisTarget = (VesselTarget) objectIn.readObject();
            publishOwnShipChanged(oldOwnShip, aisTarget);
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load own-ship file: " + e.getMessage(), e);
            // Delete possible corrupted or old file
            new File(".ownship").delete();
        }

        if (aisTarget == null) {
            initAisTarget();
        }
    }

    /**
     * Called when a bean is added to this bean context
     * 
     * @param obj
     *            the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
            pntHandler.addListener(this);
        }
    }

    /**
     * Publish the update of a target to all listeners
     */
    public final void publishUpdate() {
        for (IOwnShipListener listener : listeners) {
            listener.ownShipUpdated(this);
        }
    }

    /**
     * Informs listeners that the {@link VesselTarget} used as own ship by this
     * {@code OwnShipHandler} has now changed.
     * 
     * @param oldOwnShip The {@link VesselTarget} that was previously used as own ship.
     * @param newOwnShip The {@link VesselTarget} that is now used as own ship.
     */
    public final void publishOwnShipChanged(VesselTarget oldOwnShip, VesselTarget newOwnShip) {
        for (IOwnShipListener listener : this.listeners) {
            listener.ownShipChanged(oldOwnShip, newOwnShip);
        }
    }

    /**
     * Adds a listener for own-ship updates
     * 
     * @param listener
     *            the listener to add
     */
    public final void addListener(IOwnShipListener listener) {
        listeners.addIfAbsent(listener);
    }

    /**
     * Removes a listener for own-ship updates
     * 
     * @param listener
     *            the listener to remove
     */
    public final void removeListener(IOwnShipListener listener) {
        listeners.remove(listener);
    }

    /**
     * Called after the OwnShipHandler has been initialized.
     */
    @Override
    public void run() {
        while (true) {
            Util.sleep(10000);
            // Update status of own-ship
            updatePeriodic();
        }
    }
}
