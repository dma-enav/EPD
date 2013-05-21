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

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;

import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.layers.ais.VesselTargetGraphic;
import dk.dma.epd.common.prototype.layers.msi.MsiDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiSymbolGraphic;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.sensor.gps.GpsHandler;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.menuitems.AisIntendedRouteToggle;
import dk.dma.epd.ship.gui.menuitems.AisTargetDetails;
import dk.dma.epd.ship.gui.menuitems.AisTargetLabelToggle;
import dk.dma.epd.ship.gui.menuitems.GeneralClearMap;
import dk.dma.epd.ship.gui.menuitems.GeneralHideIntendedRoutes;
import dk.dma.epd.ship.gui.menuitems.GeneralNewRoute;
import dk.dma.epd.ship.gui.menuitems.GeneralShowIntendedRoutes;
import dk.dma.epd.ship.gui.menuitems.IMapMenuAction;
import dk.dma.epd.ship.gui.menuitems.MonaLisaRouteRequest;
import dk.dma.epd.ship.gui.menuitems.MsiAcknowledge;
import dk.dma.epd.ship.gui.menuitems.MsiDetails;
import dk.dma.epd.ship.gui.menuitems.MsiZoomTo;
import dk.dma.epd.ship.gui.menuitems.NogoRequest;
import dk.dma.epd.ship.gui.menuitems.RouteActivateToggle;
import dk.dma.epd.ship.gui.menuitems.RouteAppendWaypoint;
import dk.dma.epd.ship.gui.menuitems.RouteCopy;
import dk.dma.epd.ship.gui.menuitems.RouteDelete;
import dk.dma.epd.ship.gui.menuitems.RouteEditEndRoute;
import dk.dma.epd.ship.gui.menuitems.RouteHide;
import dk.dma.epd.ship.gui.menuitems.RouteLegInsertWaypoint;
import dk.dma.epd.ship.gui.menuitems.RouteMetocProperties;
import dk.dma.epd.ship.gui.menuitems.RouteProperties;
import dk.dma.epd.ship.gui.menuitems.RouteRequestMetoc;
import dk.dma.epd.ship.gui.menuitems.RouteReverse;
import dk.dma.epd.ship.gui.menuitems.RouteShowMetocToggle;
import dk.dma.epd.ship.gui.menuitems.RouteWaypointActivateToggle;
import dk.dma.epd.ship.gui.menuitems.RouteWaypointDelete;
import dk.dma.epd.ship.gui.menuitems.SarTargetDetails;
import dk.dma.epd.ship.gui.menuitems.SendToSTCC;
import dk.dma.epd.ship.gui.menuitems.SuggestedRouteDetails;
import dk.dma.epd.ship.gui.route.RouteSuggestionDialog;
import dk.dma.epd.ship.layers.ais.AisLayer;
import dk.dma.epd.ship.layers.msi.MsiLayer;
import dk.dma.epd.ship.layers.route.RouteLayer;
import dk.dma.epd.ship.layers.voyage.VoyageLayer;
import dk.dma.epd.ship.monalisa.MonaLisaHandler;
import dk.dma.epd.ship.monalisa.RecievedRoute;
import dk.dma.epd.ship.msi.MsiHandler;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.EnavServiceHandler;

/**
 * Right click map menu
 */
public class MapMenu extends JPopupMenu implements ActionListener,
        LightMapHandlerChild, BeanContextChild, BeanContextMembershipListener {

    private static final long serialVersionUID = 1L;

    private IMapMenuAction action;
    private MsiHandler msiHandler;

    // menu items
    private GeneralClearMap clearMap;
    private GeneralHideIntendedRoutes hideIntendedRoutes;
    private GeneralShowIntendedRoutes showIntendedRoutes;
    private GeneralNewRoute newRoute;
    private JMenu scaleMenu;
    private AisIntendedRouteToggle aisIntendedRouteToggle;
    private AisTargetDetails aisTargetDetails;

    private SarTargetDetails sarTargetDetails;
    private AisTargetLabelToggle aisTargetLabelToggle;
    private NogoRequest nogoRequest;
    private MsiAcknowledge msiAcknowledge;
    private MsiDetails msiDetails;
    private MsiZoomTo msiZoomTo;
    private RouteActivateToggle routeActivateToggle;
    private RouteAppendWaypoint routeAppendWaypoint;
    private RouteHide routeHide;
    private RouteCopy routeCopy;
    private RouteReverse routeReverse;
    private RouteDelete routeDelete;
    private RouteProperties routeProperties;
    private RouteMetocProperties routeMetocProperties;
    private RouteRequestMetoc routeRequestMetoc;
    private MonaLisaRouteRequest monaLisaRouteRequest;
    private RouteShowMetocToggle routeShowMetocToggle;
    private RouteLegInsertWaypoint routeLegInsertWaypoint;
    private RouteWaypointActivateToggle routeWaypointActivateToggle;
    private RouteWaypointDelete routeWaypointDelete;
    private SuggestedRouteDetails suggestedRouteDetails;
    private RouteEditEndRoute routeEditEndRoute;
    private SendToSTCC sendToSTCC;

    // bean context
    protected String propertyPrefix;
    protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(
            this);
    protected boolean isolated;
    private RouteManager routeManager;
    private MainFrame mainFrame;
    private GpsHandler gpsHandler;
    private Route route;
    private RouteSuggestionDialog routeSuggestionDialog;
    MapBean mapBean;
    private Map<Integer, String> map;
    private NewRouteContainerLayer newRouteLayer;
    private AisLayer aisLayer;
    private AisHandler aisHandler;
    private NogoHandler nogoHandler;
    private MouseDelegator mouseDelegator;
    private EnavServiceHandler enavServiceHandler;
    private Point windowLocation;
    private MonaLisaHandler monaLisaHandler;
    
//    private RouteLayer routeLayer;
//    private VoyageLayer voyageLayer;

    public MapMenu() {
        super();

        // general menu items
        clearMap = new GeneralClearMap("Clear chart");
        clearMap.addActionListener(this);
        hideIntendedRoutes = new GeneralHideIntendedRoutes(
                "Hide all intended routes");
        hideIntendedRoutes.addActionListener(this);
        showIntendedRoutes = new GeneralShowIntendedRoutes(
                "Show all intended routes");
        showIntendedRoutes.addActionListener(this);
        newRoute = new GeneralNewRoute("Add new route - Ctrl N");
        newRoute.addActionListener(this);

        nogoRequest = new NogoRequest("Request NoGo area");
        nogoRequest.addActionListener(this);

        scaleMenu = new JMenu("Scale");

        // using treemap so scale levels are always sorted
        map = new TreeMap<>();

        // ais menu items
        aisTargetDetails = new AisTargetDetails("Show AIS target details");
        aisTargetDetails.addActionListener(this);
        aisIntendedRouteToggle = new AisIntendedRouteToggle();
        aisIntendedRouteToggle.addActionListener(this);
        aisTargetLabelToggle = new AisTargetLabelToggle();
        aisTargetLabelToggle.addActionListener(this);

        // SART menu items
        sarTargetDetails = new SarTargetDetails("SART details");
        sarTargetDetails.addActionListener(this);

        // msi menu items
        msiDetails = new MsiDetails("Show MSI details");
        msiDetails.addActionListener(this);
        msiAcknowledge = new MsiAcknowledge("Acknowledge MSI");
        msiAcknowledge.addActionListener(this);
        msiZoomTo = new MsiZoomTo("Zoom to MSI");
        msiZoomTo.addActionListener(this);

        // route general items
        sendToSTCC = new SendToSTCC("Send to STCC");
        sendToSTCC.addActionListener(this);

        routeActivateToggle = new RouteActivateToggle();
        routeActivateToggle.addActionListener(this);
        routeHide = new RouteHide("Hide route");
        routeHide.addActionListener(this);

        routeCopy = new RouteCopy("Copy route");
        routeCopy.addActionListener(this);

        routeReverse = new RouteReverse("Reverse route");
        routeReverse.addActionListener(this);

        routeDelete = new RouteDelete("Delete route");
        routeDelete.addActionListener(this);

        monaLisaRouteRequest = new MonaLisaRouteRequest(
                "Request MonaLisa Route");
        monaLisaRouteRequest.addActionListener(this);
        routeRequestMetoc = new RouteRequestMetoc("Request METOC");
        routeRequestMetoc.addActionListener(this);
        routeShowMetocToggle = new RouteShowMetocToggle();
        routeShowMetocToggle.addActionListener(this);
        routeProperties = new RouteProperties("Route properties");
        routeProperties.addActionListener(this);
        routeMetocProperties = new RouteMetocProperties("METOC properties");
        routeMetocProperties.addActionListener(this);
        routeAppendWaypoint = new RouteAppendWaypoint("Append waypoint");
        routeAppendWaypoint.addActionListener(this);

        // route leg menu
        routeLegInsertWaypoint = new RouteLegInsertWaypoint(
                "Insert waypoint here");
        routeLegInsertWaypoint.addActionListener(this);

        // route waypoint menu
        routeWaypointActivateToggle = new RouteWaypointActivateToggle(
                "Activate waypoint");
        routeWaypointActivateToggle.addActionListener(this);
        routeWaypointDelete = new RouteWaypointDelete("Delete waypoint");
        routeWaypointDelete.addActionListener(this);

        // suggested route menu
        suggestedRouteDetails = new SuggestedRouteDetails(
                "Suggested route details");
        suggestedRouteDetails.addActionListener(this);

        // route edit menu
        routeEditEndRoute = new RouteEditEndRoute("End route");
        routeEditEndRoute.addActionListener(this);

    }

    /**
     * Adds the general menu to the right-click menu. Remember to always add
     * this first, when creating specific menus.
     * 
     * @param alone
     *            TODO
     */
    public void generalMenu(boolean alone) {
        scaleMenu.removeAll();

        // clear previous map scales
        map.clear();
        // Initialize the scale levels, and give them name (this should be done
        // from settings later...)
        map.put(5000, "Berthing      (1 : 5.000)");
        map.put(10000, "Harbour       (1 : 10.000)");
        map.put(70000, "Approach      (1 : 70.000)");
        map.put(300000, "Coastal       (1 : 300.000)");
        map.put(2000000, "Overview      (1 : 2.000.000)");
        map.put(20000000, "Ocean         (1 : 20.000.000)");
        // put current scale level
        Integer currentScale = (int) mapBean.getScale();

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        map.put(currentScale,
                "Current scale (1 : " + formatter.format(currentScale) + ")");

        // Iterate through the treemap, adding the menuitems and assigning
        // actions
        Set<Integer> keys = map.keySet();
        for (final Integer key : keys) {
            String value = map.get(key);
            JMenuItem menuItem = new JMenuItem(value);
            menuItem.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    mapBean.setScale(key);
                }
            });
            scaleMenu.add(menuItem);
        }

        hideIntendedRoutes.setAisHandler(aisHandler);
        showIntendedRoutes.setAisHandler(aisHandler);

        newRoute.setMouseDelegator(mouseDelegator);
        newRoute.setMainFrame(mainFrame);

        nogoRequest.setNogoHandler(nogoHandler);
        nogoRequest.setMainFrame(mainFrame);
        nogoRequest.setAisHandler(aisHandler);

        if (alone) {
            removeAll();
            add(clearMap);
            add(hideIntendedRoutes);
            add(showIntendedRoutes);
            add(newRoute);
            if (!EPDShip.getSettings().getGuiSettings().isRiskNogoDisabled()) {
                add(nogoRequest);
            }
            add(scaleMenu);
            return;
        }

        addSeparator();
        add(clearMap);
        add(hideIntendedRoutes);
        add(scaleMenu);
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

        aisIntendedRouteToggle.setVesselTargetSettings(vesselTarget
                .getSettings());
        aisIntendedRouteToggle.setAisLayer(aisLayer);
        aisIntendedRouteToggle.setVesselTarget(vesselTarget);

        if (vesselTarget.getAisRouteData() != null
                && vesselTarget.getAisRouteData().hasRoute()) {
            aisIntendedRouteToggle.setEnabled(true);
        } else {
            aisIntendedRouteToggle.setEnabled(false);
        }
        if (vesselTarget.getSettings().isShowRoute()) {
            aisIntendedRouteToggle.setText("Hide intended route");
        } else {
            aisIntendedRouteToggle.setText("Show intended route");
        }
        add(aisIntendedRouteToggle);

        aisTargetLabelToggle.setVesselTargetGraphic(targetGraphic);
        aisTargetLabelToggle.setAisLayer(aisLayer);
        add(aisTargetLabelToggle);
        if (targetGraphic.getShowNameLabel()) {
            aisTargetLabelToggle.setText("Hide AIS target label");
        } else {
            aisTargetLabelToggle.setText("Show AIS target label");
        }

        generalMenu(false);
    }

    /**
     * Options for suggested route
     */
    public void aisSuggestedRouteMenu(VesselTarget vesselTarget) {
        removeAll();

        aisIntendedRouteToggle.setVesselTargetSettings(vesselTarget
                .getSettings());
        aisIntendedRouteToggle.setAisLayer(aisLayer);
        aisIntendedRouteToggle.setVesselTarget(vesselTarget);

        if (vesselTarget.getAisRouteData() != null
                && vesselTarget.getAisRouteData().hasRoute()) {
            aisIntendedRouteToggle.setEnabled(true);
        } else {
            aisIntendedRouteToggle.setEnabled(false);
        }
        if (vesselTarget.getSettings().isShowRoute()) {
            aisIntendedRouteToggle.setText("Hide intended route");
        } else {
            aisIntendedRouteToggle.setText("Show intended route");
        }
        add(aisIntendedRouteToggle);

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
        sarTargetDetails.setGpsHandler(gpsHandler);

        add(sarTargetDetails);

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

        generalMenu(false);
    }

    public void sendToSTCC(int routeIndex){
        removeAll();
        
        sendToSTCC.setRoute(route);
        sendToSTCC.setRouteLocation(windowLocation);
        sendToSTCC
                .setEnabled(enavServiceHandler.getMonaLisaSTCCList().size() >0);
        
        if (monaLisaHandler.isTransaction()){
            sendToSTCC.setText("Show STCC info");
        }else{
            sendToSTCC.setText("Send to STCC");
        }
        
        add(sendToSTCC);
        
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

        addSeparator();

        sendToSTCC.setRoute(route);
        sendToSTCC.setRouteLocation(windowLocation);
        sendToSTCC
                .setEnabled(enavServiceHandler.getMonaLisaSTCCList().size() >0);
        
        if (mainFrame.getMonaLisaSTCCDialog().isActive()){
            sendToSTCC.setText("Show STCC info");
        }else{
            sendToSTCC.setText("Send to STCC");
        }
        
        add(sendToSTCC);

//        addSeparator();

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
        // monaLisaRouteRequest.setMonaLisaRouteExchange(EPDShip.getMonaLisaRouteExchange());
        monaLisaRouteRequest.setMainFrame(mainFrame);
        monaLisaRouteRequest.setAisHandler(aisHandler);
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

        generalMenu(false);
    }

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
    }

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
    }

    public void suggestedRouteMenu(RecievedRoute aisSuggestedRoute) {
        removeAll();

        suggestedRouteDetails.setSuggestedRoute(aisSuggestedRoute);
        suggestedRouteDetails.setRouteSuggestionDialog(routeSuggestionDialog);
        add(suggestedRouteDetails);

        generalMenu(false);
    }

    public void routeEditMenu() {
        removeAll();
        routeEditEndRoute.setNewRouteLayer(newRouteLayer);
        routeEditEndRoute.setRouteManager(routeManager);
        add(routeEditEndRoute);

        generalMenu(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action = (IMapMenuAction) e.getSource();
        action.doAction();
    }

    // Allows MapMenu to be added to the MapHandler (eg. use the find and init)
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler) obj;
        }
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }
        if (obj instanceof RouteSuggestionDialog) {
            routeSuggestionDialog = (RouteSuggestionDialog) obj;
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        if (obj instanceof NewRouteContainerLayer) {
            newRouteLayer = (NewRouteContainerLayer) obj;
        }
        if (obj instanceof AisLayer) {
            aisLayer = (AisLayer) obj;
        }
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }
        if (obj instanceof GpsHandler) {
            gpsHandler = (GpsHandler) obj;
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

        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
        }
        if (obj instanceof MonaLisaHandler) {
            monaLisaHandler = (MonaLisaHandler) obj;
        }
//        if (obj instanceof VoyageLayer) {
//            voyageLayer = (VoyageLayer) obj;
//        }
        
        

    }

    public void findAndInit(Iterator<?> it) {
        while (it.hasNext()) {
            findAndInit(it.next());
        }
    }

    @Override
    public void findAndUndo(Object obj) {
    }

    @Override
    public void childrenAdded(BeanContextMembershipEvent bcme) {
        if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
            findAndInit(bcme.iterator());
        }
    }

    @Override
    public void childrenRemoved(BeanContextMembershipEvent bcme) {
        Iterator<?> it = bcme.iterator();
        while (it.hasNext()) {
            findAndUndo(it.next());
        }
    }

    @Override
    public BeanContext getBeanContext() {
        return beanContextChildSupport.getBeanContext();
    }

    @Override
    public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

        if (in_bc != null) {
            if (!isolated || beanContextChildSupport.getBeanContext() == null) {
                in_bc.addBeanContextMembershipListener(this);
                beanContextChildSupport.setBeanContext(in_bc);
                findAndInit(in_bc.iterator());
            }
        }
    }

    @Override
    public void addVetoableChangeListener(String propertyName,
            VetoableChangeListener in_vcl) {
        beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
    }

    @Override
    public void removeVetoableChangeListener(String propertyName,
            VetoableChangeListener in_vcl) {
        beanContextChildSupport.removeVetoableChangeListener(propertyName,
                in_vcl);
    }

    public void setRouteLocation(Point point) {
        this.windowLocation = point;
    }

}
