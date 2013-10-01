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
package dk.dma.epd.ship.service.voct;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateListener;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.voct.SARInput;
import dk.dma.epd.ship.layers.voct.VoctLayer;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all
 * information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManager implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    private SAROperation sarOperation;
    
    
    private static final Logger LOG = LoggerFactory
            .getLogger(VOCTManager.class);

    private boolean hasSar;

    private SARInput sarInputDialog;

    private CopyOnWriteArrayList<VOCTUpdateListener> listeners = new CopyOnWriteArrayList<>();

    private RapidResponseData rapidResponseData;
    VoctLayer voctLayer;

    public VOCTManager() {
        EPDShip.startThread(this, "VOCTManager");
        LOG.info("Started VOCT Manager");
    }

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

    /**
     * @return the hasSar
     */
    public boolean isHasSar() {
        return hasSar;
    }

    public void setSarType(SAR_TYPE type) {
        sarOperation = null;
        sarOperation = new SAROperation(type, this);
    }

    public SAR_TYPE getSarType() {
        if (sarOperation != null) {
            return sarOperation.getOperationType();
        }
        return SAR_TYPE.NONE;
    }

    public void inputRapidResponseData(String sarID, DateTime TLKP, DateTime CSS,
            Position LKP, double x, double y, double SF, int searchObject,
            List<SARWeatherData> sarWeatherDataPoints) {

        RapidResponseData data = new RapidResponseData(sarID, TLKP, CSS, LKP, x, y,
                SF, searchObject);
        
        data.setWeatherPoints(sarWeatherDataPoints);
        
        sarOperation.startRapidResponseCalculations(data);
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
        notifyListeners(VOCTUpdateEvent.SAR_DISPLAY);
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
        return new VOCTManager();

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
     * @return the rapidResponseData
     */
    public RapidResponseData getRapidResponseData() {
        return rapidResponseData;
    }

    /**
     * @param rapidResponseData
     *            the rapidResponseData to set
     */
    public void setRapidResponseData(RapidResponseData rapidResponseData) {
        this.rapidResponseData = rapidResponseData;

        notifyListeners(VOCTUpdateEvent.SAR_READY);
    }

    public void EffortAllocationDataEntered() {
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_READY);
        sarOperation.calculateEffortAllocation(rapidResponseData);

        System.out.println("Display");
        notifyListeners(VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY);

    }
    
    public void generateSearchPattern(SearchPatternGenerator.searchPattern type, Position CSP){
        
        rapidResponseData.setCSP(CSP);
        
        SearchPatternGenerator searchPatternGenerator = new SearchPatternGenerator(this, sarOperation);
        
        SearchPatternRoute searchRoute = searchPatternGenerator.generateSearchPattern(type, rapidResponseData);
        
        //Remove old and overwrite
        if (rapidResponseData.getSearchPatternRoute() != null){
            int routeIndex = EPDShip.getRouteManager().getRouteIndex(rapidResponseData.getSearchPatternRoute());
         
            EPDShip.getRouteManager().removeRoute(routeIndex);
        }
        
        
        rapidResponseData.setSearchPatternRoute(searchRoute);
        
        EPDShip.getRouteManager().addRoute(searchRoute);
        
        notifyListeners(VOCTUpdateEvent.SEARCH_PATTERN_GENERATED);
    }

}
