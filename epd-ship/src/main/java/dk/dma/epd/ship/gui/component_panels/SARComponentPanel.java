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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.prototype.event.mouse.IMapCoordListener;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.ship.gui.panels.SARPanel;
import dk.dma.epd.ship.service.voct.VOCTManager;

public class SARComponentPanel extends OMComponentPanel implements
 Runnable, ProjectionListener, IMapCoordListener,
        VOCTUpdateListener, DockableComponentPanel {

    private static final long serialVersionUID = 1L;
    private final SARPanel sarPanel;
    private VOCTManager voctManager;

    public SARComponentPanel() {
        super();

        // this.setMinimumSize(new Dimension(10, 165));

        sarPanel = new SARPanel();
        // activeWaypointPanel.setVisible(false);
        sarPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);

        setLayout(new BorderLayout(0, 0));
        add(sarPanel, BorderLayout.NORTH);
        setVisible(false);
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }



    @Override
    public void findAndInit(Object obj) {

   

        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
            sarPanel.setVoctManager(voctManager);
            voctManager.addListener(this);
            
        }
    }

    @Override
    public void voctUpdated(VOCTUpdateEvent e) {

        if (e == VOCTUpdateEvent.SAR_CANCEL) {
            sarPanel.sarCancel();
        }

        if (e == VOCTUpdateEvent.SAR_DISPLAY) {
            sarPanel.sarComplete(voctManager.getSarData());
            sarPanel.getBtnEffortAllocation().setEnabled(true);
//            sarPanel.getBtnGenerateSearchPattern().setEnabled(true);
        }
        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY) {
            sarPanel.effortAllocationComplete(voctManager.getSarData());
        }
        if (e == VOCTUpdateEvent.SEARCH_PATTERN_GENERATED) {
            sarPanel.searchPatternGenerated(voctManager.getSarData());
        }

        if (e == VOCTUpdateEvent.SAR_RECEIVED_CLOUD) {
            
            sarPanel.sarComplete(voctManager.getSarData());
            sarPanel.getBtnReopenCalculations().setEnabled(false);

            
            if (voctManager.getSarData().getEffortAllocationData().size() > 0) {
                sarPanel.effortAllocationComplete(voctManager.getSarData());
                sarPanel.getBtnEffortAllocation().setEnabled(false);

                if (voctManager.getSarData().getEffortAllocationData().get(0)
                        .getSearchPatternRoute() != null) {

                    sarPanel.getChckbxShowDynamicPattern().setEnabled(true);
                    sarPanel.getBtnGenerateSearchPattern().setEnabled(false);
                }else{
                    sarPanel.getChckbxShowDynamicPattern().setEnabled(true);
                    sarPanel.getBtnGenerateSearchPattern().setEnabled(true);
                }

            }else{
                sarPanel.getBtnEffortAllocation().setEnabled(true);
                sarPanel.resetEffortAllocation();
                
            }

        }
    }

    @Override
    public void receiveCoord(LatLonPoint llp) {
        // TODO Auto-generated method stub
        
    }

    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "SAR";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
