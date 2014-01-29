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
    private AisSettings settings;
    private JCheckBox chckbxShowShipLabels;
    private JCheckBox chckbxBroadcastIntendedRoute;
    private JCheckBox chckbxShowIntendedRoutesByDefault;
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
        
        this.spinnerCogVectorLengthMin = new JSpinner(new SpinnerNumberModel(
                new Integer(1), new Integer(1), null, new Integer(1)));
        this.spinnerCogVectorLengthMin.setBounds(16, 45, 75, 20);
        appearancePanel.add(this.spinnerCogVectorLengthMin);
        
        this.spinnerCogVectorLengthMax = new JSpinner(new SpinnerNumberModel(
                new Integer(6), new Integer(1), null, new Integer(1)));
        this.spinnerCogVectorLengthMax.setBounds(16, 70, 75, 20);
        appearancePanel.add(this.spinnerCogVectorLengthMax);
        
        this.spinnerCogVectorLengthScaleStepSize = new JSpinner(new SpinnerNumberModel(
                new Float(5000), new Float(2000), null, new Float(1000)));
        this.spinnerCogVectorLengthScaleStepSize.setBounds(16, 95, 75, 20);
        appearancePanel.add(this.spinnerCogVectorLengthScaleStepSize);
        
        this.spinnerCogVectorHideBelow = new JSpinner(new SpinnerNumberModel(
                new Float(0.1), new Float(0.1), new Float(100), new Float(0.1)));
        this.spinnerCogVectorHideBelow.setBounds(16, 120, 75, 20);
        appearancePanel.add(this.spinnerCogVectorHideBelow);
        
        this.spinnerAISRedraw = new JSpinner();
        this.spinnerAISRedraw.setBounds(16, 145, 75, 20);
        appearancePanel.add(this.spinnerAISRedraw);
        
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
        
        this.add(appearancePanel);
        
        
        /************** AIS intended route settings ***************/
        
        JPanel intendedRoutePanel = new JPanel();
        intendedRoutePanel.setBounds(6, 297, 438, 135);
        intendedRoutePanel.setLayout(null);
        intendedRoutePanel.setBorder(new TitledBorder(
                null, "AIS Intended Route", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        chckbxBroadcastIntendedRoute = new JCheckBox("Broadcast intended route");
        chckbxBroadcastIntendedRoute.setBounds(16, 20, 189, 20);
        intendedRoutePanel.add(chckbxBroadcastIntendedRoute);
        
        chckbxShowIntendedRoutesByDefault = new JCheckBox("Show intended routes by default");
        chckbxShowIntendedRoutesByDefault.setBounds(16, 45, 235, 20);
        intendedRoutePanel.add(chckbxShowIntendedRoutesByDefault);
        
        spinnerIntendedRouteMaxWps = new JSpinner();
        spinnerIntendedRouteMaxWps.setBounds(16, 70, 75, 20);
        intendedRoutePanel.add(spinnerIntendedRouteMaxWps);
        
        JLabel lblMaximumWaypointsIn = new JLabel("Maximum waypoints in an intended route");
        lblMaximumWaypointsIn.setBounds(103, 72, 261, 16);
        intendedRoutePanel.add(lblMaximumWaypointsIn);
        
        spinnerIntendedRouteMaxTime = new JSpinner();
        spinnerIntendedRouteMaxTime.setBounds(16, 95, 75, 20);
        intendedRoutePanel.add(spinnerIntendedRouteMaxTime);
        
        JLabel lblMaximumDurationOf = new JLabel("Maximum duration of intended route (min)");
        lblMaximumDurationOf.setBounds(103, 97, 268, 16);
        intendedRoutePanel.add(lblMaximumDurationOf);
        
        this.add(intendedRoutePanel);
    }
    
    /**
     * Loads settings for the common components of the panel and for the ship 
     * specific AIS settings. 
     */
    public void doLoadSettings() {
        
        // Load the settings for the common components.
        super.doLoadSettings();
        
        // Get AIS settings.
        this.settings = EPDShip.getInstance().getSettings().getAisSettings();
        
        // Load appearance settings.
        this.chckbxShowShipLabels.setSelected(this.settings.isShowNameLabels());
        this.spinnerCogVectorLengthMin.setValue(this.settings.getCogVectorLengthMin());
        this.spinnerCogVectorLengthMax.setValue(this.settings.getCogVectorLengthMax());
        this.spinnerCogVectorLengthScaleStepSize.setValue(this.settings.getCogVectorLengthScaleInterval());
        this.spinnerCogVectorHideBelow.setValue(this.settings.getCogVectorHideBelow());
        this.spinnerAISRedraw.setValue(this.settings.getMinRedrawInterval());
        
        // Load AIS Intended Route settings
        this.chckbxBroadcastIntendedRoute.setSelected(this.settings.isBroadcastIntendedRoute());
        this.chckbxShowIntendedRoutesByDefault.setSelected(this.settings.isShowIntendedRouteByDefault());
        this.spinnerIntendedRouteMaxWps.setValue(this.settings.getIntendedRouteMaxWps());
        this.spinnerIntendedRouteMaxTime.setValue(this.settings.getIntendedRouteMaxTime());
    }
    
    public void doSaveSettings() {
        
        // Save the settings for the common components.
        super.doSaveSettings();
        
        // Save appearance settings.
        this.settings.setShowNameLabels(chckbxShowShipLabels.isSelected());
        this.settings.setCogVectorLengthMin((Integer) spinnerCogVectorLengthMin.getValue());
        this.settings.setCogVectorLengthMax((Integer) spinnerCogVectorLengthMax.getValue());
        this.settings.setCogVectorLengthScaleInterval((Float) spinnerCogVectorLengthScaleStepSize.getValue());
        this.settings.setCogVectorHideBelow((Float) spinnerCogVectorHideBelow.getValue());
        this.settings.setMinRedrawInterval((Integer) spinnerAISRedraw.getValue());

        // Save AIS intended route settings
        this.settings.setBroadcastIntendedRoute(chckbxBroadcastIntendedRoute.isSelected());
        this.settings.setShowIntendedRouteByDefault(chckbxShowIntendedRoutesByDefault.isSelected());
        this.settings.setIntendedRouteMaxWps((Integer) spinnerIntendedRouteMaxWps.getValue());
        this.settings.setIntendedRouteMaxTime((Integer) spinnerIntendedRouteMaxTime.getValue());
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
                    changed(this.settings.isShowNameLabels(), chckbxShowShipLabels.isSelected()) ||
                    changed(this.settings.getCogVectorLengthMin(), spinnerCogVectorLengthMin.getValue()) ||
                    changed(this.settings.getCogVectorLengthMax(), spinnerCogVectorLengthMax.getValue()) ||
                    changed(this.settings.getCogVectorLengthScaleInterval(), spinnerCogVectorLengthScaleStepSize.getValue()) ||
                    changed(this.settings.getCogVectorHideBelow(), spinnerCogVectorHideBelow.getValue()) ||
                    changed(this.settings.getMinRedrawInterval(), spinnerAISRedraw.getValue()) ||
                    
                    // Changes were made to AIS intended route settings.
                    changed(this.settings.isBroadcastIntendedRoute(), chckbxBroadcastIntendedRoute.isSelected()) ||
                    changed(this.settings.isShowIntendedRouteByDefault(), chckbxShowIntendedRoutesByDefault.isSelected()) ||
                    changed(this.settings.getIntendedRouteMaxWps(), spinnerIntendedRouteMaxWps.getValue()) ||
                    changed(this.settings.getIntendedRouteMaxTime(), spinnerIntendedRouteMaxTime.getValue());
        }
        
        return changesWereMade;
    }
}
