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
package dk.dma.epd.ship.gui.panels;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.gui.voct.ButtonsPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.EffortAllocationPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SARPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SearchPatternsPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.VOCTAdditionalInfoDialog;
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

    public SARPanel() {
        super();
        btnCancelSar.addActionListener(this);
    }

    private static final long serialVersionUID = 1L;

    private JButton btnGenerateSearchPattern;
    private JCheckBox chckbxShowDynamicPattern;
    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    private JButton btnAdditionalInfo;

    protected EffortAllocationWindow effortAllocationWindow = new EffortAllocationWindow();
    protected SearchPatternDialog searchPatternDialog = new SearchPatternDialog();
    protected VOCTAdditionalInfoDialog additionalInfoDialog = new VOCTAdditionalInfoDialog(EPDShip.getInstance().getMainFrame());

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == btnStartSar || arg0.getSource() == btnReopenCalculations) {

            if (voctManager != null) {

                voctManager.showSarInput();

            }
            return;
        }

        if (arg0.getSource() == btnEffortAllocation) {

            // We have a SAR in progress
            if (voctManager != null && ( voctManager.isHasSar() || voctManager.isLoadSarFromSerialize())) {

                // Determine what type of SAR then retrieve the input data
                if (effortAllocationWindow != null) {
                    effortAllocationWindow.setValues();
                    effortAllocationWindow.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    effortAllocationWindow.setVisible(true);
                }

            }
            return;
        }

        if (arg0.getSource() == btnGenerateSearchPattern) {

            if (searchPatternDialog != null) {

                // Semi hack for optimziation
                voctManager.updateEffectiveAreaLocation();

                searchPatternDialog.resetValues();
                searchPatternDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                searchPatternDialog.setVisible(true);
            }

            return;
        }

        if (arg0.getSource() == chckbxShowDynamicPattern) {

            if (chckbxShowDynamicPattern.isSelected()) {
                sarData.getEffortAllocationData().get(0).getSearchPatternRoute().switchToDynamic();
            } else {
                sarData.getEffortAllocationData().get(0).getSearchPatternRoute().switchToStatic();
            }

            EPDShip.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_CHANGED);

            return;
        }

        if (arg0.getSource() == btnCancelSar) {

            int n = JOptionPane.showConfirmDialog(EPDShip.getInstance().getMainFrame(),
                    "Are you sure you wish to end the SAR operation\n"
                            + "This will terminiate your involvement in the SAR operation", "End SAR?", JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                voctManager.cancelSarOperation();
            }

            return;
        }

        if (arg0.getSource() == btnAdditionalInfo) {

            additionalInfoDialog.setVisible(true);
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
        btnGenerateSearchPattern = ((SearchPatternsPanel) searchPatternPanel).getBtnGenerateSearchPattern();

        btnGenerateSearchPattern.addActionListener(this);

        chckbxShowDynamicPattern = ((SearchPatternsPanel) searchPatternPanel).getChckbxShowDynamicPattern();

        chckbxShowDynamicPattern.addActionListener(this);

        return searchPatternPanel;
    }

    @Override
    protected EffortAllocationPanelCommon createEffortAllocationPanel() {
        effortAllocationPanel = new EffortAllocationPanel();
        return effortAllocationPanel;
    }

    @Override
    protected ButtonsPanelCommon createButtonPanel() {
        buttonsPanel = new ButtonsPanel();

        btnReopenCalculations = buttonsPanel.getBtnReopenCalculations();
        btnReopenCalculations.addActionListener(this);
        btnEffortAllocation = buttonsPanel.getBtnEffortAllocation();
        btnEffortAllocation.addActionListener(this);
        btnAdditionalInfo = buttonsPanel.getAdditionalInfoButton();
        btnAdditionalInfo.addActionListener(this);

        return buttonsPanel;
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

    /**
     * @return the btnReopenCalculations
     */
    public JButton getBtnReopenCalculations() {
        return btnReopenCalculations;
    }

    /**
     * @return the btnEffortAllocation
     */
    public JButton getBtnEffortAllocation() {
        return btnEffortAllocation;
    }

    public void resetEffortAllocation() {
        effortAllocationPanel.resetValues();
    }

}
