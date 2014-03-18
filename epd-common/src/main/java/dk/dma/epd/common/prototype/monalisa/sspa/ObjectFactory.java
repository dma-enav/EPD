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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the sspa package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName ROUTERRESPONSEQNAME = new QName(
            "http://www.sspa.se/voyage-optimizer", "RouteResponse");
    private static final QName ROUTEREQUESTQNAME = new QName(
            "http://www.sspa.se/voyage-optimizer", "RouteRequest");
    private static final QName ROUTEQNAME = new QName(
            "http://www.sspa.se/voyage-optimizer",
            "route");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: sspa
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RouteresponseType }
     * 
     */
    public RouteresponseType createRouteresponseType() {
        return new RouteresponseType();
    }

    /**
     * Create an instance of {@link RouterequestType }
     * 
     */
    public RouterequestType createRouterequestType() {
        return new RouterequestType();
    }

    /**
     * Create an instance of {@link CurrentShipDataType }
     * 
     */
    public CurrentShipDataType createCurrentShipDataType() {
        return new CurrentShipDataType();
    }

    /**
     * Create an instance of {@link NGNodeType }
     * 
     */
    public NGNodeType createNGNodeType() {
        return new NGNodeType();
    }

    /**
     * Create an instance of {@link NoGoAreasType }
     * 
     */
    public NoGoAreasType createNoGoAreasType() {
        return new NoGoAreasType();
    }

    /**
     * Create an instance of {@link NGPositionType }
     * 
     */
    public NGPositionType createNGPositionType() {
        return new NGPositionType();
    }

    /**
     * Create an instance of {@link NoGoAreaType }
     * 
     */
    public NoGoAreaType createNoGoAreaType() {
        return new NoGoAreaType();
    }

    /**
     * Create an instance of {@link RouteType }
     * 
     */
    public RouteType createRouteType() {
        return new RouteType();
    }

    /**
     * Create an instance of {@link LeginfoType }
     * 
     */
    public LeginfoType createLeginfoType() {
        return new LeginfoType();
    }

    /**
     * Create an instance of {@link PositionType }
     * 
     */
    public PositionType createPositionType() {
        return new PositionType();
    }

    /**
     * Create an instance of {@link WaypointsType }
     * 
     */
    public WaypointsType createWaypointsType() {
        return new WaypointsType();
    }

    /**
     * Create an instance of {@link WaypointType }
     * 
     */
    public WaypointType createWaypointType() {
        return new WaypointType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link RouteresponseType }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sspa.se/voyage-optimizer", name = "RouteResponse")
    public JAXBElement<RouteresponseType> createRouteResponse(
            RouteresponseType value) {
        return new JAXBElement<RouteresponseType>(ROUTERRESPONSEQNAME,
                RouteresponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link RouterequestType }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sspa.se/voyage-optimizer", name = "RouteRequest")
    public JAXBElement<RouterequestType> createRouteRequest(
            RouterequestType value) {
        return new JAXBElement<RouterequestType>(ROUTEREQUESTQNAME,
                RouterequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RouteType }
     * {@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://www.sspa.se/voyage-optimizer", name = "route")
    public JAXBElement<RouteType> createRoute(RouteType value) {
        return new JAXBElement<RouteType>(ROUTEQNAME, RouteType.class, null,
                value);
    }

}
