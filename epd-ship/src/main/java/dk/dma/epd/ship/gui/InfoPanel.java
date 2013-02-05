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
package dk.dma.epd.ship.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Abstract base class for panels to be shown on the map in the glass pane
 */
public abstract class InfoPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JLabel textLabel = new JLabel();

    public InfoPanel() {
        super();
        FlowLayout flowLayout = new FlowLayout();
        setLayout(flowLayout);
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(textLabel);
        setVisible(false);
    }
    
    public void showText(String text) {
        textLabel.setText(text);
        resizeAndShow();
    }
    
    public void resizeAndShow() {
        validate();
        Dimension d = textLabel.getSize(); 
        this.setSize(d.width + 6, d.height + 2);
        setVisible(true);
    }
    
    public void setPos(int x, int y) {
        Rectangle rect = getBounds();
        setBounds(x, y, (int)rect.getWidth(), (int)rect.getHeight());
    }
    
}
