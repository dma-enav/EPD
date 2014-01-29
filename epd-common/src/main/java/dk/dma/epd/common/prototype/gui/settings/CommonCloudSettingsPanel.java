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

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.FormatException;
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
        setLayout(null);

        /************** HTTP settings ***************/
        JPanel httpPanel = new JPanel();
        httpPanel.setBounds(6, 6, 438, 84);
        add(httpPanel);
        
        httpPanel.setBorder(new TitledBorder(null, "HTTP Settings",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        // Server name
        httpPanel.setLayout(null);
        JLabel label = new JLabel("Server name:");
        label.setBounds(16, 20, 80, 16);
        httpPanel.add(label);
        txtServerName.setBounds(108, 18, 324, 20);
        httpPanel.add(txtServerName);
        
        // Server port
        JLabel label_1 = new JLabel("Server port:");
        label_1.setBounds(16, 45, 72, 16);
        httpPanel.add(label_1);
        txtServerPort.setBounds(108, 43, 80, 20);
        httpPanel.add(txtServerPort);
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
