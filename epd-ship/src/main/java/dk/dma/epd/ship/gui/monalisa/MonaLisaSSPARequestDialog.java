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
package dk.dma.epd.ship.gui.monalisa;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.monalisa.MonaLisaOptimizationResponse;
import dk.dma.epd.common.prototype.monalisa.MonaLisaRouteOptimizaton;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Dialog shown when requesting Mona Lisa Route Exchange
 */
public class MonaLisaSSPARequestDialog extends JDialog implements Runnable,
        ActionListener {

    private static final long serialVersionUID = 1L;

    // private RouteManager routeManager;
    private Route route;
    private Window parent;
    private JLabel statusLbl;
    private JButton cancelBtn;
    private Boolean cancelReq = false;
    MonaLisaRouteOptimizaton monaLisaRouteExchange;
    boolean removeIntermediateETA;
    float draft;
    int ukc;
    int timeout;
    List<Boolean> selectedWp;
    boolean showInput;
    boolean showOutput;

    public MonaLisaSSPARequestDialog(Window parent, RouteManager routeManager,
            Route route, MonaLisaRouteOptimizaton monaLisaRouteExchange,
            boolean removeIntermediateETA, float draft, int ukc, int timeout,
            List<Boolean> selectedWp, boolean showInput, boolean showOutput) {
        super(parent, "Request Mona Lisa Route Exchange");
        // this.routeManager = routeManager;
        this.route = route;
        this.parent = parent;
        this.monaLisaRouteExchange = monaLisaRouteExchange;
        this.removeIntermediateETA = removeIntermediateETA;
        this.draft = draft;
        this.ukc = ukc;
        this.timeout = timeout;
        this.selectedWp = selectedWp;
        this.showInput = showInput;
        this.showOutput = showOutput;

        initGui();
    }

    public static void requestRoute(Window parent, RouteManager routeManager,
            Route route, MonaLisaRouteOptimizaton monaLisaRouteExchange,
            boolean removeIntermediateETA, float draft, int ukc, int timeout,
            List<Boolean> selectedWp, boolean showInput, boolean showOutput) {

        MonaLisaSSPARequestDialog monaLisaRequestDialog = new MonaLisaSSPARequestDialog(
                parent, routeManager, route, monaLisaRouteExchange,
                removeIntermediateETA, draft, ukc, timeout, selectedWp,
                showInput, showOutput);

        monaLisaRequestDialog.doRequestRoute();
        // monaLisaRequestDialog = null;

    }

    private void doRequestRoute() {
        // Start thread
        new Thread(this).start();

        // Set dialog visible
        setVisible(true);
    }

    @Override
    public void run() {

        if (monaLisaRouteExchange != null || route != null
                || selectedWp != null) {

            MonaLisaOptimizationResponse response = monaLisaRouteExchange.makeRouteRequest(
                    route, removeIntermediateETA, draft, ukc, timeout,
                    selectedWp, showInput, showOutput);

            // Close dialog
            setVisible(false);

            // Give response
            JOptionPane.showMessageDialog(parent, response.getType() + " " + response.getResponse(),
                    "Mona Lisa response", JOptionPane.INFORMATION_MESSAGE);

            // ShoreServiceException error = null;
            // try {
            // routeManager.requestRouteMetoc(route);
            // } catch (ShoreServiceException e) {
            // error = e;
            // }
            //
            // if (isCancelReq()) {
            // route.removeMetoc();
            // routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
            // return;
            // }
            //
            // if (error == null) {
            // routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
            // }
            //
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

        statusLbl = new JLabel("Getting Optimized Route from server ...");
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statusLbl.setBounds(10, 23, 244, 14);
        getContentPane().add(statusLbl);
    }

    // private boolean isCancelReq() {
    // synchronized (cancelReq) {
    // return cancelReq.booleanValue();
    // }
    // }

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
        }
    }

}
