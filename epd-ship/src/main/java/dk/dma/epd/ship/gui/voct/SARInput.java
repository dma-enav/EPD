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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.LeewayValues;
import dk.dma.epd.ship.service.voct.SAR_TYPE;
import dk.dma.epd.ship.service.voct.VOCTManager;

public class SARInput extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();

    private JTextField lkpFirstLat;

    private JTextField xErrorField;
    private JTextField yErrorField;
    private JTextField safetyFactorField;

    private JComboBox<String> typeSelectionComboBox;
    private JButton nextButton;
    private JButton cancelButton;
    private JButton btnBack;

    private JLabel descriptiveImage;
    private JTextPane descriptiveText;

    private SimpleDateFormat format = new SimpleDateFormat("E dd/MM/yyyy");

    String rapidresponseTxt = "Rapid Response - Rapid Response should be used when the rescue vessel is within the designated search area in a relatively short timespan (1-2 hours after LKP).";
    String datumPointTxt = "Datum Pont - Datum point is a calculation method used when the rescue vessel arrives to the designated search area after 2 or more hours after LKP";
    String datumLineTxt = "Datum line - Datum line is used when an object is mising and a LKP is unkown but a assumed route is known";
    String backtrackTxt = "Back track - Back track is used when a object has been located that is connected to the missing vessel. By reversing the objects movements a possible search area can be established";

    ImageIcon rapidResponseIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/generic.png")));

    ImageIcon datumPointIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/datumpoint.png")));

    ImageIcon datumLineIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/datumline.png")));

    ImageIcon backtrackIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/generic.png")));

    VOCTManager voctManager;

    JPanel masterPanel;

    static final String SELECTSARTYPE = "Select SAR Type";
    static final String INPUTSARRAPIDRESPONSE = "Rapid Response Input Panel";
    static final String CALCULATIONSPANELRAPIDRESPONSE = "Rapid Response Calculations Panel";

    // First card shown is the select sar type
    String currentCard = SELECTSARTYPE;

    // Initialize with currentDate on CET
    DateTimeZone timeZone = DateTimeZone.forID("CET");
    DateTime LKPDate = new DateTime(timeZone);
    DateTime CSSDate = new DateTime(timeZone);

    private JTextField lkpSecondLat;
    private JTextField lkpThirdLat;
    private JTextField lkpFirstLon;
    private JTextField lkpSecondLon;
    private JTextField lkpThirdLon;

    int surfaceDriftPanelHeight = 50;
    int metocPoints;
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
    private JLabel calculationsText = new JLabel();
    private JButton btnNewButton;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;

    /**
     * 
     * Create the dialog.
     * 
     * @param voctManager
     */
    public SARInput(VOCTManager voctManager) {
        this.voctManager = voctManager;
        setTitle("SAR Operation");
        this.setModal(true);

        format.setTimeZone(TimeZone.getTimeZone("CET"));
        
        CSSDate = CSSDate.plusHours(1);

        System.out.println(LKPDate.toDate());
        System.out.println(CSSDate.toDate());

         setBounds(100, 100, 559, 733);
//        setBounds(100, 100, 559, 433);

        masterPanel = new JPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(masterPanel, BorderLayout.CENTER);

        buttomBar();

        // We initialize it with the init pane, this is where you select the
        // type of operation

        masterPanel.setLayout(new CardLayout());

        initPanel();
        inputPanel();
        calculationsPanel();
    }

    private void calculationsPanel() {
        JPanel calculationsPanel = new JPanel();
        JScrollPane calculationsScrollPanel = new JScrollPane(calculationsPanel);
        calculationsPanel.setLayout(null);

        masterPanel
                .add(calculationsScrollPanel, CALCULATIONSPANELRAPIDRESPONSE);

        JLabel lblCalculationsPanelTitle = new JLabel(
                "Rapid Response Calculations");
        lblCalculationsPanelTitle.setBounds(10, 11, 207, 14);
        calculationsPanel.add(lblCalculationsPanelTitle);

        calculationsPanel.add(calculationsText);
        calculationsText.setBounds(10, 30, 521, 319);

    }

    private void generateRapidResponseCalculations(){
        
        
        //Where we grab the data that was input in the previous window
        //Will be seperate for each type of calculation but HTML generated to keep layout simple and possibly exportable
        
//      LKPDate;
//      CSSDate;
        
        
        double difference = (double) (LKPDate.getMillis() - CSSDate.getMillis()) / 60 / 60 / 1000;
//        System.out.println("Hours since started: " + difference);
        

        
        //Generate a html sheet of rapid response calculations
        StringBuilder str = new StringBuilder();
        String name = "How does this look";
        
        str.append("<html>");
        str.append("<br>Time of Last Known Position: " + LKPDate + "</br>");
        str.append("<br>Commence Search Start time: " + CSSDate + "</br>");
        str.append("<br>Time difference: " + difference + " hours</br>");
        
        
      double currentTWC = surfaceDriftPanelList.get(0).getTWCKnots() * difference;
        
        str.append("<br>Using " + metocPoints + " weather points</br>");
        
        str.append("<br>TWC is " + currentTWC + " with heading " + surfaceDriftPanelList.get(0).getTWCHeading() + "</br>");
        
        str.append("<br>Using formula for " + searchObjectDropDown.getSelectedItem() + " with values as " + LeewayValues.getLeeWayContent().get(
                searchObjectDropDown.getSelectedIndex()) + "</br>");
        
        double leewaySpeed = LeewayValues.personInWater(surfaceDriftPanelList.get(0).getLeeway());
        
        str.append("<br>Gives a Leeway speed of " + leewaySpeed + " with heading " + surfaceDriftPanelList.get(0).getLeewayHeading() + "</br>");
        
        double leeway = leewaySpeed * difference;
        str.append("<br>Leeway is " + leeway + " weather points</br>");

//        
//        Ellipsoid reference = Ellipsoid.WGS84;
//        double[] endBearing = new double[1];
//
//        // Object starts at LKP, with TWCheading, drifting for currentWTC
//        // knots where will it end up
//        Position currentPos = calculateEndingGlobalCoordinates(reference,
//                LKP, TWCHeading, Converter.nmToMeters(currentTWC),
//                endBearing);
//
//        System.out.println("Current is: " + currentPos.getLatitude());
//        System.out.println("Current is: " + currentPos.getLongitude());
//
//        endBearing = new double[1];
//        
//        Position windPos = calculateEndingGlobalCoordinates(reference,
//                currentPos, downWind, Converter.nmToMeters(leeway),
//                endBearing);
//
//        
//        
//        System.out.println("Wind pos is: " + windPos.getLatitude());
//        System.out.println("Wind pos is: " + windPos.getLongitude());
//        
//        
//        Position datum = windPos;
//        
//        System.out.println("Final position is " + datum);
//
//        // RDV Direction
//        double rdvDirection = bearing(LKP, windPos, Heading.RL);
//
//        System.out.println("RDV Direction: " + rdvDirection);
//
//        // RDV Distance
//        double rdvDistance = range(LKP, windPos, Heading.RL);
//
//        System.out.println("RDV Distance: " + rdvDistance);
//
//        // RDV Speed
//        double rdvSpeed = rdvDistance / timeElasped;
//
//        System.out.println("RDV Speed: " + rdvSpeed);
//
//        // Radius:
//        double radius = x + y + 0.3 * rdvDistance * SF;
//
//        System.out.println("Radius is: " + radius);
//        
        
        
        
        
        
        
        str.append("</html>");

        calculationsText.setText(str.toString());
        
        
        
    }

    private void inputPanel() {

        JPanel inputPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setPreferredSize(new Dimension(559, 363));
        // scrollPane.setPreferredSize(new Dimension(559, 763));
        masterPanel.add(scrollPane, INPUTSARRAPIDRESPONSE);

        inputPanel.setPreferredSize(new Dimension(500, 600));
        // inputPanel.setPreferredSize(new Dimension(500, 700));

        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        // getContentPane().add(inputPanel, BorderLayout.CENTER);
        inputPanel.setLayout(null);

        JLabel lblRapidResponseOperation = new JLabel(
                "Rapid Response Operation");
        lblRapidResponseOperation.setBounds(10, 11, 207, 14);
        inputPanel.add(lblRapidResponseOperation);

        JPanel lkpPanel = new JPanel();
        lkpPanel.setBorder(new TitledBorder(null, "Last Known Position",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        lkpPanel.setBounds(20, 36, 494, 87);
        inputPanel.add(lkpPanel);
        lkpPanel.setLayout(null);

        lkpDatePicker = new JXDatePicker();
        lkpDatePicker.setBounds(170, 22, 105, 20);
        lkpPanel.add(lkpDatePicker);
        lkpDatePicker.setDate(LKPDate.toDate());

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

        JLabel lblLastKnownPosition = new JLabel("Last Known Position:");
        lblLastKnownPosition.setBounds(13, 50, 147, 14);
        lkpPanel.add(lblLastKnownPosition);

        lkpFirstLat = new JTextField();
        lkpFirstLat.setText("56");
        lkpFirstLat.setBounds(170, 47, 20, 20);
        lkpPanel.add(lkpFirstLat);
        lkpFirstLat.setColumns(10);

        timeZoneDropdown = new JComboBox<String>();
        timeZoneDropdown.setModel(new DefaultComboBoxModel<String>(
                new String[] { "CET", "UTC", "GMT" }));
        timeZoneDropdown.setBounds(342, 22, 46, 20);
        lkpPanel.add(timeZoneDropdown);
        timeZoneDropdown.addActionListener(this);

        lkpSecondLat = new JTextField();
        lkpSecondLat.setText("21");
        lkpSecondLat.setColumns(10);
        lkpSecondLat.setBounds(190, 47, 20, 20);
        lkpPanel.add(lkpSecondLat);

        lkpThirdLat = new JTextField();
        lkpThirdLat.setText("639");
        lkpThirdLat.setColumns(10);
        lkpThirdLat.setBounds(210, 47, 30, 20);
        lkpPanel.add(lkpThirdLat);

        JComboBox comboLKPLat = new JComboBox();
        comboLKPLat
                .setModel(new DefaultComboBoxModel(new String[] { "N", "S" }));
        comboLKPLat.setBounds(240, 47, 30, 20);
        lkpPanel.add(comboLKPLat);

        lkpFirstLon = new JTextField();
        lkpFirstLon.setText("13");
        lkpFirstLon.setColumns(10);
        lkpFirstLon.setBounds(278, 47, 20, 20);
        lkpPanel.add(lkpFirstLon);

        lkpSecondLon = new JTextField();
        lkpSecondLon.setText("67");
        lkpSecondLon.setColumns(10);
        lkpSecondLon.setBounds(298, 47, 20, 20);
        lkpPanel.add(lkpSecondLon);

        JComboBox comboLKPLon = new JComboBox();
        comboLKPLon
                .setModel(new DefaultComboBoxModel(new String[] { "E", "W" }));
        comboLKPLon.setBounds(348, 47, 30, 20);
        lkpPanel.add(comboLKPLon);

        lkpThirdLon = new JTextField();
        lkpThirdLon.setText("070");
        lkpThirdLon.setColumns(10);
        lkpThirdLon.setBounds(318, 47, 30, 20);
        lkpPanel.add(lkpThirdLon);

        JPanel commenceStartPanel = new JPanel();
        commenceStartPanel.setBorder(new TitledBorder(null,
                "Commence Search Start", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        commenceStartPanel.setBounds(20, 134, 494, 91);
        commenceStartPanel.setLayout(null);
        inputPanel.add(commenceStartPanel);

        cssDatePicker = new JXDatePicker();
        cssDatePicker.setBounds(170, 22, 105, 20);
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

        commenceStartPanel.add(commenceStartSpinner);
        
        JLabel lblCommenceStartPosition = new JLabel("Commence Start Position:");
        lblCommenceStartPosition.setBounds(13, 56, 147, 14);
        commenceStartPanel.add(lblCommenceStartPosition);
        
        textField = new JTextField();
        textField.setText("56");
        textField.setColumns(10);
        textField.setBounds(170, 53, 20, 20);
        commenceStartPanel.add(textField);
        
        textField_1 = new JTextField();
        textField_1.setText("21");
        textField_1.setColumns(10);
        textField_1.setBounds(190, 53, 20, 20);
        commenceStartPanel.add(textField_1);
        
        textField_2 = new JTextField();
        textField_2.setText("639");
        textField_2.setColumns(10);
        textField_2.setBounds(210, 53, 30, 20);
        commenceStartPanel.add(textField_2);
        
        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(240, 53, 30, 20);
        commenceStartPanel.add(comboBox);
        
        textField_3 = new JTextField();
        textField_3.setText("13");
        textField_3.setColumns(10);
        textField_3.setBounds(278, 53, 20, 20);
        commenceStartPanel.add(textField_3);
        
        textField_4 = new JTextField();
        textField_4.setText("67");
        textField_4.setColumns(10);
        textField_4.setBounds(298, 53, 20, 20);
        commenceStartPanel.add(textField_4);
        
        textField_5 = new JTextField();
        textField_5.setText("070");
        textField_5.setColumns(10);
        textField_5.setBounds(318, 53, 30, 20);
        commenceStartPanel.add(textField_5);
        
        JComboBox comboBox_1 = new JComboBox();
        comboBox_1.setBounds(348, 53, 30, 20);
        commenceStartPanel.add(comboBox_1);

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

        scrollPaneSurfaceDrift.setBounds(20, 233, 494, 166);

        inputPanel.add(scrollPaneSurfaceDrift);

        surfaceDriftPanelContainer.setLayout(null);

        surfaceDriftPanelContainer.add(addPoint(metocPoints));

        JButton btnFetchMetocData = new JButton("Fetch METOC Data");
        btnFetchMetocData.setEnabled(false);
        btnFetchMetocData.setBounds(10, 22, 123, 23);
        surfaceDriftPanelContainer.add(btnFetchMetocData);

        btnAddPoint = new JButton("Add point");
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
        panel.setBounds(20, 410, 487, 100);
        inputPanel.add(panel);
        panel.setLayout(null);

        JLabel lblInitialPositionError = new JLabel(
                "Initial Position Error (X), nm:");
        lblInitialPositionError.setBounds(13, 25, 164, 14);
        panel.add(lblInitialPositionError);

        xErrorField = new JTextField();
        xErrorField.setText("1.0");
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
        panel_1.setBounds(20, 520, 487, 87);
        inputPanel.add(panel_1);
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

        for (int i = 0; i < LeewayValues.getLeeWayTypes().size(); i++) {
            searchObjectDropDown.addItem(LeewayValues.getLeeWayTypes().get(i));

        }

    }

    private void initPanel() {
        masterPanel.add(initPanel, SELECTSARTYPE);

        initPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        initPanel.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Inititate New SAR Operation",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(10, 11, 523, 340);
        initPanel.add(panel);
        panel.setLayout(null);

        typeSelectionComboBox = new JComboBox<String>();
        typeSelectionComboBox.setBounds(126, 21, 102, 20);
        panel.add(typeSelectionComboBox);
        typeSelectionComboBox.setModel(new DefaultComboBoxModel<String>(
                new String[] { "Rapid Response", "Datum Point", "Datum Line",
                        "Back Track" }));

        typeSelectionComboBox.addActionListener(this);

        JLabel lblSelectSarType = new JLabel("Select SAR Type");
        lblSelectSarType.setBounds(10, 24, 140, 14);
        panel.add(lblSelectSarType);

        descriptiveText = new JTextPane();

        descriptiveText.setBounds(10, 52, 503, 112);
        panel.add(descriptiveText);
        descriptiveText.setBackground(UIManager.getColor("Button.background"));
        descriptiveText.setEditable(false);
        descriptiveText.setText(rapidresponseTxt);

        descriptiveImage = new JLabel(" ");
        descriptiveImage.setBounds(126, 202, 282, 127);
        panel.add(descriptiveImage);
        descriptiveImage.setIcon(rapidResponseIcon);

    }

    private void buttomBar() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        btnBack = new JButton("Back");
        buttonPane.add(btnBack);
        btnBack.addActionListener(this);
        btnBack.setEnabled(false);

        nextButton = new JButton("Next");
        buttonPane.add(nextButton);
        getRootPane().setDefaultButton(nextButton);
        nextButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == timeZoneDropdown) {
            updateTimeZone();
            return;
        }

        if (arg0.getSource() == typeSelectionComboBox) {
            int selectedIndex = typeSelectionComboBox.getSelectedIndex();
            // 0 Rapid Response
            // 1 Datum Point
            // 2 Datum Line
            // 3 Back track

            switch (selectedIndex) {
            case 0:
                descriptiveImage.setIcon(rapidResponseIcon);
                descriptiveText.setText(rapidresponseTxt);
                break;
            case 1:
                descriptiveImage.setIcon(datumPointIcon);
                descriptiveText.setText(datumPointTxt);
                break;
            case 2:
                descriptiveImage.setIcon(datumLineIcon);
                descriptiveText.setText(datumLineTxt);
                break;
            case 3:
                descriptiveImage.setIcon(backtrackIcon);
                descriptiveText.setText(backtrackTxt);
                break;
            }

            // if (typeSelectionCombo.)

        }

        if (arg0.getSource() == nextButton) {
            // Get the current card and action depends on that

            System.out.println("Next button pressed, currently at :"
                    + currentCard);

            // We're at SAR selection screen
            if (currentCard == SELECTSARTYPE) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                btnBack.setEnabled(true);

                inititateSarType();

                // The type select determines which panel we show
                cl.show(masterPanel, INPUTSARRAPIDRESPONSE);
                currentCard = INPUTSARRAPIDRESPONSE;
                return;
            }

            // We're at input screen
            if (currentCard == INPUTSARRAPIDRESPONSE) {
                updateValues();
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                btnBack.setEnabled(true);
                nextButton.setText("Finish");

                generateRapidResponseCalculations();

                // The type select determines which panel we show
                cl.show(masterPanel, CALCULATIONSPANELRAPIDRESPONSE);
                currentCard = CALCULATIONSPANELRAPIDRESPONSE;
                return;
            }

            // We're at confirmation screen
            if (currentCard == CALCULATIONSPANELRAPIDRESPONSE) {
                System.out.println(currentCard);
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                btnBack.setEnabled(true);
                nextButton.setText("Next");

                // Set the dialog back to input screen for reentering
                cl.show(masterPanel, INPUTSARRAPIDRESPONSE);
                currentCard = INPUTSARRAPIDRESPONSE;

                System.out.println("Hiding");

                // Inititate calculations and hide dialog
                this.setVisible(false);
                return;
            }

        }

        if (arg0.getSource() == btnBack) {

            // If we're at Rapid Response or Datum or Back back go back to init
            if (currentCard == INPUTSARRAPIDRESPONSE) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                cl.show(masterPanel, SELECTSARTYPE);
                btnBack.setEnabled(false);
                currentCard = SELECTSARTYPE;
                return;
            }

            // We're at confirmation
            if (currentCard == CALCULATIONSPANELRAPIDRESPONSE) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                cl.show(masterPanel, INPUTSARRAPIDRESPONSE);
                btnBack.setEnabled(true);
                nextButton.setText("Next");
                currentCard = INPUTSARRAPIDRESPONSE;
                return;
            }

        }

        if (arg0.getSource() == cancelButton) {
            this.setVisible(false);
            voctManager.cancelSarOperation();
        }

        if (arg0.getSource() == btnAddPoint) {
            surfaceDriftPanelContainer.add(addPoint(metocPoints));
            scrollPaneSurfaceDrift.validate();
            scrollPaneSurfaceDrift.repaint();

        }

        if (arg0.getSource() == searchObjectDropDown) {
            searchObjectText.setText(LeewayValues.getLeeWayContent().get(
                    searchObjectDropDown.getSelectedIndex()));
        }

    }

    private static ImageIcon scaleImage(ImageIcon icon) {
        // Scale it?
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(282, 127,
                java.awt.Image.SCALE_SMOOTH);

        ImageIcon newIcon = new ImageIcon(newimg);

        return newIcon;
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

    private void updateValues() {

        // voctManager

    }

    private void checkTime() {
        // LKP must be before commence start

        cssDatePicker.getDate();

        lkpDatePicker.getDate();

        lkpSpinner.getValue();

        commenceStartSpinner.getValue();

        DateTimeZone timeZone = DateTimeZone.forID("CET");

        DateTime TLKP = new DateTime(2013, 7, 2, 8, 0, timeZone);

        DateTime CSS = new DateTime(2013, 7, 2, 10, 30, timeZone);
    }

    @SuppressWarnings("deprecation")
    private void updateTimeZone() {
        
        System.out.println("Updated timezone");

        String selectedTimeZone = (String) timeZoneDropdown.getSelectedItem();

        timeZone = DateTimeZone.forID(selectedTimeZone);

        System.out.println("LKP time is: " + LKPDate);
        
        System.out.println("To date version of lkp is: " + LKPDate.toDate());
        
        //Updated internal time
        LKPDate = LKPDate.toDateTime(timeZone);
        CSSDate = CSSDate.toDateTime(timeZone);

        
        //Update spinners
        
        Date lkpTempDate = new Date();
        lkpTempDate.setHours(LKPDate.getHourOfDay());
//        lkpTempDate.setMinutes(LKPDate.getMinuteOfDay());
        
        System.out.println(LKPDate.getHourOfDay());
        System.out.println(LKPDate.getMinuteOfDay());
        
        
        
        lkpSpinner.getModel().setValue(lkpTempDate);
        
        Date CSSTempDate = new Date();
        CSSTempDate.setHours(CSSDate.getHourOfDay());
//        CSSTempDate.setMinutes(CSSDate.getMinuteOfDay());
        
        commenceStartSpinner.getModel().setValue(lkpTempDate);
        
        //What about datepickers?
    }

    private void inititateSarType() {
        int selectedIndex = typeSelectionComboBox.getSelectedIndex();
        // 0 Rapid Response
        // 1 Datum Point
        // 2 Datum Line
        // 3 Back track

        switch (selectedIndex) {
        case 0:
            voctManager.setSarType(SAR_TYPE.RAPID_RESPONSE);
            break;
        case 1:
            voctManager.setSarType(SAR_TYPE.DATUM_POINT);
            break;
        case 2:
            voctManager.setSarType(SAR_TYPE.DATUM_LINE);
            break;
        case 3:
            voctManager.setSarType(SAR_TYPE.BACKTRACK);
            break;
        }
    }
    
    
    private void validateInputAndInititate(){
        SAR_TYPE type = voctManager.getSarType();
        
        switch (type) {
        case RAPID_RESPONSE:
            voctManager.setSarType(SAR_TYPE.RAPID_RESPONSE);
            break;
        case DATUM_POINT:
            voctManager.setSarType(SAR_TYPE.DATUM_POINT);
            break;
        case DATUM_LINE:
            voctManager.setSarType(SAR_TYPE.DATUM_LINE);
            break;
        case BACKTRACK:
            voctManager.setSarType(SAR_TYPE.BACKTRACK);
            break;
        case NONE:
            break;
        }
    }
    
    private void validateRapidResponse(){
        
    }
}
