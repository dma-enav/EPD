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

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.NameUtils;
import dma.msinm.MCLocation;
import dma.msinm.MCMessage;
import dma.msinm.MCMessageDesc;
import dma.msinm.MCMsiNmService;
import dma.msinm.MCPoint;
import dma.msinm.MCReference;
import net.maritimecloud.core.id.MaritimeId;
import org.apache.commons.lang.StringUtils;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An MSI-NM-specific implementation of the {@linkplain NotificationPanel} class
 */
public class MsiNmNotificationPanel extends NotificationPanel<MsiNmNotification> implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String[] NAMES = {
        "", "TYPE", "ID", "Updated", "Main area"
    };

    private JComboBox<MsiNmServiceItem> msiNmServiceComboBox = new JComboBox<>();

    /**
     * Constructor
     */
    public MsiNmNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);
        
        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setPreferredWidth(30);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        splitPane.setDividerLocation(400);

        // Create the MSI-NM service selector
        JPanel msinmServicePanel = new JPanel();
        listPanel.add(msinmServicePanel, BorderLayout.NORTH);
        msinmServicePanel.add(new JLabel("MSI-NM Provider"));
        msinmServicePanel.add(msiNmServiceComboBox);

        refreshMsiNmServices();
        msiNmServiceComboBox.addActionListener(this);
    }

    /**
     * Called when the MSI-NM service selection changes
     * @param ae the action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == msiNmServiceComboBox) {
            MsiNmServiceItem selItem = (MsiNmServiceItem)msiNmServiceComboBox.getSelectedItem();
            if (selItem != null) {
                EPD.getInstance().getMsiNmHandler().setSelectedMsiNmServiceId(selItem.getId());
            }
        }
    }

    /**
     * Refreshes the MSI-NM service list
     */
    public void refreshMsiNmServices() {

        MaritimeId msiNmServiceId = EPD.getInstance().getMsiNmHandler().getSelectedMsiNmServiceId();

        msiNmServiceComboBox.removeAllItems();
        MsiNmServiceItem selItem = null;
        for (MCMsiNmService service : EPD.getInstance().getMsiNmHandler().getMsiNmServiceList()) {
            MsiNmServiceItem item = new MsiNmServiceItem(service);
            msiNmServiceComboBox.addItem(item);
            if (item.getId().equals(msiNmServiceId)) {
                selItem = item;
            }
        }

        if (selItem == null && msiNmServiceId != null) {
            selItem = new MsiNmServiceItem(msiNmServiceId);
            msiNmServiceComboBox.addItem(selItem);
        }

        if (selItem != null) {
            msiNmServiceComboBox.setSelectedItem(selItem);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotitficationType() {
        return NotificationType.MSI_NM;
    }
            
    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationTableModel<MsiNmNotification> initTableModel() {
        return new NotificationTableModel<MsiNmNotification>() {
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
                MsiNmNotification notification = getNotification(rowIndex);
                
                switch (columnIndex) {
                case 0: return !notification.isRead()
                                ? ICON_UNREAD
                                : (notification.isAcknowledged() ? ICON_ACKNOWLEDGED : null);
                case 1: return notification.get().getSeriesIdentifier().getMainType();
                case 2: return notification.getSeriesId();
                case 3: return Formatter.formatShortDateTimeNoTz(notification.getDate());
                case 4: return notification.getAreaLineage(1, 2);
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
    protected NotificationDetailPanel<MsiNmNotification> initNotificationDetailPanel() {
        return new MsiNmDetailPanel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acknowledgeNotification(MsiNmNotification notification) {
        if (notification != null && !notification.isAcknowledged()) {
            notification.setAcknowledged(true);
            notification.setRead(true); // Implied by acknowledged

            // NB: doUpdate() will automatically trigger a table refresh
            EPD.getInstance().getMsiNmHandler().doUpdate();
            selectFirstUnacknowledgedRow();
            notifyListeners();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotification(MsiNmNotification notification) {
        int row = table.getSelectedRow();
        if (notification != null) {
            // NB: deleteMsiNmMessage() will automatically trigger a table refresh
            EPD.getInstance().getMsiNmHandler().deleteMsiNmMessage(notification);
            setSelectedRow(row - 1);
            notifyListeners();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        MsiNmServiceHandlerCommon msiNmHandler = EPD.getInstance().getMsiNmHandler();

        // Is filtered or not?
        boolean filtered = EPD.getInstance().getSettings().getEnavSettings().isMsiFilter();
        List<MsiNmNotification> notifications = msiNmHandler.getMsiNmMessages(filtered);
        tableModel.setNotifications(notifications);
        refreshTableData();
        notifyListeners();
    }

    /**
     * Helper class used for MSI-NM service selection
     */
    public static class MsiNmServiceItem {
        MCMsiNmService service;
        String name;
        MaritimeId id;

        /**
         * Constructor
         * @param service the MSI-NM service
         */
        public MsiNmServiceItem(MCMsiNmService service) {
            this.service = service;
            this.id = service.getCaller();
            this.name = NameUtils.getName(this.id, NameUtils.NameFormat.MEDIUM);
        }

        /**
         * Constructor
         * @param id the MSI-NM id
         */
        public MsiNmServiceItem(MaritimeId id) {
            this.id = id;
            this.name = NameUtils.getName(this.id, NameUtils.NameFormat.MEDIUM) + " (Un-connected)";
        }

        public MCMsiNmService getService() {
            return service;
        }

        public MaritimeId getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}


/**
 * Displays relevant MSI-NM detail information
 */
class MsiNmDetailPanel extends NotificationDetailPanel<MsiNmNotification> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public MsiNmDetailPanel() {
        super();
        
        buildGUI();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(MsiNmNotification notification) {
        this.notification = notification;
        
        // Special case
        if (notification == null) {
            contentLbl.setText("");
            return;
        }

        MCMessage message = notification.get();

        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");
        append(html, "Unique ID", message.getId());
        append(html, "Message ID", notification.getSeriesId());

        append(html, "Area", notification.getAreaLineage(0, 100));
        MCMessageDesc desc = message.getDescs().size() > 0 ? message.getDescs().get(0) : null;
        if (desc != null) {
            append(html, "Title", Formatter.formatString(desc.getTitle(), ""));
            append(html, "Details", desc.getDescription());
            append(html, "Time", desc.getTime());
            append(html, "Note", desc.getNote());
            append(html, "Publication", desc.getPublication());
            append(html, "Source", desc.getSource());
        }

        append(html, "Updated", Formatter.formatShortDateTime(new Date(message.getUpdated().getTime())));
        append(html, "Created", Formatter.formatShortDateTime(new Date(message.getCreated().getTime())));
        append(html, "Valid from", Formatter.formatShortDateTime(new Date(message.getValidFrom().getTime())));
        if (message.getValidTo() != null) {
            append(html, "Valid to", Formatter.formatShortDateTime(new Date(message.getValidTo().getTime())));
        }
        for (MCReference ref : message.getReferences()) {
            append(html, "Reference", notification.formatSeriesId(ref.getSeriesIdentifier()));
        }

        for (MCLocation loc : message.getLocations()) {
            List<String> points = new ArrayList<>();
            for (MCPoint pt : loc.getPoints()) {
                points.add(String.format("(%.4f,%.4f)", pt.getLat(), pt.getLon()));
            }
            append(html, "Location", loc.getType().name() + ": " + StringUtils.join(points.iterator(), ", "));
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
