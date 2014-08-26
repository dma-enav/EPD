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

import java.util.ArrayList;
import java.util.List;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.service.ServiceEndpoint;

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
    public static Integer toMmsi(MaritimeId id) {
        if (id == null || !(id instanceof MmsiId)) {
            return null;
        }
        String mmsi = id.toString().split("mmsi://")[1];
        return Integer.parseInt(mmsi);
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
    public static <E, T> ServiceEndpoint<E, T> findServiceWithMmsi(List<ServiceEndpoint<E, T>> serviceList, long mmsi) {
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
    public static <E, T> ServiceEndpoint<E, T> findServiceWithId(List<ServiceEndpoint<E, T>> serviceList, MaritimeId id) {
        if (serviceList != null) {
            for (ServiceEndpoint<E, T> service : serviceList) {
                if (id.equals(service.getId())) {
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
    public static <E, T> ServiceEndpoint<E, T> findSTCCService(List<ServiceEndpoint<E, T>> serviceList) {
        if (serviceList != null) {
            for (ServiceEndpoint<E, T> service : serviceList) {
                if (isSTCC(service.getId())) {
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
    public static <E, T> List<ServiceEndpoint<E, T>> findSTCCServices(List<ServiceEndpoint<E, T>> serviceList) {

        List<ServiceEndpoint<E, T>> returnList = new ArrayList<ServiceEndpoint<E, T>>();

        if (serviceList != null) {
            for (ServiceEndpoint<E, T> service : serviceList) {
                if (isSTCC(service.getId())) {
                    returnList.add(service);
                }
            }
        }
        return returnList;
    }
}
