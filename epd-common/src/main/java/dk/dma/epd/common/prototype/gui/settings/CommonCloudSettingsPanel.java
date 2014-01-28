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

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTHWEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Maritime cloud tab panel
 */
public class CommonCloudSettingsPanel extends BaseSettingsPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextField txtServerPort = new JTextField();
    private JTextField txtServerName = new JTextField();
    private EnavSettings enavSettings;
    protected Insets insets5  = new Insets(5, 5, 5, 5);

    /**
     * Constructor
     */
    public CommonCloudSettingsPanel() {
        super("Cloud", new ImageIcon(CommonCloudSettingsPanel.class.getResource
                ("/images/settings/cloud.png")));
        
        setLayout(new GridBagLayout());

        // Cloud connection settings
        JPanel cloudPanel = new JPanel(new GridBagLayout());
        add(cloudPanel, 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.1, NORTHWEST, HORIZONTAL, new Insets(15, 5, 5, 5), 0, 0));
        
        cloudPanel.setBorder(new TitledBorder(null, "HTTP Settings",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        // Server name
        GraphicsUtil.fixSize(txtServerName, 250);
        int gridy = 0;
        cloudPanel.add(new JLabel("Server name:"), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        cloudPanel.add(txtServerName, 
                new GridBagConstraints(1, gridy, 1, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        // Server port
        gridy++;
        GraphicsUtil.fixSize(txtServerPort, 60);
        cloudPanel.add(new JLabel("Server port:"), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        cloudPanel.add(txtServerPort, 
                new GridBagConstraints(1, gridy, 1, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        this.enavSettings = EPD.getInstance().getSettings().getEnavSettings();
        txtServerName.setText(enavSettings.getCloudServerHost());
        txtServerPort.setText(Integer.toString(enavSettings
                .getCloudServerPort()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        enavSettings.setCloudServerHost(txtServerName.getText());
        enavSettings.setCloudServerPort(getIntVal(
                txtServerPort.getText(), enavSettings.getHttpPort()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return 
                changed(enavSettings.getCloudServerHost(), txtServerName.getText()) ||
                changed(enavSettings.getCloudServerPort(), txtServerPort.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.CLOUD);
    }
    
    
    private static int getIntVal(String fieldVal, int defaultValue) {
        Integer val;
        try {
            val = ParseUtils.parseInt(fieldVal);
        } catch (FormatException e) {
            val = null;
        }
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }
}
