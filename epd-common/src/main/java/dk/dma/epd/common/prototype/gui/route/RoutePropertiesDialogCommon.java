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
package dk.dma.epd.common.prototype.gui.route;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Dialog with route properties
 */
public class RoutePropertiesDialogCommon extends JDialog implements ActionListener,
        Runnable, FocusListener, WindowListener, DocumentListener {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();

    JPanel waypointList;
    JScrollPane WaypointPanel;

    private JTextField originTxT;
    private JTextField destinationTxT;
    private JTextField inrouteTxT;
    private JTextField nameTxT;
    private JTextField distanceTxT;
    int offset = 9;

    protected JXDatePicker departurePicker;
    protected JXDatePicker arrivalPicker;
    protected JSpinner departureSpinner;
    protected JSpinner arrivalSpinner;

    private Border fieldBorder = new MatteBorder(1, 1, 1, 1, new Color(65, 65,
            65));
    private Border columnBorder = new MatteBorder(1, 1, 0, 0, new Color(65, 65,
            65));

    int selectedWp = -1;
    int lastSelectedWp = -1;

    private JButton btnZoomTo;
    private JButton btnDelete;
    protected JButton btnActivate;

    List<RoutePropertiesRow> waypointTable = new ArrayList<>();
    volatile boolean internalOperation;

    Route route;
    private JButton closeBtn;

    private Window parent;

    // private JTextField totalDistField;
    // private JTextField startTimeField;
    // private JTextField ttgField;
    // private JTextField etaField;

    // private WptTableModel wptTableModel;

    private RouteManagerCommon routeManager;
    protected ActiveRoute activeRoute;

    public RoutePropertiesDialogCommon(Window parent, RouteManagerCommon routeManager,
            int routeId) {
        super(parent, "Route Properties", Dialog.ModalityType.APPLICATION_MODAL);

        this.setResizable(false);

        this.parent = parent;
        this.routeManager = routeManager;

        if (routeManager.isActiveRoute(routeId)) {
            this.route = routeManager.getActiveRoute();
            activeRoute = (ActiveRoute) this.route;
        } else {
            this.route = routeManager.getRoute(routeId);
        }

        setBounds(100, 100, 904, 435);
        getContentPane().setLayout(null);
        contentPanel.setBounds(10, 11, 884, 343);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        getContentPane().add(contentPanel);
        contentPanel.setLayout(null);

        initGui();
        initValues();

        // (new Thread(this)).start();

        addWindowListener(this);

        updateButtons();

        // Parse the route
        parseRoute();
    }

    public RoutePropertiesDialogCommon(Window parent, Route route, boolean editable) {
        super(parent, "Route Properties", Dialog.ModalityType.APPLICATION_MODAL);

        if (!editable) {
            activeRoute = new ActiveRoute(route, null);
        }

        this.setResizable(false);

        this.parent = parent;

        this.route = route;

        setBounds(100, 100, 904, 435);
        getContentPane().setLayout(null);
        contentPanel.setBounds(10, 11, 884, 343);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        getContentPane().add(contentPanel);
        contentPanel.setLayout(null);

        initGui();
        initValues();

        // (new Thread(this)).start();

        addWindowListener(this);

        updateButtons();

        // Parse the route
        parseRoute();
    }

    private void initGui() {

        JPanel RouteProperties = new JPanel();
        RouteProperties.setBorder(new TitledBorder(new LineBorder(new Color(0,
                0, 0)), "Route Properties", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        RouteProperties.setBounds(0, 0, 865, 126);
        contentPanel.add(RouteProperties);
        RouteProperties.setLayout(null);

        JLabel lblNewLabel = new JLabel("Name:");
        lblNewLabel.setBounds(10, 23, 81, 14);
        RouteProperties.add(lblNewLabel);

        JLabel lblDepartFrom = new JLabel("Origin:");
        lblDepartFrom.setBounds(10, 48, 87, 14);
        RouteProperties.add(lblDepartFrom);

        JLabel lblNewLabel_1 = new JLabel("Total Distance:");
        lblNewLabel_1.setBounds(10, 101, 81, 14);
        RouteProperties.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Estimated Time of Departure:");
        lblNewLabel_2.setBounds(227, 23, 146, 14);
        RouteProperties.add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Estimated Time of Arrival:");
        lblNewLabel_3.setBounds(227, 48, 146, 14);
        RouteProperties.add(lblNewLabel_3);

        JLabel lblNewLabel_4 = new JLabel("Destination:");
        lblNewLabel_4.setBounds(10, 76, 87, 14);
        RouteProperties.add(lblNewLabel_4);

        JLabel lblNewLabel_5 = new JLabel("Estimated Time Inroute:");
        lblNewLabel_5.setBounds(227, 73, 146, 14);
        RouteProperties.add(lblNewLabel_5);

        originTxT = new JTextField();
        originTxT.setColumns(10);
        originTxT.setBorder(fieldBorder);
        originTxT.setBounds(86, 45, 121, 20);
        RouteProperties.add(originTxT);

        destinationTxT = new JTextField();
        destinationTxT.setColumns(10);
        destinationTxT.setBorder(fieldBorder);
        destinationTxT.setBounds(86, 71, 121, 20);
        RouteProperties.add(destinationTxT);

        inrouteTxT = new JTextField();
        inrouteTxT.setColumns(10);
        inrouteTxT.setBorder(fieldBorder);
        inrouteTxT.setBounds(383, 70, 159, 20);
        inrouteTxT.setEditable(false);
        RouteProperties.add(inrouteTxT);

        nameTxT = new JTextField();
        nameTxT.setColumns(10);
        nameTxT.setBorder(fieldBorder);
        nameTxT.setBounds(86, 20, 121, 20);
        RouteProperties.add(nameTxT);

        distanceTxT = new JTextField();
        distanceTxT.setColumns(10);
        distanceTxT.setBorder(fieldBorder);
        distanceTxT.setBounds(86, 98, 121, 20);
        distanceTxT.setEditable(false);
        RouteProperties.add(distanceTxT);

        WaypointPanel = new JScrollPane();
        WaypointPanel
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        WaypointPanel
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        WaypointPanel.setBounds(0, 137, 865, 195);
        WaypointPanel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0,
                0)), "Route Details", TitledBorder.LEADING, TitledBorder.TOP,
                null, null));
        contentPanel.add(WaypointPanel);

        waypointList = new JPanel();

        waypointList.setBorder(new LineBorder(new Color(0, 0, 0)));

        waypointList.setBounds(0, 0, 1000, 1000);
        WaypointPanel.setViewportView(waypointList);
        // topPanel.add( scrollPane, BorderLayout.CENTER );
        waypointList.setLayout(null);
        waypointList.setPreferredSize(new Dimension(195, 530));
        // WaypointList.setSize(100, 100);

        // Begin list of column labels

        JLabel lblName = new JLabel("  Name");
        lblName.setBackground(Color.GRAY);
        lblName.setOpaque(true);
        lblName.setBounds(16, 0, 60, 14);
        lblName.setBorder(columnBorder);
        waypointList.add(lblName);

        JLabel lblLatitude = new JLabel("  Latitude");
        lblLatitude.setBackground(Color.LIGHT_GRAY);
        lblLatitude.setOpaque(true);
        lblLatitude.setBounds(76, 0, 69, 14);
        lblLatitude.setBorder(columnBorder);
        waypointList.add(lblLatitude);

        JLabel lblLongitude = new JLabel("  Longitude");
        lblLongitude.setBackground(Color.GRAY);
        lblLongitude.setOpaque(true);
        lblLongitude.setBounds(145, 0, 69, 14);
        lblLongitude.setBorder(columnBorder);
        waypointList.add(lblLongitude);

        JLabel lblRad = new JLabel("  Rad");
        lblRad.setOpaque(true);
        lblRad.setBackground(Color.LIGHT_GRAY);
        lblRad.setBounds(214, 0, 45, 14);
        lblRad.setBorder(columnBorder);
        waypointList.add(lblRad);

        JLabel lblRot = new JLabel("  Rot");
        lblRot.setBackground(Color.GRAY);
        lblRot.setBounds(259, 0, 30, 14);
        lblRot.setBorder(columnBorder);
        waypointList.add(lblRot);
        lblRot.setOpaque(true);

        JLabel lblTTG = new JLabel("  TTG");
        lblTTG.setBackground(Color.LIGHT_GRAY);
        lblTTG.setBounds(289, 0, 64, 14);
        lblTTG.setOpaque(true);
        lblTTG.setBorder(columnBorder);
        waypointList.add(lblTTG);

        JLabel lblETA = new JLabel("  ETA");
        lblETA.setBackground(Color.GRAY);
        lblETA.setBounds(353, 0, 86, 14);
        lblETA.setOpaque(true);
        lblETA.setBorder(new MatteBorder(1, 1, 0, 1, new Color(65, 65, 65)));
        waypointList.add(lblETA);

        // Waypoint attributes
        JLabel lblRng = new JLabel("  Rng");
        lblRng.setBackground(Color.LIGHT_GRAY);
        lblRng.setOpaque(true);
        lblRng.setBounds(438, 0, 54, 23);
        lblRng.setBorder(columnBorder);
        waypointList.add(lblRng);

        JLabel lblBRG = new JLabel("  BRG");
        lblBRG.setBackground(Color.GRAY);
        lblBRG.setBounds(492, 0, 54, 23);
        lblBRG.setOpaque(true);
        lblBRG.setBorder(columnBorder);
        waypointList.add(lblBRG);

        JLabel lblHeading = new JLabel("  Heading");
        lblHeading.setBackground(Color.LIGHT_GRAY);
        lblHeading.setBounds(546, 0, 48, 23);

        lblHeading.setOpaque(true);
        lblHeading.setBorder(columnBorder);
        waypointList.add(lblHeading);

        JLabel lblSOG = new JLabel("  SOG");
        lblSOG.setBackground(Color.GRAY);
        lblSOG.setBounds(594, 0, 48, 23);
        lblSOG.setOpaque(true);
        lblSOG.setBorder(columnBorder);
        waypointList.add(lblSOG);

        JLabel lblXtdS = new JLabel("  XTD S");
        lblXtdS.setBackground(Color.LIGHT_GRAY);
        lblXtdS.setBounds(642, 0, 44, 23);
        lblXtdS.setBorder(columnBorder);
        lblXtdS.setOpaque(true);

        waypointList.add(lblXtdS);

        JLabel lblXtdp = new JLabel("  XTD P");
        lblXtdp.setBackground(Color.GRAY);
        lblXtdp.setBounds(686, 0, 44, 23);
        lblXtdp.setOpaque(true);
        lblXtdp.setBorder(columnBorder);
        waypointList.add(lblXtdp);

        JLabel lblSafeW = new JLabel("  SF Width");
        lblSafeW.setBackground(Color.LIGHT_GRAY);
        lblSafeW.setBounds(730, 0, 54, 23);
        lblSafeW.setOpaque(true);
        lblSafeW.setBorder(columnBorder);
        waypointList.add(lblSafeW);

        JLabel lblSafeH = new JLabel("  SF Len");
        lblSafeH.setBackground(Color.GRAY);
        lblSafeH.setBounds(784, 0, 54, 23);
        lblSafeH.setBorder(new MatteBorder(1, 1, 0, 1, new Color(65, 65, 65)));
        lblSafeH.setOpaque(true);

        waypointList.add(lblSafeH);

        departurePicker = new JXDatePicker();
        arrivalPicker = new JXDatePicker();

        SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");
        departurePicker.setFormats(format);
        departurePicker.setBounds(383, 20, 105, 20);
        departurePicker.getEditor().setBorder(fieldBorder);
        departurePicker.getEditor().addFocusListener(this);
        RouteProperties.add(departurePicker);

        arrivalPicker.setFormats(format);
        arrivalPicker.setBounds(383, 45, 105, 20);
        arrivalPicker.getEditor().setBorder(fieldBorder);
        arrivalPicker.getEditor().addFocusListener(this);
        RouteProperties.add(arrivalPicker);

        Date date = new Date();
        SpinnerDateModel departureSm = new SpinnerDateModel(date, null, null,
                Calendar.HOUR_OF_DAY);

        SpinnerDateModel arrivalSm = new SpinnerDateModel(date, null, null,
                Calendar.HOUR_OF_DAY);

        departureSpinner = new JSpinner(departureSm);

        departureSpinner.setLocation(488, 20);
        departureSpinner.setSize(54, 20);
        departureSpinner.setBorder(fieldBorder);
        JSpinner.DateEditor de_departureSpinner = new JSpinner.DateEditor(
                departureSpinner, "HH:mm");
        departureSpinner.setEditor(de_departureSpinner);

        RouteProperties.add(departureSpinner);

        arrivalSpinner = new JSpinner(arrivalSm);
        arrivalSpinner.setLocation(488, 45);
        arrivalSpinner.setSize(54, 20);
        arrivalSpinner.setBorder(fieldBorder);

        JSpinner.DateEditor de_arrivalSpinner = new JSpinner.DateEditor(
                arrivalSpinner, "HH:mm");

        arrivalSpinner.setEditor(de_arrivalSpinner);

        RouteProperties.add(arrivalSpinner);

        List<String> strings = new ArrayList<>();
        strings.add("Copenhagen");
        strings.add("Oslo");

        AutoCompleteDecorator.decorate(originTxT, strings, false);
        AutoCompleteDecorator.decorate(destinationTxT, strings, false);

        JPanel interactionButtonPane = new JPanel();
        interactionButtonPane.setBounds(10, 354, 253, 32);
        getContentPane().add(interactionButtonPane);

        btnZoomTo = new JButton("Zoom to");
        btnZoomTo.addActionListener(this);
        interactionButtonPane.add(btnZoomTo);

        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(this);
        interactionButtonPane.add(btnDelete);
        btnDelete.setEnabled(false);

        btnActivate = new JButton("Activate");
        interactionButtonPane.add(btnActivate);
        btnActivate.addActionListener(this);
        btnActivate.setEnabled(false);

        JPanel buttonPane = new JPanel();
        buttonPane.setBounds(795, 353, 83, 33);
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane);
        {
            closeBtn = new JButton("Close");
            closeBtn.addActionListener(this);
            closeBtn.setActionCommand("Close");
            buttonPane.add(closeBtn);
            getRootPane().setDefaultButton(closeBtn);
        }

    }

    private void parseRoute() {

        for (int i = 0; i < route.getWaypoints().size(); i++) {

            RouteWaypoint currentWaypoint = route.getWaypoints().get(i);

            if (currentWaypoint != null) {

                // generateWaypoints(-5 + ( (i+1) * 19));

                String name = Formatter.formatString(currentWaypoint.getName());
                String latitude = currentWaypoint.getPos()
                        .getLatitudeAsString();
                String longitude = currentWaypoint.getPos()
                        .getLongitudeAsString();
                String rad = "N/A";
                if (currentWaypoint.getTurnRad() != null) {
                    rad = Formatter.formatDistNM(currentWaypoint.getTurnRad());
                    // rad =
                    // Double.toString(parseDouble(currentWaypoint.getTurnRad().toString()));
                }

                String rot = "N/A";
                // Error in ROT Calculations
                // if (currentWaypoint.getRot() != null){
                // rot = currentWaypoint.getRot().toString();
                // }

                String ttg = "N/A";
                String eta = "N/A";
                String rng = "N/A";
                String brg = "N/A";
                String heading = "N/A";
                String speed = "N/A";
                String xtds = "N/A";
                String xtdp = "N/A";
                String sfwidth = "N/A";
                String sflen = "N/A";

                eta = Formatter.formatShortDateTimeNoTz(route.getWpEta(i));
                ttg = Formatter.formatTime(route.getWpTtg(i));

                if (currentWaypoint.getOutLeg() != null
                        && currentWaypoint != null) {

                    // ttg =
                    // Long.toString(currentWaypoint.getOutLeg().calcTtg());
                    rng = Formatter.formatDistNM(route.getWpRng(i));
                    // brg =
                    // Double.toString(currentWaypoint.getOutLeg().calcBrg());
                    brg = Formatter.formatDegrees(
                            route.getWpBrg(currentWaypoint), 2);
                    // heading =
                    // (currentWaypoint.getOutLeg().getHeading().name());
                    heading = Formatter.formatHeading(currentWaypoint
                            .getHeading());

                    speed = Formatter.formatSpeed(currentWaypoint.getOutLeg()
                            .getSpeed());
                    xtds = Formatter.formatMeters(currentWaypoint.getOutLeg()
                            .getXtdStarboardMeters());
                    xtdp = Formatter.formatMeters(currentWaypoint.getOutLeg()
                            .getXtdPortMeters());
                    sfwidth = Formatter.formatMeters(currentWaypoint
                            .getOutLeg().getSFWidth());
                    sflen = Formatter.formatMeters(currentWaypoint.getOutLeg()
                            .getSFLen());

                }

                RoutePropertiesRow currentRow = generateWaypoint(-5 + (i + 1)
                        * 19, name, latitude, longitude, rad, rot, ttg, eta,
                        rng, brg, heading, speed, xtds, xtdp, sfwidth, sflen,
                        i, route.getWaypoints().size());

                waypointTable.add(currentRow);
            }
        }

        waypointList.setPreferredSize(new Dimension(195, -5
                + (route.getWaypoints().size() + 1) * 19));

        // WaypointList.sets

        // start at 18, add 19
        // for (int i = 1; i < 50; i++) {
        //
        // generateWaypoints(-5 + (i*19));
        // }

    }

    private void initValues() {
        nameTxT.setText(route.getName());
        originTxT.setText(route.getDeparture());
        destinationTxT.setText(route.getDestination());

        updateDynamicValues();

        // Listeners
        nameTxT.addFocusListener(this);
        originTxT.addFocusListener(this);
        destinationTxT.addFocusListener(this);

        // Departure listeners
        ((DefaultEditor) departureSpinner.getEditor()).getTextField()
                .getDocument().addDocumentListener(this);
        ((DefaultEditor) departureSpinner.getEditor()).getTextField()
                .getDocument().putProperty("name", "departureSpinner");

        JFormattedTextField departureEditor = departurePicker.getEditor();
        departureEditor.getDocument().addDocumentListener(this);
        departureEditor.getDocument().putProperty("name", "departurePicker");
        departurePicker.addActionListener(this);

        // Arrival listeners
        ((DefaultEditor) arrivalSpinner.getEditor()).getTextField()
                .getDocument().addDocumentListener(this);
        ((DefaultEditor) arrivalSpinner.getEditor()).getTextField()
                .getDocument().putProperty("name", "arrivalSpinner");

        JFormattedTextField arrivalEditor = arrivalPicker.getEditor();
        arrivalEditor.getDocument().addDocumentListener(this);
        arrivalEditor.getDocument().putProperty("name", "arrivalPicker");
        arrivalPicker.addActionListener(this);

    }

    private void updateDynamicValues() {
        // Get start time or default now
        route.adjustStartTime();
        Date starttime = route.getStarttime();

        departurePicker.setDate(starttime);
        ((SpinnerDateModel) departureSpinner.getModel()).setValue(starttime);

        // Attempt to get ETA (only possible if GPS data is available)
        Date etaStart = route.getEta(starttime);
        if (etaStart != null){
        	// GPS data available.
            arrivalPicker.setDate(etaStart);
            ((SpinnerDateModel) arrivalSpinner.getModel()).setValue(etaStart);
        }
        else
        {
        	// No GPS data available.
        	// Find the default ETA.
        	Date defaultEta = route.getEtas().get(route.getEtas().size() - 1);
        	arrivalPicker.setDate(defaultEta);
        	((SpinnerDateModel) arrivalSpinner.getModel()).setValue(defaultEta);
        }
        
        
        
        if (activeRoute == null) {
            // arrivalPicker.setDate(route.getEta(starttime));
            // ((SpinnerDateModel) arrivalSpinner.getModel()).setValue(route
            // .getEta(starttime));

        } else {
            departurePicker.setEnabled(false);
            departureSpinner.setEnabled(false);
            arrivalPicker.setEnabled(false);
            arrivalSpinner.setEnabled(false);
        }

        inrouteTxT.setText(Formatter.formatTime(route.calcTtg()));
        distanceTxT.setText(Formatter.formatDistNM(route.calcDtg()));
    }

    private void updateButtons() {

        boolean wpSelected = selectedWp >= 0;

        if (route == activeRoute) {
            btnActivate.setEnabled(wpSelected);
        } else {
            btnDelete.setEnabled(wpSelected);
        }

        // if (activeRoute != null){
        // btnActivate.setEnabled(wpSelected);
        // }else{
        // btnDelete.setEnabled(wpSelected);
        // }

        btnZoomTo.setEnabled(wpSelected);

        // btnActivate.setVisible(activeRoute != null);

    }

    private void close() {
        dispose();
    }

    @Override
    public void focusLost(FocusEvent e) {

        if (e.getSource() instanceof WaypointJTextField) {
            if (selectedWp >= 0) {
                deselectRow(selectedWp);
            }

            String name = ((WaypointJTextField) e.getSource()).getName();
            final int id = ((WaypointJTextField) e.getSource()).getId();

            switch (name) {
            case "lat":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        waypointTable
                                .get(id)
                                .getLatitude()
                                .setText(
                                        route.getWaypoints().get(id).getPos()
                                                .getLatitudeAsString());
                        checkTimeDiff();
                    }
                });
                break;

            case "lon":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // System.out.println("Set longitude? "
                        // + route.getWaypoints().get(id).getPos()
                        // .getLongitudeAsString());

                        waypointTable
                                .get(id)
                                .getLongitude()
                                .setText(
                                        route.getWaypoints().get(id).getPos()
                                                .getLongitudeAsString());
                        checkTimeDiff();
                    }
                });
                break;
            case "rad":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        waypointTable
                                .get(id)
                                .getRad()
                                .setText(
                                        Formatter.formatDistNM(route
                                                .getWaypoints().get(id)
                                                .getTurnRad()));
                    }
                });
                break;
            case "heading":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        waypointTable
                                .get(id)
                                .getHeading()
                                .setText(
                                        Formatter.formatHeading(route
                                                .getWaypoints().get(id)
                                                .getHeading()));
                    }
                });
                break;
            case "sog":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        waypointTable
                                .get(id)
                                .getSog()
                                .setText(
                                        Formatter.formatSpeed(route
                                                .getWaypoints().get(id)
                                                .getOutLeg().getSpeed()));
                    }
                });
                break;

            case "xtds":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        waypointTable
                                .get(id)
                                .getXtds()
                                .setText(
                                        Formatter.formatMeters(route
                                                .getWaypoints().get(id)
                                                .getOutLeg()
                                                .getXtdStarboardMeters()));
                    }
                });
                break;

            case "xtdp":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        waypointTable
                                .get(id)
                                .getXtdp()
                                .setText(
                                        Formatter
                                                .formatMeters(route
                                                        .getWaypoints().get(id)
                                                        .getOutLeg()
                                                        .getXtdPortMeters()));
                    }
                });
                break;

            case "sfw":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        waypointTable
                                .get(id)
                                .getSfwidth()
                                .setText(
                                        Formatter.formatMeters(route
                                                .getWaypoints().get(id)
                                                .getOutLeg().getSFWidth()));
                    }
                });
                break;

            case "sfl":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        waypointTable
                                .get(id)
                                .getSflen()
                                .setText(
                                        Formatter.formatMeters(route
                                                .getWaypoints().get(id)
                                                .getOutLeg().getSFLen()));
                    }
                });
                break;
            }

        }

        if (e.getSource() == nameTxT) {
            route.setName(nameTxT.getText());
        } else if (e.getSource() == originTxT) {
            route.setDeparture(originTxT.getText());
        } else if (e.getSource() == destinationTxT) {
            route.setDestination(destinationTxT.getText());
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof WaypointJTextField) {
            WaypointJTextField txtField = (WaypointJTextField) e.getSource();

            selectedWp = txtField.getId();
            lastSelectedWp = selectedWp;

            selectRow(selectedWp);

            // System.out.println("Selected a waypoint object on "
            // + txtField.getId());
        } else {
            if (selectedWp >= 0) {
                deselectRow(selectedWp);
                selectedWp = -1;
            }

        }
        updateButtons();

    }

    private void selectRow(int id) {
        waypointTable.get(id).setSelected(activeRoute != null);
    }

    private void deselectRow(int id) {
        waypointTable.get(id).deSelect(activeRoute != null);

    }

    private void activateWp() {
        int index = selectedWp;
        if (index < 0) {
            return;
        }
        routeManager.changeActiveWp(index);
        // updateTable();
        // Check for updated stuff
        // TO DO
    }

    @SuppressWarnings("deprecation")
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnZoomTo) {
            List<Position> waypoint = new ArrayList<>();
            waypoint.add(route.getWaypoints().get(selectedWp).getPos());

            // EPD.getMainFrame().getChartPanel().zoomTo(waypoint);
        }

        if (e.getSource() == closeBtn) {
            close();
        }
        if (e.getSource() == departurePicker) {
            route.getStarttime().setMonth(departurePicker.getDate().getMonth());
            route.getStarttime().setDate(departurePicker.getDate().getDate());

            checkTimeDiff();
            // System.out.println(departurePicker.getDate());
        }
        // departurePicker

        // if (e.getSource() == arrivalPicker) {
        //
        // }

        else if (e.getSource() == btnActivate) {
            activateWp();
        } else if (e.getSource() == btnDelete) {
            // System.out.println(lastSelectedWp);
            if (lastSelectedWp < 0) {
                return;
            }

            if (route.getWaypoints().size() < 3) {
                int result = JOptionPane
                        .showConfirmDialog(
                                parent,
                                "A route must have at least two waypoints.\nDo you want to delete the route?",
                                "Delete Route?", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    route.deleteWaypoint(lastSelectedWp);

                    if (routeManager != null) {

                        routeManager.removeRoute(routeManager
                                .getRouteIndex(route));
                        routeManager
                                .notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
                        close();
                    }
                }
            }

            else {
                route.deleteWaypoint(lastSelectedWp);

                if (routeManager != null) {
                    routeManager
                            .notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
                    removeAndMoveWaypoints(lastSelectedWp);
                }
            }

            // updateTable();
        }
    }

    private void removeAndMoveWaypoints(int waypointId) {
        RoutePropertiesRow toBeDeleted = waypointTable.get(waypointId);

        // System.out.println("Remove waypoint at y"
        // + toBeDeleted.getName().getLocation().y);
        // System.out.println("Move up all waypoints at " + waypointId + 1);

        int moveLocation = 0;
        for (int i = waypointId + 1; i < waypointTable.size(); i++) {
            waypointTable.get(i).moveRow(
                    toBeDeleted.getName().getLocation().y + moveLocation * 19);
            waypointTable.get(i).updateId();
            moveLocation++;
        }
        removeWaypoint(waypointId);
        waypointTable.remove(waypointId);

        waypointTable.get(waypointTable.size() - 1).setLast(true);
        waypointTable.get(0).setFirst();
        selectedWp = -1;
        lastSelectedWp = -1;

        checkTimeDiff();
    }

    private void removeWaypoint(int id) {
        waypointList.remove(waypointTable.get(id).getLockLbl());
        waypointList.remove(waypointTable.get(id).getName());
        waypointList.remove(waypointTable.get(id).getBrg());
        waypointList.remove(waypointTable.get(id).getEta());
        waypointList.remove(waypointTable.get(id).getHeading());
        waypointList.remove(waypointTable.get(id).getLatitude());
        waypointList.remove(waypointTable.get(id).getLongitude());
        waypointList.remove(waypointTable.get(id).getRad());
        waypointList.remove(waypointTable.get(id).getRot());
        waypointList.remove(waypointTable.get(id).getRng());
        waypointList.remove(waypointTable.get(id).getSflen());
        waypointList.remove(waypointTable.get(id).getSfwidth());
        waypointList.remove(waypointTable.get(id).getSog());
        waypointList.remove(waypointTable.get(id).getTtg());
        waypointList.remove(waypointTable.get(id).getXtdp());
        waypointList.remove(waypointTable.get(id).getXtds());

        waypointList.updateUI();

    }

    public void checkLocks() {
        // Is everything but last locked? lock the last one...

        int locked = 0;
        for (int i = 0; i < waypointTable.size(); i++) {

            if (waypointTable.get(i).isLocked()) {

                locked++;
            }

        }

        if (locked == waypointTable.size() - 1
                && !waypointTable.get(waypointTable.size() - 1).isLocked()) {
            waypointTable.get(waypointTable.size() - 1).getLockLbl()
                    .lockButton();
            arrivalPicker.setEnabled(false);
            arrivalSpinner.setEnabled(false);
        } else {
            waypointTable.get(waypointTable.size() - 1).getLockLbl()
                    .unlockButton();
            arrivalPicker.setEnabled(true);
            arrivalSpinner.setEnabled(true);
        }

    }

    @Override
    public void run() {
        // while (true) {
        // EeINS.sleep(10000);
        // updateDynamicValues();
        // wptTableModel.fireTableDataChanged();
        // }
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (routeManager != null) {
            routeManager.validateMetoc(route);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    private RoutePropertiesRow generateWaypoint(int y, String name,
            String latitude, String longitude, String rad, String rot,
            String ttg, String eta, String rng, String brg, String heading,
            String speed, String xtds, String xtdp, String sfwidth,
            String sflen, int id, int total) {

        LockLabel lockLbl = new LockLabel(this);
        lockLbl.setBackground(Color.WHITE);
        lockLbl.setBounds(0, y, 17, 20);

        waypointList.add(lockLbl);

        WaypointJTextField nameTxT = new WaypointJTextField(id, "name");
        nameTxT.setBounds(16, y, 60, 20);
        waypointList.add(nameTxT);
        nameTxT.setColumns(10);
        nameTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        nameTxT.setText(name);
        nameTxT.addFocusListener(this);

        nameTxT.getDocument().addDocumentListener(this);
        nameTxT.getDocument().putProperty("name", "nameTxT:" + id);

        WaypointJTextField latTxT = new WaypointJTextField(id, "lat");
        latTxT.setColumns(10);
        latTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        latTxT.setBounds(76, y, 69, 20);
        waypointList.add(latTxT);
        latTxT.addFocusListener(this);
        latTxT.setText(latitude);

        latTxT.getDocument().addDocumentListener(this);
        latTxT.getDocument().putProperty("name", "latTxT:" + id);

        WaypointJTextField lonTxT = new WaypointJTextField(id, "lon");
        lonTxT.setColumns(10);
        lonTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        lonTxT.setBounds(145, y, 69, 20);
        waypointList.add(lonTxT);
        lonTxT.setText(longitude);
        lonTxT.addFocusListener(this);

        lonTxT.getDocument().addDocumentListener(this);
        lonTxT.getDocument().putProperty("name", "lonTxT:" + id);

        WaypointJTextField radTxT = new WaypointJTextField(id, "rad");
        radTxT.setColumns(10);
        radTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        radTxT.setBounds(214, y, 45, 20);
        waypointList.add(radTxT);
        radTxT.setText(rad);
        radTxT.addFocusListener(this);

        radTxT.getDocument().addDocumentListener(this);
        radTxT.getDocument().putProperty("name", "radTxT:" + id);

        if (id == 0) {
            radTxT.setEditable(false);
        }
        WaypointJTextField rotTxT = new WaypointJTextField(id, "rot");
        rotTxT.setColumns(10);
        rotTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        rotTxT.setBounds(259, y, 30, 20);
        waypointList.add(rotTxT);
        rotTxT.setText(rot);
        rotTxT.setEditable(false);
        rotTxT.addFocusListener(this);

        WaypointJTextField ttgTxT = new WaypointJTextField(id, "ttg");
        ttgTxT.setColumns(10);
        ttgTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        ttgTxT.setBounds(289, y, 64, 20);
        waypointList.add(ttgTxT);
        ttgTxT.setText(ttg);
        ttgTxT.setEditable(false);
        ttgTxT.addFocusListener(this);

        WaypointJTextField etaTxT = new WaypointJTextField(id, "eta");
        etaTxT.setColumns(10);
        etaTxT.setBorder(new MatteBorder(1, 1, 1, 1, new Color(65, 65, 65)));
        etaTxT.setBounds(353, y, 86, 20);
        waypointList.add(etaTxT);
        etaTxT.setText(eta);
        etaTxT.setEditable(false);
        etaTxT.addFocusListener(this);

        WaypointJTextField rngTxT = new WaypointJTextField(id, "rng");
        rngTxT.setColumns(10);
        rngTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        rngTxT.setBounds(438, y + offset, 54, 20);
        waypointList.add(rngTxT);
        rngTxT.setText(rng);
        rngTxT.setEditable(false);
        rngTxT.addFocusListener(this);

        WaypointJTextField brgTxT = new WaypointJTextField(id, "brg");
        brgTxT.setColumns(10);
        brgTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        brgTxT.setBounds(492, y + offset, 54, 20);
        waypointList.add(brgTxT);
        brgTxT.setText(brg);
        brgTxT.setEditable(false);
        brgTxT.addFocusListener(this);

        WaypointJTextField headingTxT = new WaypointJTextField(id, "heading");
        headingTxT.setColumns(10);
        headingTxT
                .setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        headingTxT.setBounds(546, y + offset, 48, 20);
        waypointList.add(headingTxT);
        headingTxT.setText(heading);
        headingTxT.addFocusListener(this);

        headingTxT.getDocument().addDocumentListener(this);
        headingTxT.getDocument().putProperty("name", "headingTxT:" + id);

        WaypointJTextField sogTxT = new WaypointJTextField(id, "sog");
        sogTxT.setColumns(10);
        sogTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        sogTxT.setBounds(594, y + offset, 48, 20);
        waypointList.add(sogTxT);
        sogTxT.setText(speed);
        sogTxT.addFocusListener(this);

        sogTxT.getDocument().addDocumentListener(this);
        sogTxT.getDocument().putProperty("name", "sogTxT:" + id);

        WaypointJTextField xtdsTxT = new WaypointJTextField(id, "xtds");
        xtdsTxT.setColumns(10);
        xtdsTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        xtdsTxT.setBounds(642, y + offset, 44, 20);
        waypointList.add(xtdsTxT);
        xtdsTxT.setText(xtds);
        xtdsTxT.addFocusListener(this);

        xtdsTxT.getDocument().addDocumentListener(this);
        xtdsTxT.getDocument().putProperty("name", "xtdSTxT:" + id);

        WaypointJTextField xtdPTxT = new WaypointJTextField(id, "xtdp");
        xtdPTxT.setColumns(10);
        xtdPTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        xtdPTxT.setBounds(686, y + offset, 44, 20);
        waypointList.add(xtdPTxT);
        xtdPTxT.setText(xtdp);
        xtdPTxT.addFocusListener(this);

        xtdPTxT.getDocument().addDocumentListener(this);
        xtdPTxT.getDocument().putProperty("name", "xtdPTxT:" + id);

        WaypointJTextField sfwTxT = new WaypointJTextField(id, "sfw");
        sfwTxT.setColumns(10);
        sfwTxT.setBorder(new MatteBorder(1, 1, 1, 0, new Color(65, 65, 65)));
        sfwTxT.setBounds(730, y + offset, 54, 20);
        waypointList.add(sfwTxT);
        sfwTxT.setText(sfwidth);
        sfwTxT.addFocusListener(this);

        sfwTxT.getDocument().addDocumentListener(this);
        sfwTxT.getDocument().putProperty("name", "sfWTxT:" + id);

        WaypointJTextField sflTxT = new WaypointJTextField(id, "sfl");
        sflTxT.setColumns(10);
        sflTxT.setBorder(new MatteBorder(1, 1, 1, 1, new Color(65, 65, 65)));
        sflTxT.setBounds(784, y + offset, 54, 20);
        sflTxT.setText(sflen);
        waypointList.add(sflTxT);
        sflTxT.addFocusListener(this);

        sflTxT.getDocument().addDocumentListener(this);
        sflTxT.getDocument().putProperty("name", "sfLTxT:" + id);

        RoutePropertiesRow routeRow = null;

        if (id == total - 1) {

            // Disable stuff
            headingTxT.setEditable(false);
            headingTxT.setVisible(false);
            sogTxT.setEditable(false);
            sogTxT.setVisible(false);
            xtdsTxT.setEditable(false);
            xtdsTxT.setVisible(false);
            xtdPTxT.setEditable(false);
            xtdPTxT.setVisible(false);
            sfwTxT.setEditable(false);
            sfwTxT.setVisible(false);
            sflTxT.setEditable(false);
            sflTxT.setVisible(false);
            rngTxT.setVisible(false);
            brgTxT.setVisible(false);

            routeRow = new RoutePropertiesRow(nameTxT, latTxT, lonTxT, radTxT,
                    rotTxT, ttgTxT, etaTxT, rngTxT, brgTxT, headingTxT, sogTxT,
                    xtdsTxT, xtdPTxT, sfwTxT, sflTxT, id, true, lockLbl);

        } else {
            routeRow = new RoutePropertiesRow(nameTxT, latTxT, lonTxT, radTxT,
                    rotTxT, ttgTxT, etaTxT, rngTxT, brgTxT, headingTxT, sogTxT,
                    xtdsTxT, xtdPTxT, sfwTxT, sflTxT, id, false, lockLbl);
        }

        lockLbl.setOwnRow(routeRow);

        if (activeRoute != null) {
            nameTxT.setEditable(false);
            latTxT.setEditable(false);
            lonTxT.setEditable(false);
            lockLbl.setEnabled(false);
            sogTxT.setEditable(false);
            sflTxT.setEditable(false);
            sfwTxT.setEditable(false);
            xtdsTxT.setEditable(false);
            xtdPTxT.setEditable(false);
            radTxT.setEditable(false);
            headingTxT.setEditable(false);

        }

        return routeRow;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    @SuppressWarnings("deprecation")
    @Override
    public void insertUpdate(DocumentEvent e) {

        String name = (String) e.getDocument().getProperty("name");

        // Departure
        if ("departureSpinner".equals(name)) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) departureSpinner
                    .getEditor();
            // System.out.println("DepartureTime was changed to "
            // + departureSpinner.getValue());
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());
                route.getStarttime().setHours(testDate.getHours());
                route.getStarttime().setMinutes(testDate.getMinutes());
                route.getStarttime().setSeconds(testDate.getSeconds());

                checkTimeDiff();
            } catch (ParseException e1) {
                // Ignore
            }

            // departurePicker

            // System.out.println("DepartureTime text was changed to "
            // + editor.getTextField().getText());
        }

        if ("departurePicker".equals(name)) {
            SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");
            Date testDate = null;

            try {
                testDate = format.parse(departurePicker.getEditor().getText());
                testDate.setHours(route.getStarttime().getHours());
                testDate.setMinutes(route.getStarttime().getMinutes());
                testDate.setSeconds(route.getStarttime().getSeconds());
                route.getStarttime().setTime(testDate.getTime());

                checkTimeDiff();
            } catch (ParseException e1) {
                // ignore
            }
        }

        // Arrival
        // System.out.println("Document listener " + msgbox + " " + name + " " +
        // e.getDocument());

        if ("arrivalSpinner".equals(name) && !internalOperation) {

            // Generate the current arrival date
            Date currentArrivalDate = arrivalPicker.getDate();

            JSpinner.DateEditor editor = (JSpinner.DateEditor) arrivalSpinner
                    .getEditor();
            // System.out.println("DepartureTime was changed to "
            // + departureSpinner.getValue());
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());
                // Found valid time, combing
                currentArrivalDate.setHours(testDate.getHours());
                currentArrivalDate.setMinutes(testDate.getMinutes());
                currentArrivalDate.setSeconds(testDate.getSeconds());

                if (route.getStarttime().getTime() >= currentArrivalDate
                        .getTime()) {

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // departurePicker.setDate(starttime);
                            long startTimeVal = route.getStarttime().getTime() + 3600000;
                            Date eta = new Date(startTimeVal);
                            ((SpinnerDateModel) arrivalSpinner.getModel())
                                    .setValue(eta);
                        }
                    });

                } else {
                    // Find new speeds and let that recalculate everything

                    recalculateSpeeds(currentArrivalDate);

                }

            } catch (ParseException e1) {
                // Ignore
            }

        }

        if ("arrivalPicker".equals(name) && !internalOperation) {

            // Get the time
            Date time = (Date) arrivalSpinner.getValue();

            // Get the date
            SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");
            Date currentArrivalDate = null;
            try {
                currentArrivalDate = format.parse(arrivalPicker.getEditor()
                        .getText());
                currentArrivalDate.setHours(time.getHours());
                currentArrivalDate.setMinutes(time.getMinutes());
                currentArrivalDate.setSeconds(time.getSeconds());

                if (route.getStarttime().getTime() >= currentArrivalDate
                        .getTime()) {

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            arrivalPicker.setDate(route.calculateEta());
                            // ((SpinnerDateModel)
                            // arrivalSpinner.getModel()).setValue(route
                            // .getEta(route.getStarttime()));
                        }
                    });

                } else {
                    // Find new speeds and let that recalculate everything

                    recalculateSpeeds(currentArrivalDate);

                }

            } catch (ParseException e2) {

                // Ignore
            }

            // Combine them

            // Check if possible

            // Either calculate speeds or reset

            // Generate the current arrival date

        }

        modifyWaypoint(name);

    }

    private void recalculateSpeeds(Date currentArrivalDate) {

        // System.out.println("Recalculating speeds?");

        // Total distance
        double distanceToTravel = route.calcDtg();

        for (int i = 0; i < waypointTable.size(); i++) {

            if (waypointTable.get(i).isLocked()) {
                distanceToTravel = distanceToTravel - route.getWpRng(i);
            }

        }

        // System.out.println("Locked is: " + locked);
        // System.out.println("Total size is " + waypointTable.size());
        //
        // if (locked == waypointTable.size() - 1
        // && !waypointTable.get(waypointTable.size() - 1).isLocked()) {
        //
        // waypointTable.get(0).getLockLbl().unlockButton();
        //
        // JOptionPane
        // .showMessageDialog(
        // this,
        // "All waypoints are locked, cannot calculate new SoG. Unlocking WP1",
        // "Input error", JOptionPane.ERROR_MESSAGE);
        //
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        //
        // internalOperation = true;
        // System.out.println("Resetting");
        //
        // arrivalPicker.setDate(route.getEta());
        // ((SpinnerDateModel) arrivalSpinner.getModel())
        // .setValue(route.getEta(route.getStarttime()));
        // //
        // // JOptionPane.showMessageDialog(null,
        // // "All waypoints are locked, cannot calculate new sog",
        // // "Input error", JOptionPane.ERROR_MESSAGE);
        // internalOperation = false;
        // System.out.println("Internal operation is false");
        //
        // }
        // });
        // System.out.println("Cannot proceed, everything is locked");
        //
        //
        // } else {

        // System.out.println("New total distance: " + distanceToTravel);

        // And we want to get there in miliseconds:
        long timeToTravel = currentArrivalDate.getTime()
                - route.getStarttime().getTime();

        // System.out.println("We want to travel " + distanceToTravel +
        // " nm");
        // System.out.println("We have " + timeToTravel / 60 / 1000
        // + " minutes to get there");

        // So we need to travel how fast?
        double speed = distanceToTravel / (timeToTravel / 60 / 1000) * 60;

        for (int i = 0; i < route.getWaypoints().size(); i++) {
            if (!waypointTable.get(i).isLocked()) {
                route.getWaypoints().get(i).setSpeed(speed);
                // }

            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    internalOperation = true;
                    refreshSoGList();
                    updateFields();
                    internalOperation = false;
                    // System.out.println("Internal operation is false");
                }
            });

        }
    }

    void refreshSoGList() {
        for (int i = 0; i < route.getWaypoints().size() - 1; i++) {
            RouteWaypoint currentWaypoint = route.getWaypoints().get(i);
            String speed = Formatter.formatSpeed(currentWaypoint.getOutLeg()
                    .getSpeed());

            waypointTable.get(i).getSog().setText(speed);
        }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        String name = (String) e.getDocument().getProperty("name");
        modifyWaypoint(name);
    }

    private void modifyWaypoint(String name) {

        // Waypoint name
        if (name.contains("nameTxT")) {
            modifyName(name);
        }

        // Waypoint Lat
        if (name.contains("latTxT")) {
            modifyLat(name);
        }

        // Waypoint Lon
        if (name.contains("lonTxT")) {
            modifyLon(name);
        }

        // Waypoint Rad
        if (name.contains("radTxT")) {
            modifyRad(name);
        }

        // Waypoint Heading
        if (name.contains("headingTxT")) {
            modifyHeading(name);
        }

        // Waypoint Sog
        if (name.contains("sogTxT") && !internalOperation) {
            modifySog(name);
        }

        // Waypoint Xtd S
        if (name.contains("xtdSTxT")) {
            modifyXtdS(name);
        }

        // Waypoint Xtd P
        if (name.contains("xtdPTxT")) {
            modifyXtdP(name);
        }

        // Waypoint SF W
        if (name.contains("sfWTxT")) {
            modifySFW(name);
        }

        // Waypoint SF Len
        if (name.contains("sfLTxT")) {
            modifySFL(name);
        }
    }

    private void modifyName(String name) {
        int id = Integer.parseInt(name.split(":")[1]);

        route.getWaypoints().get(id)
                .setName(waypointTable.get(id).getName().getText());
    }

    private void modifyLat(String name) {
        final int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);
        try {
            route.getWaypoints()
                    .get(id)
                    .setPos(wpt.getPos().withLatitude(
                            parseLat(waypointTable.get(id).getLatitude()
                                    .getText())));

            // System.out.println("Latitude was updated on " + id);
            // System.out.println("New value is " +
            // route.getWaypoints().get(id).getPos().getLatitudeAsString());
            // System.out.println("New value is " +
            // Formatter.latToPrintable(wpt.getPos().getLatitude()));
            // Formatter.latToPrintable(wpt.getPos().getLatitude()
        } catch (Exception e1) {
            // Invalid lat, we do nothing, focus lost will handle it
        }

    }

    private void modifyLon(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);
        try {
            wpt.setPos(wpt.getPos().withLongitude(
                    parseLon(waypointTable.get(id).getLongitude().getText())));
        } catch (Exception e1) {
            // Invalid lon, we do nothing, focus lost will handle it
        }
    }

    private void modifyRad(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);
        try {
            wpt.setTurnRad(parseDouble(waypointTable.get(id).getRad().getText()));
        } catch (Exception e1) {
            // Invalid rad, we do nothing, focus lost will handle it
        }
    }

    private void modifyHeading(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);

        String head = waypointTable.get(id).getHeading().getText();
        if (head != null && head.equalsIgnoreCase("GC")) {
            wpt.getOutLeg().setHeading(Heading.GC);
        } else {
            wpt.getOutLeg().setHeading(Heading.RL);
        }
    }

    private void modifySog(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);

        try {

            if (!waypointTable.get(id).getSog().getText().equals("")) {
                wpt.getOutLeg().setSpeed(
                        parseDouble(waypointTable.get(id).getSog().getText()));
                checkTimeDiff();
            }

        } catch (Exception e) {
            // Ignore exception, value not set and corrected in lost focus
        }
    }

    private void modifyXtdP(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);

        try {
            wpt.getOutLeg()
                    .setXtdPort(
                            parseDouble(waypointTable.get(id).getXtdp()
                                    .getText()) / 1852.0);
        } catch (Exception e) {
            //
        }
    }

    private void modifyXtdS(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);

        try {
            wpt.getOutLeg()
                    .setXtdStarboard(
                            parseDouble(waypointTable.get(id).getXtds()
                                    .getText()) / 1852.0);
        } catch (Exception e) {
            //
        }
    }

    private void modifySFW(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);

        try {
            wpt.getOutLeg().setSFWidth(
                    parseDouble(waypointTable.get(id).getSfwidth().getText()));
        } catch (Exception e) {
            //
        }
    }

    private void modifySFL(String name) {
        int id = Integer.parseInt(name.split(":")[1]);
        RouteWaypoint wpt = route.getWaypoints().get(id);

        try {
            wpt.getOutLeg().setSFLen(
                    parseDouble(waypointTable.get(id).getSflen().getText()));
        } catch (Exception e) {
            //
        }
    }

    @SuppressWarnings("deprecation")
    void checkTimeDiff() {

        if (activeRoute == null) {

            // System.out.println("Check time diff called?");

            internalOperation = true;

            route.adjustStartTime();
            final Date starttime = route.getStarttime();

            // Is the date the same?
            if (starttime.getDate() != departurePicker.getDate().getDate()
                    || starttime.getMonth() != departurePicker.getDate()
                            .getMonth()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        departurePicker.setDate(starttime);
                    }
                });

            }

            JSpinner.DateEditor editor = (JSpinner.DateEditor) departureSpinner
                    .getEditor();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());
            } catch (ParseException e1) {
                // Ignore
            }

            // Is the time the same?
            if (starttime.getHours() != testDate.getHours()
                    || starttime.getMinutes() != testDate.getMinutes()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // departurePicker.setDate(starttime);
                        ((SpinnerDateModel) departureSpinner.getModel())
                                .setValue(starttime);
                    }
                });

            }

            arrivalPicker.setDate(route.getEta(starttime));

            ((SpinnerDateModel) arrivalSpinner.getModel()).setValue(route
                    .getEta(starttime));

            // route.calcAllWpEta();

            updateFields();

            internalOperation = false;
            // System.out.println("Internal operation is false");
        }
    }

    private static double parseLat(String latStr) throws FormatException {
        return ParseUtils.parseLatitude(latStr);
    }

    private static double parseLon(String lonStr) throws FormatException {
        return ParseUtils.parseLongitude(lonStr);
    }

    private static double parseDouble(String str) throws FormatException {
        str = str.replaceAll(",", ".");
        String[] parts = StringUtils.split(str, " ");
        return ParseUtils.parseDouble(parts[0]);
    }

    void updateFields() {
        route.calcValues(true);
        route.calcAllWpEta();

        inrouteTxT.setText(Formatter.formatTime(route.calcTtg()));

        for (int i = 0; i < route.getWaypoints().size(); i++) {
            String eta = Formatter.formatShortDateTimeNoTz(route.getWpEta(i));
            String ttg = Formatter.formatTime(route.getWpTtg(i));
            waypointTable.get(i).getEta().setText(eta);
            waypointTable.get(i).getTtg().setText(ttg);

        }

    }

}
