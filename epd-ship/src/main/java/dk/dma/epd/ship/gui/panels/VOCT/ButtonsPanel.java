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

public class ButtonsPanel extends ButtonsPanelCommon{

    private static final long serialVersionUID = 1L;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    
    
    public ButtonsPanel(){
     
        btnReopenCalculations = new JButton("Reopen Calculations");
        add(btnReopenCalculations);
        
        btnEffortAllocation = new JButton("Effort Allocation");
        add(btnEffortAllocation);
        
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

  
    
    
}
