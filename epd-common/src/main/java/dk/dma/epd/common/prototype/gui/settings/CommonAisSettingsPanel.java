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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.AisSettings;

/**
 * 
 * @author adamduehansen
 *
 */
public class CommonAisSettingsPanel extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JPanel transponderPanel;
    private AisSettings settings;
    private JCheckBox chckbxAllowSending;
    private JCheckBox chckbxStrictTimeout;

    /**
     * Constructs a new CommonAisSettingsPanel object.
     */
    public CommonAisSettingsPanel() {
        super("AIS", new ImageIcon(CommonAisSettingsPanel.class.getResource
                ("/images/settings/ais.png")));
        this.setLayout(null);
        
        
        /************** Transponder settings ***************/
        
        this.transponderPanel = new JPanel();
        this.transponderPanel.setBounds(6, 6, 438, 82);
        this.transponderPanel.setBorder(new TitledBorder(
                null, "Transponder Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.transponderPanel.setLayout(null);
        
        chckbxAllowSending = new JCheckBox("Allow Sending");
        chckbxAllowSending.setBounds(16, 20, 128, 20);
        this.transponderPanel.add(chckbxAllowSending);
        
        chckbxStrictTimeout = new JCheckBox("Strict timeout");
        chckbxStrictTimeout.setBounds(16, 45, 128, 20);
        this.transponderPanel.add(chckbxStrictTimeout);
        
        this.add(this.transponderPanel);
    }
    
 
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return 
                // Changes in transponder settings.
                changed(this.settings.isAllowSending(), this.chckbxAllowSending.isSelected()) ||
                changed(this.settings.isStrict(), this.chckbxStrictTimeout.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        
        // Get AIS settings.
        this.settings = this.getSettings().getAisSettings();
        
        // Load transponder settings.
        this.chckbxAllowSending.setSelected(this.settings.isAllowSending());
        this.chckbxStrictTimeout.setSelected(this.settings.isStrict());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        
        // Save transponder settings.
        this.settings.setAllowSending(this.chckbxAllowSending.isSelected());
        this.settings.setStrict(this.chckbxStrictTimeout.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        super.fireSettingsChanged(Type.AIS);
    }

    public JPanel getTransponderPanel() {
        return this.transponderPanel;
    }
}
