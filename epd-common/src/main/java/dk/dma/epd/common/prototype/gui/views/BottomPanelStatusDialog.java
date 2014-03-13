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
package dk.dma.epd.common.prototype.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.status.AisStatus;
import dk.dma.epd.common.prototype.status.CloudStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.PntStatus;
import dk.dma.epd.common.prototype.status.ShoreServiceStatus;
import dk.dma.epd.common.text.Formatter;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

/**
 * Displays the textual status of the services in the bottom panel
 */
public class BottomPanelStatusDialog extends JDialog implements ActionListener {

    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private JLabel lblContactStatus;
    private JLabel lblLastContactStatus;
    private JLabel lblReceptionStatus;
    private JLabel lblSendingStatus;
    private JLabel lblLastReceptionStatus;
    private JLabel lblLastSendStatus;
    private JLabel lblPositionStatus;
    private JLabel lblSourceStatus;
    private JLabel lblLastPNTDataStatus;
    private JLabel lblAisReceptionStatus;
    private JLabel lblAisSendingStatus;
    private JLabel lblAisLastReceptionStatus;
    private JLabel lblAisLastSendStatus;
    
    /**
     * Constructor
     */
    public BottomPanelStatusDialog() {
        
        super(EPD.getInstance().getMainFrame(), "Status", true);
                
        // Fonts.
        Font titleFont = new Font("LucidaGrande", Font.BOLD, 14);
        Font statusFont = new Font("LucidaGrande", Font.PLAIN, 11);

        // Window settings.
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(310, 632);
        this.setLocationRelativeTo(EPD.getInstance().getMainFrame());
        this.getContentPane().setLayout(new BorderLayout(10, 10));
        

        // Status panel.
        JPanel statusPanel = new JPanel();
        getContentPane().add(statusPanel, BorderLayout.CENTER);
        statusPanel.setLayout(null);
        
        // PNT status
        JPanel pntPanel = new JPanel();
        pntPanel.setBorder(
                new TitledBorder(null, "PNT", TitledBorder.LEADING, TitledBorder.TOP, titleFont));
        pntPanel.setBounds(6, 6, 292, 120);
        pntPanel.setLayout(null);
        statusPanel.add(pntPanel);
        
        JLabel lblPosition = new JLabel("Position:");
        lblPosition.setFont(statusFont);
        lblPosition.setBounds(16, 30, 55, 16);
        pntPanel.add(lblPosition);
        
        this.lblPositionStatus = new JLabel("status");
        this.lblPositionStatus.setFont(statusFont);
        this.lblPositionStatus.setBounds(121, 30, 165, 16);
        pntPanel.add(this.lblPositionStatus);
        
        JLabel lblSource = new JLabel("Source:");
        lblSource.setFont(statusFont);
        lblSource.setBounds(16, 55, 61, 16);
        pntPanel.add(lblSource);
        
        this.lblSourceStatus = new JLabel("status");
        this.lblSourceStatus.setFont(statusFont);
        this.lblSourceStatus.setBounds(121, 55, 165, 16);
        pntPanel.add(this.lblSourceStatus);
        
        JLabel lblLastPntData = new JLabel("Last PNT data:");
        lblLastPntData.setFont(statusFont);
        lblLastPntData.setBounds(16, 80, 90, 16);
        pntPanel.add(lblLastPntData);
        
        this.lblLastPNTDataStatus = new JLabel("status");
        this.lblLastPNTDataStatus.setFont(statusFont);
        this.lblLastPNTDataStatus.setBounds(121, 80, 165, 16);
        pntPanel.add(this.lblLastPNTDataStatus);
        
        // AIS status
        JPanel aisPanel = new JPanel();
        aisPanel.setBorder(
                new TitledBorder(null, "AIS", TitledBorder.LEADING, TitledBorder.TOP, titleFont));
        aisPanel.setBounds(6, 138, 292, 150);
        aisPanel.setLayout(null);
        statusPanel.add(aisPanel);
        
        JLabel lblAisReception = new JLabel("Reception:");
        lblAisReception.setFont(statusFont);
        lblAisReception.setBounds(16, 30, 66, 16);
        aisPanel.add(lblAisReception);
        
        this.lblAisReceptionStatus = new JLabel("status");
        this.lblAisReceptionStatus.setFont(statusFont);
        this.lblAisReceptionStatus.setBounds(121, 30, 165, 16);
        aisPanel.add(this.lblAisReceptionStatus);
        
        JLabel lblAisSending = new JLabel("Sending:");
        lblAisSending.setFont(statusFont);
        lblAisSending.setBounds(16, 55, 61, 16);
        aisPanel.add(lblAisSending);
        
        this.lblAisSendingStatus = new JLabel("status");
        this.lblAisSendingStatus.setFont(statusFont);
        this.lblAisSendingStatus.setBounds(121, 55, 165, 16);
        aisPanel.add(this.lblAisSendingStatus);
        
        JLabel lblAisLastReception = new JLabel("Last reception:");
        lblAisLastReception.setFont(statusFont);
        lblAisLastReception.setBounds(16, 80, 93, 16);
        aisPanel.add(lblAisLastReception);
        
        this.lblAisLastReceptionStatus = new JLabel("status");
        this.lblAisLastReceptionStatus.setFont(statusFont);
        this.lblAisLastReceptionStatus.setBounds(121, 80, 165, 16);
        aisPanel.add(this.lblAisLastReceptionStatus);
        
        JLabel lblAisLastSend = new JLabel("Last send:");
        lblAisLastSend.setFont(statusFont);
        lblAisLastSend.setBounds(16, 105, 66, 16);
        aisPanel.add(lblAisLastSend);
        
        this.lblAisLastSendStatus = new JLabel("status");
        this.lblAisLastSendStatus.setFont(statusFont);
        this.lblAisLastSendStatus.setBounds(121, 105, 165, 16);
        aisPanel.add(this.lblAisLastSendStatus);
        
        // Shore services status
        JPanel shoreServicesPanel = new JPanel();
        shoreServicesPanel.setBorder(
                new TitledBorder(null, "Shore Services", TitledBorder.LEADING, TitledBorder.TOP, titleFont));
        shoreServicesPanel.setBounds(6, 300, 292, 95);
        shoreServicesPanel.setLayout(null);
        statusPanel.add(shoreServicesPanel);
        
        JLabel lblContact = new JLabel("Contact:");
        lblContact.setFont(statusFont);
        lblContact.setBounds(16, 30, 53, 16);
        shoreServicesPanel.add(lblContact);
        
        this.lblContactStatus = new JLabel("status");
        this.lblContactStatus.setFont(statusFont);
        this.lblContactStatus.setBounds(121, 30, 165, 16);
        shoreServicesPanel.add(this.lblContactStatus);
        
        JLabel lblLastContact = new JLabel("Last contact:");
        lblLastContact.setFont(statusFont);
        lblLastContact.setBounds(16, 55, 81, 16);
        shoreServicesPanel.add(lblLastContact);
        
        this.lblLastContactStatus = new JLabel("status");
        this.lblLastContactStatus.setFont(statusFont);
        this.lblLastContactStatus.setBounds(121, 55, 165, 16);
        shoreServicesPanel.add(this.lblLastContactStatus);
        
        // Maritime Cloud status.
        JPanel maritimeStatuspanel = new JPanel();
        maritimeStatuspanel.setBorder(
                new TitledBorder(null, "Maritime Cloud", TitledBorder.LEADING, TitledBorder.TOP, titleFont));
        maritimeStatuspanel.setBounds(6, 407, 292, 150);
        statusPanel.add(maritimeStatuspanel);
        maritimeStatuspanel.setLayout(null);
        
        JLabel lblReception = new JLabel("Reception:");
        lblReception.setFont(statusFont);
        lblReception.setBounds(16, 30, 66, 16);
        maritimeStatuspanel.add(lblReception);
        
        this.lblReceptionStatus = new JLabel("status");
        this.lblReceptionStatus.setFont(statusFont);
        this.lblReceptionStatus.setBounds(121, 30, 165, 16);
        maritimeStatuspanel.add(this.lblReceptionStatus);
        
        JLabel lblSending = new JLabel("Sending:");
        lblSending.setFont(statusFont);
        lblSending.setBounds(16, 55, 61, 16);
        maritimeStatuspanel.add(lblSending);
        
        this.lblSendingStatus = new JLabel("status");
        this.lblSendingStatus.setFont(statusFont);
        this.lblSendingStatus.setBounds(121, 55, 165, 16);
        maritimeStatuspanel.add(this.lblSendingStatus);
        
        JLabel lblLastReception = new JLabel("Last reception:");
        lblLastReception.setFont(statusFont);
        lblLastReception.setBounds(16, 80, 93, 16);
        maritimeStatuspanel.add(lblLastReception);
        
        this.lblLastReceptionStatus = new JLabel("status");
        this.lblLastReceptionStatus.setFont(statusFont);
        this.lblLastReceptionStatus.setBounds(121, 80, 165, 16);
        maritimeStatuspanel.add(this.lblLastReceptionStatus);
        
        JLabel lblLastSend = new JLabel("Last send:");
        lblLastSend.setFont(statusFont);
        lblLastSend.setBounds(16, 105, 66, 16);
        maritimeStatuspanel.add(lblLastSend);
        
        this.lblLastSendStatus = new JLabel("status");
        this.lblLastSendStatus.setFont(statusFont);
        this.lblLastSendStatus.setBounds(121, 105, 165, 16);
        maritimeStatuspanel.add(this.lblLastSendStatus);

        // -----------------------------------
        // Bottom button panel.
        JPanel btnPanel = new JPanel();
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(this);
        btnPanel.add(btnClose);
        this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update status text of each status component.
     * @param statusComponents
     */
    public void showStatus(List<IStatusComponent> statusComponents) {
        
        for (IStatusComponent iStatusComponent : statusComponents) {
            ComponentStatus componentStatus = iStatusComponent.getStatus();
            
            System.out.println("Status: " + componentStatus.toString());
            
            if (componentStatus instanceof PntStatus) {
                
                PntStatus pntStatus = (PntStatus) componentStatus;
                this.lblPositionStatus.setText(pntStatus.getStatus().toString());
                this.lblLastPNTDataStatus.setText(Formatter.formatLongDateTime(pntStatus.getPntData().getLastUpdated()));
                
                try {
                    this.lblSourceStatus.setText(pntStatus.getPntData().getPntSource().toString());                    
                } catch (NullPointerException e) {
                    this.lblSourceStatus.setText("N/A");
                }
                
                this.colorStatusLabel(this.lblPositionStatus);
                
            } else if (componentStatus instanceof AisStatus) {
                
                AisStatus aisStatus = (AisStatus) componentStatus;
                this.lblAisReceptionStatus.setText(aisStatus.getReceiveStatus().toString());
                this.lblAisSendingStatus.setText(aisStatus.getSendStatus().toString());
                this.lblAisLastReceptionStatus.setText(Formatter.formatLongDateTime(aisStatus.getLastReceived()));
                this.lblAisLastSendStatus.setText(Formatter.formatLongDateTime(aisStatus.getLastSent()));
                
                this.colorStatusLabel(this.lblAisReceptionStatus);
                this.colorStatusLabel(this.lblAisSendingStatus);
                
            } else if (componentStatus instanceof ShoreServiceStatus) {
                
                ShoreServiceStatus shoreServices = (ShoreServiceStatus) componentStatus;
                this.lblContactStatus.setText(shoreServices.getStatus().toString());
                this.lblLastContactStatus.setText(Formatter.formatLongDateTime(shoreServices.getLastContact()));
                
                this.colorStatusLabel(this.lblContactStatus);
                this.colorStatusLabel(this.lblLastContactStatus);
                
            } else if (componentStatus instanceof CloudStatus) {
                
                CloudStatus cloudStatus = (CloudStatus) componentStatus;
                this.lblReceptionStatus.setText(cloudStatus.getStatus().toString());
                this.lblSendingStatus.setText(cloudStatus.getSendStatus().toString());
                this.lblLastReceptionStatus.setText(Formatter.formatLongDateTime(cloudStatus.getLastReceived()));
                this.lblLastSendStatus.setText(Formatter.formatLongDateTime(cloudStatus.getLastSent()));
                
                this.colorStatusLabel(this.lblReceptionStatus);
                this.colorStatusLabel(this.lblSendingStatus);
            }
        }
    }
    
    /**
     * Used to color a JLabels font.
     * @param label
     *          Label to be colored.
     */
    private void colorStatusLabel(JLabel label) {
        
        // Get the status text.
        String statusText = label.getText();
        
        if (statusText.equals("OK")) {
            label.setForeground(Color.GREEN);
        } else if (statusText.equals("ERROR")) {
            label.setForeground(Color.RED);
        } else if (statusText.equals("UNKNOWN")) {
            label.setForeground(Color.GRAY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Close the window.
        this.dispose();
    }
}
