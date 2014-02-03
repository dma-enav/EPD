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

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.shore.EPDShore;

public class ToggleAisTargetNames extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
    private boolean namesShouldBeVisible;

    public ToggleAisTargetNames() {

        super("Toggle AIS target names");
        namesShouldBeVisible = true; // AIS target names are visible as default.
    }

    @Override
    public void doAction() {
        
        // Check if AIS target names are already set to be shown or not.
        if (namesShouldBeVisible) {
            namesShouldBeVisible = false;
        } else if (!namesShouldBeVisible) {
            namesShouldBeVisible = true;
        }
        
        // Toggle ais target names for the selected frame only.
        EPDShore.getInstance().getMainFrame().getActiveMapWindow().getChartPanel().
                getAisLayer().setShowNameLabels(namesShouldBeVisible);
    }
}
