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

/**
 * This class works as an history, in which HistoryPosition objects
 * are stored in an ArrayList object. When a HistoryPosition objects
 * is put in the list, a integer will point to this new object.
 * When this class is requested to go back into the history (or forward
 * into the history) the pointer will decrease (or increase) so that
 * it will point to another HistoryPosition object in the history.
 * 
 * @author adamduehansen
 *
 */
public class MapHistory {

    /**
     * Private fields.
     */
    private ArrayList<HistoryPosition> historyOfPositions;
    private int pointerInHistory;

    /**
     * Constructs a new MapHisotory.
     */
    public MapHistory() {
        this.historyOfPositions = new ArrayList<HistoryPosition>();
        this.pointerInHistory   = -1; // No elements in the history.
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
    public synchronized boolean addHistoryElement(HistoryPosition newHistory, boolean increasePointer) {
        // TODO: Add reset functionality for a button?
        
        // The new history element must:
        //  - be initialized (not null) and
        //  - be equal to -1 or not equal to the newest element in history.
        if ((newHistory != null) && 
                (this.pointerInHistory == -1 || 
                !newHistory.equals(this.historyOfPositions.get(this.historyOfPositions.size()-1)))) {
                        
            // If the pointer is not at the newest position, reset the history from the position of the pointer plus 1 to the end.
            if (this.pointerInHistory != this.historyOfPositions.size()-1) {
                this.resetHistory(this.historyOfPositions.size()-1, this.pointerInHistory);
            }
            
            // Add the new element in the history.
            this.historyOfPositions.add(newHistory);
            
            // Increase the pointer, if told so by the boolean.
            if (increasePointer) {
                pointerInHistory++;
            }
            
//            System.out.println("DEBUG:\t"+this.toString());
            return true;
        }
        
        return false;
    }
    
    /**
     * Resets the history from the given start point to end point.
     * 
     * @param startPoint
     *          From what index the history should reset.
     * @param endPoint
     *          From what index the history should reset to.
     */
    public void resetHistory(int startPoint, int endPoint) {
        for(int i = startPoint; i >= endPoint; i--) {
            this.historyOfPositions.remove(i);
        }
        
        this.pointerInHistory--;
    }

    /**
     * Get the element which is one index
     * 
     * @return The position of one step back in the history.
     */
    public HistoryPosition goOneHistoryElementBack() {
        
        this.pointerInHistory--;

//        System.out.println("DEBUG:\t"+this.toString());
        return this.historyOfPositions.get(pointerInHistory);
    }

    /**
     * Go forward in the history.
     * 
     * @return The position of one step forward in the history.
     */
    public HistoryPosition goOneHistoryElementForward() {


        this.pointerInHistory++;
        
//        System.out.println("DEBUG:\t"+this.toString());
        return this.historyOfPositions.get(pointerInHistory);
    }

    /**
     * Tells if the user contains any elements of history.
     * 
     * @return True if the history contains any elements and false if not.
     */
    public boolean containsElements() {
        return this.historyOfPositions.size() > 0;
    }
    
    /**
     * 
     * @return True if the pointer is at the highest element in the history, or false if not.
     */
    public boolean isAtHighestElement() {
        return this.pointerInHistory == this.historyOfPositions.size()-1;
    }
    
    /**
     * 
     * @return True if the pointer is at the lowest element in the history, or false if not.
     */
    public boolean isAtLowestElement() {
        return this.pointerInHistory == 0;
    }
    
    /**
     * 
     * @return The pointer in history.
     */
    public int getPointerInHistory() {
        return this.pointerInHistory;
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
