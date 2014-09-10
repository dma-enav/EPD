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
package dk.dma.epd.ship.gui.route;

import java.awt.BorderLayout;

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

    private RouteManagerPanel routePanel;

    /**
     * Constructor
     * 
     * @param parent
     *            the parent frame
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

        try {
            setOpacity((float) 0.95);
        } catch (Exception E) {
            System.out.println("Failed to set opacity, ignore");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void routesChanged(RoutesUpdateEvent e) {
        routePanel.updateTable();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        try {
            setOpacity((float) 0.95);
        } catch (Exception E) {
            System.out.println("Failed to set opacity, ignore");
        }



    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        // this.repaint();
        // getRootPane ().setOpaque (false);
        // getContentPane ().setBackground(new Color (48, 48, 48, 200));

        // System.out.println("Set location yo");

        //
        // if (routePanel != null){
        // routePanel.getRootPane ().setOpaque (false);
        // routePanel.setBackground(new Color (48, 48, 48, 200));
        // }

    }

}
