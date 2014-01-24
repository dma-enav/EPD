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
package dk.dma.epd.common.prototype.gui;

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.event.GoBackMouseListener;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.gui.route.ButtonLabelCommon;
import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;

/**
 * This class is a GUI component of going backwards in history.
 * 
 * @author adamduehansen
 *
 */
public class GoBackButton extends ButtonLabelCommon {

    /****************\
    * private fields *
    \****************/
    private static final long serialVersionUID = 1L;
    private HistoryListener historyListener;    // The history listener associated with this button. 
    private CommonChartPanel chartPanel;        // The chart panel which controls projection.
    private GoForwardButton goForwardButton;    // The opposite button of this: the going forward button.
    
    /**
     * Constructs a new button with an arrow.
     * 
     * @param icon 
     *          Path to arrow.
     */
    public GoBackButton() {
        super(new ImageIcon(GoBackButton.class.getResource("/images/navigation_buttons/go-back.png")));
    }
    
    /****************\
    * public methods *
    \****************/
    
    /**
     * Adds a HistoryListener to the button.
     * 
     * @param historyListener
     */
    public void setHistoryListener(HistoryListener historyListener) {
        this.historyListener = historyListener;
    }
    
    /**
     * Sets the ChartPanel for the button.
     * 
     * @param chartPanel
     */
    public void setChartPanel(CommonChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }
    
    /**
     * Sets the GoForwardButton of the button.
     * 
     * @param goForwardButton
     */
    public void setGoForwardButton(GoForwardButton goForwardButton) {
        this.goForwardButton = goForwardButton;
    }
    
    public void initMouseListener() {
        addMouseListener(new GoBackMouseListener(this, this.goForwardButton, this.historyListener, this.chartPanel));
    }
}
