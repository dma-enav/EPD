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
package dk.dma.epd.common.prototype.gui.settings;

import java.awt.BorderLayout;
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
    private CommonMapSettingsPanel parent;
    
    
    public AdvancedSettingsWindow(CommonMapSettingsPanel parent) {
        
        super(EPD.getInstance().getMainFrame(), "Advanced Settings", true);
        this.setBounds(100, 100, 500, 750);
        this.setLocationRelativeTo(EPD.getInstance().getMainFrame());
        this.parent = parent;
        
        try {
            
            /* Add a panel at the bottom the window with an "ok" which
             * close and saves the panel.
             */
            JPanel panel = new JPanel();
            getContentPane().add(panel, BorderLayout.SOUTH);
            panel.setLayout(new BorderLayout(0, 0));
            
            JButton okBtn = new JButton("Ok");
            okBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    //
                    closeAndSave();
                    
                }
            });
            panel.add(okBtn, BorderLayout.EAST);

            // Try to get the enc layer.
            JTabbedPane gui = (JTabbedPane) 
                    EPD.getInstance().getMainFrame().getActiveChartPanel().getEncLayer().getGUI();
            gui.setVisible(true);
            
            // Remove unused tabs.
            if (gui.getTabCount() > 1) {
                gui.removeTabAt(1);
                gui.removeTabAt(1);
            }
            
            // Add gui to the panel.
            this.getContentPane().add(gui);
            
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

            

//            System.out.println(EPD.getInstance().getSettings().getS57Settings());
            EPD.getInstance().getSettings().getS57Settings().setS52mapSettings(result);
            parent.s57MapSettingsChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.dispose();
    }
}
