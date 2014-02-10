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

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static dk.dma.epd.common.graphics.GraphicsUtil.fixSize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Dialog used for viewing and editing route properties
 */
public class RoutePropertiesDialogCommon2 extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int DELTA_START_COL_INDEX = 8;

    private Window parent;
    private RouteManagerCommon routeManager;
    protected ActiveRoute activeRoute;
    protected Route route = new Route();
    protected boolean[] locked;

    // Column 1 widgets
    private JTextField nameTxT = new JTextField();
    private JTextField originTxT = new JTextField();
    private JTextField destinationTxT = new JTextField();
    private JTextField distanceTxT = new JTextField();

    // Column 2 widgets
    private JXDatePicker departurePicker = new JXDatePicker();
    private JSpinner departureSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
    private JXDatePicker arrivalPicker = new JXDatePicker();
    private JSpinner arrivalSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
    private JTextField inrouteTxT = new JTextField();
    private JComboBox<Route.EtaCalculationType> ddlTtgData 
        = new JComboBox<Route.EtaCalculationType>(Route.EtaCalculationType.values());
    
    // Route details table
    private DefaultTableModel routeTableModel;    
    private DeltaTable routeDetailTable;
    private int selectedWp = -1;
    
    // Button panel
    private JButton btnZoomTo = new JButton("Zoom to");
    private JButton btnDelete = new JButton("Delete");
    protected JButton btnActivate = new JButton("Activate");
    private JButton btnClose = new JButton("Close");

    
    /**
     * Constructor
     * 
     * @param parent the parent window
     * @param routeManager the route manager
     * @param routeId the route index
     */
    public RoutePropertiesDialogCommon2(Window parent, RouteManagerCommon routeManager, int routeId) {
        this(parent, routeManager.getRoute(routeId), true);
        this.routeManager = routeManager;
    }
    
    /**
     * Constructor
     * 
     * @param parent the parent window
     * @param route the route
     * @param editable whether the route is editable or not
     */
    public RoutePropertiesDialogCommon2(Window parent, Route route, boolean editable) {
        super(parent, "Route Properties", Dialog.ModalityType.APPLICATION_MODAL);
        
        this.parent = parent;
        this.route = route;
        if (!editable) {
            activeRoute = new ActiveRoute(route, null);
        }
        locked = new boolean[route.getWaypoints().size()];
        
        initGui();
        initValues();
        
        setBounds(100, 100, 1000, 500);
    }
    
    /**
     * Initializes the user interface
     */
    private void initGui() {
        Insets insets1  = new Insets(5, 5, 0, 5);
        Insets insets2  = new Insets(5, 25, 0, 5);
        Insets insets3  = new Insets(5, 5, 0, 0);
        Insets insets4  = new Insets(5, 0, 0, 5);
        Insets insets5  = new Insets(5, 5, 5, 5);
        Insets insets6  = new Insets(5, 25, 5, 5);
        Insets insets10  = new Insets(10, 10, 10, 10);
        
        JPanel content = new JPanel(new GridBagLayout());
        getContentPane().add(content);
        
        
        // ********************************
        // ** Route properties panel
        // ********************************
        
        JPanel routeProps = new JPanel(new GridBagLayout());
        routeProps.setBorder(new TitledBorder(new LineBorder(Color.black), "Route Properties"));
        content.add(routeProps, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets10, 0, 0));
        
        // Auto-completion
        List<String> strings = new ArrayList<>();
        strings.add("Copenhagen");
        strings.add("Oslo");
        AutoCompleteDecorator.decorate(originTxT, strings, false);
        AutoCompleteDecorator.decorate(destinationTxT, strings, false);
        
        // Column 1 widgets
        int gridY = 0;        
        routeProps.add(new JLabel("Name:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routeProps.add(fixSize(nameTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        routeProps.add(new JLabel("Origin:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routeProps.add(fixSize(originTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        routeProps.add(new JLabel("Destination:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routeProps.add(fixSize(destinationTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        distanceTxT.setEditable(false);
        routeProps.add(new JLabel("Total Distance:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routeProps.add(fixSize(distanceTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        // Column 2 widgets
        gridY = 0;
        int h = (int)departurePicker.getPreferredSize().getHeight();
        departurePicker.setFormats(new SimpleDateFormat("E dd/MM/yyyy"));
        departureSpinner.setEditor(new JSpinner.DateEditor(departureSpinner, "HH:mm"));
        routeProps.add(new JLabel("Estimated Time of Departure:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        routeProps.add(fixSize(departurePicker, 120), new GridBagConstraints(3, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets3, 0, 0));
        routeProps.add(fixSize(departureSpinner, 60, h), new GridBagConstraints(4, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets4, 0, 0));
        
        arrivalPicker.setFormats(new SimpleDateFormat("E dd/MM/yyyy"));
        arrivalSpinner.setEditor(new JSpinner.DateEditor(arrivalSpinner, "HH:mm"));
        routeProps.add(new JLabel("Estimated Time of Arrival:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        routeProps.add(fixSize(arrivalPicker, 120), new GridBagConstraints(3, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets3, 0, 0));
        routeProps.add(fixSize(arrivalSpinner, 60, h), new GridBagConstraints(4, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets4, 0, 0));
        
        inrouteTxT.setEditable(false);
        routeProps.add(new JLabel("Estimated Time in-route:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        routeProps.add(fixSize(inrouteTxT, 180), new GridBagConstraints(3, gridY++, 2, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));

        routeProps.add(new JLabel("Calculate TTG/ETA using:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets6, 0, 0));
        routeProps.add(fixSize(ddlTtgData, 180), new GridBagConstraints(3, gridY++, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        routeProps.add(new JLabel(""), new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));

        
        // ********************************
        // ** Route detail panel
        // ********************************
        
        routeTableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            String[] columnNames = {
                    " ", "Name", "Latutide", "Longtitude", "Rad", "Rot", "TTG", "ETA", "RNG",
                    "BRG", "Heading", "SOG", "XTDS", "XTD P", "SF Width", "SFLen" };

            @Override
            public int getRowCount() {
                return route.getWaypoints().size();
            }

            @Override
            public int getColumnCount() {
                return 16;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return columnNames[columnIndex];
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                RouteWaypoint wp = route.getWaypoints().get(rowIndex);
                switch (columnIndex) {
                case  0: return locked[rowIndex];
                case  1: return wp.getName();
                case  2: return wp.getPos().getLatitudeAsString();
                case  3: return wp.getPos().getLongitudeAsString();
                case  4: return wp.getTurnRad() == null ? "N/A" : Formatter.formatDistNM(wp.getTurnRad());
                case  5: return "N/A";
                case  6: return Formatter.formatTime(route.getWpTtg(rowIndex));
                case  7: return Formatter.formatShortDateTimeNoTz(route.getWpEta(rowIndex));
                case  8: return Formatter.formatDistNM(route.getWpRng(rowIndex));
                case  9: return Formatter.formatDegrees(route.getWpBrg(wp), 2);
                case 10: return wp.getHeading();
                case 11: return Formatter.formatSpeed(wp.getOutLeg().getSpeed());
                case 12: return Formatter.formatMeters(wp.getOutLeg().getXtdStarboardMeters());
                case 13: return Formatter.formatMeters(wp.getOutLeg().getXtdPortMeters());
                case 14: return Formatter.formatMeters(wp.getOutLeg().getSFWidth());
                case 15: return Formatter.formatMeters(wp.getOutLeg().getSFLen());
                default: return null;
                }
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                try {
                    RouteWaypoint wp = route.getWaypoints().get(rowIndex);
                    switch (columnIndex) {
                    case  0: 
                        locked[rowIndex] = ((Boolean)value).booleanValue(); 
                        fireTableRowsUpdated(rowIndex, rowIndex); 
                        break;
                    case  1: wp.setName(value.toString()); break;
                    case  2: wp.setPos(Position.create(ParseUtils.parseLatitude(value.toString()), wp.getPos().getLongitude())); break;
                    case  3: wp.setPos(Position.create(wp.getPos().getLatitude(), ParseUtils.parseLatitude(value.toString()))); break;
                    case  10: wp.getOutLeg().setHeading((Heading)value); break;
                    default:
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return (columnIndex == 0 || !locked[rowIndex]) &&
                        (columnIndex < 5 || columnIndex > 9);
            }   
        };
        
        
        // ********************************
        // ** Route detail panel
        // ********************************
        
        routeDetailTable = new DeltaTable(routeTableModel, DELTA_START_COL_INDEX);
        routeDetailTable.setTableFont(routeDetailTable.getTableFont().deriveFont(10.0f));
        routeDetailTable.setNonEditableBgColor(UIManager.getColor("Table.background").darker().darker());
        
        routeDetailTable.addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectedWp = routeDetailTable.getSelectedRow();
                    updateButtons();
                }
            }});

        // Configure lock column
        routeDetailTable.fixColumnWidth(0, 25);
        routeDetailTable.getColumn(0).setCellRenderer(new LockTableCell.CustomBooleanCellRenderer());
        routeDetailTable.getColumn(0).setCellEditor(new LockTableCell.CustomBooleanCellEditor());

        // Configure heading column
        JComboBox<Heading> headingCombo = new JComboBox<>(Heading.values());
        headingCombo.setFont(headingCombo.getFont().deriveFont(10.0f));
        routeDetailTable.getColumn(10).setCellEditor(new DefaultCellEditor(headingCombo));

        JPanel routeTablePanel = new JPanel(new BorderLayout());
        routeTablePanel.add(routeDetailTable, BorderLayout.CENTER);
        routeTablePanel.setBorder(new TitledBorder(new LineBorder(Color.black), "Route Details"));
        content.add(routeTablePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, insets10, 0, 0));
        
        
        // ********************************
        // ** Button panel
        // ********************************
        
        JPanel btnPanel = new JPanel(new GridBagLayout());
        content.add(btnPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, insets10, 0, 0));
        
        btnZoomTo.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                // TODO: EPD.getMainFrame().getChartPanel().zoomTo(waypoint);
            }});
        btnDelete.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                onDelete();
            }});
        btnActivate.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
            }});
        btnClose.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                dispose();
            }});
        btnPanel.add(btnZoomTo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnDelete, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnActivate, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnClose, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0, EAST, NONE, insets5, 0, 0));
    }
    
    /**
     * Updates the dialog with the value of the current route
     */
    private void initValues() {
        nameTxT.setText(route.getName());
        originTxT.setText(route.getDeparture());
        destinationTxT.setText(route.getDestination());
        
        // Get start time or default now
        route.adjustStartTime();
        Date starttime = route.getStarttime();

        departurePicker.setDate(starttime);
        ((SpinnerDateModel) departureSpinner.getModel()).setValue(starttime);

        // Attempt to get ETA (only possible if GPS data is available)
        Date etaStart = route.getEta(starttime);
        if (etaStart != null) {
            // GPS data available.
            arrivalPicker.setDate(etaStart);
            ((SpinnerDateModel) arrivalSpinner.getModel()).setValue(etaStart);
        } else {
            // No GPS data available.
            // Find the default ETA.
            Date defaultEta = route.getEtas().get(route.getEtas().size() - 1);
            arrivalPicker.setDate(defaultEta);
            ((SpinnerDateModel) arrivalSpinner.getModel()).setValue(defaultEta);
        }

        if (activeRoute != null) {
            departurePicker.setEnabled(false);
            departureSpinner.setEnabled(false);
            arrivalPicker.setEnabled(false);
            arrivalSpinner.setEnabled(false);
        }

        inrouteTxT.setText(Formatter.formatTime(route.getRouteTtg()));
        distanceTxT.setText(Formatter.formatDistNM(route.getRouteDtg()));
        
        updateButtons();
    }
    
    /** 
     * Updates the enabled state of the buttons
     */
    private void updateButtons() {

        boolean wpSelected = selectedWp >= 0;

        if (route == activeRoute) {
            btnActivate.setEnabled(wpSelected);
        } else {
            btnDelete.setEnabled(wpSelected);
        }

        btnZoomTo.setEnabled(wpSelected);
    }
    
    /**
     * Called when Delete is clicked
     */
    private void onDelete() {
        // Check that there is a selection
        if (selectedWp < 0) {
            return;
        }
        
        // If the route has two way points or less, delete the entire route
        if (route.getWaypoints().size() < 3) {
            // ... but get confirmation first
            int result = JOptionPane.showConfirmDialog(parent,
                    "A route must have at least two waypoints.\nDo you want to delete the route?", 
                    "Delete Route?",
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                route.deleteWaypoint(selectedWp);
                routeTableModel.fireTableDataChanged();
                if (routeManager != null) {
                    routeManager.removeRoute(routeManager.getRouteIndex(route));
                    routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
                    dispose();
                }
            }
        }

        else {
            // Delete the selected way point
            route.deleteWaypoint(selectedWp);
            routeTableModel.fireTableDataChanged();
            if (routeManager != null) {
                routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
            }
        }
    }

    
    /**
     * Test method
     */
    public static final void main(String... args) throws Exception {

        //=====================
        // Create test data
        //=====================
        final Route route = new Route();
        final LinkedList<RouteWaypoint> waypoints = new LinkedList<>();
        route.setWaypoints(waypoints);
        route.setStarttime(new Date());

        int len = 10;
        final boolean[] locked = new boolean[len];
        for (int x = 0; x < len; x++) {
            locked[x] = false;
            RouteWaypoint wp = new RouteWaypoint();
            waypoints.add(wp);

            // Set leg values
            if (x > 0) {
                RouteLeg leg = new RouteLeg();
                leg.setSpeed(12.00 + x);
                leg.setHeading(Heading.RL);
                leg.setXtdPort(185.0);
                leg.setXtdStarboard(185.0);

                wp.setInLeg(leg);
                waypoints.get(x-1).setOutLeg(leg);
                leg.setStartWp(waypoints.get(x-1));
                leg.setEndWp(wp);
            }

            wp.setName("WP_00" + x);
            wp.setPos(Position.create(56.02505 + Math.random() * 2.0, 12.37 + Math.random() * 2.0));    
        }
        for (int x = 1; x < len; x++) {
            waypoints.get(x).setTurnRad(0.5 + x * 0.2);
        }
        route.calcValues(true);
        
        // Launch the route properties dialog
        PntTime.init();
        RoutePropertiesDialogCommon2 dialog = new RoutePropertiesDialogCommon2(null, route, true);
        dialog.setVisible(true);
    }   

}
