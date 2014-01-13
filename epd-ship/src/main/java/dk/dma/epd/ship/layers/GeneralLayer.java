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
package dk.dma.epd.ship.layers;

import java.awt.event.MouseEvent;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapEventUtils;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.MapMenu;

/**
 * General layer that may be sub-classed by other layers.
 * <p>
 * Contains default functionality for handling mouse right click
 */
public class GeneralLayer extends OMGraphicHandlerLayer implements MapMouseListener {

    private static final long serialVersionUID = 1L;

    protected MapMenu mapMenu;
    protected MapBean mapBean;
    protected MainFrame mainFrame;
    
    protected OMGraphicList graphics = new OMGraphicList();

    /**
     * Returns {@code this} as the {@linkplain MapMouseListener}
     * @return this
     */
    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    /**
     * Returns the mouse mode service list
     * @return the mouse mode service list
     */
    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
    }

    /**
     * Provides default behavior for right-clicks by
     * showing the general menu.
     * @param evt the mouse event
     */
    @Override
    public boolean mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            mapMenu.generalMenu(true);
            mapMenu.setVisible(true);

            if (EPDShip.getInstance().getMainFrame().getHeight() < evt.getYOnScreen()
                    + mapMenu.getHeight()) {
                mapMenu.show(this, evt.getX() - 2,
                        evt.getY() - mapMenu.getHeight());
            } else {
                mapMenu.show(this, evt.getX() - 2, evt.getY() - 2);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent arg0) {
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mouseMoved() {
    }

    @Override
    public boolean mouseMoved(MouseEvent arg0) {
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent arg0) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent arg0) {
        return false;
    }

    /**
     * Returns the mouse selection tolerance
     * @return the mouse selection tolerance
     */
    public float getMouseSelectTolerance() {
        return EPDShip.getInstance().getSettings().getGuiSettings().getMouseSelectTolerance();
    }
    
    /**
     * Returns the first graphics element placed at the mouse event location
     * that matches any of the types passed along. 
     * 
     * @param evt the mouse event
     * @param types the possible types
     * @return the first matching graphics element
     */
    public final OMGraphic getSelectedGraphic(MouseEvent evt, Class<?>... types) {
        return MapEventUtils.getSelectedGraphic(graphics, evt, getMouseSelectTolerance(), types);
    }
    
    /**
     * Called when a bean is added to the bean context
     * @param obj the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MapMenu) {
            mapMenu = (MapMenu) obj;
        } else if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        } else if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
        }
    }

    /**
     * Called when a bean is removed from the bean context
     * @param obj the bean being removed
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj == mapMenu) {
            mapMenu = null;
        } else if (obj == mapBean) {
            mapBean = null;
        } else if (obj == mainFrame) {
            mainFrame = null;
        }
    }
}
