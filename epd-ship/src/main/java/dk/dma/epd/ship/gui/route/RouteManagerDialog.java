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
package dk.dma.epd.ship.gui.route;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Route manager dialog
 */
public class RouteManagerDialog extends JDialog implements IRoutesUpdateListener {

    private static final long serialVersionUID = 1L;

    protected RouteManager routeManager;
    
    private JTabbedPane tabbedPane;
    private  RouteManagerPanel routePanel;

    public RouteManagerDialog(JFrame parent) {
        super(parent, "Route Manager", false);
        routeManager = EPDShip.getInstance().getRouteManager();

        setSize(600, 430);
//        this.setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.NORTH);



        routePanel = new RouteManagerPanel(routeManager, this);
        
        
        tabbedPane.addTab("Routes", null, routePanel, null);

        
        routeManager.addListener(this);
        

    }

    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        routePanel.updateTable();
    }

}
