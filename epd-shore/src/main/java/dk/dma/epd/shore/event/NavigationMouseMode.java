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

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dk.dma.epd.common.prototype.event.mouse.CommonNavigationMouseMode;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * The mouse mode used in navigation mode - click zoom mode
 */
public class NavigationMouseMode extends CommonNavigationMouseMode {
    
    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Navigation".
     */
    public static final transient String MODEID = "NAVIGATION";

    private ClickTimer clickTimer;
    protected Point point1, point2;

    private JPanel glassFrame;

    /**
     * Construct a NavMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public NavigationMouseMode(ChartPanel chartPanel) {
        super(chartPanel, EPDShore.getInstance().getSettings().getMapSettings().getMaxScale(), MODEID);
        clickTimer = ClickTimer.getClickTimer();
    }

    /**
    * Called when a CoordMouseMode is added to a BeanContext, or when another
    * object is added to the BeanContext after that. The CoordMouseMode looks
    * for an InformationDelegator to use to fire the coordinate updates. If
    * another InforationDelegator is added when one is already set, the later
    * one will replace the current one.
    * 
    * @param someObj an object being added to the BeanContext.
    */
    @Override
    public void findAndInit(Object someObj) {
        if (someObj instanceof JMapFrame) {
            glassFrame = ((JMapFrame) someObj).getGlassPanel();
        }

        super.findAndInit(someObj);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            EPDShore.getInstance().getMainFrame().getActiveMapWindow().getLayerTogglingPanel().getHistoryListener().saveToHistoryBeforeMoving();
            EPDShore.getInstance().getMainFrame().getActiveMapWindow().getLayerTogglingPanel().getHistoryListener().setShouldSave(true);
        }
        
        super.mouseClicked(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            EPDShore.getInstance().getMainFrame().getActiveMapWindow().getLayerTogglingPanel().getHistoryListener().saveToHistoryBeforeMoving();
            EPDShore.getInstance().getMainFrame().getActiveMapWindow().getLayerTogglingPanel().getHistoryListener().setShouldSave(true);
        }

        super.mouseReleased(e);
    }

    /**
     * If the mouse is entered on a MapBean object, the doZoom boolean will
     * be set to true.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        glassFrame.setCursor(super.NAV_CURSOR);
    }

    /**
     * If the the mouse is pressed down, the first point will be saved,
     * the second point will be reset, and the doZoom boolean will be
     * set to true, so that if the mouse is releasted, after being 
     * dragged, a zoom to that selected area will be executed.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        clickTimer.setInterval(500);
        clickTimer.startTime();
        super.mousePressed(e);        
    }
}
