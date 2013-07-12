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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import dk.dma.epd.ship.EPDShip;
import dk.navicon.s52.presentation.S52ViewingGroup;
import dk.navicon.s52.pure.presentation.S52Layer;

public class AdvancedSettingsWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Create the frame.
     */
    public AdvancedSettingsWindow() {
        super(EPDShip.getMainFrame(), "Advanced Settings", true);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setBounds(100, 100, 500, 750);

        this.setLocationRelativeTo(EPDShip.getMainFrame());

        JTabbedPane gui = (JTabbedPane) EPDShip.getMainFrame().getChartPanel()
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

//            Properties newProps = new Properties();
//
//            newProps.put("enc.viewGroupSettings", result);
//
//
//            File propFile = new File(EPDShip.getHomePath().resolve("settings.properties").toString());
//            Properties props = new Properties();
//            if (propFile.exists()) {
//                try {
//                    FileInputStream fis = new FileInputStream(propFile);
//                    props.load(fis);
//                    fis.close();
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//            }
//
//            props.putAll(newProps);
//            try {
//                System.out.println("STORING IT");
//                FileOutputStream fis = new FileOutputStream(propFile);
//                props.store(fis, "Created "
//                        + new Date());
//                fis.close();
//            } catch (Exception e) {
//                System.out.println(e);
//            }

            // System.out.println("Setting: " + sb.toString());
            // s52Layer.setProperties(props);

            // System.out.println("Original result:");

            // fromString
            // result
            // S52Layer.fromString(S52ViewingGroup.viewGrpSettingsAsString());

            // System.out.println(S52ViewingGroup.viewGrpSettingsAsString());

            // result = result.replace("\n", "foo").replace("\r",
            // "bar").replace("\=", "fub");

            // result = result.replace("\r", "\r");
            // System.out.println("Received result");
            // System.out.println(result);
            // result = result.replace("\\", "\\\\");
            // \

             EPDShip.getSettings().getMapSettings().setS52mapSettings(result);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.dispose();
    }

}
