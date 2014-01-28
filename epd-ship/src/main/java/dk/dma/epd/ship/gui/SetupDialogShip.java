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

import javax.swing.JFrame;

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;
import dk.dma.epd.ship.gui.setuptabs.ShipMapSettingsPanel;

public class SetupDialogShip extends SetupDialogCommon {

    private static final long serialVersionUID = 1L;
    private ShipMapSettingsPanel mapSettings;

    public SetupDialogShip(JFrame mainFrame) {
        super(mainFrame, "Ship Setup", true);

        mapSettings   = new ShipMapSettingsPanel();
        
        //registerSettingsPanels(mapSettings);
    }
}
