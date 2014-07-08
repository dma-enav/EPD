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
package dk.dma.epd.common.prototype.monalisa.sspa;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for waypointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waypointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="wpt-id" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="ETA" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="wpt-name" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="position" type="{http://www.sspa.se/voyage-optimizer}PositionType"/>
 *         &lt;element name="leg-info" type="{http://www.sspa.se/voyage-optimizer}leginfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waypointType", namespace = "http://www.sspa.se/voyage-optimizer", propOrder = {
    "wptId",
    "position",
    "eta",
    "fixed",
    "wptName",
    "legInfo"
})
public class WaypointType {

    @XmlElement(name = "wpt-id", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected Integer wptId;
    @XmlElement(name = "ETA")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eta;
    @XmlElement(name = "fixed")
    protected boolean fixed;
    @XmlElement(name = "wpt-name")
    protected String wptName;
    @XmlElement(required = true)
    protected PositionType position;
    @XmlElement(name = "leg-info")
    protected LeginfoType legInfo;

    /**
     * Gets the value of the wptId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public Integer getWptId() {
        return wptId;
    }

    /**
     * Sets the value of the wptId property.
     * 
     * @param i
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setWptId(int i) {
        this.wptId = i;
    }

    /**
     * Gets the value of the eta property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getETA() {
        return eta;
    }

    /**
     * Sets the value of the eta property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setETA(XMLGregorianCalendar value) {
        this.eta = value;
    }

    /**
     * Gets the value of the fixed property.
     * 
     * @return
     *     possible object is
     *     {@link boolean }
     *     
     */
    public boolean getFixed() {
        return fixed;
    }

    /**
     * Sets the value of the fixed property.
     * 
     * @param value
     *     allowed object is
     *     {@link boolean }
     *     
     */
    public void setFixed(boolean value) {
        this.fixed = value;
    }

    /**
     * Gets the value of the wptName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWptName() {
        return wptName;
    }

    /**
     * Sets the value of the wptName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWptName(String value) {
        this.wptName = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setPosition(PositionType value) {
        this.position = value;
    }

    /**
     * Gets the value of the legInfo property.
     * 
     * @return
     *     possible object is
     *     {@link LeginfoType }
     *     
     */
    public LeginfoType getLegInfo() {
        return legInfo;
    }

    /**
     * Sets the value of the legInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link LeginfoType }
     *     
     */
    public void setLegInfo(LeginfoType value) {
        this.legInfo = value;
    }

}
