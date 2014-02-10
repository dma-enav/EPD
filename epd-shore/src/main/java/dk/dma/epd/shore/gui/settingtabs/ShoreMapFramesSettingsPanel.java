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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;

import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author adamduehansen
 * 
 */
public class ShoreMapFramesSettingsPanel extends BaseSettingsPanel {

    /**
     * Private fields
     */
    private static final long serialVersionUID = 1L;
    private JTabbedPane mapWindowsTabbedPane;
    private Map<JMapFrame, ShoreMapFrameSettingsPanel> mapFrames;
    private ShoreMapFrameSettingsPanel mapFrameSettings;
    private List<JMapFrame> mapFramesUI;

    public ShoreMapFramesSettingsPanel() {
        super("Map Windows", new ImageIcon(ShoreMapFramesSettingsPanel.class.getResource("/images/settings/maps.png")));

        this.setLayout(null);

        this.mapFrames = new HashMap<JMapFrame, ShoreMapFrameSettingsPanel>();

        this.mapWindowsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.mapWindowsTabbedPane.setBounds(6, 6, 608, 288);
        this.add(this.mapWindowsTabbedPane);
    }

    /**
     * Shows the settings for the map frame given.
     * 
     * @param activeMapWindow
     *            The map to show settings for.
     */
    public void showSettingsFor(JMapFrame activeMapWindow) {

        // Go through the list of map frame until the map at index
        // is the same as the given.
        for (int i = 0; i < mapFramesUI.size(); i++) {

            if (mapFramesUI.get(i).equals(activeMapWindow)) {

                mapWindowsTabbedPane.setSelectedIndex(i);
            }
        }
    }
    
    /**
     * Resizes the panels to fit the container.
     */
    public void resizePanelsToFitContainer() {
        
        // For each map frame settings panel get the components.
        for (ShoreMapFrameSettingsPanel mapFrame : this.mapFrames.values()) {

            // For each component check if it is an instance of JPanel.
            for (Component component : mapFrame.getComponents()) {
                
                if (component instanceof JPanel) {
                    
                    // If it yes -> resize it.
                    component.setSize(this.mapWindowsTabbedPane.getWidth()-20, component.getHeight());
                }
            }            
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {

        // Check for changes in all map frame settings.
        for (ShoreMapFrameSettingsPanel mapFrame : this.mapFrames.values()) {
            if (mapFrame.checkSettingsChanged()) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {

        // Clear old maps.
        mapWindowsTabbedPane.removeAll();
        mapFrames.clear();

        // Get map frames.
        MainFrame mainFrame = EPDShore.getInstance().getMainFrame();
        mapFramesUI = mainFrame.getMapWindows();

        // For every map frame, add settings for it.
        for (int i = 0; i < mapFramesUI.size(); i++) {

            // Create and load the settings for the specific JMapFrame.
            this.mapFrameSettings = new ShoreMapFrameSettingsPanel(mapFramesUI.get(i));
            this.mapFrameSettings.loadSettings();

            // Add the panel.
            this.mapWindowsTabbedPane.add(this.mapFrameSettings);
            this.mapFrames.put(mapFramesUI.get(i), this.mapFrameSettings);

            // Create the pane title for the tab.
            JLabel paneTitle = new JLabel(mapFrameSettings.getName());
            paneTitle.setFont(new Font(paneTitle.getFont().getName(), Font.PLAIN, 13));
            paneTitle.setIcon(this.mapFrameSettings.getIcon());
            paneTitle.setIconTextGap(5);
            paneTitle.setHorizontalTextPosition(SwingConstants.RIGHT);
            this.mapWindowsTabbedPane.setTabComponentAt(i, paneTitle);
        }
        
        // Resize map frame settings to fit tabbed pane.
        this.resizePanelsToFitContainer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {

        for (ShoreMapFrameSettingsPanel mapFrame : this.mapFrames.values()) {
            mapFrame.saveSettings();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {/* ... */
    }
}
