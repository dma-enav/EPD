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
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Base class that may be used by dockable panels.
 * Contains standard functionality for these panels
 */
public class DockablePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor
     */
    public DockablePanel() {
        super();
    }

    /**
     * Constructor
     * 
     * @param layout the layout manager to use
     */
    public DockablePanel(LayoutManager layout) {
        super(layout);
    }
    
    /**
     * Sets the default panel font, "Segoe UI", on the label
     * along with the horizontal alignment of the label
     * 
     * @param label the label to adjust
     * @param alignment the label alignment
     * @param size the font size of the text
     * @param style the style of the font
     * @return the updated label
     */
    protected <T extends JLabel> T adjust(T label, int alignment, int size, int style) {
        label.setFont(new Font("Segoe UI", style, size));
        label.setHorizontalAlignment(alignment);
        return label;
    }

    /**
     * Sets the default panel font on the component
     * 
     * @param label the label to adjust
     * @param alignment the label alignment
     * @return the updated label
     */
    protected <T extends JLabel> T adjust(T label, int alignment) {
        return adjust(label, alignment, 12, Font.PLAIN);
    }
    
}
