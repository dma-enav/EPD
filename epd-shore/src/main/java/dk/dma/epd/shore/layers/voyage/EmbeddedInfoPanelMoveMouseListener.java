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
public class EmbeddedInfoPanelMoveMouseListener implements MouseListener, MouseMotionListener {
    JPanel target;
    JInternalFrame parent;
    Point start_drag;
    Point start_loc;

    /**
     * Constructor for setting of the listener
     * @param toolBar     reference to the frame which will be dragged
     * @param frame        reference to the mainframe
     */
    public EmbeddedInfoPanelMoveMouseListener(JPanel toolBar, JInternalFrame parent) {
        this.target = toolBar;
        this.parent = parent;
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
            
            
            if (parent.getSize().getWidth() > new_location.getX() + frame.getWidth()){
//                System.out.println("OK width");
            }else{
                return;
            }
            
            
            if (parent.getSize().getHeight() > new_location.getY() + frame.getHeight() ){
//                System.out.println("OK Height");
            }else{
//                System.out.println("ERROR Height");
                return;
            }
            
            
            if (new_location.getX() < 0 || new_location.getY() < 18){
                return;
            }
            
//            if (frame.getWidth())
//            frame.getHeight()
            
//            System.out.println("Size of frame is : " + parent.getSize() + " name " + parent.getName());
//            System.out.println("New position is " + new_location);
//            if (frame.getSize().getWidth())
            
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
