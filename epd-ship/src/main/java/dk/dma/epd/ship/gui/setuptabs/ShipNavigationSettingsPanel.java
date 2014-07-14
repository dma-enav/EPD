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

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.layers.RouteLayerCommonSettings;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.gui.MapSettings;
import dk.dma.epd.ship.settings.handlers.RouteManagerSettings;

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
public class ShipNavigationSettingsPanel extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JCheckBox chckbxLookAhead;
    private JSpinner spinnerAutoFollowTolerance;
//    private JSpinner spinnerScaleToShowMinuteMarks;
    private JSpinner spinnerScaleToShowRouteArrows;
    private JSpinner spinnerNewRouteDefaultSpeed;
    private JSpinner spinnerNewRouteDefaultTurnRadius;
    private JSpinner spinnerNewRouteDefaultXtd;

    private MapSettings mapSettings;
    private RouteManagerSettings<?> routeManagerSettings;
    private RouteLayerCommonSettings<?> routeLayerSettings;
    
    /**
     * Constructs a new ShipNavigationSettingsPanel object.
     */
    public ShipNavigationSettingsPanel() {
        super("Navigation", new ImageIcon(ShipAisSettingsPanel.class.getResource("/images/settingspanels/navigation.png")));
        setLayout(null);
        
        
        /************** Own ship settings ***************/
        
        JPanel ownShipPanel = new JPanel();
        ownShipPanel.setBounds(6, 6, 438, 110);
        ownShipPanel.setLayout(null);
        ownShipPanel.setBorder(new TitledBorder(null, "Own Ship", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));        
        
        this.chckbxLookAhead = new JCheckBox("Look ahead");
        this.chckbxLookAhead.setBounds(16, 20, 128, 20);
        ownShipPanel.add(this.chckbxLookAhead);
        
        this.spinnerAutoFollowTolerance = new JSpinner();
        this.spinnerAutoFollowTolerance.setBounds(16, 45, 75, 20);
        ownShipPanel.add(this.spinnerAutoFollowTolerance);
        
        JLabel lblAutoFollowTolerance = new JLabel("Auto follow tolerance (%)");
        lblAutoFollowTolerance.setBounds(103, 47, 156, 16);
        ownShipPanel.add(lblAutoFollowTolerance);
        
//        this.spinnerScaleToShowMinuteMarks = new JSpinner();
//        this.spinnerScaleToShowMinuteMarks.setBounds(16, 70, 75, 20);
//        ownShipPanel.add(this.spinnerScaleToShowMinuteMarks);
        
        JLabel lblScaleToShow = new JLabel("Scale to show minute marks (screen distance in pixed)");
        lblScaleToShow.setBounds(103, 72, 342, 16);
        ownShipPanel.add(lblScaleToShow);
        
        this.add(ownShipPanel);
        
        
        /************** Route settings ***************/
        
        JPanel routePanel = new JPanel();
        routePanel.setBounds(6, 128, 438, 135);
        routePanel.setLayout(null);
        routePanel.setBorder(new TitledBorder(null, "Route Settings", TitledBorder.LEADING, 
                TitledBorder.TOP, null, null));

        this.spinnerScaleToShowRouteArrows = new JSpinner(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
        this.spinnerScaleToShowRouteArrows.setBounds(16, 20, 75, 20);
        routePanel.add(this.spinnerScaleToShowRouteArrows);
        
        this.spinnerNewRouteDefaultSpeed = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Float(1)));
        this.spinnerNewRouteDefaultSpeed.setBounds(16, 45, 75, 20);
        routePanel.add(spinnerNewRouteDefaultSpeed);
        
        this.spinnerNewRouteDefaultTurnRadius = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Float(1)));
        this.spinnerNewRouteDefaultTurnRadius.setBounds(16, 70, 75, 20);
        routePanel.add(spinnerNewRouteDefaultTurnRadius);
        
        this.spinnerNewRouteDefaultXtd = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Float(1)));
        this.spinnerNewRouteDefaultXtd.setBounds(16, 95, 75, 20);
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
        
        this.add(routePanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        
        return
                // Check for changes in own ship settings.
                changed(this.mapSettings.isLookAhead(), this.chckbxLookAhead.isSelected()) ||
                changed(this.mapSettings.getAutoFollowPctOffTollerance(), this.spinnerAutoFollowTolerance.getValue()) ||
//                changed(this.settings.getShowMinuteMarksSelf(), this.spinnerScaleToShowMinuteMarks.getValue()) ||
                
                // Check for changes in route settings.
                changed(this.routeLayerSettings.getShowArrowScale(), this.spinnerScaleToShowRouteArrows.getValue()) ||
                changed(this.routeManagerSettings.getDefaultSpeed(), this.spinnerNewRouteDefaultSpeed.getValue()) ||
                changed(this.routeManagerSettings.getDefaultTurnRad(), this.spinnerNewRouteDefaultTurnRadius.getValue()) ||
                changed(this.routeManagerSettings.getDefaultXtd(), this.spinnerNewRouteDefaultXtd.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
//        settings = EPDShip.getInstance().getSettings().getNavSettings();
        this.mapSettings = EPDShip.getInstance().getSettings().getMapSettings();
        this.routeManagerSettings = EPDShip.getInstance().getSettings().getRouteManagerSettings();
        this.routeLayerSettings = EPDShip.getInstance().getSettings().getPrimaryRouteLayerSettings();
        
        // Initialize Own Ship settings.
        this.chckbxLookAhead.setSelected(this.mapSettings.isLookAhead());
        this.spinnerAutoFollowTolerance.setValue(this.mapSettings.getAutoFollowPctOffTollerance());
//        this.spinnerScaleToShowMinuteMarks.setValue(this.mapSettings.getShowMinuteMarksSelf());
        
        // Initialize Route settings.
        this.spinnerScaleToShowRouteArrows.setValue(this.routeLayerSettings.getShowArrowScale());
        this.spinnerNewRouteDefaultSpeed.setValue(this.routeManagerSettings.getDefaultSpeed());
        this.spinnerNewRouteDefaultTurnRadius.setValue(this.routeManagerSettings.getDefaultTurnRad());
        this.spinnerNewRouteDefaultXtd.setValue(this.routeManagerSettings.getDefaultXtd());
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        
        // Save Onw Ship settings.
        this.mapSettings.setLookAhead(this.chckbxLookAhead.isSelected());
        this.mapSettings.setAutoFollowPctOffTollerance((Integer) this.spinnerAutoFollowTolerance.getValue());
//        this.settings.setShowMinuteMarksSelf((Integer) this.spinnerScaleToShowMinuteMarks.getValue());
        
        // Save route settings.
        this.routeLayerSettings.setShowArrowScale((Float) this.spinnerScaleToShowRouteArrows.getValue());
        this.routeManagerSettings.setDefaultSpeed((Double) this.spinnerNewRouteDefaultSpeed.getValue());
        this.routeManagerSettings.setDefaultTurnRad((Double) this.spinnerNewRouteDefaultTurnRadius.getValue());
        this.routeManagerSettings.setDefaultXtd((Double) this.spinnerNewRouteDefaultXtd.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.NAVIGATION);
    }

}
