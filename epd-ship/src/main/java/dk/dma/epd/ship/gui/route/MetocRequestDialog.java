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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.ship.route.RouteManager;
import dk.frv.enav.common.xml.metoc.MetocForecast;

/**
 * Dialog shown when requesting METOC
 */
public class MetocRequestDialog extends JDialog implements Runnable, ActionListener {
    
    private static final long serialVersionUID = 1L;
    
    private RouteManager routeManager;
    private Route route;
    private Window parent;
    private JLabel statusLbl;
    private JButton cancelBtn;
    private Boolean cancelReq = false;
    
    public MetocRequestDialog(Window parent, RouteManager routeManager, Route route) {
        super(parent, "Request METOC");
        this.routeManager = routeManager;
        this.route = route;
        this.parent = parent;
        
        initGui();        
    }
    
    public static void requestMetoc(Window parent, RouteManager routeManager, Route route) {
        MetocRequestDialog metocRequestDialog = new MetocRequestDialog(parent, routeManager, route);
        metocRequestDialog.doRequestMetoc();
        metocRequestDialog = null;
    }
    
    private void doRequestMetoc() {
        // Start thread
        new Thread(this).start();
        
        // Set dialog visible
        setVisible(true);
    }
    
    @Override
    public void run() {
        ShoreServiceException error = null;
        try {
            routeManager.requestRouteMetoc(route);
        } catch (ShoreServiceException e) {
            error = e;
        }
        
        if (isCancelReq()) {
            route.removeMetoc();
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
            return;
        }
        
        if (error == null) {
            routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
        }
        
        // Close dialog        
        setVisible(false);        
        
        // Give response        
        if (error != null) {
            String text = error.getMessage();
            if (error.getExtraMessage() != null) {
                text += ": " + error.getExtraMessage();
            }
            JOptionPane.showMessageDialog(parent, text, "Shore service error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            MetocForecast metocForecast = route.getMetocForecast();
            JOptionPane.showMessageDialog(parent, "Received " + metocForecast.getForecasts().size() + " METOC forecast points", "Shore service result",
                    JOptionPane.INFORMATION_MESSAGE);
        }        
    }
    
    private void initGui() {
        setSize(280, 130);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        getContentPane().setLayout(null);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(96, 58, 80, 23);
        getContentPane().add(cancelBtn);
        cancelBtn.addActionListener(this);
        
        statusLbl = new JLabel("Getting METOC from shore server ...");
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statusLbl.setBounds(10, 23, 244, 14);
        getContentPane().add(statusLbl);
    }
    
    private boolean isCancelReq() {
        synchronized (cancelReq) {
            return cancelReq.booleanValue();
        }
    }
    
    private void setCancelReq(boolean cancel) {
        synchronized (cancelReq) {
            this.cancelReq = cancel;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelBtn) {
            setCancelReq(true);
            setVisible(false);
            route.removeMetoc();
            routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        }
    }

}
