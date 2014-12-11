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
package dk.dma.epd.shore.layers.msi;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.layers.msi.MsiNmDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiNmLayerCommon;
import dk.dma.epd.common.prototype.layers.msi.MsiNmNmSymbolGraphic;
import dk.dma.epd.shore.gui.views.MapMenu;


/**
 * Shore specific layer class for handling all MSI-NM messages
 */
public class MsiNmLayer extends MsiNmLayerCommon {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for the layer
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
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {        
        if(clickedGraphics instanceof MsiNmNmSymbolGraphic){
            MsiNmNmSymbolGraphic msi = (MsiNmNmSymbolGraphic) clickedGraphics;
            getMapMenu().msiMenu(msi);
        
        } else if(clickedGraphics instanceof MsiNmDirectionalIcon) {
            MsiNmDirectionalIcon direction = (MsiNmDirectionalIcon) clickedGraphics;
            getMapMenu().msiDirectionalMenu(direction, this);
        }
    }
    
}
