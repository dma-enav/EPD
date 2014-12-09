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
package dk.dma.epd.shore.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import dk.dma.epd.common.prototype.event.mouse.NoGoMouseModeCommon;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;
import dk.dma.epd.shore.gui.views.JMapFrame;

public class NoGoMouseMode extends NoGoMouseModeCommon {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "NoGo".
     */
    public static final transient String MODE_ID = "NoGo";

    private ChartPanelCommon chartPanel;

    private JPanel glassFrame;

    /**
     * Constructs a NoGoMouseListener: sets the ID of the mode, the consume mode to true, and the cursor to the crosshair.
     */
    public NoGoMouseMode(ChartPanelCommon chartPanel) {
        super(chartPanel);
        this.chartPanel = chartPanel;
        this.setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * If the mouse is entered on a MapBean object, the doZoom boolean will be set to true.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        glassFrame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        // chartPanel.getMouseMode()
        glassFrame.setCursor(chartPanel.getMouseDelegator().getActiveMouseMode().getModeCursor());
    }

    @Override
    public void findAndInit(Object someObj) {
        if (someObj instanceof JMapFrame) {
            glassFrame = ((JMapFrame) someObj).getGlassPanel();
        }

        super.findAndInit(someObj);
    }
}
