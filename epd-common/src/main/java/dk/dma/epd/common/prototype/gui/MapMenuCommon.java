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
package dk.dma.epd.common.prototype.gui;

import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.MapBean;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.menuitems.CenterVesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.ClearPastTrack;
import dk.dma.epd.common.prototype.gui.menuitems.HideAllIntendedRoutes;
import dk.dma.epd.common.prototype.gui.menuitems.IntendedRouteColor;
import dk.dma.epd.common.prototype.gui.menuitems.IntendedRouteToggle;
import dk.dma.epd.common.prototype.gui.menuitems.MsiNmAcknowledge;
import dk.dma.epd.common.prototype.gui.menuitems.MsiNmDetails;
import dk.dma.epd.common.prototype.gui.menuitems.MsiNmZoomTo;
import dk.dma.epd.common.prototype.gui.menuitems.RouteAppendWaypoint;
import dk.dma.epd.common.prototype.gui.menuitems.RouteCopy;
import dk.dma.epd.common.prototype.gui.menuitems.RouteDelete;
import dk.dma.epd.common.prototype.gui.menuitems.RouteHide;
import dk.dma.epd.common.prototype.gui.menuitems.RouteLegInsertWaypoint;
import dk.dma.epd.common.prototype.gui.menuitems.RouteMetocProperties;
import dk.dma.epd.common.prototype.gui.menuitems.RouteProperties;
import dk.dma.epd.common.prototype.gui.menuitems.RouteRequestMetoc;
import dk.dma.epd.common.prototype.gui.menuitems.RouteReverse;
import dk.dma.epd.common.prototype.gui.menuitems.RouteShowMetocToggle;
import dk.dma.epd.common.prototype.gui.menuitems.RouteWaypointActivateToggle;
import dk.dma.epd.common.prototype.gui.menuitems.RouteWaypointDelete;
import dk.dma.epd.common.prototype.gui.menuitems.RouteWaypointEditEta;
import dk.dma.epd.common.prototype.gui.menuitems.SendChatMessage;
import dk.dma.epd.common.prototype.gui.menuitems.SetShowPastTracks;
import dk.dma.epd.common.prototype.gui.menuitems.ShowAllIntendedRoutes;
import dk.dma.epd.common.prototype.gui.menuitems.ToggleShowPastTrack;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.layers.intendedroute.IntendedRouteGraphic;
import dk.dma.epd.common.prototype.layers.msi.MsiNmDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiNmLayerCommon;
import dk.dma.epd.common.prototype.layers.msi.MsiNmNmSymbolGraphic;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

/**
 * Abstract base class for the right click map menu
 * <p>
 * TODO: Move more common functionality to this class
 */
public abstract class MapMenuCommon extends JPopupMenu implements ActionListener,
        LightMapHandlerChild, BeanContextChild, BeanContextMembershipListener {

    private static final long serialVersionUID = 1L;

    protected IMapMenuAction action;

    // Common menu items for shore and ship
    protected SetShowPastTracks hidePastTracks;
    protected SetShowPastTracks showPastTracks;
    protected ToggleShowPastTrack aisTogglePastTrack;
    protected ClearPastTrack aisClearPastTrack;

    protected RouteAppendWaypoint routeAppendWaypoint;
    protected RouteHide routeHide;
    protected RouteCopy routeCopy;
    protected RouteReverse routeReverse;
    protected RouteDelete routeDelete;
    protected RouteProperties routeProperties;
    protected RouteRequestMetoc routeRequestMetoc;
    protected RouteMetocProperties routeMetocProperties;
    protected RouteShowMetocToggle routeShowMetocToggle;
    protected RouteLegInsertWaypoint routeLegInsertWaypoint;
    protected RouteWaypointActivateToggle routeWaypointActivateToggle;
    protected RouteWaypointDelete routeWaypointDelete;
    protected RouteWaypointEditEta routeWaypointEditEta;

    protected IntendedRouteToggle intendedRouteToggle;
    protected HideAllIntendedRoutes hideIntendedRoutes;
    protected ShowAllIntendedRoutes showIntendedRoutes;
    protected IntendedRouteColor intendedRouteColor;
    
    protected MsiNmAcknowledge msiNmAcknowledge;
    protected MsiNmDetails msiNmDetails;
    protected MsiNmZoomTo msiNmZoomTo;

    protected CenterVesselTarget centerVesselTarget;
    
    protected SendChatMessage sendChatMessage;
    
    protected JMenu scaleMenu;
    protected Map<Integer, String> map;
    protected MapBean mapBean;
    
    protected AisHandlerCommon aisHandler;
    protected IntendedRouteHandlerCommon intendedRouteHandler;
    protected MsiNmServiceHandlerCommon msiNmHandler;
    
    // bean context
    protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);
    protected boolean isolated;
    
    /**
     * The location on screen where this MapMenu was last displayed. 
     */
    private Point latestScreenLocation;
    
    /**
     * Constructor
     */
    public MapMenuCommon() {
        super();

        // Past-track menu items
        showPastTracks = new SetShowPastTracks("Show all past-tracks", true);
        showPastTracks.addActionListener(this);
        hidePastTracks = new SetShowPastTracks("Hide all past-tracks", false);
        hidePastTracks.addActionListener(this);
        aisTogglePastTrack = new ToggleShowPastTrack();
        aisTogglePastTrack.addActionListener(this);
        aisClearPastTrack = new ClearPastTrack();
        aisClearPastTrack.addActionListener(this);

        // Intended route
        intendedRouteToggle = new IntendedRouteToggle();
        intendedRouteToggle.addActionListener(this);
        hideIntendedRoutes = new HideAllIntendedRoutes("Hide all intended routes");
        hideIntendedRoutes.addActionListener(this);
        showIntendedRoutes = new ShowAllIntendedRoutes("Show all intended routes");
        showIntendedRoutes.addActionListener(this);
        intendedRouteColor = new IntendedRouteColor();

        // Route
        routeHide = new RouteHide("Hide route");
        routeHide.addActionListener(this);
        routeCopy = new RouteCopy("Copy route");
        routeCopy.addActionListener(this);
        routeReverse = new RouteReverse("Reverse route");
        routeReverse.addActionListener(this);
        routeDelete = new RouteDelete("Delete route", this);
        routeDelete.addActionListener(this);
        routeRequestMetoc = new RouteRequestMetoc("Request METOC...");
        routeRequestMetoc.addActionListener(this);
        routeMetocProperties = new RouteMetocProperties("METOC properties...");
        routeMetocProperties.addActionListener(this);
        routeShowMetocToggle = new RouteShowMetocToggle();
        routeShowMetocToggle.addActionListener(this);
        routeProperties = new RouteProperties("Route properties...");
        routeProperties.addActionListener(this);
        routeAppendWaypoint = new RouteAppendWaypoint("Append waypoint");
        routeAppendWaypoint.addActionListener(this);
        routeLegInsertWaypoint = new RouteLegInsertWaypoint("Insert waypoint here");
        routeLegInsertWaypoint.addActionListener(this);
        routeWaypointActivateToggle = new RouteWaypointActivateToggle("Activate waypoint");
        routeWaypointActivateToggle.addActionListener(this);
        routeWaypointDelete = new RouteWaypointDelete("Delete waypoint");
        routeWaypointDelete.addActionListener(this);
        routeWaypointEditEta = new RouteWaypointEditEta("Set waypoint ETA...");
        routeWaypointEditEta.addActionListener(this);
        
        // MSI menu items
        msiNmAcknowledge = new MsiNmAcknowledge("Acknowledge MSI-NM");
        msiNmAcknowledge.addActionListener(this);
        msiNmDetails = new MsiNmDetails("Show MSI-NM details...");
        msiNmDetails.addActionListener(this);
        msiNmZoomTo = new MsiNmZoomTo("Zoom to MSI-NM");
        msiNmZoomTo.addActionListener(this);
        
        centerVesselTarget = new CenterVesselTarget("Jump to ship");
        centerVesselTarget.addActionListener(this);
        
        sendChatMessage = new SendChatMessage();
        sendChatMessage.addActionListener(this);
        
        // using treemap so scale levels are always sorted
        map = new TreeMap<>();
        scaleMenu = new JMenu("Scale");

        
    }

    /**
     * Adds the general menu to the right-click menu. Remember to always add
     * this first, when creating specific menus.
     * 
     * @param alone alone or not
     */
    public abstract void generalMenu(boolean alone);
    
    /**
     * Updates the scale menu based on the current scale
     */
    public void generateScaleMenu() {
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

        
        
        revalidate();
    }

    /**
     * Creates the route leg menu
     * @param routeIndex the route index
     * @param routeLeg the route leg
     * @param point the mouse localtion
     */
    public abstract void routeLegMenu(int routeIndex, RouteLeg routeLeg, Point point);
    
    /**
     * Creates the route way point menu
     * @param routeIndex the route index
     * @param routeWaypointIndex the route way point index
     */
    public abstract void routeWaypointMenu(int routeIndex, int routeWaypointIndex);
    
    /**
     * Creates the route edit menu
     */
    public abstract void routeEditMenu();

    /**
     * Creates the intended route menu
     */
    public void intendedRouteMenu(final IntendedRouteGraphic routeGraphics) {
        removeAll();

        // Toggle show intended route
        addIntendedRouteToggle(routeGraphics.getIntendedRoute());
        
        centerVesselTarget.setVesselPosition(routeGraphics.getVesselPostion());
        centerVesselTarget.setMapBean(mapBean);
        add(centerVesselTarget);
        
        // Add a color selector menu item
        intendedRouteColor.init(this, routeGraphics, intendedRouteHandler);
        add(intendedRouteColor);

        revalidate();
        generalMenu(false);
    }
    
    /**
     * Adds the toggle-visibility menu item for the intended route
     * @param intendedRoute the intended route
     */
    protected void addIntendedRouteToggle(IntendedRoute intendedRoute) {
        if (intendedRoute != null) {
            intendedRouteToggle.setIntendedRouteHandler(intendedRouteHandler);
            intendedRouteToggle.setIntendedRoute(intendedRoute);
    
            intendedRouteToggle.setEnabled(intendedRoute.hasRoute());
            intendedRouteToggle.setText(intendedRoute.isVisible() 
                    ? "Hide intended route" 
                    : "Show intended route");
            checkIntendedRouteItems(intendedRouteToggle);
            add(intendedRouteToggle);
        }
    }
    
    /**
     * Checks if the intended route layer is visible. If not,
     * the menu items are disabled.
     * @param items the intended route menu items to check
     */
    protected void checkIntendedRouteItems(JMenuItem... items) {
        if (!EPD.getInstance().getSettings().getCloudSettings().isShowIntendedRoute()) {
            for (JMenuItem item : items) {
                item.setEnabled(false);
            }
        }
    }
    
    /**
     * Builds the maritime safety information menu
     * 
     * @param selectedGraphic The selected graphic (containing the MIS message)
     */
    public void msiMenu(MsiNmNmSymbolGraphic selectedGraphic) {
        removeAll();

        MsiNmNotification message = selectedGraphic.getMsiNmMessage();
        msiNmDetails.setMsiNmMessage(message);

        add(msiNmDetails);

        Boolean isAcknowledged = message.isAcknowledged();
        msiNmAcknowledge.setMsiNmHandler(msiNmHandler);
        msiNmAcknowledge.setEnabled(!isAcknowledged);
        msiNmAcknowledge.setMsiNmMessage(selectedGraphic.getMsiNmMessage());
        add(msiNmAcknowledge);

        revalidate();
        generalMenu(false);
    }

    /**
     * Builds the maritime safety information directional menu
     * 
     * @param selectedGraphic The selected graphic (containing the MIS message)
     * @param msiLayer the MSI layer
     */    
    public void msiDirectionalMenu(MsiNmDirectionalIcon selectedGraphic, MsiNmLayerCommon msiLayer) {
        removeAll();

        MsiNmNotification message = selectedGraphic.getMessage();
        msiNmDetails.setMsiNmMessage(message);
        add(msiNmDetails);

        msiNmZoomTo.setMsiLayer(msiLayer);
        msiNmZoomTo.setMessage(selectedGraphic.getMessage());
        add(msiNmZoomTo);

        revalidate();
        generalMenu(false);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        action = (IMapMenuAction) e.getSource();        
        action.doAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        else if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
        } 
        else if (obj instanceof IntendedRouteHandlerCommon) {
            intendedRouteHandler = (IntendedRouteHandlerCommon)obj;
        }
        if (obj instanceof MsiNmServiceHandlerCommon) {
            msiNmHandler = (MsiNmServiceHandlerCommon) obj;
        }
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

    @Override
    public void setVisible(boolean visible){
        if(this.isVisible()) {
            // log latest location every time this MapMenu is made visible.
            this.latestScreenLocation = this.getLocationOnScreen();
        }
        super.setVisible(visible);
    }

    /**
     * Get the position on screen where this MapMenu was last shown.
     * @return The latest position or null if this MapMenu was never shown.
     */
    public Point getLatestVisibleLocation() {
        return latestScreenLocation;
    }
}
