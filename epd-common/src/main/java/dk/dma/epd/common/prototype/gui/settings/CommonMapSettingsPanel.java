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
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.MapSettings;

/**
 * 
 * @author adamduehansen
 *
 */
public class CommonMapSettingsPanel extends BaseSettingsPanel {
    
    private static final long serialVersionUID = 1L;
    private JPanel generalMapSettings;
    private JPanel wmsSettings;
    private JTextField textFieldWMSURL;
    private MapSettings settings;
    private JSpinner spinnerDefaultMapScale;
    private JSpinner spinnerMaximumScale;
    private JSpinner spinnerLatitude;
    private JSpinner spinnerLongitude;
    private JLabel lblWmsUrl;
    
    /**
     * Constructs a new CommonMapSettingsPanel object.
     */
    public CommonMapSettingsPanel() {
        super("Map", new ImageIcon(CommonMapSettingsPanel.class.getResource
                ("/images/settings/map.png")));
        setLayout(null);
        
        
        /************** General settings ***************/
        
        generalMapSettings = new JPanel();
        generalMapSettings.setBounds(6, 6, 438, 145);
        generalMapSettings.setLayout(null);
        generalMapSettings.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));
        
        // General settings panel components.
        spinnerDefaultMapScale = new JSpinner(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
        spinnerDefaultMapScale.setBounds(16, 20, 75, 20);
        generalMapSettings.add(spinnerDefaultMapScale);
        
        JLabel lblDefaultMapScale = new JLabel("Default map scale");
        lblDefaultMapScale.setBounds(103, 22, 113, 16);
        generalMapSettings.add(lblDefaultMapScale);
        
        spinnerMaximumScale = new JSpinner(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
        spinnerMaximumScale.setBounds(16, 45, 75, 20);
        generalMapSettings.add(spinnerMaximumScale);
        
        JLabel lblMaximumScale = new JLabel("Maximum scale");
        lblMaximumScale.setBounds(103, 47, 98, 16);
        generalMapSettings.add(lblMaximumScale);
        
        JLabel lblDefaultMapCenter = new JLabel("Default map center");
        lblDefaultMapCenter.setBounds(16, 70, 120, 16);
        generalMapSettings.add(lblDefaultMapCenter);
        
        JLabel lblLatitude = new JLabel("Latitude:");
        lblLatitude.setBounds(16, 95, 55, 16);
        generalMapSettings.add(lblLatitude);
        
        spinnerLatitude = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        spinnerLatitude.setBounds(83, 93, 75, 20);
        generalMapSettings.add(spinnerLatitude);
        
        JLabel lblLongitude = new JLabel("Longitude:");
        lblLongitude.setBounds(190, 93, 75, 16);
        generalMapSettings.add(lblLongitude);
        
        spinnerLongitude = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        spinnerLongitude.setBounds(245, 91, 75, 20);
        generalMapSettings.add(spinnerLongitude);
        
        this.add(generalMapSettings);
        
        
        /************** WMS settings ***************/
        
        wmsSettings = new JPanel();
        wmsSettings.setBounds(6, 163, 438, 85);
        wmsSettings.setLayout(null);
        wmsSettings.setBorder(new TitledBorder(null, "WMS Settings", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));

        lblWmsUrl = new JLabel("WMS URL");
        lblWmsUrl.setBounds(16, 20, 61, 16);
        wmsSettings.add(lblWmsUrl);
        
        textFieldWMSURL = new JTextField();
        textFieldWMSURL.setBounds(16, 40, 405, 20);
        wmsSettings.add(textFieldWMSURL);
        textFieldWMSURL.setColumns(10);
        
        this.add(wmsSettings);
    }
    
    /**
     * 
     * @return The General settings panel.
     */
    public JPanel getGeneralPanel() {
        return this.generalMapSettings;
    }
    
    /**
     * 
     * @return The WMS settings panel.
     */
    public JPanel getWMSPanel() {
        return this.wmsSettings;
    }
    
    /**
     * 
     * @return The WMS URL JLabel Component.
     */
    public JLabel getLblWMSURL() {
        return this.lblWmsUrl;
    }
    
    public JTextField getTextFieldWMSURL() {
        return this.textFieldWMSURL;
    }

    @Override
    protected boolean checkSettingsChanged() {
        return 
                changed(this.settings.getScale(), this.spinnerDefaultMapScale.getValue()) ||
                changed(this.settings.getMaxScale(), this.spinnerMaximumScale.getValue()) ||
                changed(this.settings.getCenter().getLatitude(), this.spinnerLatitude.getValue()) ||
                changed(this.settings.getCenter().getLongitude(), this.spinnerLongitude.getValue()) ||
                
                // Changes in WMS settings.
                changed(this.settings.getWmsQuery(), this.textFieldWMSURL.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        this.settings = this.getSettings().getMapSettings();
        
        // Load settings for generel panel.
        this.spinnerDefaultMapScale.setValue(this.settings.getScale());
        this.spinnerMaximumScale.setValue(this.settings.getMaxScale());
        Float latitude  = this.settings.getCenter().getLatitude();
        Float longitude = this.settings.getCenter().getLongitude();
        this.spinnerLatitude.setValue(latitude.doubleValue());
        this.spinnerLongitude.setValue(longitude.doubleValue());

        // Load settings for WMS.
        this.textFieldWMSURL.setText(settings.getWmsQuery());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        
        // Save generel panel.
        this.settings.setScale((Float) spinnerDefaultMapScale.getValue());
        this.settings.setMaxScale((Integer) this.spinnerMaximumScale.getValue());
        LatLonPoint center = new LatLonPoint.Double(
                (Double) this.spinnerLatitude.getValue(), (Double) this.spinnerLongitude.getValue());
        this.settings.setCenter(center);
        
        // Save settings for WMS.
        this.settings.setWmsQuery(textFieldWMSURL.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.MAP);
    }
}
