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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import dk.dma.epd.common.prototype.model.voct.sardata.SARData;

public class SearchPatternsPanelCommon extends JPanel {

//    private JButton btnGenerateSearchPattern;
//    private JCheckBox chckbxShowDynamicPattern;
    
    private static final long serialVersionUID = 1L;

    
    public void disablecheckBox(){
    }
    
    public void resetValues(){
    }
    
    
    public void effortAllocationGenerated(){
        
    }
    
    public void searchPatternGenerated(SARData sarData) {
    }


    public JButton getBtnGenerateSearchPattern() {
        return null;
    }



    /**
     * @return the chckbxShowDynamicPattern
     */
    public JCheckBox getChckbxShowDynamicPattern() {
        return null;
    }

    
}
