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
package dk.dma.epd.shore.gui.views;

import static dk.dma.epd.common.graphics.GraphicsUtil.fixSize;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DateFormatter;

import net.maritimecloud.core.id.MmsiId;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;
import dk.dma.epd.common.util.TypedValue.Dist;
import dk.dma.epd.common.util.TypedValue.DistType;
import dk.dma.epd.common.util.TypedValue.SpeedType;
import dk.dma.epd.common.util.TypedValue.Time;
import dk.dma.epd.common.util.TypedValue.TimeType;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.RouteSuggestionHandler;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Dialog used by EPDShore to send a tactical route suggestion to a ship.
 */
public class SendRouteDialog extends ComponentDialog implements ActionListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SendRouteDialog.class);
    private static final boolean COPY_ROUTE = true;

    enum RouteFields {
        DEPARTURE, ARRIVAL, SPEED
    }

    // Target panel
    private JComboBox<String> mmsiListComboBox = new JComboBox<>();
    private JComboBox<String> nameComboBox = new JComboBox<>();
    private JLabel callsignLbl = new JLabel("N/A");
    private JButton chatBtn = new JButton("Chat", EPD.res().getCachedImageIcon("images/notifications/balloon.png"));

    // Route panel
    private JComboBox<String> routeListComboBox = new JComboBox<>();
    private JLabel routeLengthLbl = new JLabel("N/A");
    private JXDatePicker departurePicker = new JXDatePicker();
    private JSpinner departureSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
    private JXDatePicker arrivalPicker = new JXDatePicker();
    private JSpinner arrivalSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
    private JTextField speedTxtField = new JTextField();
    private JButton zoomBtn = new JButton("Zoom To", EPDShore.res().getCachedImageIcon("images/buttons/zoom.png"));

    // Sender panel
    private JTextArea messageTxtField = new JTextArea("Route Suggestion");

    // Send panel
    private JLabel statusLbl = new JLabel(" ");
    private JButton sendBtn = new JButton("Send", EPDShore.res().getCachedImageIcon("images/buttons/ok.png"));
    private JButton cancelBtn = new JButton("Cancel", EPDShore.res().getCachedImageIcon("images/buttons/cancel.png"));

    private AisHandler aisHandler;
    private RouteManager routeManager;
    private RouteSuggestionHandler routeSuggestionHandler;

    private Route route;
    private long mmsi = -1;
    private boolean loading;
    private boolean wasVisible;

    /**
     * Create the frame.
     */
    public SendRouteDialog(JFrame frame) {
        super(frame, "Tactical Route Exchange", Dialog.ModalityType.MODELESS);

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
        Insets insets1 = new Insets(5, 5, 5, 0);
        Insets insets2 = new Insets(5, 0, 5, 5);
        Insets insets5 = new Insets(5, 5, 5, 5);

        // *******************
        // *** Target panel
        // *******************
        JPanel targetPanel = new JPanel(new GridBagLayout());
        targetPanel.setBorder(new TitledBorder("Target"));
        content.add(targetPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        mmsiListComboBox.addActionListener(this);
        targetPanel.add(new JLabel("MMSI:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(mmsiListComboBox, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        nameComboBox.addActionListener(this);
        targetPanel.add(new JLabel("Name:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(nameComboBox, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        chatBtn.setEnabled(false);
        chatBtn.addActionListener(this);
        targetPanel.add(new JLabel("Call Sign:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(callsignLbl, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        targetPanel.add(chatBtn, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, EAST, NONE, insets5, 0, 0));

        statusLbl.setVisible(false);
        targetPanel.add(statusLbl, new GridBagConstraints(0, 3, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        // *******************
        // *** Route panel
        // *******************
        JPanel routePanel = new JPanel(new GridBagLayout());
        routePanel.setBorder(new TitledBorder("Route"));
        content.add(routePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routeListComboBox.addActionListener(this);
        routePanel.add(new JLabel("Route name:"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(routeListComboBox, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routePanel.add(new JLabel("Route length:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(routeLengthLbl, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        int h = (int) departurePicker.getPreferredSize().getHeight();
        initDatePicker(departurePicker, departureSpinner);
        routePanel.add(new JLabel("ETD:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(fixSize(departurePicker, 120), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routePanel.add(fixSize(departureSpinner, 60, h), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));

        initDatePicker(arrivalPicker, arrivalSpinner);
        routePanel.add(new JLabel("ETA:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(fixSize(arrivalPicker, 120), new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routePanel.add(fixSize(arrivalSpinner, 60, h), new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));

        speedTxtField.getDocument().addDocumentListener(new TextFieldChangeListener(speedTxtField));
        speedTxtField.setHorizontalAlignment(JTextField.RIGHT);
        routePanel.add(new JLabel("Avg. speed:"), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(speedTxtField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        zoomBtn.addActionListener(this);
        routePanel.add(zoomBtn, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));

        // *******************
        // *** Send panel
        // *******************
        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBorder(new TitledBorder("Send"));
        content.add(sendPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, CENTER, BOTH, insets5, 0, 0));

        messageTxtField.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageTxtField);
        scrollPane.setMinimumSize(new Dimension(180, 40));
        scrollPane.setPreferredSize(new Dimension(180, 40));

        sendPanel.add(new JLabel("Message:"), new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, NORTHWEST, NONE, insets5, 0, 0));
        sendPanel.add(scrollPane, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));

        sendBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        sendPanel.add(sendBtn, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        sendPanel.add(cancelBtn, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, EAST, NONE, insets5, 0, 0));
    }

    /**
     * Configures the given date picker and associated time spinner
     * 
     * @param picker
     *            the date picker
     * @param spinner
     *            the time spinner
     */
    private void initDatePicker(JXDatePicker picker, JSpinner spinner) {
        picker.setFormats(new SimpleDateFormat("E dd/MM/yyyy"));
        picker.addPropertyChangeListener("date", this);

        DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        formatter.setCommitsOnValidEdit(true);
        spinner.setEditor(editor);
        spinner.addChangeListener(new SpinnerChangeListener());
    }

    /**
     * Loads data
     */
    public void loadData() {
        loading = true;

        // Initialize MMSI list
        Set<Long> mmsiList = new HashSet<>();
        for (int i = 0; i < routeSuggestionHandler.getRouteSuggestionServiceList().size(); i++) {
            mmsiList.add(MaritimeCloudUtils.toMmsi(routeSuggestionHandler.getRouteSuggestionServiceList().get(i).getRemoteId()));
        }

        mmsiListComboBox.removeAllItems();
        for (Long mmsi : mmsiList) {
            mmsiListComboBox.addItem(String.valueOf(mmsi));
        }
        mmsiListComboBox.setEnabled(mmsiList.size() > 0);

        // Initialize routes
        routeListComboBox.removeAllItems();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            routeListComboBox.addItem(routeManager.getRoutes().get(i).getName()
                    + "                                                 " + i);
        }

        // Initialize names
        nameComboBox.removeAllItems();
        for (Long mmsi : mmsiList) {

            VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi.longValue());
            if (selectedShip != null && selectedShip.getStaticData() != null) {
                nameComboBox.addItem(selectedShip.getStaticData().getTrimmedName());
            } else {
                nameComboBox.addItem("N/A");
            }
        }

        loading = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;

        }
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }

        if (obj instanceof RouteSuggestionHandler) {
            routeSuggestionHandler = (RouteSuggestionHandler) obj;
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

        if (ae.getSource() == nameComboBox) {
            nameSelectionChanged();

        } else if (ae.getSource() == mmsiListComboBox) {
            mmsiSelectionChanged();

        } else if (ae.getSource() == routeListComboBox) {
            routeSelectionChanged();

        } else if (ae.getSource() == zoomBtn && route.getWaypoints() != null) {
            if (EPD.getInstance().getMainFrame().getActiveChartPanel() != null) {
                EPD.getInstance().getMainFrame().getActiveChartPanel().zoomToWaypoints(route.getWaypoints());
            }

        } else if (ae.getSource() == sendBtn) {
            sendRoute();

        } else if (ae.getSource() == chatBtn) {
            chat();

        } else if (ae.getSource() == cancelBtn || ae.getSource() == getRootPane()) {
            this.setVisible(false);
        }
    }

    /**
     * Display information about the maritime identity
     */
    private void chat() {
        if (mmsi == -1) {
            try {
                mmsi = Long.valueOf((String) mmsiListComboBox.getSelectedItem());
            } catch (Exception e) {
                LOG.error("Failed to set mmsi " + mmsi, e);
            }
        }
        EPD.getInstance().getNotificationCenter().openNotification(NotificationType.MESSAGES, new MmsiId((int) mmsi), false);
    }

    /**
     * Called when one of the arrival and departure pickers changes value
     * 
     * @param evt
     *            the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (loading || route == null) {
            return;
        }

        if (evt.getSource() == departurePicker) {
            Date date = combineDateTime(departurePicker.getDate(), (Date) departureSpinner.getValue());
            route.setStarttime(date);
            updateRouteFields(route, RouteFields.ARRIVAL);

        } else if (evt.getSource() == arrivalPicker) {
            Date date = combineDateTime(arrivalPicker.getDate(), (Date) arrivalSpinner.getValue());
            recalculateSpeeds(date, route);
        }
    }

    /**
     * Called when one of the arrival and departure spinners changes value
     * 
     * @param spinner
     *            the spinner
     */
    protected void spinnerValueChanged(JSpinner spinner) {
        if (loading || route == null) {
            return;
        }

        if (spinner == departureSpinner) {
            Date date = combineDateTime(departurePicker.getDate(), (Date) departureSpinner.getValue());
            route.setStarttime(date);
            updateRouteFields(route, RouteFields.ARRIVAL);

        } else if (spinner == arrivalSpinner) {
            Date date = combineDateTime(arrivalPicker.getDate(), (Date) arrivalSpinner.getValue());
            recalculateSpeeds(date, route);
        }
    }

    /**
     * Called when the speed text field changes value
     * 
     * @param field
     *            the text field
     */
    protected void textFieldValueChanged(JTextField field) {
        if (loading || route == null) {
            return;
        }

        if (field == speedTxtField) {
            try {
                double speed = parseDouble(speedTxtField.getText());
                for (int i = 0; i < route.getWaypoints().size(); i++) {
                    route.getWaypoints().get(i).setSpeed(speed);
                }
                route.calcValues(true);
                updateRouteFields(route, RouteFields.ARRIVAL);
            } catch (Exception ex) {
                LOG.error("Error parsing speed " + speedTxtField.getText(), ex);
            }
        }

    }

    /**
     * Called when the name selection has changed
     */
    private void nameSelectionChanged() {
        // System.out.println("Name selection changed");
        if (nameComboBox.getSelectedItem() != null) {
            mmsiListComboBox.setSelectedIndex(nameComboBox.getSelectedIndex());
        }
    }

    /**
     * Called when the MMSI selection has changed
     */
    private void mmsiSelectionChanged() {
        if (mmsiListComboBox.getSelectedItem() != null) {

            nameComboBox.setSelectedIndex(mmsiListComboBox.getSelectedIndex());
            try {
                mmsi = Long.valueOf((String) mmsiListComboBox.getSelectedItem());
            } catch (Exception e) {
                LOG.error("Failed to set mmsi " + mmsi, e);
            }

            VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi);

            if (selectedShip != null) {

                if (selectedShip.getStaticData() != null) {
                    callsignLbl.setText(AisMessage.trimText(selectedShip.getStaticData().getCallsign()));
                } else {
                    callsignLbl.setText("N/A");
                }
                statusLbl.setVisible(false);
            } else {
                statusLbl.setText("The ship is not visible on AIS");
                statusLbl.setVisible(true);
            }

            chatBtn.setEnabled(mmsi != -1 && EPD.getInstance().getChatServiceHandler().availableForChat((int) mmsi));

        } else {
            chatBtn.setEnabled(false);
        }
    }

    /**
     * Called when the route selection has changed
     */
    private void routeSelectionChanged() {
        if (routeListComboBox.getSelectedItem() != null) {
            setCurrentRoute(routeManager.getRoute(routeListComboBox.getSelectedIndex()));
            updateRouteFields(route);
        }
    }

    /**
     * Sends the current route to the current vessel
     */
    private void sendRoute() {
        if (route == null || mmsiListComboBox.getItemCount() == 0) {
            return;
        }

        if (mmsi == -1) {
            mmsi = Long.parseLong(mmsiListComboBox.getSelectedItem().toString());
        }

        LOG.info(String.format("Sending route suggestion to MMSI %d", mmsi));

        try {
            routeSuggestionHandler.sendRouteSuggestion(mmsi, route.getFullRouteData(), messageTxtField.getText());
            messageTxtField.setText("");
        } catch (Exception e) {
            LOG.error("Failed to send route", e);
        }

        this.setVisible(false);

        mmsi = -1;
        route = null;
    }

    /**
     * Sets the selected MMSI
     * 
     * @param mmsi
     *            the selected MMSI
     */
    public void setSelectedMMSI(long mmsi) {
        this.mmsi = mmsi;
        selectAndLoad();
    }

    /**
     * Sets the selected route
     * 
     * @param route
     *            the selected route
     */
    public void setSelectedRoute(Route route) {
        setCurrentRoute(route);
        selectAndLoad();
    }

    /**
     * Sets the given route as the current one. Depending on the {@code COPY_ROUTE} flag, the dialog either works on the original or
     * a copy of the route.
     * 
     * @param route
     *            the route to set
     */
    private void setCurrentRoute(Route route) {
        if (COPY_ROUTE && route != null) {
            this.route = route.copy();
        } else {
            this.route = route;
        }
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

        if (mmsi != -1 && mmsiListComboBox.getItemCount() > 0) {
            mmsiListComboBox.setEnabled(true);
            for (int i = 0; i < mmsiListComboBox.getItemCount(); i++) {
                if (mmsiListComboBox.getItemAt(i).equals(Long.toString(mmsi))) {
                    mmsiListComboBox.setSelectedIndex(i);
                }
            }

        }

        nameComboBox.setSelectedIndex(mmsiListComboBox.getSelectedIndex());

        if (route != null && EPDShore.getInstance().getMainFrame().getRouteManagerDialog().getRouteManager().getRoutes().size() > 0) {
            routeListComboBox.setEnabled(true);
            for (int i = 0; i < EPDShore.getInstance().getMainFrame().getRouteManagerDialog().getRouteManager().getRoutes().size(); i++) {
                if (EPDShore.getInstance().getMainFrame().getRouteManagerDialog().getRouteManager().getRoutes().get(i) == route) {
                    routeListComboBox.setSelectedIndex(i);
                }
            }

            updateRouteFields(route);
        }

        if (mmsi == -1 && mmsiListComboBox.getItemCount() > 0) {
            mmsi = Long.parseLong(mmsiListComboBox.getSelectedItem().toString());
        }

        VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi);

        if (selectedShip != null) {

            if (selectedShip.getStaticData() != null) {
                callsignLbl.setText(AisMessage.trimText(selectedShip.getStaticData().getCallsign()));
            } else {
                callsignLbl.setText("N/A");
            }
        }

        if (mmsi != -1 && route != null) {
            sendBtn.setEnabled(true);
        }

        chatBtn.setEnabled(mmsi != -1 && EPD.getInstance().getChatServiceHandler().availableForChat((int) mmsi));
    }

    /**
     * Update the ETD and ETA date fields along with the speed field.
     * 
     * @param route
     *            the route to update the date and speed fields from
     * @param fields
     *            the fields to update
     */
    private void updateRouteFields(Route route, RouteFields... fields) {
        boolean wasLoading = loading;
        loading = true;

        Set<RouteFields> fieldLookup = new HashSet<>();
        if (fields.length == 0) {
            Collections.addAll(fieldLookup, RouteFields.values());
        } else {
            Collections.addAll(fieldLookup, fields);
        }

        try {
            if (route != null) {
                routeLengthLbl.setText(Integer.toString(route.getWaypoints().size()));

                route.adjustStartTime();
                Date starttime = route.getStarttime();

                if (fieldLookup.contains(RouteFields.DEPARTURE)) {
                    departurePicker.setDate(starttime);
                    departureSpinner.setValue(starttime);
                }

                if (fieldLookup.contains(RouteFields.ARRIVAL)) {
                    // Attempt to get ETA (only possible if GPS data is available)
                    Date etaStart = route.getEta(starttime);
                    if (etaStart != null) {
                        // GPS data available.
                        arrivalPicker.setDate(etaStart);
                        arrivalSpinner.setValue(etaStart);
                    } else {
                        // No GPS data available.
                        // Find the default ETA.
                        Date defaultEta = route.getEtas().get(route.getEtas().size() - 1);
                        arrivalPicker.setDate(defaultEta);
                        arrivalSpinner.setValue(defaultEta);
                    }
                }

                if (fieldLookup.contains(RouteFields.SPEED)) {
                    // Compute the average speed
                    Dist distance = new Dist(DistType.NAUTICAL_MILES, route.getRouteDtg());
                    Time time = new Time(TimeType.MILLISECONDS, route.getRouteTtg());
                    if (time.doubleValue() < 0.000001) {
                        speedTxtField.setText("");
                    } else {
                        speedTxtField.setText(Formatter.formatSpeed(distance.inTime(time).in(SpeedType.KNOTS).doubleValue()));
                    }
                }
            }
        } finally {
            loading = wasLoading;
        }
    }

    /**
     * Given a new arrival date, re-calculate the speed
     * 
     * @param arrivalDate
     *            the new arrival date
     * @param route
     *            the route to update
     */
    private void recalculateSpeeds(Date arrivalDate, Route route) {

        // Special case if the arrival date is before the start time
        if (route.getStarttime().after(arrivalDate)) {
            // Add a day to the departure date
            arrivalDate = new Date(route.getStarttime().getTime() + 24 * 60 * 60 * 1000);
        }

        // Total distance
        Dist distanceToTravel = new Dist(DistType.NAUTICAL_MILES, route.getRouteDtg());
        // And we want to get there in milliseconds:
        Time timeToTravel = new Time(TimeType.MILLISECONDS, arrivalDate.getTime() - route.getStarttime().getTime());

        // So we need to travel how fast?
        double speed = distanceToTravel.inTime(timeToTravel).in(SpeedType.KNOTS).doubleValue();

        for (int i = 0; i < route.getWaypoints().size(); i++) {
            route.getWaypoints().get(i).setSpeed(speed);
        }
        route.calcValues(true);

        updateRouteFields(route, RouteFields.SPEED);
    }

    /***************************************************/
    /** Utility functions **/
    /***************************************************/

    /**
     * Combines the date from the first {@code date} parameter with the time from the {@code time} parameter
     * 
     * @param date
     *            the date
     * @param time
     *            the time
     * @return the combined date
     */
    private static Date combineDateTime(Date date, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        return cal.getTime();
    }

    /**
     * Parses the text field as a double. Will skip any type suffix.
     * 
     * @param str
     *            the string to parse as a double
     * @return the resulting value
     */
    private static double parseDouble(String str) throws FormatException {
        str = str.replaceAll(",", ".");
        String[] parts = StringUtils.split(str, " ");
        return ParseUtils.parseDouble(parts[0]);
    }

    /**
     * Test method
     */
    public static void main(String... args) {
        SendRouteDialog dialog = new SendRouteDialog(null);
        dialog.setVisible(true);
    }

    /***************************************************/
    /** Helper classes **/
    /***************************************************/

    /**
     * Sadly, the change listener fires twice when you click the spinner buttons. This class will only call
     * {@linkplain #spinnerValueChanged()} when the value has actually changed
     */
    class SpinnerChangeListener implements ChangeListener {
        Object oldValue;

        @Override
        public void stateChanged(ChangeEvent e) {
            Object newValue = ((JSpinner) e.getSource()).getValue();
            if (newValue != null && !newValue.equals(oldValue)) {
                spinnerValueChanged((JSpinner) e.getSource());
            }
            oldValue = newValue;
        }
    }

    /**
     * Can be attached to the document of a text field and will call {@linkplain #textFieldValueChanged()} when the value changes
     */
    class TextFieldChangeListener implements DocumentListener {
        JTextField field;

        public TextFieldChangeListener(JTextField field) {
            this.field = field;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            textFieldValueChanged(field);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            textFieldValueChanged(field);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            textFieldValueChanged(field);
        }
    }
}
