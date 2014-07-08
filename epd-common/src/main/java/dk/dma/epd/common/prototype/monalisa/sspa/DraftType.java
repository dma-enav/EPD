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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "draftType", namespace = "http://www.sspa.se/voyage-optimizer", propOrder = {
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
