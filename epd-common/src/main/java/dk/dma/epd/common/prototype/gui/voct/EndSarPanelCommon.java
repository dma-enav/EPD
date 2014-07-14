/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
