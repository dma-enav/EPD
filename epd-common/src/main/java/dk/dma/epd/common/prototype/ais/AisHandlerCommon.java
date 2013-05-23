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
package dk.dma.epd.common.prototype.ais;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisMessage21;
import dk.dma.ais.message.AisMessage24;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.nmea.IAisListener;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.SensorType;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.status.AisStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;

public class AisHandlerCommon extends MapHandlerChild implements Runnable, IAisListener, IStatusComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(AisHandlerCommon.class);
    
    protected static final String AIS_VIEW_FILE = EPD.getHomePath().resolve(".aisview").toString();

    // How long targets are saved without reports
    protected static final long TARGET_TTL = 60 * 60 * 1000; // One hour
    
    protected ConcurrentHashMap<Integer, AtoNTarget> atonTargets = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, VesselTarget> vesselTargets = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, SarTarget> sarTargets = new ConcurrentHashMap<>();

    protected CopyOnWriteArrayList<IAisTargetListener> listeners = new CopyOnWriteArrayList<>();
    protected CopyOnWriteArrayList<IAisRouteSuggestionListener> suggestionListeners = new CopyOnWriteArrayList<>();
    
    protected NmeaSensor nmeaSensor;    
    protected AisStatus aisStatus = new AisStatus();
    protected final boolean strictAisMode;
    protected final boolean showIntendedRouteDefault;
    protected final String sartMmsiPrefix;

    public AisHandlerCommon(AisSettings aisSettings) {
        sartMmsiPrefix = aisSettings.getSartPrefix();
        strictAisMode = aisSettings.isStrict();
        showIntendedRouteDefault = aisSettings.isShowIntendedRouteByDefault();
    }
    
    /**
     * Method receiving AIS messages from AIS sensor
     */
    @Override
    public final void receive(AisMessage aisMessage) {
        // Mark successful reception
        aisStatus.markAisReception();

        if (aisMessage instanceof AisPositionMessage) {
            AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
            // Create PositionData
            VesselPositionData vesselPositionData = new VesselPositionData(aisPositionMessage);
            // Update or create entry
            if (vesselPositionData.hasPos()) {
                updatePos(aisPositionMessage.getUserId(), vesselPositionData, VesselTarget.AisClass.A);
            }
        } else if (aisMessage instanceof AisMessage18) {
            AisMessage18 posMessage = (AisMessage18) aisMessage;
            VesselPositionData vesselPositionData = new VesselPositionData(posMessage);
            // Update or create entry
            if (vesselPositionData.hasPos()) {
                updatePos(posMessage.getUserId(), vesselPositionData, VesselTarget.AisClass.B);
            }
        } else if (aisMessage instanceof AisMessage21) {
            AisMessage21 msg21 = (AisMessage21) aisMessage;
            updateAton(msg21);
        } else if (aisMessage instanceof AisMessage5) {
            AisMessage5 msg5 = (AisMessage5) aisMessage;
            VesselStaticData staticData = new VesselStaticData(msg5);
            updateStatics(msg5.getUserId(), staticData);
        } else if (aisMessage instanceof AisMessage24) {
            AisMessage24 msg24 = (AisMessage24) aisMessage;
            updateClassBStatics(msg24);
        }
    }
    
    @Override
    public synchronized void receiveOwnMessage(AisMessage aisMessage) {
        return;
    }
    
    /**
     * Update AtoN target
     * 
     * @param msg21
     */
    protected void updateAton(AisMessage21 msg21) {
        if (!isWithinRange(msg21.getPos().getGeoLocation())) {
            return;
        }

        // Try to find existing entry
        AtoNTarget atonTarget = atonTargets.get(msg21.getUserId());
        // If not exists, create new and insert
        if (atonTarget == null) {
            atonTarget = new AtoNTarget();
            atonTarget.setMmsi(msg21.getUserId());
            atonTargets.put(msg21.getUserId(), atonTarget);
        }
        // Update target
        atonTarget.update(msg21);
        // Update last received
        atonTarget.setLastReceived(GnssTime.getInstance().getDate());
        // Update status
        atonTarget.setStatus(AisTarget.Status.OK);
        publishUpdate(atonTarget);
    }

    /**
     * Update vessel target statics
     * 
     * @param mmsi
     * @param staticData
     */
    protected void updateStatics(long mmsi, VesselStaticData staticData) {
        // Determine if this is SART
        if (isSarTarget(mmsi)) {
            updateSartStatics(mmsi, staticData);
            return;
        }

        // Try to find exiting target
        VesselTarget vesselTarget = vesselTargets.get(mmsi);
        // If not exists, wait for it to be created by position report
        if (vesselTarget == null) {
            return;
        }
        // Update static data
        vesselTarget.setStaticData(staticData);

    }

    /**
     * Update class b vessel statics
     * 
     * @param msg24
     */
    protected void updateClassBStatics(AisMessage24 msg24) {
        // Try to find exiting target
        VesselTarget vesselTarget = vesselTargets.get(msg24.getUserId());
        // If not exists, wait for it to be created by position report
        if (vesselTarget == null) {
            return;
        }

        // Get or create static data
        VesselStaticData staticData = vesselTarget.getStaticData();
        if (staticData == null) {
            staticData = new VesselStaticData(msg24);
            vesselTarget.setStaticData(staticData);
        } else {
            staticData.update(msg24);
        }

    }

    /**
     * Update SART statics
     * 
     * @param mmsi
     * @param staticData
     */
    protected void updateSartStatics(long mmsi, VesselStaticData staticData) {
        // Try to find exiting target
        SarTarget sarTarget = sarTargets.get(mmsi);
        // If not exists, wait for it to be created by position report
        if (sarTarget == null) {
            return;
        }
        // Update static data
        sarTarget.setStaticData(staticData);
    }


    /**
     * Update vessel target position data
     * 
     * @param mmsi
     * @param positionData
     * @param aisClass
     */
    protected void updatePos(long mmsi, VesselPositionData positionData, VesselTarget.AisClass aisClass) {
        if (!isWithinRange(positionData.getPos())) {
            return;
        }

        // Determine if this is SART
        if (isSarTarget(mmsi)) {
            updateSartPos(mmsi, positionData);
            return;
        }

        // Try to find exiting target
        VesselTarget vesselTarget = vesselTargets.get(mmsi);
        // If not exists, create and insert
        if (vesselTarget == null) {
            vesselTarget = new VesselTarget();
            vesselTarget.getSettings().setShowRoute(showIntendedRouteDefault);
            vesselTarget.setMmsi(mmsi);
            vesselTargets.put(mmsi, vesselTarget);
        }
        // Update class and pos data
        vesselTarget.setAisClass(aisClass);
        vesselTarget.setPositionData(positionData);
        // Update track
        // TODO
        // Update last received
        vesselTarget.setLastReceived(GnssTime.getInstance().getDate());
        // Update status
        vesselTarget.setStatus(AisTarget.Status.OK);
        // Publish update
        publishUpdate(vesselTarget);
    }

    /**
     * Update SART position data
     * 
     * @param mmsi
     * @param positionData
     */
    protected void updateSartPos(long mmsi, VesselPositionData positionData) {
        Date now = GnssTime.getInstance().getDate();
        // Try to find target
        SarTarget sarTarget = sarTargets.get(mmsi);
        // If not exists, create and insert
        if (sarTarget == null) {
            sarTarget = new SarTarget();
            sarTarget.setMmsi(mmsi);
            sarTarget.setFirstReceived(now);
            sarTargets.put(mmsi, sarTarget);
        }
        // Update pos data
        sarTarget.setPositionData(positionData);
        // Update last received
        sarTarget.setLastReceived(now);
        // Update status
        sarTarget.setStatus(AisTarget.Status.OK);
        // Update old
        sarTarget.setOld(false);
        // Publish update
        publishUpdate(sarTarget);
    }

    
    protected boolean isWithinRange(Position pos) {
        return true;
    }

    public final void hideAllIntendedRoutes() {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings settings = vesselTarget.getSettings();
            if (settings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
                settings.setShowRoute(false);
                publishUpdate(vesselTarget);
            }
        }
    }

    public final void showAllIntendedRoutes() {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings settings = vesselTarget.getSettings();
            if (!settings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
                settings.setShowRoute(true);
                publishUpdate(vesselTarget);
            }
        }
    }
    
    /**
     * Get list of all ships
     * 
     * @return
     */
    public final List<AisMessageExtended> getShipList() {
        List<AisMessageExtended> list = new ArrayList<AisMessageExtended>();

        if (this.getVesselTargets() != null) {
            double hdg = -1;

            for (Long key : vesselTargets.keySet()) {
                String name = " N/A";
                String dst = "N/A";
                VesselTarget currentTarget = vesselTargets.get(key);

                if (currentTarget.getStaticData() != null) {
                    name = " " + AisMessage.trimText(this.getVesselTargets().get(key).getStaticData().getName());
                }

                hdg = currentTarget.getPositionData().getCog();

                // System.out.println("Key: " + key + ", Value: " +
                // this.getVesselTargets().get(key));
                AisMessageExtended newEntry = new AisMessageExtended(name, key, hdg, dst);

                if (!vesselTargets.get(key).isGone()) {
                    list.add(newEntry);
                }
            }
        }
        return list;
    }

    public VesselTarget getOwnShip() {
        return null;
    }
    
    public final ComponentStatus getStatus() {
        return aisStatus;
    }

    public final AisStatus getAisStatus() {
        return aisStatus;
    }

    public final Map<Long, VesselTarget> getVesselTargets() {
        return vesselTargets;
    }
    
    /**
     * Get target with mmsi
     * 
     * @param mmsi
     * @return
     */
    public final AisTarget getTarget(long mmsi) {
        if (vesselTargets.containsKey(mmsi)) {
            return new VesselTarget(vesselTargets.get(mmsi));
        } else if (sarTargets.containsKey(mmsi)) {
            return new SarTarget(sarTargets.get(mmsi));
        } else if (atonTargets.containsKey(mmsi)) {
            return new AtoNTarget(atonTargets.get(mmsi));
        }
        return null;
    }


    /**
     * Update status of all targets
     */
    protected final void updateStatus() {
        Date now = GnssTime.getInstance().getDate();
        List<Long> deadTargets = new ArrayList<>();

        // Go through all vessel targets
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            if (updateTarget(vesselTarget, now)) {
                deadTargets.add(vesselTarget.getMmsi());
            }
        }

        // Remove dead targets
        for (Long mmsi : deadTargets) {
            LOG.debug("Dead target " + mmsi);
            vesselTargets.remove(mmsi);
        }

        deadTargets.clear();

        // Go through all aton targets
        for (AtoNTarget atonTarget : atonTargets.values()) {
            if (updateTarget(atonTarget, now)) {
                deadTargets.add(atonTarget.getMmsi());
            }
        }

        // Remove dead targets
        for (Long mmsi : deadTargets) {
            LOG.debug("Dead AtoN target " + mmsi);
            atonTargets.remove(mmsi);
        }

        deadTargets.clear();

        // Go through all sart targets
        for (SarTarget sarTarget : sarTargets.values()) {
            if (updateTarget(sarTarget, now)) {
                deadTargets.add(sarTarget.getMmsi());
            }
        }

        // Remove dead targets
        for (Long mmsi : deadTargets) {
            LOG.debug("Dead target " + mmsi);
            sarTargets.remove(mmsi);
        }

        deadTargets.clear();

    }
    
    /**
     * Update AIS target. Return true if the target is considered dead, not just gone
     * 
     * @param aisTarget
     * @param now
     * @return
     */
    protected final boolean updateTarget(AisTarget aisTarget, Date now) {
        if (aisTarget.isGone()) {
            // Maybe too old and needs to be deleted
            if (aisTarget.isDeadTarget(TARGET_TTL, now)) {
                return true;
            }
            return false;
        }
        if (aisTarget.hasGone(now, strictAisMode)) {
            aisTarget.setStatus(AisTarget.Status.GONE);
            publishUpdate(aisTarget);
            return false;
        }
        // Check if route information is invalid
        if (aisTarget instanceof VesselTarget) {
            if (((VesselTarget) aisTarget).checkAisRouteData()) {
                publishUpdate(aisTarget);
                return false;
            }
        }
        // Check if sart has gone old
        if (aisTarget instanceof SarTarget) {
            if (((SarTarget) aisTarget).hasGoneOld(now)) {
                publishUpdate(aisTarget);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Determine if mmsi belongs to a SART
     * 
     * @param mmsi
     * @return
     */
    public final boolean isSarTarget(long mmsi) {
        // AIS-SART transponder MMSI begins with 970
        String strMmsi = Long.toString(mmsi);
        return strMmsi.startsWith(sartMmsiPrefix);
    }
   
    /**
     * Publish the update of a target to all listeners
     * 
     * @param aisTarget
     */
    public final void publishUpdate(AisTarget aisTarget) {
        for (IAisTargetListener listener : listeners) {
            listener.targetUpdated(aisTarget);
        }
    }
    
    protected final void publishAll() {
        LOG.debug("Published all targets");
        publishAll(vesselTargets.values());
        publishAll(atonTargets.values());
        publishAll(sarTargets.values());
    }

    protected final void publishAll(Collection<? extends AisTarget> targets) {
        for (AisTarget aisTarget : targets) {
            publishUpdate((AisTarget) aisTarget);
        }
    }


    
    public final void addListener(IAisTargetListener targetListener) {
        listeners.add(targetListener);
    }

    public final void removeListener(IAisTargetListener targetListener) {
        listeners.remove(targetListener);
    }

    public final void addRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
        suggestionListeners.add(routeSuggestionListener);
    }

    public final void removeRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
        suggestionListeners.remove(routeSuggestionListener);
    }

    /**
     * Try to load AIS view from disk
     */
    public final void loadView() {
        AisStore aisStore = null;

        try (FileInputStream fileIn = new FileInputStream(AIS_VIEW_FILE);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            aisStore = (AisStore) objectIn.readObject();
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load AIS view file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(AIS_VIEW_FILE).delete();
        }

        if (aisStore == null) {
            return;
        }

        // Retrieve targets
        if (aisStore.getVesselTargets() != null) {
            vesselTargets = new ConcurrentHashMap<>(aisStore.getVesselTargets());
        }
        if (aisStore.getAtonTargets() != null) {
            atonTargets = new ConcurrentHashMap<>(aisStore.getAtonTargets());
        }
        if (aisStore.getSarTargets() != null) {
            sarTargets = new ConcurrentHashMap<>(aisStore.getSarTargets());
        }

        LOG.info("AIS handler loaded total targets: " + (vesselTargets.size() + atonTargets.size() + sarTargets.size()));

        // Update status to update old and gone (twice for old and gone)
        updateStatus();
        updateStatus();

    }

    /**
     * Save AIS view to file
     */
    public final void saveView() {
        AisStore aisStore = new AisStore();
        aisStore.setVesselTargets(vesselTargets);
        aisStore.setAtonTargets(atonTargets);
        aisStore.setSarTargets(sarTargets);

        try (FileOutputStream fileOut = new FileOutputStream(AIS_VIEW_FILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(aisStore);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Failed to save Ais view file: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        // Publish loaded targets
        Util.sleep(2000);
        publishAll();

        while (true) {
            Util.sleep(10000);
            // Update status on targets
            updateStatus();
        }
    }

    @Override
    public void findAndInit(Object obj) {
        if (nmeaSensor == null && obj instanceof NmeaSensor) {
            NmeaSensor sensor = (NmeaSensor) obj;
            if (sensor.isSensorType(SensorType.AIS)) {
                LOG.info("Found AIS sensor");
                nmeaSensor = sensor;
                nmeaSensor.addAisListener(this);
            }
        }
    }
    
    @Override
    public void findAndUndo(Object obj) {
        if (obj == nmeaSensor) {
            nmeaSensor.removeAisListener(this);
        }
    }
    
    public final class AisMessageExtended {
        public String name;
        public long MMSI;
        public double hdg;
        public String dst;

        public AisMessageExtended(String name, Long key, double hdg, String dst2) {
            this.name = name;
            this.MMSI = key;
            this.hdg = hdg;
            this.dst = dst2;
        }

    }

}
