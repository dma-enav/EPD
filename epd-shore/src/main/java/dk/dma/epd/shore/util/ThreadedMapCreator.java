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

package dk.dma.epd.shore.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.SwingUtilities;

import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.settings.gui.MapCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.MapCommonSettingsListener;
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
    private LatLonPoint center;
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
            boolean locked, boolean alwaysInFront, LatLonPoint center, float scale,
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
                MapFrameType.suggestedRoute, this.prepareLocalMapSettingsInstance());

        mainFrame.getDesktop().add(window);

        window.setTitle("Handle route request " + shipName);

        mainFrame.getMapWindows().add(window);
        mainFrame.getTopMenu().addMap(window, false, false);

        // Layer toggling panel invisible by default
        window.getLayerTogglingPanel().setVisible(false);
        
        window.alwaysFront();

        window.getChartPanel().getVoyageHandlingLayer()
                .handleVoyage(originalRoute, voyage, renegotiate);

        int positionX = 150;
        int positionY = 50;
        
        // Determine the max monitor size at the top left location
        Point screenLocation = new Point(positionX, positionY);
        SwingUtilities.convertPointToScreen(screenLocation, mainFrame);
        Rectangle screenBounds = GraphicsUtil.getMonitorBoundsForScreenPoint(screenLocation);
        int maxWidthOnScreen = screenBounds.x + screenBounds.width - screenLocation.x - 150;
        int maxHeightOnScreen = screenBounds.y + screenBounds.height - screenLocation.y - 150;

        int width = Math.min(maxWidthOnScreen, (int)(mainFrame.getSize().getWidth() - positionX - 150));
        int height = Math.min(maxHeightOnScreen, (int) (mainFrame.getSize().getHeight() - positionY - 150));

        window.setSize(width, height);
        window.setLocation(positionX, positionY);

        // Zoom to the route
        window.getChartPanel().zoomToWaypoints(voyage.getRoute().getWaypoints());

        return window;
    }

    private JMapFrame addMapWindow() {
        mainFrame.increaseWindowCount();

        JMapFrame window = new JMapFrame(mainFrame.getWindowCount(), mainFrame,
                MapFrameType.standard, this.prepareLocalMapSettingsInstance());
        mainFrame.getDesktop().add(window);

        mainFrame.getMapWindows().add(window);
        mainFrame.getTopMenu().addMap(window, false, false);

        return window;
    }

    public JMapFrame addMapWindow(boolean workspace, boolean locked,
            boolean alwaysInFront, LatLonPoint center, float scale, String title,
            Dimension size, Point location, Boolean maximized) {

        mainFrame.increaseWindowCount();
        
        MapCommonSettings<MapCommonSettingsListener> frameMapSettings = this.prepareLocalMapSettingsInstance();
        // Hack to apply workspace settings
        // Consider removing workspace and serialize yaml settings for each frame.
        frameMapSettings.setCenter(center);
        frameMapSettings.setInitialMapScale(scale);
        JMapFrame window = new JMapFrame(mainFrame.getWindowCount(), mainFrame, frameMapSettings);

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

        window.getChartPanel().goToPosition(
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

    private void setupSharedLayers(final JMapFrame window) {
        // Perform the update in the main swing thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    setupSharedLayers(window);
                }
            });
            return;
        }
        
//        MapSettings mapSettings = EPD.getInstance().getSettings().getMapSettings();
                
        if (window.getChartPanel().getWmsLayer() != null && window.getChartPanel().getWmsLayer().getSettings().isUseWms()) {
            window.getChartPanel().wmsVisible(window.getChartPanel().getWmsLayer().getSettings().isVisible());
        }

        if (window.getChartPanel().getEncLayerSettings().isEncInUse() && window.getChartPanel().getEncLayer() != null) {
            // TODO is this needed in order to display ENC on startup?
//            window.getChartPanel().encVisible(window.getChartPanel().getEncLayerSettings().isVisible());
        }

        if (mainFrame.getWindowCount() == 1 && window.getChartPanel().getWmsLayer() != null) {
            EPDShore.getInstance().getBeanHandler()
                    .add(window.getChartPanel().getWmsLayer().getWmsService());
        }
    }

    /**
     * Utility function for creating a new local {@link MapCommonSettings} instance by copying values from the global instance.
     * The returned instance is registered as observer of the global instance.
     * @return A new {@link MapCommonSettings} that has the same values as the global instance and is registered as observer of the global instance.
     */
    private MapCommonSettings<MapCommonSettingsListener> prepareLocalMapSettingsInstance() {
        // Get global map settings
        MapCommonSettings<MapCommonSettingsListener> globalMapSettings = EPDShore.getInstance().getSettings().getMapSettings();
        // Create local copy
        MapCommonSettings<MapCommonSettingsListener> localMapSettings = globalMapSettings.copy();
        // Register local copy to obey to global changes
        globalMapSettings.addObserver(localMapSettings);
        return localMapSettings;
    }
    
}
