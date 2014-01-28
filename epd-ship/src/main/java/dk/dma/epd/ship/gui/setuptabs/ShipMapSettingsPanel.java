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

import dk.dma.epd.common.prototype.gui.settings.MapSettingsPanelCommon;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JList;

public class ShipMapSettingsPanel extends MapSettingsPanelCommon {

    private static final long serialVersionUID = 1L;

    public ShipMapSettingsPanel() {
        
        // Resize the panel to add spaces for ekstra ship options.
        this.getGenerelPanel().setSize(438, 220);
        this.getWMSPanel().setBounds(6, 485, 
                438, 209);
        
        JLabel lblWmsServices = new JLabel("WMS Services");
        lblWmsServices.setBounds(6, 90, 84, 16);
        getWMSPanel().add(lblWmsServices);
        
        JList list = new JList();
        list.setBounds(6, 105, 426, 98);
        getWMSPanel().add(list);
        
        JCheckBox chckbxUseEnc = new JCheckBox("Use ENC");
        chckbxUseEnc.setBounds(6, 139, 85, 23);
        getGenerelPanel().add(chckbxUseEnc);
        
        JCheckBox chckbxUseWms = new JCheckBox("Use WMS");
        chckbxUseWms.setBounds(103, 139, 88, 23);
        getGenerelPanel().add(chckbxUseWms);
        
        JCheckBox chckbxDragWmsdisable = new JCheckBox("Drag WMS (disable for performance)");
        chckbxDragWmsdisable.setBounds(6, 174, 257, 23);
        getGenerelPanel().add(chckbxDragWmsdisable);
        
        JPanel s57Settings = new JPanel();
        s57Settings.setBounds(6, 238, 438, 235);
        s57Settings.setLayout(null);
        s57Settings.setBorder(new TitledBorder(null, "S52 Layer", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));
        
        
        add(s57Settings);
        
        JSpinner spinner = new JSpinner();
        spinner.setBounds(6, 20, 75, 22);
        s57Settings.add(spinner);
        
        JSpinner spinner_1 = new JSpinner();
        spinner_1.setBounds(6, 54, 75, 22);
        s57Settings.add(spinner_1);
        
        JSpinner spinner_2 = new JSpinner();
        spinner_2.setBounds(6, 88, 75, 22);
        s57Settings.add(spinner_2);
        
        JSpinner spinner_3 = new JSpinner();
        spinner_3.setBounds(6, 122, 75, 22);
        s57Settings.add(spinner_3);
        
        JLabel lblShallowContour = new JLabel("Shallow contour");
        lblShallowContour.setBounds(93, 23, 101, 16);
        s57Settings.add(lblShallowContour);
        
        JLabel lblSafetyDepth = new JLabel("Safety depth");
        lblSafetyDepth.setBounds(93, 57, 78, 16);
        s57Settings.add(lblSafetyDepth);
        
        JLabel lblSafetyContour = new JLabel("Safety contour");
        lblSafetyContour.setBounds(93, 91, 91, 16);
        s57Settings.add(lblSafetyContour);
        
        JLabel lblDeepContour = new JLabel("Deep contour");
        lblDeepContour.setBounds(93, 125, 91, 16);
        s57Settings.add(lblDeepContour);
        
        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(206, 19, 75, 27);
        s57Settings.add(comboBox);
        
        JLabel lblColorProfile = new JLabel("Color profile");
        lblColorProfile.setBounds(286, 23, 91, 16);
        s57Settings.add(lblColorProfile);
        
        JCheckBox chckbxShowText = new JCheckBox("Show text");
        chckbxShowText.setBounds(6, 156, 128, 16);
        s57Settings.add(chckbxShowText);
        
        JCheckBox chckbxShallowPattern = new JCheckBox("Shallow pattern");
        chckbxShallowPattern.setBounds(6, 184, 142, 16);
        s57Settings.add(chckbxShallowPattern);
        
        JCheckBox chckbxSimplePointSymbols = new JCheckBox("Simple point symbols");
        chckbxSimplePointSymbols.setBounds(6, 212, 168, 16);
        s57Settings.add(chckbxSimplePointSymbols);
        
        JCheckBox chckbxTwoShades = new JCheckBox("Two shades");
        chckbxTwoShades.setBounds(175, 184, 106, 16);
        s57Settings.add(chckbxTwoShades);
        
        JCheckBox chckbxPlainAreas = new JCheckBox("Plain areas");
        chckbxPlainAreas.setBounds(175, 156, 106, 16);
        s57Settings.add(chckbxPlainAreas);
        
        JButton btnNewButton = new JButton("Advanced Options");
        btnNewButton.setEnabled(false);
        btnNewButton.setBounds(175, 207, 117, 29);
        s57Settings.add(btnNewButton);
    }
}
