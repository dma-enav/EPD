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
import dma.msinm.MCAttachment;
import dma.msinm.MCLocation;
import dma.msinm.MCMessage;
import dma.msinm.MCMessageDesc;
import dma.msinm.MCMsiNmService;
import dma.msinm.MCPoint;
import dma.msinm.MCReference;
import net.maritimecloud.core.id.MaritimeId;
import org.apache.commons.lang.StringUtils;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * An MSI-NM-specific implementation of the {@linkplain NotificationPanel} class
 */
public class MsiNmNotificationPanel extends NotificationPanel<MsiNmNotification> implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String[] NAMES = {
        "", "TYPE", "ID", "Valid From", "Main area"
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
        msinmServicePanel.add(new JLabel("Provider"));
        msinmServicePanel.add(msiNmServiceComboBox);
        JButton reloadBtn = new JButton("Reload");
        reloadBtn.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                EPD.getInstance().getMsiNmHandler().reloadMsiNmMessages();
            }});
        msinmServicePanel.add(reloadBtn);

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
                case 3: return Formatter.formatShortDateTimeNoTz(new Date(notification.get().getValidFrom().getTime()));
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
        return new MsiNmDetailPanel(this);
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
class MsiNmDetailPanel extends NotificationDetailPanel<MsiNmNotification> implements HyperlinkListener {

    private static final long serialVersionUID = 1L;

    MsiNmNotificationPanel notificationPanel;
    JEditorPane contentPane = new JEditorPane("text/html", "");
    JScrollPane scrollPane = new JScrollPane(
            contentPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    /**
     * Constructor
     * @param notificationPanel the parent notification panel
     */
    public MsiNmDetailPanel(MsiNmNotificationPanel notificationPanel) {
        super();

        this.notificationPanel = notificationPanel;
        buildGUI();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildGUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPane.setEditable(false);
        contentPane.setOpaque(false);
        contentPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        contentPane.addHyperlinkListener(this);

        StyleSheet styleSheet = ((HTMLEditorKit)contentPane.getEditorKit()).getStyleSheet();
        styleSheet.addRule("a {color: #8888FF;}");
        styleSheet.addRule(".title {font-size: 14px;}");

        add(scrollPane, BorderLayout.CENTER);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent hle) {
        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
            try {
                URL url = hle.getURL();
                if (url.getHost().equals("msinm") && url.getPath().length() > 1) {
                    String msinmId = url.getPath().substring(1);

                    for (MsiNmNotification notification : notificationPanel.getNotifications()) {
                        if (notification.getSeriesId().equals(msinmId)) {
                            EPD.getInstance().getNotificationCenter().openNotification(NotificationType.MSI_NM, notification.getId(), false);
                            return;
                        }
                        Toolkit.getDefaultToolkit().beep();
                    }
                } else {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(new URI(hle.getURL().toString()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(MsiNmNotification notification) {
        this.notification = notification;
        
        // Special case
        if (notification == null) {
            contentPane.setText("");
            scrollPane.getViewport().setViewPosition(new Point(0, 0));
            return;
        }

        MCMessage message = notification.get();
        MCMessageDesc desc = message.getDescs().size() > 0 ? message.getDescs().get(0) : null;

        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");

        // Title line
        html.append("<tr><td colspan='2'>");
        if (message.getOriginalInformation() != null && message.getOriginalInformation()) {
            html.append("<strong>&#10029;</strong><br/>");
        }
        html.append("<b>").append(notification.getSeriesId()).append(". ").append(notification.getAreaLineage(0, 100));
        if (desc != null && StringUtils.isNotBlank(desc.getTitle())) {
            html.append(" - ").append(desc.getTitle());
        }
        html.append("</b></td></tr>");

        // References
        for (MCReference ref : message.getReferences()) {
            String id = notification.formatSeriesId(ref.getSeriesIdentifier());
            append(html, "Reference", String.format("<a href='http://msinm/%s'>%s</a> (%s)", id, id, ref.getType().name().toLowerCase()));
        }

        // Time
        if (desc != null && StringUtils.isNotBlank(desc.getTime())) {
            append(html, "Time", desc.getTime().trim().replace("\n", "<br/>"));
        } else {
            String time = Formatter.formatShortDateTime(new Date(message.getValidFrom().getTime()));
            if (message.getValidTo() != null) {
                time += " - " + Formatter.formatShortDateTime(new Date(message.getValidTo().getTime()));
            }
            append(html, "Time", time);
        }

        // Locations
        if (message.getLocations().size() > 0) {
            StringBuilder location = new StringBuilder();
            for (MCLocation loc : message.getLocations()) {
                location.append("<p>");
                formatLoc(location, loc);
                location.append("</p>");
            }
            append(html, "Location", location.toString());
        }

        if (desc != null) {
            append(html, "Details", desc.getDescription());
            append(html, "Note", desc.getNote());
            append(html, "Publication", desc.getPublication());
            append(html, "Source", desc.getSource());
        }

        if (message.getAttachments().size() > 0) {
            StringBuilder attachments = new StringBuilder();
            for (MCAttachment att : message.getAttachments()) {
                attachments.append("<div style='text-align: center; margin-bottom: 10px;'>");
                attachments.append(String.format("<div style='display: inline-block; vertical-align: middle;'><a href='%s'><img src='%s?size=32' border='0'></a></div>",
                        att.getPath(), att.getThumbnail()));
                attachments.append(String.format("<div><a href='%s'>%s</a></div>", att.getPath(), att.getName()));
                attachments.append("</div>");
            }
            append(html, "Attachments", attachments.toString());
        }

        html.append("</table>");
        html.append("</html>");
        contentPane.setText(html.toString());
        scrollPane.getViewport().setViewPosition(new Point(0,0));
    }

    /**
     * Formats the location as html
     * @param html the html
     * @param loc the location
     */
    void formatLoc(StringBuilder html, MCLocation loc) {
        if (loc.getDescs().size() > 0 && StringUtils.isNotBlank(loc.getDescs().get(0).getDescription())) {
            html.append(loc.getDescs().get(0).getDescription()).append("<br/>");
        }
        for (MCPoint pos : loc.getPoints()) {
            html.append(Formatter.latToPrintable(pos.getLat()))
                    .append(" ")
                    .append(Formatter.lonToPrintable(pos.getLon()));
            if (pos.getDescs().size() > 0 && StringUtils.isNotBlank(pos.getDescs().get(0).getDescription())) {
                html.append(", ").append(pos.getDescs().get(0).getDescription());
            }
            html.append("<br/>");
        }
    }

    /**
     * If non-empty, appends a table row with the given title and value
     * @param html the html to append to
     * @param title the title
     * @param value the value
     */
    private void append(StringBuilder html, String title, Object value) {
        if (value != null && value.toString().length() > 0) {
            html.append("<tr><td valign='top'><i>")
                .append(title)
                .append("</i></td><td>")
                .append(value)
                .append("</td></tr>");
        }
    }
}
