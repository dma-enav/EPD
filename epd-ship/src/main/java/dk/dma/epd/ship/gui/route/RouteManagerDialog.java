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
import java.awt.Color;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;

/**
 * Route manager dialog
 */
public class RouteManagerDialog extends JDialog implements IRoutesUpdateListener {

    private static final long serialVersionUID = 1L;

    private  RouteManagerPanel routePanel;

    /**
     * Constructor
     * @param parent the parent frame
     */
    public RouteManagerDialog(JFrame parent) {
        super(parent, "Route Manager", false);

        setSize(600, 430);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        // Add the route panel
        routePanel = new RouteManagerPanel(this);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(routePanel, BorderLayout.CENTER);
        
        EPD.getInstance().getRouteManager().addListener(this);
        
        getRootPane().setDefaultButton(routePanel.getCloseButton());
    
        setOpacity((float) 0.95);


        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        routePanel.updateTable();
    }
    
    @Override
    public void setVisible(boolean visible){
        super.setVisible(visible);

        setOpacity((float) 0.95);
        
//        
//        getRootPane ().setOpaque (false);
//        
//        getContentPane ().setBackground(new Color (48, 48, 48, 200));
        
        
//        routePanel.getRootPane ().setOpaque (false);
//        routePanel.setBackground(new Color (48, 48, 48, 200));
    
        
    
    }
    
    @Override
    public void setLocation(int x, int y){
        super.setLocation(x, y);
//        this.repaint();
//        getRootPane ().setOpaque (false);
//        getContentPane ().setBackground(new Color (48, 48, 48, 200));
        
//        System.out.println("Set location yo");

//
//        if (routePanel != null){
//            routePanel.getRootPane ().setOpaque (false);
//            routePanel.setBackground(new Color (48, 48, 48, 200));
//        }
        
    }
    
}
