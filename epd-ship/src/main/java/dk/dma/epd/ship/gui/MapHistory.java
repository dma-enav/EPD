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

    private ArrayList<Position> historyOfPos;
    private int posInHistory;
    
    /**
     * Constructor
     */
    public MapHistory() {
        this.historyOfPos = new ArrayList<Position>();
        this.posInHistory = -1;
    }
    
    /**
     * Adds an element to the history
     * 
     * @param newHistory: new element to be added in the history
     */
    protected void addPositionToHistory(Position newHistory) {
       // TODO:

        if (newHistory != null) {
            // Add the element to the array of positions ..
            try {
                // .. if the element is not equal to the latest element in the history.
                if (!newHistory.equals(this.getLatestElementOfHistory())) {
                    this.historyOfPos.add(newHistory);
                    this.posInHistory++;
                }
            }
            
            // If the element is the first element in the history, add it and point the position to this element.
            catch (IndexOutOfBoundsException e) {
                System.out.println(e.getMessage()+": added position to index 0");
                this.historyOfPos.add(newHistory);
                this.posInHistory++;
            }
        }
    }
    
    /**
     * Get the latest element of the historie.
     * 
     * @return the latest element in the history.
     */
    protected Position getLatestElementOfHistory() {
        return this.historyOfPos.get(posInHistory);
    }
    
    /**
     * Go one step back in history.
     * 
     * @return The position of one step back in the history.
     */
    protected Position goBack() {
        // If the position in history is not the last element, decrease the index position.
        try {
            if (this.posInHistory != 0) {
                this.posInHistory--;
            } else {
                System.out.println("Cant go further back.");
            }
            
            return this.historyOfPos.get(posInHistory);
        }
        
        
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage()+": No elements in the history");
            return null; 
        }
    }
    
    /**
     * Go forward in the history.
     * 
     * @return The position of one step forward in the history.
     */
    protected Position goForward() {
        // If the position in history is not empty or the index has not reach its end, 
        // increase the position to get the next element in history.
        if (this.posInHistory != -1 &&
                this.posInHistory != this.historyOfPos.size()-1) {
            this.posInHistory++;
        } else {
            System.out.println("Cant go further ahead.");
        }
        
        return this.historyOfPos.get(posInHistory);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MapHistory [historyOfPos=" + historyOfPos.toString() + ", posOfHistory=" + posInHistory + "]";
    }
}
