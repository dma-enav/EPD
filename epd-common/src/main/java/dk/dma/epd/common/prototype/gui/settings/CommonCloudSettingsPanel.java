/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import dk.dma.epd.common.prototype.settings.CloudSettings;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Maritime cloud tab panel
 */
public class CommonCloudSettingsPanel extends BaseSettingsPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextField txtServerPort = new JTextField();
    private JTextField txtServerName = new JTextField();
    private CloudSettings cloudSettings;
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
        this.cloudSettings = EPD.getInstance().getSettings().getCloudSettings();
        txtServerName.setText(cloudSettings.getCloudServerHost());
        txtServerPort.setText(Integer.toString(cloudSettings
                .getCloudServerPort()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        cloudSettings.setCloudServerHost(txtServerName.getText());
        cloudSettings.setCloudServerPort(getIntVal(
                txtServerPort.getText(), cloudSettings.getCloudServerPort()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return 
                changed(cloudSettings.getCloudServerHost(), txtServerName.getText()) ||
                changed(cloudSettings.getCloudServerPort(), txtServerPort.getText());
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
