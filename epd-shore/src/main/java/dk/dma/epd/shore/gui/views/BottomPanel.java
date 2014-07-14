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
package dk.dma.epd.shore.gui.views;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.prototype.gui.views.BottomPanelCommon;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.shore.EPDShore;

/**
 * Panel shown below the chart
 */
public class BottomPanel extends BottomPanelCommon {
    
    private static final long serialVersionUID = 1L;
    
    private StatusLabel lblWms;

    private IStatusComponent status;

    /**
     * Constructor.
     */
    public BottomPanel() {
        super(); 
    }
    
    /**
     * Adds the status components
     */
    @Override
    protected void addStatusComponents() {
        if (EPD.getInstance().getSettings().getPrimaryWMSLayerSettings().isUseWms()) {
            lblWms = new StatusLabel("WMS");
            addToolbarComponent(lblWms);
            addSeparator();
        }
        
        // Let super add the rest
        super.addStatusComponents();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateStatus() {
        
        super.updateStatus();
        
        // Only check WMS status if WMS is used
        if (lblWms == null) {
            return;
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
            
            @Override
            public synchronized String getShortStatusText() {
                return "Status: "+getStatus().toString();
            }
        };
        
        status = new IStatusComponent() {
            
            @Override
            public ComponentStatus getStatus() {
                return wmsStatus;
            }
        };
        
        try {
            // Create URL
            URL urlToWms = new URL(
                    EPDShore.getInstance().getSettings().getPrimaryWMSLayerSettings().getWmsQuery());
            
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
        
        lblWms.updateStatus(status);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
                
        if (status != null) {
            statusComponents.add(status);
        }
        EPDShore.getInstance().getMainFrame().getBottomPanelStatusDialog().setStatusComponents(statusComponents);        
        EPDShore.getInstance().getMainFrame().getBottomPanelStatusDialog().setVisible(true);
    }
}
