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
package dk.dma.epd.ship.layers.areanotice;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRect;
import com.bbn.openmap.omGraphics.OMText;

/**
 * Simple text box to display AreaNotice message/message type
 */
public class ASTextBox extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    public ASTextBox(int xpos, int ypos, int width, int height, java.lang.String text) {
        super();
        super.clear();
        OMRect outline = new OMRect(xpos, ypos, xpos + width, ypos + height);

        outline.setFillPaint(Color.orange.brighter());
        OMText antext = new OMText(xpos + 10, ypos + 15, text, OMText.JUSTIFY_LEFT);

        super.add(antext);
        super.add(outline);
    }

}
