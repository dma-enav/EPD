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
package dk.dma.epd.shore.voyage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.services.shore.ShoreServices;


/**
 * Manager for handling a collection of routes and active route
 */
public class VoyageManager extends MapHandlerChild implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String VOYAGESFILE = EPDShore.getHomePath().resolve(".voyages").toString();
    private static final Logger LOG = LoggerFactory.getLogger(VoyageManager.class);

    private List<Voyage> voyages = new LinkedList<Voyage>();

    private ShoreServices shoreServices;
    private AisHandler aisHandler;

    private Set<VoyageUpdateListener> listeners = new HashSet<VoyageUpdateListener>();

    public VoyageManager() {
        EPDShore.startThread(this, "VoyageManager");
    }

    public void notifyListeners(VoyageUpdateEvent e) {
        for (VoyageUpdateListener listener : listeners) {
            listener.voyagesChanged(e);
        }
        // Persist routes on update
        saveToFile();
    }

    public void removeVoyage(int index) {
        synchronized (voyages) {
            if (index < 0 || index >= voyages.size()) {
                LOG.error("Could not deactivate route with index: " + index);
                return;
            }
            voyages.remove(index);
        }

        notifyListeners(VoyageUpdateEvent.VOYAGE_REMOVED);
    }

    public void addVoyage(Voyage voyage) {
        synchronized (voyages) {
            voyages.add(voyage);
        }
        notifyListeners(VoyageUpdateEvent.VOYAGE_ADDED);
    }

    /**
     * Delete a stored voyage with ID equal to voyageId.
     * @param voyageId ID of the voyage to delete.
     * @return True if the ID is a valid ID and the voyage was successfully deleted.
     * False if ID is not a valid voyageId.
     */
    public boolean deleteVoyage(long voyageId) {
        synchronized (this.voyages) {
            for(int i = 0; i < this.voyages.size(); i++) {
                Voyage v = this.voyages.get(i);
                if(v.getId() == voyageId) {
                    this.removeVoyage(i);
                    return true;
                }
            }
            return false;
        }
    }
    
    public Voyage getVoyage(int index) {
        synchronized (voyages) {
 
            return getVoyages().get(index);
        }
    }

    public List<Voyage> getVoyages() {
        synchronized (voyages) {
            return voyages;
        }
    }

    public int getVoyageCount() {
        synchronized (voyages) {
            return voyages.size();
        }
    }

    public void addListener(VoyageUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(VoyageUpdateListener listener) {
        listeners.remove(listener);
    }

    public static VoyageManager loadVoyageManager() {
        VoyageManager manager = new VoyageManager();
        try {
            FileInputStream fileIn = new FileInputStream(VOYAGESFILE);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            VoyageStore voyageStore = (VoyageStore) objectIn.readObject();
            objectIn.close();
            fileIn.close();
            manager.setVoyages(voyageStore.getVoyages());

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load routes file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(VOYAGESFILE).delete();
        }

        return manager;
    }



    private void setVoyages(List<Voyage> voyages) {
        if (voyages != null) {
            
            this.voyages = new LinkedList<>();
            
            for (int i = 0; i < voyages.size(); i++) {
//                System.out.println(voyages.get(i).getRoute().getEtas().get(voyages.get(i).getRoute().getEtas().size()-1));
                long date = voyages.get(i).getRoute().getEtas().get(voyages.get(i).getRoute().getEtas().size()-1).getTime();

//                System.out.println("Found date in long is " + date);
//                System.out.println(System.currentTimeMillis());
                
                if (date > System.currentTimeMillis()){
                    this.voyages.add(voyages.get(i));
                }
            }
        }
    }

    public void saveToFile() {
        synchronized (voyages) {
            VoyageStore voyageStore = new VoyageStore(this);
            
            try {
                FileOutputStream fileOut = new FileOutputStream(VOYAGESFILE);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(voyageStore);
                objectOut.close();
                fileOut.close();
            } catch (IOException e) {
                LOG.error("Failed to save routes file: " + e.getMessage());
            }
        }
    }

    @Override
    public void findAndInit(Object obj) {
        if (shoreServices == null && obj instanceof ShoreServices) {
            shoreServices = (ShoreServices) obj;
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler)obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (shoreServices == obj) {
            shoreServices = null;
        }
    }

    private void checkForExpires(){
        for (int i = 0; i < voyages.size(); i++) {
//          System.out.println(voyages.get(i).getRoute().getEtas().get(voyages.get(i).getRoute().getEtas().size()-1));
          long date = voyages.get(i).getRoute().getEtas().get(voyages.get(i).getRoute().getEtas().size()-1).getTime();

//          System.out.println("Found date in long is " + date);
//          System.out.println(System.currentTimeMillis());
          
          if (date < System.currentTimeMillis()){
              removeVoyage(i);
          }
      }
    }

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            EPDShore.sleep(10000);
            checkForExpires();
        }

    }

}
