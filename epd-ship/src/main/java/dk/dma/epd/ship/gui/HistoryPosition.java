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
package dk.dma.epd.ship.gui;

import dk.dma.enav.model.geometry.Position;

public class HistoryPosition {

    private Position position;
    private float zoomScale;
    
    /**
     * 
     * @param position The position of the center in chartpanel.
     * @param zoomScale The amount of zoom when this object is created.
     */
    public HistoryPosition(Position position, float zoomScale) {
        this.position = position;
        this.zoomScale = zoomScale;
    }
   
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }

    public float getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(float zoomScale) {
        this.zoomScale = zoomScale;
    }

    public boolean sameAs(HistoryPosition hpos) {
        
        Position position = hpos.getPosition();
        float zoomScale   = hpos.getZoomScale();
        
        if (this.position.getLatitudeAsString().equals(position.getLatitudeAsString()) && 
                this.position.getLongitudeAsString().equals(position.getLongitudeAsString()) && 
                this.zoomScale == zoomScale) {
            return true;
        }
        
        return false;
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
