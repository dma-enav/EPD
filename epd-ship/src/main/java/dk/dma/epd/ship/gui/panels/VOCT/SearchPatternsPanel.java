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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.voct.SearchPatternsPanelCommon;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;

public class SearchPatternsPanel extends SearchPatternsPanelCommon{

    private JButton btnGenerateSearchPattern;
    private JCheckBox chckbxShowDynamicPattern;
    
    private static final long serialVersionUID = 1L;

 
    
    public SearchPatternsPanel(){
        setBorder(new TitledBorder(null, "Search Patterns",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        
        GridBagLayout gbl_searchPatternsPanel = new GridBagLayout();
        gbl_searchPatternsPanel.columnWidths = new int[] { 153, 0 };
        gbl_searchPatternsPanel.rowHeights = new int[] { 23, 0, 0 };
        gbl_searchPatternsPanel.columnWeights = new double[] { 1.0,
                Double.MIN_VALUE };
        gbl_searchPatternsPanel.rowWeights = new double[] { 1.0, 1.0,
                Double.MIN_VALUE };
        setLayout(gbl_searchPatternsPanel);

        btnGenerateSearchPattern = new JButton("Generate Search Pattern");
        btnGenerateSearchPattern.setEnabled(false);
        GridBagConstraints gbc_btnGenerateSearchPattern = new GridBagConstraints();
        gbc_btnGenerateSearchPattern.insets = new Insets(0, 0, 5, 0);
        gbc_btnGenerateSearchPattern.gridx = 0;
        gbc_btnGenerateSearchPattern.gridy = 0;
        add(btnGenerateSearchPattern,
                gbc_btnGenerateSearchPattern);

        chckbxShowDynamicPattern = new JCheckBox("Show Dynamic Pattern");
        chckbxShowDynamicPattern.setSelected(false);
        chckbxShowDynamicPattern.setEnabled(false);
        
        GridBagConstraints gbc_chckbxShowDynamicPattern = new GridBagConstraints();
        gbc_chckbxShowDynamicPattern.gridx = 0;
        gbc_chckbxShowDynamicPattern.gridy = 1;
        add(chckbxShowDynamicPattern,
                gbc_chckbxShowDynamicPattern);

    }



    /**
     * @return the btnGenerateSearchPattern
     */
    public JButton getBtnGenerateSearchPattern() {
        return btnGenerateSearchPattern;
    }



    /**
     * @return the chckbxShowDynamicPattern
     */
    public JCheckBox getChckbxShowDynamicPattern() {
        return chckbxShowDynamicPattern;
    }

    @Override
    public void disablecheckBox(){
        chckbxShowDynamicPattern.setEnabled(false);
    }
    
    @Override
    public void resetValues(){
        btnGenerateSearchPattern.setEnabled(false);
        chckbxShowDynamicPattern.setEnabled(false);
    }
    
    @Override
    public void searchPatternGenerated(SARData sarData) {
        chckbxShowDynamicPattern.setEnabled(true);
        chckbxShowDynamicPattern.setSelected(false);
    }

    @Override
    public void effortAllocationGenerated(){
        btnGenerateSearchPattern.setEnabled(true);
    }
  
}
