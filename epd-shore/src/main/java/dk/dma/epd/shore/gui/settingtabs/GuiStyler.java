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
package dk.dma.epd.shore.gui.settingtabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

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

    /**
     * Setting tabs that are shared between EPDShip and EPDShore should be 
     * "styled" before using them in EPDShore.<p>
     * This method will recursively style the components of the panel
     * @param component call with the settings tab to style
     */
    public static void styleSettingsTab(Component component) {
        if (component instanceof JTextField) {
            styleTextFields((JTextField)component);
        } else  if (component instanceof JSpinner) {
            styleSpinner((JSpinner)component);
        } else  if (component instanceof JTextArea) {
            styleArea((JTextArea)component);
        } else  if (component instanceof JLabel) {
            styleText((JLabel)component);
        } else  if (component instanceof JCheckBox) {
            styleCheckbox((JCheckBox)component);
        } else if (component instanceof JComponent) {
            if (component instanceof JPanel) {
                ((JPanel)component).setOpaque(false);
                Border b = ((JComponent)component).getBorder();
                if (b != null && b instanceof TitledBorder) {
                    ((TitledBorder)b).setBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)));
                }
            }
            for (Component childComponent : ((JComponent)component).getComponents()) {
                styleSettingsTab(childComponent);
            }
        }
    }
}
