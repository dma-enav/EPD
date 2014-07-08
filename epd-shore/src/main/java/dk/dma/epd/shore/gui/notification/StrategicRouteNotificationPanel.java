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
package dk.dma.epd.shore.gui.notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.gui.notification.NotificationDetailPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationTableModel;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.route.RoutePropertiesDialog;
import dk.dma.epd.shore.service.StrategicRouteHandler;
import dk.dma.epd.shore.voyage.Voyage;

/**
 * A strategic route implementation of the {@linkplain NotificationPanel} class
 */
public class StrategicRouteNotificationPanel extends NotificationPanel<StrategicRouteNotification> {

    private static final long serialVersionUID = 1L;
    
    private static final String[] NAMES = {
        "", "Name", "Callsign", "Date", "Status" };

    protected JButton routeDetailsBtn;
    protected JButton handleRequestBtn;
    
    /**
     * Constructor
     */
    public StrategicRouteNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);
        
        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(85);
        splitPane.setDividerLocation(350);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ButtonPanel initButtonPanel() {
        ButtonPanel btnPanel = new ButtonPanel(notificationCenter);
        btnPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UIManager.getColor("Separator.shadow")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        routeDetailsBtn = new JButton(
                "Route Details", 
                EPDShore.res().getCachedImageIcon("images/notificationcenter/routes.png"));
        handleRequestBtn = new JButton(
                "Handle Request", 
                EPDShore.res().getCachedImageIcon("images/notificationcenter/arrow-circle-315.png"));
        
        btnPanel.add(routeDetailsBtn);
        btnPanel.add(handleRequestBtn);
        btnPanel.add(chatBtn);
        
        routeDetailsBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                showRouteDetails();
            }});
        
        handleRequestBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                handleRouteRequest();
            }});
        
        chatBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                chatWithNotificationTarget();
            }});
        
        return btnPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateButtonEnabledState() {
        StrategicRouteNotification n = getSelectedNotification();
        routeDetailsBtn.setEnabled(n != null);
        handleRequestBtn.setEnabled(n != null && !n.isAcknowledged());
        updateChatEnabledState();
    }
    
    /**
     * Show the route details for the selected route
     */
    private void showRouteDetails() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null) {
            
            // Open the route properties dialog
            RoutePropertiesDialog routePropertiesDialog = new RoutePropertiesDialog(
                    EPDShore.getInstance().getMainFrame(), 
                    EPDShore.getInstance().getMainFrame().getActiveChartPanel(),
                    notification.get().getLatestRoute());
            
            routePropertiesDialog.setVisible(true);
        }
    }
    
    /**
     * Handle the selected route request
     */
    private void handleRouteRequest() {
        StrategicRouteNotification notification = getSelectedNotification();
        if (notification != null) {
            
            Voyage voyage = new Voyage(
                    notification.get().getMmsi(), 
                    notification.get().getLatestRoute(),
                    notification.getId());

            EPDShore.getInstance().getMainFrame().addStrategicRouteExchangeHandlingWindow(
                notification.get().getOriginalRoute(),
                notification.getCallerlName(), 
                voyage, 
                false);

            // Hide the notification center
            SwingUtilities.getWindowAncestor(detailPanel).setVisible(false);
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
                case 2: return notification.getVesselCallsign();
                case 3: return Formatter.formatShortDateTimeNoTz(notification.getDate());
                case 4: return notification.get().getStatus();
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
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        StrategicRouteHandler strategicRouteHandler = EPDShore.getInstance().getStrategicRouteHandler();
        
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
