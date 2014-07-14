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


import java.util.ArrayList;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.dialogs.ISimpleConfirmDialogListener;
import dk.dma.epd.common.prototype.gui.dialogs.SimpleConfirmDialog;

public class RouteDelete extends RouteMenuItem implements ISimpleConfirmDialogListener {
    
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
        EPD.getInstance().getRouteManager().removeRoute(routeIndex);
    }
}
