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

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.gui.settings.CloudSettingsPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.settings.EPDEnavSettings;

/**
 * The shore version of the {@linkplain CloudSettingsPanel} adds
 * settings for a shore ID and shore position
 */
public class ShoreCloudSettingsPanel extends CloudSettingsPanel {

    private static final long serialVersionUID = -7159933582775260430L;

    JSpinner spnShoreLat = new JSpinner();
    JSpinner spnShoreLon = new JSpinner();
    JTextField txtShoreId = new JTextField();
    EPDEnavSettings enavSetting;

    /**
     * Constructor
     */
    public ShoreCloudSettingsPanel() {
        super();  
        
        // Cloud connection settings
        JPanel shorePanel = new JPanel(new GridBagLayout());
        add(shorePanel, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, NORTHWEST, HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));
        
        shorePanel.setBorder(new TitledBorder(null, "Shore Settings",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        // Shore ID
        GraphicsUtil.fixSize(txtShoreId, 160);
        int gridy = 0;
        shorePanel.add(new JLabel("Shore ID:"), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shorePanel.add(txtShoreId, 
                new GridBagConstraints(1, gridy, 4, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        // Shore position
        spnShoreLat.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        spnShoreLon.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        GraphicsUtil.fixSize(spnShoreLat, 80);
        GraphicsUtil.fixSize(spnShoreLon, 80);
        gridy++;
        int gridx = 0;
        shorePanel.add(new JLabel("Position:"), 
                new GridBagConstraints(gridx++, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shorePanel.add(new JLabel("Latitude:"), 
                new GridBagConstraints(gridx++, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shorePanel.add(spnShoreLat, 
                new GridBagConstraints(gridx++, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shorePanel.add(new JLabel("Longtitude:"), 
                new GridBagConstraints(gridx++, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shorePanel.add(spnShoreLon, 
                new GridBagConstraints(gridx++, gridy, 1, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        // Shorify the panel
        setBounds(10, 11, 500, 300);
        GuiStyler.styleSettingsTab(this);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        super.doLoadSettings();
        
        EPDEnavSettings enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
        
        txtShoreId.setText(enavSettings.getShoreId());
        spnShoreLat.setValue(enavSettings.getShorePos().getLatitude());
        spnShoreLon.setValue(enavSettings.getShorePos().getLongitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        super.doSaveSettings();
        
        EPDEnavSettings enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
        
        enavSettings.setShoreId(txtShoreId.getText());
        enavSettings.setShorePos(
                new LatLonPoint.Double(
                        (Double)spnShoreLat.getValue(), 
                        (Double)spnShoreLon.getValue()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        boolean changed = super.checkSettingsChanged();
        
        if (!changed) {
            EPDEnavSettings enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
            
            changed = 
                    changed(enavSettings.getShoreId(), txtShoreId.getText()) ||
                    changed(enavSettings.getShorePos().getLatitude(), spnShoreLat.getValue()) ||
                    changed(enavSettings.getShorePos().getLongitude(), spnShoreLon.getValue());
        }
        return changed;
    }
}
