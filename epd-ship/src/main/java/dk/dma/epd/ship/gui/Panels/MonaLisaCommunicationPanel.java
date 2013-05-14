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
package dk.dma.epd.ship.gui.Panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.JScrollPane;

public class MonaLisaCommunicationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    
    JTextArea chatMessages;
    private JButton btnSend;
    private JTextField textField;
    private JPanel panel;
    private JTextField textField_1;

    public MonaLisaCommunicationPanel() {
        super();
        
//        setMinimumSize(new Dimension(200, 500));
        
        setBorder(new LineBorder(Color.GRAY));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 10, 0 };
        gridBagLayout.rowHeights = new int[] { 119, 10 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 1.0, 1.0 };
        setLayout(gridBagLayout);
        
        
        chatMessages = new JTextArea("");
        chatMessages.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chatMessages.setBackground(new Color(240, 240, 240));
        chatMessages.setLineWrap(true);
        chatMessages.setEditable(false);
        chatMessages.setBorder(null);

//        chatMessages.setMinimumSize(new Dimension(200, 500));
        
        chatMessages.setText("Hello \n Line \nLine \nLine\nLine");
        
        JScrollPane scrollPane = new JScrollPane(chatMessages);
//        scrollPane.setMinimumSize(new Dimension(200, 500));
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        add(scrollPane, gbc_scrollPane);
        
        JPanel panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.anchor = GridBagConstraints.SOUTH;
        gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 1;
        add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] {0, 2};
        gbl_panel_1.rowHeights = new int[] {0};
        gbl_panel_1.columnWeights = new double[]{1.0, 0.0};
        gbl_panel_1.rowWeights = new double[]{0.0};
        panel_1.setLayout(gbl_panel_1);
        
        textField_1 = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.insets = new Insets(0, 0, 0, 5);
        gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_1.gridx = 0;
        gbc_textField_1.gridy = 0;
        panel_1.add(textField_1, gbc_textField_1);
        textField_1.setColumns(10);
        
        JButton btnNewButton = new JButton("New button");
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.gridx = 1;
        gbc_btnNewButton.gridy = 0;
        panel_1.add(btnNewButton, gbc_btnNewButton);
    }
    

}
