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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;

public class MapWindowsPanel extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    
    private MainFrame mainFrame;
    private Map<JMapFrame, MapWindowSinglePanel> panels = new ConcurrentHashMap<>();
    private JTabbedPane mapWindowTabs = new JTabbedPane();

    public MapWindowsPanel() {
        super("Map Windows", EPDShore.res().getCachedImageIcon("images/settings/window.png"));

        setBackground(GuiStyler.backgroundColor);
        setBounds(10, 11, 493, 500);
        setLayout(null);
        
        GraphicsUtil.fixSize(mapWindowTabs, 480, 240);
        add(mapWindowTabs);
    }

    /**
     * Updates the title of the tab for the given panel
     * @param panel the panel to update the tab title for
     */
    private void updateTabTitle(MapWindowSinglePanel panel) {
        mapWindowTabs.setTitleAt(panel.getIndex(), panel.getMapTitleField().getText());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        
        mainFrame = EPDShore.getInstance().getMainFrame();

        // Clear old map windows
        mapWindowTabs.removeAll();
        panels.clear();
        
        // Add a tab for each map window
        int index = 0;
        for (JMapFrame mapWindow : mainFrame.getMapWindows()) {
            final MapWindowSinglePanel panel = new MapWindowSinglePanel(mapWindow, index++);
            mapWindowTabs.add(panel);
            panels.put(mapWindow, panel);
            panel.loadSettings();
            panel.getMapTitleField().getDocument().addDocumentListener(new DocumentListener() {                
                @Override public void removeUpdate(DocumentEvent e) { updateTabTitle(panel); }                
                @Override public void insertUpdate(DocumentEvent e) { updateTabTitle(panel); } 
                @Override public void changedUpdate(DocumentEvent e) { updateTabTitle(panel); } 
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() { 
        for (MapWindowSinglePanel panel : panels.values()) {
            panel.saveSettings();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        for (MapWindowSinglePanel panel : panels.values()) {
            if (panel.settingsChanged()) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
    }

}
