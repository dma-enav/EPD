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
package dk.dma.epd.common.prototype.gui.voct;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.VERTICAL;

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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;

import net.maritimecloud.util.Timestamp;

import org.apache.commons.lang.StringUtils;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.voct.sardata.SARTextLogMessage;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon.IVoctInfoListener;
import dk.dma.epd.common.text.Formatter;
import dma.voct.SarText;

public class VOCTAdditionalInfoPanel extends JPanel implements ActionListener,
        IVoctInfoListener {

    private static final long serialVersionUID = 1L;

    /** If the time between two messages is more than 5 minutes, print the date **/
    public static final long PRINT_DATE_INTERVAL = 1000L * 60L * 5L;

    Component noDataComponent;

    JPanel messagesPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(messagesPanel);
    JLabel titleHeader = new JLabel(" ");

    JTextComponent messageText;
    JButton addBtn;

    /**
     * Constructor
     * 
     * @param compactLayout
     *            if false, there will be message type selectors in the panel
     */
    public VOCTAdditionalInfoPanel(boolean compactLayout) {
        super(new BorderLayout());

        EPD.getInstance().getVoctManager().addVoctSarInfoListener(this);

        // Prepare the title header
        titleHeader.setBackground(getBackground().darker());
        titleHeader.setOpaque(true);
        titleHeader.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        titleHeader.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleHeader, BorderLayout.NORTH);

        // Add messages panel
        scrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        messagesPanel.setBackground(UIManager.getColor("List.background"));
        messagesPanel.setOpaque(false);
        messagesPanel.setLayout(new GridBagLayout());
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel(new GridBagLayout());
        add(sendPanel, BorderLayout.SOUTH);
        Insets insets = new Insets(2, 2, 2, 2);

        // Add text area
        // if (false) {
        // messageText = new JTextField();
        // ((JTextField) messageText).addActionListener(this);
        // sendPanel.add(messageText, new GridBagConstraints(0, 0, 1, 1, 1.0,
        // 1.0, NORTH, BOTH, insets, 0, 0));
        //
        // } else {
        messageText = new JTextArea();
        JScrollPane scrollPane2 = new JScrollPane(messageText);
        scrollPane2.setPreferredSize(new Dimension(100, 50));
        scrollPane2
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane2
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendPanel.add(scrollPane2, new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0,
                NORTH, BOTH, insets, 0, 0));
        // }

        // Add buttons
        // ButtonGroup group = new ButtonGroup();

        if (!compactLayout) {
            JToolBar msgTypePanel = new JToolBar();
            msgTypePanel.setBorderPainted(false);
            msgTypePanel.setOpaque(true);
            msgTypePanel.setFloatable(false);
            sendPanel.add(msgTypePanel, new GridBagConstraints(1, 0, 1, 1, 0.0,
                    0.0, NORTH, NONE, insets, 0, 0));

        }

        if (compactLayout) {
            addBtn = new JButton("Add to Log");
            sendPanel.add(addBtn, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    NORTH, NONE, insets, 0, 0));
        }
        // addBtn.setEnabled(false);
        // messageText.setEditable(false);
        addBtn.addActionListener(this);
        updateChatMessagePanel();
    }

    // /**
    // * Can be called whenever the chat service data to display has changed
    // *
    // * @param chatData
    // * the new updated chat service data
    // */
    // public void setChatServiceData(ChatServiceData chatData) {
    // this.chatData = chatData;
    // updateChatMessagePanel();
    // }

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

        // Only enable send-function when there is chat data, and hence a target
        addBtn.setEnabled(true);
        messageText.setEditable(true);

        // Remove all components from the messages panel
        messagesPanel.removeAll();

        Insets insets = new Insets(0, 2, 2, 2);
        Insets insets2 = new Insets(6, 2, 0, 2);

        // if (chatData != null && chatData.getMessageCount() > 0) {

        // First, add a filler component
        int y = 0;
        messagesPanel.add(new JLabel(""), new GridBagConstraints(0, y++, 1, 1,
                0.0, 1.0, NORTH, VERTICAL, insets, 0, 0));

        // Add the messages
        // long lastMessageTime = 0;

        long ownMMSI = MaritimeCloudUtils.toMmsi(EPD.getInstance()
                .getMaritimeId());

        if (EPD.getInstance().getVoctManager().getSarData() != null) {
            for (SARTextLogMessage message : EPD.getInstance().getVoctManager()
                    .getSarData().getSarMessages()) {

                boolean ownMessage = false;

                String senderName = message.getOriginalSender() + "";
                if (message.getOriginalSender() == ownMMSI) {
                    ownMessage = true;
                }
                try {
                    senderName = EPD.getInstance().getIdentityHandler()
                            .getActor(message.getOriginalSender()).getName();
                } catch (Exception e) {

                }

                // Check if we need to add a time label
                // if (message.getOriginalSendDate().getTime() - lastMessageTime
                // > PRINT_DATE_INTERVAL) {

                JLabel dateLabel = new JLabel(String.format(
                        ownMessage ? "Added %s" : "Received %s",
                        Formatter.formatShortDateTimeNoTz(new Date(message
                                .getOriginalSentDate())) + " - " + senderName));
                dateLabel.setFont(dateLabel.getFont().deriveFont(9.0f)
                        .deriveFont(Font.PLAIN));
                dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dateLabel.setForeground(Color.LIGHT_GRAY);
                messagesPanel.add(dateLabel, new GridBagConstraints(0, y++, 1,
                        1, 1.0, 0.0, NORTH, HORIZONTAL, insets2, 0, 0));
                // }

                // Add a chat message field
                JPanel msg = new JPanel();
                msg.setBorder(new ChatMessageBorder(message, ownMessage));
                JLabel msgLabel = new ChatMessageLabel(message.getMsg(),
                        ownMessage);
                msg.add(msgLabel);
                messagesPanel.add(msg, new GridBagConstraints(0, y++, 1, 1,
                        1.0, 0.0, NORTH, HORIZONTAL, insets, 0, 0));

                // lastMessageTime = message.getOriginalSentDate();
            }
        }

        // Scroll to the bottom
        validate();
        scrollPane.getVerticalScrollBar().setValue(
                scrollPane.getVerticalScrollBar().getMaximum());
        messagesPanel.repaint();

        // } else if (chatData == null && noDataComponent != null) {
        // // The noDataComponent may e.g. be a message
        // messagesPanel.add(noDataComponent, new GridBagConstraints(0, 0, 1, 1,
        // 1.0, 1.0, NORTH, BOTH, insets, 0, 0));
        // }
    }

    /**
     * Sends a chat message
     */
    protected void sendChatMessage() {

        String msg = messageText.getText();
        if (StringUtils.isBlank(msg)) {
            return;
        }
        messageText.setText("");

        SarText sarText = new SarText();
        sarText.setMsg(msg);

        // Is OSC, temp for now
        if (EPD.getInstance().getMaritimeId().getId().startsWith("99")) {
            sarText.setPriority(true);
        } else {
            sarText.setPriority(false);
        }

        sarText.setOriginalSender((long) EPD.getInstance().getMaritimeId()
                .getIdAsInt());
        
        sarText.setOriginalSendDate(System.currentTimeMillis());

        // VOCTSARInfoMessage infoMsg = new VOCTSARInfoMessage();
        // infoMsg.setMessage(msg);
        // infoMsg.setSender(MaritimeCloudUtils.toMmsi(EPD.getInstance().getMaritimeId()));
        EPD.getInstance().getVoctHandler().sendVoctMessage(sarText);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn || ae.getSource() == messageText) {
            sendChatMessage();
        }
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
     * Voct info messages recieved
     */
    @Override
    public void voctMessageUpdate() {

        updateChatMessagePanel();

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
            super(String.format("<html><div align='%s'>%s</div></html>",
                    ownMessage ? "right" : "left", Formatter.formatHtml(text)));
            setFont(getFont().deriveFont(10f));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getMaximumSize() {
            return new Dimension(scrollPane.getWidth() - 60,
                    super.getPreferredSize().height);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            Dimension s = super.getPreferredSize();
            return new Dimension(Math.min(getMaximumSize().width, s.width),
                    super.getPreferredSize().height);
        }
    }

}

/**
 * This border is used by the {@linkplain VOCTAdditionalInfoPanel} widget.
 * <p>
 * It will paint a balloon-style border around the hosting panel with the point
 * either at the bottom-left or bottom-right corner depending on whether it is
 * an own-message or not.
 * <p>
 * Alerts and warnings will be painted with a yellow and red borders
 * respectively.
 */
class ChatMessageBorder extends AbstractBorder {
    private static final long serialVersionUID = 1L;

    int cornerRadius = 12;
    int pointerWidth = 10;
    int pointerHeight = 10;
    int pointerFromBottom = 11;
    int pad = 2;
    Insets insets;
    SARTextLogMessage message;
    boolean pointerLeft;

    /**
     * Constructor
     * 
     * @param color
     */
    public ChatMessageBorder(SARTextLogMessage message, boolean ownMsg) {
        super();
        this.message = message;
        this.pointerLeft = !ownMsg;

        int i = 2;
        insets = pointerLeft ? new Insets(i, pointerWidth + i, i, i)
                : new Insets(i, i, i, pointerWidth + i);
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
    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(GraphicsUtil.ANTIALIAS_HINT);

        // Define the content rectangle
        int x0 = pointerLeft ? pad + pointerWidth : pad;
        RoundRectangle2D.Double content = new RoundRectangle2D.Double(x0, pad,
                width - 2 * pad - pointerWidth, height - 2 * pad, cornerRadius,
                cornerRadius);

        // Define the pointer triangle
        int xp = pointerLeft ? pad + pointerWidth : width - pad - pointerWidth;
        int yp = pad + height - pointerFromBottom;
        Polygon pointer = new Polygon();
        pointer.addPoint(xp, yp);
        pointer.addPoint(xp, yp - pointerHeight);
        pointer.addPoint(xp + pointerWidth * (pointerLeft ? -1 : 1), yp
                - pointerHeight / 2);

        // Combine content rectangle and pointer into one area
        Area area = new Area(content);
        area.add(new Area(pointer));

        // Fill the pop-up background
        Color col = pointerLeft ? c.getBackground().darker() : c
                .getBackground().brighter();
        g2.setColor(col);
        g2.fill(area);

    }
}
