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

import dk.dma.epd.common.prototype.event.GoForwardMouseListener;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.gui.route.ButtonLabelCommon;
import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;

/**
 * This class is a GUI component of going forward in history.
 * 
 * @author adamduehansen
 *
 */
public class GoForwardButton extends ButtonLabelCommon {
    
    /****************\
    * private fields *
    \****************/
    private static final long serialVersionUID = 1L;
    private HistoryListener historyListener;
    private CommonChartPanel chartPanel;
    private GoBackButton goBackButton;

    /**
     * Constructs a new button with an arrow.
     * 
     * @param icon 
     *          Path to arrow.
     */
    public GoForwardButton() {
        super(new ImageIcon(GoForwardButton.class.getResource("/images/navigation_buttons/go-forward.png")));
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
    public void seGotBackButton(GoBackButton backButton) {
        this.goBackButton = backButton;
    }
    
    public void initMouseListener() {
        this.addMouseListener(new GoForwardMouseListener(this, this.goBackButton, this.historyListener, this.chartPanel));
    }
}
