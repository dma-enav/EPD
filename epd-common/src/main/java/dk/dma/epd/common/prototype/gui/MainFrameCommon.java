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
package dk.dma.epd.common.prototype.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.GraphicsUtil;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.settings.GuiSettings;

/**
 * Base class for EPDShip and EPDShore main frame classes
 */
public abstract class MainFrameCommon extends JFrame {

    private static final long serialVersionUID = 1L;

    protected JPanel glassPanel;

    /**
     * Constructor
     */
    public MainFrameCommon(String title) {
        super(title);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(EPD.getInstance().getAppIcon());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                onWindowClosing();
            }
        });

        // Initialize the glass panel
        initGlassPane();

    }

    /**
     * Initializes the glass pane of the frame
     */
    protected abstract void initGlassPane();

    /**
     * Returns the "active" chart panel.
     * <p>
     * For ship, this is the one and only chart panel, and for shore, this is the active one.
     * 
     * @return the "active" chart panel
     */
    public abstract ChartPanelCommon getActiveChartPanel();

    /**
     * Zooms the active map to the given position
     * 
     * @param pos
     *            the position to zoom to
     */
    public void zoomToPosition(Position pos) {
        if (getActiveChartPanel() != null) {
            getActiveChartPanel().goToPosition(pos);
        }
    }

    /**
     * Called when the window is closing
     */
    public void onWindowClosing() {
        EPD.getInstance().closeApp(false);
    }

    /**
     * Returns a reference to the glass pane
     * 
     * @return a reference to the glass pane
     */
    public JPanel getGlassPanel() {
        return glassPanel;
    }

    /**
     * Returns an about action associated with this application
     * 
     * @return an about action associated with this application
     */
    public abstract Action getAboutAction();

    /**
     * Returns the menu bar associated with the main frame
     * 
     * @return the menu bar associated with the main frame
     */
    public abstract JMenuBar getJMenuBar();

    /**
     * Opens the setup dialog associated with the application
     * 
     * @return the setup dialog associated with the application
     */
    public abstract SetupDialogCommon openSetupDialog();

    /**
     * Saves the GUI settings of the main frame
     */
    public void saveSettings() {
        // Save gui settings
        GuiSettings guiSettings = EPD.getInstance().getSettings().getGuiSettings();
        if (!guiSettings.isFullscreen()) {
            guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
            guiSettings.setAppLocation(getLocation());
            guiSettings.setAppDimensions(getSize());
        }
    }

    /**
     * Toggles the current full screen settings
     */
    public void toggleFullScreen() {
        if (EPD.getInstance().getSettings().getGuiSettings().isFullscreen()) {
            doNormal();
        } else {
            doFullScreen();
        }
    }

    /**
     * Enters full screen
     */
    public void doFullScreen() {
        doFullScreen(true);
    }

    /**
     * Enters full screen
     * 
     * @param saveBounds
     *            save the bounds of the frame before entering fullscreen
     */
    protected void doFullScreen(boolean saveBounds) {
        GuiSettings guiSettings = EPD.getInstance().getSettings().getGuiSettings();
        guiSettings.setFullscreen(true);
        if (saveBounds) {
            guiSettings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) > 0);
            guiSettings.setAppLocation(getLocation());
            guiSettings.setAppDimensions(getSize());
        }

        setVisible(false);
        // setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(GraphicsUtil.getMaxResolution());
        setLocation(0, 0);
        dispose();
        setUndecorated(true);
        setVisible(true);
    }

    /**
     * Leaves full screen
     */
    public void doNormal() {
        GuiSettings guiSettings = EPD.getInstance().getSettings().getGuiSettings();
        guiSettings.setFullscreen(false);

        setVisible(false);
        // setExtendedState(JFrame.NORMAL);
        setSize(guiSettings.getAppDimensions());
        setLocation(guiSettings.getAppLocation());
        dispose();
        setUndecorated(false);
        setVisible(true);
    }

    public Object getTopMenu() {
        return null;
    }
}
