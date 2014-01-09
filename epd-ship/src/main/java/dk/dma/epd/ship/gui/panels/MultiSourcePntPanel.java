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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
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
public class MultiSourcePntPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String NA = "N/A";

    ResilientPntData rpntData;
    
    JLabel lblPntSource = new JLabel();
    JLabel lblJamming = new JLabel();
    JLabel lblHpl = new JLabel();
    JLabel lblErrorMajorAxis = new JLabel();
    JLabel lblErrorMinorAxis = new JLabel();
    JLabel lblErrorBearing = new JLabel();
    
    /**
     * Constructor
     */
    public MultiSourcePntPanel() {
        super(new GridBagLayout());
        
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
    }
    
    /**
     * Sets the default panel font, "Segoe UI", on the label
     * along with the horizontal alignment of the label
     * @param label the label to adjust
     * @param alignment the label alignment
     * @param size the font size of the text
     * @param style the style of the font
     * @return the updated label
     */
    private <T extends JLabel> T adjust(T label, int alignment, int size, int style) {
        label.setFont(new Font("Segoe UI", style, size));
        label.setHorizontalAlignment(alignment);
        return label;
    }

    /**
     * Sets the default panel font on the component
     * @param label the label to adjust
     * @param alignment the label alignment
     * @return the updated label
     */
    private <T extends JLabel> T adjust(T label, int alignment) {
        return adjust(label, alignment, 12, Font.PLAIN);
    }
}
