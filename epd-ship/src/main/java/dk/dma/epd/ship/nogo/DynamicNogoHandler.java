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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.component_panels.DynamicNoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog.dock_type;
import dk.dma.epd.ship.layers.nogo.DynamicNogoLayer;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.settings.EPDEnavSettings;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Component for handling MSI messages
 */
public class DynamicNogoHandler extends MapHandlerChild implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NogoHandler.class);

    Position northWestPointOwn;
    Position southEastPointOwn;
    float draughtOwn;
    float draughtTarget;

    // private long mmsiTarget = 211284230;
    private long mmsiTarget = -1;
    Position northWestPointTarget;
    Position southEastPointTarget;

    boolean nogoFailed;

    private ShoreServicesCommon shoreServices;
    private PntHandler pntHandler;
    private AisHandler aisHandler;
    private OwnShipHandler ownShipHandler;

    // Create a seperate layer for the nogo information
    private DynamicNogoLayer nogoLayer;

    private Date lastUpdate;
    private long pollInterval;

    // Data from the nogo response for own ship
    private List<NogoPolygon> nogoPolygonsOwn;
    private Date validFromOwn;
    private Date validToOwn;
    private int noGoErrorCodeOwn;
    private String noGoMessageOwn;

    private List<NogoPolygon> nogoPolygonsTarget;
    private Date validFromTarget;
    private Date validToTarget;
    private int noGoErrorCodeTarget;
    private String noGoMessageTarget;
    private Thread self;

    private boolean dynamicNoGoActive;

    private DynamicNoGoComponentPanel nogoPanel;

    public boolean getNogoFailed() {
        return nogoFailed;
    }

    public void setNogoFailed(boolean nogoFailed) {
        this.nogoFailed = nogoFailed;
    }

    private Boolean isVisible = true;

    public DynamicNogoHandler(EPDEnavSettings enavSettings) {
        // pollInterval = enavSettings.getNogoPollInterval();
        self = EPDShip.startThread(this, "DynamicNoGoHandler");
    }

    @Override
    public void run() {

        while (true) {

            try {
                if (dynamicNoGoActive) {
                    System.out.println("ello?");
                    nogoLayer.setVisible(true);
                    updateNogo();
                }
                Thread.sleep(80000);
            } catch (InterruptedException e) {
                // System.out.println("Interrupted " + dynamicNoGoActive);
            }

            if (!dynamicNoGoActive && nogoLayer != null) {
                nogoLayer.setVisible(false);
                nogoLayer.cleanUp();
                nogoPanel.inactive();
            }
        }
    }

    public synchronized void activateDynamicNoGo() {
        dynamicNoGoActive = true;
    }

    public synchronized void deactivateDynamicNoGo() {
        dynamicNoGoActive = false;
    }

    public synchronized void setTarget(long mmsi) {
        mmsiTarget = mmsi;
    }

    public synchronized void updateNogo() {

        // System.out.println("Update NoGo");

        // Is dynamic nogo activated and target not null?
        if (dynamicNoGoActive
                && aisHandler.getVesselTarget(mmsiTarget) != null) {

            // Get own ship location and add box around it, + / - something
            if (ownShipHandler.isPositionDefined()
                    && aisHandler.getVesselTarget(mmsiTarget)
                            .getPositionData() != null) {

                System.out.println("Really really update");

                Position shipLocation = ownShipHandler.getPositionData().getPos();
                southEastPointOwn = Position.create(
                        shipLocation.getLatitude() - 0.04,
                        shipLocation.getLongitude() + 0.08);
                northWestPointOwn = Position.create(
                        shipLocation.getLatitude() + 0.04,
                        shipLocation.getLongitude() - 0.08);

                shipLocation = aisHandler.getVesselTarget(mmsiTarget)
                        .getPositionData().getPos();
                southEastPointTarget = Position.create(
                        shipLocation.getLatitude() - 0.04,
                        shipLocation.getLongitude() + 0.08);
                northWestPointTarget = Position.create(
                        shipLocation.getLatitude() + 0.04,
                        shipLocation.getLongitude() - 0.08);

                notifyUpdate(false);
                nogoPanel.newRequest();

                // Set depth for own ship
                if (ownShipHandler.getStaticData() != null) {
                    // System.out.println("Getting draught from static - own");
                    draughtOwn = ownShipHandler.getStaticData()
                            .getDraught() / 10;
                } else {
                    System.out.println("Setting draught to 5");
                    draughtOwn = 5;
                }

                if (aisHandler.getVesselTarget(mmsiTarget)
                        .getStaticData() != null) {
                    // System.out.println("Getting draught from static - target");
                    draughtTarget = aisHandler.getVesselTarget(mmsiTarget).getStaticData().getDraught() / 10;

                } else {
                    // System.out.println("Setting draught to 5");
                    draughtTarget = 5;
                }
                // NorthWest pos
                // SouthEast pos

                // Get current target if exists.
                // Get current timezone - Configure dynamic NoGo box? Not
                // important
                // right now
                // Get own draught

                // aisHandler.getOwnShip().getPositionData().getPos()

                // Get target ship draught - if exists
                // aisHandler.getVesselTargets().get(mmsiTarget).getStaticData()
                // .getDraught();

                // Make two nogo requests, one for the ship, one for the target
                // Plot these requests into the DynamicNoGoLayer
                // Done?

                boolean nogoUpdated = false;
                Date now = new Date();
                if (getLastUpdate() == null
                        || now.getTime() - getLastUpdate().getTime() > pollInterval * 1000) {
                    // Poll for data from shore
                    try {
                        if (poll()) {
                            nogoUpdated = true;
                        }
                        setLastUpdate(now);
                    } catch (ShoreServiceException e) {
                        LOG.error("Failed to get NoGo from shore: "
                                + e.getMessage());

                        nogoFailed = true;
                        nogoUpdated = true;
                        setLastUpdate(now);
                    }
                }
                // Notify if update
                if (nogoUpdated) {
                    nogoLayer.cleanUp();
                    notifyUpdate(true);
                    nogoPanel.requestCompleted(getNogoFailed(),
                            getNoGoErrorCodeOwn(), getNoGoErrorCodeTarget(),
                            getNogoPolygonsOwn(), getNogoPolygonsTarget(),
                            getValidFromOwn(), getValidToOwn(),
                            getDraughtOwn(), getDraughtTarget()

                    );
                }
            }
        }
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

        Date date = new Date();
        validFromOwn = date;
        validToOwn = date;

        validFromTarget = date;
        validToTarget = date;

        if (ownShipHandler.isPositionDefined()
                && aisHandler.getVesselTarget(mmsiTarget)
                        .getPositionData() != null) {

            System.out.println("Making a request to the server");

            // Send a rest to shoreServices for NoGo
            NogoResponse nogoResponseOwn = shoreServices.nogoPoll(-draughtOwn,
                    northWestPointOwn, southEastPointOwn, validFromOwn,
                    validToOwn);

            NogoResponse nogoResponseTarget = shoreServices.nogoPoll(
                    -draughtTarget, northWestPointTarget, southEastPointTarget,
                    validFromTarget, validToTarget);

            // NogoResponse nogoResponseTarget =
            // shoreServices.nogoPoll(draughtTarget,
            // northWestPointTarget, southEastPointTarget, validFrom, validTo);

            nogoPolygonsOwn = nogoResponseOwn.getPolygons();
            validFromOwn = nogoResponseOwn.getValidFrom();
            validToOwn = nogoResponseOwn.getValidTo();
            noGoErrorCodeOwn = nogoResponseOwn.getNoGoErrorCode();
            noGoMessageOwn = nogoResponseOwn.getNoGoMessage();

            nogoPolygonsTarget = nogoResponseTarget.getPolygons();
            validFromTarget = nogoResponseTarget.getValidFrom();
            validToTarget = nogoResponseTarget.getValidTo();
            noGoErrorCodeTarget = nogoResponseTarget.getNoGoErrorCode();
            noGoMessageTarget = nogoResponseTarget.getNoGoMessage();
            // System.out.println(nogoResponse.getNoGoErrorCode());
            // System.out.println(nogoResponse.getNoGoMessage());
            // System.out.println(nogoResponse.getPolygons().size());

            if (nogoResponseOwn == null
                    || nogoResponseOwn.getPolygons() == null
                    || nogoResponseTarget == null
                    || nogoResponseTarget.getPolygons() == null) {
                return false;
            }
        }

        return true;

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
        if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
        }
        if (obj instanceof DynamicNogoLayer) {
            nogoLayer = (DynamicNogoLayer) obj;
        }
        if (pntHandler == null && obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
        }
        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }
        if (ownShipHandler == null && obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler) obj;
        }
        if (obj instanceof DynamicNoGoComponentPanel) {
            nogoPanel = (DynamicNoGoComponentPanel) obj;
        }

    }

    public float getDraughtOwn() {
        return draughtOwn;
    }

    public Position getNorthWestPointOwn() {
        return northWestPointOwn;
    }

    public Position getSouthEastPointOwn() {
        return southEastPointOwn;
    }

    public List<NogoPolygon> getNogoPolygonsOwn() {
        return nogoPolygonsOwn;
    }

    public Date getValidFromOwn() {
        return validFromOwn;
    }

    public Date getValidToOwn() {
        return validToOwn;
    }

    public int getNoGoErrorCodeOwn() {
        return noGoErrorCodeOwn;
    }

    public String getNoGoMessageOwn() {
        return noGoMessageOwn;
    }

    public List<NogoPolygon> getNogoPolygonsTarget() {
        return nogoPolygonsTarget;
    }

    public Date getValidFromTarget() {
        return validFromTarget;
    }

    public Date getValidToTarget() {
        return validToTarget;
    }

    public int getNoGoErrorCodeTarget() {
        return noGoErrorCodeTarget;
    }

    public String getNoGoMessageTarget() {
        return noGoMessageTarget;
    }

    public Position getNorthWestPointTarget() {
        return northWestPointTarget;
    }

    public Position getSouthEastPointTarget() {
        return southEastPointTarget;
    }

    public long getMmsiTarget() {
        return mmsiTarget;
    }

    public boolean isDynamicNoGoActive() {
        return dynamicNoGoActive;
    }

    public void setDynamicNoGoActive(boolean dynamicNoGoActive) {
        this.dynamicNoGoActive = dynamicNoGoActive;

        System.out.println(dynamicNoGoActive);

        // System.out.println("Interrupting!");
        self.interrupt();

        if (dynamicNoGoActive) {
            // If the dock isn't visible should it show it?
            if (!EPDShip.getInstance().getMainFrame().getDockableComponents()
                    .isDockVisible("Dynamic NoGo")) {

                // Show it display the message?
                if (EPDShip.getInstance().getSettings().getGuiSettings().isShowDockMessage()) {
                    new ShowDockableDialog(EPDShip.getInstance().getMainFrame(),
                            dock_type.DYN_NOGO);
                } else {

                    if (EPDShip.getInstance().getSettings().getGuiSettings().isAlwaysOpenDock()) {
                        EPDShip.getInstance().getMainFrame().getDockableComponents()
                                .openDock("Dynamic NoGo");
                        EPDShip.getInstance().getMainFrame().getJMenuBar()
                                .refreshDockableMenu();
                    }

                    // It shouldn't display message but take a default action

                }

            }
        }
    }

    public void setMmsiTarget(long mmsiTarget) {
        this.mmsiTarget = mmsiTarget;
    }

    public float getDraughtTarget() {
        return draughtTarget;
    }

}
