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

import java.util.Objects;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.settings.layers.VesselLayerSettings;

/**
 * Creates a menu item for the MapMenu pop-up. When the menu item is
 * clicked, the visibility of vessel name labels will be updated.
 * @author adamduehansen
 */
public class ToggleAisTargetNames extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;
//    private boolean namesShouldBeVisible;
    private VesselLayerSettings<?> settings;
    
    public ToggleAisTargetNames(VesselLayerSettings<?> settings) {
        super("Toggle AIS target names");
        this.settings = Objects.requireNonNull(settings);
//        // Initialize if vessel names labels are shown. 
//        setNamesShouldBeVisible(EPDShore.getInstance().getSettings().getAisSettings().isShowNameLabels());
    }

    @Override
    public void doAction() {
        // Simply toggle the setting value.
        /*
         *  Clients that wish to take an action based on this should be registered observers of the settings instance.
         */
        this.settings.setShowVesselNameLabels(!this.settings.isShowVesselNameLabels());
        
//        // Check if AIS target names are already set to be shown or not, and
//        // set it to the opposite.
//        if (isNamesShouldBeVisible()) {
//            setNamesShouldBeVisible(false);
//        } else if (!isNamesShouldBeVisible()) {
//            setNamesShouldBeVisible(true);
//        }
//                
//        // Update vessel target names for the selected frame only.
//        EPDShore.getInstance().getMainFrame().getActiveChartPanel().
//                getAisLayer().setShowNameLabels(isNamesShouldBeVisible());
    }

//    /**
//     * @return The visibility value of vessel names labels.
//     */
//    public boolean isNamesShouldBeVisible() {
//        return namesShouldBeVisible;
//    }
//
//    /**
//     * Updates the visibility value of vessel names labels.
//     * @param namesShouldBeVisible
//     */
//    public void setNamesShouldBeVisible(boolean namesShouldBeVisible) {
//        this.namesShouldBeVisible = namesShouldBeVisible;
//    }
}
