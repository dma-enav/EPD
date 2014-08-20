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
package dk.dma.epd.ship.gui;

import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MouseDelegator;

import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.menuitems.GeneralClearMap;
import dk.dma.epd.common.prototype.gui.menuitems.RouteHide;
import dk.dma.epd.common.prototype.gui.menuitems.SarTargetDetails;
import dk.dma.epd.common.prototype.gui.menuitems.ToggleAisTargetName;
import dk.dma.epd.common.prototype.gui.menuitems.VoyageHandlingLegInsertWaypoint;
import dk.dma.epd.common.prototype.layers.ais.VesselGraphicComponentSelector;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteSuggestionData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.menuitems.AisTargetDetails;
import dk.dma.epd.ship.gui.menuitems.GeneralNewRoute;
import dk.dma.epd.ship.gui.menuitems.MonaLisaRouteRequest;
import dk.dma.epd.ship.gui.menuitems.NogoRequest;
import dk.dma.epd.ship.gui.menuitems.RouteActivateToggle;
import dk.dma.epd.ship.gui.menuitems.RouteEditEndRoute;
import dk.dma.epd.ship.gui.menuitems.SendToSTCC;
import dk.dma.epd.ship.gui.menuitems.RouteSuggestionDetails;
import dk.dma.epd.ship.gui.menuitems.VoyageAppendWaypoint;
import dk.dma.epd.ship.gui.menuitems.VoyageHandlingWaypointDelete;
import dk.dma.epd.ship.layers.ais.AisLayer;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.StrategicRouteHandler;

/**
 * Right click map menu
 */
public class MapMenu extends MapMenuCommon {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MapMenu.class);

    // menu items
    private GeneralClearMap clearMap;
    private GeneralNewRoute newRoute;
    private AisTargetDetails aisTargetDetails;

    private SarTargetDetails sarTargetDetails;
    private ToggleAisTargetName aisTargetLabelToggle;
    private NogoRequest nogoRequest;
    
    private RouteActivateToggle routeActivateToggle;
    private MonaLisaRouteRequest monaLisaRouteRequest;
    private RouteSuggestionDetails routeSuggestionDetails;
    private RouteEditEndRoute routeEditEndRoute;
    private SendToSTCC sendToSTCC;
    private VoyageAppendWaypoint voyageAppendWaypoint;
    private VoyageHandlingWaypointDelete voyageDeleteWaypoint;
    private VoyageHandlingLegInsertWaypoint voyageLegInsertWaypoint;
    private RouteManager routeManager;
    private MainFrame mainFrame;
    private PntHandler gpsHandler;
    private NewRouteContainerLayer newRouteLayer;
    private AisLayer aisLayer;
    private OwnShipHandler ownShipHandler;
    private NogoHandler nogoHandler;
    private MouseDelegator mouseDelegator;
    private StrategicRouteHandler strategicRouteHandler;

    
    // private RouteLayer routeLayer;
    // private VoyageLayer voyageLayer;

    public MapMenu() {
        super();

        // general menu items
        clearMap = new GeneralClearMap("Clear chart");
        clearMap.addActionListener(this);
        newRoute = new GeneralNewRoute("Add new route - Ctrl N");
        newRoute.addActionListener(this);

        nogoRequest = new NogoRequest("Request NoGo area...");
        nogoRequest.addActionListener(this);
        
        // ais menu items
        aisTargetDetails = new AisTargetDetails("Show AIS target details...");
        aisTargetDetails.addActionListener(this);
//        aisTargetLabelToggle = new AisTargetLabelToggle();
//        aisTargetLabelToggle.addActionListener(this);
        this.aisTargetLabelToggle = new ToggleAisTargetName();
        this.aisTargetLabelToggle.addActionListener(this);

        // SART menu items
        sarTargetDetails = new SarTargetDetails("SART details");
        sarTargetDetails.addActionListener(this);

        // route general items
        sendToSTCC = new SendToSTCC("Send to STCC...");
        sendToSTCC.addActionListener(this);

        routeActivateToggle = new RouteActivateToggle();
        routeActivateToggle.addActionListener(this);

        monaLisaRouteRequest = new MonaLisaRouteRequest(
                "Request Optimized SSPA Route");
        monaLisaRouteRequest.addActionListener(this);

        // suggested route menu
        routeSuggestionDetails = new RouteSuggestionDetails("Route suggestion details...");
        routeSuggestionDetails.addActionListener(this);

        // route edit menu
        routeEditEndRoute = new RouteEditEndRoute("End route");
        routeEditEndRoute.addActionListener(this);
    
        // Init STCC Route negotiation items
        voyageAppendWaypoint = new VoyageAppendWaypoint("Append waypoint");
        voyageAppendWaypoint.addActionListener(this);
        voyageDeleteWaypoint = new VoyageHandlingWaypointDelete("Delete waypoint");
        voyageDeleteWaypoint.addActionListener(this);
        voyageLegInsertWaypoint = new VoyageHandlingLegInsertWaypoint("Insert waypoint here", EPDShip.getInstance().getVoyageEventDispatcher());
        voyageLegInsertWaypoint.addActionListener(this);
    }

    /**
     * Adds the general menu to the right-click menu. Remember to always add
     * this first, when creating specific menus.
     * 
     * @param alone
     */
    @Override
    public void generalMenu(boolean alone) {

        generateScaleMenu();

        hideIntendedRoutes.setIntendedRouteHandler(intendedRouteHandler);
        showIntendedRoutes.setIntendedRouteHandler(intendedRouteHandler);
        checkIntendedRouteItems(hideIntendedRoutes, showIntendedRoutes);

        newRoute.setMouseDelegator(mouseDelegator);
        newRoute.setMainFrame(mainFrame);

        nogoRequest.setNogoHandler(nogoHandler);
        nogoRequest.setMainFrame(mainFrame);
        nogoRequest.setOwnShipHandler(ownShipHandler);

        showPastTracks.setAisHandler(aisHandler);
        hidePastTracks.setAisHandler(aisHandler);
        
        // Prep the clearMap action
        routeHide.setRouteIndex(RouteHide.ALL_INACTIVE_ROUTES);
        clearMap.setMapMenuActions(hideIntendedRoutes, routeHide, hidePastTracks, mainFrame.getTopPanel().getHideAisNamesAction());
        
        if (alone) {
            removeAll();
            add(clearMap);
            add(hideIntendedRoutes);
            add(showIntendedRoutes);
            add(newRoute);
            addSeparator();
            if (!EPDShip.getInstance().getSettings().getGuiSettings().isRiskNogoDisabled()) {
                add(nogoRequest);
                addSeparator();
            }
            add(showPastTracks);
            add(hidePastTracks);
            addSeparator();
            add(scaleMenu);
            revalidate();
            return;
        }

        addSeparator();

        add(clearMap);
        add(hideIntendedRoutes);
        add(scaleMenu);
        revalidate();
    }

    /**
     * Builds ais target menu
     */
    public void aisMenu(VesselGraphicComponentSelector targetGraphic, TopPanel toppanel) {
        removeAll();
        aisTargetDetails.setTopPanel(toppanel);

        VesselTarget vesselTarget = targetGraphic.getVesselTarget();
        aisTargetDetails.setMSSI(vesselTarget.getMmsi());
        add(aisTargetDetails);

        // Toggle show intended route
        addIntendedRouteToggle(intendedRouteHandler.getIntendedRoute(vesselTarget.getMmsi()));

        // Toggle show past-track
        aisTogglePastTrack.setMobileTarget(vesselTarget);
        aisTogglePastTrack.setAisLayerToRefresh(aisLayer);
        aisTogglePastTrack.setText((vesselTarget.getSettings().isShowPastTrack()) ? "Hide past-track" : "Show past-track");
        add(aisTogglePastTrack);
        
        // Clear past-track
        aisClearPastTrack.setMobileTarget(vesselTarget);
        aisClearPastTrack.setText("Clear past-track");
        aisClearPastTrack.setAisLayer(aisLayer);
        add(aisClearPastTrack);
        
        // Toggle show label
        aisTargetLabelToggle.setVesselTargetGraphic(targetGraphic);
        aisTargetLabelToggle.setIAisTargetListener(aisLayer);
        add(aisTargetLabelToggle);
        if (targetGraphic.getShowNameLabel()) {
            aisTargetLabelToggle.setText("Hide AIS target label");
        } else {
            aisTargetLabelToggle.setText("Show AIS target label");
        }
        
        // Send chat message
        addSeparator();
        sendChatMessage.setVesselTarget(vesselTarget);
        sendChatMessage.checkEnabled();
        add(sendChatMessage);

        revalidate();
        generalMenu(false);
    }

    /**
     * Builds own-ship menu
     */
    public void ownShipMenu() {
        removeAll();

        // Toggle show past-track
        VesselTarget ownShip = ownShipHandler.getAisTarget();
        aisTogglePastTrack.setMobileTarget(ownShip);
        aisTogglePastTrack.setAisLayerToRefresh(null);
        aisTogglePastTrack.setText((ownShip.getSettings().isShowPastTrack()) ? "Hide past-track" : "Show past-track");
        add(aisTogglePastTrack);
        
        // Clear past-track
        aisClearPastTrack.setMobileTarget(ownShip);
        aisClearPastTrack.setAisLayer(null);
        aisClearPastTrack.setText("Clear past-track");
        add(aisClearPastTrack);
        
        revalidate();
        generalMenu(false);
    }

    /**
     * SART menu option
     * 
     * @param aisLayer
     * @param sarTarget
     */
    public void sartMenu(AisLayer aisLayer, SarTarget sarTarget) {
        removeAll();

        sarTargetDetails.setSarTarget(sarTarget);
        sarTargetDetails.setMainFrame(mainFrame);
        sarTargetDetails.setPntHandler(gpsHandler);

        add(sarTargetDetails);

        addSeparator();
        
        // Toggle show past-track
        aisTogglePastTrack.setMobileTarget(sarTarget);
        aisTogglePastTrack.setAisLayerToRefresh(aisLayer);
        aisTogglePastTrack.setText((sarTarget.getSettings().isShowPastTrack()) ? "Hide past-track" : "Show past-track");
        add(aisTogglePastTrack);
        
        // Clear past-track
        aisClearPastTrack.setMobileTarget(sarTarget);
        aisClearPastTrack.setAisLayer(aisLayer);
        aisClearPastTrack.setText("Clear past-track");
        add(aisClearPastTrack);
        revalidate();
        
        generalMenu(false);
    }

    public void sendToSTCC(int routeIndex) {
        removeAll();

        // Check if we are in a route exchange transaction
        if (strategicRouteHandler.isTransaction()) {
            sendToSTCC.setText("Show STCC info...");
            sendToSTCC.setTransactionId(strategicRouteHandler.getCurrentTransactionId());
       
        } else {
            try {
                // Look up the route
                Route route = routeManager.getRoute(routeIndex);
                if (routeManager.isActiveRoute(routeIndex)) {
                    route = routeManager.getActiveRoute();
                }
        
                sendToSTCC.setText("Send to STCC...");
                sendToSTCC.setRoute(route);
                sendToSTCC.setEnabled(strategicRouteHandler.strategicRouteSTCCExists()
                                && routeManager.getActiveRouteIndex() != routeIndex
                                && strategicRouteHandler.getStatus().getStatus() == ComponentStatus.Status.OK);
        
            } catch (Exception ex) {
                sendToSTCC.setEnabled(false);
                LOG.error("Error opening Send to STCC map menu for route index " + routeIndex, ex);
            }
        }

        add(sendToSTCC);
        revalidate();
    }

    public void addVoyageHandlingWaypointAppendMenuItem(Route route, int routeIndex) {
        // Update associated route + route index
        this.voyageAppendWaypoint.setRouteIndex(routeIndex);
        this.voyageAppendWaypoint.setRoute(route);
        this.add(this.voyageAppendWaypoint);
    }
    
    public void addVoyageHandlingWaypointDeleteMenuItem(Route route, int routeIndex, int waypointIndex) {
        this.voyageDeleteWaypoint.setRouteIndex(routeIndex);
        this.voyageDeleteWaypoint.setRoute(route);
        this.voyageDeleteWaypoint.setVoyageWaypointIndex(waypointIndex);
        this.add(this.voyageDeleteWaypoint);
    }
    
    public void addVoyageHandlingLegInsertWaypointMenuItem(Route route, RouteLeg routeLeg, Point point, int routeIndex) {
        this.voyageLegInsertWaypoint.setMapBean(this.mapBean);
        this.voyageLegInsertWaypoint.setRoute(route);
        this.voyageLegInsertWaypoint.setRouteLeg(routeLeg);
        this.voyageLegInsertWaypoint.setPoint(point);
        this.voyageLegInsertWaypoint.setRouteIndex(routeIndex);
        this.add(this.voyageLegInsertWaypoint);
    }

    public void generalRouteMenu(int routeIndex) {

        // Look up the route
        Route route = routeManager.getRoute(routeIndex);
        if (routeManager.isActiveRoute(routeIndex)) {
            route = routeManager.getActiveRoute();
        }

        
        if (routeManager.getActiveRouteIndex() == routeIndex) {
            routeActivateToggle.setText("Deactivate route");
            routeHide.setEnabled(false);
            routeDelete.setEnabled(false);
            routeAppendWaypoint.setEnabled(false);

        } else {
            routeActivateToggle.setText("Activate route");
            routeHide.setEnabled(true);
            routeDelete.setEnabled(true);
            routeAppendWaypoint.setEnabled(true);
        }

        routeAppendWaypoint.setRouteIndex(routeIndex);
        add(routeAppendWaypoint);

        // addSeparator();
        Separator seperator = new Separator();
        seperator.setVisible(true);
        this.add(seperator);

        sendToSTCC.setRoute(route);
        sendToSTCC.setTransactionId(strategicRouteHandler.getCurrentTransactionId());
        sendToSTCC.setEnabled(strategicRouteHandler.strategicRouteSTCCExists()
                        && routeManager.getActiveRouteIndex() != routeIndex
                        && strategicRouteHandler.getStatus().getStatus() == ComponentStatus.Status.OK);

        if (strategicRouteHandler.isTransaction()) {
            sendToSTCC.setText("Show STCC info...");
        } else {
            sendToSTCC.setText("Send to STCC...");
        }

        add(sendToSTCC);

        // addSeparator();

        routeActivateToggle.setRouteIndex(routeIndex);
        add(routeActivateToggle);

        routeHide.setRouteIndex(routeIndex);
        add(routeHide);

        routeDelete.setRouteIndex(routeIndex);
        add(routeDelete);

        routeCopy.setRouteIndex(routeIndex);
        add(routeCopy);

        routeReverse.setRouteIndex(routeIndex);
        add(routeReverse);

        monaLisaRouteRequest.setRouteManager(routeManager);
        monaLisaRouteRequest.setRouteIndex(routeIndex);
        monaLisaRouteRequest.setMainFrame(mainFrame);
        monaLisaRouteRequest.setOwnShipHandler(ownShipHandler);
        add(monaLisaRouteRequest);

        routeRequestMetoc.setRouteIndex(routeIndex);
        add(routeRequestMetoc);

        if (routeManager.hasMetoc(route)) {
            routeShowMetocToggle.setEnabled(true);
        } else {
            routeShowMetocToggle.setEnabled(false);
        }

        if (route.getRouteMetocSettings().isShowRouteMetoc()
                && routeManager.hasMetoc(route)) {
            routeShowMetocToggle.setText("Hide METOC");
        } else {
            routeShowMetocToggle.setText("Show METOC");
        }

        routeShowMetocToggle.setRouteIndex(routeIndex);
        add(routeShowMetocToggle);

        routeMetocProperties.setRouteIndex(routeIndex);
        add(routeMetocProperties);

        routeProperties.setRouteIndex(routeIndex);
        routeProperties.setChartPanel(EPDShip.getInstance().getMainFrame().getChartPanel());
        add(routeProperties);

//        generalMenu(false); //TODO: is this supposed to be commented out?
        revalidate();
    }

    /**
     * Creates the route leg menu
     * @param routeIndex the route index
     * @param routeLeg the route leg
     * @param point the mouse location
     */
    @Override
    public void routeLegMenu(int routeIndex, RouteLeg routeLeg, Point point) {
        removeAll();

        if (routeManager.getActiveRouteIndex() == routeIndex) {
            routeLegInsertWaypoint.setEnabled(false);
        } else {
            routeLegInsertWaypoint.setEnabled(true);
        }

        routeLegInsertWaypoint.setMapBean(mapBean);
        routeLegInsertWaypoint.setRouteLeg(routeLeg);
        routeLegInsertWaypoint.setRouteIndex(routeIndex);
        routeLegInsertWaypoint.setPoint(point);

        add(routeLegInsertWaypoint);

        generalRouteMenu(routeIndex);
        // TODO: add leg specific items
        
        revalidate();
    }

    /**
     * Creates the route way point menu
     * @param routeIndex the route index
     * @param routeWaypointIndex the route way point index
     */
    @Override
    public void routeWaypointMenu(int routeIndex, int routeWaypointIndex) {
        removeAll();

        routeWaypointActivateToggle.setRouteWaypointIndex(routeWaypointIndex);
        routeWaypointActivateToggle.setRouteManager(routeManager);

        if (routeManager.getActiveRouteIndex() == routeIndex) {
            routeWaypointActivateToggle.setEnabled(true);
            routeWaypointDelete.setEnabled(false);
            routeWaypointEditEta.setEnabled(false);
            routeWaypointEditEta.setVisible(false);
        } else {
            routeWaypointActivateToggle.setEnabled(false);
            routeWaypointDelete.setEnabled(true);
            routeWaypointEditEta.setVisible(true);
            routeWaypointEditEta.setEnabled(true);
        }

        add(routeWaypointActivateToggle);

        routeWaypointDelete.setRouteWaypointIndex(routeWaypointIndex);
        routeWaypointDelete.setRouteIndex(routeIndex);
        add(routeWaypointDelete);
        routeWaypointEditEta.setRouteWaypointIndex(routeWaypointIndex);
        routeWaypointEditEta.setRouteIndex(routeIndex);
        add(routeWaypointEditEta);

        generalRouteMenu(routeIndex);
        revalidate();
    }

    public void routeSuggestionMenu(RouteSuggestionData routeSuggestion) {
        removeAll();

        routeSuggestionDetails.setRouteSuggestion(routeSuggestion);
        add(routeSuggestionDetails);

        generalMenu(false);
        revalidate();
    }

    /**
     * Creates the route edit menu
     */
    @Override
    public void routeEditMenu() {
        removeAll();
        routeEditEndRoute.setNewRouteLayer(newRouteLayer);
        routeEditEndRoute.setRouteManager(routeManager);
        add(routeEditEndRoute);

        generalMenu(false);
        revalidate();
    }

    // Allows MapMenu to be added to the MapHandler (eg. use the find and init)
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }
        if (obj instanceof NewRouteContainerLayer) {
            newRouteLayer = (NewRouteContainerLayer) obj;
        }
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer) obj;
        }
        if (obj instanceof OwnShipHandler) {
            ownShipHandler = (OwnShipHandler) obj;
        }
        if (obj instanceof PntHandler) {
            gpsHandler = (PntHandler) obj;
        }
        if (obj instanceof NogoHandler) {
            nogoHandler = (NogoHandler) obj;
        }
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
        }
        if (obj instanceof MouseDelegator) {
            mouseDelegator = (MouseDelegator) obj;
        }
        if (obj instanceof StrategicRouteHandler) {
            strategicRouteHandler = (StrategicRouteHandler) obj;
        }

    }
}
