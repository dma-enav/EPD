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
package dk.dma.epd.common.graphics;

/**
 * Interface that a graphic can implement in order to allow for clients to
 * toggle selection of the graphic. For example, an implementation could add an
 * extra selection graphic (e.g. a ring surrounding the original graphic) or
 * change the current display of the graphic to visualize the
 * selection/deselection.
 * 
 * @author Janus Varmarken
 */
public interface ISelectableGraphic {
    /**
     * Update the selected status of this graphic.
     * 
     * @param selected
     *            True if the graphic is selected, false if it is deselected.
     */
    public void setSelected(boolean selected);
}
