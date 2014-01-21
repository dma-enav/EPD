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

public class HistoryListener implements ProjectionListener {

    private String command;
    public static final String DRAGGED = "dragged";
    public static final String CENTERED = "centered";
    public static final String SCALED = "scaled";
    
    private double positionX;
    private double positionY;
    private Position position;
    private float zoomScale;
    private HistoryPosition hpos;    
    
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        
        // TODO: Clean up this mess.
        
        if (this.command.equals(HistoryListener.DRAGGED)) {
            System.out.println("DEBUG:\tSaving to history with dragged mode.");
            
            // Get position of the current position.
            positionX = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getX();
            positionY = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getY();
            position  = Position.create(positionY, positionX);
            zoomScale = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getScale();
            hpos = new HistoryPosition(position, zoomScale);
            EPDShip.getInstance().getMainFrame().mapHistory.addHistoryElement(hpos, true);
            
            EPDShip.getInstance().getMainFrame().getTopPanel().toggleGoBackButton();
            
            this.command = "";
            
        } else if (this.command.equals(HistoryListener.CENTERED)) {
            System.out.println("DEBUG:\tSaving to history with centered mode.");
            
            // Get position of the current position.
            positionX = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getX();
            positionY = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getY();
            position  = Position.create(positionY, positionX);
            zoomScale = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getScale();
            hpos = new HistoryPosition(position, zoomScale);
            EPDShip.getInstance().getMainFrame().mapHistory.addHistoryElement(hpos, true);
            
            EPDShip.getInstance().getMainFrame().getTopPanel().toggleGoBackButton();
            
            this.command = "";
            
        } else if (this.command.equals(HistoryListener.SCALED)) {
            System.out.println("DEBUG:\tSaving to history with scaled mode.");
            
            // Get position of the current position.
            positionX = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getX();
            positionY = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getY();
            position  = Position.create(positionY, positionX);
            zoomScale = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getScale();
            hpos = new HistoryPosition(position, zoomScale);
            EPDShip.getInstance().getMainFrame().mapHistory.addHistoryElement(hpos, true);
            
            EPDShip.getInstance().getMainFrame().getTopPanel().toggleGoBackButton();
            
            this.command = "";
        }
    }
    
    public boolean savingToEmptyHistory() {
        if (EPDShip.getInstance().getMainFrame().mapHistory.getPointerInHistory() == -1) {
            // Get position of the current position.
            positionX = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getX();
            positionY = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getCenter().getY();
            
            // Create a position object from the coordinats.
            position  = Position.create(positionY, positionX);
            
            // Get the zoom scale value.
            zoomScale = EPDShip.getInstance().getMainFrame().getChartPanel().getMap().getScale();
            
            // Create a HistoryPosition object from the position and the zoom scale value.
            hpos = new HistoryPosition(position, zoomScale);
            
            // Add it to the history.
            EPDShip.getInstance().getMainFrame().mapHistory.addHistoryElement(hpos, true);
            
            return true;
        }
        
        return false;
    }
}
