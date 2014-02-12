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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.service.MaritimeCloudService;
import dk.dma.epd.shore.services.shore.ShoreServices;

import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

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
    private StatusLabel lblWms;
    private JToolBar statusbar;

    /**
     * Constructor.
     */
    public BottomPanel() {
        super();
        
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        statusbar = new JToolBar();
        statusbar.setFloatable(false);
        
        lblShoreServices = new StatusLabel("Shore Services");
        statusbar.add(lblShoreServices);
        
        addSeparator();
        
        lblAisConnection = new StatusLabel("AIS");
        statusbar.add(lblAisConnection);
        
        addSeparator();
        
        lblWms = new StatusLabel("WMS");
        statusbar.add(lblWms);
        
        addSeparator();
        
        lblMaritimeCloud = new StatusLabel("Maritime Cloud");
        statusbar.add(lblMaritimeCloud);
        
        this.add(statusbar, BorderLayout.EAST);
        
        // Run to check for changes in status.
        new Thread(this).start();
    }
    
    /**
     * Adds a seperator to the statusbar.
     */
    private void addSeparator() {
        Component horizontalStrut = Box.createHorizontalStrut(5);
        JSeparator seperator = new JSeparator();
        seperator.setOrientation(SwingConstants.VERTICAL);
        this.statusbar.add(horizontalStrut);
        this.statusbar.add(seperator);
        this.statusbar.add(horizontalStrut);
    }
    
    /**
     * Update status of the status labels.
     */
    private void updateStatus() {
        
        // AIS connection status.
        if (this.aisHandler != null) {
            lblAisConnection.updateStatus(this.aisHandler);
        }
        
        // Shore service status.
        if (this.shoreServices != null) {
            lblShoreServices.updateStatus(this.shoreServices);
        } 
        
        // Maritime clound status.
        if (this.maritimeCloudService != null) {
            lblMaritimeCloud.updateStatus(this.maritimeCloudService);
        }
        
        
        /********* WMS STATUS *********/
        
        // WMS connection status.
        HttpURLConnection connection = null;
        
        // Do a bit of hack to update status.
        final ComponentStatus wmsStatus = new ComponentStatus("WMS") {
            
            @Override
            public String getStatusHtml() {
                return "";
            }
        };
        
        IStatusComponent status = new IStatusComponent() {
            
            @Override
            public ComponentStatus getStatus() {
                return wmsStatus;
            }
        };
        
        try {
            // Create URL
            URL urlToWms = new URL(
                    EPDShore.getInstance().getSettings().getMapSettings().getWmsQuery());
            
            // Open connection to the url.
            connection = (HttpURLConnection) urlToWms.openConnection();
            connection.connect();
            
            // A file was found.
            if (connection.getResponseCode() == 200) {
                
                // TODO: Should be checking if the parsed url contains wms.
                wmsStatus.setStatus(ComponentStatus.Status.OK);                
            }
            
            // A file was not found.
        } catch (MalformedURLException e) {
            
            wmsStatus.setStatus(ComponentStatus.Status.ERROR);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.lblWms.updateStatus(status);
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
}
