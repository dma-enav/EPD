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
package dk.dma.epd.ship.gui.panels;

import dk.dma.ais.message.AisMessage5;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.sensor.nmea.PntSource;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData.ErrorEllipse;
import dk.dma.epd.common.text.Formatter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NONE;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;

/**
 * Multi-source PNT panel.
 * <p>
 * Displays the current PNT source and error state of the resilient PNT sensor
 */
public class MultiSourcePntPanel extends DockablePanel {

    private static final long serialVersionUID = 1L;
    private static final String NA = "N/A";

    ResilientPntData rpntData;
    VesselPositionData positionData;
    VesselStaticData staticData;

    JLabel lblPntSource = new JLabel();
    JLabel lblJamming = new JLabel();
    JLabel lblHpl = new JLabel();
    JLabel lblErrorMajorAxis = new JLabel();
    JLabel lblErrorMinorAxis = new JLabel();
    JLabel lblErrorBearing = new JLabel();
    RpntErrorMeter rpntErrorMeter = new RpntErrorMeter(140);
    
    /**
     * Constructor
     */
    public MultiSourcePntPanel() {
        super(new GridBagLayout());
        
        Insets insets5      = new Insets(5, 5, 5, 5);
        Insets insetsB5     = new Insets(0, 0, 5, 0);
        Insets insetsBR5    = new Insets(0, 0, 5, 5);
        
        // Title
        int gridy = 0;
        add(adjust(new JLabel("Resilient PNT"), SwingConstants.CENTER, 14, Font.BOLD), 
                new GridBagConstraints(0, gridy, 2, 1, 1.0, 0.0, CENTER, HORIZONTAL, insetsB5, 0, 0));

        // PNT Source
        gridy++;
        add(adjust(new JLabel("Source"), LEFT), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblPntSource, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        
        // Jamming
        gridy++;
        add(adjust(new JLabel("Integrity"), LEFT),
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblJamming, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        
        // Horizontal protection level
        gridy++;
        add(adjust(new JLabel("HPL"), LEFT), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblHpl, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        
        // Error ellipse major axis
        gridy++;
        add(adjust(new JLabel("Major axis"), LEFT), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblErrorMajorAxis, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        
        // Error ellipse minor axis
        gridy++;
        add(adjust(new JLabel("Minor axis"), LEFT), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblErrorMinorAxis, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        
        // Error ellipse major axis bearing
        /** Removed by request from UKHO
        gridy++;
        add(adjust(new JLabel("Bearing"), LEFT), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblErrorBearing, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        **/

        // RPNT error meter
        gridy++;
        add(rpntErrorMeter, 
                new GridBagConstraints(0, gridy, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));        
        
        // Lastly, update the field values
        setRpntData(null);
    }
    
    /**
     * Sets the new {@code ResilientPntData} and updates the UI.
     *
     * @param rpntData the new data
     */
    public void setRpntData(final ResilientPntData rpntData) {

        this.rpntData = rpntData;
        updatePanel();
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
     * Based on the current state data, update and repaint the panel in the main Swing thread
     */
    public void updatePanel() {
        // Update in the Swing worker thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    updatePanel();
                }});
            return;
        }

        if (rpntData == null) {
            lblPntSource.setText(NA);
            lblJamming.setText(NA);
            lblHpl.setText(NA);
            lblErrorMajorAxis.setText(NA);
            lblErrorMinorAxis.setText(NA);
            lblErrorBearing.setText(NA);

        } else {
            lblPntSource.setText(rpntData.getPntSource().toString());
            lblJamming.setText(rpntData.getHpl() > getHal() ? "Alert" : "OK");
            lblHpl.setText(Formatter.formatMeters(rpntData.getHpl(), 1));
            lblErrorMajorAxis.setText(Formatter.formatMeters(rpntData.getErrorEllipse().getMajorAxis(), 1));
            lblErrorMinorAxis.setText(Formatter.formatMeters(rpntData.getErrorEllipse().getMinorAxis(), 1));
            lblErrorBearing.setText(Formatter.formatDegrees(rpntData.getErrorEllipse().getBearing(), 0));
        }

        rpntErrorMeter.repaint();
    }

    /**
     * Returns the HAL
     * @return the HAL
     */
    protected int getHal() {
        // NP check to support testing
        return EPD.getInstance() == null ? 25 : EPD.getInstance().getSettings().getSensorSettings().getMsPntHal();
    }

    /**
     * Test method
     * @param args unused
     */
    public static void main(String[] args) {
        JFrame f = new JFrame("Test");
        f.setBounds(100, 100, 400, 400);

        MultiSourcePntPanel p = new MultiSourcePntPanel();
        p.setBackground(Color.black);
        f.getContentPane().add(p);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);

        ResilientPntData rpntData = new ResilientPntData(PntSource.GPS, ResilientPntData.JammingFlag.OK, 22, 15, 12, 143);
        p.setRpntData(rpntData);

        VesselPositionData pos = new VesselPositionData();
        pos.setCog(132.0f);
        pos.setNavStatus(15);
        pos.setPos(Position.create(56.04, 12.35));
        pos.setPosAcc(0);
        pos.setRot(0.0f);
        pos.setSog(21.3f);
        pos.setTrueHeading(131.0f);
        AisMessage5 ais = new AisMessage5();
        ais.setDimBow(50);
        ais.setDimPort(4);
        ais.setDimStarboard(8);
        ais.setDimStern(13);
        ais.setDraught(26);
        p.shipPntDataChanged(pos,  new VesselStaticData(ais));
    }

    /**
     * Paints the HPL and error ellipse in a meter whose radius is defined by 
     * the HAL
     */
    class RpntErrorMeter extends JComponent {

        private static final long serialVersionUID = 1L;
        
        Color bgColor                   = new Color(255, 255, 255, 20);
        Color halColor                  = new Color(255, 100, 100, 200);
        Color hplColor                  = new Color(100, 100, 255, 200);
        Color errorEllipseErrorColor    = new Color(255, 100, 100, 100);
        Color errorEllipseOkColor       = new Color(100, 255, 100, 100);
        Color shipColor                 = new Color(255, 255, 255, 100);
        Ellipse2D halCircle             = new Ellipse2D.Double();
        Ellipse2D hplCircle             = new Ellipse2D.Double();
        Ellipse2D errorEllipse          = new Ellipse2D.Double();
        Point2D center                  = new Point2D.Double();
        double margin                   = 5d;
        int meterSize;

        /**
         * Constructor
         */
        public RpntErrorMeter(int s) {
            super();
            setLayout(null);
            setOpaque(false);

            this.meterSize = s;
            Dimension size = new Dimension(s, s);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            
            center.setLocation((double)s / 2d, (double)s / 2d);
        }

        /**
         * Compute the scale of the meter panel, computed so as to include the HAL, the HPL and
         * the dimensions of the ship
         * @return the scale
         */
        private double computeScale() {
            // Find the max value to display

            double val = (double)getHal();
            if (rpntData != null) {
                val = Math.max(val, rpntData.getHpl());
            }
            if (staticData != null) {
                val = Math.max(val, 2.0 * staticData.getDimPort());
                val = Math.max(val, 2.0 * staticData.getDimStarboard());
                val = Math.max(val, 2.0 * staticData.getDimBow());
                val = Math.max(val, 2.0 * staticData.getDimStern());
            }
            return ((double)meterSize - 2 * margin) / val;
        }

        /**
         * Paints the component
         */
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double scale = computeScale();

            g2.setColor(bgColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Set the affine transformation
            g2.translate(center.getX(), center.getY());
            g2.scale(scale, scale);

            // Draw the vessel outline
            drawVesselOutline(g2, scale);

            g2.setStroke(new BasicStroke(Math.max(0.5f, 2.0f / (float)scale)));

            // Draw HAL circle
            int hal = getHal();
            g2.setColor(halColor);
            halCircle.setFrameFromCenter(0, 0, hal, hal);
            g2.draw(halCircle);

            if (rpntData != null) {
                // Draw HPL circle
                g2.setColor(hplColor);
                hplCircle.setFrameFromCenter(0, 0, rpntData.getHpl(), rpntData.getHpl());
                g2.draw(hplCircle);
                
                // Draw error ellipse
                ErrorEllipse el = rpntData.getErrorEllipse();
                g2.rotate(el.getOMBearing());
                if (positionData != null) {
                    g2.rotate(-Math.toRadians(positionData.getTrueHeading()));
                }
                Color elliseColor = rpntData.getHpl() > hal ? errorEllipseErrorColor : errorEllipseOkColor;
                g2.setColor(elliseColor);
                errorEllipse.setFrameFromCenter(0, 0, el.getMajorAxis(), el.getMinorAxis());
                g2.fill(errorEllipse);
            }
        }

        /**
         * Draws the vessel as an outline
         * @param g2 the graphical context
         */
        private void drawVesselOutline(Graphics2D g2, double scale) {

            Path2D ship = new Path2D.Double();
            if (staticData != null) {

                // Determine lower-left and width and height of ship
                double llx = -staticData.getDimPort();
                double lly = staticData.getDimStern();
                double w = staticData.getDimPort() + staticData.getDimStarboard();
                double h = staticData.getDimBow() + staticData.getDimStern();

                // Create the shape of the ship
                ship.moveTo(llx, lly);
                ship.lineTo(llx, lly - h * 0.85);
                ship.lineTo(llx + w / 2.0, lly - h);
                ship.lineTo(llx + w, lly - h * 0.85);
                ship.lineTo(llx + w, lly);
                ship.closePath();

                // Draw the ship
                g2.setStroke(new BasicStroke(1.0f / (float)scale));
                g2.setColor(shipColor);
                g2.draw(ship);
            }
        }

     }

}

