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

import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.MapFrameCommon;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;

/**
 * Adds type-safety to the {@linkplain GeneralLayerCommon}
 */
public class GeneralLayerCommonTS<
    MAINFRAME extends MainFrameCommon,
    MAPMENU extends MapMenuCommon,
    MAPFRAME extends MapFrameCommon> extends GeneralLayerCommon {

    private static final long serialVersionUID = 1L;
    
    /**
     * Returns a reference to the main frame
     * @return a reference to the main frame
     */
    @Override
    @SuppressWarnings("unchecked")
    public MAINFRAME getMainFrame() {
        return (MAINFRAME)mainFrame;
    }   

    /**
     * Returns a reference to the map menu
     * @return a reference to the map menu
     */
    @Override
    @SuppressWarnings("unchecked")
    public MAPMENU getMapMenu() {
        return (MAPMENU)mapMenu;
    }   

    /**
     * Returns a reference to the map frame
     * @return a reference to the map frame
     */
    @Override
    @SuppressWarnings("unchecked")
    public MAPFRAME getMapFrame() {
        return (MAPFRAME)mapFrame;
    }
}
