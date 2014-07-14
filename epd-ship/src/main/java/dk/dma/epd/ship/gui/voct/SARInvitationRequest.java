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
package dk.dma.epd.ship.gui.voct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.common.prototype.gui.ComponentFrame;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.service.voct.VOCTManager;

/**
 * Dialog shown when route suggestion is received
 */
public class SARInvitationRequest extends ComponentFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 1L;

    private JButton acceptBtn;
    private JButton rejectBtn;
    private JButton zoomBtn;
    private JPanel routePanel;

    private JLabel lblSARType;
    private JLabel lblSARID;
    private VOCTManager voctManager;

    JLabel messageInfoLabel;

    VOCTCommunicationMessage voctCommunicationMessage;

    SAR_TYPE type;

    /**
     * @wbp.parser.constructor
     */
    public SARInvitationRequest(VOCTManager voctManager, VOCTCommunicationMessage message) {
        super();

        this.voctCommunicationMessage = message;

        type = message.getType();

        setupVariables(voctManager);
    }

    private void setupVariables(VOCTManager voctManager) {

        this.voctManager = voctManager;
        setResizable(false);
        setTitle("Search and Rescue Request");

        setSize(337, 228);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(EPDShip.getInstance().getMainFrame());

        initGui();
        setLabels();
        new Thread(this).start();

    }

    private void initGui() {

        zoomBtn = new JButton("Zoom to");
        zoomBtn.setToolTipText("Zoom to the SAR area on map");
        zoomBtn.addActionListener(this);

        routePanel = new JPanel();
        routePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Search and Rescue Request",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
                groupLayout.createSequentialGroup().addContainerGap()
                        .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(51, Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
                groupLayout.createSequentialGroup().addContainerGap()
                        .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        JLabel lblOperationName = new JLabel("Type:");

        JLabel lblSarId = new JLabel("SAR ID:");

        JLabel lblRequestYourVessel = new JLabel("Search and Rescue Request with the following information:");
        acceptBtn = new JButton("Accept");
        acceptBtn.setToolTipText("Indicate that suggested route will be used");
        acceptBtn.addActionListener(this);

        rejectBtn = new JButton("Reject");
        rejectBtn.setToolTipText("Reject the suggested route");
        rejectBtn.addActionListener(this);

        lblSARType = new JLabel("N/A");

        lblSARID = new JLabel("N/A");

        messageInfoLabel = new JLabel("N/A");

        GroupLayout gl_routePanel = new GroupLayout(routePanel);
        gl_routePanel.setHorizontalGroup(gl_routePanel.createParallelGroup(Alignment.TRAILING).addGroup(
                gl_routePanel
                        .createSequentialGroup()
                        .addGroup(
                                gl_routePanel
                                        .createParallelGroup(Alignment.LEADING)
                                        .addGroup(
                                                gl_routePanel
                                                        .createSequentialGroup()
                                                        .addContainerGap()
                                                        .addComponent(acceptBtn, GroupLayout.PREFERRED_SIZE, 87,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18)
                                                        .addComponent(rejectBtn, GroupLayout.PREFERRED_SIZE, 87,
                                                                GroupLayout.PREFERRED_SIZE).addGap(18).addComponent(zoomBtn))
                                        .addGroup(
                                                gl_routePanel
                                                        .createSequentialGroup()
                                                        .addContainerGap()
                                                        .addGroup(
                                                                gl_routePanel.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(lblSarId).addComponent(lblOperationName))
                                                        .addGap(54)
                                                        .addGroup(
                                                                gl_routePanel.createParallelGroup(Alignment.TRAILING)
                                                                        .addComponent(lblSARID).addComponent(lblSARType)))
                                        .addGroup(
                                                gl_routePanel.createSequentialGroup().addContainerGap()
                                                        .addComponent(lblRequestYourVessel))
                                        .addGroup(
                                                gl_routePanel.createSequentialGroup().addContainerGap()
                                                        .addComponent(messageInfoLabel)))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        gl_routePanel.setVerticalGroup(gl_routePanel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_routePanel
                        .createSequentialGroup()
                        .addGroup(
                                gl_routePanel.createParallelGroup(Alignment.BASELINE).addComponent(lblSARID)
                                        .addComponent(lblSarId, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                        .addGap(6)
                        .addGroup(
                                gl_routePanel.createParallelGroup(Alignment.BASELINE).addComponent(lblOperationName)
                                        .addComponent(lblSARType))
                        .addGap(18)
                        .addComponent(lblRequestYourVessel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(messageInfoLabel)
                        .addPreferredGap(ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addGroup(
                                gl_routePanel.createParallelGroup(Alignment.BASELINE).addComponent(acceptBtn)
                                        .addComponent(rejectBtn).addComponent(zoomBtn))));
        routePanel.setLayout(gl_routePanel);
        getContentPane().setLayout(groupLayout);

    }

    private void setLabels() {

        String dataContained = "SAR Search Area";
        lblSARType.setText(type.toString());

        if (type == SAR_TYPE.RAPID_RESPONSE) {
            if (!voctCommunicationMessage.getSarDataRapidResponse().getSarID().equals("")) {
                lblSARID.setText(voctCommunicationMessage.getSarDataRapidResponse().getSarID());
            }

        }

        if (type == SAR_TYPE.DATUM_POINT) {
            if (!voctCommunicationMessage.getSarDataDatumPoint().getSarID().equals("")) {
                lblSARID.setText(voctCommunicationMessage.getSarDataDatumPoint().getSarID());
            }

        }

        if (voctCommunicationMessage.getEffortAllocationData() != null) {
            dataContained = dataContained + ", designated operational area";
        }

        if (voctCommunicationMessage.getSearchPattern() != null) {
            dataContained = dataContained + ", search pattern";
        }

        messageInfoLabel.setText(dataContained);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {

        voctManager.handleDialogAction(false, voctCommunicationMessage, type);

        super.dispose();

    }

    private void disposeInternal() {
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == acceptBtn) {
            voctManager.handleDialogAction(true, voctCommunicationMessage, type);

            // Is the SAR Panel active? If not show it
            if (!EPDShip.getInstance().getMainFrame().getDockableComponents().isDockVisible("SAR")) {
                EPDShip.getInstance().getMainFrame().getDockableComponents().openDock("SAR");
                EPDShip.getInstance().getMainFrame().getJMenuBar().refreshDockableMenu();
            }

            disposeInternal();
            return;
        }

        if (arg0.getSource() == rejectBtn) {

            voctManager.handleDialogAction(false, voctCommunicationMessage, type);

            disposeInternal();
            return;
        }

        if (arg0.getSource() == zoomBtn) {
            List<Position> positions = new ArrayList<Position>();

            if (type == SAR_TYPE.RAPID_RESPONSE) {

                positions.add(Position.create(voctCommunicationMessage.getSarDataRapidResponse().getA().getLatitude(),
                        voctCommunicationMessage.getSarDataRapidResponse().getA().getLongitude()));
                positions.add(Position.create(voctCommunicationMessage.getSarDataRapidResponse().getB().getLatitude(),
                        voctCommunicationMessage.getSarDataRapidResponse().getB().getLongitude()));
                positions.add(Position.create(voctCommunicationMessage.getSarDataRapidResponse().getC().getLatitude(),
                        voctCommunicationMessage.getSarDataRapidResponse().getC().getLongitude()));
                positions.add(Position.create(voctCommunicationMessage.getSarDataRapidResponse().getD().getLatitude(),
                        voctCommunicationMessage.getSarDataRapidResponse().getD().getLongitude()));

            }

            if (type == SAR_TYPE.DATUM_POINT) {
                positions.add(Position.create(voctCommunicationMessage.getSarDataDatumPoint().getA().getLatitude(),
                        voctCommunicationMessage.getSarDataDatumPoint().getA().getLongitude()));
                positions.add(Position.create(voctCommunicationMessage.getSarDataDatumPoint().getB().getLatitude(),
                        voctCommunicationMessage.getSarDataDatumPoint().getB().getLongitude()));
                positions.add(Position.create(voctCommunicationMessage.getSarDataDatumPoint().getC().getLatitude(),
                        voctCommunicationMessage.getSarDataDatumPoint().getC().getLongitude()));
                positions.add(Position.create(voctCommunicationMessage.getSarDataDatumPoint().getD().getLatitude(),
                        voctCommunicationMessage.getSarDataDatumPoint().getD().getLongitude()));

            }

            EPDShip.getInstance().getMainFrame().getChartPanel().zoomTo(positions);

            return;
        }

    }
}
