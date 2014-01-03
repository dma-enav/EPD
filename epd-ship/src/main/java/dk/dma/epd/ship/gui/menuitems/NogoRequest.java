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
package dk.dma.epd.ship.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.nogo.NogoDialog;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.ownship.OwnShipHandler;

public class NogoRequest extends JMenuItem implements IMapMenuAction {
    private static final long serialVersionUID = 1L;
    private NogoHandler nogoHandler;
    private MainFrame mainFrame;
    private OwnShipHandler ownShipHandler;
    
    public void setOwnShipHandler(OwnShipHandler ownShipHandler){
        this.ownShipHandler = ownShipHandler;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setNogoHandler(NogoHandler nogoHandler) {
        this.nogoHandler = nogoHandler;
    }

    public NogoRequest(String text) {
        super();
        setText(text);
    }
    
    @Override
    public void doAction() {
        NogoDialog nogoDialog = new NogoDialog(mainFrame, nogoHandler, ownShipHandler);
        nogoDialog.setVisible(true);
    }
}

