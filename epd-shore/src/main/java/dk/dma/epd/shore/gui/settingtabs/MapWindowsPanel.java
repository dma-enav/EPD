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

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.shore.EPDShore;

public class MapWindowsPanel extends BaseShoreSettingsPanel {

    private static final long serialVersionUID = 1L;
    
    private JLabel lblTheresCurrently;
    private JLabel lblTheCurrentWorkspace;

    public MapWindowsPanel() {
        super("Map Windows", "window.png");


        setBackground(GuiStyler.backgroundColor);
        setBounds(10, 11, 493, 600);
        setLayout(null);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(GuiStyler.backgroundColor);
        panel_1.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Map Windows", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        panel_1.setBounds(10, 11, 473, 283);

        add(panel_1);
        panel_1.setLayout(null);

        lblTheresCurrently = new JLabel("There's currently x active Map Windows");
        GuiStyler.styleText(lblTheresCurrently);
        lblTheresCurrently.setBounds(10, 33, 352, 14);
        panel_1.add(lblTheresCurrently);

        lblTheCurrentWorkspace = new JLabel("The current workspace is: name");
        GuiStyler.styleText(lblTheCurrentWorkspace);
        lblTheCurrentWorkspace.setBounds(10, 54, 317, 14);
        panel_1.add(lblTheCurrentWorkspace);

        JLabel lblClickOnThe = new JLabel("Click on the Map Tabs to change settings for the individual map");
        GuiStyler.styleText(lblClickOnThe);
        lblClickOnThe.setBounds(10, 133, 387, 87);
        panel_1.add(lblClickOnThe);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings(){
        super.loadSettings();
        
        lblTheresCurrently.setText("There's currently " + 
                EPDShore.getInstance().getMainFrame().getMapWindows().size() + 
                " active Map Windows");
        lblTheCurrentWorkspace.setText("The current workspace is " + 
                EPDShore.getInstance().getSettings().getGuiSettings().getWorkspace());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveSettings() {        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wasChanged() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
    }

}
