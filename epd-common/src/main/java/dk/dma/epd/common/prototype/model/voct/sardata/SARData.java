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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;

public class SARData implements Serializable {


    private static final long serialVersionUID = 1L;
    String sarID;
    private DateTime LKPDate;
    private DateTime CSSDate;

    private Position LKP;
    private Position CSP;
//    EffortAllocationData effortAllocationData = new EffortAllocationData();

    List<EffortAllocationData> effortAllocationData = new ArrayList<EffortAllocationData>();
    
    
    private double x;
    private double y;
    private double safetyFactor;
    
    private int searchObject;

    private List<SARWeatherData> weatherPoints = new ArrayList<SARWeatherData>();
    
    
    
    
    public SARData(String sarID, DateTime TLKP, DateTime CSS, Position LKP,
            double x, double y, double safetyFactor, int searchObject) {

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
     * @return the effortAllocationData
     */
    public EffortAllocationData getFirstEffortAllocationData() {
        
        if (effortAllocationData.size() == 0){
            EffortAllocationData data = new EffortAllocationData();
            effortAllocationData.add(data);
        }
        
        return effortAllocationData.get(0);
    }

    /**
     * @param effortAllocationData the effortAllocationData to set
     */
    public void setFirstEffortAllocationData(EffortAllocationData effortAllocationData) {
        this.effortAllocationData.add(0, effortAllocationData);
    }
    
    
    public void addEffortAllocationData(EffortAllocationData data, int i){
        
        //Does it already have the element? then replace it
        if (effortAllocationData.size() > i){
            effortAllocationData.set(i, data);    
        }else{
            effortAllocationData.add(data);
        }
        
        
        
//        for (int i = 0; i < effortAllocationData.size(); i++) {
//            
//            if (effortAllocationData.get(i) == data){
//                return;
//            }
//            
//        }
//        
//        
    }
    
    public void removeAllEffortAllocationData(){
        effortAllocationData.clear();
    }
    
    

    /**
     * @return the effortAllocationData
     */
    public List<EffortAllocationData> getEffortAllocationData() {
        return effortAllocationData;
    }


    /**
     * @param effortAllocationData the effortAllocationData to set
     */
    public void setEffortAllocationData(
            List<EffortAllocationData> effortAllocationData) {
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
     * @param weatherPoints the weatherPoints to set
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
     * @param sarID the sarID to set
     */
    public void setSarID(String sarID) {
        this.sarID = sarID;
    }


    public String generateHTML() {
        return "Invalid type";
    }

    
    

}
