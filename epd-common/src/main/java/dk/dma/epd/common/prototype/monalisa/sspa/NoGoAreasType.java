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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A list of no-go-areas
 * 
 * <p>Java class for NoGoAreasType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NoGoAreasType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nogoarea" type="{http://www.sspa.se/optiroute}NoGoAreaType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoGoAreasType", propOrder = {
    "nogoarea"
})
public class NoGoAreasType {

    @XmlElement(required = true)
    protected NoGoAreaType nogoarea;

    /**
     * Gets the value of the nogoarea property.
     * 
     * @return
     *     possible object is
     *     {@link NoGoAreaType }
     *     
     */
    public NoGoAreaType getNogoarea() {
        return nogoarea;
    }

    /**
     * Sets the value of the nogoarea property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoGoAreaType }
     *     
     */
    public void setNogoarea(NoGoAreaType value) {
        this.nogoarea = value;
    }

}
