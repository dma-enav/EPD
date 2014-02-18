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

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.graphics.Resources;
import dk.dma.epd.common.prototype.EPD;


/**
 * Base class for EPDShip and EPDShore main frame classes
 * <p>
 * TODO: Move more common functionality to this class
 */
public abstract class MainFrameCommon extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MainFrameCommon.class);

    protected JPanel glassPanel;
        
    /**
     * Constructor
     */
    public MainFrameCommon(String title) {
        super(title);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(getAppIcon());
        
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
     * Called when the window is closing
     */
    public void onWindowClosing() {
        EPD.getInstance().closeApp(false);
    }

    /**
     * Returns the application icon
     * @return the application icon
     */
    protected Image getAppIcon() {
        ImageIcon icon =  Resources.get(EPD.getInstance().getClass()).getCachedImageIcon("/images/appicon.png");
        if (icon != null) {
            return icon.getImage();
        }
        LOG.error("Could not find app icon");
        return null;
    }    


    /**
     * Returns a reference to the glass pane
     * @return a reference to the glass pane
     */
    public JPanel getGlassPanel() {
        return glassPanel;
    }    
}
