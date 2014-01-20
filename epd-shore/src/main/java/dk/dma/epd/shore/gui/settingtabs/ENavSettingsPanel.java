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

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.util.ParseUtils;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.settings.EPDEnavSettings;


public class ENavSettingsPanel  extends BaseShoreSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JTextField textFieldServerPort;
    private JTextField textFieldServerName;
    private JTextField textFieldConnectionTimeout;
    private JTextField textFieldReadTimeout;
    private JSpinner spinnerActiveRouteMetocPoll;
    private JSpinner spinnerMetocTimeDiffTolerance;
    private JSpinner spinnerMsiPollInterval;
    private JSpinner spinnerMsiTextboxesVisibleAtScale;
    private JSpinner spinnerMetocTtl;
    private JSpinner spinnerMsiRelevanceGpsUpdateRange;
    private JSpinner spinnerMsiVisibilityFromOwnShipRange;
    private JSpinner spinnerMsiVisibilityFromNewWaypoint;
    private EPDEnavSettings enavSettings;


    public ENavSettingsPanel(){
        super("e-Nav Services", "servers-network.png");

        setBackground(GuiStyler.backgroundColor);
        setBounds(10, 11, 493, 600);
        setLayout(null);


        JPanel MetocPanel = new JPanel();
        MetocPanel.setBounds(10, 0, 434, 95);


        MetocPanel.setBackground(GuiStyler.backgroundColor);
        MetocPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "METOC Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        spinnerMetocTtl = new JSpinner();
        GuiStyler.styleSpinner(spinnerMetocTtl);
        spinnerMetocTtl.setBounds(16, 16, 70, 20);

        JLabel label = new JLabel("METOC validity duration (min)");
        GuiStyler.styleText(label);
        label.setBounds(90, 19, 142, 14);

        spinnerActiveRouteMetocPoll = new JSpinner();
        GuiStyler.styleSpinner(spinnerActiveRouteMetocPoll);
        spinnerActiveRouteMetocPoll.setBounds(16, 42, 70, 20);

        JLabel label_1 = new JLabel("Active route METOC poll interval (min)");
        GuiStyler.styleText(label_1);
        label_1.setBounds(90, 45, 182, 14);

        spinnerMetocTimeDiffTolerance = new JSpinner();
        GuiStyler.styleSpinner(spinnerMetocTimeDiffTolerance);
        spinnerMetocTimeDiffTolerance.setBounds(16, 68, 70, 20);

        JLabel label_2 = new JLabel("METOC time difference tolerance (min)");
        GuiStyler.styleText(label_2);
        label_2.setBounds(90, 71, 185, 14);

        JPanel HttpPanel = new JPanel();
        HttpPanel.setBounds(10, 93, 434, 135);
        HttpPanel.setBackground(GuiStyler.backgroundColor);
        HttpPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "HTTP Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));


        JLabel label_3 = new JLabel("Server name:");
        GuiStyler.styleText(label_3);
        label_3.setBounds(16, 19, 65, 14);

        JLabel label_4 = new JLabel("Server port:");
        GuiStyler.styleText(label_4);
        label_4.setBounds(16, 45, 59, 14);

        JLabel label_5 = new JLabel("Connection timeout:");
        GuiStyler.styleText(label_5);
        label_5.setBounds(16, 71, 97, 14);

        JLabel label_6 = new JLabel("Read timeout:");
        GuiStyler.styleText(label_6);
        label_6.setBounds(16, 97, 68, 14);

        textFieldServerPort = new JTextField();
        GuiStyler.styleTextFields(textFieldServerPort);
        textFieldServerPort.setBounds(117, 42, 288, 20);

        textFieldServerName = new JTextField();
        GuiStyler.styleTextFields(textFieldServerName);
        textFieldServerName.setBounds(117, 16, 288, 20);
        textFieldServerName.setColumns(10);

        textFieldConnectionTimeout = new JTextField();
        GuiStyler.styleTextFields(textFieldConnectionTimeout);
        textFieldConnectionTimeout.setBounds(117, 68, 288, 20);

        textFieldReadTimeout = new JTextField();
        GuiStyler.styleTextFields(textFieldReadTimeout);
        textFieldReadTimeout.setBounds(117, 94, 288, 20);

        JPanel MsiPanel = new JPanel();
        MsiPanel.setBounds(10, 229, 434, 144);
        MsiPanel.setBackground(GuiStyler.backgroundColor);
        MsiPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "MSI Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));


        spinnerMsiPollInterval = new JSpinner();
        GuiStyler.styleSpinner(spinnerMsiPollInterval);
        spinnerMsiPollInterval.setBounds(16, 16, 70, 20);

        JLabel label_7 = new JLabel("MSI poll interval (sec)");
        GuiStyler.styleText(label_7);
        label_7.setBounds(90, 19, 328, 14);

        spinnerMsiTextboxesVisibleAtScale = new JSpinner();
        GuiStyler.styleSpinner(spinnerMsiTextboxesVisibleAtScale);
        spinnerMsiTextboxesVisibleAtScale.setBounds(16, 42, 70, 20);

        JLabel label_8 = new JLabel("MSI textbox visibility scale (map scale)");
        GuiStyler.styleText(label_8);
        label_8.setBounds(90, 45, 328, 14);

        spinnerMsiRelevanceGpsUpdateRange = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        GuiStyler.styleSpinner(spinnerMsiRelevanceGpsUpdateRange);
        spinnerMsiRelevanceGpsUpdateRange.setBounds(16, 68, 70, 20);

        spinnerMsiVisibilityFromOwnShipRange = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        GuiStyler.styleSpinner(spinnerMsiVisibilityFromOwnShipRange);
        spinnerMsiVisibilityFromOwnShipRange.setBounds(16, 94, 70, 20);

        JLabel lblRangeBetweenGps = new JLabel("GPS position interval before MSI visibility is calcualted");
        GuiStyler.styleText(lblRangeBetweenGps);
        lblRangeBetweenGps.setBounds(90, 71, 328, 14);

        JLabel lblRelevancyRangeFrom = new JLabel("MSI visibility range from own ship");
        GuiStyler.styleText(lblRelevancyRangeFrom);
        lblRelevancyRangeFrom.setBounds(90, 97, 328, 14);

        spinnerMsiVisibilityFromNewWaypoint = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        GuiStyler.styleSpinner(spinnerMsiVisibilityFromNewWaypoint);
        spinnerMsiVisibilityFromNewWaypoint.setBounds(16, 120, 70, 20);

        JLabel lblMsiVisibilityRange = new JLabel("MSI visibility range from new waypoint at route creation");
        GuiStyler.styleText(lblMsiVisibilityRange);
        lblMsiVisibilityRange.setBounds(90, 123, 328, 14);
        HttpPanel.setLayout(null);
        HttpPanel.add(label_3);
        HttpPanel.add(label_4);
        HttpPanel.add(label_5);
        HttpPanel.add(label_6);
        HttpPanel.add(textFieldServerPort);
        HttpPanel.add(textFieldServerName);
        HttpPanel.add(textFieldConnectionTimeout);
        HttpPanel.add(textFieldReadTimeout);
        MetocPanel.setLayout(null);
        MetocPanel.add(spinnerMetocTtl);
        MetocPanel.add(label);
        MetocPanel.add(spinnerActiveRouteMetocPoll);
        MetocPanel.add(label_1);
        MetocPanel.add(spinnerMetocTimeDiffTolerance);
        MetocPanel.add(label_2);
        setLayout(null);
        MsiPanel.setLayout(null);
        MsiPanel.add(spinnerMsiPollInterval);
        MsiPanel.add(label_7);
        MsiPanel.add(spinnerMsiTextboxesVisibleAtScale);
        MsiPanel.add(label_8);
        MsiPanel.add(spinnerMsiRelevanceGpsUpdateRange);
        MsiPanel.add(lblRangeBetweenGps);
        MsiPanel.add(spinnerMsiVisibilityFromOwnShipRange);
        MsiPanel.add(lblRelevancyRangeFrom);
        MsiPanel.add(spinnerMsiVisibilityFromNewWaypoint);
        MsiPanel.add(lblMsiVisibilityRange);
        add(MsiPanel);
        add(MetocPanel);
        add(HttpPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings() {
        super.loadSettings();
        
        this.enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
        spinnerMetocTtl.setValue(enavSettings.getMetocTtl());
        spinnerActiveRouteMetocPoll.setValue(enavSettings.getActiveRouteMetocPollInterval());
        spinnerMetocTimeDiffTolerance.setValue(enavSettings.getMetocTimeDiffTolerance());

        textFieldServerName.setText(enavSettings.getServerName());
        textFieldServerPort.setText(Integer.toString(enavSettings.getHttpPort()));
        textFieldConnectionTimeout.setText(Integer.toString(enavSettings.getConnectTimeout()));
        textFieldReadTimeout.setText(Integer.toString(enavSettings.getReadTimeout()));

        spinnerMsiPollInterval.setValue(enavSettings.getMsiPollInterval());
        spinnerMsiTextboxesVisibleAtScale.setValue(enavSettings.getMsiTextboxesVisibleAtScale());

        spinnerMsiRelevanceGpsUpdateRange.setValue(enavSettings.getMsiRelevanceGpsUpdateRange());
        spinnerMsiVisibilityFromOwnShipRange.setValue(enavSettings.getMsiRelevanceFromOwnShipRange());
        spinnerMsiVisibilityFromNewWaypoint.setValue(enavSettings.getMsiVisibilityFromNewWaypoint());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveSettings() {
        enavSettings.setMetocTtl((Integer) spinnerMetocTtl.getValue());
        enavSettings.setActiveRouteMetocPollInterval((Integer) spinnerActiveRouteMetocPoll.getValue());
        enavSettings.setMetocTimeDiffTolerance((Integer) spinnerMetocTimeDiffTolerance.getValue());

        enavSettings.setServerName(textFieldServerName.getText());
        enavSettings.setHttpPort(getIntVal(textFieldServerPort.getText(), enavSettings.getHttpPort()));
        enavSettings.setReadTimeout(getIntVal(textFieldReadTimeout.getText(), enavSettings.getReadTimeout()));
        enavSettings.setConnectTimeout(getIntVal(textFieldConnectionTimeout.getText(), enavSettings.getConnectTimeout()));

        enavSettings.setMsiPollInterval((Integer) spinnerMsiPollInterval.getValue());
        enavSettings.setMsiTextboxesVisibleAtScale((Integer) spinnerMsiTextboxesVisibleAtScale.getValue());

        enavSettings.setMsiRelevanceGpsUpdateRange((Double) spinnerMsiRelevanceGpsUpdateRange.getValue());
        enavSettings.setMsiRelevanceFromOwnShipRange((Double) spinnerMsiVisibilityFromOwnShipRange.getValue());
        enavSettings.setMsiVisibilityFromNewWaypoint((Double) spinnerMsiVisibilityFromNewWaypoint.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasChanged() {
        if (!loaded) {
            return false;
        }
        
        return
                changed(enavSettings.getMetocTtl(), spinnerMetocTtl.getValue()) ||
                changed(enavSettings.getActiveRouteMetocPollInterval(), spinnerActiveRouteMetocPoll.getValue()) ||
                changed(enavSettings.getMetocTimeDiffTolerance(), spinnerMetocTimeDiffTolerance.getValue()) ||

                changed(enavSettings.getServerName(), textFieldServerName.getText()) ||
                changed(enavSettings.getHttpPort(), textFieldServerPort.getText()) ||
                changed(enavSettings.getConnectTimeout(), textFieldConnectionTimeout.getText()) ||
                changed(enavSettings.getReadTimeout(), textFieldReadTimeout.getText()) ||

                changed(enavSettings.getMsiPollInterval(), spinnerMsiPollInterval.getValue()) ||
                changed(enavSettings.getMsiTextboxesVisibleAtScale(), spinnerMsiTextboxesVisibleAtScale.getValue()) ||

                changed(enavSettings.getMsiRelevanceGpsUpdateRange(), spinnerMsiRelevanceGpsUpdateRange.getValue()) ||
                changed(enavSettings.getMsiRelevanceFromOwnShipRange(), spinnerMsiVisibilityFromOwnShipRange.getValue()) ||
                changed(enavSettings.getMsiVisibilityFromNewWaypoint(), spinnerMsiVisibilityFromNewWaypoint.getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.ENAV);
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
