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
package dk.dma.epd.ship.gui.panels;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.service.ServiceEndpoint;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.text.Formatter;

public class STCCCommunicationPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    JTextArea chatMessages;
    private JTextField txtField;
    private JButton sendBtn;

    private int stccMmsi;
    private JScrollPane scrollPane;

    public STCCCommunicationPanel() {
        super();

        // setMinimumSize(new Dimension(200, 500));

        setBorder(new LineBorder(Color.GRAY));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 10, 0 };
        gridBagLayout.rowHeights = new int[] { 10, 100, 20 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0 };
        setLayout(gridBagLayout);

        JLabel commsTitle = new JLabel("STCC Comms");
        commsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        commsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        GridBagConstraints gbc_commsTitle = new GridBagConstraints();
        gbc_commsTitle.anchor = GridBagConstraints.NORTH;
        gbc_commsTitle.insets = new Insets(0, 0, 5, 0);
        gbc_commsTitle.gridx = 0;
        gbc_commsTitle.gridy = 0;
        add(commsTitle, gbc_commsTitle);

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
        gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        add(scrollPane, gbc_scrollPane);

        JPanel panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.anchor = GridBagConstraints.NORTH;
        gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 2;
        add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 2 };
        gbl_panel_1.rowHeights = new int[] { 0 };
        gbl_panel_1.columnWeights = new double[] { 1.0, 0.0 };
        gbl_panel_1.rowWeights = new double[] { 0.0 };
        panel_1.setLayout(gbl_panel_1);

        txtField = new JTextField();
        GridBagConstraints gbc_txtField = new GridBagConstraints();
        gbc_txtField.insets = new Insets(0, 0, 0, 5);
        gbc_txtField.fill = GridBagConstraints.BOTH;
        gbc_txtField.gridx = 0;
        gbc_txtField.gridy = 0;
        panel_1.add(txtField, gbc_txtField);
        txtField.setColumns(10);

        sendBtn = new JButton("Send");
        GridBagConstraints gbc_sendBtn = new GridBagConstraints();
        gbc_sendBtn.gridx = 1;
        gbc_sendBtn.gridy = 0;
        panel_1.add(sendBtn, gbc_sendBtn);

        sendBtn.addActionListener(this);

        txtField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "sendChatMsg");
        txtField.getActionMap().put("sendChatMsg", sendChatMsg);

        deactivateChat();
    }

    private Action sendChatMsg = new AbstractAction("sendChatMsg") {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            sendChatMessage();
        }
    };

    private void sendChatMessage() {
        if (!txtField.getText().equals("")) {

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

                if (mmsi == stccMmsi) {
                    hasEndPoint = true;
                }

            }

            if (hasEndPoint) {

                MaritimeId id = new MmsiId(stccMmsi);
                NotificationSeverity severity = NotificationSeverity.MESSAGE;

                List<NotificationAlert> alerts = new ArrayList<>();
                alerts.add(new NotificationAlert(AlertType.BEEP));
                alerts.add(new NotificationAlert(AlertType.POPUP));

                EPD.getInstance().getChatServiceHandler().sendChatMessage(id, txtField.getText(), "My ship", severity, alerts);
            }

            txtField.setText("");
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        sendChatMessage();

    }

    public void addChatMessage(String message) {
        String currentText = "";

        if (!chatMessages.getText().equals("")) {
            currentText = chatMessages.getText() + "\n";
        }
        currentText = currentText + message;
        chatMessages.setText(currentText);

        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    public void activateChat(int stccMmsi) {
        this.stccMmsi = stccMmsi;
        if (!txtField.isEnabled()) {
            chatMessages.setText("");
            retrieveExistingMessages();
        }

        
        sendBtn.setEnabled(true);
        txtField.setEnabled(true);

    }

    private void retrieveExistingMessages() {

        long mmsi = stccMmsi;

        List<ChatServiceMessage> chatConversations = EPD.getInstance().getChatServiceHandler().getChatMessagesForID((int) mmsi);

        if (chatConversations != null) {

            for (int i = 0; i < chatConversations.size(); i++) {
                ChatServiceMessage message = chatConversations.get(i);

                String senderName = "";

                if (message.getRecipientID() == mmsi) {
                    // Message was sent by us
                    senderName = "You";
                } else {
                    // Message was sent to us
                    senderName = EPD.getInstance().getName(MaritimeCloudUtils.toMaritimeId((int) mmsi));
                }

                String chatMessage = Formatter.formateTimeFromDate(message.getSendDate()) + " - " + senderName + " : "
                        + message.getMessage();

                addChatMessage(chatMessage);
            }

        }else{
            System.out.println("No messages for " + mmsi);
        }

    }
    
    
    
    public void deactivateChat() {
        this.stccMmsi = -1;
        sendBtn.setEnabled(false);
        txtField.setEnabled(false);
        chatMessages.setText("You must inititate a route negotitation before you can communicate with the STCC");

    }
}
