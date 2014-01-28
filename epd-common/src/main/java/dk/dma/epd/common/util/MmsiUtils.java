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

/**
 * Utility methods for converting between different representations
 * of MMSI identifiers
 */
public class MmsiUtils {

    public static final String STCC_MMSI_PREFIX = "999";
    
    /**
     * Returns if the given maritime id is from a sea traffic 
     * control center
     * 
     * @param id the maritime id to check
     * @return if the id is from a sea traffic control center
     */
    public static boolean isSTCC(MaritimeId id) {
        return id != null && id.toString().startsWith("mmsi://" + STCC_MMSI_PREFIX);
    }
    
    /**
     * Returns a {@linkplain MaritimeId} based on the given MMSI
     * @param mmsi the MMSI to return as a {@linkplain MaritimeId}
     * @return the corresponding {@linkplain MaritimeId}
     */
    public static MaritimeId toMaritimeId(String mmsi) {
        return new MmsiId(Integer.valueOf(mmsi));
    }
    
    /**
     * Returns a {@linkplain MaritimeId} based on the given MMSI
     * @param mmsi the MMSI to return as a {@linkplain MaritimeId}
     * @return the corresponding {@linkplain MaritimeId}
     */
    public static MaritimeId toMaritimeId(int mmsi) {
        return new MmsiId(mmsi);
    }
    
    /**
     * Extracts the actual MMSI from a {@linkplain MaritimeId}
     * @param id the maritime id to extract the MMSI from
     * @return the MMSI or null, if none can be extracted
     */
    public static Integer toMmsi(MaritimeId id) {
        if (id == null || !(id instanceof MmsiId)) {
            return null;
        }
        String mmsi = id.toString().split("mmsi://")[1];
        return Integer.parseInt(mmsi);
    }
}
