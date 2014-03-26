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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.gui.notification.NotificationDetailPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationTableModel;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.service.RouteSuggestionHandler;

/**
 * A route suggestion implementation of the {@linkplain NotificationPanel} class
 */
public class RouteSuggestionNotificationPanel extends NotificationPanel<RouteSuggestionNotification> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RouteSuggestionNotificationPanel.class);
    
    private static final String[] NAMES = {
        "", "ID", "MMSI", "Route Name", "Status" };
    
    protected JButton resendBtn;

    /**
     * Constructor
     */
    public RouteSuggestionNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);
        
        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        splitPane.setDividerLocation(400);
        setCellAlignment(1, JLabel.RIGHT);
        setCellAlignment(2, JLabel.RIGHT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ButtonPanel initButtonPanel() {
        ButtonPanel btnPanel = super.initButtonPanel();

        resendBtn = new JButton(
                "Resend", 
                EPDShore.res().getCachedImageIcon("images/notificationcenter/arrow-circle-315.png"));
        btnPanel.add(resendBtn);
        
        resendBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                resendSelectedRouteSuggestion();
            }});
        
        return btnPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateButtonEnabledState() {
        super.updateButtonEnabledState();
        RouteSuggestionNotification n = getSelectedNotification();
       resendBtn.setEnabled(n != null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotitficationType() {
        return NotificationType.TACTICAL_ROUTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationTableModel<RouteSuggestionNotification> initTableModel() {
        return new NotificationTableModel<RouteSuggestionNotification>() {
            private static final long serialVersionUID = 1L;
            
            @Override 
            public String[] getColumnNames() { 
                return NAMES; 
            }
            
            @Override 
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return ImageIcon.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
            
            @Override 
            public Object getValueAt(int rowIndex, int columnIndex) {
                RouteSuggestionNotification notification = getNotification(rowIndex);
                
                switch (columnIndex) {
                case 0: return !notification.isRead() 
                                ? ICON_UNREAD 
                                : (notification.isAcknowledged() ? ICON_ACKNOWLEDGED : null);
                case 1: return notification.getId();
                case 2: return "" + notification.get().getMmsi();
                case 3: return notification.get().getMessage().getRoute().getName();
                case 4: return notification.get().getStatus().toString();
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
    protected NotificationDetailPanel<RouteSuggestionNotification> initNotificationDetailPanel() {
        return new RouteSuggestionDetailPanel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void acknowledgeNotification(RouteSuggestionNotification notification) {
        if (notification != null && !notification.isAcknowledged()) {
            RouteSuggestionHandler routeSuggestionHandler = EPDShore.getInstance().getRouteSuggestionHandler();
            RouteSuggestionData routeSuggestion = notification.get();
            // NB: routeSuggestionHandler.setRouteSuggestionAcknowledged() will automatically trigger a table refresh
            routeSuggestionHandler.setRouteSuggestionAcknowledged(routeSuggestion.getId());
            selectFirstUnacknowledgedRow();
            notifyListeners();
        }    
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(RouteSuggestionNotification notification) {
        int row = table.getSelectedRow();
        if (notification != null) {
            RouteSuggestionHandler routeSuggestionHandler = EPDShore.getInstance().getRouteSuggestionHandler();
            RouteSuggestionData routeSuggestion = notification.get();
            // NB: routeSuggestionHandler.removeSuggestion() will automatically trigger a table refresh
            routeSuggestionHandler.removeSuggestion(routeSuggestion.getId());
            setSelectedRow(row - 1);
            notifyListeners();
        }
    }

    /**
     * Re-sends the selected route suggestion
     */
    protected void resendSelectedRouteSuggestion() {
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            RouteSuggestionHandler routeSuggestionHandler = EPDShore.getInstance().getRouteSuggestionHandler();
            RouteSuggestionData routeSuggestion = notification.get();
            try {
                routeSuggestionHandler.sendRouteSuggestion(
                        routeSuggestion.getMmsi(), 
                        routeSuggestion.getMessage().getRoute(), 
                        routeSuggestion.getMessage().getSender(), 
                        routeSuggestion.getMessage().getMessage());
                
            } catch (Exception ex) {
                LOG.error("Error re-sending route suggestion", ex);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        RouteSuggestionHandler routeSuggestionHandler = EPDShore.getInstance().getRouteSuggestionHandler();
        
        // The back-end does not support the "read" flag, so, we store it
        Set<Long> readNotificationIds = new HashSet<>();
        for (RouteSuggestionNotification notificaiton : tableModel.getNotifications()) {
            if (notificaiton.isRead()) {
                readNotificationIds.add(notificaiton.getId());
            }
        }
        
        List<RouteSuggestionNotification> notifications = new ArrayList<>();
        for (RouteSuggestionData routeSuggestion : routeSuggestionHandler.getSortedRouteSuggestions()) {
            RouteSuggestionNotification notification = new RouteSuggestionNotification(routeSuggestion);
            
            // Restore the "read" flag
            if (readNotificationIds.contains(notification.getId())) {
                notification.setRead(true);
            }
            notifications.add(notification);
        }
        tableModel.setNotifications(notifications);
        refreshTableData();
        notifyListeners();
    }

}


/**
 * Displays relevant route suggestion detail information
 */
class RouteSuggestionDetailPanel extends NotificationDetailPanel<RouteSuggestionNotification> {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public RouteSuggestionDetailPanel() {
        super();
        
        buildGUI();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(RouteSuggestionNotification notification) {
        this.notification = notification;
        
        // Special case
        if (notification == null) {
            contentLbl.setText("");
            return;
        }
        
        RouteSuggestionData routeSuggestion = notification.get();
        
        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");
        append(html, "ID", routeSuggestion.getId());
        append(html, "MMSI", routeSuggestion.getMmsi());
        append(html, "Route Name", routeSuggestion.getMessage().getRoute().getName());
        append(html, "Sent Date", Formatter.formatShortDateTime(routeSuggestion.getMessage().getSentDate()));
        append(html, "Sender", routeSuggestion.getMessage().getSender());
        append(html, "Message", routeSuggestion.getMessage().getMessage());
        append(html, "Status", routeSuggestion.getStatus().toString());
        if (routeSuggestion.getReply() != null) {
            append(html, "Reply Sent", Formatter.formatShortDateTime(routeSuggestion.getReply().getSentDate()));
            append(html, "Message", routeSuggestion.getReply().getMessage());
        } else {
            append(html, "Reply Sent", "No reply received yet");
            append(html, "Message", "No reply received yet");
        }        
        html.append("</table>");
        html.append("</html>");
        contentLbl.setText(html.toString());
    }

    /**
     * If non-empty, appends a table row with the given title and value
     * @param html the html to append to
     * @param title the title
     * @param value the value
     */
    private void append(StringBuilder html, String title, Object value) {
        if (value != null && value.toString().length() > 0) {
            html.append("<tr><td valign='top'><b>")
                .append(title)
                .append("</b></td><td>")
                .append(value)
                .append("</td></tr>");
        }
    }
}

