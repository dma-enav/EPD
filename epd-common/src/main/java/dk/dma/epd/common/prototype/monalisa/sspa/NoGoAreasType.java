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
 * A list of no-go-areas
 * 
 * <p>Java class for NoGoAreasType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NoGoAreasType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nogoarea" type="{http://www.sspa.se/voyage-optimizer}NoGoAreaType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoGoAreasType", propOrder = {
    "nogoarea"
})
public class NoGoAreasType {

    @XmlElement(required = true)
    protected NoGoAreaType nogoarea;

    /**
     * Gets the value of the nogoarea property.
     * 
     * @return
     *     possible object is
     *     {@link NoGoAreaType }
     *     
     */
    public NoGoAreaType getNogoarea() {
        return nogoarea;
    }

    /**
     * Sets the value of the nogoarea property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoGoAreaType }
     *     
     */
    public void setNogoarea(NoGoAreaType value) {
        this.nogoarea = value;
    }

}
