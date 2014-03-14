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
package dk.dma.epd.shore.gui.notification;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static dk.dma.epd.common.graphics.GraphicsUtil.fixSize;
import static dk.dma.epd.common.graphics.GraphicsUtil.minSize;
import static dk.dma.epd.common.graphics.GraphicsUtil.bold;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.gui.notification.NotificationDetailPanel;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.text.Formatter;

/**
 * Displays relevant route suggestion detail information 
 * for the selected route in the notification center.
 */
public class StrategicRouteNotificationDetailPanel extends NotificationDetailPanel<StrategicRouteNotification> {

    private static final long serialVersionUID = 1L;

    JLabel transactionTxt = new JLabel(" ");
    JLabel statusTxt = new JLabel(" ");
    
    JLabel mmsiTxt = new JLabel(" ");
    JLabel nameTxt = new JLabel(" ");
    JLabel callSignTxt = new JLabel(" ");
    JLabel destinationTxt = new JLabel(" ");

    JLabel typeTxt = new JLabel(" ");
    JLabel lengthTxt = new JLabel(" ");
    JLabel widthTxt = new JLabel(" ");
    JLabel draughtTxt = new JLabel(" ");

    JLabel sogTxt = new JLabel(" ");
    JLabel cogTxt = new JLabel(" ");

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
    
    String[] infoTitles = { 
            "MMSI:", "Name:", "Call Sign:", "Destination:", "SOG:", 
            "Type:", "Lenght:", "Width:", "Draught:",  "COG:" };
    JLabel[] infoLabels = {
            mmsiTxt, nameTxt, callSignTxt, destinationTxt, sogTxt,
            typeTxt, lengthTxt, widthTxt, draughtTxt, cogTxt };
    int[] infoCols = { 5, 5 };
    
    /**
     * Constructor
     */
    public StrategicRouteNotificationDetailPanel() {
        super();
        
        buildGUI();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildGUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create the transaction and status labels
        Insets insets5  = new Insets(5, 5, 5, 5);
        add(bold(new JLabel("Transaction ID:")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        add(fixSize(transactionTxt, 100), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        add(bold(new JLabel("Status:")), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        add(fixSize(statusTxt, 80), new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));

        // Create the info panel
        Insets insets2  = new Insets(2, 5, 2, 5);
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Ship Info"));
        for (int col = 0, index = 0; col < infoCols.length; col++) {
            for (int row = 0; row < infoCols[col]; row++, index++) {
                infoPanel.add(bold(new JLabel(infoTitles[index])), new GridBagConstraints(col * 2, row, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
                infoPanel.add(minSize(infoLabels[index], 40), new GridBagConstraints(col * 2 + 1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
            }
        }
        add(infoPanel, new GridBagConstraints(0, 1, 4, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        // Create the messages tabbed pane
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UIManager.getColor("Separator.shadow")));
        tabPanel.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setOpaque(false);
        add(tabPanel, new GridBagConstraints(0, 2, 4, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));   
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(StrategicRouteNotification notification) {
        this.notification = notification;
        
        // If no notification is set, reset all fields
        if (notification == null) {
            transactionTxt.setText("");
            statusTxt.setText("");
            for (int x = 0; x < infoLabels.length; x++) {
                infoLabels[x].setText("XXX");
            }
            setNegotiationTabs(null);
            return;
        }
        
        StrategicRouteNegotiationData message = notification.get();
        
        transactionTxt.setText(String.valueOf(notification.getId()));
        statusTxt.setText(message.getStatus().toString());
        statusTxt.setForeground(getStatusColor(message.getStatus()));

        // Start by re-setting all info fields to N/A
        for (int x = 0; x < infoLabels.length; x++) {
            infoLabels[x].setText("N/A");
        }
        mmsiTxt.setText(String.valueOf(message.getRouteMessage().get(0).getMmsi()));
        
        VesselStaticData staticData = notification.getVesselStaticData();
        if (staticData != null) {
            nameTxt.setText(staticData.getTrimmedName());
            nameTxt.setToolTipText(staticData.getTrimmedName());
            callSignTxt.setText(staticData.getTrimmedCallsign());
            if (!StringUtils.isBlank(staticData.getTrimmedDestination())) {
                destinationTxt.setText(staticData.getTrimmedDestination());
                destinationTxt.setToolTipText(staticData.getTrimmedDestination());
            }
            typeTxt.setText(staticData.getShipType().toString());
            typeTxt.setToolTipText(staticData.getShipType().toString());
            lengthTxt.setText(staticData.getDimBow() + staticData.getDimStern() + "");
            widthTxt.setText(staticData.getDimPort() + staticData.getDimStarboard() + "");
            draughtTxt.setText(staticData.getDraught() / 10 + "");
        }
        
        VesselPositionData positionData = notification.getVesselPositionData();
        if (positionData != null) {
            sogTxt.setText(Formatter.formatCurrentSpeed((double)positionData.getSog()));
            cogTxt.setText(Formatter.formatDegrees((double)positionData.getCog(), 0));            
        }
        
        setNegotiationTabs(message);
    }   

    /**
     * Refreshes the tabs of the tabbed pane
     * @param message the route negotiation data
     */
    private void setNegotiationTabs(StrategicRouteNegotiationData message) {
        tabbedPane.removeAll();
        
        if (message == null) {
            return;
        }

        // for each route message
        for (int i = message.getRouteMessage().size() - 1; i >= 0; i--) {
            
            StrategicRouteRequestMessage request = message.getRouteMessage().get(i);
            StrategicRouteRequestReply reply = (message.getRouteReply().size() > i)
                    ? message.getRouteReply().get(i)
                    : null;
            
            StrategicNegotiationView negotiation = 
                    new StrategicNegotiationView(request, reply);

            negotiation.setOpaque(false);
            tabbedPane.addTab("Message " + (i + 1), null, negotiation, null);            
        }
    }
    
    /**
     * Returns the color to use for the given status
     * @param status the status
     * @return the color to use for the given status
     */
    private Color getStatusColor(StrategicRouteStatus status) {
        switch (status) {
        case PENDING:       return Color.YELLOW;
        case AGREED:        return new Color(130, 165, 80);
        case REJECTED:      return new Color(165, 80, 80);
        case NEGOTIATING:   return Color.YELLOW;
        case CANCELED:      return new Color(165, 80, 80);
        }
        return Color.LIGHT_GRAY;
    }
}


/**
 * Displays the messages associated with a strategic route request
 */
class StrategicNegotiationView extends JPanel {

    private static final long serialVersionUID = 1L;

    JLabel lblNoReply;
    JPanel replyPanel;

    /**
     * Create the panel.
     * 
     * @param requestMsg the request message
     * @param replyMsg the reply message
     */
    public StrategicNegotiationView(
            StrategicRouteRequestMessage requestMsg,
            StrategicRouteRequestReply replyMsg) {

        super(new GridBagLayout());
        setOpaque(false);
        
        Insets insets1  = new Insets(5, 5, 5, 5);
        Insets insets2  = new Insets(5, 5, 2, 5);
        
        // *************************
        // Create the request panel
        // *************************
        JPanel requestPanel = new JPanel(new GridBagLayout());
        requestPanel.setBorder(new TitledBorder("Voyage Request"));
        add(requestPanel, 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));

        requestPanel.add(bold(new JLabel("Request Message:")), 
                new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
        requestPanel.add(createTextArea(requestMsg.getMessage()), 
                new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));
        requestPanel.add(bold(new JLabel("Sent:")), 
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        requestPanel.add(new JLabel(Formatter.formatLongDateTime(requestMsg.getSent())), 
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        requestPanel.add(bold(new JLabel("Route Name:")), 
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        requestPanel.add(new JLabel(requestMsg.getRoute().getName()), 
                new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        
        // **********************
        // Create the reply panel
        // **********************
        
        JPanel replyPanel = new JPanel(new GridBagLayout());
        replyPanel.setBorder(new TitledBorder("STCC Reply"));
        add(replyPanel, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));
        
        // Handle no-reply case
        if (replyMsg == null) {
            replyPanel.add(new JLabel("No reply sent"), 
                    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));
        } else {
        
            // Format the reply
            String type = "";
            if (replyMsg.getStatus() == StrategicRouteStatus.AGREED) {
                type = "Route Approved";
            } else if (replyMsg.getStatus() == StrategicRouteStatus.NEGOTIATING) {
                type = "Route modified and sent for negotiation";
            }
            
            replyPanel.add(bold(new JLabel("Reply Message:")), 
                    new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
            replyPanel.add(createTextArea(replyMsg.getMessage()), 
                    new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));
            replyPanel.add(bold(new JLabel("Sent:")), 
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
            replyPanel.add(new JLabel(Formatter.formatLongDateTime(new Date(replyMsg.getSendDate()))), 
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
            replyPanel.add(bold(new JLabel("Type:")), 
                    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
            replyPanel.add(new JLabel(type), 
                    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        }

        // Add filler
        add(new JLabel(""), 
                new GridBagConstraints(0, 10, 1, 1, 1.0, 1.0, WEST, BOTH, insets1, 0, 0));
    }
    
    /**
     * Creates a text area wrapped in scroll pane
     * @param text the text to display in the text area
     * @return the text area wrapped in a scroll pane
     */
    private JScrollPane createTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setBackground(Color.GRAY);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(100, 40));
        scrollPane.setPreferredSize(new Dimension(100, 40));
        scrollPane.setBorder(new MatteBorder(1, 1, 1, 1, UIManager.getColor("Separator.shadow")));
        
        return scrollPane;
    }
}

