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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.ComponentFrame;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.route.RouteManager;
import dk.dma.epd.shore.service.RouteSuggestionHandler;

public class SendRouteDialog extends ComponentFrame implements MouseListener,
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
    private JLabel zoomLbl;

    private static int moveHandlerHeight = 18;

    private JPanel mainPanel;
    private SendRouteDialog sendRoute;
    private JComboBox<String> mmsiListComboBox;
    private JLabel callsignLbl;
    private JComboBox<String> nameComboBox;
    private JComboBox<String> routeListComboBox;
    private JLabel routeLengthLbl;
    private JLabel statusLbl;
    JTextArea messageTxtField;
    JTextField senderTxtField;
    private AisHandler aisHandler;
    private RouteManager routeManager;

    private Route route;
    private long mmsi = -1;
    private boolean loading;

    private RouteSuggestionHandler routeSuggestionHandler;

    /**
     * Create the frame.
     */
    public SendRouteDialog() {
        super("Route Exchange", false, true, false, false);

        setResizable(false);
        setTitle("Route Exchange");
        setBounds(100, 100, 275, 520 + moveHandlerHeight);

        initGUI();
    }

    public void initGUI() {

        // Strip off
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI())
                .setNorthPane(null);
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Map tools
        mapPanel = new JPanel(new GridLayout(1, 3));
        mapPanel.setPreferredSize(new Dimension(520, moveHandlerHeight));
        mapPanel.setOpaque(true);
        mapPanel.setBackground(Color.DARK_GRAY);
        mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(30, 30, 30)));

        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this,
                EPDShore.getInstance().getMainFrame());
        mapPanel.addMouseListener(mml);
        mapPanel.addMouseMotionListener(mml);

        // Placeholder - for now
        mapPanel.add(new JLabel());

        // Movehandler/Title dragable)
        moveHandler = new JLabel("Route Exchange", SwingConstants.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.addMouseListener(this);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
        actions = moveHandler.getListeners(MouseMotionListener.class);
        mapPanel.add(moveHandler);

        sendRoute = this;

        // The tools (only close for send route dialog)
        JPanel mapToolsPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0));
        mapToolsPanel.setOpaque(false);
        mapToolsPanel.setPreferredSize(new Dimension(60, 50));

        JLabel close = new JLabel(EPDShore.res().getCachedImageIcon("images/window/close.png"));
        close.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                sendRoute.setVisible(false);
            }

        });
        close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
        mapToolsPanel.add(close);
        mapPanel.add(mapToolsPanel);

        createGUIContent();

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(mapPanel, BorderLayout.NORTH);
        masterPanel.add(mainPanel, BorderLayout.SOUTH);

        masterPanel.setBackground(new Color(45, 45, 45));
        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));

        this.setContentPane(masterPanel);

    }

    /**
     * Function for setting up custom GUI for the map frame
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void createGUIContent() {

        this.setBackground(GuiStyler.backgroundColor);
        mainPanel = new JPanel();

        mainPanel.setSize(264, 520);
        mainPanel.setPreferredSize(new Dimension(264, 520));

        mainPanel.setLayout(null);

        mainPanel.setBackground(GuiStyler.backgroundColor);
        mainPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)));

        JPanel targetPanel = new JPanel();
        targetPanel.setBackground(GuiStyler.backgroundColor);
        targetPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "Target", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        targetPanel.setBounds(10, 25, 244, 114);
        mainPanel.add(targetPanel);
        targetPanel.setLayout(null);

        JLabel mmsiTitleLbl = new JLabel("MMSI:");
        mmsiTitleLbl.setBounds(10, 22, 46, 14);
        targetPanel.add(mmsiTitleLbl);
        GuiStyler.styleText(mmsiTitleLbl);

        JLabel nameTitlelbl = new JLabel("Name:");
        nameTitlelbl.setBounds(10, 47, 46, 14);
        targetPanel.add(nameTitlelbl);
        GuiStyler.styleText(nameTitlelbl);

        mmsiListComboBox = new JComboBox();
        mmsiListComboBox.setModel(new DefaultComboBoxModel());
        mmsiListComboBox.setBounds(91, 21, 88, 17);
        mmsiListComboBox.setEnabled(true);
        GuiStyler.styleDropDown(mmsiListComboBox);
        targetPanel.add(mmsiListComboBox);

        JLabel callsignTitlelbl = new JLabel("Call Sign:");
        callsignTitlelbl.setBounds(10, 72, 46, 14);
        targetPanel.add(callsignTitlelbl);
        GuiStyler.styleText(callsignTitlelbl);

        callsignLbl = new JLabel("N/A");
        callsignLbl.setBounds(91, 72, 143, 14);
        targetPanel.add(callsignLbl);
        GuiStyler.styleText(callsignLbl);

        nameComboBox = new JComboBox();
        nameComboBox.setModel(new DefaultComboBoxModel());
        nameComboBox.setBounds(91, 47, 143, 17);
        targetPanel.add(nameComboBox);
        GuiStyler.styleDropDown(nameComboBox);

        JPanel routePanel = new JPanel();
        routePanel.setBackground(GuiStyler.backgroundColor);
        routePanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "Route", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        routePanel.setBounds(10, 153, 244, 114);
        mainPanel.add(routePanel);
        routePanel.setLayout(null);

        JLabel routeNameTitleLbl = new JLabel("Route name:");
        routeNameTitleLbl.setBounds(10, 23, 77, 14);
        routePanel.add(routeNameTitleLbl);
        GuiStyler.styleText(routeNameTitleLbl);

        JLabel routeLengthTitleLbl = new JLabel("Route Length:");
        routeLengthTitleLbl.setBounds(10, 48, 77, 14);
        routePanel.add(routeLengthTitleLbl);
        GuiStyler.styleText(routeLengthTitleLbl);

        // JButton zoomToBtn = new JButton("Zoom to");
        // zoomToBtn.setBounds(10, 80, 89, 23);
        // routePanel.add(zoomToBtn);

        zoomLbl = new JLabel("Zoom To", EPDShore.res().getCachedImageIcon("images/buttons/zoom.png"),
                SwingConstants.CENTER);
        GuiStyler.styleButton(zoomLbl);
        zoomLbl.setBounds(10, 80, 75, 20);
        routePanel.add(zoomLbl);

        routeListComboBox = new JComboBox();
        routeListComboBox.setModel(new DefaultComboBoxModel());
        routeListComboBox.setEnabled(false);
        routeListComboBox.setBounds(91, 20, 143, 20);
        routePanel.add(routeListComboBox);
        GuiStyler.styleDropDown(routeListComboBox);

        routeLengthLbl = new JLabel("N/A");
        routeLengthLbl.setBounds(91, 48, 143, 14);
        routePanel.add(routeLengthLbl);
        GuiStyler.styleText(routeLengthLbl);

        JPanel informationPanel = new JPanel();
        informationPanel.setBackground(GuiStyler.backgroundColor);
        informationPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "Route", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        informationPanel.setBounds(10, 278, 244, 114);
        mainPanel.add(informationPanel);
        informationPanel.setLayout(null);

        JLabel senderTitleLbl = new JLabel("Sender:");
        senderTitleLbl.setBounds(10, 23, 77, 14);
        informationPanel.add(senderTitleLbl);
        GuiStyler.styleText(senderTitleLbl);

        senderTxtField = new JTextField("DMA Shore");
        senderTxtField.setBounds(95, 23, 77, 14);
        informationPanel.add(senderTxtField);
        GuiStyler.styleTextFields(senderTxtField);

        JLabel messageTitleLbl = new JLabel("Message:");
        messageTitleLbl.setBounds(10, 48, 77, 14);
        informationPanel.add(messageTitleLbl);
        GuiStyler.styleText(messageTitleLbl);

        messageTxtField = new JTextArea("Route Suggestion");
        messageTxtField.setBounds(95, 48, 135, 45);
        messageTxtField.setLineWrap(true);
        // messageTxtField.setWrapStyleWord(true);
        // messageTxtField.setColumns(20);
        // messageTxtField.setRows(5);
        JScrollPane sp = new JScrollPane(messageTxtField);
        sp.setBounds(95, 48, 135, 45);
        informationPanel.add(sp);
        // sp.setBorder(GuiStyler.border);
        sp.setBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)));
        // sp.setBorder(null);
        // .setBackground(GuiStyler.backgroundColor);
        GuiStyler.styleArea(messageTxtField);
        messageTxtField.setBorder(null);

        JPanel sendPanel = new JPanel();
        sendPanel.setBackground(GuiStyler.backgroundColor);
        sendPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "Send", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        sendPanel.setBounds(10, 406, 244, 95);
        mainPanel.add(sendPanel);
        sendPanel.setLayout(null);

        sendLbl = new JLabel("SEND", EPDShore.res().getCachedImageIcon("images/buttons/ok.png"),
                SwingConstants.CENTER);
        sendLbl.setBounds(10, 61, 75, 20);
        GuiStyler.styleButton(sendLbl);
        sendPanel.add(sendLbl);
        sendLbl.setEnabled(false);

        // JButton sendBtn = new JButton("Send");
        // sendBtn.setBounds(10, 61, 89, 23);
        // sendPanel.add(sendBtn);

        statusLbl = new JLabel("");
        statusLbl.setBounds(10, 11, 224, 39);
        sendPanel.add(statusLbl);
        GuiStyler.styleText(statusLbl);

        // JButton cancelBtn = new JButton("Cancel");
        // cancelBtn.setBounds(145, 61, 89, 23);
        // sendPanel.add(cancelBtn);

        cancelLbl = new JLabel("CANCEL", EPDShore.res().getCachedImageIcon("images/buttons/cancel.png"),
                SwingConstants.CENTER);
        GuiStyler.styleButton(cancelLbl);
        cancelLbl.setBounds(160, 61, 75, 20);
        sendPanel.add(cancelLbl);

        sendLbl.addMouseListener(this);
        cancelLbl.addMouseListener(this);
        zoomLbl.addMouseListener(this);

        mmsiListComboBox.addActionListener(this);
        nameComboBox.addActionListener(this);
        routeListComboBox.addActionListener(this);

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

        if (arg0.getSource() == zoomLbl && route.getWaypoints() != null) {

            if (EPDShore.getInstance().getMainFrame().getActiveMapWindow() != null) {
                EPDShore.getInstance().getMainFrame().getActiveMapWindow().getChartPanel()
                        .zoomToPoint(route.getWaypoints().getFirst().getPos());
            } else if (EPDShore.getInstance().getMainFrame().getMapWindows().size() > 0) {
                EPDShore.getInstance().getMainFrame().getMapWindows().get(0).getChartPanel()
                        .zoomToPoint(route.getWaypoints().getFirst().getPos());
            }
        }

        if (arg0.getSource() == sendLbl && sendLbl.isEnabled()) {

            // int mmsiTarget = Integer.parseInt((String)
            // mmsiListComboBox.getSelectedItem());
            // mmsiTarget = 219230000;

            // AisServices service = ESD.getAisServices();

            if (route == null && routeListComboBox.getSelectedIndex() != -1) {
                route = routeManager.getRoutes().get(
                        routeListComboBox.getSelectedIndex());
                // System.out.println("no route");
            }

            if (mmsi == -1) {
                mmsi = Long.parseLong(mmsiListComboBox.getSelectedItem()
                        .toString());
            }

            try {
                routeSuggestionHandler.sendRouteSuggestion(mmsi,
                        route.getFullRouteData(), senderTxtField.getText(),
                        messageTxtField.getText());
                messageTxtField.setText("");
            } catch (Exception e) {
                System.out.println("Failed to send route");
            }

            // service.sendRouteSuggestion(mmsiTarget, route);

            // Send it
            // System.out.println("Selected the mmsi: " +
            // mmsiListComboBox.getSelectedItem() + " Hardcoded to: 219230000");
            // System.out.println("Selected the route: " + route.getName());
            // System.out.println("The route is index: " +
            // routeListComboBox.getSelectedIndex());

            this.setVisible(false);

            this.mmsi = -1;
            this.route = null;

        }
        if (arg0.getSource() == cancelLbl) {

            // Hide it
            this.setVisible(false);
        }

        if (arg0.getSource() == zoomLbl) {

            // go to the route on the map

        }
    }

    public void loadData() {
        // System.out.println("load data");
        loading = true;
        mmsiListComboBox.removeAllItems();

        // mmsi://

        // Remove duplicates
        List<String> mmsi = new ArrayList<String>();

        for (int i = 0; i < routeSuggestionHandler.getRouteSuggestionServiceList().size(); i++) {
            mmsi.add(routeSuggestionHandler.getRouteSuggestionServiceList().get(i).getId()
                    .toString().split("//")[1]);
        }

        HashSet<String> hs = new HashSet<String>();
        hs.addAll(mmsi);
        mmsi.clear();
        mmsi.addAll(hs);

        if (routeSuggestionHandler.getRouteSuggestionServiceList().size() > 0) {
            mmsiListComboBox.setEnabled(true);
            for (int i = 0; i < mmsi.size(); i++) {
                mmsiListComboBox.addItem(mmsi.get(i));
            }
        }

        routeListComboBox.removeAllItems();

        for (int i = 0; i < routeManager.getRoutes().size(); i++) {
            routeListComboBox.addItem(routeManager.getRoutes().get(i).getName()
                    + "                                                 " + i);
        }

        nameComboBox.removeAllItems();
        for (int i = 0; i < mmsi.size(); i++) {

            VesselTarget selectedShip = aisHandler.getVesselTarget(Long.parseLong(mmsi.get(i)));
            if (selectedShip != null) {

                if (selectedShip.getStaticData() != null) {
                    nameComboBox
                            .addItem(selectedShip.getStaticData().getName());
                } else {
                    nameComboBox.addItem("N/A");

                }
            } else {
                nameComboBox.addItem("N/A");

            }
        }

        loading = false;

    }

    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;

        }
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager) obj;
        }

        if (obj instanceof RouteSuggestionHandler) {
            routeSuggestionHandler = (RouteSuggestionHandler) obj;
        }

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == nameComboBox && !loading) {
            if (nameComboBox.getSelectedItem() != null) {

                mmsiListComboBox.setSelectedIndex(nameComboBox
                        .getSelectedIndex());

                // try {
                // mmsi = Long.valueOf((String) mmsiListComboBox
                // .getSelectedItem());
                // } catch (Exception e) {
                // System.out.println("Failed to set mmsi " + mmsi);
                // }
                //
                // // System.out.println("mmsi selected to set to " + mmsi);
                // VesselTarget selectedShip =
                // aisHandler.getVesselTargets().get(
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
            }
        }

        if (arg0.getSource() == mmsiListComboBox && !loading) {

            if (mmsiListComboBox.getSelectedItem() != null) {

                nameComboBox.setSelectedIndex(mmsiListComboBox
                        .getSelectedIndex());
                try {
                    mmsi = Long.valueOf((String) mmsiListComboBox
                            .getSelectedItem());
                } catch (Exception e) {
                    System.out.println("Failed to set mmsi " + mmsi);
                }

                // System.out.println("mmsi selected to set to " + mmsi);
                VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi);

                if (selectedShip != null) {

                    if (selectedShip.getStaticData() != null) {
                        // TO DO
                        // nameLbl.setText(AisMessage.trimText(selectedShip
                        // .getStaticData().getName()));

                        callsignLbl.setText(AisMessage.trimText(selectedShip
                                .getStaticData().getCallsign()));
                    } else {
                        // nameLbl.setText("N/A");
                        callsignLbl.setText("N/A");
                    }

                } else {
                    statusLbl.setText("The ship is not visible on AIS");

                }
            }
        }

        if (arg0.getSource() == routeListComboBox && !loading) {
            // System.out.println("Selected route");
            if (routeListComboBox.getSelectedItem() != null) {

                route = routeManager.getRoute(routeListComboBox
                        .getSelectedIndex());
                routeLengthLbl.setText(Integer.toString(route.getWaypoints()
                        .size()));
            }
            // if (route.getWaypoints().size() > 8) {
            // statusLbl
            // .setText("<html>The Route has more than 8 waypoints.<br>Only the first 8 will be sent to the ship</html>");
            // } else {
            // statusLbl.setText("");
            // }

        }

    }

    public void setSelectedMMSI(long mmsi) {
        this.mmsi = mmsi;
        selectAndLoad();
    }

    public void setSelectedRoute(Route route) {

        this.route = route;
        selectAndLoad();
    }

    private void selectAndLoad() {
        loadData();

        if (mmsi != -1 && mmsiListComboBox.getItemCount() > 0) {
            mmsiListComboBox.setEnabled(true);
            for (int i = 0; i < mmsiListComboBox.getItemCount(); i++) {
                if (mmsiListComboBox.getItemAt(i).equals(Long.toString(mmsi))) {
                    mmsiListComboBox.setSelectedIndex(i);
                }
            }

        }

        nameComboBox.setSelectedIndex(mmsiListComboBox.getSelectedIndex());

        if (route != null
                && EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                        .getRouteManager().getRoutes().size() > 0) {
            routeListComboBox.setEnabled(true);
            for (int i = 0; i < EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                    .getRouteManager().getRoutes().size(); i++) {
                if (EPDShore.getInstance().getMainFrame().getRouteManagerDialog()
                        .getRouteManager().getRoutes().get(i) == route) {
                    routeListComboBox.setSelectedIndex(i);
                }
            }
        }

        if (mmsi == -1 && mmsiListComboBox.getItemCount() > 0) {
            mmsi = Long
                    .parseLong(mmsiListComboBox.getSelectedItem().toString());
        }

        VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi);

        if (selectedShip != null) {

            if (selectedShip.getStaticData() != null) {
                // nameLbl.setText(AisMessage.trimText(selectedShip
                // .getStaticData().getName()));

                callsignLbl.setText(AisMessage.trimText(selectedShip
                        .getStaticData().getCallsign()));
            } else {
                // nameLbl.setText("N/A");
                callsignLbl.setText("N/A");
            }
        }

        if (mmsi != -1 && route != null) {
            sendLbl.setEnabled(true);
        }

    }

}
