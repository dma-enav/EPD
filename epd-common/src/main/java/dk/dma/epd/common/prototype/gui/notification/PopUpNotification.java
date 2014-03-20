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
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.CENTER;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.text.Formatter;

/**
 * Defines a pop-up notification panel which displays a message 
 * for a certain amount of time.
 * <p>
 * Position-wise, the widget is locked to the bottom-left corner of
 * it's parent layered pane.
 */
public class PopUpNotification extends JPanel implements ActionListener, SwingConstants {

    private static final long serialVersionUID = 1L;
    
    public static final int EMPTY_HEIGHT = 30;
    public static final int WIDTH = 300;
    
    private static final int OPEN_TIME_SECONDS = 20;
    private static final Color BG_COLOR = Color.DARK_GRAY;
    
    private JLayeredPane layeredPane;
    private JPanel notificationList = new JPanel();
    private NotificationBorder border;
    private Timer autoCloseTimer;
    private ResizeHandler resizeHandler = new ResizeHandler();
    private Point blLocation;
    private Dimension oldContainerSize;
    private boolean autoClose = true;
    
    /**
     * Constructor
     * <p>
     * Installs the widget in the layered pane.<p>
     * Position-wise, the widget is locked to the bottom-left corner of
     * it's parent layered pane.
     * 
     * @param layeredPane the layered pane
     * @param blLoation the bottom-left location
     */
    public PopUpNotification(JLayeredPane layeredPane, Point blLocation) {
        super(new BorderLayout());
    
        this.layeredPane = layeredPane;
        this.blLocation = blLocation;
        if (layeredPane.getSize().width > 0) {
            oldContainerSize = layeredPane.getSize();
        }
        
        SwingUtilities.getWindowAncestor(layeredPane)
            .addComponentListener(resizeHandler);

        setLocation(blLocation.x, blLocation.y - EMPTY_HEIGHT);
        setSize(WIDTH, EMPTY_HEIGHT);
        
        layeredPane.setLayer(this, JLayeredPane.POPUP_LAYER);
        layeredPane.add(this);
        
        setOpaque(false);
        setVisible(false);
        
        border = new NotificationBorder();
        border.setDrawDropShadow(true);
        setBorder(border);
        setBackground(BG_COLOR);        
        
        notificationList.setOpaque(false);
        notificationList.setLayout(new BoxLayout(notificationList, BoxLayout.Y_AXIS));
        notificationList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(notificationList, BorderLayout.CENTER);
    }

    /**
     * Adjusts the bottom-left location of the pop-up
     * @param blLocation the new bottom-left location
     */
    public void adjustBottomLeftLocation(Point blLocation) {
        if (!this.blLocation.equals(blLocation)) {
            this.blLocation = blLocation;
            setLocation(blLocation.x, blLocation.y - getHeight());
            repaint();
        }
    }
    
    /**
     * Adjusts the size of the pop-up panel to accommodate
     * between 1 and three notification panels.
     */
    private void adjustBounds() {
        int count = notificationList.getComponentCount();
        int displayCount = Math.max(1, Math.min(3, count));
        int displayHeight = displayCount * NotificationPopUpPanel.HEIGHT; 
        int newHeight = displayHeight + EMPTY_HEIGHT; 
        
        if (newHeight == getHeight()) {
            return;
        }
        setSize(getWidth(), newHeight);
        setLocation(blLocation.x, blLocation.y - newHeight);
        validate();
        repaint();
    }
    
    /**
     * Adds a notification to the panel and set it visible
     * 
     * @param panel the notification panel
     * @param notification the notification
     */
    @SuppressWarnings("all")
    public synchronized void addNotification(NotificationPanel<?> panel, Notification<?, ?> notification) {
        
        // With alerts, do not auto-close
        if (notification.getSeverity() == NotificationSeverity.ALERT) {
            autoClose = false;
        }
        
        NotificationPopUpPanel notificationPanel = new NotificationPopUpPanel(panel, notification);
        notificationPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BG_COLOR));
        
        // Add the notification at the top
        notificationList.add(notificationPanel, 0);
        
        // Remove all non-alert notifications of the same type with the same id
        List<Component> toBeDeleted = new ArrayList<>();
        for (int x = 1; x < notificationList.getComponents().length; x++) {
            NotificationPopUpPanel<?> np = (NotificationPopUpPanel<?>)notificationList.getComponents()[x];
            if (np.getNotification().getType().equals(notification.getType()) &&
                    np.getNotification().getId().equals(notification.getId()) &&
                    np.getNotification().getSeverity() != NotificationSeverity.ALERT) {
                toBeDeleted.add(np);
            }
        }
        for (Component component : toBeDeleted) {
            notificationList.remove(component);
        }
        
        notificationList.validate();
        
        adjustBounds();
        showPopUp();
    }
    
    /**
     * Checks if any of the notification panels should be removed.
     * <p>
     * This is the case if, say, the notifications have been 
     * acknowledged or deleted from the notification center.
     * <p>
     * This method should thus be called whenever the changes have 
     * happened in the notification center.
     */
    public synchronized void checkNotifications() {
        int alertNo = 0;
        autoClose = true;
        
        List<Component> toBeDeleted = new ArrayList<>();
        for (Component component : notificationList.getComponents()) {
            NotificationPopUpPanel<?> panel = (NotificationPopUpPanel<?>)component;
            if (panel.shouldBeRemoved()) {
                toBeDeleted.add(component);
            } else if (panel.getNotification().getSeverity() == NotificationSeverity.ALERT) {
                alertNo++;
            }
        }
       
        if (toBeDeleted.size() > 0) {
            for (Component component : toBeDeleted) {
                notificationList.remove(component);
            }
            notificationList.validate();
        }
        
        if (notificationList.getComponentCount() == 0) {
            closePopUp();
        } else if (alertNo > 0) {
            autoClose = false;
            showPopUp();
        
        } else if (isVisible() && autoCloseTimer == null) {
            // Special case: The pop-up has been open because of 
            // an alert, but now the last alert has disappeared.
            // Start the auto-close count down
            startClosingAfter(OPEN_TIME_SECONDS);
        }
        
        adjustBounds();
    }
    
    /**
     * Closes the pop-up
     */
    private synchronized void closePopUp() {
        setVisible(false);
        if (autoCloseTimer != null) {
            autoCloseTimer.stop();
            autoCloseTimer = null;
        }
        border.setAlpha(1.0f);
    }
    
    /**
     * Shows the pop-up. If it has started to fade out,
     * make it fully visible again.
     */
    private synchronized void showPopUp() {
        setVisible(true);
        if (autoClose) {
            startClosingAfter(OPEN_TIME_SECONDS);
            
        } else if (autoCloseTimer != null) {
            autoCloseTimer.stop();
            autoCloseTimer = null;
            border.setAlpha(1.0f);
            repaint();
        }
    }
    
    /**
     * Called by the timer when the notification is closed
     * @param e the action event
     */
    @Override 
    public void actionPerformed(ActionEvent e) {
        float alpha = border.getAlpha();
        if (alpha < 0.1) {
            closePopUp();
        } else {
            border.setAlpha(alpha - 0.05f);
            repaint();
        }
    }
    
    /**
     * Starts closing the notification widget
     * 
     * @param initialDelaySeconds the initial delay before it starts closing
     */
    private synchronized void startClosingAfter(int initialDelaySeconds) {
        if (autoCloseTimer != null) {
            border.setAlpha(1.0f);
            repaint();
            autoCloseTimer.restart();
            return;
        }
        autoCloseTimer = new Timer(100, PopUpNotification.this);
        autoCloseTimer.setInitialDelay(initialDelaySeconds * 1000);
        autoCloseTimer.setRepeats(true);
        autoCloseTimer.start();
    }
    
    /**
     * Test method
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBounds(100, 100, 600, 600);
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        Point location = new Point(100, 200);
        
        PopUpNotification notif = new PopUpNotification(frame.getLayeredPane(), location);
        GeneralNotification n = new GeneralNotification();
        n.setTitle("Peder was here");
        n.setDescription("This is a\nmultiline test\nwith a lot of text that should not cause the field to expand\nDone.");
        n.setSeverity(NotificationSeverity.ALERT);
        n.setLocation(Position.create(53.0, 12.2));
        notif.addNotification(null, n);
        notif.addNotification(null, n);
        notif.addNotification(null, n);
        frame.setVisible(true);
    }

    /**
     * Handles re-positioning of the widget, when the 
     * window is resized.
     * <p>
     * The widget is re-positioned according the the {@code lockPosition}.
     */
    class ResizeHandler extends ComponentAdapter {
        @Override
        public void componentShown(ComponentEvent e) {
            oldContainerSize = layeredPane.getSize();
        }

        @Override 
        public void componentResized(ComponentEvent e) {
            if (oldContainerSize == null || 
               (oldContainerSize.width == 0 && oldContainerSize.height == 0)) {
                return;
            }
            
            int dh = layeredPane.getHeight() - oldContainerSize.height;
            oldContainerSize = layeredPane.getSize();
            Point p = PopUpNotification.this.blLocation;            
            adjustBottomLeftLocation(new Point(p.x, p.y + dh));
        }        
    }    
}



/**
 * The notification pop-up panel displays the title and 
 * description for a notification.
 * <p>
 * It also sports buttons for showing the notification
 * in the notification center and for dismissing,
 * i.e. acknowledging, the notification
 */
class NotificationPopUpPanel<N extends Notification<?, ?>> extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    public static final int HEIGHT = 66;
    
    private NotificationPanel<N> panel;
    private N notification;
    private JButton showBtn, dismissBtn, gotoBtn;
    
    /**
     * Constructor
     * 
     * @param notification the associated notification
     */
    public NotificationPopUpPanel(NotificationPanel<N> panel,  N notification) {
        super(new GridBagLayout());
        this.panel = panel;
        this.notification = notification;

        // Lock the height
        setMinimumSize(new Dimension(getMinimumSize().width, HEIGHT));
        setMaximumSize(new Dimension(getMaximumSize().width, HEIGHT));
        setSize(new Dimension(getSize().width, HEIGHT));

        Insets insets1  = new Insets(3, 3, 0, 3);
        
        JLabel icon = new JLabel(getNotificationIcon());
        add(icon, new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0, CENTER, NONE, insets1, 0, 0));
        
        JLabel titleLbl = new JLabel(notification.getTitle());
        titleLbl.setFont(titleLbl.getFont().deriveFont(11f).deriveFont(Font.BOLD));
        add(titleLbl, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets1, 0, 0));

        String descHtlm = String.format("<html>%s</html>", Formatter.formatHtml(notification.getDescription()));
        JLabel descLbl = new JLabel(descHtlm);
        descLbl.setFont(descLbl.getFont().deriveFont(9f));
        descLbl.setVerticalAlignment(JLabel.TOP);
        add(descLbl, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, insets1, 0, 0));
        
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        add(btnPanel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));

        showBtn = createButton("Show");
        btnPanel.add(showBtn, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));
        
        if (notification.canAcknowledge()) {
            dismissBtn = createButton("Dismiss");
            btnPanel.add(dismissBtn, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));
        }
        
        if (notification.getLocation() != null) {
            gotoBtn = createButton("Goto");
            btnPanel.add(gotoBtn, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));
        }
    }

    /**
     * Translates {@linkplain NotificationSeverity} into a standard swing icon
     * @return the swing icon that corresponds to the notification severity
     */
    private Icon getNotificationIcon() {
        switch (notification.getSeverity()) {
        case ALERT:     return UIManager.getIcon("OptionPane.errorIcon");
        case WARNING:   return UIManager.getIcon("OptionPane.warningIcon");
        default:        return UIManager.getIcon("OptionPane.informationIcon");
        }
    }
    
    /**
     * Creates a diminutive button used in this panel
     * @param title the title of the button
     * @return the instantiated button
     */
    private JButton createButton(String title) {
        JButton button = new JButton(title);
        button.setFont(button.getFont().deriveFont(9.0f));
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 16));
        return button;
    }

    /**
     * Called when one of the buttons is clicked
     * @param ae the action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == showBtn) {
            EPD.getInstance().getNotificationCenter()
                .openNotification(notification.getType(), notification.getId(), false);
            EPD.getInstance().getNotificationCenter().setVisible(true);
            
        } else if (ae.getSource() == dismissBtn) {
            panel.acknowledgeNotification(notification);
            
        } else if (ae.getSource() == gotoBtn) {
            if (notification.getLocation() != null) {
                EPD.getInstance().getMainFrame().zoomToPosition(notification.getLocation());
            }
        }
    }
    
    /**
     * Checks if the notification has been deleted, read or acknowledged,
     * in which case, this panel should be removed from the pop-up.
     * <p>
     * Rules:
     * <ul>
     *   <li>If deleted, the notification should always be removed</li>
     *   <li>For non-alerts, remove the notification if it is read or acknowledged.</li>
     *   <li>For alerts, remove the notification if it is acknowledged.</li>
     * </ul>
     */
    public boolean shouldBeRemoved() {
        // The notification we are holding in this panel may not be the current
        // version of the notification panel.
        // Check the state of a fresh version of the notification
        Notification<?, ?> not = panel.getNotificationById(notification.getId());
        if (not == null) {
            return true;
        } else if (not.getSeverity() == NotificationSeverity.ALERT) {
            return not.isAcknowledged();
        }
        // Non-alerts
        return not.isAcknowledged() || not.isRead();
    }

    /**
     * Returns the notification associated with this panel
     * @return the notification associated with this panel
     */
    public N getNotification() {
        return notification;
    }
}



/**
 * This border is used by the {@linkplain PopUpNotification} widget.
 * <p>
 * It will paint a balloon-help style shape around the widget
 * and controls the transparency of the widget using the {@code alpha}
 * field.
 */
class NotificationBorder extends AbstractBorder {
    private static final long serialVersionUID = 1L;

    private int cornerRadius = 14;
    private int pointerSize = 14;
    private int pointerX = 50;
    private int pad = 2;
    private Insets insets = new Insets(4, 4, pointerSize + 4, 4);
    private float alpha = 1.0f;
    private boolean drawDropShadow;

    /**
     * Constructor
     * @param color
     */
    public NotificationBorder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return getBorderInsets(c);
    }
    
    /**
     * Returns the current transparency alpha of the widget
     * @return the current transparency alpha of the widget
     */
    public float getAlpha() {
        return alpha;
    }
    
    /**
     * Sets the current transparency alpha of the widget
     * @param alpha the current transparency alpha of the widget
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Returns whether to paint a drop shadow or not
     * @return whether to paint a drop shadow or not
     */
    public boolean isDrawDropShadow() {
        return drawDropShadow;
    }

    /**
     * Sets whether to paint a drop shadow or not
     * @param drawDropShadow  whether to paint a drop shadow or not
     */
    public void setDrawDropShadow(boolean drawDropShadow) {
        this.drawDropShadow = drawDropShadow;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHints(GraphicsUtil.ANTIALIAS_HINT);
        
        // Adjust the transparency alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int bottomLineY = height - 2 * pad - pointerSize;

        // Define the content rectangle
        RoundRectangle2D.Double content = new RoundRectangle2D.Double(
                pad, pad, width - 2 * pad, bottomLineY, cornerRadius, cornerRadius);

        // Define the bottom pointer triangle
        Polygon pointer = new Polygon();
        pointer.addPoint(pointerX, bottomLineY);
        pointer.addPoint(pointerX + pointerSize, bottomLineY);
        pointer.addPoint(pointerX + (pointerSize / 2), height - pad);

        // Combine content rectangle and pointer into one area
        Area area = new Area(content);
        area.add(new Area(pointer));
        
        // Simulate a drop shadow
        if (drawDropShadow) {
            Stroke saveStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(50, 50, 50, 50));
            g2.translate(1, 0);
            g2.draw(area);
            g2.translate(-1, 0);
            g2.setStroke(saveStroke);
        }
        
        // Fill the pop-up background
        g2.setColor(c.getBackground());
        g2.fill(area);
    }
}
