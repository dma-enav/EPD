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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion.Status;
import dk.dma.epd.common.prototype.enavcloud.RouteSuggestionService.RouteSuggestionMessage;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.gui.ChartPanel;
import dk.dma.epd.ship.gui.ComponentFrame;
import dk.dma.epd.ship.gui.MainFrame;
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
    
    private AisAdressedRouteSuggestion routeSuggestion;
    private RouteSuggestionMessage cloudRouteSuggestion;

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
    
    public RouteSuggestionDialog(MainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
        setResizable(false);
        setTitle("AIS Route Suggestion");
        
        setSize(380, 406);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(mainFrame);        

        initGui();
        
        new Thread(this).start();
    }
    
    public void showSuggestion(AisAdressedRouteSuggestion routeSuggestion) {
        this.routeSuggestion = routeSuggestion;
        
        titleLbl.setText("AIS Addressed route suggestion from MMSI: " + routeSuggestion.getSender());
        
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<table cellpadding='0' cellspacing='3'>");
        str.append("<tr><td>Received:</td><td>" + Formatter.formatShortDateTime(routeSuggestion.getReceived()) + "</td></tr>");
        str.append("<tr><td>Status:</td><td>" + formatRouteSuggestioStatus(routeSuggestion.getStatus()) + "</td></tr>");
        str.append("<tr><td>Type:</td><td>" + Formatter.formatAisRouteType(routeSuggestion.getRouteType()) + "</td></tr>");
        str.append("<tr><td>ID:</td><td>" + routeSuggestion.getMsgLinkId() + "</td></tr>");
        str.append("<tr><td>ETA first wp:</td><td>" + Formatter.formatShortDateTime(routeSuggestion.getEtaFirst()) + "</td></tr>");
        str.append("<tr><td>ETA last wp:</td><td>" + Formatter.formatShortDateTime(routeSuggestion.getEtaLast()) + "</td></tr>");
        str.append("<tr><td>Duration:</td><td>" + Formatter.formatTime(routeSuggestion.getDuration()) + "</td></tr>");
        str.append("<tr><td>Avg speed:</td><td>" + Formatter.formatSpeed(routeSuggestion.getSpeed()) + "</td></tr>");
        str.append("</table>");
        str.append("</html>");
        routeInfoLbl.setText(str.toString());
        
        updateBtnStatus();
        updateWpInfo();
        
        showAndPosition();
    }
    
    
    public void showSuggestion(RouteSuggestionMessage cloudRouteSuggestion) {
        System.out.println("Show suggestion!");
        
        
        this.cloudRouteSuggestion = cloudRouteSuggestion;
        
//        titleLbl.setText("AIS Addressed route suggestion from MMSI: " + cloudRouteSuggestion.getSender());
        titleLbl.setText("AIS Addressed route suggestion from MMSI: " + " Someone in the cloud");
        
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<table cellpadding='0' cellspacing='3'>");
        str.append("<tr><td>Received:</td><td>" + "UNKNOWN"+ "</td></tr>");
        str.append("<tr><td>Status:</td><td>" + "UNKNOWN" + "</td></tr>");
        str.append("<tr><td>Type:</td><td>" + "UNKNOWN" + "</td></tr>");
        str.append("<tr><td>ID:</td><td>" + cloudRouteSuggestion.messageName() + "</td></tr>");
        str.append("<tr><td>ETA first wp:</td><td>" + Formatter.formatShortDateTime(cloudRouteSuggestion.getRoute().getWaypoints().get(0).getEta()) + "</td></tr>");
        str.append("<tr><td>ETA last wp:</td><td>" + Formatter.formatShortDateTime(cloudRouteSuggestion.getRoute().getWaypoints().get(cloudRouteSuggestion.getRoute().getWaypoints().size()-1).getEta()) + "</td></tr>");
        str.append("<tr><td>Duration:</td><td>" + "UNKNOWN" + "</td></tr>");
        str.append("<tr><td>Avg speed:</td><td>" + "UNKNOWN" + "</td></tr>");
        str.append("</table>");
        str.append("</html>");
        routeInfoLbl.setText(str.toString());
        
//        updateBtnStatus();
//        updateWpInfo();
        
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
        if (gpsData != null && !gpsData.isBadPosition() && routeSuggestion.getWaypoints().size() > 0) {
            double dst = routeSuggestion.getWaypoints().get(0).rhumbLineDistanceTo(gpsData.getPosition()) / 1852;
            str.append(Formatter.formatDistNM(dst));
            double brg = routeSuggestion.getWaypoints().get(0).rhumbLineBearingTo(gpsData.getPosition());
            str.append(" / " + Formatter.formatDegrees(brg, 2));
            Long ttg = null;
            if (routeSuggestion.getEtaFirst() != null) {
                ttg = routeSuggestion.getEtaFirst().getTime() - GnssTime.getInstance().getDate().getTime();
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
        zoomBtn.setEnabled(!routeSuggestion.isHidden());
        
        if (routeSuggestion.isHidden()) {        
            hideBtn.setText("Show");
        } else {
            hideBtn.setText("Hide");
        }
        
        hideBtn.setVisible(!routeSuggestion.isAcceptable());
        
        acceptBtn.setEnabled(routeSuggestion.isAcceptable());
        rejectBtn.setEnabled(routeSuggestion.isRejectable());
        notedBtn.setEnabled(routeSuggestion.isNoteable());
        ignoreBtn.setEnabled(routeSuggestion.isIgnorable());
        postponeBtn.setEnabled(routeSuggestion.isPostponable());        

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
            chartPanel.zoomTo(routeSuggestion.getWaypoints());
        } else if (e.getSource() == hideBtn) {
            routeSuggestion.setHidden(!routeSuggestion.isHidden());
            updateBtnStatus();
            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
        } else if (e.getSource() == acceptBtn) {
            routeManager.aisRouteSuggestionReply(routeSuggestion, AisAdressedRouteSuggestion.Status.ACCEPTED);
            close();
        } else if (e.getSource() == rejectBtn) {
            routeManager.aisRouteSuggestionReply(routeSuggestion, AisAdressedRouteSuggestion.Status.REJECTED);
            close();
        } else if (e.getSource() == notedBtn) {
            routeManager.aisRouteSuggestionReply(routeSuggestion, AisAdressedRouteSuggestion.Status.NOTED);
            close();
        } else if (e.getSource() == ignoreBtn) {
            routeSuggestion.setStatus(AisAdressedRouteSuggestion.Status.IGNORED);
            routeManager.notifyListeners(RoutesUpdateEvent.SUGGESTED_ROUTES_CHANGED);
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
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 355, Short.MAX_VALUE)
                            .addGap(9))
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addComponent(replyPanel, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(ignoreBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(postponeBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(183, Short.MAX_VALUE))))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(replyPanel, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(ignoreBtn)
                        .addComponent(postponeBtn))
                    .addContainerGap(92, Short.MAX_VALUE))
        );
        
        GroupLayout gl_replyPanel = new GroupLayout(replyPanel);
        gl_replyPanel.setHorizontalGroup(
            gl_replyPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_replyPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(acceptBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(rejectBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(notedBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(60, Short.MAX_VALUE))
        );
        gl_replyPanel.setVerticalGroup(
            gl_replyPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_replyPanel.createSequentialGroup()
                    .addGroup(gl_replyPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(acceptBtn)
                        .addComponent(rejectBtn)
                        .addComponent(notedBtn))
                    .addContainerGap(35, Short.MAX_VALUE))
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
