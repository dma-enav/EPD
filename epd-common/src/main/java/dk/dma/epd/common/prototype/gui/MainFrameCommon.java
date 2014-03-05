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
package dk.dma.epd.common.prototype.gui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.notification.ChatServiceDialog;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.common.prototype.settings.GuiSettings;


/**
 * Base class for EPDShip and EPDShore main frame classes
 */
public abstract class MainFrameCommon extends JFrame {

    private static final long serialVersionUID = 1L;

    protected JPanel glassPanel;
    
    // Common dialogs
    protected ChatServiceDialog chatServiceDialog;
        
    /**
     * Constructor
     */
    public MainFrameCommon(String title) {
        super(title);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(EPD.getInstance().getAppIcon());
        
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent we) {
                onWindowClosing();
            }});

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
     * For ship, this is the one and only chart panel, 
     * and for shore, this is the active one.
     * 
     * @return the "active" chart panel
     */
    public abstract ChartPanelCommon getActiveChartPanel();
    
    /**
     * Zooms the active map to the given position
     * @param pos the position to zoom to
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
     * @return a reference to the glass pane
     */
    public JPanel getGlassPanel() {
        return glassPanel;
    }    
    
    /**
     * Returns the chat service dialog
     * @return the chat service dialog
     * @return
     */
    public ChatServiceDialog getChatServiceDialog() {
        return chatServiceDialog;
    }

    /**
     * Returns an about action associated with this application
     * @return an about action associated with this application
     */
    public abstract Action getAboutAction();
    
    /**
     * Opens the setup dialog associated with the application
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
     * @param saveBounds save the bounds of the frame before entering fullscreen
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
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(getMaxResolution());
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
        //setExtendedState(JFrame.NORMAL);
        setSize(guiSettings.getAppDimensions());
        setLocation(guiSettings.getAppLocation());
        dispose();
        setUndecorated(false);
        setVisible(true);
    }

    /**
     * Return the max resolution possible across all monitors
     * 
     * @return the max resolution possible across all monitors
     */
    private Dimension getMaxResolution() {
        int width = 0;
        int height = 0;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (GraphicsDevice curGs : gs) {
            DisplayMode mode = curGs.getDisplayMode();
            width += mode.getWidth();

            if (height < mode.getHeight()) {
                height = mode.getHeight();
            }

        }
        return new Dimension(width, height);
    }
}
