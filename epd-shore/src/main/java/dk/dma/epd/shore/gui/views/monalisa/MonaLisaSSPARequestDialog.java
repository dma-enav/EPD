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
package dk.dma.epd.shore.gui.views.monalisa;

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
import dk.dma.epd.shore.layers.voyage.VoyageHandlingLayer;
import dk.dma.epd.shore.service.MonaLisaRouteOptimization;

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
    MonaLisaRouteOptimization monaLisaRouteExchange;
    boolean removeIntermediateETA;
    float draft;
    int ukc;
    int timeout;
    List<Boolean> selectedWp;
    boolean showInput;
    boolean showOutput;
    VoyageHandlingLayer voyageHandlingLayer;

    public MonaLisaSSPARequestDialog(Window parent,
            Route route, MonaLisaRouteOptimization monaLisaRouteExchange,
            boolean removeIntermediateETA, float draft, int ukc, int timeout,
            List<Boolean> selectedWp, boolean showInput, boolean showOutput, VoyageHandlingLayer voyageHandlingLayer) {
        super(parent, "Request Mona Lisa Route Exchange");
        
        this.voyageHandlingLayer = voyageHandlingLayer;
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

    public static void requestRoute(Window parent,
            Route route, MonaLisaRouteOptimization monaLisaRouteExchange,
            boolean removeIntermediateETA, float draft, int ukc, int timeout,
            List<Boolean> selectedWp, boolean showInput, boolean showOutput, VoyageHandlingLayer voyageHandlingLayer) {

        
        MonaLisaSSPARequestDialog monaLisaRequestDialog = new MonaLisaSSPARequestDialog(
                parent, route, monaLisaRouteExchange,
                removeIntermediateETA, draft, ukc, timeout, selectedWp,
                showInput, showOutput, voyageHandlingLayer);

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
                    selectedWp, showInput, showOutput, voyageHandlingLayer);

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
