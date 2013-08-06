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
public class SARPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JLabel wptTitleLabel;
    private JLabel brgTitleLabel;
    private JLabel rngTitleLabel;
    private JLabel ttgRouteTitleLabel;
    private JLabel etaNextTitleLabel;
    private JLabel etaRouteTitleLabel;
    private JLabel etaRouteLabel;
    private JLabel etaNextLabel;
    private JLabel ttgRouteLabel;
    private JLabel rngLabel;
    private JLabel brgLabel;
    private JLabel wptLabel;
    private RouteManager routeManager;
    private JLabel lblNewLabel;
    private JLabel lblNewLabel_1;

    public SARPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{10, 10, 0};
        gridBagLayout.rowHeights = new int[]{20, 16, 16, 16, 16, 16, 16, 16, 10};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);
        JLabel lblSAR = new JLabel("Search And Rescue");
        lblSAR.setHorizontalAlignment(SwingConstants.CENTER);
        lblSAR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints gbc_lblSAR = new GridBagConstraints();
        gbc_lblSAR.anchor = GridBagConstraints.NORTH;
        gbc_lblSAR.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSAR.insets = new Insets(0, 0, 5, 0);
        gbc_lblSAR.gridwidth = 2;
        gbc_lblSAR.gridx = 0;
        gbc_lblSAR.gridy = 0;
        add(lblSAR, gbc_lblSAR);
        
        wptTitleLabel = new JLabel("Type:");
        wptTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_wptTitleLabel = new GridBagConstraints();
        gbc_wptTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_wptTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_wptTitleLabel.gridx = 0;
        gbc_wptTitleLabel.gridy = 1;
        add(wptTitleLabel, gbc_wptTitleLabel);
        
        wptLabel = new JLabel("Rapid Response");
        wptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_wptLabel = new GridBagConstraints();
        gbc_wptLabel.anchor = GridBagConstraints.NORTH;
        gbc_wptLabel.insets = new Insets(0, 0, 5, 0);
        gbc_wptLabel.gridx = 1;
        gbc_wptLabel.gridy = 1;
        add(wptLabel, gbc_wptLabel);
        
        brgTitleLabel = new JLabel("Time of Last Known Position:");
        brgTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_brgTitleLabel = new GridBagConstraints();
        gbc_brgTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_brgTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_brgTitleLabel.gridx = 0;
        gbc_brgTitleLabel.gridy = 2;
        add(brgTitleLabel, gbc_brgTitleLabel);
        
        brgLabel = new JLabel("5.03");
        brgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_brgLabel = new GridBagConstraints();
        gbc_brgLabel.anchor = GridBagConstraints.NORTH;
        gbc_brgLabel.insets = new Insets(0, 0, 5, 0);
        gbc_brgLabel.gridx = 1;
        gbc_brgLabel.gridy = 2;
        add(brgLabel, gbc_brgLabel);
        
        rngTitleLabel = new JLabel("Time of Commence Search Start:");
        rngTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_rngTitleLabel = new GridBagConstraints();
        gbc_rngTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_rngTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_rngTitleLabel.gridx = 0;
        gbc_rngTitleLabel.gridy = 3;
        add(rngTitleLabel, gbc_rngTitleLabel);
        
        rngLabel = new JLabel("2.61");
        rngLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_rngLabel = new GridBagConstraints();
        gbc_rngLabel.anchor = GridBagConstraints.NORTH;
        gbc_rngLabel.insets = new Insets(0, 0, 5, 0);
        gbc_rngLabel.gridx = 1;
        gbc_rngLabel.gridy = 3;
        add(rngLabel, gbc_rngLabel);
        
        lblNewLabel = new JLabel("Initial Position Error (X):");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 4;
        add(lblNewLabel, gbc_lblNewLabel);
        
        lblNewLabel_1 = new JLabel("New label");
        GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
        gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblNewLabel_1.gridx = 1;
        gbc_lblNewLabel_1.gridy = 4;
        add(lblNewLabel_1, gbc_lblNewLabel_1);
        
        ttgRouteTitleLabel = new JLabel("SRU Navigation Error (Y):");
        ttgRouteTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_ttgRouteTitleLabel = new GridBagConstraints();
        gbc_ttgRouteTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_ttgRouteTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_ttgRouteTitleLabel.gridx = 0;
        gbc_ttgRouteTitleLabel.gridy = 5;
        add(ttgRouteTitleLabel, gbc_ttgRouteTitleLabel);
        
        ttgRouteLabel = new JLabel("Down wind: 56°17,3 N    7°59,1 E\t");
        ttgRouteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_ttgRouteLabel = new GridBagConstraints();
        gbc_ttgRouteLabel.anchor = GridBagConstraints.NORTH;
        gbc_ttgRouteLabel.insets = new Insets(0, 0, 5, 0);
        gbc_ttgRouteLabel.gridx = 1;
        gbc_ttgRouteLabel.gridy = 5;
        add(ttgRouteLabel, gbc_ttgRouteLabel);
        
        etaNextTitleLabel = new JLabel("Area:");
        etaNextTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_etaNextTitleLabel = new GridBagConstraints();
        gbc_etaNextTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_etaNextTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_etaNextTitleLabel.gridx = 0;
        gbc_etaNextTitleLabel.gridy = 6;
        add(etaNextTitleLabel, gbc_etaNextTitleLabel);
        
        etaNextLabel = new JLabel("27 nm^2 (excl. Decution of Land)");
        etaNextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_etaNextLabel = new GridBagConstraints();
        gbc_etaNextLabel.anchor = GridBagConstraints.NORTH;
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
            ttgRouteLabel.setText("N/A");
            etaNextLabel.setText("N/A");
            etaRouteLabel.setText("N/A");
        }else{
        ActiveRoute activeRoute = routeManager.getActiveRoute();
        wptLabel.setText(activeRoute.getActiveWp().getName());
        brgLabel.setText(Formatter.formatDegrees(activeRoute.getActiveWpBrg(), 1));
        rngLabel.setText(Formatter.formatDistNM(activeRoute.getActiveWpRng()));
        ttgRouteLabel.setText(Formatter.formatTime(activeRoute.calcTtg()));
        etaNextLabel.setText(Formatter.formatShortDateTime(activeRoute.getActiveWaypointEta()));
        etaRouteLabel.setText(Formatter.formatShortDateTime(activeRoute.calculateEta()));
        }
    }
    
    public void setRouteManager(RouteManager routeManager) {
        this.routeManager = routeManager;
    }
}
