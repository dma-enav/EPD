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

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.EPDNavSettings;

/**
 * Navigation tab panel in setup panel
 */
public class NavigationTab extends BaseSettingsPanel {
    private static final long serialVersionUID = 1L;
    private JCheckBox checkBoxLookAhead;
    private JSpinner spinnerAutoFollowPctOffTolerance;
    private JSpinner spinnerShowMinuteMarksSelf;
    private JSpinner spinnerShowArrowScale;
    private JSpinner spinnerDefaultSpeed;
    private JSpinner spinnerDefaultTurnRad;
    private JSpinner spinnerDefaultXtd;
    private EPDNavSettings navSettings;

    public NavigationTab() {
        super("Navigation");
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Own Ship", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        spinnerAutoFollowPctOffTolerance = new JSpinner();
        
        JLabel label_1 = new JLabel("Auto follow tolerance (%)");
        
        checkBoxLookAhead = new JCheckBox("Look ahead");
        
        spinnerShowMinuteMarksSelf = new JSpinner();
        
        JLabel label_2 = new JLabel("Scale to show minute marks (screen distance in pixels)");
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(spinnerAutoFollowPctOffTolerance, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_1))
                        .addComponent(checkBoxLookAhead)
                        .addGroup(gl_panel.createSequentialGroup()
                            .addComponent(spinnerShowMinuteMarksSelf, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_2)))
                    .addContainerGap(75, Short.MAX_VALUE))
        );
        gl_panel.setVerticalGroup(
            gl_panel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel.createSequentialGroup()
                    .addComponent(checkBoxLookAhead)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerAutoFollowPctOffTolerance, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_1))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_2)
                        .addComponent(spinnerShowMinuteMarksSelf, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(32, Short.MAX_VALUE))
        );
        panel.setLayout(gl_panel);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Route Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        spinnerShowArrowScale = new JSpinner(new SpinnerNumberModel(new Float(0), null, null, new Float(1)));
        
        JLabel label_3 = new JLabel("Scale to show route arrows (map scale)");
        
        spinnerDefaultSpeed = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        JLabel label_4 = new JLabel("New route default speed");
        
        spinnerDefaultTurnRad = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        JLabel label_5 = new JLabel("New route default turn radius");
        
        spinnerDefaultXtd = new JSpinner(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
        
        JLabel label_6 = new JLabel("New route default xtd");
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGap(0, 429, Short.MAX_VALUE)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(spinnerShowArrowScale, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addGap(4)
                            .addComponent(label_3))
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(spinnerDefaultSpeed, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_4))
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(spinnerDefaultTurnRad, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_5))
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(spinnerDefaultXtd, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(label_6)))
                    .addContainerGap(144, Short.MAX_VALUE))
        );
        gl_panel_1.setVerticalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGap(0, 139, Short.MAX_VALUE)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addComponent(spinnerShowArrowScale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addGap(3)
                            .addComponent(label_3)))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerDefaultSpeed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_4))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerDefaultTurnRad, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_5))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(spinnerDefaultXtd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_6))
                    .addContainerGap(23, Short.MAX_VALUE))
        );
        panel_1.setLayout(gl_panel_1);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                        .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(135, Short.MAX_VALUE))
        );
        setLayout(groupLayout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings() {
        this.navSettings = EPDShip.getInstance().getSettings().getNavSettings();
        checkBoxLookAhead.setSelected(navSettings.isLookAhead());
        spinnerAutoFollowPctOffTolerance.setValue(navSettings.getAutoFollowPctOffTollerance());
        spinnerShowMinuteMarksSelf.setValue(navSettings.getShowMinuteMarksSelf());
        
        spinnerShowArrowScale.setValue(navSettings.getShowArrowScale());
        spinnerDefaultSpeed.setValue(navSettings.getDefaultSpeed());
        spinnerDefaultTurnRad.setValue(navSettings.getDefaultTurnRad());
        spinnerDefaultXtd.setValue(navSettings.getDefaultXtd());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveSettings() {
        navSettings.setLookAhead(checkBoxLookAhead.isSelected());
        navSettings.setAutoFollowPctOffTollerance((Integer) spinnerAutoFollowPctOffTolerance.getValue());
        navSettings.setShowMinuteMarksSelf((Integer) spinnerShowMinuteMarksSelf.getValue());
        
        navSettings.setShowArrowScale((Float) spinnerShowArrowScale.getValue());
        navSettings.setDefaultSpeed((Double) spinnerDefaultSpeed.getValue());
        navSettings.setDefaultTurnRad((Double) spinnerDefaultTurnRad.getValue());
        navSettings.setDefaultXtd((Double) spinnerDefaultXtd.getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasChanged() {
        return 
                changed(navSettings.isLookAhead(), checkBoxLookAhead.isSelected()) ||
                changed(navSettings.getAutoFollowPctOffTollerance(), spinnerAutoFollowPctOffTolerance.getValue()) ||
                changed(navSettings.getShowMinuteMarksSelf(), spinnerShowMinuteMarksSelf.getValue()) ||
                
                changed(navSettings.getShowArrowScale(), spinnerShowArrowScale.getValue()) ||
                changed(navSettings.getDefaultSpeed(), spinnerDefaultSpeed.getValue()) ||
                changed(navSettings.getDefaultTurnRad(), spinnerDefaultTurnRad.getValue()) ||
                changed(navSettings.getDefaultXtd(), spinnerDefaultXtd.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.NAVIGATION);
    }
}
