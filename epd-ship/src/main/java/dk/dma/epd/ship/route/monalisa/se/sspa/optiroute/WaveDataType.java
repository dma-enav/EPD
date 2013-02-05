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


/**
 * <p>Java class for WaveDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WaveDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WaveDirection" type="{http://www.sspa.se/optiroute}DirectionType"/>
 *         &lt;element name="WaveHeight" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WaveDataType", propOrder = {
    "waveDirection",
    "waveHeight"
})
public class WaveDataType {

    @XmlElement(name = "WaveDirection")
    protected float waveDirection;
    @XmlElement(name = "WaveHeight")
    protected float waveHeight;

    /**
     * Gets the value of the waveDirection property.
     * 
     */
    public float getWaveDirection() {
        return waveDirection;
    }

    /**
     * Sets the value of the waveDirection property.
     * 
     */
    public void setWaveDirection(float value) {
        this.waveDirection = value;
    }

    /**
     * Gets the value of the waveHeight property.
     * 
     */
    public float getWaveHeight() {
        return waveHeight;
    }

    /**
     * Sets the value of the waveHeight property.
     * 
     */
    public void setWaveHeight(float value) {
        this.waveHeight = value;
    }

}
