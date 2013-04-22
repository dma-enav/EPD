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
package dk.dma.epd.shore.gui.utils;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.route.RouteManagerDialog;
import dk.dma.epd.shore.gui.route.SendVoyageDialog;
import dk.dma.epd.shore.gui.views.JMainDesktopPane;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.JSettingsWindow;
import dk.dma.epd.shore.gui.views.NotificationArea;
import dk.dma.epd.shore.gui.views.NotificationCenter;
import dk.dma.epd.shore.gui.views.SendRouteDialog;
import dk.dma.epd.shore.gui.views.StatusArea;
import dk.dma.epd.shore.gui.views.ToolBar;

public class JMainDesktopManager extends DefaultDesktopManager {
    /**
     * Desktopmanager used in controlling windows
     */
    private static final long serialVersionUID = 1L;
    private JMainDesktopPane desktop;
    private HashMap<Integer, JInternalFrame> toFront;
    private ToolBar toolbar;
    private NotificationCenter notCenter;
    private NotificationArea notificationArea;
    private StatusArea statusArea;
    private JSettingsWindow settings;
    private RouteManagerDialog routeManager;
    private SendRouteDialog routeDialog;
    private SendVoyageDialog sendVoyageDialog;
    
    /**
     * Constructor for desktopmanager
     * @param desktop
     */
    public JMainDesktopManager(JMainDesktopPane desktop) {
        this.desktop = desktop;
        toFront = new HashMap<Integer, JInternalFrame>();
    }

    /**
     * Activate a frame and handle the ordering
     */
    public void activateFrame(JInternalFrame f) {

        if (f instanceof JMapFrame) {

            if (EPDShore.getMainFrame() != null){
            EPDShore.getMainFrame().setActiveMapWindow((JMapFrame) f);
            }
            
            if (toFront.size() == 0) {
                super.activateFrame(f);
            } else {
                if (toFront.containsKey(((JMapFrame) f).getId())) {
                    super.activateFrame(f);
                } else {
                    super.activateFrame(f);
                    Iterator<Map.Entry<Integer, JInternalFrame>> it = toFront.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Integer, JInternalFrame> pairs = it.next();
                        super.activateFrame(pairs.getValue());
                    }
                }
            }

        }
        super.activateFrame(statusArea);
        super.activateFrame(notificationArea);
        super.activateFrame(toolbar);
        super.activateFrame(notCenter);
        super.activateFrame(settings);
        super.activateFrame(routeManager);
        super.activateFrame(routeDialog);
        super.activateFrame(sendVoyageDialog);
    }
    
    public void clearToFront(){
        toFront.clear();
    }

    /**
     * Set an internalframe to be infront
     * @param id
     * @param f
     */
    public void addToFront(int id, JInternalFrame f) {
        if (toFront.containsKey(id)) {
            toFront.remove(id);
        } else {
            toFront.put(id, f);
        }
    }

    /**
     * Dragging ended of component
     */
    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
        resizeDesktop();
    }

    /**
     * Resizing ended of component
     */
    public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
        resizeDesktop();
    }

    /**
     * return the scrollPane
     * @return
     */
    private JScrollPane getScrollPane() {
        if (desktop.getParent() instanceof JViewport) {
            JViewport viewPort = (JViewport) desktop.getParent();
            if (viewPort.getParent() instanceof JScrollPane) {
                return (JScrollPane) viewPort.getParent();
            }
        }
        return null;
    }

    /**
     * Get scrollPane insets
     * @return
     */
    private Insets getScrollPaneInsets() {
        JScrollPane scrollPane = getScrollPane();
        if (scrollPane == null) {
            return new Insets(0, 0, 0, 0);
        } else {
            return getScrollPane().getBorder().getBorderInsets(scrollPane);
        }
    }

    /**
     * Resize desktop
     */
    public void resizeDesktop() {
        int x = 0;
        int y = 0;
        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {
            JInternalFrame[] allFrames = desktop.getAllFrames();
            for (int i = 0; i < allFrames.length; i++) {
                if (allFrames[i].getX() + allFrames[i].getWidth() > x) {
                    x = allFrames[i].getX() + allFrames[i].getWidth();
                }
                if (allFrames[i].getY() + allFrames[i].getHeight() > y) {
                    y = allFrames[i].getY() + allFrames[i].getHeight();
                }
            }
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
                        - scrollInsets.bottom);
            }

            if (x <= d.getWidth()) {
                x = ((int) d.getWidth()) - 20;
            }
            if (y <= d.getHeight()) {
                y = ((int) d.getHeight()) - 20;
            }
            desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    /**
     * set normal size
     */
    public void setNormalSize() {
        JScrollPane scrollPane = getScrollPane();
        int x = 0;
        int y = 0;
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right, d.getHeight() - scrollInsets.top
                        - scrollInsets.bottom);
            }

            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            desktop.setAllSize(x, y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    /**
     * Set notification center
     * @param notCenter
     */
    public void setNotCenter(NotificationCenter notCenter) {
        this.notCenter = notCenter;
    }
    
    /**
     * Set Settings Window
     * @param notCenter
     */
    public void setSettings(JSettingsWindow settings) {
        this.settings = settings;
    }
    
    /**
     * Set RouteManager Window
     * @param notCenter
     */
    public void setRouteManager(RouteManagerDialog routeManager) {
        this.routeManager = routeManager;
    }

    /**
     * Set RouteExchange Dialog
     * @param notCenter
     */
    public void setRouteExchangeDialog(SendRouteDialog routeDialog) {
        this.routeDialog = routeDialog;
    }

    /**
     * Set Voyage Send Dialog
     * @param notCenter
     */
    public void setSendVoyageDialog(SendVoyageDialog sendVoyageDialog) {
        this.sendVoyageDialog = sendVoyageDialog;
    }
    
    /**
     * Set notification area
     * @param notificationArea
     */
    public void setNotificationArea(NotificationArea notificationArea) {
        this.notificationArea = notificationArea;
    }

    /**
     * Set status area
     * @param statusArea
     */
    public void setStatusArea(StatusArea statusArea) {
        this.statusArea = statusArea;
    }

    /**
     * Set toolbar
     * @param toolbar
     */
    public void setToolbar(ToolBar toolbar) {
        this.toolbar = toolbar;
    }
}
