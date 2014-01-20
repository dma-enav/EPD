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
package dk.dma.epd.ship.gui;

import java.util.ArrayList;

import dk.dma.enav.model.geometry.Position;

public class MapHistory {

    private ArrayList<Position> historyOfPositions;
    private int pointerInHistory;

    /**
     * Constructor
     */
    public MapHistory() {
        this.historyOfPositions = new ArrayList<Position>();
        this.pointerInHistory = -1; // No elements in the history.
    }

    /**
     * Adds an element to the list of Position objects. If the element is not added to the highest element in
     * history, the history will be reset from the point of entry to the highest element.
     * 
     * @param newHistory
     *            new element to be added in the history
     * @param increasePointer
     *            if true, will increase the pointer in history.
     */
    protected synchronized boolean addHistoryElement(Position newHistory, boolean increasePointer) {
        // TODO: Add reset functionality for a button?

        // The new history element must not be a null object go into adding the position.
        if (newHistory != null) {
            // If the pointer is at the first element OR the position wished to be added are not equal to the newest element in history. 
            if (this.pointerInHistory == -1 || !this.isSamePositionAsHighest(newHistory)) {
                
                // If the pointer is not at the newest position, reset the history from this pointer to the end.
                if (this.pointerInHistory != this.historyOfPositions.size()-1) {
//                    System.out.println("DEBUG: Reseting some history; pointer is: "+this.pointerInHistory);
                    
                    // Remove every element in the history from the position of the pointer to the end history.
                    for(int i = this.historyOfPositions.size()-1; i >= this.pointerInHistory+1; i--) {
//                        System.out.println("DEBUG: Deleting an item at index: "+i+"; "+this.toString());
                        this.historyOfPositions.remove(i);
                    }
                    
                    // Set the newest position to the highest element in the history.
                    this.historyOfPositions.set(this.pointerInHistory, newHistory);
                    // Do not increase the pointer, since the newest element will only be removed again.
                    increasePointer = false;
                    // End.
                    return true;
                }
                
                // Add the new element in the history.
                this.historyOfPositions.add(newHistory);
                
                // Increase the pointer, if told so by the boolean.
                if (increasePointer) {
                    pointerInHistory++;
                }
            }

            return true;
        }
        
        return false;
    }
    
    /**
     * Checks if the position parameter is the same according to the latitude and longitude of the highest element in history.
     * 
     * @param position: the position
     * @return True if the latitude and longitude of the position parameter as strings are equal to the newest element, or false if not. 
     */
    private boolean isSamePositionAsHighest(Position position) {
        
        Position highestElementPosition = this.historyOfPositions.get(this.historyOfPositions.size()-1);
        
        if (highestElementPosition.getLatitudeAsString().equals(position.getLatitudeAsString()) && 
                highestElementPosition.getLongitudeAsString().equals(position.getLongitudeAsString())) {
            return true;
        }
        
        return false;
    }

    /**
     * Get the element which is one index
     * 
     * @return The position of one step back in the history.
     */
    protected Position goOneHistoryElementBack(Position currentPosition) {
        
        this.pointerInHistory--;

        return this.historyOfPositions.get(pointerInHistory);
    }

    /**
     * Go forward in the history.
     * 
     * @return The position of one step forward in the history.
     */
    protected Position goOneHistoryElementForward() {
        
        this.pointerInHistory++;
        
        return this.historyOfPositions.get(pointerInHistory);
    }

    /**
     * Tells if the user contains any elements of history.
     * 
     * @return True if the history contains any elements and false if not.
     */
    protected boolean containsElements() {
        return this.historyOfPositions.size() > 0;
    }
    
    /**
     * 
     * @return True if the pointer is at the highest element in the history, or false if not.
     */
    protected boolean isAtHighestElement() {
        return this.pointerInHistory == this.historyOfPositions.size()-1;
    }
    
    /**
     * 
     * @return True if the pointer is at the lowest element in the history, or false if not.
     */
    protected boolean isAtLowestElement() {
        return this.pointerInHistory == 0;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MapHistory [historyOfPositions=" + historyOfPositions + ", pointerInHistory=" + pointerInHistory + "]";
    }
}
