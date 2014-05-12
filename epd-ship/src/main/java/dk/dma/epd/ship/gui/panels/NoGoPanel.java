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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * GPS panel in sensor panel
 */
public class NoGoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel nogoTitleLabelSlider = new JLabel("NoGo");
    private JLabel statusTitleLabelSlider = new JLabel("Status");
    private JLabel statusLabelSlider = new JLabel("N/A");
    private final JLabel validToTxtLabelSlider = new JLabel("N/A");
    private final JLabel validFromTxtLabelSlider = new JLabel("N/A");
    private final JLabel draughtTxtLabelSlider = new JLabel("N/A");
    private final JLabel additionalTxtTitleLabelSlider = new JLabel("N/A");
    private final JLabel validFromLabelTitleSlider = new JLabel("Valid From");
    private final JLabel validToLabelTitleSlider = new JLabel("Valid to");
    private final JLabel draughtLabelTitleSlider = new JLabel("Draught");
    private final JLabel additionalTxtTitleLabel2Slider = new JLabel("N/A");

    private JLabel nogoTitleLabel = new JLabel("NoGo");
    private JLabel statusTitleLabel = new JLabel("Status");
    private JLabel statusLabel = new JLabel("N/A");
    private final JLabel validToTxtLabel = new JLabel("N/A");
    private final JLabel validFromTxtLabel = new JLabel("N/A");
    private final JLabel draughtTxtLabel = new JLabel("N/A");
    private final JLabel additionalTxtTitleLabel = new JLabel("N/A");
    private final JLabel validFromLabelTitle = new JLabel("Valid From");
    private final JLabel validToLabelTitle = new JLabel("Valid to");
    private final JLabel draughtLabelTitle = new JLabel("Draught");
    private final JLabel additionalTxtTitleLabel2 = new JLabel("N/A");

    private JPanel singleNoGoPanel;
    private JPanel multipleNoGoPanel;

    private JSlider slider;

    private DecimalFormat df = new DecimalFormat("#.#");

    // private final JLabel label = new JLabel("NoGo");

    public NoGoPanel() {

        FlowLayout flowLayout = (FlowLayout) getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        singleNoGoPanel = new JPanel();
        multipleNoGoPanel = new JPanel();

        // this.add(singleNoGoPanel);
        // this.add(multipleNoGoPanel);

        createSingleRequestPanel();
        createSliderRequestPanel();

        activateSinglePanel();
        // activateSliderPanel();
    }

    public void activateSliderPanel() {
        this.remove(singleNoGoPanel);
        this.add(multipleNoGoPanel);
    }

    public void activateSinglePanel() {
        this.add(singleNoGoPanel);
        this.remove(multipleNoGoPanel);
    }

    private void createSliderRequestPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 125, 153 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 15, 0, 0, 0, 0, 0, 10 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        multipleNoGoPanel.setLayout(gridBagLayout);
        nogoTitleLabelSlider.setHorizontalAlignment(SwingConstants.CENTER);
        nogoTitleLabelSlider.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_nogoTitleLabel = new GridBagConstraints();
        gbc_nogoTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_nogoTitleLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_nogoTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_nogoTitleLabel.gridwidth = 2;
        gbc_nogoTitleLabel.gridx = 0;
        gbc_nogoTitleLabel.gridy = 0;
        multipleNoGoPanel.add(nogoTitleLabelSlider, gbc_nogoTitleLabel);

        statusTitleLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        statusTitleLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusTitleLabel = new GridBagConstraints();
        gbc_statusTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_statusTitleLabel.gridx = 0;
        gbc_statusTitleLabel.gridy = 1;
        multipleNoGoPanel.add(statusTitleLabelSlider, gbc_statusTitleLabel);

        statusLabelSlider.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusLabel = new GridBagConstraints();
        gbc_statusLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusLabel.insets = new Insets(0, 0, 5, 0);
        gbc_statusLabel.gridx = 1;
        gbc_statusLabel.gridy = 1;
        multipleNoGoPanel.add(statusLabelSlider, gbc_statusLabel);

        validFromLabelTitleSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validFromLabelTitleSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_validFromLabelTitle = new GridBagConstraints();
        gbc_validFromLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validFromLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validFromLabelTitle.gridx = 0;
        gbc_validFromLabelTitle.gridy = 2;
        multipleNoGoPanel.add(validFromLabelTitleSlider, gbc_validFromLabelTitle);

        validFromTxtLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validFromTxtLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel1 = new GridBagConstraints();
        gbc_statLabel1.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel1.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel1.gridx = 1;
        gbc_statLabel1.gridy = 2;
        multipleNoGoPanel.add(validFromTxtLabelSlider, gbc_statLabel1);

        GridBagConstraints gbc_validToLabelTitle = new GridBagConstraints();
        gbc_validToLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validToLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validToLabelTitle.gridx = 0;
        gbc_validToLabelTitle.gridy = 3;
        multipleNoGoPanel.add(validToLabelTitleSlider, gbc_validToLabelTitle);
        validToLabelTitleSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validToLabelTitleSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        validToTxtLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validToTxtLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel2 = new GridBagConstraints();
        gbc_statLabel2.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel2.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel2.gridx = 1;
        gbc_statLabel2.gridy = 3;
        multipleNoGoPanel.add(validToTxtLabelSlider, gbc_statLabel2);

        GridBagConstraints gbc_draughtLabelTitle = new GridBagConstraints();
        gbc_draughtLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_draughtLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_draughtLabelTitle.gridx = 0;
        gbc_draughtLabelTitle.gridy = 4;
        multipleNoGoPanel.add(draughtLabelTitleSlider, gbc_draughtLabelTitle);
        draughtLabelTitleSlider.setHorizontalAlignment(SwingConstants.LEFT);
        draughtLabelTitleSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        draughtTxtLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        draughtTxtLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel3 = new GridBagConstraints();
        gbc_statLabel3.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel3.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel3.gridx = 1;
        gbc_statLabel3.gridy = 4;
        multipleNoGoPanel.add(draughtTxtLabelSlider, gbc_statLabel3);

        additionalTxtTitleLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabelSlider.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel = new GridBagConstraints();
        gbc_additionalTxtTitleLabel.gridwidth = 2;
        gbc_additionalTxtTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_additionalTxtTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_additionalTxtTitleLabel.gridx = 0;
        gbc_additionalTxtTitleLabel.gridy = 5;
        multipleNoGoPanel.add(additionalTxtTitleLabelSlider, gbc_additionalTxtTitleLabel);

        additionalTxtTitleLabel2Slider.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabel2Slider.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel2 = new GridBagConstraints();
        gbc_additionalTxtTitleLabel2.insets = new Insets(0, 0, 5, 0);
        gbc_additionalTxtTitleLabel2.anchor = GridBagConstraints.WEST;
        gbc_additionalTxtTitleLabel2.gridwidth = 2;
        gbc_additionalTxtTitleLabel2.gridx = 0;
        gbc_additionalTxtTitleLabel2.gridy = 6;
        multipleNoGoPanel.add(additionalTxtTitleLabel2Slider, gbc_additionalTxtTitleLabel2);

        slider = new JSlider(JSlider.HORIZONTAL, 0, 60, 0);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(10);
        // slider.addChangeListener(this);
        slider.setEnabled(false);

        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.NORTHWEST;
        gbc_label.insets = new Insets(0, 0, 5, 0);
        gbc_label.gridwidth = 2;
        gbc_label.gridx = 0;
        gbc_label.gridy = 7;
        // multipleNoGoPanel.add(slider, gbc_label);

        // GridBagConstraints gbc_label = new GridBagConstraints();
        // gbc_label.insets = new Insets(0, 0, 0, 5);
        // gbc_label.gridx = 0;
        // gbc_label.gridy = 7;
        // test.setHorizontalAlignment(SwingConstants.CENTER);
        // test.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // multipleNoGoPanel.add(test, gbc_label);
    }

    private void createSingleRequestPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 125, 153 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 15, 0, 0, 0, 0, 0, 10 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        singleNoGoPanel.setLayout(gridBagLayout);
        nogoTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nogoTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_nogoTitleLabel = new GridBagConstraints();
        gbc_nogoTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_nogoTitleLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_nogoTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_nogoTitleLabel.gridwidth = 2;
        gbc_nogoTitleLabel.gridx = 0;
        gbc_nogoTitleLabel.gridy = 0;
        singleNoGoPanel.add(nogoTitleLabel, gbc_nogoTitleLabel);

        statusTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusTitleLabel = new GridBagConstraints();
        gbc_statusTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_statusTitleLabel.gridx = 0;
        gbc_statusTitleLabel.gridy = 1;
        singleNoGoPanel.add(statusTitleLabel, gbc_statusTitleLabel);

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusLabel = new GridBagConstraints();
        gbc_statusLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusLabel.insets = new Insets(0, 0, 5, 0);
        gbc_statusLabel.gridx = 1;
        gbc_statusLabel.gridy = 1;
        singleNoGoPanel.add(statusLabel, gbc_statusLabel);

        validFromLabelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        validFromLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_validFromLabelTitle = new GridBagConstraints();
        gbc_validFromLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validFromLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validFromLabelTitle.gridx = 0;
        gbc_validFromLabelTitle.gridy = 2;
        singleNoGoPanel.add(validFromLabelTitle, gbc_validFromLabelTitle);

        validFromTxtLabel.setHorizontalAlignment(SwingConstants.LEFT);
        validFromTxtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel1 = new GridBagConstraints();
        gbc_statLabel1.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel1.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel1.gridx = 1;
        gbc_statLabel1.gridy = 2;
        singleNoGoPanel.add(validFromTxtLabel, gbc_statLabel1);

        GridBagConstraints gbc_validToLabelTitle = new GridBagConstraints();
        gbc_validToLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validToLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validToLabelTitle.gridx = 0;
        gbc_validToLabelTitle.gridy = 3;
        singleNoGoPanel.add(validToLabelTitle, gbc_validToLabelTitle);
        validToLabelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        validToLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        validToTxtLabel.setHorizontalAlignment(SwingConstants.LEFT);
        validToTxtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel2 = new GridBagConstraints();
        gbc_statLabel2.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel2.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel2.gridx = 1;
        gbc_statLabel2.gridy = 3;
        singleNoGoPanel.add(validToTxtLabel, gbc_statLabel2);

        GridBagConstraints gbc_draughtLabelTitle = new GridBagConstraints();
        gbc_draughtLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_draughtLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_draughtLabelTitle.gridx = 0;
        gbc_draughtLabelTitle.gridy = 4;
        singleNoGoPanel.add(draughtLabelTitle, gbc_draughtLabelTitle);
        draughtLabelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        draughtLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        draughtTxtLabel.setHorizontalAlignment(SwingConstants.LEFT);
        draughtTxtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel3 = new GridBagConstraints();
        gbc_statLabel3.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel3.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel3.gridx = 1;
        gbc_statLabel3.gridy = 4;
        singleNoGoPanel.add(draughtTxtLabel, gbc_statLabel3);

        additionalTxtTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel = new GridBagConstraints();
        gbc_additionalTxtTitleLabel.gridwidth = 2;
        gbc_additionalTxtTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_additionalTxtTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_additionalTxtTitleLabel.gridx = 0;
        gbc_additionalTxtTitleLabel.gridy = 5;
        singleNoGoPanel.add(additionalTxtTitleLabel, gbc_additionalTxtTitleLabel);

        additionalTxtTitleLabel2.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel2 = new GridBagConstraints();
        gbc_additionalTxtTitleLabel2.gridwidth = 2;
        gbc_additionalTxtTitleLabel2.gridx = 0;
        gbc_additionalTxtTitleLabel2.gridy = 6;
        singleNoGoPanel.add(additionalTxtTitleLabel2, gbc_additionalTxtTitleLabel2);
    }

    public void initLabels() {

        statusLabel.setText("Inactive");
        statusLabelSlider.setText("Inactive");

        statusLabel.setEnabled(false);
        statusLabelSlider.setEnabled(false);

        // Valid from
        validFromTxtLabel.setEnabled(false);
        validFromTxtLabelSlider.setEnabled(false);

        // Valid to
        validToTxtLabel.setEnabled(false);
        validToTxtLabelSlider.setEnabled(false);

        // Draught
        draughtTxtLabel.setEnabled(false);
        draughtTxtLabelSlider.setEnabled(false);

        // Additional txt
        additionalTxtTitleLabel.setEnabled(false);
        additionalTxtTitleLabelSlider.setEnabled(false);

        additionalTxtTitleLabel2.setEnabled(false);
        additionalTxtTitleLabel2Slider.setEnabled(false);

        additionalTxtTitleLabel.setText("");
        additionalTxtTitleLabelSlider.setText("");

        additionalTxtTitleLabel2.setText("");
        additionalTxtTitleLabel2Slider.setText("");
    }

    public void newRequestSingle() {
        statusLabel.setEnabled(true);
        validFromTxtLabel.setEnabled(true);
        validToTxtLabel.setEnabled(true);
        draughtTxtLabel.setEnabled(true);
        additionalTxtTitleLabel.setEnabled(true);
        additionalTxtTitleLabel2.setEnabled(true);

        statusLabel.setText("Connecting...");
        statusLabel.setForeground(Color.GREEN);
        validFromTxtLabel.setText("N/A");
        validToTxtLabel.setText("N/A");
        draughtTxtLabel.setText("N/A");

        additionalTxtTitleLabel.setText("Requesting NoGo");
        additionalTxtTitleLabel2.setText("Please standby");
    }

    public void newRequestMultiple() {
        statusLabelSlider.setEnabled(true);
        validFromTxtLabelSlider.setEnabled(true);
        validToTxtLabelSlider.setEnabled(true);
        draughtTxtLabelSlider.setEnabled(true);
        additionalTxtTitleLabelSlider.setEnabled(true);
        additionalTxtTitleLabel2Slider.setEnabled(true);

        statusLabelSlider.setText("Connecting...");
        statusLabelSlider.setForeground(Color.GREEN);
        validFromTxtLabelSlider.setText("N/A");
        validToTxtLabelSlider.setText("N/A");
        draughtTxtLabelSlider.setText("N/A");

        additionalTxtTitleLabelSlider.setText("Requesting NoGo");
        additionalTxtTitleLabel2Slider.setText("Please standby");
    }

    public void singleRequestFailed() {
        statusLabel.setText("Failed");
        statusLabel.setForeground(Color.RED);
        additionalTxtTitleLabel.setText("An error occured retrieving NoGo");
        additionalTxtTitleLabel2.setText("Try again in a few minutes");

        validFromTxtLabel.setEnabled(false);
        validToTxtLabel.setEnabled(false);
        draughtTxtLabel.setEnabled(false);
    }

    public void multipleRequestFailed() {
        statusLabelSlider.setText("Failed");
        statusLabelSlider.setForeground(Color.RED);
        additionalTxtTitleLabelSlider.setText("An error occured retrieving NoGo");
        additionalTxtTitleLabel2Slider.setText("Try again in a few minutes");

        validFromTxtLabelSlider.setEnabled(false);
        validToTxtLabelSlider.setEnabled(false);
        draughtTxtLabelSlider.setEnabled(false);
    }

    public void noConnectionSingle() {
        statusLabel.setText("Failed");
        statusLabel.setForeground(Color.RED);
        additionalTxtTitleLabel.setText("No network connection");
        additionalTxtTitleLabel2.setText("Reestablish network and try again");

        validFromTxtLabel.setEnabled(false);
        validToTxtLabel.setEnabled(false);
        draughtTxtLabel.setEnabled(false);
    }

    public void noConnectionMultiple() {
        statusLabelSlider.setText("Failed");
        statusLabelSlider.setForeground(Color.RED);
        additionalTxtTitleLabelSlider.setText("No network connection");
        additionalTxtTitleLabel2Slider.setText("Reestablish network and try again");

        validFromTxtLabelSlider.setEnabled(false);
        validToTxtLabelSlider.setEnabled(false);
        draughtTxtLabelSlider.setEnabled(false);
    }

    public void requestCompletedSingle(int errorCodeOwn, List<NogoPolygon> polygonsOwn, Date validFrom, Date validTo, Double draught) {
        draught = -draught;

        // int draughtInt = (int) Math.round(draught);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM , HH:mm");

        String validFromStr = "";
        String validToStr = "";

        if (validFrom != null) {
            validFromStr = sdf.format(validFrom);
            validToStr = sdf.format(validTo);
        }

        if (errorCodeOwn == 17) {
            statusLabel.setText("Failed");
            statusLabel.setForeground(Color.RED);
            additionalTxtTitleLabel.setText("No data for region");

            validFromTxtLabel.setEnabled(false);
            validToTxtLabel.setEnabled(false);
            draughtTxtLabel.setEnabled(false);

            additionalTxtTitleLabel2.setText("");
            return;
        }

        if (errorCodeOwn == 18) {
            statusLabel.setText("Limited");
            statusLabel.setForeground(Color.ORANGE);
            additionalTxtTitleLabel.setText("No tide data available for region");
            additionalTxtTitleLabel2.setText("");
            validFromTxtLabel.setText("N/A");
            validToTxtLabel.setText("N/A");
            draughtTxtLabel.setText(df.format(draught) + " meters");
            return;
        }
        if (polygonsOwn.size() == 0) {
            statusLabel.setText("Success");
            statusLabel.setForeground(Color.GREEN);
            validFromTxtLabel.setText(validFromStr);
            validToTxtLabel.setText(validToStr);
            draughtTxtLabel.setText(df.format(draught) + " meters");
            additionalTxtTitleLabel.setText("Entire region is Go");
            additionalTxtTitleLabel2.setText("");

            validFromTxtLabel.setEnabled(true);
            validToTxtLabel.setEnabled(true);
            draughtTxtLabel.setEnabled(true);
            return;

        }
        if (errorCodeOwn == 0) {
            statusLabel.setText("Success");
            statusLabel.setForeground(Color.GREEN);
            validFromTxtLabel.setText(validFromStr);
            validToTxtLabel.setText(validToStr);

            draughtTxtLabel.setText(df.format(draught) + " meters");
            additionalTxtTitleLabel.setText("");
            additionalTxtTitleLabel2.setText("");

            validFromTxtLabel.setEnabled(true);
            validToTxtLabel.setEnabled(true);
            draughtTxtLabel.setEnabled(true);
            return;
        }

    }

    public void requestCompletedMultiple(int errorCodeOwn, List<NogoPolygon> polygonsOwn, Date validFrom, Date validTo,
            Double draught) {
        draught = -draught;

        // int draughtInt = (int) Math.round(draught);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM , HH:mm");

        String validFromStr = "";
        String validToStr = "";

        if (validFrom != null) {
            validFromStr = sdf.format(validFrom);
            validToStr = sdf.format(validTo);
        }

        if (errorCodeOwn == 17) {
            statusLabelSlider.setText("Failed");
            statusLabelSlider.setForeground(Color.RED);
            additionalTxtTitleLabelSlider.setText("No data for region");

            validFromTxtLabelSlider.setEnabled(false);
            validToTxtLabelSlider.setEnabled(false);
            draughtTxtLabelSlider.setEnabled(false);

            additionalTxtTitleLabel2Slider.setText("");
            return;
        }

        if (errorCodeOwn == 18) {
            statusLabelSlider.setText("Limited");
            statusLabelSlider.setForeground(Color.ORANGE);
            additionalTxtTitleLabelSlider.setText("No tide data available for region");
            additionalTxtTitleLabel2Slider.setText("");
            validFromTxtLabelSlider.setText("N/A");
            validToTxtLabelSlider.setText("N/A");
            draughtTxtLabelSlider.setText(df.format(draught) + " meters");
            return;
        }
        if (polygonsOwn.size() == 0) {
            statusLabelSlider.setText("Success");
            statusLabelSlider.setForeground(Color.GREEN);
            validFromTxtLabelSlider.setText(validFromStr);
            validToTxtLabelSlider.setText(validToStr);
            draughtTxtLabelSlider.setText(df.format(draught) + " meters");
            additionalTxtTitleLabelSlider.setText("Entire region is Go");
            additionalTxtTitleLabel2Slider.setText("");

            validFromTxtLabelSlider.setEnabled(true);
            validToTxtLabelSlider.setEnabled(true);
            draughtTxtLabelSlider.setEnabled(true);
            return;

        }
        if (errorCodeOwn == 0) {
            statusLabelSlider.setText("Success");
            statusLabelSlider.setForeground(Color.GREEN);
            validFromTxtLabelSlider.setText(validFromStr);
            validToTxtLabelSlider.setText(validToStr);

            draughtTxtLabelSlider.setText(df.format(draught) + " meters");
            additionalTxtTitleLabelSlider.setText("");
            additionalTxtTitleLabel2Slider.setText("");

            validFromTxtLabelSlider.setEnabled(true);
            validToTxtLabelSlider.setEnabled(true);
            draughtTxtLabelSlider.setEnabled(true);
            return;
        }

    }

}
