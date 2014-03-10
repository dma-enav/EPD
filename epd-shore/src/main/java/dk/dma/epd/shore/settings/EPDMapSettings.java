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
package dk.dma.epd.shore.settings;

import dk.dma.epd.common.prototype.settings.MapSettings;

/**
 * Map/chart settings
 */
public class EPDMapSettings extends MapSettings {

    private static final long serialVersionUID = 1L;
    
    //Used internally to check if new map windows should try to make dongle check - if no dongle, don't retry on every new map
    private boolean encSuccess = true;

    public boolean isEncSuccess() {
        return encSuccess;
    }

    public void setEncSuccess(boolean encSuccess) {
        this.encSuccess = encSuccess;
    }
    

}
