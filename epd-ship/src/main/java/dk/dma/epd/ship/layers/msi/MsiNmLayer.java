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
package dk.dma.epd.ship.layers.msi;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.proj.coords.LatLonPoint;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.msi.MsiNmDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiNmLayerCommon;
import dk.dma.epd.common.prototype.layers.msi.MsiNmNmSymbolGraphic;
import dk.dma.epd.common.prototype.layers.routeedit.NewRouteContainerLayer;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.MapMenu;

import java.awt.event.MouseEvent;

/**
 * Ship specific layer class for handling all MSI-NM messages
 */
public class MsiNmLayer extends MsiNmLayerCommon {
    
    private static final long serialVersionUID = 1L;

    private MouseDelegator mouseDelegator;
    private NewRouteContainerLayer newRouteLayer;

    /**
     * Constructor
     */
    public MsiNmLayer() {
        super();

        // Register the classes the will trigger the map menu
        registerMapMenuClasses(MsiNmNmSymbolGraphic.class, MsiNmDirectionalIcon.class);
    }

    /**
     * Returns a reference to the map menu
     * @return a reference to the map menu
     */
    @Override
    public MapMenu getMapMenu() {
        return (MapMenu)mapMenu;
    }   

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MouseDelegator) {
            mouseDelegator = (MouseDelegator) obj;
        }
        if (obj instanceof NewRouteContainerLayer) {
            newRouteLayer = (NewRouteContainerLayer) obj;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = RouteEditMouseMode.MODE_ID;
        ret[2] = DragMouseMode.MODE_ID;
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {        
        if(clickedGraphics instanceof MsiNmNmSymbolGraphic){
            MsiNmNmSymbolGraphic msi = (MsiNmNmSymbolGraphic) clickedGraphics;
            getMapMenu().msiMenu(msi);
        
        } else if(clickedGraphics instanceof MsiNmDirectionalIcon) {
            MsiNmDirectionalIcon direction = (MsiNmDirectionalIcon) clickedGraphics;
            getMapMenu().msiDirectionalMenu(direction, this);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        if (RouteEditMouseMode.MODE_ID.equals(mouseDelegator.getActiveMouseModeID())) {
            msiNmHandler.updateNewRouteMousePosition(null, null, null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseMoved(MouseEvent e) {
        // Testing mouse mode for the MSI relevancy
        if (RouteEditMouseMode.MODE_ID.equals(mouseDelegator.getActiveMouseModeID())) {
            LatLonPoint mousePosition = ((MapBean) e.getSource())
                    .getProjection().inverse(e.getPoint());
            Position pos = Position.create(mousePosition.getLatitude(), mousePosition.getLongitude());
            msiNmHandler.updateNewRouteMousePosition(pos, newRouteLayer.getRoute(), getProjection());
        }
        
        return super.mouseMoved(e);
    }
}
