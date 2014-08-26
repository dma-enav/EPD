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
package dk.dma.epd.ship.fal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.fal.FALReport;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.fal.FALManagerCommon;

public class FALManager extends FALManagerCommon {

    private StaticFalShipData staticShipData = new StaticFalShipData();

    private static final String FAL_SHIP = EPD.getInstance().getHomePath().resolve(".falship").toString();

    private static final String FAL_REPORTS = EPD.getInstance().getHomePath().resolve(".fal").toString();

    private static final Logger LOG = LoggerFactory.getLogger(FALManager.class);

    private List<FALReport> falReports = new ArrayList<FALReport>();

    /**************************************/
    /** Life cycle operations **/
    /**************************************/

    /**
     * Loads and instantiates a {@code FALManager} containing previous FAL reports as well as the static entered data
     * 
     * @return the new fal manager
     */
    public static FALManager loadFALManager() {

        FALManager manager = new FALManager();

        // Load the static data
        try (FileInputStream fileIn = new FileInputStream(FAL_SHIP); ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {
            StaticFalShipData staticFalShipDataLoaded = (StaticFalShipData) objectIn.readObject();
            manager.setStaticShipData(staticFalShipDataLoaded);

            LOG.info("Ship FAL data Loaded");
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load fal static ship file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(FAL_SHIP).delete();
        }

        // Load all stored fal reports
        try (FileInputStream fileIn = new FileInputStream(FAL_REPORTS); ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {
            @SuppressWarnings("unchecked")
            List<FALReport> falReports = (List<FALReport>) objectIn.readObject();
            manager.setFalReports(falReports);
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load stored fal reports: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(FAL_REPORTS).delete();
        }

        return manager;
    }

    @Override
    public synchronized void saveToFile() {

        try (FileOutputStream fileOut = new FileOutputStream(FAL_REPORTS);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
            objectOut.writeObject(falReports);
        } catch (IOException e) {
            LOG.error("Failed to save FAL reports: " + e.getMessage());
        }
    }

    public synchronized void saveStaticData() {
        try (FileOutputStream fileOut = new FileOutputStream(FAL_SHIP);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
            objectOut.writeObject(staticShipData);
        } catch (IOException e) {
            LOG.error("Failed to save FAL data: " + e.getMessage());
        }
    }

    /**
     * @return the staticShipData
     */
    public StaticFalShipData getStaticShipData() {
        return staticShipData;
    }

    /**
     * @param staticShipData
     *            the staticShipData to set
     */
    public void setStaticShipData(StaticFalShipData staticShipData) {
        this.staticShipData = staticShipData;
    }

    /**
     * @return the falReports
     */
    public List<FALReport> getFalReports() {
        return falReports;
    }

    public void replaceFalReport(FALReport falreport) {
        for (int i = 0; i < falReports.size(); i++) {
            if (falReports.get(i).getId() == falreport.getId()) {
                falReports.set(i, falreport);
                break;
            }
        }

        saveToFile();
    }

    public void addFALReport(FALReport falReport) {
        falReports.add(falReport);
        saveToFile();
    }

    public void removeFALReport(int index) {
        falReports.remove(index);
        saveToFile();
    }

    /**
     * @param falReports
     *            the falReports to set
     */
    public void setFalReports(List<FALReport> falReports) {
        this.falReports = falReports;
    }

    public FALReport getFalReportWithID(long id) {
        for (int i = 0; i < falReports.size(); i++) {

            if (falReports.get(i).getId() == id) {
                return falReports.get(i);
            }

        }
        return null;
    }
}
