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
package dk.dma.epd.common.prototype.gui.notification;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.maritimecloud.core.id.MaritimeId;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;

/**
 * Defines the base class for the notification center 
 */
public class NotificationCenterCommon extends ComponentDialog implements 
    IMsiUpdateListener,
    IChatServiceListener
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param window the parent window
     */
    public NotificationCenterCommon(Window window) {
        super(window, "Notification Center", Dialog.ModalityType.MODELESS);
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setBounds(100, 100, 800, 600);
        
        initGUI();
    }
    
    /*************************************/
    /** GUI methods                     **/
    /*************************************/
    
    /**
     * Set up the graphical user interface
     */
    protected void initGUI() {
        JPanel content = new JPanel(new BorderLayout());
        getContentPane().add(content);
        
    }
    
    /**
     * Returns the labels that should be installed in the bottom panel
     * @return the labels that should be installed in the bottom panel
     */
    public JLabel getBottomPanelLabels() {
        return null;
    }
    
    /**
     * Change the visibility
     */
    public void toggleVisibility() {
        setVisible(!isVisible());
    }

    /*************************************/
    /** Listener methods                **/
    /*************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void msiUpdate() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void chatMessageReceived(MaritimeId senderId, ChatServiceMessage message) {
        
    }
    
    /*************************************/
    /** Test method                     **/
    /*************************************/
    
    /**
     * Test method
     */
    public static void main(String... args) {
        NotificationCenterCommon n = new NotificationCenterCommon(null);
        n.setVisible(true);
        
    }
}
