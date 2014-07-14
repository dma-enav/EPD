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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.gui.ais.SartDetailsDialog;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;

/**
 * Menu item that displays the sar target details
 */
public class SarTargetDetails extends JMenuItem implements IMapMenuAction {
    private static final long serialVersionUID = 1L;
    
    private JFrame mainFrame;
    private PntHandler pntHandler;
    private SarTarget sarTarget;

    /**
     * Constructor
     * @param text menu item text
     */
    public SarTargetDetails(String text) {
        super();
        this.setText(text);
    }
    
    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
         new SartDetailsDialog(mainFrame, sarTarget, pntHandler);
    }
    
    /**
     * Sets the main frame
     * @param mainFrame
     */
    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    /**
     * Sets the PNT handler
     * @param pntHandler
     */
    public void setPntHandler(PntHandler pntHandler) {
        this.pntHandler = pntHandler;
    }
    
    /**
     * Sets the sar target
     * @param sarTarget
     */
    public void setSarTarget(SarTarget sarTarget) {
        this.sarTarget = sarTarget;
    }
}
