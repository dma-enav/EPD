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
package dk.dma.epd.common.prototype.gui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.prototype.gui.notification.NotificationCenterCommon;
import dk.dma.epd.common.prototype.gui.notification.NotificationLabel;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel.NotificationStatistics;
import dk.dma.epd.common.prototype.gui.notification.PopUpNotification;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel.NotificationPanelListener;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.MaritimeCloudServiceCommon;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.prototype.status.IStatusComponent;

/**
 * Panel shown below the chart
 */
public class BottomPanelCommon extends OMComponentPanel implements MouseListener, ActionListener, NotificationPanelListener {

    private static final long serialVersionUID = 1L;

    Timer statusTimer;
    
    private ShoreServicesCommon shoreServices;
    private AisHandlerCommon aisHandler;
    private MaritimeCloudServiceCommon maritimeCloudService;

    private StatusLabel aisStatus = new StatusLabel("AIS");
    private StatusLabel shoreServiceStatus = new StatusLabel("Shore services");
    private StatusLabel cloudStatus = new StatusLabel("Maritime Cloud");

    protected List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();
    private JToolBar statusToolBar = new JToolBar();
    private JPanel statusIcons = new JPanel();
    
    private JPanel notificationPanel = new JPanel();
    private PopUpNotification notificationPopUp;
    private Map<NotificationPanel<?>, NotificationLabel> notificationLabelLookUp = new HashMap<>();
    
    /**
     * Constructor
     */
    public BottomPanelCommon() {
        super();
        setLayout(new BorderLayout());
        
        // Set up status panel
        add(statusIcons, BorderLayout.EAST);
        statusIcons.add(statusToolBar);
        statusToolBar.setFloatable(false);
        statusToolBar.addMouseListener(this);        
        // Add the status components
        addStatusComponents();
        
        statusTimer = new Timer(3000, this);
        statusTimer.start();
    }
    
    /**
     * Adds the status components
     */
    protected void addStatusComponents() {
        addToolbarComponent(aisStatus);
        addSeparator();
        addToolbarComponent(shoreServiceStatus);
        addSeparator();
        addToolbarComponent(cloudStatus);        
    }
    
    /**
     * Called when the notification center has been initialized
     * @param notifcationCenter the notification center
     */
    protected void addNotificationCenter(final NotificationCenterCommon notifcationCenter) {

        // Add a label for each notification panel in the center
        for (NotificationPanel<?> panel : notifcationCenter.getPanels()) {
            NotificationLabel label = new NotificationLabel(panel) {
                private static final long serialVersionUID = 1L;
                @Override public void labelClicked(NotificationType type) {
                    notifcationCenter.setActiveType(type);
                    notifcationCenter.setVisible(true);
                }
            };
            notificationPanel.add(label);
            notificationLabelLookUp.put(panel, label);
            
            // Hook up as a listener for notification changes
            panel.addListener(this);
        }
        add(notificationPanel, BorderLayout.WEST);
        
        
        // Set up the notification panel
        notificationPopUp = new PopUpNotification(
                GraphicsUtil.getTopLevelContainer(notificationPanel),
                new Point(0, getLocation().y));
    }

    
    /**
     * Adds the given component to the toolbar
     * @param component the component to add
     */
    protected void addToolbarComponent(Component component) {
        statusToolBar.add(component);
        component.addMouseListener(this);
    }
    
    /**
     * Adds a separator to the toolbar
     */
    protected void addSeparator() {
        statusToolBar.add(Box.createHorizontalStrut(5));
        statusToolBar.add( new JSeparator(SwingConstants.VERTICAL));
        statusToolBar.add(Box.createHorizontalStrut(5));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
            statusComponents.add(aisHandler);
        } else if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
            statusComponents.add(shoreServices);
        } else if (obj instanceof MaritimeCloudServiceCommon) {
            maritimeCloudService = (MaritimeCloudServiceCommon) obj;
            statusComponents.add(maritimeCloudService);
        } else if (obj instanceof NotificationCenterCommon) {
            addNotificationCenter((NotificationCenterCommon) obj);
        }
    }
    
    /**
     * Trigger the given alert for the given notification
     * 
     * @param panel the notification panel
     * @param notification the notification
     * @param alert the alert
     */
    public void triggerAlert(NotificationPanel<?> panel, Notification<?, ?> notification, NotificationAlert alert) {
        notificationPopUp.addNotification(panel, notification);
        
        // Re-position the pop-up depending on the panel
        NotificationLabel label = notificationLabelLookUp.get(panel);
        if (label != null) {
            Point location = new Point(label.getX() + 10, getLocation().y);
            notificationPopUp.adjustBottomLeftLocation(location);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notificationsUpdated(NotificationStatistics stats) {
        // Check if some of the notifications should be removed from the pop-up
        notificationPopUp.checkNotifications();
    }
    
    /**
     * Called by the status update timer every 3 seconds.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        updateStatus();
    }

    /**
     * Updates the status 
     */
    protected void updateStatus() {
        if (aisHandler != null) {
            aisStatus.updateStatus(aisHandler);
        }
        if (shoreServices != null) {
            shoreServiceStatus.updateStatus(shoreServices);
        }
        if (maritimeCloudService != null) {
            cloudStatus.updateStatus(maritimeCloudService);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
