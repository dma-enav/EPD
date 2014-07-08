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
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.common.prototype.gui.voct.EffortAllocationWindowCommon;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.SweepWidthValues;
import dk.dma.epd.common.prototype.model.voct.WeatherCorrectionFactors;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRU.SRU_TYPE;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.SRUUpdateEvent;
import dk.dma.epd.shore.voct.SRUUpdateListener;
import dk.dma.epd.shore.voct.VOCTManager;

public class EffortAllocationWindow extends EffortAllocationWindowCommon
        implements ListSelectionListener, MouseListener, TableModelListener, SRUUpdateListener {
    private static final long serialVersionUID = 1L;

    private final JPanel initPanel = new JPanel();
    private JTextField windspeedField;
    private JTextField waterElevationField;
    private JTextField probabilityOfDetectionVal;

    JComboBox<String> targetTypeDropdown;
    // JComboBox<Integer> visibilityDropDown;

    private JCheckBox editPoD;
    private JButton calculate;
    private VOCTManager voctManager;
    private SRUManager sruManager;

    private JLabel noSRUs;

    private JLabel lblAvailableSrus;

    // DefaultListModel<String> listModel = new DefaultListModel<String>();
    // JList<String> sruScrollPane;

    private JScrollPane sruScrollPane;
    private JTable sruTable;
    private EffortAllocationWindowTabelModel sruTableModel;
    private ListSelectionModel sruSelectionModel;

    /**
     * Create the dialog.
     */
    public EffortAllocationWindow() {
        setTitle("Effort Allocation");
        this.setModal(true);
        this.setResizable(false);

        // setBounds(100, 100, 559, 733);
        setBounds(100, 100, 559, 575);
        getContentPane().setLayout(new BorderLayout());

        buttomBar();

        initPanel();

        this.setVisible(false);
    }

    public void setVisible(boolean visible) {

        if (visible) {

            if (sruManager.getSRUCount() == 0) {
                noSRUs.setVisible(true);
                calculate.setEnabled(false);

                lblAvailableSrus.setVisible(false);
                sruScrollPane.setVisible(false);
            } else {

                // fillSruList();
                sruTableModel.updateCalculateTable();

                lblAvailableSrus.setVisible(true);
                sruScrollPane.setVisible(true);

                noSRUs.setVisible(false);
                calculate.setEnabled(true);

            }

        }

        super.setVisible(visible);

    }

    // private void fillSruList() {
    // // sruJList.removeAll();
    // listModel.removeAllElements();
    // List<SRU> sruList = sruManager.getSRUs();
    //
    // for (int i = 0; i < sruList.size(); i++) {
    // SRU currentSRU = sruList.get(i);
    // String sruTarget = currentSRU.getName() + " - "
    // + currentSRU.getSearchSpeed() + " kn - "
    // + currentSRU.getType();
    //
    // listModel.addElement(sruTarget);
    //
    // }
    //
    // }

    public void setVoctManager(VOCTManager voctManager) {
        this.voctManager = voctManager;
        sruManager = voctManager.getSruManager();
        sruManager.addListener(this);
    }

    private void initPanel() {
        initPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(initPanel, BorderLayout.CENTER);

        initPanel.setLayout(null);

        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"),
                    "Calculate Effective Search Area", TitledBorder.LEADING,
                    TitledBorder.TOP, null, null));
            panel.setBounds(10, 11, 523, 478);
            initPanel.add(panel);
            panel.setLayout(null);

            JPanel panel_2 = new JPanel();
            panel_2.setBorder(new TitledBorder(null, "SRU Information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_2.setBounds(10, 32, 503, 165);
            panel.add(panel_2);
            panel_2.setLayout(null);

            lblAvailableSrus = new JLabel("Available SRUs:");
            lblAvailableSrus.setBounds(10, 23, 137, 14);
            panel_2.add(lblAvailableSrus);

            DefaultTableModel model = new DefaultTableModel(30, 3);

            sruTable = new JTable(model) {
                private static final long serialVersionUID = 1L;

                public Component prepareRenderer(TableCellRenderer renderer,
                        int Index_row, int Index_col) {
                    Component comp = super.prepareRenderer(renderer, Index_row,
                            Index_col);
                    if (Index_row % 2 == 0) {
                        comp.setBackground(new Color(49, 49, 49));
                    } else {
                        comp.setBackground(new Color(65, 65, 65));
                    }

                    if (isCellSelected(Index_row, Index_col)) {
                        comp.setForeground(Color.white);
                        comp.setBackground(new Color(85, 85, 85));
                    }

                    return comp;
                }
            };

            // routeTable.setTableHeader(null);

            sruTable.setBorder(new EmptyBorder(0, 0, 0, 0));
            // routeTable.setIntercellSpacing(new Dimension(0, 0));
            sruTable.setBackground(new Color(49, 49, 49));
            sruTable.setShowVerticalLines(false);
            sruTable.setShowHorizontalLines(false);
            sruTable.setShowGrid(false);
            sruTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            sruTable.setForeground(Color.white);
            sruTable.setSelectionForeground(Color.gray);
            // routeTable.setRowHeight(20);
            sruTable.setFocusable(false);
            // routeTable.setAutoResizeMode(0);

            sruTableModel = new EffortAllocationWindowTabelModel(EPDShore.getInstance()
                    .getVoctManager().getSruManager(),
                    EPDShore.getInstance().getVoctManager());
            sruTableModel.addTableModelListener(this);

            sruTable.setShowHorizontalLines(false);
            sruTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // sruScrollPane = new JList<String>(listModel);
            sruScrollPane = new JScrollPane(sruTable);
            sruScrollPane.setEnabled(false);

            sruScrollPane.setBounds(10, 50, 483, 104);

            sruScrollPane
                    .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            sruScrollPane
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            sruTable.setFillsViewportHeight(true);

            sruScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                    new Color(30, 30, 30)));

            // TODO: Comment this line when using WindowBuilder
            sruTable.setModel(sruTableModel);
            // for (int i = 0; i < 2; i++) {
            //
            // if (i == 0){
            // sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
            // }
            // if (i == 1){
            // sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
            // }
            // if (i == 2){
            // sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
            // }
            //
            //
            // }
            sruSelectionModel = sruTable.getSelectionModel();
            sruSelectionModel.addListSelectionListener(this);
            sruTable.setSelectionModel(sruSelectionModel);
            sruTable.addMouseListener(this);

            panel_2.add(sruScrollPane);

            noSRUs = new JLabel(
                    "There are no SRUs added. Please add a SRU before doing Effort Allocation");
            noSRUs.setBounds(10, 23, 446, 14);
            noSRUs.setVisible(false);
            panel_2.add(noSRUs);

            JPanel panel_3 = new JPanel();
            panel_3.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "SAR Information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_3.setBounds(10, 208, 503, 55);
            panel.add(panel_3);
            panel_3.setLayout(null);
            {
                JLabel lblSelectSarType = new JLabel("Target Type:");
                lblSelectSarType.setBounds(10, 21, 140, 14);
                panel_3.add(lblSelectSarType);
            }

            targetTypeDropdown = new JComboBox<String>();
            targetTypeDropdown.setBounds(139, 18, 354, 20);
            panel_3.add(targetTypeDropdown);
            // targetTypeDropdown.setModel(new DefaultComboBoxModel<String>(
            // new String[] { "Person in Water, raft or boat < 30 ft",
            // "Other targets" }));

            for (int i = 0; i < SweepWidthValues.getSweepWidthTypes().size(); i++) {
                targetTypeDropdown.addItem(SweepWidthValues
                        .getSweepWidthTypes().get(i));
            }

            JPanel panel_4 = new JPanel();
            panel_4.setBorder(new TitledBorder(null, "Weather information",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_4.setBounds(10, 293, 503, 88);
            panel.add(panel_4);
            panel_4.setLayout(null);

            JLabel lblWindKnots = new JLabel("Wind Speed, knots:");
            lblWindKnots.setBounds(12, 26, 147, 14);
            panel_4.add(lblWindKnots);

            windspeedField = new JTextField();
            windspeedField.setColumns(10);
            windspeedField.setBounds(159, 23, 33, 20);
            panel_4.add(windspeedField);

            JLabel lblTotalWindCurrent = new JLabel("Water Elevation, feet:");
            lblTotalWindCurrent.setBounds(12, 56, 147, 14);
            panel_4.add(lblTotalWindCurrent);

            waterElevationField = new JTextField();
            waterElevationField.setColumns(10);
            waterElevationField.setBounds(159, 53, 33, 20);
            panel_4.add(waterElevationField);

            // visibilityDropDown = new JComboBox<Integer>();
            // visibilityDropDown.setModel(new DefaultComboBoxModel<Integer>(
            // new Integer[] { 1, 3, 5, 10, 15, 20 }));
            // visibilityDropDown.setBounds(276, 23, 45, 20);
            // panel_4.add(visibilityDropDown);

            JPanel panel_5 = new JPanel();
            panel_5.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"), "Additional Options",
                    TitledBorder.LEADING, TitledBorder.TOP, null, null));
            panel_5.setBounds(10, 394, 503, 71);
            panel.add(panel_5);
            panel_5.setLayout(null);

            JLabel lblDesiredProbabilityOf = new JLabel(
                    "Desired Probability of Detection:");
            lblDesiredProbabilityOf.setBounds(12, 24, 197, 14);
            panel_5.add(lblDesiredProbabilityOf);

            probabilityOfDetectionVal = new JTextField();
            probabilityOfDetectionVal.setEnabled(false);
            probabilityOfDetectionVal.setEditable(false);
            probabilityOfDetectionVal.setText("78%");
            probabilityOfDetectionVal.setBounds(193, 22, 41, 20);
            panel_5.add(probabilityOfDetectionVal);
            probabilityOfDetectionVal.setColumns(10);

            editPoD = new JCheckBox("Edit");
            editPoD.setBounds(237, 19, 105, 25);
            panel_5.add(editPoD);

            editPoD.addActionListener(this);
        }

    }

    public void setValues() {
        // VesselTarget ownship = EPDShip.getAisHandler().getOwnShip();
        //
        // if (ownship != null) {
        // if (ownship.getStaticData() != null) {
        // shipName.setText(ownship.getStaticData().getName());
        //
        // double length = ownship.getStaticData().getDimBow()
        // + ownship.getStaticData().getDimStern();
        // // String width = Integer.toString(ownship.getStaticData()
        // // .getDimPort()
        // // + ownship.getStaticData().getDimStarboard()) + " M";
        //
        // // Is the lenght indicated by the AIS longer than 89 feet then
        // // it falls under Ship category
        // if (Converter.metersToFeet(length) > 89) {
        // sruType.setSelectedIndex(1);
        // }
        //
        // }
        // }
    }

    private void buttomBar() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
            calculate = new JButton("Calculate Effective Area");
            buttonPane.add(calculate);
            getRootPane().setDefaultButton(calculate);
            calculate.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == editPoD) {
            if (editPoD.isSelected()) {
                probabilityOfDetectionVal.setEnabled(true);
                probabilityOfDetectionVal.setEditable(true);
            } else {
                probabilityOfDetectionVal.setEnabled(false);
                probabilityOfDetectionVal.setEditable(false);
            }
        }

        if (arg0.getSource() == calculate) {
            if (checkValues()) {
                // Ready to go
                voctManager.EffortAllocationDataEntered();
                this.setVisible(false);
            }
        }

    }

    private boolean checkValues() {

        SARData sarData = voctManager.getSarData();

        // sarData.removeAllEffortAllocationData();

        List<SRU> sruList = sruManager.getSRUs();

        if (sruList.size() == 0) {
            return false;
        }

        int targetType = targetTypeDropdown.getSelectedIndex();
        // int visibility = (int) visibilityDropDown.getSelectedItem();

        double probabilityOfDetection = getProbabilityOfDetection();
        // System.out.println(probabilityOfDetection);
        if (probabilityOfDetection == -9999) {
            return false;
        }

        for (int i = 0; i < sruList.size(); i++) {

            System.out.println("Calculation for " + i
                    + (boolean) sruTable.getValueAt(i, 2));

            if ((boolean) sruTable.getValueAt(i, 2)) {

                SRU currentSRU = sruList.get(i);

                EffortAllocationData data;

                if (sarData.getEffortAllocationData().size() > i) {
                    data = sarData.getEffortAllocationData().get(i);

                    // Delete the old route
                    if (data.getSearchPatternRoute() != null) {
                        System.out
                                .println("Removing routes from old sar effort allocation data");
                        EPDShore.getInstance().getRouteManager().getRoutes()
                                .remove(data.getSearchPatternRoute());
                        EPDShore.getInstance().getRouteManager().notifyListeners(
                                RoutesUpdateEvent.ROUTE_REMOVED);
                        data.setSearchPatternRoute(null);
                    }

                }

                data = new EffortAllocationData();

                data.setGroundSpeed(currentSRU.getSearchSpeed());
                data.setPod(probabilityOfDetection);

                // Wc = Wu x Fw x Fv x Ff

                // Wu is done by table lookup

                int windSpeed = getWindSpeed();

                if (windSpeed == -9999) {
                    return false;
                }

                int waterLevel = getWaterElevation();
                if (waterLevel == -9999) {
                    return false;
                }

                int fwRow = 0;

                if (windSpeed >= 0 && windSpeed <= 15 || waterLevel >= 0
                        && waterLevel <= 3) {
                    fwRow = 0;
                }

                if (windSpeed > 15 && windSpeed <= 25 || waterLevel > 3
                        && waterLevel <= 5) {
                    fwRow = 1;
                }

                if (windSpeed > 25 || waterLevel > 5) {
                    fwRow = 2;
                }

                // Two types of search object for FW
                // Person in Water, raft or boat less than 30 feet
                // Or
                // Other
                double fw;

                // PIW, raft or small boat
                if (targetType >= 0 && targetType <= 10 || targetType >= 14
                        && targetType < 17) {
                    fw = WeatherCorrectionFactors.getPIWAndSmallBoats().get(
                            fwRow);
                } else {
                    // Other object
                    fw = WeatherCorrectionFactors.getOtherObjects().get(fwRow);
                }

                double wu = 0.0;

                if (currentSRU.getType() == SRU_TYPE.Smaller_Vessel) {
                    // Small type
                    wu = SweepWidthSmallShipLookup(targetType,
                            currentSRU.getVisibility());
                } else {
                    if (currentSRU.getType() == SRU_TYPE.Ship) {
                        wu = SweepWidthLargeShipLookup(targetType,
                                currentSRU.getVisibility());
                    }
                }

                double ff = currentSRU.getFatigue();

                double wc = wu * fw * ff;

                System.out.println("wu is " + wu + " fw " + fw + " ff " + ff);

                data.setW(wc);

                System.out.println("Calculating for ");

                System.out.println("Setting W to " + wc);

                data.setSearchTime(currentSRU.getSearchTime());

                sarData.addEffortAllocationData(data, i);

            } else {
                sarData.getEffortAllocationData().get(i).setNoRedraw(true);
            }
        }

        return true;
    }

    private double SweepWidthSmallShipLookup(int searchObject, int visibility) {
        return SweepWidthValues.getSmallerVessels().get(searchObject)
                .get(visibility);
    }

    private double SweepWidthLargeShipLookup(int searchObject, int visibility) {
        return SweepWidthValues.getLargerVessels().get(searchObject)
                .get(visibility);
    }

    private int getWindSpeed() {

        String windSpeed = windspeedField.getText();

        if (windSpeed.equals("")) {
            displayMissingField("Wind Speed");
            return -9999;
        } else {
            try {
                return Integer.parseInt(windSpeed);
            } catch (Exception e) {
                displayMissingField("Wind speed");
                return -9999;
            }
        }

    }

    private int getWaterElevation() {

        String waterElevation = waterElevationField.getText();

        if (waterElevation.equals("")) {
            displayMissingField("Water Elevation");
            return -9999;
        } else {
            try {
                return Integer.parseInt(waterElevation);
            } catch (Exception e) {
                displayMissingField("Water Elevation");
                return -9999;
            }
        }

    }

    private double getProbabilityOfDetection() {

        String probabilityOfDetection = probabilityOfDetectionVal.getText();

        // Remove %
        try {
            probabilityOfDetection = (String) probabilityOfDetection
                    .subSequence(0, probabilityOfDetection.length() - 1);
        } catch (Exception e) {
            // Invalid - ignore
        }

        if (probabilityOfDetection.equals("")) {
            displayMissingField("Probability of Detection");
            return -9999;
        } else {
            try {
                if (probabilityOfDetection.contains(",")) {
                    probabilityOfDetection = probabilityOfDetection.replace(
                            ",", ".");
                }

                return Double.parseDouble(probabilityOfDetection) / 100;
            } catch (Exception e) {
                displayMissingField("Probability of Detection");
                return -9999;
            }
        }
    }

    private void displayMissingField(String fieldname) {
        // Missing or incorrect value in
        JOptionPane.showMessageDialog(this, "Missing or incorrect value in "
                + fieldname, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tableChanged(TableModelEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sruUpdated(SRUUpdateEvent e, long mmsi) {
        sruTableModel.updateCalculateTable();
        sruTableModel.fireTableDataChanged();
    }
}
