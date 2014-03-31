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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.util.ConcurrentHashSet;
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
import dk.dma.epd.common.prototype.sensor.nmea.IAisSensorListener;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.status.AisStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Util;

public abstract class AisHandlerCommon extends MapHandlerChild implements Runnable, IAisSensorListener, IStatusComponent {
    
    private static final Logger LOG = LoggerFactory.getLogger(AisHandlerCommon.class);
    
    protected static final String AIS_VIEW_FILE = EPD.getInstance().getHomePath().resolve(".aisview").toString();

    // How long targets are saved without reports
    protected static final long TARGET_TTL = 60 * 60 * 1000; // One hour
    
    protected ConcurrentHashMap<Integer, AtoNTarget> atonTargets = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, VesselTarget> vesselTargets = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<Long, SarTarget> sarTargets = new ConcurrentHashMap<>();

    protected CopyOnWriteArrayList<IAisTargetListener> listeners = new CopyOnWriteArrayList<>();
    
    protected AisStatus aisStatus = new AisStatus();
    protected final boolean strictAisMode;
    protected final String sartMmsiPrefix;
    protected final Set<String> simulatedSartMmsi = new ConcurrentHashSet<>();
    protected final int pastTrackMaxTime;       // NB: In minutes
    protected final int pastTrackDisplayTime;   // NB: In minutes
    protected final int pastTrackMinDist;       // NB: In meters
    

    /**
     * Constructor
     * @param aisSettings
     */
    public AisHandlerCommon(AisSettings aisSettings) {
        sartMmsiPrefix = aisSettings.getSartPrefix();
        Collections.addAll(simulatedSartMmsi, aisSettings.getSimulatedSartMmsi());
        strictAisMode = aisSettings.isStrict();
        this.pastTrackMaxTime = aisSettings.getPastTrackMaxTime();
        this.pastTrackDisplayTime = aisSettings.getPastTrackDisplayTime();
        this.pastTrackMinDist = aisSettings.getPastTrackMinDist();
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
    
    /**
     * Clears all AIS targets
     */
    public synchronized void clearAisTargets() {
        atonTargets.clear();
        vesselTargets.clear();
        sarTargets.clear();
        publishAll();
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
        atonTarget.setLastReceived(PntTime.getInstance().getDate());
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
        VesselTarget vesselTarget = vesselTargets.get( (long) msg24.getUserId());
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
     * Update the vessel or SAR target with position data
     * @param mobileTarget the target to update
     * @param positionData the position data
     */
    protected void updateMobileTargetPos(MobileTarget mobileTarget, VesselPositionData positionData) {

        // Update class pos data
        mobileTarget.setPositionData(positionData);
        
        // Update past-track
        mobileTarget.addPastTrackPosition(positionData.getPos());
        
        // Update last received
        mobileTarget.setLastReceived(PntTime.getInstance().getDate());
        
        // Update status
        mobileTarget.setStatus(AisTarget.Status.OK);
        
        // Publish update
        publishUpdate(mobileTarget);
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
            vesselTarget.getSettings().setPastTrackDisplayTime(pastTrackDisplayTime);
            vesselTarget.getSettings().setPastTrackMinDist(pastTrackMinDist);
            vesselTarget.setMmsi(mmsi);
            vesselTargets.put(mmsi, vesselTarget);
        }
        // Update class 
        vesselTarget.setAisClass(aisClass);
        // Update target from position data
        updateMobileTargetPos(vesselTarget, positionData);
    }

    /**
     * Update SART position data
     * 
     * @param mmsi
     * @param positionData
     */
    protected void updateSartPos(long mmsi, VesselPositionData positionData) {
        // Try to find target
        SarTarget sarTarget = sarTargets.get(mmsi);
        // If not exists, create and insert
        if (sarTarget == null) {
            sarTarget = new SarTarget();
            sarTarget.setMmsi(mmsi);
            sarTarget.getSettings().setPastTrackDisplayTime(pastTrackDisplayTime);
            sarTarget.getSettings().setPastTrackMinDist(pastTrackMinDist);
            sarTarget.setFirstReceived(PntTime.getInstance().getDate());
            sarTargets.put(mmsi, sarTarget);
        }
        // Update old
        sarTarget.setOld(false);
        // Update target from position data
        updateMobileTargetPos(sarTarget, positionData);
    }

    
    /**
     * Should be implemented by specialized versions of the AisHandlerCommon class
     * 
     * @param pos the position to check
     * @return if the position is within range
     */
    protected abstract boolean isWithinRange(Position pos);
    
    /**
     * Shows or hides past tracks for all vessel and sar targets
     * @param show whether to show or hide past tracks
     */
    public void setShowAllPastTracks(boolean show) {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings settings = vesselTarget.getSettings();
            if (show != settings.isShowPastTrack()) {
                settings.setShowPastTrack(show);
                publishUpdate(vesselTarget);
            }
        }
        for (SarTarget sarTarget : sarTargets.values()) {
            VesselTargetSettings settings = sarTarget.getSettings();
            if (show != settings.isShowPastTrack()) {
                settings.setShowPastTrack(show);
                publishUpdate(sarTarget);
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

        double hdg = -1;

        for (Long key : vesselTargets.keySet()) {
            String name = " N/A";
            String dst = "N/A";
            VesselTarget currentTarget = vesselTargets.get(key);

            if (currentTarget.getStaticData() != null) {
                name = " " + getVesselTarget(key).getStaticData().getTrimmedName();
            }

            hdg = currentTarget.getPositionData().getCog();

            // System.out.println("Key: " + key + ", Value: " +
            // this.getVesselTargets().get(key));
            AisMessageExtended newEntry = new AisMessageExtended(name, key, hdg, dst);

            if (!vesselTargets.get(key).isGone()) {
                list.add(newEntry);
            }
        }
        return list;
    }
    
    /**
     * Returns the list of mobile (vessel + sar) targets. Optionally specify a required status.
     * 
     * @param status if not null, the targets must have this status
     * @return the list of targets.
     */
    public final List<MobileTarget> getMobileTargets(AisTarget.Status status) {
        
        List<MobileTarget> mobileTargets = new ArrayList<>(vesselTargets.size() + sarTargets.size());
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            if (status == null || status == vesselTarget.status) {
                mobileTargets.add(vesselTarget);
            }
        }
        for (SarTarget sarTarget : sarTargets.values()) {
            if (status == null || status == sarTarget.status) {
                mobileTargets.add(sarTarget);
            }
        }
        return mobileTargets;
    }

    public final ComponentStatus getStatus() {
        return aisStatus;
    }

    public final AisStatus getAisStatus() {
        return aisStatus;
    }

    public final VesselTarget getVesselTarget(Long mmsi) {
        return vesselTargets.get(mmsi);
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
        } else if (atonTargets.containsKey((int) mmsi)) {
            return new AtoNTarget(atonTargets.get((int) mmsi));
        }
        return null;
    }

    /**
     * Update status of all targets
     */
    protected final void updateStatus() {
        Date now = PntTime.getInstance().getDate();
        List<Long> deadTargets = new ArrayList<>();

        // Go through all vessel targets
        updateStatus(vesselTargets, deadTargets, now, false);

        // Go through all aton targets
        updateStatus(atonTargets, deadTargets, now, true);

        // Go through all sart targets
        updateStatus(sarTargets, deadTargets, now, false);
        
        // Allow sub-classes to perform a periodic update
        updatePeriodic();
    }
    
    /**
     * Sub-classes can override to perform periodic updates.
     * Called every 10 seconds.
     */
    protected void updatePeriodic() {
    }
    
    /**
     * Update the list of AIS targets and purge the dead targets
     * @param aisTargets the list of AIS targets to update
     * @param deadTargets use for collecting dead targets
     * @param now the current time
     * @param intKey cater with atonTargets, which use Integer as a key
     */
    protected final <T extends AisTarget, N extends Number> void updateStatus(Map<N, T> aisTargets, List<Long> deadTargets, Date now, boolean intKey) {
        // Go through the list of AIS targets
        for (T aisTarget : aisTargets.values()) {
            if (updateTarget(aisTarget, now)) {
                deadTargets.add(aisTarget.getMmsi());
            }
        }

        // Remove dead targets
        for (Long mmsi : deadTargets) {
            LOG.debug("Dead target " + mmsi);
            // cater with atonTargets, which use Integer as a key
            aisTargets.remove(intKey ? Integer.valueOf((int)mmsi.longValue()) : mmsi);
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
        // Clean up old past-track points of the mobile targets
        if (aisTarget instanceof MobileTarget) {
            // Convert from minutes to seconds
            ((MobileTarget)aisTarget).getPastTrackData().cleanup(60*pastTrackMaxTime); 
        }
        
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
        String strMmsi = Long.toString(mmsi);
        
        // Check if we have simulated SarTargets.
        // These are configured in the settings.properties file, by specifying
        // a comma-separated list of vessel mmsi for the "ais.simulatedSartMmsi" property.
        if (simulatedSartMmsi.contains(strMmsi)) {
            return true;
        }
                    
        // AIS-SART transponder MMSI begins with 970
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

    /**
     * Get AisMessageExtended for a single VesselTarget
     * 
     * @param currentTarget
     * @param currentData the current PntData
     * @return
     */
    public AisMessageExtended getShip(VesselTarget currentTarget, PntData currentData) {
        String name = " N/A";
        String dst = "N/A";
        Position ownPosition;
        double hdg = -1;
        Position targetPosition = null;

        if (currentTarget.getStaticData() != null) {
            name = " " + currentTarget.getStaticData().getTrimmedName();
        }
        if (!currentData.isBadPosition()) {
            ownPosition = currentData.getPosition();

            if (currentTarget.getPositionData().getPos() != null) {
                targetPosition = currentTarget.getPositionData().getPos();
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                dst = nf.format(Converter.metersToNm(ownPosition.rhumbLineDistanceTo(targetPosition))) + " NM";

            }
        }
        hdg = currentTarget.getPositionData().getCog();

        return new AisMessageExtended(name, currentTarget.getMmsi(), hdg, dst);
    }
        
    /**
     * Try to load AIS view from disk
     */
    public void loadView() {
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
    public void saveView() {
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
