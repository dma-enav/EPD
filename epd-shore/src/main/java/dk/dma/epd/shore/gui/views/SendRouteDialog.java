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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;

import org.jdesktop.swingx.JXDatePicker;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.util.TypedValue.Dist;
import dk.dma.epd.common.util.TypedValue.DistType;
import dk.dma.epd.common.util.TypedValue.SpeedType;
import dk.dma.epd.common.util.TypedValue.Time;
import dk.dma.epd.common.util.TypedValue.TimeType;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.RouteSuggestionHandler;

/**
 * Dialog used by EPDShore to send a tactical route suggestion to a ship.
 */
public class SendRouteDialog extends ComponentDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    
    // Target panel
    private JComboBox<String> mmsiListComboBox = new JComboBox<>();
    private JComboBox<String> nameComboBox = new JComboBox<>();
    private JLabel callsignLbl = new JLabel("N/A");
    
    // Route panel
    private JComboBox<String> routeListComboBox = new JComboBox<>();
    private JLabel routeLengthLbl = new JLabel("N/A");
    private JXDatePicker departurePicker = new JXDatePicker();
    private JSpinner departureSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
    private JXDatePicker arrivalPicker = new JXDatePicker();
    private JSpinner arrivalSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
    private JButton zoomBtn = new JButton("Zoom To", EPDShore.res().getCachedImageIcon("images/buttons/zoom.png"));
    
    // Sender panel
    private JTextField senderTxtField = new JTextField("DMA Shore");
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


    /**
     * Create the frame.
     */
    public SendRouteDialog(JFrame frame) {
        super(frame, "Tactical Route Exchange", Dialog.ModalityType.MODELESS);
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocationRelativeTo(frame);
        setLocation(100, 100);

        initGUI();
        pack();
    }

    /**
     * Initialize the GUI
     */
    public void initGUI() {

        JPanel content = new JPanel(new GridBagLayout());
        setContentPane(content);
        Insets insets1  = new Insets(5, 5, 5, 0);
        Insets insets2  = new Insets(5, 0, 5, 5);
        Insets insets5  = new Insets(5, 5, 5, 5);
        
        // *******************
        // *** Target panel 
        // *******************
        JPanel targetPanel = new JPanel(new GridBagLayout());
        targetPanel.setBorder(new TitledBorder("Target"));
        content.add(targetPanel, 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        mmsiListComboBox.addActionListener(this);
        targetPanel.add(new JLabel("MMSI:"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(mmsiListComboBox, 
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        nameComboBox.addActionListener(this);
        targetPanel.add(new JLabel("Name:"), 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(nameComboBox, 
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        targetPanel.add(new JLabel("Call Sign:"), 
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(callsignLbl, 
                new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        
        // *******************
        // *** Route panel 
        // *******************
        JPanel routePanel = new JPanel(new GridBagLayout());
        routePanel.setBorder(new TitledBorder("Route"));
        content.add(routePanel, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        routeListComboBox.addActionListener(this);
        routePanel.add(new JLabel("Route name:"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(routeListComboBox, 
                new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        routePanel.add(new JLabel("Route length:"), 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(routeLengthLbl, 
                new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        int h = (int)departurePicker.getPreferredSize().getHeight();
        initDatePicker(departurePicker, departureSpinner);
        routePanel.add(new JLabel("ETD:"), 
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(fixSize(departurePicker, 120), 
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routePanel.add(fixSize(departureSpinner, 60, h), 
                new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));

        initDatePicker(arrivalPicker, arrivalSpinner);
        routePanel.add(new JLabel("ETA:"), 
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(fixSize(arrivalPicker, 120), 
                new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routePanel.add(fixSize(arrivalSpinner, 60, h), 
                new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
                
        zoomBtn.addActionListener(this);
        routePanel.add(zoomBtn, 
                new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        
        // *******************
        // *** Sender panel 
        // *******************
        JPanel senderPanel = new JPanel(new GridBagLayout());
        senderPanel.setBorder(new TitledBorder("Sender"));
        content.add(senderPanel, 
                new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));
        
        senderPanel.add(new JLabel("Sender:"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        senderPanel.add(senderTxtField, 
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        messageTxtField.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageTxtField);
        scrollPane.setMinimumSize(new Dimension(180, 40));
        scrollPane.setPreferredSize(new Dimension(180, 40));
        senderPanel.add(new JLabel("Message:"), 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, insets5, 0, 0));
        senderPanel.add(scrollPane, 
                new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));
        

        // *******************
        // *** Send panel 
        // *******************
        JPanel sendPanel = new JPanel(new GridBagLayout());
        sendPanel.setBorder(new TitledBorder("Sender"));
        content.add(sendPanel, 
                new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, insets5, 0, 0));
        
        sendPanel.add(statusLbl, 
                new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        sendBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        sendPanel.add(sendBtn, 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        sendPanel.add(cancelBtn, 
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, EAST, NONE, insets5, 0, 0));
    }


    /**
     * Configures the given date picker and associated time spinner
     * @param picker the date picker
     * @param spinner the time spinner
     */
    private void initDatePicker(JXDatePicker picker, JSpinner spinner) {
        picker.setFormats(new SimpleDateFormat("E dd/MM/yyyy"));
        DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        ((DefaultFormatter)editor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
        spinner.setEditor(editor);
    }

    /**
     * Loads data
     */
    public void loadData() {
        loading = true;
        
        // Initialize MMSI list
        Set<Integer> mmsiList = new HashSet<>();
        for (int i = 0; i < routeSuggestionHandler.getRouteSuggestionServiceList().size(); i++) {
            mmsiList.add(MaritimeCloudUtils.toMmsi(routeSuggestionHandler.getRouteSuggestionServiceList().get(i).getId()));
        }

        mmsiListComboBox.removeAllItems();
        for (Integer mmsi : mmsiList) {
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
        for (Integer mmsi : mmsiList) {

            VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi.longValue());
            if (selectedShip != null && selectedShip.getStaticData() != null) {
                nameComboBox.addItem(selectedShip.getStaticData().getName());
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if (ae.getSource() == nameComboBox && !loading) {
            if (nameComboBox.getSelectedItem() != null) {
                mmsiListComboBox.setSelectedIndex(nameComboBox.getSelectedIndex());
            }
        }

        else if (ae.getSource() == mmsiListComboBox && !loading) {

            if (mmsiListComboBox.getSelectedItem() != null) {

                nameComboBox.setSelectedIndex(mmsiListComboBox.getSelectedIndex());
                try {
                    mmsi = Long.valueOf((String) mmsiListComboBox.getSelectedItem());
                } catch (Exception e) {
                    System.out.println("Failed to set mmsi " + mmsi);
                }

                // System.out.println("mmsi selected to set to " + mmsi);
                VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi);

                if (selectedShip != null) {

                    if (selectedShip.getStaticData() != null) {
                        callsignLbl.setText(AisMessage.trimText(selectedShip
                                .getStaticData().getCallsign()));
                    } else {
                        callsignLbl.setText("N/A");
                    }
                } else {
                    statusLbl.setText("The ship is not visible on AIS");
                }
            }
        }

        else if (ae.getSource() == routeListComboBox && !loading) {
            if (routeListComboBox.getSelectedItem() != null) {
                route = routeManager.getRoute(routeListComboBox.getSelectedIndex());
                routeLengthLbl.setText(Integer.toString(route.getWaypoints().size()));
                updateDates(route);
            }
        }

        else if (ae.getSource() == zoomBtn && route.getWaypoints() != null) {

            EPD.getInstance().getMainFrame()
                .zoomToPosition(route.getWaypoints().getFirst().getPos());
        }

        else if (ae.getSource() == sendBtn) {

            if (route == null && routeListComboBox.getSelectedIndex() != -1) {
                route = routeManager.getRoutes().get(routeListComboBox.getSelectedIndex());
            }

            if (mmsi == -1) {
                mmsi = Long.parseLong(mmsiListComboBox.getSelectedItem().toString());
            }

            try {
                Date etd = combineDateTime(departurePicker.getDate(), (Date)departureSpinner.getValue());
                route.setStarttime(etd);

                Date eta = combineDateTime(arrivalPicker.getDate(), (Date)arrivalSpinner.getValue());
                recalculateSpeeds(eta, route);
                
                routeSuggestionHandler.sendRouteSuggestion(mmsi,
                        route.getFullRouteData(), senderTxtField.getText(),
                        messageTxtField.getText());
                messageTxtField.setText("");
            } catch (Exception e) {
                System.out.println("Failed to send route");
            }

            this.setVisible(false);

            this.mmsi = -1;
            this.route = null;

        }
        
        else if (ae.getSource() == cancelBtn) {
            // Hide it
            this.setVisible(false);
        }
    }

    /**
     * Sets the selected MMSI
     * @param mmsi the selected MMSI
     */
    public void setSelectedMMSI(long mmsi) {
        this.mmsi = mmsi;
        selectAndLoad();
    }

    /**
     * Sets the selected route
     * @param route the selected route
     */
    public void setSelectedRoute(Route route) {

        this.route = route;
        selectAndLoad();
    }

    /**
     * Loads base data and updates the UI with the selected MMSI and route
     */
    private void selectAndLoad() {
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

        if (route != null
                && EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                        .getRouteManager().getRoutes().size() > 0) {
            routeListComboBox.setEnabled(true);
            for (int i = 0; i < EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                    .getRouteManager().getRoutes().size(); i++) {
                if (EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                        .getRouteManager().getRoutes().get(i) == route) {
                    routeListComboBox.setSelectedIndex(i);
                }
            }            
            
            updateDates(route);
        }
        
        if (mmsi == -1 && mmsiListComboBox.getItemCount() > 0) {
            mmsi = Long.parseLong(mmsiListComboBox.getSelectedItem().toString());
        }

        VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi);

        if (selectedShip != null) {

            if (selectedShip.getStaticData() != null) {
                callsignLbl.setText(AisMessage.trimText(selectedShip
                        .getStaticData().getCallsign()));
            } else {
                callsignLbl.setText("N/A");
            }
        }

        if (mmsi != -1 && route != null) {
            sendBtn.setEnabled(true);
        }

    }
    
    private void updateDates(Route route) {
        if (route != null) {
            Date starttime = route.getStarttime();

            departurePicker.setDate(starttime);
            departureSpinner.setValue(starttime);

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
    }
    
    /**
     * Given a new arrival date, re-calculate the speed
     * @param arrivalDate the new arrival date
     * @param route the route to update
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
    }

    /**
     * Combines the date from the first {@code date} parameter with the 
     * time from the {@code time} parameter
     * 
     * @param date the date
     * @param time the time
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
     * Test method
     */
    public static void main(String... args) {
        SendRouteDialog dialog = new SendRouteDialog(null);
        dialog.setVisible(true);
    }
}

