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
package dk.dma.epd.shore.voct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationReplyDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationReplyRapidResponse;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;
import dk.dma.epd.common.prototype.service.IIntendedRouteListener;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.layers.voct.VoctLayerTracking;
import dk.dma.epd.shore.service.IntendedRouteHandler;
import dk.dma.epd.shore.service.VoctHandler;
import dk.dma.epd.shore.voct.SRU.sru_status;

public class SRUManager extends MapHandlerChild implements Runnable, IIntendedRouteListener {

    private List<SRU> srus = new LinkedList<SRU>();

    private VOCTManager voctManager;
    private VoctHandler voctHandler;

    // private EnavServiceHandler enavServiceHandler;

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
        // saveToFile();
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

        for (int i = 0; i < voctHandler.getVoctMessageList().size(); i++) {

            long mmsi = Long.parseLong(voctHandler.getVoctMessageList().get(i).getId().toString().split("//")[1]);

            // System.out.println("Is mmsi " + mmsi + " a SRU?");
            for (int j = 0; j < srus.size(); j++) {
                // System.out.println("Comparing " + srus.get(j).getMmsi() +
                // " with " + mmsi);
                if (srus.get(j).getMmsi() == mmsi) {
                    // System.out.println("Yes " + srus.get(j).getMmsi() +
                    // " found");

                    // System.out.println("SRU Name: " + srus.get(j).getName()
                    // + " : " + srus.get(j).getStatus());

                    // Change the status
                    if (srus.get(j).getStatus() != sru_status.ACCEPTED && srus.get(j).getStatus() != sru_status.AVAILABLE
                            && srus.get(j).getStatus() != sru_status.INVITED) {
                        // System.out.println("Updating status WHY");
                        srus.get(j).setStatus(sru_status.AVAILABLE);
                    }

                    // && (srus.get(i).getStatus() == sru_status.ACCEPTED
                    // || srus.get(i).getStatus() == sru_status.AVAILABLE ||
                    // srus
                    // .get(i).getStatus() == sru_status.INVITED)) {

                    // System.out.println("Adding SRU to list");
                    // availableSRUs.add(srus.get(i));

                }
            }

        }

    }

    public void setSRUStatus(int i, sru_status status) {

        // What if we remove a SRU
        srus.get(i).setStatus(status);
        updateSRUsStatus();
        notifyListeners(SRUUpdateEvent.SRU_STATUS_CHANGED, i);
    }

    public void sruSRUStatus(long mmsi, VoctMsgStatus status) {

        for (int i = 0; i < srus.size(); i++) {
            if (srus.get(i).getMmsi() == mmsi) {
                srus.get(i).setVoctMsgStatus(status);
                updateSRUsStatus();
                notifyListeners(SRUUpdateEvent.SRU_STATUS_CHANGED, i);
            }
        }
    }

    public void sruSRUStatus(long mmsi, CloudMessageStatus status) {

        for (int i = 0; i < srus.size(); i++) {
            if (srus.get(i).getMmsi() == mmsi) {
                srus.get(i).setCloudStatus(status);
                srus.get(i).setVoctMsgStatus(VoctMsgStatus.UNKNOWN);
                updateSRUsStatus();
                notifyListeners(SRUUpdateEvent.SRU_STATUS_CHANGED, i);
            }
        }
    }

    // private synchronized void maintainAvailableSRUs() {
    // availableSRUs.clear();
    // for (int i = 0; i < srus.size(); i++) {
    // if (srus.get(i).getStatus() == sru_status.ACCEPTED
    // || srus.get(i).getStatus() == sru_status.AVAILABLE
    // || srus.get(i).getStatus() == sru_status.INVITED) {
    //
    // availableSRUs.add(srus.get(i));
    //
    // }
    // }
    // }

    public int getAvailableSRUS() {
        return sRUCommunication.size();
    }

    public void handleSRUReply(long mmsi, VoctMsgStatus msgStatus) {

        System.out.println("Handling SRU Reply!");

        SRU sru = null;

        int sruID = -1;

        for (int i = 0; i < srus.size(); i++) {
            if (srus.get(i).getMmsi() == mmsi) {
                // Select the SRU we got the message from
                sru = srus.get(i);
                sruID = i;
                System.out.println("SRU Selected");
                break;
            }
        }

        System.out.println("SRU Choosen " + sru);

        // Make sure we got the message from a SRU on our list.
        if (sru != null) {
            VoctMsgStatus status = msgStatus;

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

                // Notify voctmanager to paint efffort allocation area for SRU i
                voctLayerTracking.drawEffectiveArea(sru.getMmsi(), sruID);

                // System.out.println("SRU status set to acceptd");
                // System.out.println("Running through all SRUS");
                // for (int i = 0; i < srus.size(); i++) {
                // System.out.println(srus.get(i).getStatus());
                // }

                notifyListeners(SRUUpdateEvent.SRU_ACCEPT, mmsi);
                break;

            // If theres an old entry, remove it
            case REJECTED:
                sru.setStatus(sru_status.DECLINED);
                if (sRUCommunication.containsKey(mmsi)) {
                    sRUCommunication.remove(mmsi);
                }

                // Remove if we previously had one
                voctLayerTracking.removeEffectiveArea(sru.getMmsi(), sruID);

                notifyListeners(SRUUpdateEvent.SRU_REJECT, mmsi);

                break;
            default:
                sru.setStatus(sru_status.UNKNOWN);

                break;
            }

        }

        // if (!EPDShore.getInstance().getEnavServiceHandler().isListeningToVoct()) {
        // System.out.println("Starting voct listening");
        // EPDShore.getInstance().getEnavServiceHandler().listenToSAR();
        // }

    }

    public void handleSRUReply(VOCTCommunicationReplyRapidResponse reply) {

        System.out.println("Handling SRU Reply!");

        SRU sru = null;

        int sruID = -1;

        for (int i = 0; i < srus.size(); i++) {
            if (srus.get(i).getMmsi() == reply.getMmsi()) {
                // Select the SRU we got the message from
                sru = srus.get(i);
                sruID = i;
                System.out.println("SRU SElected");
                break;
            }
        }

        System.out.println("SRU Choosen " + sru);

        // Make sure we got the message from a SRU on our list.
        if (sru != null) {
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
                sRUCommunication.put(reply.getMmsi(), new SRUCommunicationObject(sru));

                // Notify voctmanager to paint efffort allocation area for SRU i
                voctLayerTracking.drawEffectiveArea(sru.getMmsi(), sruID);

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
                voctLayerTracking.removeEffectiveArea(sru.getMmsi(), sruID);

                notifyListeners(SRUUpdateEvent.SRU_REJECT, reply.getMmsi());

                break;
            default:
                sru.setStatus(sru_status.UNKNOWN);

                break;
            }

        }

        // if (!EPDShore.getInstance().getEnavServiceHandler().isListeningToVoct()) {
        // System.out.println("Starting voct listening");
        // EPDShore.getInstance().getEnavServiceHandler().listenToSAR();
        // }

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
        srus.get(i).setVisible(visible);
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
    public List<SRU> getSRUs() {
        return srus;
    }

    public void addSRU(SRU sru) {
        synchronized (srus) {
            srus.add(sru);
            notifyListeners(SRUUpdateEvent.SRU_ADDED, srus.size());
        }

    }

    public void removeSRU(int i) {
        if (srus.size() >= i + 1) {

            synchronized (srus) {
                SRU sru = srus.remove(i);
                voctManager.removeEffortAllocationData(i);
                if (sRUCommunication.containsKey(sru.getMmsi())) {
                    sRUCommunication.remove(sru.getMmsi());
                }
                // maintainAvailableSRUs();
                notifyListeners(SRUUpdateEvent.SRU_REMOVED, i);
            }
        }
    }

    public HashMap<Long, SRUCommunicationObject> getsRUCommunication() {
        return sRUCommunication;
    }

    public ArrayList<SRUCommunicationObject> getSRUCommunicationList() {
        return new ArrayList<SRUCommunicationObject>(sRUCommunication.values());
    }

    public SRU getSRUs(int index) {
        return getSRUs().get(index);
    }

    public static SRUManager loadSRUManager() {
        SRUManager manager = new SRUManager();

        // try {
        // FileInputStream fileIn = new FileInputStream(VOYAGESFILE);
        // ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        // VoyageStore voyageStore = (VoyageStore) objectIn.readObject();
        // objectIn.close();
        // fileIn.close();
        // manager.setVoyages(voyageStore.getVoyages());
        //
        // } catch (FileNotFoundException e) {
        // // Not an error
        // } catch (Exception e) {
        // LOG.error("Failed to load routes file: " + e.getMessage());
        // // Delete possible corrupted or old file
        // new File(VOYAGESFILE).delete();
        // }

        return manager;
    }

    // public void handleSRUBroadcast(long mmsi, VOCTSARBroadCast r) {
    //
    // System.out.println("Recieved Broadcast");
    //
    // // Only react to mmsi that we invited
    // if (sRUCommunication.containsKey(mmsi)) {
    //
    // sRUCommunication.get(mmsi).addBroadcastMessage(r);
    //
    // notifyListeners(SRUUpdateEvent.BROADCAST_MESSAGE, mmsi);
    // }
    // }

    public void forceTrackingLayerRepaint() {
        voctLayerTracking.doPrepare();
    }

    public void handleSRUReply(VOCTCommunicationReplyDatumPoint l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void intendedRouteEvent(IntendedRoute intendedRoute) {
        long mmsi = intendedRoute.getMmsi();

        if (sRUCommunication.containsKey(mmsi)) {
            sRUCommunication.get(mmsi).setLastMessageRecieved(intendedRoute.getReceived());
        }
        notifyListeners(SRUUpdateEvent.BROADCAST_MESSAGE, mmsi);
    }
}
