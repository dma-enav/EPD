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
package dk.dma.epd.shore.gui.settingtabs;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dk.dma.epd.common.prototype.gui.settings.BaseSettings;

/**
 * Interface that must be implemented by all EPDShore settings panels
 * <p>
 * It extends {@linkplain BaseSettings} with a label
 */
public interface BaseShoreSettings extends BaseSettings {

    /**
     * Returns the label associated with the settings panel
     * @return the label associated with the settings panel
     */
    JLabel getLabel();
    
    /**
     * Returns a reference to the panel implementing this interface
     * @return the panel implementing this interface
     */
    JPanel getPanel();
}
