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
package dk.dma.epd.shore.gui.voct;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;

import dk.dma.epd.common.prototype.gui.voct.ButtonsPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.EffortAllocationPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SARPanelCommon;
import dk.dma.epd.common.prototype.gui.voct.SearchPatternsPanelCommon;
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
    
    protected EffortAllocationWindow effortAllocationWindow = new EffortAllocationWindow();
    
    private JButton btnTrackingWindow;
    
    
    private VOCTManager voctManager;
    
   public SARPanelPlanning(){
       super();
       
       setVoctManager(EPDShore.getVoctManager());
       
       
       
       voctManager.addListener(this);
       sarComplete(voctManager.getSarData());
       setTitle("Search And Rescue - Planning");
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
//       searchPatternDialog.setVoctManager(voctManager);
   }
    
    
    @Override
    public void voctUpdated(VOCTUpdateEvent e) {
        
        if (e == VOCTUpdateEvent.SAR_CANCEL){
//            sarCancel();
        }
        
        if (e == VOCTUpdateEvent.SAR_DISPLAY){
            System.out.println("SAR PANEL DISPLAY ?");
            sarComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY){
            effortAllocationComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.SEARCH_PATTERN_GENERATED){
            searchPatternGenerated(voctManager.getSarData());
        }
        
        this.repaint();
        
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == btnTrackingWindow){
            
            for (int i = 0; i < EPDShore.getMainFrame().getMapWindows().size(); i++) {
                
                if (EPDShore.getMainFrame().getMapWindows().get(i).getType() == MapFrameType.SAR_Tracking){
                    //Resize windows
                    EPDShore.getMainFrame().getMapWindows().get(i).toFront();
                    return;
                }
            }
            
            EPDShore.getMainFrame().addSARWindow(MapFrameType.SAR_Tracking);
            
            return;
        }
        
        if (arg0.getSource() == btnStartSar
                || arg0.getSource() == btnReopenCalculations) {

            
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

        
        
        
        
        
//        if (arg0.getSource() == btnGenerateSearchPattern) {
//
//            if (searchPatternDialog != null) {
//
//                // Semi hack for optimziation
//                voctManager.updateEffectiveAreaLocation();
//
//                searchPatternDialog.setValues();
//                searchPatternDialog
//                        .setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
//                searchPatternDialog.setVisible(true);
//            }
//
//            return;
//        }

//        if (arg0.getSource() == chckbxShowDynamicPattern) {
//
//            if (chckbxShowDynamicPattern.isSelected()) {
//                sarData.getSearchPatternRoute().switchToDynamic();
//            } else {
//                sarData.getSearchPatternRoute().switchToStatic();
//            }
//
//            EPDShip.getRouteManager().notifyListeners(
//                    RoutesUpdateEvent.ROUTE_CHANGED);
//
//            return;
//        }

    }

    @Override
    protected SearchPatternsPanelCommon createSearchPatternsPanel() {
        searchPatternPanel = new SearchPatternsPanel();
        
        
//        btnGenerateSearchPattern = searchPatternPanel
//                .getBtnGenerateSearchPattern();

//        btnGenerateSearchPattern.addActionListener(this);

//        chckbxShowDynamicPattern = searchPatternPanel
//                .getChckbxShowDynamicPattern();

//        chckbxShowDynamicPattern.addActionListener(this);

        return searchPatternPanel;
    }

    protected EffortAllocationPanelCommon createEffortAllocationPanel() {
        effortAllocationPanel = new EffortAllocationPanel();
        return effortAllocationPanel;
    }
    
    
    
    @Override
    protected ButtonsPanelCommon createButtonPanel(){
        buttonsPanel = new ButtonsPanel();
        
        btnTrackingWindow = ((ButtonsPanel) buttonsPanel).getBtnTrackingWindow();
        btnTrackingWindow.addActionListener(this);
        
        btnReopenCalculations = buttonsPanel.getBtnReopenCalculations();
        btnReopenCalculations.addActionListener(this);
        
        btnEffortAllocation = buttonsPanel.getBtnEffortAllocation();
        btnEffortAllocation.addActionListener(this);
    
        btnSruDialog = buttonsPanel.getBtnSruDialog();
        btnSruDialog.addActionListener(this);
        
        return buttonsPanel;
    }
}
