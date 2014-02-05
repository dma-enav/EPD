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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JMenuItem;

import com.bbn.openmap.MapBean;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Centers the given vessel
 */
public class CenterVesselTarget extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    
    private Position pos;
    private MapBean mapBean;

    /**
     * Constructor
     * @param text menu item text
     */
    public CenterVesselTarget(String text) {
        super();
        this.setText(text);
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        
        // Sanity check
        if (pos == null) {
            return;
        }
        
        mapBean.setCenter((float)pos.getLatitude(), (float)pos.getLongitude());
    }
    
    /**
     * Sets the vessel position
     * @param pos the vessel position
     */
    public void setVesselPosition(Position pos) {
        this.pos = pos;
    }
    
    /**
     * Sets the map bean
     * @param mapBean the map bean
     */
    public void setMapBean(MapBean mapBean) {
        this.mapBean = mapBean;
    }
}
