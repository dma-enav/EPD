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
import javax.swing.SwingConstants;

public class RapidResponseDatumPointInputPanel extends JPanel implements
        ActionListener, DocumentListener {

    private static final long serialVersionUID = 1L;

    private JTextField lkpFirstLat;

    private JTextField xErrorField;
    private JTextField yErrorField;
    private JTextField safetyFactorField;

    private SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");

    // Initialize with currentDate on CET
    private DateTimeZone timeZone = DateTimeZone.forID("CET");
    private DateTime LKPDate = new DateTime(timeZone);
    private DateTime CSSDate = new DateTime(timeZone);

    private JTextField lkpSecondLat;
    private JTextField lkpThirdLat;
    private JTextField lkpFirstLon;
    private JTextField lkpSecondLon;
    private JTextField lkpThirdLon;

    private int surfaceDriftPanelHeight = 50;
    private int metocPoints;
    private JPanel surfaceDriftPanelContainer;
    private JButton btnAddPoint;
    private JScrollPane scrollPaneSurfaceDrift;
    private JComboBox<String> searchObjectDropDown;
    private JLabel searchObjectText;

    private JXDatePicker cssDatePicker;
    private JXDatePicker lkpDatePicker;
    private JSpinner lkpSpinner;
    private JSpinner commenceStartSpinner;

    private JComboBox<String> timeZoneDropdown;

    private List<SurfaceDriftPanel> surfaceDriftPanelList = new ArrayList<SurfaceDriftPanel>();
    private JButton btnRemoveLastButton;

    private JComboBox<String> comboLKPLat;
    private JComboBox<String> comboLKPLon;
    private JTextField sarIDTxtField;
    private JPanel topPanel;

    public RapidResponseDatumPointInputPanel() {

        format.setTimeZone(TimeZone.getTimeZone("CET"));

        CSSDate = CSSDate.plusHours(1);

        System.out.println(LKPDate.toDate());
        System.out.println(CSSDate.toDate());

        setPreferredSize(new Dimension(500, 600));
        // inputPanel.setPreferredSize(new Dimension(500, 700));

        setBorder(new EmptyBorder(5, 5, 5, 5));
        // getContentPane().add(inputPanel, BorderLayout.CENTER);
        setLayout(null);

        JPanel lkpPanel = new JPanel();
        lkpPanel.setBorder(new TitledBorder(null, "Last Known Position",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        lkpPanel.setBounds(20, 59, 494, 87);
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

        JLabel lblLastKnownPosition = new JLabel("Last Known Position:");
        lblLastKnownPosition.setBounds(13, 50, 147, 14);
        lkpPanel.add(lblLastKnownPosition);

        lkpFirstLat = new JTextField();
        lkpFirstLat.setHorizontalAlignment(SwingConstants.RIGHT);

        lkpFirstLat.setText("");
        lkpFirstLat.setBounds(170, 47, 30, 20);
        lkpPanel.add(lkpFirstLat);
        lkpFirstLat.setColumns(10);

        timeZoneDropdown = new JComboBox<String>();
        timeZoneDropdown.setModel(new DefaultComboBoxModel<String>(
                new String[] { "CET", "UTC", "GMT" }));
        timeZoneDropdown.setBounds(342, 22, 46, 20);
        lkpPanel.add(timeZoneDropdown);
        timeZoneDropdown.addActionListener(this);

        lkpSecondLat = new JTextField();
        lkpSecondLat.setText("");
        lkpSecondLat.setColumns(10);
        lkpSecondLat.setBounds(200, 47, 20, 20);
        lkpPanel.add(lkpSecondLat);

        lkpThirdLat = new JTextField();
        lkpThirdLat.setText("");
        lkpThirdLat.setColumns(10);
        lkpThirdLat.setBounds(220, 47, 30, 20);
        lkpPanel.add(lkpThirdLat);

        comboLKPLat = new JComboBox<String>();
        comboLKPLat.setModel(new DefaultComboBoxModel<String>(new String[] {
                "N", "S" }));
        comboLKPLat.setBounds(250, 47, 37, 20);
        lkpPanel.add(comboLKPLat);

        lkpFirstLon = new JTextField();
        lkpFirstLon.setHorizontalAlignment(SwingConstants.RIGHT);
        lkpFirstLon.setText("");
        lkpFirstLon.setColumns(10);
        lkpFirstLon.setBounds(290, 47, 30, 20);
        lkpPanel.add(lkpFirstLon);

        lkpSecondLon = new JTextField();
        lkpSecondLon.setText("");
        lkpSecondLon.setColumns(10);
        lkpSecondLon.setBounds(320, 47, 20, 20);
        lkpPanel.add(lkpSecondLon);

        comboLKPLon = new JComboBox<String>();
        comboLKPLon.setModel(new DefaultComboBoxModel<String>(new String[] {
                "E", "W" }));
        comboLKPLon.setBounds(370, 47, 37, 20);
        lkpPanel.add(comboLKPLon);

        lkpThirdLon = new JTextField();
        lkpThirdLon.setText("");
        lkpThirdLon.setColumns(10);
        lkpThirdLon.setBounds(340, 47, 30, 20);
        lkpPanel.add(lkpThirdLon);

        JPanel commenceStartPanel = new JPanel();
        commenceStartPanel.setBorder(new TitledBorder(null,
                "Commence Search Start", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        commenceStartPanel.setBounds(20, 157, 494, 53);
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

        scrollPaneSurfaceDrift.setBounds(20, 224, 494, 175);

        add(scrollPaneSurfaceDrift);

        surfaceDriftPanelContainer.setLayout(null);

        surfaceDriftPanelContainer.add(addPoint(metocPoints));

        JButton btnFetchMetocData = new JButton("Fetch METOC Data");
        btnFetchMetocData.setEnabled(false);
        btnFetchMetocData.setBounds(10, 22, 123, 23);
        surfaceDriftPanelContainer.add(btnFetchMetocData);

        btnAddPoint = new JButton("Add point");
        // btnAddPoint.setEnabled(false);
        btnAddPoint.setBounds(265, 22, 89, 23);
        surfaceDriftPanelContainer.add(btnAddPoint);
        btnAddPoint.addActionListener(this);

        btnRemoveLastButton = new JButton("Remove Last");
        btnRemoveLastButton.setEnabled(false);
        btnRemoveLastButton.setBounds(364, 22, 120, 23);
        surfaceDriftPanelContainer.add(btnRemoveLastButton);
        btnRemoveLastButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Other variables",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(20, 410, 487, 100);
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
        safetyFactorField.setEnabled(false);
        panel.add(safetyFactorField);
        safetyFactorField.setColumns(10);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Search Object",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(20, 520, 487, 87);
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
        lkpFirstLat.setText("56");
        lkpSecondLat.setText("30");
        lkpThirdLat.setText("290");
        lkpFirstLon.setText("11");
        lkpSecondLon.setText("57");
        lkpThirdLon.setText("840");
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

    private JPanel removePoint() {

        SurfaceDriftPanel surfaceDriftPanel = surfaceDriftPanelList
                .get(surfaceDriftPanelList.size() - 1);

        surfaceDriftPanelHeight = surfaceDriftPanelHeight - 110;

        surfaceDriftPanelContainer.setPreferredSize(new Dimension(494,
                surfaceDriftPanelHeight));

        metocPoints--;

        surfaceDriftPanelList.remove(surfaceDriftPanel);

        return surfaceDriftPanel;
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
        if (arg0.getSource() == timeZoneDropdown) {
            updateTimeZone();
            return;
        }

        if (arg0.getSource() == btnAddPoint) {
            surfaceDriftPanelContainer.add(addPoint(metocPoints));
            scrollPaneSurfaceDrift.validate();
            scrollPaneSurfaceDrift.repaint();
            btnRemoveLastButton.setEnabled(true);

            return;
        }

        if (arg0.getSource() == btnRemoveLastButton) {
            surfaceDriftPanelContainer.remove(removePoint());
            scrollPaneSurfaceDrift.validate();
            scrollPaneSurfaceDrift.repaint();

            if (metocPoints == 1) {
                btnRemoveLastButton.setEnabled(false);
            }

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

    public double getRapidResponseDatumLKPLat() {
        String LKPLatitude = lkpFirstLat.getText() + " "
                + lkpSecondLat.getText() + "." + lkpThirdLat.getText()
                + comboLKPLat.getSelectedItem();

        System.out.println(LKPLatitude);

        try {
            return parseLat(LKPLatitude);
        } catch (Exception e1) {
            displayMissingField("LKP Latitude");
        }

        return -9999;

    }

    public double getRapidResponseDatumLKPLon() {
        String LKPLongitude = lkpFirstLon.getText() + " "
                + lkpSecondLon.getText() + "." + lkpThirdLon.getText()
                + comboLKPLon.getSelectedItem();

        System.out.println(LKPLongitude);

        try {
            return parseLon(LKPLongitude);
        } catch (Exception e1) {
            displayMissingField("LKP Longitude");
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

        
        DateTime previous = null;
        
         for (int i = 0; i < surfaceDriftPanelList.size(); i++) {
             
             //We are at first point
             if (previous == null){
                 
                 //Is this point before LKP
                 if (!LKPDate.isAfter(surfaceDriftPanelList.get(i).getDateTime()
                         .getTime())){
                     
                     displayMsgbox("Surfact drift point must be before\nLast Known Position");
                     return false;
                 }else{
                     previous = new DateTime(surfaceDriftPanelList.get(i).getDateTime().getTime());
                 }
                 
             }else{
                 if (previous.isAfter(surfaceDriftPanelList.get(i).getDateTime()
                         .getTime())){
                     
                     displayMsgbox("Surfact drift point must be in correct time order");
                     
                     return false;
                     
                 }
                 
                 if (CSSDate.isBefore(surfaceDriftPanelList.get(i).getDateTime()
                         .getTime())){
                     
                     displayMsgbox("Surface Drift Point " + (i+1) + " is not useable as it is after CSS \nPlease Remove");
                     
                     return false;
                     
                 }
                 
                 if (LKPDate.isAfter(surfaceDriftPanelList.get(i).getDateTime().getTime())){
                     
                     displayMsgbox("Multiple Surface Drift points before LKP \nPlease remove irrelevant ones");
                     
                     return false;
                     
                 }
                 
                 
                 
                 //Point must be after previous
                 //Point must be before CSP
                 
             }
             
             
             
         }
        
         
         return true;
         
         
//        //First point must be before LKP
//        if (LKPDate.isAfter(surfaceDriftPanelList.get(0).getDateTime()
//                .getTime())) {
//            
//            
//            
//            
//            
//            
//            
//            return true;
//        }

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


//        displayMsgbox("Surfact drift point must be before\nLast Known Position");

//        return true;
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
