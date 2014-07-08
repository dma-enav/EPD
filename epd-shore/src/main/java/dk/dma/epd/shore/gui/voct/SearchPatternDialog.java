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
package dk.dma.epd.shore.gui.voct;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.gui.voct.SearchPatternDialogCommon;
import dk.dma.epd.common.prototype.model.voct.SearchPatternGenerator;
import dk.dma.epd.common.prototype.voct.VOCTManagerCommon;
import dk.dma.epd.common.util.ParseUtils;

public class SearchPatternDialog extends SearchPatternDialogCommon{

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField cssFirstLat;
    private JTextField cssSecondLat;
    private JTextField cssThirdLat;
    private JTextField cssThirdLon;
    private JTextField cssSecondLon;
    private JTextField cssFirstLon;

    private JComboBox<String> typeSelectionComboBox;

    private JComboBox<String> comboCSSLon;
    private JComboBox<String> comboCSSLat;
    private JComboBox<String> importCSPFromDropdown;

    private JLabel descriptiveImage;
    private JTextPane descriptiveText;

    private VOCTManagerCommon voctManager;

    private JButton generateButton;
    private JButton cancelButton;

    ImageIcon parallelsweepsearchIcon = scaleImage(new ImageIcon(SearchPatternDialogCommon.class
            .getClassLoader()
            .getResource("images/voct/parallelsweepsearch.png")));

    ImageIcon creepingLineSearchIcon = scaleImage(new ImageIcon(SearchPatternDialogCommon.class
            .getClassLoader().getResource("images/voct/creepinglinesearch.png")));

    ImageIcon trackLineSearchReturnIcon = scaleImage(new ImageIcon(
            SearchPatternDialogCommon.class.getClassLoader().getResource(
                    "images/voct/tracklinesearchreturn.png")));

    ImageIcon trackLineSearchNonReturnIcon = scaleImage(new ImageIcon(
            SearchPatternDialogCommon.class.getClassLoader().getResource(
                    "images/voct/tracklinesearchnonreturn.png")));

    ImageIcon expandingSquareSearchIcon = scaleImage(new ImageIcon(
            SearchPatternDialogCommon.class.getClassLoader().getResource(
                    "images/voct/expandingsquaresearch.png")));

    String parallelSweepSearch = "Parallel Sweep Search is used when the search area is large, an even coverage is wanted, exact location of the object is unknown. eg. datum has low confidence";
    String creepingLineSearch = "Creeping Line Search is used when the search is long and narrow, the most likely position of the search object is presumed to be between two points, it is desirable with a fast coverage of the most likely area first";
    String trackLineSearchReturn = "Track Line Search, return is used when a vessel (or person) is reported as missing and the only track is the presumed route. It gives a relative fast and thorough coverage of the missing objects presumed route and its adjacent areas The route begins and ends in the same end of the route";
    String trackLineSearchNonReturn = "Track Line Search, non-return is used when a vessel (or person) is reported as missing and the only track is the presumed route. It gives a relative fast and thorough coverage of the missing objects presumed route and its adjacent areas The route begins and ends in the opposite end of the route";
    String expandingSquareSearch = "Expanding Square Search is used when the search object is presumed to be located within a relatively small area. The search begins in the most likely position (Datum). This method requires accurate navigation.";

    
    int currentID;
    
    /**
     * Create the dialog.
     */
    public SearchPatternDialog() {
        setTitle("Generate Search Pattern");
        setResizable(false);
        setBounds(100, 100, 548, 478);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JPanel cspPanel = new JPanel();
        cspPanel.setBorder(new TitledBorder(null,
                "Search Pattern Start Position", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        cspPanel.setBounds(10, 11, 522, 108);
        contentPanel.add(cspPanel);
        cspPanel.setLayout(null);
        {
            JLabel label = new JLabel("Commence Start Position:");
            label.setBounds(10, 52, 127, 14);
            cspPanel.add(label);
        }
        {
            cssFirstLat = new JTextField();
            cssFirstLat.setText("56");
            cssFirstLat.setColumns(10);
            cssFirstLat.setBounds(140, 49, 20, 20);
            cspPanel.add(cssFirstLat);
        }
        {
            cssSecondLat = new JTextField();
            cssSecondLat.setText("20");
            cssSecondLat.setColumns(10);
            cssSecondLat.setBounds(160, 49, 20, 20);
            cspPanel.add(cssSecondLat);
        }
        {
            cssThirdLat = new JTextField();
            cssThirdLat.setText("000");
            cssThirdLat.setColumns(10);
            cssThirdLat.setBounds(180, 49, 30, 20);
            cspPanel.add(cssThirdLat);
        }
        {
            comboCSSLat = new JComboBox<String>();
            comboCSSLat.setBounds(210, 49, 30, 20);
            comboCSSLat.setModel(new DefaultComboBoxModel<String>(new String[] {
                    "N", "S" }));
            cspPanel.add(comboCSSLat);
        }
        {
            cssThirdLon = new JTextField();
            cssThirdLon.setText("000");
            cssThirdLon.setColumns(10);
            cssThirdLon.setBounds(288, 49, 30, 20);
            cspPanel.add(cssThirdLon);
        }
        {
            comboCSSLon = new JComboBox<String>();
            comboCSSLon.setBounds(318, 49, 30, 20);
            comboCSSLon.setModel(new DefaultComboBoxModel<String>(new String[] {
                    "E", "W" }));
            cspPanel.add(comboCSSLon);
        }
        {
            cssSecondLon = new JTextField();
            cssSecondLon.setText("00");
            cssSecondLon.setColumns(10);
            cssSecondLon.setBounds(268, 49, 20, 20);
            cspPanel.add(cssSecondLon);
        }
        {
            cssFirstLon = new JTextField();
            cssFirstLon.setText("12");
            cssFirstLon.setColumns(10);
            cssFirstLon.setBounds(248, 49, 20, 20);
            cspPanel.add(cssFirstLon);
        }
        {
            JLabel lblToGenerateA = new JLabel(
                    "To generate a search pattern you need to choose a starting point.");
            lblToGenerateA.setBounds(10, 14, 398, 14);
            cspPanel.add(lblToGenerateA);
        }
        {
            JLabel lblTheWidthAnd = new JLabel(
                    "The width and height of the effective area box will be used to determine width and height of the pattern");
            lblTheWidthAnd.setBounds(10, 28, 512, 14);
            cspPanel.add(lblTheWidthAnd);
        }
        {
            importCSPFromDropdown = new JComboBox<String>();
            importCSPFromDropdown.setModel(new DefaultComboBoxModel<String>(
                    new String[] { "Import From:", "Top Left", "Top Right",
                            "Bottom Left", "Bottom Right" }));
            importCSPFromDropdown.setBounds(385, 49, 127, 20);
            importCSPFromDropdown.addActionListener(this);
            cspPanel.add(importCSPFromDropdown);
        }

        JPanel typeSelectPanel = new JPanel();
        typeSelectPanel.setBorder(new TitledBorder(null, "Search Pattern Type",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        typeSelectPanel.setBounds(10, 123, 522, 283);
        contentPanel.add(typeSelectPanel);
        typeSelectPanel.setLayout(null);
        {
            JLabel lblSelectSearchPattern = new JLabel("Select Search Pattern");
            lblSelectSearchPattern.setBounds(10, 18, 124, 14);
            typeSelectPanel.add(lblSelectSearchPattern);
        }
        {
            typeSelectionComboBox = new JComboBox<String>();
            typeSelectionComboBox.setModel(new DefaultComboBoxModel<String>(
                    new String[] { "Parallel Sweep Search",
                            "Creeping Line Search", "Track Line Search",
                            "Track Line Search, non-return",
                            "Expanding Square Search" }));
            typeSelectionComboBox.setBounds(144, 15, 152, 20);
            typeSelectionComboBox.addActionListener(this);
            typeSelectPanel.add(typeSelectionComboBox);
        }
        {
            descriptiveText = new JTextPane();
            descriptiveText.setBounds(10, 46, 502, 43);
            descriptiveText.setText(parallelSweepSearch);
            descriptiveText.setEditable(false);
            descriptiveText.setOpaque(false);
            typeSelectPanel.add(descriptiveText);
        }

        descriptiveImage = new JLabel(" ");
        descriptiveImage.setHorizontalAlignment(SwingConstants.CENTER);
        descriptiveImage.setBounds(82, 100, 372, 172);
        typeSelectPanel.add(descriptiveImage);
        descriptiveImage.setIcon(parallelsweepsearchIcon);

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                generateButton = new JButton("Generate");
                generateButton.addActionListener(this);
                buttonPane.add(generateButton);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                buttonPane.add(cancelButton);
            }
        }
    }

    public boolean getValuesAndInititate() {
        SearchPatternGenerator.searchPattern searchPatternType = SearchPatternGenerator.searchPattern.unknown;

        // commenceStartPosition

        int selectedIndex = typeSelectionComboBox.getSelectedIndex();
        // 0 Parallel Sweep Search
        // 1 Creeping Line Search
        // 2 Track Line Search
        // 3 Track Line Search, non-return
        // 4 Expanding Square Search

        switch (selectedIndex) {
        case 0:
            searchPatternType = SearchPatternGenerator.searchPattern.Parallel_Sweep_Search;
            break;
        case 1:
            searchPatternType = SearchPatternGenerator.searchPattern.Creeping_Line_Search;
            break;
        case 2:
            searchPatternType = SearchPatternGenerator.searchPattern.Track_Line_Search;
            break;
        case 3:
            searchPatternType = SearchPatternGenerator.searchPattern.Track_Line_Search_nonreturn;
            break;
        case 4:
            searchPatternType = SearchPatternGenerator.searchPattern.Expanding_Square_Search;
            break;
        }

        Position commenceStartPosition = null;

        if (searchPatternType != SearchPatternGenerator.searchPattern.Expanding_Square_Search) {

            // Get CSS values
            double commenceSearchStartLat = getCSSLat();
            double commenceSearchStartLon = getCSSLon();

            if (commenceSearchStartLat != -9999
                    && commenceSearchStartLon != -9999) {
                commenceStartPosition = Position.create(commenceSearchStartLat,
                        commenceSearchStartLon);
            } else {
                // Failed to parse CSP
                displayMissingField("Commence Start Position");
                return false;
            }

        }

        // Create the route
        voctManager.generateSearchPattern(searchPatternType,
                commenceStartPosition, currentID);

        return true;

    }

    private double getCSSLat() {
        String LKPLatitude = cssFirstLat.getText() + " "
                + cssSecondLat.getText() + "." + cssThirdLat.getText()
                + comboCSSLat.getSelectedItem();

        try {
            return parseLat(LKPLatitude);
        } catch (Exception e1) {
            // displayMissingField("CSP Latitude");
        }

        return -9999;

    }

    private double getCSSLon() {
        String LKPLongitude = cssFirstLon.getText() + " "
                + cssSecondLon.getText() + "." + cssThirdLon.getText()
                + comboCSSLon.getSelectedItem();

        System.out.println(LKPLongitude);

        try {
            return parseLon(LKPLongitude);
        } catch (Exception e1) {

        }

        return -9999;

    }

    private static double parseLon(String lonStr) throws FormatException {
        return ParseUtils.parseLongitude(lonStr);
    }

    private static double parseLat(String latStr) throws FormatException {
        return ParseUtils.parseLatitude(latStr);
    }

    private static ImageIcon scaleImage(ImageIcon icon) {
        // Scale it?
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(281, 171,
                java.awt.Image.SCALE_SMOOTH);

        ImageIcon newIcon = new ImageIcon(newimg);

        return newIcon;
    }

    @Override
    public void setVoctManager(VOCTManagerCommon voctManager) {
        this.voctManager = voctManager;
    }

    public void resetValues(int id) {
        this.currentID = id;
        
        System.out.println("CURRENT ID: " + currentID);
        
        cssFirstLat.setText("");
        cssSecondLat.setText("");
        cssThirdLat.setText("");
        comboCSSLat.setSelectedIndex(0);

        cssFirstLon.setText("");
        cssSecondLon.setText("");
        cssThirdLon.setText("");
        comboCSSLon.setSelectedIndex(0);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == typeSelectionComboBox) {
            int selectedIndex = typeSelectionComboBox.getSelectedIndex();
            // 0 Parallel Sweep Search
            // 1 Creeping Line Search
            // 2 Track Line Search
            // 3 Track Line Search, non-return
            // 4 Expanding Square Search

            switch (selectedIndex) {
            case 0:
                toggleInput(true);
                generateButton.setEnabled(true);
                descriptiveImage.setIcon(parallelsweepsearchIcon);
                descriptiveText.setText(parallelSweepSearch);
                break;
            case 1:
                toggleInput(true);
                generateButton.setEnabled(true);
                descriptiveImage.setIcon(creepingLineSearchIcon);
                descriptiveText.setText(creepingLineSearch);
                break;
            case 2:
                toggleInput(false);
                generateButton.setEnabled(false);
                descriptiveImage.setIcon(trackLineSearchReturnIcon);
                descriptiveText.setText(trackLineSearchReturn);
                break;
            case 3:
                toggleInput(false);
                generateButton.setEnabled(false);
                descriptiveImage.setIcon(trackLineSearchNonReturnIcon);
                descriptiveText.setText(trackLineSearchNonReturn);
                break;

            case 4:
                toggleInput(false);
                generateButton.setEnabled(true);
                descriptiveImage.setIcon(expandingSquareSearchIcon);
                descriptiveText.setText(expandingSquareSearch);
                break;
            }
            return;
        }

        if (arg0.getSource() == generateButton) {

            if (getValuesAndInititate()) {
                this.setVisible(false);
                return;
            }

        }

        if (arg0.getSource() == cancelButton) {
            this.setVisible(false);
            return;
        }

        if (arg0.getSource() == importCSPFromDropdown) {
            setValues(importCSPFromDropdown.getSelectedIndex());
        }
    }

    
    private void toggleInput(boolean enabled){
        cssFirstLat.setEnabled(enabled);
        cssSecondLat.setEnabled(enabled);
        cssThirdLat.setEnabled(enabled);

        comboCSSLat.setEnabled(enabled);
        cssFirstLon.setEnabled(enabled);
        cssSecondLon.setEnabled(enabled);
        cssThirdLon.setEnabled(enabled);
        comboCSSLon.setEnabled(enabled);
        importCSPFromDropdown.setEnabled(enabled);


        
    }

    
    private void setValues(int position) {
        Position importedPosition = null;

        switch (position) {
        case 0:
            return;
        case 1:
            importedPosition = voctManager.getSarData().getEffortAllocationData().get(currentID)
                    .getEffectiveAreaA();
            break;
        case 2:
            importedPosition = voctManager.getSarData().getEffortAllocationData().get(currentID).getEffectiveAreaB();
            break;
        case 3:
            importedPosition = voctManager.getSarData().getEffortAllocationData().get(currentID).getEffectiveAreaC();
            break;
        case 4:
            importedPosition =voctManager.getSarData().getEffortAllocationData().get(currentID).getEffectiveAreaD();
            break;
        }

        String lat = importedPosition.getLatitudeAsString();

        System.out.println("Lat is" + lat);

        // Get last character

        String latValues = lat.substring(0, lat.length() - 1);
        System.out.println("Lat values is: " + latValues);

        String firstSplit = latValues.split(" ")[1].trim();

        System.out.println("Split space 1 is " + firstSplit);

        System.out.println(firstSplit.contains("."));

        for (int i = 0; i < firstSplit.split("\\.").length; i++) {
            System.out.println("Splitted val is" + firstSplit.split("\\.")[i]);
        }

        String firstValueLat = latValues.split(" ")[0].trim();
        String secondValueLat = firstSplit.split("\\.")[0].trim();
        String thirdValueLat = firstSplit.split("\\.")[1].trim();
        String lastCharLat = lat.substring(lat.length() - 1, lat.length())
                .trim();

        cssFirstLat.setText(firstValueLat);
        cssSecondLat.setText(secondValueLat);
        cssThirdLat.setText(thirdValueLat);
        if (lastCharLat.equals("N")) {
            comboCSSLat.setSelectedIndex(0);
        } else {
            comboCSSLat.setSelectedIndex(1);
        }

        String lon = importedPosition.getLongitudeAsString();

        String lonValues = lon.substring(0, lat.length() - 1);

        String firstValueLon = lonValues.split(" ")[0].trim();
        String secondValueLon = lonValues.split(" ")[1].split("\\.")[0].trim();
        String thirdValueLon = lonValues.split(" ")[1].split("\\.")[1].trim();
        String lastCharLon = lon.substring(lon.length() - 1, lon.length())
                .trim();

        cssFirstLon.setText(firstValueLon);
        cssSecondLon.setText(secondValueLon);
        cssThirdLon.setText(thirdValueLon);

        if (lastCharLon.equals("E")) {
            comboCSSLat.setSelectedIndex(0);
        } else {
            comboCSSLat.setSelectedIndex(1);
        }

        comboCSSLon.setSelectedIndex(0);
    }

    private void displayMissingField(String fieldname) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, "Missing or incorrect value in "
                + fieldname, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

}

