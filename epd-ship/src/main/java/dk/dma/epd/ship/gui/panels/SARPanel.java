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
package dk.dma.epd.ship.gui.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.route.RouteManager;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.Box;

/**
 * Active waypoint panel in sensor panel
 */
public class SARPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private RouteManager routeManager;
    private JPanel panel_1;
    private JPanel panel_2;
    private JPanel panel_3;
    private JPanel panel_4;
    private JPanel panel_5;
    private JPanel panel_6;
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

    public SARPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {100, 0};
        gridBagLayout.rowHeights = new int[]{20, 16, 16, 16, 16, 16, 16, 10};
        gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
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
        
        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.insets = new Insets(0, 0, 5, 0);
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 1;
        add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[]{32, 28, 0};
        gbl_panel_1.rowHeights = new int[]{14, 0};
        gbl_panel_1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        panel_1.setLayout(gbl_panel_1);
        
        JLabel lblType = new JLabel("Type:");
        GridBagConstraints gbc_lblType = new GridBagConstraints();
        gbc_lblType.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblType.insets = new Insets(0, 0, 5, 0);
        gbc_lblType.gridx = 0;
        gbc_lblType.gridy = 0;
        panel_1.add(lblType, gbc_lblType);
        
        JLabel lblRapidResponse = new JLabel("Rapid Response");
        GridBagConstraints gbc_lblRapidResponse = new GridBagConstraints();
        gbc_lblRapidResponse.anchor = GridBagConstraints.WEST;
        gbc_lblRapidResponse.gridx = 1;
        gbc_lblRapidResponse.gridy = 0;
        panel_1.add(lblRapidResponse, gbc_lblRapidResponse);
        
        panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Date and Time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.insets = new Insets(0, 0, 5, 0);
        gbc_panel_2.fill = GridBagConstraints.BOTH;
        gbc_panel_2.gridx = 0;
        gbc_panel_2.gridy = 2;
        add(panel_2, gbc_panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] {32, 28, 0};
        gbl_panel_2.rowHeights = new int[]{14, 0, 0, 0};
        gbl_panel_2.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);
        
        lblTimeOfLast = new JLabel("Time of Last Known Position (LKP):");
        GridBagConstraints gbc_lblTimeOfLast = new GridBagConstraints();
        gbc_lblTimeOfLast.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTimeOfLast.insets = new Insets(0, 0, 5, 5);
        gbc_lblTimeOfLast.gridx = 0;
        gbc_lblTimeOfLast.gridy = 0;
        panel_2.add(lblTimeOfLast, gbc_lblTimeOfLast);
        
        label = new JLabel("08:00 02/07/2013");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.fill = GridBagConstraints.HORIZONTAL;
        gbc_label.insets = new Insets(0, 0, 5, 0);
        gbc_label.gridx = 1;
        gbc_label.gridy = 0;
        panel_2.add(label, gbc_label);
        
        lblNewLabel = new JLabel("Time of Commence Search Start:");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        panel_2.add(lblNewLabel, gbc_lblNewLabel);
        
        lblNewLabel_1 = new JLabel("10:30 02/07/2013");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridx = 1;
        gbc_lblNewLabel_1.gridy = 1;
        panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);
        
        lblTimeElasped = new JLabel("Time elasped:");
        GridBagConstraints gbc_lblTimeElasped = new GridBagConstraints();
        gbc_lblTimeElasped.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTimeElasped.insets = new Insets(0, 0, 0, 5);
        gbc_lblTimeElasped.gridx = 0;
        gbc_lblTimeElasped.gridy = 2;
        panel_2.add(lblTimeElasped, gbc_lblTimeElasped);
        
        lblHours = new JLabel("2 hours 30 minutes");
        GridBagConstraints gbc_lblHours = new GridBagConstraints();
        gbc_lblHours.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblHours.gridx = 1;
        gbc_lblHours.gridy = 2;
        panel_2.add(lblHours, gbc_lblHours);
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, "Last Surface Drifts dw continuation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.insets = new Insets(0, 0, 5, 0);
        gbc_panel_3.fill = GridBagConstraints.BOTH;
        gbc_panel_3.gridx = 0;
        gbc_panel_3.gridy = 3;
        add(panel_3, gbc_panel_3);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[] {32, 28, 0};
        gbl_panel_3.rowHeights = new int[] {14, 0, 0, 0};
        gbl_panel_3.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_panel_3.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        panel_3.setLayout(gbl_panel_3);
        
        lblDirection = new JLabel("Direction:");
        GridBagConstraints gbc_lblDirection = new GridBagConstraints();
        gbc_lblDirection.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblDirection.insets = new Insets(0, 0, 5, 5);
        gbc_lblDirection.gridx = 0;
        gbc_lblDirection.gridy = 0;
        panel_3.add(lblDirection, gbc_lblDirection);
        
        label_1 = new JLabel("180°");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.WEST;
        gbc_label_1.insets = new Insets(0, 0, 5, 0);
        gbc_label_1.gridx = 1;
        gbc_label_1.gridy = 0;
        panel_3.add(label_1, gbc_label_1);
        
        lblSpeed = new JLabel("Speed:");
        GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
        gbc_lblSpeed.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSpeed.insets = new Insets(0, 0, 0, 5);
        gbc_lblSpeed.gridx = 0;
        gbc_lblSpeed.gridy = 1;
        panel_3.add(lblSpeed, gbc_lblSpeed);
        
        lblKt = new JLabel("1.8 kt");
        GridBagConstraints gbc_lblKt = new GridBagConstraints();
        gbc_lblKt.anchor = GridBagConstraints.WEST;
        gbc_lblKt.gridx = 1;
        gbc_lblKt.gridy = 1;
        panel_3.add(lblKt, gbc_lblKt);
        
        panel_4 = new JPanel();
        panel_4.setBorder(new TitledBorder(null, "Position of Datum to LKP", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.insets = new Insets(0, 0, 5, 0);
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.gridx = 0;
        gbc_panel_4.gridy = 4;
        add(panel_4, gbc_panel_4);
        GridBagLayout gbl_panel_4 = new GridBagLayout();
        gbl_panel_4.columnWidths = new int[] {0, 0, 0, 0, 0};
        gbl_panel_4.rowHeights = new int[] {0, 0};
        gbl_panel_4.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
        gbl_panel_4.rowWeights = new double[]{1.0, 1.0};
        panel_4.setLayout(gbl_panel_4);
        
        lblLatitude = new JLabel("Latitude");
        GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
        gbc_lblLatitude.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
        gbc_lblLatitude.gridx = 1;
        gbc_lblLatitude.gridy = 0;
        panel_4.add(lblLatitude, gbc_lblLatitude);
        
        lblLongitude = new JLabel("Longitude");
        GridBagConstraints gbc_lblLongitude = new GridBagConstraints();
        gbc_lblLongitude.insets = new Insets(0, 0, 5, 5);
        gbc_lblLongitude.gridx = 2;
        gbc_lblLongitude.gridy = 0;
        panel_4.add(lblLongitude, gbc_lblLongitude);
        
        lblRdv = new JLabel("RDV");
        GridBagConstraints gbc_lblRdv = new GridBagConstraints();
        gbc_lblRdv.insets = new Insets(0, 0, 5, 5);
        gbc_lblRdv.gridx = 3;
        gbc_lblRdv.gridy = 0;
        panel_4.add(lblRdv, gbc_lblRdv);
        
        lblRadius = new JLabel("Radius");
        GridBagConstraints gbc_lblRadius = new GridBagConstraints();
        gbc_lblRadius.insets = new Insets(0, 0, 5, 0);
        gbc_lblRadius.gridx = 4;
        gbc_lblRadius.gridy = 0;
        panel_4.add(lblRadius, gbc_lblRadius);
        
        lblDownwind = new JLabel("Downwind:");
        GridBagConstraints gbc_lblDownwind = new GridBagConstraints();
        gbc_lblDownwind.insets = new Insets(0, 0, 0, 5);
        gbc_lblDownwind.gridx = 0;
        gbc_lblDownwind.gridy = 1;
        panel_4.add(lblDownwind, gbc_lblDownwind);
        
        lblN = new JLabel("56°18,2 N\t");
        GridBagConstraints gbc_lblN = new GridBagConstraints();
        gbc_lblN.insets = new Insets(0, 0, 0, 5);
        gbc_lblN.gridx = 1;
        gbc_lblN.gridy = 1;
        panel_4.add(lblN, gbc_lblN);
        
        lblE = new JLabel("7°58,0 E\t");
        GridBagConstraints gbc_lblE = new GridBagConstraints();
        gbc_lblE.insets = new Insets(0, 0, 0, 5);
        gbc_lblE.gridx = 2;
        gbc_lblE.gridy = 1;
        panel_4.add(lblE, gbc_lblE);
        
        label_2 = new JLabel("4.41");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.insets = new Insets(0, 0, 0, 5);
        gbc_label_2.gridx = 3;
        gbc_label_2.gridy = 1;
        panel_4.add(label_2, gbc_label_2);
        
        label_3 = new JLabel("2.42");
        GridBagConstraints gbc_label_3 = new GridBagConstraints();
        gbc_label_3.gridx = 4;
        gbc_label_3.gridy = 1;
        panel_4.add(label_3, gbc_label_3);
        
        panel_5 = new JPanel();
        panel_5.setBorder(new TitledBorder(null, "Positions of the Search Area", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_5 = new GridBagConstraints();
        gbc_panel_5.insets = new Insets(0, 0, 5, 0);
        gbc_panel_5.fill = GridBagConstraints.BOTH;
        gbc_panel_5.gridx = 0;
        gbc_panel_5.gridy = 5;
        add(panel_5, gbc_panel_5);
        GridBagLayout gbl_panel_5 = new GridBagLayout();
        gbl_panel_5.columnWidths = new int[] {32, 28, 0};
        gbl_panel_5.rowHeights = new int[] {0, 14, 0, 0, 0, 0, 0};
        gbl_panel_5.columnWeights = new double[]{1.0, 1.0, 1.0};
        gbl_panel_5.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        panel_5.setLayout(gbl_panel_5);
        
        lblLatitude_1 = new JLabel("Latitude");
        GridBagConstraints gbc_lblLatitude_1 = new GridBagConstraints();
        gbc_lblLatitude_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblLatitude_1.gridx = 1;
        gbc_lblLatitude_1.gridy = 0;
        panel_5.add(lblLatitude_1, gbc_lblLatitude_1);
        
        lblLongitude_1 = new JLabel("Longitude");
        GridBagConstraints gbc_lblLongitude_1 = new GridBagConstraints();
        gbc_lblLongitude_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblLongitude_1.gridx = 2;
        gbc_lblLongitude_1.gridy = 0;
        panel_5.add(lblLongitude_1, gbc_lblLongitude_1);
        
        lblA = new JLabel("A");
        GridBagConstraints gbc_lblA = new GridBagConstraints();
        gbc_lblA.insets = new Insets(0, 0, 5, 5);
        gbc_lblA.gridx = 0;
        gbc_lblA.gridy = 1;
        panel_5.add(lblA, gbc_lblA);
        
        lblN_1 = new JLabel("56°20,6 N\t");
        GridBagConstraints gbc_lblN_1 = new GridBagConstraints();
        gbc_lblN_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_1.gridx = 1;
        gbc_lblN_1.gridy = 1;
        panel_5.add(lblN_1, gbc_lblN_1);
        
        lblE_1 = new JLabel("7°53,6 E\t");
        GridBagConstraints gbc_lblE_1 = new GridBagConstraints();
        gbc_lblE_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_1.gridx = 2;
        gbc_lblE_1.gridy = 1;
        panel_5.add(lblE_1, gbc_lblE_1);
        
        lblB = new JLabel("B");
        GridBagConstraints gbc_lblB = new GridBagConstraints();
        gbc_lblB.insets = new Insets(0, 0, 5, 5);
        gbc_lblB.gridx = 0;
        gbc_lblB.gridy = 2;
        panel_5.add(lblB, gbc_lblB);
        
        lblN_2 = new JLabel("56°20,6 N\t");
        GridBagConstraints gbc_lblN_2 = new GridBagConstraints();
        gbc_lblN_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_2.gridx = 1;
        gbc_lblN_2.gridy = 2;
        panel_5.add(lblN_2, gbc_lblN_2);
        
        lblE_2 = new JLabel("8°02,4 E\t");
        GridBagConstraints gbc_lblE_2 = new GridBagConstraints();
        gbc_lblE_2.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_2.gridx = 2;
        gbc_lblE_2.gridy = 2;
        panel_5.add(lblE_2, gbc_lblE_2);
        
        lblC = new JLabel("C");
        GridBagConstraints gbc_lblC = new GridBagConstraints();
        gbc_lblC.insets = new Insets(0, 0, 5, 5);
        gbc_lblC.gridx = 0;
        gbc_lblC.gridy = 3;
        panel_5.add(lblC, gbc_lblC);
        
        lblN_3 = new JLabel("56°15,8 N\t");
        GridBagConstraints gbc_lblN_3 = new GridBagConstraints();
        gbc_lblN_3.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_3.gridx = 1;
        gbc_lblN_3.gridy = 3;
        panel_5.add(lblN_3, gbc_lblN_3);
        
        lblE_3 = new JLabel("8°02,4 E\t");
        GridBagConstraints gbc_lblE_3 = new GridBagConstraints();
        gbc_lblE_3.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_3.gridx = 2;
        gbc_lblE_3.gridy = 3;
        panel_5.add(lblE_3, gbc_lblE_3);
        
        lblD = new JLabel("D");
        GridBagConstraints gbc_lblD = new GridBagConstraints();
        gbc_lblD.insets = new Insets(0, 0, 5, 5);
        gbc_lblD.gridx = 0;
        gbc_lblD.gridy = 4;
        panel_5.add(lblD, gbc_lblD);
        
        lblN_4 = new JLabel("56°15,8 N\t");
        GridBagConstraints gbc_lblN_4 = new GridBagConstraints();
        gbc_lblN_4.insets = new Insets(0, 0, 5, 5);
        gbc_lblN_4.gridx = 1;
        gbc_lblN_4.gridy = 4;
        panel_5.add(lblN_4, gbc_lblN_4);
        
        lblE_4 = new JLabel("7°53,6 E\t");
        GridBagConstraints gbc_lblE_4 = new GridBagConstraints();
        gbc_lblE_4.insets = new Insets(0, 0, 5, 0);
        gbc_lblE_4.gridx = 2;
        gbc_lblE_4.gridy = 4;
        panel_5.add(lblE_4, gbc_lblE_4);
        
        horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalStrut.gridx = 1;
        gbc_horizontalStrut.gridy = 5;
        panel_5.add(horizontalStrut, gbc_horizontalStrut);
        
        lblAreaSize = new JLabel("Area [nm²]");
        GridBagConstraints gbc_lblAreaSize = new GridBagConstraints();
        gbc_lblAreaSize.insets = new Insets(0, 0, 0, 5);
        gbc_lblAreaSize.gridx = 0;
        gbc_lblAreaSize.gridy = 6;
        panel_5.add(lblAreaSize, gbc_lblAreaSize);
        
        label_4 = new JLabel("23");
        GridBagConstraints gbc_label_4 = new GridBagConstraints();
        gbc_label_4.insets = new Insets(0, 0, 0, 5);
        gbc_label_4.gridx = 1;
        gbc_label_4.gridy = 6;
        panel_5.add(label_4, gbc_label_4);
        
        panel_6 = new JPanel();
        GridBagConstraints gbc_panel_6 = new GridBagConstraints();
        gbc_panel_6.fill = GridBagConstraints.BOTH;
        gbc_panel_6.gridx = 0;
        gbc_panel_6.gridy = 6;
        add(panel_6, gbc_panel_6);
        
        btnReopenCalculations = new JButton("Reopen Calculations");
        panel_6.add(btnReopenCalculations);
        
        btnEffortAllocation = new JButton("Effort Allocation");
        panel_6.add(btnEffortAllocation);
    }
    
    
    
    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }
}
