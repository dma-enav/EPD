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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the se.sspa.optiroute package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName ROUTE_RESPONSE_QNAME = new QName("http://www.sspa.se/optiroute", "RouteResponse");
    private static final QName ROUTE_REQUEST_QNAME = new QName("http://www.sspa.se/optiroute", "RouteRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: se.sspa.optiroute
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RouteRequest }
     * 
     */
    public RouteRequest createRouteRequest() {
        return new RouteRequest();
    }

    /**
     * Create an instance of {@link DirectionSpeedType }
     * 
     */
    public DirectionSpeedType createDirectionSpeedType() {
        return new DirectionSpeedType();
    }

    /**
     * Create an instance of {@link CurrentShipDataType }
     * 
     */
    public CurrentShipDataType createCurrentShipDataType() {
        return new CurrentShipDataType();
    }

    /**
     * Create an instance of {@link WeatherpointType }
     * 
     */
    public WeatherpointType createWeatherpointType() {
        return new WeatherpointType();
    }

    /**
     * Create an instance of {@link WaveDataType }
     * 
     */
    public WaveDataType createWaveDataType() {
        return new WaveDataType();
    }

    /**
     * Create an instance of {@link SpeedType }
     * 
     */
    public SpeedType createSpeedType() {
        return new SpeedType();
    }

    /**
     * Create an instance of {@link WeatherPointsType }
     * 
     */
    public WeatherPointsType createWeatherPointsType() {
        return new WeatherPointsType();
    }

    /**
     * Create an instance of {@link DepthPointType }
     * 
     */
    public DepthPointType createDepthPointType() {
        return new DepthPointType();
    }

    /**
     * Create an instance of {@link RouteResponse }
     * 
     */
    public RouteResponse createRouteResponse() {
        return new RouteResponse();
    }

    /**
     * Create an instance of {@link DepthPointsType }
     * 
     */
    public DepthPointsType createDepthPointsType() {
        return new DepthPointsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sspa.se/optiroute", name = "RouteResponse")
    public JAXBElement<RouteResponse> createRouteResponse(RouteResponse value) {
        return new JAXBElement<>(ROUTE_RESPONSE_QNAME, RouteResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sspa.se/optiroute", name = "RouteRequest")
    public JAXBElement<RouteRequest> createRouteRequest(RouteRequest value) {
        return new JAXBElement<>(ROUTE_REQUEST_QNAME, RouteRequest.class, null, value);
    }

}
