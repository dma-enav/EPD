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
