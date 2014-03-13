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
package dk.dma.epd.common.prototype.layers;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.bbn.openmap.event.MapMouseMode;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.event.mouse.CommonDistanceCircleMouseMode;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

public class CommonRulerLayer extends EPDLayerCommon {

    // Private fields.
    private static final long serialVersionUID = 1L;
    private ChartPanelCommon chartPanel;
    private CommonRulerGraphic rulerGraphic;

    /**
     * Called when a bean is added to the bean context. If the bean
     * object is an instance of CommonChartPanel the chart panel
     * will be initialized from the passed object.
     * 
     * @param obj
     *            the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        
        if (obj instanceof ChartPanelCommon) {
            chartPanel = (ChartPanelCommon) obj;
        }
    }
    
    /**
     * Returns the mouse mode service list
     * 
     * @return the mouse mode service list
     */
    @Override
    public String[] getMouseModeServiceList() {
        
        String[] serviceList = new String[1];
        serviceList[0] = CommonDistanceCircleMouseMode.MODE_ID;
        return serviceList;
    }
    
    @Override
    public boolean mouseClicked(MouseEvent e) {
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            
            // Clear any old range circle from graphics.
            if (rulerGraphic != null) {
                graphics.remove(rulerGraphic);
            }
            // LatLon representation of point clicked.
            LatLonPoint ptClicked = chartPanel.getMap().getProjection()
                    .inverse(e.getPoint());
            // Transform to internal position representation.
            // The point clicked is the center of the range circle.
            Position circleCenter = Position.create(ptClicked.getLatitude(),
                    ptClicked.getLongitude());
            rulerGraphic = new CommonRulerGraphic(circleCenter);
            graphics.add(rulerGraphic);
            // Repaint
            doPrepare();
            // Event has been handled.
            return true;
        
        } else if (SwingUtilities.isRightMouseButton(e)) {
            
            // Right click means exit this mouse mode...
            // Clear all graphics from this mode
            clearRuler();
            doPrepare();

            // Put chart panel back to previous mouse mode
            MapMouseMode mode = chartPanel.getMouseDelegator()
                    .getActiveMouseMode();
            if (mode instanceof CommonDistanceCircleMouseMode) {
                String prevModeID = ((CommonDistanceCircleMouseMode) chartPanel
                        .getMouseDelegator().getActiveMouseMode())
                        .getPreviousMouseMode();
                chartPanel.setMouseMode(prevModeID);
            }
            
            // Event has been handled.
            return true;
        }
        
        return false;
    }
    
    /**
     * Clears the ruler graphics
     */
    public void clearRuler() {
        if (rulerGraphic != null) {
            synchronized(graphics) {
                graphics.clear();
                rulerGraphic = null;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void projectionChanged(ProjectionEvent e) {
        doPrepare();
        super.projectionChanged(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseMoved(MouseEvent e) {
        if (rulerGraphic != null) {
            // if the user has provided a center location
            // we need to draw the distance line as well
            // as the outer circle.
            // First find mouse position in lat-lon.
            LatLonPoint llp = chartPanel.getMap().getProjection()
                    .inverse(e.getPoint());
            // Convert to internal position representation.
            Position mousePos = Position.create(llp.getLatitude(),
                    llp.getLongitude());
            // Call the rg to draw the distancel ine and the circle
            rulerGraphic.updateOutside(mousePos);
            // repaint
            doPrepare();
            return true;
        }
        return false;
    }
}
