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
package dk.dma.epd.common.prototype.gui.voct;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class EndSarPanelCommon extends JPanel {

    private static final long serialVersionUID = 1L;
    private JButton btnEndSAR;

    public EndSarPanelCommon() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 143, 0 };
        gridBagLayout.rowHeights = new int[] { 23, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);

        btnEndSAR = new JButton("End SAR");
        GridBagConstraints gbc_btnReopenCalculations = new GridBagConstraints();
        gbc_btnReopenCalculations.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnReopenCalculations.anchor = GridBagConstraints.NORTH;
        gbc_btnReopenCalculations.gridx = 0;
        gbc_btnReopenCalculations.gridy = 0;
        add(btnEndSAR, gbc_btnReopenCalculations);
    }

    /**
     * @return the btnEndSAR
     */
    public JButton getBtnEndSAR() {
        return btnEndSAR;
    }

}
