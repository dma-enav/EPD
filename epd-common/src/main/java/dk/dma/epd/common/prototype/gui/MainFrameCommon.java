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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.notification.ChatServiceDialog;


/**
 * Base class for EPDShip and EPDShore main frame classes
 * <p>
 * TODO: Move more common functionality to this class
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
     * Zooms the active map to the given position
     * @param pos the position to zoom to
     */
    public abstract void zoomToPosition(Position pos);
    
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
    
}
