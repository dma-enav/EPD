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
package dk.dma.epd.shore.gui.voct.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;

import dk.dma.epd.common.prototype.gui.voct.EndSarPanelCommon;

public class CancelSarPanel extends EndSarPanelCommon {

    private static final long serialVersionUID = 1L;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    private JButton btnSruDialog;
    private JButton btnTrackingWindow;
    private JButton btnAdditionalInfo;

    public CancelSarPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 143, 0 };
        gridBagLayout.rowHeights = new int[] { 23, 23, 23, 23, 23, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        btnReopenCalculations = new JButton("Reopen Calculations");
        GridBagConstraints gbc_btnReopenCalculations = new GridBagConstraints();
        gbc_btnReopenCalculations.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnReopenCalculations.anchor = GridBagConstraints.NORTH;
        gbc_btnReopenCalculations.insets = new Insets(0, 0, 5, 0);
        gbc_btnReopenCalculations.gridx = 0;
        gbc_btnReopenCalculations.gridy = 0;
        add(btnReopenCalculations, gbc_btnReopenCalculations);

        btnSruDialog = new JButton("Manage SRUs");
        GridBagConstraints gbc_btnSruDialog = new GridBagConstraints();
        gbc_btnSruDialog.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSruDialog.anchor = GridBagConstraints.NORTH;
        gbc_btnSruDialog.insets = new Insets(0, 0, 5, 0);
        gbc_btnSruDialog.gridx = 0;
        gbc_btnSruDialog.gridy = 1;
        add(btnSruDialog, gbc_btnSruDialog);

        btnEffortAllocation = new JButton("Effort Allocation");
        GridBagConstraints gbc_btnEffortAllocation = new GridBagConstraints();
        gbc_btnEffortAllocation.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnEffortAllocation.anchor = GridBagConstraints.NORTH;
        gbc_btnEffortAllocation.insets = new Insets(0, 0, 5, 0);
        gbc_btnEffortAllocation.gridx = 0;
        gbc_btnEffortAllocation.gridy = 2;
        add(btnEffortAllocation, gbc_btnEffortAllocation);

        btnTrackingWindow = new JButton("Open Tracking Window");
        GridBagConstraints gbc_btnTrackingWindow = new GridBagConstraints();
        gbc_btnTrackingWindow.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnTrackingWindow.anchor = GridBagConstraints.NORTH;
        gbc_btnTrackingWindow.insets = new Insets(0, 0, 5, 0);
        gbc_btnTrackingWindow.gridx = 0;
        gbc_btnTrackingWindow.gridy = 3;
        add(btnTrackingWindow, gbc_btnTrackingWindow);

        btnAdditionalInfo = new JButton("Additional Info");
        GridBagConstraints gbc_btnAdditionalInfo = new GridBagConstraints();
        gbc_btnAdditionalInfo.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnAdditionalInfo.anchor = GridBagConstraints.NORTH;
        gbc_btnAdditionalInfo.gridx = 0;
        gbc_btnAdditionalInfo.gridy = 4;
        add(btnAdditionalInfo, gbc_btnAdditionalInfo);
    }

}
