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
package dk.dma.epd.common.prototype.gui;

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

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.menuitems.CenterVesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.ClearPastTrack;
import dk.dma.epd.common.prototype.gui.menuitems.HideAllIntendedRoutes;
import dk.dma.epd.common.prototype.gui.menuitems.RouteAppendWaypoint;
import dk.dma.epd.common.prototype.gui.menuitems.RouteCopy;
import dk.dma.epd.common.prototype.gui.menuitems.RouteDelete;
import dk.dma.epd.common.prototype.gui.menuitems.RouteHide;
import dk.dma.epd.common.prototype.gui.menuitems.RouteLegInsertWaypoint;
import dk.dma.epd.common.prototype.gui.menuitems.RouteProperties;
import dk.dma.epd.common.prototype.gui.menuitems.RouteRequestMetoc;
import dk.dma.epd.common.prototype.gui.menuitems.RouteReverse;
import dk.dma.epd.common.prototype.gui.menuitems.RouteShowMetocToggle;
import dk.dma.epd.common.prototype.gui.menuitems.RouteWaypointActivateToggle;
import dk.dma.epd.common.prototype.gui.menuitems.RouteWaypointDelete;
import dk.dma.epd.common.prototype.gui.menuitems.ShowAllIntendedRoutes;
import dk.dma.epd.common.prototype.gui.menuitems.IntendedRouteToggle;
import dk.dma.epd.common.prototype.gui.menuitems.MsiAcknowledge;
import dk.dma.epd.common.prototype.gui.menuitems.SetShowPastTracks;
import dk.dma.epd.common.prototype.gui.menuitems.ToggleShowPastTrack;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

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
    protected RouteShowMetocToggle routeShowMetocToggle;
    protected RouteLegInsertWaypoint routeLegInsertWaypoint;
    protected RouteWaypointActivateToggle routeWaypointActivateToggle;
    protected RouteWaypointDelete routeWaypointDelete;

    protected IntendedRouteToggle intendedRouteToggle;
    protected HideAllIntendedRoutes hideIntendedRoutes;
    protected ShowAllIntendedRoutes showIntendedRoutes;
    
    protected MsiAcknowledge msiAcknowledge;

    protected CenterVesselTarget centerVesselTarget;
    
    protected JMenu scaleMenu;
    protected Map<Integer, String> map;
    protected MapBean mapBean;
    
    protected AisHandlerCommon aisHandler;
    
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

        // Route
        routeHide = new RouteHide("Hide route");
        routeHide.addActionListener(this);
        routeCopy = new RouteCopy("Copy route");
        routeCopy.addActionListener(this);
        routeReverse = new RouteReverse("Reverse route");
        routeReverse.addActionListener(this);
        routeDelete = new RouteDelete("Delete route", this);
        routeDelete.addActionListener(this);
        routeRequestMetoc = new RouteRequestMetoc("Request METOC");
        routeRequestMetoc.addActionListener(this);
        routeShowMetocToggle = new RouteShowMetocToggle();
        routeShowMetocToggle.addActionListener(this);
        routeProperties = new RouteProperties("Route properties");
        routeProperties.addActionListener(this);
        routeAppendWaypoint = new RouteAppendWaypoint("Append waypoint");
        routeAppendWaypoint.addActionListener(this);
        routeLegInsertWaypoint = new RouteLegInsertWaypoint("Insert waypoint here");
        routeLegInsertWaypoint.addActionListener(this);
        routeWaypointActivateToggle = new RouteWaypointActivateToggle("Activate waypoint");
        routeWaypointActivateToggle.addActionListener(this);
        routeWaypointDelete = new RouteWaypointDelete("Delete waypoint");
        routeWaypointDelete.addActionListener(this);
        
        // MSI menu items
        msiAcknowledge = new MsiAcknowledge("Acknowledge MSI");
        msiAcknowledge.addActionListener(this);
        
        centerVesselTarget = new CenterVesselTarget("Jump to ship");
        centerVesselTarget.addActionListener(this);
        
        // using treemap so scale levels are always sorted
        map = new TreeMap<Integer, String>();
        scaleMenu = new JMenu("Scale");

    }

    /**
     * Adds the general menu to the right-click menu. Remember to always add
     * this first, when creating specific menus.
     * 
     * @param alone
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
