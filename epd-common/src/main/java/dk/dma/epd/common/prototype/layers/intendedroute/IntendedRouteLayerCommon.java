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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;
import dk.dma.epd.common.prototype.layers.GeneralLayerCommon;

/**
 * Base layer for displaying intended routes in EPDShip and EPDShore
 * 
 * @author Janus Varmarken
 */
public abstract class IntendedRouteLayerCommon extends GeneralLayerCommon implements IAisTargetListener {

    private static final long serialVersionUID = 1L;
    
    /**
     * Map from MMSI to intended route graphic.
     */
    protected ConcurrentHashMap<Long, IntendedRouteGraphic> intendedRoutes = new ConcurrentHashMap<>();  

    protected IntendedRouteInfoPanel intendedRouteInfoPanel;
    private CommonChartPanel chartPanel;
    private OMGraphic closest;
    private OMCircle dummyCircle = new OMCircle();
    
    /**
     * Called when the given AIS target is updated
     * @param aisTarget the AIS target that has been updated
     */
    @Override
    public void targetUpdated(AisTarget aisTarget) {
        boolean redraw = false;
        
        IntendedRouteGraphic intendedRoute = intendedRoutes.get(aisTarget.getMmsi());
        if(aisTarget.isGone() && intendedRoute != null) {
            // This target should no longer be painted
            intendedRoutes.remove(aisTarget.getMmsi());
            graphics.remove(intendedRoute);
            redraw = true;
        }
        else if (!aisTarget.isGone() && aisTarget instanceof VesselTarget && ((VesselTarget)aisTarget).hasIntendedRoute()) {
            // Get the needed data from model
            VesselTarget vessel = (VesselTarget) aisTarget;
            
            VesselPositionData posData = vessel.getPositionData();
            VesselStaticData staticData = vessel.getStaticData();
            VesselTargetSettings targetSettings = vessel.getSettings();
            CloudIntendedRoute cloudIntendedRoute = vessel.getIntendedRoute();
            Position pos = posData.getPos();
            
            if(intendedRoute == null) {
                // No current intended route graphic for this target - create it
                intendedRoute = new IntendedRouteGraphic();
                // add the new intended route graphic to the set of managed intended route graphics
                intendedRoutes.put(vessel.getMmsi(), intendedRoute);
                graphics.add(intendedRoute);
            }
            // Determine vessel name
            String name;
            if (staticData != null) {
                name = AisMessage.trimText(staticData.getName());
            } else {
                Long mmsi = vessel.getMmsi();
                name = "ID:" + mmsi.toString();
            }
            
            if(vessel.hasIntendedRoute()) {
                System.out.println(name + " has an intended route!");
            }
            
            // Update the graphic with the updated vessel, route and position data
            intendedRoute.update(vessel, name, cloudIntendedRoute, pos);
            // Update graphic visibility according to model
            intendedRoute.setVisible(targetSettings.isShowRoute());
            redraw = true;
        }
        
        if(redraw) {
            doPrepare();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent pe) {
        // Check if we need to display the arrow heads
        if (getProjection() != null) {
            
            boolean showArrowHeads = getProjection().getScale() < EPD.getInstance()
                    .getSettings().getNavSettings().getShowArrowScale();
            
            for (IntendedRouteGraphic intendedRoute : intendedRoutes.values()) {
                intendedRoute.showArrowHeads(showArrowHeads);
            }
        }
        super.projectionChanged(pe);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public synchronized OMGraphicList prepare() {
        if (getProjection() == null) {
            return graphics;
        }
        graphics.project(getProjection(), true);
        return graphics;
    }
    
    /**
     * {@inheritDoc}
     */
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof AisHandlerCommon) {
            // register as listener for AIS messages
            ((AisHandlerCommon)obj).addListener(this);
        } else if (obj instanceof CommonChartPanel) {
            this.chartPanel = (CommonChartPanel)obj;
        }
    }
    
    /**
     * Handle mouse moved
     */
    @Override
    public boolean mouseMoved(MouseEvent e) {

        OMGraphic newClosest = getSelectedGraphic(e, IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);

        if (newClosest != null && newClosest != closest) {
            closest = newClosest;
            Point containerPoint = SwingUtilities.convertPoint(mapBean, e.getPoint(), mainFrame);

            if (newClosest instanceof IntendedRouteWpCircle) {
                IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) newClosest;
                intendedRouteInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                intendedRouteInfoPanel.showWpInfo(wpCircle);
                intendedRouteInfoPanel.setVisible(true);
                getGlassPanel().setVisible(true);
                return true;
                
            } else if (newClosest instanceof IntendedRouteLegGraphic) {
                // lets user see ETA continually along route leg
                Point2D worldLocation = chartPanel.getMap().getProjection().inverse(e.getPoint());
                IntendedRouteLegGraphic legGraphic = (IntendedRouteLegGraphic) newClosest;
                intendedRouteInfoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                intendedRouteInfoPanel.showLegInfo(legGraphic, worldLocation);
                closest = dummyCircle;
                intendedRouteInfoPanel.setVisible(true);
                getGlassPanel().setVisible(true);
                return true;
            }
        } else if (newClosest == null) {
            closest = null;
            intendedRouteInfoPanel.setVisible(false);
        }
        return false;
    }
}
