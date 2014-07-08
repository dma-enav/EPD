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

    public DatumPointDataSARIS(DatumPointSARISDTO sarDataDatumPointSaris) {
        super(sarDataDatumPointSaris.getSarID(), new DateTime(sarDataDatumPointSaris.getLKPDate().getTime()), new DateTime(
                sarDataDatumPointSaris.getCSSDate()), Position.create(sarDataDatumPointSaris.getLKP().getLatitude(),
                sarDataDatumPointSaris.getLKP().getLongitude()), sarDataDatumPointSaris.getX(), sarDataDatumPointSaris.getY(),
                sarDataDatumPointSaris.getSafetyFactor(), sarDataDatumPointSaris.getSearchObject());

        setSarisTarget(sarDataDatumPointSaris.getSarisTarget());
        setSarAreaData(sarDataDatumPointSaris.getSarAreaDat());

        // setWeatherPoints(sarDataDatumPointSaris.getWeatherData());

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DatumPointDataSARIS [sarisTarget=" + sarisTarget + ", sarWeatherData=" + sarWeatherData + ", sarAreaData="
                + sarAreaData + ", effortAllocationData=" + effortAllocationData + ", getAdditionalInfo()=" + getAdditionalInfo()
                + ", getFirstEffortAllocationData()=" + getFirstEffortAllocationData() + ", getEffortAllocationData()="
                + getEffortAllocationData() + ", getLKPDate()=" + getLKPDate() + ", getCSSDate()=" + getCSSDate() + ", getCSP()="
                + getCSP() + ", getX()=" + getX() + ", getY()=" + getY() + ", getSafetyFactor()=" + getSafetyFactor()
                + ", getSearchObject()=" + getSearchObject() + ", getLKP()=" + getLKP() + ", getWeatherPoints()="
                + getWeatherPoints() + ", getSarID()=" + getSarID() + ", generateHTML()=" + generateHTML() + ", getClass()="
                + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }

}
