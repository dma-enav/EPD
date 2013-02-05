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

import dk.dma.epd.ship.settings.EPDAisSettings;
import dk.dma.epd.ship.settings.EPDNavSettings;

/**
 * AIS tab panel in setup panel
 */
public class AisTab extends JPanel {
    private static final long serialVersionUID = 1L;
    private JCheckBox checkBoxAllowSending;
    private JCheckBox checkBoxStrict;
    private JCheckBox checkBoxShowNameLabels;
    private JSpinner spinnerCogVectorLength;
    private JSpinner spinnerShowMinuteMarksAISTarget;
    private JSpinner spinnerMinRedrawInterval;
    private JCheckBox checkBoxShowIntendedRoutesByDefault;
    private JCheckBox checkBoxBroadcastIntendedRoute;
    private JSpinner spinnerIntendedRouteMaxWps;
    private JSpinner spinnerIntendedRouteMaxTime;
    private EPDAisSettings aisSettings;
    private EPDNavSettings navSettings;

    public AisTab() {
        
        JPanel appearancePanel = new JPanel();
        appearancePanel.setBorder(new TitledBorder(null, "Appearance", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        checkBoxShowNameLabels = new JCheckBox("Show ship labels");
        
        spinnerCogVectorLength = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        JLabel lblCogVectorLength = new JLabel("Own ship and targets COG vector length (min)");
        
        spinnerShowMinuteMarksAISTarget = new JSpinner();
        
        JLabel label_1 = new JLabel("Scale to show minute marks (screen distance in pixels)");
        
        spinnerMinRedrawInterval = new JSpinner();
        
        JLabel label_2 = new JLabel("AIS redraw interval (sec)");
        GroupLayout gl_appearancePanel = new GroupLayout(appearancePanel);
        gl_appearancePanel.setHorizontalGroup(
            gl_appearancePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_appearancePanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_appearancePanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(checkBoxShowNameLabels)
                        .addGroup(Alignment.TRAILING, gl_appearancePanel.createSequentialGroup()
                            .addGroup(gl_appearancePanel.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(spinnerMinRedrawInterval)
                                .addComponent(spinnerCogVectorLength)
                                .addComponent(spinnerShowMinuteMarksAISTarget, GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_appearancePanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(label_1)
                                .addComponent(lblCogVectorLength)
                                .addComponent(label_2))
                            .addGap(48))))
        );
        gl_appearancePanel.setVerticalGroup(
            gl_appearancePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_appearancePanel.createSequentialGroup()
                    .addComponent(checkBoxShowNameLabels)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_appearancePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerCogVectorLength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCogVectorLength))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_appearancePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerShowMinuteMarksAISTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_1))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_appearancePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerMinRedrawInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_2))
                    .addContainerGap(14, Short.MAX_VALUE))
        );
        appearancePanel.setLayout(gl_appearancePanel);
        
        JPanel transponderPanel = new JPanel();
        transponderPanel.setBorder(new TitledBorder(null, "Transponder Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        checkBoxAllowSending = new JCheckBox("Allow sending");
        
        checkBoxStrict = new JCheckBox("Strict timeout rules");
        GroupLayout gl_transponderPanel = new GroupLayout(transponderPanel);
        gl_transponderPanel.setHorizontalGroup(
            gl_transponderPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_transponderPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_transponderPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(checkBoxAllowSending)
                        .addComponent(checkBoxStrict))
                    .addContainerGap(405, Short.MAX_VALUE))
        );
        gl_transponderPanel.setVerticalGroup(
            gl_transponderPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_transponderPanel.createSequentialGroup()
                    .addComponent(checkBoxAllowSending)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(checkBoxStrict)
                    .addContainerGap(38, Short.MAX_VALUE))
        );
        transponderPanel.setLayout(gl_transponderPanel);
        
        JPanel intendedRoutePanel = new JPanel();
        intendedRoutePanel.setBorder(new TitledBorder(null, "AIS Intended Route", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        checkBoxShowIntendedRoutesByDefault = new JCheckBox("Show intended routes by default");
        
        checkBoxBroadcastIntendedRoute = new JCheckBox("Broadcast intended route");
        
        spinnerIntendedRouteMaxWps = new JSpinner();
        
        JLabel label_3 = new JLabel("Maximum waypoints in an intended route");
        
        spinnerIntendedRouteMaxTime = new JSpinner();
        
        JLabel label_4 = new JLabel("Maximum duration of intended route (min)");
        GroupLayout gl_intendedRoutePanel = new GroupLayout(intendedRoutePanel);
        gl_intendedRoutePanel.setHorizontalGroup(
            gl_intendedRoutePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_intendedRoutePanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_intendedRoutePanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(checkBoxShowIntendedRoutesByDefault)
                        .addComponent(checkBoxBroadcastIntendedRoute)
                        .addGroup(gl_intendedRoutePanel.createSequentialGroup()
                            .addComponent(spinnerIntendedRouteMaxWps, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_3))
                        .addGroup(gl_intendedRoutePanel.createSequentialGroup()
                            .addComponent(spinnerIntendedRouteMaxTime, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_4)))
                    .addContainerGap(78, Short.MAX_VALUE))
        );
        gl_intendedRoutePanel.setVerticalGroup(
            gl_intendedRoutePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_intendedRoutePanel.createSequentialGroup()
                    .addComponent(checkBoxBroadcastIntendedRoute)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(checkBoxShowIntendedRoutesByDefault)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_intendedRoutePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerIntendedRouteMaxWps, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_3))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_intendedRoutePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerIntendedRouteMaxTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_4))
                    .addContainerGap(14, Short.MAX_VALUE))
        );
        intendedRoutePanel.setLayout(gl_intendedRoutePanel);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(transponderPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(appearancePanel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 370, Short.MAX_VALUE)
                        .addComponent(intendedRoutePanel, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(transponderPanel, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(appearancePanel, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(intendedRoutePanel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(16, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
    }
    
    public void loadSettings(EPDAisSettings aisSettings, EPDNavSettings navSettings) {
        this.aisSettings = aisSettings;
        this.navSettings = navSettings;
        checkBoxAllowSending.setSelected(aisSettings.isAllowSending());
        checkBoxStrict.setSelected(aisSettings.isStrict());
        
        checkBoxShowNameLabels.setSelected(aisSettings.isShowNameLabels());
        spinnerCogVectorLength.setValue(aisSettings.getCogVectorLength());
        spinnerShowMinuteMarksAISTarget.setValue(aisSettings.getShowMinuteMarksAISTarget());
        spinnerMinRedrawInterval.setValue(aisSettings.getMinRedrawInterval());
        
        checkBoxBroadcastIntendedRoute.setSelected(aisSettings.isBroadcastIntendedRoute());
        checkBoxShowIntendedRoutesByDefault.setSelected(aisSettings.isShowIntendedRouteByDefault());
        spinnerIntendedRouteMaxWps.setValue(aisSettings.getIntendedRouteMaxWps());
        spinnerIntendedRouteMaxTime.setValue(aisSettings.getIntendedRouteMaxTime());    
    }
    
    public void saveSettings() {
        aisSettings.setAllowSending(checkBoxAllowSending.isSelected());
        aisSettings.setStrict(checkBoxStrict.isSelected());
        
        aisSettings.setShowNameLabels(checkBoxShowNameLabels.isSelected());
        aisSettings.setCogVectorLength((Double) spinnerCogVectorLength.getValue());
        navSettings.setCogVectorLength((Double) spinnerCogVectorLength.getValue());
        
        aisSettings.setShowMinuteMarksAISTarget((Integer) spinnerShowMinuteMarksAISTarget.getValue());
        aisSettings.setMinRedrawInterval((Integer) spinnerMinRedrawInterval.getValue());
        
        aisSettings.setBroadcastIntendedRoute(checkBoxBroadcastIntendedRoute.isSelected());
        aisSettings.setShowIntendedRouteByDefault(checkBoxShowIntendedRoutesByDefault.isSelected());
        aisSettings.setIntendedRouteMaxWps((Integer) spinnerIntendedRouteMaxWps.getValue());
        aisSettings.setIntendedRouteMaxTime((Integer) spinnerIntendedRouteMaxTime.getValue());
    }
}
