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
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;

/**
 * 
 * @author adamduehansen
 *
 */
public class MapSettingsPanelCommon extends BaseSettingsPanel {
    
    private static final long serialVersionUID = 1L;
    private JPanel generelMapSettings;
    private JPanel wmsSettings;
    private JTextField textField;
    
    public MapSettingsPanelCommon() {
        super("Map", new ImageIcon(MapSettingsPanelCommon.class.getResource
                ("/images/settings/map.png")));
        setLayout(null);
        
        // Generel settings apenel.
        generelMapSettings = new JPanel();
        generelMapSettings.setBounds(6, 6, 438, 145);
        generelMapSettings.setLayout(null);
        generelMapSettings.setBorder(new TitledBorder(null, "Generel", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));
        
        // Generel settings panel components.
        JSpinner spinnerDefaultMapScale = new JSpinner();
        spinnerDefaultMapScale.setBounds(6, 20, 75, 19);
        generelMapSettings.add(spinnerDefaultMapScale);
        
        JLabel lblDefaultMapScale = new JLabel("Default map scale");
        lblDefaultMapScale.setBounds(93, 23, 113, 16);
        generelMapSettings.add(lblDefaultMapScale);
        
        JSpinner spinnerMaximumScale = new JSpinner();
        spinnerMaximumScale.setBounds(6, 51, 75, 19);
        generelMapSettings.add(spinnerMaximumScale);
        
        JLabel lblMaximumScale = new JLabel("Maximum scale");
        lblMaximumScale.setBounds(93, 51, 98, 16);
        generelMapSettings.add(lblMaximumScale);
        
        JLabel lblDefaultMapCenter = new JLabel("Default map center");
        lblDefaultMapCenter.setBounds(6, 82, 120, 16);
        generelMapSettings.add(lblDefaultMapCenter);
        
        JLabel lblLatitude = new JLabel("Latitude:");
        lblLatitude.setBounds(6, 110, 55, 16);
        generelMapSettings.add(lblLatitude);
        
        JSpinner spinnerLatitude = new JSpinner();
        spinnerLatitude.setBounds(73, 108, 75, 19);
        generelMapSettings.add(spinnerLatitude);
        
        JLabel lblLongitude = new JLabel("Longitude:");
        lblLongitude.setBounds(160, 108, 75, 16);
        generelMapSettings.add(lblLongitude);
        
        JSpinner spinnerLongitude = new JSpinner();
        spinnerLongitude.setBounds(247, 108, 75, 19);
        generelMapSettings.add(spinnerLongitude);
        
        // Add the panel.
        this.add(generelMapSettings);
        
        
        wmsSettings = new JPanel();
        wmsSettings.setBounds(6, 163, 438, 131);
        wmsSettings.setLayout(null);
        wmsSettings.setBorder(new TitledBorder(null, "WMS Settings", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));

        this.add(wmsSettings);
        
        JLabel lblWmsUrl = new JLabel("WMS URL");
        lblWmsUrl.setBounds(6, 20, 61, 16);
        wmsSettings.add(lblWmsUrl);
        
        textField = new JTextField();
        textField.setBounds(6, 48, 426, 28);
        wmsSettings.add(textField);
        textField.setColumns(10);
    }
    
    public JPanel getGenerelPanel() {
        return this.generelMapSettings;
    }
    
    public JPanel getWMSPanel() {
        return this.wmsSettings;
    }

    @Override
    protected boolean checkSettingsChanged() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void doLoadSettings() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doSaveSettings() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void fireSettingsChanged() {
        // TODO Auto-generated method stub

    }
}
