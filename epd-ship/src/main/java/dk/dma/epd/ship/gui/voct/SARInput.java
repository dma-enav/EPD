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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXDatePicker;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.VOCTManager;
import javax.swing.ScrollPaneConstants;

public class SARInput extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();

    private JTextField textField;
    private JTextField textField_1;
    private JTextField lkpFirstLat;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    private JTextField textField_6;

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

    // First card shown is the select sar type
    String currentCard = SELECTSARTYPE;

    Date currentDate = new Date();
    private JTextField lkpSecondLat;
    private JTextField lkpThirdLat;
    private JTextField lkpFirstLon;
    private JTextField lkpSecondLon;
    private JTextField lkpThirdLon;

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
        setBounds(100, 100, 559, 733);
        // setBounds(100, 100, 559, 433);

        masterPanel = new JPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(masterPanel, BorderLayout.CENTER);

        buttomBar();

        // We initialize it with the init pane, this is where you select the
        // type of operation

        masterPanel.setLayout(new CardLayout());

        initPanel();
        inputPanel();
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

        JXDatePicker lkpDatePicker = new JXDatePicker();
        lkpDatePicker.setBounds(170, 22, 105, 20);
        lkpPanel.add(lkpDatePicker);
        lkpDatePicker.setDate(currentDate);

        lkpDatePicker.setFormats(format);

        JLabel lblTimeOfLast = new JLabel("Time of Last Known Position:");
        lblTimeOfLast.setBounds(13, 25, 147, 14);
        lkpPanel.add(lblTimeOfLast);

        Date date = new Date();
        SpinnerDateModel currentTimeModel = new SpinnerDateModel(date, null,
                null, Calendar.HOUR_OF_DAY);

        JSpinner lkpSpinner = new JSpinner(currentTimeModel);

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

        JComboBox comboBox_1 = new JComboBox();
        comboBox_1.setModel(new DefaultComboBoxModel(new String[] { "UTC",
                "CET", "GMT" }));
        comboBox_1.setBounds(342, 22, 46, 20);
        lkpPanel.add(comboBox_1);

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
        commenceStartPanel.setBounds(20, 134, 494, 53);
        commenceStartPanel.setLayout(null);
        inputPanel.add(commenceStartPanel);

        JXDatePicker commenceStartSearch = new JXDatePicker();
        commenceStartSearch.setBounds(170, 22, 105, 20);
        commenceStartPanel.add(commenceStartSearch);

        commenceStartSearch.setFormats(format);
        commenceStartSearch.setDate(currentDate);

        JLabel lblCommenceStartSearch = new JLabel("Time of Search Start:");
        lblCommenceStartSearch.setBounds(13, 25, 147, 14);
        commenceStartPanel.add(lblCommenceStartSearch);

        Date date2 = new Date();
        SpinnerDateModel currentTimeModel2 = new SpinnerDateModel(date2, null,
                null, Calendar.HOUR_OF_DAY);

        JSpinner commenceStartSpinner = new JSpinner(currentTimeModel2);

        commenceStartSpinner.setLocation(278, 22);
        commenceStartSpinner.setSize(54, 20);
        JSpinner.DateEditor dateEditorCommenceSearchStart = new JSpinner.DateEditor(
                commenceStartSpinner, "HH:mm");
        commenceStartSpinner.setEditor(dateEditorCommenceSearchStart);

        commenceStartPanel.add(commenceStartSpinner);

        
        
        JPanel surfaceDriftPanel = new JPanel();
        JScrollPane scrollPaneSurfaceDrift = new JScrollPane(surfaceDriftPanel);
        scrollPaneSurfaceDrift.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneSurfaceDrift.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        surfaceDriftPanel.setBorder(new TitledBorder(null, "Surface Drift",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
//        surfaceDriftPanel.setBounds(20, 195, 494, 9000);
//        surfaceDriftPanel.setSize(494, 9000)

        surfaceDriftPanel.setPreferredSize(new Dimension(494, 600));
        
        scrollPaneSurfaceDrift.setBounds(20, 195, 494, 166);
        
        inputPanel.add(scrollPaneSurfaceDrift);
        
        
        surfaceDriftPanel.setLayout(null);
        
        surfaceDriftPanel.add(addPoint(0));
        surfaceDriftPanel.add(addPoint(1));
        surfaceDriftPanel.add(addPoint(2));

        JButton btnFetchMetocData = new JButton("Fetch METOC Data");
        btnFetchMetocData.setEnabled(false);
        btnFetchMetocData.setBounds(10, 22, 123, 23);
        surfaceDriftPanel.add(btnFetchMetocData);

        JButton btnAddPoint = new JButton("Add point");
        btnAddPoint.setBounds(395, 22, 89, 23);
        surfaceDriftPanel.add(btnAddPoint);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Other variables",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(20, 372, 487, 100);
        inputPanel.add(panel);
        panel.setLayout(null);

        JLabel lblInitialPositionError = new JLabel(
                "Initial Position Error (X), nm:");
        lblInitialPositionError.setBounds(13, 25, 164, 14);
        panel.add(lblInitialPositionError);

        textField_4 = new JTextField();
        textField_4.setBounds(184, 22, 86, 20);
        panel.add(textField_4);
        textField_4.setColumns(10);

        JLabel lblSruNavigationalError = new JLabel(
                "SRU Navigational Error (Y), nm:");
        lblSruNavigationalError.setBounds(13, 50, 164, 14);
        panel.add(lblSruNavigationalError);

        textField_5 = new JTextField();
        textField_5.setBounds(184, 47, 86, 20);
        panel.add(textField_5);
        textField_5.setColumns(10);

        JLabel lblNoteGps = new JLabel("Note: GPS = 0.1 nm");
        lblNoteGps.setBounds(280, 50, 122, 14);
        panel.add(lblNoteGps);

        JLabel lblSafetyFactorFs = new JLabel("Safety Factor, Fs:");
        lblSafetyFactorFs.setBounds(13, 75, 147, 14);
        panel.add(lblSafetyFactorFs);

        textField_6 = new JTextField();
        textField_6.setBounds(184, 72, 86, 20);
        panel.add(textField_6);
        textField_6.setColumns(10);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Search Object",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(20, 482, 487, 87);
        inputPanel.add(panel_1);
        panel_1.setLayout(null);

        JComboBox comboBox_3 = new JComboBox();
        comboBox_3.setModel(new DefaultComboBoxModel(
                new String[] { "Person in water (PIW)" }));
        comboBox_3.setBounds(10, 22, 457, 20);
        panel_1.add(comboBox_3);

        JLabel lblLeeway = new JLabel(
                "Leeway = 0.011 x U + 0.068, Divergence = 30");
        lblLeeway.setBounds(10, 53, 457, 14);
        panel_1.add(lblLeeway);

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

            // We're at SAR selection screen
            if (currentCard == SELECTSARTYPE) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                btnBack.setEnabled(true);

                // The type select determines which panel we show
                cl.show(masterPanel, INPUTSARRAPIDRESPONSE);
                currentCard = INPUTSARRAPIDRESPONSE;
            }

        }

        if (arg0.getSource() == btnBack) {

            // If we're at Rapid Response or Datum or Back back go back to init
            if (currentCard == INPUTSARRAPIDRESPONSE) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                cl.show(masterPanel, SELECTSARTYPE);
                btnBack.setEnabled(false);
                currentCard = SELECTSARTYPE;
            }

        }

        if (arg0.getSource() == cancelButton) {
            this.setVisible(false);
            voctManager.cancelSarOperation();
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
        JPanel pointXPanel = new JPanel();
        pointXPanel.setBorder(new TitledBorder(null, "Point " + (number + 1),
                TitledBorder.LEADING, TitledBorder.TOP, null, null));

//        int offset = 56 + (474 * number);
        int offset = 56 + (110 * number);
        System.out.println("Offset is " + offset);
        
        
        pointXPanel.setBounds(10, offset, 474, 99);
        pointXPanel.setLayout(null);

        JXDatePicker surfaceDriftPicker = new JXDatePicker();
        surfaceDriftPicker.setBounds(160, 22, 105, 20);
        pointXPanel.add(surfaceDriftPicker);

        surfaceDriftPicker.setFormats(format);

        JLabel lblsurfaceDriftTime = new JLabel("Date & Time of Surface Drift:");
        lblsurfaceDriftTime.setBounds(13, 25, 147, 14);
        pointXPanel.add(lblsurfaceDriftTime);

        Date date3 = new Date();
        SpinnerDateModel currentTimeModel3 = new SpinnerDateModel(date3, null,
                null, Calendar.HOUR_OF_DAY);

        JSpinner surfaceDriftSpinner = new JSpinner(currentTimeModel3);

        surfaceDriftSpinner.setLocation(268, 22);
        surfaceDriftSpinner.setSize(54, 20);
        JSpinner.DateEditor dateEditorSurfaceDrift = new JSpinner.DateEditor(
                surfaceDriftSpinner, "HH:mm");
        surfaceDriftSpinner.setEditor(dateEditorSurfaceDrift);

        pointXPanel.add(surfaceDriftSpinner);

        JLabel lblTWC = new JLabel("Total Water Current, knots:");
        lblTWC.setBounds(13, 50, 147, 14);
        pointXPanel.add(lblTWC);

        textField = new JTextField();
        textField.setBounds(160, 47, 33, 20);
        pointXPanel.add(textField);
        textField.setColumns(10);

        JLabel lblTwcVectorHeading = new JLabel(
                "TWC Vector, Heading or Degrees:");
        lblTwcVectorHeading.setBounds(203, 50, 173, 14);
        pointXPanel.add(lblTwcVectorHeading);

        JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(new String[] { "N", "NE",
                "NW", "S", "SW", "SE", "E", "W" }));
        comboBox.setBounds(372, 47, 33, 20);
        pointXPanel.add(comboBox);

        textField_1 = new JTextField();
        textField_1.setText("00.0°");
        textField_1.setBounds(415, 47, 47, 20);
        pointXPanel.add(textField_1);
        textField_1.setColumns(10);

        JLabel lblLeewayKnots = new JLabel("Leeway, knots:");
        lblLeewayKnots.setBounds(13, 78, 147, 14);
        pointXPanel.add(lblLeewayKnots);

        textField_2 = new JTextField();
        textField_2.setColumns(10);
        textField_2.setBounds(160, 75, 33, 20);
        pointXPanel.add(textField_2);

        JLabel lblLwVectorHeading = new JLabel("LW Vector, Heading or Degrees:");
        lblLwVectorHeading.setBounds(203, 78, 173, 14);
        pointXPanel.add(lblLwVectorHeading);

        JComboBox comboBox_2 = new JComboBox();
        comboBox_2.setModel(new DefaultComboBoxModel(new String[] { "N" }));
        comboBox_2.setBounds(372, 75, 33, 20);
        pointXPanel.add(comboBox_2);

        textField_3 = new JTextField();
        textField_3.setText("00.0°");
        textField_3.setColumns(10);
        textField_3.setBounds(415, 75, 47, 20);
        pointXPanel.add(textField_3);

        return pointXPanel;
    }
}
