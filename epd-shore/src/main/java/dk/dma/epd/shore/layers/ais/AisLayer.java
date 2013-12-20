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
package dk.dma.epd.shore.layers.ais;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import com.bbn.openmap.event.MapEventUtils;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.ais.AisTargetSelectionGraphic;
import dk.dma.epd.common.prototype.layers.ais.IntendedRouteLegGraphic;
import dk.dma.epd.common.prototype.layers.ais.IntendedRouteWpCircle;
import dk.dma.epd.common.prototype.layers.ais.PastTrackInfoPanel;
import dk.dma.epd.common.prototype.layers.ais.PastTrackWpCircle;
import dk.dma.epd.common.prototype.layers.ais.SarTargetGraphic;
import dk.dma.epd.common.prototype.layers.ais.SartGraphic;
import dk.dma.epd.common.prototype.layers.ais.TargetGraphic;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.dma.epd.shore.gui.views.StatusArea;

/**
 * The class AisLayer is the layer containing all AIS targets. The class handles the drawing of vessels on the chartPanel.
 */
@ThreadSafe
public class AisLayer extends OMGraphicHandlerLayer implements Runnable, IAisTargetListener, MapMouseListener {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(AisLayer.class);

    @GuardedBy("graphics")
    private final OMGraphicList graphics = new OMGraphicList();

    private volatile AisHandler aisHandler;
    private AisInfoPanel aisInfoPanel;
    private StatusArea statusArea;
    private ChartPanel chartPanel;
    private MainFrame mainFrame;
    private JMapFrame jMapFrame;
    private final IntendedRouteInfoPanel intendedRouteInfoPanel = new IntendedRouteInfoPanel();
    private final PastTrackInfoPanel pastTrackInfoPanel = new PastTrackInfoPanel();
    private MapMenu aisTargetMenu;

    @GuardedBy("targets")
    private final Map<Long, TargetGraphic> targets = new ConcurrentHashMap<>();

    private volatile boolean shouldRun = true;
    private volatile float mapScale;

    private final Thread aisThread;

    private volatile OMGraphic closest;
    private final AisTargetSelectionGraphic targetSelectionGraphic = new AisTargetSelectionGraphic();

    /**
     * Keeps the AisLayer thread alive
     */
    @Override
    public void run() {

        while (shouldRun) {
            try {
                drawTargets();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

        }
        synchronized (targets) {
            targets.clear();
        }
        synchronized (graphics) {
            graphics.clear();
            graphics.add(targetSelectionGraphic);
        }
    }

    /**
     * Starts the AisLayer thread
     */
    public AisLayer() {
        synchronized (graphics) {
            graphics.add(targetSelectionGraphic);
        }
        aisThread = new Thread(this);
        aisThread.start();
    }

    public Thread getAisThread() {
        return aisThread;
    }

    /**
     * Kills the AisLayer thread
     */
    public void stop() {
        shouldRun = false;
    }

    /**
     * Clears all targets from the map and in the local memory
     */
    public void mapClearTargets() {
        synchronized (graphics) {
            graphics.clear();
            graphics.add(targetSelectionGraphic);
        }
        synchronized (targets) {
            targets.clear();
        }
    }

    public void removeSelection() {
        targetSelectionGraphic.setVisible(false);
        
        mainFrame.setSelectedMMSI(-1);

        statusArea.removeHighlight();

        doPrepare();
    }

    /**
     * Check if vessel is near map coordinates or it's
     * sending an intended route
     * @param mobileTarget
     * @return if the target should be included
     */
    private boolean drawTarget(MobileTarget mobileTarget) {
        Point2D lr = chartPanel.getMap().getProjection().getLowerRight();
        Point2D ul = chartPanel.getMap().getProjection().getUpperLeft();
        Position pos = mobileTarget.getPositionData().getPos();
        
        boolean t1 = pos.getLatitude() >= lr.getY();
        boolean t2 = pos.getLatitude() <= ul.getY();
        boolean t3 = pos.getLongitude() >= ul.getX();
        boolean t4 = pos.getLongitude() <= lr.getX();
        
        if (!(t1 && t2 && t3 && t4)) {
            VesselTarget vesselTarget = (mobileTarget instanceof VesselTarget) ? (VesselTarget)mobileTarget : null;
            if (vesselTarget == null || !vesselTarget.hasIntendedRoute()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Draws or updates the supported AIS (vessels and sar) targets on the map
     */
    private void drawTargets() {
        if (aisHandler == null) {
            return;
        }

        if (chartPanel != null) {

            if (chartPanel.getMap().getScale() != mapScale) {
                mapScale = chartPanel.getMap().getScale();
                mapClearTargets();
            }

            for (MobileTarget mobileTarget : aisHandler.getMobileTargets(AisTarget.Status.OK)) {
                
                // Check if vessel is near map coordinates or it's
                // sending an intended route
                if (!drawTarget(mobileTarget)) {
                    continue;
                }

                synchronized (targets) {
                    TargetGraphic targetGraphic = targets.get(mobileTarget.getMmsi());
                    if (targetGraphic == null) {
                        if (mobileTarget instanceof VesselTarget) {
                            targetGraphic = new Vessel(mobileTarget.getMmsi());
                        } else if (mobileTarget instanceof SarTarget) {
                            targetGraphic = new SarTargetGraphic();
                        } else {
                            LOG.error("Unknown target type");
                            continue;
                        }
                        synchronized (graphics) {
                            graphics.add(targetGraphic);
                        }
                        targets.put(mobileTarget.getMmsi(), targetGraphic);
                    }
                    
                    // Update the target graphics
                    targetGraphic.update(mobileTarget, null, null, mapScale);

                    if (mobileTarget.getMmsi() == mainFrame.getSelectedMMSI()) {
                        targetSelectionGraphic.moveSymbol(mobileTarget.getPositionData().getPos());
                        setStatusAreaTxt();
                    }                        
                }
            }
        }

        doPrepare();

    }

    @Override
    public OMGraphicList prepare() {
        synchronized (graphics) {
            graphics.project(getProjection());
        }
        return graphics;
    }

    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            aisHandler.addListener(this);
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
        }
        if (obj instanceof StatusArea) {
            statusArea = (StatusArea) obj;
        }
        if (obj instanceof JMapFrame) {
            jMapFrame = (JMapFrame) obj;
            // highlightInfoPanel = new HighlightInfoPanel();
            // jMapFrame.getGlassPanel().add(highlightInfoPanel);
            aisInfoPanel = new AisInfoPanel();
            jMapFrame.getGlassPanel().add(aisInfoPanel);
            jMapFrame.getGlassPanel().add(intendedRouteInfoPanel);
            jMapFrame.getGlassPanel().add(pastTrackInfoPanel);
            jMapFrame.getGlassPanel().setVisible(true);
        }
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
        }
        if (obj instanceof MapMenu) {
            aisTargetMenu = (MapMenu) obj;
        }
    }

    @Override
    public void findAndUndo(Object obj) {
        if (obj == aisHandler) {
            aisHandler.removeListener(this);
        }
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = DragMouseMode.MODEID; // "DragMouseMode"
        ret[1] = NavigationMouseMode.MODEID; // "ZoomMouseMode"
        ret[2] = SelectMouseMode.MODEID; // "SelectMouseMode"
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        
        OMGraphic newClosest = MapEventUtils.getSelectedGraphic(
                graphics, 
                e, 
                3.0f, 
                VesselLayer.class,
                IntendedRouteWpCircle.class,
                IntendedRouteLegGraphic.class,
                SartGraphic.class);

        if (e.getButton() == MouseEvent.BUTTON1) {

            removeSelection();

            if (newClosest != null && newClosest instanceof VesselLayer) {
                synchronized (targets) {
                    long mmsi = ((VesselLayer) newClosest).getMMSI();
                    mainFrame.setSelectedMMSI(mmsi);

                    targetSelectionGraphic.setVisible(true);

                    targetSelectionGraphic.moveSymbol(Position.create(((VesselLayer) newClosest).getLat(),
                            ((VesselLayer) newClosest).getLon()));
                }

                doPrepare();

                setStatusAreaTxt();

            }

        }

        if (e.getButton() == MouseEvent.BUTTON3 && newClosest != null) {

            if (newClosest instanceof VesselLayer) {

                VesselLayer vesselLayer = (VesselLayer) newClosest;
                aisTargetMenu.aisMenu(vesselLayer.getVessel().getVesselTarget());
                aisTargetMenu.setVisible(true);
                aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;

            } else if (newClosest instanceof IntendedRouteWpCircle) {

                IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) newClosest;
                VesselTarget vesselTarget = wpCircle.getIntendedRouteGraphic().getVesselTarget();
                aisTargetMenu.aisSuggestedRouteMenu(vesselTarget);
                aisTargetMenu.setVisible(true);
                aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
                
            } else if (newClosest instanceof IntendedRouteLegGraphic) {

                IntendedRouteLegGraphic wpCircle = (IntendedRouteLegGraphic) newClosest;
                VesselTarget vesselTarget = wpCircle.getIntendedRouteGraphic().getVesselTarget();
                aisTargetMenu.aisSuggestedRouteMenu(vesselTarget);
                aisTargetMenu.setVisible(true);
                aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
                
            } else if (newClosest instanceof SartGraphic) {

                SartGraphic sartGraphic = (SartGraphic) newClosest;
                SarTarget sarTarget = sartGraphic.getSarTargetGraphic().getSarTarget();
                aisTargetMenu.sartMenu(this, sarTarget);
                aisTargetMenu.setVisible(true);
                aisTargetMenu.show(this, e.getX() - 2, e.getY() - 2);
                return true;
            }

        }
        return false;
    }

    /**
     * Returns the {@code Vessel} with the given mmsi, or null if not found
     * @param mmsi the mmsi of the vessel
     * @return the vessel or null if not found
     */
    private Vessel getVessel(Long mmsi) {
        synchronized (targets) {
            TargetGraphic target = this.targets.get(mmsi);
            if (target != null && target instanceof Vessel) {
                return (Vessel)target;
            }
        }
        return null;
    }
    
    private void setStatusAreaTxt() {
        HashMap<String, String> info = new HashMap<String, String>();
        Vessel vessel = getVessel(mainFrame.getSelectedMMSI());
        if (vessel != null) {

            info.put("MMSI", Long.toString(vessel.getMMSI()));
            info.put("Name", vessel.getName());
            info.put("COG", vessel.getHeading());
            info.put("Call sign", vessel.getCallSign());
            info.put("LAT", vessel.getLat());
            info.put("LON", vessel.getLon());
            info.put("SOG", vessel.getSog());
            info.put("ETA", vessel.getEta());
            info.put("Type", vessel.getShipType());
            statusArea.receiveHighlight(info, vessel.getMMSI());

            // statusArea.receiveHighlight(info, vessel.getMMSI());
        }
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest;
        synchronized (graphics) {
            allClosest = graphics.findAll(e.getX(), e.getY(), 3.0f);
        }
        for (OMGraphic omGraphic : allClosest) {
            newClosest = omGraphic;
            break;
        }

        if (allClosest.size() == 0) {
            aisInfoPanel.setVisible(false);
            intendedRouteInfoPanel.setVisible(false);
            pastTrackInfoPanel.setVisible(false);
            closest = null;
            return false;
        }

        if (newClosest != closest) {
            Point containerPoint = SwingUtilities.convertPoint(chartPanel, e.getPoint(), jMapFrame);

            if (newClosest instanceof PastTrackWpCircle) {
                closest = newClosest;
                PastTrackWpCircle wpCircle = (PastTrackWpCircle) newClosest;
                pastTrackInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                pastTrackInfoPanel.showWpInfo(wpCircle);
                pastTrackInfoPanel.setVisible(true);
            }

            if (newClosest instanceof IntendedRouteWpCircle) {
                closest = newClosest;
                IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) newClosest;
                intendedRouteInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                intendedRouteInfoPanel.showWpInfo(wpCircle);
            }

            if (newClosest instanceof VesselLayer) {
                jMapFrame.getGlassPane().setVisible(true);
                closest = newClosest;
                VesselLayer vessel = (VesselLayer) newClosest;
                int x = (int) containerPoint.getX() + 10;
                int y = (int) containerPoint.getY() + 10;
                synchronized (targets) {
                    aisInfoPanel.showAisInfo(getVessel(vessel.getMMSI()));
                }                
                if (chartPanel.getMap().getProjection().getWidth() - x < aisInfoPanel.getWidth()) {
                    x -= aisInfoPanel.getWidth() + 20;
                }
                if (chartPanel.getMap().getProjection().getHeight() - y < aisInfoPanel.getHeight()) {
                    y -= aisInfoPanel.getHeight() + 20;
                }
                aisInfoPanel.setPos(x, y);
                aisInfoPanel.setVisible(true);

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        return false;
    }

    @Override
    public void mouseMoved() {
    }

    @Override
    public boolean mousePressed(MouseEvent arg0) {
        return false;
    }

    @Override
    public void targetUpdated(AisTarget arg0) {
    }
}
