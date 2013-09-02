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
package dk.dma.epd.shore.layers.voct;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import dk.dma.epd.shore.EPDShore;

public class SARPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private JPanel statusPanel;
    private JPanel timeAndDatePanel;
    private JPanel weatherPanel;
    private JPanel datumPanel;
    private JPanel searchAreaPanel;
    private JPanel buttonPanel;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    private JLabel lblTimeOfLast;
    private JLabel label;
    private JLabel lblNewLabel;
    private JLabel lblNewLabel_1;
    private JLabel lblTimeElasped;
    private JLabel lblHours;
    private JLabel lblDirection;
    private JLabel label_1;
    private JLabel lblSpeed;
    private JLabel lblKt;
    private JLabel lblDownwind;
    private JLabel lblLatitude;
    private JLabel lblLongitude;
    private JLabel lblRdv;
    private JLabel lblRadius;
    private JLabel lblN;
    private JLabel lblE;
    private JLabel label_2;
    private JLabel label_3;
    private JLabel lblA;
    private JLabel lblB;
    private JLabel lblC;
    private JLabel lblD;
    private JLabel lblAreaSize;
    private JLabel lblLatitude_1;
    private JLabel lblLongitude_1;
    private JLabel lblN_1;
    private JLabel lblE_1;
    private JLabel lblN_2;
    private JLabel lblE_2;
    private JLabel lblN_3;
    private JLabel lblE_3;
    private JLabel lblN_4;
    private JLabel lblE_4;
    private JLabel label_4;
    private Component horizontalStrut;
    private JPanel effortAllocationPanel;
    private JLabel typeShip;
    private JLabel lblMhvBopa;
    private JLabel lblNm;
    private JButton btnSruList;
    
    
    private static int iconWidth = 20;
    private static int iconHeight = 20;
    private JLabel typeHeli;
    private JCheckBox checkBox;
    private JCheckBox checkBox_1;
    private JCheckBox checkBox_2;
    private JLabel typePlane;
    private JLabel lblRescue;

    public SARPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 0};
        gridBagLayout.rowHeights = new int[]{20, 34, 16, 16, 16, 16, 16, 0, 10};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        JLabel lblSAR = new JLabel("Search And Rescue");
        lblSAR.setHorizontalAlignment(SwingConstants.CENTER);
        lblSAR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_lblSAR = new GridBagConstraints();
        gbc_lblSAR.anchor = GridBagConstraints.NORTH;
        gbc_lblSAR.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSAR.insets = new Insets(0, 0, 5, 0);
        gbc_lblSAR.gridx = 0;
        gbc_lblSAR.gridy = 0;
        add(lblSAR, gbc_lblSAR);
        
        statusPanel = new JPanel();
        statusPanel.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_statusPanel = new GridBagConstraints();
        gbc_statusPanel.anchor = GridBagConstraints.NORTH;
        gbc_statusPanel.insets = new Insets(0, 0, 5, 0);
        gbc_statusPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_statusPanel.gridx = 0;
        gbc_statusPanel.gridy = 1;
        add(statusPanel, gbc_statusPanel);
        GridBagLayout gbl_statusPanel = new GridBagLayout();
        gbl_statusPanel.columnWidths = new int[]{32, 28, 0};
        gbl_statusPanel.rowHeights = new int[]{14, 0};
        gbl_statusPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_statusPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        statusPanel.setLayout(gbl_statusPanel);
        
        JLabel lblType = new JLabel("Type:");
        GridBagConstraints gbc_lblType = new GridBagConstraints();
        gbc_lblType.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblType.insets = new Insets(0, 0, 5, 0);
        gbc_lblType.gridx = 0;
        gbc_lblType.gridy = 0;
        statusPanel.add(lblType, gbc_lblType);
        
        JLabel lblRapidResponse = new JLabel("Rapid Response");
        GridBagConstraints gbc_lblRapidResponse = new GridBagConstraints();
        gbc_lblRapidResponse.anchor = GridBagConstraints.WEST;
        gbc_lblRapidResponse.gridx = 1;
        gbc_lblRapidResponse.gridy = 0;
        statusPanel.add(lblRapidResponse, gbc_lblRapidResponse);
        
        timeAndDatePanel = new JPanel();
        timeAndDatePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Date and Time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_timeAndDatePanel = new GridBagConstraints();
        gbc_timeAndDatePanel.anchor = GridBagConstraints.NORTH;
        gbc_timeAndDatePanel.insets = new Insets(0, 0, 5, 0);
        gbc_timeAndDatePanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_timeAndDatePanel.gridx = 0;
        gbc_timeAndDatePanel.gridy = 2;
        add(timeAndDatePanel, gbc_timeAndDatePanel);
        GridBagLayout gbl_timeAndDatePanel = new GridBagLayout();
        gbl_timeAndDatePanel.columnWidths = new int[] {32, 28, 0};
        gbl_timeAndDatePanel.rowHeights = new int[]{14, 0, 0, 0};
        gbl_timeAndDatePanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_timeAndDatePanel.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
        timeAndDatePanel.setLayout(gbl_timeAndDatePanel);
        
        lblTimeOfLast = new JLabel("Time of Last Known Position (LKP):");
        GridBagConstraints gbc_lblTimeOfLast = new GridBagConstraints();
        gbc_lblTimeOfLast.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTimeOfLast.insets = new Insets(0, 0, 5, 5);
        gbc_lblTimeOfLast.gridx = 0;
        gbc_lblTimeOfLast.gridy = 0;
        timeAndDatePanel.add(lblTimeOfLast, gbc_lblTimeOfLast);
        
        label = new JLabel("08:00 02/07/2013");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.fill = GridBagConstraints.HORIZONTAL;
        gbc_label.insets = new Insets(0, 0, 5, 0);
        gbc_label.gridx = 1;
        gbc_label.gridy = 0;
        timeAndDatePanel.add(label, gbc_label);
        
        lblNewLabel = new JLabel("Time of Commence Search Start:");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        timeAndDatePanel.add(lblNewLabel, gbc_lblNewLabel);
        
        lblNewLabel_1 = new JLabel("10:30 02/07/2013");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridx = 1;
        gbc_lblNewLabel_1.gridy = 1;
        timeAndDatePanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
        
        lblTimeElasped = new JLabel("Time elasped:");
        GridBagConstraints gbc_lblTimeElasped = new GridBagConstraints();
        gbc_lblTimeElasped.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTimeElasped.insets = new Insets(0, 0, 0, 5);
        gbc_lblTimeElasped.gridx = 0;
        gbc_lblTimeElasped.gridy = 2;
        timeAndDatePanel.add(lblTimeElasped, gbc_lblTimeElasped);
        
        lblHours = new JLabel("2 hours 30 minutes");
        GridBagConstraints gbc_lblHours = new GridBagConstraints();
        gbc_lblHours.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblHours.gridx = 1;
        gbc_lblHours.gridy = 2;
        timeAndDatePanel.add(lblHours, gbc_lblHours);
        
        weatherPanel = new JPanel();
        weatherPanel.setBorder(new TitledBorder(null, "Last Surface Drifts dw continuation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_weatherPanel = new GridBagConstraints();
        gbc_weatherPanel.anchor = GridBagConstraints.NORTH;
        gbc_weatherPanel.insets = new Insets(0, 0, 5, 0);
        gbc_weatherPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_weatherPanel.gridx = 0;
        gbc_weatherPanel.gridy = 3;
        add(weatherPanel, gbc_weatherPanel);
        GridBagLayout gbl_weatherPanel = new GridBagLayout();
        gbl_weatherPanel.columnWidths = new int[] {32, 28, 0};
        gbl_weatherPanel.rowHeights = new int[] {14, 0, 0, 0};
        gbl_weatherPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_weatherPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        weatherPanel.setLayout(gbl_weatherPanel);
        
        lblDirection = new JLabel("Direction:");
        GridBagConstraints gbc_lblDirection = new GridBagConstraints();
        gbc_lblDirection.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblDirection.insets = new Insets(0, 0, 5, 5);
        gbc_lblDirection.gridx = 0;
        gbc_lblDirection.gridy = 0;
        weatherPanel.add(lblDirection, gbc_lblDirection);
        
        label_1 = new JLabel("180°");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.WEST;
        gbc_label_1.insets = new Insets(0, 0, 5, 0);
        gbc_label_1.gridx = 1;
        gbc_label_1.gridy = 0;
        weatherPanel.add(label_1, gbc_label_1);
        
        lblSpeed = new JLabel("Speed:");
        GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
        gbc_lblSpeed.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSpeed.insets = new Insets(0, 0, 0, 5);
        gbc_lblSpeed.gridx = 0;
        gbc_lblSpeed.gridy = 1;
        weatherPanel.add(lblSpeed, gbc_lblSpeed);
        
        lblKt = new JLabel("1.8 kt");
        GridBagConstraints gbc_lblKt = new GridBagConstraints();
        gbc_lblKt.anchor = GridBagConstraints.WEST;
        gbc_lblKt.gridx = 1;
        gbc_lblKt.gridy = 1;
        weatherPanel.add(lblKt, gbc_lblKt);
        
        datumPanel = new JPanel();
        datumPanel.setBorder(new TitledBorder(null, "Position of Datum to LKP", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_datumPanel = new GridBagConstraints();
        gbc_datumPanel.anchor = GridBagConstraints.NORTH;
        gbc_datumPanel.insets = new Insets(0, 0, 5, 0);
        gbc_datumPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_datumPanel.gridx = 0;
        gbc_datumPanel.gridy = 4;
        add(datumPanel, gbc_datumPanel);
        GridBagLayout gbl_datumPanel = new GridBagLayout();
        gbl_datumPanel.columnWidths = new int[] {0, 0, 0, 0, 0};
        gbl_datumPanel.rowHeights = new int[] {0, 0};
        gbl_datumPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
        gbl_datumPanel.rowWeights = new double[]{1.0, 1.0};
        datumPanel.setLayout(gbl_datumPanel);
        
        lblLatitude = new JLabel("Latitude");
        GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
        gbc_lblLatitude.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
        gbc_lblLatitude.gridx = 1;
        gbc_lblLatitude.gridy = 0;
        datumPanel.add(lblLatitude, gbc_lblLatitude);
        
        lblLongitude = new JLabel("Longitude");
        GridBagConstraints gbc_lblLongitude = new GridBagConstraints();
        gbc_lblLongitude.insets = new Insets(0, 0, 5, 5);
        gbc_lblLongitude.gridx = 2;
        gbc_lblLongitude.gridy = 0;
        datumPanel.add(lblLongitude, gbc_lblLongitude);
        
        lblRdv = new JLabel("RDV");
        GridBagConstraints gbc_lblRdv = new GridBagConstraints();
        gbc_lblRdv.insets = new Insets(0, 0, 5, 5);
        gbc_lblRdv.gridx = 3;
        gbc_lblRdv.gridy = 0;
        datumPanel.add(lblRdv, gbc_lblRdv);
        
        lblRadius = new JLabel("Radius");
        GridBagConstraints gbc_lblRadius = new GridBagConstraints();
        gbc_lblRadius.insets = new Insets(0, 0, 5, 0);
        gbc_lblRadius.gridx = 4;
        gbc_lblRadius.gridy = 0;
        datumPanel.add(lblRadius, gbc_lblRadius);
        
        lblDownwind = new JLabel("Downwind:");
        GridBagConstraints gbc_lblDownwind = new GridBagConstraints();
        gbc_lblDownwind.insets = new Insets(0, 0, 0, 5);
        gbc_lblDownwind.gridx = 0;
        gbc_lblDownwind.gridy = 1;
        datumPanel.add(lblDownwind, gbc_lblDownwind);
        
        lblN = new JLabel("56°18,2 N\t");
        GridBagConstraints gbc_lblN = new GridBagConstraints();
        gbc_lblN.insets = new Insets(0, 0, 0, 5);
        gbc_lblN.gridx = 1;
        gbc_lblN.gridy = 1;
        datumPanel.add(lblN, gbc_lblN);
        
        lblE = new JLabel("7°58,0 E\t");
        GridBagConstraints gbc_lblE = new GridBagConstraints();
        gbc_lblE.insets = new Insets(0, 0, 0, 5);
        gbc_lblE.gridx = 2;
        gbc_lblE.gridy = 1;
        datumPanel.add(lblE, gbc_lblE);
        
        label_2 = new JLabel("4.41");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.insets = new Insets(0, 0, 0, 5);
        gbc_label_2.gridx = 3;
        gbc_label_2.gridy = 1;
        datumPanel.add(label_2, gbc_label_2);
        
        label_3 = new JLabel("2.42");
        GridBagConstraints gbc_label_3 = new GridBagConstraints();
        gbc_label_3.gridx = 4;
        gbc_label_3.gridy = 1;
        datumPanel.add(label_3, gbc_label_3);
        
        searchAreaPanel = new JPanel();
        searchAreaPanel.setBorder(new TitledBorder(null, "Positions of the Search Area", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_searchAreaPanel = new GridBagConstraints();
        gbc_searchAreaPanel.anchor = GridBagConstraints.NORTH;
        gbc_searchAreaPanel.insets = new Insets(0, 0, 5, 0);
        gbc_searchAreaPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_searchAreaPanel.gridx = 0;
        gbc_searchAreaPanel.gridy = 5;
        add(searchAreaPanel, gbc_searchAreaPanel);
        GridBagLayout gbl_searchAreaPanel = new GridBagLayout();
        gbl_searchAreaPanel.columnWidths = new int[] {32, 28, 0};
        gbl_searchAreaPanel.rowHeights = new int[] {0, 14, 0, 0, 0, 0, 0};
        gbl_searchAreaPanel.columnWeights = new double[]{1.0, 1.0, 1.0};
        gbl_searchAreaPanel.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        searchAreaPanel.setLayout(gbl_searchAreaPanel);
        
        lblLatitude_1 = new JLabel("Latitude");
        GridBagConstraints gbc_lblLatitude_1 = new GridBagConstraints();
        gbc_lblLatitude_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblLatitude_1.gridx = 1;
        gbc_lblLatitude_1.gridy = 0;
        searchAreaPanel.add(lblLatitude_1, gbc_lblLatitude_1);
        
        lblLongitude_1 = new JLabel("Longitude");
        GridBagConstraints gbc_lblLongitude_1 = new GridBagConstraints();
        gbc_lblLongitude_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblLongitude_1.gridx = 2;
        gbc_lblLongitude_1.gridy = 0;
        searchAreaPanel.add(lblLongitude_1, gbc_lblLongitude_1);
        
        lblA = new JLabel("A");
        GridBagConstraints gbc_lblA = new GridBagConstraints();
        gbc_lblA.insets = new Insets(0, 0, 5, 5);
        gbc_lblA.gridx = 0;
        gbc_lblA.gridy = 1;
        searchAreaPanel.add(lblA, gbc_lblA);
        
        lblN_1 = new JLabel("56°20,6 N\t");
        GridBagConstraints gbc_lblN_1 = new GridBagConstraints();
        gbc_lblN_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_1.gridx = 1;
        gbc_lblN_1.gridy = 1;
        searchAreaPanel.add(lblN_1, gbc_lblN_1);
        
        lblE_1 = new JLabel("7°53,6 E\t");
        GridBagConstraints gbc_lblE_1 = new GridBagConstraints();
        gbc_lblE_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_1.gridx = 2;
        gbc_lblE_1.gridy = 1;
        searchAreaPanel.add(lblE_1, gbc_lblE_1);
        
        lblB = new JLabel("B");
        GridBagConstraints gbc_lblB = new GridBagConstraints();
        gbc_lblB.insets = new Insets(0, 0, 5, 5);
        gbc_lblB.gridx = 0;
        gbc_lblB.gridy = 2;
        searchAreaPanel.add(lblB, gbc_lblB);
        
        lblN_2 = new JLabel("56°20,6 N\t");
        GridBagConstraints gbc_lblN_2 = new GridBagConstraints();
        gbc_lblN_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_2.gridx = 1;
        gbc_lblN_2.gridy = 2;
        searchAreaPanel.add(lblN_2, gbc_lblN_2);
        
        lblE_2 = new JLabel("8°02,4 E\t");
        GridBagConstraints gbc_lblE_2 = new GridBagConstraints();
        gbc_lblE_2.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_2.gridx = 2;
        gbc_lblE_2.gridy = 2;
        searchAreaPanel.add(lblE_2, gbc_lblE_2);
        
        lblC = new JLabel("C");
        GridBagConstraints gbc_lblC = new GridBagConstraints();
        gbc_lblC.insets = new Insets(0, 0, 5, 5);
        gbc_lblC.gridx = 0;
        gbc_lblC.gridy = 3;
        searchAreaPanel.add(lblC, gbc_lblC);
        
        lblN_3 = new JLabel("56°15,8 N\t");
        GridBagConstraints gbc_lblN_3 = new GridBagConstraints();
        gbc_lblN_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_3.gridx = 1;
        gbc_lblN_3.gridy = 3;
        searchAreaPanel.add(lblN_3, gbc_lblN_3);
        
        lblE_3 = new JLabel("8°02,4 E\t");
        GridBagConstraints gbc_lblE_3 = new GridBagConstraints();
        gbc_lblE_3.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_3.gridx = 2;
        gbc_lblE_3.gridy = 3;
        searchAreaPanel.add(lblE_3, gbc_lblE_3);
        
        lblD = new JLabel("D");
        GridBagConstraints gbc_lblD = new GridBagConstraints();
        gbc_lblD.insets = new Insets(0, 0, 5, 5);
        gbc_lblD.gridx = 0;
        gbc_lblD.gridy = 4;
        searchAreaPanel.add(lblD, gbc_lblD);
        
        lblN_4 = new JLabel("56°15,8 N\t");
        GridBagConstraints gbc_lblN_4 = new GridBagConstraints();
        gbc_lblN_4.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_4.gridx = 1;
        gbc_lblN_4.gridy = 4;
        searchAreaPanel.add(lblN_4, gbc_lblN_4);
        
        lblE_4 = new JLabel("7°53,6 E\t");
        GridBagConstraints gbc_lblE_4 = new GridBagConstraints();
        gbc_lblE_4.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_4.gridx = 2;
        gbc_lblE_4.gridy = 4;
        searchAreaPanel.add(lblE_4, gbc_lblE_4);
        
        horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalStrut.gridx = 1;
        gbc_horizontalStrut.gridy = 5;
        searchAreaPanel.add(horizontalStrut, gbc_horizontalStrut);
        
        lblAreaSize = new JLabel("Area [nm²]");
        GridBagConstraints gbc_lblAreaSize = new GridBagConstraints();
        gbc_lblAreaSize.insets = new Insets(0, 0, 0, 5);
        gbc_lblAreaSize.gridx = 0;
        gbc_lblAreaSize.gridy = 6;
        searchAreaPanel.add(lblAreaSize, gbc_lblAreaSize);
        
        label_4 = new JLabel("23");
        GridBagConstraints gbc_label_4 = new GridBagConstraints();
        gbc_label_4.insets = new Insets(0, 0, 0, 5);
        gbc_label_4.gridx = 1;
        gbc_label_4.gridy = 6;
        searchAreaPanel.add(label_4, gbc_label_4);
        
        buttonPanel = new JPanel();
        GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
        gbc_buttonPanel.anchor = GridBagConstraints.NORTH;
        gbc_buttonPanel.insets = new Insets(0, 0, 5, 0);
        gbc_buttonPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_buttonPanel.gridx = 0;
        gbc_buttonPanel.gridy = 6;
        add(buttonPanel, gbc_buttonPanel);
        
        btnReopenCalculations = new JButton("Reopen Calculations");
        buttonPanel.add(btnReopenCalculations);
        
        btnEffortAllocation = new JButton("Allocated SRUs");
        buttonPanel.add(btnEffortAllocation);
        
        btnSruList = new JButton("SRU List");
        buttonPanel.add(btnSruList);
        
        effortAllocationPanel = new JPanel();
        effortAllocationPanel.setBorder(new TitledBorder(null, "Effort Allocation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_effortAllocationPanel = new GridBagConstraints();
        gbc_effortAllocationPanel.anchor = GridBagConstraints.NORTH;
        gbc_effortAllocationPanel.fill = GridBagConstraints.HORIZONTAL;
        gbc_effortAllocationPanel.gridx = 0;
        gbc_effortAllocationPanel.gridy = 7;
        add(effortAllocationPanel, gbc_effortAllocationPanel);
        GridBagLayout gbl_effortAllocationPanel = new GridBagLayout();
        gbl_effortAllocationPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_effortAllocationPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
        gbl_effortAllocationPanel.columnWeights = new double[]{1.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_effortAllocationPanel.rowWeights = new double[]{1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
        effortAllocationPanel.setLayout(gbl_effortAllocationPanel);
        
        typeShip = new JLabel(toolbarIcon("images/voct/ship.png"));
        
        
        
        GridBagConstraints gbc_typeShip = new GridBagConstraints();
        gbc_typeShip.insets = new Insets(0, 0, 5, 5);
        gbc_typeShip.gridx = 0;
        gbc_typeShip.gridy = 0;
        effortAllocationPanel.add(typeShip, gbc_typeShip);
        
        lblMhvBopa = new JLabel("MHV Bopa");
        GridBagConstraints gbc_lblMhvBopa = new GridBagConstraints();
        gbc_lblMhvBopa.insets = new Insets(0, 0, 5, 5);
        gbc_lblMhvBopa.gridx = 1;
        gbc_lblMhvBopa.gridy = 0;
        effortAllocationPanel.add(lblMhvBopa, gbc_lblMhvBopa);
        
        checkBox = new JCheckBox("");
        GridBagConstraints gbc_checkBox = new GridBagConstraints();
        gbc_checkBox.insets = new Insets(0, 0, 5, 0);
        gbc_checkBox.gridx = 2;
        gbc_checkBox.gridy = 0;
        effortAllocationPanel.add(checkBox, gbc_checkBox);
        
        typeHeli = new JLabel(toolbarIcon("images/voct/helicopter.png"));
        GridBagConstraints gbc_typeHeli = new GridBagConstraints();
        gbc_typeHeli.insets = new Insets(0, 0, 5, 5);
        gbc_typeHeli.gridx = 0;
        gbc_typeHeli.gridy = 1;
        effortAllocationPanel.add(typeHeli, gbc_typeHeli);
        
        lblNm = new JLabel("R169");
        GridBagConstraints gbc_lblNm = new GridBagConstraints();
        gbc_lblNm.insets = new Insets(0, 0, 5, 5);
        gbc_lblNm.gridx = 1;
        gbc_lblNm.gridy = 1;
        effortAllocationPanel.add(lblNm, gbc_lblNm);
        
        checkBox_1 = new JCheckBox("");
        GridBagConstraints gbc_checkBox_1 = new GridBagConstraints();
        gbc_checkBox_1.insets = new Insets(0, 0, 5, 0);
        gbc_checkBox_1.gridx = 2;
        gbc_checkBox_1.gridy = 1;
        effortAllocationPanel.add(checkBox_1, gbc_checkBox_1);
        
        typePlane = new JLabel(toolbarIcon("images/voct/plane.png"));
        GridBagConstraints gbc_typePlane = new GridBagConstraints();
        gbc_typePlane.insets = new Insets(0, 0, 5, 5);
        gbc_typePlane.gridx = 0;
        gbc_typePlane.gridy = 2;
        effortAllocationPanel.add(typePlane, gbc_typePlane);
        
        lblRescue = new JLabel("Rescue 170");
        GridBagConstraints gbc_lblRescue = new GridBagConstraints();
        gbc_lblRescue.insets = new Insets(0, 0, 5, 5);
        gbc_lblRescue.gridx = 1;
        gbc_lblRescue.gridy = 2;
        effortAllocationPanel.add(lblRescue, gbc_lblRescue);
        
        checkBox_2 = new JCheckBox("");
        GridBagConstraints gbc_checkBox_2 = new GridBagConstraints();
        gbc_checkBox_2.insets = new Insets(0, 0, 5, 0);
        gbc_checkBox_2.gridx = 2;
        gbc_checkBox_2.gridy = 2;
        effortAllocationPanel.add(checkBox_2, gbc_checkBox_2);
    }
    
    

    public ImageIcon toolbarIcon(String imgpath) {

        ImageIcon icon = new ImageIcon(EPDShore.class.getClassLoader()
                .getResource(imgpath));
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(iconWidth, iconHeight,
                java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }
}
