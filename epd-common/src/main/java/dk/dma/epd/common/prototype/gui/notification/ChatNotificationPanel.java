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
package dk.dma.epd.common.prototype.gui.notification;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.ChatNotification;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.service.ChatServiceData;
import dk.dma.epd.common.text.Formatter;

/**
 * A panel for chat sessions
 */
public class ChatNotificationPanel  extends NotificationPanel<ChatNotification> {

    private static final long serialVersionUID = 1L;

    private static final String[] NAMES = {
        "", "", "#", "Date", "Type", "ID"
    };
    
    /**
     * Constructor
     */
    public ChatNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);
        
        fixColumnWidth(0, 18);
        fixColumnWidth(1, 18);
        fixColumnWidth(2, 24);
        fixColumnWidth(3, 80);
        fixColumnWidth(4, 60);
        setCellAlignment(2, JLabel.RIGHT);
        splitPane.setDividerLocation(420);        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotitficationType() {
        return NotificationType.MESSAGES;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ButtonPanel initButtonPanel() {
        ButtonPanel buttonPanel = new ButtonPanel(notificationCenter);
        buttonPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UIManager.getColor("Separator.shadow")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        buttonPanel.add(gotoBtn);
        buttonPanel.add(deleteBtn);
        deleteBtn.setText("Clear");
        
        gotoBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                gotoSelectedNotification();
            }});
        
        deleteBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                deleteSelectedNotifications();
            }});

        return buttonPanel;        
    }
    
    /**
     * This method is called whenever the selection changes
     * to update the enabled state of the buttons
     */
    protected void updateButtonEnabledState() {
        List<ChatNotification> selection = getSelectedNotifications();
        boolean canDelete = selection.size() > 0;
        deleteBtn.setEnabled(canDelete);
        gotoBtn.setEnabled(selection.size() == 1 && selection.get(0).getLocation() != null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationTableModel<ChatNotification> initTableModel() {
        return new NotificationTableModel<ChatNotification>() {
            private static final long serialVersionUID = 1L;
            
            @Override 
            public String[] getColumnNames() { 
                return NAMES; 
            }
            
            @Override 
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex <= 1) {
                    return ImageIcon.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
            
            @Override 
            public Object getValueAt(int rowIndex, int columnIndex) {
                ChatNotification notification = getNotification(rowIndex);
                
                switch (columnIndex) {
                case 0: return !notification.isRead() ? ICON_UNREAD : null;
                case 1: return notification.getSeverity() == NotificationSeverity.ALERT
                                ? ICON_ALERT
                                : (notification.getSeverity() == NotificationSeverity.WARNING ? ICON_WARNING : null);
                case 2: return notification.get().getMessageCount();
                case 3: return Formatter.formatShortDateTimeNoTz(notification.getDate());
                case 4: return notification.getTargetType();
                case 5: return notification.getTargetName();
                default:
                }
                return null; 
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationDetailPanel<ChatNotification> initNotificationDetailPanel() {
        return new ChatNotificationDetailPanel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(ChatNotification notification) {
        if (notification != null) {
            EPD.getInstance().getChatServiceHandler().clearChatMessages(notification.getId());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        
        List<ChatNotification> notifications = new ArrayList<>();
        for (ChatServiceData chatData : EPD.getInstance().getChatServiceHandler().getChatMessages().values()) {
            ChatNotification notification = new ChatNotification(chatData);
            notifications.add(notification);
        }
        tableModel.setNotifications(notifications);
        refreshTableData();
        notifyListeners();
    }
}

/**
 * The detail panel for chat notifications
 */
class ChatNotificationDetailPanel extends NotificationDetailPanel<ChatNotification> {

    private static final long serialVersionUID = 1L;

    ChatServicePanel chatPanel = new ChatServicePanel(false);
    
    /**
     * Constructor
     */
    public ChatNotificationDetailPanel() {
        super();
        buildGUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildGUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(chatPanel, BorderLayout.CENTER);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(ChatNotification notification) {
        this.notification = notification;
        
        if (notification == null) {
            chatPanel.setChatServiceData(null);
        } else {
            chatPanel.setChatServiceData(notification.get());
        }
        chatPanel.repaint();
    }
}
