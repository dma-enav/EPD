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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.commons.lang.StringUtils;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.prototype.notification.MsiNotification;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.text.Formatter;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

/**
 * An MSI-specific implementation of the {@linkplain NotificationPanel} class
 */
public class MsiNotificationPanel extends NotificationPanel<MsiNotification> {

    private static final long serialVersionUID = 1L;

    private static final String[] NAMES = {
        "", "ID", "Priority", "Updated", "Main area"
    };
    
    /**
     * Constructor
     */
    public MsiNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);
        
        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        splitPane.setDividerLocation(400);
        setCellAlignment(1, JLabel.RIGHT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotitficationType() {
        return NotificationType.MSI;
    }
            
    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationTableModel<MsiNotification> initTableModel() {
        return new NotificationTableModel<MsiNotification>() {
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
                MsiNotification notification = getNotification(rowIndex);
                
                switch (columnIndex) {
                case 0: return !notification.isRead() 
                                ? ICON_UNREAD 
                                : (notification.isAcknowledged() ? ICON_ACKNOWLEDGED : null);
                case 1: return notification.getId();
                case 2: return notification.get().getMsiMessage().getPriority();
                case 3: return Formatter.formatShortDateTimeNoTz(notification.getDate());
                case 4: return notification.get().getMsiMessage().getLocation() != null
                                ? notification.get().getMsiMessage().getLocation().getArea()
                                : "";
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
    protected NotificationDetailPanel<MsiNotification> initNotificationDetailPanel() {
        return new MsiDetailPanel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void acknowledgeNotification(MsiNotification notification) {
        if (notification != null && !notification.isAcknowledged()) {
            MsiHandler msiHandler = EPD.getInstance().getMsiHandler();
            // NB: msiHandler.setAcknowledged() will automatically trigger a table refresh
            msiHandler.setAcknowledged(notification.get().getMsiMessage());
            selectFirstUnacknowledgedRow();
            notifyListeners();
        }    
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(MsiNotification notification) {
        int row = table.getSelectedRow();
        if (notification != null) {
            MsiHandler msiHandler = EPD.getInstance().getMsiHandler();
            // NB: msiHandler.deleteMessage() will automatically trigger a table refresh
            msiHandler.deleteMessage(notification.get().getMsiMessage());
            setSelectedRow(row - 1);
            notifyListeners();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        MsiHandler msiHandler = EPD.getInstance().getMsiHandler();

        // The back-end does not support the "read" flag, so, we store it
        Set<Integer> readNotificationIds = new HashSet<>();
        for (MsiNotification notificaiton : tableModel.getNotifications()) {
            if (notificaiton.isRead()) {
                readNotificationIds.add(notificaiton.getId());
            }
        }
        
        // Is filtered or not?
        boolean filtered = EPD.getInstance().getSettings().getMsiHandlerSettings().isMsiFilter();
        List<MsiNotification> notifications = new ArrayList<>();
        List<MsiMessageExtended> messages = 
                filtered 
                ? msiHandler.getFilteredMessageList()
                : msiHandler.getMessageList();
         
        // Convert the MSI messages into MSI notificaitons
        for (MsiMessageExtended message : messages) {
            MsiNotification notification = new MsiNotification(message);
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
 * Displays relevant MSI detail information
 */
class MsiDetailPanel extends NotificationDetailPanel<MsiNotification> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public MsiDetailPanel() {
        super();
        
        buildGUI();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(MsiNotification notification) {
        this.notification = notification;
        
        // Special case
        if (notification == null) {
            contentLbl.setText("");
            return;
        }

        MsiMessage msiMessage = notification.get().getMsiMessage();
        MsiLocation msiLocation = msiMessage.getLocation();

        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");
        append(html, "Unique ID", msiMessage.getId());
        append(html, "Msg ID", msiMessage.getMessageId());
        append(html, "Version", msiMessage.getVersion());
        append(html, "Message", msiMessage.getMessage());
        append(html, "ENC text", Formatter.formatString(msiMessage.getEncText(), ""));
        if (msiLocation != null) {
            append(html, "Area", Formatter.formatString(msiLocation.getArea(), ""));
            if (msiLocation.getSubArea() != null && msiLocation.getSubArea().length() > 0) {
                append(html, "Sub area", Formatter.formatString(msiLocation.getSubArea(), ""));
            }
        }
        append(html, "Updated", Formatter.formatShortDateTime(msiMessage.getUpdated()));
        append(html, "Created", Formatter.formatShortDateTime(msiMessage.getCreated()));
        append(html, "Reference", Formatter.formatString(msiMessage.getReference(), ""));
        if (msiMessage.getNavtexNo() != null && msiMessage.getNavtexNo().length() > 0) {
            append(html, "Navtex no", Formatter.formatString(msiMessage.getNavtexNo(), ""));
        }
        append(html, "Priority", Formatter.formatString(msiMessage.getPriority(), ""));
        append(html, "Valid from", Formatter.formatShortDateTime(msiMessage.getValidFrom()));
        append(html, "Valid to", Formatter.formatShortDateTime(msiMessage.getValidTo()));
        if (msiMessage.getLocationPrecision() != null) {
            append(html, "Location precision", Formatter.formatDouble(msiMessage.getLocationPrecision(), 2));
        }
        if (msiMessage.getValidForDraugth() != null) {
            append(html, "Valid for draught", Formatter.formatDouble(msiMessage.getValidForDraugth(), 2));
        }
        if (msiMessage.getValidForShipType() != null) {
            append(html, "Valid for ship type", Formatter.formatString(msiMessage.getValidForShipType(), ""));
        }
        append(html, "Organisation", Formatter.formatString(msiMessage.getOrganisation(), ""));
        append(html, "Username", Formatter.formatString(msiMessage.getUsername(), ""));
        if (msiLocation != null && msiLocation.getPoints() != null) {
            List<String> points = new ArrayList<>();
            for (MsiPoint msiPoint : msiLocation.getPoints()) {
                points.add(String.format("(%.4f,%.4f)", msiPoint.getLatitude(), msiPoint.getLongitude()));
            }
            append(html, "Location", msiLocation.getLocationType().name() + ": " + StringUtils.join(points.iterator(), ", "));
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
