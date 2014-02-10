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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import dk.dma.epd.common.prototype.gui.settings.CommonMapSettingsPanel;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.EPDMapSettings;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

/**
 * 
 * @author adamduehansen
 *
 */
public class ShipMapSettingsPanel extends CommonMapSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JComboBox<String> comboBoxColorProfile;
    private JCheckBox chckbxUseEnc;
    private JCheckBox chckbxUseWms;
    private JCheckBox chckbxDragWmsdisable;
    private EPDMapSettings mapSettings;
    private JSpinner spinnerShallowContour;
    private JSpinner spinnerSafetyContour;
    private JSpinner spinnerSafetyDepth;
    private JSpinner spinnerDeepContour;
    private JLabel lblShallowContour;
    private JCheckBox chckbxShowText;
    private JCheckBox chckbxShallowPattern;
    private JCheckBox chckbxSimplePointSymbols;
    private JCheckBox chckbxTwoShades;
    private JCheckBox chckbxPlainAreas;

    /**
     * Constructs a new ShipMapSettingsPanel object.
     */
    public ShipMapSettingsPanel() {
        
        // Get the general panel from super class.
        this.getGeneralPanel().setLocation(6, 6);
        
        // Resize the panel to add spaces for ekstra ship options.
        this.getGeneralPanel().setSize(438, 184);
        this.getWMSPanel().setBounds(
                super.getWMSPanel().getX(), 
                super.getWMSPanel().getY()+268, 
                super.getWMSPanel().getWidth(), 
                super.getWMSPanel().getHeight());
        
        
        /************** General settings ***************/
        
        this.chckbxUseEnc = new JCheckBox("Use ENC");
        this.chckbxUseEnc.setBounds(16, 120, 85, 23);
        this.getGeneralPanel().add(chckbxUseEnc);
        
        this.chckbxUseWms = new JCheckBox("Use WMS");
        this.chckbxUseWms.setBounds(113, 120, 88, 23);
        this.getGeneralPanel().add(chckbxUseWms);
        
        this.chckbxDragWmsdisable = new JCheckBox("Drag WMS (disable for performance)");
        this.chckbxDragWmsdisable.setBounds(16, 145, 257, 23);
        this.getGeneralPanel().add(this.chckbxDragWmsdisable);
        
        
        /************** S52 settings ***************/
        
        JPanel s52Settings = new JPanel();
        s52Settings.setBounds(6, 202, 438, 219);
        s52Settings.setLayout(null);
        s52Settings.setBorder(new TitledBorder(
                null, "S52 Layer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        this.spinnerShallowContour = new JSpinner();
        this.spinnerShallowContour.setBounds(16, 20, 75, 20);
        s52Settings.add(this.spinnerShallowContour);
        
        this.spinnerSafetyDepth = new JSpinner();
        this.spinnerSafetyDepth.setBounds(16, 45, 75, 20);
        s52Settings.add(this.spinnerSafetyDepth);
        
        this.spinnerSafetyContour = new JSpinner();
        this.spinnerSafetyContour.setBounds(16, 70, 75, 20);
        s52Settings.add(this.spinnerSafetyContour);
        
        this.spinnerDeepContour = new JSpinner();
        this.spinnerDeepContour.setBounds(16, 95, 75, 20);
        s52Settings.add(this.spinnerDeepContour);
        
        this.lblShallowContour = new JLabel("Shallow contour");
        this.lblShallowContour.setBounds(103, 22, 101, 16);
        s52Settings.add(this.lblShallowContour);
        
        JLabel lblSafetyDepth = new JLabel("Safety depth");
        lblSafetyDepth.setBounds(103, 47, 78, 16);
        s52Settings.add(lblSafetyDepth);
        
        JLabel lblSafetyContour = new JLabel("Safety contour");
        lblSafetyContour.setBounds(103, 72, 91, 16);
        s52Settings.add(lblSafetyContour);
        
        JLabel lblDeepContour = new JLabel("Deep contour");
        lblDeepContour.setBounds(103, 97, 91, 16);
        s52Settings.add(lblDeepContour);
        
        String[] colorModes = {"Day", "Dusk", "Night"};
        this.comboBoxColorProfile = new JComboBox<String>(colorModes);
        this.comboBoxColorProfile.setBounds(206, 19, 75, 20);
        s52Settings.add(this.comboBoxColorProfile);
        
        JLabel lblColorProfile = new JLabel("Color profile");
        lblColorProfile.setBounds(293, 20, 91, 16);
        s52Settings.add(lblColorProfile);
        
        this.chckbxShowText = new JCheckBox("Show text");
        this.chckbxShowText.setBounds(16, 125, 128, 23);
        s52Settings.add(this.chckbxShowText);
        
        this.chckbxShallowPattern = new JCheckBox("Shallow pattern");
        this.chckbxShallowPattern.setBounds(16, 150, 142, 23);
        s52Settings.add(this.chckbxShallowPattern);
        
        this.chckbxSimplePointSymbols = new JCheckBox("Simple point symbols");
        this.chckbxSimplePointSymbols.setBounds(16, 175, 168, 23);
        s52Settings.add(this.chckbxSimplePointSymbols);
        
        this.chckbxTwoShades = new JCheckBox("Two shades");
        this.chckbxTwoShades.setBounds(220, 150, 106, 23);
        s52Settings.add(this.chckbxTwoShades);
        
        this.chckbxPlainAreas = new JCheckBox("Plain areas");
        this.chckbxPlainAreas.setBounds(220, 125, 106, 23);
        s52Settings.add(this.chckbxPlainAreas);
        
        JButton btnAdvancedOptions = new JButton("Advanced Options");
        
        
        
        if (!EPDShip.getInstance().getSettings().getMapSettings().isUseEnc()){
            btnAdvancedOptions.setEnabled(false);
        }
        
        btnAdvancedOptions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                @SuppressWarnings("unused")
                AdvancedSettingsWindow advSettingsWindow = new AdvancedSettingsWindow();
//                advSettingsWindow.setVisible(true);
//                btnAdvancedOptions
                
            }
        });
        
        btnAdvancedOptions.setBounds(220, 175, 107, 20);
        s52Settings.add(btnAdvancedOptions);
        
        this.add(s52Settings);
    }
    
    public void doLoadSettings() {
        
        // Load the settings for the common components.
        super.doLoadSettings();

        mapSettings = EPDShip.getInstance().getSettings().getMapSettings();
        
        // Load extended generel settings.
        this.chckbxUseEnc.setSelected(mapSettings.isUseEnc());
        this.chckbxUseWms.setSelected(mapSettings.isUseWms());
        this.chckbxDragWmsdisable.setSelected(mapSettings.isUseWmsDragging());
        
        // Load S52 settings.
        this.spinnerShallowContour.setValue(this.mapSettings.getS52ShallowContour());
        this.spinnerSafetyDepth.setValue(this.mapSettings.getS52SafetyDepth());
        this.spinnerSafetyContour.setValue(this.mapSettings.getS52SafetyContour());
        this.spinnerDeepContour.setValue(this.mapSettings.getS52DeepContour());
        this.chckbxShowText.setSelected(mapSettings.isS52ShowText());
        this.chckbxShallowPattern.setSelected(mapSettings.isS52ShallowPattern());
        this.chckbxSimplePointSymbols.setSelected(mapSettings.isUseSimplePointSymbols());
        this.chckbxPlainAreas.setSelected(mapSettings.isUsePlainAreas());
        this.chckbxTwoShades.setSelected(mapSettings.isS52TwoShades());
        this.comboBoxColorProfile.setSelectedItem(mapSettings.getColor());
    }
    
    public void doSaveSettings() {
        
        // Save the settings for the common components. 
        super.doSaveSettings();
        
        // Save extended generel settings.
        this.mapSettings.setUseEnc(this.chckbxUseEnc.isSelected());
        this.mapSettings.setUseWms(this.chckbxUseWms.isSelected());
        this.mapSettings.setUseWmsDragging(this.chckbxDragWmsdisable.isSelected());
        
        // Save S52 settings.
        this.mapSettings.setS52ShallowContour((Integer) spinnerShallowContour.getValue());
        this.mapSettings.setS52SafetyDepth((Integer) this.spinnerSafetyDepth.getValue());
        this.mapSettings.setS52SafetyContour((Integer) this.spinnerSafetyContour.getValue());
        this.mapSettings.setS52DeepContour((Integer) this.spinnerDeepContour.getValue());
        this.mapSettings.setS52ShowText(this.chckbxShowText.isSelected());
        this.mapSettings.setS52ShallowPattern(this.chckbxShallowPattern.isSelected());
        this.mapSettings.setUseSimplePointSymbols(this.chckbxSimplePointSymbols.isSelected());
        this.mapSettings.setUsePlainAreas(this.chckbxPlainAreas.isSelected());
        this.mapSettings.setS52TwoShades(this.chckbxTwoShades.isSelected());
        this.mapSettings.setColor(comboBoxColorProfile.getSelectedItem().toString());
    }
    
    public boolean checkSettingsChanged() {
        
        // First check if changes were made in common components.
        boolean changesWereMade = super.checkSettingsChanged();
        
        // Only check if changes were made in ship components if super.checkSettingsChanged
        // return false:
        // Consider a change were made to the common components but not the ship components. It
        // would result in "changesWereMade" to be false, and the changes in common components
        // would not be saved!
        if (!changesWereMade) {
            changesWereMade = 
                    // Extended generel components changes.
                    changed(this.mapSettings.isUseEnc(), this.chckbxUseEnc.isSelected()) ||
                    changed(this.mapSettings.isUseWms(), this.chckbxUseWms.isSelected()) ||
                    changed(this.mapSettings.isUseWmsDragging(), this.chckbxDragWmsdisable.isSelected()) ||
                    
                    // S52 panel changes.
                    changed(this.mapSettings.getS52ShallowContour(), this.spinnerShallowContour.getValue()) || 
                    changed(this.mapSettings.getS52SafetyDepth(), this.spinnerSafetyDepth.getValue()) ||
                    changed(this.mapSettings.getS52SafetyContour(), this.spinnerSafetyContour.getValue()) ||
                    changed(this.mapSettings.getS52DeepContour(), this.spinnerDeepContour.getValue()) ||
                    changed(this.mapSettings.isS52ShowText(), this.chckbxShowText.isSelected()) ||
                    changed(this.mapSettings.isS52ShallowPattern(), this.chckbxShallowPattern.isSelected()) ||
                    changed(this.mapSettings.isUseSimplePointSymbols(), this.chckbxSimplePointSymbols.isSelected()) ||
                    changed(this.mapSettings.isUsePlainAreas(), this.chckbxPlainAreas.isSelected()) ||
                    changed(this.mapSettings.isS52TwoShades(), this.chckbxTwoShades.isSelected()) ||
                    changed(this.mapSettings.getColor(), this.comboBoxColorProfile.getSelectedItem().toString());
        }
        
        return changesWereMade;
    }
}
