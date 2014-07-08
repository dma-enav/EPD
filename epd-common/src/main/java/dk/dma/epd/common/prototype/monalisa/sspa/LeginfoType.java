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


/**
 * Compulsary element except for the last waypoint
 * 
 * <p>Java class for leginfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="leginfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="legtype" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="RHUMBLINE"/>
 *               &lt;enumeration value="GREAT-CIRCLE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="turn-radius" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="planned-speed" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="rhs-xte" type="{http://www.sspa.se/voyage-optimizer}XTEType" minOccurs="0"/>
 *         &lt;element name="lhs-xte" type="{http://www.sspa.se/voyage-optimizer}XTEType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "leginfoType", namespace = "http://www.sspa.se/voyage-optimizer", propOrder = {
    "legtype",
    "turnRadius",
    "plannedSpeed",
    "rhsXte",
    "lhsXte"
})
public class LeginfoType {

    protected String legtype;
    @XmlElement(name = "turn-radius")
    @XmlSchemaType(name = "positiveInteger")
    protected Integer turnRadius;
    @XmlElement(name = "planned-speed")
    protected Float plannedSpeed;
    @XmlElement(name = "rhs-xte")
    protected Integer rhsXte;
    @XmlElement(name = "lhs-xte")
    protected Integer lhsXte;

    /**
     * Gets the value of the legtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegtype() {
        return legtype;
    }

    /**
     * Sets the value of the legtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegtype(String value) {
        this.legtype = value;
    }

    /**
     * Gets the value of the turnRadius property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public Integer getTurnRadius() {
        return turnRadius;
    }

    /**
     * Sets the value of the turnRadius property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTurnRadius(Integer value) {
        this.turnRadius = value;
    }

    /**
     * Gets the value of the plannedSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPlannedSpeed() {
        return plannedSpeed;
    }

    /**
     * Sets the value of the plannedSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPlannedSpeed(Float value) {
        this.plannedSpeed = value;
    }

    /**
     * Gets the value of the rhsXte property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRhsXte() {
        return rhsXte;
    }

    /**
     * Sets the value of the rhsXte property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRhsXte(Integer value) {
        this.rhsXte = value;
    }

    /**
     * Gets the value of the lhsXte property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLhsXte() {
        return lhsXte;
    }

    /**
     * Sets the value of the lhsXte property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLhsXte(Integer value) {
        this.lhsXte = value;
    }

}
