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
package dk.dma.epd.shore.gui.voct;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.gui.voct.ButtonsPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.EffortAllocationPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SARPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SearchPatternsPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.VOCTAdditionalInfoDialog;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.MapFrameType;
import dk.dma.epd.shore.gui.voct.panels.ButtonsPanel;
import dk.dma.epd.shore.gui.voct.panels.EffortAllocationPanel;
import dk.dma.epd.shore.gui.voct.panels.SearchPatternsPanel;
import dk.dma.epd.shore.voct.VOCTManager;

public class SARPanelPlanning extends SARPanelCommon implements VOCTUpdateListener {

    private static final long serialVersionUID = 1L;

    private JButton btnReopenCalculations;
    private JButton btnEffortAllocation;
    private JButton btnSruDialog;
    private JButton additionalInfoDialogBtn;

    protected EffortAllocationWindow effortAllocationWindow = new EffortAllocationWindow();
    protected VOCTAdditionalInfoDialog additionalInfoDialog = new VOCTAdditionalInfoDialog(EPDShore.getInstance().getMainFrame());

    private JButton btnTrackingWindow;

    private VOCTManager voctManager;

    public SARPanelPlanning() {
        super();

        setVoctManager(EPDShore.getInstance().getVoctManager());

        voctManager.addListener(this);
        sarComplete(voctManager.getSarData());
        setTitle("Search And Rescue - Planning");

        btnCancelSar.addActionListener(this);
    }

    /**
     * @param voctManager
     *            the voctManager to set
     */
    @Override
    public void setVoctManager(VOCTManagerCommon voctManager) {
        super.setVoctManager(voctManager);
        this.voctManager = (VOCTManager) voctManager;
        effortAllocationWindow.setVoctManager(this.voctManager);
        // searchPatternDialog.setVoctManager(voctManager);
    }

    @Override
    public void voctUpdated(VOCTUpdateEvent e) {

        if (e == VOCTUpdateEvent.SAR_CANCEL) {
            // sarCancel();
        }

        if (e == VOCTUpdateEvent.SAR_DISPLAY) {
//            System.out.println("SAR PANEL DISPLAY ?");
            sarComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY) {
            effortAllocationComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.SEARCH_PATTERN_GENERATED) {
            searchPatternGenerated(voctManager.getSarData());
        }

        this.repaint();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == btnTrackingWindow) {

            for (int i = 0; i < EPDShore.getInstance().getMainFrame().getMapWindows().size(); i++) {

                if (EPDShore.getInstance().getMainFrame().getMapWindows().get(i).getType() == MapFrameType.SAR_Tracking) {
                    // Resize windows
                    EPDShore.getInstance().getMainFrame().getMapWindows().get(i).toFront();
                    return;
                }
            }

            EPDShore.getInstance().getMainFrame().addSARWindow(MapFrameType.SAR_Tracking);

            return;
        }

        if (arg0.getSource() == btnStartSar || arg0.getSource() == btnReopenCalculations) {

            if (voctManager != null) {

                voctManager.showSarInput();

            }
            return;
        }

        if (arg0.getSource() == btnSruDialog) {

            if (voctManager != null) {

                voctManager.showSRUManagerDialog();

            }
            return;
        }

        if (arg0.getSource() == btnEffortAllocation) {

            // We have a SAR in progress
            if (voctManager != null && (voctManager.isHasSar() || voctManager.isLoadSarFromSerialize())) {

                // Determine what type of SAR then retrieve the input data
                if (effortAllocationWindow != null) {
                    effortAllocationWindow.setValues();
                    effortAllocationWindow.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                    effortAllocationWindow.setVisible(true);
                }

            }
            return;
        }

        if (arg0.getSource() == btnCancelSar) {

            // final JOptionPane optionPane = new JOptionPane("Are you sure you wish to end the SAR operation\n"
            // + "This will terminiate the SAR for all participating SRUs", JOptionPane.WARNING_MESSAGE,
            // JOptionPane.YES_NO_OPTION);

            int n = JOptionPane.showConfirmDialog(EPDShore.getInstance().getMainFrame(),
                    "Are you sure you wish to end the SAR operation\n"
                            + "This will terminiate the SAR operation for all participating SRUs", "End SAR?",
                    JOptionPane.YES_NO_OPTION);

            if (n == JOptionPane.YES_OPTION) {
                voctManager.cancelSarOperation();
            }

            return;
        }

        if (arg0.getSource() == additionalInfoDialogBtn) {
            additionalInfoDialog.setVisible(true);
        }

        // if (arg0.getSource() == btnGenerateSearchPattern) {
        //
        // if (searchPatternDialog != null) {
        //
        // // Semi hack for optimziation
        // voctManager.updateEffectiveAreaLocation();
        //
        // searchPatternDialog.setValues();
        // searchPatternDialog
        // .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        // searchPatternDialog.setVisible(true);
        // }
        //
        // return;
        // }

        // if (arg0.getSource() == chckbxShowDynamicPattern) {
        //
        // if (chckbxShowDynamicPattern.isSelected()) {
        // sarData.getSearchPatternRoute().switchToDynamic();
        // } else {
        // sarData.getSearchPatternRoute().switchToStatic();
        // }
        //
        // EPDShip.getRouteManager().notifyListeners(
        // RoutesUpdateEvent.ROUTE_CHANGED);
        //
        // return;
        // }

    }

    @Override
    protected SearchPatternsPanelCommon createSearchPatternsPanel() {
        searchPatternPanel = new SearchPatternsPanel();
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

        btnTrackingWindow = ((ButtonsPanel) buttonsPanel).getBtnTrackingWindow();
        btnTrackingWindow.addActionListener(this);

        btnReopenCalculations = buttonsPanel.getBtnReopenCalculations();
        btnReopenCalculations.addActionListener(this);

        btnEffortAllocation = buttonsPanel.getBtnEffortAllocation();
        btnEffortAllocation.addActionListener(this);

        btnSruDialog = buttonsPanel.getBtnSruDialog();
        btnSruDialog.addActionListener(this);

        additionalInfoDialogBtn = buttonsPanel.getAdditionalInfoButton();
        additionalInfoDialogBtn.addActionListener(this);

        return buttonsPanel;
    }
}
