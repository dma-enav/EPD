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
package dk.dma.epd.common.prototype.event;

import dk.dma.enav.model.geometry.Position;
/**
 * This class contains a Position object for the current
 * center of position on the map and a float value for the 
 * zoom scale. An object of this class is used to store
 * information about the position of the map.
 * 
 * @author adamduehansen
 *
 */
public class HistoryPosition {

    /**
     * Private fields.
     */
    private Position position;
    private float zoomScale;
    
    /**
     * Constructs a new HistoryPosition object with a Position and a zoom scale.
     * 
     * @param position The position which should be saved.
     * @param zoomScale The amount of zoom when this object is created.
     */
    public HistoryPosition(Position position, float zoomScale) {
        this.position = position;
        this.zoomScale = zoomScale;
    }
   
    /**
     * 
     * @return Gives the Position object of this class.
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * 
     * @param Sets the Position object of this class to the param.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * 
     * @return Gives the float value of zoom scale of this class. 
     */
    public float getZoomScale() {
        return zoomScale;
    }

    /**
     * 
     * @param zoomScale Sets the float value of zoom scale of this class.
     */
    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }
    
    //TODO: write an equal method that works correctly!
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        HistoryPosition hpos = (HistoryPosition) obj;
        
        if (this.zoomScale == hpos.zoomScale && 
                this.position.getLatitude() == hpos.getPosition().getLatitude() &&
                this.position.getLongitude() == hpos.getPosition().getLongitude()) {
            return true;
        }
        
        return false;        
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "POS: "+this.position.getLatitude()+", "+this.position.getLongitude();
    }
}
