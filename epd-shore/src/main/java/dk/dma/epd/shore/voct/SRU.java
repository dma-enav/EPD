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
package dk.dma.epd.shore.voct;

import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.SRU_NETWORK_STATUS;

/**
 * Definition of a SRU
 * 
 * 
 */

public class SRU {

    public enum SRU_TYPE {
        // SHIP, HELICOPTER, PLANE
        Smaller_Vessel, Ship
    }

    public enum sru_status {
        AVAILABLE, UNAVAILABLE, INVITED, ACCEPTED, DECLINED, UNKNOWN, LOCALONLY, RECIEVED_BY_CLIENT
    }

    private String name;
    private long mmsi;
    private SRU_TYPE type;
    private sru_status status;
    private boolean visible;
    private double searchSpeed;
    private int visibility;
    private double fatigue;
    private int searchTime;

    private SRU_NETWORK_STATUS networkStatus;

    public SRU(String name, long mmsi, SRU_TYPE type, sru_status status, double searchSpeed, int visibility, double fatigue,
            int searchTime) {

        this.name = name;
        this.mmsi = mmsi;
        this.type = type;
        this.status = status;
        this.visible = true;
        this.searchSpeed = searchSpeed;
        this.visibility = visibility;
        this.fatigue = fatigue;
        this.searchTime = searchTime;
        networkStatus = SRU_NETWORK_STATUS.NOT_SENT;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mmsi
     */
    public long getMmsi() {
        return mmsi;
    }

    /**
     * @param mmsi
     *            the mmsi to set
     */
    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }

    /**
     * @return the type
     */
    public SRU_TYPE getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(SRU_TYPE type) {
        this.type = type;
    }

    /**
     * @return the status
     */
    public sru_status getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(sru_status status) {
        this.status = status;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible
     *            the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the searchSpeed
     */
    public double getSearchSpeed() {
        return searchSpeed;
    }

    /**
     * @param searchSpeed
     *            the searchSpeed to set
     */
    public void setSearchSpeed(double searchSpeed) {
        this.searchSpeed = searchSpeed;
    }

    /**
     * @return the visibility
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * @param visibility
     *            the visibility to set
     */
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the fatigue
     */
    public double getFatigue() {
        return fatigue;
    }

    /**
     * @param fatigue
     *            the fatigue to set
     */
    public void setFatigue(double fatigue) {
        this.fatigue = fatigue;
    }

    /**
     * @return the searchTime
     */
    public int getSearchTime() {
        return searchTime;
    }

    /**
     * @param searchTime
     *            the searchTime to set
     */
    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    /**
     * @return the networkStatus
     */
    public SRU_NETWORK_STATUS getNetworkStatus() {
        return networkStatus;
    }

    /**
     * @param networkStatus
     *            the networkStatus to set
     */
    public void setNetworkStatus(SRU_NETWORK_STATUS networkStatus) {
        this.networkStatus = networkStatus;
    }

    @Override
    public String toString() {
        return "SRU [name=" + name + ", mmsi=" + mmsi + ", type=" + type + ", status=" + status + ", visible=" + visible
                + ", searchSpeed=" + searchSpeed + ", visibility=" + visibility + ", fatigue=" + fatigue + ", searchTime="
                + searchTime + "]";
    }

}
