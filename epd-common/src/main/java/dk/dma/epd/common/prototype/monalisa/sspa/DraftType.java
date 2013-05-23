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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "draftType", namespace = "http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange", propOrder = {
        "forward", "aft" })
public class DraftType {

    @XmlElement(name = "forward")
    @XmlSchemaType(name = "forward")
    protected Float forward;
    @XmlElement(name = "aft")
    @XmlSchemaType(name = "aft")
    protected Float aft;

    public Float getForward() {
        return forward;
    }

    public void setForward(Float forward) {
        this.forward = forward;
    }

    public Float getAft() {
        return aft;
    }

    public void setAft(Float aft) {
        this.aft = aft;
    }

}
