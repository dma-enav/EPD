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
package dk.dma.epd.ship.gui;


import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.displayer.DisplayerDockBorder;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;

public class BorderMod implements
        UIBridge<BorderModifier, UIValue<BorderModifier>> {    
    
    /* our first modifier creates a red border */
    private BorderModifier borderModi = new BorderModifier() {
        @Override
        public Border modify(Border border) {
            return BorderFactory.createLineBorder(new Color(240, 240, 240));
        }
    };

    public BorderMod() {
    }

    public void destroy() {
    }


    /*
     * Tells whether we should pay attention to some border. We only pay
     * attention to those borders which are shown directly on a
     * SplitDockStation. Note that we can cast uiValue to
     * DisplayerDockBorder because of the restrictions we applied when
     * "publishing" the bridge on line 50.
     */
    private boolean shouldManage(UIValue<BorderModifier> uiValue) {
        DisplayerDockBorder displayer = (DisplayerDockBorder) uiValue;
        return displayer.getDisplayer().getStation() instanceof SplitDockStation;
    }

    @Override
    public void remove(String id, UIValue<BorderModifier> uiValue) {
        
    }

    /* This method may be called any time for installed listeners. */
    @Override
    public void set(String id, BorderModifier value,
            UIValue<BorderModifier> uiValue) {
        if (shouldManage(uiValue)) {
                uiValue.set(borderModi);
        } else {
            uiValue.set(value);
        }
    }

    @Override
    public void add(String arg0, UIValue<BorderModifier> arg1) {
        // TODO Auto-generated method stub
        
    }
}
