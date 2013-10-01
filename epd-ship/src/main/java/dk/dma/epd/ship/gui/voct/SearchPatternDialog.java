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
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultComboBoxModel;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.util.ParseUtils;
import dk.dma.epd.ship.EPDShip;

import javax.swing.JTextPane;
import java.awt.SystemColor;

public class SearchPatternDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField cssFirstLat;
    private JTextField cssSecondLat;
    private JTextField cssThirdLat;
    private JTextField cssThirdLon;
    private JTextField cssSecondLon;
    private JTextField cssFirstLon;

    
    private JComboBox<String> comboCSSLon;
    JComboBox<String> comboCSSLat;
    private JLabel descriptiveImage;
    
    
    ImageIcon parallelsweepsearchIcon = scaleImage(new ImageIcon(EPDShip.class
            .getClassLoader().getResource("images/voct/parallelsweepsearch.png")));

    
    String parallelSweepSearch = "Parallel Sweep search is used when the search area is large, an even coverage is wanted, exact location of the object is unknown. eg. datum has low confidence";
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SearchPatternDialog dialog = new SearchPatternDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public SearchPatternDialog() {
        setTitle("Generate Search Pattern");
        setResizable(false);
        setBounds(100, 100, 548, 393);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        
        JPanel cspPanel = new JPanel();
        cspPanel.setBorder(new TitledBorder(null, "Search Pattern Start Position", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
            JLabel lblToGenerateA = new JLabel("To generate a search pattern you need to choose a starting point.");
            lblToGenerateA.setBounds(10, 14, 398, 14);
            cspPanel.add(lblToGenerateA);
        }
        {
            JLabel lblTheWidthAnd = new JLabel("The width and height of the effective area box will be used to determine width and height of the pattern");
            lblTheWidthAnd.setBounds(10, 28, 512, 14);
            cspPanel.add(lblTheWidthAnd);
        }
        {
            JComboBox comboBox = new JComboBox();
            comboBox.setModel(new DefaultComboBoxModel(new String[] {"Select From:", "Top Left", "Top Right", "Bottom Left", "Bottom Right"}));
            comboBox.setBounds(385, 49, 127, 20);
            cspPanel.add(comboBox);
        }
        
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Search Pattern Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(10, 123, 522, 198);
        contentPanel.add(panel_1);
        panel_1.setLayout(null);
        {
            JLabel lblSelectSearchPattern = new JLabel("Select Search Pattern");
            lblSelectSearchPattern.setBounds(10, 18, 124, 14);
            panel_1.add(lblSelectSearchPattern);
        }
        {
            JComboBox<String> comboBox = new JComboBox<String>();
            comboBox.setModel(new DefaultComboBoxModel(new String[] {"Parallel Sweep Search", "Creeping Line Search", "Track Line Search", "Track Line Search, non-return", "Expanding Square Search"}));
            comboBox.setBounds(144, 15, 152, 20);
            panel_1.add(comboBox);
        }
        {
            JTextPane textPane = new JTextPane();
            textPane.setBounds(10, 46, 502, 54);
            textPane.setText(parallelSweepSearch);
            textPane.setEditable(false);
            textPane.setBackground(SystemColor.menu);
            panel_1.add(textPane);
        }
        
            descriptiveImage = new JLabel(" ");
            descriptiveImage.setBounds(144, 85, 246, 102);
            panel_1.add(descriptiveImage);
            descriptiveImage.setIcon(parallelsweepsearchIcon);
            
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
    
    
    
    //Not implemented yet
    public boolean getValues(){
        // Get CSS values
        double commenceSearchStartLat = getRapidResponseCSSLat();
        double commenceSearchStartLon = getRapidResponseCSSLon();

        Position commenceStartPosition;

        if (commenceSearchStartLat != -9999 && commenceSearchStartLon != -9999) {
            commenceStartPosition = Position.create(commenceSearchStartLat,
                    commenceSearchStartLon);
        } else {
            System.out.println("Failed lon");
            System.out.println(commenceSearchStartLat);
            System.out.println(commenceSearchStartLon);
            // msgbox
            return false;
        }
        
        return false;

    }
    
    private double getRapidResponseCSSLat() {
        String LKPLatitude = cssFirstLat.getText() + " "
                + cssSecondLat.getText() + "." + cssThirdLat.getText()
                + comboCSSLat.getSelectedItem();

        try {
            return parseLat(LKPLatitude);
        } catch (Exception e1) {
            // Invalid lon, we do nothing, focus lost will handle it
        }

        return -9999;

    }

    private double getRapidResponseCSSLon() {
        String LKPLongitude = cssFirstLon.getText() + " "
                + cssSecondLon.getText() + "." + cssThirdLon.getText()
                + comboCSSLon.getSelectedItem();

        System.out.println(LKPLongitude);

        try {
            return parseLon(LKPLongitude);
        } catch (Exception e1) {
            // Invalid lon, we do nothing, focus lost will handle it
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
        Image newimg = img.getScaledInstance(239, 145,
                java.awt.Image.SCALE_SMOOTH);

        ImageIcon newIcon = new ImageIcon(newimg);

        return newIcon;
    }

}
