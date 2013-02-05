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
package dk.dma.epd.shore.gui.utils;

import java.util.ArrayList;

import javax.swing.JLabel;

public class ToolItemGroup {

    private ArrayList<JLabel> toolItems = new ArrayList<JLabel>();
    private boolean singleEnable;



    public void addToolItem(JLabel toolItem) {
        this.toolItems.add(toolItem);
    }

    public void setToolItems(ArrayList<JLabel> toolItems) {
        this.toolItems = toolItems;
    }

    public ArrayList<JLabel> getToolItems() {
        return toolItems;
    }

    public void setSingleEnable(boolean singleEnable) {
        this.singleEnable = singleEnable;
    }

    public boolean isSingleEnable() {
        return singleEnable;
    }

}
