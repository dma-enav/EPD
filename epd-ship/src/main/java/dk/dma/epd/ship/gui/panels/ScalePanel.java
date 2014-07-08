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
