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
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.gui.SystemTrayCommon;
import dk.dma.epd.common.prototype.gui.views.BottomPanelCommon;
import dk.dma.epd.common.prototype.notification.ChatNotification;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon;
import dk.dma.epd.common.prototype.service.RouteSuggestionHandlerCommon.RouteSuggestionListener;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon.StrategicRouteListener;
import dma.msinm.MCMsiNmService;
import net.maritimecloud.core.id.MaritimeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TrayIcon.MessageType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon.IMsiNmServiceListener;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

/**
 * Defines the base class for the notification center. Can either be used directly or extended.
 * <p>
 * By default, is exposes two notification panels:
 * <ul>
 * <li>Notifications: a general notification panel.</li>
 * <li>MSI: A maritime safety information panel.</li>
 * </ul>
 */
public abstract class NotificationCenterCommon extends ComponentDialog implements ActionListener, IMsiNmServiceListener,
        IChatServiceListener, StrategicRouteListener, RouteSuggestionListener {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationCenterCommon.class);

    protected Timer alertTimer = new Timer(3 * 1000, this); // Every 3 seconds

    protected MsiNmServiceHandlerCommon msiNmHandler;
    protected ChatServiceHandlerCommon chatServiceHandler;
    protected StrategicRouteHandlerCommon strategicRouteHandler;
    protected RouteSuggestionHandlerCommon routeSuggestionHandler;

    protected SystemTrayCommon systemTray;
    protected BottomPanelCommon bottomPanel;

    protected JPanel contentPanel = new JPanel(new BorderLayout());
    protected JPanel typePanel = new JPanel(new GridBagLayout());

    protected GeneralNotificationPanel generalPanel = new GeneralNotificationPanel(this);
    protected MsiNmNotificationPanel msiNmPanel = new MsiNmNotificationPanel(this);
    protected ChatNotificationPanel chatPanel = new ChatNotificationPanel(this);

    protected List<NotificationPanel<?>> panels = new CopyOnWriteArrayList<>();
    protected Map<NotificationType, NotificationLabel> labels = new ConcurrentHashMap<>();

    protected NotificationType activeType;

    // Maximized/minimized handling
    protected boolean maximized = true;
    protected int saveDeltaWidth;

    private VoiceAlertTimers voiceAlertTimers = new VoiceAlertTimers();

    /**
     * Constructor
     * 
     * @param window
     *            the parent window
     */
    public NotificationCenterCommon(Window window) {
        super(window, "Notification Center", Dialog.ModalityType.MODELESS);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setBounds(100, 100, 900, 500);
        if (window != null) {
            setLocationRelativeTo(window);
        }

        registerPanels();
        initGUI();
        setActiveType(NotificationType.NOTIFICATION);

        strategicRouteHandler = EPD.getInstance().getStrategicRouteHandler();
        strategicRouteHandler.addStrategicRouteListener(this);

        routeSuggestionHandler = EPD.getInstance().getRouteSuggestionHandler();
        routeSuggestionHandler.addRouteSuggestionListener(this);

        alertTimer.setCoalesce(true);
        alertTimer.setRepeats(true);
        alertTimer.start();
    }

    /**
     * Register the panels. Sub-classes can override to control the order of the panels
     */
    protected void registerPanels() {
        panels.add(generalPanel);
        panels.add(chatPanel);
        panels.add(msiNmPanel);
    }

    /**
     * Called periodically by the alert timer
     * 
     * @param ae
     *            the action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        // Run through all alerts of all notifications of all panels
        // and check if they should be triggered
        for (NotificationPanel<?> panel : panels) {
            for (Notification<?, ?> notification : panel.getNotifications()) {
                for (NotificationAlert alert : notification.getAlerts()) {
                    if (alert.shouldTrigger(notification)) {
                        triggerAlert(panel, notification, alert);
                    }
                }
            }
        }
    }

    /**
     * Trigger the given alert for the given notification
     * 
     * @param panel
     *            the notification panel
     * @param notification
     *            the notification
     * @param alert
     *            the alert
     */
    protected void triggerAlert(NotificationPanel<?> panel, Notification<?, ?> notification, NotificationAlert alert) {
        try {
            LOG.info("Triggering alert " + alert + " for notification " + notification.getId());

            // Handle opening the notification
            if (alert.hasAlertType(AlertType.OPEN)) {
                openNotification(notification.getType(), notification.getId(), false);
            }

            // Handle beep alerts
            if (alert.hasAlertType(AlertType.BEEP)) {

                // java.util.Timer warningTimer;
                // warningTimer = new java.util.Timer();
                // warningTimer.scheduleAtFixedRate(new ContinousVoiceAlerts(notification, warningTimer), 0, // initial delay
                // 10 * 1000); // subsequent rate
            }

            if (EPD.getInstance().getSettings().getGuiSettings().isUseAudio()) {

                VoiceAlertTimer voiceAlertTimer = new VoiceAlertTimer(notification, voiceAlertTimers);
                voiceAlertTimers.addVoiceAlert(voiceAlertTimer);

                // java.util.Timer warningTimer;
                // warningTimer = new java.util.Timer();
                // warningTimer.scheduleAtFixedRate(new ContinousVoiceAlerts(notification, warningTimer), 0, // initial delay
                // 10 * 1000); // subsequent rate

            }

            // Handle system tray alerts
            if (systemTray != null && alert.hasAlertType(AlertType.SYSTEM_TRAY)) {
                MessageType msgType;
                switch (notification.getSeverity()) {
                case ALERT:
                    msgType = MessageType.ERROR;
                    break;
                case WARNING:
                    msgType = MessageType.WARNING;
                    break;
                case MESSAGE:
                    msgType = MessageType.INFO;
                    break;
                default:
                    msgType = MessageType.NONE;
                }
                systemTray.displayMessage(notification.getTitle(), notification.getDescription(), msgType);
            }

            // Handle pop-up alerts
            if (bottomPanel != null && alert.hasAlertType(AlertType.POPUP)) {
                bottomPanel.triggerAlert(panel, notification, alert);

            }

        } catch (Exception ex) {
            LOG.error("Failed triggering alert for notification " + notification.getId(), ex);
        }

        // Flag the the alert has been triggered
        alert.flagTriggered();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiNmServiceHandlerCommon && msiNmHandler == null) {
            msiNmHandler = (MsiNmServiceHandlerCommon)obj;
            msiNmHandler.addListener(this);
            msiNmPanel.refreshMsiNmServices();
            msiNmPanel.refreshNotifications();

        } else if (obj instanceof ChatServiceHandlerCommon && chatServiceHandler == null) {
            chatServiceHandler = (ChatServiceHandlerCommon) obj;
            chatServiceHandler.addListener(this);
            chatPanel.refreshNotifications();

        } else if (obj instanceof SystemTrayCommon && systemTray == null) {
            systemTray = (SystemTrayCommon) obj;

        } else if (obj instanceof BottomPanelCommon && bottomPanel == null) {
            bottomPanel = (BottomPanelCommon) obj;
        }
    }

    /*************************************/
    /** GUI methods **/
    /*************************************/

    /**
     * Set up the graphical user interface
     */
    protected void initGUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        typePanel.setBackground(UIManager.getColor("List.background"));
        Insets insets = new Insets(5, 5, 0, 5);
        int gridY = 0;
        for (NotificationPanel<?> panel : panels) {
            NotificationLabel label = new NotificationLabel(panel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void labelClicked(NotificationType type) {
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
     * 
     * @param type
     *            the active notification type of the center
     */
    public void setActiveType(final NotificationType type) {
        // Ensure that we operate in the Swing event thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setActiveType(type);
                }
            });
            return;
        }

        if (activeType == type) {
            return;
        }

        // Remove the old active panel
        if (activeType != null) {
            contentPanel.remove(getPanel(activeType));
            labels.get(activeType).setSelected(false);
        }

        activeType = type;
        contentPanel.add(getPanel(activeType), BorderLayout.CENTER);
        labels.get(activeType).setSelected(true);

        if (!maximized) {
            setContentPane(getPanel(activeType).getDetailPanel());
        }

        revalidate();
        repaint();
    }

    /**
     * Opens the notification center with the given notification type and the given maximized state
     * 
     * @param type
     *            the active notification type of the center
     * @param maximized
     *            the maximized state of the notification center
     */
    public void openNotificationCenter(final NotificationType type, final boolean maximized) {
        // Ensure that we operate in the Swing event thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openNotificationCenter(type, maximized);
                }
            });
            return;
        }

        setActiveType(type);
        // NB: We call setVisible() before setMaximized().
        // Before the window has been visible the first time around
        // you cannot rely on sizes used by the maximize mechanism.
        setVisible(true);
        setMaximized(maximized);
    }

    /**
     * Returns the panel with the given notification type
     * 
     * @param type
     *            the notification type
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
     * 
     * @return the list of notification panels
     */
    public List<NotificationPanel<?>> getPanels() {
        return panels;
    }

    /**
     * Returns the labels that should be installed in the bottom panel
     * 
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

    /**
     * Selects the notification with the given id
     * 
     * @param notificationType
     *            the notification type
     * @param id
     *            the id of the notification
     */
    public void selectNotification(NotificationType notificationType, Object id) {
        setActiveType(notificationType);
        getPanel(notificationType).setSelectedId(id);
    }

    /**
     * If the notification with the given type and id is the currently selected one, then refresh the selection.
     * 
     * @param notificationType
     *            the notification type
     * @param id
     *            the id of the notification
     */
    public void checkRefreshSelection(final NotificationType notificationType, final Object id) {
        if (notificationType == activeType) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getPanel(notificationType).checkRefreshSelection(id);
                }
            });
        }
    }

    /**
     * Selects the notification with the given id and makes the notification center visible. If the notification center was
     * previously visible, then the maximized state is preserved. Otherwise, the notification center is opened in the given default
     * maximized state.
     * 
     * @param notificationType
     *            the notification type
     * @param id
     *            the id of the notification
     * @param defaultMaximized
     *            the maximized state to apply if the notification center was not visible
     */
    public void openNotification(NotificationType notificationType, Object id, boolean defaultMaximized) {
        boolean maximized = (isVisible()) ? this.maximized : defaultMaximized;
        openNotificationCenter(notificationType, maximized);
        getPanel(notificationType).setSelectedId(id);
    }

    /**
     * Adds a notification of the given type.
     * <p>
     * Sub-classes should add their own notification types
     * 
     * @param notification
     *            the notification to add
     */
    public void addNotification(Notification<?, ?> notification) {
        if (notification instanceof GeneralNotification) {
            generalPanel.addNotification((GeneralNotification) notification);
        } else if (notification instanceof MsiNmNotification) {
            msiNmPanel.addNotification((MsiNmNotification) notification);
        } else if (notification instanceof ChatNotification) {
            chatPanel.addNotification((ChatNotification) notification);
        } else {
            throw new IllegalArgumentException("Unknown notification type: " + notification);
        }
    }

    /**
     * Returns the currently active notification type
     * 
     * @return the currently active notification type
     */
    public NotificationType getActiveType() {
        return activeType;
    }

    /*************************************/
    /** Maximize/minimize methods **/
    /*************************************/

    /**
     * Returns if the notification center is maximized
     * 
     * @return if the notification center is maximized
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Sets the maximized state of the notification center
     * 
     * @param maximized
     *            the maximized state of the notification center
     */
    public void setMaximized(boolean maximized) {
        if (maximized == this.maximized) {
            return;
        }

        // Let panels adapt to the maximized flag
        for (NotificationPanel<?> panel : panels) {
            panel.setMaximized(maximized);
        }

        Rectangle bounds = getBounds();
        if (maximized) {
            setBounds(bounds.x - saveDeltaWidth, bounds.y, bounds.width + saveDeltaWidth, bounds.height);
            setContentPane(contentPanel);
        } else {
            saveDeltaWidth = contentPanel.getWidth() - getPanel(activeType).getDetailPanel().getWidth();
            setContentPane(getPanel(activeType).getDetailPanel());
            setBounds(bounds.x + saveDeltaWidth, bounds.y, bounds.width - saveDeltaWidth, bounds.height);
        }
        this.maximized = maximized;
    }

    /**
     * Toggles the maximized state of the notification center
     */
    public void toggleMaximized() {
        setMaximized(!maximized);
    }

    /*************************************/
    /** Listener methods **/
    /*************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void chatMessagesUpdated(MaritimeId targetId) {

        // Update the chat panel
        chatPanel.refreshNotifications();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void msiNmServicesChanged(List<MCMsiNmService> msiNmServiceList) {
        msiNmPanel.refreshMsiNmServices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void msiNmMessagesChanged(List<MsiNmNotification> msiNmMessages) {
        msiNmPanel.refreshNotifications();
    }
}
