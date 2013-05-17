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
package dk.dma.epd.common.prototype.ais;

import java.io.Serializable;

import net.jcip.annotations.ThreadSafe;

/**
 * Class holding settings for vessel targets
 */
@ThreadSafe
public class VesselTargetSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean hide;
    private boolean showRoute;
    
    /**
     * Empty constructor
     */
    public VesselTargetSettings() {
        
    }
    
    /**
     * Copy constructor
     * @param settings
     */
    public VesselTargetSettings(VesselTargetSettings settings) {
        this.hide = settings.hide;
        this.showRoute = settings.showRoute;
    }

    /**
     * Is the target hidden on the display or not
     * @return
     */
    public synchronized boolean isHide() {
        return hide;
    }

    /**
     * Set visibility
     * @param hide
     */
    public synchronized void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * Will the intended route be shown for the target if it is available
     * @return
     */
    public synchronized boolean isShowRoute() {
        return showRoute;
    }

    /**
     * Set visibility of intended route
     * @param showRoute
     */
    public synchronized void setShowRoute(boolean showRoute) {
        this.showRoute = showRoute;
    }
    
}
