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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentHashMap;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.service.IIntendedRouteListener;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;

/**
 * Base layer for displaying intended routes in EPDShip and EPDShore
 */
public class IntendedRouteCPALayer extends EPDLayerCommon implements IIntendedRouteListener, ProjectionListener,
        IRoutesUpdateListener {

    private static final long serialVersionUID = 1L;

    /**
     * Map from MMSI to intended route graphic.
     */
    protected ConcurrentHashMap<Long, IntendedRouteGraphic> intendedRoutes = new ConcurrentHashMap<>();

    protected IntendedRouteCPAInfoPanel tcpaInfoPanel = new IntendedRouteCPAInfoPanel();

    private IntendedRouteHandlerCommon intendedRouteHandler;

    /**
     * Constructor
     */
    public IntendedRouteCPALayer() {
        super();

        // Automatically add info panels
        registerInfoPanel(tcpaInfoPanel, IntendedRouteCPAGraphic.class);

        // Register the classes the will trigger the map menu
        // registerMapMenuClasses(IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);

        // Starts the repaint timer, which runs every minute
        // The initial delay is 100ms and is used to batch up repaints()
        startTimer(100, 60 * 1000);
    }

    /**
     * Called when an intended route event has occured
     */
    @Override
    public void intendedRouteEvent(IntendedRoute intendedRoute) {
        repaintTCPAs();
    }

    private void repaintTCPAs() {

        synchronized (graphics) {
            graphics.clear();

            for (FilteredIntendedRoute filteredIntendedRoute : intendedRouteHandler.getFilteredIntendedRoutes().values()) {
                IntendedRouteFilterMessage minDistMessage = filteredIntendedRoute.getMinimumDistanceMessage();
                boolean acknowledged = filteredIntendedRoute.isNotificationAcknowledged();
                for (IntendedRouteFilterMessage message : filteredIntendedRoute.getFilterMessages()) {

                    if (!message.isNotificationOnly() && message.routesVisible()) {
                        graphics.add(new IntendedRouteCPAGraphic(message, message == minDistMessage, acknowledged));
                    }

                }
            }
        }

        doPrepare();
    }

    /**
     * Called periodically by the timer
     */
    @Override
    protected void timerAction() {
        repaintTCPAs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent pe) {
        super.projectionChanged(pe);
    }

    /**
     * {@inheritDoc}
     */
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (obj instanceof IntendedRouteHandlerCommon) {
            intendedRouteHandler = (IntendedRouteHandlerCommon) obj;
            // register as listener for intended routes
            intendedRouteHandler.addListener(this);
            // Loads the existing intended routes

        }

        else if (obj instanceof RouteManagerCommon) {
            ((RouteManagerCommon) obj).addListener(this);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof IntendedRouteCPAGraphic) {

            IntendedRouteCPAGraphic cpaGraphics = (IntendedRouteCPAGraphic) newClosest;

            tcpaInfoPanel.showWpInfo(cpaGraphics);

        }
        return true;
    }

    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        repaintTCPAs();
    }

}
