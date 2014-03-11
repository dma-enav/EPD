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
package dk.dma.epd.ship.layers.ruler;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseMode;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.ship.event.DistanceCircleMouseMode;
import dk.dma.epd.ship.gui.ChartPanel;

/**
 * Implementation of the ruler layer which allows
 * the user to measure distances and angles in the map
 */
public class RulerLayer extends EPDLayerCommon {

    private static final long serialVersionUID = 1L;

    private ChartPanel chartPanel;
    private RulerGraphic rulerGraphic;

    /**
     * Constructor
     */
    public RulerLayer() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getMouseModeServiceList() {
        String[] serviceList = new String[1];
        serviceList[0] = DistanceCircleMouseMode.MODE_ID;
        return serviceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
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
            rulerGraphic = new RulerGraphic(circleCenter);
            graphics.add(rulerGraphic);
            // Repaint
            doPrepare();
            // Event has been handled.
            return true;
        case MouseEvent.BUTTON3:
            // Right click means exit this mouse mode...
            // Clear all graphics from this mode
            clearRuler();

            // Put chart panel back to previous mouse mode
            MapMouseMode mode = chartPanel.getMouseDelegator()
                    .getActiveMouseMode();
            if (mode instanceof DistanceCircleMouseMode) {
                String prevModeID = ((DistanceCircleMouseMode) chartPanel
                        .getMouseDelegator().getActiveMouseMode())
                        .getPreviousMouseMode();
                chartPanel.setMouseMode(prevModeID);
            }
            // Event has been handled.
            return true;
        default:
            // RulerLayer does not respond to this mouse button.
            return false;
        }

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
