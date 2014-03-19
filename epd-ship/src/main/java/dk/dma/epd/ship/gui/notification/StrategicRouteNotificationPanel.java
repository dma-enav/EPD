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
package dk.dma.epd.ship.gui.notification;

import static dk.dma.epd.common.graphics.GraphicsUtil.bold;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.SOUTH;

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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.common.prototype.gui.notification.NotificationDetailPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationTableModel;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon.RouteChangeListener;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.service.StrategicRouteHandler;

/**
 * A strategic route implementation of the {@linkplain NotificationPanel} class
 */
public class StrategicRouteNotificationPanel extends NotificationPanel<StrategicRouteNotification> {

    private static final long serialVersionUID = 1L;
    
    private static final String[] NAMES = {
        "", "Name", "Called", "Status" };
    
    protected JButton routeDetailsBtn;
    protected JButton cancelBtn;
    
    StrategicNotificationReplyView replyPanel;
    
    /**
     * Constructor
     */
    public StrategicRouteNotificationPanel() {
        super();
        
        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(85);
        splitPane.setDividerLocation(330);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel initButtonPanel() {
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UIManager.getColor("Separator.shadow")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        cancelBtn = new JButton(
                "Cancel", 
                EPD.res().getCachedImageIcon("images/notifications/cross.png"));
        routeDetailsBtn = new JButton(
                "Route Details", 
                EPD.res().getCachedImageIcon("images/notifications/routes.png"));
        
        btnPanel.add(cancelBtn);
        btnPanel.add(routeDetailsBtn);
        btnPanel.add(gotoBtn);
        
        cancelBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                handleCancel();
            }});
        
        routeDetailsBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                showRouteDetails();
            }});
        
        gotoBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                gotoSelectedRoute();
            }});
        
        // Configure the reply panel
        replyPanel = new StrategicNotificationReplyView();
        
        replyPanel.getAcceptBtn().addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                handleAccept();
            }});
        
        replyPanel.getRejectBtn().addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                handleReject();
            }});
        
        
        return btnPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateButtonEnabledState() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null && notification.get().hasRouteMessages()) {
            gotoBtn.setEnabled(true);
            routeDetailsBtn.setEnabled(true);
            
            StrategicRouteMessage msg = notification.get().getLatestRouteMessage();
            
            cancelBtn.setEnabled(!msg.isFromStcc() && !notification.isAcknowledged());
            replyPanel.getRejectBtn().setEnabled(!notification.isAcknowledged() && msg.isFromStcc());
            replyPanel.getAcceptBtn().setEnabled(!notification.isAcknowledged() && msg.isFromStcc());
            
        } else  {
            gotoBtn.setEnabled(false);
            routeDetailsBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setSelectedNotification() {
        // Let super set the notification 
        super.setSelectedNotification();
        
        // If the latest message is from the STCC, install a reply panel
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null && notification.get().hasRouteMessages()) {
            StrategicRouteMessage msg = notification.get().getLatestRouteMessage();
            if (msg.isFromStcc()) {
                replyPanel.getMessageTxtField().setText("");
                replyPanel.getAcceptBtn().setText("Accept");
                getNotificationDetailPanel().addReplyPanelInMessagesPanel(replyPanel);
            }
        }        
    }    
    
    /**
     * Called when a voyage layer route is modified
     */
    public void changeToModifiedAcceptBtn() {
        SwingUtilities.invokeLater(new Runnable() {            
            @Override public void run() {
                replyPanel.getAcceptBtn().setText("Send modified");
            }
        });
    }
    
    /**
     * Cancels the current strategic route
     */
    private void handleCancel() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null) {
            EPDShip.getInstance().getStrategicRouteHandler()
                .sendRejectMsg(
                        notification.getId(),  
                        "Request cancelled", 
                        StrategicRouteStatus.CANCELED);
            EPDShip.getInstance().getNotificationCenter().setVisible(false);
        }
    }
    
    /**
     * Rejects the current strategic route
     */
    private void handleReject() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null) {
            EPDShip.getInstance().getStrategicRouteHandler()
                .sendRejectMsg(
                        notification.getId(),  
                        replyPanel.getMessageTxtField().getText(), 
                        StrategicRouteStatus.REJECTED);
            EPDShip.getInstance().getNotificationCenter().setVisible(false);
        }
    }
    
    /**
     * Accepts the current strategic route
     */
    private void handleAccept() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null) {
            
            EPDShip.getInstance().getStrategicRouteHandler()
                .sendReply(notification.getId(), replyPanel.getMessageTxtField().getText());
            EPDShip.getInstance().getNotificationCenter().setVisible(false);
        }
    }
    
    /**
     * Show the route details for the selected route
     */
    private void showRouteDetails() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null) {
            
            // Open the route properties dialog
            final VoyageLayer voyageLayer = EPDShip.getInstance().getStrategicRouteHandler().getVoyageLayer();
            boolean readOnly = !notification.get().getLatestRouteMessage().isFromStcc();
            Route route = readOnly 
                        ? notification.getLatestRoute()
                        : voyageLayer.getModifiedSTCCRoute();
            RoutePropertiesDialogCommon routePropertiesDialog = new RoutePropertiesDialogCommon(
                    EPDShip.getInstance().getMainFrame(), 
                    EPDShip.getInstance().getMainFrame().getChartPanel(),
                    route, 
                    readOnly);
            
            // Apply changes to the route
            routePropertiesDialog.addRouteChangeListener(new RouteChangeListener() {                
                @Override  public void routeChanged() {
                    voyageLayer.voyageUpdated();
                }});
            
            routePropertiesDialog.setVisible(true);
        }
    }
    /**
     * Zoom to the route of the currently selected notification
     */
    public void gotoSelectedRoute() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null && 
                EPD.getInstance().getMainFrame().getActiveChartPanel() != null) {
            EPD.getInstance().getMainFrame().getActiveChartPanel()
                .zoomToWaypoints(notification.getOriginalRoute().getWaypoints());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotitficationType() {
        return NotificationType.STRATEGIC_ROUTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationTableModel<StrategicRouteNotification> initTableModel() {
        return new NotificationTableModel<StrategicRouteNotification>() {
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
                StrategicRouteNotification notification = getNotification(rowIndex);
                
                switch (columnIndex) {
                case 0: return !notification.isRead() 
                        ? ICON_UNREAD 
                        : (notification.isAcknowledged() ? ICON_ACKNOWLEDGED : null);
                case 1: return notification.getCallerlName();
                case 2: return Formatter.formatShortDateTime(notification.getDate());
                case 3: return notification.get().getStatus();
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
    protected NotificationDetailPanel<StrategicRouteNotification> initNotificationDetailPanel() {
        return new StrategicRouteNotificationDetailPanel();
    }
    
    /**
     * Returns the notification detail panel
     * @return the notification detail panel
     */
    private StrategicRouteNotificationDetailPanel getNotificationDetailPanel() {
        return (StrategicRouteNotificationDetailPanel)notificationDetailPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        StrategicRouteHandler strategicRouteHandler = EPDShip.getInstance().getStrategicRouteHandler();
        
        // The back-end does not support the "read" flag, so, we store it
        Set<Long> readNotificationIds = new HashSet<>();
        for (StrategicRouteNotification notificaiton : tableModel.getNotifications()) {
            if (notificaiton.isRead()) {
                readNotificationIds.add(notificaiton.getId());
            }
        }
        
        List<StrategicRouteNotification> notifications = new ArrayList<>();
        for (StrategicRouteNegotiationData routeData : strategicRouteHandler.getSortedStrategicNegotiationData()) {
            StrategicRouteNotification notification = new StrategicRouteNotification(routeData);
            
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
 * A panel used for accepting or rejecting a route request
 */
class StrategicNotificationReplyView extends JPanel {

    private static final long serialVersionUID = 1L;

    JButton acceptBtn = new JButton("Accept", EPD.res().getCachedImageIcon("images/notifications/tick.png"));;
    JButton rejectBtn = new JButton("Reject", EPD.res().getCachedImageIcon("images/notifications/cross.png"));
    JTextArea messageTxtField = new JTextArea();
    
    /**
     * Create the panel.
     */
    public StrategicNotificationReplyView() {

        super(new GridBagLayout());
        
        Insets insets0  = new Insets(0, 0, 0, 0);
        Insets insets1  = new Insets(5, 5, 5, 5);
        Insets insets2  = new Insets(5, 5, 0, 0);

        // Title
        JPanel titlePanel = new JPanel(new GridBagLayout());
        add(titlePanel, 
                new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets0, 0, 0));

        titlePanel.setBackground(titlePanel.getBackground().darker());
        titlePanel.add(bold(new JLabel("Send reply to STCC")), 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));
        
        // Reply
        messageTxtField.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageTxtField);
        scrollPane.setMinimumSize(new Dimension(180, 40));
        scrollPane.setPreferredSize(new Dimension(180, 40));
        add(new JLabel("Reply:"), 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, insets2, 0, 0));
        add(scrollPane, 
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));
        
        // Buttons
        JPanel btnPanel = new JPanel(new GridBagLayout());
        add(btnPanel, 
                new GridBagConstraints(1, 1, 1, 2, 0.0, 0.0, SOUTH, NONE, insets1, 0, 0));
        
        btnPanel.add(acceptBtn, 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
        btnPanel.add(rejectBtn, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
    }

    public JButton getAcceptBtn() {
        return acceptBtn;
    }

    public JButton getRejectBtn() {
        return rejectBtn;
    }

    public JTextArea getMessageTxtField() {
        return messageTxtField;
    }
 }
