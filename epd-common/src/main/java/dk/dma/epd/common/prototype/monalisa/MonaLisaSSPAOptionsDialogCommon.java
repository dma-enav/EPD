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
