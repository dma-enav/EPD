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
import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;

import dk.dma.epd.common.prototype.event.mouse.AbstractCoordMouseMode;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Mouse mode used in selection vessels
 */
public class SelectMouseMode extends AbstractCoordMouseMode {
    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Select".
     */
    public static final transient String MODEID = "Select";

    private JPanel glassFrame;

    boolean layerMouseDrag;

    /**
     * Construct a SelectMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public SelectMouseMode(ChartPanel chartPanel) {
        super(MODEID, true);
    }

    /**
     * Find and init bean function used in initializing other classes
     */
    @Override
    public void findAndInit(Object someObj) {

        super.findAndInit(someObj);
        
        if (someObj instanceof JMapFrame) {
            glassFrame = ((JMapFrame) someObj).getGlassPanel();
            glassFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() != 2) {
            super.mouseSupport.fireMapMouseClicked(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        this.glassFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
       super.mouseDragged(e);
       
       if (e.getSource() instanceof MapBean &&
               SwingUtilities.isLeftMouseButton(e)) {
                       
           // Ensure that layer elements can be dragged (fx waypoints)
           mouseSupport.fireMapMouseDragged(e);
       }
    }
}
