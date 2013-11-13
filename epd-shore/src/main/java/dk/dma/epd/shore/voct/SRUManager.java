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

import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.CLOUD_STATUS;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationReply;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.layers.voct.VoctLayerTracking;
import dk.dma.epd.shore.service.EnavServiceHandler;
import dk.dma.epd.shore.voct.SRU.sru_status;

public class SRUManager extends MapHandlerChild implements Runnable {

    private List<SRU> srus = new LinkedList<SRU>();

    private VOCTManager voctManager;
    private EnavServiceHandler enavServiceHandler;

    private LinkedHashMap<Long, SRUCommunicationObject> sRUCommunication = new LinkedHashMap<Long, SRUCommunicationObject>();
    private VoctLayerTracking voctLayerTracking;
    
    private CopyOnWriteArrayList<SRUUpdateListener> listeners = new CopyOnWriteArrayList<>();

    
    
    public SRUManager() {
        EPDShore.startThread(this, "sruManager");
    }

    
    public void setVoctTrackingLayer(VoctLayerTracking layer){
        this.voctLayerTracking = layer;
        System.out.println("We got a tracking layer for sru manager");
    }
    
    public void notifyListeners(SRUUpdateEvent e, int id) {
        for (SRUUpdateListener listener : listeners) {
            listener.sruUpdated(e, id);
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
        while (true) {
            EPDShore.sleep(1000);
            updateSRUsStatus();
            // maintainAvailableSRUs();
            // Maintain list of available SRUs
        }

    }

    private void updateSRUsStatus() {

        for (int i = 0; i < enavServiceHandler.getVoctMessageList().size(); i++) {

            long mmsi = Long.parseLong(enavServiceHandler.getVoctMessageList()
                    .get(i).getId().toString().split("//")[1]);

            // System.out.println("Is mmsi " + mmsi + " a SRU?");
            for (int j = 0; j < srus.size(); j++) {
                // System.out.println("Comparing " + srus.get(j).getMmsi() +
                // " with " + mmsi);
                if (srus.get(j).getMmsi() == mmsi) {
                    // System.out.println("Yes " + srus.get(j).getMmsi() +
                    // " found");

//                    System.out.println("SRU Name: " + srus.get(j).getName()
//                            + " : " + srus.get(j).getStatus());

                    // Change the status
                    if (srus.get(j).getStatus() != sru_status.ACCEPTED
                            && srus.get(j).getStatus() != sru_status.AVAILABLE) {
                        System.out.println("Updating status WHY");
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

    public void handleSRUReply(VOCTCommunicationReply reply) {

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
            CLOUD_STATUS status = reply.getStatus();

            switch (status) {
            // If its been accepted we create an entry in the hashmap, should we
            // overwrite the old one?
            // Remove old one, put new one
            case RECIEVED_ACCEPTED:
                sru.setStatus(sru_status.ACCEPTED);
                if (sRUCommunication.containsKey(reply.getMmsi())) {
                    sRUCommunication.remove(reply.getMmsi());
                } else {
                    sRUCommunication.put(reply.getMmsi(),
                            new SRUCommunicationObject(sru));
                }

                
                //Notify voctmanager to paint efffort allocation area for SRU i
                voctLayerTracking.drawEffectiveArea(sru.getMmsi(), sruID);                
                
                
                
                
                
                
//                System.out.println("SRU status set to acceptd");
//                System.out.println("Running through all SRUS");
//                for (int i = 0; i < srus.size(); i++) {
//                    System.out.println(srus.get(i).getStatus());
//                }
                break;

            // If theres an old entry, remove it
            case RECIEVED_REJECTED:
                sru.setStatus(sru_status.DECLINED);
                if (sRUCommunication.containsKey(reply.getMmsi())) {
                    sRUCommunication.remove(reply.getMmsi());
                }

                //Remove if we previously had one
                voctLayerTracking.removeEffectiveArea(sru.getMmsi(), sruID);    
                
                break;
            default:
                sru.setStatus(sru_status.UNKNOWN);

                break;
            }

        }
        
        notifyListeners(SRUUpdateEvent.CLOUD_MESSAGE, sruID);

    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
        }

        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
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
        if (srus.size() >=i+1) {

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
        // sRUCommunication.
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

        manager.addStaticData();

        return manager;
    }

    public void addStaticData() {

        // SRU bopa = new SRU("MHV BOPA", 1, SRU.sru_type.Ship,
        // SRU.sru_status.UNKNOWN, true);

        // SRU plane = new SRU("Plane 001", 1, SRU.sru_type.PLANE,
        // SRU.sru_status.UNKNOWN, true);
        // SRU helicopter = new SRU("Helicopter", 1, SRU.sru_type.HELICOPTER,
        // SRU.sru_status.UNKNOWN, true);

        // srus.add(bopa);
        // srus.add(plane);
        // srus.add(helicopter);

    }

}
