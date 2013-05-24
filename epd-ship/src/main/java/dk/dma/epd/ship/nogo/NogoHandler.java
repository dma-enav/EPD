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

import java.util.Date;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.shoreservice.ShoreServices;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ComponentPanels.NoGoComponentPanel;
import dk.dma.epd.ship.gui.ComponentPanels.ShowDockableDialog;
import dk.dma.epd.ship.gui.ComponentPanels.ShowDockableDialog.dock_type;
import dk.dma.epd.ship.layers.nogo.NogoLayer;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Component for handling NOGO areas
 */
@ThreadSafe
public class NogoHandler extends MapHandlerChild implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NogoHandler.class);

    Position northWestPoint;
    Position southEastPoint;
    Double draught;
    boolean nogoFailed;

    private ShoreServices shoreServices;

    // Create a seperate layer for the nogo information
    private NogoLayer nogoLayer;

    private Date lastUpdate;
    private long pollInterval;

    // Data from the nogo response
    private List<NogoPolygon> nogoPolygons;
    private Date validFrom;
    private Date validTo;
    private int noGoErrorCode;
    private String noGoMessage;

    private NoGoComponentPanel nogoPanel;
    
    
    public NogoLayer getNogoLayer() {
        return nogoLayer;
    }

    public int getNoGoErrorCode() {
        return noGoErrorCode;
    }

    public String getNoGoMessage() {
        return noGoMessage;
    }

    public boolean getNogoFailed() {
        return nogoFailed;
    }

    public void setNogoFailed(boolean nogoFailed) {
        this.nogoFailed = nogoFailed;
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
        // pollInterval = enavSettings.getNogoPollInterval();
        EPDShip.startThread(this, "NogoHandler");
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(30000);
            // updateNogo();
        }
    }

    public synchronized void updateNogo() {
        
        
        // If the dock isn't visible should it show it?
        if (!EPDShip.getMainFrame().getDockableComponents()
                .isDockVisible("NoGo")) {

            // Show it display the message?
            if (EPDShip.getSettings().getGuiSettings().isShowDockMessage()) {
                new ShowDockableDialog(EPDShip.getMainFrame(),
                        dock_type.NOGO);
            } else {

                if (EPDShip.getSettings().getGuiSettings().isAlwaysOpenDock()) {
                    EPDShip.getMainFrame().getDockableComponents()
                            .openDock("NoGo");
                    EPDShip.getMainFrame().getEeINSMenuBar()
                            .refreshDockableMenu();
                }

                // It shouldn't display message but take a default action

            }

        }
        
        
        
        
        
        
        notifyUpdate(false);
        nogoPanel.newRequest();
        boolean nogoUpdated = false;
        Date now = new Date();
        
//        
//        System.out.println("Standard locations at:");
//        System.out.println("south east point:" + southEastPoint);
//        System.out.println("north west point: " + northWestPoint);
        
        
        if (getLastUpdate() == null || now.getTime() - getLastUpdate().getTime() > pollInterval * 1000) {
            // Poll for data from shore
            try {
                if (poll()) {
                    nogoUpdated = true;
                }
                setLastUpdate(now);
            } catch (ShoreServiceException e) {
                LOG.error("Failed to get NoGo from shore: " + e.getMessage());

                nogoFailed = true;
                nogoUpdated = true;
                setLastUpdate(now);
            }
        }
        // Notify if update
        if (nogoUpdated) {
            notifyUpdate(true);
            nogoPanel.requestCompleted(nogoFailed, noGoErrorCode, nogoPolygons, validFrom, validTo, draught);
        }

    }

    public Position getNorthWestPoint() {
        return northWestPoint;
    }

    public Position getSouthEastPoint() {
        return southEastPoint;
    }

    public void notifyUpdate(boolean completed) {
        if (nogoLayer != null) {
            nogoLayer.doUpdate(completed);
        }
    }

    public boolean poll() throws ShoreServiceException {

        if (shoreServices == null) {
            return false;
        }

        // Date date = new Date();
        // Send a rest to shoreServices for NoGo
//        System.out.println(draught);
//        System.out.println(northWestPoint);
//        System.out.println(southEastPoint);
//        System.out.println(validFrom);
//        System.out.println(validTo);
        
        
        NogoResponse nogoResponse = shoreServices.nogoPoll(draught, northWestPoint, southEastPoint, validFrom, validTo);

        nogoPolygons = nogoResponse.getPolygons();
        validFrom = nogoResponse.getValidFrom();
        validTo = nogoResponse.getValidTo();
        noGoErrorCode = nogoResponse.getNoGoErrorCode();
        noGoMessage = nogoResponse.getNoGoMessage();

//        System.out.println(nogoResponse.getNoGoErrorCode());
//        System.out.println(nogoResponse.getNoGoMessage());
//        System.out.println(nogoResponse.getPolygons().size());

        if (nogoResponse == null || nogoResponse.getPolygons() == null) {
            return false;
        }
        return true;

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
        return nogoPolygons;
    }

    public synchronized Date getLastUpdate() {
        return lastUpdate;
    }

    private synchronized void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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
        if (obj instanceof ShoreServices) {
            shoreServices = (ShoreServices) obj;
        }
        if (obj instanceof NogoLayer) {
            nogoLayer = (NogoLayer) obj;
        }
        if (obj instanceof NoGoComponentPanel) {
            nogoPanel = (NoGoComponentPanel) obj;
        }

    }

}
