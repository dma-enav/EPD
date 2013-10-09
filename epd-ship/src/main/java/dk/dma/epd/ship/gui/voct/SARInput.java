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
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.VOCTManager;

public class SARInput extends JDialog implements ActionListener,
        DocumentListener {

    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();

    private JComboBox<String> typeSelectionComboBox;
    private JButton nextButton;
    private JButton cancelButton;
    private JButton btnBack;

    private JLabel descriptiveImage;
    private JTextPane descriptiveText;

    private String rapidresponseTxt = "Rapid Response - Rapid Response should be used when the rescue vessel is within the designated search area in a relatively short timespan (1-2 hours after LKP).";
    private String datumPointTxt = "Datum Pont - Datum point is a calculation method used when the rescue vessel arrives to the designated search area after 2 or more hours after LKP";
    private String datumLineTxt = "Datum line - Datum line is used when an object is mising and a LKP is unkown but a assumed route is known";
    private String backtrackTxt = "Back track - Back track is used when a object has been located that is connected to the missing vessel. By reversing the objects movements a possible search area can be established";

    private ImageIcon rapidResponseIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/generic.png")));

    private ImageIcon datumPointIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/datumpoint.png")));

    private ImageIcon datumLineIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/datumline.png")));

    private ImageIcon backtrackIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/generic.png")));

    private VOCTManager voctManager;

    private JPanel masterPanel;

    private static final String SELECTSARTYPE = "Select SAR Type";
    private static final String INPUTSARRAPIDRESPONSEDATUM = "Rapid Response And Datum Input Panel";
    private static final String CALCULATIONSPANELRAPIDRESPONSE = "Rapid Response Calculations Panel";

    // First card shown is the select sar type
    private String currentCard = SELECTSARTYPE;


    
    private JLabel calculationsText = new JLabel();
    private RapidResponseDatumPointInputPanel rapidResponseDatumPointInputPanel;

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

      

        // setBounds(100, 100, 559, 733);
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

        rapidResponseDatumPointInputPanel = new RapidResponseDatumPointInputPanel();

        JScrollPane rapidResponseScrollPanel = new JScrollPane(
                rapidResponseDatumPointInputPanel);
        rapidResponseScrollPanel.setPreferredSize(new Dimension(559, 363));

        masterPanel.add(rapidResponseScrollPanel, INPUTSARRAPIDRESPONSEDATUM);

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
        // descriptiveText.setBackground(UIManager.getColor("Button.background"));
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
                nextButton.setEnabled(true);
                break;
            case 1:
                descriptiveImage.setIcon(datumPointIcon);
                descriptiveText.setText(datumPointTxt);
                nextButton.setEnabled(true);
                break;
            case 2:
                descriptiveImage.setIcon(datumLineIcon);
                descriptiveText.setText(datumLineTxt);
                nextButton.setEnabled(false);
                break;
            case 3:
                descriptiveImage.setIcon(backtrackIcon);
                descriptiveText.setText(backtrackTxt);
                nextButton.setEnabled(false);
                break;
            }

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
                cl.show(masterPanel, currentCard);
                
                return;
            }

            // We're at input screen
            if (currentCard == INPUTSARRAPIDRESPONSEDATUM) {
                updateValues();
                CardLayout cl = (CardLayout) (masterPanel.getLayout());

                if (validateInputAndInititate()) {
                    calculationsText.setText(voctManager.getSarData()
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
                cl.show(masterPanel, INPUTSARRAPIDRESPONSEDATUM);
                currentCard = INPUTSARRAPIDRESPONSEDATUM;

                System.out.println("Hiding");

                // Display SAR command
                voctManager.displaySar();

                this.setVisible(false);
                return;
            }

        }

        if (arg0.getSource() == btnBack) {

            // If we're at Rapid Response or Datum or Back back go back to init
            if (currentCard == INPUTSARRAPIDRESPONSEDATUM) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                cl.show(masterPanel, SELECTSARTYPE);
                btnBack.setEnabled(false);
                currentCard = SELECTSARTYPE;
                return;
            }

            // We're at confirmation
            if (currentCard == CALCULATIONSPANELRAPIDRESPONSE) {
                CardLayout cl = (CardLayout) (masterPanel.getLayout());
                cl.show(masterPanel, INPUTSARRAPIDRESPONSEDATUM);
                btnBack.setEnabled(true);
                nextButton.setText("Next");
                currentCard = INPUTSARRAPIDRESPONSEDATUM;
                return;
            }

        }

        if (arg0.getSource() == cancelButton) {
            this.setVisible(false);
            voctManager.cancelSarOperation();
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

    private void updateValues() {

        // voctManager

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
            rapidResponseDatumPointInputPanel.setSARType(SAR_TYPE.RAPID_RESPONSE);
            currentCard = INPUTSARRAPIDRESPONSEDATUM;
            break;
        case 1:
            voctManager.setSarType(SAR_TYPE.DATUM_POINT);
            rapidResponseDatumPointInputPanel.setSARType(SAR_TYPE.DATUM_POINT);
            currentCard = INPUTSARRAPIDRESPONSEDATUM;
            break;
        case 2:
            voctManager.setSarType(SAR_TYPE.DATUM_LINE);
//            currentCard = INPUTSARRAPIDRESPONSEDATUM;
            break;
        case 3:
            voctManager.setSarType(SAR_TYPE.BACKTRACK);
            currentCard = INPUTSARRAPIDRESPONSEDATUM;
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
            return validateRapidResponse();
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
        double rapidResponseLKPLat = rapidResponseDatumPointInputPanel.getRapidResponseDatumLKPLat();
        double rapidResponseLKPLon = rapidResponseDatumPointInputPanel.getRapidResponseDatumLKPLon();

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

        System.out.println("LKP Date is " + rapidResponseDatumPointInputPanel.getLKPDate());
        System.out.println("CSS Date is " + rapidResponseDatumPointInputPanel.getCSSDate());

        // Time and date will be automatically sorted

        // Get weather
        SurfaceDriftPanel firstPanel = rapidResponseDatumPointInputPanel.getSurfaceDriftPanelList().get(0);

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

        SARWeatherData sarWeatherData = new SARWeatherData(twcHeading,
                TWCKnots, leewayKnots, leewayHeading);

        List<SARWeatherData> sarWeatherDataPoints = new ArrayList<SARWeatherData>();
        sarWeatherDataPoints.add(sarWeatherData);

        double xError = rapidResponseDatumPointInputPanel.getInitialPositionError();

        if (xError == -9999) {
            // Error message is handled within function
            return false;
        }

        double yError = rapidResponseDatumPointInputPanel.getNavError();

        if (yError == -9999) {
            // Error message is handled within function
            return false;
        }

        double safetyFactor = rapidResponseDatumPointInputPanel.getSafetyFactor();

        if (safetyFactor == -9999) {
            // Error message is handled within function
            return false;
        }

        int searchObject = rapidResponseDatumPointInputPanel.getSearchItemID();

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

        if (!rapidResponseDatumPointInputPanel.checkTime()) {
            return false;
        }

        if (!rapidResponseDatumPointInputPanel.checkMetocTime()) {
            return false;
        }
        
        

        voctManager.inputRapidResponseDatumData(rapidResponseDatumPointInputPanel.getSARID(), rapidResponseDatumPointInputPanel.getLKPDate(),
                rapidResponseDatumPointInputPanel.getCSSDate(), rapidResponsePosition, xError, yError, safetyFactor,
                searchObject, sarWeatherDataPoints);

        return true;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // TODO Auto-generated method stub

    }

}
