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
package dk.dma.epd.shore.layers.voyage;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import dk.dma.epd.shore.EPDShore;

/**
 * Class for setting up a component to use for dragging the frame
 */
public class VoyageInfoPlanMoveMouseListener implements MouseListener, MouseMotionListener {
    JPanel target;
    JInternalFrame frame;
    Point start_drag;
    Point start_loc;

    /**
     * Constructor for setting of the listener
     * @param toolBar     reference to the frame which will be dragged
     * @param frame        reference to the mainframe
     */
    public VoyageInfoPlanMoveMouseListener(JPanel toolBar, JInternalFrame frame) {
        this.target = toolBar;
        this.frame = frame;
    }
    /**
     * Function for getting the current mouse location
     * @param e
     * @return location location of the mouse
     */
    Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.target.getLocationOnScreen();
        Point location = new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
        return location;
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Function for dragging the frame accordingly to the mouse drag
     */
    public void mouseDragged(MouseEvent e) {

        // This should be tested in multiple OS or be avoided.
        //int offset_x = 16;
        //int offset_y = 59;

        //int frameWidth = frame.getSize().width;
        //int frameHeight = frame.getSize().height;

        Point current = this.getScreenLocation(e);

        if(current != null && start_drag != null) {

            Point offset = new Point(
                    (int) current.getX() - (int) start_drag.getX(),
                    (int) current.getY() - (int) start_drag.getY());
            
            JPanel frame = target;

            int newX = (int) (this.start_loc.getX() + offset.getX());
            int newY = (int) (this.start_loc.getY() + offset.getY());

            /*
            if(newX < 0) newX = 0;
            if((newX + target.getSize().width + offset_x) > frameWidth) newX = frameWidth - target.getSize().width - offset_x;
            if(newY < 0) newY = 0;
            if((newY + target.getSize().height + offset_y) > frameHeight) newY = frameHeight - target.getSize().height - offset_y;
            */

            Point new_location = new Point(newX, newY);
            frame.setLocation(new_location);

            if (EPDShore.getInstance().getMainFrame() != null){
            EPDShore.getInstance().getMainFrame().getDesktop().getManager().resizeDesktop();
            }
        }

    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Function for saving the initial start position when mouse is being dragged
     */
    public void mousePressed(MouseEvent e) {
        this.start_drag = this.getScreenLocation(e);
        this.start_loc = target.getLocation();
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
