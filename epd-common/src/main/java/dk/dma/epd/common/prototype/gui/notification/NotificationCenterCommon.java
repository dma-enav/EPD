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

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.maritimecloud.core.id.MaritimeId;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;

/**
 * Defines the base class for the notification center. Can either
 * be used directly or extended.
 * <p>
 * By default, is exposes two notification panels:
 * <ul>
 *   <li>Notifications: a general notification panel.</li>
 *   <li>MSI: A maritime safety information panel.</li>
 * </ul>
 */
public class NotificationCenterCommon extends ComponentDialog implements 
    IMsiUpdateListener,
    IChatServiceListener
{
    private static final long serialVersionUID = 1L;

    protected MsiHandler msiHandler;
    protected ChatServiceHandlerCommon chatServiceHandler;
    
    protected JPanel contentPanel = new JPanel(new BorderLayout());
    protected GeneralNotificationPanel generalPanel = new GeneralNotificationPanel();
    protected MsiNotificationPanel msiPanel = new MsiNotificationPanel();
    protected List<NotificationPanel<?>> panels = new CopyOnWriteArrayList<>();
    protected Map<NotificationType, NotificationLabel> labels = new ConcurrentHashMap<>();
    
    protected NotificationType activeType;
    
    /**
     * Constructor
     * 
     * @param window the parent window
     */
    public NotificationCenterCommon(Window window) {
        super(window, "Notification Center", Dialog.ModalityType.MODELESS);
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        
        registerPanels();
        initGUI();
        setActiveType(NotificationType.NOTIFICATION);
    }
    
    /**
     * Register the panels.
     * Sub-classes can override to control the order of the panels
     */
    protected void registerPanels() {
        panels.add(generalPanel);
        panels.add(msiPanel);        
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiHandler && msiHandler == null) {
            msiHandler = (MsiHandler) obj;
            msiHandler.addListener(this);
            msiPanel.refreshNotifications();
        
        } else if (obj instanceof ChatServiceHandlerCommon && chatServiceHandler == null) {
            chatServiceHandler = (ChatServiceHandlerCommon)obj;
            chatServiceHandler.addListener(this);
        }
    }
    
    /*************************************/
    /** GUI methods                     **/
    /*************************************/
    
    /**
     * Set up the graphical user interface
     */
    protected void initGUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        JPanel typePanel = new JPanel(new GridBagLayout());
        typePanel.setBackground(UIManager.getColor("List.background"));
        Insets insets  = new Insets(5, 5, 0, 5);
        int gridY = 0;
        for (NotificationPanel<?> panel : panels) {
            NotificationLabel label = new NotificationLabel(panel) {
                private static final long serialVersionUID = 1L;
                @Override public void labelClicked(NotificationType type) {
                    setActiveType(type);
                }
            };
            labels.put(panel.getNotitficationType(), label);
            label.setDrawSelectionPointer(true);
            typePanel.add(label, new GridBagConstraints(0, gridY++, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets, 0, 0));
        }
        // Filler
        typePanel.add(new JLabel(), new GridBagConstraints(0, gridY++, 1, 1, 0.0, 1.0, WEST, BOTH, insets, 0, 0));
        
        contentPanel.add(typePanel, BorderLayout.WEST);
    }
    
    /**
     * Sets the active notification type of the center
     * @param type the active notification type of the center
     */
    public void setActiveType(final NotificationType type) {
        // Ensure that we operate in the Swing event thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    setActiveType(type);
                }
            });
        }
        
        if (activeType == type) {
            return;
        }
        
        if (activeType != null) {
            contentPanel.remove(getPanel(activeType));
            labels.get(activeType).setSelected(false);
        }
        activeType = type;
        contentPanel.add(getPanel(activeType), BorderLayout.CENTER);
        labels.get(activeType).setSelected(true);
        pack();
        repaint();
    }
    
    /**
     * Returns the panel with the given notification type
     * @param type the notification type
     * @return the panel with the given notification type
     */
    public NotificationPanel<?> getPanel(NotificationType type) {
        for (NotificationPanel<?> panel : panels) {
            if (panel.getNotitficationType() == type) {
                return panel;
            }
        }
        return null;
    }
    
    /**
     * Returns the list of notification panels
     * @return the list of notification panels
     */
    public List<NotificationPanel<?>> getPanels() {
        return panels;
    }
    
    /**
     * Returns the labels that should be installed in the bottom panel
     * @return the labels that should be installed in the bottom panel
     */
    public JLabel getBottomPanelLabels() {
        return null;
    }
    
    /**
     * Change the visibility
     */
    public void toggleVisibility() {
        setVisible(!isVisible());
    }

    /*************************************/
    /** Listener methods                **/
    /*************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void msiUpdate() {
        msiPanel.refreshNotifications();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void chatMessageReceived(MaritimeId senderId, ChatServiceMessage message) {
        GeneralNotification notification = new GeneralNotification();
        notification.setId(message.getId());
        notification.setDate(new Date(message.getSendDate()));
        notification.setTitle("Message from " + message.getSenderName());
        notification.setDescription(message.getMessage());
        generalPanel.addNotification(notification);
    }
    
    /*************************************/
    /** Test method                     **/
    /*************************************/
    
    /**
     * Test method
     */
    public static void main(String... args) {
        NotificationCenterCommon n = new NotificationCenterCommon(null);
        GeneralNotification not1 = new GeneralNotification();
        not1.setTitle("hkjdfhg");
        not1.setNotificationSeverity(NotificationSeverity.ALERT);
        n.generalPanel.tableModel.notifications.add(not1);
        GeneralNotification not2 = new GeneralNotification();
        not2.setTitle("ddd");
        not2.setRead(true);
        n.generalPanel.tableModel.notifications.add(not2);
        n.setVisible(true);
        
    }
}
