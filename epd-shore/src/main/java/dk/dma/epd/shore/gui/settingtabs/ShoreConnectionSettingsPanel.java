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
package dk.dma.epd.shore.gui.settingtabs;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.status.AisStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.ShoreServiceStatus;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;

public class ShoreConnectionSettingsPanel extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    
    private List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();
    
    private JLabel lblSsLastContactStatus;
    private JLabel lblSsContactStatus;
    private JLabel lblAisReceptionStatus;
    private JLabel lblAisSendingStatus;
    private JLabel lblAisLastReceptionStatus;
    private JLabel lblAisLastSendingStatus;
	private JLabel lblWmsContactStatus;
	private JLabel lblWmsLastContactStatus;

    public ShoreConnectionSettingsPanel() {
        super("Connections", new ImageIcon(
                ShoreConnectionSettingsPanel.class.getResource("/images/settings/connections.png")));
        setLayout(null);
        
        
        /************** Shore Services ***************/

        JPanel shorePanel = new JPanel();
        shorePanel.setBounds(6, 6, 438, 80);
        shorePanel.setLayout(null);
        shorePanel.setBorder(new TitledBorder(
                null, "Shore Services", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                
        JLabel lblSsContact = new JLabel("Contact:");
        lblSsContact.setBounds(16, 20, 53, 16);
        shorePanel.add(lblSsContact);
        
        JLabel lblSsLastContact = new JLabel("Last Contact:");
        lblSsLastContact.setBounds(16, 45, 83, 16);
        shorePanel.add(lblSsLastContact);
        
        lblSsLastContactStatus = new JLabel("null");
        lblSsLastContactStatus.setBounds(121, 45, 299, 16);
        shorePanel.add(lblSsLastContactStatus);
        
        lblSsContactStatus = new JLabel("null");
        lblSsContactStatus.setBounds(121, 20, 299, 16);
        shorePanel.add(lblSsContactStatus);
        
        this.add(shorePanel);
        
        
        /************** AIS settings ***************/
        
        JPanel aisPanel = new JPanel();
        aisPanel.setBounds(6, 98, 438, 129);
        aisPanel.setLayout(null);
        aisPanel.setBorder(new TitledBorder(
                null, "AIS", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        JLabel lblReception = new JLabel("Reception:");
        lblReception.setBounds(16, 20, 66, 16);
        aisPanel.add(lblReception);
        
        JLabel lblSending = new JLabel("Sending:");
        lblSending.setBounds(16, 45, 61, 16);
        aisPanel.add(lblSending);
        
        JLabel lblLastReception = new JLabel("Last reception:");
        lblLastReception.setBounds(16, 70, 93, 16);
        aisPanel.add(lblLastReception);
        
        JLabel lblLastSending = new JLabel("Last Sending:");
        lblLastSending.setBounds(16, 95, 84, 16);
        aisPanel.add(lblLastSending);
        
        lblAisSendingStatus = new JLabel("null");
        lblAisSendingStatus.setBounds(121, 45, 298, 16);
        aisPanel.add(lblAisSendingStatus);
        
        lblAisReceptionStatus = new JLabel("null");
        lblAisReceptionStatus.setBounds(121, 20, 298, 16);
        aisPanel.add(lblAisReceptionStatus);
        
        lblAisLastReceptionStatus = new JLabel("null");
        lblAisLastReceptionStatus.setBounds(121, 70, 298, 16);
        aisPanel.add(lblAisLastReceptionStatus);
        
        lblAisLastSendingStatus = new JLabel("null");
        lblAisLastSendingStatus.setBounds(121, 95, 298, 16);
        aisPanel.add(lblAisLastSendingStatus);
        
        this.add(aisPanel);
        
        
        /************** WMS settings ***************/
        
        JPanel wmsPanel = new JPanel();
        wmsPanel.setBounds(6, 239, 438, 80);
        wmsPanel.setLayout(null);
        wmsPanel.setBorder(new TitledBorder(
                null, "WMS", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        this.add(wmsPanel);
        
        JLabel lblWmsContact = new JLabel("Contact:");
        lblWmsContact.setBounds(16, 20, 53, 16);
        wmsPanel.add(lblWmsContact);
        
        JLabel lblWmsLastContact = new JLabel("Last Contact");
        lblWmsLastContact.setBounds(16, 45, 79, 16);
        wmsPanel.add(lblWmsLastContact);
        
        lblWmsContactStatus = new JLabel("null");
        lblWmsContactStatus.setBounds(121, 20, 299, 16);
        wmsPanel.add(lblWmsContactStatus);
        
        lblWmsLastContactStatus = new JLabel("null");
        lblWmsLastContactStatus.setBounds(121, 45, 299, 16);
        wmsPanel.add(lblWmsLastContactStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        	
    	for (IStatusComponent statusComponent : this.statusComponents) {
			
    		ComponentStatus componentStatus = statusComponent.getStatus();
			
			if (componentStatus.getName().equals("Shore services")) {
				
				ShoreServiceStatus shoreServiceStatus = (ShoreServiceStatus) componentStatus;
				
		        this.lblSsContactStatus.setText(shoreServiceStatus.getStatus().toString());
		        this.setStatusColor(lblSsContactStatus);
		        this.lblSsLastContactStatus.setText(Formatter.formatLongDateTime(shoreServiceStatus.getLastContact()));
			}
			
			if (componentStatus.getName().equals("AIS")) {

				componentStatus.getStatus();
				AisStatus aisStatus = (AisStatus) componentStatus;
				
		        this.lblAisReceptionStatus.setText(aisStatus.getReceiveStatus().toString());
		        this.lblAisSendingStatus.setText(aisStatus.getSendStatus().toString());
		        this.setStatusColor(lblAisReceptionStatus);
		        this.setStatusColor(lblAisSendingStatus);
		        this.lblAisLastReceptionStatus.setText(Formatter.formatLongDateTime(aisStatus.getLastReceived()));
		        this.lblAisLastSendingStatus.setText(Formatter.formatLongDateTime(aisStatus.getLastSent()));
			}
		}
        
        // Get WMS connections.
        HttpURLConnection connection = null;
        
        	try {
        		// Create a url to the website which stores the wms.
				URL urlToWms = new URL(EPDShore.getInstance().getSettings().getGuiSettings().getWmsQuery());
				
				// Open connection to the url.
				connection = (HttpURLConnection) urlToWms.openConnection();
				connection.connect();
				
				// Change the status text if a file was found.
				if (connection.getResponseCode() == 200) {
					
					// TODO: Should do some checking if the xml document contains valid information,
					// before declaring it "OK".
					
					this.lblWmsContactStatus.setText("OK");
				}
				
			} catch (MalformedURLException e) {
				// An error occured while loading the url.
				// It might be that the connection request returned a 404.
				this.lblWmsContactStatus.setText("ERROR");
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	setStatusColor(this.lblWmsContactStatus);
        	this.lblWmsLastContactStatus.setText(Formatter.formatLongDateTime(new Date()));
    }

	public void addStatusComponent(IStatusComponent statusComponent) {
		this.statusComponents.add(statusComponent);
	}

    protected void setStatusColor(JLabel statusLabel) {
    	
    	EPDShore.getInstance().getBeanHandler();
        
        // Grab the status text.
        String statusText = statusLabel.getText();
        
        Color green  = new Color(130, 165, 80);
        Color red    = new Color(165, 80, 80);
        Color yellow = new Color(208, 192, 61);
        
        // Color the status labels.
        if (statusText.equals("OK")) {
            statusLabel.setForeground(green);
        } else if (statusText.equals("ERROR")) {
            statusLabel.setForeground(red);
        } else if (statusText.equals("UNKNOWN")) {
            statusLabel.setForeground(yellow);
        } else if (statusText.equals("PARTIAL")) {
            statusLabel.setForeground(yellow);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {}
}
