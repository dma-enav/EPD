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
package dk.dma.epd.ship.gui.panels;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.SafeHavenUtils;

/**
 * Displays the safe haven and target speed relative to the planned route
 */
public class SafeHavenPanel extends DockablePanel {

    private static final long serialVersionUID = 1L;
    
    PntData pntData;
    ActiveRoute activeRoute;
    
    Position sfPos;
    double sfBearing, sfWidth, sfHeight;
    Color sfColor = SafeHavenUtils.SF_COLOR_GRAY;
    List<Position> sfBounds = new ArrayList<>();
    
    JLabel lblTargetSpeed = new JLabel("N/A");
    JLabel lblPresentSpeed = new JLabel("N/A");
    SafeHavenView safeHavenView = new SafeHavenView(100);
    
    /**
     * Constructor
     */
    public SafeHavenPanel() {
        super(new GridBagLayout());
        
        Insets insets0      = new Insets(0, 0, 0, 0);
        Insets insets5      = new Insets(5, 5, 5, 5);
        Insets insetsT5     = new Insets(5, 0, 0, 0);
        Insets insetsB5     = new Insets(0, 0, 5, 0);
        
        // Title
        int gridy = 0;
        add(adjust(new JLabel("Safe Haven"), SwingConstants.CENTER, 14, Font.BOLD), 
                new GridBagConstraints(0, gridy++, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insets0, 0, 0));
        
        // Safe haven view
        JPanel safeHavenViewPanel = new JPanel();
        safeHavenViewPanel.add(safeHavenView);
        add(safeHavenViewPanel, 
                new GridBagConstraints(0, gridy++, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insets5, 0, 0));
        
        // Target speed
        add(adjust(new JLabel("Target Speed"), SwingConstants.CENTER, 10, Font.PLAIN), 
                new GridBagConstraints(0, gridy++, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insetsT5, 0, 0));
        add(adjust(lblTargetSpeed, SwingConstants.CENTER, 12, Font.BOLD), 
                new GridBagConstraints(0, gridy++, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insetsB5, 0, 0));
        
        // Present speed
        add(adjust(new JLabel("Present Speed"), SwingConstants.CENTER, 10, Font.PLAIN), 
                new GridBagConstraints(0, gridy++, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insetsT5, 0, 0));
        add(adjust(lblPresentSpeed, SwingConstants.CENTER), 
                new GridBagConstraints(0, gridy++, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insetsB5, 0, 0));
        
    }    

    /**
     * Updates the own-ship position
     * @param pntData the own-ship PNT data
     */
    public synchronized void shipPntDataChanged(PntData pntData) {
        this.pntData = pntData;
        updatePanel();
    }

    /**
     * Called whenever the active route is changed
     * @param activeRoute the current active route
     */
    public synchronized void activeRouteUpdated(ActiveRoute activeRoute) {
        this.activeRoute = activeRoute;
        updatePanel();
    }

    /**
     * Updates the panel with the speed, target speed and safe haven state
     */
    public void updatePanel() {
        // Run only in event dispatch thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    doUpdatePanel();
                }
            });
        } else {
            doUpdatePanel();
        }
    }

    /**
     * Updates the panel with the speed, target speed and safe haven state.
     * This method is run in the Swing event thread.
     */
    private synchronized void doUpdatePanel() {
        // Update the present speed label
        if (pntData == null) {
            lblPresentSpeed.setText("N/A");
        } else {
            lblPresentSpeed.setText(
                    String.format("(%s)", Formatter.formatCurrentSpeed(pntData.getSog(), 1)));
        }

        // Update the target speed label
        if (activeRoute == null || !activeRoute.isVisible()) {
            lblTargetSpeed.setText("N/A");
            
            sfColor = SafeHavenUtils.SF_COLOR_GRAY;
            lblTargetSpeed.setForeground(sfColor);
            
        } else {
            // Compute safe haven attributes
            sfPos = activeRoute.getSafeHavenLocation();
            sfBearing = activeRoute.getSafeHavenBearing();
            if (activeRoute.getActiveWp().getOutLeg() != null) {
                sfWidth = activeRoute.getActiveWp().getOutLeg().getSFWidth();
                sfHeight = activeRoute.getActiveWp().getOutLeg().getSFLen();
            } else {
                sfWidth = activeRoute.getWaypoints().get(activeRoute.getWaypoints().size() - 2).getOutLeg().getSFWidth();
                sfHeight = activeRoute.getWaypoints().get(activeRoute.getWaypoints().size() - 2).getOutLeg().getSFLen();
            }
            
            lblTargetSpeed.setText(
                    String.format("%s", Formatter.formatCurrentSpeed(activeRoute.getSafeHavenSpeed(), 1)));

            if (pntData == null || pntData.getPosition() == null) {
                sfColor = SafeHavenUtils.SF_COLOR_GRAY;
                lblTargetSpeed.setForeground(sfColor);
                
            } else {
                // Update the bounds - not used yet
                SafeHavenUtils.calculateBounds(sfPos, sfBearing, sfWidth, sfHeight, sfBounds);
                
                double distance = sfPos.distanceTo(pntData.getPosition(), CoordinateSystem.CARTESIAN);
                
                // For now:
                if (distance < Math.min(sfWidth / 2.0,  sfHeight / 2.0)) {
                    sfColor = SafeHavenUtils.SF_COLOR_GREEN;
                } else if (distance < Converter.nmToMeters(1)) {
                    sfColor = SafeHavenUtils.SF_COLOR_YELLOW;
                } else {
                    sfColor = SafeHavenUtils.SF_COLOR_RED;
                }
                lblTargetSpeed.setForeground(sfColor);                
            }
        }
        
        safeHavenView.updateSafeHavenView();
    }    
    
    /**
     * Paints the safe haven
     */
    class SafeHavenView extends JComponent {

        private static final long serialVersionUID = 1L;
        
        Point2D center      = new Point2D.Double();
        Rectangle2D bounds  = new Rectangle2D.Double();
        Line2D horizLine    = new Line2D.Double();
        Line2D vertLine     = new Line2D.Double();
        
        JLabel top      = new JLabel("          ");
        JLabel left     = new JLabel("          ");
        JLabel bottom   = new JLabel("          ");
        JLabel right    = new JLabel("          ");

        /**
         * Constructor
         */
        public SafeHavenView(int s) {
            super();
            setLayout(null);
            setOpaque(true);
            
            Dimension size = new Dimension(s, s);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            
            addLabel(top, SwingConstants.CENTER);
            addLabel(left, SwingConstants.LEFT);
            addLabel(bottom, SwingConstants.CENTER);
            addLabel(right, SwingConstants.RIGHT);
            int pad = 2, lw = 40, lh = 14;
            top.setBounds((getWidth() - lw) / 2, pad, lw, lh);
            left.setBounds(pad, (getHeight() - lh) / 2, lw, lh);
            bottom.setBounds((getWidth() - lw) / 2, getHeight() - lh - pad, lw, lh);
            right.setBounds(getWidth() - lw - pad, (getHeight() - lh) / 2, lw, lh);
            
            center.setLocation((double)s / 2d, (double)s / 2d);
        }
        
        /**
         * Adds and adjusts the attributes of the given label
         * @param lbl the label
         * @param alignment the alignment
         */
        private void addLabel(JLabel lbl, int alignment) {
            add(lbl);
            lbl.setHorizontalAlignment(alignment);
            lbl.setFont(lbl.getFont().deriveFont(9f).deriveFont(Font.PLAIN));
            lbl.setForeground(Color.white);
        }
        
        /**
         * Updates the view
         */
        protected void updateSafeHavenView() {
            top.setText("+ 60 min");
            left.setText("500 m");
            bottom.setText("- 60 min");
            right.setText("500 m");
            repaint();
        }
        
        /**
         * Paints the component
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the main background safe haven color
            Color col = sfColor;
            g2.setColor(col.darker());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(col.darker().darker());
            g2.drawRect(0, 0, getWidth(), getHeight());
            
            if (activeRoute == null || pntData == null || pntData.getPosition() == null) {
                return;
            }
            
            double distance = sfPos.distanceTo(pntData.getPosition(), CoordinateSystem.CARTESIAN);
            double scale = getWidth() / Math.max(sfWidth, distance * 2d);

            
            // Set the affine transformation
            g2.translate(center.getX(), center.getY());
            g2.scale(scale, scale);
            g2.setStroke(new BasicStroke(1.0f / (float)scale));
            
            
            // Draw scaled safe have bounds
            bounds.setFrameFromCenter(0, 0, sfWidth / 2.0,  sfHeight / 2.0);
            g2.setColor(col);
            g2.fill(bounds);
            g2.setColor(Color.black);
            g2.draw(bounds);

        
            // Draw coordinate lines
            BasicStroke lineStroke = new BasicStroke(
                    1.5f / (float)scale,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[] { 4f / (float)scale, 2f / (float)scale }, 0.0f);
            g2.setStroke(lineStroke); 
            g2.setColor(new Color(150, 150, 150, 50));
            horizLine.setLine(- getWidth() / 2.0 / scale, 0, getWidth() / 2.0 / scale, 0);
            vertLine.setLine(0, - getHeight() / 2.0 / scale, 0, getHeight() / 2.0 / scale);
            g2.draw(horizLine);
            g2.draw(vertLine);
        }
    }
}
