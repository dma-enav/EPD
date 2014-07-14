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
