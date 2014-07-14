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
