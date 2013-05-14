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
package dk.dma.epd.ship.gui.monalisa;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.ChartPanel;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.route.RouteManager;

/**
 * The nogo dialog
 */
public class MonaLisaOptionsDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JButton requestOptiBtn;
    private JButton cancelButton;
    private JButton selectWpBtn;
    JSpinner spinnerDraught;

    JLabel totalWpLbl;
    JLabel selectWpLbl;

    private JTextField serverTxtField;
    private JTextField portTxtField;
    private JTextField timeoutTxTField;
    private JTextField ukctextField;

    JComboBox<String> routeDropDown;

    private List<Boolean> selectedWp;

    ChartPanel chartPanel;
    RouteManager routeManager;
    MainFrame mainFrame;
    AisHandler aisHandler;

    JCheckBox showOutPutCheckBox;
    JCheckBox showInputCheckBox;
    JCheckBox intermediateETACheckBox;
    private boolean loading;

    int routeid;

    public MonaLisaOptionsDialog(JFrame parent, RouteManager routeManager,
            AisHandler aisHandler) {
        super(parent, "Request Mona Lisa Route Exchange", true);

        mainFrame = (MainFrame) parent;

        this.chartPanel = mainFrame.getChartPanel();
        this.routeManager = routeManager;
        this.aisHandler = aisHandler;

        setSize(366, 505);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel contentPanel = new JPanel();

        getContentPane().setLayout(new BorderLayout());

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"), "Route Selection",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(15, 30, 327, 115);

        JLabel lblNogoRequest = new JLabel("Mona Lisa Route Exchange");
        lblNogoRequest.setBounds(15, 5, 238, 14);
        lblNogoRequest.setFont(new Font("Tahoma", Font.BOLD, 11));
        contentPanel.setLayout(null);
        panel.setLayout(null);
        contentPanel.add(panel);

        routeDropDown = new JComboBox<String>();
        routeDropDown.setBounds(10, 20, 134, 20);
        panel.add(routeDropDown);
        routeDropDown.addActionListener(this);

        JLabel lblName = new JLabel("Total Waypoints:");
        lblName.setBounds(10, 51, 90, 14);
        panel.add(lblName);

        totalWpLbl = new JLabel("N/A");
        totalWpLbl.setBounds(120, 51, 46, 14);
        panel.add(totalWpLbl);

        JLabel lblSize = new JLabel("Selected Waypoints:");
        lblSize.setBounds(10, 74, 100, 14);
        panel.add(lblSize);

        selectWpLbl = new JLabel("N/A");
        selectWpLbl.setBounds(120, 74, 46, 14);
        panel.add(selectWpLbl);

        selectWpBtn = new JButton("Select waypoints");
        selectWpBtn.setBounds(176, 17, 113, 23);
        selectWpBtn.addActionListener(this);

        panel.add(selectWpBtn);
        contentPanel.add(lblNogoRequest);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"), "Optimization Options",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_1.setBounds(15, 156, 327, 128);
        contentPanel.add(panel_1);
        panel_1.setLayout(null);

        intermediateETACheckBox = new JCheckBox("Remove Intermediate ETAs");
        intermediateETACheckBox.setBounds(6, 24, 157, 23);
        panel_1.add(intermediateETACheckBox);

        JLabel lblNewLabel = new JLabel("Draft:");
        lblNewLabel.setBounds(10, 56, 38, 16);
        panel_1.add(lblNewLabel);

        spinnerDraught = new JSpinner();
        spinnerDraught.setBounds(43, 54, 38, 20);
        panel_1.add(spinnerDraught);
        spinnerDraught.setModel(new SpinnerNumberModel(new Integer(5),
                new Integer(0), null, new Integer(1)));

        JLabel lblUkc = new JLabel("UKC");
        lblUkc.setBounds(10, 84, 38, 14);
        panel_1.add(lblUkc);

        ukctextField = new JTextField();
        ukctextField.setBounds(43, 83, 38, 20);
        panel_1.add(ukctextField);
        ukctextField.setColumns(10);
        ukctextField.setText("1");

        JFormattedTextField txt = ((JSpinner.NumberEditor) spinnerDraught
                .getEditor()).getTextField();

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "Debug", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        panel_2.setBounds(15, 295, 327, 138);
        contentPanel.add(panel_2);
        panel_2.setLayout(null);

        JLabel lblServer = new JLabel("Server:");
        lblServer.setBounds(10, 11, 46, 14);
        panel_2.add(lblServer);

        serverTxtField = new JTextField();
        serverTxtField.setBounds(95, 8, 217, 20);
        panel_2.add(serverTxtField);
        serverTxtField.setColumns(10);
        serverTxtField.setText(EPDShip.getSettings().getEnavSettings().getMonaLisaServer());
        serverTxtField.setEditable(false);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(10, 36, 46, 14);
        panel_2.add(lblPort);
        
        portTxtField = new JTextField();
        portTxtField.setBounds(95, 33, 39, 20);
        panel_2.add(portTxtField);
        portTxtField.setColumns(10);
        portTxtField.setText(String.valueOf(EPDShip.getSettings().getEnavSettings().getMonaLisaPort()));
        portTxtField.setEditable(false);

        JLabel lblTimeout = new JLabel("Timeout in ms:");
        lblTimeout.setBounds(10, 61, 74, 14);
        panel_2.add(lblTimeout);

        timeoutTxTField = new JTextField();
        timeoutTxTField.setBounds(94, 58, 58, 20);
        panel_2.add(timeoutTxTField);
        timeoutTxTField.setColumns(10);
        timeoutTxTField.setText("60000");
        
        showOutPutCheckBox = new JCheckBox("Show XML output");
        showOutPutCheckBox.setBounds(6, 80, 128, 23);
        panel_2.add(showOutPutCheckBox);

        showInputCheckBox = new JCheckBox("Show XML input");
        showInputCheckBox.setBounds(6, 106, 110, 23);
        panel_2.add(showInputCheckBox);
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                requestOptiBtn = new JButton("Request Optimizaton");
                requestOptiBtn.addActionListener(this);

                buttonPane.add(requestOptiBtn);
                getRootPane().setDefaultButton(requestOptiBtn);
            }
            {
                cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(this);
                buttonPane.add(cancelButton);
            }
        }

        if (aisHandler != null
                && aisHandler.getOwnShip().getStaticData() != null) {
            Integer draught = (int) (aisHandler.getOwnShip().getStaticData()
                    .getDraught() / 10);
            spinnerDraught.setValue(draught);
        }

        

        
    }

    public void showDialog(int routeid) {
        // this.route = route;
        this.routeid = routeid;
        loadData();
        setVisible(true);
    }

    private void loadData() {

        loading = true;

        selectedWp = new ArrayList<Boolean>();
        routeDropDown.removeAllItems();
        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            routeDropDown.addItem(routeManager.getRoutes().get(i).getName()
                    + "                                                 " + i);
        }
        routeDropDown.setSelectedIndex(routeid);
        totalWpLbl.setText(String.valueOf(routeManager.getRoute(routeid)
                .getWaypoints().size()));
        for (int i = 0; i < routeManager.getRoute(routeid).getWaypoints()
                .size(); i++) {
            selectedWp.add(true);
        }

        selectWpLbl.setText(String.valueOf(selectedWp.size()));

        loading = false;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == routeDropDown && !loading) {
            routeid = routeDropDown.getSelectedIndex();
            loadData();
        }

        if (e.getSource() == requestOptiBtn) {
            if (selectWpLbl.getText().equals("0") || selectWpLbl.getText().equals("1")){
                JOptionPane.showMessageDialog(this,"You must select some waypoints");
            }
            
            //Which WP to send
//            selectedWp;
            //The route
//            routeid
       
            
            boolean removeIntermediateETA = intermediateETACheckBox.isSelected();
            try {
                float draft = Float.parseFloat(spinnerDraught.getValue().toString());
                int ukc = Integer.parseInt(ukctextField.getText());
                int timeout = Integer.parseInt(timeoutTxTField.getText());
                
                
                
                System.out.println("Creating mona lisa request with the following data: ");
                System.out.println("Remove ETA? " + removeIntermediateETA);
                System.out.println("Draft: " + draft);
                System.out.println("UKC "  + ukc);
                System.out.println("Timeout " + timeout);
                
                
                this.dispose();
                
                // Send off the request
              MonaLisaRequestDialog.requestRoute(EPDShip.getMainFrame(), routeManager,
                      routeManager.getRoute(routeid), EPDShip.getMonaLisaRouteExchange(),removeIntermediateETA , draft, ukc, timeout, selectedWp
                      ,showInputCheckBox.isSelected(), showOutPutCheckBox.isSelected());
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(this,"Invalid integer input");
            }

            
            


        }
        if (e.getSource() == cancelButton) {
            // Cancel the request
            this.dispose();
        }
        if (e.getSource() == selectWpBtn) {
            MonaLisaWPSelection selectionDialog = new MonaLisaWPSelection(this,
                    routeManager, selectedWp, routeid);
            selectionDialog.setVisible(true);
        }
    }
    
    public void resetSelected(){
        for (int i = 0; i < routeManager.getRoute(routeid).getWaypoints()
                .size(); i++) {
            selectedWp.add(true);
        }
    }

    public void updateSelected() {
        int selected = 0;
        
        for (int i = 0; i < selectedWp.size(); i++) {
            if (selectedWp.get(i)){
                selected++;
            }
        }
        
        selectWpLbl.setText(String.valueOf(selected));
    }
}
