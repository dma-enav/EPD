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

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.shore.EPDShore;

/**
 * Abstract base class that may be implemented by EPDShore settings panels.
 */
public class BaseShoreSettingsPanel extends BaseSettingsPanel implements BaseShoreSettings {

    private static final long serialVersionUID = 1L;

    JLabel label;
    
    /**
     * Constructor
     * 
     * @param name the name of the settings panel
     * @param image the image relative to the "images/settings" folder
     */
    public BaseShoreSettingsPanel(String name, String image) {
        super(name);
        setVisible(false);
        
        // Create the image label
        if (image != null) {
            label = new JLabel(
                    name, 
                    EPDShore.res().getCachedImageIcon("images/settings/" + image), 
                    SwingConstants.LEFT);
            GuiStyler.styleTabButton(label);
        }
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
    }    
}
