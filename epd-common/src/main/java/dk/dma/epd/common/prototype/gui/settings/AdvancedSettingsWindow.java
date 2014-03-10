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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import dk.dma.epd.common.prototype.EPD;

public class AdvancedSettingsWindow extends JDialog {
    
    private static final long serialVersionUID = 1L;

    public AdvancedSettingsWindow() {
        
        super(EPD.getInstance().getMainFrame(), "Advanced Settings", true);
        this.setSize(new Dimension(500, 750));
        this.setLocationRelativeTo(EPD.getInstance().getMainFrame());
        
        try {
            
            // Try to get the enc layer.
            JTabbedPane gui = (JTabbedPane) 
                    EPD.getInstance().getMainFrame().getActiveChartPanel().getEncLayer().getGUI();
            gui.setVisible(true);
            
            if (gui.getTabCount() > 1) {
                gui.removeTabAt(1);
                gui.removeTabAt(1);
            }
            
            // Add gui to the panel.
            this.getContentPane().add(gui);
            
            /* Add a panel at the bottom the window with an "ok" which
             * close and saves the panel.
             */
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BorderLayout());
            this.getContentPane().add(bottomPanel);
            
            JButton btnOk = new JButton("Ok");
            btnOk.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    // Close and save the window.
                    closeAndSave();
                }
            });
            bottomPanel.add(btnOk, BorderLayout.EAST);
            
            this.setVisible(true);
            
        } catch (NullPointerException e) {
            System.out.println("No enc layer found.");
        }
        
        this.setVisible(true);
    }
    
    /**
     * 
     */
    public void closeAndSave() {
        Class<?> c;
        try {
            c = Class.forName("dk.navicon.s52.presentation.S52ViewingGroup");
            Method m = c.getMethod("viewGrpSettingsAsString");
            String result = (String) m.invoke(null);

            EPD.getInstance().getSettings().getS57Settings().setS52mapSettings(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.dispose();
    }
}
