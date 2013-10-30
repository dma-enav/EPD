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
package dk.dma.epd.ship.gui.menuitems;


import java.util.ArrayList;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.dialogs.ISimpleConfirmDialogListener;
import dk.dma.epd.common.prototype.gui.dialogs.SimpleConfirmDialog;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.route.RouteManager;

public class RouteDelete extends JMenuItem implements IMapMenuAction, ISimpleConfirmDialogListener {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int routeIndex;
    private RouteManager routeManager;
    private MapMenu parentMenu;
    
    public RouteDelete(String text, MapMenu parentMenu) {
        super();
        setText(text);
        this.parentMenu = parentMenu;
    }
    
    @Override
    public void doAction() {
//        if (JOptionPane.showConfirmDialog(this, "Delete route?", "Route dele", JOptionPane.YES_NO_OPTION) == 0) {
//            routeManager.removeRoute(routeIndex);
//        }
        ArrayList<ISimpleConfirmDialogListener> diaListeners = new ArrayList<ISimpleConfirmDialogListener>();
        diaListeners.add(this);
        SimpleConfirmDialog.showSimpleConfirmDialog("Route delete", "Delete route?", diaListeners, this.parentMenu.getLatestVisibleLocation()); 
    }

    public void setRouteIndex(int routeIndex) {
        this.routeIndex = routeIndex;
    }
    
    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }

    @Override
    public void onNoClicked() {
        // User cancelled route deletion, do nothing.
        System.out.println("No clicked!!");
    }

    @Override
    public void onYesClicked() {
        // User confirmed route deletion
        System.out.println("Yes clicked!!");
        this.routeManager.removeRoute(routeIndex);
    }
}
