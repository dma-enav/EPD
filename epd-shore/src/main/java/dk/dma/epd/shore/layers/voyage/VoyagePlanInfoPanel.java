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
package dk.dma.epd.shore.layers.voyage;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.service.ServiceEndpoint;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.route.RoutePropertiesDialog;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.voyage.Voyage;

/**
 * This panel contains information about the ship and voyage plan. It also contains the Send Voyage functionality.
 */
public class VoyagePlanInfoPanel extends JPanel implements ActionListener, IChatServiceListener {

    private static final long serialVersionUID = 1L;

    private Voyage voyage;
    private AisHandler aisHandler;
    ChartPanel chartPanel;
    VoyageHandlingLayer voyageHandlingLayer;

    JLabel lblShipName = new JLabel(" ");
    JLabel lblCallSign = new JLabel(" ");
    JLabel lblRouteName = new JLabel(" ");
    JLabel lblCog = new JLabel(" ");
    JLabel lblSog = new JLabel(" ");
    JLabel lblTd = new JLabel(" ");
    JLabel lblETA = new JLabel(" ");
    JTextArea txtMessage = new JTextArea();

    JButton ZoomToShipBtn = new JButton("Zoom to ship in center");
    JButton OpenShipDetailstextBtn = new JButton("Open ship details");
    JButton OpenVpDetalsBtn = new JButton("Open voyage plan details");
    JButton HideOtherVoyagesBtn = new JButton("Hide other voyages");
    JButton sendBtn = new JButton("Send Voyage");

    JTextArea chatMessages;
    private JTextField chatMsgField;
    private JButton sendChatBtn;

    private JScrollPane scrollPane;

    /**
     * Create the panel.
     * 
     * @param voyage
     */
    public VoyagePlanInfoPanel(VoyageHandlingLayer voyageHandlingLayer) {
        super();

        this.voyageHandlingLayer = voyageHandlingLayer;

        setOpaque(false);
        // GridBagLayout gridBagLayout_1 = new GridBagLayout();
        // gridBagLayout_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        // gridBagLayout_1.columnWeights = new double[]{1.0};
        // setLayout(gridBagLayout_1);
        setLayout(new GridBagLayout());
        Insets insets5 = new Insets(2, 5, 2, 5);

        // *******************
        // *** Ship panel
        // *******************
        JPanel shipPanel = new JPanel(new GridBagLayout());
        shipPanel.setOpaque(false);
        shipPanel.setBorder(new TitledBorder("Ship"));
        add(shipPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(2, 5, 5, 0), 0, 0));

        shipPanel.add(new JLabel("Name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shipPanel.add(lblShipName, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        shipPanel.add(new JLabel("Call sign:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shipPanel.add(lblCallSign, new GridBagConstraints(1, 1, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        shipPanel.add(new JLabel("COG:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shipPanel.add(lblCog, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        shipPanel.add(new JLabel("SOG:"), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shipPanel.add(lblSog, new GridBagConstraints(3, 2, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        // *******************
        // *** Route panel
        // *******************
        JPanel routePanel = new JPanel(new GridBagLayout());
        routePanel.setOpaque(false);
        routePanel.setBorder(new TitledBorder("Route"));
        add(routePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(2, 5, 5, 0), 0, 0));

        routePanel.add(new JLabel("Name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblRouteName, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routePanel.add(new JLabel("TD:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblTd, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routePanel.add(new JLabel("ETA:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblETA, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        // *******************
        // *** Action panel
        // *******************
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new TitledBorder("Actions"));
        add(actionPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(2, 5, 5, 0), 0, 0));

        ZoomToShipBtn.addActionListener(this);
        OpenShipDetailstextBtn.addActionListener(this);
        OpenVpDetalsBtn.addActionListener(this);
        HideOtherVoyagesBtn.addActionListener(this);

        ZoomToShipBtn.setFocusable(false);
        OpenShipDetailstextBtn.setFocusable(false);
        OpenVpDetalsBtn.setFocusable(false);
        HideOtherVoyagesBtn.setFocusable(false);

        actionPanel.add(ZoomToShipBtn, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        actionPanel.add(OpenShipDetailstextBtn, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        actionPanel.add(OpenVpDetalsBtn, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        actionPanel.add(HideOtherVoyagesBtn, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        // *******************
        // *** Direct Communication Panel
        // *******************
        JPanel replyPanel = new JPanel(new GridBagLayout());
        replyPanel.setOpaque(false);
        replyPanel.setBorder(new TitledBorder("Direct Communication"));
        add(replyPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, new Insets(2, 5, 5, 0), 0, 0));

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 10, 0 };
        gridBagLayout.rowHeights = new int[] { 10, 100, 20 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0 };
        replyPanel.setLayout(gridBagLayout);

        chatMessages = new JTextArea("");
        chatMessages.setForeground(Color.WHITE);
        chatMessages.setBackground(Color.DARK_GRAY);
        chatMessages.setLineWrap(true);
        chatMessages.setEditable(false);
        chatMessages.setBorder(null);
        chatMessages.setRows(10);

        scrollPane = new JScrollPane(chatMessages);
        scrollPane.setViewportView(chatMessages);
        // scrollPane.setMinimumSize(new Dimension(200, 500));
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.anchor = GridBagConstraints.NORTH;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        replyPanel.add(scrollPane, gbc_scrollPane);

        JPanel panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.anchor = GridBagConstraints.NORTH;
        gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 2;
        replyPanel.add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 2 };
        gbl_panel_1.rowHeights = new int[] { 0 };
        gbl_panel_1.columnWeights = new double[] { 1.0, 0.0 };
        gbl_panel_1.rowWeights = new double[] { 0.0 };
        panel_1.setLayout(gbl_panel_1);

        chatMsgField = new JTextField();
        GridBagConstraints gbc_txtField = new GridBagConstraints();
        gbc_txtField.insets = new Insets(0, 0, 0, 5);
        gbc_txtField.fill = GridBagConstraints.BOTH;
        gbc_txtField.gridx = 0;
        gbc_txtField.gridy = 0;
        panel_1.add(chatMsgField, gbc_txtField);
        chatMsgField.setColumns(10);

        sendChatBtn = new JButton("Send Message");
        GridBagConstraints gbc_sendBtn = new GridBagConstraints();
        gbc_sendBtn.gridx = 1;
        gbc_sendBtn.gridy = 0;
        panel_1.add(sendChatBtn, gbc_sendBtn);

        sendChatBtn.addActionListener(this);

        chatMsgField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendChatMsg");
        chatMsgField.getActionMap().put("sendChatMsg", sendChatMsg);

        // *******************
        // *** Finish Negotitation Panel
        // *******************
        JPanel finishNegotitationPanel = new JPanel(new GridBagLayout());
        finishNegotitationPanel.setOpaque(false);
        finishNegotitationPanel.setBorder(new TitledBorder("Finish Negotitation Handling"));

        // GridBagConstraints gbc_panel = new GridBagConstraints();
        // gbc_panel.insets = new Insets(0, 0, 5, 0);
        // gbc_panel.fill = GridBagConstraints.BOTH;
        // gbc_panel.gridx = 0;
        // gbc_panel.gridy = 4;
        // add(finishNegotitationPanel, gbc_panel);
        add(finishNegotitationPanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets5, 0, 0));

        sendBtn.addActionListener(this);
        sendBtn.requestFocus();
        txtMessage.setLineWrap(true);
        JScrollPane changeScrollPane = new JScrollPane(txtMessage);
        changeScrollPane.setMinimumSize(new Dimension(180, 60));
        changeScrollPane.setPreferredSize(new Dimension(180, 60));

        finishNegotitationPanel
                .add(new JLabel("Changes:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        finishNegotitationPanel
                .add(changeScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        finishNegotitationPanel.add(sendBtn, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, EAST, NONE, insets5, 0, 0));

        // Filler
        add(new JLabel(" "), new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, NORTH, BOTH, new Insets(2, 5, 2, 0), 0, 0));

        
        setListeners();
    }

    private void setListeners(){
        
        EPD.getInstance().getChatServiceHandler().addListener(this);
    }
    
    private Action sendChatMsg = new AbstractAction("sendChatMsg") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            sendChatMessage();
        }
    };

    private void sendChatMessage() {
        if (!chatMsgField.getText().equals("")) {

            // chatMessages

            // ChatServiceTarget target = (ChatServiceTarget)targetComboBox.getSelectedItem();
            // EPD.getInstance().getChatServiceHandler().sendChatMessage(
            // target.getId(),
            // messageTxt.getText(),
            // senderNameTxt.getText(),
            // (NotificationSeverity)severityComboBox.getSelectedItem(),
            // alerts);

            boolean hasEndPoint = false;

            for (ServiceEndpoint<ChatServiceMessage, Void> service : EPD.getInstance().getChatServiceHandler().getChatServiceList()) {
                Integer mmsi = MaritimeCloudUtils.toMmsi(service.getId());

                if (mmsi == voyage.getMmsi()) {
                    hasEndPoint = true;
                }

            }

            if (hasEndPoint) {

                MaritimeId id = new MmsiId((int) voyage.getMmsi());
                NotificationSeverity severity = NotificationSeverity.MESSAGE;

                List<NotificationAlert> alerts = new ArrayList<>();
                alerts.add(new NotificationAlert(AlertType.BEEP));
                alerts.add(new NotificationAlert(AlertType.POPUP));

                EPD.getInstance().getChatServiceHandler().sendChatMessage(id, chatMsgField.getText(), "Shore", severity, alerts);
            }

            chatMsgField.setText("");
        }
    }

    public void setAisHandler(AisHandler aisHandler) {
        this.aisHandler = aisHandler;
        checkAisData();
    }

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;

        lblShipName.setText("" + voyage.getMmsi());
        lblCallSign.setText("N/A");

        lblRouteName.setText(voyage.getRoute().getName());

        lblCog.setText("N/A");
        lblSog.setText("N/A");
        lblTd.setText(Formatter.formatShortDateTime(voyage.getRoute().getEtas().get(0)));
        lblETA.setText(Formatter.formatShortDateTime(voyage.getRoute().getEtas().get(voyage.getRoute().getEtas().size() - 1)));

        checkAisData();
    }

    private void checkAisData() {
        if (aisHandler != null && voyage != null) {

            VesselTarget vesselTarget = aisHandler.getVesselTarget(voyage.getMmsi());
            if (vesselTarget != null && vesselTarget.getStaticData() != null) {
                VesselStaticData staticData = vesselTarget.getStaticData();

                lblShipName.setText(staticData.getTrimmedName());
                lblCallSign.setText(staticData.getTrimmedCallsign());
                lblCog.setText(Formatter.formatDegrees((double) vesselTarget.getPositionData().getCog(), 0));
                lblSog.setText(Formatter.formatCurrentSpeed((double) vesselTarget.getPositionData().getSog()));

            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == sendChatBtn) {
            sendChatMessage();
        }

        
        if (ae.getSource() == sendBtn) {
            voyageHandlingLayer.sendVoyage(txtMessage.getText());
        }

        if (ae.getSource() == ZoomToShipBtn) {
            VesselTarget vesselTarget = aisHandler.getVesselTarget(voyage.getMmsi());
            if (vesselTarget != null) {
                chartPanel.goToPosition(vesselTarget.getPositionData().getPos());
            }
        }

        if (ae.getSource() == OpenShipDetailstextBtn) {

            EPD.getInstance().getNotificationCenter().openNotification(NotificationType.STRATEGIC_ROUTE, voyage.getId(), false);
        }

        if (ae.getSource() == OpenVpDetalsBtn) {

            RoutePropertiesDialog routePropertiesDialog = new RoutePropertiesDialog(EPDShore.getInstance().getMainFrame(),
                    chartPanel, voyage.getRoute(), voyageHandlingLayer);
            routePropertiesDialog.setVisible(true);

        }

        if (ae.getSource() == HideOtherVoyagesBtn) {

            // If it\s visibile then toggle switched to
            if (chartPanel.getVoyageLayer().isVisible()) {
                HideOtherVoyagesBtn.setText("Show other voyages");
            } else {
                HideOtherVoyagesBtn.setText("Hide other voyages");
            }

            chartPanel.getVoyageLayer().setVisible(!chartPanel.getVoyageLayer().isVisible());
        }
    }

    private void addChatMessage(String message) {
        String currentText = "";

        if (!chatMessages.getText().equals("")) {
            currentText = chatMessages.getText() + "\n";
        }
        currentText = currentText + message;
        chatMessages.setText(currentText);
        
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    @Override
    public void chatMessageReceived(MaritimeId senderId, ChatServiceMessage message) {
        String senderName = EPD.getInstance().getName(senderId);
        String chatMessage = Formatter.formateTimeFromDate(message.getSendDate()) + " - " + senderName + " : "
                + message.getMessage();

        addChatMessage(chatMessage);

    }

    @Override
    public void chatMessageSent(MaritimeId recipientId, ChatServiceMessage message) {
        String senderName = "You";
        String chatMessage = Formatter.formateTimeFromDate(message.getSendDate()) + " - " + senderName + " : "
                + message.getMessage();

        addChatMessage(chatMessage);
    }
}
