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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;

import dk.dma.epd.common.graphics.GraphicsUtil;

/**
 * Defines a pop-up notification panel which displays a message 
 * for a certain amount of time.
 * <p>
 * The widget will be locked to the position specified by the
 * {@code lockPosition} field.
 */
public class PopUpNotification extends JPanel implements ActionListener, SwingConstants {

    private static final long serialVersionUID = 1L;
    
    private JLayeredPane layeredPane;
    private NotificationBorder border;
    private Timer timer;
    private ResizeHandler resizeHandler = new ResizeHandler();
    private Point location;
    private Dimension oldContainerSize;
    private int lockPosition;
    
    
    /**
     * Constructor
     */
    public PopUpNotification() {
        super();
    
        setOpaque(false);
        border = new NotificationBorder();
        border.setDrawDropShadow(true);
        setBorder(border);
        setBackground(Color.red);
        
        JButton btn = new JButton("XXXXXXXXXXXXXXXXX");
        add(btn);
        btn.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                startClosing();
            }
        });
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
            layeredPane.remove(this);
            layeredPane.repaint();
        } else {
            border.setAlpha(alpha - 0.05f);
            repaint();
        }
    }
    
    /**
     * Starts closing the notification widget
     */
    private synchronized void startClosing() {
        if (timer != null) {
            return;
        }
        SwingUtilities.getWindowAncestor(layeredPane)
            .removeComponentListener(resizeHandler);
        GraphicsUtil.setEnabled(this, false);
        timer = new Timer(100, PopUpNotification.this);
        timer.setRepeats(true);
        timer.start();
    }

    /**
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
    public void installInLayeredPane(JLayeredPane layeredPane, int lockPosition, Rectangle bounds) {
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
        PopUpNotification notif = new PopUpNotification();
        Rectangle bounds = new Rectangle(100, 100, 200, 200);
        notif.installInLayeredPane(frame.getLayeredPane(), SwingConstants.SOUTH_WEST, bounds);
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
