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
package dk.dma.epd.shore.gui.route;

import java.awt.Window;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.route.RouteManager;


/**
 * Dialog with route properties
 */
public class RoutePropertiesDialog extends  dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialog{

    public RoutePropertiesDialog(Window parent,
            RouteManager routeManager,
            int routeId) {
        
        super(parent, routeManager, routeId);
        // TODO Auto-generated constructor stub
    }

    
    public RoutePropertiesDialog(Window mainFrame, Route route) {
        super(mainFrame, route, false);
        
        
        
//        departurePicker.setEnabled(false);
//        departureSpinner.setEnabled(false);
//        arrivalPicker.setEnabled(false);
//        arrivalSpinner.setEnabled(false);
    }


    
}
