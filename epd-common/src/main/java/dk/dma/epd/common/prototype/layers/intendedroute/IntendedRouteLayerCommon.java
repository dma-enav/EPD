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

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;

/**
 * Base layer for displaying intended routes in EPDShip and EPDShore
 * 
 * @author Janus Varmarken
 */
public abstract class IntendedRouteLayerCommon extends EPDLayerCommon implements IAisTargetListener {

    private static final long serialVersionUID = 1L;
    
    /**
     * Map from MMSI to intended route graphic.
     */
    protected ConcurrentHashMap<Long, IntendedRouteGraphic> intendedRoutes = new ConcurrentHashMap<>();  

    protected IntendedRouteInfoPanel intendedRouteInfoPanel = new IntendedRouteInfoPanel();
    private CommonChartPanel chartPanel;
    private OMCircle dummyCircle = new OMCircle();
    
    /**
     * Constructor
     */
    public IntendedRouteLayerCommon() {
        super();
        
        // Automatically add info panels
        registerInfoPanel(intendedRouteInfoPanel, IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);
    }
    
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
            
            // Update the graphic with the updated vessel, route and position data
            intendedRoute.update(vessel, name, cloudIntendedRoute, pos);
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
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof IntendedRouteWpCircle) {
            intendedRouteInfoPanel.showWpInfo((IntendedRouteWpCircle) newClosest);
        } else {
            // lets user see ETA continually along route leg
            Point2D worldLocation = chartPanel.getMap().getProjection().inverse(evt.getPoint());
            intendedRouteInfoPanel.showLegInfo((IntendedRouteLegGraphic)newClosest, worldLocation);
            closest = dummyCircle;
        }
        return true;
    }
}
