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

import dk.dma.epd.common.prototype.gui.settings.CommonAisSettingsPanel;
import dk.dma.epd.common.prototype.settings.handlers.AisHandlerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.AisLayerCommonSettings;

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
    private JCheckBox chckbxShowShipLabels;
    private AisLayerCommonSettings<?> layerSettings;
    
    /**
     * Constructs a new ShipAisSettingsPanel object.
     */
    public ShipAisSettingsPanel(AisHandlerCommonSettings<?> handlerSettings, AisLayerCommonSettings<?> layerSettings) {
        super(handlerSettings);
        this.layerSettings = layerSettings;
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
        
        // Load appearance settings.
        chckbxShowShipLabels.setSelected(layerSettings.isShowVesselNameLabels());
        spinnerCogVectorLengthMin.setValue(layerSettings.getMovementVectorLengthMin());
        spinnerCogVectorLengthMax.setValue(layerSettings.getMovementVectorLengthMax());
        spinnerCogVectorLengthScaleStepSize.setValue(layerSettings.getMovementVectorLengthStepSize());
        spinnerCogVectorHideBelow.setValue(layerSettings.getMovementVectorHideBelow());
        spinnerAISRedraw.setValue(layerSettings.getLayerRedrawInterval());
    }
    
    public void doSaveSettings() {
        
        // Save the settings for the common components.
        super.doSaveSettings();
        
        // Save appearance settings.
        layerSettings.setShowVesselNameLabels(chckbxShowShipLabels.isSelected());
        layerSettings.setMovementVectorLengthMin((Integer) spinnerCogVectorLengthMin.getValue());
        layerSettings.setMovementVectorLengthMax((Integer) spinnerCogVectorLengthMax.getValue());
        layerSettings.setMovementVectorLengthStepSize((Float) spinnerCogVectorLengthScaleStepSize.getValue());
        layerSettings.setMovementVectorHideBelow((Float) spinnerCogVectorHideBelow.getValue());
        layerSettings.setLayerRedrawInterval((Integer) spinnerAISRedraw.getValue());
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
                    changed(layerSettings.isShowVesselNameLabels(), chckbxShowShipLabels.isSelected()) ||
                    changed(layerSettings.getMovementVectorLengthMin(), spinnerCogVectorLengthMin.getValue()) ||
                    changed(layerSettings.getMovementVectorLengthMax(), spinnerCogVectorLengthMax.getValue()) ||
                    changed(layerSettings.getMovementVectorLengthStepSize(), spinnerCogVectorLengthScaleStepSize.getValue()) ||
                    changed(layerSettings.getMovementVectorHideBelow(), spinnerCogVectorHideBelow.getValue()) ||
                    changed(layerSettings.getLayerRedrawInterval(), spinnerAISRedraw.getValue());
        }
        
        return changesWereMade;
    }
}
