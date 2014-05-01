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
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Maritime cloud tab panel
 */
public class CommonCloudSettingsPanel extends BaseSettingsPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextField txtServerPort = new JTextField();
    private JTextField txtServerName = new JTextField();
    protected Insets insets5  = new Insets(5, 5, 5, 5);

    protected NetworkSettings<?> cloudSettings;
    
    /**
     * Constructor
     */
    public CommonCloudSettingsPanel(NetworkSettings<?> cloudSettings) {
        super("Cloud", new ImageIcon(CommonCloudSettingsPanel.class.getResource
                ("/images/settings/cloud.png")));
        this.cloudSettings = cloudSettings;
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
        txtServerName.setBounds(108, 18, 313, 20);
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
        txtServerName.setText(cloudSettings.getHost());
        txtServerPort.setText(Integer.toString(cloudSettings
                .getPort()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        cloudSettings.setHost(txtServerName.getText());
        cloudSettings.setPort(getIntVal(
                txtServerPort.getText(), cloudSettings.getPort()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return 
                changed(cloudSettings.getHost(), txtServerName.getText()) ||
                changed(cloudSettings.getPort(), txtServerPort.getText());
    }

    // TODO remove
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
