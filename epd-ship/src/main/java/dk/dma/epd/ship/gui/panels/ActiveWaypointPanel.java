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
package dk.dma.epd.ship.gui.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Active waypoint panel in sensor panel
 */
public class ActiveWaypointPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JLabel wptTitleLabel;
    private JLabel brgTitleLabel;
    private JLabel rngTitleLabel;
    private JLabel ttgLegTitleLabel;
    private JLabel ttgRouteTitleLabel;
    private JLabel etaNextTitleLabel;
    private JLabel etaRouteTitleLabel;
    private JLabel etaRouteLabel;
    private JLabel etaNextLabel;
    private JLabel ttgRouteLabel;
    private JLabel ttgLegLabel;
    private JLabel rngLabel;
    private JLabel brgLabel;
    private JLabel wptLabel;
    private RouteManager routeManager;

    public ActiveWaypointPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{10, 10, 0};
        gridBagLayout.rowHeights = new int[]{20, 16, 16, 16, 16, 16, 16, 16, 10};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        JLabel lblActiveWaypoint = new JLabel("Active Waypoint");
        lblActiveWaypoint.setHorizontalAlignment(SwingConstants.CENTER);
        lblActiveWaypoint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_lblActiveWaypoint = new GridBagConstraints();
        gbc_lblActiveWaypoint.anchor = GridBagConstraints.NORTH;
        gbc_lblActiveWaypoint.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblActiveWaypoint.insets = new Insets(0, 0, 5, 0);
        gbc_lblActiveWaypoint.gridwidth = 2;
        gbc_lblActiveWaypoint.gridx = 0;
        gbc_lblActiveWaypoint.gridy = 0;
        add(lblActiveWaypoint, gbc_lblActiveWaypoint);
        
        wptTitleLabel = new JLabel("WPT");
        wptTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_wptTitleLabel = new GridBagConstraints();
        gbc_wptTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_wptTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_wptTitleLabel.gridx = 0;
        gbc_wptTitleLabel.gridy = 1;
        add(wptTitleLabel, gbc_wptTitleLabel);
        
        wptLabel = new JLabel("N/A");
        wptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_wptLabel = new GridBagConstraints();
        gbc_wptLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_wptLabel.insets = new Insets(0, 0, 5, 0);
        gbc_wptLabel.gridx = 1;
        gbc_wptLabel.gridy = 1;
        add(wptLabel, gbc_wptLabel);
        
        brgTitleLabel = new JLabel("BRG");
        brgTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_brgTitleLabel = new GridBagConstraints();
        gbc_brgTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_brgTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_brgTitleLabel.gridx = 0;
        gbc_brgTitleLabel.gridy = 2;
        add(brgTitleLabel, gbc_brgTitleLabel);
        
        brgLabel = new JLabel("N/A");
        brgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_brgLabel = new GridBagConstraints();
        gbc_brgLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_brgLabel.insets = new Insets(0, 0, 5, 0);
        gbc_brgLabel.gridx = 1;
        gbc_brgLabel.gridy = 2;
        add(brgLabel, gbc_brgLabel);
        
        rngTitleLabel = new JLabel("RNG");
        rngTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_rngTitleLabel = new GridBagConstraints();
        gbc_rngTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_rngTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_rngTitleLabel.gridx = 0;
        gbc_rngTitleLabel.gridy = 3;
        add(rngTitleLabel, gbc_rngTitleLabel);
        
        rngLabel = new JLabel("N/A");
        rngLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_rngLabel = new GridBagConstraints();
        gbc_rngLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_rngLabel.insets = new Insets(0, 0, 5, 0);
        gbc_rngLabel.gridx = 1;
        gbc_rngLabel.gridy = 3;
        add(rngLabel, gbc_rngLabel);
        
        ttgLegTitleLabel = new JLabel("TTG leg");
        ttgLegTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_ttgLegTitleLabel = new GridBagConstraints();
        gbc_ttgLegTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_ttgLegTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_ttgLegTitleLabel.gridx = 0;
        gbc_ttgLegTitleLabel.gridy = 4;
        add(ttgLegTitleLabel, gbc_ttgLegTitleLabel);
        
        ttgLegLabel = new JLabel("N/A");
        ttgLegLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_ttgLegLabel = new GridBagConstraints();
        gbc_ttgLegLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_ttgLegLabel.insets = new Insets(0, 0, 5, 0);
        gbc_ttgLegLabel.gridx = 1;
        gbc_ttgLegLabel.gridy = 4;
        add(ttgLegLabel, gbc_ttgLegLabel);
        
        ttgRouteTitleLabel = new JLabel("TTG route");
        ttgRouteTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_ttgRouteTitleLabel = new GridBagConstraints();
        gbc_ttgRouteTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_ttgRouteTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_ttgRouteTitleLabel.gridx = 0;
        gbc_ttgRouteTitleLabel.gridy = 5;
        add(ttgRouteTitleLabel, gbc_ttgRouteTitleLabel);
        
        ttgRouteLabel = new JLabel("N/A");
        ttgRouteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_ttgRouteLabel = new GridBagConstraints();
        gbc_ttgRouteLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_ttgRouteLabel.insets = new Insets(0, 0, 5, 0);
        gbc_ttgRouteLabel.gridx = 1;
        gbc_ttgRouteLabel.gridy = 5;
        add(ttgRouteLabel, gbc_ttgRouteLabel);
        
        etaNextTitleLabel = new JLabel("ETA next");
        etaNextTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_etaNextTitleLabel = new GridBagConstraints();
        gbc_etaNextTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_etaNextTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_etaNextTitleLabel.gridx = 0;
        gbc_etaNextTitleLabel.gridy = 6;
        add(etaNextTitleLabel, gbc_etaNextTitleLabel);
        
        etaNextLabel = new JLabel("N/A");
        etaNextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_etaNextLabel = new GridBagConstraints();
        gbc_etaNextLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_etaNextLabel.insets = new Insets(0, 0, 5, 0);
        gbc_etaNextLabel.gridx = 1;
        gbc_etaNextLabel.gridy = 6;
        add(etaNextLabel, gbc_etaNextLabel);
        
        etaRouteTitleLabel = new JLabel("ETA route");
        etaRouteTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_etaRouteTitleLabel = new GridBagConstraints();
        gbc_etaRouteTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_etaRouteTitleLabel.insets = new Insets(0, 0, 0, 5);
        gbc_etaRouteTitleLabel.gridx = 0;
        gbc_etaRouteTitleLabel.gridy = 7;
        add(etaRouteTitleLabel, gbc_etaRouteTitleLabel);
        
        etaRouteLabel = new JLabel("N/A");
        etaRouteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_etaRouteLabel = new GridBagConstraints();
        gbc_etaRouteLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_etaRouteLabel.gridx = 1;
        gbc_etaRouteLabel.gridy = 7;
        add(etaRouteLabel, gbc_etaRouteLabel);
    }
    
    public void updateActiveNavData(){
        if (routeManager == null) {
            return;
        }
        if(!routeManager.isRouteActive()){
            wptLabel.setText("N/A");
            brgLabel.setText("N/A");
            rngLabel.setText("N/A");
            ttgLegLabel.setText("N/A");
            ttgRouteLabel.setText("N/A");
            etaNextLabel.setText("N/A");
            etaRouteLabel.setText("N/A");
        }else{
        ActiveRoute activeRoute = routeManager.getActiveRoute();
        wptLabel.setText(activeRoute.getActiveWp().getName());
        brgLabel.setText(Formatter.formatDegrees(activeRoute.getActiveWpBrg(), 1));
        rngLabel.setText(Formatter.formatDistNM(activeRoute.getActiveWpRng()));
        ttgLegLabel.setText(Formatter.formatTime(activeRoute.getActiveWpTtg()));
        ttgRouteLabel.setText(Formatter.formatTime(activeRoute.getRouteTtg()));
        etaNextLabel.setText(Formatter.formatShortDateTime(activeRoute.getActiveWaypointEta()));
        etaRouteLabel.setText(Formatter.formatShortDateTime(activeRoute.getEta()));
        }
    }
    
    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }
}
