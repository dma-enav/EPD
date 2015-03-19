/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.service;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.LocalEndpoint;

import java.util.List;
import java.util.function.Predicate;

/**
 * Utility methods for converting between different representations of MMSI identifiers and for searching lists of Maritime Cloud
 * services based on MMSI
 */
@SuppressWarnings("unused")
public class MaritimeCloudUtils {

    public static final String STCC_MMSI_PREFIX = "999";

    /**
     * Returns if the given maritime id is from a sea traffic control center
     * 
     * @param id
     *            the maritime id to check
     * @return if the id is from a sea traffic control center
     */
    public static boolean isSTCC(MaritimeId id) {
        return id != null && (id.toString().startsWith("mmsi://" + STCC_MMSI_PREFIX) || id.toString().startsWith("mmsi:" + STCC_MMSI_PREFIX));
    }

    /**
     * Returns if the given maritime id is from a ship, i.e. not from a sea traffic control center
     * 
     * @param id
     *            the maritime id to check
     * @return if the id is from a ship
     */
    public static boolean isShip(MaritimeId id) {
        return id != null && !isSTCC(id);
    }

    /**
     * Returns a {@linkplain MaritimeId} based on the given MMSI
     * 
     * @param mmsi
     *            the MMSI to return as a {@linkplain MaritimeId}
     * @return the corresponding {@linkplain MaritimeId}
     */
    public static MaritimeId toMaritimeId(String mmsi) {
        return new MmsiId(Integer.valueOf(mmsi));
    }

    /**
     * Returns a {@linkplain MaritimeId} based on the given MMSI
     * 
     * @param mmsi
     *            the MMSI to return as a {@linkplain MaritimeId}
     * @return the corresponding {@linkplain MaritimeId}
     */
    public static MaritimeId toMaritimeId(int mmsi) {
        return new MmsiId(mmsi);
    }

    /**
     * Extracts the actual MMSI from a {@linkplain MaritimeId}
     * 
     * @param id
     *            the maritime id to extract the MMSI from
     * @return the MMSI or null, if none can be extracted
     */
    public static Long toMmsi(MaritimeId id) {
        if (id == null || !(id instanceof MmsiId)) {
            return null;
        }
        String str = id.toString();
        if (str.startsWith("mmsi://")) {
            return Long.parseLong(id.toString().split("mmsi://")[1]);
        } else if (str.startsWith("mmsi:")) {
            return Long.parseLong(id.toString().split("mmsi:")[1]);
        }
        return null;
    }

    /**
     * Finds the first service in the list with a matching MMSI
     * 
     * @param serviceList
     *            the list of services to check
     * @param mmsi
     *            the MMSI of the service to find
     * @return the matching service or null if not found
     */
    public static <E extends LocalEndpoint> E  findServiceWithMmsi(List<E> serviceList, long mmsi) {
        return findServiceWithId(serviceList, new MmsiId((int) mmsi));
    }

    /**
     * Finds the first service in the list with a matching maritime id
     * 
     * @param serviceList
     *            the list of services to check
     * @param id
     *            the maritime id of the service to find
     * @return the matching service or null if not found
     */
    public static <E extends LocalEndpoint> E findServiceWithId(List<E> serviceList, MaritimeId id) {
        if (serviceList != null) {
            for (E service : serviceList) {
                if (id.equals(service.getRemoteId())) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Returns a predicate that filters either ships or shore centers based on the parameter
     * @param ship whether to filter on ships or shore centers
     * @return the predicate
     */
    public static <E extends LocalEndpoint> Predicate<E> filterByType(boolean ship) {
        return e -> isShip(e.getRemoteId()) == ship;
    }
}
