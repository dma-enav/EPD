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
package dk.dma.epd.common.prototype.gui.menuitems;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

/**
 * Opens the route properties dialog
 */
public class RouteProperties extends RouteMenuItem {
    
    private static final long serialVersionUID = 1L;

    ChartPanelCommon chartPanel;    

    /**
     * Constructor
     * @param text
     */
    public RouteProperties(String text) {
        super();
        setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doAction() {
        RoutePropertiesDialogCommon routePropertiesDialog = 
                new RoutePropertiesDialogCommon(
                        EPD.getInstance().getMainFrame(), 
                        chartPanel,
                        routeIndex);
        routePropertiesDialog.setVisible(true);
    }

    /**
     * Sets the current chart panel
     * @param chartPanel
     */
    public void setChartPanel(ChartPanelCommon chartPanel) {
        this.chartPanel = chartPanel;
    }
}
