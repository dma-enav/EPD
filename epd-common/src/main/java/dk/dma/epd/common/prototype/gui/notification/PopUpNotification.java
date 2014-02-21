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
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

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

import org.jdesktop.swingx.JXLabel;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;

/**
 * Defines a pop-up notification panel which displays a message 
 * for a certain amount of time.
 * <p>
 * The widget will be locked to the position specified by the
 * {@code lockPosition} field.
 */
public class PopUpNotification extends JPanel implements ActionListener, SwingConstants {

    private static final long serialVersionUID = 1L;
    
    private static final int OPEN_TIME_SECONDS = 10;
    private static final Color BG_COLOR = Color.DARK_GRAY;
    
    private JLayeredPane layeredPane;
    private JPanel notificationList = new JPanel();
    private NotificationBorder border;
    private Timer timer;
    private ResizeHandler resizeHandler = new ResizeHandler();
    private Point location;
    private Dimension oldContainerSize;
    private int lockPosition;
    
    
    /**
     * Constructor
     * <p>
     * Installs the widget in the layered pane.<p>
     * Position-wise, the widget is locked to one of the following 
     * {@linkplain SwingConstants} positions:
     * <ul>
     *   <li>{@code NORTH, EAST, SOUTH, WEST}: The given side of the container.
     *   <li>{@code NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST}: The given corner of the container.
     *   <li>{@code CENTER}: The center of the container.
     * </ul>
     * 
     * @param layeredPane the layered pane
     * @param lockPosition the position to lock the widget to
     * @param bounds the bounds of the widget
     */
    public PopUpNotification(JLayeredPane layeredPane, int lockPosition, Rectangle bounds) {
        super(new BorderLayout());
    
        this.layeredPane = layeredPane;
        this.location = bounds.getLocation();
        this.lockPosition = lockPosition;
        if (layeredPane.getSize().width > 0) {
            oldContainerSize = layeredPane.getSize();
        }
        
        SwingUtilities.getWindowAncestor(layeredPane)
            .addComponentListener(resizeHandler);

        setLocation(location);
        setBounds(bounds);
        
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
     * Adds a notification to the panel and set it visible
     */
    public synchronized void addNotification(Notification<?, ?> notification) {
        NotificationPopUpPanel notificationPanel = new NotificationPopUpPanel(notification);
        notificationPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BG_COLOR));
        notificationList.add(notificationPanel);
        setVisible(true);
        startClosingAfter(OPEN_TIME_SECONDS);
    }
    
    /**
     * Called by the timer when the notification is closed
     * @param e the action event
     */
    @Override 
    public void actionPerformed(ActionEvent e) {
        float alpha = border.getAlpha();
        if (alpha < 0.1) {
            timer.stop();
            timer = null;
            border.setAlpha(1.0f);
            setVisible(false);
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
        if (timer != null) {
            border.setAlpha(1.0f);
            repaint();
            timer.restart();
            return;
        }
        timer = new Timer(100, PopUpNotification.this);
        timer.setInitialDelay(initialDelaySeconds * 1000);
        timer.setRepeats(true);
        timer.start();
    }

    /**
     * Checks if the lock-position is one of the given values
     * @param values the values to check
     * @return if the lock-position is one of the given values
     */
    private boolean lockPositionIn(int... values) {
        for (int val : values) {
            if (val == lockPosition) {
                return true;
            }
        }
        return false;
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
        Rectangle bounds = new Rectangle(100, 100, 300, 200);
        
        PopUpNotification notif = new PopUpNotification(frame.getLayeredPane(), SwingConstants.SOUTH_WEST, bounds);
        GeneralNotification n = new GeneralNotification();
        n.setTitle("Peder was here");
        n.setDescription("This is a\nmultiline test");
        n.setSeverity(NotificationSeverity.ALERT);
        n.setLocation(Position.create(53.0, 12.2));
        notif.addNotification(n);
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
            if (oldContainerSize == null) {
                return;
            }
            
            Dimension newContainerSize = layeredPane.getSize();
            
            int dx = 0;
            int dw = newContainerSize.width - oldContainerSize.width;
            if (lockPositionIn(NORTH, CENTER, SOUTH)) {
                dx = dw / 2;
            } else if (lockPositionIn(NORTH_EAST, EAST, SOUTH_EAST)) {
                dx = dw;
            }
            
            int dy = 0;
            int dh = newContainerSize.height - oldContainerSize.height;
            if (lockPositionIn(EAST, CENTER, WEST)) {
                dy = dh / 2;
            } else if (lockPositionIn(SOUTH_EAST, SOUTH, SOUTH_WEST)) {
                dy = dh;
            }
            
            setLocation(location.x + dx, location.y + dy);
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
class NotificationPopUpPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int HEIGHT = 66;
    
    private Notification<?, ?> notification;
    private JButton showBtn, dismissBtn, gotoBtn;
    
    /**
     * Constructor
     * 
     * @param notification the associated notification
     */
    public NotificationPopUpPanel(Notification<?, ?> notification) {
        super(new GridBagLayout());
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

        JXLabel descLbl = new JXLabel(notification.getDescription());
        descLbl.setLineWrap(true);
        descLbl.setFont(descLbl.getFont().deriveFont(9f));
        descLbl.setVerticalAlignment(JLabel.TOP);
        add(descLbl, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, insets1, 0, 0));
        
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        add(btnPanel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));

        showBtn = createButton("Show");
        btnPanel.add(showBtn, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));
        
        dismissBtn = createButton("Dismiss");
        btnPanel.add(dismissBtn, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, insets1, 0, 0));
        
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
                .selectNotification(notification.getType(), notification.getId());
            EPD.getInstance().getNotificationCenter().setVisible(true);
            
        } else if (ae.getSource() == dismissBtn) {
            
        } else if (ae.getSource() == gotoBtn) {
            
        }
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
