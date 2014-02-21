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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

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
import dk.dma.epd.common.prototype.gui.notification.PopUpNotification;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.MaritimeCloudServiceCommon;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.prototype.status.IStatusComponent;

/**
 * Panel shown below the chart
 */
public class BottomPanelCommon extends OMComponentPanel implements MouseListener, ActionListener {

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
        }
        add(notificationPanel, BorderLayout.WEST);
        
        /*
        Rectangle bounds = new Rectangle(100, BottomPanelCommon.this.getLocation().y - 200, 200, 200);
        PopUpNotification notification = new PopUpNotification();
        notification.installInLayeredPane(
                GraphicsUtil.getTopLevelContainer(notificationPanel),
                SwingConstants.SOUTH_WEST,
                bounds);
        */
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
     * @param type the notification type
     * @param notification the notification
     * @param alert the alert
     */
    public void triggerAlert(NotificationType type, Notification<?, ?> notification, NotificationAlert alert) {
        Rectangle bounds = new Rectangle(100, BottomPanelCommon.this.getLocation().y - 200, 200, 200);
        PopUpNotification popup = new PopUpNotification();
        popup.installInLayeredPane(
                GraphicsUtil.getTopLevelContainer(notificationPanel),
                SwingConstants.SOUTH_WEST,
                bounds);
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
        if (e.getSource() instanceof StatusLabel) {
            BottomPanelStatusDialog statusDialog = new BottomPanelStatusDialog();
            statusDialog.showStatus(statusComponents);
            statusDialog.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
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
