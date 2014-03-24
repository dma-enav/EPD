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

package dk.dma.epd.common.prototype.monalisa.sspa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Data on the ship that the route will be optimized for
 * 
 * <p>Java class for CurrentShipDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ShipDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mmsi" type="{http://www.sspa.se/voyage-optimizer}MMSIType" minOccurs="0"/>
 *         &lt;element name="imoid" type="{http://www.sspa.se/voyage-optimizer}IMOIDType" minOccurs="0"/>
 *         &lt;element name="forwarddraft" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="aftdraft" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="ukcrequested" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="optimization" type="{http://www.sspa.se/voyage-optimizer}OptimizationType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipDataType", propOrder = {
    "draft",
    "lwl",
    "beam",
    "displacement",
    "ukcrequested",
    "windage",
    "mmsi",
    "imoid",
    "optimization"
})
public class CurrentShipDataType {

    protected String mmsi;
    protected String imoid;
    protected float lwl = 100.0f;
    protected float beam = 20.0f;
    protected float windage = 1500.0f;
    protected DraftType draft;
    protected float displacement = 12000.0f;
    protected float ukcrequested;
    protected String optimization;

    /**
     * Gets the value of the mmsi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMmsi() {
        return mmsi;
    }

    /**
     * Sets the value of the mmsi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMmsi(String value) {
        this.mmsi = value;
    }

    /**
     * Gets the value of the imoid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImoid() {
        return imoid;
    }

    /**
     * Sets the value of the imoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImoid(String value) {
        this.imoid = value;
    }

    /**
     * Gets the value of the draft property.
     * 
     */
    public DraftType getDraft() {
        return draft;
    }

    /**
     * Sets the value of the draft property.
     * 
     */
    public void setDraft(DraftType value) {
        this.draft = value;
    }

    /**
     * Gets the value of the ukcrequested property.
     * 
     */
    public float getUkcrequested() {
        return ukcrequested;
    }

    /**
     * Sets the value of the ukcrequested property.
     * 
     */
    public void setUkcrequested(float value) {
        this.ukcrequested = value;
    }

    /**
     * Gets the value of the optimization property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOptimization() {
        return optimization;
    }

    /**
     * Sets the value of the optimization property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOptimization(String value) {
        this.optimization = value;
    }

}
