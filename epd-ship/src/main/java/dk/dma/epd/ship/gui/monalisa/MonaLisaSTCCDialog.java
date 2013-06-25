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
package dk.dma.epd.ship.gui.monalisa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;

import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.common.prototype.gui.route.RoutePropertiesDialogCommon;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.layers.route.RouteLayer;
import dk.dma.epd.ship.monalisa.MonaLisaHandler;

public class MonaLisaSTCCDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    JLabel routeName;
    JLabel lblDate;
    JLabel lblTime;
    JLabel lblStatus;

    JLabel dateField;
    JLabel timeField;
    JLabel statusField;
    JLabel lblPostRoute;
    JButton btnMain;

    JLabel lblRouteTitle;
    JTextArea lblChanges;
    JTextArea lblMessages;

    JTextArea chatMessages;

    private JButton btnAccept;
    private JButton btnReject;
    private JButton btnViewRoute;
    private JButton btnWait;

    private JTextArea routeMessage;
    // private EnavServiceHandler enavServiceHandler;
    private MonaLisaHandler monaLisaHandler;
    RouteLayer routeLayer;

    private boolean isActive;

    private Dimension defaultSize = new Dimension(187, 208);
    private Dimension negotiationSize = new Dimension(355, 425);

    JPanel routeAcceptedPanel;
    JPanel routeNotAcceptedPanel;

    private Route latestReceivedRoute;
//    private MonaLisaRouteRequestReply reply;

    long transactionID;

     private MainFrame mainFrame;

    /**
     * Create the dialog.
     */
    public MonaLisaSTCCDialog(MainFrame mainFrame) {

        super(mainFrame, "STCC Info", false);
         this.mainFrame = mainFrame;

        setAlwaysOnTop(true);
        setResizable(false);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        

        // Default
        // setBounds(100, 100, 187, 208);

        // Modification
        setBounds(100, 100, 355, 425);
        
        setLocationRelativeTo(mainFrame);

        setResizable(false);

        monaLisaHandler = EPDShip.getMonaLisaHandler();

        // enavServiceHandler = EPDShip.getEnavServiceHandler();
        // enavServiceHandler.setMonaLisaSTCCDialog(this);

        initGui();

        this.setVisible(false);

    }

    private void initGui() {
        routeAcceptedPanel = new JPanel();

        routeAcceptedPanel.setLayout(null);

        JLabel lblRouteName = new JLabel("Route");
        lblRouteName.setBounds(10, 5, 34, 14);
        lblRouteName.setFont(new Font("Tahoma", Font.BOLD, 11));
        routeAcceptedPanel.add(lblRouteName);

        routeName = new JLabel("N/A");
        routeName.setBounds(54, 5, 128, 14);
        routeName.setFont(new Font("Tahoma", Font.BOLD, 11));
        routeAcceptedPanel.add(routeName);

        lblPostRoute = new JLabel("sent to STCC");
        lblPostRoute.setBounds(10, 20, 172, 14);
        lblPostRoute.setFont(new Font("Tahoma", Font.BOLD, 11));
        routeAcceptedPanel.add(lblPostRoute);

        lblDate = new JLabel("Date:");
        lblDate.setBounds(10, 43, 27, 14);
        routeAcceptedPanel.add(lblDate);

        lblTime = new JLabel("Time:");
        lblTime.setBounds(11, 59, 26, 14);
        routeAcceptedPanel.add(lblTime);

        lblStatus = new JLabel("Status:");
        lblStatus.setBounds(9, 77, 35, 14);
        routeAcceptedPanel.add(lblStatus);

        btnMain = new JButton("Cancel request");
        btnMain.setBounds(21, 146, 139, 23);
        routeAcceptedPanel.add(btnMain);
        btnMain.addActionListener(this);

        dateField = new JLabel("N/A");
        dateField.setBounds(54, 43, 128, 14);
        routeAcceptedPanel.add(dateField);

        timeField = new JLabel("N/A");
        timeField.setBounds(54, 59, 128, 14);
        routeAcceptedPanel.add(timeField);

        statusField = new JLabel("N/A");
        statusField.setBounds(54, 77, 128, 14);
        routeAcceptedPanel.add(statusField);

        routeMessage = new JTextArea();
        routeMessage.setBackground(new Color(240, 240, 240));
        routeMessage.setBorder(null);
        routeMessage.setLineWrap(true);
        routeMessage.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(routeMessage);
        scrollPane.setBorder(null);
        scrollPane.setBounds(10, 93, 161, 42);
        routeAcceptedPanel.add(scrollPane);

        routeNotAcceptedPanel = new JPanel();

        routeNotAcceptedPanel.setLayout(null);

        lblRouteTitle = new JLabel("Route \"routename\" STCC Change request");
        lblRouteTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblRouteTitle.setBounds(10, 11, 273, 14);
        routeNotAcceptedPanel.add(lblRouteTitle);

        JLabel lblChange = new JLabel("Route Changes:");
        lblChange.setBounds(10, 30, 128, 14);
        routeNotAcceptedPanel.add(lblChange);

        lblChanges = new JTextArea("N/A");
        lblChanges.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblChanges.setBackground(new Color(240, 240, 240));
        lblChanges.setBorder(null);
        lblChanges.setLineWrap(true);
        lblChanges.setEditable(false);

        JScrollPane spChanges = new JScrollPane(lblChanges);
        // spChanges.setBorder(null);
        spChanges.setBounds(10, 46, 329, 71);
        routeNotAcceptedPanel.add(spChanges);

        JLabel lblMessageTitle = new JLabel("Message:");
        lblMessageTitle.setBounds(10, 120, 115, 14);
        routeNotAcceptedPanel.add(lblMessageTitle);

        lblMessages = new JTextArea("N/A");
        lblMessages.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblMessages.setBackground(new Color(240, 240, 240));
        lblMessages.setBorder(null);
        lblMessages.setLineWrap(true);
        lblMessages.setEditable(false);

        JScrollPane spMessage = new JScrollPane(lblMessages);
        // spMessage.setBorder(null);
        spMessage.setBounds(10, 135, 329, 71);
        routeNotAcceptedPanel.add(spMessage);

        btnReject = new JButton("Reject");
        btnReject.setBounds(10, 363, 70, 23);
        routeNotAcceptedPanel.add(btnReject);
        btnReject.addActionListener(this);

        btnViewRoute = new JButton("View Route");
        btnViewRoute.setBounds(10, 298, 87, 23);
        btnViewRoute.addActionListener(this);
        routeNotAcceptedPanel.add(btnViewRoute);

        btnAccept = new JButton("Accept");
        btnAccept.setBounds(194, 363, 145, 23);
        btnAccept.addActionListener(this);
        routeNotAcceptedPanel.add(btnAccept);

        chatMessages = new JTextArea("");
        chatMessages.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chatMessages.setBackground(Color.WHITE);
        chatMessages.setLineWrap(true);
        chatMessages.setBorder(null);

        JScrollPane chatSp = new JScrollPane(chatMessages);
        chatSp.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
        chatSp.setBounds(10, 224, 329, 63);
        routeNotAcceptedPanel.add(chatSp);

        // getContentPane().add(routeAcceptedPanel, BorderLayout.CENTER);
        getContentPane().add(routeNotAcceptedPanel, BorderLayout.CENTER);

        JLabel lblReplyMessage = new JLabel("Reply message:");
        lblReplyMessage.setBounds(10, 207, 145, 14);
        routeNotAcceptedPanel.add(lblReplyMessage);

        JButton btnWait = new JButton("Wait");
        btnWait.setBounds(107, 298, 89, 23);
        routeNotAcceptedPanel.add(btnWait);
        btnWait.addActionListener(this);
    }

    public void setRouteName(Route route, long transactionID) {
        this.latestReceivedRoute = route;
        this.transactionID = transactionID;

        isActive = true;

        routeName.setText("\"" + latestReceivedRoute.getName() + "\"");

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(date);
        dateField.setText(formattedDate);

        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm");
        String formattedTime = sdt.format(date);
        timeField.setText(formattedTime);

        statusField.setText("Pending");

        activateDefaultLayout();

    }

    public void changeModifiedAcceptBtn() {
        btnAccept.setText("Send Modified");
    }

    private void activateDefaultLayout() {
        setSize(defaultSize);

        System.out.println("Activating normal");

        lblMessages.setText("");
        chatMessages.setText("");

        getContentPane().remove(routeNotAcceptedPanel);
        getContentPane().add(routeAcceptedPanel, BorderLayout.CENTER);
        
        setLocationRelativeTo(mainFrame);
    }

    private void activateNegotiationLayout() {
        lblMessages.setText("");
        chatMessages.setText("");
        setSize(negotiationSize);

        getContentPane().remove(routeAcceptedPanel);
        getContentPane().add(routeNotAcceptedPanel, BorderLayout.CENTER);

        
        setLocationRelativeTo(mainFrame);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInActive() {
        isActive = false;
    }

    public void handleReply(MonaLisaRouteRequestReply reply) {
//        this.reply = reply;
        this.setRouteName(new Route(reply.getRoute()), this.transactionID);

        btnAccept.setText("Accept");

        // if (EPDShip.getRouteManager().getActiveRouteIndex() != -1){
        // btnAccept.setText("Accept and Activate");
        // }else{
        //
        // }

        // Reply is in
        if (reply.getStatus() == MonaLisaRouteStatus.AGREED) {
            this.setSize(defaultSize);
            statusField.setText("Route Agreed");
            lblPostRoute.setText("STCC Agreed");
            lblDate.setText("Valid");
            statusField.setText("Route agreed");
            btnMain.setText("Acknowledge");
            // btnCancelRequest.setBackground(Color.GREEN);
            // btnCancelRequest.setForeground(Color.GREEN);
            routeMessage.setText(reply.getMessage());
            setInActive();
            // routeLayer.stopRouteAnimated();

            activateDefaultLayout();

        } else if (reply.getStatus() == MonaLisaRouteStatus.NEGOTIATING) {
            activateNegotiationLayout();

            lblRouteTitle.setText("Route \"" + reply.getRoute().getName()
                    + "\" STCC Change request");
            lblChanges.setText(findChanges());
            lblMessages.setText(reply.getMessage());

            // findChanges();

        }

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnMain) {

            // Cancel request
            if (isActive) {
                setInActive();
                monaLisaHandler.cancelRouteRequest(transactionID);
                this.setVisible(false);
            } else {
                // Is not active and button pressed - when can this happen?
                // its being acked?
                setInActive();
                monaLisaHandler.sendAgreeMsg(transactionID,
                        chatMessages.getText());
                this.setVisible(false);

            }

            btnMain.setText("Cancel request");
        }

        if (e.getSource() == btnAccept) {

            System.out.println("btn accept");
            // Accept or send modified clicked, let monalisahandler figure it
            // out
            monaLisaHandler.sendReply(chatMessages.getText());
            // this.setVisible(false);
            btnAccept.setText("Accept");
        }
        if (e.getSource() == btnReject) {

            // Send reject message
            monaLisaHandler.sendReject(chatMessages.getText());
            this.setVisible(false);

        }
        if (e.getSource() == btnViewRoute) {

            RoutePropertiesDialogCommon routePropertiesDialog = new RoutePropertiesDialogCommon(
                    EPDShip.getMainFrame(), latestReceivedRoute, false);

            routePropertiesDialog.setVisible(true);

        }

        if (e.getSource() == btnWait) {
            this.setVisible(false);
        }

    }

    public void setRouteLayer(RouteLayer routeLayer) {
        this.routeLayer = routeLayer;
    }

    public void initializeNew() {
        lblPostRoute.setText("sent to STCC");
        btnMain.setText("Cancel request");

    }

    private String findChanges() {

        Route originalRoute = new Route(monaLisaHandler
                .getMonaLisaNegotiationData()
                .get(transactionID)
                .getRouteMessage()
                .get(monaLisaHandler.getMonaLisaNegotiationData()
                        .get(transactionID).getRouteMessage().size() - 1)
                .getRoute());
        // transactionID
        // System.out.println("The original route is comparable from : "
        // + monaLisaHandler.getMonaLisaNegotiationData()
        // .get(transactionID).getRouteMessage().size());
        //
        // System.out.println("Comparing ETAS");
        //
        // for (int i = 0; i < originalRoute.getEtas().size(); i++) {
        // System.out.println("Original ETA for " + i + " is: "
        // + originalRoute.getEtas().get(i)
        // + " vs Receieved Route ETA: "
        // + latestReceivedRoute.getEtas().get(i));
        // }

        // String
        String changes = "";

        if (originalRoute.getWaypoints().size() == latestReceivedRoute
                .getWaypoints().size()) {

            for (int i = 0; i < latestReceivedRoute.getWaypoints().size(); i++) {

                // System.out.println("Looking at index " + i);

                double originalLat = originalRoute.getWaypoints().get(i)
                        .getPos().getLatitude();
                double originalLon = originalRoute.getWaypoints().get(i)
                        .getPos().getLongitude();

                double newLat = latestReceivedRoute.getWaypoints().get(i)
                        .getPos().getLatitude();
                double newLon = latestReceivedRoute.getWaypoints().get(i)
                        .getPos().getLongitude();

                if (originalLat != newLat || originalLon != newLon) {
                    changes = changes + "Waypoint " + (i + 1)
                            + " new position\n";
                }
                System.out.println(originalRoute.getEtas().get(i).getTime()
                        + " vs "
                        + latestReceivedRoute.getEtas().get(i).getTime());

                // System.out.println("new position " + changes);
                // } else {
                if (latestReceivedRoute.getEtas().get(i).getTime() != originalRoute
                        .getEtas().get(i).getTime()) {
                    changes = changes
                            + "Wp "
                            + (i + 1)
                            + " ETA Changed from "
                            + Formatter.formatShortDateTimeNoTz(originalRoute
                                    .getEtas().get(i))
                            + " to "
                            + Formatter
                                    .formatShortDateTimeNoTz(latestReceivedRoute
                                            .getEtas().get(i)) + "\n";

                    // System.out.println("new eta" + changes);
                }

            }

        } else {

            if (latestReceivedRoute.getWaypoints().size() > originalRoute
                    .getWaypoints().size()) {
                int wpAdded = latestReceivedRoute.getWaypoints().size()
                        - originalRoute.getWaypoints().size();

                changes = wpAdded + " new waypoints added";
            } else {
                int wpRemoved = originalRoute.getWaypoints().size()
                        - latestReceivedRoute.getWaypoints().size();
                changes = wpRemoved + " waypoints removed";
            }

        }

        if (changes.equals("")) {
            changes = "No changes in receieved route";
        }

        // changes = changes + "</html>";

        System.out.println("Returning: " + changes);

        return changes;
    }
}
