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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Own ship panel in sensor panel
 */
public class OwnShipPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JLabel ownShipTitleLabel = new JLabel("Own Ship");
    private JLabel nameTitleLabel = new JLabel("Name");
    private JLabel nameLabel = new JLabel("N/A");
    private JLabel CallsignTitleLabel = new JLabel("Callsign");
    private JLabel callsignLabel = new JLabel("N/A");
    private JLabel mmsiTitleLabel = new JLabel("MMSI");
    private JLabel mmsiLabel = new JLabel("N/A");
    
    public OwnShipPanel() {
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 10, 10, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);
        ownShipTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ownShipTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_ownShipTitleLabel = new GridBagConstraints();
        gbc_ownShipTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_ownShipTitleLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_ownShipTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_ownShipTitleLabel.gridwidth = 2;
        gbc_ownShipTitleLabel.gridx = 0;
        gbc_ownShipTitleLabel.gridy = 0;
        add(ownShipTitleLabel, gbc_ownShipTitleLabel);

        nameTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_nameTitleLabel = new GridBagConstraints();
        gbc_nameTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_nameTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_nameTitleLabel.gridx = 0;
        gbc_nameTitleLabel.gridy = 1;
        add(nameTitleLabel, gbc_nameTitleLabel);

        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_nameLabel = new GridBagConstraints();
        gbc_nameLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_nameLabel.insets = new Insets(0, 0, 5, 0);
        gbc_nameLabel.gridx = 1;
        gbc_nameLabel.gridy = 1;
        add(nameLabel, gbc_nameLabel);

        CallsignTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        CallsignTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_CallsignTitleLabel = new GridBagConstraints();
        gbc_CallsignTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_CallsignTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_CallsignTitleLabel.gridx = 0;
        gbc_CallsignTitleLabel.gridy = 2;
        add(CallsignTitleLabel, gbc_CallsignTitleLabel);

        callsignLabel.setHorizontalAlignment(SwingConstants.LEFT);
        callsignLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_callsignLabel = new GridBagConstraints();
        gbc_callsignLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_callsignLabel.insets = new Insets(0, 0, 5, 0);
        gbc_callsignLabel.gridx = 1;
        gbc_callsignLabel.gridy = 2;
        add(callsignLabel, gbc_callsignLabel);

        mmsiTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mmsiTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_mmsiTitleLabel = new GridBagConstraints();
        gbc_mmsiTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_mmsiTitleLabel.insets = new Insets(0, 0, 0, 5);
        gbc_mmsiTitleLabel.gridx = 0;
        gbc_mmsiTitleLabel.gridy = 3;
        add(mmsiTitleLabel, gbc_mmsiTitleLabel);

        mmsiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mmsiLabel.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_mmsiLabel = new GridBagConstraints();
        gbc_mmsiLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_mmsiLabel.gridx = 1;
        gbc_mmsiLabel.gridy = 3;
        add(mmsiLabel, gbc_mmsiLabel);
    }
    
    public JLabel getNameLabel() {
        return nameLabel;
    }
    
    public JLabel getCallsignLabel() {
        return callsignLabel;
    }
    
    public JLabel getMmsiLabel() {
        return mmsiLabel;
    }
}
