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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.Route.EtaCalculationType;
import dk.dma.epd.common.prototype.model.route.RouteLeg;
import dk.dma.epd.common.prototype.model.route.RouteWaypoint;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;
import dk.dma.epd.common.util.TypedValue.Dist;
import dk.dma.epd.common.util.TypedValue.DistType;
import dk.dma.epd.common.util.TypedValue.SpeedType;
import dk.dma.epd.common.util.TypedValue.Time;
import dk.dma.epd.common.util.TypedValue.TimeType;

/**
 * Dialog used for viewing and editing route properties
 */
public class RoutePropertiesDialogCommon extends JDialog implements ActionListener, PropertyChangeListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RoutePropertiesDialogCommon.class);
    
    private static final String[] COL_NAMES = {
            " ", "Name", "Latutide", "Longtitude", 
            "Rad", "Rot", "TTG", "ETA", 
            "RNG", "BRG", "Head.", "SOG", 
            "XTDS", "XTD P", "SF Width", "SF Len" };

    private static final int[] COL_MIN_WIDTHS = {
        25, 60, 70, 70,
        45, 30, 70, 70,
        70, 50, 40, 50,
        50, 50, 50, 50
    };
    
    private static final int DELTA_START_COL_INDEX = 8;

    private Window parent;
    private ChartPanelCommon chartPanel;
    private RouteManagerCommon routeManager;
    protected Route route = new Route();
    protected boolean[] locked;
    protected boolean readOnlyRoute;
    boolean quiescent;
    protected List<RouteChangeListener> listeners = new CopyOnWriteArrayList<>();


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
    private JComboBox<EtaCalculationType> etaCalculationTime = new JComboBox<EtaCalculationType>(EtaCalculationType.values());
    
    // Route details table
    private DefaultTableModel routeTableModel;    
    private DeltaTable routeDetailTable;
    private int selectedWp = -1;
    
    // Button panel
    private JButton btnZoomToRoute = new JButton("Zoom to Route");
    private JButton btnZoomToWp = new JButton("Zoom to Way Point");
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
    public RoutePropertiesDialogCommon(Window parent, ChartPanelCommon chartPanel, RouteManagerCommon routeManager, int routeId) {
        this(parent, 
             chartPanel, 
             routeManager.getRoute(routeId), 
             routeManager.isActiveRoute(routeId));
        this.routeManager = routeManager;
    }
    
    /**
     * Constructor
     * 
     * @param parent the parent window
     * @param route the route
     * @param readOnlyRoute whether the route is read-only or not
     */
    public RoutePropertiesDialogCommon(Window parent, ChartPanelCommon chartPanel, Route route, boolean readOnlyRoute) {
        super(parent, "Route Properties", Dialog.ModalityType.APPLICATION_MODAL);
        
        this.parent = parent;
        this.chartPanel = chartPanel;
        this.route = route;
        this.readOnlyRoute = readOnlyRoute;
        locked = new boolean[route.getWaypoints().size()];
        
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (routeManager != null) {
                    routeManager.validateMetoc(RoutePropertiesDialogCommon.this.route);
                }
            }});
        
        initGui();
        initValues();
        
        setBounds(100, 100, 1000, 400);
        setLocationRelativeTo(parent);
    }
    
    /***************************************************/
    /** UI initialization                             **/
    /***************************************************/
    
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
        nameTxT.setEditable(!readOnlyRoute);
        nameTxT.getDocument().addDocumentListener(new TextFieldChangeListener(nameTxT));
        routeProps.add(new JLabel("Name:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routeProps.add(fixSize(nameTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        originTxT.setEditable(!readOnlyRoute);
        originTxT.getDocument().addDocumentListener(new TextFieldChangeListener(originTxT));
        routeProps.add(new JLabel("Origin:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routeProps.add(fixSize(originTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        destinationTxT.setEnabled(!readOnlyRoute);
        destinationTxT.getDocument().addDocumentListener(new TextFieldChangeListener(destinationTxT));
        routeProps.add(new JLabel("Destination:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        routeProps.add(fixSize(destinationTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));
        
        distanceTxT.setEditable(false);
        routeProps.add(new JLabel("Total Distance:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routeProps.add(fixSize(distanceTxT, 120), new GridBagConstraints(1, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        // Column 2 widgets
        gridY = 0;
        int h = (int)departurePicker.getPreferredSize().getHeight();
        initDatePicker(departurePicker, departureSpinner);
        routeProps.add(new JLabel("Estimated Time of Departure:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        routeProps.add(fixSize(departurePicker, 120), new GridBagConstraints(3, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets3, 0, 0));
        routeProps.add(fixSize(departureSpinner, 60, h), new GridBagConstraints(4, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets4, 0, 0));
        
        initDatePicker(arrivalPicker, arrivalSpinner);
        routeProps.add(new JLabel("Estimated Time of Arrival:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        routeProps.add(fixSize(arrivalPicker, 120), new GridBagConstraints(3, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets3, 0, 0));
        routeProps.add(fixSize(arrivalSpinner, 60, h), new GridBagConstraints(4, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets4, 0, 0));
        
        inrouteTxT.setEditable(false);
        routeProps.add(new JLabel("Estimated Time in-route:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
        routeProps.add(fixSize(inrouteTxT, 180), new GridBagConstraints(3, gridY++, 2, 1, 0.0, 0.0, WEST, NONE, insets1, 0, 0));

        etaCalculationTime.setEnabled(!readOnlyRoute);
        etaCalculationTime.addActionListener(this);
        routeProps.add(new JLabel("Calculate TTG/ETA using:"), new GridBagConstraints(2, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets6, 0, 0));
        routeProps.add(fixSize(etaCalculationTime, 180), new GridBagConstraints(3, gridY++, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        routeProps.add(new JLabel(""), new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
        
        
        // ********************************
        // ** Route detail panel
        // ********************************
        
        routeTableModel = createRouteTableModel();
        routeDetailTable = new DeltaTable(routeTableModel, DELTA_START_COL_INDEX);
        routeDetailTable.setTableFont(routeDetailTable.getTableFont().deriveFont(10.0f));
        routeDetailTable.setNonEditableBgColor(UIManager.getColor("Table.background").darker().darker());
        routeDetailTable.addListSelectionListener(this);

        // Set the minimum column widths
        for (int x = 0; x < COL_MIN_WIDTHS.length; x++) {
            routeDetailTable.getColumn(x).setMinWidth(COL_MIN_WIDTHS[x]);
        }
        
        // Configure lock column
        routeDetailTable.fixColumnWidth(0, COL_MIN_WIDTHS[0]);
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
        
        btnZoomToRoute.addActionListener(this);
        btnZoomToWp.addActionListener(this);
        btnDelete.addActionListener(this);
        btnActivate.addActionListener(this);
        btnClose.addActionListener(this);
        getRootPane().setDefaultButton(btnClose);
        btnPanel.add(btnZoomToRoute, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnZoomToWp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnDelete, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnActivate, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        btnPanel.add(btnClose, new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, EAST, NONE, insets5, 0, 0));
    }
    
    
    /**
     * Initializes the table model used for route details
     * 
     * @return the table model
     */
    private DefaultTableModel createRouteTableModel() {
        return new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return route.getWaypoints().size();
            }

            @Override
            public int getColumnCount() {
                return COL_NAMES.length;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return COL_NAMES[columnIndex];
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
                case 15: return Formatter.formatTime(wp.getOutLeg().getSFLenInMilleseconds());
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
                        checkLockedRows();
                        fireTableRowsUpdated(rowIndex, rowIndex); 
                        break;
                    case  1: 
                        wp.setName(value.toString()); 
                        break;
                    case  2: 
                        wp.setPos(Position.create(ParseUtils.parseLatitude(value.toString()), wp.getPos().getLongitude()));
                        adjustStartTime();
                        notifyRouteListeners(RoutesUpdateEvent.ROUTE_CHANGED);
                        break;
                    case  3: 
                        wp.setPos(Position.create(wp.getPos().getLatitude(), ParseUtils.parseLongitude(value.toString()))); 
                        adjustStartTime();
                        notifyRouteListeners(RoutesUpdateEvent.ROUTE_CHANGED);
                        break;
                    case  4: 
                        wp.setTurnRad(parseDouble(value.toString())); 
                        break;
                    case 10: 
                        wp.getOutLeg().setHeading((Heading)value); 
                        adjustStartTime();
                        notifyRouteListeners(RoutesUpdateEvent.ROUTE_CHANGED);
                        break;
                    case 11: 
                        wp.getOutLeg().setSpeed(parseDouble(value.toString())); 
                        adjustStartTime(); 
                        break;
                    case 12: 
                        wp.getOutLeg().setXtdStarboard(parseDouble(value.toString()) / 1852.0);
                        break;
                    case 13: 
                        wp.getOutLeg().setXtdPort(parseDouble(value.toString()) / 1852.0);
                        break;
                    case 14: 
                        wp.getOutLeg().setSFWidth(parseDouble(value.toString()));
                        break;
                    case 15: 
                        wp.getOutLeg().setSFLenInMilliseconds(parseTime(value.toString()));
                        break;
                    default:
                    }
                    routeUpdated();
                } catch (Exception ex) {
                    LOG.warn(String.format(
                            "Failed updating field '%s' in row %d: %s", COL_NAMES[columnIndex], rowIndex, ex.getMessage()));
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return !readOnlyRoute &&
                        (columnIndex == 0 || !locked[rowIndex]) &&
                        (columnIndex < 5 || columnIndex > 9) &&
                        !(columnIndex == 4 && rowIndex == 0);
            }   
        };
    }
    
    /**
     * Configures the given date picker and associated time spinner
     * @param picker the date picker
     * @param spinner the time spinner
     */
    private void initDatePicker(JXDatePicker picker, JSpinner spinner) {
        picker.setFormats(new SimpleDateFormat("E dd/MM/yyyy"));
        picker.addPropertyChangeListener("date", this);
        DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        ((DefaultFormatter)editor.getTextField().getFormatter()).setCommitsOnValidEdit(true);
        spinner.setEditor(editor);
        spinner.addChangeListener(new SpinnerChangeListener());
        
        // Set the enabled state
        picker.setEnabled(!readOnlyRoute);
        spinner.setEnabled(!readOnlyRoute);
    }
    
    /**
     * Updates the dialog with the value of the current route
     */
    private void initValues() {
        
        // Should not trigger listeners
        quiescent = true;
        
        nameTxT.setText(route.getName());
        originTxT.setText(route.getDeparture());
        destinationTxT.setText(route.getDestination());
        
        etaCalculationTime.setSelectedItem(route.getEtaCalculationType());        

        // Update the route start time and the start time-related fields 
        adjustStartTime();
        
        updateButtonEnabledState();
        
        // Done
        quiescent = false;
    }
    
    /** 
     * Updates the enabled state of the buttons
     */
    private void updateButtonEnabledState() {
        boolean wpSelected = selectedWp >= 0;
        btnActivate.setEnabled(wpSelected && readOnlyRoute);
        btnDelete.setEnabled(wpSelected && !readOnlyRoute);
        btnZoomToWp.setEnabled(wpSelected && chartPanel != null);
        btnZoomToRoute.setEnabled(chartPanel != null);
        
        boolean allRowsLocked = checkLockedRows();
        arrivalPicker.setEnabled(!readOnlyRoute && !allRowsLocked);
        arrivalSpinner.setEnabled(!readOnlyRoute && !allRowsLocked);
    }
    
    /***************************************************/
    /** UI listener events                            **/
    /***************************************************/
    
    /**
     * Called when the table selection changes
     * @param evt the event
     */
    @Override 
    public void valueChanged(ListSelectionEvent evt) {
        // Check if we are in a quiescent state
        if (quiescent) {
            return;
        }
        
        if (!evt.getValueIsAdjusting()) {
            selectedWp = routeDetailTable.getSelectedRow();
            updateButtonEnabledState();
        }
    }
    
    /**
     * Handle action events
     * @param evt the action event
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        // Check if we are in a quiescent state
        if (quiescent) {
            return;
        }
        
        if (evt.getSource() == btnZoomToRoute && chartPanel != null) {
            chartPanel.zoomToWaypoints(route.getWaypoints());
        
        } else if (evt.getSource() == btnZoomToWp && chartPanel != null) {
            chartPanel.goToPosition(route.getWaypoints().get(selectedWp).getPos());
            
        } else if (evt.getSource() == btnDelete) {
            onDelete();
            routeUpdated();
        
        } else if (evt.getSource() == btnActivate) {
            routeManager.changeActiveWp(selectedWp);
            routeUpdated();
        
        } else if (evt.getSource() == btnClose) {
            dispose();
        
        } else if (evt.getSource() == etaCalculationTime) {
            route.setEtaCalculationType((EtaCalculationType)etaCalculationTime.getSelectedItem());
            adjustStartTime();
            routeUpdated();
        }        
    }

    /** 
     * Called when one of the arrival and departure pickers changes value
     * @param evt the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Check if we are in a quiescent state
        if (quiescent) {
            return;
        }
        
        if (evt.getSource() == departurePicker) {
            Date date = combineDateTime(departurePicker.getDate(), (Date)departureSpinner.getValue());
            route.setStarttime(date);
            adjustStartTime();
            
        } else if (evt.getSource() == arrivalPicker) {
            Date date = combineDateTime(arrivalPicker.getDate(), (Date)arrivalSpinner.getValue());
            recalculateSpeeds(date);
        }
        
        routeUpdated();
    }
    
    /** 
     * Called when one of the arrival and departure spinners changes value
     * @param spinner the spinner
     */
    protected void spinnerValueChanged(JSpinner spinner) {
        // Check if we are in a quiescent state
        if (quiescent) {
            return;
        }
        
        if (spinner == departureSpinner) {
            Date date = combineDateTime(departurePicker.getDate(), (Date)departureSpinner.getValue());
            route.setStarttime(date);
            adjustStartTime();
            
        } else if (spinner == arrivalSpinner) {            
            Date date = combineDateTime(arrivalPicker.getDate(), (Date)arrivalSpinner.getValue());
            recalculateSpeeds(date);
        }
        
        routeUpdated();
    }
    
    /**
     * Called when one of the name, origin or destination
     * text field changes value
     * @param field the text field
     */
    protected void textFieldValueChanged(JTextField field) {
        // Check if we are in a quiescent state
        if (quiescent) {
            return;
        }
        
        if (field == nameTxT) {
            route.setName(nameTxT.getText());
        } else if (field == originTxT) {
            route.setDeparture(originTxT.getText());
        } else if (field == destinationTxT) {
            route.setDestination(destinationTxT.getText());
        }
        
        routeUpdated();
    }
    
    /***************************************************/
    /** Model update functions                        **/
    /***************************************************/
    
    /**
     * Adds a listener for route updates
     * @param listener the lister to add
     */
    public void addRouteChangeListener(RouteChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for route updates
     * @param listener the lister to remove
     */
    public void removeRouteChangeListener(RouteChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Sub-classes can override this to be notified 
     * whenever the route has been updated in the
     * route properties dialog
     */
    protected void routeUpdated() {
        if (!readOnlyRoute) {
            for (RouteChangeListener listener : listeners) {
                listener.routeChanged();
            }
        }
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
                if (routeManager != null) {
                    routeManager.removeRoute(routeManager.getRouteIndex(route));
                    routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
                }
                dispose();
            }
        }

        else {
            // Update the locked list
            boolean[] newLocked = new boolean[locked.length - 1];
            for (int x = 0; x < newLocked.length; x++) {
                newLocked[x] = locked[x + (x >= selectedWp ? 1 : 0)];
            }
            locked = newLocked;
            
            // Delete the selected way point
            route.deleteWaypoint(selectedWp);
            adjustStartTime();
            routeTableModel.fireTableDataChanged();
            if (routeManager != null) {
                routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
            }
        }
    }

    /**
     * Notifies route listeners that the route has been updated
     * @param event the event to signal
     */
    private void notifyRouteListeners(RoutesUpdateEvent event) {
        if (routeManager != null) {
            routeManager.notifyListeners(event);
        }
    }
    
    /**
     * Called in order to adjust the route start time and the UI accordingly
     */
    private void adjustStartTime() {
        
        // Stop widget listeners
        boolean wasQuiescent = quiescent;
        quiescent = true;

        // Get start time or default now
        if (!readOnlyRoute) {
            route.adjustStartTime();
        }
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
        
        // Recalculate and update route fields
        updateFields();
        
        // Restore the quiescent state
        quiescent = wasQuiescent;
    }
    
    /**
     * Called when route values changes and the fields should be refreshed
     */
    private void updateFields() {
        if (!readOnlyRoute) {
            route.calcValues(true);
            route.calcAllWpEta();
        }
        inrouteTxT.setText(Formatter.formatTime(route.getRouteTtg()));
        distanceTxT.setText(Formatter.formatDistNM(route.getRouteDtg()));
        routeTableModel.fireTableDataChanged();
    }

    /**
     * Given a new arrival date, re-calculate the speed
     * @param arrivalDate the new arrival date
     */
    private void recalculateSpeeds(Date arrivalDate) {
        // Stop widget listeners
        boolean wasQuiescent = quiescent;
        quiescent = true;

        // Special case if the arrival date is before the start time
        if (route.getStarttime().after(arrivalDate)) {
            // Reset arrival to a valid time
            arrivalPicker.setDate(route.getEta());
            arrivalSpinner.setValue(route.getEta());
            
            quiescent = wasQuiescent;
            return;
        }

        // Total distance
        Dist distanceToTravel = new Dist(DistType.NAUTICAL_MILES, route.getRouteDtg());
        // And we want to get there in milliseconds:
        Time timeToTravel = new Time(TimeType.MILLISECONDS, arrivalDate.getTime() - route.getStarttime().getTime());
        
        // Subtract the distance and time from the locked way points
        for (int i = 0; i < route.getWaypoints().size() - 1; i++) {
            if (locked[i]) {
                distanceToTravel = distanceToTravel
                        .subtract(new Dist(DistType.NAUTICAL_MILES, route.getWpRng(i)));
                timeToTravel = timeToTravel
                        .subtract(new Time(TimeType.MILLISECONDS, route.getWpTtg(i + 1)));
            }
        }
        
        // Ensure the remaining time is actually positive (say, more than a minute)
        if (timeToTravel.in(TimeType.MINUTES).doubleValue() < 1.0) {
            // Reset arrival to a valid time
            arrivalPicker.setDate(route.getEta());
            arrivalSpinner.setValue(route.getEta());
            
            quiescent = wasQuiescent;
            return;
        }

        // So we need to travel how fast?
        double speed = distanceToTravel.inTime(timeToTravel).in(SpeedType.KNOTS).doubleValue();

        for (int i = 0; i < route.getWaypoints().size(); i++) {
            if (!locked[i]) {
                route.getWaypoints().get(i).setSpeed(speed);
            }
        }
        
        // Update fields
        updateFields();
        
        // Restore the quiescent state
        quiescent = wasQuiescent;
    }
    
    /**
     * Checks the locked rows. 
     * If all rows except the last one are locked, also lock the last row
     * @return if all rows are locked
     */
    private boolean checkLockedRows() {
        int lockedNo = 0;
        for (int i = 0; i < locked.length; i++) {
            if (locked[i]) {
                lockedNo++;
            }
        }
        if (lockedNo == locked.length - 1 && !locked[locked.length - 1]) {
            locked[locked.length - 1] = true;
            lockedNo++;
            routeTableModel.fireTableRowsUpdated(locked.length - 1, locked.length - 1);
        }
        return lockedNo == locked.length;
    }
    
    /***************************************************/
    /** Utility functions                             **/
    /***************************************************/
    
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
     * Parses the text field as a double. Will skip any type suffix.
     * @param str the string to parse as a double
     * @return the resulting value
     */
    private static double parseDouble(String str) throws FormatException {
        str = str.replaceAll(",", ".");
        String[] parts = StringUtils.split(str, " ");
        return ParseUtils.parseDouble(parts[0]);
    }
    
    /**
     * Parses the text, which has the time format hh:mm:ss, into milliseconds.
     * @param str the string to parse
     * @return the time in milliseconds
     */
    private static long parseTime(String str)  throws Exception {
        String[] parts = str.split(":");
        return new Time(TimeType.HOURS, Long.valueOf(parts[0]))
          .add(new Time(TimeType.MINUTES, Long.valueOf(parts[1])))
          .add(new Time(TimeType.SECONDS, Long.valueOf(parts[2])))
          .in(TimeType.MILLISECONDS).longValue();
    }
    
    /***************************************************/
    /** Test method                                   **/
    /***************************************************/
    
    /**
     * Test method
     */
    public static final void main(String... args) throws Exception {

        //=====================
        // Create test data
        //=====================
        final Route route = new Route();
        route.setName("Test route");
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
        RoutePropertiesDialogCommon dialog = new RoutePropertiesDialogCommon(null, null, route, false);
        dialog.setVisible(true);
    }   

    /***************************************************/
    /** Helper classes                                **/
    /***************************************************/

    /**
     * Sadly, the change listener fires twice when you click
     * the spinner buttons. This class will only call {@linkplain #spinnerValueChanged()}
     * when the value has actually changed
     */
    class SpinnerChangeListener implements ChangeListener {
        Object oldValue;
        
        @Override
        public void stateChanged(ChangeEvent e) {
            Object newValue = ((JSpinner)e.getSource()).getValue();
            if (newValue != null && !newValue.equals(oldValue)) {
                spinnerValueChanged((JSpinner)e.getSource());
            }
            oldValue = newValue;
        }
    }
    
    /**
     * Can be attached to the document of a text field and will call
     *  {@linkplain #textFieldValueChanged()} when the value changes
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


    /**
     * Interface to be implemented by clients wishing 
     * to be notified about updates to the route
     */
    public interface RouteChangeListener {
        
        /**
         * Signal that the route has changed
         */
        void routeChanged();

    }
}
