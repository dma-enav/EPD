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
import dk.dma.epd.ship.settings.EPDAisSettings;

/**
 * AIS tab panel in setup panel
 */
public class AisTab extends BaseSettingsPanel {
    private static final long serialVersionUID = 1L;
    private JCheckBox checkBoxAllowSending;
    private JCheckBox checkBoxStrict;
    private JCheckBox checkBoxShowNameLabels;
    private JSpinner spinnerMinRedrawInterval;
    private JCheckBox checkBoxShowIntendedRoutesByDefault;
    private JCheckBox checkBoxBroadcastIntendedRoute;
    private JSpinner spinnerIntendedRouteMaxWps;
    private JSpinner spinnerIntendedRouteMaxTime;
    private EPDAisSettings aisSettings;
    private JSpinner spinnerCogVectorLengthMax;
    private JSpinner spinnerCogVectorLengthMin;
    private JSpinner spinnerCogVectorLengthScaleStepSize;
    private JSpinner spinnerCogVectorHideBelow;

    public AisTab() {
        super("AIS");

        JPanel appearancePanel = new JPanel();
        appearancePanel.setBorder(new TitledBorder(null, "Appearance", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        checkBoxShowNameLabels = new JCheckBox("Show ship labels");

        JLabel lblCogVectorLengthMin = new JLabel("Own ship and targets COG vector length minimum (minutes)");

        spinnerMinRedrawInterval = new JSpinner();

        JLabel lblAisRedraw = new JLabel("AIS redraw interval (sec)");

        spinnerCogVectorLengthMin = new JSpinner();
        spinnerCogVectorLengthMin.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));

        spinnerCogVectorLengthMax = new JSpinner();
        spinnerCogVectorLengthMax.setModel(new SpinnerNumberModel(new Integer(6), new Integer(1), null, new Integer(1)));

        JLabel lblCogVectorLengthMax = new JLabel("Own ship and targets COG vector length maximum (minutes)");

        spinnerCogVectorLengthScaleStepSize = new JSpinner();
        spinnerCogVectorLengthScaleStepSize
                .setModel(new SpinnerNumberModel(new Float(5000), new Float(2000), null, new Float(1000)));

        JLabel lblScaleStepSize = new JLabel("Scale step size for each increment of the COG vector length");

        spinnerCogVectorHideBelow = new JSpinner(new SpinnerNumberModel(new Float(0.1), new Float(0.1), new Float(100), new Float(
                0.1)));

        JLabel lblHideCogVector = new JLabel("Hide COG vector when below (kn)");
        GroupLayout gl_appearancePanel = new GroupLayout(appearancePanel);
        gl_appearancePanel
                .setHorizontalGroup(gl_appearancePanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                gl_appearancePanel
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                gl_appearancePanel
                                                        .createParallelGroup(Alignment.LEADING)
                                                        .addComponent(checkBoxShowNameLabels)
                                                        .addGroup(
                                                                gl_appearancePanel
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                gl_appearancePanel
                                                                                        .createParallelGroup(Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                gl_appearancePanel
                                                                                                        .createParallelGroup(
                                                                                                                Alignment.TRAILING,
                                                                                                                false)
                                                                                                        .addComponent(
                                                                                                                spinnerCogVectorLengthMin,
                                                                                                                Alignment.LEADING,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                89,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(
                                                                                                                spinnerCogVectorLengthMax,
                                                                                                                Alignment.LEADING,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                89,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(
                                                                                                                spinnerCogVectorHideBelow,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                89,
                                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                                        .addComponent(
                                                                                                                spinnerCogVectorLengthScaleStepSize,
                                                                                                                Alignment.LEADING,
                                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                                89,
                                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                                        .addComponent(spinnerMinRedrawInterval,
                                                                                                GroupLayout.PREFERRED_SIZE, 89,
                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                        .addPreferredGap(ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                gl_appearancePanel
                                                                                        .createParallelGroup(Alignment.LEADING)
                                                                                        .addComponent(lblAisRedraw)
                                                                                        .addComponent(lblCogVectorLengthMin)
                                                                                        .addComponent(lblCogVectorLengthMax)
                                                                                        .addComponent(lblScaleStepSize)
                                                                                        .addComponent(lblHideCogVector))))
                                        .addGap(225)));
        gl_appearancePanel.setVerticalGroup(gl_appearancePanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_appearancePanel
                        .createSequentialGroup()
                        .addComponent(checkBoxShowNameLabels)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_appearancePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerCogVectorLengthMin, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblCogVectorLengthMin))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_appearancePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerCogVectorLengthMax, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblCogVectorLengthMax))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_appearancePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerCogVectorLengthScaleStepSize, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblScaleStepSize))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_appearancePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerCogVectorHideBelow, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblHideCogVector))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_appearancePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerMinRedrawInterval, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblAisRedraw))
                        .addContainerGap(15, Short.MAX_VALUE)));
        appearancePanel.setLayout(gl_appearancePanel);

        JPanel transponderPanel = new JPanel();
        transponderPanel.setBorder(new TitledBorder(null, "Transponder Settings", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));

        checkBoxAllowSending = new JCheckBox("Allow sending");

        checkBoxStrict = new JCheckBox("Strict timeout rules");
        GroupLayout gl_transponderPanel = new GroupLayout(transponderPanel);
        gl_transponderPanel.setHorizontalGroup(gl_transponderPanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_transponderPanel
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_transponderPanel.createParallelGroup(Alignment.LEADING).addComponent(checkBoxAllowSending)
                                        .addComponent(checkBoxStrict)).addContainerGap(405, Short.MAX_VALUE)));
        gl_transponderPanel.setVerticalGroup(gl_transponderPanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_transponderPanel.createSequentialGroup().addComponent(checkBoxAllowSending)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(checkBoxStrict)
                        .addContainerGap(38, Short.MAX_VALUE)));
        transponderPanel.setLayout(gl_transponderPanel);

        JPanel intendedRoutePanel = new JPanel();
        intendedRoutePanel.setBorder(new TitledBorder(null, "AIS Intended Route", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));

        checkBoxShowIntendedRoutesByDefault = new JCheckBox("Show intended routes by default");

        checkBoxBroadcastIntendedRoute = new JCheckBox("Broadcast intended route");

        spinnerIntendedRouteMaxWps = new JSpinner();

        JLabel label_3 = new JLabel("Maximum waypoints in an intended route");

        spinnerIntendedRouteMaxTime = new JSpinner();

        JLabel label_4 = new JLabel("Maximum duration of intended route (min)");
        GroupLayout gl_intendedRoutePanel = new GroupLayout(intendedRoutePanel);
        gl_intendedRoutePanel.setHorizontalGroup(gl_intendedRoutePanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_intendedRoutePanel
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_intendedRoutePanel
                                        .createParallelGroup(Alignment.LEADING)
                                        .addComponent(checkBoxShowIntendedRoutesByDefault)
                                        .addComponent(checkBoxBroadcastIntendedRoute)
                                        .addGroup(
                                                gl_intendedRoutePanel
                                                        .createSequentialGroup()
                                                        .addComponent(spinnerIntendedRouteMaxWps, GroupLayout.PREFERRED_SIZE, 70,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(label_3))
                                        .addGroup(
                                                gl_intendedRoutePanel
                                                        .createSequentialGroup()
                                                        .addComponent(spinnerIntendedRouteMaxTime, GroupLayout.PREFERRED_SIZE, 70,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(label_4)))
                        .addContainerGap(78, Short.MAX_VALUE)));
        gl_intendedRoutePanel.setVerticalGroup(gl_intendedRoutePanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_intendedRoutePanel
                        .createSequentialGroup()
                        .addComponent(checkBoxBroadcastIntendedRoute)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(checkBoxShowIntendedRoutesByDefault)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_intendedRoutePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerIntendedRouteMaxWps, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(label_3))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl_intendedRoutePanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(spinnerIntendedRouteMaxTime, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(label_4))
                        .addContainerGap(14, Short.MAX_VALUE)));
        intendedRoutePanel.setLayout(gl_intendedRoutePanel);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(
                groupLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(appearancePanel, GroupLayout.PREFERRED_SIZE, 721, Short.MAX_VALUE)
                                        .addComponent(transponderPanel, GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                                        .addComponent(intendedRoutePanel, GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE))
                        .addContainerGap()));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
                groupLayout.createSequentialGroup().addContainerGap()
                        .addComponent(transponderPanel, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(appearancePanel, GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE).addGap(18)
                        .addComponent(intendedRoutePanel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(199, Short.MAX_VALUE)));
        setLayout(groupLayout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings() {
        this.aisSettings = EPDShip.getInstance().getSettings().getAisSettings();
        checkBoxAllowSending.setSelected(aisSettings.isAllowSending());
        checkBoxStrict.setSelected(aisSettings.isStrict());

        checkBoxShowNameLabels.setSelected(aisSettings.isShowNameLabels());
        spinnerCogVectorLengthMin.setValue(aisSettings.getCogVectorLengthMin());
        spinnerCogVectorLengthMax.setValue(aisSettings.getCogVectorLengthMax());
        spinnerCogVectorLengthScaleStepSize.setValue(aisSettings.getCogVectorLengthScaleInterval());
        spinnerCogVectorHideBelow.setValue(aisSettings.getCogVectorHideBelow());
        spinnerMinRedrawInterval.setValue(aisSettings.getMinRedrawInterval());

        checkBoxBroadcastIntendedRoute.setSelected(aisSettings.isBroadcastIntendedRoute());
        checkBoxShowIntendedRoutesByDefault.setSelected(aisSettings.isShowIntendedRouteByDefault());
        spinnerIntendedRouteMaxWps.setValue(aisSettings.getIntendedRouteMaxWps());
        spinnerIntendedRouteMaxTime.setValue(aisSettings.getIntendedRouteMaxTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveSettings() {
        aisSettings.setAllowSending(checkBoxAllowSending.isSelected());
        aisSettings.setStrict(checkBoxStrict.isSelected());

        aisSettings.setShowNameLabels(checkBoxShowNameLabels.isSelected());
        aisSettings.setCogVectorLengthMin((Integer) spinnerCogVectorLengthMin.getValue());
        aisSettings.setCogVectorLengthMax((Integer) spinnerCogVectorLengthMax.getValue());
        aisSettings.setCogVectorLengthScaleInterval((Float) spinnerCogVectorLengthScaleStepSize.getValue());
        aisSettings.setCogVectorHideBelow((Float) spinnerCogVectorHideBelow.getValue());
        aisSettings.setMinRedrawInterval((Integer) spinnerMinRedrawInterval.getValue());

        aisSettings.setBroadcastIntendedRoute(checkBoxBroadcastIntendedRoute.isSelected());
        aisSettings.setShowIntendedRouteByDefault(checkBoxShowIntendedRoutesByDefault.isSelected());
        aisSettings.setIntendedRouteMaxWps((Integer) spinnerIntendedRouteMaxWps.getValue());
        aisSettings.setIntendedRouteMaxTime((Integer) spinnerIntendedRouteMaxTime.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasChanged() {
        return 
                changed(aisSettings.isAllowSending(), checkBoxAllowSending.isSelected()) ||
                changed(aisSettings.isStrict(), checkBoxStrict.isSelected()) ||
                
                changed(aisSettings.isShowNameLabels(), checkBoxShowNameLabels.isSelected()) ||
                changed(aisSettings.getCogVectorLengthMin(), spinnerCogVectorLengthMin.getValue()) ||
                changed(aisSettings.getCogVectorLengthMax(), spinnerCogVectorLengthMax.getValue()) ||
                changed(aisSettings.getCogVectorLengthScaleInterval(), spinnerCogVectorLengthScaleStepSize.getValue()) ||
                changed(aisSettings.getCogVectorHideBelow(), spinnerCogVectorHideBelow.getValue()) ||
                changed(aisSettings.getMinRedrawInterval(), spinnerMinRedrawInterval.getValue()) ||
                
                changed(aisSettings.isBroadcastIntendedRoute(), checkBoxBroadcastIntendedRoute.isSelected()) ||
                changed(aisSettings.isShowIntendedRouteByDefault(), checkBoxShowIntendedRoutesByDefault.isSelected()) ||
                changed(aisSettings.getIntendedRouteMaxWps(), spinnerIntendedRouteMaxWps.getValue()) ||
                changed(aisSettings.getIntendedRouteMaxTime(), spinnerIntendedRouteMaxTime.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.AIS);
    }
}
