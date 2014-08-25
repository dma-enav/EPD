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
package dk.dma.epd.ship.gui.fal;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.service.ServiceEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.fal.FALReport;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.enavcloud.FALReportingService.FALReportMessage;
import dk.dma.epd.common.prototype.enavcloud.FALReportingService.FALReportReply;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.util.NameUtils;
import dk.dma.epd.common.util.NameUtils.NameFormat;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.fal.FALManager;
import dk.dma.epd.ship.gui.identity.IdentityViewer;

/**
 * Sends a FAL report to a store station
 */
public class TransmitFALReportDialog extends ComponentDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TransmitFALReportDialog.class);

    // Target panel
    private JComboBox<String> portListComboBox = new JComboBox<>();

    // fal panel
    private JComboBox<String> falListComboBox = new JComboBox<>();
    private JButton viewReportBtn = new JButton("View Report", EPDShip.res().getCachedImageIcon("images/buttons/report.png"));

    // Send panel
    private JButton sendBtn = new JButton("Send", EPDShip.res().getCachedImageIcon("images/buttons/ok.png"));
    private JButton cancelBtn = new JButton("Cancel", EPDShip.res().getCachedImageIcon("images/buttons/cancel.png"));
    private JButton btnInfo = new JButton("Info", EPDShip.res().getCachedImageIcon("images/buttons/information.png"));
    private JButton btnChat = new JButton("Chat", EPD.res().getCachedImageIcon("images/notifications/balloon.png"));

    private FALManager falManager;
    private IdentityHandler identityHandler;

    private Preferences prefs;

    private FALReport selectedFalReport;

    private long portMmsi = -1;
    private boolean loading;
    private boolean wasVisible;

    private ArrayList<Integer> mmsiList = new ArrayList<Integer>();

    IdentityViewer viewer;

    /**
     * Create the frame.
     */
    public TransmitFALReportDialog(FALManagerDialog falManagerDialog) {
        super(EPDShip.getInstance().getMainFrame(), "Transmit FAL Report", Dialog.ModalityType.MODELESS);

        setLocationRelativeTo(falManagerDialog);

        prefs = Preferences.userNodeForPackage(TransmitFALReportDialog.class);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setLocation(100, 100);

        initGUI();

        // Hook up enter key to send and escape key to cancel
        getRootPane().setDefaultButton(sendBtn);
        getRootPane()
                .registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();

        falManager = EPDShip.getInstance().getFalManager();
        identityHandler = EPDShip.getInstance().getIdentityHandler();

        this.setVisible(true);

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
        stccPanel.setBorder(new TitledBorder("Port Authority"));
        content.add(stccPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));

        portListComboBox.addActionListener(this);
        stccPanel.add(new JLabel("Destination:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 5, 5, 5),
                0, 0));
        stccPanel.add(portListComboBox,
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));

        btnInfo.setEnabled(false);
        btnInfo.addActionListener(this);
        btnChat.setEnabled(false);
        btnChat.addActionListener(this);
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnChat);
        btnPanel.add(btnInfo);
        stccPanel.add(btnPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, EAST, NONE, new Insets(0, 0, 0, 0), 0, 0));

        // *******************
        // *** FAL panel
        // *******************
        JPanel falPanel = new JPanel(new GridBagLayout());
        falPanel.setBorder(new TitledBorder("FAL Report"));
        content.add(falPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 0), 0, 0));

        falListComboBox.addActionListener(this);
        falPanel.add(new JLabel("Select:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        falPanel.add(falListComboBox, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        viewReportBtn.addActionListener(this);
        falPanel.add(viewReportBtn, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, EAST, NONE, insets5, 0, 0));

        // *******************
        // *** Send panel
        // *******************
        JPanel sendPanel = new JPanel();
        content.add(sendPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));

        sendBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        sendPanel.add(sendBtn);
        sendPanel.add(cancelBtn);

    }

    /**
     * Called when one of the buttons are clicked or if one of the combo-boxes changes value
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (loading) {
            return;
        }

        if (ae.getSource() == portListComboBox) {
            stccMmsiSelectionChanged();

        } else if (ae.getSource() == falListComboBox) {
            falReportSelectionChanged();

        } else if (ae.getSource() == viewReportBtn) {
            int i = falListComboBox.getSelectedIndex();
            if (i >= 0) {
                FALReportingDialog dialog = new FALReportingDialog(falManager, falManager.getFalReports().get(i).getId(), false);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            }
        } else if (ae.getSource() == sendBtn) {
            sendFAL();
        } else if (ae.getSource() == cancelBtn || ae.getSource() == getRootPane()) {
            setVisible(false);

        } else if (ae.getSource() == btnInfo) {
            viewIdentityInfo();

        } else if (ae.getSource() == btnChat) {
            chat();
        }
    }

    /**
     * Display information about the maritime identity
     */
    private void viewIdentityInfo() {
        if (portMmsi == -1) {
            portMmsi = mmsiList.get(portListComboBox.getSelectedIndex());
        }

        if (viewer == null) {
            viewer = new IdentityViewer(EPDShip.getInstance().getMainFrame(), identityHandler.getActor(portMmsi));
        } else {
            viewer.loadValues(identityHandler.getActor(portMmsi));
        }

        viewer.setVisible(true);
    }

    /**
     * Display information about the maritime identity
     */
    private void chat() {
        if (portMmsi == -1) {
            portMmsi = mmsiList.get(portListComboBox.getSelectedIndex());
        }
        EPD.getInstance().getNotificationCenter().openNotification(NotificationType.MESSAGES, new MmsiId((int) portMmsi), false);
    }

    /**
     * Called when the STCC MMSI selection has changed
     */
    private void stccMmsiSelectionChanged() {
        if (portListComboBox.getSelectedItem() != null) {
            portMmsi = mmsiList.get(portListComboBox.getSelectedIndex());

            btnInfo.setEnabled(identityHandler.actorExists(portMmsi));
            btnChat.setEnabled(EPD.getInstance().getChatServiceHandler().availableForChat((int) portMmsi));

        } else {
            btnInfo.setEnabled(false);
            btnChat.setEnabled(false);
        }
    }

    /**
     * Called when the fal selection has changed
     */
    private void falReportSelectionChanged() {
        if (falListComboBox.getSelectedItem() != null) {
            this.selectedFalReport = falManager.getFalReports().get(falListComboBox.getSelectedIndex());
            // .getRoute();
        }
    }

    /**
     * Sends the current fal to the selected shore station
     */
    private void sendFAL() {
        if (selectedFalReport == null) {
            return;
        }

        if (portMmsi == -1) {

            if (portListComboBox.getSelectedIndex() == -1) {
                return;
            }

            portMmsi = mmsiList.get(portListComboBox.getSelectedIndex());
        }

        LOG.info(String.format("Sending FAL to Shore MMSI %d", portMmsi));

        try {

            int i = falListComboBox.getSelectedIndex();
            FALReport falReport = falManager.getFalReports().get(i);

            EPDShip.getInstance().getFalHandler().sendFALReport(portMmsi, "Transmitting FAL Report", falReport);

            // strategicRouteHandler.sendStrategicRouteToSTCC(stccMmsi, route, messageTxtField.getText());
            // messageTxtField.setText("Route Approval Requested");
        } catch (Exception e) {
            LOG.error("Failed to send route", e);
        }

        this.setVisible(false);

        // Save the MMSI in the preferences
        try {
            prefs.put("mmsi", String.valueOf(portMmsi));
            prefs.sync();
        } catch (BackingStoreException e) {
            System.err.println("Failed saving paths" + e);
        }

        portMmsi = -1;
        selectedFalReport = null;
    }

    /**
     * Sets the selected MMSI
     * 
     * @param mmsi
     *            the selected MMSI
     */
    public void setSelectedMMSI(long mmsi) {
        this.portMmsi = mmsi;
        selectAndLoad();

        btnInfo.setEnabled(identityHandler.actorExists(portMmsi));
        btnChat.setEnabled(EPD.getInstance().getChatServiceHandler().availableForChat((int) portMmsi));
    }

    /**
     * Sets the selected fal report
     * 
     * @param selectedFalReport
     *            the selected fal report
     */
    public void setSelectedFALReport(FALReport falReport) {
        this.selectedFalReport = falReport;
        selectAndLoad();
    }

    /**
     * Loads data
     */
    private void loadData() {
        loading = true;

        // Initialize MMSI list
        mmsiList.clear();
        portListComboBox.removeAllItems();
        for (ServiceEndpoint<FALReportMessage, FALReportReply> service : EPDShip.getInstance().getFalHandler().getFalRecievers()) {
            mmsiList.add(MaritimeCloudUtils.toMmsi(service.getId()));
            portListComboBox.addItem(NameUtils.getName(service.getId(), NameFormat.MEDIUM));
        }
        portListComboBox.setEnabled(mmsiList.size() > 0);

        // Initialize routes
        falListComboBox.removeAllItems();
        for (int i = 0; i < falManager.getFalReports().size(); i++) {
            falListComboBox.addItem(falManager.getFalReports().get(i).getFalReportName()
                    + "                                                 " + i);
        }
        sendBtn.setEnabled(mmsiList.size() > 0 && falManager.getFalReports().size() > 0);
        loading = false;
    }

    /**
     * Loads base data and updates the UI with the selected MMSI and fal report
     */
    private void selectAndLoad() {

        // The first time around, position relative to frame
        if (!wasVisible) {
            wasVisible = true;
            setLocationRelativeTo(getParent());
        }

        loadData();

        String mmsi = portMmsi != -1 ? Long.toString(portMmsi) : prefs.get("mmsi", null);
        if (mmsi != null && portListComboBox.getItemCount() > 0) {
            for (int i = 0; i < portListComboBox.getItemCount(); i++) {
                if (mmsiList.get(i).equals(mmsi)) {
                    portListComboBox.setSelectedIndex(i);
                }
            }

        }

        if (portMmsi == -1 && portListComboBox.getItemCount() > 0) {
            portMmsi = mmsiList.get(portListComboBox.getSelectedIndex());
        }

        btnInfo.setEnabled(identityHandler.actorExists(portMmsi));
        btnChat.setEnabled(EPD.getInstance().getChatServiceHandler().availableForChat((int) portMmsi));

        if (selectedFalReport != null && falManager.getFalReports().size() > 0) {
            falListComboBox.setEnabled(true);
            for (int i = 0; i < falManager.getFalReports().size(); i++) {
                if (falManager.getFalReports().get(i) == selectedFalReport) {
                    falListComboBox.setSelectedIndex(i);
                }
            }
        }

        if (portMmsi != -1 && selectedFalReport != null) {
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
        TransmitFALReportDialog dialog = new TransmitFALReportDialog(null);
        dialog.setVisible(true);
    }
}
