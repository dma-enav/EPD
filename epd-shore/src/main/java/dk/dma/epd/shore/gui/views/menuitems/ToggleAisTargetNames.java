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
