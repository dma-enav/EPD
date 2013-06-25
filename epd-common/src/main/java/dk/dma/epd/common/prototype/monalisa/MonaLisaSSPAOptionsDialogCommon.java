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
package dk.dma.epd.common.prototype.monalisa;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import dk.dma.epd.common.prototype.route.RouteManagerCommon;


/**
 * The nogo dialog
 */
public class MonaLisaSSPAOptionsDialogCommon extends JDialog {
    private static final long serialVersionUID = 1L;

    JSpinner spinnerDraught;

    JLabel totalWpLbl;
    JLabel selectWpLbl;



    private List<Boolean> selectedWp;



    private RouteManagerCommon routeManager;

    int routeid;



    public MonaLisaSSPAOptionsDialogCommon(JFrame parent, String string, boolean b) {
        super(parent, string, b);
    }

    public void resetSelected(){
        for (int i = 0; i < routeManager.getRoute(routeid).getWaypoints()
                .size(); i++) {
            selectedWp.add(true);
        }
    }

    public void updateSelected() {
        int selected = 0;
        
        for (int i = 0; i < selectedWp.size(); i++) {
            if (selectedWp.get(i)){
                selected++;
            }
        }
        
        selectWpLbl.setText(String.valueOf(selected));
    }
}
