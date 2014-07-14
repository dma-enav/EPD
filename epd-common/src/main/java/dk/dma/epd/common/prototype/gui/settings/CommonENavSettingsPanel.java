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

import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.handlers.MSIHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.handlers.MetocHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.common.util.ParseUtils;
import java.awt.GridLayout;

/**
 * 
 * @author adamduehansen
 *
 */
public class CommonENavSettingsPanel extends BaseSettingsPanel {
    private static final long serialVersionUID = 1L;
    private JTextField textFieldConnectionTimeout;
    private JTextField textFieldServerPort;
    private JTextField textFieldServerName;
    private JTextField textFieldReadTimeout;
    private JSpinner spinnerMETOCValidityDuration;
    private JSpinner spinnerActiveRouteMETOCPollInterval;
    private JSpinner spinnerMETOCTimeDifferenceTolerance;
    private JSpinner spinnerMSIPollInterval;
    private JSpinner spinnerMSITextBoxVisibilityScale;
    private JSpinner spinnerGPSPositionInterval;
    private JSpinner spinnerMSIVisibilityRangeFromOwnShip;
    private JSpinner spinnerMSIVisibilituRangeAtWaypoint;

    protected NetworkSettings<?> httpSettings;
    protected MetocHandlerCommonSettings<?> metocHandlerSettings;
    protected MSIHandlerCommonSettings<?> msiHandlerSettings;
    protected MSILayerCommonSettings<?> msiLayerSettings;
    
    /**
     * Constructs a new ENavSettingsPanelCommon object.
     */
    public CommonENavSettingsPanel(NetworkSettings<?> httpSettings, MetocHandlerCommonSettings<?> metocHandlerSettings, MSIHandlerCommonSettings<?> msiHandlerSettings, MSILayerCommonSettings<?> msiLayerSettings) {
        // Create the panel with a name and the path to its icon.
        super("e-Nav Services", new ImageIcon(CommonENavSettingsPanel.class.getResource
                ("/images/settings/servers-network.png")));
        this.httpSettings = Objects.requireNonNull(httpSettings);
        this.metocHandlerSettings = Objects.requireNonNull(metocHandlerSettings);
        this.msiHandlerSettings = Objects.requireNonNull(msiHandlerSettings);
        this.msiLayerSettings = msiLayerSettings;
        
        
        /************** METOC settings ***************/
        setLayout(new GridLayout(0, 1, 6, 6));
        
        JPanel METOCPanel = new JPanel();
        METOCPanel.setLayout(null);
        METOCPanel.setBorder(new TitledBorder(null, "METOC Settings", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        
        // METOC settings panel components.
        this.spinnerMETOCValidityDuration = new JSpinner();
        this.spinnerMETOCValidityDuration.setBounds(16, 20, 75, 20);
        METOCPanel.add(this.spinnerMETOCValidityDuration);
        
        JLabel lblMetocValidityDuration = new JLabel("METOC validity duration (min)");
        lblMetocValidityDuration.setBounds(103, 22, 188, 16);
        METOCPanel.add(lblMetocValidityDuration);
        
        this.spinnerActiveRouteMETOCPollInterval = new JSpinner();
        this.spinnerActiveRouteMETOCPollInterval.setBounds(16, 45, 75, 20);
        METOCPanel.add(this.spinnerActiveRouteMETOCPollInterval);
        
        JLabel lblActiveRouteMetoc = new JLabel("Active route METOC poll interval (min)");
        lblActiveRouteMetoc.setBounds(103, 47, 240, 16);
        METOCPanel.add(lblActiveRouteMetoc);
        
        this.spinnerMETOCTimeDifferenceTolerance = new JSpinner();
        this.spinnerMETOCTimeDifferenceTolerance.setBounds(16, 70, 75, 20);
        METOCPanel.add(this.spinnerMETOCTimeDifferenceTolerance);
        
        JLabel lblMetocTimeDifference = new JLabel("METOC time difference tolerance (min)");
        lblMetocTimeDifference.setBounds(101, 72, 242, 16);
        METOCPanel.add(lblMetocTimeDifference);
        
        this.add(METOCPanel);
        
        
        /************** HTTP settings ***************/        
        
        JPanel httpPanel = new JPanel();
        httpPanel.setLayout(null);
        httpPanel.setBorder(new TitledBorder(null, "HTTP Settings", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));
        
        // Http settings panel components.
        JLabel lblServerName = new JLabel("Server name:");
        lblServerName.setBounds(16, 19, 80, 16);
        httpPanel.add(lblServerName);
        
        this.textFieldServerName = new JTextField();
        this.textFieldServerName.setBounds(136, 18, 280, 17);
        httpPanel.add(this.textFieldServerName);
        this.textFieldServerName.setColumns(10);
        
        JLabel lblServerPort = new JLabel("Server port:");
        lblServerPort.setBounds(16, 43, 80, 16);
        httpPanel.add(lblServerPort);
        
        this.textFieldServerPort = new JTextField();
        this.textFieldServerPort.setBounds(136, 42, 280, 17);
        httpPanel.add(this.textFieldServerPort);
        this.textFieldServerPort.setColumns(10);
        
        JLabel lblConnectionTimeout = new JLabel("Connection Timeout:");
        lblConnectionTimeout.setBounds(16, 67, 132, 16);
        httpPanel.add(lblConnectionTimeout);
        
        this.textFieldConnectionTimeout = new JTextField();
        this.textFieldConnectionTimeout.setBounds(136, 66, 280, 17);
        httpPanel.add(this.textFieldConnectionTimeout);
        this.textFieldConnectionTimeout.setColumns(10);
        
        JLabel lblReadTimeout = new JLabel("Read timeout:");
        lblReadTimeout.setBounds(16, 91, 87, 16);
        httpPanel.add(lblReadTimeout);
        
        this.textFieldReadTimeout = new JTextField();
        this.textFieldReadTimeout.setBounds(136, 90, 280, 17);
        httpPanel.add(this.textFieldReadTimeout);
        this.textFieldReadTimeout.setColumns(10);
        
        this.add(httpPanel);
        
        
        /************** MSI settings ***************/         
        
        JPanel MSIPanel = new JPanel();
        MSIPanel.setLayout(null);
        MSIPanel.setBorder(new TitledBorder(null, "MSI Settings", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        
        // MSI settings panel components.
        this.spinnerMSIPollInterval = new JSpinner();
        this.spinnerMSIPollInterval.setBounds(16, 20, 75, 20);
        MSIPanel.add(this.spinnerMSIPollInterval);
        
        JLabel lblMsiPollInterval = new JLabel("MSI poll interval (sec)");
        lblMsiPollInterval.setBounds(103, 22, 134, 16);
        MSIPanel.add(lblMsiPollInterval);
        
        this.spinnerMSITextBoxVisibilityScale = new JSpinner();
        this.spinnerMSITextBoxVisibilityScale.setBounds(16, 45, 75, 20);
        MSIPanel.add(this.spinnerMSITextBoxVisibilityScale);
        
        JLabel lblMsiTextboxVisibility = new JLabel("MSI textbox visibility scale (map scale)");
        lblMsiTextboxVisibility.setBounds(103, 47, 244, 16);
        MSIPanel.add(lblMsiTextboxVisibility);
        
        this.spinnerGPSPositionInterval = new JSpinner(new SpinnerNumberModel(
                new Double(0), null, null, new Double(1)));
        this.spinnerGPSPositionInterval.setBounds(16, 70, 75, 20);
        MSIPanel.add(this.spinnerGPSPositionInterval);
        
        JLabel lblGpsPositionintervalBefore = new JLabel("GPS position interval before MSI visibility is calculated");
        lblGpsPositionintervalBefore.setBounds(103, 72, 341, 16);
        MSIPanel.add(lblGpsPositionintervalBefore);
        
        this.spinnerMSIVisibilityRangeFromOwnShip = new JSpinner(new SpinnerNumberModel(
                new Double(0), null, null, new Double(1)));
        this.spinnerMSIVisibilityRangeFromOwnShip.setBounds(16, 95, 75, 20);
        MSIPanel.add(this.spinnerMSIVisibilityRangeFromOwnShip);
        
        JLabel lblMsiVisibilityRange = new JLabel("MSI visibility range from own ship");
        lblMsiVisibilityRange.setBounds(103, 97, 214, 16);
        MSIPanel.add(lblMsiVisibilityRange);
        
        this.spinnerMSIVisibilituRangeAtWaypoint = new JSpinner(new SpinnerNumberModel(
                new Double(0), null, null, new Double(1)));
        this.spinnerMSIVisibilituRangeAtWaypoint.setBounds(16, 120, 75, 20);
        MSIPanel.add(this.spinnerMSIVisibilituRangeAtWaypoint);
        
        JLabel lblMsiVisibilityRange_1 = new JLabel("MSI visibility range from new waypoint at route creation");
        lblMsiVisibilityRange_1.setBounds(103, 122, 351, 16);
        MSIPanel.add(lblMsiVisibilityRange_1);
        
        this.add(MSIPanel);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        
        return 
                // Changes in METOC panel.
                changed(this.metocHandlerSettings.getMetocTtl(), this.spinnerMETOCValidityDuration.getValue()) ||
                changed(this.metocHandlerSettings.getActiveRouteMetocPollInterval(), this.spinnerActiveRouteMETOCPollInterval.getValue()) ||
                changed(this.metocHandlerSettings.getMetocTimeDiffTolerance(), this.spinnerMETOCTimeDifferenceTolerance.getValue()) ||
                
                changed(this.httpSettings.getHost(), this.textFieldServerName.getText()) ||
                changed(this.httpSettings.getPort(), this.textFieldServerPort.getText()) ||
                changed(this.httpSettings.getConnectTimeout(), this.textFieldConnectionTimeout.getText()) ||
                changed(this.httpSettings.getReadTimeout(), this.textFieldReadTimeout.getText()) ||

                changed(this.msiHandlerSettings.getMsiPollInterval(), this.spinnerMSIPollInterval.getValue()) ||
                changed(this.msiHandlerSettings.getMsiRelevanceGpsUpdateRange(), this.spinnerGPSPositionInterval.getValue()) ||
                changed(this.msiHandlerSettings.getMsiRelevanceFromOwnShipRange(), this.spinnerMSIVisibilityRangeFromOwnShip.getValue()) ||

                changed(this.msiLayerSettings.getMsiTextboxesVisibleAtScale(), this.spinnerMSITextBoxVisibilityScale.getValue()) ||
                changed(this.msiLayerSettings.getMsiVisibilityFromNewWaypoint(), this.spinnerMSIVisibilituRangeAtWaypoint.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        
        // Initialize METOC settings.
        this.spinnerMETOCValidityDuration.setValue(this.metocHandlerSettings.getMetocTtl());
        this.spinnerActiveRouteMETOCPollInterval.setValue(this.metocHandlerSettings.getActiveRouteMetocPollInterval());
        this.spinnerMETOCTimeDifferenceTolerance.setValue(this.metocHandlerSettings.getMetocTimeDiffTolerance());
        
        // Initialize http settings.
        this.textFieldServerName.setText(this.httpSettings.getHost());
        this.textFieldServerPort.setText(Integer.toString(this.httpSettings.getPort()));
        this.textFieldConnectionTimeout.setText(Integer.toString(this.httpSettings.getConnectTimeout()));
        this.textFieldReadTimeout.setText(Integer.toString(this.httpSettings.getReadTimeout()));
        
        // initialize MSI settings.
        this.spinnerMSIPollInterval.setValue(this.msiHandlerSettings.getMsiPollInterval());
        this.spinnerGPSPositionInterval.setValue(this.msiHandlerSettings.getMsiRelevanceGpsUpdateRange());
        this.spinnerMSIVisibilityRangeFromOwnShip.setValue(this.msiHandlerSettings.getMsiRelevanceFromOwnShipRange());
        this.spinnerMSITextBoxVisibilityScale.setValue(this.msiLayerSettings.getMsiTextboxesVisibleAtScale());
        this.spinnerMSIVisibilituRangeAtWaypoint.setValue(this.msiLayerSettings.getMsiVisibilityFromNewWaypoint());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        
        // Save METOC settings.
        this.metocHandlerSettings.setMetocTtl((Integer) this.spinnerMETOCValidityDuration.getValue());
        this.metocHandlerSettings.setActiveRouteMetocPollInterval((Integer) this.spinnerActiveRouteMETOCPollInterval.getValue());
        this.metocHandlerSettings.setMetocTimeDiffTolerance((Integer) this.spinnerMETOCTimeDifferenceTolerance.getValue()); 

        // Save HTTP settings.
        this.httpSettings.setHost(this.textFieldServerName.getText());
        this.httpSettings.setPort(getIntVal(this.textFieldServerPort.getText(), this.httpSettings.getPort()));
        this.httpSettings.setReadTimeout(getIntVal(this.textFieldReadTimeout.getText(), this.httpSettings.getReadTimeout()));
        this.httpSettings.setConnectTimeout(getIntVal(this.textFieldConnectionTimeout.getText(), this.httpSettings.getConnectTimeout()));
        
        // Save MSI settings.
        this.msiHandlerSettings.setMsiPollInterval((Integer) this.spinnerMSIPollInterval.getValue());
        this.msiHandlerSettings.setMsiRelevanceGpsUpdateRange((Double) this.spinnerGPSPositionInterval.getValue());
        this.msiHandlerSettings.setMsiRelevanceFromOwnShipRange((Double) this.spinnerMSIVisibilityRangeFromOwnShip.getValue());
        this.msiLayerSettings.setMsiTextboxesVisibleAtScale((Integer) this.spinnerMSITextBoxVisibilityScale.getValue());
        this.msiLayerSettings.setMsiVisibilityFromNewWaypoint((Double) this.spinnerMSIVisibilituRangeAtWaypoint.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.ENAV);
    }
    
    /**
     * Converts a textfield value into an Integer object.
     * @param fieldValue The textvalue to be converted.
     * @param defaultValue The default value.
     * @return The converted textvalue.
     */
    private int getIntVal(String fieldValue, int defaultValue) {
        Integer value;
        try {
            value = ParseUtils.parseInt(fieldValue);
        } catch (FormatException e) {
            value = null;
        }
        
        if (value == null) {
            return defaultValue;
        }
        
        return value;
    }
}
