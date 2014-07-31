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
package dk.dma.epd.ship.service.voct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.SearchPatternGenerator;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.voct.SARInput;
import dk.dma.epd.ship.gui.voct.SARInvitationRequest;
import dk.dma.epd.ship.layers.voct.VoctLayer;
import dk.dma.epd.ship.service.VoctHandler;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManager extends VOCTManagerCommon {

    private static final long serialVersionUID = 1L;
    private SARInput sarInputDialog;
    private VoctHandler voctHandler;
    private long currentID = -1;

    // private VOCTBroadcastService voctBroadcastService;

    VoctLayer voctLayer;

    public VOCTManager() {
        EPDShip.startThread(this, "VOCTManager");
        LOG.info("Started VOCT Manager");
    }

    @Override
    public void showSarInput() {
        LOG.info("Started new SAR Operation");
        if (!hasSar) {
            hasSar = true;

            // Create the GUI input boxes

            // Voct specific test
            sarInputDialog = new SARInput(this);
            sarInputDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            sarInputDialog.setVisible(true);

        } else {
            // Cannot inititate a SAR without terminating the existing one, show
            // existing dialog?
            sarInputDialog.setVisible(true);
        }

    }

    /**
     * @param voctLayer
     *            the voctLayer to set
     */
    public void setVoctLayer(VoctLayer voctLayer) {
        this.voctLayer = voctLayer;
    }

    @Override
    public void addListener(VOCTUpdateListener listener) {
        super.addListener(listener);

        if (loadSarFromSerialize) {
            listener.voctUpdated(VOCTUpdateEvent.SAR_DISPLAY);

            if (sarData != null) {
                if (sarData.getEffortAllocationData() != null) {

                    if (sarData.getEffortAllocationData().size() > 0) {

                        listener.voctUpdated(VOCTUpdateEvent.EFFORT_ALLOCATION_READY);
                        listener.voctUpdated(VOCTUpdateEvent.EFFORT_ALLOCATION_SERIALIZED);

                        Iterator<Entry<Long, EffortAllocationData>> iter = sarData.getEffortAllocationData().entrySet().iterator();
                        while (iter.hasNext()) {
                            Entry<Long, EffortAllocationData> entry = iter.next();
                            System.out.println("Entry long is " + entry.getKey());
                            if (entry.getValue().getSearchPatternRoute() != null) {

                                SearchPatternRoute searchPattern = entry.getValue().getSearchPatternRoute();
                                for (int i = 0; i < EPD.getInstance().getRouteManager().getRoutes().size(); i++) {
                                    if (EPD.getInstance().getRouteManager().getRoute(i).toString().equals(searchPattern.toString())) {
                                        EPD.getInstance().getRouteManager().getRoutes().set(i, searchPattern);
                                        notifyListeners(VOCTUpdateEvent.SEARCH_PATTERN_GENERATED);
                                        break;
                                    }
                                }

                            }

                        }

                    }

                }
            }
        }
    }

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            Util.sleep(10000);

        }

    }

    public static VOCTManager loadVOCTManager() {

        // Where we load or serialize old VOCTS
        VOCTManager voctManager = new VOCTManager();
        try (FileInputStream fileIn = new FileInputStream(VOCT_FILE); ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {

            SARData sarDataLoaded = (SARData) objectIn.readObject();
            voctManager.setLoadSarFromSerialize(true);
            voctManager.initializeFromSerializedFile(sarDataLoaded);

        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load routes file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(VOCT_FILE).delete();
        }

        return voctManager;

    }

    @Override
    public synchronized void saveToFile() {

        if (hasSar || loadSarFromSerialize) {

            try (FileOutputStream fileOut = new FileOutputStream(VOCT_FILE);
                    ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
                objectOut.writeObject(sarData);
            } catch (IOException e) {
                LOG.error("Failed to save VOCT data: " + e.getMessage());
            }
        }
    }

    @Override
    public void generateSearchPattern(SearchPatternGenerator.searchPattern type, Position CSP, long id) {

        sarData.setCSP(CSP);

        SearchPatternGenerator searchPatternGenerator = new SearchPatternGenerator(sarOperation);

        SearchPatternRoute searchRoute = searchPatternGenerator.generateSearchPattern(type, sarData, EPDShip.getInstance()
                .getSettings().getNavSettings(), id);

        // Remove old and overwrite
        if (sarData.getEffortAllocationData().get(id).getSearchPatternRoute() != null) {
            int routeIndex = EPDShip.getInstance().getRouteManager()
                    .getRouteIndex(sarData.getEffortAllocationData().get(id).getSearchPatternRoute());

            EPDShip.getInstance().getRouteManager().removeRoute(routeIndex);
        }

        sarData.getEffortAllocationData().get(id).setSearchPatternRoute(searchRoute);

        EPDShip.getInstance().getRouteManager().addRoute(searchRoute);

        notifyListeners(VOCTUpdateEvent.SEARCH_PATTERN_GENERATED);

        saveToFile();
    }

    @Override
    public void updateEffectiveAreaLocation() {
        voctLayer.updateEffectiveAreaLocation(sarData);
    }

    public void removeOldSARData() {
        // Remove any old SAR data
        if (sarData != null) {
            if (sarData.getEffortAllocationData().size() > 0) {
                if (sarData.getEffortAllocationData().get(0L).getSearchPatternRoute() != null) {
                    System.out.println("Removing existing routes");

                    int routeIndex = EPDShip.getInstance().getRouteManager()
                            .getRouteIndex(sarData.getEffortAllocationData().get(0L).getSearchPatternRoute());

                    EPDShip.getInstance().getRouteManager().removeRoute(routeIndex);

                    EPDShip.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
                }
            }
        }
    }

    @Override
    public void notifyListeners(VOCTUpdateEvent e) {
        super.notifyListeners(e);
        if (e == VOCTUpdateEvent.SAR_CANCEL) {
            currentID = -1;
        }

    }

    /**
     * public void handleDialogAction(boolean accepted, VOCTCommunicationMessageDatumPoint message, SAR_TYPE type) {
     * 
     * if (accepted) { voctHandler.sendVOCTReply(VoctMsgStatus.ACCEPTED, message.getId(), "Accepted", type);
     * 
     * removeOldSARData();
     * 
     * DatumPointData data = new DatumPointData(message.getSarData());
     * 
     * if (message.getEffortAllocationData() != null) {
     * 
     * // message.getEffortAllocationData() EffortAllocationData effortAllocationData = new
     * EffortAllocationData(message.getEffortAllocationData());
     * 
     * if (message.getSearchPattern() != null) { SearchPatternRoute searchPattern = new SearchPatternRoute(new
     * Route(message.getSearchPattern()));
     * 
     * sarOperation = new SAROperation(SAR_TYPE.DATUM_POINT);
     * 
     * SearchPatternGenerator searchPatternGenerator = new SearchPatternGenerator(sarOperation);
     * searchPatternGenerator.calculateDynamicWaypoints(searchPattern, data);
     * 
     * effortAllocationData.setSearchPatternRoute(searchPattern); EPDShip.getInstance().getRouteManager().addRoute(searchPattern);
     * EPDShip.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);
     * 
     * }
     * 
     * data.addEffortAllocationData(effortAllocationData, 0);
     * 
     * }
     * 
     * this.setSarData(data); setSarType(SAR_TYPE.DATUM_POINT);
     * 
     * hasSar = true;
     * 
     * notifyListeners(VOCTUpdateEvent.SAR_RECEIVED_CLOUD);
     * 
     * // Force start startVOCTBroadcast(); } else { voctHandler.sendVOCTReply(VoctMsgStatus.REJECTED, message.getId(), "Rejected",
     * type);
     * 
     * }
     * 
     * }
     **/
    public void handleDialogAction(boolean accepted, VOCTCommunicationMessage message, SAR_TYPE type) {

        if (accepted) {

            voctHandler.sendVOCTReply(VoctMsgStatus.ACCEPTED, message.getId(), "Accepted", type);

            removeOldSARData();

            SARData data = null;

            if (type == SAR_TYPE.RAPID_RESPONSE) {
                data = new RapidResponseData(message.getSarDataRapidResponse());
                setSarType(SAR_TYPE.RAPID_RESPONSE);
                saveToFile();
            }

            if (type == SAR_TYPE.DATUM_POINT) {
                data = new DatumPointData(message.getSarDataDatumPoint());
                setSarType(SAR_TYPE.DATUM_POINT);
            }

            if (type == SAR_TYPE.SARIS_DATUM_POINT) {
                data = new DatumPointDataSARIS(message.getSarDataDatumPointSaris());
                setSarType(SAR_TYPE.SARIS_DATUM_POINT);
            }

            if (message.getEffortAllocationData() != null) {

                EffortAllocationData effortAllocationData = new EffortAllocationData(message.getEffortAllocationData());

                if (message.getSearchPattern() != null) {
                    SearchPatternRoute searchPattern = new SearchPatternRoute(new Route(message.getSearchPattern()));

                    SearchPatternGenerator searchPatternGenerator = new SearchPatternGenerator(sarOperation);
                    searchPatternGenerator.calculateDynamicWaypoints(searchPattern, data);

                    effortAllocationData.setSearchPatternRoute(searchPattern);
                    EPDShip.getInstance().getRouteManager().addRoute(searchPattern);
                    EPDShip.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_ADDED);

                }

                data.addEffortAllocationData(0, effortAllocationData);

            }

            this.setSarData(data);

            hasSar = true;

            notifyListeners(VOCTUpdateEvent.SAR_RECEIVED_CLOUD);

            saveToFile();
        } else {
            voctHandler.sendVOCTReply(VoctMsgStatus.REJECTED, message.getId(), "Rejected", type);
        }

    }

    public void handleSARDataPackage(VOCTCommunicationMessage message) {

        if (message.getStatus() == VoctMsgStatus.WITHDRAWN) {

            int n = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(), "The OSC has cancelled the operation\n"
                    + "Do you wish to end your SAR participation?", "End SAR?", JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                cancelSarOperation();
            }

        } else {

            currentID = message.getId();
            SARInvitationRequest sarInviteDialog = new SARInvitationRequest(this, message);
            sarInviteDialog.setVisible(true);
        }

    }

    @Override
    public void showSARFuture(int i) {

        if (this.sarOperation.getOperationType() != SAR_TYPE.SARIS_DATUM_POINT) {

            if (i == 0) {
                voctLayer.showFutureData(sarData);
            } else {
                voctLayer.showFutureData(sarFutureData.get((i / 30) - 1));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof VoctHandler) {
            voctHandler = (VoctHandler) obj;
        }

    }

    /**
     * @return the currentID
     */
    public long getCurrentID() {
        return currentID;
    }

}
