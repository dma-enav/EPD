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
package dk.dma.epd.ship.gui;

import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.prototype.gui.views.BottomPanelCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;

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
        new BottomPanelStatusDialog(statusComponents);
    }
}
