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
package dk.dma.epd.ship.gui.route;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion.Status;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.gui.ChartPanel;
import dk.dma.epd.ship.gui.ComponentFrame;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.route.RecievedRoute;
import dk.dma.epd.ship.route.RouteManager;

/**
 * Dialog shown when route suggestion is received
 */
public class RouteSuggestionDialog extends ComponentFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 1L;
    
    private MainFrame mainFrame;
    private RouteManager routeManager;
    private ChartPanel chartPanel;
    private GpsHandler gpsHandler;

    private RecievedRoute cloudRouteSuggestion;


    private JButton acceptBtn;
    private JButton rejectBtn;
    private JButton notedBtn;
    private JButton ignoreBtn;
    private JButton postponeBtn;
    private JLabel titleLbl;
    private JLabel routeInfoLbl;
    private JButton zoomBtn;
    private JPanel routePanel;
    private JPanel replyPanel;
    private JButton hideBtn;
    private JLabel wpInfoLabel;
    private JTextArea textArea;
    
    public RouteSuggestionDialog(MainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
        setResizable(false);
        setTitle("AIS Route Suggestion");
        
        setSize(380, 500);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(mainFrame);        

        initGui();
        
        new Thread(this).start();
    }
    
    
    public void showSuggestion(RecievedRoute cloudRouteSuggestion) {
        
        this.cloudRouteSuggestion = cloudRouteSuggestion;
        
        titleLbl.setText("Route suggestion from " + cloudRouteSuggestion.getSender());
        
        textArea.setText("No message");
        
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<table cellpadding='0' cellspacing='3'>");
        str.append("<tr><td>Sent:</td><td>" + cloudRouteSuggestion.getSent()+ "</td></tr>");
        str.append("<tr><td>Received:</td><td>" + cloudRouteSuggestion.getRecieved()+ "</td></tr>");
        str.append("<tr><td>Message:</td><td>" + cloudRouteSuggestion.getMessage() + "</td></tr>");
        str.append("<tr><td>Status:</td><td>" + cloudRouteSuggestion.getStatus() + "</td></tr>");
        str.append("<tr><td>ID:</td><td>" + cloudRouteSuggestion.getId() + "</td></tr>");
        str.append("<tr><td>ETA first wp:</td><td>" + Formatter.formatShortDateTime(cloudRouteSuggestion.getRoute().getEtas().get(0)) + "</td></tr>");
        str.append("<tr><td>ETA last wp:</td><td>" + Formatter.formatShortDateTime(cloudRouteSuggestion.getRoute().getEtas().get(cloudRouteSuggestion.getRoute().getWaypoints().size()-1)) + "</td></tr>");
        str.append("</table>");
        str.append("</html>");
        routeInfoLbl.setText(str.toString());
        
        updateBtnStatus();
        updateWpInfo();
        
        showAndPosition();
    }
    
    //moved here to start breaking up dependencies
    public static String formatRouteSuggestioStatus(Status status) {
        switch (status) {
        case PENDING:
            return "Pending";
        case ACCEPTED:
            return "Accepted";
        case REJECTED:
            return "Rejected";
        case NOTED:
            return "Noted";
        case IGNORED:
            return "Ignored";
        default:
            return "Unknown";
        }
    }

    private void updateWpInfo() {
        // Get current position
        StringBuilder str = new StringBuilder();
        str.append("<html><b>DST/BRG/TTG/SPD</b><br/>");
        GpsData gpsData = gpsHandler.getCurrentData();
        if (gpsData != null && !gpsData.isBadPosition() && cloudRouteSuggestion.getRoute().getWaypoints().size() > 0) {
            double dst = cloudRouteSuggestion.getRoute().getWaypoints().get(0).getPos().rhumbLineDistanceTo(gpsData.getPosition()) / 1852;
            str.append(Formatter.formatDistNM(dst));
            double brg = cloudRouteSuggestion.getRoute().getWaypoints().get(0).getPos().rhumbLineBearingTo(gpsData.getPosition());
            str.append(" / " + Formatter.formatDegrees(brg, 2));
            Long ttg = null;
            if (cloudRouteSuggestion.getRoute().getEtas().get(0) != null) {
                ttg = cloudRouteSuggestion.getRoute().getEtas().get(0).getTime() - GnssTime.getInstance().getDate().getTime();
            }
            if (ttg != null && ttg < 0) {
                ttg = null;
            }
            Double spd = null;
            if (ttg != null) {
                spd = dst / ((double)ttg / 1000 / 60 / 60);
            }
            str.append(" / " + Formatter.formatTime(ttg));
            str.append(" / " + Formatter.formatSpeed(spd));
            
        } else {
            str.append("N/A");
        }
        str.append("</html>");
        
        wpInfoLabel.setText(str.toString());
    }
    
    private void updateBtnStatus() {
        zoomBtn.setEnabled(!cloudRouteSuggestion.isHidden());
        
        if (cloudRouteSuggestion.isHidden()) {        
            hideBtn.setText("Show");
        } else {
            hideBtn.setText("Hide");
        }
        
        hideBtn.setVisible(!cloudRouteSuggestion.isAcceptable());
        
        acceptBtn.setEnabled(cloudRouteSuggestion.isAcceptable());
        rejectBtn.setEnabled(cloudRouteSuggestion.isRejectable());
        notedBtn.setEnabled(cloudRouteSuggestion.isNoteable());
        ignoreBtn.setEnabled(cloudRouteSuggestion.isIgnorable());
        postponeBtn.setEnabled(cloudRouteSuggestion.isPostponable());        

    }
    
    private void showAndPosition() {
        validate();
        Rectangle rect = mainFrame.getBounds();
        int x = (int)rect.getMaxX() - getWidth() - 20;
        int y = (int)rect.getMaxY() - getHeight() - 20;
        setLocation(x, y);        
        setVisible(true);
        setState(java.awt.Frame.NORMAL);
    }
    
    private void close() {
        setVisible(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == zoomBtn) {
            List<Position> posList = new ArrayList<>();
            for (int i = 0; i < cloudRouteSuggestion.getRoute().getWaypoints().size(); i++) {
                posList.add(cloudRouteSuggestion.getRoute().getWaypoints().get(i).getPos());
            }
            
            chartPanel.zoomTo(posList);
        } else if (e.getSource() == hideBtn) {
            cloudRouteSuggestion.setHidden(!cloudRouteSuggestion.isHidden());
            updateBtnStatus();
            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
        } else if (e.getSource() == acceptBtn) {
            routeManager.routeSuggestionReply(cloudRouteSuggestion, Status.ACCEPTED, textArea.getText());
//            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            close();
        } else if (e.getSource() == rejectBtn) {
            routeManager.routeSuggestionReply(cloudRouteSuggestion, Status.REJECTED, textArea.getText());
//            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            close();
        } else if (e.getSource() == notedBtn) {
            routeManager.routeSuggestionReply(cloudRouteSuggestion, Status.NOTED, textArea.getText());
//            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            close();
        } else if (e.getSource() == ignoreBtn) {
            routeManager.routeSuggestionReply(cloudRouteSuggestion, Status.IGNORED, textArea.getText());
//            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
            close();
        } else if (e.getSource() == postponeBtn) {
            close();
        }        
    }
    
    @Override
    public void run() {
        while (true) {
            Util.sleep(5000);
            if (isVisible()) {
                updateWpInfo();
            }
        }        
    }

    
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof RouteManager) {
            routeManager = (RouteManager)obj;
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel)obj;
        }
        if (obj instanceof GpsHandler) {
            gpsHandler = (GpsHandler)obj;
        }
    }
    
    private void initGui() {
        acceptBtn = new JButton("Accept");
        acceptBtn.setToolTipText("Indicate that suggested route will be used");
        acceptBtn.addActionListener(this);
        
        rejectBtn = new JButton("Reject");
        rejectBtn.setToolTipText("Reject the suggested route");
        rejectBtn.addActionListener(this);
        
        notedBtn = new JButton("Noted");
        notedBtn.setToolTipText("Acknowledge reception, but route suggestion will or cannot be used");
        notedBtn.addActionListener(this);
        
        titleLbl = new JLabel("Addressed route suggestion from MMSI: 1293213");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));        
        
        routeInfoLbl = new JLabel("<html>\r\n<table cellspacing='1' cellpadding='0'>\r\n<tr><td>Received</td><td> sda</td></tr>\r\n<tr><td>Status</td><td> sda</td></tr>\r\n<tr><td>Type</td><td> sda</td></tr>\r\n<tr><td>ID</td><td> sda</td></tr>\r\n<tr><td>ETA first</td><td> sda</td></tr>\r\n<tr><td>ETA last</td><td> sda</td></tr>\r\n<tr><td>Duration</td><td> sda</td></tr>\r\n<tr><td>Avg speed</td><td> sda</td></tr>\r\n</table>\r\n</html>");
        routeInfoLbl.setVerticalAlignment(SwingConstants.TOP);
        
        zoomBtn = new JButton("Zoom to");
        zoomBtn.setToolTipText("Zoom to the suggested route on map");
        zoomBtn.addActionListener(this);
        
        hideBtn = new JButton("Hide");
        hideBtn.addActionListener(this);
        hideBtn.setToolTipText("Hide the suggested route on map");
        
        wpInfoLabel = new JLabel();
        wpInfoLabel.setText("dasd");
        wpInfoLabel.setToolTipText("Distance, bearing, time-to-go and speed to first waypoint");
        wpInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        routePanel = new JPanel();
        routePanel.setBorder(new TitledBorder(null, "Route", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        replyPanel = new JPanel();
        replyPanel.setBorder(new TitledBorder(null, "Reply", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        ignoreBtn = new JButton("Ignore");
        ignoreBtn.setToolTipText("Ignore suggestion and remove from map");
        ignoreBtn.addActionListener(this);
        
        postponeBtn = new JButton("Postpone");
        postponeBtn.setToolTipText("Postpone decision to later. Route will remain on map.");
        postponeBtn.addActionListener(this);
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(replyPanel, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 355, Short.MAX_VALUE)
                            .addGap(9))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(ignoreBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(postponeBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(184, Short.MAX_VALUE))))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(replyPanel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(ignoreBtn)
                        .addComponent(postponeBtn))
                    .addContainerGap())
        );
        
        textArea = new JTextArea("No message");

        JScrollPane textPane = new JScrollPane(textArea);
        
        GroupLayout gl_replyPanel = new GroupLayout(replyPanel);
        gl_replyPanel.setHorizontalGroup(
            gl_replyPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_replyPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_replyPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(textPane, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_replyPanel.createSequentialGroup()
                            .addComponent(acceptBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(rejectBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(notedBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(25, Short.MAX_VALUE))
        );
        gl_replyPanel.setVerticalGroup(
            gl_replyPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_replyPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(textPane, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_replyPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(acceptBtn)
                        .addComponent(rejectBtn)
                        .addComponent(notedBtn))
                    .addContainerGap())
        );
        replyPanel.setLayout(gl_replyPanel);
                
        GroupLayout gl_routePanel = new GroupLayout(routePanel);
        gl_routePanel.setHorizontalGroup(
            gl_routePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_routePanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(titleLbl, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addComponent(zoomBtn)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(hideBtn, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
                        .addComponent(routeInfoLbl, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
                        .addComponent(wpInfoLabel))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gl_routePanel.setVerticalGroup(
            gl_routePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_routePanel.createSequentialGroup()
                    .addComponent(titleLbl)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(routeInfoLbl, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(wpInfoLabel)
                    .addPreferredGap(ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(zoomBtn)
                        .addComponent(hideBtn)))
        );
        routePanel.setLayout(gl_routePanel);
        getContentPane().setLayout(groupLayout);
        
        
    }

}
