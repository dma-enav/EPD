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

import dk.dma.enav.model.geometry.Position;

public class EffortAllocationData {
    private double w;
    private double groundSpeed;
    private double pod;
    private double trackSpacing;
    private int searchTime;
    private double effectiveAreaSize;

//    private double effectiveAreaWidth;
//    private double effectiveAreaHeight;

    private Position effectiveAreaA;
    private Position effectiveAreaB;
    private Position effectiveAreaC;
    private Position effectiveAreaD;
    
    SearchPatternRoute searchPatternRoute;
    
    /**
     * @return the searchPatternRoute
     */
    public SearchPatternRoute getSearchPatternRoute() {
        return searchPatternRoute;
    }

    /**
     * @param searchPatternRoute the searchPatternRoute to set
     */
    public void setSearchPatternRoute(SearchPatternRoute searchPatternRoute) {
        this.searchPatternRoute = searchPatternRoute;
    }
    
    /**
     * @return the effectiveAreaSize
     */
    public double getEffectiveAreaSize() {
        return effectiveAreaSize;
    }

    /**
     * @param effectiveAreaSize the effectiveAreaSize to set
     */
    public void setEffectiveAreaSize(double effectiveAreaSize) {
        this.effectiveAreaSize = effectiveAreaSize;
    }

    /**
     * @return the searchTime
     */
    public int getSearchTime() {
        return searchTime;
    }

    /**
     * @param searchTime the searchTime to set
     */
    public void setSearchTime(int searchTime) {
        this.searchTime = searchTime;
    }

    /**
     * @return the w
     */
    public double getW() {
        return w;
    }

    /**
     * @param w the w to set
     */
    public void setW(double w) {
        this.w = w;
    }

    /**
     * @return the groundSpeed
     */
    public double getGroundSpeed() {
        return groundSpeed;
    }

    /**
     * @param groundSpeed the groundSpeed to set
     */
    public void setGroundSpeed(double groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    /**
     * @return the pod
     */
    public double getPod() {
        return pod;
    }

    /**
     * @param pod the pod to set
     */
    public void setPod(double pod) {
        this.pod = pod;
    }

    /**
     * @return the trackSpacing
     */
    public double getTrackSpacing() {
        return trackSpacing;
    }

    /**
     * @param trackSpacing the trackSpacing to set
     */
    public void setTrackSpacing(double trackSpacing) {
        this.trackSpacing = trackSpacing;
    }


//    /**
//     * @param effectiveAreaWidth the effectiveAreaWidth to set
//     */
//    public void setEffectiveAreaWidth(double effectiveAreaWidth) {
//        this.effectiveAreaWidth = effectiveAreaWidth;
//    }

//    /**
//     * @return the effectiveAreaHeight
//     */
//    public double getEffectiveAreaHeight() {
//        return effectiveAreaHeight;
//    }

//    /**
//     * @param effectiveAreaHeight the effectiveAreaHeight to set
//     */
//    public void setEffectiveAreaHeight(double effectiveAreaHeight) {
//        this.effectiveAreaHeight = effectiveAreaHeight;
//    }

    /**
     * @return the effectiveAreaA
     */
    public Position getEffectiveAreaA() {
        return effectiveAreaA;
    }

    /**
     * @param effectiveAreaA the effectiveAreaA to set
     */
    public void setEffectiveAreaA(Position effectiveAreaA) {
        System.out.println("Updatng new A");
        this.effectiveAreaA = effectiveAreaA;
    }

    /**
     * @return the effectiveAreaB
     */
    public Position getEffectiveAreaB() {
        return effectiveAreaB;
    }

    /**
     * @param effectiveAreaB the effectiveAreaB to set
     */
    public void setEffectiveAreaB(Position effectiveAreaB) {
        this.effectiveAreaB = effectiveAreaB;
    }

    /**
     * @return the effectiveAreaC
     */
    public Position getEffectiveAreaC() {
        return effectiveAreaC;
    }

    /**
     * @param effectiveAreaC the effectiveAreaC to set
     */
    public void setEffectiveAreaC(Position effectiveAreaC) {
        this.effectiveAreaC = effectiveAreaC;
    }

    /**
     * @return the effectiveAreaD
     */
    public Position getEffectiveAreaD() {
        return effectiveAreaD;
    }

    /**
     * @param effectiveAreaD the effectiveAreaD to set
     */
    public void setEffectiveAreaD(Position effectiveAreaD) {
        this.effectiveAreaD = effectiveAreaD;
    }
}
