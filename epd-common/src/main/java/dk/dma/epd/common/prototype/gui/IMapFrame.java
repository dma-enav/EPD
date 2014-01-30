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
package dk.dma.epd.common.prototype.gui;

import java.awt.Component;

import javax.swing.JPanel;

/**
 * Interface that should be implemented by the map frame component,
 * i.e. the widget that holds the maps.
 * <p>
 * In EPDShip that will be the {@code MainFrame} class and in EPDShore 
 * that will be the {@code JMapFrame} class.
 */
public interface IMapFrame {

    
    /**
     * Function for getting the glassPanel of the map frame
     * @return glassPanel the glassPanel of the map frame
     */
    JPanel getGlassPanel();
    
    /**
     * Returns a reference to the map frame cast as a component
     * @return a reference to the map frame cast as a component
     */
    Component asComponent();
    
    /**
     * Disposes the component
     */
    void dispose();
}
