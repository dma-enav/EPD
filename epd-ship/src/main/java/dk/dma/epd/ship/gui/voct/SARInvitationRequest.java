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
import dk.dma.epd.common.prototype.enavcloud.VOCTCommunicationService.VOCTCommunicationMessage;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.ComponentFrame;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.service.voct.VOCTManager;

/**
 * Dialog shown when route suggestion is received
 */
public class SARInvitationRequest extends ComponentFrame implements
        ActionListener, Runnable {
    private static final long serialVersionUID = 1L;

    private JButton acceptBtn;
    private JButton rejectBtn;
    private JButton zoomBtn;
    private JPanel routePanel;

    private JLabel lblSARType;
    private JLabel lblSARID;
    private VOCTManager voctManager;

    VOCTCommunicationMessage message;
    JLabel messageInfoLabel;

    public SARInvitationRequest(VOCTManager voctManager,
            VOCTCommunicationMessage message) {
        super();
        this.voctManager = voctManager;
        this.message = message;
        setResizable(false);
        setTitle("Search and Rescue Request");

        setSize(337, 228);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(EPDShip.getMainFrame());

        initGui();
        setLabels();
        new Thread(this).start();
    }

    private void initGui() {

        zoomBtn = new JButton("Zoom to");
        zoomBtn.setToolTipText("Zoom to the SAR area on map");
        zoomBtn.addActionListener(this);

        routePanel = new JPanel();
        routePanel.setBorder(new TitledBorder(UIManager
                .getBorder("TitledBorder.border"), "Search and Rescue Request",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
                Alignment.LEADING).addGroup(
                groupLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(routePanel, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(51, Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
                Alignment.LEADING).addGroup(
                groupLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addComponent(routePanel, GroupLayout.PREFERRED_SIZE,
                                180, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)));

        JLabel lblOperationName = new JLabel("Type:");

        JLabel lblSarId = new JLabel("SAR ID:");

        JLabel lblRequestYourVessel = new JLabel(
                "Search and Rescue Request with the following information:");
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
        gl_routePanel.setHorizontalGroup(
            gl_routePanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_routePanel.createSequentialGroup()
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(acceptBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(rejectBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(zoomBtn))
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblSarId)
                                .addComponent(lblOperationName))
                            .addGap(54)
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.TRAILING)
                                .addComponent(lblSARID)
                                .addComponent(lblSARType)))
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(lblRequestYourVessel))
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(messageInfoLabel)))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_routePanel.setVerticalGroup(
            gl_routePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_routePanel.createSequentialGroup()
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblSARID)
                        .addComponent(lblSarId, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblOperationName)
                        .addComponent(lblSARType))
                    .addGap(18)
                    .addComponent(lblRequestYourVessel)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(messageInfoLabel)
                    .addPreferredGap(ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(acceptBtn)
                        .addComponent(rejectBtn)
                        .addComponent(zoomBtn)))
        );
        routePanel.setLayout(gl_routePanel);
        getContentPane().setLayout(groupLayout);

    }

    private void setLabels() {
        
        String dataContained = "SAR Search Area";
        if (!message.getSarData().getSarID().equals("")) {
            lblSARID.setText(message.getSarData().getSarID());
        }

        if (message.getEffortAllocationData() != null){
            dataContained = dataContained+ ", designated operational area";
        }
        
        if (message.getSearchPattern() != null) {
            dataContained = dataContained+ ", search pattern";
        }
        
        messageInfoLabel.setText(dataContained);
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        voctManager.handleDialogAction(false, message);
        super.dispose();

    }

    private void disposeInternal() {
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (arg0.getSource() == acceptBtn) {

            voctManager.handleDialogAction(true, message);
            disposeInternal();
            return;
        }

        if (arg0.getSource() == rejectBtn) {
            voctManager.handleDialogAction(false, message);
            disposeInternal();
            return;
        }

        if (arg0.getSource() == zoomBtn) {

            List<Position> positions = new ArrayList<Position>();

            positions
                    .add(Position.create(message.getSarData().getA()
                            .getLatitude(), message.getSarData().getA()
                            .getLongitude()));
            positions
                    .add(Position.create(message.getSarData().getB()
                            .getLatitude(), message.getSarData().getB()
                            .getLongitude()));
            positions
                    .add(Position.create(message.getSarData().getC()
                            .getLatitude(), message.getSarData().getC()
                            .getLongitude()));
            positions
                    .add(Position.create(message.getSarData().getD()
                            .getLatitude(), message.getSarData().getD()
                            .getLongitude()));

            EPDShip.getMainFrame().getChartPanel().zoomTo(positions);

            return;
        }

    }
}
