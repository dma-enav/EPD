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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXDatePicker;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;
import dk.dma.epd.common.util.ParseUtils;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.VOCTManager;

public class SARInput extends JDialog implements ActionListener,
        DocumentListener {

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

    private JComboBox<String> comboLKPLat;
    private JComboBox<String> comboLKPLon;
    private JTextField sarIDTxtField;

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
        this.setResizable(false);

        format.setTimeZone(TimeZone.getTimeZone("CET"));

        CSSDate = CSSDate.plusHours(1);

        System.out.println(LKPDate.toDate());
        System.out.println(CSSDate.toDate());

//        setBounds(100, 100, 559, 733);
         setBounds(100, 100, 559, 500);

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

        masterPanel
                .add(calculationsScrollPanel, CALCULATIONSPANELRAPIDRESPONSE);
        calculationsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        calculationsText.setVerticalAlignment(SwingConstants.TOP);
        calculationsText.setHorizontalAlignment(SwingConstants.LEFT);

        calculationsPanel.add(calculationsText);

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

        JPanel lkpPanel = new JPanel();
        lkpPanel.setBorder(new TitledBorder(null, "Last Known Position",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        lkpPanel.setBounds(20, 59, 494, 87);
        inputPanel.add(lkpPanel);
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
        lkpSecondLat.setText("30");
        lkpSecondLat.setColumns(10);
        lkpSecondLat.setBounds(190, 47, 20, 20);
        lkpPanel.add(lkpSecondLat);

        lkpThirdLat = new JTextField();
        lkpThirdLat.setText("290");
        lkpThirdLat.setColumns(10);
        lkpThirdLat.setBounds(210, 47, 30, 20);
        lkpPanel.add(lkpThirdLat);

        comboLKPLat = new JComboBox<String>();
        comboLKPLat
                .setModel(new DefaultComboBoxModel<String>(new String[] { "N", "S" }));
        comboLKPLat.setBounds(240, 47, 30, 20);
        lkpPanel.add(comboLKPLat);

        lkpFirstLon = new JTextField();
        lkpFirstLon.setText("11");
        lkpFirstLon.setColumns(10);
        lkpFirstLon.setBounds(278, 47, 20, 20);
        lkpPanel.add(lkpFirstLon);

        lkpSecondLon = new JTextField();
        lkpSecondLon.setText("57");
        lkpSecondLon.setColumns(10);
        lkpSecondLon.setBounds(298, 47, 20, 20);
        lkpPanel.add(lkpSecondLon);

        comboLKPLon = new JComboBox<String>();
        comboLKPLon
                .setModel(new DefaultComboBoxModel<String>(new String[] { "E", "W" }));
        comboLKPLon.setBounds(348, 47, 30, 20);
        lkpPanel.add(comboLKPLon);

        lkpThirdLon = new JTextField();
        lkpThirdLon.setText("840");
        lkpThirdLon.setColumns(10);
        lkpThirdLon.setBounds(318, 47, 30, 20);
        lkpPanel.add(lkpThirdLon);

        JPanel commenceStartPanel = new JPanel();
        commenceStartPanel.setBorder(new TitledBorder(null,
                "Commence Search Start", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        commenceStartPanel.setBounds(20, 157, 494, 53);
        commenceStartPanel.setLayout(null);
        inputPanel.add(commenceStartPanel);

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

        inputPanel.add(scrollPaneSurfaceDrift);

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
        
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "Rapid Response Operation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_2.setBounds(20, 0, 487, 48);
        inputPanel.add(panel_2);
        panel_2.setLayout(null);
        
        JLabel lblSarId = new JLabel("SAR No.");
        lblSarId.setBounds(10, 23, 55, 14);
        panel_2.add(lblSarId);
        
        sarIDTxtField = new JTextField("");
        sarIDTxtField.setBounds(58, 20, 136, 20);
        panel_2.add(sarIDTxtField);
        sarIDTxtField.setColumns(10);

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
//        descriptiveText.setBackground(UIManager.getColor("Button.background"));
        descriptiveText.setOpaque(false);
        descriptiveText.setEditable(false);
        descriptiveText.setText(rapidresponseTxt);

        descriptiveImage = new JLabel(" ");
        descriptiveImage.setHorizontalAlignment(SwingConstants.CENTER);
        descriptiveImage.setBounds(20, 131, 493, 200);
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

                if (validateInputAndInititate()) {
                    calculationsText.setText(voctManager.getRapidResponseData()
                            .generateHTML());
                    btnBack.setEnabled(true);
                    nextButton.setText("Finish");

                    // The type select determines which panel we show
                    cl.show(masterPanel, CALCULATIONSPANELRAPIDRESPONSE);
                    currentCard = CALCULATIONSPANELRAPIDRESPONSE;
                } else {
                    // do nothing, missing data, internally sorted
                }

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

                // Display SAR command
                voctManager.displaySar();

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

        if (arg0.getSource() == lkpDatePicker) {
            
            Date tempDate = lkpDatePicker.getDate();
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(tempDate);
            
            LKPDate = LKPDate.withDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DAY_OF_MONTH));
            return;
        }
        if (arg0.getSource() == cssDatePicker) {
            
            Date tempDate = cssDatePicker.getDate();
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(tempDate);
            
            CSSDate = CSSDate.withDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DAY_OF_MONTH));
            return;
        }

    }

    private static ImageIcon scaleImage(ImageIcon icon) {
        // Scale it?
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(444, 200,
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

    private boolean validateInputAndInititate() {
        SAR_TYPE type = voctManager.getSarType();

        System.out.println("Type is" + type);

        switch (type) {
        case RAPID_RESPONSE:
            return validateRapidResponse();
        case DATUM_POINT:
            voctManager.setSarType(SAR_TYPE.DATUM_POINT);
            return false;
        case DATUM_LINE:
            voctManager.setSarType(SAR_TYPE.DATUM_LINE);
            return false;
        case BACKTRACK:
            voctManager.setSarType(SAR_TYPE.BACKTRACK);
            return false;
        case NONE:
            return false;
        }

        return false;
    }

    private boolean validateRapidResponse() {

        System.out.println("Validating");

        // Get LKP values
        double rapidResponseLKPLat = getRapidResponseLKPLat();
        double rapidResponseLKPLon = getRapidResponseLKPLon();

        Position rapidResponsePosition;

        if (rapidResponseLKPLat != -9999 && rapidResponseLKPLon != -9999) {
            rapidResponsePosition = Position.create(rapidResponseLKPLat,
                    rapidResponseLKPLon);
        } else {
            // msgbox
            System.out.println("Failed lat");
            return false;
        }


        System.out.println("All validated correctly, we got positions");

        System.out.println("LKP Date is " + LKPDate);
        System.out.println("CSS Date is " + CSSDate);

        // Time and date will be automatically sorted

        // Get weather
        SurfaceDriftPanel firstPanel = surfaceDriftPanelList.get(0);

        double TWCKnots = firstPanel.getTWCKnots();

        if (TWCKnots == -9999) {
            // Error message is handled within function
            return false;
        }

        double leewayKnots = firstPanel.getLeeway();

        if (leewayKnots == -9999) {
            // Error message is handled within function
            return false;
        }

        double twcHeading = firstPanel.getTWCHeading();

        if (twcHeading == -9999) {
            // Error message is handled within function
            return false;
        }

        double leewayHeading = firstPanel.getLeewayHeading();

        if (leewayHeading == -9999) {
            // Error message is handled within function
            return false;
        }

        SARWeatherData sarWeatherData = new SARWeatherData(twcHeading, TWCKnots, leewayKnots, leewayHeading);
        
        List<SARWeatherData> sarWeatherDataPoints = new ArrayList<SARWeatherData>();
        sarWeatherDataPoints.add(sarWeatherData);
        
        
        double xError = getInitialPositionError();

        if (xError == -9999) {
            // Error message is handled within function
            return false;
        }

        double yError = getNavError();

        if (yError == -9999) {
            // Error message is handled within function
            return false;
        }

        double safetyFactor = getSafetyFactor();

        if (safetyFactor == -9999) {
            // Error message is handled within function
            return false;
        }

        int searchObject = searchObjectDropDown.getSelectedIndex();

        // Only valid search objects is value 0 to 19
        if (searchObject < 0 || searchObject > 20) {
            // Error message is handled within function
            System.out.println("failed search object with id " + searchObject);
            return false;
        }

        // rapidResponsePosition
        // commenceStartPosition
        // TWCKnots
        // twcHeading
        // leewayKnots
        // leewayHeading
        // xError
        // yError
        // safetyFactor
        // searchObject

        if (!checkTime()) {
            return false;
        }

        if (!checkMetocTime()) {
            return false;
        }


        
        voctManager.inputRapidResponseData(sarIDTxtField.getText(), LKPDate, CSSDate,
                rapidResponsePosition, xError, yError,
                safetyFactor, searchObject, sarWeatherDataPoints);

        return true;
    }

    private boolean checkMetocTime() {
        // The weather point must be before LKP
        if (LKPDate.isAfter(surfaceDriftPanelList.get(0).getDateTime()
                .getTime())) {
            return true;
        }

        displayMsgbox("Surfact drift point must be before\nLast Known Position");

        return false;
    }

    private boolean checkTime() {

        // Do we start the search AFTER the LKP
        if (CSSDate.isAfter(LKPDate)) {
            return true;
        }

        displayMsgbox("Commence Search Start Must be after\nLast Known Position");

        return false;
    }

    private double getSafetyFactor() {

        String sfField = yErrorField.getText();

        if (sfField.equals("")) {
            displayMissingField("Safety Factor, FS");
            return -9999;
        } else {
            try {
                return Double.parseDouble(sfField);
            } catch (Exception e) {
                displayMissingField("Safety Factor, FS");
                return -9999;
            }
        }

    }

    private double getNavError() {

        String yField = yErrorField.getText();

        if (yField.equals("")) {
            displayMissingField("Navigational Error, Y");
            return -9999;
        } else {
            try {
                return Double.parseDouble(yField);
            } catch (Exception e) {
                displayMissingField("Navigational Error, Y");
                return -9999;
            }
        }

    }

    private double getInitialPositionError() {

        String xField = xErrorField.getText();

        if (xField.equals("")) {
            displayMissingField("Initial Position Error, X");
            return -9999;
        } else {
            try {
                return Double.parseDouble(xField);
            } catch (Exception e) {
                displayMissingField("Initial Position Error, X");
                return -9999;
            }
        }

    }

    private void displayMissingField(String fieldname) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, "Missing or incorrect value in "
                + fieldname, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void displayMsgbox(String msg) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, msg, "Input Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void insertUpdate(DocumentEvent e) {
        String name = (String) e.getDocument().getProperty("name");

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

                LKPDate = LKPDate.withHourOfDay(testDate.getHours());
                LKPDate = LKPDate.withMinuteOfHour(testDate.getMinutes());
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

                CSSDate = CSSDate.withHourOfDay(testDate.getHours());
                CSSDate = CSSDate.withMinuteOfHour(testDate.getMinutes());
            } catch (ParseException e1) {
                // Ignore
            }

            // departurePicker

            // System.out.println("DepartureTime text was changed to "
            // + editor.getTextField().getText());
        }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

  

    private double getRapidResponseLKPLat() {
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

    private double getRapidResponseLKPLon() {
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
}
