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
package dk.dma.epd.shore.voct;

import java.io.Serializable;

import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.VoctMsgStatus;

/**
 * Definition of a SRU
 * 
 * 
 */

public class SRU implements Serializable{

    private static final long serialVersionUID = 1L;

    public enum SRU_TYPE {
        // SHIP, HELICOPTER, PLANE
        Smaller_Vessel, Ship
    }

    public enum sru_status {
        AVAILABLE, UNAVAILABLE, INVITED, ACCEPTED, DECLINED, UNKNOWN, LOCALONLY
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

    private VoctMsgStatus voctMsgStatus;
    private CloudMessageStatus cloudStatus;

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
        voctMsgStatus = VoctMsgStatus.UNKNOWN;
        cloudStatus = CloudMessageStatus.NOT_SENT;
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
     * @return the voctMsgStatus
     */
    public VoctMsgStatus getVoctMsgStatus() {
        return voctMsgStatus;
    }

    /**
     * @param voctMsgStatus the voctMsgStatus to set
     */
    public void setVoctMsgStatus(VoctMsgStatus voctMsgStatus) {
        this.voctMsgStatus = voctMsgStatus;
    }

    /**
     * @return the cloudStatus
     */
    public CloudMessageStatus getCloudStatus() {
        return cloudStatus;
    }

    /**
     * @param cloudStatus the cloudStatus to set
     */
    public void setCloudStatus(CloudMessageStatus cloudStatus) {
        this.cloudStatus = cloudStatus;
    }

    @Override
    public String toString() {
        return "SRU [name=" + name + ", mmsi=" + mmsi + ", type=" + type + ", status=" + status + ", visible=" + visible
                + ", searchSpeed=" + searchSpeed + ", visibility=" + visibility + ", fatigue=" + fatigue + ", searchTime="
                + searchTime + "]";
    }

}
