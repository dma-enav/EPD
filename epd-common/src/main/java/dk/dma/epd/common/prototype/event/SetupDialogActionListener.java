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

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;

public class SetupDialogActionListener implements ActionListener {

    private SetupDialogCommon setupDialog;

    public SetupDialogActionListener(SetupDialogCommon setupDialog) {
        this.setupDialog = setupDialog;
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == this.setupDialog.getOkButton()) {
            // Save changes if changes were made.
            this.setupDialog.saveSettings();
            this.setupDialog.dispose();
        } else if (e.getSource() == this.setupDialog.getCancelButton()) {
            // Close the window.
            this.setupDialog.dispose();
        }
    }
}
