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
package dk.dma.epd.ship.gui.setuptabs;

import dk.dma.epd.common.prototype.gui.settings.CommonAisSettingsPanel;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.ship.EPDShip;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

/**
 * 
 * @author adamduehansen
 *
 */
public class ShipAisSettingsPanel extends CommonAisSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JSpinner spinnerCogVectorLengthMin;
    private JSpinner spinnerCogVectorLengthMax;
    private JSpinner spinnerCogVectorLengthScaleStepSize;
    private JSpinner spinnerCogVectorHideBelow;
    private JSpinner spinnerAISRedraw;
    private AisSettings aisSettings;
    private JCheckBox chckbxShowShipLabels;

    /**
     * Constructs a new ShipAisSettingsPanel object.
     */
    public ShipAisSettingsPanel() {

        // Get the transponderPanel from super class.
        super.getTransponderPanel().setLocation(6, 6);
        
        
        /************** Appearance settings ***************/
        
        JPanel appearancePanel = new JPanel();
        appearancePanel.setBounds(6, 100, 438, 185);
        appearancePanel.setLayout(null);
        appearancePanel.setBorder(new TitledBorder(
                null, "Appearance", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        chckbxShowShipLabels = new JCheckBox("Show ship labels");
        chckbxShowShipLabels.setBounds(16, 20, 137, 23);
        appearancePanel.add(chckbxShowShipLabels);
        
        spinnerCogVectorLengthMin = new JSpinner(new SpinnerNumberModel(
                new Integer(1), new Integer(1), null, new Integer(1)));
        spinnerCogVectorLengthMin.setBounds(16, 45, 75, 20);
        appearancePanel.add(spinnerCogVectorLengthMin);
        
        spinnerCogVectorLengthMax = new JSpinner(new SpinnerNumberModel(
                new Integer(6), new Integer(1), null, new Integer(1)));
        spinnerCogVectorLengthMax.setBounds(16, 70, 75, 20);
        appearancePanel.add(spinnerCogVectorLengthMax);
        
        spinnerCogVectorLengthScaleStepSize = new JSpinner(new SpinnerNumberModel(
                new Float(5000), new Float(2000), null, new Float(1000)));
        spinnerCogVectorLengthScaleStepSize.setBounds(16, 95, 75, 20);
        appearancePanel.add(spinnerCogVectorLengthScaleStepSize);
        
        spinnerCogVectorHideBelow = new JSpinner(new SpinnerNumberModel(
                new Float(0.1), new Float(0.1), new Float(100), new Float(0.1)));
        spinnerCogVectorHideBelow.setBounds(16, 120, 75, 20);
        appearancePanel.add(spinnerCogVectorHideBelow);
        
        spinnerAISRedraw = new JSpinner();
        spinnerAISRedraw.setBounds(16, 145, 75, 20);
        appearancePanel.add(spinnerAISRedraw);
        
        JLabel lblOwnShipAnd = new JLabel("Own ship and targets COG vector length minimum (minutes)");
        lblOwnShipAnd.setBounds(103, 47, 383, 16);
        appearancePanel.add(lblOwnShipAnd);
        
        JLabel lblOwnShipAnd_1 = new JLabel("Own ship and targets COG vector length maximum (minutes)");
        lblOwnShipAnd_1.setBounds(103, 72, 383, 16);
        appearancePanel.add(lblOwnShipAnd_1);
        
        JLabel lblScaleStepSize = new JLabel("Scale step size for each increment of the COG vector length");
        lblScaleStepSize.setBounds(103, 97, 374, 16);
        appearancePanel.add(lblScaleStepSize);
        
        JLabel lblHideCogVector = new JLabel("Hide COG vector when below (kn)");
        lblHideCogVector.setBounds(103, 122, 210, 16);
        appearancePanel.add(lblHideCogVector);
        
        JLabel lblAisRedrawInterval = new JLabel("AIS redraw interval (sec)");
        lblAisRedrawInterval.setBounds(103, 147, 150, 16);
        appearancePanel.add(lblAisRedrawInterval);
        
        add(appearancePanel);
    }
    
    /**
     * Loads settings for the common components of the panel and for the ship 
     * specific AIS settings. 
     */
    public void doLoadSettings() {
        
        // Load the settings for the common components.
        super.doLoadSettings();
        
        // Get settings.
        aisSettings = EPDShip.getInstance().getSettings().getAisSettings();
        
        // Load appearance settings.
        chckbxShowShipLabels.setSelected(aisSettings.isShowNameLabels());
        spinnerCogVectorLengthMin.setValue(aisSettings.getCogVectorLengthMin());
        spinnerCogVectorLengthMax.setValue(aisSettings.getCogVectorLengthMax());
        spinnerCogVectorLengthScaleStepSize.setValue(aisSettings.getCogVectorLengthScaleInterval());
        spinnerCogVectorHideBelow.setValue(aisSettings.getCogVectorHideBelow());
        spinnerAISRedraw.setValue(aisSettings.getMinRedrawInterval());
    }
    
    public void doSaveSettings() {
        
        // Save the settings for the common components.
        super.doSaveSettings();
        
        // Save appearance settings.
        aisSettings.setShowNameLabels(chckbxShowShipLabels.isSelected());
        aisSettings.setCogVectorLengthMin((Integer) spinnerCogVectorLengthMin.getValue());
        aisSettings.setCogVectorLengthMax((Integer) spinnerCogVectorLengthMax.getValue());
        aisSettings.setCogVectorLengthScaleInterval((Float) spinnerCogVectorLengthScaleStepSize.getValue());
        aisSettings.setCogVectorHideBelow((Float) spinnerCogVectorHideBelow.getValue());
        aisSettings.setMinRedrawInterval((Integer) spinnerAISRedraw.getValue());
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
                    // Changes were made to appearance settings.
                    changed(aisSettings.isShowNameLabels(), chckbxShowShipLabels.isSelected()) ||
                    changed(aisSettings.getCogVectorLengthMin(), spinnerCogVectorLengthMin.getValue()) ||
                    changed(aisSettings.getCogVectorLengthMax(), spinnerCogVectorLengthMax.getValue()) ||
                    changed(aisSettings.getCogVectorLengthScaleInterval(), spinnerCogVectorLengthScaleStepSize.getValue()) ||
                    changed(aisSettings.getCogVectorHideBelow(), spinnerCogVectorHideBelow.getValue()) ||
                    changed(aisSettings.getMinRedrawInterval(), spinnerAISRedraw.getValue());
        }
        
        return changesWereMade;
    }
}
