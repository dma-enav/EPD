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

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.EPDNavSettings;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

public class ShipNavigationSettingsPanel extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JCheckBox chckbxLookAhead;
    private JSpinner spinnerAutoFollowTolerance;
    private JSpinner spinnerScaleToShowMinuteMarks;
    private EPDNavSettings settings;
    private JSpinner spinnerScaleToShowRouteArrows;
    private JSpinner spinnerNewRouteDefaultSpeed;
    private JSpinner spinnerNewRouteDefaultTurnRadius;
    private JSpinner spinnerNewRouteDefaultXtd;

    public ShipNavigationSettingsPanel() {
        super("Navigation", new ImageIcon(ShipAisSettingsPanel.class.getResource("/images/toolbar/radar.png")));
        setLayout(null);
        
        // Own Ship settings.
        JPanel ownShipPanel = new JPanel();
        ownShipPanel.setBounds(6, 6, 438, 110);
        ownShipPanel.setLayout(null);
        ownShipPanel.setBorder(new TitledBorder(null, "Own Ship", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));        
        
        chckbxLookAhead = new JCheckBox("Look ahead");
        chckbxLookAhead.setBounds(16, 20, 128, 20);
        ownShipPanel.add(chckbxLookAhead);
        
        spinnerAutoFollowTolerance = new JSpinner();
        spinnerAutoFollowTolerance.setBounds(16, 45, 75, 20);
        ownShipPanel.add(spinnerAutoFollowTolerance);
        
        JLabel lblAutoFollowTolerance = new JLabel("Auto follow tolerance (%)");
        lblAutoFollowTolerance.setBounds(103, 47, 156, 16);
        ownShipPanel.add(lblAutoFollowTolerance);
        
        spinnerScaleToShowMinuteMarks = new JSpinner();
        spinnerScaleToShowMinuteMarks.setBounds(16, 70, 75, 20);
        ownShipPanel.add(spinnerScaleToShowMinuteMarks);
        
        JLabel lblScaleToShow = new JLabel("Scale to show minute marks (screen distance in pixed)");
        lblScaleToShow.setBounds(103, 72, 342, 16);
        ownShipPanel.add(lblScaleToShow);
        
        // Add the panel.
        this.add(ownShipPanel);
        
        // Route settings
        JPanel routePanel = new JPanel();
        routePanel.setBounds(6, 128, 438, 135);
        routePanel.setLayout(null);
        routePanel.setBorder(new TitledBorder(null, "Route Settings", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));

        spinnerScaleToShowRouteArrows = new JSpinner(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
        spinnerScaleToShowRouteArrows.setBounds(16, 20, 75, 20);
        routePanel.add(spinnerScaleToShowRouteArrows);
        
        spinnerNewRouteDefaultSpeed = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Float(1)));
        spinnerNewRouteDefaultSpeed.setBounds(16, 45, 75, 20);
        routePanel.add(spinnerNewRouteDefaultSpeed);
        
        spinnerNewRouteDefaultTurnRadius = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Float(1)));
        spinnerNewRouteDefaultTurnRadius.setBounds(16, 70, 75, 20);
        routePanel.add(spinnerNewRouteDefaultTurnRadius);
        
        spinnerNewRouteDefaultXtd = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Float(1)));
        spinnerNewRouteDefaultXtd.setBounds(16, 95, 75, 20);
        routePanel.add(spinnerNewRouteDefaultXtd);
        
        JLabel lblScaleToShow_1 = new JLabel("Scale to show route arrows (map scale)");
        lblScaleToShow_1.setBounds(103, 22, 329, 16);
        routePanel.add(lblScaleToShow_1);
        
        JLabel lblNewRouteDefault = new JLabel("New route default speed");
        lblNewRouteDefault.setBounds(103, 47, 329, 16);
        routePanel.add(lblNewRouteDefault);
        
        JLabel lblNewRouteDefault_1 = new JLabel("New route default turn radius");
        lblNewRouteDefault_1.setBounds(103, 72, 329, 16);
        routePanel.add(lblNewRouteDefault_1);
        
        JLabel lblNewRouteDefault_2 = new JLabel("New route default xtd");
        lblNewRouteDefault_2.setBounds(103, 97, 329, 16);
        routePanel.add(lblNewRouteDefault_2);
        
        // Add the panel.
        this.add(routePanel);
    }
    
    @Override
    protected boolean checkSettingsChanged() {
        
        return
                // Check for changes in own ship settings.
                changed(this.settings.isLookAhead(), this.chckbxLookAhead.isSelected()) ||
                changed(this.settings.getAutoFollowPctOffTollerance(), this.spinnerAutoFollowTolerance.getValue()) ||
                changed(this.settings.getShowMinuteMarksSelf(), this.spinnerScaleToShowMinuteMarks.getValue()) ||
                
                // Check for changes in route settings.
                changed(this.settings.getShowArrowScale(), this.spinnerScaleToShowRouteArrows.getValue()) ||
                changed(this.settings.getDefaultSpeed(), this.spinnerNewRouteDefaultSpeed.getValue()) ||
                changed(this.settings.getDefaultTurnRad(), this.spinnerNewRouteDefaultTurnRadius.getValue()) ||
                changed(this.settings.getDefaultXtd(), this.spinnerNewRouteDefaultXtd.getValue());
    }

    @Override
    protected void doLoadSettings() {
        
        settings = EPDShip.getInstance().getSettings().getNavSettings();
        
        // Initialize Own Ship settings.
        this.chckbxLookAhead.setSelected(this.settings.isLookAhead());
        this.spinnerAutoFollowTolerance.setValue(this.settings.getAutoFollowPctOffTollerance());
        this.spinnerScaleToShowMinuteMarks.setValue(this.settings.getShowMinuteMarksSelf());
        
        // Initialize Route settings.
        this.spinnerScaleToShowRouteArrows.setValue(this.settings.getShowArrowScale());
        this.spinnerNewRouteDefaultSpeed.setValue(this.settings.getDefaultSpeed());
        this.spinnerNewRouteDefaultTurnRadius.setValue(this.settings.getDefaultTurnRad());
        this.spinnerNewRouteDefaultXtd.setValue(this.settings.getDefaultXtd());
        
    }

    @Override
    protected void doSaveSettings() {
        
        // Save Onw Ship settings.
        this.settings.setLookAhead(this.chckbxLookAhead.isSelected());
        this.settings.setAutoFollowPctOffTollerance((Integer) this.spinnerAutoFollowTolerance.getValue());
        this.settings.setShowMinuteMarksSelf((Integer) this.spinnerScaleToShowMinuteMarks.getValue());
        
        // Save route settings.
        this.settings.setShowArrowScale((Float) this.spinnerScaleToShowRouteArrows.getValue());
        this.settings.setDefaultSpeed((Double) this.spinnerNewRouteDefaultSpeed.getValue());
        this.settings.setDefaultTurnRad((Double) this.spinnerNewRouteDefaultTurnRadius.getValue());
        this.settings.setDefaultXtd((Double) this.spinnerNewRouteDefaultXtd.getValue());
    }

    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.NAVIGATION);
    }

}
