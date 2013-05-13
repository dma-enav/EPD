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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.route.RoutePropertiesDialog;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.NotificationCenter;
import dk.dma.epd.shore.voyage.Voyage;

import javax.swing.JButton;

public class VoyagePlanInfoPanel extends JPanel implements MouseListener {

    
    private static final long serialVersionUID = 1L;
    private JLabel moveHandler;
    private JPanel masterPanel;
    private JPanel notificationPanel;
    private static int moveHandlerHeight = 18;
    private JMapFrame parent;
    private Voyage voyage;
    private AisHandler aisHandler;

    private JLabel lblShipName;
    private JLabel lblCallSign;
    JLabel lblRouteName;
    JLabel lblCogSog;
    JLabel lblTd;
    JLabel lblETA;
    
    

    JLabel ZoomToShipBtn;
    JLabel closeBtn;
    JLabel OpenShipDetailstextBtn;
    JLabel OpenVpDetalsBtn;
    JLabel HideOtherVoyagesBtn;
    
    ChartPanel chartPanel;
    
    NotificationCenter notificationCenter;
    
    
    /**
     * Create the panel.
     * 
     * @param voyage
     */
    public VoyagePlanInfoPanel() {
        super();

        
        this.notificationCenter = EPDShore.getMainFrame().getNotificationCenter();
        
        // setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
                new Color(30, 30, 30), new Color(45, 45, 45)));
        // textLabel = new JLabel();
        // add(textLabel);
        // setVisible(false);
        // textLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        // textLabel.setBackground(new Color(83, 83, 83));
        // textLabel.setForeground(new Color(237, 237, 237));
        setBackground(new Color(83, 83, 83));
        setLayout(null);

        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Voyage Plan Info", SwingConstants.CENTER);
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(208, moveHandlerHeight));

        // Create the grid for the notifications
        notificationPanel = new JPanel();
        notificationPanel
                .setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        notificationPanel.setBackground(new Color(83, 83, 83));

        notificationPanel.setSize(208, 300 - moveHandlerHeight);
        notificationPanel.setPreferredSize(new Dimension(208,
                300 - moveHandlerHeight));

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.setBounds(0, 0, 208, 300);
        masterPanel.add(moveHandler, BorderLayout.NORTH);
        masterPanel.add(notificationPanel, BorderLayout.SOUTH);
        notificationPanel.setLayout(null);

        lblShipName = new JLabel("");
        lblShipName.setHorizontalAlignment(SwingConstants.CENTER);
        lblShipName.setBounds(0, 7, 204, 14);

        GuiStyler.styleText(lblShipName);

        notificationPanel.add(lblShipName);

        lblCallSign = new JLabel("()");
        lblCallSign.setHorizontalAlignment(SwingConstants.CENTER);
        lblCallSign.setBounds(0, 21, 204, 14);
        GuiStyler.styleText(lblCallSign);
        notificationPanel.add(lblCallSign);

        lblRouteName = new JLabel("Route Name");
        lblRouteName.setBounds(10, 32, 188, 14);
        GuiStyler.styleText(lblRouteName);
        notificationPanel.add(lblRouteName);

        lblCogSog = new JLabel("COG: xxx, SOG: xxx");
        lblCogSog.setBounds(10, 46, 188, 14);
        GuiStyler.styleText(lblCogSog);
        notificationPanel.add(lblCogSog);

        lblTd = new JLabel("TD:  xxxxxx");
        lblTd.setBounds(10, 60, 188, 14);
        GuiStyler.styleText(lblTd);
        notificationPanel.add(lblTd);

        lblETA = new JLabel("ETA");
        lblETA.setBounds(10, 74, 184, 14);
        GuiStyler.styleText(lblETA);
        notificationPanel.add(lblETA);

        ZoomToShipBtn = new JLabel("Zoom to ship in center");
        ZoomToShipBtn.setHorizontalAlignment(SwingConstants.CENTER);
        ZoomToShipBtn.setBounds(34, 102, 140, 25);
        ZoomToShipBtn.addMouseListener(this);
        GuiStyler.styleButton(ZoomToShipBtn);
        notificationPanel.add(ZoomToShipBtn);

        OpenShipDetailstextBtn = new JLabel("Open ship details");
        OpenShipDetailstextBtn.setHorizontalAlignment(SwingConstants.CENTER);
        OpenShipDetailstextBtn.setBounds(34, 134, 140, 25);
        GuiStyler.styleButton(OpenShipDetailstextBtn);
        OpenShipDetailstextBtn.addMouseListener(this);

        notificationPanel.add(OpenShipDetailstextBtn);

        OpenVpDetalsBtn = new JLabel("Open VP details");
        OpenVpDetalsBtn.setHorizontalAlignment(SwingConstants.CENTER);
        OpenVpDetalsBtn.setBounds(34, 166, 140, 25);
        GuiStyler.styleButton(OpenVpDetalsBtn);
        notificationPanel.add(OpenVpDetalsBtn);
        OpenVpDetalsBtn.addMouseListener(this);

        HideOtherVoyagesBtn = new JLabel("Hide other voyages");
        HideOtherVoyagesBtn.setHorizontalAlignment(SwingConstants.CENTER);
        HideOtherVoyagesBtn.setBounds(34, 198, 140, 25);
        GuiStyler.styleButton(HideOtherVoyagesBtn);
        HideOtherVoyagesBtn.addMouseListener(this);

        notificationPanel.add(HideOtherVoyagesBtn);

        closeBtn = new JLabel("Close");
        closeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        closeBtn.setBounds(127, 246, 71, 25);
        GuiStyler.styleButton(closeBtn);
        notificationPanel.add(closeBtn);

        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));
        add(masterPanel);
        
        
        closeBtn.addMouseListener(this);



    }


    public void setParent(JMapFrame parent) {
        this.parent = parent;

        VoyageInfoPlanMoveMouseListener mml = new VoyageInfoPlanMoveMouseListener(
                this, parent);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);

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
        
        
        lblShipName.setText("MMSI: " + voyage.getMmsi());
        lblCallSign.setText("N/A");

        lblRouteName.setText(voyage.getRoute().getName());

        lblCogSog.setText("COG: N/A, SOG: N/A");
        lblTd.setText("TD: " + Formatter.formatShortDateTime(voyage.getRoute().getEtas()
                .get(0)));
        lblETA.setText("ETA: " + Formatter.formatShortDateTime(voyage.getRoute()
                .getEtas().get(voyage.getRoute().getEtas().size() - 1)));
        
        checkAisData();
    }

    private void checkAisData() {
        if (aisHandler != null && voyage != null) {

            if (aisHandler.getVesselTargets().get(voyage.getMmsi())
                    .getStaticData() != null) {
                VesselStaticData staticData = aisHandler.getVesselTargets()
                        .get(voyage.getMmsi()).getStaticData();

                lblShipName.setText(staticData.getName());
                lblCallSign.setText("(" + staticData.getCallsign().trim() + ")");
                lblCogSog.setText("COG: "
                        + aisHandler.getVesselTargets().get(voyage.getMmsi())
                                .getPositionData().getCog()
                        + ", SOG: "
                        + Formatter.formatCurrentSpeed((double) aisHandler
                                .getVesselTargets().get(voyage.getMmsi())
                                .getPositionData().getSog()));

            }

        }
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
        if (arg0.getSource() == closeBtn && closeBtn.isEnabled()) {
            this.setVisible(false);
        }

        if (arg0.getSource() == ZoomToShipBtn && ZoomToShipBtn.isEnabled()) {
            if (aisHandler.getVesselTargets().containsKey(voyage.getMmsi())){
                chartPanel.zoomToPoint(aisHandler.getVesselTargets().get(voyage.getMmsi()).getPositionData().getPos());
            }
        }
        
        if (arg0.getSource() == OpenShipDetailstextBtn && OpenShipDetailstextBtn.isEnabled()) {
            
            notificationCenter.showMonaLisaMsg(2, voyage.getId());
            
            //Notification Center
            
            
            
            
        }
 
        if (arg0.getSource() == OpenVpDetalsBtn && OpenVpDetalsBtn.isEnabled()) {
            //Display the route
            
            System.out.println(voyage.getId());
            
            RoutePropertiesDialog routePropertiesDialog = new RoutePropertiesDialog(
                    EPDShore.getMainFrame(), voyage.getRoute());
            routePropertiesDialog.setVisible(true);
            
        }
        
        if (arg0.getSource() == HideOtherVoyagesBtn && HideOtherVoyagesBtn.isEnabled()) {
            
//            HideOtherVoyagesBtn.
            
            
            //If it\s visibile then toggle switched to 
            if (chartPanel.getVoyageLayer().isVisible()){
                HideOtherVoyagesBtn.setText("Show other voyages");
            }else{
                HideOtherVoyagesBtn.setText("Hide other voyages");
            }
            
            chartPanel.getVoyageLayer().setVisible(!chartPanel.getVoyageLayer().isVisible());
            //Toggle voyage layer
            
        }

    }
}
