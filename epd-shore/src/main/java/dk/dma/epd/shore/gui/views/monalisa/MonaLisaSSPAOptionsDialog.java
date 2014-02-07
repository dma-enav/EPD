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
package dk.dma.epd.shore.gui.views.monalisa;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
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

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.monalisa.MonaLisaSSPAWPSelection;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.layers.voyage.VoyageHandlingLayer;

/**
 * The nogo dialog
 */
public class MonaLisaSSPAOptionsDialog extends
        dk.dma.epd.common.prototype.monalisa.MonaLisaSSPAOptionsDialogCommon
        implements ActionListener {
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

    private List<Boolean> selectedWp;

    ChartPanel chartPanel;
    AisHandlerCommon aisHandler;
    JCheckBox intermediateETACheckBox;

    VoyageHandlingLayer voyageHandlingLayer;
    // private long mmsi;

    private Route route;
    private JLabel routeNameLbl;
    private JLabel routeNameTxt;

    public MonaLisaSSPAOptionsDialog(Route route,
            VoyageHandlingLayer voyageHandlingLayer, AisHandlerCommon aisHandler,
            long mmsi) {

        super(EPDShore.getInstance().getMainFrame(), "Request Mona Lisa Route Exchange", true);

        this.route = route;
        this.aisHandler = aisHandler;
        // this.mmsi = mmsi;
        this.voyageHandlingLayer = voyageHandlingLayer;

        setSize(366, 449);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(EPDShore.getInstance().getMainFrame());
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

        JLabel lblNogoRequest = new JLabel("SSPA Route Optimization");
        lblNogoRequest.setBounds(15, 5, 238, 14);
        lblNogoRequest.setFont(new Font("Tahoma", Font.BOLD, 11));
        contentPanel.setLayout(null);
        panel.setLayout(null);
        contentPanel.add(panel);

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

        routeNameLbl = new JLabel("Route Name:");
        routeNameLbl.setBounds(10, 20, 78, 14);
        panel.add(routeNameLbl);

        routeNameTxt = new JLabel("N/A");
        routeNameTxt.setBounds(10, 34, 156, 14);
        panel.add(routeNameTxt);
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
        panel_2.setBounds(15, 295, 327, 91);
        contentPanel.add(panel_2);
        panel_2.setLayout(null);

        JLabel lblServer = new JLabel("Server:");
        lblServer.setBounds(10, 11, 46, 14);
        panel_2.add(lblServer);

        serverTxtField = new JTextField();
        serverTxtField.setBounds(95, 8, 217, 20);
        panel_2.add(serverTxtField);
        serverTxtField.setColumns(10);
        serverTxtField.setText(EPDShore.getInstance().getSettings().getEnavSettings()
                .getMonaLisaServer());
        serverTxtField.setEditable(false);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(10, 36, 46, 14);
        panel_2.add(lblPort);

        portTxtField = new JTextField();
        portTxtField.setBounds(95, 33, 39, 20);
        panel_2.add(portTxtField);
        portTxtField.setColumns(10);
        portTxtField.setText(String.valueOf(EPDShore.getInstance().getSettings()
                .getEnavSettings().getMonaLisaPort()));
        portTxtField.setEditable(false);

        JLabel lblTimeout = new JLabel("Timeout in ms:");
        lblTimeout.setBounds(10, 61, 74, 14);
        panel_2.add(lblTimeout);

        timeoutTxTField = new JTextField();
        timeoutTxTField.setBounds(94, 58, 58, 20);
        panel_2.add(timeoutTxTField);
        timeoutTxTField.setColumns(10);
        timeoutTxTField.setText("60000");
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
                && aisHandler.getVesselTarget(mmsi).getStaticData() != null) {
            Integer draught = (int) (aisHandler.getVesselTarget(mmsi)
                    .getStaticData().getDraught() / 10);
            spinnerDraught.setValue(draught);
        }

    }

    public void showDialog() {

        loadData();
        setVisible(true);
    }

    private void loadData() {

        selectedWp = new ArrayList<Boolean>();

        // Route name
        routeNameTxt.setText(route.getName());
        totalWpLbl.setText(String.valueOf(route.getWaypoints().size()));
        for (int i = 0; i < route.getWaypoints().size(); i++) {
            selectedWp.add(true);
        }

        selectWpLbl.setText(String.valueOf(selectedWp.size()));

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == requestOptiBtn) {
            if (selectWpLbl.getText().equals("0")
                    || selectWpLbl.getText().equals("1")) {
                JOptionPane.showMessageDialog(this,
                        "You must select some waypoints");
            }

            // Which WP to send
            // selectedWp;
            // The route
            // routeid

            boolean removeIntermediateETA = intermediateETACheckBox
                    .isSelected();
            try {
                float draft = Float.parseFloat(spinnerDraught.getValue()
                        .toString());
                int ukc = Integer.parseInt(ukctextField.getText());
                int timeout = Integer.parseInt(timeoutTxTField.getText());

                System.out
                        .println("Creating mona lisa request with the following data: ");
                System.out.println("Remove ETA? " + removeIntermediateETA);
                System.out.println("Draft: " + draft);
                System.out.println("UKC " + ukc);
                System.out.println("Timeout " + timeout);

                this.dispose();

                
                
                // Send off the request
                MonaLisaSSPARequestDialog.requestRoute(EPDShore.getInstance().getMainFrame(),
                        route, EPDShore.getInstance().getMonaLisaRouteExchange(),
                        removeIntermediateETA, draft, ukc, timeout, selectedWp,
                        false, false, voyageHandlingLayer);

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(this, "Invalid integer input");
            }

        }
        if (e.getSource() == cancelButton) {
            // Cancel the request
            this.dispose();
        }
        if (e.getSource() == selectWpBtn) {
            MonaLisaSSPAWPSelection selectionDialog = new MonaLisaSSPAWPSelection(this,
                    route, selectedWp);

            selectionDialog.setVisible(true);
        }
    }

    public void resetSelected() {
        for (int i = 0; i < route.getWaypoints().size(); i++) {
            selectedWp.add(true);
        }
    }

    public void updateSelected() {
        int selected = 0;

        for (int i = 0; i < selectedWp.size(); i++) {
            if (selectedWp.get(i)) {
                selected++;
            }
        }

        selectWpLbl.setText(String.valueOf(selected));
    }
}
