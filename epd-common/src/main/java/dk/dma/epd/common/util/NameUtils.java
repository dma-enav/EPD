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
package dk.dma.epd.common.util;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.model.identity.MaritimeIdentity;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;

/**
 * Utility class for resolving names for maritime entities
 */
public class NameUtils {

    public enum NameFormat { SHORT, MEDIUM, LONG };
    
    /**
     * Returns the name associated with the given maritime id.
     * 
     * @param mmsi the MMSI of the maritime entity
     * @return the name associated with the id
     */
    public static String getName(int mmsi) {
        return getType(new MmsiId(mmsi));
    }
    
    /**
     * Returns the name associated with the given maritime id.
     * 
     * @param id the maritime id
     * @return the name associated with the id
     */
    public static String getName(MaritimeId id) {
        return getName(id, NameFormat.SHORT);
    }
    
    /**
     * Returns the name associated with the given maritime id.
     * 
     * @param id the maritime id
     * @param format the name format
     * @return the name associated with the id
     */
    public static String getName(MaritimeId id, NameFormat format) {
        Integer mmsi = MaritimeCloudUtils.toMmsi(id);
        if (mmsi == null) {
            return "N/A";
        }
        // Default name is MMSI
        String name = String.valueOf(mmsi);

        // Support use outside EPD
        if (EPD.getInstance() == null) {
            return name;
        }
        
        IdentityHandler idHandler = EPD.getInstance().getIdentityHandler();
        AisHandlerCommon aisHandler = EPD.getInstance().getAisHandler();
        
        // Look up name in identityHandler and aisHandler
        if (idHandler.actorExists(mmsi.longValue())) {
            MaritimeIdentity actor = idHandler.getActor(mmsi.longValue());
            
            if (format == NameFormat.SHORT) {
                name = actor.getName();
            } else if (format == NameFormat.MEDIUM) {
                name = String.format("%s (%d)", actor.getName(), mmsi);
            } else {
                name = String.format("%s: %s (%d)", actor.getRole().toString(), actor.getName(), mmsi);
            }
            
        } else if (MaritimeCloudUtils.isSTCC(id)) {
            
            if (format == NameFormat.SHORT || format == NameFormat.MEDIUM) {
                name = String.valueOf(mmsi);
            } else {
                name = String.format("Shore: %d", mmsi);
            }
            
        } else if (MaritimeCloudUtils.isShip(id) &&
                aisHandler.getVesselTarget(mmsi.longValue()) != null &&
                aisHandler.getVesselTarget(mmsi.longValue()).getStaticData() != null) {
            
            VesselStaticData staticData = aisHandler.getVesselTarget(mmsi.longValue()).getStaticData();
            
            if (format == NameFormat.SHORT) {
                name = staticData.getTrimmedName();
            } else if (format == NameFormat.MEDIUM) {
                name = String.format("%s (%d, %s)", staticData.getTrimmedName(), mmsi, staticData.getTrimmedCallsign());
            } else {
                name = String.format("Ship: %s (%d, %s)", staticData.getTrimmedName(), mmsi, staticData.getTrimmedCallsign());
            }
            
        } else if (MaritimeCloudUtils.isShip(id)) {
            
            if (format == NameFormat.SHORT || format == NameFormat.MEDIUM) {
                name = String.valueOf(mmsi);
            } else {
                name = String.format("Ship: %d", mmsi);
            }
        }

        return name;
    }
    
    /**
     * Returns the type associated with the given maritime id.
     * 
     * @param mmsi the MMSI of the maritime entity
     * @return the type associated with the id
     */
    public static String getType(int mmsi) {
        return getType(new MmsiId(mmsi));
    }
    
    /**
     * Returns the type associated with the given maritime id.
     * 
     * @param id the maritime id
     * @return the type associated with the id
     */
    public static String getType(MaritimeId id) {
        Integer mmsi = MaritimeCloudUtils.toMmsi(id);
        if (mmsi == null) {
            return "N/A";
        }

        // Look up name in identityHandler and aisHandler, if none exists use the given one
        if (EPD.getInstance().getIdentityHandler().actorExists(mmsi.longValue())) {
            return EPD.getInstance().getIdentityHandler().getActor(mmsi.longValue()).getRole().toString();
            
        } else if (MaritimeCloudUtils.isSTCC(id)) {
            return "STCC";
        }
        return "Ship";
    }
    

}
