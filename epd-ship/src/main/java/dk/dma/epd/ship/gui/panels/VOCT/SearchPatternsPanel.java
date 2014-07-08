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
        System.out.println("Enabling search pattern btn");
        btnGenerateSearchPattern.setEnabled(true);
    }
  
}
