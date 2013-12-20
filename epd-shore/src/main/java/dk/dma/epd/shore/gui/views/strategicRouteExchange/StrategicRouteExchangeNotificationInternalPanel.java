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
package dk.dma.epd.shore.gui.views.strategicRouteExchange;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteStatus;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;

public class StrategicRouteExchangeNotificationInternalPanel extends JPanel {

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

    JTabbedPane tabbedPane;

    public StrategicRouteExchangeNotificationInternalPanel() {
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
        panel.setBackground(GuiStyler.backgroundColor);
        panel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(
                70, 70, 70)), "Ship Info", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        panel.setBounds(20, 44, 368, 103);
        add(panel);
        panel.setLayout(null);

        JLabel lblMmsi = new JLabel("MMSI:");
        lblMmsi.setBounds(10, 20, 58, 14);
        GuiStyler.styleTitle(lblMmsi);
        panel.add(lblMmsi);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(10, 40, 46, 14);
        GuiStyler.styleTitle(lblName);
        panel.add(lblName);

        JLabel lblCallSign = new JLabel("Call Sign:");
        lblCallSign.setBounds(10, 60, 58, 14);
        GuiStyler.styleTitle(lblCallSign);
        panel.add(lblCallSign);

        JLabel lblType = new JLabel("Type:");
        lblType.setBounds(165, 20, 46, 14);
        GuiStyler.styleTitle(lblType);
        panel.add(lblType);

        JLabel lblDestination = new JLabel("Destination:");
        lblDestination.setBounds(10, 80, 77, 14);
        GuiStyler.styleTitle(lblDestination);
        panel.add(lblDestination);

        JLabel lblLength = new JLabel("Length:");
        lblLength.setBounds(165, 40, 46, 14);
        GuiStyler.styleTitle(lblLength);
        panel.add(lblLength);

        JLabel lblWidth = new JLabel("Width:");
        lblWidth.setBounds(165, 60, 46, 14);
        GuiStyler.styleTitle(lblWidth);
        panel.add(lblWidth);

        JLabel lblSog = new JLabel("SOG");
        lblSog.setBounds(300, 20, 30, 14);
        GuiStyler.styleTitle(lblSog);
        panel.add(lblSog);

        JLabel lblCog = new JLabel("COG");
        lblCog.setBounds(300, 40, 30, 14);
        GuiStyler.styleTitle(lblCog);
        panel.add(lblCog);

        JLabel lblDraught = new JLabel("Draught:");
        lblDraught.setBounds(165, 80, 46, 14);
        GuiStyler.styleTitle(lblDraught);
        panel.add(lblDraught);

        mmsiTxt = new JLabel("");
        mmsiTxt.setBounds(80, 20, 75, 14);
        GuiStyler.styleText(mmsiTxt);
        panel.add(mmsiTxt);

        nameTxt = new JLabel("");
        nameTxt.setBounds(80, 40, 75, 14);
        GuiStyler.styleText(nameTxt);
        panel.add(nameTxt);

        callSignTxt = new JLabel("");
        callSignTxt.setBounds(80, 60, 75, 14);
        GuiStyler.styleText(callSignTxt);
        panel.add(callSignTxt);

        destinationTxt = new JLabel("");
        destinationTxt.setBounds(80, 80, 75, 14);
        GuiStyler.styleText(destinationTxt);
        panel.add(destinationTxt);

        typeTxt = new JLabel("");
        typeTxt.setBounds(215, 20, 77, 14);
        GuiStyler.styleText(typeTxt);
        panel.add(typeTxt);

        lengthTxt = new JLabel("");
        lengthTxt.setBounds(215, 40, 75, 14);
        GuiStyler.styleText(lengthTxt);
        panel.add(lengthTxt);

        widthTxt = new JLabel("");
        widthTxt.setBounds(215, 60, 80, 14);
        GuiStyler.styleText(widthTxt);
        panel.add(widthTxt);

        draughtTxt = new JLabel("");
        draughtTxt.setBounds(215, 80, 80, 14);
        GuiStyler.styleText(draughtTxt);
        panel.add(draughtTxt);

        sogTxt = new JLabel("");
        sogTxt.setBounds(328, 20, 30, 14);
        GuiStyler.styleText(sogTxt);
        panel.add(sogTxt);

        cogTxt = new JLabel("");
        cogTxt.setBounds(326, 40, 32, 14);
        GuiStyler.styleText(cogTxt);
        panel.add(cogTxt);

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(20, 170, 368, 319);

        // tabbedPane.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
        // new Color(70, 70, 70)), "Messages", TitledBorder.LEADING,
        // TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        tabbedPane.setBorder(null);
        tabbedPane.setBackground(GuiStyler.backgroundColor);
        tabbedPane.setForeground(GuiStyler.textColor);
        tabbedPane.setFont(GuiStyler.defaultFont);

        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = new Color(70, 70, 70);
                lightHighlight = new Color(70, 70, 70);
                shadow = new Color(70, 70, 70);
                darkShadow = new Color(70, 70, 70);
                focus = new Color(70, 70, 70);
            }
        });

        add(tabbedPane);

        // tabbedPane.setVisible(false);

        // JPanel panel_1 = new JPanel();
        // panel_1.setBackground(GuiStyler.backgroundColor);
        // panel_1.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
        // new Color(70, 70, 70)), "Ship Info", TitledBorder.LEADING,
        // TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        // tabbedPane.add(panel_1);

    }

    public void updateLabels(StrategicRouteNegotiationData message,
            VesselTarget aisData) {

        if (aisData != null && aisData.getStaticData() != null) {

            transactionIDText.setText(message.getId() + "");

            // Color coding?
            pendngTxt.setText(message.getStatus().toString());
            setStatusColor(message.getStatus());

            mmsiTxt.setText(message.getRouteMessage().get(0).getMmsi() + "");

            nameTxt.setText(aisData.getStaticData().getName().trim());
            callSignTxt.setText(aisData.getStaticData().getCallsign().trim());

            if (aisData.getStaticData().getDestination() != null){
                destinationTxt.setText(aisData.getStaticData().getDestination()
                        .trim());
            }else{
                destinationTxt.setText("N/A");
            }
            


            typeTxt.setText(aisData.getStaticData().getShipType().toString());

            lengthTxt.setText(aisData.getStaticData().getDimBow()
                    + aisData.getStaticData().getDimStern() + "");
            widthTxt.setText(aisData.getStaticData().getDimPort()
                    + aisData.getStaticData().getDimStarboard() + "");
            draughtTxt.setText(aisData.getStaticData().getDraught() / 10 + "");
            sogTxt.setText(aisData.getPositionData().getSog() + "");
            cogTxt.setText(aisData.getPositionData().getCog() + "");

            setNegotiationTabs(message);
        }
    }

    private void setStatusColor(StrategicRouteStatus status) {
        // PENDING, AGREED, REJECTED, NEGOTIATING, CANCELED
        switch (status) {
        case PENDING:
            pendngTxt.setForeground(Color.YELLOW);
            break;
        case AGREED:
            pendngTxt.setForeground(new Color(130, 165, 80));
            break;
        case REJECTED:
            pendngTxt.setForeground(new Color(165, 80, 80));
            break;
        case NEGOTIATING:
            pendngTxt.setForeground(Color.YELLOW);
            break;
        case CANCELED:
            pendngTxt.setForeground(new Color(165, 80, 80));
            break;
        }
    }

    public void updateLabels(StrategicRouteNegotiationData message) {

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

    private void setNegotiationTabs(StrategicRouteNegotiationData message) {
        // tabbedPane.setVisible(true);

        tabbedPane.removeAll();

        // for each route message
        for (int i = message.getRouteMessage().size() - 1; i > -1; i--) {

            System.out.println("looking at message " + i);

            StrategicNegotiationView negotiation = new StrategicNegotiationView(
                    message.getRouteMessage().get(i));

            System.out.println("Any replies to be found? "
                    + message.getRouteReply().size());
            if (message.getRouteReply().size() > i) {
                negotiation.handleReply(message.getRouteReply().get(i));
            }

            if (message.isCompleted()) {

                System.out.println("Its completed with status "
                        + message.getStatus());
            }

            tabbedPane.addTab("Message " + (i + 1), null, negotiation, null);

            // message.getRouteMessage().get(i).
            // message.get
        }

        System.out.println("hmm done?");
    }
}
