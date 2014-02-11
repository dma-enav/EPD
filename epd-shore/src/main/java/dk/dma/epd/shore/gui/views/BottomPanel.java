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
package dk.dma.epd.shore.gui.views;

import com.bbn.openmap.gui.OMComponentPanel;

import java.awt.FlowLayout;

import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.service.MaritimeCloudService;
import dk.dma.epd.shore.services.shore.ShoreServices;

public class BottomPanel extends OMComponentPanel implements Runnable {
    
    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private AisHandler aisHandler;
    private ShoreServices shoreServices;
    private StatusLabel lblAisConnection;
    private StatusLabel lblShoreServices;
    private MaritimeCloudService maritimeCloudService;
    private StatusLabel lblMaritimeCloud;

    /**
     * Constructor.
     */
    public BottomPanel() {
        super();
        
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        this.setLayout(flowLayout);
        
        lblShoreServices = new StatusLabel("Shore Services");
        this.add(lblShoreServices);
        
        lblAisConnection = new StatusLabel("AIS");
        this.add(lblAisConnection);
        
        StatusLabel lblWms = new StatusLabel("WMS");
        this.add(lblWms);
        
        lblMaritimeCloud = new StatusLabel("Maritime Cloud");
        this.add(lblMaritimeCloud);
        
        new Thread(this).start();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        
        if (obj instanceof AisHandler) {
            this.aisHandler = (AisHandler) obj;
        } else if (obj instanceof ShoreServices) {
            this.shoreServices = (ShoreServices) obj;
        } else if (obj instanceof MaritimeCloudService) {
            maritimeCloudService = (MaritimeCloudService) obj;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while(true) {
            updateStatus();
            Util.sleep(3000);
        }
    }
    
    /**
     * Update status of the status labels.
     */
    private void updateStatus() {
        
        if (this.aisHandler != null) {
            lblAisConnection.updateStatus(this.aisHandler);
        } 
        
        if (this.shoreServices != null) {
            lblShoreServices.updateStatus(this.shoreServices);
        } 
        
        if (this.maritimeCloudService != null) {
            lblMaritimeCloud.updateStatus(this.maritimeCloudService);
        }
    }
}
