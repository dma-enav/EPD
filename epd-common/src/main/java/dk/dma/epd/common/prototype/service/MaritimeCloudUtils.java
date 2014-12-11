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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for converting between different representations of MMSI identifiers and for searching lists of Maritime Cloud
 * services based on MMSI
 */
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
        return id != null && id.toString().startsWith("mmsi://" + STCC_MMSI_PREFIX);
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
     * Finds the first STCC (sea traffic control center) in the list
     * 
     * @param serviceList
     *            the list of services to check
     * @return an STCC service or null if not found
     */
    public static LocalEndpoint findSTCCService(List<? extends LocalEndpoint> serviceList) {
        if (serviceList != null) {
            for (LocalEndpoint service : serviceList) {
                if (isSTCC(service.getRemoteId())) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Finds all STCC (sea traffic control centers) in the list
     * 
     * @param serviceList
     *            the list of services to check
     * @return all STCC services in a list or empty list if none exists
     */
    public static List<LocalEndpoint> findSTCCServices(List<? extends LocalEndpoint> serviceList) {

        List<LocalEndpoint> returnList = new ArrayList<>();

        if (serviceList != null) {
            for (LocalEndpoint service : serviceList) {
                if (isSTCC(service.getRemoteId())) {
                    returnList.add(service);
                }
            }
        }
        return returnList;
    }
}
