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
package dk.dma.epd.shore.layers.voyage;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.route.RoutePropertiesDialog;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.voyage.Voyage;

/**
 * This panel contains information about the ship and voyage plan.
 * It also contains the Send Voyage functionality.
 */
public class VoyagePlanInfoPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private Voyage voyage;
    private AisHandler aisHandler;
    ChartPanel chartPanel;
    VoyageHandlingLayer voyageHandlingLayer;

    JLabel lblShipName = new JLabel(" ");
    JLabel lblCallSign = new JLabel(" ");
    JLabel lblRouteName = new JLabel(" ");
    JLabel lblCog = new JLabel(" ");
    JLabel lblSog = new JLabel(" ");
    JLabel lblTd = new JLabel(" ");
    JLabel lblETA = new JLabel(" ");
    JTextArea txtMessage = new JTextArea();

    JButton ZoomToShipBtn = new JButton("Zoom to ship in center");
    JButton OpenShipDetailstextBtn = new JButton("Open ship details");
    JButton OpenVpDetalsBtn = new JButton("Open voyage plan details");
    JButton HideOtherVoyagesBtn = new JButton("Hide other voyages");
    JButton sendBtn = new JButton("Send Voyage");
    
    
    /**
     * Create the panel.
     * 
     * @param voyage
     */
    public VoyagePlanInfoPanel(VoyageHandlingLayer voyageHandlingLayer) {
        super();

        this.voyageHandlingLayer = voyageHandlingLayer;
        
        setOpaque(false);
        setLayout(new GridBagLayout());
        Insets insets5  = new Insets(2, 5, 2, 5);
        
        // *******************
        // *** Ship panel 
        // *******************
        JPanel shipPanel = new JPanel(new GridBagLayout());
        shipPanel.setOpaque(false);
        shipPanel.setBorder(new TitledBorder("Ship"));
        add(shipPanel, 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets5, 0, 0));
        
        shipPanel.add(new JLabel("Name:"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shipPanel.add(lblShipName, 
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        shipPanel.add(new JLabel("Call sign:"), 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        shipPanel.add(lblCallSign, 
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        // *******************
        // *** Route panel 
        // *******************
        JPanel routePanel = new JPanel(new GridBagLayout());
        routePanel.setOpaque(false);
        routePanel.setBorder(new TitledBorder("Route"));
        add(routePanel, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets5, 0, 0));
        
        routePanel.add(new JLabel("Name:"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblRouteName, 
                new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        routePanel.add(new JLabel("COG:"), 
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblCog, 
                new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        routePanel.add(new JLabel("SOG:"), 
                new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblSog, 
                new GridBagConstraints(3, 1, 1, 1, 0.5, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routePanel.add(new JLabel("TD:"), 
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblTd, 
                new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        routePanel.add(new JLabel("ETA:"), 
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        routePanel.add(lblETA, 
                new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        
        // *******************
        // *** Action panel 
        // *******************
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new TitledBorder("Actions"));
        add(actionPanel, 
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets5, 0, 0));

        ZoomToShipBtn.addActionListener(this);
        OpenShipDetailstextBtn.addActionListener(this);
        OpenVpDetalsBtn.addActionListener(this);
        HideOtherVoyagesBtn.addActionListener(this);
        
        ZoomToShipBtn.setFocusable(false);
        OpenShipDetailstextBtn.setFocusable(false);
        OpenVpDetalsBtn.setFocusable(false);
        HideOtherVoyagesBtn.setFocusable(false);
        
        actionPanel.add(ZoomToShipBtn, 
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        actionPanel.add(OpenShipDetailstextBtn, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        actionPanel.add(OpenVpDetalsBtn, 
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        actionPanel.add(HideOtherVoyagesBtn, 
                new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        // *******************
        // *** Reply panel 
        // *******************
        JPanel replyPanel = new JPanel(new GridBagLayout());
        replyPanel.setOpaque(false);
        replyPanel.setBorder(new TitledBorder("Reply"));
        add(replyPanel, 
                new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, NORTH, HORIZONTAL, insets5, 0, 0));

        sendBtn.addActionListener(this);
        sendBtn.requestFocus();
        txtMessage.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(txtMessage);
        scrollPane.setMinimumSize(new Dimension(180, 60));
        scrollPane.setPreferredSize(new Dimension(180, 60));
        
        replyPanel.add(new JLabel("Message:"), 
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        replyPanel.add(scrollPane, 
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        replyPanel.add(sendBtn, 
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, EAST, NONE, insets5, 0, 0));
        
        // Filler
        add(new JLabel(" "), 
                new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, NORTH, BOTH, insets5, 0, 0));
        
    }

    public void setAisHandler(AisHandler aisHandler) {
        this.aisHandler = aisHandler;
        checkAisData();
    }
    
    public void setChartPanel(ChartPanel chartPanel){
        this.chartPanel = chartPanel;
    }
    
    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
        
        
        lblShipName.setText("" + voyage.getMmsi());
        lblCallSign.setText("N/A");

        lblRouteName.setText(voyage.getRoute().getName());

        lblCog.setText("N/A");
        lblSog.setText("N/A");
        lblTd.setText(Formatter.formatShortDateTime(voyage.getRoute().getEtas().get(0)));
        lblETA.setText(Formatter.formatShortDateTime(voyage.getRoute()
                .getEtas().get(voyage.getRoute().getEtas().size() - 1)));
        
        checkAisData();
    }

    private void checkAisData() {
        if (aisHandler != null && voyage != null) {

            VesselTarget vesselTarget = aisHandler.getVesselTarget(voyage.getMmsi());
            if (vesselTarget != null && vesselTarget.getStaticData() != null) {
                VesselStaticData staticData = vesselTarget.getStaticData();

                lblShipName.setText(staticData.getTrimmedName());
                lblCallSign.setText(staticData.getTrimmedCallsign());
                lblCog.setText(Formatter.formatDegrees((double)vesselTarget.getPositionData().getCog(), 0));
                lblSog.setText(Formatter.formatCurrentSpeed((double)vesselTarget.getPositionData().getSog()));

            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        
        if (ae.getSource() == sendBtn) {
            voyageHandlingLayer.sendVoyage(txtMessage.getText());
        }

        if (ae.getSource() == ZoomToShipBtn) {
            VesselTarget vesselTarget = aisHandler.getVesselTarget(voyage.getMmsi());
            if (vesselTarget != null){
                chartPanel.goToPosition(vesselTarget.getPositionData().getPos());
            }
        }
        
        if (ae.getSource() == OpenShipDetailstextBtn) {
            
            EPD.getInstance().getNotificationCenter()
                .selectNotification(NotificationType.STRATEGIC_ROUTE, voyage.getId());
            EPD.getInstance().getNotificationCenter().setVisible(true);
            
        }
 
        if (ae.getSource() == OpenVpDetalsBtn) {
            
            RoutePropertiesDialog routePropertiesDialog = new RoutePropertiesDialog(
                    EPDShore.getInstance().getMainFrame(), 
                    chartPanel,
                    voyage.getRoute(), 
                    voyageHandlingLayer);
            routePropertiesDialog.setVisible(true);
            
        }
        
        if (ae.getSource() == HideOtherVoyagesBtn) {

            //If it\s visibile then toggle switched to 
            if (chartPanel.getVoyageLayer().isVisible()){
                HideOtherVoyagesBtn.setText("Show other voyages");
            }else{
                HideOtherVoyagesBtn.setText("Hide other voyages");
            }
            
            chartPanel.getVoyageLayer().setVisible(!chartPanel.getVoyageLayer().isVisible());
        }
    }
}
