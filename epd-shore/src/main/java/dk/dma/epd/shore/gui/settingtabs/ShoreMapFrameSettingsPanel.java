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
package dk.dma.epd.shore.gui.settingtabs;

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

/**
 * 
 * @author adamduehansen
 *
 */
public class ShoreMapFrameSettingsPanel extends BaseSettingsPanel {
    
    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private JTextField textFieldMapName;
    private JCheckBox chckbxLocked;
    private JCheckBox chckbxAlwaysOnTop;
    private JMapFrame mapWindow;
    private MainFrame mainFrame;

    public ShoreMapFrameSettingsPanel(JMapFrame mapWindow) {
        super(mapWindow.getTitle(), new ImageIcon(
                ShoreMapFrameSettingsPanel.class.getResource("/images/settings/map.png")));
        this.setLayout(null);
        
        // 
        this.mapWindow = mapWindow;
        this.mainFrame = EPDShore.getInstance().getMainFrame();        
        
        
        /************** Frame settings panel ***************/

        JPanel framePanel = new JPanel();
        framePanel.setBounds(6, 6, 438, 110);
        framePanel.setLayout(null);
        framePanel.setBorder(new TitledBorder(
                null, "Map Frame Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        JLabel lblFrameName = new JLabel("Frame name");
        lblFrameName.setBounds(17, 20, 76, 16);
        framePanel.add(lblFrameName);
        
        this.textFieldMapName = new JTextField();
        this.textFieldMapName.setBounds(105, 18, 316, 20);
        framePanel.add(this.textFieldMapName);
        this.textFieldMapName.setColumns(10);
        
        this.chckbxLocked = new JCheckBox("Locked");
        this.chckbxLocked.setBounds(16, 45, 128, 20);
        framePanel.add(this.chckbxLocked);
        
        this.chckbxAlwaysOnTop = new JCheckBox("Always on top");
        this.chckbxAlwaysOnTop.setBounds(16, 70, 121, 20);
        framePanel.add(this.chckbxAlwaysOnTop);
        
        this.add(framePanel);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return 
                // Check for changes made in the frame.
                changed(this.mapWindow.getTitle(), this.textFieldMapName.getText()) ||
                changed(this.mapWindow.isLocked(), this.chckbxLocked.isSelected()) ||
                changed(this.mapWindow.isInFront(), this.chckbxAlwaysOnTop.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        
        // Load the settings for the map frame.
        this.textFieldMapName.setText(this.mapWindow.getTitle());
        this.chckbxLocked.setSelected(this.mapWindow.isLocked());
        this.chckbxAlwaysOnTop.setSelected(this.mapWindow.isInFront());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        
        this.mapWindow.setTitle(this.textFieldMapName.getText());
        this.mainFrame.renameMapWindow(this.mapWindow);
        
        if (this.chckbxLocked.isSelected() != this.mapWindow.isLocked()) {
            this.mapWindow.lockUnlockWindow();
            this.mainFrame.lockMapWindow(this.mapWindow, this.chckbxLocked.isSelected());
        }
        
        if (this.chckbxAlwaysOnTop.isSelected() != this.mapWindow.isInFront()) {
            this.mapWindow.alwaysFront();
            this.mainFrame.onTopMapWindow(this.mapWindow, this.chckbxAlwaysOnTop.isSelected());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {/*...*/}
}
