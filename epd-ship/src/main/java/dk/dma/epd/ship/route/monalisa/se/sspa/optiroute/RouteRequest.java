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
 * Information required by the route optimization in order to calculate the optimal route
 * 
 * <p>Java class for routerequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="routerequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Route" type="{http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange}routeType"/>
 *         &lt;element name="WeatherPoints" type="{http://www.sspa.se/optiroute}WeatherPointsType"/>
 *         &lt;element name="DepthPoints" type="{http://www.sspa.se/optiroute}DepthPointsType"/>
 *         &lt;element name="CurrentShipData" type="{http://www.sspa.se/optiroute}CurrentShipDataType"/>
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
@XmlType(name = "RouteRequest", propOrder = {
    "route",
    "weatherPoints",
    "depthPoints",
    "currentShipData"
})
public class RouteRequest {

    @XmlElement(name = "Route", required = true)
    protected RouteType route;
    @XmlElement(name = "WeatherPoints", required = true)
    protected WeatherPointsType weatherPoints;
    @XmlElement(name = "DepthPoints", required = true)
    protected DepthPointsType depthPoints;
    @XmlElement(name = "CurrentShipData", required = true)
    protected CurrentShipDataType currentShipData;

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
     * Gets the value of the weatherPoints property.
     * 
     * @return
     *     possible object is
     *     {@link WeatherPointsType }
     *     
     */
    public WeatherPointsType getWeatherPoints() {
        return weatherPoints;
    }

    /**
     * Sets the value of the weatherPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link WeatherPointsType }
     *     
     */
    public void setWeatherPoints(WeatherPointsType value) {
        this.weatherPoints = value;
    }

    /**
     * Gets the value of the depthPoints property.
     * 
     * @return
     *     possible object is
     *     {@link DepthPointsType }
     *     
     */
    public DepthPointsType getDepthPoints() {
        return depthPoints;
    }

    /**
     * Sets the value of the depthPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link DepthPointsType }
     *     
     */
    public void setDepthPoints(DepthPointsType value) {
        this.depthPoints = value;
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

}
