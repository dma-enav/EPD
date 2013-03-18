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
package dk.dma.epd.shore.ais;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

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
import dk.dma.enav.model.geometry.CoordinateSystem;
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
import dk.dma.epd.common.prototype.sensor.nmea.IAisListener;
import dk.dma.epd.common.prototype.sensor.nmea.NmeaSensor;
import dk.dma.epd.common.prototype.sensor.nmea.SensorType;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.service.ais.AisServices;
import dk.dma.epd.shore.settings.ESDSettings;
import dk.dma.epd.shore.status.AisStatus;
import dk.dma.epd.shore.status.ComponentStatus;
import dk.dma.epd.shore.status.IStatusComponent;

/**
 * Class for handling incoming AIS messages on a vessel and maintainer of AIS
 * target tables
 */
public class AisHandler extends MapHandlerChild implements IAisListener,
        IStatusComponent, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AisHandler.class);

    // private static final String aisViewFile = ".aisview";
    private static final String AIS_VIEW_FILE = EPDShore.getHomePath()
            .resolve(".aisview").toString();

    // How long targets are saved without reports
    protected static final long TARGET_TTL = 60 * 60 * 1000; // One hour

    private Map<Integer, AtoNTarget> atonTargets = new HashMap<>();
    protected Map<Long, VesselTarget> vesselTargets = new HashMap<Long, VesselTarget>();
    protected Map<Long, List<PastTrackPoint>> pastTrack = new HashMap<Long, List<PastTrackPoint>>();
    protected Map<Long, SarTarget> sarTargets = new HashMap<Long, SarTarget>();
    protected List<IAisTargetListener> listeners = new ArrayList<IAisTargetListener>();

    private List<IAisRouteSuggestionListener> suggestionListeners = new ArrayList<>();
    private VesselTarget ownShip = new VesselTarget();
    private double aisRange;
    private NmeaSensor nmeaSensor;
    private AisStatus aisStatus = new AisStatus();
    private String sartMmsiPrefix = "970";

    protected boolean showIntendedRouteDefault;
    protected boolean strictAisMode = true;

    protected static final double SIMULATED_AIS_RANGE = 20;

    protected ESDSettings settings;

    // private long ownMMSI = -1;

    /**
     * Empty constructor not used
     */
    public AisHandler() {

        this.settings = EPDShore.getSettings();
        // aisRange = settings.getAisSettings().getAisSensorRange();

        sartMmsiPrefix = settings.getAisSettings().getSartPrefix();

        this.showIntendedRouteDefault = true;
        this.strictAisMode = settings.getAisSettings().isStrict();

        // this.ownMMSI = settings.getAisSettings().getOwnMMSI();

        EPDShore.startThread(this, "AisHandler");
    }

    /**
     * Get target with mmsi
     * 
     * @param mmsi
     * @return null
     */
    public synchronized AisTarget getTarget(long mmsi) {
        if (vesselTargets.containsKey(mmsi)) {
            return new VesselTarget(vesselTargets.get(mmsi));
        } else if (sarTargets.containsKey(mmsi)) {
            return new SarTarget(sarTargets.get(mmsi));
        } else if (atonTargets.containsKey(mmsi)) {
            return new AtoNTarget(atonTargets.get(mmsi));
        }
        return null;
    }

    public synchronized VesselTarget getOwnShip() {
        if (ownShip == null) {
            return null;
        }
        return new VesselTarget(ownShip);
    }

    /**
     * Add a route suggestion
     * 
     * @param routeSuggestionListener
     */
    public void addRouteSuggestionListener(
            IAisRouteSuggestionListener routeSuggestionListener) {
        suggestionListeners.add(routeSuggestionListener);
    }

    /**
     * Constructor used in connection with routes
     * 
     * @param showIntendedRouteDefault
     *            - show the ships intended routs be displays
     * @param strictAisMode
     *            - use strict Ais Mode
     */
    public AisHandler(boolean showIntendedRouteDefault, boolean strictAisMode) {

    }

    /**
     * Add listener to AisHandler
     * 
     * @param targetListener
     *            - class that is added to listeners
     */
    public synchronized void addListener(IAisTargetListener targetListener) {
        listeners.add(targetListener);
    }

    /**
     * Find and init bean function used in initializing other classes
     */
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
            // aisServices = (AisServices) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == nmeaSensor) {
            nmeaSensor.removeAisListener(this);
        }
    }

    /**
     * Get list of all ships
     * 
     * @return
     */
    public synchronized List<AisMessageExtended> getShipList() {
        List<AisMessageExtended> list = new ArrayList<AisMessageExtended>();

        if (this.getVesselTargets() != null) {
            double hdg = -1;

            for (Long key : this.getVesselTargets().keySet()) {
                String name = " N/A";
                String dst = "N/A";
                VesselTarget currentTarget = this.getVesselTargets().get(key);

                if (currentTarget.getStaticData() != null) {
                    name = " "
                            + AisMessage.trimText(this.getVesselTargets()
                                    .get(key).getStaticData().getName());
                }

                hdg = currentTarget.getPositionData().getCog();

                // System.out.println("Key: " + key + ", Value: " +
                // this.getVesselTargets().get(key));
                AisMessageExtended newEntry = new AisMessageExtended(name, key,
                        hdg, dst);

                if (!this.getVesselTargets().get(key).isGone()) {
                    list.add(newEntry);
                }
            }
        }
        return list;
    }

    /**
     * Get range of AIS
     * 
     * @return
     */
    public double getAisRange() {
        return aisRange;
    }

    /**
     * Return the aisStatus
     * 
     * @return - aisStatus
     */
    public AisStatus getAisStatus() {
        return aisStatus;
    }

    /**
     * get aisstatus as a Component status type
     */
    @Override
    public ComponentStatus getStatus() {
        return aisStatus;
    }

    /**
     * Return list of vessels
     * 
     * @return vesseltargets
     */
    public Map<Long, VesselTarget> getVesselTargets() {
        return vesselTargets;
    }

    /**
     * Function used to hide all intended routes
     */
    public synchronized void hideAllIntendedRoutes() {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings vesselTargetSettings = vesselTarget
                    .getSettings();
            if (vesselTargetSettings.isShowRoute()
                    && vesselTarget.hasIntendedRoute()) {
                vesselTargetSettings.setShowRoute(false);
                publishUpdate(vesselTarget);
            }
        }
    }

    /**
     * Determine if mmsi belongs to a SART
     * 
     * @param mmsi
     * @return startsWith
     */
    public boolean isSarTarget(long mmsi) {
        // AIS-SART transponder MMSI begins with 970
        String strMmsi = Long.toString(mmsi);
        boolean startsWith = strMmsi.startsWith(sartMmsiPrefix);
        return startsWith;
    }

    /**
     * Try to load AIS view from disk
     */
    public synchronized void loadView() {
        AisStore aisStore = null;

        try {
            FileInputStream fileIn = new FileInputStream(AIS_VIEW_FILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            aisStore = (AisStore) objectIn.readObject();
            objectIn.close();
            fileIn.close();
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
            vesselTargets = aisStore.getVesselTargets();
        }
        if (aisStore.getAtonTargets() != null) {
            atonTargets = aisStore.getAtonTargets();
        }
        if (aisStore.getSarTargets() != null) {
            sarTargets = aisStore.getSarTargets();
        }

        LOG.info("AIS handler loaded total targets: "
                + (vesselTargets.size() + atonTargets.size() + sarTargets
                        .size()));

        // Update status to update old and gone (twice for old and gone)
        updateStatus();
        updateStatus();
    }

    /**
     * Publish all vessels
     */
    protected synchronized void publishAll() {
        LOG.debug("Published all targets");
        publishAll(vesselTargets.values());
        publishAll(atonTargets.values());
        publishAll(sarTargets.values());
    }

    /**
     * Publish a specific collection
     * 
     * @param targets
     *            collection to be published
     */
    protected synchronized void publishAll(Collection<?> targets) {
        for (Object aisTarget : targets) {
            publishUpdate((AisTarget) aisTarget);
        }
    }

    /**
     * Publish the update of a target to all listeners
     * 
     * @param aisTarget
     */
    public synchronized void publishUpdate(AisTarget aisTarget) {
        for (IAisTargetListener listener : listeners) {
            listener.targetUpdated(aisTarget);
        }
    }

    /**
     * Method receiving AIS messages from AIS sensor
     */
    @Override
    public synchronized void receive(AisMessage aisMessage) {
        // Mark successful reception
        aisStatus.markAisReception();

        if (aisMessage instanceof AisPositionMessage) {
            AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
            // Create PositionData
            VesselPositionData vesselPositionData = new VesselPositionData(
                    aisPositionMessage);
            // Update or create entry
            if (vesselPositionData.hasPos()) {
                updatePos(aisPositionMessage.getUserId(), vesselPositionData,
                        VesselTarget.AisClass.A);
            }
        } else if (aisMessage instanceof AisMessage18) {
            AisMessage18 posMessage = (AisMessage18) aisMessage;
            VesselPositionData vesselPositionData = new VesselPositionData(
                    posMessage);
            // Update or create entry
            if (vesselPositionData.hasPos()) {
                updatePos(posMessage.getUserId(), vesselPositionData,
                        VesselTarget.AisClass.B);
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
                LOG.error("Failed to get application specific message: "
                        + e.getMessage());
                return;
            }
            // Handle broadcast messages
            if (aisMessage.getMsgId() == 8 && appMessage != null) {
                // Handle route information
                if (appMessage.getDac() == BroadcastIntendedRoute.DAC
                        && appMessage.getFi() == BroadcastIntendedRoute.FI) {
                    BroadcastIntendedRoute intendedRoute = (BroadcastIntendedRoute) appMessage;
                    // LOG.info("BroadcastRouteInformation: " +
                    // routeInformation);
                    // Handle intended route
                    updateIntendedRoute(aisMessage.getUserId(),
                            new AisIntendedRoute(intendedRoute));
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
                if (appMessage.getDac() == RouteSuggestion.DAC
                        && appMessage.getFi() == RouteSuggestion.FI) {
                    RouteSuggestion routeSuggestion = (RouteSuggestion) appMessage;
                    LOG.info("RouteSuggestion: " + routeSuggestion);
                    AisAdressedRouteSuggestion addressedRouteSuggestion = new AisAdressedRouteSuggestion(
                            routeSuggestion);
                    addressedRouteSuggestion.setSender(aisMessage.getUserId());
                    for (IAisRouteSuggestionListener suggestionListener : suggestionListeners) {
                        suggestionListener
                                .receiveRouteSuggestion(addressedRouteSuggestion);
                    }
                    // Acknowledge the reception
                    if (suggestionListeners.size() > 0) {
                        // aisServices.acknowledgeRouteSuggestion(msg6,
                        // routeSuggestion);
                    }
                }
            }
        }
    }

    /**
     * Remove a class from being a listener
     * 
     * @param targetListener
     *            target to be removed
     */
    public synchronized void removeListener(IAisTargetListener targetListener) {
        listeners.remove(targetListener);
    }

    /**
     * Run method used when creating the Thread
     */
    @Override
    public void run() {

        // Publish loaded targets
        EPDShore.sleep(2000);
        publishAll();

        while (true) {
            EPDShore.sleep(10000);
            // Update status on targets
            updateStatus();

            // List<AisMessageExtended> shipList = getShipList();
            //
            // System.out.println("Recieving AIS:");
            // for (int i = 0; i < shipList.size(); i++) {
            // System.out.println("ID " + shipList.get(i).MMSI + " : "
            // + shipList.get(i).name);
            // }
            // System.out.println("AIS Recieved");

        }
    }

    /**
     * Save AIS view to file
     */
    public synchronized void saveView() {
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

    /**
     * Show the all intended routes
     */
    public synchronized void showAllIntendedRoutes() {
        for (VesselTarget vesselTarget : vesselTargets.values()) {
            VesselTargetSettings vesselTargetSettings = vesselTarget
                    .getSettings();
            if (!vesselTargetSettings.isShowRoute()
                    && vesselTarget.hasIntendedRoute()) {
                vesselTargetSettings.setShowRoute(true);
                publishUpdate(vesselTarget);
            }
        }
    }

    /**
     * Update AtoN target
     * 
     * @param msg21
     */
    protected synchronized void updateAton(AisMessage21 msg21) {
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
     * Update class b vessel statics
     * 
     * @param msg24
     */
    protected synchronized void updateClassBStatics(AisMessage24 msg24) {
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
     * Update intended route of vessel target
     * 
     * @param mmsi
     * @param routeData
     */
    protected synchronized void updateIntendedRoute(long mmsi,
            AisIntendedRoute routeData) {
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
     * Update vessel target position data
     * 
     * @param mmsi
     * @param positionData
     * @param aisClass
     */
    protected synchronized void updatePos(long mmsi,
            VesselPositionData positionData, VesselTarget.AisClass aisClass) {
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
        
        //Add past track
        if (pastTrack.containsKey(mmsi)){
            
            //Should it add the key?
            Position prevPos = pastTrack.get(mmsi).get(pastTrack.get(mmsi).size() - 1).getPosition();
            
            //In km, how often should points be saved? 1km?
                        if (prevPos.distanceTo(positionData.getPos(), CoordinateSystem.CARTESIAN) > 100){
                         
//                            System.out.println("Target " + mmsi + " has moved more than 50 since last");
                            pastTrack.get(mmsi).add(new PastTrackPoint(new Date(), positionData.getPos()));
                        }
                        
//            System.out.println(prevPos.distanceTo(positionData.getPos(), CoordinateSystem.CARTESIAN));
            
            
        }else{
            pastTrack.put(mmsi, new ArrayList<PastTrackPoint>());
            pastTrack.get(mmsi).add(new PastTrackPoint(new Date(), positionData.getPos()));
        }
        
        // Publish update
        publishUpdate(vesselTarget);
    }
    
    
    public Map<Long, List<PastTrackPoint>> getPastTrack() {
        return pastTrack;
    }

    /**
     * Update SART position data
     * 
     * @param mmsi
     * @param positionData
     */
    protected synchronized void updateSartPos(long mmsi,
            VesselPositionData positionData) {
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
     * Update SART statics
     * 
     * @param mmsi
     * @param staticData
     */
    protected synchronized void updateSartStatics(long mmsi,
            VesselStaticData staticData) {
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
     * Update vessel target statics
     * 
     * @param mmsi
     * @param staticData
     */
    protected synchronized void updateStatics(long mmsi,
            VesselStaticData staticData) {
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
     * Update status of all targets
     */
    protected synchronized void updateStatus() {
        Date now = GnssTime.getInstance().getDate();
        List<Long> deadTargets = new ArrayList<Long>();

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
     * Update AIS target. Return true if the target is considered dead, not just
     * gone
     * 
     * @param aisTarget
     * @param now
     * @return
     */
    protected boolean updateTarget(AisTarget aisTarget, Date now) {
        if (aisTarget.isGone()) {
            // Maybe too old and needs to be deleted
            if (aisTarget.isDeadTarget(TARGET_TTL, now)) {
                return true;
            }
            return false;
        }

        if (aisTarget.hasGone(now, EPDShore.getSettings().getAisSettings()
                .isStrict())) {
            aisTarget.setStatus(AisTarget.Status.GONE);
            publishUpdate(aisTarget);
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

    @Override
    public synchronized void receiveOwnMessage(AisMessage aisMessage) {
        // Determine if our vessel has changed. Clear if so.
        if (ownShip != null) {
            if (aisMessage.getUserId() != ownShip.getMmsi()) {
                ownShip = new VesselTarget();
            }
        }

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

    /**
     * AisMessageExtended class used in storing messages from the AisTransponder
     * 
     */
    public class AisMessageExtended {
        public String name;
        public long MMSI;
        public double hdg;
        public String dst;

        /**
         * Datastructure for the extended ais messages
         * 
         * @param name
         *            - name of ship
         * @param key
         *            - mmsi of ship
         * @param hdg
         *            - heading of ship
         * @param dst
         *            - distance to ship
         */
        public AisMessageExtended(String name, Long key, double hdg, String dst) {
            this.name = name;
            this.MMSI = key;
            this.hdg = hdg;
            this.dst = dst;
        }
    }

}
