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

import javax.swing.JPanel;

/**
 * Abstract base class that may be implemented by settings panels.
 * <p>
 * It provides crude functionality for tracking if the settings were changed.
 */
public abstract class BaseSettingsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    protected boolean wasChanged;
    protected String name;
    
    /**
     * Constructor
     * 
     * @param name the name of the settings panel
     */
    public BaseSettingsPanel(String name) {
        this.name = name;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * Called when the settings panel needs to be loaded.<br>
     * Overriding classes should call {@code super.loadSettings()} first.
     */
    public void loadSettings() {
        wasChanged = true;
    }

    /**
     * Called when the settings panel needs to be saved.<br>
     * Overriding classes should call {@code super.saveSettings()} last.
     */
    public void saveSettings() {
        wasChanged = false;
    }

    /**
     * Returns if the settings was changed and needs saving
     * @return if the settings was changed and needs saving
     */
    public boolean wasChanged() {
        return wasChanged;
    }
}
