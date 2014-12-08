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
package dk.dma.epd.shore.gui.nogo;

import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.gui.nogo.NogoDialogCommon;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.nogo.NogoHandler;

/*
 * The nogo dialog
 */
public class NogoDialog extends NogoDialogCommon {
    private static final long serialVersionUID = 1L;

    public NogoDialog(JMapFrame parent, NogoHandler nogoHandler) {

        super(EPDShore.getInstance().getMainFrame());

        chartPanel = parent.getChartPanel();

        this.nogoHandler = nogoHandler;

        setSize(384, 445);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        initGUI();

        updateTotalDepthLabel();

    }

}
