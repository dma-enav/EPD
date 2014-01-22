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
package dk.dma.epd.ship.event;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.HistoryPosition;

/**
 * This class is a listener for projection changes and therefore implements
 * the ProjectionListener interface. The listener intention is to listen for
 * three different types projection changes:
 *  - Dragged: When the map projection is changed by dragging the map.
 *  - Centering: When the map projection is changed by centering on the ship.
 *  - Scaled: When the map projection is changed because navigation tool was
 *      used to scale into the map.
 * The class has a static final field for every type of projection change.
 * 
 * When the projection is changed because of one of these three types, a
 * HistoryPosition object is created an saved in the history.
 * 
 * @author adamduehansen
 *
 */
public class HistoryListener implements ProjectionListener {

    public static final String DRAGGED = "dragged";
    public static final String CENTERED = "centered";
    public static final String SCALED = "scaled";
    private final String NO_COMMAND = "no_command"; // init.
    
    private String command;       // The command for the type of projection change.
    private double positionX;     // The x-axis of the position.
    private double positionY;     // The y-axis of the position.
    private Position position;    // The Position object created from positionX and positionY. 
    private float zoomScale;      // The zoom scale of the position.
    private HistoryPosition hpos; // The HistoryPosition object created from position and zoomScale.
    
    /**
     * 
     * @param command: The command which is executed.
     */
    public void setCommand(String command) {
        this.command = command;
    }
    
    /**
     * 
     * @return The executed command.
     */
    public String getCommand() {
        return this.command;
    }

    /*
     * (non-Javadoc)
     * @see com.bbn.openmap.event.ProjectionListener#projectionChanged(com.bbn.openmap.event.ProjectionEvent)
     */
    @Override
    public void projectionChanged(ProjectionEvent e) {
                
        if (this.getCommand().equals(HistoryListener.DRAGGED) || 
                this.getCommand().equals(HistoryListener.CENTERED) || 
                this.getCommand().equals(HistoryListener.SCALED)) {
            
            addElementToHistory();
            
            // Reset command.
            this.setCommand(this.NO_COMMAND);
        }
    }
    
    /**
     * Saves the current position in 
     */
    public void saveToHistoryBeforeMoving() {
        if (EPDShip.getInstance().getMainFrame().mapHistory.getPointerInHistory() == -1) {
            addElementToHistory();
        } else {
            addElementToHistory();
        }

    }
    
    /**
     * Adds the current Position in map and zoomscale to the MapHistory.
     * 
     * @param increaseHistoryPointer
     */
    private void addElementToHistory() {
        
        // Get x and y coordinates of the center of the current view of map.
        positionX = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getX();
        positionY = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getY();
        
        // Create the position object.
        position  = Position.create(positionY, positionX);
        
        // Get the zoom level.
        zoomScale = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getScale();
        
        // Create the HistoryPosition object.
        hpos = new HistoryPosition(position, zoomScale);
        
        // Add the object and toggle the go back button.
        EPDShip.getInstance().getMainFrame().mapHistory.addHistoryElement(hpos, true);
        EPDShip.getInstance().getMainFrame().getTopPanel().toggleGoBackButton();
    }
}
