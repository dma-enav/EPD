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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisBinaryMessage;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisMessage21;
import dk.dma.ais.message.AisMessage24;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.AisMessage6;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.message.binary.AisApplicationMessage;
import dk.dma.ais.message.binary.BroadcastIntendedRoute;
import dk.dma.ais.message.binary.RouteSuggestion;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.ais.AisIntendedRoute;
import dk.dma.epd.common.prototype.ais.AisStore;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.AtoNTarget;
import dk.dma.epd.common.prototype.ais.IAisRouteSuggestionListener;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.nmea.IAisListener;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.SensorType;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.communication.ais.AisServices;
import dk.dma.epd.ship.status.AisStatus;

/**
 * Class for handling incoming AIS messages and maintainer of AIS target tables
 */
@ThreadSafe
public class AisHandler extends dk.dma.epd.common.prototype.ais.AisHandler implements IAisListener, IStatusComponent, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AisHandler.class);

    // private static final String aisViewFile = ".aisview";
    private static final String AIS_VIEW_FILE = EPDShip.getHomePath().resolve(".aisview").toString();

    // How long targets are saved without reports
    private static final long TARGET_TTL = 60 * 60 * 1000; // One hour

    private ConcurrentHashMap<Integer, AtoNTarget> atonTargets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, VesselTarget> vesselTargets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, SarTarget> sarTargets = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<IAisTargetListener> listeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<IAisRouteSuggestionListener> suggestionListeners = new CopyOnWriteArrayList<>();

    private volatile VesselTarget ownShip = new VesselTarget();
    private final double aisRange;
    private NmeaSensor nmeaSensor;
    private AisServices aisServices;
    private AisStatus aisStatus = new AisStatus();
    private final String sartMmsiPrefix;

    public AisHandler() {
        aisRange = EPDShip.getSettings().getSensorSettings().getAisSensorRange();
        sartMmsiPrefix = EPDShip.getSettings().getAisSettings().getSartPrefix();
        EPDShip.startThread(this, "AisHandler");
    }

    /**
     * Get target with mmsi
     * 
     * @param mmsi
     * @return
     */
    public AisTarget getTarget(long mmsi) {
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
     * Method receiving AIS messages from AIS sensor
     */
    @Override
    public void receive(AisMessage aisMessage) {
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
        } else if (aisMessage instanceof AisBinaryMessage) {
            AisBinaryMessage binaryMessage = (AisBinaryMessage) aisMessage;
            AisApplicationMessage appMessage;
            try {
                appMessage = binaryMessage.getApplicationMessage();
            } catch (SixbitException e) {
                LOG.error("Failed to get application specific message: " + e.getMessage());
                return;
            }
            // Handle broadcast messages
            if (aisMessage.getMsgId() == 8 && appMessage != null) {
                // Handle route information
                if (appMessage.getDac() == BroadcastIntendedRoute.DAC && appMessage.getFi() == BroadcastIntendedRoute.FI) {
                    BroadcastIntendedRoute intendedRoute = (BroadcastIntendedRoute) appMessage;
                    // LOG.info("BroadcastRouteInformation: " +
                    // routeInformation);
                    // Handle intended route
                    updateIntendedRoute(aisMessage.getUserId(), new AisIntendedRoute(intendedRoute));
                }
            }
            // Handle addressed messages
            if (aisMessage.getMsgId() == 6 && appMessage != null) {

                // Check if for own ship
                AisMessage6 msg6 = (AisMessage6) aisMessage;
                if (ownShip.getMmsi() != msg6.getDestination()) {
                    return;
                }

                // Handle adressed route information
                if (appMessage.getDac() == RouteSuggestion.DAC && appMessage.getFi() == RouteSuggestion.FI) {
                    RouteSuggestion routeSuggestion = (RouteSuggestion) appMessage;
                    LOG.info("RouteSuggestion: " + routeSuggestion);
                    AisAdressedRouteSuggestion addressedRouteSuggestion = new AisAdressedRouteSuggestion(routeSuggestion);
                    addressedRouteSuggestion.setSender(aisMessage.getUserId());
                    for (IAisRouteSuggestionListener suggestionListener : suggestionListeners) {
                        suggestionListener.receiveRouteSuggestion(addressedRouteSuggestion);
                    }
                    // Acknowledge the reception
                    if (suggestionListeners.size() > 0) {
                        aisServices.acknowledgeRouteSuggestion(msg6, routeSuggestion);
                    }
                }
            }
        }
    }

    public void hideAllIntendedRoutes() {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings settings = vesselTarget.getSettings();
            if (settings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
                settings.setShowRoute(false);
                publishUpdate(vesselTarget);
            }
        }
    }

    public void showAllIntendedRoutes() {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings settings = vesselTarget.getSettings();
            if (!settings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
                settings.setShowRoute(true);
                publishUpdate(vesselTarget);
            }
        }
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
            ownShip.setLastReceived(GnssTime.getInstance().getDate());
            ownShip.setMmsi(aisMessage.getUserId());
        }

    }

    /**
     * Update AtoN target
     * 
     * @param msg21
     */
    private void updateAton(AisMessage21 msg21) {
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
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    private void updateIntendedRoute(long mmsi, AisIntendedRoute routeData) {
        // Try to find exiting target
        VesselTarget vesselTarget = vesselTargets.get(mmsi);
        // If not exists, wait for it to be created by position report
        if (vesselTarget == null) {
            return;
        }
        // Update intented route
        vesselTarget.setAisRouteData(routeData);
        publishUpdate(vesselTarget);
    }

    /**
     * Update vessel target statics
     * 
     * @param mmsi
     * @param staticData
     */
    private void updateStatics(long mmsi, VesselStaticData staticData) {
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
    private void updateClassBStatics(AisMessage24 msg24) {
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
    private void updateSartStatics(long mmsi, VesselStaticData staticData) {
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
     * Determine if mmsi belongs to a SART
     * 
     * @param mmsi
     * @return
     */
    public boolean isSarTarget(long mmsi) {
        // AIS-SART transponder MMSI begins with 970
        String strMmsi = Long.toString(mmsi);
        return strMmsi.startsWith(sartMmsiPrefix);
    }

    /**
     * Update vessel target position data
     * 
     * @param mmsi
     * @param positionData
     * @param aisClass
     */
    private void updatePos(long mmsi, VesselPositionData positionData, VesselTarget.AisClass aisClass) {
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
            vesselTarget.getSettings().setShowRoute(EPDShip.getSettings().getAisSettings().isShowIntendedRouteByDefault());
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
    private void updateSartPos(long mmsi, VesselPositionData positionData) {
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

    /**
     * Determine if position is within range
     * 
     * @param pos
     * @return
     */
    private boolean isWithinRange(Position pos) {
        if (getAisRange() <= 0) {
            return true;
        }
        GpsData gpsData = EPDShip.getGpsHandler().getCurrentData();
        if (gpsData == null) {
            return false;
        }
        double distance = gpsData.getPosition().rhumbLineDistanceTo(pos) / 1852.0;
        return distance <= aisRange;
    }

    /**
     * Publish the update of a target to all listeners
     * 
     * @param aisTarget
     */
    public void publishUpdate(AisTarget aisTarget) {
        for (IAisTargetListener listener : listeners) {
            listener.targetUpdated(aisTarget);
        }
    }

    private void publishAll() {
        LOG.debug("Published all targets");
        publishAll(vesselTargets.values());
        publishAll(atonTargets.values());
        publishAll(sarTargets.values());
    }

    private void publishAll(Collection<? extends AisTarget> targets) {
        for (AisTarget aisTarget : targets) {
            publishUpdate((AisTarget) aisTarget);
        }
    }

    public void addListener(IAisTargetListener targetListener) {
        listeners.add(targetListener);
    }

    public void removeListener(IAisTargetListener targetListener) {
        listeners.remove(targetListener);
    }

    public void addRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
        suggestionListeners.add(routeSuggestionListener);
    }

    public void removeRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
        suggestionListeners.remove(routeSuggestionListener);
    }

    @Override
    public VesselTarget getOwnShip() {
        synchronized (ownShip) {
            return ownShip;
        }
    }

    /**
     * Update status of all targets
     */
    private void updateStatus() {
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
    private boolean updateTarget(AisTarget aisTarget, Date now) {
        if (aisTarget.isGone()) {
            // Maybe too old and needs to be deleted
            if (aisTarget.isDeadTarget(TARGET_TTL, now)) {
                return true;
            }
            return false;
        }
        if (aisTarget.hasGone(now, EPDShip.getSettings().getAisSettings().isStrict())) {
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

    public double getAisRange() {
        return aisRange;
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
        } else if (obj instanceof AisServices) {
            aisServices = (AisServices) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == nmeaSensor) {
            nmeaSensor.removeAisListener(this);
        }
    }

    @Override
    public ComponentStatus getStatus() {
        return aisStatus;
    }

    public AisStatus getAisStatus() {
        return aisStatus;
    }

    public Map<Long, VesselTarget> getVesselTargets() {
        return vesselTargets;
    }

    public List<AisMessageExtended> getShipList() {
        List<AisMessageExtended> list = new ArrayList<>();

        if (vesselTargets != null) {

            for (Long key : this.getVesselTargets().keySet()) {
                VesselTarget currentTarget = vesselTargets.get(key);
                AisMessageExtended newEntry = this.getShip(currentTarget);

                if (!vesselTargets.get(key).isGone()) {
                    list.add(newEntry);
                }
            }
        }
        return list;
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
        if (!EPDShip.getGpsHandler().getCurrentData().isBadPosition()) {
            ownPosition = EPDShip.getGpsHandler().getCurrentData().getPosition();

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

        // Retrieve own ship (well not for now, may cause troubles in regard to
        // communication)
        // ownShip = aisStore.getOwnShip();

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
        ownShip.setPositionData(null);
        aisStore.setOwnShip(ownShip);

        try (FileOutputStream fileOut = new FileOutputStream(AIS_VIEW_FILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(aisStore);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Failed to save Ais view file: " + e.getMessage());
        }
    }

    public class AisMessageExtended {
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
