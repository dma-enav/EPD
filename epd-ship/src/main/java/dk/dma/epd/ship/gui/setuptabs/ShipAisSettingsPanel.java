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
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.CloudSettings;
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
    private CloudSettings cloudSettings;
    private JCheckBox chckbxShowShipLabels;
    private JCheckBox chckbxBroadcastIntendedRoute;
    private JSpinner spinnerIntendedRouteMaxWps;
    private JSpinner spinnerIntendedRouteMaxTime;

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
        
        
        /************** AIS intended route settings ***************/
        
        JPanel intendedRoutePanel = new JPanel();
        intendedRoutePanel.setBounds(6, 297, 438, 115);
        intendedRoutePanel.setLayout(null);
        intendedRoutePanel.setBorder(new TitledBorder(
                null, "AIS Intended Route", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        chckbxBroadcastIntendedRoute = new JCheckBox("Broadcast intended route");
        chckbxBroadcastIntendedRoute.setBounds(16, 20, 189, 20);
        intendedRoutePanel.add(chckbxBroadcastIntendedRoute);
        
        spinnerIntendedRouteMaxWps = new JSpinner();
        spinnerIntendedRouteMaxWps.setBounds(16, 50, 75, 20);
        intendedRoutePanel.add(spinnerIntendedRouteMaxWps);
        
        JLabel lblMaximumWaypointsIn = new JLabel("Maximum waypoints in an intended route");
        lblMaximumWaypointsIn.setBounds(103, 52, 261, 16);
        intendedRoutePanel.add(lblMaximumWaypointsIn);
        
        spinnerIntendedRouteMaxTime = new JSpinner();
        spinnerIntendedRouteMaxTime.setBounds(16, 75, 75, 20);
        intendedRoutePanel.add(spinnerIntendedRouteMaxTime);
        
        JLabel lblMaximumDurationOf = new JLabel("Maximum duration of intended route (min)");
        lblMaximumDurationOf.setBounds(103, 77, 268, 16);
        intendedRoutePanel.add(lblMaximumDurationOf);
        
        add(intendedRoutePanel);
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
        cloudSettings = EPDShip.getInstance().getSettings().getCloudSettings();
        
        // Load appearance settings.
        chckbxShowShipLabels.setSelected(aisSettings.isShowNameLabels());
        spinnerCogVectorLengthMin.setValue(aisSettings.getCogVectorLengthMin());
        spinnerCogVectorLengthMax.setValue(aisSettings.getCogVectorLengthMax());
        spinnerCogVectorLengthScaleStepSize.setValue(aisSettings.getCogVectorLengthScaleInterval());
        spinnerCogVectorHideBelow.setValue(aisSettings.getCogVectorHideBelow());
        spinnerAISRedraw.setValue(aisSettings.getMinRedrawInterval());
        
        // Load cloud Intended Route settings
        chckbxBroadcastIntendedRoute.setSelected(cloudSettings.isBroadcastIntendedRoute());
        //spinnerIntendedRouteMaxWps.setValue(cloudSettings.getIntendedRouteMaxWps());
        //spinnerIntendedRouteMaxTime.setValue(cloudSettings.getIntendedRouteMaxTime());
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

        // Save cloud intended route settings
//        cloudSettings.setBroadcastIntendedRoute(chckbxBroadcastIntendedRoute.isSelected());
        //cloudSettings.setIntendedRouteMaxWps((Integer) spinnerIntendedRouteMaxWps.getValue());
        //cloudSettings.setIntendedRouteMaxTime((Integer) spinnerIntendedRouteMaxTime.getValue());
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
                    changed(aisSettings.getMinRedrawInterval(), spinnerAISRedraw.getValue()) ||
                    
                    // Changes were made to AIS intended route settings.
                    changed(cloudSettings.isBroadcastIntendedRoute(), chckbxBroadcastIntendedRoute.isSelected());
                    //changed(cloudSettings.getIntendedRouteMaxWps(), spinnerIntendedRouteMaxWps.getValue()) ||
                    //changed(cloudSettings.getIntendedRouteMaxTime(), spinnerIntendedRouteMaxTime.getValue());
        }
        
        return changesWereMade;
    }
}
