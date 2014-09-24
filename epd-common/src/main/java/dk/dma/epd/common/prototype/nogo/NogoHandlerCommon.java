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
package dk.dma.epd.common.prototype.nogo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.nogo.NogoLayer;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;
import dk.frv.enav.common.xml.nogoslices.response.NogoResponseSlices;

/**
 * Component for handling NOGO areas
 */
@ThreadSafe
public class NogoHandlerCommon extends MapHandlerChild {

    private static final Logger LOG = LoggerFactory.getLogger(NogoHandlerCommon.class);

    protected List<NoGoDataEntry> nogoData = new ArrayList<NoGoDataEntry>();

    Position northWestPoint;
    Position southEastPoint;

    Double draught;

    boolean nogoFailed;

    protected ShoreServicesCommon shoreServices;

    // Create a seperate layer for the nogo information
    protected NogoLayer nogoLayer;

    // private Date lastUpdate;
    private Date validFrom;
    private Date validTo;

    // private int minutesBetween;
    protected boolean useSlices;

    // private NoGoComponentPanel nogoPanel;

    // int completedSlices;

    protected boolean requestInProgress;

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

    public NogoHandlerCommon() {
    }

    public synchronized void updateNogo(boolean useSlices, int minutesBetween) {

        nogoData = new ArrayList<NoGoDataEntry>();
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

            nogoLayer.initializeNoGoStorage(nogoData.size());
        } else {
            // Do a single request

            DateTime startDate = new DateTime(validFrom.getTime());
            DateTime endDate = new DateTime(validTo.getTime());

            NoGoDataEntry nogoDataEntry = new NoGoDataEntry(startDate, endDate);
            nogoData.add(nogoDataEntry);
        }

        NoGoWorker nogoWorker = createWorker(nogoData.size(), new DateTime(validFrom.getTime()), new DateTime(validTo.getTime()));

        nogoWorker.start();

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

    }

    public void noNetworkConnection() {

    }

    public synchronized void nogoRequestCompleted(NogoResponseSlices nogoResponses) {

        LOG.info("NoGo Worker has completed its request");

        int completedSlices = 0;

        for (int i = 0; i < nogoResponses.getResponses().size(); i++) {
            NogoResponse response = nogoResponses.getResponses().get(i);

            completedSlices = completedSlices + 1;

            NoGoDataEntry dataEntry = nogoData.get(i);

            dataEntry.setNogoPolygons(response.getPolygons());
            dataEntry.setNoGoMessage(response.getNoGoMessage());
            dataEntry.setNoGoErrorCode(response.getNoGoErrorCode());

            // Special handling of slices
            if (this.useSlices) {
                updatePanelCompleteMultiple(dataEntry.getNoGoErrorCode(), dataEntry.getNogoPolygons(), dataEntry.getValidFrom(),
                        dataEntry.getValidTo(), draught, i);

                updateLayerMultipleResult(i);

                updatePanelCompletedSlices(completedSlices, nogoData.size());
            } else {
                updatePanelCompleteSingle(dataEntry.getNoGoErrorCode(), dataEntry.getNogoPolygons(), validFrom, validTo, draught);

                updateLayerSingleResult();
            }

        }

    }

    protected void updatePanelCompleteMultiple(int errorcode, List<NogoPolygon> polygons, DateTime validFrom, DateTime validTo,
            Double draught, int i) {

    }

    protected void updatePanelCompleteSingle(int errorcode, List<NogoPolygon> polygons, Date validFrom, Date validTo, Double draught) {

    }

    protected void updatePanelCompletedSlices(int completedSlices, int i) {

    }

    public synchronized void nogoRequestCompleted(NogoResponse response) {

        NoGoDataEntry dataEntry = nogoData.get(0);

        dataEntry.setNogoPolygons(response.getPolygons());
        dataEntry.setNoGoMessage(response.getNoGoMessage());
        dataEntry.setNoGoErrorCode(response.getNoGoErrorCode());

        updatePanelCompleteSingle(dataEntry.getNoGoErrorCode(), dataEntry.getNogoPolygons(), validFrom, validTo, draught);

        updateLayerSingleResult();

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

    protected void resetLayer() {
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
