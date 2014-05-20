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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import dk.dma.epd.ship.EPDShip;

public class AdvancedSettingsWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Create the frame.
     */
    public AdvancedSettingsWindow() {
        super(EPDShip.getInstance().getMainFrame(), "Advanced Settings", true);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setBounds(100, 100, 500, 750);

        this.setLocationRelativeTo(EPDShip.getInstance().getMainFrame());

        JTabbedPane gui = (JTabbedPane) EPDShip.getInstance().getMainFrame().getChartPanel()
                .getEncLayer().getGUI();
        gui.setVisible(true);
        //

        if (gui.getTabCount() > 1) {
            gui.removeTabAt(1);
            gui.removeTabAt(1);
        }

        // gui.getTabComponentAt(1).remove();

        // gui.removeTabAt(2);

        // gui.getComponent(1).setVisible(false);
        // gui.getComponent(2).setVisible(false);

        getContentPane().add(gui);

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

        this.setVisible(true);

    }

    private void closeAndSave() {
        Class<?> c;
        try {
            c = Class.forName("dk.navicon.s52.presentation.S52ViewingGroup");
            Method m = c.getMethod("viewGrpSettingsAsString");
            String result = (String) m.invoke(null);

            EPDShip.getInstance().getSettings().getS57LayerSettings().setS52mapSettings(result);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.dispose();
    }

}
