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
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.gui.settings.CloudSettingsPanel;
import dk.dma.epd.shore.EPDShore;

/**
 * Maritime cloud tab panel
 */
public class CloudShoreSettingsPanel extends CloudSettingsPanel implements BaseShoreSettings {
    
    private static final long serialVersionUID = 1L;
    
    JLabel label;
    boolean loaded;
    
    /**
     * Constructor
     * 
     * @param name the name of the settings panel
     * @param image the image relative to the "images/settings" folder
     */
    public CloudShoreSettingsPanel() {
        super();
        setVisible(false);
        
        // Create the image label
        label = new JLabel(
                getName(), 
                EPDShore.res().getCachedImageIcon("images/settings/cloud.png"), 
                SwingConstants.LEFT);
        GuiStyler.styleTabButton(label);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JLabel getLabel() {
        return label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPanel getPanel() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings() {
        super.loadSettings();
        setVisible(true);
        loaded = true;
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasChanged() {
        if (!loaded) {
            return false;
        }
        return super.wasChanged();
    }
}
