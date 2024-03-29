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
package dk.dma.epd.common.prototype.event;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

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

    
    private boolean shouldSave;
    private double positionX;     // The x-axis of the position.
    private double positionY;     // The y-axis of the position.
    private Position position;    // The Position object created from positionX and positionY. 
    private float zoomScale;      // The zoom scale of the position.
    private HistoryPosition hpos; // The HistoryPosition object created from position and zoomScale.
    private ChartPanelCommon chartPanel;
    private HistoryList historyList;
    private HistoryNavigationPanelInterface navigationPanel;
    
    public HistoryListener(ChartPanelCommon chartPanel) {
        this.chartPanel  = chartPanel;
        this.historyList = new HistoryList();
        this.setShouldSave(false);
    }
    
    public void setNavigationPanel(HistoryNavigationPanelInterface navigationPanel) {
        this.navigationPanel = navigationPanel;
        
        // Go back properties.
        this.navigationPanel.getGoBackButton().setHistoryListener(this);
        this.navigationPanel.getGoBackButton().setChartPanel(chartPanel);
        this.navigationPanel.getGoBackButton().setGoForwardButton(navigationPanel.getGoForwardButton());
        this.navigationPanel.getGoBackButton().initMouseListener();
        
        // Go Forward properties.
        this.navigationPanel.getGoForwardButton().setHistoryListener(this);
        this.navigationPanel.getGoForwardButton().setChartPanel(chartPanel);
        this.navigationPanel.getGoForwardButton().seGotBackButton(navigationPanel.getGoBackButton());
        this.navigationPanel.getGoForwardButton().initMouseListener();
    }

    /*
     * (non-Javadoc)
     * @see com.bbn.openmap.event.ProjectionListener#projectionChanged(com.bbn.openmap.event.ProjectionEvent)
     */
    @Override
    public void projectionChanged(ProjectionEvent e) {

        if (this.getShouldSave()) {
            addHistoryPosition();
            setShouldSave(false);
        }
    }

    private void addHistoryPosition() {
        // Get x and y coordinates of the center of the current view of map.
        this.positionX = this.chartPanel.getMap().getCenter().getX();
        this.positionY = this.chartPanel.getMap().getCenter().getY();
        
        // Create the position object from the x and y coordinates.
        this.position = Position.create(positionY, positionX);
        
        // Get the zoom scale of the current view
        this.zoomScale = this.chartPanel.getMap().getScale();
        
        // Create a HistoryPosition object which can store the position and zoom scale.
        this.hpos = new HistoryPosition(this.position, this.zoomScale);
        
        // Add the object to the history listener, and make it increase the pointer.
        this.historyList.addHistoryElement(hpos, true);
        
        this.navigationPanel.getGoBackButton().setEnabled(true);
        
        if (this.isAtHighestElement()) {
            
            this.navigationPanel.getGoForwardButton().setEnabled(false);
        }
    }
    
    /**
     * Saves the current position before moving to a new one.
     * This is used when the very first position move is used.
     */
    public void saveToHistoryBeforeMoving() {
        if (historyList.getPointerInHistory() == -1) {
            addHistoryPosition();
        } else {
            addHistoryPosition();
        }
    }
    
    /**
     * 
     * @return Returns the element one position back.
     */
    public HistoryPosition goOneElementBack() {
        if (historyList.isAtLowestElement() || 
                historyList.getPointerInHistory() == -1) {
            return null;
        }
         
        return this.historyList.goOneHistoryElementBack();
    }

    /**
     * 
     * @return Returns the element on position forward.
     */
    public HistoryPosition goOneElementForward() {
        if (historyList.isAtHighestElement() ||
                historyList.getPointerInHistory() == -1) {
            
            if (historyList.isAtHighestElement()) {
                this.navigationPanel.getGoForwardButton().setEnabled(false);
            }
            
            return null;
        }
        
        return this.historyList.goOneHistoryElementForward();            
    }
    
    public boolean isAtHighestElement() {
        return this.historyList.isAtHighestElement();
    }
    
    public boolean isAtLowestElement() {
        return this.historyList.isAtLowestElement();
    }

    public boolean containsElements() {
        return this.historyList.containsElements();
    }

    public boolean getShouldSave() {
        return shouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }
}
