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
package dk.dma.epd.ship.gui.panels.VOCT;

import javax.swing.JButton;

import dk.dma.epd.common.prototype.gui.voct.ButtonsPanelCommon;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ButtonsPanel extends ButtonsPanelCommon {

    private static final long serialVersionUID = 1L;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    private JButton btnAdditionalInfo;

    public ButtonsPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{129, 109, 0};
        gridBagLayout.rowHeights = new int[]{23, 23, 0};
        gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        
                btnReopenCalculations = new JButton("Reopen Calculations");
                GridBagConstraints gbc_btnReopenCalculations = new GridBagConstraints();
                gbc_btnReopenCalculations.anchor = GridBagConstraints.NORTH;
                gbc_btnReopenCalculations.insets = new Insets(0, 0, 5, 5);
                gbc_btnReopenCalculations.gridx = 0;
                gbc_btnReopenCalculations.gridy = 0;
                add(btnReopenCalculations, gbc_btnReopenCalculations);
                
                        btnEffortAllocation = new JButton("Effort Allocation");
                        GridBagConstraints gbc_btnEffortAllocation = new GridBagConstraints();
                        gbc_btnEffortAllocation.anchor = GridBagConstraints.NORTH;
                        gbc_btnEffortAllocation.insets = new Insets(0, 0, 5, 0);
                        gbc_btnEffortAllocation.gridx = 1;
                        gbc_btnEffortAllocation.gridy = 0;
                        add(btnEffortAllocation, gbc_btnEffortAllocation);
        
                btnAdditionalInfo = new JButton("Additional Info");
                GridBagConstraints gbc_btnAdditionalInfo = new GridBagConstraints();
                gbc_btnAdditionalInfo.anchor = GridBagConstraints.NORTH;
                gbc_btnAdditionalInfo.gridwidth = 2;
                gbc_btnAdditionalInfo.gridx = 0;
                gbc_btnAdditionalInfo.gridy = 1;
                add(btnAdditionalInfo, gbc_btnAdditionalInfo);

    }

    /**
     * @return the btnReopenCalculations
     */
    @Override
    public JButton getBtnReopenCalculations() {
        return btnReopenCalculations;
    }

    /**
     * @return the btnEffortAllocation
     */
    @Override
    public JButton getBtnEffortAllocation() {
        return btnEffortAllocation;
    }

    @Override
    public JButton getAdditionalInfoButton(){
        return btnAdditionalInfo;
    }
    
}
