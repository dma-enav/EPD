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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;

/**
 * Wraps access to the system tray
 */
public class SystemTrayCommon extends MapHandlerChild {

    private static final Logger LOG = LoggerFactory.getLogger(SystemTrayCommon.class);
    
    private TrayIcon trayIcon;
    private SystemTray tray;
    
    /**
     * Constructor
     */
    public SystemTrayCommon() {
        super();
     
        initSystemTray();
    }
    
    /**
     * Initializes the system tray
     */
    private void initSystemTray() {
        if (SystemTray.isSupported()) {
            try { 
                tray = SystemTray.getSystemTray();
                Dimension dim = tray.getTrayIconSize();
                String title = EPD.getInstance().getMainFrame().getTitle();
                Image image = GraphicsUtil.resizeImage(
                        EPD.getInstance().getMainFrame().getAppIcon(), 
                        BufferedImage.TYPE_INT_ARGB, 
                        dim.width, 
                        dim.height);
                
                trayIcon = new TrayIcon(image, title);
                
                // Create a pop-up menu components
                MenuItem aboutItem = new MenuItem("About");
                PopupMenu popup = new PopupMenu();
                popup.add(aboutItem);
                trayIcon.setPopupMenu(popup);
                
                tray.add(trayIcon);
                LOG.info("Added system tray");
            } catch (AWTException e) {
                tray = null;
                LOG.error("TrayIcon could not be added.", e);
            }
        }
    }
    
    /**
     * Returns if the system tray is supported or not
     * @return if the system tray is supported or not
     */
    public boolean isSupported() {
        return (tray != null);
    }

    /**
     * Display the given message in the system tray
     * 
     * @param title the title of the message
     * @param message the message 
     * @param type the type of message
     */
    public void displayMessage(String title, String message, MessageType type) {
        if (isSupported()) {
            trayIcon.displayMessage(title, message, type);
        }
    }
    
    /**
     * Removes the tray icon
     */
    public void shutdown() {
        if (isSupported()) {
            tray.remove(trayIcon);
            tray = null;
        }
    }
}

