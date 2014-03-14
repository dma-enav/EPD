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
        if (EPD.getInstance().getSettings().getMapSettings().isUseWms()) {
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
        
        lblWms.updateStatus(status);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (status != null) {
            statusComponents.add(status);            
        }
        new BottomPanelStatusDialog(statusComponents);
    }
}
