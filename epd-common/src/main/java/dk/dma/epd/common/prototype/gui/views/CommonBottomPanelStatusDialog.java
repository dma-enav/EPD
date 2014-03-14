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
import java.awt.Component;
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
import dk.dma.epd.common.prototype.status.ShoreServiceStatus;
import dk.dma.epd.common.text.Formatter;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import java.awt.GridLayout;

/**
 * Displays the textual status of the services in the bottom panel
 */
public class CommonBottomPanelStatusDialog extends JDialog implements ActionListener {

    protected final static Font TITLE_FONT = new Font("LucidaGrande", Font.BOLD, 14);
    protected final static Font PLAIN_FONT = new Font("LucidaGrande", Font.PLAIN, 11);
    protected final static int PANEL_HEIGHT = 160;
    protected JPanel statusPanel;
    protected Timer timer;
    protected List<IStatusComponent> statusComponents;

    private static final long serialVersionUID = 1L;
    private JLabel lblContactStatus;
    private JLabel lblLastContactStatus;
    private JLabel lblReceptionStatus;
    private JLabel lblSendingStatus;
    private JLabel lblLastReceptionStatus;
    private JLabel lblLastSendStatus;
    private JLabel lblAisReceptionStatus;
    private JLabel lblAisSendingStatus;
    private JLabel lblAisLastReceptionStatus;
    private JLabel lblAisLastSendStatus;
    private JButton btnClose;
    
    
    /**
     * <p>Creates a CommonBottomPanelStatusDialog.<br>
     * This will create the window, a panel for status and a panel at
     * the bottom of the window with a "close" button.
     * @param statusComponents
     *          The status components which stores status.
     */
    public CommonBottomPanelStatusDialog(List<IStatusComponent> statusComponents) {
        
        super(EPD.getInstance().getMainFrame(), "Status", true);
        this.statusComponents = statusComponents;

        // Start the timer to update status while window is open.
        timer = new Timer(500, this);
        
        // Window settings.
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setSize(310, 0);
        this.getContentPane().setLayout(new BorderLayout());

        // Status panel settings.
        this.statusPanel = new JPanel();
        this.getContentPane().add(statusPanel, BorderLayout.CENTER);
        this.statusPanel.setLayout(new GridLayout(0, 1));
        
        // Bottom button panel.
        JPanel btnPanel = new JPanel();
        btnClose = new JButton("Close");
        btnClose.addActionListener(this);
        btnPanel.add(btnClose);
        this.getContentPane().add(btnPanel, BorderLayout.SOUTH);        
    }

    /**
     * Creates the status panels. If any additional panels were given
     * it creates adds those too.
     * @param additionalStatusPanels
     *          Additional status panels to be added.
     */
    protected void createStatusPanels(List<JPanel> additionalStatusPanels) {
        
        // Add additional panels first.
        if (additionalStatusPanels != null) {
            for (JPanel statusPanel : additionalStatusPanels) {
                this.add(statusPanel);
            }
        }
        
        // AIS status
        JPanel aisPanel = new JPanel();
        aisPanel.setBorder(
                new TitledBorder(null, "AIS", TitledBorder.LEADING, TitledBorder.TOP, TITLE_FONT));
        aisPanel.setLayout(null);
        this.add(aisPanel);
        
        JLabel lblAisReception = new JLabel("Reception:");
        lblAisReception.setFont(PLAIN_FONT);
        lblAisReception.setBounds(16, 30, 66, 16);
        aisPanel.add(lblAisReception);
        
        this.lblAisReceptionStatus = new JLabel("status");
        this.lblAisReceptionStatus.setFont(PLAIN_FONT);
        this.lblAisReceptionStatus.setBounds(121, 30, 165, 16);
        aisPanel.add(this.lblAisReceptionStatus);
        
        JLabel lblAisSending = new JLabel("Sending:");
        lblAisSending.setFont(PLAIN_FONT);
        lblAisSending.setBounds(16, 55, 61, 16);
        aisPanel.add(lblAisSending);
        
        this.lblAisSendingStatus = new JLabel("status");
        this.lblAisSendingStatus.setFont(PLAIN_FONT);
        this.lblAisSendingStatus.setBounds(121, 55, 165, 16);
        aisPanel.add(this.lblAisSendingStatus);
        
        JLabel lblAisLastReception = new JLabel("Last reception:");
        lblAisLastReception.setFont(PLAIN_FONT);
        lblAisLastReception.setBounds(16, 80, 93, 16);
        aisPanel.add(lblAisLastReception);
        
        this.lblAisLastReceptionStatus = new JLabel("status");
        this.lblAisLastReceptionStatus.setFont(PLAIN_FONT);
        this.lblAisLastReceptionStatus.setBounds(121, 80, 165, 16);
        aisPanel.add(this.lblAisLastReceptionStatus);
        
        JLabel lblAisLastSend = new JLabel("Last send:");
        lblAisLastSend.setFont(PLAIN_FONT);
        lblAisLastSend.setBounds(16, 105, 66, 16);
        aisPanel.add(lblAisLastSend);
        
        this.lblAisLastSendStatus = new JLabel("status");
        this.lblAisLastSendStatus.setFont(PLAIN_FONT);
        this.lblAisLastSendStatus.setBounds(121, 105, 165, 16);
        aisPanel.add(this.lblAisLastSendStatus);
        
        // Shore services status
        JPanel shoreServicesPanel = new JPanel();
        shoreServicesPanel.setBorder(
                new TitledBorder(null, "Shore Services", TitledBorder.LEADING, TitledBorder.TOP, TITLE_FONT));
        shoreServicesPanel.setLayout(null);
        this.add(shoreServicesPanel);
        
        JLabel lblContact = new JLabel("Contact:");
        lblContact.setFont(PLAIN_FONT);
        lblContact.setBounds(16, 30, 53, 16);
        shoreServicesPanel.add(lblContact);
        
        this.lblContactStatus = new JLabel("status");
        this.lblContactStatus.setFont(PLAIN_FONT);
        this.lblContactStatus.setBounds(121, 30, 165, 16);
        shoreServicesPanel.add(this.lblContactStatus);
        
        JLabel lblLastContact = new JLabel("Last contact:");
        lblLastContact.setFont(PLAIN_FONT);
        lblLastContact.setBounds(16, 55, 81, 16);
        shoreServicesPanel.add(lblLastContact);
        
        this.lblLastContactStatus = new JLabel("status");
        this.lblLastContactStatus.setFont(PLAIN_FONT);
        this.lblLastContactStatus.setBounds(121, 55, 165, 16);
        shoreServicesPanel.add(this.lblLastContactStatus);
        
        // Maritime Cloud status.
        JPanel maritimeStatuspanel = new JPanel();
        maritimeStatuspanel.setBorder(
                new TitledBorder(null, "Maritime Cloud", TitledBorder.LEADING, TitledBorder.TOP, TITLE_FONT));
        this.add(maritimeStatuspanel);
        maritimeStatuspanel.setLayout(null);
        
        JLabel lblReception = new JLabel("Reception:");
        lblReception.setFont(PLAIN_FONT);
        lblReception.setBounds(16, 30, 66, 16);
        maritimeStatuspanel.add(lblReception);
        
        this.lblReceptionStatus = new JLabel("status");
        this.lblReceptionStatus.setFont(PLAIN_FONT);
        this.lblReceptionStatus.setBounds(121, 30, 165, 16);
        maritimeStatuspanel.add(this.lblReceptionStatus);
        
        JLabel lblSending = new JLabel("Sending:");
        lblSending.setFont(PLAIN_FONT);
        lblSending.setBounds(16, 55, 61, 16);
        maritimeStatuspanel.add(lblSending);
        
        this.lblSendingStatus = new JLabel("status");
        this.lblSendingStatus.setFont(PLAIN_FONT);
        this.lblSendingStatus.setBounds(121, 55, 165, 16);
        maritimeStatuspanel.add(this.lblSendingStatus);
        
        JLabel lblLastReception = new JLabel("Last reception:");
        lblLastReception.setFont(PLAIN_FONT);
        lblLastReception.setBounds(16, 80, 93, 16);
        maritimeStatuspanel.add(lblLastReception);
        
        this.lblLastReceptionStatus = new JLabel("status");
        this.lblLastReceptionStatus.setFont(PLAIN_FONT);
        this.lblLastReceptionStatus.setBounds(121, 80, 165, 16);
        maritimeStatuspanel.add(this.lblLastReceptionStatus);
        
        JLabel lblLastSend = new JLabel("Last send:");
        lblLastSend.setFont(PLAIN_FONT);
        lblLastSend.setBounds(16, 105, 66, 16);
        maritimeStatuspanel.add(lblLastSend);
        
        this.lblLastSendStatus = new JLabel("status");
        this.lblLastSendStatus.setFont(PLAIN_FONT);
        this.lblLastSendStatus.setBounds(121, 105, 165, 16);
        maritimeStatuspanel.add(this.lblLastSendStatus);        
    }
    
    /**
     * Update status text of each status component.
     */
    protected void showStatus() {
        
        for (IStatusComponent iStatusComponent : this.statusComponents) {
            ComponentStatus componentStatus = iStatusComponent.getStatus();
                        
            if (componentStatus instanceof AisStatus) {
                
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
    protected void colorStatusLabel(JLabel label) {
        
        // Get the status text.
        String statusText = label.getText();
        
        if (statusText.equals("OK")) {
            label.setForeground(Color.GREEN);
        } else if (statusText.equals("ERROR")) {
            label.setForeground(Color.RED);
        } else if (statusText.equals("UNKNOWN")) {
            label.setForeground(Color.GRAY);
        } else if (statusText.equals("PARTIAL")) {
            label.setForeground(Color.YELLOW);
        }
    }
    
    /**
     * Adds a component to the status frame, and resizes the window
     * height add space for the new panel.
     * @param comp
     *          The JPanel to be added.
     * @return
     */
    @Override
    public Component add(Component comp) {
        
        if (comp instanceof JPanel) {            
            this.statusPanel.add(comp);
            this.setSize(310, this.getSize().height + PANEL_HEIGHT);
            
        }
        
        return comp;
    }

    /**
     * Handles an action event. This is called every 500 ms by the
     * timer object and the status is updated.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // If the source is the timer, check for status update.
        if (e.getSource() == this.timer ) {
            this.showStatus();
        
        // If the source is the button, stop the timer and close the window.
        } else if (e.getSource() == this.btnClose) {
            this.timer.stop();
            this.dispose();
        }
    }
}
