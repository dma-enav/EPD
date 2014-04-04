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
package dk.dma.epd.ship.gui.component_panels;

/**
 * Interface that must be implemented by all dockable component panels
 */
public interface DockableComponentPanel {

    /**
     * Used as title and ID for the dockable component
     * @return the title and ID for the dockable component
     */
    String getDockableComponentName();
    
    /**
     * Returns whether or not to include the panel in a default layout
     * @return whether or not to include the panel in a default layout
     */
    boolean includeInDefaultLayout();
    
    /**
     * Returns whether or not to include the panel in the "Panels" menu
     * @return whether or not to include the panel in the "Panels" menu
     */
    boolean includeInPanelsMenu();
}
