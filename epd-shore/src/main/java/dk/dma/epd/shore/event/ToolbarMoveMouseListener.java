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
package dk.dma.epd.shore.event;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JInternalFrame;

import dk.dma.epd.common.prototype.event.mouse.AbstractCoordMouseMode;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.MainFrame;

/**
 * Class for setting up a component to use for dragging the frame
 */
public class ToolbarMoveMouseListener extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;
    JInternalFrame target;
    MainFrame frame;
    Point start_drag;
    Point start_loc;

    /**
     * Constructor for setting of the listener
     * @param toolBar     reference to the frame which will be dragged
     * @param frame        reference to the mainframe
     */
    public ToolbarMoveMouseListener(JInternalFrame toolBar, MainFrame frame) {
        super("", false);
        this.target = toolBar;
        this.frame = frame;
    }
    /**
     * Function for getting the current mouse location
     * @param e
     * @return location location of the mouse
     */
    private Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.target.getLocationOnScreen();
        Point location = new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
        return location;
    }

    /**
     * Function for dragging the frame accordingly to the mouse drag
     */
    public void mouseDragged(MouseEvent e) {

        Point current = this.getScreenLocation(e);

        if(current != null && start_drag != null) {

            Point offset = new Point(
                    (int) current.getX() - (int) start_drag.getX(),
                    (int) current.getY() - (int) start_drag.getY());
            JInternalFrame frame = target;

            int newX = (int) (this.start_loc.getX() + offset.getX());
            int newY = (int) (this.start_loc.getY() + offset.getY());

            Point new_location = new Point(newX, newY);
            frame.setLocation(new_location);

            if (EPDShore.getInstance().getMainFrame() != null){
                EPDShore.getInstance().getMainFrame().getDesktop().getManager().resizeDesktop();
            }
        }
    }

    /**
     * Function for saving the initial start position when mouse is being dragged
     */
    public void mousePressed(MouseEvent e) {
        this.start_drag = this.getScreenLocation(e);
        this.start_loc = target.getLocation();
    }
}
