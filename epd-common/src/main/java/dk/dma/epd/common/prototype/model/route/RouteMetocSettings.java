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
package dk.dma.epd.common.prototype.model.route;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import dk.frv.enav.common.xml.metoc.MetocDataTypes;
/**
 * Metoc settings for route
 */
public class RouteMetocSettings implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private boolean showRouteMetoc;
    private int interval = 15;
    private Set<MetocDataTypes> dataTypes = new HashSet<>();
    private Double windWarnLimit;
    private Double currentWarnLimit;
    private Double waveWarnLimit;
    
    private String provider = "dmi";
    
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public RouteMetocSettings() {
        for (MetocDataTypes dataType : MetocDataTypes.allTypes()) {
            dataTypes.add(dataType);
        }
    }

    public boolean isShowRouteMetoc() {
        return showRouteMetoc;
    }

    public void setShowRouteMetoc(boolean showRouteMetoc) {
        this.showRouteMetoc = showRouteMetoc;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Set<MetocDataTypes> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Set<MetocDataTypes> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public Double getWindWarnLimit() {
        return windWarnLimit;
    }

    public void setWindWarnLimit(Double windWarnLimit) {
        this.windWarnLimit = windWarnLimit;
    }

    public Double getWaveWarnLimit() {
        return waveWarnLimit;
    }

    public void setWaveWarnLimit(Double waveWarnLimit) {
        this.waveWarnLimit = waveWarnLimit;
    }

    public Double getCurrentWarnLimit() {
        return currentWarnLimit;
    }

    public void setCurrentWarnLimit(Double currentWarnLimit) {
        this.currentWarnLimit = currentWarnLimit;
    }

}
