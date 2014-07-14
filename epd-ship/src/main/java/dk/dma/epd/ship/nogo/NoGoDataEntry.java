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
package dk.dma.epd.ship.nogo;

import java.util.List;

import org.joda.time.DateTime;

import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class NoGoDataEntry {

    private List<NogoPolygon> nogoPolygons;
    private DateTime validFrom;
    private DateTime validTo;
    private int noGoErrorCode;
    private String noGoMessage;

    /**
     * @param nogoPolygons
     * @param validFrom
     * @param validTo
     * @param noGoErrorCode
     * @param noGoMessage
     */
    public NoGoDataEntry(DateTime validFrom, DateTime validTo) {
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    /**
     * @return the nogoPolygons
     */
    public List<NogoPolygon> getNogoPolygons() {
        return nogoPolygons;
    }

    /**
     * @param nogoPolygons
     *            the nogoPolygons to set
     */
    public void setNogoPolygons(List<NogoPolygon> nogoPolygons) {
        this.nogoPolygons = nogoPolygons;
    }

    /**
     * @return the validFrom
     */
    public DateTime getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom
     *            the validFrom to set
     */
    public void setValidFrom(DateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public DateTime getValidTo() {
        return validTo;
    }

    /**
     * @param validTo
     *            the validTo to set
     */
    public void setValidTo(DateTime validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the noGoErrorCode
     */
    public int getNoGoErrorCode() {
        return noGoErrorCode;
    }

    /**
     * @param noGoErrorCode
     *            the noGoErrorCode to set
     */
    public void setNoGoErrorCode(int noGoErrorCode) {
        this.noGoErrorCode = noGoErrorCode;
    }

    /**
     * @return the noGoMessage
     */
    public String getNoGoMessage() {
        return noGoMessage;
    }

    /**
     * @param noGoMessage
     *            the noGoMessage to set
     */
    public void setNoGoMessage(String noGoMessage) {
        this.noGoMessage = noGoMessage;
    }

}
