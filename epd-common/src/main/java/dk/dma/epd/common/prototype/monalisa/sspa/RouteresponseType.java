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
import javax.xml.bind.annotation.XmlType;


/**
 * The calculated optimal route, with ETA for each waypoint and fuel consumption for requested and final routes
 * 
 * <p>Java class for routeresponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routeresponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FuelRequested" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="FuelFinal" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="UkcActual" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Route" type="{http://www.sspa.se/voyage-optimizer}routeType"/>
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
@XmlType(name = "routeresponseType", propOrder = {
    "fuelRequested",
    "fuelFinal",
    "ukcActual",
    "route",
    "optimization"
})
public class RouteresponseType {

    @XmlElement(name = "FuelRequested")
    protected float fuelRequested;
    @XmlElement(name = "FuelFinal")
    protected float fuelFinal;
    @XmlElement(name = "UkcActual")
    protected float ukcActual;
    @XmlElement(name = "Route", required = true)
    protected RouteType route;
    protected String optimization;

    /**
     * Gets the value of the fuelRequested property.
     * 
     */
    public float getFuelRequested() {
        return fuelRequested;
    }

    /**
     * Sets the value of the fuelRequested property.
     * 
     */
    public void setFuelRequested(float value) {
        this.fuelRequested = value;
    }

    /**
     * Gets the value of the fuelFinal property.
     * 
     */
    public float getFuelFinal() {
        return fuelFinal;
    }

    /**
     * Sets the value of the fuelFinal property.
     * 
     */
    public void setFuelFinal(float value) {
        this.fuelFinal = value;
    }

    /**
     * Gets the value of the ukcActual property.
     * 
     */
    public float getUkcActual() {
        return ukcActual;
    }

    /**
     * Sets the value of the ukcActual property.
     * 
     */
    public void setUkcActual(float value) {
        this.ukcActual = value;
    }

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
