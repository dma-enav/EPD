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
package dk.dma.epd.ship.gui;

import java.awt.Point;

import com.bbn.openmap.MouseDelegator;

import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.menuitems.GeneralClearMap;
import dk.dma.epd.common.prototype.gui.menuitems.RouteHide;
import dk.dma.epd.common.prototype.gui.menuitems.SarTargetDetails;
import dk.dma.epd.common.prototype.gui.menuitems.ToggleAisTargetName;
import dk.dma.epd.common.prototype.gui.menuitems.VoyageHandlingLegInsertWaypoint;
import dk.dma.epd.common.prototype.layers.ais.VesselTargetGraphic;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteGraphic;
import dk.dma.epd.common.prototype.layers.msi.MsiDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiSymbolGraphic;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.menuitems.AisTargetDetails;
import dk.dma.epd.ship.gui.menuitems.GeneralNewRoute;
import dk.dma.epd.ship.gui.menuitems.MonaLisaRouteRequest;
import dk.dma.epd.ship.gui.menuitems.MsiDetails;
import dk.dma.epd.ship.gui.menuitems.MsiZoomTo;
import dk.dma.epd.ship.gui.menuitems.NogoRequest;
import dk.dma.epd.ship.gui.menuitems.RouteActivateToggle;
import dk.dma.epd.ship.gui.menuitems.RouteEditEndRoute;
import dk.dma.epd.ship.gui.menuitems.SendToSTCC;
import dk.dma.epd.ship.gui.menuitems.SuggestedRouteDetails;
import dk.dma.epd.ship.gui.menuitems.VoyageAppendWaypoint;
import dk.dma.epd.ship.gui.menuitems.VoyageHandlingWaypointDelete;
import dk.dma.epd.ship.gui.route.RouteSuggestionDialog;
import dk.dma.epd.ship.layers.ais.AisLayer;
import dk.dma.epd.ship.layers.msi.MsiLayer;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.ownship.OwnShipHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.StrategicRouteHandler;
import dk.dma.epd.ship.service.SuggestedRoute;

/**
 * Right click map menu
 */
public class MapMenu extends MapMenuCommon {

    private static final long serialVersionUID = 1L;

    private MsiHandler msiHandler;

    // menu items
    private GeneralClearMap clearMap;
    private GeneralNewRoute newRoute;
    private AisTargetDetails aisTargetDetails;

    private SarTargetDetails sarTargetDetails;
    private ToggleAisTargetName aisTargetLabelToggle;
    private NogoRequest nogoRequest;
    private MsiDetails msiDetails;
    private MsiZoomTo msiZoomTo;
    
    private RouteActivateToggle routeActivateToggle;
    private MonaLisaRouteRequest monaLisaRouteRequest;
    private SuggestedRouteDetails suggestedRouteDetails;
    private RouteEditEndRoute routeEditEndRoute;
    private SendToSTCC sendToSTCC;
    private VoyageAppendWaypoint voyageAppendWaypoint;
    private VoyageHandlingWaypointDelete voyageDeleteWaypoint;
    private VoyageHandlingLegInsertWaypoint voyageLegInsertWaypoint;
    private RouteManager routeManager;
    private MainFrame mainFrame;
    private PntHandler gpsHandler;
    private Route route;
    private RouteSuggestionDialog routeSuggestionDialog;
    private NewRouteContainerLayer newRouteLayer;
    private AisLayer aisLayer;
    private OwnShipHandler ownShipHandler;
    private NogoHandler nogoHandler;
    private MouseDelegator mouseDelegator;
    private Point windowLocation;
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

        nogoRequest = new NogoRequest("Request NoGo area");
        nogoRequest.addActionListener(this);
        
        // ais menu items
        aisTargetDetails = new AisTargetDetails("Show AIS target details");
        aisTargetDetails.addActionListener(this);
//        aisTargetLabelToggle = new AisTargetLabelToggle();
//        aisTargetLabelToggle.addActionListener(this);
        this.aisTargetLabelToggle = new ToggleAisTargetName();
        this.aisTargetLabelToggle.addActionListener(this);

        // SART menu items
        sarTargetDetails = new SarTargetDetails("SART details");
        sarTargetDetails.addActionListener(this);

        // msi menu items
        msiDetails = new MsiDetails("Show MSI details");
        msiDetails.addActionListener(this);
        msiZoomTo = new MsiZoomTo("Zoom to MSI");
        msiZoomTo.addActionListener(this);

        // route general items
        sendToSTCC = new SendToSTCC("Send to STCC");
        sendToSTCC.addActionListener(this);

        routeActivateToggle = new RouteActivateToggle();
        routeActivateToggle.addActionListener(this);

        monaLisaRouteRequest = new MonaLisaRouteRequest(
                "Request Optimized SSPA Route");
        monaLisaRouteRequest.addActionListener(this);

        // suggested route menu
        suggestedRouteDetails = new SuggestedRouteDetails(
                "Suggested route details");
        suggestedRouteDetails.addActionListener(this);

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

        hideIntendedRoutes.setAisHandler(aisHandler);
        showIntendedRoutes.setAisHandler(aisHandler);
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
        routeHide.setRouteManager(routeManager);
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
    public void aisMenu(VesselTargetGraphic targetGraphic, TopPanel toppanel) {
        removeAll();
        aisTargetDetails.setTopPanel(toppanel);

        VesselTarget vesselTarget = targetGraphic.getVesselTarget();
        aisTargetDetails.setMSSI(vesselTarget.getMmsi());
        add(aisTargetDetails);

        // Toggle show intended route
        intendedRouteToggle.setAisTargetListener(aisLayer);
        intendedRouteToggle.setVesselTarget(vesselTarget);

        if (vesselTarget.getIntendedRoute() != null
                && vesselTarget.getIntendedRoute().hasRoute()) {
            intendedRouteToggle.setEnabled(true);
        } else {
            intendedRouteToggle.setEnabled(false);
        }
        if (vesselTarget.getSettings().isShowRoute()) {
            intendedRouteToggle.setText("Hide intended route");
        } else {
            intendedRouteToggle.setText("Show intended route");
        }
        checkIntendedRouteItems(intendedRouteToggle);
        add(intendedRouteToggle);

        // Toggle show past-track
        aisTogglePastTrack.setMobileTarget(vesselTarget);
        aisTogglePastTrack.setAisLayer(aisLayer);
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

        revalidate();
        generalMenu(false);
    }

    /**
     * Options for intended route
     */
    public void intendedRouteMenu(final VesselTarget vesselTarget, final IntendedRouteGraphic routeGraphics) {
        removeAll();

        intendedRouteToggle.setAisTargetListener(aisLayer);
        intendedRouteToggle.setVesselTarget(vesselTarget);

        if (vesselTarget.getIntendedRoute() != null
                && vesselTarget.getIntendedRoute().hasRoute()) {
            intendedRouteToggle.setEnabled(true);
        } else {
            intendedRouteToggle.setEnabled(false);
        }
        if (vesselTarget.getSettings().isShowRoute()) {
            intendedRouteToggle.setText("Hide intended route");
        } else {
            intendedRouteToggle.setText("Show intended route");
        }
        checkIntendedRouteItems(intendedRouteToggle);
        add(intendedRouteToggle);
        
        centerVesselTarget.setVesselTarget(vesselTarget);
        centerVesselTarget.setMapBean(mapBean);
        add(centerVesselTarget);
        
        // Add a color selector menu item
        intendedRouteColor.init(this, routeGraphics);
        add(intendedRouteColor);

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
        aisTogglePastTrack.setAisLayer(null);
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
        aisTogglePastTrack.setAisLayer(aisLayer);
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

    /**
     * Builds the maritime safety information menu
     * 
     * @param topPanel
     *            Reference to the top panel to get the msi dialog
     * @param selectedGraphic
     *            The selected graphic (containing the msi message)
     */
    public void msiMenu(TopPanel topPanel, MsiSymbolGraphic selectedGraphic) {
        removeAll();

        msiDetails.setTopPanel(topPanel);
        msiDetails.setMsiMessage(selectedGraphic.getMsiMessage());
        add(msiDetails);

        Boolean isAcknowledged = msiHandler.isAcknowledged(selectedGraphic
                .getMsiMessage().getMessageId());
        msiAcknowledge.setMsiHandler(msiHandler);
        msiAcknowledge.setEnabled(!isAcknowledged);
        msiAcknowledge.setMsiMessage(selectedGraphic.getMsiMessage());
        add(msiAcknowledge);

        revalidate();
        generalMenu(false);
    }

    public void msiDirectionalMenu(TopPanel topPanel,
            MsiDirectionalIcon selectedGraphic, MsiLayer msiLayer) {
        removeAll();

        msiDetails.setTopPanel(topPanel);
        msiDetails.setMsiMessage(selectedGraphic.getMessage().msiMessage);
        add(msiDetails);

        msiZoomTo.setMsiLayer(msiLayer);
        msiZoomTo.setMsiMessageExtended(selectedGraphic.getMessage());
        add(msiZoomTo);

        revalidate();
        generalMenu(false);
    }

    public void sendToSTCC(int routeIndex) {
        removeAll();

        System.out.println("Route index is: " + routeIndex
                + " Active route index is: "
                + routeManager.getActiveRouteIndex());

        sendToSTCC.setRoute(route);
        sendToSTCC.setRouteLocation(windowLocation);
        sendToSTCC
                .setEnabled(strategicRouteHandler.strategicRouteSTCCExists()
                        && routeManager.getActiveRouteIndex() != routeIndex
                        && strategicRouteHandler.getStatus().getStatus() == ComponentStatus.Status.OK);

        if (strategicRouteHandler.isTransaction()) {
            sendToSTCC.setText("Show STCC info");
        } else {
            sendToSTCC.setText("Send to STCC");
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

        routeAppendWaypoint.setRouteManager(routeManager);
        routeAppendWaypoint.setRouteIndex(routeIndex);
        add(routeAppendWaypoint);

        // addSeparator();
        Separator seperator = new Separator();
        seperator.setVisible(true);
        this.add(seperator);

        sendToSTCC.setRoute(route);
        sendToSTCC.setRouteLocation(windowLocation);
        sendToSTCC
                .setEnabled(strategicRouteHandler.strategicRouteSTCCExists()
                        && routeManager.getActiveRouteIndex() != routeIndex
                        && strategicRouteHandler.getStatus().getStatus() == ComponentStatus.Status.OK);

        if (strategicRouteHandler.isTransaction()) {
            sendToSTCC.setText("Show STCC info");
        } else {
            sendToSTCC.setText("Send to STCC");
        }

        add(sendToSTCC);

        // addSeparator();

        routeActivateToggle.setRouteManager(routeManager);
        routeActivateToggle.setRouteIndex(routeIndex);
        add(routeActivateToggle);

        routeHide.setRouteManager(routeManager);
        routeHide.setRouteIndex(routeIndex);
        add(routeHide);

        routeDelete.setRouteManager(routeManager);
        routeDelete.setRouteIndex(routeIndex);
        add(routeDelete);

        routeCopy.setRouteManager(routeManager);
        routeCopy.setRouteIndex(routeIndex);
        add(routeCopy);

        routeReverse.setRouteManager(routeManager);
        routeReverse.setRouteIndex(routeIndex);
        add(routeReverse);

        route = routeManager.getRoute(routeIndex);
        if (routeManager.isActiveRoute(routeIndex)) {
            route = routeManager.getActiveRoute();
        }

        monaLisaRouteRequest.setRouteManager(routeManager);
        monaLisaRouteRequest.setRouteIndex(routeIndex);
        monaLisaRouteRequest.setMainFrame(mainFrame);
        monaLisaRouteRequest.setOwnShipHandler(ownShipHandler);
        add(monaLisaRouteRequest);

        routeRequestMetoc.setRouteManager(routeManager);
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

        routeShowMetocToggle.setRouteManager(routeManager);
        routeShowMetocToggle.setRouteIndex(routeIndex);
        add(routeShowMetocToggle);

        routeMetocProperties.setRouteManager(routeManager);
        routeMetocProperties.setRouteIndex(routeIndex);
        add(routeMetocProperties);

        routeProperties.setRouteManager(routeManager);
        routeProperties.setRouteIndex(routeIndex);
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
        routeLegInsertWaypoint.setRouteManager(routeManager);
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
        } else {
            routeWaypointActivateToggle.setEnabled(false);
            routeWaypointDelete.setEnabled(true);
        }

        add(routeWaypointActivateToggle);

        routeWaypointDelete.setRouteWaypointIndex(routeWaypointIndex);
        routeWaypointDelete.setRouteIndex(routeIndex);
        routeWaypointDelete.setRouteManager(routeManager);
        add(routeWaypointDelete);

        generalRouteMenu(routeIndex);
        revalidate();
    }

    public void suggestedRouteMenu(SuggestedRoute aisSuggestedRoute) {
        removeAll();

        suggestedRouteDetails.setSuggestedRoute(aisSuggestedRoute);
        suggestedRouteDetails.setRouteSuggestionDialog(routeSuggestionDialog);
        add(suggestedRouteDetails);

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
        
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler) obj;
        }
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }
        if (obj instanceof RouteSuggestionDialog) {
            routeSuggestionDialog = (RouteSuggestionDialog) obj;
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


    public void setRouteLocation(Point point) {
        this.windowLocation = point;
    }

}
