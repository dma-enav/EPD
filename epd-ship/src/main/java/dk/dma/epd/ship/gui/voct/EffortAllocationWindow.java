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
package dk.dma.epd.ship.gui.voct;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;

public class EffortAllocationWindow extends JDialog {

    private final JPanel initPanel = new JPanel();
    
    private JTextField textField;
    private JTextField textField_1;
    private JTextField txtn;
    private JTextField txte;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    private JTextField textField_6;
    private JTextField textField_7;
    private JTextField textField_8;
    private JTextField textField_9;
    private JTextField textField_10;
    private JTextField textField_11;
    private JTextField textField_12;

    /**
     * Create the dialog.
     */
    public EffortAllocationWindow() {
        setTitle("Effort Allocation");
        this.setModal(true);
//        setBounds(100, 100, 559, 733);
        setBounds(100, 100, 559, 575);
        getContentPane().setLayout(new BorderLayout());
        
        buttomBar();
        
        initPanel();
    }
    
    
  
    
    private void initPanel(){
        initPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(initPanel, BorderLayout.CENTER);
        
        
        
        
        initPanel.setLayout(null);
        ImageIcon icon = new ImageIcon("C:\\Dropbox\\Master Thesis - SAR - A Look into the Future of Maritime Services\\Master Thesis\\images\\rdvexample.png");
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance( 282, 127,  java.awt.Image.SCALE_SMOOTH ) ; 
        icon = new ImageIcon( newimg );
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Calculate Effective Search Area", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel.setBounds(10, 11, 523, 478);
            initPanel.add(panel);
            panel.setLayout(null);
            
            JPanel panel_1 = new JPanel();
            panel_1.setBorder(new TitledBorder(null, "Description", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_1.setBounds(10, 20, 503, 77);
            panel.add(panel_1);
            panel_1.setLayout(null);
            
            JTextPane txtField = new JTextPane();
            txtField.setBounds(12, 20, 483, 44);
            panel_1.add(txtField);
            txtField.setBackground(UIManager.getColor("Button.background"));
            txtField.setEditable(false);
            txtField.setText("Probability of Detection is a statistical measurement for determining the success rate for location an object lost at sea. More text to explain, guidelines, please verify information");
            
            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(null, "SRU Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_2.setBounds(10, 98, 503, 99);
            panel.add(panel_2);
            panel_2.setLayout(null);
            
            JLabel lblVesselName = new JLabel("Vessel Name:");
            lblVesselName.setBounds(10, 22, 83, 14);
            panel_2.add(lblVesselName);
            
            JLabel lblVesselDimensions = new JLabel("Vessel Dimensions:");
            lblVesselDimensions.setBounds(10, 47, 105, 14);
            panel_2.add(lblVesselDimensions);
            
            JLabel lblTopSpeed = new JLabel("Top Speed, knots:");
            lblTopSpeed.setBounds(10, 69, 105, 14);
            panel_2.add(lblTopSpeed);
            
            JLabel lblNewLabel = new JLabel("MHV 911 BOPA");
            lblNewLabel.setBounds(119, 22, 95, 14);
            panel_2.add(lblNewLabel);
            
            textField_7 = new JTextField();
            textField_7.setBounds(111, 44, 59, 20);
            panel_2.add(textField_7);
            textField_7.setColumns(10);
            
            textField_8 = new JTextField();
            textField_8.setBounds(217, 44, 86, 20);
            panel_2.add(textField_8);
            textField_8.setColumns(10);
            
            JLabel lblWidth = new JLabel("width, ");
            lblWidth.setBounds(180, 47, 46, 14);
            panel_2.add(lblWidth);
            
            JLabel lblLength = new JLabel("length");
            lblLength.setBounds(313, 47, 46, 14);
            panel_2.add(lblLength);
            
            textField_9 = new JTextField();
            textField_9.setBounds(111, 66, 86, 20);
            panel_2.add(textField_9);
            textField_9.setColumns(10);
            
            JPanel panel_3 = new JPanel();
            panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "SAR Information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_3.setBounds(10, 208, 503, 72);
            panel.add(panel_3);
            panel_3.setLayout(null);
            {
                JLabel lblSelectSarType = new JLabel("Target Type:");
                lblSelectSarType.setBounds(10, 21, 140, 14);
                panel_3.add(lblSelectSarType);
            }
            {
                JComboBox comboBox = new JComboBox();
                comboBox.setBounds(139, 18, 354, 20);
                panel_3.add(comboBox);
                comboBox.setModel(new DefaultComboBoxModel(new String[] {"Person in Water, raft or boat < 30 ft", "Other targets"}));
            }
            
            JLabel lblTimeSpentSearching = new JLabel("Time spent searching:");
            lblTimeSpentSearching.setBounds(10, 46, 124, 14);
            panel_3.add(lblTimeSpentSearching);
            
            JSpinner spinner = new JSpinner();
            spinner.setModel(new SpinnerNumberModel(new Integer(1), null, null, new Integer(1)));
            spinner.setBounds(139, 44, 54, 20);
            panel_3.add(spinner);
            
            JLabel lblHours = new JLabel("hours");
            lblHours.setBounds(205, 46, 46, 14);
            panel_3.add(lblHours);
            
            JPanel panel_4 = new JPanel();
            panel_4.setBorder(new TitledBorder(null, "Weather information", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_4.setBounds(10, 293, 503, 88);
            panel.add(panel_4);
            panel_4.setLayout(null);
            
            JLabel lblWindCurrentKnots = new JLabel("Wind Current, knots:");
            lblWindCurrentKnots.setBounds(12, 26, 147, 14);
            panel_4.add(lblWindCurrentKnots);
            
            textField_10 = new JTextField();
            textField_10.setColumns(10);
            textField_10.setBounds(159, 23, 33, 20);
            panel_4.add(textField_10);
            
            JLabel lblTotalWindCurrent = new JLabel("Water Elevation, feet:");
            lblTotalWindCurrent.setBounds(12, 56, 147, 14);
            panel_4.add(lblTotalWindCurrent);
            
            textField_11 = new JTextField();
            textField_11.setColumns(10);
            textField_11.setBounds(159, 53, 33, 20);
            panel_4.add(textField_11);
            
            JPanel panel_5 = new JPanel();
            panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Additional Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_5.setBounds(10, 394, 503, 71);
            panel.add(panel_5);
            panel_5.setLayout(null);
            
            JLabel lblDesiredProbabilityOf = new JLabel("Desired Probability of Detection:");
            lblDesiredProbabilityOf.setBounds(12, 24, 197, 14);
            panel_5.add(lblDesiredProbabilityOf);
            
            textField_12 = new JTextField();
            textField_12.setEnabled(false);
            textField_12.setEditable(false);
            textField_12.setText("79%");
            textField_12.setBounds(193, 22, 41, 20);
            panel_5.add(textField_12);
            textField_12.setColumns(10);
            
            JCheckBox chckbxEdit = new JCheckBox("Edit");
            chckbxEdit.setBounds(237, 19, 105, 25);
            panel_5.add(chckbxEdit);
        }
        
    }
    
    private void buttomBar(){
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
            JButton okButton = new JButton("Calculate Effective Area");
            okButton.setActionCommand("OK");
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);
        }
    }
}
