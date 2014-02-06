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
package dk.dma.epd.common.prototype.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;

/**
 * This class is a listener for the setup dialog.
 * @author adamduehansen
 *
 */
public class SetupDialogHandler implements ActionListener, WindowListener {

    /**
     * Private fields.
     */
    private SetupDialogCommon setupDialog;
    private Timer timer;

    /**
     * @param setupDialog The setup dialog which this class should listen to.
     */
    public SetupDialogHandler(SetupDialogCommon setupDialog) {
        this.setupDialog = setupDialog;
        
        timer = new Timer(500, this);
        timer.start();
    }
    
    /**
     * Finds the parent JOptionPane.
     * @param parent The parent JComponent.
     * @return 
     */
    private JOptionPane getOptionPane(JComponent parent) {
        
        JOptionPane pane = null;
        
        if (!(parent instanceof JOptionPane)) {
            pane = getOptionPane((JComponent)parent.getParent());
        } else {
            pane = (JOptionPane) parent;
        }
        
        return pane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Dialog buttons.
        if (e.getSource() == this.setupDialog.getAcceptButton()) {
            
            // Save changes if changes were made.
            this.setupDialog.saveSettings();
            timer.stop();
            this.setupDialog.dispose();
            
        } else if (e.getSource() == this.setupDialog.getCancelButton()) {
            
            // Close the window.
            timer.stop();
            this.setupDialog.dispose();
            
        // Warning buttons.
        } else if (e.getSource() == this.setupDialog.getWarningAcceptButton()) {
            
            JOptionPane pane = getOptionPane((JComponent) e.getSource());                
            pane.setValue(this.setupDialog.getWarningAcceptButton());
            
        } else if (e.getSource() == this.setupDialog.getWarningCancelButton()) {
            
            JOptionPane pane = getOptionPane((JComponent) e.getSource());
            pane.setValue(this.setupDialog.getWarningCancelButton());
        }
        
        this.setupDialog.checkSettingsChanged();
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        
        // Check if any changes were made.
        if (this.setupDialog.checkSettingsChanged()) {
            // If there are any changes, ask the user if changes should be saved. 
            int answer = this.setupDialog.askIfShouldSaveChanges();
            
            if (answer == JOptionPane.YES_OPTION) {
                // Save changes
                this.setupDialog.saveSettings();
            } else if (answer == JOptionPane.NO_OPTION) {
                // Do nothing.
            }
        } else {
            // Close window.
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

	public Timer getTimer() {
		return this.timer;
	}
}
