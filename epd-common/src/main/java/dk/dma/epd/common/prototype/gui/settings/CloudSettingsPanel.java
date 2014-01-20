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
package dk.dma.epd.common.prototype.gui.settings;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Maritime cloud tab panel
 */
public class CloudSettingsPanel extends BaseSettingsPanel {
    private static final long serialVersionUID = 1L;
    private JTextField textFieldServerPort;
    private JTextField textFieldServerName;
    private EnavSettings enavSettings;

    public CloudSettingsPanel() {
        super("Cloud");
        
        JPanel cloudPanel = new JPanel();
        cloudPanel.setBorder(new TitledBorder(null, "HTTP Settings",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel label_3 = new JLabel("Server name:");

        JLabel label_4 = new JLabel("Server port:");

        textFieldServerPort = new JTextField();

        textFieldServerName = new JTextField();
        textFieldServerName.setColumns(10);
        GroupLayout gl_CloudPanel = new GroupLayout(cloudPanel);
        gl_CloudPanel
                .setHorizontalGroup(gl_CloudPanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                gl_CloudPanel
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                gl_CloudPanel
                                                        .createParallelGroup(
                                                                Alignment.LEADING)
                                                        .addComponent(label_3)
                                                        .addComponent(label_4))
                                        .addGap(36)
                                        .addGroup(
                                                gl_CloudPanel
                                                        .createParallelGroup(
                                                                Alignment.LEADING,
                                                                false)
                                                        .addComponent(
                                                                textFieldServerPort,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                288,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                textFieldServerName,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                288,
                                                                GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap()));
        gl_CloudPanel
                .setVerticalGroup(gl_CloudPanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                                gl_CloudPanel
                                        .createSequentialGroup()
                                        .addGroup(
                                                gl_CloudPanel
                                                        .createParallelGroup(
                                                                Alignment.BASELINE)
                                                        .addComponent(label_3)
                                                        .addComponent(
                                                                textFieldServerName,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                ComponentPlacement.RELATED)
                                        .addGroup(
                                                gl_CloudPanel
                                                        .createParallelGroup(
                                                                Alignment.BASELINE)
                                                        .addComponent(
                                                                textFieldServerPort,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(label_4))
                                        .addContainerGap(34, Short.MAX_VALUE)));
        cloudPanel.setLayout(gl_CloudPanel);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
                Alignment.LEADING).addGroup(
                groupLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cloudPanel, GroupLayout.PREFERRED_SIZE,
                                434, Short.MAX_VALUE).addGap(6)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
                Alignment.LEADING).addGroup(
                groupLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cloudPanel, GroupLayout.PREFERRED_SIZE,
                                72, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(217, Short.MAX_VALUE)));
        setLayout(groupLayout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Cloud Settings";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings() {
        this.enavSettings = EPD.getInstance().getSettings().getEnavSettings();
        textFieldServerName.setText(enavSettings.getCloudServerHost());
        textFieldServerPort.setText(Integer.toString(enavSettings
                .getCloudServerPort()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveSettings() {
        enavSettings.setCloudServerHost(textFieldServerName.getText());
        enavSettings.setCloudServerPort(getIntVal(
                textFieldServerPort.getText(), enavSettings.getHttpPort()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasChanged() {
        return 
                changed(enavSettings.getCloudServerHost(), textFieldServerName.getText()) ||
                changed(enavSettings.getCloudServerPort(), textFieldServerPort.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        fireSettingsChanged(Type.CLOUD);
    }
    
    
    private static int getIntVal(String fieldVal, int defaultValue) {
        Integer val;
        try {
            val = ParseUtils.parseInt(fieldVal);
        } catch (FormatException e) {
            val = null;
        }
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }
}
