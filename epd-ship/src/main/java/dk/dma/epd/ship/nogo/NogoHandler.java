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
package dk.dma.epd.ship.nogo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import net.jcip.annotations.ThreadSafe;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.component_panels.NoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog.dock_type;
import dk.dma.epd.ship.layers.nogo.NogoLayer;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Component for handling NOGO areas
 */
@ThreadSafe
public class NogoHandler extends MapHandlerChild {

    private static final Logger LOG = LoggerFactory.getLogger(NogoHandler.class);

    private List<NoGoDataEntry> nogoData = new ArrayList<NoGoDataEntry>();

    Position northWestPoint;
    Position southEastPoint;

    Double draught;

    boolean nogoFailed;

    private ShoreServicesCommon shoreServices;

    // Create a seperate layer for the nogo information
    private NogoLayer nogoLayer;

    // private Date lastUpdate;
    private Date validFrom;
    private Date validTo;

    // private int minutesBetween;
    private boolean useSlices;

    private NoGoComponentPanel nogoPanel;

    int completedRequests;

    private boolean requestInProgress;

    public NogoLayer getNogoLayer() {
        return nogoLayer;
    }

    public void setNorthWestPoint(Position northWestPoint) {
        this.northWestPoint = northWestPoint;
    }

    public void setSouthEastPoint(Position southEastPoint) {
        this.southEastPoint = southEastPoint;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    private Boolean isVisible = true;

    public NogoHandler(EPDEnavSettings enavSettings) {
    }

    public synchronized void updateNogo(boolean useSlices, int minutesBetween) {

        if (requestInProgress) {
            JOptionPane.showMessageDialog(EPDShip.getInstance().getMainFrame(),
                    "Please wait for the previous NoGo request to be completed before initiating a new",
                    "Unable to comply with NoGo request", JOptionPane.WARNING_MESSAGE);
        } else {

            LOG.info("New NoGo Requested Initiated");
            requestInProgress = true;
            // If the dock isn't visible should it show it?
            if (!EPDShip.getInstance().getMainFrame().getDockableComponents().isDockVisible("NoGo")) {

                // Show it display the message?
                if (EPDShip.getInstance().getSettings().getGuiSettings().isShowDockMessage()) {
                    new ShowDockableDialog(EPDShip.getInstance().getMainFrame(), dock_type.NOGO);
                } else {

                    if (EPDShip.getInstance().getSettings().getGuiSettings().isAlwaysOpenDock()) {
                        EPDShip.getInstance().getMainFrame().getDockableComponents().openDock("NoGo");
                        EPDShip.getInstance().getMainFrame().getJMenuBar().refreshDockableMenu();
                    }

                    // It shouldn't display message but take a default action

                }

            }
            this.useSlices = useSlices;
            // this.minutesBetween = minutesBetween;

            this.resetLayer();

            // Setup the panel
            if (this.useSlices) {
                nogoPanel.activateMultiple();
                nogoPanel.newRequestMultiple();
            } else {
                nogoPanel.activateSingle();
                nogoPanel.newRequestSingle();

            }

            nogoData = new ArrayList<NoGoDataEntry>();
            System.out.println("Resetting nogodata");
            // New Request - determine how many time slices are needed to complete the request or if we even need to do slices

            // Calculate slices
            if (this.useSlices) {

                DateTime startDate = new DateTime(validFrom.getTime());
                DateTime endDate = new DateTime(validTo.getTime());

                DateTime currentVal;

                currentVal = startDate.plusMinutes(minutesBetween);

                NoGoDataEntry nogoDataEntry = new NoGoDataEntry(startDate, currentVal);
                nogoData.add(nogoDataEntry);

                while (currentVal.isBefore(endDate)) {
                    startDate = currentVal;
                    currentVal = startDate.plusMinutes(minutesBetween);
                    nogoDataEntry = new NoGoDataEntry(startDate, currentVal);
                    nogoData.add(nogoDataEntry);
                }

                nogoPanel.initializeSlider(nogoData.size());
                nogoLayer.initializeNoGoStorage(nogoData.size());
            } else {
                // Do a single request

                DateTime startDate = new DateTime(validFrom.getTime());
                DateTime endDate = new DateTime(validTo.getTime());

                NoGoDataEntry nogoDataEntry = new NoGoDataEntry(startDate, endDate);
                nogoData.add(nogoDataEntry);
            }

            NoGoWorker nogoWorker = createWorker(nogoData.size(), new DateTime(validFrom.getTime()),
                    new DateTime(validTo.getTime()));

            nogoWorker.start();

            completedRequests = 0;

            System.out.println("Nogo data size is " + nogoData.size());
            // createWorker(nogoData.size()).run();

            // // Create the workers
            // for (int i = 1; i < nogoData.size(); i++) {
            // // System.out.println("Next worker " + i);
            // NoGoWorker nogoWorker = new NoGoWorker(this, this.shoreServices, i);
            // nogoWorker.setValues(draught, northWestPoint, southEastPoint, nogoData.get(i).getValidFrom(), nogoData.get(i)
            // .getValidTo());
            //
            // nogoWorker.run();
            // // System.out.println("Run created for " + i);
            // }

            // } else {

            //
        }
    }

    private NoGoWorker createWorker(int slices, DateTime startDate, DateTime endDate) {
        NoGoWorker nogoWorker = new NoGoWorker(this, this.shoreServices, 0, slices);
        nogoWorker.setValues(draught, northWestPoint, southEastPoint, startDate, endDate);
        return nogoWorker;
    }

    /**
     * Handles a failed NoGo request, either because of data error, or no connection
     */
    public void nogoTimedOut() {
        if (this.useSlices) {
            nogoPanel.nogoFailedMultiple();
        } else {
            nogoPanel.nogoFailedSingle();
        }
    }

    public void noNetworkConnection() {
        if (this.useSlices) {
            nogoPanel.noConnectionMultiple();
        } else {
            nogoPanel.noConnectionSingle();
        }
    }

    public synchronized void nogoWorkerCompleted(int i, NogoResponse response) {

        completedRequests = completedRequests + 1;

        System.out.println("NoGo worker " + i + " has completed its request");

        NoGoDataEntry dataEntry = nogoData.get(i);

        dataEntry.setNogoPolygons(response.getPolygons());
        dataEntry.setNoGoMessage(response.getNoGoMessage());
        dataEntry.setNoGoErrorCode(response.getNoGoErrorCode());

        // Special handling of slices
        if (this.useSlices) {
            nogoPanel.requestCompletedMultiple(dataEntry.getNoGoErrorCode(), dataEntry.getNogoPolygons(), dataEntry.getValidFrom(),
                    dataEntry.getValidTo(), draught, i);
            updateLayerMultipleResult(i);

            nogoPanel.setCompletedSlices(completedRequests, nogoData.size());
        } else {
            nogoPanel
                    .requestCompletedSingle(dataEntry.getNoGoErrorCode(), dataEntry.getNogoPolygons(), validFrom, validTo, draught);

            updateLayerSingleResult();
        }

    }

    public synchronized void setNoGoRequestCompleted() {
        requestInProgress = false;
    }

    public Position getNorthWestPoint() {
        return northWestPoint;
    }

    public Position getSouthEastPoint() {
        return southEastPoint;
    }

    private void resetLayer() {
        nogoLayer.addFrame(northWestPoint, southEastPoint);
    }

    private void updateLayerMultipleResult(int i) {
        // System.out.println("Value " + i + " is ready");
        nogoLayer.addResultFromMultipleRequest(nogoData.get(i), i);
    }

    private void updateLayerSingleResult() {
        // Single result returned
        nogoLayer.singleResultCompleted(nogoData.get(0));
    }

    // private boolean poll() throws ShoreServiceException {
    //
    // if (shoreServices == null) {
    // return false;
    // }
    //
    // // Date date = new Date();
    // // Send a rest to shoreServices for NoGo
    // // System.out.println(draught);
    // // System.out.println(northWestPoint);
    // // System.out.println(southEastPoint);
    // // System.out.println(validFrom);
    // // System.out.println(validTo);
    //
    // if (useSlices) {
    //
    // } else {
    //
    // }
    //
    // NogoResponse nogoResponse = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom, validTo);
    //
    // System.out.println("Response 1");
    // NogoResponse nogoResponse2 = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom, validTo);
    // System.out.println("Response 2");
    // NogoResponse nogoResponse3 = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom, validTo);
    // System.out.println("Response 3");
    // NogoResponse nogoResponse4 = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom, validTo);
    // System.out.println("Response 4");
    //
    // // nogoPolygons = nogoResponse.getPolygons();
    // validFrom = nogoResponse.getValidFrom();
    // validTo = nogoResponse.getValidTo();
    // noGoErrorCode = nogoResponse.getNoGoErrorCode();
    // noGoMessage = nogoResponse.getNoGoMessage();
    //
    // // System.out.println(nogoResponse.getNoGoErrorCode());
    // // System.out.println(nogoResponse.getNoGoMessage());
    // // System.out.println(nogoResponse.getPolygons().size());
    //
    // if (nogoResponse == null || nogoResponse.getPolygons() == null) {
    // return false;
    // }
    // return true;
    //
    // }

    public Double getDraught() {
        return draught;
    }

    public void setDraught(Double draught) {
        this.draught = -draught;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public synchronized List<NogoPolygon> getPolygons() {
        return null;
    }

    public boolean toggleLayer() {
        if (isVisible) {
            nogoLayer.setVisible(false);
            isVisible = false;
        } else {
            nogoLayer.setVisible(true);
            isVisible = true;
        }
        return isVisible;
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
        }
        if (obj instanceof NogoLayer) {
            nogoLayer = (NogoLayer) obj;
        }
        if (obj instanceof NoGoComponentPanel) {
            nogoPanel = (NoGoComponentPanel) obj;
        }

    }

    public void showNoGoIndex(int id) {
        nogoLayer.drawSpecificResult(id - 1);
    }

    /**
     * @return the nogoData
     */
    public List<NoGoDataEntry> getNogoData() {
        return nogoData;
    }

}
