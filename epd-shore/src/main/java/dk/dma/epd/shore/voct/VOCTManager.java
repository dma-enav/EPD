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
import java.util.List;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.SearchPatternGenerator;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.voct.SARInput;
import dk.dma.epd.shore.layers.voct.VoctLayer;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all
 * information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManager extends VOCTManagerCommon {


    private static final long serialVersionUID = 1L;
    private SARInput sarInputDialog;
    private SARData sarData;

    List<VoctLayer> voctLayers = new ArrayList<VoctLayer>();
    
    private static final Logger LOG = LoggerFactory
            .getLogger(VOCTManagerCommon.class);

    public VOCTManager() {
        EPDShore.startThread(this, "VOCTManager");
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

    
    @Override
    protected void updateLayers(){
        
        if (voctLayers.size() == 0){
            EPDShore.getMainFrame().addSARWindow();
        }
    }
    
    
    @Override
    public void addListener(VOCTUpdateListener listener) {
        super.addListener(listener);
        
        if (listener instanceof VoctLayer){
            voctLayers.add((VoctLayer) listener);
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
        return new VOCTManager();

    }


    @Override
    public void generateSearchPattern(
            SearchPatternGenerator.searchPattern type, Position CSP) {

        sarData.setCSP(CSP);

        SearchPatternGenerator searchPatternGenerator = new SearchPatternGenerator(
                sarOperation);

        SearchPatternRoute searchRoute = searchPatternGenerator
                .generateSearchPattern(type, sarData, EPDShore.getSettings()
                        .getNavSettings());

        // Remove old and overwrite
        if (sarData.getSearchPatternRoute() != null) {
            int routeIndex = EPDShore.getRouteManager().getRouteIndex(
                    sarData.getSearchPatternRoute());

            EPDShore.getRouteManager().removeRoute(routeIndex);
        }

        sarData.setSearchPatternRoute(searchRoute);

        EPDShore.getRouteManager().addRoute(searchRoute);

        notifyListeners(VOCTUpdateEvent.SEARCH_PATTERN_GENERATED);
    }

    @Override
    public void updateEffectiveAreaLocation() {
//        voctLayer.updateEffectiveAreaLocation(sarData);
    }

}
