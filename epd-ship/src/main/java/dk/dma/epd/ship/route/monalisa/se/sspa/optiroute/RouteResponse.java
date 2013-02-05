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


package dk.dma.epd.ship.route.monalisa.se.sspa.optiroute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import dk.dma.epd.ship.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.RouteType;


/**
 * The calculated optimal route, with ETA for each waypoint and fuel consumption for requested and final routes
 * 
 * <p>Java class for routeresponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routeresponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FuelRequested" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="FuelFinal" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="Route" type="{http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange}routeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="RouteResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RouteResponse", propOrder = {
    "fuelRequested",
    "fuelFinal",
    "route"
})
public class RouteResponse {

    @XmlElement(name = "FuelRequested")
    protected float fuelRequested;
    @XmlElement(name = "FuelFinal")
    protected float fuelFinal;
    @XmlElement(name = "Route", required = true)
    protected RouteType route;

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

}
