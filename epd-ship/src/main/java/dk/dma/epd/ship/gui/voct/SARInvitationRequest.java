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
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceDatumPoint.VOCTCommunicationMessageDatumPoint;
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationServiceRapidResponse.VOCTCommunicationMessageRapidResponse;
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

    VOCTCommunicationMessageRapidResponse rapidResponseMessage;
    VOCTCommunicationMessageDatumPoint datumPointMessage;

    SAR_TYPE type;

    /**
     * @wbp.parser.constructor
     */
    public SARInvitationRequest(VOCTManager voctManager, VOCTCommunicationMessageRapidResponse message) {
        super();

        this.rapidResponseMessage = message;

        type = SAR_TYPE.RAPID_RESPONSE;

        setupVariables(voctManager);
    }

    public SARInvitationRequest(VOCTManager voctManager, VOCTCommunicationMessageDatumPoint message) {
        super();

        this.datumPointMessage = message;

        type = SAR_TYPE.DATUM_POINT;

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
            if (!rapidResponseMessage.getSarData().getSarID().equals("")) {
                lblSARID.setText(rapidResponseMessage.getSarData().getSarID());
            }

            if (rapidResponseMessage.getEffortAllocationData() != null) {
                dataContained = dataContained + ", designated operational area";
            }

            if (rapidResponseMessage.getSearchPattern() != null) {
                dataContained = dataContained + ", search pattern";
            }

        }

        if (type == SAR_TYPE.DATUM_POINT) {
            if (!datumPointMessage.getSarData().getSarID().equals("")) {
                lblSARID.setText(datumPointMessage.getSarData().getSarID());
            }

            if (datumPointMessage.getEffortAllocationData() != null) {
                dataContained = dataContained + ", designated operational area";
            }

            if (datumPointMessage.getSearchPattern() != null) {
                dataContained = dataContained + ", search pattern";
            }

        }

        messageInfoLabel.setText(dataContained);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {

        if (type == SAR_TYPE.RAPID_RESPONSE) {
            voctManager.handleDialogAction(false, rapidResponseMessage, type);
        }

        if (type == SAR_TYPE.DATUM_POINT) {
            voctManager.handleDialogAction(false, datumPointMessage, type);
        }

        super.dispose();

    }

    private void disposeInternal() {
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == acceptBtn) {

            if (type == SAR_TYPE.RAPID_RESPONSE) {
                System.out.println("Rapid Response ACCEPT BTN");
                voctManager.handleDialogAction(true, rapidResponseMessage, type);
            }

            if (type == SAR_TYPE.DATUM_POINT) {
                voctManager.handleDialogAction(true, datumPointMessage, type);
            }

            // Is the SAR Panel active? If not show it
            if (!EPDShip.getInstance().getMainFrame().getDockableComponents().isDockVisible("SAR")) {
                EPDShip.getInstance().getMainFrame().getDockableComponents().openDock("SAR");
                EPDShip.getInstance().getMainFrame().getJMenuBar().refreshDockableMenu();
            }

            disposeInternal();
            return;
        }

        if (arg0.getSource() == rejectBtn) {

            if (type == SAR_TYPE.RAPID_RESPONSE) {
                voctManager.handleDialogAction(false, rapidResponseMessage, type);
            }

            if (type == SAR_TYPE.DATUM_POINT) {
                voctManager.handleDialogAction(false, datumPointMessage, type);
            }

            disposeInternal();
            return;
        }

        if (arg0.getSource() == zoomBtn) {
            List<Position> positions = new ArrayList<Position>();

            if (type == SAR_TYPE.RAPID_RESPONSE) {
                positions.add(Position.create(rapidResponseMessage.getSarData().getA().getLatitude(), rapidResponseMessage
                        .getSarData().getA().getLongitude()));
                positions.add(Position.create(rapidResponseMessage.getSarData().getB().getLatitude(), rapidResponseMessage
                        .getSarData().getB().getLongitude()));
                positions.add(Position.create(rapidResponseMessage.getSarData().getC().getLatitude(), rapidResponseMessage
                        .getSarData().getC().getLongitude()));
                positions.add(Position.create(rapidResponseMessage.getSarData().getD().getLatitude(), rapidResponseMessage
                        .getSarData().getD().getLongitude()));

            }

            if (type == SAR_TYPE.DATUM_POINT) {
                positions.add(Position.create(datumPointMessage.getSarData().getA().getLatitude(), datumPointMessage.getSarData()
                        .getA().getLongitude()));
                positions.add(Position.create(datumPointMessage.getSarData().getB().getLatitude(), datumPointMessage.getSarData()
                        .getB().getLongitude()));
                positions.add(Position.create(datumPointMessage.getSarData().getC().getLatitude(), datumPointMessage.getSarData()
                        .getC().getLongitude()));
                positions.add(Position.create(datumPointMessage.getSarData().getD().getLatitude(), datumPointMessage.getSarData()
                        .getD().getLongitude()));

            }

            EPDShip.getInstance().getMainFrame().getChartPanel().zoomTo(positions);

            return;
        }

    }
}
