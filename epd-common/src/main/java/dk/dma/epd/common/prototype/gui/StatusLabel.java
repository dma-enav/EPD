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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.graphics.Resources;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;

/**
 * Used for displaying the status of services in the bottom panels
 * of the EPD systems.
 */
public class StatusLabel extends JLabel {

    private static final long serialVersionUID = 1L;
    
    private static Map<ComponentStatus.Status, ImageIcon> imageMap = new ConcurrentHashMap<>();    
    static {        
        // Add the different status icons.
        Resources statusRes = EPD.res().folder("images/status");
        imageMap.put(ComponentStatus.Status.OK, statusRes.getCachedImageIcon("OK.png"));
        imageMap.put(ComponentStatus.Status.ERROR, statusRes.getCachedImageIcon("ERROR.png"));
        imageMap.put(ComponentStatus.Status.PARTIAL, statusRes.getCachedImageIcon("PARTIAL.png"));
        imageMap.put(ComponentStatus.Status.UNKNOWN, statusRes.getCachedImageIcon("UNKNOWN.png"));
    }

    /**
     * Constructor
     * @param name The name of this status label.
     */
    public StatusLabel(String name) {
        super(name);
        
        setFont(new Font("tahoma", Font.PLAIN, 9));
        setHorizontalTextPosition(SwingConstants.LEFT);
        
        // Set default icon.
        setIcon(imageMap.get(ComponentStatus.Status.UNKNOWN));
    }
    
    /**
     * Update the status.
     * @param statusComponent The update to update to.
     */
    public void updateStatus(IStatusComponent statusComponent) {
        
        ComponentStatus componentStatus = statusComponent.getStatus();
        setIcon(imageMap.get(componentStatus.getStatus()));
        setToolTipText(componentStatus.getShortStatusText());
    }
}
