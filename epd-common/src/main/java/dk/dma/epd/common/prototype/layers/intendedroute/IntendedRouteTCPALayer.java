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
package dk.dma.epd.common.prototype.layers.intendedroute;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.model.intendedroute.FilteredIntendedRoute;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IIntendedRouteListener;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;

/**
 * Base layer for displaying intended routes in EPDShip and EPDShore
 */
public class IntendedRouteTCPALayer extends EPDLayerCommon implements IIntendedRouteListener, ProjectionListener {

    private static final long serialVersionUID = 1L;

    /**
     * Map from MMSI to intended route graphic.
     */
    protected ConcurrentHashMap<Long, IntendedRouteGraphic> intendedRoutes = new ConcurrentHashMap<>();

    protected IntendedRouteInfoPanel intendedRouteInfoPanel = new IntendedRouteInfoPanel();

    private ChartPanelCommon chartPanel;
    private AisHandlerCommon aisHandler;
    private IntendedRouteHandlerCommon intendedRouteHandler;
    private OMCircle dummyCircle = new OMCircle();

    /**
     * Constructor
     */
    public IntendedRouteTCPALayer() {
        super();

        // Automatically add info panels
        // registerInfoPanel(intendedRouteInfoPanel, IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);

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

    }

    /**
     * Called periodically by the timer
     */
    @Override
    protected void timerAction() {

        graphics.clear();

        for (FilteredIntendedRoute filteredIntendedRoute : intendedRouteHandler.getFilteredIntendedRoutes().values()) {
            // intendedRouteGraphic.updateIntendedRoute();
            //
            for (int i = 0; i < filteredIntendedRoute.getFilterMessages().size(); i++) {
                graphics.add(new IntendedRouteTCPAGraphic(filteredIntendedRoute.getFilterMessages().get(i).getPosition1(),
                        filteredIntendedRoute.getFilterMessages().get(i).getPosition2(), 1));

            }
        }

        doPrepare();
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

        if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
            // register as listener for AIS messages
        } else if (obj instanceof IntendedRouteHandlerCommon) {
            intendedRouteHandler = (IntendedRouteHandlerCommon) obj;
            // register as listener for intended routes
            intendedRouteHandler.addListener(this);
            // Loads the existing intended routes

        } else if (obj instanceof ChartPanelCommon) {
            this.chartPanel = (ChartPanelCommon) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof IntendedRouteWpCircle) {
            intendedRouteInfoPanel.showWpInfo((IntendedRouteWpCircle) newClosest);
        } else {
            // lets user see ETA continually along route leg
            Point2D worldLocation = chartPanel.getMap().getProjection().inverse(evt.getPoint());
            intendedRouteInfoPanel.showLegInfo((IntendedRouteLegGraphic) newClosest, worldLocation);
            closest = dummyCircle;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {
        if (clickedGraphics instanceof IntendedRouteWpCircle) {

            IntendedRouteWpCircle wpCircle = (IntendedRouteWpCircle) clickedGraphics;
            mapMenu.intendedRouteMenu(wpCircle.getIntendedRouteGraphic());

        } else if (clickedGraphics instanceof IntendedRouteLegGraphic) {

            IntendedRouteLegGraphic wpLeg = (IntendedRouteLegGraphic) clickedGraphics;
            mapMenu.intendedRouteMenu(wpLeg.getIntendedRouteGraphic());
        }
    }

}
