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
package dk.dma.epd.shore.voct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationReply;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;
import dk.dma.epd.common.prototype.service.IIntendedRouteListener;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.layers.voct.VoctLayerTracking;
import dk.dma.epd.shore.service.IntendedRouteHandler;
import dk.dma.epd.shore.service.VoctHandler;
import dk.dma.epd.shore.voct.SRU.sru_status;
import dma.voct.VOCTReplyStatus;

public class SRUManager extends MapHandlerChild implements Runnable,
        IIntendedRouteListener {

    private VOCTManager voctManager;
    private VoctHandler voctHandler;
    private static final String SRU_FILE = EPD.getInstance().getHomePath()
            .resolve(".srus").toString();
    private static final Logger LOG = LoggerFactory.getLogger(SRUManager.class);

    // private List<SRU> srus = new LinkedList<SRU>();
    private Map<Long, SRU> srus = new HashMap<Long, SRU>();
    private LinkedHashMap<Long, SRUCommunicationObject> sRUCommunication = new LinkedHashMap<Long, SRUCommunicationObject>();
    private VoctLayerTracking voctLayerTracking;

    private CopyOnWriteArrayList<SRUUpdateListener> listeners = new CopyOnWriteArrayList<>();

    public SRUManager() {
        EPDShore.startThread(this, "sruManager");
    }

    public void setVoctTrackingLayer(VoctLayerTracking layer) {
        this.voctLayerTracking = layer;
    }

    public void notifyListeners(SRUUpdateEvent e, long mmsi) {
        for (SRUUpdateListener listener : listeners) {
            listener.sruUpdated(e, mmsi);
        }

        // Persist update VOCT info
        saveToFile();
    }

    public synchronized void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(SRU_FILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
            objectOut.writeObject(srus);
        } catch (IOException e) {
            LOG.error("Failed to save VOCT data: " + e.getMessage());
        }
    }

    public void addListener(SRUUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SRUUpdateListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {

        // Maintanaince routines

        // while (true) {
        // EPDShore.sleep(1000);
        // updateSRUsStatus();
        // maintainAvailableSRUs();
        // Maintain list of available SRUs
        // }

    }

    public void updateSRUsStatus() {
        System.out.println("UPDATE SRU STATUS");
        for (int i = 0; i < voctHandler.getVoctMessageList().size(); i++) {
System.out.println("ID is " + voctHandler.getVoctMessageList().get(i)
                    .getRemoteId().getId());
            long mmsi = Long.parseLong(voctHandler.getVoctMessageList().get(i)
                    .getRemoteId().getId());
                    
//                    .getId().toString().split("//")[1]);

            System.out.println("Is mmsi " + mmsi + " a SRU?");
            System.out.println(srus);
            if (srus.containsKey(mmsi)) {
                SRU sru = srus.get(mmsi);

                // Change the status
                if (sru.getStatus() != sru_status.ACCEPTED
                        && sru.getStatus() != sru_status.AVAILABLE
                        && sru.getStatus() != sru_status.INVITED) {
                    // System.out.println("Updating status WHY");
                    sru.setStatus(sru_status.AVAILABLE);
                }

            }

        }

    }

    public void setSRUStatus(long mmsi, sru_status status) {

        // What if we remove a SRU
        srus.get(mmsi).setStatus(status);
        updateSRUsStatus();
        notifyListeners(SRUUpdateEvent.SRU_STATUS_CHANGED, mmsi);
    }

    public void sruSRUStatus(long mmsi, VoctMsgStatus status) {

        if (srus.containsKey(mmsi)) {
            SRU sru = srus.get(mmsi);
            sru.setVoctMsgStatus(status);
            updateSRUsStatus();
            notifyListeners(SRUUpdateEvent.SRU_STATUS_CHANGED, mmsi);
        }

    }

    public void sruSRUStatus(long mmsi, CloudMessageStatus status) {

        if (srus.containsKey(mmsi)) {
            SRU sru = srus.get(mmsi);

            if (status == CloudMessageStatus.RECEIVED_BY_CLOUD){
                if (sru.getCloudStatus() == CloudMessageStatus.RECEIVED_BY_CLIENT){
                    System.out.println("Not overwriting");
                    return;
                }
            }
            
            sru.setCloudStatus(status);
            sru.setVoctMsgStatus(VoctMsgStatus.UNKNOWN);
            updateSRUsStatus();
            notifyListeners(SRUUpdateEvent.SRU_STATUS_CHANGED, mmsi);
        }

    }

    public int getAvailableSRUS() {
        return sRUCommunication.size();
    }

    public void handleSRUReply(long mmsi, VOCTReplyStatus voctReplyStatus) {

        System.out.println("Handling SRU Reply!");

        if (srus.containsKey(mmsi)) {
            SRU sru = null;
            sru = srus.get(mmsi);

            VOCTReplyStatus status = voctReplyStatus;

            switch (status) {
            // If its been accepted we create an entry in the hashmap, should we
            // overwrite the old one?
            // Remove old one, put new one
            case ACCEPTED:
                
                sru.setStatus(sru_status.ACCEPTED);
                if (sRUCommunication.containsKey(mmsi)) {
                    sRUCommunication.remove(mmsi);
                }
                sRUCommunication.put(mmsi, new SRUCommunicationObject(sru));
                sRUCommunication.get(mmsi).setLastMessageRecieved(new Date());
                // Notify voctmanager to paint efffort allocation area for SRU i
                
                if (voctLayerTracking!= null){
                    voctLayerTracking.drawEffectiveArea(sru.getMmsi());    
                }
                

                // System.out.println("SRU status set to acceptd");
                // System.out.println("Running through all SRUS");
                // for (int i = 0; i < srus.size(); i++) {
                // System.out.println(srus.get(i).getStatus());
                // }

                // voctManager.

                notifyListeners(SRUUpdateEvent.SRU_ACCEPT, mmsi);
                break;

            // If theres an old entry, remove it
            case REJECTED:
                sru.setStatus(sru_status.DECLINED);
                if (sRUCommunication.containsKey(mmsi)) {
                    sRUCommunication.remove(mmsi);
                }

                // Remove if we previously had one
                voctLayerTracking.removeEffectiveArea(sru.getMmsi());

                notifyListeners(SRUUpdateEvent.SRU_REJECT, mmsi);

                break;
            case WITHDRAWN:
                sru.setStatus(sru_status.DECLINED);
                if (sRUCommunication.containsKey(mmsi)) {
                    sRUCommunication.remove(mmsi);
                }

                // Remove if we previously had one
                voctLayerTracking.removeEffectiveArea(sru.getMmsi());

                notifyListeners(SRUUpdateEvent.SRU_REJECT, mmsi);
            default:
                sru.setStatus(sru_status.UNKNOWN);

                break;
            }
        }

    }

    public List<Long> cancelAllSRU() {

        List<Long> srusList = new ArrayList<Long>();

        for (Iterator<Long> it = sRUCommunication.keySet().iterator(); it
                .hasNext();) {
            srusList.add(it.next());
        }

        for (Entry<Long, SRU> entry : srus.entrySet()) {
            SRU sru = entry.getValue();

            if (sRUCommunication.containsKey(sru.getMmsi())) {
                sru.setStatus(sru_status.UNKNOWN);
            }

        }

        sRUCommunication.clear();
        return srusList;

    }

    public void handleSRUReply(VOCTCommunicationReply reply) {

        // System.out.println("Handling SRU Reply!");

        if (srus.containsKey(reply.getMmsi())) {
            SRU sru = null;
            sru = srus.get(reply.getMmsi());

            // SRU_STATUS status = reply.getStatus();
            VoctMsgStatus status = VoctMsgStatus.ACCEPTED;

            switch (status) {
            // If its been accepted we create an entry in the hashmap, should we
            // overwrite the old one?
            // Remove old one, put new one
            case ACCEPTED:
                sru.setStatus(sru_status.ACCEPTED);
                if (sRUCommunication.containsKey(reply.getMmsi())) {
                    sRUCommunication.remove(reply.getMmsi());
                }
                sRUCommunication.put(reply.getMmsi(),
                        new SRUCommunicationObject(sru));

                // Notify voctmanager to paint efffort allocation area for SRU i
                voctLayerTracking.drawEffectiveArea(sru.getMmsi());

                // System.out.println("SRU status set to acceptd");
                // System.out.println("Running through all SRUS");
                // for (int i = 0; i < srus.size(); i++) {
                // System.out.println(srus.get(i).getStatus());
                // }

                notifyListeners(SRUUpdateEvent.SRU_ACCEPT, reply.getMmsi());
                break;

            // If theres an old entry, remove it
            case REJECTED:
                sru.setStatus(sru_status.DECLINED);
                if (sRUCommunication.containsKey(reply.getMmsi())) {
                    sRUCommunication.remove(reply.getMmsi());
                }

                // Remove if we previously had one
                voctLayerTracking.removeEffectiveArea(sru.getMmsi());

                notifyListeners(SRUUpdateEvent.SRU_REJECT, reply.getMmsi());

                break;
            default:
                sru.setStatus(sru_status.UNKNOWN);

                break;
            }

        }

    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
        }

        if (obj instanceof VoctHandler) {
            voctHandler = (VoctHandler) obj;
        }
        if (obj instanceof IntendedRouteHandler) {
            ((IntendedRouteHandler) obj).addListener(this);
        }
    }

    public void toggleSRUVisiblity(int i, boolean visible) {
        srus.get(getSRUsAsList()[i].getMmsi()).setVisible(visible);
        voctManager.toggleSRUVisibility(i, visible);

        notifyListeners(SRUUpdateEvent.SRU_VISIBILITY_CHANGED, i);

    }

    public int getSRUCount() {
        synchronized (srus) {
            return srus.size();
        }
    }

    /**
     * @return the sru
     */
    public Map<Long, SRU> getSRUs() {
        return srus;
    }

    public void addSRU(SRU sru) {
        synchronized (srus) {
            srus.put(sru.getMmsi(), sru);
            notifyListeners(SRUUpdateEvent.SRU_ADDED, srus.size());
            // saveToFile();
        }

    }

    public SRU[] getSRUsAsList() {
        // (SRU[])
        return srus.values().toArray(new SRU[0]);
    }

    public void removeSRU(int i) {
        if (srus.size() >= i + 1) {

            synchronized (srus) {

                long sruMmsi = getSRUsAsList()[i].getMmsi();

                voctManager.removeEffortAllocationData(sruMmsi);
                srus.remove(sruMmsi);
                if (sRUCommunication.containsKey(sruMmsi)) {
                    sRUCommunication.remove(sruMmsi);
                }
                // maintainAvailableSRUs();
                notifyListeners(SRUUpdateEvent.SRU_REMOVED, i);
                voctManager.saveToFile();
            }
        }
    }

    public HashMap<Long, SRUCommunicationObject> getsRUCommunication() {
        return sRUCommunication;
    }

    public ArrayList<SRUCommunicationObject> getSRUCommunicationList() {
        return new ArrayList<SRUCommunicationObject>(sRUCommunication.values());
    }

    public SRU getSRUs(long mmsi) {
        return getSRUs().get(mmsi);
    }

    @SuppressWarnings("unchecked")
    public static SRUManager loadSRUManager() {
        SRUManager manager = new SRUManager();

        try {
            FileInputStream fileIn = new FileInputStream(SRU_FILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            manager.setSrus((Map<Long, SRU>) objectIn.readObject());
            objectIn.close();
            fileIn.close();

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load sru file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(SRU_FILE).delete();
        }

        return manager;
    }

    /**
     * @param srus
     *            the srus to set
     */
    public void setSrus(Map<Long, SRU> srus) {
        this.srus = srus;
    }

    public void forceTrackingLayerRepaint() {
        voctLayerTracking.doPrepare();
    }

    @Override
    public void intendedRouteEvent(IntendedRoute intendedRoute) {
        if (intendedRoute != null) {
            long mmsi = intendedRoute.getMmsi();

            if (sRUCommunication.containsKey(mmsi)) {
                sRUCommunication.get(mmsi).setLastMessageRecieved(
                        intendedRoute.getReceived());
            }
            notifyListeners(SRUUpdateEvent.BROADCAST_MESSAGE, mmsi);
        }
    }
}
