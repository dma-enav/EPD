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
package dk.dma.epd.shore.gui.settingtabs;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.gui.settings.CommonCloudSettingsPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.settings.EPDEnavSettings;

/**
 * The shore version of the {@linkplain CommonCloudSettingsPanel} adds
 * settings for a shore ID and shore position
 */
public class ShoreCloudSettingsPanel extends CommonCloudSettingsPanel {

    private static final long serialVersionUID = -7159933582775260430L;

    private JSpinner spinnerShoreLat;
    private JSpinner spinnerShoreLon;
    private JTextField textFieldShoreId;

    /**
     * Constructor
     */
    public ShoreCloudSettingsPanel() {
        
        JPanel shorePanel = new JPanel();
        shorePanel.setBounds(6, 106, 438, 105);
        shorePanel.setLayout(null);
        shorePanel.setBorder(new TitledBorder(
                null, "Shore Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel lblShoreId = new JLabel("Shore Id");
        lblShoreId.setBounds(16, 20, 55, 16);
        shorePanel.add(lblShoreId);
        
        this.textFieldShoreId = new JTextField();
        this.textFieldShoreId.setBounds(83, 18, 338, 20);
        shorePanel.add(textFieldShoreId);
        
        JLabel lblPosition = new JLabel("Position");
        lblPosition.setBounds(16, 45, 61, 16);
        shorePanel.add(lblPosition);
        
        JLabel lblLatitude = new JLabel("Latitude");
        lblLatitude.setBounds(16, 67, 61, 16);
        shorePanel.add(lblLatitude);
        
        this.spinnerShoreLat = new JSpinner(new SpinnerNumberModel(
                new Double(0), null, null, new Double(1)));
        this.spinnerShoreLat.setBounds(83, 65, 90, 20);
        shorePanel.add(this.spinnerShoreLat);
        
        JLabel lblLongitude = new JLabel("Longitude");
        lblLongitude.setBounds(185, 67, 63, 16);
        shorePanel.add(lblLongitude);
        
        this.spinnerShoreLon = new JSpinner(new SpinnerNumberModel(
                new Double(0), null, null, new Double(1)));
        this.spinnerShoreLon.setBounds(260, 65, 90, 20);
        shorePanel.add(this.spinnerShoreLon);

        this.add(shorePanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        super.doLoadSettings();
        
        EPDEnavSettings enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
        
        textFieldShoreId.setText(enavSettings.getShoreId());
        spinnerShoreLat.setValue(enavSettings.getShorePos().getLatitude());
        spinnerShoreLon.setValue(enavSettings.getShorePos().getLongitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        super.doSaveSettings();
        
        EPDEnavSettings enavSettings = EPDShore.getInstance().getSettings().getEnavSettings();
        
        enavSettings.setShoreId(textFieldShoreId.getText());
        enavSettings.setShorePos(
                new LatLonPoint.Double(
                        ((Number)spinnerShoreLat.getValue()).doubleValue(), 
                        ((Number)spinnerShoreLon.getValue()).doubleValue()));
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
                    changed(enavSettings.getShoreId(), textFieldShoreId.getText()) ||
                    changed(enavSettings.getShorePos().getLatitude(), spinnerShoreLat.getValue()) ||
                    changed(enavSettings.getShorePos().getLongitude(), spinnerShoreLon.getValue());
        }
        return changed;
    }
}
