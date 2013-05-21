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
