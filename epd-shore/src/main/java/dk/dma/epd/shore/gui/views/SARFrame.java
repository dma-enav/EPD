/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.shore.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.voct.SARPanelPlanning;
import dk.dma.epd.shore.gui.voct.SARPanelTracking;

public class SARFrame extends JMapFrame {

    private static final long serialVersionUID = 1L;

    int sarPanelWidth = 285;
    int resizeWidthHack = 20;

    private SARPanelPlanning sarPanelPlanning;
    private SARPanelTracking sarPanelTracking;
    

    public SARFrame(int id, MainFrame mainFrame, MapFrameType type) {
        super(id, mainFrame, type, EPDShore.getInstance().getSettings().getMapSettings().copy());
        // Make local map settings obey to global map settings.
        EPDShore.getInstance().getSettings().getMapSettings().addObserver(this.getMapSettings());
        System.out.println("SAR Frame created with type : " + type);
    }

    @Override
    public void initGUI() {
        makeKeyBindings();

        // Listen for resize
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                repaintMapWindow();
            }
        });

        // Strip off
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI())
                .setNorthPane(null);
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Map tools
        mapPanel = new JPanel(new GridLayout(1, 3));
        mapPanel.setPreferredSize(new Dimension(500, moveHandlerHeight));
        mapPanel.setOpaque(true);
        mapPanel.setBackground(Color.DARK_GRAY);
        mapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(30, 30, 30)));

        ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this,
                mainFrame);
        mapPanel.addMouseListener(mml);
        mapPanel.addMouseMotionListener(mml);

        // Placeholder - for now
        mapPanel.add(new JLabel());

        // Movehandler/Title dragable)
        moveHandler = new JLabel("New Window " + id, SwingConstants.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getClickCount() == 2) {
                    rename();
                }
            }
        });
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);
        actions = moveHandler.getListeners(MouseMotionListener.class);
        mapPanel.add(moveHandler);

        // The tools (minimize, maximize and close)
        JPanel mapToolsPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0));
        mapToolsPanel.setOpaque(false);
        mapToolsPanel.setPreferredSize(new Dimension(60, 50));

        JLabel minimize = new JLabel(new ImageIcon(EPDShore.class
                .getClassLoader().getResource("images/window/minimize.png")));
        minimize.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                try {
                    setIcon(true);
                } catch (PropertyVetoException e1) {
                    e1.printStackTrace();
                }
            }

        });
        minimize.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 3));
        mapToolsPanel.add(minimize);

        maximize = new JLabel(new ImageIcon(EPDShore.class.getClassLoader()
                .getResource("images/window/maximize.png")));
        maximize.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                try {
                    if (maximized) {
                        setMaximum(false);
                        maximized = false;
                        maximize.setIcon(new ImageIcon(EPDShore.class
                                .getClassLoader().getResource(
                                        "images/window/maximize.png")));
                    } else {
                        setMaximum(true);
                        maximized = true;
                        maximize.setIcon(new ImageIcon(EPDShore.class
                                .getClassLoader().getResource(
                                        "images/window/restore.png")));
                    }
                } catch (PropertyVetoException e1) {
                    e1.printStackTrace();
                }
            }

        });
        maximize.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        mapToolsPanel.add(maximize);

        // JLabel close = new JLabel(new
        // ImageIcon(EPDShore.class.getClassLoader()
        // .getResource("images/window/close.png")));
        // close.addMouseListener(new MouseAdapter() {
        //
        // public void mouseReleased(MouseEvent e) {
        //
        // try {
        // mapFrame.setClosed(true);
        // } catch (PropertyVetoException e1) {
        // e1.printStackTrace();
        // }
        // }
        //
        // });
        // close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
        // mapToolsPanel.add(close);
        mapPanel.add(mapToolsPanel);

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(mapPanel, BorderLayout.NORTH);

        System.out.println("Init gui called");
        
        if (type == MapFrameType.SAR_Planning) {
            System.out.println("SAR Planning panel created");
            sarPanelPlanning = new SARPanelPlanning();

            masterPanel.add(sarPanelPlanning, BorderLayout.EAST);

            sarPanelPlanning.setSize(sarPanelWidth, 768);
            sarPanelPlanning.setPreferredSize(new Dimension(sarPanelWidth, 768));
        } else {
            System.out.println("SAR Tracking panel created");
            sarPanelTracking = new SARPanelTracking();

            masterPanel.add(sarPanelTracking, BorderLayout.EAST);

            sarPanelTracking.setSize(sarPanelWidth, 768);
            sarPanelTracking
                    .setPreferredSize(new Dimension(sarPanelWidth, 768));
        }

        masterPanel.add(chartPanel, BorderLayout.WEST);

        masterPanel.setBackground(new Color(45, 45, 45));
        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));

        this.setContentPane(masterPanel);
        repaintMapWindow();

        // Init the map right click menu
        mapMenu = new MapMenu();
        chartPanel.getMapHandler().add(mapMenu);

    }

    @Override
    public void repaintMapWindow() {

        System.out.println("Repaint called");
        
        width = getSize().width;
        int innerHeight = getSize().height - moveHandlerHeight
                - chartPanelOffset;
        height = getSize().height;

        if (locked) {
            innerHeight = getSize().height - 4; // 4 for border
        }

        // And finally set the size and repaint it
        chartPanel
                .setSize(width - sarPanelWidth - resizeWidthHack, innerHeight);
        chartPanel.setPreferredSize(new Dimension(width - sarPanelWidth
                - resizeWidthHack, innerHeight));

        if (type != null) {

            if (type == MapFrameType.SAR_Planning) {
                sarPanelPlanning.setSize(sarPanelWidth, innerHeight);
                sarPanelPlanning.setPreferredSize(new Dimension(sarPanelWidth,
                        innerHeight));
            } else {
                sarPanelTracking.setSize(sarPanelWidth, innerHeight);
                sarPanelTracking.setPreferredSize(new Dimension(sarPanelWidth,
                        innerHeight));
            }

        }

        this.setSize(width, height);
        this.revalidate();
        this.repaint();

    }

}
