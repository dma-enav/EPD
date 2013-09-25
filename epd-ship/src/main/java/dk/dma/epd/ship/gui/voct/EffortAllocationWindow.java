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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.LeewayValues;
import dk.dma.epd.ship.service.voct.RapidResponseData;
import dk.dma.epd.ship.service.voct.SweepWidthValues;
import dk.dma.epd.ship.service.voct.VOCTManager;
import dk.dma.epd.ship.service.voct.WeatherCorrectionFactors;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;

public class EffortAllocationWindow extends JDialog implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();

    private JLabel shipName;
    private JTextField topSpeed;
    private JTextField windspeedField;
    private JTextField waterElevationField;
    private JTextField probabilityOfDetectionVal;


    JComboBox<String> targetTypeDropdown;
    JSpinner hoursSearching;
    private JComboBox<String> sruType;
    JComboBox<Integer> visibilityDropDown;

    private JCheckBox editPoD;
    private JButton calculate;
    private JComboBox<Double> fatigueDropDown;
    private VOCTManager voctManager;

    /**
     * Create the dialog.
     */
    public EffortAllocationWindow(VOCTManager voctManager) {
        setTitle("Effort Allocation");
        this.setModal(true);
        // setBounds(100, 100, 559, 733);
        setBounds(100, 100, 559, 575);
        getContentPane().setLayout(new BorderLayout());

        this.voctManager = voctManager;

        buttomBar();

        initPanel();

        setValues();

    }

    private void initPanel() {
        initPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(initPanel, BorderLayout.CENTER);

        initPanel.setLayout(null);
        ImageIcon icon = new ImageIcon(
                "C:\\Dropbox\\Master Thesis - SAR - A Look into the Future of Maritime Services\\Master Thesis\\images\\rdvexample.png");
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(282, 127,
                java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"),
                    "Calculate Effective Search Area", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            panel.setBounds(10, 11, 523, 478);
            initPanel.add(panel);
            panel.setLayout(null);

            JPanel panel_1 = new JPanel();
            panel_1.setBorder(new TitledBorder(null, "Description",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_1.setBounds(10, 20, 503, 77);
            panel.add(panel_1);
            panel_1.setLayout(null);

            JTextPane txtField = new JTextPane();
            txtField.setBounds(12, 20, 483, 44);
            panel_1.add(txtField);
            txtField.setBackground(UIManager.getColor("Button.background"));
            txtField.setEditable(false);
            txtField.setText("Probability of Detection is a statistical measurement for determining the success rate for location an object lost at sea. Recommended PoD is 79%");

            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(null, "SRU Information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_2.setBounds(10, 98, 503, 99);
            panel.add(panel_2);
            panel_2.setLayout(null);

            JLabel lblVesselName = new JLabel("Vessel Name:");
            lblVesselName.setBounds(10, 22, 83, 14);
            panel_2.add(lblVesselName);

            JLabel lblTopSpeed = new JLabel("Top Speed, knots:");
            lblTopSpeed.setBounds(10, 69, 105, 14);
            panel_2.add(lblTopSpeed);

            shipName = new JLabel("N/A");
            shipName.setBounds(112, 22, 95, 14);
            panel_2.add(shipName);

            topSpeed = new JTextField();
            topSpeed.setBounds(109, 66, 34, 20);
            panel_2.add(topSpeed);
            topSpeed.setColumns(10);

            JLabel lblNewLabel = new JLabel("Type:");
            lblNewLabel.setBounds(10, 44, 34, 14);
            panel_2.add(lblNewLabel);

            sruType = new JComboBox<String>();
            sruType.setModel(new DefaultComboBoxModel<String>(new String[] {
                    "Smaller vessel (40 feet)", "Ship (90 feet)" }));
            sruType.setBounds(109, 41, 148, 20);
            panel_2.add(sruType);

            JLabel lblFatigue = new JLabel("Fatigue:");
            lblFatigue.setBounds(273, 22, 46, 14);
            panel_2.add(lblFatigue);

            fatigueDropDown = new JComboBox<Double>();
            fatigueDropDown.setModel(new DefaultComboBoxModel<Double>(
                    new Double[] { 1.0, 0.9 }));
            fatigueDropDown.setBounds(329, 19, 45, 20);
            panel_2.add(fatigueDropDown);

            JPanel panel_3 = new JPanel();
            panel_3.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "SAR Information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_3.setBounds(10, 208, 503, 72);
            panel.add(panel_3);
            panel_3.setLayout(null);
            {
                JLabel lblSelectSarType = new JLabel("Target Type:");
                lblSelectSarType.setBounds(10, 21, 140, 14);
                panel_3.add(lblSelectSarType);
            }

            targetTypeDropdown = new JComboBox<String>();
            targetTypeDropdown.setBounds(139, 18, 354, 20);
            panel_3.add(targetTypeDropdown);
            // targetTypeDropdown.setModel(new DefaultComboBoxModel<String>(
            // new String[] { "Person in Water, raft or boat < 30 ft",
            // "Other targets" }));

            for (int i = 0; i < SweepWidthValues.getSweepWidthTypes().size(); i++) {
                targetTypeDropdown.addItem(SweepWidthValues
                        .getSweepWidthTypes().get(i));
            }

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

            JPanel panel_4 = new JPanel();
            panel_4.setBorder(new TitledBorder(null, "Weather information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_4.setBounds(10, 293, 503, 88);
            panel.add(panel_4);
            panel_4.setLayout(null);

            JLabel lblWindKnots = new JLabel("Wind Speed, knots:");
            lblWindKnots.setBounds(12, 26, 147, 14);
            panel_4.add(lblWindKnots);

            windspeedField = new JTextField();
            windspeedField.setColumns(10);
            windspeedField.setBounds(159, 23, 33, 20);
            panel_4.add(windspeedField);

            JLabel lblTotalWindCurrent = new JLabel("Water Elevation, feet:");
            lblTotalWindCurrent.setBounds(12, 56, 147, 14);
            panel_4.add(lblTotalWindCurrent);

            waterElevationField = new JTextField();
            waterElevationField.setColumns(10);
            waterElevationField.setBounds(159, 53, 33, 20);
            panel_4.add(waterElevationField);

            JLabel lblVisibilityNm = new JLabel("Visibility, nm");
            lblVisibilityNm.setBounds(212, 26, 68, 14);
            panel_4.add(lblVisibilityNm);

            visibilityDropDown = new JComboBox<Integer>();
            visibilityDropDown.setModel(new DefaultComboBoxModel<Integer>(
                    new Integer[] { 1, 3, 5, 10, 15, 20 }));
            visibilityDropDown.setBounds(276, 23, 45, 20);
            panel_4.add(visibilityDropDown);

            JPanel panel_5 = new JPanel();
            panel_5.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "Additional Options",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_5.setBounds(10, 394, 503, 71);
            panel.add(panel_5);
            panel_5.setLayout(null);

            JLabel lblDesiredProbabilityOf = new JLabel(
                    "Desired Probability of Detection:");
            lblDesiredProbabilityOf.setBounds(12, 24, 197, 14);
            panel_5.add(lblDesiredProbabilityOf);

            probabilityOfDetectionVal = new JTextField();
            probabilityOfDetectionVal.setEnabled(false);
            probabilityOfDetectionVal.setEditable(false);
            probabilityOfDetectionVal.setText("79%");
            probabilityOfDetectionVal.setBounds(193, 22, 41, 20);
            panel_5.add(probabilityOfDetectionVal);
            probabilityOfDetectionVal.setColumns(10);

            editPoD = new JCheckBox("Edit");
            editPoD.setBounds(237, 19, 105, 25);
            panel_5.add(editPoD);

            editPoD.addActionListener(this);
        }

    }

    private void setValues() {
        VesselTarget ownship = EPDShip.getAisHandler().getOwnShip();

        if (ownship != null) {
            if (ownship.getStaticData() != null) {
                shipName.setText(ownship.getStaticData().getName());

                double length = ownship.getStaticData().getDimBow()
                        + ownship.getStaticData().getDimStern();
                // String width = Integer.toString(ownship.getStaticData()
                // .getDimPort()
                // + ownship.getStaticData().getDimStarboard()) + " M";

                // Is the lenght indicated by the AIS longer than 89 feet then
                // it falls under Ship category
                if (Converter.metersToFeet(length) > 89) {
                    sruType.setSelectedIndex(1);
                }

            }
        }
    }

    private void buttomBar() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
            calculate = new JButton("Calculate Effective Area");
            buttonPane.add(calculate);
            getRootPane().setDefaultButton(calculate);
            calculate.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == editPoD) {
            if (editPoD.isSelected()) {
                probabilityOfDetectionVal.setEnabled(true);
                probabilityOfDetectionVal.setEditable(true);
            } else {
                probabilityOfDetectionVal.setEnabled(false);
                probabilityOfDetectionVal.setEditable(false);
            }
        }

        if (arg0.getSource() == calculate) {
            if (checkValues()) {
                // Ready to go
                voctManager.EffortAllocationDataEntered();
                this.setVisible(false);
            }
        }

    }

    private boolean checkValues() {

        RapidResponseData rapidResponseData = voctManager.getRapidResponseData();
        
        if (getMaxSpeed() == -9999) {
            return false;
        }

        rapidResponseData.setGroundSpeed(getMaxSpeed());

        // Wc = Wu x Fw x Fv x Ff

        // Wu is done by table lookup

        int targetType = targetTypeDropdown.getSelectedIndex();
        int visibility = (int) visibilityDropDown.getSelectedItem();

        double wu = 0.0;

        if (sruType.getSelectedIndex() == 0) {
            // Small type
            wu = SweepWidthSmallShipLookup(targetType, visibility);
        } else {
            if (sruType.getSelectedIndex() == 1) {
                wu = SweepWidthLargeShipLookup(targetType, visibility);
            }
        }

        int windSpeed = getWindSpeed();

        if (windSpeed == -9999) {
            return false;
        }

        int waterLevel = getWaterElevation();
        if (waterLevel == -9999) {
            return false;
        }

        int fwRow = 0;

        if (windSpeed >= 0 && windSpeed <= 15 || waterLevel >= 0
                && waterLevel <= 3) {
            fwRow = 0;
        }

        if (windSpeed > 15 && windSpeed <= 25 || waterLevel > 3
                && waterLevel <= 5) {
            fwRow = 1;
        }

        if (windSpeed > 25 || waterLevel > 5) {
            fwRow = 2;
        }

        // Two types of search object for FW
        // Person in Water, raft or boat less than 30 feet
        // Or
        // Other
        double fw;

        // PIW, raft or small boat
        if (targetType >= 0 && targetType <= 10 || targetType >= 14
                && targetType < 17) {
            fw = WeatherCorrectionFactors.getPIWAndSmallBoats().get(fwRow);
        } else {
            // Other object
            fw = WeatherCorrectionFactors.getOtherObjects().get(fwRow);
        }

        double ff = (double) fatigueDropDown.getSelectedItem();

        double wc = wu * fw * ff;

        rapidResponseData.setW(wc);

        System.out.println("The following Sweep Width is calculated:");
        System.out.println("Wc = Wu x Fw x Ff");
        System.out.println(wc + " = " + wu + " * " + fw + " * " + ff);

        double probabilityOfDetection = getProbabilityOfDetection();
        System.out.println(probabilityOfDetection);
        if (probabilityOfDetection == -9999) {
            return false;
        }

        rapidResponseData.setPod(probabilityOfDetection);

        int timeSearching = getSearchTimeHours();
        
        if (timeSearching == -9999) {
            System.out.println("failed to get time searching spinner val");
            return false;
        }
        rapidResponseData.setSearchTime(timeSearching);

        return true;
    }

    private double SweepWidthSmallShipLookup(int searchObject, int visibility) {
        return SweepWidthValues.getSmallerVessels().get(searchObject)
                .get(visibility);
    }

    private double SweepWidthLargeShipLookup(int searchObject, int visibility) {
        return SweepWidthValues.getLargerVessels().get(searchObject)
                .get(visibility);
    }

    private int getWindSpeed() {

        String windSpeed = windspeedField.getText();

        if (windSpeed.equals("")) {
            displayMissingField("Wind Speed");
            return -9999;
        } else {
            try {
                return Integer.parseInt(windSpeed);
            } catch (Exception e) {
                displayMissingField("Wind speed");
                return -9999;
            }
        }

    }

    private int getWaterElevation() {

        String waterElevation = waterElevationField.getText();

        if (waterElevation.equals("")) {
            displayMissingField("Water Elevation");
            return -9999;
        } else {
            try {
                return Integer.parseInt(waterElevation);
            } catch (Exception e) {
                displayMissingField("Water Elevation");
                return -9999;
            }
        }

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
                return Double.parseDouble(groundSpeed);
            } catch (Exception e) {
                displayMissingField("SRU Top Speed");
                return -9999;
            }
        }

    }

    private double getProbabilityOfDetection() {

        String probabilityOfDetection = probabilityOfDetectionVal.getText();

        // Remove %
        try {
            probabilityOfDetection = (String) probabilityOfDetection
                    .subSequence(0, probabilityOfDetection.length() - 1);
        } catch (Exception e) {
            // Invalid
        }

        if (probabilityOfDetection.equals("")) {
            displayMissingField("Probability of Detection");
            return -9999;
        } else {
            try {
                return Double.parseDouble(probabilityOfDetection) / 100;
            } catch (Exception e) {
                displayMissingField("Probability of Detection");
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
