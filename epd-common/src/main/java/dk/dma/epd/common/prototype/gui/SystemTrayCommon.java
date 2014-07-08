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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
                        EPD.getInstance().getAppIcon(), 
                        BufferedImage.TYPE_INT_ARGB, 
                        dim.width, 
                        dim.height);
                
                trayIcon = new TrayIcon(image, title);
                
                // Create a pop-up menu components
                MenuItem aboutItem = new MenuItem("About...");
                aboutItem.addActionListener(EPD.getInstance().getMainFrame().getAboutAction());
                
                MenuItem setupItem = new MenuItem("Setup...");
                setupItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        EPD.getInstance().getMainFrame().openSetupDialog();
                    }});
                
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        EPD.getInstance().closeApp(false);
                    }});
                
                PopupMenu popup = new PopupMenu();
                popup.add(aboutItem);
                popup.add(setupItem);
                popup.addSeparator();
                popup.add(exitItem);
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
        return tray != null;
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

