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
package dk.dma.epd.ship.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.prototype.gui.notification.ChatServicePanel;


public class STCCCommunicationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    ChatServicePanel chatMessages = new ChatServicePanel(true);

    public STCCCommunicationPanel() {
        super(new BorderLayout());

        setBorder(new LineBorder(Color.GRAY));

        JLabel commsTitle = new JLabel("STCC Comms");
        commsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        commsTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(commsTitle, BorderLayout.NORTH);

        // Add a message to display in the chat messages panel when there are no chat data
        JTextArea noDataLabel = new JTextArea("You must inititate a route negotitation before you can communicate with the STCC");
        noDataLabel.setLineWrap(true);
        noDataLabel.setEditable(false);
        noDataLabel.setFocusable(false);
        noDataLabel.setOpaque(false);
        chatMessages.setNoDataComponent(noDataLabel);
        
        chatMessages.setPreferredSize(new Dimension(chatMessages.getPreferredSize().width, 150));
        add(chatMessages, BorderLayout.CENTER);

        deactivateChat();
    }

    public void updateChatMessagePanel() {
        chatMessages.updateChatMessagePanel();
    }

    public void activateChat(int stccMmsi) {
        chatMessages.setTargetMaritimeId(new MmsiId(stccMmsi));
    }

    public void deactivateChat() {
        chatMessages.setChatServiceData(null);
        //chatMessages.setText("You must inititate a route negotitation before you can communicate with the STCC");
    }
}
