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
package dk.dma.epd.common.prototype.gui.voct;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.prototype.model.voct.SARFileParser;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.util.ParseUtils;

public class SimpleSARInputPanel extends JPanel implements ActionListener,
        DocumentListener {

    private static final long serialVersionUID = 1L;

    private JTextField xErrorField;
    private JTextField yErrorField;
    private JTextField safetyFactorField;

    private SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");

    // Initialize with currentDate on CET
    private DateTimeZone timeZone = DateTimeZone.forID("CET");
    private DateTime LKPDate = new DateTime(timeZone);
    private DateTime CSSDate = new DateTime(timeZone);

    private int surfaceDriftPanelHeight = 50;
    private int metocPoints;
    private JComboBox<String> searchObjectDropDown;
    private JLabel searchObjectText;

    private JXDatePicker cssDatePicker;
    private JXDatePicker lkpDatePicker;
    private JSpinner lkpSpinner;
    private JSpinner commenceStartSpinner;

    private JComboBox<String> timeZoneDropdown;

    private List<SurfaceDriftPanel> surfaceDriftPanelList = new ArrayList<SurfaceDriftPanel>();
    private JTextField sarIDTxtField;
    private JPanel topPanel;
    private JTextField datumLon;
    private JTextField datumLat;
    private JTextField aLon;
    private JTextField aLat;
    private JTextField bLon;
    private JTextField bLat;
    private JTextField cLon;
    private JTextField cLat;
    private JTextField dLon;
    private JTextField dLat;

    private JButton btnImportSimpleSar;
    JLabel lblFileSelected;

    public SimpleSARInputPanel() {

        format.setTimeZone(TimeZone.getTimeZone("CET"));

        CSSDate = CSSDate.plusHours(1);

        // System.out.println(LKPDate.toDate());
        // System.out.println(CSSDate.toDate());

        setPreferredSize(new Dimension(500, 600));
        // inputPanel.setPreferredSize(new Dimension(500, 700));

        setBorder(new EmptyBorder(5, 5, 5, 5));
        // getContentPane().add(inputPanel, BorderLayout.CENTER);
        setLayout(null);

        JPanel lkpPanel = new JPanel();
        lkpPanel.setBorder(new TitledBorder(null, "Last Known Position",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        lkpPanel.setBounds(20, 46, 494, 60);
        add(lkpPanel);
        lkpPanel.setLayout(null);

        lkpDatePicker = new JXDatePicker();
        lkpDatePicker.setBounds(170, 22, 105, 20);
        lkpPanel.add(lkpDatePicker);
        lkpDatePicker.setDate(LKPDate.toDate());
        lkpDatePicker.addActionListener(this);

        lkpDatePicker.setFormats(format);

        JLabel lblTimeOfLast = new JLabel("Time of Last Known Position:");
        lblTimeOfLast.setBounds(13, 25, 147, 14);
        lkpPanel.add(lblTimeOfLast);

        SpinnerDateModel lkpTimeModel = new SpinnerDateModel(LKPDate.toDate(),
                null, null, Calendar.HOUR_OF_DAY);

        lkpSpinner = new JSpinner(lkpTimeModel);

        lkpSpinner.setLocation(278, 22);
        lkpSpinner.setSize(54, 20);
        JSpinner.DateEditor dateEditorLKP = new JSpinner.DateEditor(lkpSpinner,
                "HH:mm");
        lkpSpinner.setEditor(dateEditorLKP);

        lkpPanel.add(lkpSpinner);

        ((DefaultEditor) lkpSpinner.getEditor()).getTextField().getDocument()
                .addDocumentListener(this);
        ((DefaultEditor) lkpSpinner.getEditor()).getTextField().getDocument()
                .putProperty("name", "lkpSpinner");

        timeZoneDropdown = new JComboBox<String>();
        timeZoneDropdown.setModel(new DefaultComboBoxModel<String>(
                new String[] { "CET", "UTC", "GMT" }));
        timeZoneDropdown.setBounds(342, 22, 46, 20);
        lkpPanel.add(timeZoneDropdown);
        timeZoneDropdown.addActionListener(this);

        JPanel commenceStartPanel = new JPanel();
        commenceStartPanel.setBorder(new TitledBorder(null,
                "Commence Search Start", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        commenceStartPanel.setBounds(20, 117, 494, 53);
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

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Other variables",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(20, 489, 487, 100);
        add(panel);
        panel.setLayout(null);

        JLabel lblInitialPositionError = new JLabel(
                "Initial Position Error (X), nm:");
        lblInitialPositionError.setBounds(13, 25, 164, 14);
        panel.add(lblInitialPositionError);

        xErrorField = new JTextField();
        xErrorField.setText("1");
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
        safetyFactorField.setEnabled(false);
        panel.add(safetyFactorField);
        safetyFactorField.setColumns(10);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Search Object",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(20, 391, 487, 87);
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
        topPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        topPanel.setBounds(20, 0, 487, 37);
        add(topPanel);
        topPanel.setLayout(null);

        JLabel lblSarId = new JLabel("SAR No.");
        lblSarId.setBounds(10, 14, 55, 14);
        topPanel.add(lblSarId);

        sarIDTxtField = new JTextField("");
        sarIDTxtField.setBounds(58, 11, 136, 20);
        topPanel.add(sarIDTxtField);
        sarIDTxtField.setColumns(10);

        JPanel importSarPanel = new JPanel();
        importSarPanel.setBorder(new TitledBorder(null, "Simple SAR Data",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        importSarPanel.setBounds(20, 177, 501, 203);
        add(importSarPanel);
        importSarPanel.setLayout(null);

        btnImportSimpleSar = new JButton("Import Simple SAR");
        btnImportSimpleSar.setBounds(10, 29, 121, 23);
        importSarPanel.add(btnImportSimpleSar);
        btnImportSimpleSar.addActionListener(this);

        lblFileSelected = new JLabel("No file selected");
        lblFileSelected.setBounds(141, 33, 350, 14);
        importSarPanel.add(lblFileSelected);

        JLabel lblDatum = new JLabel("Datum:");
        lblDatum.setBounds(20, 63, 46, 14);
        importSarPanel.add(lblDatum);

        JLabel lblA = new JLabel("A:");
        lblA.setBounds(20, 88, 46, 14);
        importSarPanel.add(lblA);

        JLabel lblB = new JLabel("B:");
        lblB.setBounds(20, 118, 46, 14);
        importSarPanel.add(lblB);

        JLabel lblC = new JLabel("C:");
        lblC.setBounds(20, 148, 46, 14);
        importSarPanel.add(lblC);

        JLabel lblD = new JLabel("D:");
        lblD.setBounds(20, 178, 46, 14);
        importSarPanel.add(lblD);

        datumLon = new JTextField();
        datumLon.setText("");
        datumLon.setHorizontalAlignment(SwingConstants.RIGHT);
        datumLon.setColumns(10);
        datumLon.setBounds(196, 57, 110, 20);
        importSarPanel.add(datumLon);

        datumLat = new JTextField();
        datumLat.setText("");
        datumLat.setHorizontalAlignment(SwingConstants.RIGHT);
        datumLat.setColumns(10);
        datumLat.setBounds(76, 57, 110, 20);
        importSarPanel.add(datumLat);

        aLon = new JTextField();
        aLon.setText("");
        aLon.setHorizontalAlignment(SwingConstants.RIGHT);
        aLon.setColumns(10);
        aLon.setBounds(196, 82, 110, 20);
        importSarPanel.add(aLon);

        aLat = new JTextField();
        aLat.setText("");
        aLat.setHorizontalAlignment(SwingConstants.RIGHT);
        aLat.setColumns(10);
        aLat.setBounds(76, 82, 110, 20);
        importSarPanel.add(aLat);

        bLon = new JTextField();
        bLon.setText("");
        bLon.setHorizontalAlignment(SwingConstants.RIGHT);
        bLon.setColumns(10);
        bLon.setBounds(196, 113, 110, 20);
        importSarPanel.add(bLon);

        bLat = new JTextField();
        bLat.setText("");
        bLat.setHorizontalAlignment(SwingConstants.RIGHT);
        bLat.setColumns(10);
        bLat.setBounds(76, 113, 110, 20);
        importSarPanel.add(bLat);

        cLon = new JTextField();
        cLon.setText("");
        cLon.setHorizontalAlignment(SwingConstants.RIGHT);
        cLon.setColumns(10);
        cLon.setBounds(196, 142, 110, 20);
        importSarPanel.add(cLon);

        cLat = new JTextField();
        cLat.setText("");
        cLat.setHorizontalAlignment(SwingConstants.RIGHT);
        cLat.setColumns(10);
        cLat.setBounds(76, 142, 110, 20);
        importSarPanel.add(cLat);

        dLon = new JTextField();
        dLon.setText("");
        dLon.setHorizontalAlignment(SwingConstants.RIGHT);
        dLon.setColumns(10);
        dLon.setBounds(196, 172, 110, 20);
        importSarPanel.add(dLon);

        dLat = new JTextField();
        dLat.setText("");
        dLat.setHorizontalAlignment(SwingConstants.RIGHT);
        dLat.setColumns(10);
        dLat.setBounds(76, 172, 110, 20);
        importSarPanel.add(dLat);

        for (int i = 0; i < LeewayValues.getLeeWayTypes().size(); i++) {
            searchObjectDropDown.addItem(LeewayValues.getLeeWayTypes().get(i));

        }

        // initSetValues();

    }

    private void initSetValues() {
        xErrorField.setText("1.0");
    }

    @Override
    public void changedUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        String name = (String) arg0.getDocument().getProperty("name");

        // Departure
        if ("lkpSpinner".equals(name)) {
            JSpinner.DateEditor editor = (JSpinner.DateEditor) lkpSpinner
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

            // departurePicker

            // System.out.println("DepartureTime text was changed to "
            // + editor.getTextField().getText());
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

        //
        if (arg0.getSource() == btnImportSimpleSar) {
            // // Import started
            //
            // // Open a file menu select for xml files
            //

            //
            String sarFolder = EPD.getInstance().getHomePath().toString()
                    + "\\sar";
            System.out.println("Sar folder path is " + sarFolder);
            JFileChooser fileChooser = new JFileChooser(new File(sarFolder));
            fileChooser.addChoosableFileFilter(new SARFilter());
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.showOpenDialog(null);

            if (fileChooser.getSelectedFile() != null) {

                String fileChoosen = fileChooser.getSelectedFile()
                        .getAbsolutePath();

                try {

                    SARFileParser parser = new SARFileParser(fileChoosen);
                    lblFileSelected.setText(fileChoosen);

                    datumLat.setText(parser.getDatum().getLatitudeAsString());
                    datumLon.setText(parser.getDatum().getLongitudeAsString());

                    System.out.println("Longitude is " + parser.getDatum().getLongitudeAsString());
                    
                    aLat.setText(parser.getA().getLatitudeAsString());
                    aLon.setText(parser.getA().getLongitudeAsString());

                    bLat.setText(parser.getB().getLatitudeAsString());
                    bLon.setText(parser.getB().getLongitudeAsString());

                    cLat.setText(parser.getC().getLatitudeAsString());
                    cLon.setText(parser.getC().getLongitudeAsString());

                    dLat.setText(parser.getD().getLatitudeAsString());
                    dLon.setText(parser.getD().getLongitudeAsString());

                    sarIDTxtField.setText(parser.getSarNumber());

                    searchObjectDropDown.setSelectedIndex(parser
                            .getSearchObject());

                    // Retrieve variables from parser
                    // Move to final page where we show the info
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Error reading file, error message "
                                    + e.getMessage() + "\nCheck file syntax",
                            "Failed to read SAR file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (arg0.getSource() == timeZoneDropdown) {
            updateTimeZone();
            return;
        }

        if (arg0.getSource() == searchObjectDropDown) {
            searchObjectText.setText(LeewayValues.getLeeWayContent().get(
                    searchObjectDropDown.getSelectedIndex()));
            return;
        }

        if (arg0.getSource() == lkpDatePicker) {

            Date tempDate = lkpDatePicker.getDate();

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

        DateTime previous = null;

        for (int i = 0; i < surfaceDriftPanelList.size(); i++) {

            // We are at first point
            if (previous == null) {

                // Is this point before LKP
                if (!LKPDate.isAfter(surfaceDriftPanelList.get(i).getDateTime()
                        .getTime())) {

                    displayMsgbox("Surfact drift point must be before\nLast Known Position");
                    return false;
                } else {
                    previous = new DateTime(surfaceDriftPanelList.get(i)
                            .getDateTime().getTime());
                }

            } else {
                if (previous.isAfter(surfaceDriftPanelList.get(i).getDateTime()
                        .getTime())) {

                    displayMsgbox("Surfact drift point must be in correct time order");

                    return false;

                }

                if (CSSDate.isBefore(surfaceDriftPanelList.get(i).getDateTime()
                        .getTime())) {

                    displayMsgbox("Surface Drift Point "
                            + (i + 1)
                            + " is not useable as it is after CSS \nPlease Remove");

                    return false;

                }

                if (LKPDate.isAfter(surfaceDriftPanelList.get(i).getDateTime()
                        .getTime())) {

                    displayMsgbox("Multiple Surface Drift points before LKP \nPlease remove irrelevant ones");

                    return false;

                }

                // Point must be after previous
                // Point must be before CSP

            }

        }

        return true;

        // //First point must be before LKP
        // if (LKPDate.isAfter(surfaceDriftPanelList.get(0).getDateTime()
        // .getTime())) {
        //
        //
        //
        //
        //
        //
        //
        // return true;
        // }

        // for (int i = 0; i < surfaceDriftPanelList.size(); i++) {
        //
        // if (!LKPDate.isAfter(surfaceDriftPanelList.get(i).getDateTime()
        // .getTime())) {
        // displayMsgbox("Surfact drift point " + (i + 1)
        // + " must be before\nLast Known Position");
        // return false;
        // }
        // // else{
        // // System.out.println("Time is " +
        // // surfaceDriftPanelList.get(i).getDateTime()) ;
        // // }
        // }

        // // The weather point must be before LKP

        // displayMsgbox("Surfact drift point must be before\nLast Known Position");

        // return true;
    }

    public boolean checkTime() {

        // Do we start the search AFTER the LKP
        if (CSSDate.isAfter(LKPDate)) {
            return true;
        }

        displayMsgbox("Commence Search Start Must be after\nLast Known Position");

        return false;
    }

    public double getSafetyFactor() {

        String sfField = safetyFactorField.getText();

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

    public double getDatumLat() {

        try {
            return parseLat(datumLat.getText());
        } catch (Exception e1) {
            displayMissingField("Datum Latitude");
        }

        return -9999;

    }

    public double getDatumLon() {
        try {
            return parseLon(datumLon.getText());
        } catch (Exception e1) {
            displayMissingField("Datum Longitude");
        }

        return -9999;
    }

    public double getALat() {
        try {
            return parseLat(aLat.getText());
        } catch (Exception e1) {
            displayMissingField("A Latitude");
        }

        return -9999;
    }
    
    public double getALon(){
        try {
            return parseLon(aLon.getText());
        } catch (Exception e1) {
            displayMissingField("A Longitude");
        }

        return -9999;
    }

    
    
    
    public double getBLat() {
        try {
            return parseLat(bLat.getText());
        } catch (Exception e1) {
            displayMissingField("B Latitude");
        }

        return -9999;
    }
    
    public double getBLon(){
        try {
            return parseLon(bLon.getText());
        } catch (Exception e1) {
            displayMissingField("B Longitude");
        }

        return -9999;
    }
    
    
    
    public double getCLat() {
        try {
            return parseLat(cLat.getText());
        } catch (Exception e1) {
            displayMissingField("C Latitude");
        }

        return -9999;
    }
    
    public double getCLon(){
        try {
            return parseLon(cLon.getText());
        } catch (Exception e1) {
            displayMissingField("c Longitude");
        }

        return -9999;
    }
    
    public double getDLat() {
        try {
            return parseLat(dLat.getText());
        } catch (Exception e1) {
            displayMissingField("D Latitude");
        }

        return -9999;
    }
    
    public double getDLon(){
        try {
            return parseLon(dLon.getText());
        } catch (Exception e1) {
            displayMissingField("D Longitude");
        }

        return -9999;
    }
    
}

class SARFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.endsWith(".sar");
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

}
