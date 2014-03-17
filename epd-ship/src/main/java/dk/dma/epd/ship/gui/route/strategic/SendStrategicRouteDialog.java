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
package dk.dma.epd.ship.gui.route.strategic;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.route.RouteManager;
import dk.dma.epd.ship.service.StrategicRouteHandler;

/**
 * Sends a strategic route to an STCC
 */
public class SendStrategicRouteDialog extends ComponentDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SendStrategicRouteDialog.class);

    // Target panel
    private JComboBox<String> stccMmsiListComboBox = new JComboBox<>();

    // Route panel
    private JComboBox<String> routeListComboBox = new JComboBox<>();
    private JLabel routeLengthLbl = new JLabel("N/A");
    private JButton zoomBtn = new JButton("Zoom To", EPDShip.res().getCachedImageIcon("images/buttons/zoom.png"));

    // Request panel
    private JTextArea messageTxtField = new JTextArea("Route Approval Requested");

    // Send panel
    private JButton sendBtn = new JButton("Send", EPDShip.res().getCachedImageIcon("images/buttons/ok.png"));
    private JButton cancelBtn = new JButton("Cancel", EPDShip.res().getCachedImageIcon("images/buttons/cancel.png"));

    private RouteManager routeManager;
    private StrategicRouteHandler strategicRouteHandler;
    private IdentityHandler identityHandler;

    private Preferences prefs;
    private Route route;
    private long stccMmsi = -1;
    private boolean loading;
    private boolean wasVisible;

    private ArrayList<Integer> mmsiList = new ArrayList<Integer>();

    /**
     * Create the frame.
     */
    public SendStrategicRouteDialog(JFrame frame) {
        super(frame, "Send Route to STCC", Dialog.ModalityType.MODELESS);

        prefs = Preferences.userNodeForPackage(SendStrategicRouteDialog.class);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocation(100, 100);

        initGUI();

        // Hook up enter key to send and escape key to cancel
        getRootPane().setDefaultButton(sendBtn);
        getRootPane()
                .registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
    }

    /**
     * Initialize the GUI
     */
    public void initGUI() {

        JPanel content = new JPanel(new GridBagLayout());
        setContentPane(content);
        Insets insets5 = new Insets(5, 5, 5, 5);

        // *******************
        // *** Target panel
        // *******************
        JPanel stccPanel = new JPanel(new GridBagLayout());
        stccPanel.setBorder(new TitledBorder("STCC"));
        content.add(stccPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        stccMmsiListComboBox.addActionListener(this);
        stccPanel.add(new JLabel("STCC:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        stccPanel.add(stccMmsiListComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        // *******************
        // *** Route panel
        // *******************
        JPanel routePanel = new JPanel(new GridBagLayout());
        routePanel.setBorder(new TitledBorder("Route"));
        content.add(routePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routeListComboBox.addActionListener(this);
        routePanel.add(new JLabel("Route name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(routeListComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routePanel.add(new JLabel("Route length:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(routeLengthLbl, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        zoomBtn.addActionListener(this);
        routePanel.add(zoomBtn, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));

        // *******************
        // *** Request panel
        // *******************
        JPanel requestPanel = new JPanel(new GridBagLayout());
        requestPanel.setBorder(new TitledBorder("Sender"));
        content.add(requestPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));

        messageTxtField.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageTxtField);
        scrollPane.setMinimumSize(new Dimension(180, 40));
        scrollPane.setPreferredSize(new Dimension(180, 40));
        requestPanel.add(new JLabel("Message:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, NORTHWEST, NONE, insets5, 0, 0));
        requestPanel.add(scrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));

        // *******************
        // *** Send panel
        // *******************
        JPanel sendPanel = new JPanel();
        content.add(sendPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insets5, 0, 0));

        sendBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        sendPanel.add(sendBtn);
        sendPanel.add(cancelBtn);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }

        if (obj instanceof StrategicRouteHandler) {
            strategicRouteHandler = (StrategicRouteHandler) obj;
        }
        if (obj instanceof IdentityHandler) {
            identityHandler = (IdentityHandler) obj;
        }

    }

    /**
     * Called when one of the buttons are clicked or if one of the combo-boxes changes value
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (loading) {
            return;
        }

        if (ae.getSource() == stccMmsiListComboBox) {
            stccMmsiSelectionChanged();

        } else if (ae.getSource() == routeListComboBox) {
            routeSelectionChanged();

        } else if (ae.getSource() == zoomBtn && route.getWaypoints() != null) {
            EPD.getInstance().getMainFrame().getActiveChartPanel().zoomToWaypoints(route.getWaypoints());

        } else if (ae.getSource() == sendBtn) {
            sendRoute();

        } else if (ae.getSource() == cancelBtn || ae.getSource() == getRootPane()) {
            this.setVisible(false);
        }
    }

    /**
     * Called when the STCC MMSI selection has changed
     */
    private void stccMmsiSelectionChanged() {
        if (stccMmsiListComboBox.getSelectedItem() != null) {

            try {
                // stccMmsi = Long.valueOf((String) stccMmsiListComboBox.getSelectedItem());
                stccMmsi = mmsiList.get(stccMmsiListComboBox.getSelectedIndex());
            } catch (Exception e) {
                LOG.error("Failed to set mmsi " + stccMmsi, e);
            }
        }
    }

    /**
     * Called when the route selection has changed
     */
    private void routeSelectionChanged() {
        if (routeListComboBox.getSelectedItem() != null) {
            this.route = routeManager.getRoute(routeListComboBox.getSelectedIndex());
            routeLengthLbl.setText(Integer.toString(route.getWaypoints().size()));
        }
    }

    /**
     * Sends the current route to the current vessel
     */
    private void sendRoute() {
        if (route == null) {
            return;
        }

        if (stccMmsi == -1) {
            stccMmsi = mmsiList.get(stccMmsiListComboBox.getSelectedIndex());
        }

        LOG.info(String.format("Sending route to STCC MMSI %d", stccMmsi));

        try {
            strategicRouteHandler.sendStrategicRouteToSTCC(stccMmsi, route, messageTxtField.getText());
            messageTxtField.setText("Route Approval Requested");
        } catch (Exception e) {
            LOG.error("Failed to send route", e);
        }

        this.setVisible(false);

        // Save the MMSI in the preferences
        try {
            prefs.put("mmsi", String.valueOf(stccMmsi));
            prefs.sync();
        } catch (BackingStoreException e) {
            System.err.println("Failed saving paths" + e);
        }

        stccMmsi = -1;
        route = null;
    }

    /**
     * Sets the selected MMSI
     * 
     * @param mmsi
     *            the selected MMSI
     */
    public void setSelectedMMSI(long mmsi) {
        this.stccMmsi = mmsi;
        selectAndLoad();
    }

    /**
     * Sets the selected route
     * 
     * @param route
     *            the selected route
     */
    public void setSelectedRoute(Route route) {
        this.route = route;
        selectAndLoad();
    }

    /**
     * Loads data
     */
    private void loadData() {
        loading = true;

        // Initialize MMSI list
        mmsiList.clear();
        for (int i = 0; i < strategicRouteHandler.getStrategicRouteSTCCList().size(); i++) {
            if (MaritimeCloudUtils.isSTCC(strategicRouteHandler.getStrategicRouteSTCCList().get(i).getId())) {
                mmsiList.add(MaritimeCloudUtils.toMmsi(strategicRouteHandler.getStrategicRouteSTCCList().get(i).getId()));
            }
        }

        stccMmsiListComboBox.removeAllItems();
        for (Integer mmsi : mmsiList) {

            if (identityHandler.actorExists(mmsi)) {
                stccMmsiListComboBox.addItem(identityHandler.getActor(mmsi).getName() + " (" + String.valueOf(mmsi) + ")");
            } else {
                stccMmsiListComboBox.addItem(String.valueOf(mmsi));
            }

        }
        stccMmsiListComboBox.setEnabled(mmsiList.size() > 0);

        // Initialize routes
        routeListComboBox.removeAllItems();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            routeListComboBox.addItem(routeManager.getRoutes().get(i).getName()
                    + "                                                 " + i);
        }

        loading = false;
    }

    /**
     * Loads base data and updates the UI with the selected MMSI and route
     */
    private void selectAndLoad() {

        // The first time around, position relative to frame
        if (!wasVisible) {
            wasVisible = true;
            setLocationRelativeTo(getParent());
        }

        loadData();

        String mmsi = stccMmsi != -1 ? Long.toString(stccMmsi) : prefs.get("mmsi", null);
        if (mmsi != null && stccMmsiListComboBox.getItemCount() > 0) {
            for (int i = 0; i < stccMmsiListComboBox.getItemCount(); i++) {
                if (mmsiList.get(i).equals(mmsi)) {
                    stccMmsiListComboBox.setSelectedIndex(i);
                }
            }

        } else if (stccMmsi == -1 && stccMmsiListComboBox.getItemCount() > 0) {
            stccMmsi = mmsiList.get(stccMmsiListComboBox.getSelectedIndex());
            // Long.parseLong(stccMmsiListComboBox.getSelectedItem().toString());
        }

        if (route != null && EPDShip.getInstance().getRouteManager().getRoutes().size() > 0) {
            routeListComboBox.setEnabled(true);
            for (int i = 0; i < EPDShip.getInstance().getRouteManager().getRoutes().size(); i++) {
                if (EPDShip.getInstance().getRouteManager().getRoutes().get(i) == route) {
                    routeListComboBox.setSelectedIndex(i);
                }
            }
        }

        if (stccMmsi != -1 && route != null) {
            sendBtn.setEnabled(true);
        }

    }

    /***************************************************/
    /** Utility functions **/
    /***************************************************/

    /**
     * Test method
     */
    public static void main(String... args) {
        SendStrategicRouteDialog dialog = new SendStrategicRouteDialog(null);
        dialog.setVisible(true);
    }
}
