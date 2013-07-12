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
package dk.dma.epd.ship.gui.setuptabs;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.ship.settings.EPDMapSettings;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

/**
 * Map tab panel in setup panel
 */
public class MapTab extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private JSpinner spinnerDefaultMapScale;
    private JSpinner spinnerMaximumScale;
    private JSpinner spinnerLatitude;
    private JSpinner spinnerLongitude;
    private JSpinner spinnerShallowContour;
    private JSpinner spinnerSafetyDepth;
    private JCheckBox chckbxUseENC;
    private JSpinner spinnerSafetyContour;
    private JSpinner spinnerDeepContour;
    private JCheckBox chckbxShowText;
    private JCheckBox chckbxShallowPattern;
    private JCheckBox chckbxSimplePointSymbols;
    private JCheckBox chckbxPlainAreas;
    private JCheckBox chckbxTwoShades;
    private JComboBox<String[]> comboBoxColorProfile;
    private EPDMapSettings mapSettings;
    
    /**
     * Create the panel.
     */
    public MapTab() {
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "S52 Layer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                        .addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(113, Short.MAX_VALUE))
        );
        
        spinnerShallowContour = new JSpinner();
        
        spinnerSafetyDepth = new JSpinner();
        
        spinnerSafetyContour = new JSpinner();
        
        spinnerDeepContour = new JSpinner();
        
        chckbxShowText = new JCheckBox("Show text");
        
        chckbxShallowPattern = new JCheckBox("Shallow pattern");
        
        chckbxSimplePointSymbols = new JCheckBox("Simple point symbols");
        
        chckbxPlainAreas = new JCheckBox("Plain areas");
        
        chckbxTwoShades = new JCheckBox("Two shades");
        
        JLabel lblNewLabel = new JLabel("Shallow contour");
        
        JLabel lblSafetyDepth = new JLabel("Safety depth");
        
        JLabel lblSafetyContour = new JLabel("Safety contour");
        
        JLabel lblDeepContour = new JLabel("Deep contour");
        
        String[] colorModes = { "Day", "Dusk", "Night"};
        
        comboBoxColorProfile = new JComboBox(colorModes);
//        comboBoxColorProfile.setModel(new DefaultComboBoxModel(new String[] {"Day", "Dusk", "Night"}));
        
        JLabel lblColorProfile = new JLabel("Color profile");
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addComponent(chckbxSimplePointSymbols)
                        .addComponent(chckbxShallowPattern)
                        .addComponent(chckbxShowText)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(spinnerShallowContour, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblNewLabel))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(spinnerSafetyDepth, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblSafetyDepth))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(spinnerSafetyContour, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblSafetyContour))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(spinnerDeepContour, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblDeepContour)))
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(54)
                            .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                                .addComponent(chckbxTwoShades)
                                .addComponent(chckbxPlainAreas)))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addGap(18)
                            .addComponent(comboBoxColorProfile, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lblColorProfile)))
                    .addContainerGap(120, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerShallowContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNewLabel)
                        .addComponent(comboBoxColorProfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblColorProfile))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerSafetyDepth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSafetyDepth))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerSafetyContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSafetyContour))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerDeepContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDeepContour))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(chckbxPlainAreas)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(chckbxTwoShades))
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(chckbxShowText)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(chckbxShallowPattern)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(chckbxSimplePointSymbols)))
                    .addContainerGap(9, Short.MAX_VALUE))
        );
        panel.setLayout(gl_panel);
        
        spinnerDefaultMapScale = new JSpinner();
        spinnerDefaultMapScale.setModel(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
        
        spinnerMaximumScale = new JSpinner();
        spinnerMaximumScale.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
        
        spinnerLatitude = new JSpinner();
        spinnerLatitude.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        spinnerLongitude = new JSpinner();
        spinnerLongitude.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        chckbxUseENC = new JCheckBox("Use ENC");
        
        JLabel lblStartupMapCenter = new JLabel("Default map center");
        
        JLabel lblLatitude = new JLabel("Latitude");
        
        JLabel lblLongitude = new JLabel("Longitude");
        
        JLabel lblDefaultMapScale = new JLabel("Default map scale");
        
        JLabel lblMaximumZoomLevel = new JLabel("Maximum scale");
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addComponent(chckbxUseENC)
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(spinnerDefaultMapScale, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblDefaultMapScale))
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(spinnerMaximumScale, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblMaximumZoomLevel))
                        .addComponent(lblStartupMapCenter)
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(lblLatitude)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(spinnerLatitude, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addGap(29)
                            .addComponent(lblLongitude)
                            .addGap(5)
                            .addComponent(spinnerLongitude, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(138, Short.MAX_VALUE))
        );
        gl_panel_1.setVerticalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerDefaultMapScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDefaultMapScale))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerMaximumScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMaximumZoomLevel))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblStartupMapCenter)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblLatitude)
                        .addComponent(spinnerLatitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLongitude)
                        .addComponent(spinnerLongitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxUseENC)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_1.setLayout(gl_panel_1);
        setLayout(groupLayout);
    }
    
    public void loadSettings(EPDMapSettings mapSettings) {
        this.mapSettings = mapSettings;
        spinnerDefaultMapScale.setValue(mapSettings.getScale());
        spinnerMaximumScale.setValue(mapSettings.getMaxScale());
        Float latitude = mapSettings.getCenter().getLatitude();
        Float longitude = mapSettings.getCenter().getLongitude();
        spinnerLatitude.setValue(latitude.doubleValue());
        spinnerLongitude.setValue(longitude.doubleValue());
        chckbxUseENC.setSelected(mapSettings.isUseEnc());
        
        spinnerShallowContour.setValue(mapSettings.getS52ShallowContour());
        spinnerSafetyDepth.setValue(mapSettings.getS52SafetyDepth());
        spinnerSafetyContour.setValue(mapSettings.getS52SafetyContour());
        spinnerDeepContour.setValue(mapSettings.getS52DeepContour());
        chckbxShowText.setSelected(mapSettings.isS52ShowText());
        chckbxShallowPattern.setSelected(mapSettings.isS52ShallowPattern());
        chckbxSimplePointSymbols.setSelected(mapSettings.isUseSimplePointSymbols());
        chckbxPlainAreas.setSelected(mapSettings.isUsePlainAreas());
        chckbxTwoShades.setSelected(mapSettings.isS52TwoShades());
        
        comboBoxColorProfile.setSelectedItem(mapSettings.getColor());
    }
    
    public void saveSettings() {
        mapSettings.setScale((Float) spinnerDefaultMapScale.getValue());
        mapSettings.setMaxScale((Integer) spinnerMaximumScale.getValue());
        LatLonPoint center = new LatLonPoint.Double((Double) spinnerLatitude.getValue(), (Double) spinnerLongitude.getValue());
        mapSettings.setCenter(center);
        mapSettings.setUseEnc(chckbxUseENC.isSelected());
        
        mapSettings.setS52ShallowContour((Integer) spinnerShallowContour.getValue());
        mapSettings.setS52SafetyDepth((Integer) spinnerSafetyDepth.getValue());
        mapSettings.setS52SafetyContour((Integer) spinnerSafetyContour.getValue());
        mapSettings.setS52DeepContour((Integer)spinnerDeepContour.getValue());
        mapSettings.setS52ShowText(chckbxShowText.isSelected());
        mapSettings.setS52ShallowPattern(chckbxShallowPattern.isSelected());
        mapSettings.setUseSimplePointSymbols(chckbxSimplePointSymbols.isSelected());
        mapSettings.setUsePlainAreas(chckbxPlainAreas.isSelected());
        mapSettings.setS52TwoShades(chckbxTwoShades.isSelected());
        mapSettings.setColor(comboBoxColorProfile.getSelectedItem().toString());
        
    }
}
