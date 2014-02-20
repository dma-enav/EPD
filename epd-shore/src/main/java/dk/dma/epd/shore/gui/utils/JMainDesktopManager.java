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
import dk.dma.epd.shore.gui.route.strategic.SendStrategicRouteDialog;
import dk.dma.epd.shore.gui.views.JMainDesktopPane;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.SendRouteDialog;
import dk.dma.epd.shore.gui.views.StatusArea;
import dk.dma.epd.shore.gui.views.ToolBar;
import dk.dma.epd.shore.gui.voct.SRUManagerDialog;

public class JMainDesktopManager extends DefaultDesktopManager {
    /**
     * Desktopmanager used in controlling windows
     */
    private static final long serialVersionUID = 1L;
    private JMainDesktopPane desktop;
    private HashMap<Integer, JInternalFrame> toFront;
    private ToolBar toolbar;
    private StatusArea statusArea;
    private RouteManagerDialog routeManager;
    private SendRouteDialog routeDialog;
    private SendStrategicRouteDialog sendVoyageDialog;
    private SRUManagerDialog sruManagerDialog;

    /**
     * Constructor for desktopmanager
     * 
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

            if (EPDShore.getInstance().getMainFrame() != null) {
                EPDShore.getInstance().getMainFrame().setActiveMapWindow((JMapFrame) f);
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
        super.activateFrame(toolbar);
        super.activateFrame(routeManager);
        super.activateFrame(routeDialog);
        super.activateFrame(sendVoyageDialog);
        super.activateFrame(sruManagerDialog);
    }

    public void clearToFront() {
        toFront.clear();
    }

    /**
     * Set an internalframe to be infront
     * 
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
     * 
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
     * 
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

        // Get the scroll pane of the desktop pane.
        JScrollPane scrollPane = this.getScrollPane();

        // These booleans will be true if a frame has crossed the desktop pane window.
        boolean horizontalCrossed = false;
        boolean verticalCrossed = false;

        // Check for each frame in the desktop panel if it has been
        for (JInternalFrame frame : this.desktop.getAllFrames()) {

            // Variables for distance between right border and bottom.
            int frmHorizontalDistanceFromLeft = (int) (frame.getLocation().x + frame.getSize().getWidth());
            int frmVerticalDistanceFromUpper = (int) (frame.getLocation().y + frame.getSize().getHeight());

            // Set boolean if a frame has crossed out from the rigt
            if ((frame.isVisible()) && frmHorizontalDistanceFromLeft > this.desktop.getSize().getWidth()) {
                horizontalCrossed = true;
            }

            // Set boolean if a frame has crossed out from the bottom.
            if ((frame.isVisible()) && frmVerticalDistanceFromUpper > this.desktop.getSize().getHeight()) {
                verticalCrossed = true;
            }

            // Show scroll bars.
            this.desktop.setAllSize(frmHorizontalDistanceFromLeft, frmVerticalDistanceFromUpper);
            scrollPane.invalidate();
            scrollPane.validate();

            // Stop the loop if a frame has crossed out of the desktop pane.
            if (verticalCrossed || horizontalCrossed) {
                return;
            }
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
     * Set RouteManager Window
     * 
     * @param notCenter
     */
    public void setRouteManager(RouteManagerDialog routeManager) {
        this.routeManager = routeManager;
    }

    /**
     * Set RouteExchange Dialog
     * 
     * @param notCenter
     */
    public void setRouteExchangeDialog(SendRouteDialog routeDialog) {
        this.routeDialog = routeDialog;
    }

    /**
     * Set Voyage Send Dialog
     * 
     * @param notCenter
     */
    public void setSendVoyageDialog(SendStrategicRouteDialog sendVoyageDialog) {
        this.sendVoyageDialog = sendVoyageDialog;
    }

    /**
     * Set status area
     * 
     * @param statusArea
     */
    public void setStatusArea(StatusArea statusArea) {
        this.statusArea = statusArea;
    }

    /**
     * Set toolbar
     * 
     * @param toolbar
     */
    public void setToolbar(ToolBar toolbar) {
        this.toolbar = toolbar;
    }

    public void setSRUManagerDialog(SRUManagerDialog sruManagerDialog) {
        this.sruManagerDialog = sruManagerDialog;

    }
}
