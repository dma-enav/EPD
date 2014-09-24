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
package dk.dma.epd.ship.gui.nogo;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.nogo.NogoDialogCommon;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.dma.epd.ship.ownship.OwnShipHandler;

/**
 * The nogo dialog
 */
public class NogoDialog extends NogoDialogCommon {
    private static final long serialVersionUID = 1L;

    public NogoDialog(JFrame parent, NogoHandler nogoHandler, OwnShipHandler ownShipHandler) {
        super(parent);
        this.mainFrame = (MainFrameCommon) parent;

        this.chartPanel = ((MainFrame) mainFrame).getChartPanel();

        this.nogoHandler = nogoHandler;

        setSize(384, 445);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        initGUI();

        if (ownShipHandler != null && ownShipHandler.getStaticData() != null) {

            int shipDraught = (int) ownShipHandler.getStaticData().getDraught();

            // System.out.println("Pure value is " + ownShipHandler.getStaticData().getDraught());
            double draught = shipDraught / 10.0;
            // System.out.println("Draught is " + draught);
            spinnerDraught.setValue(draught);

        }

        updateTotalDepthLabel();

    }

    @Override
    protected void toggleMenuLayerSelection() {
        if (mainFrame != null) {
            ((MainFrame) mainFrame).getJMenuBar().getNogoLayer().setSelected(true);
            nogoHandler.getNogoLayer().setVisible(true);
        }
    }

}
