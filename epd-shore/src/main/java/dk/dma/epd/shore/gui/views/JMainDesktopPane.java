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
package dk.dma.epd.shore.gui.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import dk.dma.epd.shore.gui.utils.JMainDesktopManager;

public class JMainDesktopPane extends JDesktopPane {
    /**
     * DesktopPane used for internalframes
     */
    private static final long serialVersionUID = 1L;

    private static int FRAME_OFFSET = 20;

    private JMainDesktopManager manager;
    private MainFrame mainFrame;

    /**
     * Initialize the desktop pane
     * 
     * @param mainFrame
     */
    public JMainDesktopPane(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        manager = new JMainDesktopManager(this);
        setDesktopManager(manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    // public void setBounds(int x, int y, int w, int h) {
    // super.setBounds(x, y, w, h);
    // checkDesktopSize();
    // }

    /**
     * Add a component
     * 
     * @param frame
     *            element to be added
     * @return
     */
    public Component add(JInternalFrame frame) {
        JInternalFrame[] array = getAllFrames();
        Point p;
        int w;
        int h;

        Component retval = super.add(frame);
        // checkDesktopSize();

        if (array.length > 0) {
            p = array[0].getLocation();
            p.x = p.x + FRAME_OFFSET;
            p.y = p.y + FRAME_OFFSET;
        } else {
            p = new Point(0, 0);
        }
        frame.setLocation(p.x, p.y);
        if (frame.isResizable()) {

            w = getWidth() - getWidth() / 3;
            h = getHeight() - getHeight() / 3;
            // System.out.println(getWidth());
            // System.out.println(getHeight());
            if (w < frame.getMinimumSize().getWidth()) {
                w = (int) frame.getMinimumSize().getWidth();
            }
            if (h < frame.getMinimumSize().getHeight()) {
                h = (int) frame.getMinimumSize().getHeight();
            }

            if (w > 700) {
                w = 400;
            }
            if (h > 700) {
                h = 400;
            }

            frame.setSize(w, h);

        }
        moveToFront(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            frame.toBack();
        }

        return retval;
    }

    /**
     * Added a window that comes from a workspace
     * 
     * @param frame
     *            to be added
     * @param workspaceWindow
     *            indicates if it is loaded from a workspace or not
     * @return
     */
    public Component add(JInternalFrame frame, boolean workspaceWindow) {
        Component retval = super.add(frame);
        // checkDesktopSize();

        moveToFront(frame);
        // frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            frame.toBack();
        }
        return retval;
    }

    /**
     * Cascade all internal frames
     */
    public void cascadeFrames() {
        int x = 0;
        int y = 0;        
        
        int jMapFramesCount = mainFrame.getMapWindows().size();

        // manager.setNormalSize();
        int frameHeight = getBounds().height - 5 - jMapFramesCount * FRAME_OFFSET;
        int frameWidth = getBounds().width - 5 - jMapFramesCount * FRAME_OFFSET;
        for (int i = mainFrame.getMapWindows().size() - 1; i >= 0; i--) {
            mainFrame.getMapWindows().get(i).setSize(frameWidth, frameHeight);
            mainFrame.getMapWindows().get(i).setLocation(x, y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
    }

    /**
     * Return the JMainDesktopManager
     * 
     * @return manager
     */
    public JMainDesktopManager getManager() {
        return manager;
    }

    /**
     * Function called when one of its components are closed
     */
    public void remove(Component c) {

        /*
         * This method is also used when a map is being minimized - so checking for window is closed to be sure that the window is
         * not removed from the desktop pane.
         */
        if (c instanceof JMapFrame && ((JMapFrame)c).isClosed()) {
            JMapFrame mapFrame = (JMapFrame)c;
            mainFrame.removeMapWindow(mapFrame);

            // Cleanup of the threads attached to the window - important if adding new layers
            if (mapFrame.getChartPanel().getWmsLayer() != null) {
                mapFrame.getChartPanel().getWmsLayer().stop();
            }
        }

        super.remove(c);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the given dimension.
     */
    public void setAllSize(Dimension d) {
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred) to the given width and height.
     */
    public void setAllSize(int width, int height) {
        setAllSize(new Dimension(width, height));
    }

    /**
     * Tile all internal frames
     */
    public void tileFrames() {
        java.awt.Component[] allFrames = getAllFrames();
        manager.setNormalSize();

        int jMapFramesCount = 0;

        for (Component allFrame : allFrames) {
            if (allFrame instanceof JMapFrame) {
                jMapFramesCount++;
            }
        }

        int frameWidth = getBounds().width / jMapFramesCount;

        int frameHeight = getBounds().height / jMapFramesCount;
        int y = 0;
        int x = 0;

        for (Component allFrame : allFrames) {
            if (allFrame instanceof JMapFrame) {
                // allFrames[i].setSize(getBounds().width, frameHeight);
                allFrame.setSize(frameWidth, getBounds().height);
                allFrame.setLocation(x, 0);
                y = y + frameHeight;
                x = x + frameWidth;
            }
        }
    }

    // private void checkDesktopSize() {
    // if (getParent() != null && isVisible())
    // manager.resizeDesktop();
    // }
}
