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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.gui.RotatedLabel;
import dk.dma.epd.common.prototype.gui.RotatedLabel.Direction;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;
import dk.dma.epd.common.util.SafeHavenUtils;
import dk.dma.epd.common.util.TypedValue.Dist;
import dk.dma.epd.common.util.TypedValue.DistType;
import dk.dma.epd.common.util.TypedValue.Speed;
import dk.dma.epd.common.util.TypedValue.SpeedType;
import dk.dma.epd.common.util.TypedValue.Time;
import dk.dma.epd.common.util.TypedValue.TimeType;

/**
 * Displays the safe haven and target speed relative to the planned route
 */
public class SafeHavenPanel extends DockablePanel {

    private static final long serialVersionUID = 1L;
    
    private static final double SHIP_SCALE = 1.0;
    private static final Color COORDINATE_LINE_COLOR = new Color(50, 50, 50, 100);
    
    /**
     * Defines the current state of the safe haven panel
     */
    public enum State {
        INACTIVE(false, SafeHavenUtils.SF_COLOR_GRAY),
        OK(true, SafeHavenUtils.SF_COLOR_GREEN),
        WARN(true, SafeHavenUtils.SF_COLOR_YELLOW),
        ALERT(true, SafeHavenUtils.SF_COLOR_RED);
        
        Color color;
        boolean active;
        
        private State(boolean active, Color color) {
            this.active = active;
            this.color = color;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public Color getColor() {
            return color;
        }
    }
    
    VesselPositionData positionData;
    VesselStaticData staticData;
    ActiveRoute activeRoute;
    
    Position sfPos;
    double sfBearing, sfWidth, sfLen;
    double sfScale = 1.0;
    State state = State.INACTIVE;
    List<Position> sfBounds = new ArrayList<>();
    
    JLabel lblTargetSpeed = new JLabel("N/A");
    JLabel lblPresentSpeed = new JLabel("N/A");
    SafeHavenView safeHavenView = new SafeHavenView(140);
    
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
     * @param positionData the own-ship PNT data
     * @param staticData the vessel static data
     */
    public synchronized void shipPntDataChanged(VesselPositionData positionData, VesselStaticData staticData) {
        this.positionData = positionData;
        this.staticData = staticData;
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
        if (positionData == null || positionData.getPos() == null) {
            lblPresentSpeed.setText("N/A");
        } else {
            lblPresentSpeed.setText(
                    String.format("(%s)", Formatter.formatCurrentSpeed((double)positionData.getSog(), 1)));
        }

        // Update the target speed label
        if (activeRoute == null || !activeRoute.isVisible() || Math.abs(activeRoute.getSafeHavenSpeed()) < 0.0000001) {
            lblTargetSpeed.setText("N/A");            
            state = State.INACTIVE;
            
        } else {
            // Compute safe haven attributes
            sfPos = activeRoute.getSafeHavenLocation();
            sfBearing = activeRoute.getSafeHavenBearing();
            if (activeRoute.getActiveWp().getOutLeg() != null) {
                sfWidth = activeRoute.getActiveWp().getOutLeg().getSFWidth();
                sfLen = activeRoute.getActiveWp().getOutLeg().getSFLen();
            } else {
                sfWidth = activeRoute.getWaypoints().get(activeRoute.getWaypoints().size() - 2).getOutLeg().getSFWidth();
                sfLen = activeRoute.getWaypoints().get(activeRoute.getWaypoints().size() - 2).getOutLeg().getSFLen();
            }
            
            lblTargetSpeed.setText(
                    String.format("%s", Formatter.formatCurrentSpeed(activeRoute.getSafeHavenSpeed(), 1)));

            if (positionData == null || positionData.getPos() == null) {
                state = State.INACTIVE;
                
            } else {
                
                double distance = sfPos.distanceTo(positionData.getPos(), CoordinateSystem.CARTESIAN);
                Point2D shipPos = computeShipPositionRelativeToSafeHaven();
                Rectangle2D safeHavenBounds = computeSafeHavenBounds();
                
                // Use same criteria as SafeHavenArea
                if (safeHavenBounds.contains(shipPos)) {
                    state = State.OK;
                } else if (distance < Converter.nmToMeters(1)) {
                    state = State.WARN;
                } else {
                    state = State.ALERT;
                }
            }
        }
        
        lblTargetSpeed.setForeground(state.getColor());
        safeHavenView.updateSafeHavenView();
    }    
    
    /**
     * Computes the ship position relative to the safe haven center
     * @return the ship position relative to the safe haven center
     */
    protected Point2D computeShipPositionRelativeToSafeHaven() {
        Point2D pos = new Point2D.Double();
        // Compute the ship offset relative to the safe haven
        double distanceToShip = sfPos.rhumbLineDistanceTo(positionData.getPos());
        double bearingToShip =  sfPos.rhumbLineBearingTo(positionData.getPos());            
        
        // Get the angle of the hypotenuse to the ship and use Pythagoras
        double angleToShip = (-sfBearing + bearingToShip - 90 + 360.0D) % 360.0D;
        pos.setLocation(
                distanceToShip * Math.cos(Math.toRadians(angleToShip)),
                distanceToShip * Math.sin(Math.toRadians(angleToShip)));
        
        return pos;
    }
    
    /**
     * Computes the safe haven bounds
     * @return  the safe haven bounds
     */
    protected Rectangle2D computeSafeHavenBounds() {
        Rectangle2D bounds  = new Rectangle2D.Double();        
        bounds.setFrameFromCenter(0, 0, sfWidth / 2.0,  sfLen / 2.0);
        return bounds;
    }
    
    /**
     * Paints the safe haven
     */
    class SafeHavenView extends JComponent {

        private static final long serialVersionUID = 1L;
        
        Point2D center      = new Point2D.Double();
        Line2D horizLine    = new Line2D.Double();
        Line2D vertLine     = new Line2D.Double();
        
        RotatedLabel top    = new RotatedLabel();
        RotatedLabel left   = new RotatedLabel();
        RotatedLabel bottom = new RotatedLabel();
        RotatedLabel right  = new RotatedLabel();

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
            
            // Configure and add labels
            addLabel(top, Direction.HORIZONTAL);
            addLabel(left, Direction.VERTICAL_DOWN);
            addLabel(bottom, Direction.HORIZONTAL);
            addLabel(right, Direction.VERTICAL_DOWN);
            int pad = 1;
            top.setLocation((getWidth() - top.getWidth()) / 2, pad);
            left.setLocation(pad, (getHeight() - left.getHeight()) / 2);
            bottom.setLocation((getWidth() - bottom.getWidth()) / 2, getHeight() - bottom.getHeight() - pad);
            right.setLocation(getWidth() - right.getWidth() - pad, (getHeight() - right.getHeight()) / 2);
            
            // Add center safe haven view
            center.setLocation((double)s / 2d, (double)s / 2d);
        }
        
        /**
         * Adds and adjusts the attributes of the given label
         * @param lbl the label
         * @param direction the direction of the label text
         */
        private void addLabel(RotatedLabel lbl, Direction direction) {
            add(lbl);
            lbl.setText("N/A");
            lbl.setFont(lbl.getFont().deriveFont(9f).deriveFont(Font.PLAIN));
            lbl.setForeground(Color.white);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setSize(60, lbl.getPreferredSize().height);
            lbl.setDirection(direction);
        }
        
        /**
         * Updates the view
         */
        protected void updateSafeHavenView() {
            
            if (state == State.INACTIVE) {
                top.setText("N/A");
                left.setText("N/A");
                bottom.setText("N/A");
                right.setText("N/A");
            } else {
                
                // Update the scaling to include the safe haven and the ship position (+100 meters for good measure)
                Point2D shipPos = computeShipPositionRelativeToSafeHaven();
                double scalew = getWidth() / Math.max(sfWidth, Math.abs(shipPos.getX() * 2.0) + 100.0);
                double scaleh = getHeight() / Math.max(sfLen, Math.abs(shipPos.getY() * 2.0) + 100.0);
                sfScale = Math.min(scalew, scaleh);
                
                String h = formatTime(getWidth() / 2.0 / sfScale);
                String w = formatDist(getHeight() / 2.0 / sfScale);
                top.setText("+ " + h);
                left.setText(w);
                bottom.setText("- " + h);
                right.setText(w);
            }
            
            repaint();
        }
        
        /**
         * Formats the distance 
         * @param dist the distance
         * @return the formatted distance
         */
        private String formatDist(double dist) {
            Dist d = new Dist(DistType.METERS, dist);
            
            if (d.in(DistType.NAUTICAL_MILES).doubleValue() < 1.0) {
                return String.format("%d m", d.intValue());
            } else if (d.in(DistType.NAUTICAL_MILES).doubleValue() < 5.0) {
                return String.format(Locale.US, "%.1f nm", d.in(DistType.NAUTICAL_MILES).doubleValue());                
            } else {
                return String.format("%d nm", d.in(DistType.NAUTICAL_MILES).intValue());
            }
        }
        
        /**
         * Formats the distance as time 
         * @param dist the time
         * @return the formatted time
         */
        private String formatTime(double dist) {
            Time t = new Dist(DistType.METERS, dist)
                .withSpeed(new Speed(SpeedType.KNOTS, activeRoute.getSafeHavenSpeed()))
                .in(TimeType.MINUTES);                    
            
            if (t.doubleValue() < 5.0) {
                long secs = t.in(TimeType.SECONDS).longValue();
                return String.format("%02d:%02d min", (secs % 3600) / 60, secs % 60);                
            } else if (t.in(TimeType.HOURS).doubleValue() < 1.0) {
                return String.format("%d min", t.intValue());
            } else if (t.in(TimeType.HOURS).doubleValue() < 5.0) {
                long secs = t.in(TimeType.SECONDS).longValue();
                return String.format("%d:%02d hrs", secs / 3600, (secs % 3600) / 60);                
            } else {
                return String.format("%d hrs", t.in(TimeType.HOURS).intValue());
            }
        }
        
        /**
         * Paints the component
         */
        @Override
        public void paintComponent(Graphics g) {
            
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the main background safe haven color
            g2.setColor(state.getColor().darker());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(state.getColor().darker().darker());
            g2.drawRect(0, 0, getWidth(), getHeight());
            
            // Bail if we are not in an active state
            if (!state.isActive()) {
                return;
            }
            
            // Set the affine transformation that centers on the safe haven center point
            // and scales according the the sfScale
            g2.translate(center.getX(), center.getY());
            g2.scale(sfScale, sfScale);
            g2.setStroke(new BasicStroke(1.0f / (float)sfScale));
            
            
            // Draw scaled safe have bounds
            Rectangle2D safeHavenBounds = computeSafeHavenBounds();
            g2.setColor(state.getColor());
            g2.fill(safeHavenBounds);
            g2.setColor(state.getColor().darker().darker());
            g2.draw(safeHavenBounds);

        
            // Draw coordinate lines
            BasicStroke lineStroke = new BasicStroke(
                    1.0f / (float)sfScale,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[] { 4f / (float)sfScale, 2f / (float)sfScale }, 0.0f);
            g2.setStroke(lineStroke); 
            g2.setColor(COORDINATE_LINE_COLOR);
            horizLine.setLine(- getWidth() / 2.0 / sfScale, 0, getWidth() / 2.0 / sfScale, 0);
            vertLine.setLine(0, - getHeight() / 2.0 / sfScale, 0, getHeight() / 2.0 / sfScale);
            g2.draw(horizLine);
            g2.draw(vertLine);
            
            
            // Draw the vessel
            drawVessel(g2);
        }
        
        /**
         * Draws the vessel
         * @param g2 the graphical context
         */
        private void drawVessel(Graphics2D g2) {
            if (positionData != null && positionData.getPos() != null) {
                drawVesselOutline(g2);            
            }
        }
        
        /**
         * Draws the vessel as an outline
         * @param g2 the graphical context
         */
        private void drawVesselOutline(Graphics2D g2) {
            
            // Compute the ship offset relative to the safe haven
            Point2D shipPos = computeShipPositionRelativeToSafeHaven();
            
            // Translate the graphics context so that the ship position is centered
            g2.translate(shipPos.getX(), shipPos.getY());
            
            Path2D ship = new Path2D.Double();
            if (staticData != null) {
                // Determine the heading of the ship relative to the safe haven
                // which is painted pointing north
                double shipAngle = (-sfBearing + positionData.getTrueHeading() + 360.0D) % 360.0D;
                g2.rotate(Math.toRadians(shipAngle));
                
                // Determine lower-left and width and height of ship
                double llx = -staticData.getDimPort() * SHIP_SCALE;
                double lly = staticData.getDimStern() * SHIP_SCALE;
                double w = staticData.getDimPort() * SHIP_SCALE + staticData.getDimStarboard() * SHIP_SCALE;
                double h = staticData.getDimBow() * SHIP_SCALE + staticData.getDimStern() * SHIP_SCALE;
                
                // Create the shape of the ship
                ship.moveTo(llx, lly);
                ship.lineTo(llx, lly - h * 0.85);
                ship.lineTo(llx + w / 2.0, lly - h);
                ship.lineTo(llx + w, lly - h * 0.85);
                ship.lineTo(llx + w, lly);
                ship.closePath();
            
            } else {
                // Create a marker
                double size = 10 / 2 * SHIP_SCALE;
                ship.moveTo(-size, size);
                ship.lineTo(-size, -size);
                ship.lineTo(size, -size);
                ship.lineTo(size, size);
                ship.closePath();
            }
            
            // Draw the ship
            g2.setStroke(new BasicStroke(2.0f / (float)sfScale));
            g2.setColor(Color.white);
            g2.fill(ship);
            g2.setStroke(new BasicStroke(1.0f / (float)sfScale));
            g2.setColor(Color.black);
            g2.draw(ship);
            
            // Draw a dot for the indicator
            if (SHIP_SCALE > 3) {
                g2.setColor(Color.red);
                Ellipse2D dot = new Ellipse2D.Double();
                dot.setFrameFromCenter(new Point2D.Double(0.0, 0.0), new Point2D.Double(1.0 / sfScale, 1.0 / sfScale));
                g2.fill(dot);
            }
        }
    }
}
