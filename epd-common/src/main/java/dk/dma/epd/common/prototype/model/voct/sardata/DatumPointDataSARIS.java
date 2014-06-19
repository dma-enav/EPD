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
package dk.dma.epd.common.prototype.model.voct.sardata;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.DatumPointSARISDTO;
import dk.dma.enav.model.voct.SARAreaData;
import dk.dma.enav.model.voct.SARISTarget;
import dk.dma.enav.model.voct.WeatherDataDTO;

public class DatumPointDataSARIS extends SARData {

    private static final long serialVersionUID = 1L;

    private List<SARISTarget> sarisTarget;
    private List<SARWeatherData> sarWeatherData;
    private List<SARAreaData> sarAreaData;

    public DatumPointDataSARIS(String sarID, DateTime TLKP, DateTime CSS, Position LKP, double x, double y, double safetyFactor,
            int searchObject) {
        super(sarID, TLKP, CSS, LKP, x, y, safetyFactor, searchObject);
        this.setCSP(LKP);
    }

    /**
     * @return the sarisTarget
     */
    public List<SARISTarget> getSarisTarget() {
        return sarisTarget;
    }

    /**
     * @param sarisTarget
     *            the sarisTarget to set
     */
    public void setSarisTarget(List<SARISTarget> sarisTarget) {
        this.sarisTarget = sarisTarget;
    }

    /**
     * @return the sarWeatherData
     */
    public List<SARWeatherData> getSarWeatherData() {
        return sarWeatherData;
    }

    /**
     * @param sarWeatherData
     *            the sarWeatherData to set
     */
    public void setSarWeatherData(List<SARWeatherData> sarWeatherData) {
        this.sarWeatherData = sarWeatherData;
    }

    /**
     * @return the sarAreaData
     */
    public List<SARAreaData> getSarAreaData() {
        return sarAreaData;
    }

    /**
     * @param sarAreaData
     *            the sarAreaData to set
     */
    public void setSarAreaData(List<SARAreaData> sarAreaData) {
        this.sarAreaData = sarAreaData;
    }

    public DatumPointSARISDTO getModelData() {

        // private List<SARISTarget> sarisTarget;
        // private List<SARWeatherData> sarWeatherData;
        // private List<SARAreaData> sarAreaData;

        List<WeatherDataDTO> weatherList = new ArrayList<WeatherDataDTO>();

        for (int i = 0; i < getWeatherPoints().size(); i++) {
            weatherList.add(getWeatherPoints().get(i).getDTO());
        }

        return new DatumPointSARISDTO(getSarID(), this.getLKPDate().toDate(), this.getCSSDate().toDate(), this.getLKP().getDTO(),
                this.getCSP().getDTO(), this.getX(), this.getY(), this.getSafetyFactor(), this.getSearchObject(), this.getLKP()
                        .getDTO(), 0, 0, 0, 0, 0, 0, 0, weatherList, sarisTarget, sarAreaData);
    }
}
