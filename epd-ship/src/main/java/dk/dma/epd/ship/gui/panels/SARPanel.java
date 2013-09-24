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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.voct.EffortAllocationWindow;
import dk.dma.epd.ship.service.voct.RapidResponseData;
import dk.dma.epd.ship.service.voct.VOCTManager;

/**
 * Active waypoint panel in sensor panel
 */
public class SARPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JPanel noSar;
    private JPanel sarStartedPanel;
    private JButton btnStartSar;

    private JPanel statusPanel;
    private JPanel timeAndDatePanel;
    private JPanel weatherPanel;
    private JPanel datumPanel;
    private JPanel searchAreaPanel;
    private JPanel buttonPanel;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    private JLabel lblTimeOfLast;
    private JLabel lkpDate;
    private JLabel lblNewLabel;
    private JLabel cssDateStart;
    private JLabel lblTimeElasped;
    private JLabel timeElapsed;
    private JLabel lblDirection;
    private JLabel rdvDirection;
    private JLabel lblSpeed;
    private JLabel rdvSpeed;
    private JLabel lblDownwind;
    private JLabel lblLatitude;
    private JLabel lblLongitude;
    private JLabel lblRdv;
    private JLabel lblRadius;
    private JLabel datumLat;
    private JLabel datumLon;
    private JLabel rdvDistance;
    private JLabel datumRadius;
    private JLabel lblA;
    private JLabel lblB;
    private JLabel lblC;
    private JLabel lblD;
    private JLabel lblAreaSize;
    private JLabel lblLatitude_1;
    private JLabel lblLongitude_1;
    private JLabel pointAlat;
    private JLabel pointAlon;
    private JLabel pointBlat;
    private JLabel pointBlon;
    private JLabel pointClat;
    private JLabel pointClon;
    private JLabel pointDlat;
    private JLabel pointDlon;
    private JLabel areaSize;
    private Component horizontalStrut;
    private JPanel effortAllocationPanel;
    private JLabel lblProbabilityOfDetection;
    private JLabel label_5;
    private JLabel lblEffectiveSearchArea;
    private JLabel lblNm;
    private JLabel lblSearchCraftGround;
    private JLabel lblKnhr;

    static final String SARPANEL = "SAR Panel";
    static final String NOSARPANEL = "NO Sar panel";

    private VOCTManager voctManager;

    public SARPanel() {

        setLayout(new CardLayout());

        initGui();
        initSarOperation();
    }

    private void initGui() {

        noSar = new JPanel();

        add(noSar, NOSARPANEL);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 100, 0 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 16, 16, 16, 16, 16, 0,
                10 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0,
                1.0, 1.0, Double.MIN_VALUE };
        noSar.setLayout(gridBagLayout);

        JLabel lblSAR = new JLabel("Search And Rescue");
        lblSAR.setHorizontalAlignment(SwingConstants.CENTER);
        lblSAR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_lblSAR = new GridBagConstraints();
        gbc_lblSAR.anchor = GridBagConstraints.NORTH;
        gbc_lblSAR.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSAR.insets = new Insets(0, 0, 5, 0);
        gbc_lblSAR.gridx = 0;
        gbc_lblSAR.gridy = 0;
        noSar.add(lblSAR, gbc_lblSAR);

        JLabel lblNoSearchAnd = new JLabel(
                "No Search and Rescue operation underway");
        GridBagConstraints gbc_lblNoSearchAnd = new GridBagConstraints();
        gbc_lblNoSearchAnd.insets = new Insets(0, 0, 5, 0);
        gbc_lblNoSearchAnd.gridx = 0;
        gbc_lblNoSearchAnd.gridy = 1;
        noSar.add(lblNoSearchAnd, gbc_lblNoSearchAnd);

        btnStartSar = new JButton("Start SAR");
        GridBagConstraints gbc_btnStartSar = new GridBagConstraints();
        gbc_btnStartSar.insets = new Insets(0, 0, 5, 0);
        gbc_btnStartSar.gridx = 0;
        gbc_btnStartSar.gridy = 2;
        noSar.add(btnStartSar, gbc_btnStartSar);

        btnStartSar.addActionListener(this);
    }

    private void initSarOperation() {
        sarStartedPanel = new JPanel();
        add(sarStartedPanel, SARPANEL);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 100, 0 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 16, 16, 16, 16, 16, 0,
                10 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 1.0, 1.0,
                1.0, 1.0, Double.MIN_VALUE };
        sarStartedPanel.setLayout(gridBagLayout);
        JLabel lblSAR = new JLabel("Search And Rescue");
        lblSAR.setHorizontalAlignment(SwingConstants.CENTER);
        lblSAR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_lblSAR = new GridBagConstraints();
        gbc_lblSAR.anchor = GridBagConstraints.NORTH;
        gbc_lblSAR.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSAR.insets = new Insets(0, 0, 5, 0);
        gbc_lblSAR.gridx = 0;
        gbc_lblSAR.gridy = 0;
        sarStartedPanel.add(lblSAR, gbc_lblSAR);

        statusPanel = new JPanel();
        statusPanel.setBorder(new TitledBorder(null, "Status",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_statusPanel = new GridBagConstraints();
        gbc_statusPanel.insets = new Insets(0, 0, 5, 0);
        gbc_statusPanel.fill = GridBagConstraints.BOTH;
        gbc_statusPanel.gridx = 0;
        gbc_statusPanel.gridy = 1;
        sarStartedPanel.add(statusPanel, gbc_statusPanel);
        GridBagLayout gbl_statusPanel = new GridBagLayout();
        gbl_statusPanel.columnWidths = new int[] { 32, 28, 0 };
        gbl_statusPanel.rowHeights = new int[] { 14, 0 };
        gbl_statusPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_statusPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
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
        timeAndDatePanel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"), "Date and Time",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_timeAndDatePanel = new GridBagConstraints();
        gbc_timeAndDatePanel.insets = new Insets(0, 0, 5, 0);
        gbc_timeAndDatePanel.fill = GridBagConstraints.BOTH;
        gbc_timeAndDatePanel.gridx = 0;
        gbc_timeAndDatePanel.gridy = 2;
        sarStartedPanel.add(timeAndDatePanel, gbc_timeAndDatePanel);
        GridBagLayout gbl_timeAndDatePanel = new GridBagLayout();
        gbl_timeAndDatePanel.columnWidths = new int[] { 32, 28, 0 };
        gbl_timeAndDatePanel.rowHeights = new int[] { 14, 0, 0, 0 };
        gbl_timeAndDatePanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_timeAndDatePanel.rowWeights = new double[] { 1.0, 1.0, 1.0,
                Double.MIN_VALUE };
        timeAndDatePanel.setLayout(gbl_timeAndDatePanel);

        lblTimeOfLast = new JLabel("Time of Last Known Position (LKP):");
        GridBagConstraints gbc_lblTimeOfLast = new GridBagConstraints();
        gbc_lblTimeOfLast.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTimeOfLast.insets = new Insets(0, 0, 5, 5);
        gbc_lblTimeOfLast.gridx = 0;
        gbc_lblTimeOfLast.gridy = 0;
        timeAndDatePanel.add(lblTimeOfLast, gbc_lblTimeOfLast);

        lkpDate = new JLabel("N/A");
        GridBagConstraints gbc_lkpDate = new GridBagConstraints();
        gbc_lkpDate.fill = GridBagConstraints.HORIZONTAL;
        gbc_lkpDate.insets = new Insets(0, 0, 5, 0);
        gbc_lkpDate.gridx = 1;
        gbc_lkpDate.gridy = 0;
        timeAndDatePanel.add(lkpDate, gbc_lkpDate);

        lblNewLabel = new JLabel("Time of Commence Search Start:");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 1;
        timeAndDatePanel.add(lblNewLabel, gbc_lblNewLabel);

        cssDateStart = new JLabel("N/A");
        GridBagConstraints gbc_cssDateStart = new GridBagConstraints();
        gbc_cssDateStart.fill = GridBagConstraints.HORIZONTAL;
        gbc_cssDateStart.insets = new Insets(0, 0, 5, 0);
        gbc_cssDateStart.gridx = 1;
        gbc_cssDateStart.gridy = 1;
        timeAndDatePanel.add(cssDateStart, gbc_cssDateStart);

        lblTimeElasped = new JLabel("Time elasped:");
        GridBagConstraints gbc_lblTimeElasped = new GridBagConstraints();
        gbc_lblTimeElasped.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTimeElasped.insets = new Insets(0, 0, 0, 5);
        gbc_lblTimeElasped.gridx = 0;
        gbc_lblTimeElasped.gridy = 2;
        timeAndDatePanel.add(lblTimeElasped, gbc_lblTimeElasped);

        timeElapsed = new JLabel("N/A");
        GridBagConstraints gbc_timeElapsed = new GridBagConstraints();
        gbc_timeElapsed.fill = GridBagConstraints.HORIZONTAL;
        gbc_timeElapsed.gridx = 1;
        gbc_timeElapsed.gridy = 2;
        timeAndDatePanel.add(timeElapsed, gbc_timeElapsed);

        weatherPanel = new JPanel();
        weatherPanel.setBorder(new TitledBorder(null,
                "Last Surface Drifts dw continuation", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_weatherPanel = new GridBagConstraints();
        gbc_weatherPanel.insets = new Insets(0, 0, 5, 0);
        gbc_weatherPanel.fill = GridBagConstraints.BOTH;
        gbc_weatherPanel.gridx = 0;
        gbc_weatherPanel.gridy = 3;
        sarStartedPanel.add(weatherPanel, gbc_weatherPanel);
        GridBagLayout gbl_weatherPanel = new GridBagLayout();
        gbl_weatherPanel.columnWidths = new int[] { 32, 28, 0 };
        gbl_weatherPanel.rowHeights = new int[] { 14, 0, 0, 0 };
        gbl_weatherPanel.columnWeights = new double[] { 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_weatherPanel.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        weatherPanel.setLayout(gbl_weatherPanel);

        lblDirection = new JLabel("Direction:");
        GridBagConstraints gbc_lblDirection = new GridBagConstraints();
        gbc_lblDirection.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblDirection.insets = new Insets(0, 0, 5, 5);
        gbc_lblDirection.gridx = 0;
        gbc_lblDirection.gridy = 0;
        weatherPanel.add(lblDirection, gbc_lblDirection);

        rdvDirection = new JLabel("N/A");
        GridBagConstraints gbc_rdvDirection = new GridBagConstraints();
        gbc_rdvDirection.anchor = GridBagConstraints.WEST;
        gbc_rdvDirection.insets = new Insets(0, 0, 5, 0);
        gbc_rdvDirection.gridx = 1;
        gbc_rdvDirection.gridy = 0;
        weatherPanel.add(rdvDirection, gbc_rdvDirection);

        lblSpeed = new JLabel("Speed:");
        GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
        gbc_lblSpeed.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSpeed.insets = new Insets(0, 0, 0, 5);
        gbc_lblSpeed.gridx = 0;
        gbc_lblSpeed.gridy = 1;
        weatherPanel.add(lblSpeed, gbc_lblSpeed);

        rdvSpeed = new JLabel("1.8 kt");
        GridBagConstraints gbc_rdvSpeed = new GridBagConstraints();
        gbc_rdvSpeed.anchor = GridBagConstraints.WEST;
        gbc_rdvSpeed.gridx = 1;
        gbc_rdvSpeed.gridy = 1;
        weatherPanel.add(rdvSpeed, gbc_rdvSpeed);

        datumPanel = new JPanel();
        datumPanel.setBorder(new TitledBorder(null, "Position of Datum to LKP",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_datumPanel = new GridBagConstraints();
        gbc_datumPanel.insets = new Insets(0, 0, 5, 0);
        gbc_datumPanel.fill = GridBagConstraints.BOTH;
        gbc_datumPanel.gridx = 0;
        gbc_datumPanel.gridy = 4;
        sarStartedPanel.add(datumPanel, gbc_datumPanel);
        GridBagLayout gbl_datumPanel = new GridBagLayout();
        gbl_datumPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_datumPanel.rowHeights = new int[] { 0, 0 };
        gbl_datumPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
        gbl_datumPanel.rowWeights = new double[] { 1.0, 1.0 };
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

        datumLat = new JLabel("N/A");
        GridBagConstraints gbc_datumLat = new GridBagConstraints();
        gbc_datumLat.insets = new Insets(0, 0, 0, 5);
        gbc_datumLat.gridx = 1;
        gbc_datumLat.gridy = 1;
        datumPanel.add(datumLat, gbc_datumLat);

        datumLon = new JLabel("N/A");
        GridBagConstraints gbc_datumLon = new GridBagConstraints();
        gbc_datumLon.insets = new Insets(0, 0, 0, 5);
        gbc_datumLon.gridx = 2;
        gbc_datumLon.gridy = 1;
        datumPanel.add(datumLon, gbc_datumLon);

        rdvDistance = new JLabel("N/A");
        GridBagConstraints gbc_rdvDistance = new GridBagConstraints();
        gbc_rdvDistance.insets = new Insets(0, 0, 0, 5);
        gbc_rdvDistance.gridx = 3;
        gbc_rdvDistance.gridy = 1;
        datumPanel.add(rdvDistance, gbc_rdvDistance);

        datumRadius = new JLabel("N/A");
        GridBagConstraints gbc_datumRadius = new GridBagConstraints();
        gbc_datumRadius.gridx = 4;
        gbc_datumRadius.gridy = 1;
        datumPanel.add(datumRadius, gbc_datumRadius);

        searchAreaPanel = new JPanel();
        searchAreaPanel.setBorder(new TitledBorder(null,
                "Positions of the Search Area", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_searchAreaPanel = new GridBagConstraints();
        gbc_searchAreaPanel.insets = new Insets(0, 0, 5, 0);
        gbc_searchAreaPanel.fill = GridBagConstraints.BOTH;
        gbc_searchAreaPanel.gridx = 0;
        gbc_searchAreaPanel.gridy = 5;
        sarStartedPanel.add(searchAreaPanel, gbc_searchAreaPanel);
        GridBagLayout gbl_searchAreaPanel = new GridBagLayout();
        gbl_searchAreaPanel.columnWidths = new int[] { 32, 28, 0 };
        gbl_searchAreaPanel.rowHeights = new int[] { 0, 14, 0, 0, 0, 0, 0 };
        gbl_searchAreaPanel.columnWeights = new double[] { 1.0, 1.0, 1.0 };
        gbl_searchAreaPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0,
                1.0, 1.0, 1.0 };
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

        pointAlat = new JLabel("N/A");
        GridBagConstraints gbc_pointAlat = new GridBagConstraints();
        gbc_pointAlat.insets = new Insets(0, 0, 5, 5);
        gbc_pointAlat.gridx = 1;
        gbc_pointAlat.gridy = 1;
        searchAreaPanel.add(pointAlat, gbc_pointAlat);

        pointAlon = new JLabel("N/A");
        GridBagConstraints gbc_pointAlon = new GridBagConstraints();
        gbc_pointAlon.insets = new Insets(0, 0, 5, 0);
        gbc_pointAlon.gridx = 2;
        gbc_pointAlon.gridy = 1;
        searchAreaPanel.add(pointAlon, gbc_pointAlon);

        lblB = new JLabel("B");
        GridBagConstraints gbc_lblB = new GridBagConstraints();
        gbc_lblB.insets = new Insets(0, 0, 5, 5);
        gbc_lblB.gridx = 0;
        gbc_lblB.gridy = 2;
        searchAreaPanel.add(lblB, gbc_lblB);

        pointBlat = new JLabel("N/A");
        GridBagConstraints gbc_pointBlat = new GridBagConstraints();
        gbc_pointBlat.insets = new Insets(0, 0, 5, 5);
        gbc_pointBlat.gridx = 1;
        gbc_pointBlat.gridy = 2;
        searchAreaPanel.add(pointBlat, gbc_pointBlat);

        pointBlon = new JLabel("N/A");
        GridBagConstraints gbc_pointBlon = new GridBagConstraints();
        gbc_pointBlon.insets = new Insets(0, 0, 5, 0);
        gbc_pointBlon.gridx = 2;
        gbc_pointBlon.gridy = 2;
        searchAreaPanel.add(pointBlon, gbc_pointBlon);

        lblC = new JLabel("C");
        GridBagConstraints gbc_lblC = new GridBagConstraints();
        gbc_lblC.insets = new Insets(0, 0, 5, 5);
        gbc_lblC.gridx = 0;
        gbc_lblC.gridy = 3;
        searchAreaPanel.add(lblC, gbc_lblC);

        pointClat = new JLabel("N/A");
        GridBagConstraints gbc_pointClat = new GridBagConstraints();
        gbc_pointClat.insets = new Insets(0, 0, 5, 5);
        gbc_pointClat.gridx = 1;
        gbc_pointClat.gridy = 3;
        searchAreaPanel.add(pointClat, gbc_pointClat);

        pointClon = new JLabel("N/A");
        GridBagConstraints gbc_pointClon = new GridBagConstraints();
        gbc_pointClon.insets = new Insets(0, 0, 5, 0);
        gbc_pointClon.gridx = 2;
        gbc_pointClon.gridy = 3;
        searchAreaPanel.add(pointClon, gbc_pointClon);

        lblD = new JLabel("D");
        GridBagConstraints gbc_lblD = new GridBagConstraints();
        gbc_lblD.insets = new Insets(0, 0, 5, 5);
        gbc_lblD.gridx = 0;
        gbc_lblD.gridy = 4;
        searchAreaPanel.add(lblD, gbc_lblD);

        pointDlat = new JLabel("N/A");
        GridBagConstraints gbc_pointDlat = new GridBagConstraints();
        gbc_pointDlat.insets = new Insets(0, 0, 5, 5);
        gbc_pointDlat.gridx = 1;
        gbc_pointDlat.gridy = 4;
        searchAreaPanel.add(pointDlat, gbc_pointDlat);

        pointDlon = new JLabel("N/A");
        GridBagConstraints gbc_pointDlon = new GridBagConstraints();
        gbc_pointDlon.insets = new Insets(0, 0, 5, 0);
        gbc_pointDlon.gridx = 2;
        gbc_pointDlon.gridy = 4;
        searchAreaPanel.add(pointDlon, gbc_pointDlon);

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

        areaSize = new JLabel("N/A");
        GridBagConstraints gbc_areaSize = new GridBagConstraints();
        gbc_areaSize.insets = new Insets(0, 0, 0, 5);
        gbc_areaSize.gridx = 1;
        gbc_areaSize.gridy = 6;
        searchAreaPanel.add(areaSize, gbc_areaSize);

        buttonPanel = new JPanel();
        GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
        gbc_buttonPanel.insets = new Insets(0, 0, 5, 0);
        gbc_buttonPanel.fill = GridBagConstraints.BOTH;
        gbc_buttonPanel.gridx = 0;
        gbc_buttonPanel.gridy = 6;
        sarStartedPanel.add(buttonPanel, gbc_buttonPanel);

        btnReopenCalculations = new JButton("Reopen Calculations");
        btnReopenCalculations.addActionListener(this);
        buttonPanel.add(btnReopenCalculations);

        btnEffortAllocation = new JButton("Effort Allocation");
        btnEffortAllocation.addActionListener(this);
        buttonPanel.add(btnEffortAllocation);

        effortAllocationPanel = new JPanel();
        effortAllocationPanel.setBorder(new TitledBorder(null,
                "Effort Allocation", TitledBorder.LEADING, TitledBorder.TOP,
                null, null));
        GridBagConstraints gbc_effortAllocationPanel = new GridBagConstraints();
        gbc_effortAllocationPanel.fill = GridBagConstraints.BOTH;
        gbc_effortAllocationPanel.gridx = 0;
        gbc_effortAllocationPanel.gridy = 7;
        sarStartedPanel.add(effortAllocationPanel, gbc_effortAllocationPanel);
        GridBagLayout gbl_effortAllocationPanel = new GridBagLayout();
        gbl_effortAllocationPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_effortAllocationPanel.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_effortAllocationPanel.columnWeights = new double[] { 1.0, 1.0,
                Double.MIN_VALUE };
        gbl_effortAllocationPanel.rowWeights = new double[] { 1.0, 1.0, 1.0,
                Double.MIN_VALUE };
        effortAllocationPanel.setLayout(gbl_effortAllocationPanel);

        lblProbabilityOfDetection = new JLabel("Probability of Detection:");
        GridBagConstraints gbc_lblProbabilityOfDetection = new GridBagConstraints();
        gbc_lblProbabilityOfDetection.insets = new Insets(0, 0, 5, 5);
        gbc_lblProbabilityOfDetection.gridx = 0;
        gbc_lblProbabilityOfDetection.gridy = 0;
        effortAllocationPanel.add(lblProbabilityOfDetection,
                gbc_lblProbabilityOfDetection);

        label_5 = new JLabel("79%");
        GridBagConstraints gbc_label_5 = new GridBagConstraints();
        gbc_label_5.insets = new Insets(0, 0, 5, 0);
        gbc_label_5.gridx = 1;
        gbc_label_5.gridy = 0;
        effortAllocationPanel.add(label_5, gbc_label_5);

        lblEffectiveSearchArea = new JLabel("Effective Search Area:");
        GridBagConstraints gbc_lblEffectiveSearchArea = new GridBagConstraints();
        gbc_lblEffectiveSearchArea.insets = new Insets(0, 0, 5, 5);
        gbc_lblEffectiveSearchArea.gridx = 0;
        gbc_lblEffectiveSearchArea.gridy = 1;
        effortAllocationPanel.add(lblEffectiveSearchArea,
                gbc_lblEffectiveSearchArea);

        lblNm = new JLabel("10 nm2");
        GridBagConstraints gbc_lblNm = new GridBagConstraints();
        gbc_lblNm.insets = new Insets(0, 0, 5, 0);
        gbc_lblNm.gridx = 1;
        gbc_lblNm.gridy = 1;
        effortAllocationPanel.add(lblNm, gbc_lblNm);

        lblSearchCraftGround = new JLabel("Search Craft Ground Speed:");
        GridBagConstraints gbc_lblSearchCraftGround = new GridBagConstraints();
        gbc_lblSearchCraftGround.insets = new Insets(0, 0, 0, 5);
        gbc_lblSearchCraftGround.gridx = 0;
        gbc_lblSearchCraftGround.gridy = 2;
        effortAllocationPanel.add(lblSearchCraftGround,
                gbc_lblSearchCraftGround);

        lblKnhr = new JLabel("15 kn/h");
        GridBagConstraints gbc_lblKnhr = new GridBagConstraints();
        gbc_lblKnhr.gridx = 1;
        gbc_lblKnhr.gridy = 2;
        effortAllocationPanel.add(lblKnhr, gbc_lblKnhr);
    }

    /**
     * @param voctManager
     *            the voctManager to set
     */
    public void setVoctManager(VOCTManager voctManager) {
        this.voctManager = voctManager;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == btnStartSar || arg0.getSource() == btnReopenCalculations) {

            if (voctManager != null) {

                voctManager.showSarInput();

            }
        }
        
        if (arg0.getSource() == btnEffortAllocation) {

            //We have a SAR in progress
            if (voctManager != null && voctManager.isHasSar()) {

                //Determine what type of SAR then retrieve the input data
                EffortAllocationWindow window = new EffortAllocationWindow(voctManager.getRapidResponseData());
                window.setVisible(true);
            }
        }
        
    }

    public void sarComplete(RapidResponseData data) {
        setRapidResponseData(data);
        CardLayout cl = (CardLayout) (this.getLayout());
        cl.show(this, SARPANEL);
    }

    public void sarCancel() {
        CardLayout cl = (CardLayout) (this.getLayout());
        cl.show(this, NOSARPANEL);
    }

    private void setRapidResponseData(RapidResponseData data) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH':'mm '-' dd'/'MM");
        
        lkpDate.setText(fmt.print(data.getLKPDate()));
        cssDateStart.setText(fmt.print(data.getCSSDate()));
        timeElapsed.setText(Formatter.formatHours(data.getTimeElasped()) + "");
        rdvDirection.setText(Formatter.formatDouble(data.getRdvDirection(), 2) + "°");
        rdvSpeed.setText(Formatter.formatDouble(data.getRdvSpeed(), 2) + "kn/h");
        datumLat.setText(data.getDatum().getLatitudeAsString());
        datumLon.setText(data.getDatum().getLongitudeAsString());
        rdvDistance.setText(Formatter.formatDouble(data.getRdvDistance(), 2) + " nm");
        datumRadius.setText(Formatter.formatDouble(data.getRadius(), 2) + " nm");
        
        pointAlat.setText(data.getA().getLatitudeAsString());
        pointAlon.setText(data.getA().getLongitudeAsString());
        pointBlat.setText(data.getB().getLatitudeAsString());
        pointBlon.setText(data.getB().getLongitudeAsString());
        pointClat.setText(data.getC().getLatitudeAsString());
        pointClon.setText(data.getC().getLongitudeAsString());
        pointDlat.setText(data.getD().getLatitudeAsString());
        pointDlon.setText(data.getD().getLongitudeAsString());

        areaSize.setText(Formatter.formatDouble(data.getRadius()*2* data.getRadius()*2, 2) + " ");
    }

}
