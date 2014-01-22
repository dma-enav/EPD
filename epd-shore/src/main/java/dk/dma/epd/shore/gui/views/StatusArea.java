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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.event.IMapCoordListener;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;

/**
 * Class for setting up the status area of the application
 *
 * @author Steffen D. Sommer (steffendsommer@gmail.com)
 */
public class StatusArea extends JInternalFrame implements IMapCoordListener, BeanContextChild {

    private static final long serialVersionUID = 1L;
    private Boolean locked = false;
    private JLabel moveHandler;
    private JPanel masterPanel;
    private JPanel statusPanel;
    private JPanel highlightPanel;
    private static int moveHandlerHeight = 18;
    private static int statusItemHeight = 20;
    private static int statusItemWidth = 130;
    private static int statusPanelOffset = 4;
    private HashMap<String, JLabel> statusItems = new HashMap<String, JLabel>();
    private ConcurrentHashMap<String, JLabel> highlightItems = new ConcurrentHashMap<String, JLabel>();
    public int width;
    public int height;
    private long highlightedMMSI;

    /**
     * Constructor for setting up the status area
     *
     * @param mainFrame
     *            reference to the mainframe
     */
    public StatusArea(MainFrame mainFrame) {

        // Setup location
        this.setLocation(10 + moveHandlerHeight, 80 + mainFrame.getToolbar().getHeight() + mainFrame
                .getNotificationArea().getHeight());
        this.setVisible(true);
        this.setResizable(false);

        // Strip off window looks
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        this.setBorder(null);

        // Create the top movehandler (for dragging)
        moveHandler = new JLabel("Status", SwingConstants.CENTER);
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(statusItemWidth, moveHandlerHeight));
        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this, mainFrame);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);

        // Create the grid for the status items
        statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(0, 1));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
        statusPanel.setBackground(new Color(83, 83, 83));

        // Add status items here
        // Status: X coordinate
        statusItems.put("LAT", new JLabel(" LAT: N/A"));

        // Status: Y coordinate
        statusItems.put("LON", new JLabel(" LON: N/A"));

        // Create the grid for the highlighted ship info area
        highlightPanel = new JPanel();
        highlightPanel.setLayout(new GridLayout(0, 1));
        highlightPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 0));
        highlightPanel.setBackground(new Color(83, 83, 83));

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(moveHandler, BorderLayout.NORTH);
        masterPanel.add(statusPanel, BorderLayout.CENTER);
        masterPanel.add(highlightPanel, BorderLayout.SOUTH);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(
                45, 45, 45)));
        this.getContentPane().add(masterPanel);

        // And finally refresh the status bar
        repaintToolbar();
    }

    /**
     * Function for locking/unlocking the status bar
     */
    public void toggleLock() {

        if (locked) {

            masterPanel.add(moveHandler, BorderLayout.NORTH);
            locked = false;
            repaintToolbar();

            // Align the status bar according to the height of the movehandler
            int newX = (int) this.getLocation().getX();
            int newY = (int) this.getLocation().getY();
            Point new_location = new Point(newX, newY - moveHandlerHeight);
            this.setLocation(new_location);

        } else {

            masterPanel.remove(moveHandler);
            locked = true;
            repaintToolbar();

            // Align the status bar according to the height of the movehandler
            int newX = (int) this.getLocation().getX();
            int newY = (int) this.getLocation().getY();
            Point new_location = new Point(newX, newY + moveHandlerHeight);
            this.setLocation(new_location);

        }
    }

    /**
     * Function for refreshing the status area after editing status items
     */
    public void repaintToolbar() {

        // Lets start by adding all the notifications
        for (Entry<String, JLabel> entry : statusItems.entrySet()) {
            JLabel statusItem = entry.getValue();
            statusItem.setFont(new Font("Arial", Font.PLAIN, 11));
            statusItem.setForeground(new Color(237, 237, 237));
            statusPanel.add(statusItem);
        }

        // Then add all the highlighted vessel info
        highlightPanel.removeAll();
        JLabel highlightTitle = new JLabel(" Highlighted Vessel");
        highlightTitle.setFont(new Font("Arial", Font.BOLD, 11));
        highlightTitle.setForeground(new Color(237,237,237));
        if(highlightItems.size()>0){
            highlightTitle.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(65, 65, 65)));
            highlightPanel.add(highlightTitle);
        }
        for (Entry<String, JLabel> entry : highlightItems.entrySet()) {
            JLabel highlightItem = entry.getValue();
            highlightItem.setFont(new Font("Arial", Font.PLAIN, 11));
            highlightItem.setForeground(new Color(237, 237, 237));
            highlightPanel.add(highlightItem);
        }

        // Then calculate the size of the status bar according to the number of
        // status items
        width = statusItemWidth;
        int innerHeight = statusItems.size() * statusItemHeight;
        // Expanding width highlight size.
        int innerHeight2 = highlightItems.size() * statusItemHeight;
        if(highlightItems.size()>0) {
            innerHeight2 += statusItemHeight;
        }

        // find height of the two areas plus 7 for separator
        height = innerHeight+innerHeight2+7;

        if (!locked) {
            height = innerHeight + innerHeight2 + moveHandlerHeight;
        }

        // And finally set the size and repaint it
        statusPanel.setSize(width, innerHeight - statusPanelOffset);
        statusPanel.setPreferredSize(new Dimension(width, innerHeight - statusPanelOffset));
        // Also for highlight panel
        highlightPanel.setSize(width, innerHeight2 - statusPanelOffset);
        highlightPanel.setPreferredSize(new Dimension(width, innerHeight2 - statusPanelOffset));
        this.setSize(width, height);
        this.revalidate();
        this.repaint();

    }

    /**
     * Function for getting the width of the status bar
     *
     * @return width width of the status bar
     */
    public int getWidth() {
        return width;
    }

    /**
     * Function for getting the height of the status bar
     *
     * @return height height of the status bar
     */
    public int getHeight() {
        return height;
    }

    /**
     * Overriding function for setting the behavior when a coordinate is
     * received
     *
     * @param llp
     *            point including lat and lon
     */
    @Override
    public void receiveCoord(LatLonPoint llp) {

        statusItems.get("LAT").setText(" LAT  " + Formatter.latToPrintable(llp.getLatitude()));
        statusItems.get("LON").setText(" LON " + Formatter.lonToPrintable(llp.getLongitude()));

    }

    @Override
    public void addVetoableChangeListener(String arg0, VetoableChangeListener arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public BeanContext getBeanContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeVetoableChangeListener(String arg0, VetoableChangeListener arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setBeanContext(BeanContext arg0) throws PropertyVetoException {
        // TODO Auto-generated method stub
    }

    public void setHighlightedVesselMMSI(long MMSI){
        this.highlightedMMSI = MMSI;
    }

    public long getHighlightedVesselMMSI(){
        return this.highlightedMMSI;
    }

    /**
     * Update status area with highlighted vessel info
     * @param hashMap
     * @param MMSI The MMSI of the vessel
     * @param name The name (if set) of the vessel, else N/A.
     */
    public void receiveHighlight(HashMap<String, String> hashMap, long MMSI) {
        for (Entry<String, String> ii : hashMap.entrySet()) {
            if(highlightItems.containsKey(ii.getKey())){
                highlightItems.get(ii.getKey()).setText(AisMessage.trimText(" "+ii.getKey()+"  " + ii.getValue()));
            } else {
                highlightItems.put(ii.getKey(), new JLabel(AisMessage.trimText(" "+ii.getKey()+"  " + ii.getValue())));
            }
        }
        repaintToolbar();
    }

    public void removeHighlight(){
        highlightedMMSI = -1;
        highlightItems.clear();
        repaintToolbar();
    }

}
