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
package dk.dma.epd.common;

import com.bbn.openmap.omGraphics.OMGraphicConstants;

/**
 * Enumeration for heading rhumb line or great circle
 */
public enum Heading {
    RL(OMGraphicConstants.LINETYPE_RHUMB), 
    GC(OMGraphicConstants.LINETYPE_GREATCIRCLE);
    
    private int omLineType;
    
    /**
     * Constructor
     * @param omLineType
     */
    private Heading(int omLineType) {
        this.omLineType = omLineType;
    }
    
    /**
     * Return the OpenMap graphics constant for the heading
     * @return  the OpenMap graphics constant for the heading
     */
    public int getOMLineType() {
        return omLineType;
    }
}
