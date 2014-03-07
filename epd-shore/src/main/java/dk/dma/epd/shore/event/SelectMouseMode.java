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
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getSource() instanceof MapBean && SwingUtilities.isLeftMouseButton(e)) {
            mouseSupport.fireMapMousePressed(e);
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
