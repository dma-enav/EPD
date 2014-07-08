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
package dk.dma.epd.shore.voyage;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A serializable class for storing route information
 */
public class VoyageStore implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Voyage> voyages = new LinkedList<Voyage>();

    public VoyageStore(VoyageManager voyageManager) {
        this.voyages = voyageManager.getVoyages();
    }

    public List<Voyage> getVoyages() {
        return voyages;
    }

    public void setVoyages(List<Voyage> voyages) {
        this.voyages = voyages;
    }

}
