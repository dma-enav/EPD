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
