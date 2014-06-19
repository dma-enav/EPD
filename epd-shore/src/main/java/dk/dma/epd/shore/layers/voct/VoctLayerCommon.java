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
package dk.dma.epd.shore.layers.voct;

import java.awt.event.MouseEvent;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;

import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.voct.VOCTUpdateListener;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.layers.GeneralLayer;
import dk.dma.epd.shore.voct.VOCTManager;

public class VoctLayerCommon extends GeneralLayer implements MapMouseListener, VOCTUpdateListener {

    private static final long serialVersionUID = 1L;
    protected MapBean mapBean;
    protected VOCTManager voctManager;
    protected JMapFrame jMapFrame;

    @Override
    public void voctUpdated(VOCTUpdateEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = DragMouseMode.MODEID; // "DragMouseMode"
        ret[1] = NavigationMouseMode.MODEID; // "ZoomMouseMoude"
        ret[2] = SelectMouseMode.MODEID; // "SelectMouseMode"
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub

    }

    @Override
    public boolean mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
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

    public void updateEffectiveAreaLocation(SARData sarData) {
        // TODO Auto-generated method stub

    }

    public void toggleEffectiveAreaVisibility(int id, boolean visible) {
        // TODO Auto-generated method stub

    }

    public void removeEffortAllocationArea(int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof JMapFrame) {
            jMapFrame = (JMapFrame) obj;
        }

        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
            voctManager.addListener(this);
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
    }

    public void showFutureData(SARData sarData) {
        // TODO Auto-generated method stub

    }

}
