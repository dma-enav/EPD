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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;

public class SARData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sarID;
    private DateTime LKPDate;
    private DateTime CSSDate;

    private Position LKP;
    private Position CSP;
    // EffortAllocationData effortAllocationData = new EffortAllocationData();

    List<EffortAllocationData> effortAllocationData = new ArrayList<EffortAllocationData>();

    private double x;
    private double y;
    private double safetyFactor;

    private int searchObject;

    private List<SARWeatherData> weatherPoints = new ArrayList<SARWeatherData>();
    private String additionalInfo;

    public SARData(String sarID, DateTime TLKP, DateTime CSS, Position LKP, double x, double y, double safetyFactor,
            int searchObject) {

        this.setSarID(sarID);
        this.setLKP(LKP);
        this.setLKPDate(TLKP);
        this.setCSSDate(CSS);

        this.setX(x);
        this.setY(y);
        this.setSafetyFactor(safetyFactor);
        this.setSearchObject(searchObject);
    }

    /**
     * @return the additionalInfo
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * @param additionalInfo
     *            the additionalInfo to set
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * @return the effortAllocationData
     */
    public EffortAllocationData getFirstEffortAllocationData() {

        if (effortAllocationData.size() == 0) {
            EffortAllocationData data = new EffortAllocationData();
            effortAllocationData.add(data);
        }

        return effortAllocationData.get(0);
    }

    /**
     * @param effortAllocationData
     *            the effortAllocationData to set
     */
    public void setFirstEffortAllocationData(EffortAllocationData effortAllocationData) {
        this.effortAllocationData.add(0, effortAllocationData);
    }

    public void addEffortAllocationData(EffortAllocationData data, int i) {

        // Does it already have the element? then replace it
        if (effortAllocationData.size() > i) {
            effortAllocationData.set(i, data);
        } else {
            effortAllocationData.add(data);
        }

        // for (int i = 0; i < effortAllocationData.size(); i++) {
        //
        // if (effortAllocationData.get(i) == data){
        // return;
        // }
        //
        // }
        //
        //
    }

    public void removeAllEffortAllocationData() {
        effortAllocationData.clear();
    }

    /**
     * @return the effortAllocationData
     */
    public List<EffortAllocationData> getEffortAllocationData() {
        return effortAllocationData;
    }

    /**
     * @param effortAllocationData
     *            the effortAllocationData to set
     */
    public void setEffortAllocationData(List<EffortAllocationData> effortAllocationData) {
        this.effortAllocationData = effortAllocationData;
    }

    /**
     * @return the lKPDate
     */
    public DateTime getLKPDate() {
        return LKPDate;
    }

    /**
     * @param lKPDate
     *            the lKPDate to set
     */
    public void setLKPDate(DateTime lKPDate) {
        LKPDate = lKPDate;
    }

    /**
     * @return the cSSDate
     */
    public DateTime getCSSDate() {
        return CSSDate;
    }

    /**
     * @param cSSDate
     *            the cSSDate to set
     */
    public void setCSSDate(DateTime cSSDate) {
        CSSDate = cSSDate;
    }

    /**
     * @return the cSP
     */
    public Position getCSP() {
        return CSP;
    }

    /**
     * @param cSP
     *            the cSP to set
     */
    public void setCSP(Position cSP) {
        CSP = cSP;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x
     *            the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y
     *            the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the sF
     */
    public double getSafetyFactor() {
        return safetyFactor;
    }

    /**
     * @param sF
     *            the sF to set
     */
    public void setSafetyFactor(double safetyFactor) {
        this.safetyFactor = safetyFactor;
    }

    /**
     * @return the searchObject
     */
    public int getSearchObject() {
        return searchObject;
    }

    /**
     * @param searchObject
     *            the searchObject to set
     */
    public void setSearchObject(int searchObject) {
        this.searchObject = searchObject;
    }

    /**
     * @param lKP
     *            the lKP to set
     */
    public void setLKP(Position lKP) {
        LKP = lKP;
    }

    /**
     * @return the lKP
     */
    public Position getLKP() {
        return LKP;
    }

    /**
     * @return the weatherPoints
     */
    public List<SARWeatherData> getWeatherPoints() {
        return weatherPoints;
    }

    /**
     * @param weatherPoints
     *            the weatherPoints to set
     */
    public void setWeatherPoints(List<SARWeatherData> weatherPoints) {
        this.weatherPoints = weatherPoints;
    }

    /**
     * @return the sarID
     */
    public String getSarID() {
        return sarID;
    }

    /**
     * @param sarID
     *            the sarID to set
     */
    public void setSarID(String sarID) {
        this.sarID = sarID;
    }

    public String generateHTML() {
        return "Invalid type";
    }

}
