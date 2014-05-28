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

import java.util.Objects;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.gui.settings.CommonCloudSettingsPanel;
import dk.dma.epd.common.prototype.settings.network.NetworkSettings;
import dk.dma.epd.shore.settings.IdentitySettings;

/**
 * The shore version of the {@linkplain CommonCloudSettingsPanel} adds
 * settings for a shore ID and shore position
 */
public class ShoreCloudSettingsPanel extends CommonCloudSettingsPanel {

    private static final long serialVersionUID = -7159933582775260430L;

    private JSpinner spinnerShoreLat;
    private JSpinner spinnerShoreLon;
    private JTextField textFieldShoreId;

    private final IdentitySettings shoreIdentitySettings;
    
    /**
     * Constructor
     */
    public ShoreCloudSettingsPanel(NetworkSettings<NetworkSettings.IObserver> cloudSettings, IdentitySettings shoreIdentitySettings) {
        super(cloudSettings);
        this.shoreIdentitySettings = Objects.requireNonNull(shoreIdentitySettings);
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
        textFieldShoreId.setText(shoreIdentitySettings.getShoreId());
        spinnerShoreLat.setValue(shoreIdentitySettings.getShorePos().getLatitude());
        spinnerShoreLon.setValue(shoreIdentitySettings.getShorePos().getLongitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        super.doSaveSettings();
        
        shoreIdentitySettings.setShoreId(textFieldShoreId.getText());
        shoreIdentitySettings.setShorePos(
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
            changed = 
                    changed(shoreIdentitySettings.getShoreId(), textFieldShoreId.getText()) ||
                    changed(shoreIdentitySettings.getShorePos().getLatitude(), spinnerShoreLat.getValue()) ||
                    changed(shoreIdentitySettings.getShorePos().getLongitude(), spinnerShoreLon.getValue());
        }
        return changed;
    }
}
