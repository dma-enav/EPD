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
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.util.ParseUtils;

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
    private EnavSettings settings;
    
    /**
     * Constructs a new ENavSettingsPanelCommon object.
     */
    public CommonENavSettingsPanel() {
        // Create the panel with a name and the path to its icon.
        super("e-Nav Services", new ImageIcon(CommonENavSettingsPanel.class.getResource
                ("/images/settings/servers-network.png")));
        this.setLayout(null);
        
        // METOC panel
        JPanel METOCPanel = new JPanel();
        METOCPanel.setLayout(null);
        METOCPanel.setBounds(6, 6, 438, 110);
        METOCPanel.setBorder(new TitledBorder(null, "METOC Settings", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        
        spinnerMETOCValidityDuration = new JSpinner();
        spinnerMETOCValidityDuration.setBounds(16, 20, 70, 20);
        METOCPanel.add(spinnerMETOCValidityDuration);
        
        JLabel lblMetocValidityDuration = new JLabel("METOC validity duration (min)");
        lblMetocValidityDuration.setBounds(98, 22, 188, 16);
        METOCPanel.add(lblMetocValidityDuration);
        
        spinnerActiveRouteMETOCPollInterval = new JSpinner();
        spinnerActiveRouteMETOCPollInterval.setBounds(16, 45, 70, 20);
        METOCPanel.add(spinnerActiveRouteMETOCPollInterval);
        
        JLabel lblActiveRouteMetoc = new JLabel("Active route METOC poll interval (min)");
        lblActiveRouteMetoc.setBounds(98, 47, 240, 16);
        METOCPanel.add(lblActiveRouteMetoc);
        
        spinnerMETOCTimeDifferenceTolerance = new JSpinner();
        spinnerMETOCTimeDifferenceTolerance.setBounds(16, 70, 70, 20);
        METOCPanel.add(spinnerMETOCTimeDifferenceTolerance);
        
        JLabel lblMetocTimeDifference = new JLabel("METOC time difference tolerance (min)");
        lblMetocTimeDifference.setBounds(98, 72, 242, 16);
        METOCPanel.add(lblMetocTimeDifference);
        
        // Add the panel.
        this.add(METOCPanel);
        
        // Http panel
        JPanel httpPanel = new JPanel();
        httpPanel.setLayout(null);
        httpPanel.setBounds(6, 128, 438, 128);
        httpPanel.setBorder(new TitledBorder(null, "HTTP Settings", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));
        
        // Http panel components.
        JLabel lblServerName = new JLabel("Server name:");
        lblServerName.setBounds(16, 19, 80, 16);
        httpPanel.add(lblServerName);
        
        textFieldServerName = new JTextField();
        textFieldServerName.setBounds(136, 18, 280, 17);
        httpPanel.add(textFieldServerName);
        textFieldServerName.setColumns(10);
        
        JLabel lblServerPort = new JLabel("Server port:");
        lblServerPort.setBounds(16, 43, 80, 16);
        httpPanel.add(lblServerPort);
        
        textFieldServerPort = new JTextField();
        textFieldServerPort.setBounds(136, 42, 280, 17);
        httpPanel.add(textFieldServerPort);
        textFieldServerPort.setColumns(10);
        
        JLabel lblConnectionTimeout = new JLabel("Connection Timeout:");
        lblConnectionTimeout.setBounds(16, 67, 132, 16);
        httpPanel.add(lblConnectionTimeout);
        
        textFieldConnectionTimeout = new JTextField();
        textFieldConnectionTimeout.setBounds(136, 66, 280, 17);
        httpPanel.add(textFieldConnectionTimeout);
        textFieldConnectionTimeout.setColumns(10);
        
        JLabel lblReadTimeout = new JLabel("Read timeout:");
        lblReadTimeout.setBounds(16, 91, 87, 16);
        httpPanel.add(lblReadTimeout);
        
        textFieldReadTimeout = new JTextField();
        textFieldReadTimeout.setBounds(136, 90, 280, 17);
        httpPanel.add(textFieldReadTimeout);
        textFieldReadTimeout.setColumns(10);
        
        // Add the panel.
        this.add(httpPanel);
        
        // MSI panel
        JPanel MSIPanel = new JPanel();
        MSIPanel.setLayout(null);
        MSIPanel.setBounds(6, 268, 438, 160);
        MSIPanel.setBorder(new TitledBorder(null, "MSI Settings", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        
        spinnerMSIPollInterval = new JSpinner();
        spinnerMSIPollInterval.setBounds(16, 20, 75, 20);
        MSIPanel.add(spinnerMSIPollInterval);
        
        JLabel lblMsiPollInterval = new JLabel("MSI poll interval (sec)");
        lblMsiPollInterval.setBounds(103, 22, 134, 16);
        MSIPanel.add(lblMsiPollInterval);
        
        spinnerMSITextBoxVisibilityScale = new JSpinner();
        spinnerMSITextBoxVisibilityScale.setBounds(16, 45, 75, 20);
        MSIPanel.add(spinnerMSITextBoxVisibilityScale);
        
        JLabel lblMsiTextboxVisibility = new JLabel("MSI textbox visibility scale (map scale)");
        lblMsiTextboxVisibility.setBounds(103, 47, 244, 16);
        MSIPanel.add(lblMsiTextboxVisibility);
        
        spinnerGPSPositionInterval = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        spinnerGPSPositionInterval.setBounds(16, 70, 75, 20);
        MSIPanel.add(spinnerGPSPositionInterval);
        
        JLabel lblGpsPositionintervalBefore = new JLabel("GPS position interval before MSI visibility is calculated");
        lblGpsPositionintervalBefore.setBounds(103, 72, 341, 16);
        MSIPanel.add(lblGpsPositionintervalBefore);
        
        spinnerMSIVisibilityRangeFromOwnShip = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        spinnerMSIVisibilityRangeFromOwnShip.setBounds(16, 95, 75, 20);
        MSIPanel.add(spinnerMSIVisibilityRangeFromOwnShip);
        
        JLabel lblMsiVisibilityRange = new JLabel("MSI visibility range from own ship");
        lblMsiVisibilityRange.setBounds(103, 97, 214, 16);
        MSIPanel.add(lblMsiVisibilityRange);
        
        spinnerMSIVisibilituRangeAtWaypoint = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        spinnerMSIVisibilituRangeAtWaypoint.setBounds(16, 120, 75, 20);
        MSIPanel.add(spinnerMSIVisibilituRangeAtWaypoint);
        
        JLabel lblMsiVisibilityRange_1 = new JLabel("MSI visibility range from new waypoint at route creation");
        lblMsiVisibilityRange_1.setBounds(103, 122, 351, 16);
        MSIPanel.add(lblMsiVisibilityRange_1);
        
        // Add the panel
        this.add(MSIPanel);        
    }

    @Override
    protected boolean checkSettingsChanged() {
        
        return 
                // Changes in METOC panel.
                changed(this.settings.getMetocTtl(), this.spinnerMETOCValidityDuration) || 
                changed(this.settings.getActiveRouteMetocPollInterval(), this.spinnerActiveRouteMETOCPollInterval.getValue()) ||
                changed(this.settings.getMetocTimeDiffTolerance(), this.spinnerMETOCTimeDifferenceTolerance.getValue()) ||
                
                // Changes in HTTP panel.
                changed(this.settings.getServerName(), this.textFieldServerName.getText()) || 
                changed(this.settings.getHttpPort(), this.textFieldServerPort.getText()) ||
                changed(this.settings.getConnectTimeout(), this.textFieldConnectionTimeout.getText()) ||
                changed(this.settings.getReadTimeout(), this.textFieldReadTimeout.getText()) ||
                
                // Changes in MSI panel.
                changed(this.settings.getMsiPollInterval(), this.spinnerMSIPollInterval.getValue()) ||
                changed(this.settings.getMsiTextboxesVisibleAtScale(), this.spinnerMSITextBoxVisibilityScale.getValue()) ||
                changed(this.settings.getMsiRelevanceGpsUpdateRange(), this.spinnerGPSPositionInterval.getValue()) ||
                changed(this.settings.getMsiVisibilityFromNewWaypoint(), this.spinnerMSIVisibilityRangeFromOwnShip.getValue()) ||
                changed(this.settings.getMsiRelevanceFromOwnShipRange(), this.spinnerMSIVisibilityRangeFromOwnShip);
    }

    @Override
    protected void doLoadSettings() {
        
        this.settings = this.getSettings().getEnavSettings();
        
        // Initialize METOC settings.
        this.spinnerMETOCValidityDuration.setValue(settings.getMetocTtl());
        this.spinnerActiveRouteMETOCPollInterval.setValue(settings.getActiveRouteMetocPollInterval());
        this.spinnerMETOCTimeDifferenceTolerance.setValue(settings.getMetocTimeDiffTolerance());
        
        // Initialize http settings.
        this.textFieldServerName.setText(settings.getServerName());
        this.textFieldServerPort.setText(Integer.toString(settings.getHttpPort()));
        this.textFieldConnectionTimeout.setText(Integer.toString(settings.getConnectTimeout()));
        this.textFieldReadTimeout.setText(Integer.toString(settings.getReadTimeout()));
        
        // initialize MSI settings.
        this.spinnerMSIPollInterval.setValue(settings.getMsiPollInterval());
        this.spinnerMSITextBoxVisibilityScale.setValue(settings.getMsiTextboxesVisibleAtScale());
        this.spinnerGPSPositionInterval.setValue(settings.getMsiRelevanceGpsUpdateRange());
        this.spinnerMSIVisibilityRangeFromOwnShip.setValue(settings.getMsiRelevanceFromOwnShipRange());
        this.spinnerMSIVisibilituRangeAtWaypoint.setValue(settings.getMsiVisibilityFromNewWaypoint());
    }

    @Override
    protected void doSaveSettings() {
        
        // Save METOC settings.
        this.settings.setMetocTtl((Integer) this.spinnerMETOCValidityDuration.getValue());
        this.settings.setActiveRouteMetocPollInterval((Integer) this.spinnerActiveRouteMETOCPollInterval.getValue());
        this.settings.setMetocTimeDiffTolerance((Integer) this.spinnerMETOCTimeDifferenceTolerance.getValue()); 

        // Save HTTP settings.
        this.settings.setServerName(this.textFieldServerName.getText());
        this.settings.setHttpPort(getIntVal(this.textFieldServerPort.getText(), this.settings.getHttpPort()));
        this.settings.setReadTimeout(getIntVal(this.textFieldReadTimeout.getText(), this.settings.getReadTimeout()));
        this.settings.setConnectTimeout(getIntVal(this.textFieldConnectionTimeout.getText(), this.settings.getConnectTimeout()));
        
        // Save MSI settings.
        this.settings.setMsiPollInterval((Integer) this.spinnerMSIPollInterval.getValue());
        this.settings.setMsiTextboxesVisibleAtScale((Integer) this.spinnerMSITextBoxVisibilityScale.getValue());
        this.settings.setMsiRelevanceGpsUpdateRange((Double) this.spinnerGPSPositionInterval.getValue());
        this.settings.setMsiRelevanceFromOwnShipRange((Double) this.spinnerMSIVisibilityRangeFromOwnShip.getValue());
        this.settings.setMsiVisibilityFromNewWaypoint((Double) this.spinnerMSIVisibilituRangeAtWaypoint.getValue());
    }

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
