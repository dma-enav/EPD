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
package dk.dma.epd.ship.layers.ais;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.ISelectableGraphic;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.AtoNTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;
import dk.dma.epd.common.prototype.layers.ais.AtonTargetGraphic;
import dk.dma.epd.common.prototype.layers.ais.PastTrackWpCircle;
import dk.dma.epd.common.prototype.layers.ais.SartGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphic;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphicComponentSelector;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.gui.TopPanel;
import dk.dma.epd.ship.gui.component_panels.AisComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog;
import dk.dma.epd.ship.gui.component_panels.ShowDockableDialog.dock_type;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * AIS layer. Showing AIS targets and intended routes.
 */
@ThreadSafe
public class AisLayer extends AisLayerCommon<AisHandler> implements IAisTargetListener {

    private static final long serialVersionUID = 1L;

    private OwnShipHandler ownShipHandler;

    private AisTargetInfoPanel aisTargetInfoPanel = new AisTargetInfoPanel();
    private SarTargetInfoPanel sarTargetInfoPanel = new SarTargetInfoPanel();

    // Only accessed in event dispatch thread
    private AisComponentPanel aisPanel;

    private volatile long selectedMMSI = -1;

    private TopPanel topPanel;

    public AisLayer(int redrawIntervalMillis) {
        super(redrawIntervalMillis);
        
        // Register graphics for mouse over notifications
        this.registerInfoPanel(this.aisTargetInfoPanel, VesselGraphic.class, AtonTargetGraphic.class);
        this.registerInfoPanel(this.sarTargetInfoPanel, SartGraphic.class);
    }

    /**
     * Move the target selection and force it to be painted
     * 
     * @param aisTarget
     */
    public void updateSelection(final AisTarget aisTarget, final boolean clicked) {
        // Run only in event dispath thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateSelection(aisTarget, clicked);

                }
            });
            return;
        }

        // If the dock isn't visible should it show it?
        if (!EPDShip.getInstance().getMainFrame().getDockableComponents().isDockVisible("AIS Target") && clicked) {

            // Show it display the message?
            if (EPDShip.getInstance().getSettings().getGuiSettings().isShowDockMessage()) {
                new ShowDockableDialog(EPDShip.getInstance().getMainFrame(), dock_type.AIS);
            } else {

                if (EPDShip.getInstance().getSettings().getGuiSettings().isAlwaysOpenDock()) {
                    EPDShip.getInstance().getMainFrame().getDockableComponents().openDock("AIS Target");
                    EPDShip.getInstance().getMainFrame().getEeINSMenuBar().refreshDockableMenu();
                }

                // It shouldn't display message but take a default action

            }

        }

        VesselTarget vessel = (VesselTarget) aisTarget;

        double rhumbLineDistance = -1.0;
        double rhumbLineBearing = -1.0;

        if (vessel.getStaticData() != null) {

            if (ownShipHandler.isPositionDefined()) {
                rhumbLineDistance = ownShipHandler.getPositionData().getPos()
                        .rhumbLineDistanceTo(vessel.getPositionData().getPos());
                rhumbLineBearing = ownShipHandler.getPositionData().getPos().rhumbLineBearingTo(vessel.getPositionData().getPos()

                );
            }

            aisPanel.receiveHighlight(vessel.getMmsi(), vessel.getStaticData().getName(), vessel.getStaticData().getCallsign(),
                    vessel.getPositionData().getCog(), rhumbLineDistance, rhumbLineBearing, vessel.getPositionData().getSog()

            );
        } else {

            if (ownShipHandler.isPositionDefined()) {
                rhumbLineDistance = ownShipHandler.getPositionData().getPos()
                        .rhumbLineDistanceTo(vessel.getPositionData().getPos());
                rhumbLineBearing = ownShipHandler.getPositionData().getPos().rhumbLineBearingTo(vessel.getPositionData().getPos()

                );
            }

            aisPanel.receiveHighlight(vessel.getMmsi(), vessel.getPositionData().getCog(), rhumbLineDistance, rhumbLineBearing,
                    vessel.getPositionData().getSog());
        }
        if (vessel.getStaticData() != null && ownShipHandler.isPositionDefined()) {
            aisPanel.dynamicNogoAvailable(true);
        } else {
            aisPanel.dynamicNogoAvailable(false);
        }

        doPrepare();

    }

    /**
     * Remove the selection ring
     */
    public synchronized void removeSelection() {
        // Run only in event dispath thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    removeSelection();
                }
            });
            return;
        }
        selectedMMSI = -1;
        aisPanel.resetHighLight();
        doPrepare();
    }

    @Override
    public void targetUpdated(AisTarget aisTarget) {
        // Let super class manage the graphics map and list by invoking the super implementation of this method
        super.targetUpdated(aisTarget);
        // Sanity check
        if (aisTarget == null) {
            return;
        }

        long mmsi = aisTarget.getMmsi();

        if (aisTarget.isGone() && mmsi == selectedMMSI) {
            // Clear selection if selected target was the target that is now gone.
            removeSelection();
        }
        else if (mmsi == selectedMMSI) {
            // Update selection if MMSI of updated target matches MMSI of selected target.
            updateSelection(aisTarget, false);
        }
    }

    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof AisComponentPanel) {
            aisPanel = (AisComponentPanel) obj;
        }
        if (obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler) obj;
        }
        if (obj instanceof PntHandler) {
            sarTargetInfoPanel.setPntHandler((PntHandler) obj);
        }
        if (obj instanceof TopPanel) {
            topPanel = (TopPanel) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        super.findAndUndo(obj);
    }

    @Override
    protected void handleMouseClick(OMGraphic clickedGraphics, MouseEvent evt) {
        // Let super class handle selection marker
        super.handleMouseClick(clickedGraphics, evt);
        // Should run on event dispatch thread as we are updating swing components
        assert SwingUtilities.isEventDispatchThread();
        if(clickedGraphics == null) {
            this.removeSelection();
        }
        else if(clickedGraphics instanceof ISelectableGraphic && clickedGraphics instanceof VesselGraphic) {
            // TODO consider if locking on vt is needed - add a dummy Object instance as mutex if it is
            VesselTarget vt = ((VesselGraphic)clickedGraphics).getMostRecentVesselTarget();
            this.selectedMMSI = vt.getMmsi();
            this.updateSelection(vt, true);
        }
    }
    
    @Override
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {
        if (clickedGraphics instanceof VesselGraphic) {
            VesselGraphic vg = (VesselGraphic) clickedGraphics;
            // TODO this is NOT pretty. Update aisMenu to take VesselGraphic arg?
            VesselGraphicComponentSelector vtg = (VesselGraphicComponentSelector) this.getTargetGraphic(vg.getMostRecentVesselTarget().getMmsi());
            this.getMapMenu().aisMenu(vtg, topPanel);
        }
        else if (clickedGraphics instanceof SartGraphic) {
            SartGraphic sartGraphic = (SartGraphic) clickedGraphics;
            SarTarget sarTarget = sartGraphic.getSarTargetGraphic().getSarTarget();
            getMapMenu().sartMenu(this, sarTarget);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest,
            MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof PastTrackWpCircle) {
            PastTrackWpCircle wpCircle = (PastTrackWpCircle) newClosest;
            pastTrackInfoPanel.showWpInfo(wpCircle);
            return true;
        } else if (newClosest instanceof VesselGraphic) {
            VesselTarget vesselTarget = ((VesselGraphic)newClosest).getMostRecentVesselTarget();
            aisTargetInfoPanel.showAisInfo(vesselTarget);
            return true;
        }
        else if (newClosest instanceof SartGraphic) {
            SartGraphic sartGraphic = (SartGraphic) newClosest;
            sarTargetInfoPanel.showSarInfo(sartGraphic.getSarTargetGraphic().getSarTarget());
            return true;
        }
        else if (newClosest instanceof AtonTargetGraphic) {
            AtoNTarget atonTarget = ((AtonTargetGraphic) newClosest).getAtonTarget();
            aisTargetInfoPanel.showAtonInfo(atonTarget);
            return true;
        }
        return false;
    }
    
    public void zoomTo(Position position) {
        mapBean.setCenter(position.getLatitude(), position.getLongitude());
        // mapBean.setScale(EeINS.getSettings().getEnavSettings().getMsiTextboxesVisibleAtScale());
    }
    
    /**
     * This method is called repeatedly as specified by the {@code LazyLayerCommon} and signals that this AisLayer should repaint itself.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        // Repaint every time the timer expires.
        this.doPrepare();
    }
    
    
    @Override
    public void forceLayerUpdate() {
        // force a repaint
        this.doPrepare();
    }
    
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu) super.getMapMenu();
    }
}
