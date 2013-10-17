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
package dk.dma.epd.common.prototype.voct;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.SAROperation;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.SearchPatternGenerator;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumLineData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;
import dk.dma.epd.common.util.Util;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all
 * information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManagerCommon implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    protected SAROperation sarOperation;


    protected boolean hasSar;



    private CopyOnWriteArrayList<VOCTUpdateListener> listeners = new CopyOnWriteArrayList<>();

    protected SARData sarData;


    /**
     * @return the hasSar
     */
    public boolean isHasSar() {
        return hasSar;
    }

    public void setSarType(SAR_TYPE type) {
        sarOperation = null;
        sarOperation = new SAROperation(type);
    }

    public SAR_TYPE getSarType() {
        if (sarOperation != null) {
            return sarOperation.getOperationType();
        }
        return SAR_TYPE.NONE;
    }

    public void inputDatumLineData(String sarID, DateTime TLKP,
            DateTime DSP2Date, DateTime DSP3Date, DateTime CSS, Position LKP,
            Position DSP2, Position DSP3, double x, double y, double SF,
            int searchObject, List<SARWeatherData> sarWeatherDataPoints) {
        
        

        DatumPointData dsp1 = new DatumPointData(sarID, TLKP, CSS, LKP, x, y,
                SF, searchObject);
        DatumPointData dsp2 = new DatumPointData(sarID, DSP2Date, CSS, DSP2, x,
                y, SF, searchObject);
        DatumPointData dsp3 = new DatumPointData(sarID, DSP3Date, CSS, DSP3, x,
                y, SF, searchObject);

        DatumLineData datumLineSar = new DatumLineData(sarID, TLKP, CSS, LKP,
                x, y, SF, searchObject);
        datumLineSar.setWeatherPoints(sarWeatherDataPoints);

        dsp1.setWeatherPoints(sarWeatherDataPoints);
        dsp2.setWeatherPoints(sarWeatherDataPoints);
        dsp3.setWeatherPoints(sarWeatherDataPoints);

        datumLineSar.addDatumData(dsp1);
        datumLineSar.addDatumData(dsp2);
        datumLineSar.addDatumData(dsp3);

        setSarData(sarOperation.startDatumLineCalculations(datumLineSar));

    }

    public void inputRapidResponseDatumData(String sarID, DateTime TLKP,
            DateTime CSS, Position LKP, double x, double y, double SF,
            int searchObject, List<SARWeatherData> sarWeatherDataPoints) {
        
        

        if (getSarType() == SAR_TYPE.RAPID_RESPONSE) {
            RapidResponseData data = new RapidResponseData(sarID, TLKP, CSS,
                    LKP, x, y, SF, searchObject);

            data.setWeatherPoints(sarWeatherDataPoints);

            setSarData(sarOperation.startRapidResponseCalculations(data));
        }

        if (getSarType() == SAR_TYPE.DATUM_POINT) {
            DatumPointData data = new DatumPointData(sarID, TLKP, CSS, LKP, x,
                    y, SF, searchObject);

            data.setWeatherPoints(sarWeatherDataPoints);

            setSarData(sarOperation.startDatumPointCalculations(data));
        }

    }

    
    protected void updateLayers(){
        //Used for EPDShore
    }
    
    
    /**
     * User has clicked the Cancel button, abort operation and reset
     */
    public void cancelSarOperation() {
        sarOperation = null;
        hasSar = false;

        notifyListeners(VOCTUpdateEvent.SAR_CANCEL);
    }

    public void displaySar() {
        updateLayers();
        notifyListeners(VOCTUpdateEvent.SAR_DISPLAY);
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
        
        
        System.out.println("SAR data is not null!");
        System.out.println(sarData != null);
        this.sarData = sarData;

        notifyListeners(VOCTUpdateEvent.SAR_READY);
    }

    public void EffortAllocationDataEntered() {
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_READY);
        sarOperation.calculateEffortAllocation(sarData);

        System.out.println("Display");
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY);

    }

    public void generateSearchPattern(
            SearchPatternGenerator.searchPattern type, Position CSP) {

    }

    public void updateEffectiveAreaLocation() {
        
    }

    public void showSarInput() {
 
    }

}
