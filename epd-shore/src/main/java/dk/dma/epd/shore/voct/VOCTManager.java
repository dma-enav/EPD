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
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.SearchPatternGenerator;
import dk.dma.epd.common.prototype.model.voct.sardata.SearchPatternRoute;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.MapFrameType;
import dk.dma.epd.shore.gui.voct.SARInput;
import dk.dma.epd.shore.gui.voct.SRUManagerDialog;
import dk.dma.epd.shore.layers.voct.VoctLayerCommon;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.VoctHandler;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManager extends VOCTManagerCommon implements IRoutesUpdateListener {

    private static final long serialVersionUID = 1L;
    private SARInput sarInputDialog;
    private SRUManagerDialog sruManagerDialog;
    private RouteManager routeManager;

    private VoctHandler voctHandler;
    // private SARData sarData;

    private SRUManager sruManager;

    List<VoctLayerCommon> voctLayers = new ArrayList<VoctLayerCommon>();

    private static final Logger LOG = LoggerFactory.getLogger(VOCTManagerCommon.class);

    private long voctID = -1;

    public VOCTManager() {
        EPDShore.startThread(this, "VOCTManager");
        LOG.info("Started VOCT Manager");
    }

    /**
     * @return the voctID
     */
    public long getVoctID() {
        return voctID;
    }

    @Override
    public void showSarInput() {
        LOG.info("Started new SAR Operation");
        if (!hasSar) {
            hasSar = true;

            voctID = System.currentTimeMillis();
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

    public void showSRUManagerDialog() {

        if (sruManagerDialog != null) {
            sruManagerDialog.setVisible(true);
        }

    }

    /**
     * @return the sruManagerDialog
     */
    public SRUManagerDialog getSruManagerDialog() {
        return sruManagerDialog;
    }

    @Override
    protected void updateLayers() {

        if (voctLayers.size() == 0) {

            EPDShore.getInstance().getMainFrame().addSARWindow(MapFrameType.SAR_Planning);
        }

        notifyListeners(VOCTUpdateEvent.SAR_DISPLAY);
    }

    @Override
    public void addListener(VOCTUpdateListener listener) {
        super.addListener(listener);

        if (listener instanceof VoctLayerCommon) {
            voctLayers.add((VoctLayerCommon) listener);
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
    public void generateSearchPattern(SearchPatternGenerator.searchPattern type, Position CSP, int id) {

        updateEffectiveAreaLocation();

        sarData.setCSP(CSP);

        SearchPatternGenerator searchPatternGenerator = new SearchPatternGenerator(sarOperation);

        SearchPatternRoute searchRoute = searchPatternGenerator.generateSearchPattern(type, sarData, EPDShore.getInstance()
                .getSettings().getNavSettings(), id);

        // Remove old and overwrite
        if (sarData.getEffortAllocationData().get(id).getSearchPatternRoute() != null) {
            System.out.println("Previous route found");
            int routeIndex = EPDShore.getInstance().getRouteManager()
                    .getRouteIndex(sarData.getEffortAllocationData().get(id).getSearchPatternRoute());

            System.out.println("Route index of old is " + routeIndex);

            EPDShore.getInstance().getRouteManager().removeRoute(routeIndex);
        }

        sarData.getEffortAllocationData().get(id).setSearchPatternRoute(searchRoute);

        EPDShore.getInstance().getRouteManager().addRoute(searchRoute);

        notifyListeners(VOCTUpdateEvent.SEARCH_PATTERN_GENERATED);
    }

    @Override
    public void updateEffectiveAreaLocation() {
        // voctLayer.updateEffectiveAreaLocation(sarData);

        voctLayers.get(0).updateEffectiveAreaLocation(sarData);
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof SRUManager) {

            sruManager = (SRUManager) obj;

        }

        if (obj instanceof SRUManagerDialog) {
            sruManagerDialog = (SRUManagerDialog) obj;
        }

        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
            routeManager.addListener(this);
        }

        if (obj instanceof VoctHandler) {
            voctHandler = (VoctHandler) obj;
        }

    }

    /**
     * @return the sruManager
     */
    public SRUManager getSruManager() {
        return sruManager;
    }

    public void toggleSRUVisibility(int id, boolean visible) {
        System.out.println("Toggle visibility voctmanager");
        for (int i = 0; i < voctLayers.size(); i++) {
            voctLayers.get(i).toggleEffectiveAreaVisibility(id, visible);
        }

    }

    public void removeEffortAllocationData(int i) {

        if (sarData != null) {

            if (sarData.getEffortAllocationData().size() > i) {

                if (sarData.getEffortAllocationData().get(i).getSearchPatternRoute() != null) {

                    routeManager.getRoutes().remove(sarData.getEffortAllocationData().get(i).getSearchPatternRoute());
                    routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
                }

                sarData.getEffortAllocationData().remove(i);

                for (int j = 0; j < voctLayers.size(); j++) {
                    voctLayers.get(j).removeEffortAllocationArea(i);
                }
            }
        }

    }

    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        if (e == RoutesUpdateEvent.ROUTE_REMOVED) {

            checkRoutes();
        }

    }

    private void checkRoutes() {

        if (sarData != null) {

            for (int i = 0; i < sarData.getEffortAllocationData().size(); i++) {
                if (!routeManager.getRoutes().contains(sarData.getEffortAllocationData().get(i).getSearchPatternRoute())) {
                    System.out.println("Route removed");
                    sarData.getEffortAllocationData().get(i).setSearchPatternRoute(null);
                }
            }
        }
    }

    @Override
    public void cancelSarOperation() {
        super.cancelSarOperation();

        // What do we need to cancel for the VOCT

        // Send cancel SAR message to all participants currently involved

        // Close down SAR TRACKINg and SAR Planning

        for (int i = 0; i < EPDShore.getInstance().getMainFrame().getMapWindows().size(); i++) {

            if (EPDShore.getInstance().getMainFrame().getMapWindows().get(i).getType() == MapFrameType.SAR_Tracking
                    || EPDShore.getInstance().getMainFrame().getMapWindows().get(i).getType() == MapFrameType.SAR_Planning) {
                // Resize windows
                EPDShore.getInstance().getMainFrame().getMapWindows().get(i).dispose();

            }
        }
        EPDShore.getInstance().getMainFrame().removeSARWindows();

        // Clear up voctLayers
        for (int i = 0; i < voctLayers.size(); i++) {
            voctLayers.get(i).dispose();
        }

        voctLayers.clear();

        voctHandler.sendCancelMessage(sruManager.cancelAllSRU());
    }

    @Override
    public void EffortAllocationDataEntered() {
        updateEffectiveAreaLocation();
        super.EffortAllocationDataEntered();

        checkRoutes();
    }

    @Override
    public void showSARFuture(int i) {

        if (i == 0) {
            voctLayers.get(0).showFutureData(sarData);
        } else {
            voctLayers.get(0).showFutureData(sarFutureData.get((i / 30) - 1));
        }

    }
    
    
}
