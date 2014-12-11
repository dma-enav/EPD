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
package dk.dma.epd.common.prototype.gui.notification;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.VERTICAL;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.util.Timestamp;

import org.apache.commons.lang.StringUtils;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.service.ChatServiceData;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;
import dk.dma.epd.common.prototype.service.internal.EPDChatMessage;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.NameUtils;
import dk.dma.epd.common.util.NameUtils.NameFormat;
import dma.messaging.MaritimeText;
import dma.messaging.MaritimeTextingNotificationSeverity;

/**
 * Display the chat service messages exchanged with a specific maritime id.
 * <p>
 * If the panel is instantiated with {@code compactLayout} set to false, there will be a set of message type buttons that can be
 * used to select the message type (i.e. message, warning, alert).
 * <p>
 * If you hook the panel up as a {@linkplain IChatServiceListener} to the chat service handler, it will display the message exchange
 * of the latest chat message being received or sent.
 * <p>
 * Otherwise, use the {@code #setChatServiceData()} function to control which exchange to display.
 * <p>
 * You can set a {@code noDataComponent} that gets displayed when no chat data is available.
 */
public class ChatServicePanel extends JPanel implements ActionListener, IChatServiceListener {

    private static final long serialVersionUID = 1L;

    /** If the time between two messages is more than 5 minutes, print the date **/
    public static final long PRINT_DATE_INTERVAL = 1000L * 60L * 5L;

    protected static final ImageIcon ICON_MESSAGE = EPD.res().getCachedImageIcon("images/notifications/balloon.png");
    protected static final ImageIcon ICON_ALERT = EPD.res().getCachedImageIcon("images/notifications/error.png");
    protected static final ImageIcon ICON_WARNING = EPD.res().getCachedImageIcon("images/notifications/warning.png");

    ChatServiceData chatData;
    Component noDataComponent;

    JPanel messagesPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(messagesPanel);
    JLabel titleHeader = new JLabel(" ");

    JTextComponent messageText;
    JButton sendBtn;
    JToggleButton messageTypeBtn, warningTypeBtn, alertTypeBtn;

    /**
     * Constructor
     * 
     * @param compactLayout
     *            if false, there will be message type selectors in the panel
     */
    public ChatServicePanel(boolean compactLayout) {
        super(new BorderLayout());

        // Prepare the title header
        titleHeader.setBackground(getBackground().darker());
        titleHeader.setOpaque(true);
        titleHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        titleHeader.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleHeader, BorderLayout.NORTH);

        // Add messages panel
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        messagesPanel.setBackground(UIManager.getColor("List.background"));
        messagesPanel.setOpaque(false);
        messagesPanel.setLayout(new GridBagLayout());
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel(new GridBagLayout());
        add(sendPanel, BorderLayout.SOUTH);
        Insets insets = new Insets(2, 2, 2, 2);

        // Add text area
        if (compactLayout) {
            messageText = new JTextField();
            ((JTextField) messageText).addActionListener(this);
            sendPanel.add(messageText, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, insets, 0, 0));

        } else {
            messageText = new JTextArea();
            JScrollPane scrollPane2 = new JScrollPane(messageText);
            scrollPane2.setPreferredSize(new Dimension(100, 50));
            scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            sendPanel.add(scrollPane2, new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, NORTH, BOTH, insets, 0, 0));
        }

        // Add buttons
        ButtonGroup group = new ButtonGroup();
        messageTypeBtn = createMessageTypeButton("Send messages", ICON_MESSAGE, true, group);
        warningTypeBtn = createMessageTypeButton("Send warnings", ICON_WARNING, false, group);
        alertTypeBtn = createMessageTypeButton("Send alerts", ICON_ALERT, false, group);

        if (!compactLayout) {
            JToolBar msgTypePanel = new JToolBar();
            msgTypePanel.setBorderPainted(false);
            msgTypePanel.setOpaque(true);
            msgTypePanel.setFloatable(false);
            sendPanel.add(msgTypePanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, NORTH, NONE, insets, 0, 0));
            msgTypePanel.add(messageTypeBtn);
            msgTypePanel.add(warningTypeBtn);
            msgTypePanel.add(alertTypeBtn);
        }

        if (compactLayout) {
            sendBtn = new JButton("Send");
            sendPanel.add(sendBtn, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, NORTH, NONE, insets, 0, 0));
        } else {
            sendBtn = new JButton("Send", ICON_MESSAGE);
            sendBtn.setPreferredSize(new Dimension(100, sendBtn.getPreferredSize().height));
            sendPanel.add(sendBtn, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, NORTH, NONE, insets, 0, 0));
        }
        sendBtn.setEnabled(false);
        messageText.setEditable(false);
        sendBtn.addActionListener(this);
    }

    /**
     * Creates a diminutive toggle button used for selecting the message type
     * 
     * @param title
     *            the title of the button
     * @param icon
     *            the icon to use
     * @param selected
     *            whether the button is selected or not
     * @param group
     *            the group to add the button to
     * @return the instantiated button
     */
    private JToggleButton createMessageTypeButton(String title, ImageIcon icon, boolean selected, ButtonGroup group) {
        JToggleButton button = new JToggleButton(icon);
        group.add(button);
        button.setSelected(selected);
        button.setToolTipText(title);
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(18, 18));
        return button;
    }

    /**
     * Can be called whenever the chat service data to display has changed
     * 
     * @param chatData
     *            the new updated chat service data
     */
    public void setChatServiceData(ChatServiceData chatData) {
        this.chatData = chatData;
        updateChatMessagePanel();
    }

    /**
     * Can be called whenever the chat service data for the given maritime id to display has changed
     * 
     * @param id
     *            the target maritime id
     */
    public void setTargetMaritimeId(MaritimeId id) {
        ChatServiceData chatData = EPD.getInstance().getChatServiceHandler().getChatServiceData(id);
        // NB: chat data may be null
        setChatServiceData(chatData);
    }

    /**
     * Updates the chat message panel in the Swing event thread
     */
    public void updateChatMessagePanel() {
        // Ensure that we operate in the Swing event thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateChatMessagePanel();
                }
            });
            return;
        }

        // Set the title header
        titleHeader.setText(chatData != null ? NameUtils.getName(chatData.getId(), NameFormat.MEDIUM) : " ");
        titleHeader.setVisible(chatData != null);

        // Only enable send-function when there is chat data, and hence a target
        sendBtn.setEnabled(chatData != null);
        messageText.setEditable(chatData != null);

        // Remove all components from the messages panel
        messagesPanel.removeAll();

        Insets insets = new Insets(0, 2, 2, 2);
        Insets insets2 = new Insets(6, 2, 0, 2);

        if (chatData != null && chatData.getMessageCount() > 0) {

            // First, add a filler component
            int y = 0;
            messagesPanel.add(new JLabel(""), new GridBagConstraints(0, y++, 1, 1, 0.0, 1.0, NORTH, VERTICAL, insets, 0, 0));

            // Add the messages
            long lastMessageTime = 0;
            for (EPDChatMessage message : chatData.getMessages()) {

                System.out.println("Message is " + message);

                // Check if we need to add a time label
                if (message.getSendDate().getTime() - lastMessageTime > PRINT_DATE_INTERVAL) {
                    JLabel dateLabel = new JLabel(String.format(message.isOwnMessage() ? "Sent to %s" : "Received %s",
                            Formatter.formatShortDateTimeNoTz(new Date(message.getSendDate().getTime()))));
                    dateLabel.setFont(dateLabel.getFont().deriveFont(9.0f).deriveFont(Font.PLAIN));
                    dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    dateLabel.setForeground(Color.LIGHT_GRAY);
                    messagesPanel.add(dateLabel, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets2, 0, 0));
                }

                // Add a chat message field
                JPanel msg = new JPanel();
                msg.setBorder(new ChatMessageBorder(message));
                JLabel msgLabel = new ChatMessageLabel(message.getMsg(), message.isOwnMessage());
                msg.add(msgLabel);
                messagesPanel.add(msg, new GridBagConstraints(0, y++, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets, 0, 0));

                lastMessageTime = message.getSendDate().getTime();
            }

            // Scroll to the bottom
            validate();
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            messagesPanel.repaint();

        } else if (chatData == null && noDataComponent != null) {
            // The noDataComponent may e.g. be a message
            messagesPanel.add(noDataComponent, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, insets, 0, 0));
        }
    }

    /**
     * Sends a chat message
     */
    protected void sendChatMessage() {
        // Sanity check
        if (chatData == null) {
            return;
        }

        String msg = messageText.getText();
        if (StringUtils.isBlank(msg)) {
            return;
        }
        messageText.setText("");

        NotificationSeverity severity = messageTypeBtn.isSelected() ? NotificationSeverity.MESSAGE
                : (warningTypeBtn.isSelected() ? NotificationSeverity.WARNING : NotificationSeverity.ALERT);

        EPD.getInstance().getChatServiceHandler().sendChatMessage(chatData.getId(), msg, severity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == sendBtn || ae.getSource() == messageText) {
            sendChatMessage();
        } else if (ae.getSource() == messageTypeBtn) {
            sendBtn.setIcon(ICON_MESSAGE);
        } else if (ae.getSource() == warningTypeBtn) {
            sendBtn.setIcon(ICON_WARNING);
        } else if (ae.getSource() == alertTypeBtn) {
            sendBtn.setIcon(ICON_ALERT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chatMessagesUpdated(MaritimeId targetId) {
        setChatServiceData(EPD.getInstance().getChatServiceHandler().getChatServiceData(targetId));
    }

    /**
     * Returns the component to display when there is no chat data
     * 
     * @return the component to display when there is no chat data
     */
    public Component getNoDataComponent() {
        return noDataComponent;
    }

    /**
     * Sets the component to display when there is no chat data
     * 
     * @param noDataComponent
     *            the component to display when there is no chat data
     */
    public void setNoDataComponent(Component noDataComponent) {
        this.noDataComponent = noDataComponent;
    }

    /**
     * Test method
     */
    public static void main(String... args) {
        JFrame f = new JFrame("Chat service panel test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(100, 100, 300, 400);

        ChatServicePanel p = new ChatServicePanel(false) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void sendChatMessage() {
                MaritimeText chatMessage = new MaritimeText();
                chatMessage.setMsg(messageText.getText());
                EPDChatMessage epdChatMessage = new EPDChatMessage(chatMessage, true, Timestamp.now());

                chatData.addChatMessage(epdChatMessage);
                updateChatMessagePanel();
            }
        };
        f.getContentPane().add(p);

        MaritimeId id = new MmsiId(999333333);
        ChatServiceData chatData = new ChatServiceData(id);
        long time = System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 10L; // Go back 10 days
        for (int x = 0; x < 10; x++) {
            time += Math.random() * 1000L * 60L * 10L; // add 0-10 minutes
            boolean ownMessage = Math.random() < 0.5;
            MaritimeText msg = new MaritimeText();
            msg.setMsg("hello yourself\nJGsfdkjfhg");

            EPDChatMessage epdChatMessage = new EPDChatMessage(msg, ownMessage, Timestamp.create(time));

            if (Math.random() < 0.1) {
                epdChatMessage.setSeverity(MaritimeTextingNotificationSeverity.ALERT);
            } else if (Math.random() < 0.2) {
                epdChatMessage.setSeverity(MaritimeTextingNotificationSeverity.WARNING);
            }
            chatData.addChatMessage(epdChatMessage);
        }
        p.chatData = chatData;
        p.updateChatMessagePanel();
        f.setVisible(true);
    }

    /**
     * Label class that formats the text has html and restricts the width
     */
    class ChatMessageLabel extends JLabel {

        private static final long serialVersionUID = 1L;

        /**
         * Constructor
         * 
         * @param text
         */
        public ChatMessageLabel(String text, boolean ownMessage) {
            super(String.format("<html><div align='%s'>%s</div></html>", ownMessage ? "right" : "left", Formatter.formatHtml(text)));
            setFont(getFont().deriveFont(10f));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getMaximumSize() {
            return new Dimension(scrollPane.getWidth() - 60, super.getPreferredSize().height);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            Dimension s = super.getPreferredSize();
            return new Dimension(Math.min(getMaximumSize().width, s.width), super.getPreferredSize().height);
        }
    }
}

/**
 * This border is used by the {@linkplain ChatServicePanel} widget.
 * <p>
 * It will paint a balloon-style border around the hosting panel with the point either at the bottom-left or bottom-right corner
 * depending on whether it is an own-message or not.
 * <p>
 * Alerts and warnings will be painted with a yellow and red borders respectively.
 */
class ChatMessageBorder extends AbstractBorder {
    private static final long serialVersionUID = 1L;
    private static final Color ALERT_COLOR = new Color(255, 50, 50, 200);
    private static final Color WARN_COLOR = new Color(255, 225, 50, 200);

    int cornerRadius = 12;
    int pointerWidth = 10;
    int pointerHeight = 10;
    int pointerFromBottom = 11;
    int pad = 2;
    Insets insets;
    EPDChatMessage message;
    boolean pointerLeft;

    /**
     * Constructor
     * 
     * @param message
     */
    public ChatMessageBorder(EPDChatMessage message) {
        super();
        this.message = message;
        this.pointerLeft = !message.isOwnMessage();

        int i = 2;
        insets = pointerLeft ? new Insets(i, pointerWidth + i, i, i) : new Insets(i, i, i, pointerWidth + i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return getBorderInsets(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(GraphicsUtil.ANTIALIAS_HINT);

        // Define the content rectangle
        int x0 = pointerLeft ? pad + pointerWidth : pad;
        RoundRectangle2D.Double content = new RoundRectangle2D.Double(x0, pad, width - 2 * pad - pointerWidth, height - 2 * pad,
                cornerRadius, cornerRadius);

        // Define the pointer triangle
        int xp = pointerLeft ? pad + pointerWidth : width - pad - pointerWidth;
        int yp = pad + height - pointerFromBottom;
        Polygon pointer = new Polygon();
        pointer.addPoint(xp, yp);
        pointer.addPoint(xp, yp - pointerHeight);
        pointer.addPoint(xp + pointerWidth * (pointerLeft ? -1 : 1), yp - pointerHeight / 2);

        // Combine content rectangle and pointer into one area
        Area area = new Area(content);
        area.add(new Area(pointer));

        // Fill the pop-up background
        Color col = pointerLeft ? c.getBackground().darker() : c.getBackground().brighter();
        g2.setColor(col);
        g2.fill(area);

        if (message.getSeverity() == MaritimeTextingNotificationSeverity.WARNING
                || message.getSeverity() == MaritimeTextingNotificationSeverity.ALERT) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(message.getSeverity() == MaritimeTextingNotificationSeverity.WARNING ? WARN_COLOR : ALERT_COLOR);
            g2.draw(area);
        }
    }
}
