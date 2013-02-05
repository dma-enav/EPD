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
package dk.dma.epd.ship.gui.Panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 * Scale panel in sensor panel
 */
public class ScalePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private final JLabel scaleLabel = new JLabel("Scale N/A");
    private final JLabel timeLabel = new JLabel("N/A");
    
    public ScalePanel(){
        super();
        setBorder(new LineBorder(Color.GRAY));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{10, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbc_timeLabel = new GridBagConstraints();
        gbc_timeLabel.anchor = GridBagConstraints.NORTH;
        gbc_timeLabel.insets = new Insets(0, 0, 5, 0);
        gbc_timeLabel.gridx = 0;
        gbc_timeLabel.gridy = 0;
        add(timeLabel, gbc_timeLabel);
        scaleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scaleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_scaleLabel = new GridBagConstraints();
        gbc_scaleLabel.anchor = GridBagConstraints.NORTH;
        gbc_scaleLabel.gridx = 0;
        gbc_scaleLabel.gridy = 1;
        add(scaleLabel, gbc_scaleLabel);
    }
    
    public JLabel getTimeLabel() {
        return timeLabel;
    }
    
    public JLabel getScaleLabel() {
        return scaleLabel;
    }
    
}
