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
package dk.dma.epd.common.prototype.service;

import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serializable class to store MSI-NM messages
 */
public class MsiNmStore implements Serializable {

    private static final long serialVersionUID = 4;
    private static final Logger LOG = LoggerFactory.getLogger(MsiNmStore.class);

    private static String msiNmFile;

    private List<MsiNmNotification> msiNmMessages = new ArrayList<>();
    private Set<Integer> deletedMsiNmIds = new HashSet<>();

    public List<MsiNmNotification> getMsiNmMessages() {
        return msiNmMessages;
    }

    public void setMsiNmMessages(List<MsiNmNotification> msiNmMessages) {
        this.msiNmMessages = msiNmMessages;
    }

    public Set<Integer> getDeletedMsiNmIds() {
        return deletedMsiNmIds;
    }

    public void setDeletedMsiNmIds(Set<Integer> deletedMsiNmIds) {
        this.deletedMsiNmIds = deletedMsiNmIds;
    }

    /**
     * Constructor
     * @param homePath the home path
     */
    public MsiNmStore(Path homePath) {
        msiNmFile = homePath.resolve(".msinm").toString();
    }

    /**
     * Saves the store to file
     */
    public synchronized void saveToFile() {

        try (FileOutputStream fileOut = new FileOutputStream(msiNmFile);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(deletedMsiNmIds);
            objectOut.writeObject(msiNmMessages);

            LOG.info("Saved MSI-NM store");
        } catch (IOException e) {
            LOG.error("Failed to save MSI-NM file: " + e.getMessage());
        }
    }

    /**
     * Loads the MSI-NM store from file
     * @param homePath the home path
     * @return the MSI-NM store
     */
    @SuppressWarnings("unchecked")
    public static MsiNmStore loadFromFile(Path homePath) {

        msiNmFile = homePath.resolve(".msinm").toString();
        MsiNmStore store = new MsiNmStore(homePath);

        try (FileInputStream fileIn = new FileInputStream(msiNmFile);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            Set<Integer> deletedMsiNmIds = (Set<Integer>)objectIn.readObject();
            store.setDeletedMsiNmIds(deletedMsiNmIds);

            List<MsiNmNotification> msiNmMessages = (List<MsiNmNotification>)objectIn.readObject();
            store.setMsiNmMessages(msiNmMessages);

            return  store;
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load MSI-NM file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(msiNmFile).delete();
        }
        return store;
    }

}
