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


import java.util.ArrayList;

import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.dialogs.ISimpleConfirmDialogListener;
import dk.dma.epd.common.prototype.gui.dialogs.SimpleConfirmDialog;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;

public class RouteDelete extends RouteMenuItem<RouteManagerCommon> implements ISimpleConfirmDialogListener {
    
    private static final long serialVersionUID = 1L;
    
    private MapMenuCommon parentMenu;
    
    public RouteDelete(String text, MapMenuCommon parentMenu) {
        super();
        setText(text);
        this.parentMenu = parentMenu;
    }
    
    @Override
    public void doAction() {
        ArrayList<ISimpleConfirmDialogListener> diaListeners = new ArrayList<ISimpleConfirmDialogListener>();
        diaListeners.add(this);
        SimpleConfirmDialog.showSimpleConfirmDialog("Route delete", "Delete route?", diaListeners, parentMenu.getLatestVisibleLocation()); 
    }

    @Override
    public void onNoClicked() {
        // User cancelled route deletion, do nothing.
    }

    @Override
    public void onYesClicked() {
        // User confirmed route deletion
        routeManager.removeRoute(routeIndex);
    }
}
