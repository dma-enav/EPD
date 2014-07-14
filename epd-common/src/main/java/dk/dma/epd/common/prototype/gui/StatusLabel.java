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
package dk.dma.epd.common.prototype.gui;

import java.awt.Font;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
    public void updateStatus(final IStatusComponent statusComponent) {
        SwingUtilities.invokeLater(new Runnable() {            
            @Override public void run() {
                ComponentStatus componentStatus = statusComponent.getStatus();
                setIcon(imageMap.get(componentStatus.getStatus()));
                setToolTipText(componentStatus.getShortStatusText());
            }
        });
    }
}
