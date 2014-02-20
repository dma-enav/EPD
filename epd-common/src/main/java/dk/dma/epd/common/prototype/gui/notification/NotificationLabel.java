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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import dk.dma.epd.common.prototype.gui.notification.NotificationPanel.NotificationPanelListener;
import dk.dma.epd.common.prototype.gui.notification.NotificationPanel.NotificationStatistics;
import dk.dma.epd.common.prototype.notification.NotificationType;

/**
 * Represents a notification label that displays 
 * the name of a notification type along with
 * and indicator for the current statistics.
 */
public class NotificationLabel extends JPanel implements NotificationPanelListener, ActionListener {

    private static final long serialVersionUID = 1L;

    private static final int LABEL_HEIGHT = 24;
    private static final int LABEL_WIDTH = 125;
    private static final int POINTER_WIDTH = 6;
    private static final int BLINK_WIDTH = 6;
    private static final int BLINK_COUNT = 10;
    
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 50);
    private static final Color CLICK_COLOR = new Color(0, 0, 0, 100);
    private static final Color TITLE_COLOR = new Color(240, 240, 240);
    private static final Color COUNTER_COLOR = new Color(220, 220, 220);
    private static final Color ALERT_COLOR = new Color(255, 100, 100, 150);
    private static final Color WARN_COLOR = new Color(255, 255, 120, 150);
    private static final Color BLINK0_COLOR = new Color(206, 120, 120);
    private static final Color BLINK1_COLOR = new Color(165, 80, 80);
    
    private static final Font COUNTER_FONT = new Font("Arial", Font.PLAIN, 9);
    private static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 11);
    
    private Timer blinkTimer = new Timer(500, this);
    private int blinks;
    
    private NotificationStatistics stats;
    private int lastUnacknowledgedCount;
    private boolean drawSelectionPointer;
    private boolean selected;
    private boolean mousePressed;
    
    /**
     * Constructor
     * @param panel
     */
    public NotificationLabel(final NotificationPanel<?> panel) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        blinkTimer.setRepeats(true);
        
        panel.addListener(this);
        
        setOpaque(false);
        setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        setSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));        
        
        JLabel nameLabel = new JLabel(panel.getNotitficationType().getTitle());
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        nameLabel.setFont(TITLE_FONT);
        nameLabel.setForeground(TITLE_COLOR);
        nameLabel.setPreferredSize(new Dimension(100, LABEL_HEIGHT));
        add(nameLabel);
        
        addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                repaint();
            }

            @Override 
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                repaint();
            }

            @Override 
            public void mouseClicked(MouseEvent e) {
                labelClicked(panel.getNotitficationType());
            }
        });
    }
    
    /**
     * Paints the component
     * @param g the graphics context
     */
    @Override
    public void paintComponent(Graphics g) {
        int count = (stats == null) ? 0 : stats.unacknowledgedCount;
        String countTxt = String.valueOf(count);
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the background
        g2.setColor(mousePressed ? CLICK_COLOR : BACKGROUND_COLOR);
        
        int w = getWidth() - BLINK_WIDTH;
        if (drawSelectionPointer) {
            w -= POINTER_WIDTH;
        }
        g2.fillRect(0, 0, w, getHeight());        

        // Paint the selected pointer
        if (drawSelectionPointer && selected) {
            Polygon pointer = new Polygon();
            pointer.addPoint(w + BLINK_WIDTH, 0);
            pointer.addPoint(w + BLINK_WIDTH, getHeight());
            pointer.addPoint(getWidth(), getHeight() / 2);
            g2.fill(pointer);
        }

        // Draw the blink area
        if (count > 0) {
            g.setColor(blinks % 2 == 0 ? BLINK0_COLOR : BLINK1_COLOR);
        }
        g2.fillRect(w, 0, BLINK_WIDTH, getHeight());        
        
        
        // Let super do the normal painting
        super.paintComponent(g2);
        
        // Paint the count
        int padding = 4;
        int countSize = getHeight() - 2 * padding;
        Rectangle2D countRect = new Rectangle2D.Double(w - countSize - padding, padding, countSize, countSize);
        
        // With alerts and warnings, paint a read circle around the count
        /*
        if (stats != null && (stats.unacknowledgedWarningCount > 0 || stats.unacknowledgedAlertCount > 0)) {
            if (stats.unacknowledgedAlertCount > 0) {
                g2.setColor(ALERT_COLOR);
            } else {
                g2.setColor(WARN_COLOR);
            }
            g2.fillArc((int)countRect.getX(), (int)countRect.getY(), (int)countRect.getWidth(), (int)countRect.getHeight(), 0, 360);
        }
        */
        
        // Draw the count centered in the count rectangle
        if (stats != null && (stats.unacknowledgedWarningCount > 0 || stats.unacknowledgedAlertCount > 0)) {
            if (stats.unacknowledgedAlertCount > 0) {
                g2.setColor(ALERT_COLOR);
            } else {
                g2.setColor(WARN_COLOR);
            }
        } else {
            g2.setColor(COUNTER_COLOR);
        }
        g2.setFont(COUNTER_FONT);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(countTxt, 
                Math.round(countRect.getX() + (countRect.getWidth() - fm.stringWidth(countTxt)) / 2.0), 
                Math.round(countRect.getY() + fm.getAscent() + (countRect.getHeight() - (fm.getAscent() + fm.getDescent())) / 2.0));
    }
    
    /**
     * Called when the label is clicked.
     * Sub-classes can override to take action
     * @param type the type
     */
    public void labelClicked(NotificationType type) {    
    }
     
    /**
     * {@inheritDoc}
     */
    @Override
    public void notificationsUpdated(NotificationStatistics stats) {
        this.stats = stats;
        
        // When the number of unacknowledged notifications have increased,
        // start blinking
        if (stats.unacknowledgedCount > lastUnacknowledgedCount) {
            startBlinking(BLINK_COUNT);
        }
        lastUnacknowledgedCount = stats.unacknowledgedCount;
        
        repaint();
    }

    /**
     * Called by the blink timer
     */
    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        blinks--;
        if (blinks <= 0) {
            blinks = 0;
            blinkTimer.stop();
        }
        repaint();
    }
    
    /**
     * Restarts the blink timer with the given number of blinks
     * @param blinkCount the number of blinks
     */
    public synchronized void startBlinking(int blinkCount) {
        blinks = blinkCount;
        blinkTimer.restart();
    }

    public boolean isDrawSelectionPointer() {
        return drawSelectionPointer;
    }

    public void setDrawSelectionPointer(boolean drawSelectionPointer) {
        this.drawSelectionPointer = drawSelectionPointer;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (selected != this.selected) {
            this.selected = selected;
            repaint();
        }
    }
}
