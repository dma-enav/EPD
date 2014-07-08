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
