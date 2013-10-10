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
package dk.dma.epd.ship.gui.voct;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.util.ParseUtils;

public class DatumLineInputPanel extends JPanel implements ActionListener,
        DocumentListener {

    private static final long serialVersionUID = 1L;

    private JTextField dsp1FirstLat;

    private JTextField xErrorField;
    private JTextField yErrorField;
    private JTextField safetyFactorField;

    private SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");

    // Initialize with currentDate on CET
    private DateTimeZone timeZone = DateTimeZone.forID("CET");
    private DateTime LKPDate = new DateTime(timeZone);
    private DateTime DSP2Date = new DateTime(timeZone);
    private DateTime DSP3Date = new DateTime(timeZone);

    private DateTime CSSDate = new DateTime(timeZone);

    private JTextField dsp1SecondLat;
    private JTextField dsp1ThirdLat;
    private JTextField dsp1FirstLon;
    private JTextField dsp1SecondLon;
    private JTextField dsp1ThirdLon;
    
    private JComboBox<String> comboDsp1Lat;
    private JComboBox<String> comboDsp2Lat;
    private JComboBox<String> comboDsp3Lat;
    
    
    private JComboBox<String> comboDsp1Lon;
    private JComboBox<String> comboDsp2Lon;
    private JComboBox<String> comboDsp3Lon;

    private int surfaceDriftPanelHeight = 50;
    private int metocPoints;
    private JPanel surfaceDriftPanelContainer;
    private JButton btnAddPoint;
    private JScrollPane scrollPaneSurfaceDrift;
    private JComboBox<String> searchObjectDropDown;
    private JLabel searchObjectText;

    private JXDatePicker cssDatePicker;

    private JXDatePicker dsp1DatePicker;
    private JSpinner dsp1Spinner;

    private JXDatePicker dsp2DatePicker;
    private JSpinner dsp2Spinner;

    private JXDatePicker dsp3DatePicker;
    private JSpinner dsp3Spinner;

    private JSpinner commenceStartSpinner;

    private JComboBox<String> timeZoneDropdown;

    private List<SurfaceDriftPanel> surfaceDriftPanelList = new ArrayList<SurfaceDriftPanel>();
    private JButton btnNewButton;


    private JTextField sarIDTxtField;
    private JPanel topPanel;
    private JTextField dsp2FirstLat;
    private JTextField dsp2SecondLat;
    private JTextField dsp2ThirdLat;
    private JTextField dsp2FirstLon;
    private JTextField dsp2SecondLon;
    private JTextField dsp2ThirdLon;
    private JTextField dsp3FirstLat;
    private JTextField dsp3SecondLat;
    private JTextField dsp3ThirdLat;
    private JTextField dsp3FirstLon;
    private JTextField dsp3SecondLon;
    private JTextField dsp3ThirdLon;

    public DatumLineInputPanel() {

        format.setTimeZone(TimeZone.getTimeZone("CET"));

        CSSDate = CSSDate.plusHours(2);

        DSP2Date = DSP2Date.plusHours(1);
        
        DSP3Date = DSP3Date.plusHours(2);
        
        System.out.println(LKPDate.toDate());
        System.out.println(CSSDate.toDate());

        setPreferredSize(new Dimension(500, 902));
        // setPreferredSize(new Dimension(500, 600));

        setBorder(new EmptyBorder(5, 5, 5, 5));
        // getContentPane().add(inputPanel, BorderLayout.CENTER);
        setLayout(null);

        JPanel dsp1Panel = new JPanel();
        dsp1Panel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"),
                "First Drift Search Position", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        dsp1Panel.setBounds(20, 59, 494, 87);
        add(dsp1Panel);
        dsp1Panel.setLayout(null);

        dsp1DatePicker = new JXDatePicker();
        dsp1DatePicker.setBounds(170, 22, 105, 20);
        dsp1Panel.add(dsp1DatePicker);
        dsp1DatePicker.setDate(LKPDate.toDate());
        dsp1DatePicker.addActionListener(this);

        dsp1DatePicker.setFormats(format);

        JLabel lblTimeOfLast = new JLabel("Time of DSP 1:");
        lblTimeOfLast.setBounds(13, 25, 147, 14);
        dsp1Panel.add(lblTimeOfLast);

        SpinnerDateModel dsp1TimeModel = new SpinnerDateModel(LKPDate.toDate(),
                null, null, Calendar.HOUR_OF_DAY);

        dsp1Spinner = new JSpinner(dsp1TimeModel);

        dsp1Spinner.setLocation(278, 22);
        dsp1Spinner.setSize(54, 20);
        JSpinner.DateEditor de_dsp1Spinner = new JSpinner.DateEditor(
                dsp1Spinner, "HH:mm");
        dsp1Spinner.setEditor(de_dsp1Spinner);

        dsp1Panel.add(dsp1Spinner);

        ((DefaultEditor) dsp1Spinner.getEditor()).getTextField().getDocument()
                .addDocumentListener(this);
        ((DefaultEditor) dsp1Spinner.getEditor()).getTextField().getDocument()
                .putProperty("name", "dsp1Spinner");

        JLabel lblLastKnownPosition = new JLabel("DSP 1:");
        lblLastKnownPosition.setBounds(13, 50, 147, 14);
        dsp1Panel.add(lblLastKnownPosition);

        dsp1FirstLat = new JTextField();
        dsp1FirstLat.setHorizontalAlignment(SwingConstants.RIGHT);

        dsp1FirstLat.setText("");
        dsp1FirstLat.setBounds(170, 47, 30, 20);
        dsp1Panel.add(dsp1FirstLat);
        dsp1FirstLat.setColumns(10);

        timeZoneDropdown = new JComboBox<String>();
        timeZoneDropdown.setModel(new DefaultComboBoxModel<String>(
                new String[] { "CET", "UTC", "GMT" }));
        timeZoneDropdown.setBounds(342, 22, 46, 20);
        dsp1Panel.add(timeZoneDropdown);
        timeZoneDropdown.addActionListener(this);

        dsp1SecondLat = new JTextField();
        dsp1SecondLat.setText("");
        dsp1SecondLat.setColumns(10);
        dsp1SecondLat.setBounds(200, 47, 20, 20);
        dsp1Panel.add(dsp1SecondLat);

        dsp1ThirdLat = new JTextField();
        dsp1ThirdLat.setText("");
        dsp1ThirdLat.setColumns(10);
        dsp1ThirdLat.setBounds(220, 47, 30, 20);
        dsp1Panel.add(dsp1ThirdLat);

        comboDsp1Lat = new JComboBox<String>();
        comboDsp1Lat.setModel(new DefaultComboBoxModel<String>(new String[] {
                "N", "S" }));
        comboDsp1Lat.setBounds(250, 47, 37, 20);
        dsp1Panel.add(comboDsp1Lat);

        dsp1FirstLon = new JTextField();
        dsp1FirstLon.setHorizontalAlignment(SwingConstants.RIGHT);
        dsp1FirstLon.setText("");
        dsp1FirstLon.setColumns(10);
        dsp1FirstLon.setBounds(290, 47, 30, 20);
        dsp1Panel.add(dsp1FirstLon);

        dsp1SecondLon = new JTextField();
        dsp1SecondLon.setText("");
        dsp1SecondLon.setColumns(10);
        dsp1SecondLon.setBounds(320, 47, 20, 20);
        dsp1Panel.add(dsp1SecondLon);

        comboDsp1Lon = new JComboBox<String>();
        comboDsp1Lon.setModel(new DefaultComboBoxModel<String>(new String[] {
                "E", "W" }));
        comboDsp1Lon.setBounds(370, 47, 37, 20);
        dsp1Panel.add(comboDsp1Lon);

        dsp1ThirdLon = new JTextField();
        dsp1ThirdLon.setText("");
        dsp1ThirdLon.setColumns(10);
        dsp1ThirdLon.setBounds(340, 47, 30, 20);
        dsp1Panel.add(dsp1ThirdLon);

        JPanel dsp2Panel = new JPanel();
        dsp2Panel.setLayout(null);
        dsp2Panel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"),
                "Second Drift Search Position", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        dsp2Panel.setBounds(20, 157, 494, 87);
        add(dsp2Panel);

        dsp2DatePicker = new JXDatePicker();
        dsp2DatePicker.setDate(DSP2Date.toDate());
        dsp2DatePicker.setBounds(170, 22, 105, 20);
        dsp2Panel.add(dsp2DatePicker);

        JLabel lblTimeOfDsp = new JLabel("Time of DSP 2:");
        lblTimeOfDsp.setBounds(13, 25, 147, 14);
        dsp2Panel.add(lblTimeOfDsp);

        SpinnerDateModel dsp2TimeModel = new SpinnerDateModel(
                DSP2Date.toDate(), null, null, Calendar.HOUR_OF_DAY);

        dsp2Spinner = new JSpinner(dsp2TimeModel);
        dsp2Spinner.setBounds(278, 22, 54, 20);
        dsp2Panel.add(dsp2Spinner);

        JSpinner.DateEditor de_dsp2Spinner = new JSpinner.DateEditor(
                dsp2Spinner, "HH:mm");
        dsp2Spinner.setEditor(de_dsp2Spinner);

        ((DefaultEditor) dsp2Spinner.getEditor()).getTextField().getDocument()
                .addDocumentListener(this);
        ((DefaultEditor) dsp2Spinner.getEditor()).getTextField().getDocument()
                .putProperty("name", "dsp2Spinner");

        JLabel lblDsp = new JLabel("DSP 2:");
        lblDsp.setBounds(13, 50, 147, 14);
        dsp2Panel.add(lblDsp);

        dsp2FirstLat = new JTextField();
        dsp2FirstLat.setText("");
        dsp2FirstLat.setHorizontalAlignment(SwingConstants.RIGHT);
        dsp2FirstLat.setColumns(10);
        dsp2FirstLat.setBounds(170, 47, 30, 20);
        dsp2Panel.add(dsp2FirstLat);

        dsp2SecondLat = new JTextField();
        dsp2SecondLat.setText("");
        dsp2SecondLat.setColumns(10);
        dsp2SecondLat.setBounds(200, 47, 20, 20);
        dsp2Panel.add(dsp2SecondLat);

        dsp2ThirdLat = new JTextField();
        dsp2ThirdLat.setText("");
        dsp2ThirdLat.setColumns(10);
        dsp2ThirdLat.setBounds(220, 47, 30, 20);
        dsp2Panel.add(dsp2ThirdLat);

        comboDsp2Lat = new JComboBox<String>();
        comboDsp2Lat.setModel(new DefaultComboBoxModel<String>(new String[] {
                "N", "S" }));
        comboDsp2Lat.setBounds(250, 47, 37, 20);
        dsp2Panel.add(comboDsp2Lat);

        dsp2FirstLon = new JTextField();
        dsp2FirstLon.setText("");
        dsp2FirstLon.setHorizontalAlignment(SwingConstants.RIGHT);
        dsp2FirstLon.setColumns(10);
        dsp2FirstLon.setBounds(290, 47, 30, 20);
        dsp2Panel.add(dsp2FirstLon);

        dsp2SecondLon = new JTextField();
        dsp2SecondLon.setText("");
        dsp2SecondLon.setColumns(10);
        dsp2SecondLon.setBounds(320, 47, 20, 20);
        dsp2Panel.add(dsp2SecondLon);

        comboDsp2Lon = new JComboBox<String>();
        comboDsp2Lon.setModel(new DefaultComboBoxModel<String>(new String[] {
                "E", "W" }));
        comboDsp2Lon.setBounds(370, 47, 37, 20);
        dsp2Panel.add(comboDsp2Lon);

        dsp2ThirdLon = new JTextField();
        dsp2ThirdLon.setText("");
        dsp2ThirdLon.setColumns(10);
        dsp2ThirdLon.setBounds(340, 47, 30, 20);
        dsp2Panel.add(dsp2ThirdLon);

        JPanel dsp3Panel = new JPanel();
        dsp3Panel.setLayout(null);
        dsp3Panel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"),
                "Third Drift Search Position", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        dsp3Panel.setBounds(20, 255, 494, 87);
        add(dsp3Panel);

        dsp3DatePicker = new JXDatePicker();
        dsp3DatePicker.setDate(DSP3Date.toDate());
        dsp3DatePicker.setBounds(170, 22, 105, 20);
        dsp3Panel.add(dsp3DatePicker);

        JLabel lblTimeOfDsp_1 = new JLabel("Time of DSP3:");
        lblTimeOfDsp_1.setBounds(13, 25, 147, 14);
        dsp3Panel.add(lblTimeOfDsp_1);

        SpinnerDateModel dsp3TimeModel = new SpinnerDateModel(
                DSP3Date.toDate(), null, null, Calendar.HOUR_OF_DAY);

        dsp3Spinner = new JSpinner(dsp3TimeModel);

        dsp3Spinner.setBounds(278, 22, 54, 20);
        dsp3Panel.add(dsp3Spinner);

        JSpinner.DateEditor de_dsp3Spinner = new JSpinner.DateEditor(
                dsp3Spinner, "HH:mm");
        dsp3Spinner.setEditor(de_dsp3Spinner);

        ((DefaultEditor) dsp3Spinner.getEditor()).getTextField().getDocument()
                .addDocumentListener(this);
        ((DefaultEditor) dsp3Spinner.getEditor()).getTextField().getDocument()
                .putProperty("name", "dsp3Spinner");

        JLabel lblDsp_1 = new JLabel("DSP 3:");
        lblDsp_1.setBounds(13, 50, 147, 14);
        dsp3Panel.add(lblDsp_1);

        dsp3FirstLat = new JTextField();
        dsp3FirstLat.setText("");
        dsp3FirstLat.setHorizontalAlignment(SwingConstants.RIGHT);
        dsp3FirstLat.setColumns(10);
        dsp3FirstLat.setBounds(170, 47, 30, 20);
        dsp3Panel.add(dsp3FirstLat);

        dsp3SecondLat = new JTextField();
        dsp3SecondLat.setText("");
        dsp3SecondLat.setColumns(10);
        dsp3SecondLat.setBounds(200, 47, 20, 20);
        dsp3Panel.add(dsp3SecondLat);

        dsp3ThirdLat = new JTextField();
        dsp3ThirdLat.setText("");
        dsp3ThirdLat.setColumns(10);
        dsp3ThirdLat.setBounds(220, 47, 30, 20);
        dsp3Panel.add(dsp3ThirdLat);

        comboDsp3Lat = new JComboBox<String>();
        comboDsp3Lat.setModel(new DefaultComboBoxModel<String>(new String[] {
                "N", "S" }));
        comboDsp3Lat.setBounds(250, 47, 37, 20);
        dsp3Panel.add(comboDsp3Lat);

        dsp3FirstLon = new JTextField();
        dsp3FirstLon.setText("");
        dsp3FirstLon.setHorizontalAlignment(SwingConstants.RIGHT);
        dsp3FirstLon.setColumns(10);
        dsp3FirstLon.setBounds(290, 47, 30, 20);
        dsp3Panel.add(dsp3FirstLon);

        dsp3SecondLon = new JTextField();
        dsp3SecondLon.setText("");
        dsp3SecondLon.setColumns(10);
        dsp3SecondLon.setBounds(320, 47, 20, 20);
        dsp3Panel.add(dsp3SecondLon);

        comboDsp3Lon = new JComboBox<String>();
        comboDsp3Lon.setModel(new DefaultComboBoxModel<String>(new String[] {
                "E", "W" }));
        comboDsp3Lon.setBounds(370, 47, 37, 20);
        dsp3Panel.add(comboDsp3Lon);

        dsp3ThirdLon = new JTextField();
        dsp3ThirdLon.setText("");
        dsp3ThirdLon.setColumns(10);
        dsp3ThirdLon.setBounds(340, 47, 30, 20);
        dsp3Panel.add(dsp3ThirdLon);

        JPanel commenceStartPanel = new JPanel();
        commenceStartPanel.setBorder(new TitledBorder(null,
                "Commence Search Start", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        commenceStartPanel.setBounds(20, 353, 494, 53);
        commenceStartPanel.setLayout(null);
        add(commenceStartPanel);

        cssDatePicker = new JXDatePicker();
        cssDatePicker.setBounds(170, 22, 105, 20);
        cssDatePicker.addActionListener(this);
        commenceStartPanel.add(cssDatePicker);

        cssDatePicker.setFormats(format);
        cssDatePicker.setDate(CSSDate.toDate());

        JLabel lblCommenceStartSearch = new JLabel("Time of Search Start:");
        lblCommenceStartSearch.setBounds(13, 25, 147, 14);
        commenceStartPanel.add(lblCommenceStartSearch);

        SpinnerDateModel CSSTimeModel = new SpinnerDateModel(CSSDate.toDate(),
                null, null, Calendar.HOUR_OF_DAY);

        commenceStartSpinner = new JSpinner(CSSTimeModel);

        commenceStartSpinner.setLocation(278, 22);
        commenceStartSpinner.setSize(54, 20);
        JSpinner.DateEditor dateEditorCommenceSearchStart = new JSpinner.DateEditor(
                commenceStartSpinner, "HH:mm");
        commenceStartSpinner.setEditor(dateEditorCommenceSearchStart);

        ((DefaultEditor) commenceStartSpinner.getEditor()).getTextField()
                .getDocument().addDocumentListener(this);
        ((DefaultEditor) commenceStartSpinner.getEditor()).getTextField()
                .getDocument().putProperty("name", "commenceStartSpinner");

        commenceStartPanel.add(commenceStartSpinner);

        surfaceDriftPanelContainer = new JPanel();
        scrollPaneSurfaceDrift = new JScrollPane(surfaceDriftPanelContainer);
        scrollPaneSurfaceDrift
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        surfaceDriftPanelContainer.setBorder(new TitledBorder(null,
                "Surface Drift", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));

        // surfaceDriftPanel.setBounds(20, 195, 494, 9000);
        // surfaceDriftPanel.setSize(494, 9000)

        // Add 1 to start, 110 + 56

        surfaceDriftPanelContainer.setPreferredSize(new Dimension(494,
                surfaceDriftPanelHeight));

        // surfaceDriftPanelHeight

        scrollPaneSurfaceDrift.setBounds(20, 417, 494, 254);

        add(scrollPaneSurfaceDrift);

        surfaceDriftPanelContainer.setLayout(null);

        surfaceDriftPanelContainer.add(addPoint(metocPoints));

        JButton btnFetchMetocData = new JButton("Fetch METOC Data");
        btnFetchMetocData.setEnabled(false);
        btnFetchMetocData.setBounds(10, 22, 123, 23);
        surfaceDriftPanelContainer.add(btnFetchMetocData);

        btnAddPoint = new JButton("Add point");
        btnAddPoint.setEnabled(false);
        btnAddPoint.setBounds(265, 22, 89, 23);
        surfaceDriftPanelContainer.add(btnAddPoint);

        btnNewButton = new JButton("Remove Last");
        btnNewButton.setEnabled(false);
        btnNewButton.setBounds(364, 22, 120, 23);
        surfaceDriftPanelContainer.add(btnNewButton);
        btnAddPoint.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Other variables",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(20, 682, 487, 100);
        add(panel);
        panel.setLayout(null);

        JLabel lblInitialPositionError = new JLabel(
                "Initial Position Error (X), nm:");
        lblInitialPositionError.setBounds(13, 25, 164, 14);
        panel.add(lblInitialPositionError);

        xErrorField = new JTextField();
        xErrorField.setText("");
        xErrorField.setBounds(184, 22, 86, 20);
        panel.add(xErrorField);
        xErrorField.setColumns(10);

        JLabel lblSruNavigationalError = new JLabel(
                "SRU Navigational Error (Y), nm:");
        lblSruNavigationalError.setBounds(13, 50, 164, 14);
        panel.add(lblSruNavigationalError);

        yErrorField = new JTextField();
        yErrorField.setText("0.1");
        yErrorField.setBounds(184, 47, 86, 20);
        panel.add(yErrorField);
        yErrorField.setColumns(10);

        JLabel lblNoteGps = new JLabel("Note: GPS = 0.1 nm");
        lblNoteGps.setBounds(280, 50, 122, 14);
        panel.add(lblNoteGps);

        JLabel lblSafetyFactorFs = new JLabel("Safety Factor, Fs:");
        lblSafetyFactorFs.setBounds(13, 75, 147, 14);
        panel.add(lblSafetyFactorFs);

        safetyFactorField = new JTextField();
        safetyFactorField.setText("1.0");
        safetyFactorField.setBounds(184, 72, 86, 20);
        panel.add(safetyFactorField);
        safetyFactorField.setColumns(10);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Search Object",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(20, 793, 487, 87);
        add(panel_1);
        panel_1.setLayout(null);

        searchObjectDropDown = new JComboBox<String>();
        searchObjectDropDown.setModel(new DefaultComboBoxModel<String>());
        searchObjectDropDown.setBounds(10, 22, 457, 20);
        panel_1.add(searchObjectDropDown);
        searchObjectDropDown.addActionListener(this);

        searchObjectText = new JLabel();
        searchObjectText.setText(LeewayValues.getLeeWayContent().get(0));
        searchObjectText.setBounds(10, 53, 457, 14);
        panel_1.add(searchObjectText);

        topPanel = new JPanel();
        topPanel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"), "Datum Line Operation",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        topPanel.setBounds(20, 0, 487, 48);
        add(topPanel);
        topPanel.setLayout(null);

        JLabel lblSarId = new JLabel("SAR No.");
        lblSarId.setBounds(10, 23, 55, 14);
        topPanel.add(lblSarId);

        sarIDTxtField = new JTextField("");
        sarIDTxtField.setBounds(58, 20, 136, 20);
        topPanel.add(sarIDTxtField);
        sarIDTxtField.setColumns(10);

        for (int i = 0; i < LeewayValues.getLeeWayTypes().size(); i++) {
            searchObjectDropDown.addItem(LeewayValues.getLeeWayTypes().get(i));

        }

        initSetValues();

    }

    private void initSetValues() {
        dsp1FirstLat.setText("56");
        dsp1SecondLat.setText("25");
        dsp1ThirdLat.setText("671");
        dsp1FirstLon.setText("11");
        dsp1SecondLon.setText("21");
        dsp1ThirdLon.setText("066");

        
        dsp2FirstLat.setText("56");
        dsp2SecondLat.setText("24");
        dsp2ThirdLat.setText("038");
        dsp2FirstLon.setText("11");
        dsp2SecondLon.setText("33");
        dsp2ThirdLon.setText("980");
        
        
        dsp3FirstLat.setText("56");
        dsp3SecondLat.setText("18");
        dsp3ThirdLat.setText("212");
        dsp3FirstLon.setText("11");
        dsp3SecondLon.setText("41");
        dsp3ThirdLon.setText("913");
        
        
        
        xErrorField.setText("1.0");
    }

    private JPanel addPoint(int number) {

        SurfaceDriftPanel surfaceDriftPanel = new SurfaceDriftPanel(number);

        surfaceDriftPanelHeight = surfaceDriftPanelHeight + 110;

        surfaceDriftPanelContainer.setPreferredSize(new Dimension(494,
                surfaceDriftPanelHeight));

        metocPoints++;

        surfaceDriftPanelList.add(surfaceDriftPanel);

        return surfaceDriftPanel;
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        String name = (String) arg0.getDocument().getProperty("name");

        // DSP1
        if ("dsp1Spinner".equals(name)) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dsp1Spinner
                    .getEditor();
            // System.out.println("DepartureTime was changed to "
            // + departureSpinner.getValue());
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());

                Calendar cal = new GregorianCalendar();
                cal.setTime(testDate);

                LKPDate = LKPDate.withHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
                LKPDate = LKPDate.withMinuteOfHour(cal.get(Calendar.MINUTE));
            } catch (ParseException e1) {
                // Ignore
            }

        }

        // DSP1
        if ("dsp2Spinner".equals(name)) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dsp2Spinner
                    .getEditor();
            // System.out.println("DepartureTime was changed to "
            // + departureSpinner.getValue());
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());

                Calendar cal = new GregorianCalendar();
                cal.setTime(testDate);

                DSP2Date = DSP2Date
                        .withHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
                DSP2Date = DSP2Date.withMinuteOfHour(cal.get(Calendar.MINUTE));
            } catch (ParseException e1) {
                // Ignore
            }

        }

        // DSP1
        if ("dsp3Spinner".equals(name)) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) dsp3Spinner
                    .getEditor();
            // System.out.println("DepartureTime was changed to "
            // + departureSpinner.getValue());
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());

                Calendar cal = new GregorianCalendar();
                cal.setTime(testDate);

                DSP3Date = DSP2Date
                        .withHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
                DSP3Date = DSP2Date.withMinuteOfHour(cal.get(Calendar.MINUTE));
            } catch (ParseException e1) {
                // Ignore
            }

        }

        // Departure
        if ("commenceStartSpinner".equals(name)) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) commenceStartSpinner
                    .getEditor();
            // System.out.println("DepartureTime was changed to "
            // + departureSpinner.getValue());
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date testDate = null;
            try {
                testDate = df.parse(editor.getTextField().getText());

                Calendar cal = new GregorianCalendar();
                cal.setTime(testDate);

                CSSDate = CSSDate.withHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
                CSSDate = CSSDate.withMinuteOfHour(cal.get(Calendar.MINUTE));
            } catch (ParseException e1) {
                // Ignore
            }

            // departurePicker

            // System.out.println("DepartureTime text was changed to "
            // + editor.getTextField().getText());
        }
    }

    @Override
    public void removeUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == timeZoneDropdown) {
            updateTimeZone();
            return;
        }

        if (arg0.getSource() == btnAddPoint) {
            surfaceDriftPanelContainer.add(addPoint(metocPoints));
            scrollPaneSurfaceDrift.validate();
            scrollPaneSurfaceDrift.repaint();
            return;

        }

        if (arg0.getSource() == searchObjectDropDown) {
            searchObjectText.setText(LeewayValues.getLeeWayContent().get(
                    searchObjectDropDown.getSelectedIndex()));
            return;
        }

        if (arg0.getSource() == dsp1DatePicker) {

            Date tempDate = dsp1DatePicker.getDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime(tempDate);

            LKPDate = LKPDate
                    .withDate(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH));
            return;
        }
        if (arg0.getSource() == cssDatePicker) {

            Date tempDate = cssDatePicker.getDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime(tempDate);

            CSSDate = CSSDate
                    .withDate(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH));
            return;
        }
    }

    private void updateTimeZone() {

        // System.out.println("Updated timezone");

        String selectedTimeZone = (String) timeZoneDropdown.getSelectedItem();

        timeZone = DateTimeZone.forID(selectedTimeZone);

        // System.out.println("LKP time is: " + LKPDate);
        //
        // // Updated internal time
        // LKPDate = LKPDate.toDateTime(timeZone);
        // CSSDate = CSSDate.toDateTime(timeZone);
        //
        // System.out.println("LKP time is: " + LKPDate);

        // Update spinners

        // Date lkpTempDate = new Date();
        // lkpTempDate.setHours(LKPDate.getHourOfDay());
        // // lkpTempDate.setMinutes(LKPDate.getMinuteOfDay());
        //
        // System.out.println(LKPDate.getHourOfDay());
        // System.out.println(LKPDate.getMinuteOfDay());
        //
        // lkpSpinner.getModel().setValue(lkpTempDate);
        //
        // Date CSSTempDate = new Date();
        // CSSTempDate.setHours(CSSDate.getHourOfDay());
        // // CSSTempDate.setMinutes(CSSDate.getMinuteOfDay());
        //
        // commenceStartSpinner.getModel().setValue(lkpTempDate);

    }

    public double getDatumLKPLat() {
        String LKPLatitude = dsp1FirstLat.getText() + " "
                + dsp1SecondLat.getText() + "." + dsp1ThirdLat.getText()
                + comboDsp1Lat.getSelectedItem();

        System.out.println(LKPLatitude);

        try {
            return parseLat(LKPLatitude);
        } catch (Exception e1) {
            displayMissingField("LKP Latitude");
        }

        return -9999;

    }
    
    public double getDsp2LKPLat() {
        String LKPLatitude = dsp2FirstLat.getText() + " "
                + dsp2SecondLat.getText() + "." + dsp2ThirdLat.getText()
                + comboDsp2Lat.getSelectedItem();

        System.out.println(LKPLatitude);

        try {
            return parseLat(LKPLatitude);
        } catch (Exception e1) {
            displayMissingField("DSP 2 Latitude");
        }

        return -9999;

    }

    
    public double getDsp3LKPLat() {
        String LKPLatitude = dsp3FirstLat.getText() + " "
                + dsp3SecondLat.getText() + "." + dsp3ThirdLat.getText()
                + comboDsp3Lat.getSelectedItem();

        System.out.println(LKPLatitude);

        try {
            return parseLat(LKPLatitude);
        } catch (Exception e1) {
            displayMissingField("DSP 3 Latitude");
        }

        return -9999;

    }
    
    
    public double getDatumLKPLon() {
        String LKPLongitude = dsp1FirstLon.getText() + " "
                + dsp1SecondLon.getText() + "." + dsp1ThirdLon.getText()
                + comboDsp1Lon.getSelectedItem();

        System.out.println(LKPLongitude);

        try {
            return parseLon(LKPLongitude);
        } catch (Exception e1) {
            displayMissingField("LKP Longitude");
            System.out.println(e1.getMessage());
        }

        return -9999;
    }

    
    public double getDsp2LKPLon() {
        String LKPLongitude = dsp2FirstLon.getText() + " "
                + dsp2SecondLon.getText() + "." + dsp2ThirdLon.getText()
                + comboDsp2Lon.getSelectedItem();

        System.out.println(LKPLongitude);

        try {
            return parseLon(LKPLongitude);
        } catch (Exception e1) {
            displayMissingField("DSP 2 Longitude");
            System.out.println(e1.getMessage());
        }

        return -9999;
    }
    
    
    public double getDsp3LKPLon() {
        String LKPLongitude = dsp3FirstLon.getText() + " "
                + dsp3SecondLon.getText() + "." + dsp3ThirdLon.getText()
                + comboDsp3Lon.getSelectedItem();

        System.out.println(LKPLongitude);

        try {
            return parseLon(LKPLongitude);
        } catch (Exception e1) {
            displayMissingField("DSP 3 Longitude");
            System.out.println(e1.getMessage());
        }

        return -9999;

    }
    
    
    private static double parseLon(String lonStr) throws FormatException {
        return ParseUtils.parseLongitude(lonStr);
    }

    private static double parseLat(String latStr) throws FormatException {
        return ParseUtils.parseLatitude(latStr);
    }

    public void displayMissingField(String fieldname) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, "Missing or incorrect value in "
                + fieldname, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public void displayMsgbox(String msg) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, msg, "Input Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public boolean checkMetocTime() {
        // The weather point must be before LKP
        if (LKPDate.isAfter(surfaceDriftPanelList.get(0).getDateTime()
                .getTime())) {
            return true;
        }

        displayMsgbox("Surfact drift point must be before\nLast Known Position");

        return false;
    }

    public boolean checkTime() {

        // Do we start the search AFTER the LKP
        if (CSSDate.isAfter(LKPDate) && DSP2Date.isAfter(LKPDate) && DSP3Date.isAfter(DSP3Date)) {
            return true;
        }

        displayMsgbox("Commence Search Start Must be after\nDSP1\nDSP2 and DSP3 must be after DSP1");

        return false;
    }
    
    

    /**
     * @return the dSP2Date
     */
    public DateTime getDSP2Date() {
        return DSP2Date;
    }

    /**
     * @return the dSP3Date
     */
    public DateTime getDSP3Date() {
        return DSP3Date;
    }

    public double getSafetyFactor() {

        String sfField = yErrorField.getText();

        if (sfField.equals("")) {
            displayMissingField("Safety Factor, FS");
            return -9999;
        } else {
            try {
                if (sfField.contains(",")) {
                    sfField = sfField.replace(",", ".");
                }

                return Double.parseDouble(sfField);
            } catch (Exception e) {
                displayMissingField("Safety Factor, FS");
                return -9999;
            }
        }

    }

    public double getNavError() {

        String yField = yErrorField.getText();

        if (yField.equals("")) {
            displayMissingField("Navigational Error, Y");
            return -9999;
        } else {
            try {
                if (yField.contains(",")) {
                    yField = yField.replace(",", ".");
                }
                return Double.parseDouble(yField);
            } catch (Exception e) {
                displayMissingField("Navigational Error, Y");
                return -9999;
            }
        }

    }

    public double getInitialPositionError() {

        String xField = xErrorField.getText();

        if (xField.equals("")) {
            displayMissingField("Initial Position Error, X");
            return -9999;
        } else {
            try {
                if (xField.contains(",")) {
                    xField = xField.replace(",", ".");
                }
                return Double.parseDouble(xField);
            } catch (Exception e) {
                displayMissingField("Initial Position Error, X");
                return -9999;
            }
        }
    }

    /**
     * @return the surfaceDriftPanelList
     */
    public List<SurfaceDriftPanel> getSurfaceDriftPanelList() {
        return surfaceDriftPanelList;
    }

    public int getSearchItemID() {
        return searchObjectDropDown.getSelectedIndex();
    }

    public String getSARID() {
        return sarIDTxtField.getText();
    }

    /**
     * @return the lKPDate
     */
    public DateTime getLKPDate() {
        return LKPDate;
    }

    /**
     * @return the cSSDate
     */
    public DateTime getCSSDate() {
        return CSSDate;
    }

    public void setSARType(SAR_TYPE type) {

        if (type == SAR_TYPE.RAPID_RESPONSE) {
            topPanel.setBorder(new TitledBorder(null,
                    "Rapid Response Operation", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
        }

        if (type == SAR_TYPE.DATUM_POINT) {
            topPanel.setBorder(new TitledBorder(null, "Datum Point Operation",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
        }

    }
}
