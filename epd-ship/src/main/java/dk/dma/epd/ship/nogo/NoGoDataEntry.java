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
