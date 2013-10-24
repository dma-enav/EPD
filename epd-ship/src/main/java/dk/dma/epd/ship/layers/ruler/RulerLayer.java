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

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.event.MapMouseMode;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.ship.event.DistanceCircleMouseMode;
import dk.dma.epd.ship.gui.ChartPanel;

public class RulerLayer extends OMGraphicHandlerLayer implements
        MapMouseListener {

    // TODO update from defaulT?
    private static final long serialVersionUID = 1L;
    private OMGraphicList graphics = new OMGraphicList();
    private ChartPanel chartPanel;
    private RulerGraphic rulerGraphic;

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ChartPanel) {
            this.chartPanel = (ChartPanel) obj;
        }
    }

    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        /*
         * String[] ret = new String[2]; ret[0] = NavigationMouseMode.MODE_ID;
         * // "Gestures" ret[1] = DragMouseMode.MODE_ID; return ret;
         */
        String[] serviceList = new String[1];
        serviceList[0] = DistanceCircleMouseMode.MODE_ID;
        return serviceList;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            // Clear any old range circle from graphics.
            if (this.rulerGraphic != null) {
                this.graphics.remove(this.rulerGraphic);
            }
            // LatLon representation of point clicked.
            LatLonPoint ptClicked = this.chartPanel.getMap().getProjection()
                    .inverse(e.getPoint());
            // Transform to internal position representation.
            // The point clicked is the center of the range circle.
            Position circleCenter = Position.create(ptClicked.getLatitude(),
                    ptClicked.getLongitude());
            rulerGraphic = new RulerGraphic(circleCenter);
            this.graphics.add(this.rulerGraphic);
            // Repaint
            this.doPrepare();
            // Event has been handled.
            return true;
        case MouseEvent.BUTTON3:
            // Right click means exit this mouse mode...
            // Clear all graphics from this mode
            this.graphics.clear();
            // Put chart panel back to previous mouse mode
            // TODO this could be cleaner
            MapMouseMode mode = this.chartPanel.getMouseDelegator()
                    .getActiveMouseMode();
            if (mode instanceof DistanceCircleMouseMode) {
                String prevModeID = ((DistanceCircleMouseMode) this.chartPanel
                        .getMouseDelegator().getActiveMouseMode())
                        .getPreviousMouseMode();
                this.chartPanel.setMouseMode(prevModeID);
            }
            // Event has been handled.
            return true;
        default:
            // RulerLayer does not respond to this mouse button.
            return false;
        }

    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        doPrepare();
        super.projectionChanged(e);

    }

    @Override
    public synchronized OMGraphicList prepare() {
        this.graphics.project(getProjection());
        return graphics;
    }

    @Override
    public boolean mouseDragged(MouseEvent arg0) {
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved() {

    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        if (this.rulerGraphic != null) {
            // if the user has provided a center location
            // we need to draw the distance line as well
            // as the outer circle.
            // First find mouse position in lat-lon.
            LatLonPoint llp = this.chartPanel.getMap().getProjection()
                    .inverse(e.getPoint());
            // Convert to internal position representation.
            Position mousePos = Position.create(llp.getLatitude(),
                    llp.getLongitude());
            // Call the rg to draw the distancel ine and the circle
            this.rulerGraphic.updateOutside(mousePos);
            // repaint
            this.doPrepare();
            return true;
        }
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    

}
