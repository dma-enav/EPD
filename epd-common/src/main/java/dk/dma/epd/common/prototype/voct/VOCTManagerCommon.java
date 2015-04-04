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
package dk.dma.epd.common.prototype.voct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.voct.SAROperation;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.SearchPatternGenerator;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumLineData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.util.Util;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManagerCommon extends MapHandlerChild implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    protected SAROperation sarOperation;

    protected boolean hasSar;

    protected boolean loadSarFromSerialize;

    private CopyOnWriteArrayList<VOCTUpdateListener> listeners = new CopyOnWriteArrayList<>();

    protected SARData sarData;
    protected List<SARData> sarFutureData;

    protected static final String VOCT_FILE = EPD.getInstance().getHomePath().resolve(".voct").toString();
    protected static final Logger LOG = LoggerFactory.getLogger(VOCTManagerCommon.class);

    public enum VoctMsgStatus {
        ACCEPTED, REJECTED, NOTED, IGNORED, UNKNOWN, WITHDRAWN
    }

    /**
     * @return the hasSar
     */
    public boolean isHasSar() {
        return hasSar;
    }

    public void setSarType(SAR_TYPE type) {
//        System.out.println("SET SAR TYPE " + type);
        sarOperation = null;
        sarOperation = new SAROperation(type);
    }

    public SAR_TYPE getSarType() {
        if (sarOperation != null) {
            return sarOperation.getOperationType();
        }
        return SAR_TYPE.NONE;
    }

    public void inputDatumLineData(String sarID, DateTime TLKP, DateTime DSP2Date, DateTime DSP3Date, DateTime CSS, Position LKP,
            Position DSP2, Position DSP3, double x, double y, double SF, int searchObject, List<SARWeatherData> sarWeatherDataPoints) {

        DatumPointData dsp1 = new DatumPointData(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);
        DatumPointData dsp2 = new DatumPointData(sarID, DSP2Date, CSS, DSP2, x, y, SF, searchObject);
        DatumPointData dsp3 = new DatumPointData(sarID, DSP3Date, CSS, DSP3, x, y, SF, searchObject);

        DatumLineData datumLineSar = new DatumLineData(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);
        datumLineSar.setWeatherPoints(sarWeatherDataPoints);

        dsp1.setWeatherPoints(sarWeatherDataPoints);
        dsp2.setWeatherPoints(sarWeatherDataPoints);
        dsp3.setWeatherPoints(sarWeatherDataPoints);

        datumLineSar.addDatumData(dsp1);
        datumLineSar.addDatumData(dsp2);
        datumLineSar.addDatumData(dsp3);

        setSarData(sarOperation.startDatumLineCalculations(datumLineSar));

    }

    public void inputRapidResponseDatumData(String sarID, DateTime TLKP, DateTime CSS, Position LKP, double x, double y, double SF,
            int searchObject, List<SARWeatherData> sarWeatherDataPoints) {

        if (getSarType() == SAR_TYPE.RAPID_RESPONSE) {
            RapidResponseData data = new RapidResponseData(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);

            data.setWeatherPoints(sarWeatherDataPoints);

            setSarData(sarOperation.startRapidResponseCalculations(data));
        }

        if (getSarType() == SAR_TYPE.DATUM_POINT) {
            DatumPointData data = new DatumPointData(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);

            data.setWeatherPoints(sarWeatherDataPoints);

            setSarData(sarOperation.startDatumPointCalculations(data));

        }

    }

    public void showSARFuture(int i) {

    }

    protected void updateLayers() {
        // Used for EPDShore
    }

    /**
     * User has clicked the Cancel button, abort operation and reset
     */
    public void cancelSarOperation() {
        deleteAllRoutes();
        sarOperation = null;
        hasSar = false;

        notifyListeners(VOCTUpdateEvent.SAR_CANCEL);

        // Delete stored SAR
        new File(VOCT_FILE).delete();
    }

    private void deleteAllRoutes() {
        Iterator<Entry<Long, EffortAllocationData>> iter = sarData.getEffortAllocationData().entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, EffortAllocationData> entry = iter.next();
            if (entry.getValue().getSearchPatternRoute() != null) {

                SearchPatternRoute searchPattern = entry.getValue().getSearchPatternRoute();
                for (int i = 0; i < EPD.getInstance().getRouteManager().getRoutes().size(); i++) {
                    if (EPD.getInstance().getRouteManager().getRoute(i).toString().equals(searchPattern.toString())) {
                        EPD.getInstance().getRouteManager().removeRoute(i);
                        break;
                    }
                }

            }

        }
    }

    public void displaySar() {
        saveToFile();
        // This is where we display SAR

        updateLayers();

        notifyListeners(VOCTUpdateEvent.SAR_DISPLAY);

    }

    public void saveToFile() {

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
    public void run() {

        // Maintanaince routines
        while (true) {
            Util.sleep(10000);

        }

    }

    public void notifyListeners(VOCTUpdateEvent e) {
        for (VOCTUpdateListener listener : listeners) {
            listener.voctUpdated(e);
        }

        // Persist update VOCT info
        // saveToFile();
    }

    public void addListener(VOCTUpdateListener listener) {
        listeners.add(listener);

        // if (loadSarFromSerialize) {
        // listener.voctUpdated(VOCTUpdateEvent.SAR_DISPLAY);
        //
        // if (sarData.getEffortAllocationData().size() > 0) {
        //
        // listener.voctUpdated(VOCTUpdateEvent.EFFORT_ALLOCATION_READY);
        // listener.voctUpdated(VOCTUpdateEvent.EFFORT_ALLOCATION_SERIALIZED);
        // }
        // }
    }

    public void removeListener(VOCTUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * @return the sarData
     */
    public SARData getSarData() {
        return sarData;
    }

    /**
     * @param sarData
     *            the sarData to set
     */
    public void setSarData(SARData sarData) {

        this.sarData = sarData;

        //Disable, not sure if should be part of main features.
//        if (!(sarData instanceof DatumPointDataSARIS)) {
//            sarFutureData = sarOperation.sarFutureCalculations(sarData);
//        }
        notifyListeners(VOCTUpdateEvent.SAR_READY);
    }

    public void EffortAllocationDataEntered() {
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_READY);
        sarOperation.calculateEffortAllocation(sarData);

        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY);

        saveToFile();
    }

    public void generateSearchPattern(SearchPatternGenerator.searchPattern type, Position CSP, long id) {

    }

    public void updateEffectiveAreaLocation() {

    }

    public void showSarInput() {

    }

    /**
     * @return the loadSarFromSerialize
     */
    public boolean isLoadSarFromSerialize() {
        return loadSarFromSerialize;
    }

    /**
     * @param loadSarFromSerialize
     *            the loadSarFromSerialize to set
     */
    public void setLoadSarFromSerialize(boolean loadSarFromSerialize) {
        this.loadSarFromSerialize = loadSarFromSerialize;
    }

    /**
     * Used by EPD Shore to remove possible removed effort allocation areas
     * 
     * @param sarData
     */
    protected void checkSRU(SARData sarData) {

    }

    protected void initializeFromSerializedFile(SARData sarData) {

        checkSRU(sarData);

        if (sarData instanceof RapidResponseData) {
            setSarType(SAR_TYPE.RAPID_RESPONSE);
            RapidResponseData rapidResponseData = (RapidResponseData) sarData;
            setSarData(sarOperation.startRapidResponseCalculations(rapidResponseData));

        }

        if (sarData instanceof DatumPointData) {
            setSarType(SAR_TYPE.DATUM_POINT);
            DatumPointData datumPointData = (DatumPointData) sarData;
            setSarData(sarOperation.startDatumPointCalculations(datumPointData));

        }

        if (sarData instanceof DatumLineData) {
            setSarType(SAR_TYPE.DATUM_LINE);
            DatumLineData datumLinetData = (DatumLineData) sarData;
            setSarData(sarOperation.startDatumLineCalculations(datumLinetData));

        }

        displaySar();
    }

}
