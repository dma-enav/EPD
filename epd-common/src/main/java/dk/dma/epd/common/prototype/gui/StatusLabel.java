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
package dk.dma.epd.common.prototype.gui;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;

public class StatusLabel extends JLabel {

    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private Map<ComponentStatus.Status, ImageIcon> imageMap;

    /**
     * Constructor
     * @param name The name of this status label.
     */
    public StatusLabel(String name) {
        super(name);
        
        this.setFont(new Font("tahoma", Font.PLAIN, 9));
        this.setHorizontalTextPosition(SwingConstants.LEFT);
        
        // Add the different status icons.
        this.imageMap = new HashMap<ComponentStatus.Status, ImageIcon>();
        this.imageMap.put(ComponentStatus.Status.OK, this.getCachedStatusIcon("OK.png"));
        this.imageMap.put(ComponentStatus.Status.ERROR, this.getCachedStatusIcon("ERROR.png"));
        this.imageMap.put(ComponentStatus.Status.UNKNOWN, this.getCachedStatusIcon("UNKNOWN.png"));
        this.imageMap.put(ComponentStatus.Status.PARTIAL, this.getCachedStatusIcon("PARTIAL.png"));
        
        // Set default icon.
        this.setIcon(imageMap.get(ComponentStatus.Status.UNKNOWN));
    }
    
    /**
     * Get the image icon of the given file.
     * @param filename Name of File
     * @return A new ImageIcon object.
     */
    private ImageIcon getCachedStatusIcon(String filename) {
        
        return new ImageIcon(StatusLabel.class.getResource("/images/status/"+filename));
    }
    
    /**
     * Update the status.
     * @param statusComponent The update to update to.
     */
    public void updateStatus(IStatusComponent statusComponent) {
        
        ComponentStatus componentStatus = statusComponent.getStatus();
        this.setIcon(this.imageMap.get(componentStatus.getStatus()));
        this.setToolTipText(componentStatus.getShortStatusText());
    }
}
