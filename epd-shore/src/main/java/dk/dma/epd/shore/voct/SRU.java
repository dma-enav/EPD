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
package dk.dma.epd.shore.voct;

/**
 * Definition of a SRU
 * 
 * 
 */

public class SRU {

    public enum sru_type {
        SHIP, HELICOPTER, PLANE
    }

    public enum sru_status {
        INVITED, ACCEPTED, DECLINED, PENDING, UNKNOWN
    }

    private String name;
    private long mmsi;
    private sru_type type;
    private sru_status status;
    private boolean visible;

    public SRU(String name, long mmsi, sru_type type, sru_status status,
            boolean visible) {
        super();
        this.name = name;
        this.mmsi = mmsi;
        this.type = type;
        this.status = status;
        this.visible = visible;
        
        // Other info? SOG and such? Will add later
    }
    
    

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mmsi
     */
    public long getMmsi() {
        return mmsi;
    }

    /**
     * @param mmsi the mmsi to set
     */
    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }

    /**
     * @return the type
     */
    public sru_type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(sru_type type) {
        this.type = type;
    }

    /**
     * @return the status
     */
    public sru_status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(sru_status status) {
        this.status = status;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    
    

}
