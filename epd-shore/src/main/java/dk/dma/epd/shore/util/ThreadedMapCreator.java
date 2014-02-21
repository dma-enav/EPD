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

package dk.dma.epd.shore.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MainFrame;
import dk.dma.epd.shore.gui.views.MapFrameType;
import dk.dma.epd.shore.gui.views.SARFrame;
import dk.dma.epd.shore.voyage.Voyage;

public class ThreadedMapCreator implements Runnable {

    private boolean workspace;
    private boolean locked;
    private boolean alwaysInFront;
    private Point2D center;
    private float scale;
    private String title;
    private Dimension size;
    private Point location;
    private Boolean maximized;

    private boolean loadFromWorkspace;
    private boolean strategicRouteHandling;
    private boolean renegotiate;

    private String shipName;
    private Voyage voyage;

    private MainFrame mainFrame;
    private Route originalRoute;

    private boolean SAR;
    MapFrameType type;

    public ThreadedMapCreator(MainFrame mainFrame, boolean workspace,
            boolean locked, boolean alwaysInFront, Point2D center, float scale,
            String title, Dimension size, Point location, Boolean maximized) {

        this.mainFrame = mainFrame;

        this.workspace = workspace;
        this.locked = locked;
        this.alwaysInFront = alwaysInFront;
        this.center = center;
        this.scale = scale;
        this.title = title;
        this.size = size;
        this.location = location;
        this.maximized = maximized;

        loadFromWorkspace = true;
        strategicRouteHandling = false;
    }

    public ThreadedMapCreator(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        loadFromWorkspace = false;
        strategicRouteHandling = false;
    }

    public ThreadedMapCreator(MainFrame mainFrame, boolean SAR,
            MapFrameType type) {
        this.mainFrame = mainFrame;
        loadFromWorkspace = false;
        strategicRouteHandling = false;
        this.SAR = true;
        this.type = type;
    }

    public ThreadedMapCreator(MainFrame mainFrame, String shipName,
            Voyage voyage, Route originalRoute, boolean renegotiate) {
        this.mainFrame = mainFrame;
        this.shipName = shipName;
        this.voyage = voyage;
        this.originalRoute = originalRoute;
        loadFromWorkspace = false;
        strategicRouteHandling = true;
        this.renegotiate = renegotiate;
    }

    private JMapFrame addStrategicRouteHandlingWindow(String shipName, Voyage voyage,
            Route originalRoute, boolean renegotiate) {
        mainFrame.increaseWindowCount();

        JMapFrame window = new JMapFrame(mainFrame.getWindowCount(), mainFrame,
                MapFrameType.suggestedRoute);

        mainFrame.getDesktop().add(window);

        window.setTitle("Handle route request " + shipName);

        mainFrame.getMapWindows().add(window);
        mainFrame.getTopMenu().addMap(window, false, false);

        window.alwaysFront();

        window.getChartPanel().getVoyageHandlingLayer()
                .handleVoyage(originalRoute, voyage, renegotiate);

        int positionX = 200;
        int positionY = 200;

        int width = (int) (mainFrame.getSize().getWidth() - positionX - 100);
        int height = (int) (mainFrame.getSize().getHeight() - positionY - 100);

        window.setSize(width, height);
        // window.setSize(1280, 768);

        // 100, 100
        window.setLocation(positionX, positionY);

        // The two positions that must be shown
        Position pos1 = voyage.getRoute().getWaypoints().get(0).getPos();
        Position pos2 = voyage.getRoute().getWaypoints()
                .get(voyage.getRoute().getWaypoints().size() - 1).getPos();

        double distance = Calculator.range(pos1, pos2, Heading.RL);
        // System.out.println("Distance is: " + distance);
        int scale = 250000;

        if (distance > 1) {
            scale = 60000;
        }

        if (distance > 5) {
            scale = 120000;
        }

        if (scale > 10) {
            scale = 240000;
        }

        if (distance > 25) {
            scale = 500000;
        }
        if (distance > 50) {
            scale = 1000000;
        }
        if (distance > 100) {
            scale = 2500000;
        }
        if (distance > 200) {
            scale = 5000000;
        }
        if (distance > 400) {
            scale = 10000000;
        }

        // 5 mil

        window.getChartPanel().getMap().setScale(scale);

        // window.getChartPanel().zoomToPoint(
        // voyage.getRoute().getWaypoints().get(0).getPos());

        List<Position> waypoints = new ArrayList<>();

        for (int i = 0; i < voyage.getRoute().getWaypoints().size(); i++) {
            waypoints.add(voyage.getRoute().getWaypoints().get(i).getPos());
        }

        window.getChartPanel().zoomTo(waypoints);

        return window;
    }

    private JMapFrame addMapWindow() {
        mainFrame.increaseWindowCount();

        JMapFrame window = new JMapFrame(mainFrame.getWindowCount(), mainFrame,
                MapFrameType.standard);
        mainFrame.getDesktop().add(window);

        mainFrame.getMapWindows().add(window);
        mainFrame.getTopMenu().addMap(window, false, false);

        return window;
    }

    public JMapFrame addMapWindow(boolean workspace, boolean locked,
            boolean alwaysInFront, Point2D center, float scale, String title,
            Dimension size, Point location, Boolean maximized) {

        mainFrame.increaseWindowCount();

        JMapFrame window = new JMapFrame(mainFrame.getWindowCount(), mainFrame,
                center, scale);

        window.setTitle(title);
        // Maybe not needed
        // topMenu.renameMapMenu(window);

        mainFrame.getDesktop().add(window);
        mainFrame.getMapWindows().add(window);

        window.toFront();

        mainFrame.getTopMenu().addMap(window, locked, alwaysInFront);

        window.setSize(size);

        window.setLocation(location);

        if (maximized) {
            window.setSize(600, 600);
            window.setMaximizedIcon();
            try {
                window.setMaximum(maximized);
            } catch (PropertyVetoException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (locked) {
            window.lockUnlockWindow();
        }

        if (alwaysInFront) {
            window.alwaysFront();
        }

        return window;
    }

    private SARFrame addSARWindow() {
        // mainFrame.increaseWindowCount();

        SARFrame window = new SARFrame(-1, mainFrame, type);

        mainFrame.getDesktop().add(window);

        Dimension mainFrameSize = mainFrame.getSize();

        if (type == MapFrameType.SAR_Planning) {
            window.setTitle("Search and Rescue - Planning");
            window.setLocation(0, 0);
            window.setSize((int) mainFrameSize.getWidth() - 20,
                    (int) mainFrameSize.getHeight() - 20);

        }
        if (type == MapFrameType.SAR_Tracking) {
            window.setTitle("Search and Rescue - Tracking");

            // Put Tracking on left side of screen
            window.setLocation((int) mainFrameSize.getWidth() / 2 - 10, 0);
            window.setSize((int) mainFrameSize.getWidth() / 2 - 10,
                    (int) mainFrameSize.getHeight() - 10);
            for (int i = 0; i < EPDShore.getInstance().getMainFrame()
                    .getMapWindows().size(); i++) {
                if (EPDShore.getInstance().getMainFrame().getMapWindows()
                        .get(i).getType() == MapFrameType.SAR_Planning) {
                    JMapFrame planningWindow = EPDShore.getInstance()
                            .getMainFrame().getMapWindows().get(i);
                    planningWindow.setLocation(0, 0);
                    planningWindow.setSize(
                            (int) mainFrameSize.getWidth() / 2 - 10,
                            (int) mainFrameSize.getHeight() - 10);
                    break;
                }
            }
        }

        mainFrame.getMapWindows().add(window);

        window.alwaysFront();

        window.getChartPanel().zoomToPoint(
                EPDShore.getInstance().getVoctManager().getSarData().getLKP());
        window.getChartPanel().getMap().setScale(100000);

        return window;
    }

    @Override
    public void run() {

        if (loadFromWorkspace) {
            JMapFrame mapFrame = addMapWindow(workspace, locked, alwaysInFront,
                    center, scale, title, size, location, maximized);

            setupSharedLayers(mapFrame);
            return;
        }

        if (strategicRouteHandling) {
            JMapFrame mapFrame = addStrategicRouteHandlingWindow(shipName, voyage,
                    originalRoute, renegotiate);
            setupSharedLayers(mapFrame);
            return;
        }

        if (SAR) {

            JMapFrame mapFrame = addSARWindow();
            setupSharedLayers(mapFrame);

            return;
        }

        // long startTime = System.currentTimeMillis();
        JMapFrame mapFrame = addMapWindow();
        setupSharedLayers(mapFrame);
    }

    private JMapFrame setupSharedLayers(JMapFrame window) {

        if (!mainFrame.isWmsLayerEnabled()) {
            window.getChartPanel().getWmsLayer().setVisible(false);
            window.getChartPanel().getBgLayer().setVisible(true);
        } else {
            window.getChartPanel().getWmsLayer().setVisible(true);
            window.getChartPanel().getBgLayer().setVisible(true);
        }

        if (mainFrame.isUseEnc()) {

            if (window.getChartPanel().getEncLayer() != null) {
                if (!mainFrame.isEncLayerEnabled()) {
                    window.getChartPanel().getEncLayer().setVisible(false);
                } else {
                    window.getChartPanel().getEncLayer().setVisible(true);
                }
            }

        }

        if (!mainFrame.isMsiLayerEnabled()) {
            window.getChartPanel().getMsiLayer().setVisible(false);

        }

        if (mainFrame.getWindowCount() == 1) {
            EPDShore.getInstance().getBeanHandler()
                    .add(window.getChartPanel().getWmsLayer().getWmsService());
        }

        return window;
    }

}
