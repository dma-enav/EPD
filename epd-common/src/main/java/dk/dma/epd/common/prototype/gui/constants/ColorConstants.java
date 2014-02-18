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
package dk.dma.epd.common.prototype.gui.constants;

import java.awt.Color;

import dk.dma.epd.common.graphics.GraphicsUtil;

/**
 * @author Janus Varmarken
 */
public final class ColorConstants {
    
    public static final int HEADING_ALPHA = 255;
    
    public static final Color VESSEL_COLOR = new Color(74, 97, 205, 255);

    public static final Color VESSEL_HEADING_COLOR = GraphicsUtil.transparentColor(VESSEL_COLOR, HEADING_ALPHA);
    
    public static final Color OWNSHIP_COLOR = Color.BLACK;

    public static final Color OWNSHIP_HEADING_COLOR = GraphicsUtil.transparentColor(OWNSHIP_COLOR, HEADING_ALPHA);
}
