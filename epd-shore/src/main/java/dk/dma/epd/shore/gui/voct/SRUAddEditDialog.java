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
package dk.dma.epd.shore.gui.voct;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRU.SRU_TYPE;
import dk.dma.epd.shore.voct.SRU.sru_status;
import dk.dma.epd.shore.voct.SRUManager;

public class SRUAddEditDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();
    private JTextField topSpeed;
    JSpinner hoursSearching;
    private JComboBox<String> sruType;
    JComboBox<Integer> visibilityDropDown;
    private JButton saveSRU;
    private JComboBox<Double> fatigueDropDown;
    
    private JTextField vesselNameTxt;
    
    private SRUManager sruManager;
    
    
    int sruSelection = -1;

    /**
     * Create the dialog.
     */
    public SRUAddEditDialog(SRUManager sruManager) {
        setTitle("Manage SRU");
        this.setModal(true);
        this.setResizable(false);
        
        // setBounds(100, 100, 559, 733);
        setBounds(100, 100, 491, 289);
        getContentPane().setLayout(new BorderLayout());

        buttomBar();

        initPanel();
        
        this.sruManager = sruManager;
    }
    
    
    public SRUAddEditDialog(SRUManager sruManager, int sruID) {
        setTitle("Manage SRU");
        this.setModal(true);
        this.setResizable(false);
        
        // setBounds(100, 100, 559, 733);
        setBounds(100, 100, 491, 289);
        getContentPane().setLayout(new BorderLayout());

        buttomBar();

        initPanel();
        
        this.sruManager = sruManager;
        
        
        this.sruSelection = sruID;
        initValues(sruManager.getSRUs(sruID));
        
    }

    private void initValues(SRU sru){
        
        
        vesselNameTxt.setText(sru.getName());
        
        
        
        if (sru.getType() == SRU_TYPE.Smaller_Vessel){
            sruType.setSelectedIndex(0);
        }
        
        if (sru.getType() == SRU_TYPE.Ship){
            sruType.setSelectedIndex(1);
        }
        
        
        visibilityDropDown.setSelectedItem(sru.getVisibility());
        

        fatigueDropDown.setSelectedItem(sru.getFatigue());
        
        topSpeed.setText(sru.getSearchSpeed() + "");
        
        hoursSearching.setValue(sru.getSearchTime());
        
        
        
        
        
    }

    private void initPanel() {
        initPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(initPanel, BorderLayout.CENTER);

        initPanel.setLayout(null);
   
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Manage SRU", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel.setBounds(10, 11, 445, 226);
            initPanel.add(panel);
            panel.setLayout(null);

            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(null, "SRU Information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_2.setBounds(10, 25, 412, 99);
            panel.add(panel_2);
            panel_2.setLayout(null);

            JLabel lblVesselName = new JLabel("Vessel Name:");
            lblVesselName.setBounds(10, 22, 83, 14);
            panel_2.add(lblVesselName);

            JLabel lblTopSpeed = new JLabel("Search Velocity, knots:");
            lblTopSpeed.setBounds(10, 69, 121, 14);
            panel_2.add(lblTopSpeed);

            topSpeed = new JTextField();
            topSpeed.setBounds(129, 66, 34, 20);
            panel_2.add(topSpeed);
            topSpeed.setColumns(10);

            JLabel lblNewLabel = new JLabel("Type:");
            lblNewLabel.setBounds(10, 44, 34, 14);
            panel_2.add(lblNewLabel);

            sruType = new JComboBox<String>();
            sruType.setModel(new DefaultComboBoxModel<String>(new String[] {
                    "Smaller vessel (40 feet)", "Ship (90 feet)" }));
            sruType.setBounds(129, 41, 148, 20);
            panel_2.add(sruType);

            JLabel lblFatigue = new JLabel("Fatigue:");
            lblFatigue.setBounds(290, 22, 46, 14);
            panel_2.add(lblFatigue);

            fatigueDropDown = new JComboBox<Double>();
            fatigueDropDown.setModel(new DefaultComboBoxModel<Double>(
                    new Double[] { 1.0, 0.9 }));
            fatigueDropDown.setBounds(345, 19, 45, 20);
            panel_2.add(fatigueDropDown);
            
            vesselNameTxt = new JTextField();
            vesselNameTxt.setBounds(129, 19, 148, 20);
            panel_2.add(vesselNameTxt);
            vesselNameTxt.setColumns(10);

            JPanel panel_3 = new JPanel();
            panel_3.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "SAR Information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_3.setBounds(10, 130, 412, 72);
            panel.add(panel_3);
            panel_3.setLayout(null);
            // targetTypeDropdown.setModel(new DefaultComboBoxModel<String>(
            // new String[] { "Person in Water, raft or boat < 30 ft",
            // "Other targets" }));

      

            JLabel lblTimeSpentSearching = new JLabel("Time spent searching:");
            lblTimeSpentSearching.setBounds(10, 46, 124, 14);
            panel_3.add(lblTimeSpentSearching);

            hoursSearching = new JSpinner();
            hoursSearching.setModel(new SpinnerNumberModel(new Integer(1),
                    null, null, new Integer(1)));
            hoursSearching.setBounds(139, 44, 54, 20);
            panel_3.add(hoursSearching);

            JLabel lblHours = new JLabel("hours");
            lblHours.setBounds(205, 46, 46, 14);
            panel_3.add(lblHours);
            
                        JLabel lblVisibilityNm = new JLabel("Visibility, nm");
                        lblVisibilityNm.setBounds(10, 18, 68, 14);
                        panel_3.add(lblVisibilityNm);
                        
                                    visibilityDropDown = new JComboBox<Integer>();
                                    visibilityDropDown.setBounds(139, 15, 45, 20);
                                    panel_3.add(visibilityDropDown);
                                    visibilityDropDown.setModel(new DefaultComboBoxModel<Integer>(
                                            new Integer[] { 1, 3, 5, 10, 15, 20 }));
        }

    }

    public void setValues() {
//        VesselTarget ownship = EPDShip.getAisHandler().getOwnShip();
//
//        if (ownship != null) {
//            if (ownship.getStaticData() != null) {
//                shipName.setText(ownship.getStaticData().getName());
//
//                double length = ownship.getStaticData().getDimBow()
//                        + ownship.getStaticData().getDimStern();
//                // String width = Integer.toString(ownship.getStaticData()
//                // .getDimPort()
//                // + ownship.getStaticData().getDimStarboard()) + " M";
//
//                // Is the lenght indicated by the AIS longer than 89 feet then
//                // it falls under Ship category
//                if (Converter.metersToFeet(length) > 89) {
//                    sruType.setSelectedIndex(1);
//                }
//
//            }
//        }
    }

    private void buttomBar() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
            saveSRU = new JButton("Save");
            buttonPane.add(saveSRU);
            getRootPane().setDefaultButton(saveSRU);
            saveSRU.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == saveSRU) {
            if (checkValues()) {
                // Ready to go

                String vesselName = vesselNameTxt.getText();
                
                SRU_TYPE type = null;
                
                if (sruType.getSelectedIndex() == 0) {
                    // Small type
                    type = SRU_TYPE.Smaller_Vessel;
                } else {
                    if (sruType.getSelectedIndex() == 1) {
                        type = SRU_TYPE.Ship;
                    }
                }
                
                int visibility = (int) visibilityDropDown.getSelectedItem();
                double fatigue = (double) fatigueDropDown.getSelectedItem();
                double searchSpeed = getMaxSpeed();
                int timeSearching = getSearchTimeHours();
//                type
                
                
                //Create a new one
                if (sruSelection == -1){
                    SRU sru = new SRU(vesselName, -1, type, sru_status.PENDING, searchSpeed, visibility, fatigue, timeSearching);

                    sruManager.addSRU(sru);
                }else{
                    sruManager.getSRUs(sruSelection).setName(vesselName);
                    sruManager.getSRUs(sruSelection).setSearchSpeed(searchSpeed);
                    sruManager.getSRUs(sruSelection).setVisibility(visibility);
                    sruManager.getSRUs(sruSelection).setFatigue(fatigue);
                    sruManager.getSRUs(sruSelection).setSearchTime(timeSearching);
                }
                

                
                this.dispose();
            }
        }

    }

    private boolean checkValues() {

        if (getMaxSpeed() == -9999){
            return false;
        }
 
        
        

        return true;
    }



    private int getSearchTimeHours() {

        // String groundSpeed = (String) hoursSearching.getValue();

        try {
            return (int) hoursSearching.getValue();
        } catch (Exception e) {
            displayMissingField("Time Searching");
            return -9999;
        }

    }

    private double getMaxSpeed() {

        String groundSpeed = topSpeed.getText();

        if (groundSpeed.equals("")) {
            displayMissingField("SRU Top Speed");
            return -9999;
        } else {
            try {
                if (groundSpeed.contains(",")){
                    groundSpeed = groundSpeed.replace(",", ".");
                }
                return Double.parseDouble(groundSpeed);
            } catch (Exception e) {
                displayMissingField("SRU Top Speed");
                return -9999;
            }
        }

    }

   

    private void displayMissingField(String fieldname) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, "Missing or incorrect value in "
                + fieldname, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

}
