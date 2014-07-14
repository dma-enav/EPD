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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.gui.GoBackButton;
import dk.dma.epd.common.prototype.gui.GoForwardButton;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

public class GoBackMouseListener extends MouseAdapter {
    
    /****************\
    * private fields *
    \****************/
    
    private GoBackButton goBackButton;
    private GoForwardButton goForwardButton;
    private HistoryListener historyListener;
    private ChartPanelCommon chartPanel;
    
    public GoBackMouseListener(GoBackButton goBackButton, 
            GoForwardButton goForwardButton, 
            HistoryListener historyListener,
            ChartPanelCommon chartPanel) {
        this.goBackButton = goBackButton;
        this.goForwardButton = goForwardButton;
        this.historyListener = historyListener;
        this.chartPanel = chartPanel;
    }

    /*****************\
    * private methods *
    \*****************/
    
    /**
     * Toggles the button enabled or disabled.
     */
    private void toogleButton() {
        if (this.historyListener.containsElements()) {
            this.goForwardButton.setEnabled(true);
        }
        
        if (this.historyListener.isAtLowestElement()) {
            this.goBackButton.setEnabled(false);
        }
    }

    /****************\
    * public methods *
    \****************/
    
    @Override
    public void mouseClicked(MouseEvent e) {
        // Get the HistoryPosition one element back in history.
        HistoryPosition hpos = historyListener.goOneElementBack();
        
        // If the HistoryPosition is not null, go to the position and zoom scale.
        if (hpos != null) {
            chartPanel.goToPosition(hpos.getPosition());
            chartPanel.getMap().setScale(hpos.getZoomScale());
            this.toogleButton(); // Toggle the button.
        }
    }
    
}
