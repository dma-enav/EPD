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
package dk.dma.epd.shore.gui.views.menuitems;

import dk.dma.epd.common.prototype.gui.menuitems.RouteMenuItem;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.route.RouteMetocDialog;
import dk.dma.epd.shore.route.RouteManager;



public class RouteMetocProperties extends RouteMenuItem<RouteManager> {

    private static final long serialVersionUID = 1L;

    public RouteMetocProperties(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {
        RouteMetocDialog routeMetocDialog = new RouteMetocDialog(EPDShore.getInstance().getMainFrame(),routeManager, routeIndex);
        routeMetocDialog.setVisible(true);
        routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
    }
}
