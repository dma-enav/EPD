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
package dk.dma.epd.ship.gui;

import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.prototype.gui.views.BottomPanelCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.ship.EPDShip;

/**
 * Panel shown below the chart
 */
public class BottomPanel extends BottomPanelCommon {

    private static final long serialVersionUID = 1L;

    private PntHandler pntHandler;
    private StatusLabel pntStatus;

    public BottomPanel() {
        super();
    }
    
    /**
     * Adds the status components
     */
    @Override
    protected void addStatusComponents() {
        pntStatus = new StatusLabel("PNT");
        addToolbarComponent(pntStatus);
        addSeparator();
        
        // Let super add the rest
        super.addStatusComponents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
            statusComponents.add(pntHandler);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateStatus() {
        super.updateStatus();
        
        if (pntHandler != null) {
            pntStatus.updateStatus(pntHandler);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        EPDShip.getInstance().getMainFrame().getBottomPanelStatusDialog().setStatusComponents(this.statusComponents);
        EPDShip.getInstance().getMainFrame().getBottomPanelStatusDialog().setVisible(true);
    }
}
