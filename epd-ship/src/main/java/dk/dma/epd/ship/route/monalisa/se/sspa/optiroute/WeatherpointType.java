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
import javax.xml.bind.annotation.XmlType;

import dk.dma.epd.ship.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.PositionType;


/**
 * <p>Java class for weatherpointType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="weatherpointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="position" type="{http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange}PositionType"/>
 *         &lt;element name="winddata" type="{http://www.sspa.se/optiroute}DirectionSpeedType"/>
 *         &lt;element name="wavedata" type="{http://www.sspa.se/optiroute}WaveDataType"/>
 *         &lt;element name="currdata" type="{http://www.sspa.se/optiroute}DirectionSpeedType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "weatherpointType", propOrder = {
    "id",
    "position",
    "winddata",
    "wavedata",
    "currdata"
})
public class WeatherpointType {

    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected PositionType position;
    @XmlElement(required = true)
    protected DirectionSpeedType winddata;
    @XmlElement(required = true)
    protected WaveDataType wavedata;
    @XmlElement(required = true)
    protected DirectionSpeedType currdata;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setPosition(PositionType value) {
        this.position = value;
    }

    /**
     * Gets the value of the winddata property.
     * 
     * @return
     *     possible object is
     *     {@link DirectionSpeedType }
     *     
     */
    public DirectionSpeedType getWinddata() {
        return winddata;
    }

    /**
     * Sets the value of the winddata property.
     * 
     * @param value
     *     allowed object is
     *     {@link DirectionSpeedType }
     *     
     */
    public void setWinddata(DirectionSpeedType value) {
        this.winddata = value;
    }

    /**
     * Gets the value of the wavedata property.
     * 
     * @return
     *     possible object is
     *     {@link WaveDataType }
     *     
     */
    public WaveDataType getWavedata() {
        return wavedata;
    }

    /**
     * Sets the value of the wavedata property.
     * 
     * @param value
     *     allowed object is
     *     {@link WaveDataType }
     *     
     */
    public void setWavedata(WaveDataType value) {
        this.wavedata = value;
    }

    /**
     * Gets the value of the currdata property.
     * 
     * @return
     *     possible object is
     *     {@link DirectionSpeedType }
     *     
     */
    public DirectionSpeedType getCurrdata() {
        return currdata;
    }

    /**
     * Sets the value of the currdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link DirectionSpeedType }
     *     
     */
    public void setCurrdata(DirectionSpeedType value) {
        this.currdata = value;
    }

}
