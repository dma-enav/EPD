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

import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon.CloudMessageStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * Can be used as a base class for data sent to or received by the Maritime Cloud
 */
public class MaritimeCloudData implements Serializable {

    protected CloudMessageStatus cloudMessageStatus;
    protected Date sendDate;

    /**
     * No-arg constructor
     */
    public MaritimeCloudData() {
        this(null, new Date());
    }

    /**
     * Constructor
     * @param cloudMessageStatus the Maritime Cloud status of the data
     * @param sendDate the send date
     */
    public MaritimeCloudData(CloudMessageStatus cloudMessageStatus, Date sendDate) {
        this.cloudMessageStatus = cloudMessageStatus;
        this.sendDate = sendDate;
    }

    /**
     * @return the cloudMessageStatus
     */
    public CloudMessageStatus getCloudMessageStatus() {
        return cloudMessageStatus;
    }

    /**
     * @param newStatus the cloudMessageStatus to set
     */
    public void setCloudMessageStatus(CloudMessageStatus newStatus) {
        this.cloudMessageStatus = newStatus.combine(cloudMessageStatus);
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
}
