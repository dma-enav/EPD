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
package dk.dma.epd.common.prototype.gui.settings;

/**
 * Interface that must be implemented by all settings panels
 */
public interface BaseSettings {

    /**
     * Returns the name of the settings panel
     * @return the name of the settings panel
     */
    String getName();

    /**
     * Called when the settings panel needs to be loaded
     */
    void loadSettings();

    /**
     * Saves the settings and notifies listeners if 
     * the settings have changed.
     * @return if the settings were saved
     */
    boolean saveSettings();
    
    /**
     * Returns if the settings were changed and needs saving
     * @return if the settings were changed and needs saving
     */
    boolean wasChanged();
    
    /**
     * Adds a change listener to these settings
     * @param listener the listener to add
     */
    void addListener(ISettingsListener listener);

    /**
     * Removes a change listener from these settings
     * @param listener the listener to remove
     */
    void removeListener(ISettingsListener listener);
}
