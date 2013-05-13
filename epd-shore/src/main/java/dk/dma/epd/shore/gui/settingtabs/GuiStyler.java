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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

public class GuiStyler {

    public static Font defaultFont = new Font("Arial", Font.PLAIN, 11);
    public static Font boldFont = new Font("Arial", Font.BOLD, 11);
    public static Font subTabFont = new Font("Arial", Font.PLAIN, 10);
    public static Color textColor = new Color(237, 237, 237);
    public static Color backgroundColor = new Color(83, 83, 83);
    public static Border border = new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70));
    //Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(65, 65, 65));

    public static void styleTabButton(JLabel label){
        label.setPreferredSize(new Dimension(125, 25));
//        generalSettings.setSize(new Dimension(76, 30));
        label.setFont(defaultFont);
        label.setForeground(textColor);
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));
        label.setBackground(new Color(65, 65, 65));
        label.setOpaque(true);
    }

    public static void styleSubTab(JLabel label){
        label.setPreferredSize(new Dimension(124, 25));
//        generalSettings.setSize(new Dimension(76, 30));
//        label.setFont(subTabFont);
        label.setFont(subTabFont);
        label.setForeground(textColor);
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));
        label.setBackground(new Color(75, 75, 75));
        label.setOpaque(true);
    }

    @SuppressWarnings("rawtypes")
    public static void styleDropDown(JComboBox comboBox){

//        comboBox.setBackground(backgroundColor);
//        comboBox.setFont(defaultFont);
//        comboBox.setForeground(textColor);
//        comboBox.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));
//
//        comboBox.setOpaque(true);

//        ComboBoxEditor test = comboBox.getEditor();
//
//        test.getEditorComponent().setBackground(backgroundColor);

//        JTextField txtField = (JTextField) comboBox.getEditor().getEditorComponent();
//        styleTextFields(txtField);
//
//        JComboBox.DefaultEditor editor = (JComboBox.DefaultEditor)comboBox.getEditor();
//        editor.getTextField().setBackground(GuiStyler.backgroundColor);
//        editor.getTextField().setForeground(GuiStyler.textColor);
//        editor.getTextField().setFont(GuiStyler.defaultFont);
//        editor.getTextField().setCaretColor(GuiStyler.textColor);

    }

    public static void styleCheckbox(JCheckBox checkbox){

//        generalSettings.setSize(new Dimension(76, 30));
//        label.setFont(subTabFont);


        checkbox.setFont(subTabFont);
        checkbox.setForeground(textColor);
        checkbox.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));
        checkbox.setBackground(backgroundColor);
        checkbox.setOpaque(true);
    }

    public static void styleActiveTabButton(JLabel label){
        label.setPreferredSize(new Dimension(125, 25));
//        generalSettings.setSize(new Dimension(76, 30));
        label.setFont(defaultFont);
        label.setForeground(textColor);
        label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 5));
        label.setBackground(new Color(55, 55, 55));
        label.setOpaque(true);
    }

    public static void styleButton(final JLabel label){
        label.setPreferredSize(new Dimension(125, 25));
//        generalSettings.setSize(new Dimension(76, 30));
        label.setFont(defaultFont);
        label.setForeground(textColor);
        //label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(45, 45, 45)));
        label.setBackground(new Color(60, 60, 60));
        label.setOpaque(true);
        //label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.RIGHT);

        label.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (label.isEnabled()) {
                    label.setBackground(new Color(45, 45, 45));
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (label.isEnabled()) {
                    label.setBackground(new Color(60, 60, 60));
                }
            }
        });
    }

    public static void styleUnderMenu(JLabel label){
        label.setPreferredSize(new Dimension(125, 25));
        label.setFont(defaultFont);
        label.setForeground(textColor);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.setBackground(Color.gray);
        label.setOpaque(true);
    }

    public static void styleText(JLabel label){
        label.setFont(defaultFont);
        label.setForeground(textColor);
    }
    
    public static void styleTitle(JLabel label){
        label.setFont(boldFont);
        label.setForeground(textColor);
    }
    
    public static void styleArea(JTextArea area){
        area.setFont(defaultFont);
        area.setForeground(textColor);
        area.setBackground(GuiStyler.backgroundColor);
        area.setBorder(GuiStyler.border);
    }

    public static void styleSpinner(JSpinner spinner){
        spinner.setBorder(GuiStyler.border);

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
        editor.getTextField().setBackground(GuiStyler.backgroundColor);
        editor.getTextField().setForeground(GuiStyler.textColor);
        editor.getTextField().setFont(GuiStyler.defaultFont);
        editor.getTextField().setCaretColor(GuiStyler.textColor);
    }

    public static void styleTextFields(JTextField jtextField){
        jtextField.setBackground(GuiStyler.backgroundColor);
        jtextField.setForeground(GuiStyler.textColor);
        jtextField.setFont(GuiStyler.defaultFont);
        jtextField.setCaretColor(GuiStyler.textColor);
        jtextField.setBorder(GuiStyler.border);
    }
}
