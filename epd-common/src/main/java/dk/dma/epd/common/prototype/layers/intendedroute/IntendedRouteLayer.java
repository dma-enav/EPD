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

import java.util.concurrent.ConcurrentHashMap;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.layers.ais.IntendedRouteGraphic;

/**
 * Base layer for displaying intended routes in {@linkplain EPDShip} and {@linkplain EPDShore}
 * 
 * @author Janus Varmarken
 */
public class IntendedRouteLayer extends OMGraphicHandlerLayer implements IAisTargetListener {

    private static final long serialVersionUID = 1L;
    
    /**
     * Map from MMSI to intended route graphic.
     */
    private ConcurrentHashMap<Long, IntendedRouteGraphic> intendedRoutes = new ConcurrentHashMap<>();  
    
    /**
     * Called when the given AIS target is updated
     * @param aisTarget the AIS target that has been updated
     */
    @Override
    public void targetUpdated(AisTarget aisTarget) {
        boolean redraw = false;
        
        if(aisTarget.isGone() && intendedRoutes.containsKey(aisTarget.getMmsi())) {
            // This target should no longer be painted
            intendedRoutes.remove(aisTarget.getMmsi());
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
            
            IntendedRouteGraphic irg = intendedRoutes.get(aisTarget.getMmsi());
            if(irg == null) {
                // No current intended route graphic for this target - create it
                irg = new IntendedRouteGraphic();
                // add the new intended route graphic to the set of managed intended route graphics
                intendedRoutes.put(vessel.getMmsi(), irg);
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
            irg.update(vessel, name, cloudIntendedRoute, pos);
            // Update graphic visibility according to model
            irg.setVisible(targetSettings.isShowRoute());
            redraw = true;
        }
        
        if(redraw) {
            doPrepare();
        }
    }
    
    @Override
    public synchronized OMGraphicList prepare() {
       OMGraphicList toDraw = new OMGraphicList();
       toDraw.addAll(intendedRoutes.values());
        
        setList(null);

        toDraw.project(getProjection());
        
        setList(toDraw);
        
        return getList();
    }
    
    public void findAndInit(Object obj) {
        if(obj instanceof AisHandlerCommon) {
            // register as listener for AIS messages
            ((AisHandlerCommon)obj).addListener(this);
        }
    };
    
}
