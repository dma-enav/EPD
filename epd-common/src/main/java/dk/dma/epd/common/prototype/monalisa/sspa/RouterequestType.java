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
