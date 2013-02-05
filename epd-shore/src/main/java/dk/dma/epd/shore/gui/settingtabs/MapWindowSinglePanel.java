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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;

public class MapWindowSinglePanel extends JPanel{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private MainFrame mainFrame;
    private JTextField textField;
    private JCheckBox chckbxLocked;
    private JCheckBox chckbxAlwaysOnTop;
    private int id;

    public MapWindowSinglePanel(MainFrame mainFrame, int id){
        super();

        this.mainFrame = mainFrame;
        this.id = id;


        setBackground(GuiStyler.backgroundColor);
        setBounds(10, 11, 493, 600);
        setLayout(null);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(GuiStyler.backgroundColor);
        panel_1.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Map Window Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        panel_1.setBounds(10, 11, 473, 122);

        add(panel_1);
        panel_1.setLayout(null);

        JLabel windowName = new JLabel("Window Name:");
        windowName.setBounds(10, 33, 91, 14);
        GuiStyler.styleText(windowName);

        panel_1.add(windowName);

        chckbxLocked = new JCheckBox("Locked");
        chckbxLocked.setBounds(4, 54, 97, 23);
        GuiStyler.styleCheckbox(chckbxLocked);
        panel_1.add(chckbxLocked);

        chckbxAlwaysOnTop = new JCheckBox("Always on top");
        chckbxAlwaysOnTop.setBounds(4, 80, 97, 23);
        GuiStyler.styleCheckbox(chckbxAlwaysOnTop);
        panel_1.add(chckbxAlwaysOnTop);

        textField = new JTextField();
        textField.setBounds(90, 30, 127, 20);
        panel_1.add(textField);
        textField.setColumns(10);
        GuiStyler.styleTextFields(textField);

        JPanel panel_2 = new JPanel();
        panel_2.setBackground(GuiStyler.backgroundColor);
        panel_2.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "AIS Layer Settings", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        panel_2.setBounds(10, 144, 473, 190);

        add(panel_2);
        panel_2.setLayout(null);
    }

    public void loadSettings(){
        JMapFrame mapWindow = mainFrame.getMapWindows().get(id);

        textField.setText(mapWindow.getTitle());

//        System.out.println(textField.getText() + " locked: " + mapWindow.isLocked());

        chckbxLocked.setSelected(mapWindow.isLocked());
        chckbxAlwaysOnTop.setSelected(mapWindow.isInFront());
    }

    public void saveSettings(){
        JMapFrame mapWindow = mainFrame.getMapWindows().get(id);

        mapWindow.setTitle(textField.getText());
        mainFrame.renameMapWindow(mapWindow);

        if (chckbxLocked.isSelected() != mapWindow.isLocked()){
            mapWindow.lockUnlockWindow();
            mainFrame.lockMapWindow(mapWindow, chckbxLocked.isSelected());
        }

        if (chckbxAlwaysOnTop.isSelected() != mapWindow.isInFront()){
            mapWindow.alwaysFront();
            mainFrame.onTopMapWindow(mapWindow, chckbxAlwaysOnTop.isSelected());

        }


    }


    public String getMapTitle(){
        return textField.getText();
    }
}
