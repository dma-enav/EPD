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

package dk.dma.epd.ship.route.sspa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NGNodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NGNodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ngposition" type="{http://www.sspa.se/optiroute}NGPositionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NGNodeType", propOrder = {
    "ngposition"
})
public class NGNodeType {

    @XmlElement(required = true)
    protected NGPositionType ngposition;

    /**
     * Gets the value of the ngposition property.
     * 
     * @return
     *     possible object is
     *     {@link NGPositionType }
     *     
     */
    public NGPositionType getNgposition() {
        return ngposition;
    }

    /**
     * Sets the value of the ngposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link NGPositionType }
     *     
     */
    public void setNgposition(NGPositionType value) {
        this.ngposition = value;
    }

}
