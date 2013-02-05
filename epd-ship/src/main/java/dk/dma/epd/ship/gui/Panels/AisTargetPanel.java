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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * GPS panel in sensor panel
 */
public class AisTargetPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel aisTitleLabel = new JLabel("AIS Target");
    private JLabel nameTitleLabel = new JLabel("Name");
    private JLabel nameLabel = new JLabel("N/A");
    private JLabel callSignTitleLabel = new JLabel("Call Sign");
    private final JLabel sogTitleLabel = new JLabel("SOG");
    private final JLabel cogTitleLabel = new JLabel("COG");
    private final JLabel dstTitleLabel = new JLabel("DST");
    private final JLabel brgTitelLabel = new JLabel("BRG");
    private final JLabel callsignLabel = new JLabel("N/A");
    private final JLabel sogLabel = new JLabel("N/A");
    private final JLabel cogLabel = new JLabel("N/A");
    private final JLabel dstLabel = new JLabel("N/A");
    private final JLabel brgLabel = new JLabel("N/A");
    private final JCheckBox intendedRouteCheckbox = new JCheckBox("");
    private final JLabel intendedRouteTitelLabel = new JLabel(
            "Show Intended Route  ");
    private final JCheckBox dynamicNoGoCheckbox = new JCheckBox("");
    private final JLabel dynamicNogoTitelLabel = new JLabel("Show Dynamic NoGo  ");

    public AisTargetPanel() {

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 10, 10, 0 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 16, 0, 0, 0, 0, 0, 0, 0,
                10 };
        gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);
        aisTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aisTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_aisTitleLabel = new GridBagConstraints();
        gbc_aisTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_aisTitleLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_aisTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_aisTitleLabel.gridwidth = 2;
        gbc_aisTitleLabel.gridx = 0;
        gbc_aisTitleLabel.gridy = 0;
        add(aisTitleLabel, gbc_aisTitleLabel);

        nameTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_nameTitleLabel = new GridBagConstraints();
        gbc_nameTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_nameTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_nameTitleLabel.gridx = 0;
        gbc_nameTitleLabel.gridy = 1;
        add(nameTitleLabel, gbc_nameTitleLabel);

        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_nameLabel = new GridBagConstraints();
        gbc_nameLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_nameLabel.insets = new Insets(0, 0, 5, 0);
        gbc_nameLabel.gridx = 1;
        gbc_nameLabel.gridy = 1;
        add(nameLabel, gbc_nameLabel);

        callSignTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        callSignTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_callSignTitleLabel = new GridBagConstraints();
        gbc_callSignTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_callSignTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_callSignTitleLabel.gridx = 0;
        gbc_callSignTitleLabel.gridy = 2;
        add(callSignTitleLabel, gbc_callSignTitleLabel);

        callsignLabel.setHorizontalAlignment(SwingConstants.LEFT);
        callsignLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_callsignLabel = new GridBagConstraints();
        gbc_callsignLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_callsignLabel.insets = new Insets(0, 0, 5, 0);
        gbc_callsignLabel.gridx = 1;
        gbc_callsignLabel.gridy = 2;
        add(callsignLabel, gbc_callsignLabel);

        sogTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sogTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_sogTitleLabel = new GridBagConstraints();
        gbc_sogTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_sogTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_sogTitleLabel.gridx = 0;
        gbc_sogTitleLabel.gridy = 3;
        add(sogTitleLabel, gbc_sogTitleLabel);

        sogLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sogLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_sogLabel = new GridBagConstraints();
        gbc_sogLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_sogLabel.insets = new Insets(0, 0, 5, 0);
        gbc_sogLabel.gridx = 1;
        gbc_sogLabel.gridy = 3;
        add(sogLabel, gbc_sogLabel);

        cogTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        cogTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_cogTitleLabel = new GridBagConstraints();
        gbc_cogTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_cogTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_cogTitleLabel.gridx = 0;
        gbc_cogTitleLabel.gridy = 4;
        add(cogTitleLabel, gbc_cogTitleLabel);

        cogLabel.setHorizontalAlignment(SwingConstants.LEFT);
        cogLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_cogLabel = new GridBagConstraints();
        gbc_cogLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_cogLabel.insets = new Insets(0, 0, 5, 0);
        gbc_cogLabel.gridx = 1;
        gbc_cogLabel.gridy = 4;
        add(cogLabel, gbc_cogLabel);

        dstTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dstTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_dstTitleLabel = new GridBagConstraints();
        gbc_dstTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_dstTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_dstTitleLabel.gridx = 0;
        gbc_dstTitleLabel.gridy = 5;
        add(dstTitleLabel, gbc_dstTitleLabel);

        dstLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dstLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_dstLabel = new GridBagConstraints();
        gbc_dstLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_dstLabel.insets = new Insets(0, 0, 5, 0);
        gbc_dstLabel.gridx = 1;
        gbc_dstLabel.gridy = 5;
        add(dstLabel, gbc_dstLabel);

        brgTitelLabel.setHorizontalAlignment(SwingConstants.LEFT);
        brgTitelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_brgTitelLabel = new GridBagConstraints();
        gbc_brgTitelLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_brgTitelLabel.insets = new Insets(0, 0, 5, 5);
        gbc_brgTitelLabel.gridx = 0;
        gbc_brgTitelLabel.gridy = 6;
        add(brgTitelLabel, gbc_brgTitelLabel);

        brgLabel.setHorizontalAlignment(SwingConstants.LEFT);
        brgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_brgLabel = new GridBagConstraints();
        gbc_brgLabel.insets = new Insets(0, 0, 5, 0);
        gbc_brgLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_brgLabel.gridx = 1;
        gbc_brgLabel.gridy = 6;
        add(brgLabel, gbc_brgLabel);

        GridBagConstraints gbc_intendedRouteCheckbox = new GridBagConstraints();
        gbc_intendedRouteCheckbox.insets = new Insets(0, 0, 5, 5);
        gbc_intendedRouteCheckbox.anchor = GridBagConstraints.NORTH;
        gbc_intendedRouteCheckbox.gridx = 0;
        gbc_intendedRouteCheckbox.gridy = 8;
        add(intendedRouteCheckbox, gbc_intendedRouteCheckbox);

        intendedRouteTitelLabel.setHorizontalAlignment(SwingConstants.LEFT);
        intendedRouteTitelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_IntendedRouteTitelLabel = new GridBagConstraints();
        gbc_IntendedRouteTitelLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_IntendedRouteTitelLabel.insets = new Insets(0, 0, 5, 0);
        gbc_IntendedRouteTitelLabel.gridx = 1;
        gbc_IntendedRouteTitelLabel.gridy = 8;
        add(intendedRouteTitelLabel, gbc_IntendedRouteTitelLabel);

        GridBagConstraints gbc_dynamicNoGoCheckbox = new GridBagConstraints();
        gbc_dynamicNoGoCheckbox.anchor = GridBagConstraints.NORTH;
        gbc_dynamicNoGoCheckbox.insets = new Insets(0, 0, 0, 5);
        gbc_dynamicNoGoCheckbox.gridx = 0;
        gbc_dynamicNoGoCheckbox.gridy = 9;
        add(dynamicNoGoCheckbox, gbc_dynamicNoGoCheckbox);

        dynamicNogoTitelLabel.setHorizontalAlignment(SwingConstants.LEFT);
        dynamicNogoTitelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_dynamicNogoTitelLabel = new GridBagConstraints();
        gbc_dynamicNogoTitelLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_dynamicNogoTitelLabel.gridx = 1;
        gbc_dynamicNogoTitelLabel.gridy = 9;
        add(dynamicNogoTitelLabel, gbc_dynamicNogoTitelLabel);
    }

    public JLabel getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(JLabel nameLabel) {
        this.nameLabel = nameLabel;
    }

    public JLabel getCallsignLabel() {
        return callsignLabel;
    }

    public JLabel getSogLabel() {
        return sogLabel;
    }

    public JLabel getCogLabel() {
        return cogLabel;
    }

    public JLabel getDstLabel() {
        return dstLabel;
    }

    public JLabel getBrgLabel() {
        return brgLabel;
    }

    public JCheckBox getIntendedRouteCheckbox() {
        return intendedRouteCheckbox;
    }

    public JLabel getIntendedRouteTitelLabel() {
        return intendedRouteTitelLabel;
    }

    public JCheckBox getDynamicNoGoCheckbox() {
        return dynamicNoGoCheckbox;
    }

    public JLabel getDynamicNogoTitelLabel() {
        return dynamicNogoTitelLabel;
    }
    
    

}
