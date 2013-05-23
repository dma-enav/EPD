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

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.ship.EPDShip;

/**
 * A status label with status indication icon 
 */
public class StatusLabel extends JLabel {
    
    private static final long serialVersionUID = 1L;    
    private static Map<ComponentStatus.Status, ImageIcon> imageMap = new HashMap<>();    
    static {        
        imageMap.put(ComponentStatus.Status.OK, new ImageIcon(EPDShip.class.getResource("/images/status/OK.png")));
        imageMap.put(ComponentStatus.Status.ERROR, new ImageIcon(EPDShip.class.getResource("/images/status/ERROR.png")));
        imageMap.put(ComponentStatus.Status.PARTIAL, new ImageIcon(EPDShip.class.getResource("/images/status/PARTIAL.png")));
        imageMap.put(ComponentStatus.Status.UNKNOWN, new ImageIcon(EPDShip.class.getResource("/images/status/UNKNOWN.png")));
    }
    
    private static final Font FONT = new Font("Tahoma", Font.PLAIN, 9);
    
    public StatusLabel(String name) {        
        super(name);
        setFont(FONT);
        setHorizontalTextPosition(SwingConstants.LEFT);
        setIcon(imageMap.get(ComponentStatus.Status.UNKNOWN));
    }
    
    public void updateStatus(IStatusComponent statusComponent) {
        ComponentStatus componentStatus = statusComponent.getStatus();
        setIcon(imageMap.get(componentStatus.getStatus()));
        String shortStatusText = componentStatus.getShortStatusText();
        setToolTipText(shortStatusText);
    }
    
}
