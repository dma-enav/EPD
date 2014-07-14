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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Information required by the route optimization in order to calculate the optimal route
 * 
 * <p>Java class for routerequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routerequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Route" type="{http://www.sspa.se/voyage-optimizer}routeType"/>
 *         &lt;element name="ShipData" type="{http://www.sspa.se/voyage-optimizer}CurrentShipDataType"/>
 *         &lt;element name="nogoareas" type="{http://www.sspa.se/voyage-optimizer}NoGoAreasType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "RouteRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "routerequestType", propOrder = {
    "route",
    "currentShipData",
    "nogoareas"
})
public class RouterequestType {

    @XmlElement(name = "Route", required = true)
    protected RouteType route;
    @XmlElement(name = "ShipData", required = true)
    protected CurrentShipDataType currentShipData;
    protected NoGoAreasType nogoareas;

    /**
     * Gets the value of the route property.
     * 
     * @return
     *     possible object is
     *     {@link RouteType }
     *     
     */
    public RouteType getRoute() {
        return route;
    }

    /**
     * Sets the value of the route property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteType }
     *     
     */
    public void setRoute(RouteType value) {
        this.route = value;
    }

    /**
     * Gets the value of the currentShipData property.
     * 
     * @return
     *     possible object is
     *     {@link CurrentShipDataType }
     *     
     */
    public CurrentShipDataType getCurrentShipData() {
        return currentShipData;
    }

    /**
     * Sets the value of the currentShipData property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrentShipDataType }
     *     
     */
    public void setCurrentShipData(CurrentShipDataType value) {
        this.currentShipData = value;
    }

    /**
     * Gets the value of the nogoareas property.
     * 
     * @return
     *     possible object is
     *     {@link NoGoAreasType }
     *     
     */
    public NoGoAreasType getNogoareas() {
        return nogoareas;
    }

    /**
     * Sets the value of the nogoareas property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoGoAreasType }
     *     
     */
    public void setNogoareas(NoGoAreasType value) {
        this.nogoareas = value;
    }

}
