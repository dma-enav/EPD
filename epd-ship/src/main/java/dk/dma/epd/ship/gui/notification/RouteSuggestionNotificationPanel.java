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
package dk.dma.epd.ship.gui.notification;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTHWEST;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import net.maritimecloud.core.id.MmsiId;
import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionStatus;
import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.gui.notification.NotificationDetailPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationTableModel;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.ChatServiceData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.NameUtils;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.RouteSuggestionHandler;

/**
 * A route suggestion implementation of the {@linkplain NotificationPanel} class
 */
public class RouteSuggestionNotificationPanel extends NotificationPanel<RouteSuggestionNotification> {

    private static final long serialVersionUID = 1L;

    private static final String[] NAMES = { "", "MMSI", "Route Name", "Date", "Status" };

    protected JButton routeDetailsBtn;
    protected JButton createRouteBtn;
    protected JButton acceptBtn;
    protected JButton rejectBtn;
    protected JButton notedBtn;

    RouteSuggestionReplyView replyPanel;

    /**
     * Constructor
     */
    public RouteSuggestionNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);

        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        splitPane.setDividerLocation(360);
        setCellAlignment(1, JLabel.RIGHT);
        setCellAlignment(2, JLabel.RIGHT);

        doRefreshNotifications();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ButtonPanel initButtonPanel() {
        ButtonPanel btnPanel = new ButtonPanel(notificationCenter);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, UIManager.getColor("Separator.shadow")),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        routeDetailsBtn = new JButton("Details", EPD.res().getCachedImageIcon("images/notifications/routes.png"));

        createRouteBtn = new JButton("Add Route", EPDShip.res().getCachedImageIcon("images/toolbar/marker--plus.png"));

        btnPanel.add(routeDetailsBtn);
        btnPanel.add(createRouteBtn);
        btnPanel.add(gotoBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(chatBtn);

        routeDetailsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRouteSuggestionDetails();
            }
        });

        createRouteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRoute();
            }
        });

        gotoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gotoSelectedRouteSuggestion();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedNotifications();
            }
        });

        chatBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatWithNotificationTarget();
            }
        });

        // Configure the reply panel
        replyPanel = new RouteSuggestionReplyView();

        replyPanel.getAcceptBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAccept();
            }
        });

        replyPanel.getRejectBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReject();
            }
        });

        replyPanel.getNotedBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleWait();
            }
        });

        return btnPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setSelectedNotification() {
        // Let super set the notification
        super.setSelectedNotification();

        // If the message has not been replied yet, install a reply panel
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null && notification.get().getReply() == null) {
            notificationDetailPanel.add(replyPanel, BorderLayout.NORTH);
            replyPanel.getMessageTxtField().setText("");
        } else {
            notificationDetailPanel.remove(replyPanel);
        }
    }

    /**
     * Show the route details for the selected route suggestion
     */
    private void showRouteSuggestionDetails() {
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            RoutePropertiesDialogCommon routePropertiesDialog = new RoutePropertiesDialogCommon(EPDShip.getInstance()
                    .getMainFrame(), EPDShip.getInstance().getMainFrame().getChartPanel(), notification.get().getRoute(), true);

            routePropertiesDialog.setVisible(true);
        }
    }

    /**
     * Creates a route for the route suggestion
     */
    private void createRoute() {
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            Route route = notification.get().getRoute().copy();
            notification.get().getRoute().setVisible(false);
            route.setVisible(true);
            EPDShip.getInstance().getRouteManager().addRoute(route);
        }
    }

    /**
     * Zoom in on the selected route suggestion
     */
    private void gotoSelectedRouteSuggestion() {
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            EPD.getInstance().getMainFrame().getActiveChartPanel().zoomToWaypoints(notification.get().getRoute().getWaypoints());
        }
    }

    /**
     * Accepts the current strategic route
     */
    private void handleAccept() {
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            EPDShip.getInstance()
                    .getRouteSuggestionHandler()
                    .sendRouteSuggestionReply(notification.getId(), RouteSuggestionStatus.ACCEPTED,
                            replyPanel.getMessageTxtField().getText());
            EPDShip.getInstance().getNotificationCenter().setVisible(false);
        }
    }

    /**
     * Rejects the current strategic route
     */
    private void handleReject() {
        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            EPDShip.getInstance()
                    .getRouteSuggestionHandler()
                    .sendRouteSuggestionReply(notification.getId(), RouteSuggestionStatus.REJECTED,
                            replyPanel.getMessageTxtField().getText());
            EPDShip.getInstance().getNotificationCenter().setVisible(false);
        }
    }

    /**
     * Flags the current strategic route as noted
     */
    private void handleWait() {

        RouteSuggestionNotification notification = getSelectedNotification();
        if (notification != null) {
            // EPDShip.getInstance()
            // .getRouteSuggestionHandler()
            // .sendRouteSuggestionReply(notification.getId(), RouteSuggestionStatus.WAIT,
            // replyPanel.getMessageTxtField().getText());

            long mmsi = EPDShip.getInstance().getRouteSuggestionHandler().getRouteSuggestion(notification.getId()).getMmsi();

            ChatServiceData chatData = EPD.getInstance().getChatServiceHandler().getChatServiceData(new MmsiId((int) mmsi));

            // Sanity check
            if (chatData == null) {
                return;
            }

            String msg = "Cannot reply to the tactical route suggestion at this time. Please standby for a reply";

            NotificationSeverity severity = NotificationSeverity.MESSAGE;

            EPD.getInstance().getChatServiceHandler().sendChatMessage(chatData.getId(), msg, severity);

            EPDShip.getInstance().getNotificationCenter().setVisible(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateButtonEnabledState() {
        super.updateButtonEnabledState();
        RouteSuggestionNotification n = getSelectedNotification();
        routeDetailsBtn.setEnabled(n != null);
        createRouteBtn.setEnabled(n != null && n.get().isReplied());
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
                case 0:
                    return !notification.isRead() ? ICON_UNREAD : (notification.isAcknowledged() ? ICON_ACKNOWLEDGED : null);
                case 1:
                    return "" + notification.get().getMmsi();
                case 2:
                    return notification.get().getMessage().getRoute().getName();
                case 3:
                    return Formatter.formatShortDateTimeNoTz(notification.getDate());
                case 4:
                    return notification.get().getStatus().toString();
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

            notification.setAcknowledged(true);
            notification.setRead(true); // Implied by acknowledged

            RouteSuggestionHandler routeSuggestionHandler = EPDShip.getInstance().getRouteSuggestionHandler();
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
            RouteSuggestionHandler routeSuggestionHandler = EPDShip.getInstance().getRouteSuggestionHandler();
            RouteSuggestionData routeSuggestion = notification.get();
            // NB: routeSuggestionHandler.removeSuggestion() will automatically trigger a table refresh
            routeSuggestionHandler.removeSuggestion(routeSuggestion.getId());
            setSelectedRow(row - 1);
            notifyListeners();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        RouteSuggestionHandler routeSuggestionHandler = EPDShip.getInstance().getRouteSuggestionHandler();

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

            if (routeSuggestion.isReplied()) {
                notification.setAcknowledged(true);
            }

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
        append(html, "Sender", NameUtils.getName((int) routeSuggestion.getMmsi()));
        append(html, "Route Name", routeSuggestion.getMessage().getRoute().getName());
        append(html, "Sent Date", Formatter.formatShortDateTime(routeSuggestion.getMessage().getSentDate()));
        append(html, "Message", Formatter.formatHtml(routeSuggestion.getMessage().getMessage()));
        append(html, "Status", getStatus(routeSuggestion));
        append(html, "DST/BRG/TTG/SPD", getWpInfo(routeSuggestion));
        append(html, "ETA first wp", Formatter.formatShortDateTime(routeSuggestion.getRoute().getEtas().get(0)));
        append(html,
                "ETA last wp",
                Formatter.formatShortDateTime(routeSuggestion.getRoute().getEtas()
                        .get(routeSuggestion.getRoute().getWaypoints().size() - 1)));
        if (routeSuggestion.getReply() != null) {
            append(html, "Reply Sent", Formatter.formatShortDateTime(routeSuggestion.getReply().getSentDate()));
            append(html, "Reply Message", Formatter.formatHtml(routeSuggestion.getReply().getMessage()));
        }
        html.append("</table>");
        html.append("</html>");
        contentLbl.setText(html.toString());
    }

    /**
     * Formats the status of the route suggestion as HTML by including the Maritime Cloud message status.
     * 
     * @param routeSuggestion
     *            the route suggestion
     * @return the status
     */
    private String getStatus(RouteSuggestionData routeSuggestion) {
        StringBuilder status = new StringBuilder();
        status.append(String.format("<span style='color:%s'>%s</span>",
                GraphicsUtil.toHtmlColor(routeSuggestion.getStatus().getColor()), routeSuggestion.getStatus().toString()));
        if (routeSuggestion.getReply() != null && routeSuggestion.getReply().getCloudMessageStatus() != null) {
            status.append("&nbsp;<small>(" + routeSuggestion.getReply().getCloudMessageStatus().getTitle() + ")</small>");
        }
        return status.toString();
    }

    /**
     * Returns the way point info
     * 
     * @param routeSuggestion
     *            the route suggestion
     * @return the way point info
     */
    private String getWpInfo(RouteSuggestionData routeSuggestion) {
        // Get current position

        PntData gpsData = EPDShip.getInstance().getPntHandler().getCurrentData();
        Route route = routeSuggestion.getRoute();

        StringBuilder str = new StringBuilder();
        if (gpsData != null && !gpsData.isBadPosition() && route.getWaypoints().size() > 0) {
            double dst = route.getWaypoints().get(0).getPos().rhumbLineDistanceTo(gpsData.getPosition()) / 1852;
            str.append(Formatter.formatDistNM(dst));
            double brg = route.getWaypoints().get(0).getPos().rhumbLineBearingTo(gpsData.getPosition());
            str.append(" / " + Formatter.formatDegrees(brg, 2));
            Long ttg = null;
            if (route.getEtas().get(0) != null) {
                ttg = route.getEtas().get(0).getTime() - PntTime.getDate().getTime();
            }
            if (ttg != null && ttg < 0) {
                ttg = null;
            }
            Double spd = null;
            if (ttg != null) {
                spd = dst / ((double) ttg / 1000 / 60 / 60);
            }
            str.append(" / " + Formatter.formatTime(ttg));
            str.append(" / " + Formatter.formatSpeed(spd));

        } else {
            str.append("N/A");
        }
        return str.toString();
    }

    /**
     * If non-empty, appends a table row with the given title and value
     * 
     * @param html
     *            the html to append to
     * @param title
     *            the title
     * @param value
     *            the value
     */
    private void append(StringBuilder html, String title, Object value) {
        if (value != null && value.toString().length() > 0) {
            html.append("<tr><td valign='top'><b>").append(title).append("</b></td><td>").append(value).append("</td></tr>");
        }
    }
}

/**
 * A panel used for accepting or rejecting a route suggestion
 */
class RouteSuggestionReplyView extends JPanel {

    private static final long serialVersionUID = 1L;

    JButton acceptBtn = new JButton("Accept", EPD.res().getCachedImageIcon("images/notifications/tick.png"));;
    JButton rejectBtn = new JButton("Reject", EPD.res().getCachedImageIcon("images/notifications/cross.png"));
    JButton waitBtn = new JButton("Wait", EPD.res().getCachedImageIcon("images/notifications/flag-blue.png"));
    JTextArea messageTxtField = new JTextArea();

    /**
     * Create the panel.
     */
    public RouteSuggestionReplyView() {

        super(new GridBagLayout());
        setBorder(new TitledBorder("Reply"));

        Insets insets1 = new Insets(5, 5, 5, 5);
        Insets insets2 = new Insets(5, 5, 0, 0);

        // Reply
        messageTxtField.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageTxtField);
        scrollPane.setMinimumSize(new Dimension(180, 80));
        scrollPane.setPreferredSize(new Dimension(180, 80));
        add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));

        // Buttons
        JPanel btnPanel = new JPanel(new GridBagLayout());
        add(btnPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, NORTH, NONE, insets1, 0, 0));

        btnPanel.add(acceptBtn, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
        btnPanel.add(rejectBtn, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
        btnPanel.add(waitBtn, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
    }

    public JButton getAcceptBtn() {
        return acceptBtn;
    }

    public JButton getRejectBtn() {
        return rejectBtn;
    }

    public JButton getNotedBtn() {
        return waitBtn;
    }

    public JTextArea getMessageTxtField() {
        return messageTxtField;
    }
}
