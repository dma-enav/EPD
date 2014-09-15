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
import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IIntendedRouteListener;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;

/**
 * Base layer for displaying intended routes in EPDShip and EPDShore
 */
public class IntendedRouteLayerCommon extends EPDLayerCommon implements IAisTargetListener, IIntendedRouteListener,
        ProjectionListener {

    private static final long serialVersionUID = 1L;

    /** Refresh the intended route every 30 seconds **/
    private static final int REPAINT_TIME = 30;

    /**
     * Map from MMSI to intended route graphic.
     */
    protected ConcurrentHashMap<Long, IntendedRouteGraphic> intendedRoutes = new ConcurrentHashMap<>();

    protected IntendedRouteInfoPanel intendedRouteInfoPanel = new IntendedRouteInfoPanel();

    protected ChartPanelCommon chartPanel;
    private AisHandlerCommon aisHandler;
    private IntendedRouteHandlerCommon intendedRouteHandler;
    protected OMCircle dummyCircle = new OMCircle();

    private boolean useFilter;

    /**
     * Constructor
     */
    public IntendedRouteLayerCommon() {
        super();
        
        this.useFilter = EPD.getInstance().getSettings().getCloudSettings().isIntendedRouteFilterOn();

        // Automatically add info panels
        registerInfoPanel(intendedRouteInfoPanel, IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);

        // Register the classes the will trigger the map menu
        registerMapMenuClasses(IntendedRouteWpCircle.class, IntendedRouteLegGraphic.class);

        // Starts the repaint timer, which runs every 30 seconds
        // The initial delay is 100ms and is used to batch up repaints()
        startTimer(100, REPAINT_TIME * 1000);
    }

    /**
     * Called when the given AIS target is updated
     * 
     * @param aisTarget
     *            the AIS target that has been updated
     */
    @Override
    public void targetUpdated(AisTarget aisTarget) {
        // Sanity checks
        if (aisHandler == null || intendedRouteHandler == null || !(aisTarget instanceof VesselTarget)) {
            return;
        }

        // Look up the intended route
        IntendedRoute intendedRoute = intendedRouteHandler.getIntendedRoute(aisTarget.getMmsi());
        IntendedRouteGraphic intendedRouteGraphic = intendedRoutes.get(aisTarget.getMmsi());
        if (intendedRoute == null || intendedRouteGraphic == null) {
            return;
        }

        // Update the intended route name and vessel position from the VesselTarget
        VesselTarget vessel = aisHandler.getVesselTarget(intendedRoute.getMmsi());
        if (vessel != null) {
            if (vessel.getStaticData() != null) {
                intendedRouteGraphic.setName(vessel.getStaticData().getTrimmedName());
            }

            // Update the graphics
            intendedRouteGraphic.updateVesselPosition(vessel.getPositionData().getPos());
            doPrepare();

        }
    }

    private void removeIntendedRoute(IntendedRouteGraphic intendedRouteGraphics, long mmsi) {
        synchronized (graphics) {
            graphics.remove(intendedRouteGraphics);
        }
        intendedRoutes.remove(mmsi);

        // Cause imminent repaint
        restartTimer();
    }

    /**
     * Called when an intended route event has occured
     */
    @Override
    public void intendedRouteEvent(IntendedRoute intendedRoute) {
        if (intendedRoute == null) {
            
            //Used when settings are changed - repaint
            loadIntendedRoutes();
            return;
        }

        // No route connected - remove it from graphics
        if (!intendedRoute.hasRoute()) {
            IntendedRouteGraphic intendedRouteGraphic = intendedRoutes.get(intendedRoute.getMmsi());

            // Should always be defined, but better check...
            if (intendedRouteGraphic != null) {
                removeIntendedRoute(intendedRouteGraphic, intendedRoute.getMmsi());
            }
        } else {

            // An update is required
            if (intendedRoutes.containsKey(intendedRoute.getMmsi())) {
                IntendedRouteGraphic intendedRouteGraphic = intendedRoutes.get(intendedRoute.getMmsi());

                // Should always be defined, but better check...
                if (intendedRouteGraphic != null) {

                    // Check for filter - route could have changed and should not be shown
                    if (useFilter && intendedRouteHandler.getFilteredIntendedRoutes().containsKey(intendedRoute.getMmsi())
                            || !useFilter) {
                        // Update the graphics
                        intendedRouteGraphic.updateIntendedRoute(intendedRoute);

                        // Cause imminent repaint
                        restartTimer();

                    } else {
                        removeIntendedRoute(intendedRouteGraphic, intendedRoute.getMmsi());
                    }

                }
            } else {

                // Adding it
                if (useFilter && intendedRouteHandler.getFilteredIntendedRoutes().containsKey(intendedRoute.getMmsi())
                        || !useFilter) {

                    IntendedRouteGraphic intendedRouteGraphic = new IntendedRouteGraphic();

                    // add the new intended route graphic to the set of managed intended route graphics
                    intendedRoutes.put(intendedRoute.getMmsi(), intendedRouteGraphic);
                    synchronized (graphics) {
                        graphics.add(intendedRouteGraphic);
                    }

                    // Update the graphics
                    intendedRouteGraphic.updateIntendedRoute(intendedRoute);
                    intendedRouteGraphic.showArrowHeads(showArrowHeads());

                    // Cause imminent repaint
                    restartTimer();

                }

            }

        }
    }

    /**
     * Called periodically by the timer
     */
    @Override
    protected void timerAction() {
        for (IntendedRouteGraphic intendedRouteGraphic : intendedRoutes.values()) {
            intendedRouteGraphic.updateIntendedRoute();
        }

        doPrepare();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent pe) {
        // Check if we need to display the arrow heads
        if (getProjection() != null) {

            boolean showArrowHeads = showArrowHeads();
            for (IntendedRouteGraphic intendedRouteGraphic : intendedRoutes.values()) {
                intendedRouteGraphic.showArrowHeads(showArrowHeads);
            }
        }
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
            aisHandler.addListener(this);
        } else if (obj instanceof IntendedRouteHandlerCommon) {
            intendedRouteHandler = (IntendedRouteHandlerCommon) obj;
            // register as listener for intended routes
            intendedRouteHandler.addListener(this);
            // Loads the existing intended routes
            loadIntendedRoutes();
        } else if (obj instanceof ChartPanelCommon) {
            this.chartPanel = (ChartPanelCommon) obj;
        }
    }

    /**
     * Upon creating a new layer, we need to load the intended routes held by the intended route handler
     */
    public void loadIntendedRoutes() {
        for (IntendedRoute intendedRoute : intendedRouteHandler.fetchIntendedRoutes()) {
            intendedRouteEvent(intendedRoute);
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

    /**
     * Returns whether or not to show arrow heads on the route legs, which depends on the current projection
     * 
     * @return whether or not to show arrow heads on the route legs
     */
    private boolean showArrowHeads() {
        if (getProjection() != null) {
            return getProjection().getScale() < EPD.getInstance().getSettings().getNavSettings().getShowArrowScale();
        }
        return false;
    }

    /**
     * Used to toggle filter
     * 
     * @param enabled
     */
    public void toggleFilter(boolean enabled) {
        this.useFilter = enabled;

        // Reload all routes
        loadIntendedRoutes();
//        intendedRouteHandler.fireIntendedEvent(null);
    }

    public boolean isUseFilter() {
        return useFilter;
    }

}
