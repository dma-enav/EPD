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
