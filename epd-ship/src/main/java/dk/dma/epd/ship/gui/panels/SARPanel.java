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
package dk.dma.epd.ship.gui.panels;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import dk.dma.epd.common.prototype.gui.voct.ButtonsPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.EffortAllocationPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.EffortAllocationWindowCommon;
import dk.dma.epd.common.prototype.gui.voct.SARPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SearchPatternDialogCommon;
import dk.dma.epd.common.prototype.gui.voct.SearchPatternsPanelCommon;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.panels.VOCT.ButtonsPanel;
import dk.dma.epd.ship.gui.panels.VOCT.EffortAllocationPanel;
import dk.dma.epd.ship.gui.panels.VOCT.SearchPatternsPanel;
import dk.dma.epd.ship.gui.voct.EffortAllocationWindow;
import dk.dma.epd.ship.gui.voct.SearchPatternDialog;

/**
 * Active waypoint panel in sensor panel
 */
public class SARPanel extends SARPanelCommon {

    private static final long serialVersionUID = 1L;

    private JButton btnGenerateSearchPattern;
    private JCheckBox chckbxShowDynamicPattern;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    
    protected EffortAllocationWindow effortAllocationWindow = new EffortAllocationWindow();
    protected SearchPatternDialog searchPatternDialog = new SearchPatternDialog();


    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == btnStartSar
                || arg0.getSource() == btnReopenCalculations) {

            if (voctManager != null) {

                voctManager.showSarInput();

            }
            return;
        }

        if (arg0.getSource() == btnEffortAllocation) {

            // We have a SAR in progress
            if (voctManager != null && voctManager.isHasSar()) {

                // Determine what type of SAR then retrieve the input data
                if (effortAllocationWindow != null) {
                    effortAllocationWindow.setValues();
                    effortAllocationWindow
                            .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    effortAllocationWindow.setVisible(true);
                }

            }
            return;
        }

        if (arg0.getSource() == btnGenerateSearchPattern) {

            if (searchPatternDialog != null) {

                // Semi hack for optimziation
                voctManager.updateEffectiveAreaLocation();

                searchPatternDialog.setValues();
                searchPatternDialog
                        .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                searchPatternDialog.setVisible(true);
            }

            return;
        }

        if (arg0.getSource() == chckbxShowDynamicPattern) {

            if (chckbxShowDynamicPattern.isSelected()) {
                sarData.getSearchPatternRoute().switchToDynamic();
            } else {
                sarData.getSearchPatternRoute().switchToStatic();
            }

            EPDShip.getRouteManager().notifyListeners(
                    RoutesUpdateEvent.ROUTE_CHANGED);

            return;
        }

    }

    /**
     * @param voctManager
     *            the voctManager to set
     */
    @Override
    public void setVoctManager(VOCTManagerCommon voctManager) {
        this.voctManager = voctManager;
        effortAllocationWindow.setVoctManager(voctManager);
        searchPatternDialog.setVoctManager(voctManager);
    }
    
    @Override
    protected SearchPatternsPanelCommon createSearchPatternsPanel() {
        searchPatternPanel = new SearchPatternsPanel();
        btnGenerateSearchPattern = searchPatternPanel
                .getBtnGenerateSearchPattern();

        btnGenerateSearchPattern.addActionListener(this);

        chckbxShowDynamicPattern = searchPatternPanel
                .getChckbxShowDynamicPattern();

        chckbxShowDynamicPattern.addActionListener(this);

        return searchPatternPanel;
    }

    @Override
    protected EffortAllocationPanelCommon createEffortAllocationPanel() {
        effortAllocationPanel = new EffortAllocationPanel();
        return effortAllocationPanel;
    }
    
    @Override
    protected ButtonsPanelCommon createButtonPanel(){
        buttonsPanel = new ButtonsPanel();
        
        btnReopenCalculations = buttonsPanel.getBtnReopenCalculations();
        btnReopenCalculations.addActionListener(this);
        btnEffortAllocation = buttonsPanel.getBtnEffortAllocation();
        btnEffortAllocation.addActionListener(this);
        
        return buttonsPanel;
    }

}
