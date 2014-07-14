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
package dk.dma.epd.shore.gui.route;

import java.awt.Window;

import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon.RouteChangeListener;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.layers.voyage.VoyageHandlingLayer;

/**
 * Dialog with route properties
 */
public class RoutePropertiesDialog extends RoutePropertiesDialogCommon implements RouteChangeListener {

    private static final long serialVersionUID = 1L;
    VoyageHandlingLayer voyageHandlingLayer;

    public RoutePropertiesDialog(Window parent, ChartPanel chartPanel, int routeId) {
        super(parent, chartPanel, routeId);
        addRouteChangeListener(this);
    }

    public RoutePropertiesDialog(Window mainFrame, ChartPanel chartPanel, Route route,
            VoyageHandlingLayer voyageHandlingLayer) {
        super(mainFrame, chartPanel, route, false);
        this.voyageHandlingLayer = voyageHandlingLayer;
        btnActivate.setVisible(false);
        addRouteChangeListener(this);
    }
    
    public RoutePropertiesDialog(Window mainFrame, ChartPanel chartPanel, Route route) {
        super(mainFrame, chartPanel, route, true);
        btnActivate.setVisible(false);
        addRouteChangeListener(this);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void routeChanged() {
        if (voyageHandlingLayer != null) {
            voyageHandlingLayer.updateVoyages();
        }
    }

}
