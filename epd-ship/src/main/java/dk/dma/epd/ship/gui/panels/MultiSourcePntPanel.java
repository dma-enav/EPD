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
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData.ErrorEllipse;
import dk.dma.epd.common.text.Formatter;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.HORIZONTAL;
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
    private static final int HAL = 25;

    ResilientPntData rpntData;
    
    JLabel lblPntSource = new JLabel();
    JLabel lblJamming = new JLabel();
    JLabel lblHpl = new JLabel();
    JLabel lblErrorMajorAxis = new JLabel();
    JLabel lblErrorMinorAxis = new JLabel();
    JLabel lblErrorBearing = new JLabel();
    RpntErrorMeter rpntErrorMeter = new RpntErrorMeter(100);
    
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
        add(adjust(new JLabel("Jamming"), LEFT), 
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
        gridy++;
        add(adjust(new JLabel("Bearing"), LEFT), 
                new GridBagConstraints(0, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        add(adjust(lblErrorBearing, RIGHT), 
                new GridBagConstraints(1, gridy, 1, 1, 0.0, 0.0, WEST, NONE, insetsBR5, 0, 0));
        
        // RPNT error meter
        gridy++;
        add(rpntErrorMeter, 
                new GridBagConstraints(0, gridy, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));        
        
        // Lastly, update the field values
        setRpntData(null);
    }
    
    /**
     * Sets the new {@code ResilientPntData} and updates the UI.
     * The UI will be updated in the main Swing thread.
     * 
     * @param rpntData the new data
     */
    public void setRpntData(final ResilientPntData rpntData) {
        // Update in the Swing worker thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    setRpntData(rpntData);
                }});
            return;
        }
        
        this.rpntData = rpntData;
        
        if (rpntData == null) {
            lblPntSource.setText(NA);
            lblJamming.setText(NA);
            lblHpl.setText(NA);
            lblErrorMajorAxis.setText(NA);
            lblErrorMinorAxis.setText(NA);
            lblErrorBearing.setText(NA);
            
        } else {
            lblPntSource.setText(rpntData.getPntSource().toString());
            lblJamming.setText(rpntData.getJammingFlag().toString());
            lblHpl.setText(Formatter.formatMeters(rpntData.getHpl(), 1));  
            lblErrorMajorAxis.setText(Formatter.formatMeters(rpntData.getErrorEllipse().getMajorAxis(), 1));
            lblErrorMinorAxis.setText(Formatter.formatMeters(rpntData.getErrorEllipse().getMinorAxis(), 1));
            lblErrorBearing.setText(Formatter.formatDegrees(rpntData.getErrorEllipse().getBearing(), 0));
        }
        
        rpntErrorMeter.repaint();
    }
    
    /**
     * Paints the HPL and error ellipse in a meter whose radius is defined by 
     * the HAL
     */
    class RpntErrorMeter extends JComponent {

        private static final long serialVersionUID = 1L;
        
        Color bgColor           = new Color(255, 255, 255, 10);
        Color halColor          = new Color(255, 100, 100, 50);
        Color hplColor          = new Color(100, 100, 255, 50);
        Color errorColor        = new Color(100, 255, 100, 50);
        Ellipse2D halCircle     = new Ellipse2D.Double();
        Ellipse2D hplCircle     = new Ellipse2D.Double();
        Ellipse2D errorEllipse  = new Ellipse2D.Double();
        Point2D center          = new Point2D.Double();
        double scale;
        
        /**
         * Constructor
         */
        public RpntErrorMeter(int s) {
            super();
            setLayout(null);
            setOpaque(false);
            
            Dimension size = new Dimension(s, s);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            
            double margin = 2d;
            scale = HAL / (double)s - 2 * margin;
            center.setLocation((double)s / 2d, (double)s / 2d);
        }

        /**
         * Paints the component
         */
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(bgColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Set the affine transformation
            g2.translate(center.getX(), center.getY());
            g2.scale(scale, scale);
            g2.setStroke(new BasicStroke(Math.max(0.5f, 2.0f * (float)scale)));

            // Draw HAL circle
            g2.setColor(halColor);
            halCircle.setFrameFromCenter(0, 0, HAL / 2.0, HAL / 2.0);
            g2.draw(halCircle);

            if (rpntData != null) {
                // Draw HPL circle
                g2.setColor(hplColor);
                hplCircle.setFrameFromCenter(0, 0, rpntData.getHpl() / 2.0, rpntData.getHpl() / 2.0);
                g2.draw(hplCircle);
                
                // Draw error ellipse
                ErrorEllipse el = rpntData.getErrorEllipse();
                g2.rotate(el.getOMBearing());
                g2.setColor(errorColor);
                errorEllipse.setFrameFromCenter(0, 0, el.getMajorAxis() / 2.0, el.getMinorAxis() / 2.0);
                g2.fill(errorEllipse);
            }
        }
     }
}

