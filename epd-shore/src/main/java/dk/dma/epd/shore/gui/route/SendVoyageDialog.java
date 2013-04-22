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
package dk.dma.epd.shore.gui.route;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.gui.utils.ComponentFrame;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.EnavServiceHandler;
import dk.dma.epd.shore.voyage.Voyage;
import dk.dma.epd.shore.voyage.VoyageManager;

public class SendVoyageDialog extends ComponentFrame implements MouseListener,
        ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    boolean locked;
    boolean alwaysInFront;
    MouseMotionListener[] actions;
    private JLabel moveHandler;
    private JPanel mapPanel;
    private JPanel masterPanel;

    private JLabel sendLbl;
    private JLabel cancelLbl;

    private static int moveHandlerHeight = 18;

    private JPanel mainPanel;
    private SendVoyageDialog sendRoute;
    private AisHandler aisHandler;

    private EnavServiceHandler enavServiceHandler;
    private VoyageManager voyageManager;
    
    private JLabel lblRoutenamelbl;
    private JLabel lblShipnamecallsignlbl;
    
    private Voyage voyage;
    private boolean modifiedRoute;
    private JTextArea textArea;

    /**
     * Create the frame.
     */
    public SendVoyageDialog() {
        super("Route Exchange", false, true, false, false);

        setResizable(false);
        setTitle("Route Exchange");
        setBounds(100, 100, 269, 257);

        initGUI();
    }

    public void initGUI() {

        // Strip off
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI())
                .setNorthPane(null);
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Map tools
        mapPanel = new JPanel();
        mapPanel.setPreferredSize(new Dimension(257, moveHandlerHeight));
        mapPanel.setOpaque(true);
        mapPanel.setBackground(Color.DARK_GRAY);
        mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(30, 30, 30)));

        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this,
                EPDShore.getMainFrame());
        mapPanel.addMouseListener(mml);
        mapPanel.addMouseMotionListener(mml);
        GridBagLayout gbl_mapPanel = new GridBagLayout();
        gbl_mapPanel.columnWidths = new int[] { 20, 217, 20, 0 };
        gbl_mapPanel.rowHeights = new int[] { 17, 0 };
        gbl_mapPanel.columnWeights = new double[] { 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        gbl_mapPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        mapPanel.setLayout(gbl_mapPanel);

        // Movehandler/Title dragable)
        moveHandler = new JLabel("STCC Route Suggestion", SwingConstants.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.addMouseListener(this);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
        actions = moveHandler.getListeners(MouseMotionListener.class);

        // Placeholder - for now
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel label = new JLabel();
        mapPanel.add(label, gbc);
        // moveHandler.setSize(140, moveHandlerHeight);
        // moveHandler.setPreferredSize(new Dimension(140, moveHandlerHeight));
        GridBagConstraints gbc_moveHandler = new GridBagConstraints();
        gbc_moveHandler.fill = GridBagConstraints.BOTH;
        gbc_moveHandler.insets = new Insets(0, 0, 0, 5);
        gbc_moveHandler.gridx = 1;
        gbc_moveHandler.gridy = 0;
        mapPanel.add(moveHandler, gbc_moveHandler);

        sendRoute = this;

        createGUIContent();

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(mapPanel, BorderLayout.NORTH);

        // The tools (only close for send route dialog)
        JPanel mapToolsPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0));
        mapToolsPanel.setOpaque(false);
        mapToolsPanel.setPreferredSize(new Dimension(20, 20));

        JLabel close = new JLabel(new ImageIcon(EPDShore.class.getClassLoader()
                .getResource("images/window/close.png")));
        close.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                sendRoute.setVisible(false);
            }

        });
        close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
        mapToolsPanel.add(close);
        GridBagConstraints gbc_mapToolsPanel = new GridBagConstraints();
        gbc_mapToolsPanel.fill = GridBagConstraints.BOTH;
        gbc_mapToolsPanel.gridx = 2;
        gbc_mapToolsPanel.gridy = 0;
        mapPanel.add(mapToolsPanel, gbc_mapToolsPanel);
        masterPanel.add(mainPanel, BorderLayout.SOUTH);

        masterPanel.setBackground(new Color(45, 45, 45));
        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));
        masterPanel.setSize(257, 269);

        this.setContentPane(masterPanel);

    }

    /**
     * Function for setting up custom GUI for the map frame
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void createGUIContent() {

        this.setBackground(GuiStyler.backgroundColor);
        mainPanel = new JPanel();

        mainPanel.setSize(257, 269 - moveHandlerHeight - 6);
        mainPanel.setPreferredSize(new Dimension(257,
                269 - moveHandlerHeight - 6));

        mainPanel.setLayout(null);

        mainPanel.setBackground(GuiStyler.backgroundColor);
        mainPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)));

        JPanel sendPanel = new JPanel();
        sendPanel.setBackground(GuiStyler.backgroundColor);
        sendPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "Send", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        sendPanel.setBounds(0, 19, 257, 269 - moveHandlerHeight - 6 - 19);
        mainPanel.add(sendPanel);
        sendPanel.setLayout(null);

        sendLbl = new JLabel("SEND", new ImageIcon(EPDShore.class
                .getClassLoader().getResource("images/buttons/ok.png")),
                SwingConstants.CENTER);
        sendLbl.setBounds(9, 195, 75, 20);
        GuiStyler.styleButton(sendLbl);
        sendPanel.add(sendLbl);
        sendLbl.setEnabled(false);

        // JButton cancelBtn = new JButton("Cancel");
        // cancelBtn.setBounds(145, 61, 89, 23);
        // sendPanel.add(cancelBtn);

        cancelLbl = new JLabel("CANCEL", new ImageIcon(EPDShore.class
                .getClassLoader().getResource("images/buttons/cancel.png")),
                SwingConstants.CENTER);
        GuiStyler.styleButton(cancelLbl);
        cancelLbl.setBounds(159, 195, 75, 20);
        sendPanel.add(cancelLbl);

        lblRoutenamelbl = new JLabel("routeNameLbl");
        lblRoutenamelbl.setHorizontalAlignment(SwingConstants.CENTER);
        lblRoutenamelbl.setBounds(9, 23, 225, 14);
        GuiStyler.styleText(lblRoutenamelbl);
        sendPanel.add(lblRoutenamelbl);

        lblShipnamecallsignlbl = new JLabel("shipnameCallsignLbl");
        lblShipnamecallsignlbl.setHorizontalAlignment(SwingConstants.CENTER);
        lblShipnamecallsignlbl.setBounds(9, 48, 225, 14);
        GuiStyler.styleText(lblShipnamecallsignlbl);
        sendPanel.add(lblShipnamecallsignlbl);

        textArea = new JTextArea();
        textArea.setBounds(9, 86, 225, 95);
        textArea.setLineWrap(true);

        JScrollPane sp = new JScrollPane(textArea);
        sp.setBounds(9, 86, 225, 95);
        sendPanel.add(sp);

        GuiStyler.styleArea(textArea);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)));
        textArea.setBorder(null);

        JLabel lblMessage = new JLabel("Message:");
        lblMessage.setBounds(9, 71, 75, 14);
        GuiStyler.styleText(lblMessage);
        sendPanel.add(lblMessage);

        sendLbl.addMouseListener(this);
        cancelLbl.addMouseListener(this);

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

        // if (arg0.getSource() == zoomLbl && route.getWaypoints() != null) {
        //
        // if (EPDShore.getMainFrame().getActiveMapWindow() != null) {
        // EPDShore.getMainFrame().getActiveMapWindow().getChartPanel()
        // .zoomToPoint(route.getWaypoints().getFirst().getPos());
        // } else if (EPDShore.getMainFrame().getMapWindows().size() > 0) {
        // EPDShore.getMainFrame().getMapWindows().get(0).getChartPanel()
        // .zoomToPoint(route.getWaypoints().getFirst().getPos());
        // }
        // }
        //
         if (arg0.getSource() == sendLbl && sendLbl.isEnabled()) {
             
             MonaLisaRouteStatus replyStatus = null;
             
             if (modifiedRoute){
                 replyStatus = MonaLisaRouteService.MonaLisaRouteStatus.NEGOTIATING;
             }else{
                 replyStatus = MonaLisaRouteService.MonaLisaRouteStatus.AGREED;
             }
             
             
             
           MonaLisaRouteService.MonaLisaRouteRequestReply reply = new MonaLisaRouteService.MonaLisaRouteRequestReply(textArea.getText(),
           voyage.getId(), aisHandler.getOwnShip().getMmsi(), System
                   .currentTimeMillis(), replyStatus, voyage.getRoute().getFullRouteData());

           enavServiceHandler.getMonaLisaNegotiationData().get(voyage.getId()).addReply(reply);
           enavServiceHandler.getMonaLisaNegotiationData().get(voyage.getId()).setStatus(reply.getStatus());
           enavServiceHandler.getMonaLisaNegotiationData().get(voyage.getId()).setHandled(true);
           
           
           if (replyStatus == MonaLisaRouteStatus.AGREED){
               voyageManager.addVoyage(voyage);
           }
           
           enavServiceHandler.sendReply(reply);
         }
         
         

         
        //
        // // int mmsiTarget = Integer.parseInt((String)
        // // mmsiListComboBox.getSelectedItem());
        // // mmsiTarget = 219230000;
        //
        // // AisServices service = ESD.getAisServices();
        //
        // if (route == null && routeListComboBox.getSelectedIndex() != -1) {
        // route = routeManager.getRoutes().get(
        // routeListComboBox.getSelectedIndex());
        // // System.out.println("no route");
        // }
        //
        // if (mmsi == -1) {
        // mmsi = Long.parseLong(mmsiListComboBox.getSelectedItem()
        // .toString());
        // }
        //
        // try {
        // enavServiceHandler.sendRouteSuggestion(mmsi,
        // route.getFullRouteData(), senderTxtField.getText(),
        // messageTxtField.getText());
        // messageTxtField.setText("");
        // } catch (Exception e) {
        // System.out.println("Failed to send route");
        // }
        //
        // // service.sendRouteSuggestion(mmsiTarget, route);
        //
        // // Send it
        // // System.out.println("Selected the mmsi: " +
        // // mmsiListComboBox.getSelectedItem() + " Hardcoded to: 219230000");
        // // System.out.println("Selected the route: " + route.getName());
        // // System.out.println("The route is index: " +
        // // routeListComboBox.getSelectedIndex());
        //
        // this.setVisible(false);
        //
        // this.mmsi = -1;
        // this.route = null;
        //
        // }
        if (arg0.getSource() == cancelLbl) {

            // Hide it
            this.setVisible(false);
        }
    }

    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;

        }
        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
        }
        if (obj instanceof VoyageManager){
            voyageManager = (VoyageManager) obj;
        }

    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        //
        // if (arg0.getSource() == nameComboBox && !loading) {
        // if (nameComboBox.getSelectedItem() != null) {
        //
        // mmsiListComboBox.setSelectedIndex(nameComboBox.getSelectedIndex());
        //
        //
        // try {
        // mmsi = Long.valueOf((String) mmsiListComboBox
        // .getSelectedItem());
        // } catch (Exception e) {
        // System.out.println("Failed to set mmsi " + mmsi);
        // }
        //
        // // System.out.println("mmsi selected to set to " + mmsi);
        // VesselTarget selectedShip = aisHandler.getVesselTargets().get(
        // mmsi);
        //
        // if (selectedShip != null) {
        //
        // if (selectedShip.getStaticData() != null) {
        // // TO DO
        // // nameLbl.setText(AisMessage.trimText(selectedShip
        // // .getStaticData().getName()));
        //
        // callsignLbl.setText(AisMessage.trimText(selectedShip
        // .getStaticData().getCallsign()));
        // } else {
        // // nameLbl.setText("N/A");
        // callsignLbl.setText("N/A");
        // }
        //
        // } else {
        // statusLbl.setText("The ship is not visible on AIS");
        //
        // }
        // }
        // }

        // if (arg0.getSource() == mmsiListComboBox && !loading) {
        //
        // if (mmsiListComboBox.getSelectedItem() != null) {
        //
        // nameComboBox.setSelectedIndex(mmsiListComboBox.getSelectedIndex());
        // try {
        // mmsi = Long.valueOf((String) mmsiListComboBox
        // .getSelectedItem());
        // } catch (Exception e) {
        // System.out.println("Failed to set mmsi " + mmsi);
        // }
        //
        // // System.out.println("mmsi selected to set to " + mmsi);
        // VesselTarget selectedShip = aisHandler.getVesselTargets().get(
        // mmsi);
        //
        // if (selectedShip != null) {
        //
        // if (selectedShip.getStaticData() != null) {
        // // TO DO
        // // nameLbl.setText(AisMessage.trimText(selectedShip
        // // .getStaticData().getName()));
        //
        // callsignLbl.setText(AisMessage.trimText(selectedShip
        // .getStaticData().getCallsign()));
        // } else {
        // // nameLbl.setText("N/A");
        // callsignLbl.setText("N/A");
        // }
        //
        // } else {
        // statusLbl.setText("The ship is not visible on AIS");
        //
        // }
        // }
        // }
        //
        // if (arg0.getSource() == routeListComboBox && !loading) {
        // // System.out.println("Selected route");
        // if (routeListComboBox.getSelectedItem() != null) {
        //
        // route = routeManager.getRoute(routeListComboBox
        // .getSelectedIndex());
        // routeLengthLbl.setText(Integer.toString(route.getWaypoints()
        // .size()));
        // }
        // // if (route.getWaypoints().size() > 8) {
        // // statusLbl
        // //
        // .setText("<html>The Route has more than 8 waypoints.<br>Only the first 8 will be sent to the ship</html>");
        // // } else {
        // // statusLbl.setText("");
        // // }
        //
        // }

    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    public void setModifiedRoute(boolean modifiedRoute) {
        this.modifiedRoute = modifiedRoute;
        sendLbl.setEnabled(true);
    }

    
    
    
    
}
