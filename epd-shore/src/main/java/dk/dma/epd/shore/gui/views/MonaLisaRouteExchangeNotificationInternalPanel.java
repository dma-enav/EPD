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
package dk.dma.epd.shore.gui.views;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.service.MonaLisaRouteNegotiationData;

public class MonaLisaRouteExchangeNotificationInternalPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    JLabel transactionIDText;
    JLabel pendngTxt;
    JLabel mmsiTxt;
    JLabel nameTxt;
    JLabel callSignTxt;
    JLabel destinationTxt;

    JLabel typeTxt;
    JLabel lengthTxt;
    JLabel widthTxt;
    JLabel draughtTxt;

    JLabel sogTxt;
    JLabel cogTxt;

    public MonaLisaRouteExchangeNotificationInternalPanel() {
        setLayout(null);

        JLabel lblTransactionId = new JLabel("Transaction ID:");
        lblTransactionId.setBounds(10, 11, 84, 14);
        GuiStyler.styleText(lblTransactionId);
        add(lblTransactionId);

        transactionIDText = new JLabel("");
        transactionIDText.setBounds(91, 11, 96, 14);
        GuiStyler.styleText(transactionIDText);
        add(transactionIDText);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setBounds(251, 11, 46, 14);
        GuiStyler.styleText(lblStatus);
        add(lblStatus);

        pendngTxt = new JLabel("");
        pendngTxt.setBounds(295, 11, 69, 14);
        GuiStyler.styleText(pendngTxt);
        add(pendngTxt);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Ship Info",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.setBounds(20, 44, 368, 103);
        add(panel);
        panel.setLayout(null);

        JLabel lblMmsi = new JLabel("MMSI:");
        lblMmsi.setBounds(10, 20, 58, 14);
        GuiStyler.styleText(lblMmsi);
        panel.add(lblMmsi);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(10, 40, 46, 14);
        GuiStyler.styleText(lblName);
        panel.add(lblName);

        JLabel lblCallSign = new JLabel("Call Sign:");
        lblCallSign.setBounds(10, 60, 46, 14);
        GuiStyler.styleText(lblCallSign);
        panel.add(lblCallSign);

        JLabel lblType = new JLabel("Type");
        lblType.setBounds(150, 20, 46, 14);
        GuiStyler.styleText(lblType);
        panel.add(lblType);

        JLabel lblDestination = new JLabel("Destination:");
        lblDestination.setBounds(10, 80, 77, 14);
        GuiStyler.styleText(lblDestination);
        panel.add(lblDestination);

        JLabel lblLength = new JLabel("Length");
        lblLength.setBounds(150, 40, 46, 14);
        GuiStyler.styleText(lblLength);
        panel.add(lblLength);

        JLabel lblWidth = new JLabel("Width");
        lblWidth.setBounds(150, 60, 46, 14);
        GuiStyler.styleText(lblWidth);
        panel.add(lblWidth);

        JLabel lblSog = new JLabel("SOG");
        lblSog.setBounds(270, 20, 46, 14);
        GuiStyler.styleText(lblSog);
        panel.add(lblSog);

        JLabel lblCog = new JLabel("COG");
        lblCog.setBounds(270, 40, 46, 14);
        GuiStyler.styleText(lblCog);
        panel.add(lblCog);

        JLabel lblDraught = new JLabel("Draught");
        lblDraught.setBounds(150, 80, 46, 14);
        GuiStyler.styleText(lblDraught);
        panel.add(lblDraught);

        mmsiTxt = new JLabel("");
        mmsiTxt.setBounds(80, 20, 60, 14);
        GuiStyler.styleText(mmsiTxt);
        panel.add(mmsiTxt);

        nameTxt = new JLabel("");
        nameTxt.setBounds(80, 40, 60, 14);
        GuiStyler.styleText(nameTxt);
        panel.add(nameTxt);

        callSignTxt = new JLabel("");
        callSignTxt.setBounds(80, 60, 60, 14);
        GuiStyler.styleText(callSignTxt);
        panel.add(callSignTxt);

        destinationTxt = new JLabel("");
        destinationTxt.setBounds(80, 80, 60, 14);
        GuiStyler.styleText(destinationTxt);
        panel.add(destinationTxt);

        typeTxt = new JLabel("");
        typeTxt.setBounds(200, 20, 60, 14);
        GuiStyler.styleText(typeTxt);
        panel.add(typeTxt);

        lengthTxt = new JLabel("");
        lengthTxt.setBounds(200, 40, 60, 14);
        GuiStyler.styleText(lengthTxt);
        panel.add(lengthTxt);

        widthTxt = new JLabel("");
        widthTxt.setBounds(200, 60, 60, 14);
        GuiStyler.styleText(widthTxt);
        panel.add(widthTxt);

        draughtTxt = new JLabel("");
        draughtTxt.setBounds(200, 80, 60, 14);
        GuiStyler.styleText(draughtTxt);
        panel.add(draughtTxt);

        sogTxt = new JLabel("");
        sogTxt.setBounds(299, 20, 59, 14);
        GuiStyler.styleText(sogTxt);
        panel.add(sogTxt);

        cogTxt = new JLabel("");
        cogTxt.setBounds(299, 40, 59, 14);
        GuiStyler.styleText(cogTxt);
        panel.add(cogTxt);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(20, 170, 368, 319);
        add(tabbedPane);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Negotiation 1", null, panel_1, null);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Negotiation 2", null, panel_2, null);

        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("Negotiation 3", null, panel_3, null);

        JPanel panel_4 = new JPanel();
        tabbedPane.addTab("Negotiation 4", null, panel_4, null);

        JPanel panel_5 = new JPanel();
        tabbedPane.addTab("Negotiation 5", null, panel_5, null);

        JPanel panel_6 = new JPanel();
        tabbedPane.addTab("Negotiation 6", null, panel_6, null);

        JPanel panel_7 = new JPanel();
        tabbedPane.addTab("Negotiation 7", null, panel_7, null);

        JPanel panel_8 = new JPanel();
        tabbedPane.addTab("Negotiation 8", null, panel_8, null);

    }

    public void updateLabels(MonaLisaRouteNegotiationData message,
            VesselTarget aisData) {

        transactionIDText.setText(message.getId() + "");

        // Color coding?
        pendngTxt.setText(message.getStatus().toString());
        setStatusColor(message.getStatus());
        
        mmsiTxt.setText(message.getRouteMessage().get(0).getMmsi() + "");

        nameTxt.setText("N/A");
        callSignTxt.setText("N/A");
        destinationTxt.setText("N/A");

        typeTxt.setText("N/A");
        lengthTxt.setText("N/A");
        widthTxt.setText("N/A");
        draughtTxt.setText("N/A");
        sogTxt.setText("N/A");
        cogTxt.setText("N/A");

        setNegotiationTabs(message);
    }
    
    private void setStatusColor(MonaLisaRouteStatus status){
        // PENDING, AGREED, REJECTED, NEGOTIATING, CANCELED
        switch (status) {
        case PENDING:
            pendngTxt.setForeground(Color.YELLOW);
            break;
        case AGREED:
            pendngTxt.setForeground(Color.GREEN);
            break;
        case REJECTED:
            pendngTxt.setForeground(Color.RED);
            break;
        case NEGOTIATING:
            pendngTxt.setForeground(Color.YELLOW);
            break;
        case CANCELED:
            pendngTxt.setForeground(Color.RED);
            break;
        }
    }

    public void updateLabels(MonaLisaRouteNegotiationData message) {

        transactionIDText.setText(message.getId() + "");

        // Color coding?
        pendngTxt.setText(message.getStatus().toString());
        setStatusColor(message.getStatus());


        mmsiTxt.setText(message.getRouteMessage().get(0).getMmsi() + "");

        nameTxt.setText("N/A");
        callSignTxt.setText("N/A");
        destinationTxt.setText("N/A");

        typeTxt.setText("N/A");
        lengthTxt.setText("N/A");
        widthTxt.setText("N/A");
        draughtTxt.setText("N/A");
        sogTxt.setText("N/A");
        cogTxt.setText("N/A");

        setNegotiationTabs(message);
    }

    private void setNegotiationTabs(MonaLisaRouteNegotiationData message) {

    }
}
